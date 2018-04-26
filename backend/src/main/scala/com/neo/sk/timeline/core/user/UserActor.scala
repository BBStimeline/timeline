package com.neo.sk.timeline.core.user

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.timeline.Boot.{distributeManager, executor, scheduler, timeout}
import com.neo.sk.timeline.common.AppSettings
import com.neo.sk.timeline.common.Constant.FeedType
import com.neo.sk.timeline.core.DistributeManager
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.models.dao.{FollowDAO, UserDAO}
import com.neo.sk.timeline.ptcl.DistributeProtocol.{DisType, FeedListInfo}
import com.neo.sk.timeline.ptcl.UserProtocol._
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}
/**
  * User: sky
  * Date: 2018/4/8
  * Time: 11:11
  */
object UserActor {
  private val log = LoggerFactory.getLogger(this.getClass)

  import UserManager._
  trait Command
  case class TimeOut(msg: String) extends Command
  final case class SwitchBehavior(
                                   name: String,
                                   behavior: Behavior[Command],
                                   durationOpt: Option[FiniteDuration] = None,
                                   timeOut: TimeOut = TimeOut("busy time error")
                                 ) extends Command
  final case class RefreshFeed(sortType: Int,itemTime:Long, pageSize: Int,up:Boolean, replyTo:ActorRef[Option[List[UserFeedReq]]]) extends Command

  final case object CleanFeed extends Command

  private final case object BehaviorChangeKey
  private final case object CleanFeedKey

  private case object WaitingTimerKey

  private case object WaitingTimeOut extends Command

  final case class ItemTime(var time1:Long= 0l, var time2:Long=0l)

  private val maxFeedLength = AppSettings.feedCnt
  private val cleanFeedTime = AppSettings.feedClean.minutes
  private val waitTime=AppSettings.actorWait.minutes
  private val defaultUser=AuthorInfo("","",0)

  def init(uid: Long): Behavior[Command] = {
    Behaviors.setup[Command] { ctx =>
      implicit val stashBuffer = StashBuffer[Command](Int.MaxValue)
      log.info(s"userActor--$uid is starting")
      Behaviors.withTimers[Command] { implicit timer =>
        for {
          userInfoOpt <- UserDAO.getUserById(uid)
          feed <- UserDAO.getUserFeed(uid)
          follows <- FollowDAO.getFollows(uid)
        } yield {
          val favBoard = new mutable.HashSet[(Int, String)]()
          val favUser = new mutable.HashSet[(Int,String)]()
          val favTopic = new mutable.HashSet[(Int, String, Long)]()
          val newFeed = new mutable.HashMap[(Int, Int,String,Long,Long), (Long,Long,Option[AuthorInfo])]()
          val newReplyFeed = new mutable.HashMap[(Int, Int,String,Long,Long), (Long,Long,Option[AuthorInfo])]()

          if (feed.nonEmpty) {
            feed.filter(_.postTime != 0).sortBy(_.postTime).reverse.take(maxFeedLength).foreach { f =>
              newFeed.put((f.feedType, f.origin, f.boardname, f.topicId,f.postTime), (f.postId,f.lastReplyTime,if(f.feedType!=FeedType.USER) None else Some(AuthorInfo(f.authorId.getOrElse(""),f.authorName.getOrElse(""),f.origin))))
            }
            feed.filter(_.lastReplyTime != 0).sortBy(_.lastReplyTime).reverse.take(maxFeedLength).foreach { f =>
              newReplyFeed.put((f.feedType,f.origin, f.boardname,f.topicId, f.postTime), (f.postId,f.lastReplyTime,if(f.feedType!=FeedType.USER) None else Some(AuthorInfo(f.authorId.getOrElse(""),f.authorName.getOrElse(""),f.origin))))
            }
          }
          follows._1.map { r =>
            val name=r.origin+"-"+r.boardName
            val param=DisType(board=Some(r.boardName),origin = r.origin)
            distributeManager ! DistributeManager.NotifyFollowObject(name,FeedType.BOARD,uid,param)
            favBoard.add(r.origin, r.boardName)
          }
          follows._2.map { r =>
            val name=r.origin+"-"+r.followId
            val param=DisType(userId = Some(r.followId),userName = Some(r.followName),origin =r.origin)
            distributeManager ! DistributeManager.NotifyFollowObject(name,FeedType.USER,uid,param)
            favUser.add(r.origin, r.followId)
          }
          follows._3.map { r =>
            val name=r.origin+"-"+r.boardName+"-"+r.topicId
            val param=DisType(board=Some(r.boardName),topicId = Some(r.topicId),origin = r.origin)
            distributeManager ! DistributeManager.NotifyFollowObject(name,FeedType.TOPIC,uid,param)
            favTopic.add(r.origin, r.boardName, r.topicId)
          }
          userInfoOpt match {
            case Some(u) =>
              timer.startPeriodicTimer(CleanFeedKey, CleanFeed, cleanFeedTime)
              ctx.self ! SwitchBehavior("idle", idle(
                UserActorInfo(uid, u.userId, u.bbsId,u.headImg,
                  favBoard,
                  favUser,
                  favTopic,
                  newFeed,
                  newReplyFeed),ItemTime(userInfoOpt.getOrElse(SlickTables.rUser(id=0l,userId = "",sha1Pwd = "",createTime = 0l,lastLoginTime = 0l)).firstItemTime1,
                  userInfoOpt.getOrElse(SlickTables.rUser(id=0l,userId = "",sha1Pwd = "",createTime = 0l,lastLoginTime = 0l)).firstItemTime2)))
            case None =>
              log.warn(s"${ctx.self.path} getUserById error when init,error:$uid is not exist")
          }
        }
        switchBehavior(ctx, "busy", busy(), Some(3.minutes), TimeOut("init"))
      }
    }
  }

