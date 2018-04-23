package com.neo.sk.timeline.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import org.slf4j.LoggerFactory
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.timeline.models.dao.{FollowDAO, PostDAO, TopicDAO}
import com.neo.sk.timeline.ptcl.UserProtocol._

import scala.concurrent.duration._
import scala.collection.mutable
import com.neo.sk.timeline.Boot.{distributeManager, executor, scheduler, timeout, userManager}
import com.neo.sk.timeline.common.Constant.FeedType
import com.neo.sk.timeline.core.user.UserManager.UserLogout
import com.neo.sk.timeline.ptcl.DistributeProtocol.{DisCache, DisType, FeedListInfo}
import com.neo.sk.timeline.common.Constant.FeedType._
import com.neo.sk.timeline.core.user.UserManager
import com.neo.sk.timeline.ptcl.PostProtocol.PostEvent

import scala.concurrent.Future
/**
  * User: sky
  * Date: 2018/4/9
  * Time: 15:24
  */
object DistributeActor {
  private val log = LoggerFactory.getLogger(this.getClass)

  import com.neo.sk.timeline.core.DistributeManager._
  trait Command
  case class TimeOut(msg: String) extends Command
  case object CheckObjectTimeOut extends Command
  final case class SwitchBehavior(
                                   name: String,
                                   behavior: Behavior[Command],
                                   durationOpt: Option[FiniteDuration] = None,
                                   timeOut: TimeOut = TimeOut("busy time error")
                                 ) extends Command
  private final case object BehaviorChangeKey
  private final case object CheckObjectKey
  /**配置文件中参数*/
  private val boardBatch=500
  private val userBatch=50

  def init(name:String,variety:Int,paramOpt:Option[DisType]): Behavior[Command] = {
    Behaviors.setup[Command] { ctx =>
      log.info(s"distributeActor--$name is starting")
      implicit val stashBuffer = StashBuffer[Command](Int.MaxValue)
      val param=paramOpt.get
      Behaviors.withTimers[Command] { implicit timer =>
        val newPost:mutable.HashMap[(Int,String,Long,Long),(Long,Long,Option[AuthorInfo])]=mutable.HashMap()
        val newReplyPost:mutable.HashMap[(Int,String,Long,Long),(Long,Long,Option[AuthorInfo])]=mutable.HashMap()
        if(variety==BOARD){
          val board=param.board.get
          val origin=param.origin
          for{
            postList<-TopicDAO.getPostListByPostTime(board,origin,boardBatch)
            replyPostList<-TopicDAO.getPostListByReplyTime(board,origin,boardBatch)
          }yield {
            postList.map(p=>
              newPost.put((p.origin,p.boardName,p.topicId,p.postTime),(p.topicId,0l,None))
            )
            replyPostList.map(p=>
              newReplyPost.put((p.origin,p.boardName,p.topicId,0l),(p.lastPostId,p.lastReplyTime,None))
            )
            ctx.self ! SwitchBehavior("idle", idle(DisCache(newPost = newPost, newReplyPost=newReplyPost,name =name ,variety= variety)))
          }
        }else if(variety==USER){
          val userId=param.userId.get
          val userName=param.userName.get
          val origin=param.origin
          for{
            posts <- PostDAO.getLastTopicByUser(userId,userName,origin,userBatch)
            tps <- {
              val topics=posts.groupBy(r=>(r.origin,r.boardName,r.topicId))
              PostDAO.getUserByPostId(topics.keys.toSeq)
            }
          }yield {
            val topics=posts.groupBy(r=>(r.origin,r.boardName,r.topicId))
            topics.map { t =>
              val lastPost=t._2.map(_.postId).max
              val lastPostTime=t._2.map(_.postTime).max
              newPost.put((t._1._1, t._1._2,t._1._3,tps.filter(_._1==t._1._3).map(_._2).head), (lastPost,0l,Some(AuthorInfo(userId,userName,origin))))
              newReplyPost.put((t._1._1, t._1._2,t._1._3,0l), (lastPost,lastPostTime,Some(AuthorInfo(userId,userName,origin))))
            }
            ctx.self ! SwitchBehavior("idle", idle(DisCache(newPost = newPost, newReplyPost=newReplyPost,name =name ,variety= variety)))
          }
        }else{
          val board=param.board.get
          val topicId=param.topicId.get
          val origin=param.origin
          TopicDAO.getPostById(origin,board,topicId).map{
            case Some(p)=>
              newPost.put((p.origin,p.boardName,p.topicId,p.postTime),(p.topicId,0l,None))
              newReplyPost.put((p.origin,p.boardName,p.topicId,0l),(p.lastPostId,p.lastReplyTime,None))
              ctx.self ! SwitchBehavior("idle", idle(DisCache(newPost = newPost, newReplyPost=newReplyPost,name =name ,variety= variety)))
          }
        }
        timer.startPeriodicTimer(CheckObjectKey,CheckObjectTimeOut,5.minutes)
        switchBehavior(ctx, "busy", busy(), Some(3.minutes), TimeOut("init"))
      }
    }
  }

  def idle(disCache:DisCache)(implicit stashBuffer: StashBuffer[Command], timer: TimerScheduler[Command]): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case msg:NotifyFollowObject=>
          disCache.followList.add(msg.userId)
          Behaviors.same

        case msg:QuitFollowObject=>
          disCache.followList.remove(msg.userId)
          Behaviors.same

        case CheckObjectTimeOut=>
          if(disCache.followList.size==0){
            Behaviors.stopped
          }else{
            Behaviors.same
          }

        case DealTask(p)=>
          if(p.isMain){
            disCache.newPost.put((p.origin,p.board,p.topicId,p.postTime),(p.postId,0l,structAuthor(disCache.variety,p)))
            disCache.newReplyPost.put((p.origin,p.board,p.topicId,0l),(p.postId,p.postTime,structAuthor(disCache.variety,p)))
            disCache.followList.foreach{u=>
              userManager ! UserManager.DisEvent(u,disCache.variety,(p.origin,p.board,p.topicId,p.postTime,p.postId,0l,structAuthor(disCache.variety,p)),p.isMain)
            }
          }else{
            disCache.newReplyPost.put((p.origin,p.board,p.topicId,0l),(p.postId,p.postTime,structAuthor(disCache.variety,p)))
            disCache.followList.foreach{u=>
              userManager ! UserManager.DisEvent(u,disCache.variety,(p.origin,p.board,p.topicId,0l,p.postId,p.postTime,structAuthor(disCache.variety,p)),p.isMain)
            }
          }
          Behaviors.same

        case msg:GetFeedList=>
          val newPost=disCache.newPost.toList.sortBy(_._1._4).reverse.map(r=>(r._1._1,r._1._2,r._1._3,r._1._4,r._2._1,r._2._2,r._2._3))
          val newReplyPost=disCache.newReplyPost.toList.sortBy(_._2._1).reverse.map(r=>(r._1._1,r._1._2,r._1._3,r._1._4,r._2._1,r._2._2,r._2._3))
          msg.replyTo ! FeedListInfo(disCache.variety,newPost,newReplyPost)
          Behaviors.same

        case x=>
          log.warn(s"unknown msg: $x")
          Behaviors.unhandled
      }
    }
  }

  private def structAuthor(variety:Int,post:PostEvent)={
    if(variety==FeedType.USER) Some(AuthorInfo(post.authorId,post.authorName,post.origin)) else None
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
}