  def idle(user: UserActorInfo,itemTime:ItemTime)(implicit stashBuffer: StashBuffer[Command], timer: TimerScheduler[Command]): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case WaitingTimeOut =>

        case msg:DisEvent=> //do nothing

        case _ =>
          timer.cancel(WaitingTimerKey)
          timer.startSingleTimer(WaitingTimerKey, WaitingTimeOut, waitTime)
      }

      msg match {
        case UserLogout(_,replyTo)=>
          replyTo ! "OK"
          Behaviors.stopped

        case DisEvent(_,feedType,event,isMain)=>
          log.info(s"distribute send new post ${event._2+"-"+event._5} to user-${user.uid}")
          if(isMain){
            user.newFeed.put((feedType,event._1, event._2, event._3, event._4),(event._5, event._6, event._7))
          }else{
            user.newReplyFeed.put((feedType,event._1, event._2, event._3, event._4),(event._5, event._6, event._7))
          }
          Behaviors.same

        case UserFollowBoardMsg(_,boardName,origin)=>
          user.favBoards.add(origin, boardName)
          val name=origin+"-"+boardName
          val param=DisType(board=Some(boardName),origin = origin)
          distributeManager ! DistributeManager.NotifyFollowObject(name,FeedType.BOARD,user.uid,param)
          val future: Future[FeedListInfo] = distributeManager ? (DistributeManager.GetFeedList(FeedType.BOARD,name,_))
          future.map { data =>
            data.newPosts.foreach { event =>
              if (!user.newFeed.exists(r=>(r._1._2,r._1._3,r._1._4) == (event._1, event._2, event._3))) {
                user.newFeed.put((FeedType.BOARD,event._1, event._2, event._3, event._4),
                  (event._5, event._6, event._7))
              }
            }
            data.newReplyPosts.foreach { event =>
              if (!user.newReplyFeed.exists(r=>(r._1._2,r._1._3,r._1._4) == (event._1, event._2, event._3))) {
                user.newReplyFeed.put((FeedType.BOARD,event._1, event._2, event._3, event._4),
                  (event._5, event._6, event._7))
              }
            }
          }
          Behaviors.same

        case UserFollowTopicMsg(_,post)=>
          user.favTopic.add(post.origin, post.boardName,post.topicId)
          val name=post.origin+"-"+post.boardName+"-"+post.topicId
          val param=DisType(board=Some(post.boardName),topicId = Some(post.topicId),origin = post.origin)
          distributeManager ! DistributeManager.NotifyFollowObject(name,FeedType.TOPIC,user.uid,param)
          val future: Future[FeedListInfo] = distributeManager ? (DistributeManager.GetFeedList(FeedType.BOARD,name,_))
          future.map { data =>
            data.newPosts.foreach { event =>
              if (!user.newFeed.exists(r => (r._1._2, r._1._3, r._1._4) == (event._1, event._2, event._3))) {
                user.newFeed.put((FeedType.TOPIC, event._1, event._2, event._3, event._4),
                  (event._5, event._6, event._7))
              }
            }
            data.newReplyPosts.foreach { event =>
              if (!user.newReplyFeed.exists(r=>(r._1._2,r._1._3,r._1._4) == (event._1, event._2, event._3))) {
                user.newReplyFeed.put((FeedType.TOPIC,event._1, event._2, event._3, event._4),
                  (event._5, event._6, event._7))
              }
            }
          }
          Behaviors.same

        case UserFollowUserMsg(_,followId,followName,origin)=>
          user.favUsers.add(origin,followId)
          val name=origin+"-"+followId
          val param=DisType(userId = Some(followId),userName = Some(followName),origin =origin)
          distributeManager ! DistributeManager.NotifyFollowObject(name,FeedType.USER,user.uid,param)
          val future: Future[FeedListInfo] = distributeManager ? (DistributeManager.GetFeedList(FeedType.BOARD,name,_))
          future.map { data =>
            data.newPosts.foreach { event =>
              if (!user.newFeed.exists(r => (r._1._2, r._1._3, r._1._4) == (event._1, event._2, event._3))) {
                user.newFeed.put((FeedType.USER, event._1, event._2, event._3, event._4),
                  (event._5, event._6, event._7))
              }
            }
            data.newReplyPosts.foreach { event =>
              if (!user.newReplyFeed.exists(r=>(r._1._2,r._1._3,r._1._4) == (event._1, event._2, event._3))) {
                user.newReplyFeed.put((FeedType.USER, event._1, event._2, event._3, event._4),
                  (event._5, event._6, event._7))
              }
            }
          }
          Behaviors.same

        case msg:UserUnFollowBoardMsg=>
          user.favBoards.remove(msg.origin,msg.boardName)
          val name=msg.origin+"-"+msg.boardName
          distributeManager ! DistributeManager.QuitFollowObject(name,FeedType.BOARD,user.uid)
          user.newFeed.filter(r=> r._1._1 == FeedType.BOARD && r._1._2 == msg.origin && r._1._3 == msg.boardName).map{ r=>
            user.newFeed.remove(r._1)
          }
          user.newReplyFeed.filter(r=> r._1._1 == FeedType.BOARD && r._1._2 == msg.origin && r._1._3 == msg.boardName).map{ r=>
            user.newReplyFeed.remove(r._1)
          }
          Behaviors.same

        case msg:UserUnFollowTopicMsg=>
          user.favTopic.remove(msg.post.origin,msg.post.boardName,msg.post.topicId)
          val name=msg.post.origin+"-"+msg.post.boardName+"-"+msg.post.topicId
          distributeManager ! DistributeManager.QuitFollowObject(name,FeedType.TOPIC,user.uid)
          user.newFeed.filter(r=> r._1._1 == FeedType.TOPIC && r._1._2 == msg.post.origin && r._1._3 == msg.post.boardName && r._1._4 == msg.post.topicId).map{ r=>
            user.newFeed.remove(r._1)
          }
          user.newReplyFeed.filter(r=> r._1._1 == FeedType.TOPIC && r._1._2 == msg.post.origin && r._1._3 == msg.post.boardName && r._1._4 == msg.post.topicId).map{ r=>
            user.newReplyFeed.remove(r._1)
          }
          Behaviors.same

        case msg:UserUnFollowUserMsg=>
          user.favUsers.remove(msg.origin,msg.followId)
          val name=msg.origin+"-"+msg.followId
          distributeManager ! DistributeManager.QuitFollowObject(name,FeedType.USER,user.uid)
          user.newFeed.filter(r=> r._1._1 == FeedType.USER && r._1._2 == msg.origin && r._2._3.getOrElse(defaultUser).origin==msg.origin&&r._2._3.getOrElse(defaultUser).authorId==msg.followId).map{ r=>
            user.newFeed.remove(r._1)
          }
          user.newReplyFeed.filter(r=> r._1._1 == FeedType.USER && r._1._2 == msg.origin && r._2._3.getOrElse(defaultUser).origin==msg.origin&&r._2._3.getOrElse(defaultUser).authorId==msg.followId).map{ r=>
            user.newReplyFeed.remove(r._1)
          }
          Behaviors.same

        case msg:GetUserFeed=>
          msg.sortType match {
            case 1 => //根据创建时间
              if(msg.up){
                println("--------------1")
                if (user.newFeed.isEmpty || msg.itemTime >= user.newFeed.map(_._1._5).max) {
                  ctx.self ! RefreshFeed(msg.sortType,msg.itemTime,msg.pageSize,msg.up,msg.replyTo)
                }else{
                  val list=user.newFeed.filter(_._1._5 > msg.itemTime).map(i => UserFeedReq(i._1._2,i._1._3,i._1._4,i._2._1,i._1._5)).toList.sortBy(_.time).take(msg.pageSize).reverse
                  itemTime.time1=list.head.time+1
                  println(itemTime)
                  msg.replyTo ! Some(list)
                }
              }else{
                if (user.newFeed.isEmpty || msg.itemTime <= user.newFeed.map(_._1._5).min) {
                  msg.replyTo ! None
                } else {
                  msg.replyTo ! Some(user.newFeed.filter(_._1._5 < msg.itemTime).map(i => UserFeedReq(i._1._2,i._1._3,i._1._4,i._2._1,i._1._5)).toList.sortBy(_.time).reverse.take(msg.pageSize))
                }
              }
            case 2 => //根据最新回复时间
              if(msg.up){
                if (user.newFeed.isEmpty || msg.itemTime >= user.newReplyFeed.map(_._2._2).max) {
                  ctx.self ! RefreshFeed(msg.sortType,msg.itemTime,msg.pageSize,msg.up,msg.replyTo)
                }else{
                  val list=user.newReplyFeed.filter(_._2._2 > msg.itemTime).map(i => UserFeedReq(i._1._2,i._1._3,i._1._4,i._2._1,i._2._2)).toList.sortBy(_.time).take(msg.pageSize).reverse
                  itemTime.time2=list.head.time+1
                  msg.replyTo ! Some(list)
                }
              }else{
                if (user.newReplyFeed.isEmpty || msg.itemTime <= user.newReplyFeed.map(_._2._2).min) {
                  msg.replyTo ! None
                } else {
                  msg.replyTo ! Some(user.newReplyFeed.filter(_._2._2 < msg.itemTime).map(i => UserFeedReq(i._1._2,i._1._3,i._1._4,i._2._1,i._2._2)).toList.sortBy(_.time).reverse.take(msg.pageSize))
                }
              }
            case x@_ =>
              log.debug(s"${ctx.self.path} GetFeed sortType error....sortType is $x")
              msg.replyTo ! None
          }
          Behaviors.same

        case msg:GetLastTime=>
          msg.replyTo ! (itemTime.time1,itemTime.time2)
          Behaviors.same

        case msg:RefreshFeed=>
          val targetList = user.favBoards.map(i => (FeedType.BOARD, i._1 + "-" + i._2)).toList :::user.favTopic.map(i=>(FeedType.TOPIC,i._1+"-"+i._2+"-"+i._3)).toList ::: user.favUsers.map(i => (FeedType.USER, i._1 +"-"+i._2)).toList
          Future.sequence{
            targetList.map{ i =>
              val future: Future[FeedListInfo] = distributeManager ? (DistributeManager.GetFeedList(i._1, i._2, _))
              future.map { data =>
                data.newPosts.foreach { event =>
                  if (!user.newFeed.exists(r => (r._1._2, r._1._3, r._1._4) == (event._1, event._2, event._3))) {
                    user.newFeed.put((data.feedType, event._1, event._2, event._3, event._4),
                      (event._5, event._6, event._7))
                  }
                }
                data.newReplyPosts.foreach { event =>
                  if(!user.newReplyFeed.exists(r=>(r._1._2,r._1._3,r._1._4) == (event._1, event._2, event._3))) {
                    user.newReplyFeed.put((data.feedType,event._1, event._2, event._3,event._4),
                      (event._5, event._6, event._7))
                  }
                }
              }
            }
          }.onComplete{
            case Success(_) =>
              msg.sortType match {
                case 1 => //根据创建时间
                  if (msg.up) {
                    if (user.newFeed.isEmpty || msg.itemTime >= user.newFeed.map(_._1._5).max) {
                      msg.replyTo ! None
                    } else {
                      val list = user.newFeed.filter(_._1._5 > msg.itemTime).map(i => UserFeedReq(i._1._2, i._1._3, i._1._4, i._2._1, i._1._5)).toList.sortBy(_.time).take(msg.pageSize).reverse
                      itemTime.time1 = list.head.time+1
                      println(itemTime)
                      msg.replyTo ! Some(list)
                    }
                  } else {
                    if (user.newFeed.isEmpty || msg.itemTime <= user.newFeed.map(_._1._5).min) {
                      msg.replyTo ! None
                    } else {
                      msg.replyTo ! Some(user.newFeed.filter(_._1._5 < msg.itemTime).map(i => UserFeedReq(i._1._2, i._1._3, i._1._4, i._2._1, i._1._5)).toList.sortBy(_.time).reverse.take(msg.pageSize))
                    }
                  }
                case 2 => //根据最新回复时间
                  if (msg.up) {
                    if (user.newFeed.isEmpty || msg.itemTime >= user.newReplyFeed.map(_._2._2).max) {
                      msg.replyTo ! None
                    } else {
                      val list = user.newReplyFeed.filter(_._2._2 > msg.itemTime).map(i => UserFeedReq(i._1._2, i._1._3, i._1._4, i._2._1, i._2._2)).toList.sortBy(_.time).take(msg.pageSize).reverse
                      itemTime.time2 = list.head.time+1
                      msg.replyTo ! Some(list)
                    }
                  } else {
                    if (user.newReplyFeed.isEmpty || msg.itemTime <= user.newReplyFeed.map(_._2._2).min) {
                      msg.replyTo ! None
                    } else {
                      msg.replyTo ! Some(user.newReplyFeed.filter(_._2._2 < msg.itemTime).map(i => UserFeedReq(i._1._2, i._1._3, i._1._4, i._2._1, i._2._2)).toList.sortBy(_.time).reverse.take(msg.pageSize))
                    }
                  }
              }
            case Failure(_) =>
              log.debug(s"${ctx.self.path} RefreshFeed fail.....")
          }
          Behaviors.same

        case CleanFeed =>
          val newFeeds=user.newFeed.toList.sortBy(_._1._5).reverse.take(maxFeedLength)
          val newReplyFeed=user.newReplyFeed.toList.sortBy(_._2._2).reverse.take(maxFeedLength)
          user.newFeed.clear()
          user.newReplyFeed.clear()
          newFeeds.foreach{r=>
            user.newFeed.put(r._1,r._2)
          }
          newReplyFeed.foreach{r=>
            user.newReplyFeed.put(r._1,r._2)
          }
          val feedList=(newFeeds:::newReplyFeed).map(r=>SlickTables.rUserFeed(0l,user.uid,r._1._2,r._1._3,r._1._4,r._2._1,r._1._5,r._2._2,r._1._1,getUserInfo(r._2._3)._1,getUserInfo(r._2._3)._2))
          UserDAO.cleanFeed(user.uid, feedList)
          Behaviors.same

        case WaitingTimeOut=>
          val targetList = user.favBoards.map(i => (FeedType.BOARD, i._1 + "-" + i._2)).toList :::user.favTopic.map(i=>(FeedType.TOPIC,i._1+"-"+i._2+"-"+i._3)).toList ::: user.favUsers.map(i => (FeedType.USER, i._1 +"-"+i._2)).toList
          targetList.foreach(r=>
            distributeManager ! DistributeManager.QuitFollowObject(r._2,r._1,user.uid)
          )
          log.info(s"userActor--${user.uid} is stop")
          UserDAO.updateTime(user.uid,itemTime.time1,itemTime.time2)
          Behaviors.stopped

        case x =>
          log.warn(s"unknown msg: $x")
          Behaviors.unhandled
      }
    }
  }

  private def busy()(
    implicit stashBuffer: StashBuffer[Command],
    timer: TimerScheduler[Command]
  ): Behavior[Command] =
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case SwitchBehavior(name, behavior, durationOpt, timeOut) =>
          switchBehavior(ctx, name, behavior, durationOpt, timeOut)

        case TimeOut(m) =>
          log.debug(s"${ctx.self.path} is time out when busy,msg=$m")
          Behaviors.stopped

        case unknownMsg =>
          stashBuffer.stash(unknownMsg)
          Behaviors.same

      }
    }

  private[this] def switchBehavior(ctx: ActorContext[Command],
                                   behaviorName: String,
                                   behavior: Behavior[Command],
                                   durationOpt: Option[FiniteDuration] = None,
                                   timeOut: TimeOut = TimeOut("busy time error"))
                                  (implicit stashBuffer: StashBuffer[Command],
                                   timer: TimerScheduler[Command]) = {
    timer.cancel(BehaviorChangeKey)
    durationOpt.foreach(timer.startSingleTimer(BehaviorChangeKey, timeOut, _))
    stashBuffer.unstashAll(ctx, behavior)
  }

  private def getUserInfo(option: Option[AuthorInfo])={
    if(option.isEmpty) (None,None) else (Some(option.get.authorId),Some(option.get.authorName))
  }

}
