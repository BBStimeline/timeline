package com.neo.sk.timeline.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import org.slf4j.LoggerFactory
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.timeline.models.dao.{FollowDAO, UserDAO}
import com.neo.sk.timeline.ptcl.UserProtocol._

import scala.concurrent.duration._
import scala.collection.mutable
import com.neo.sk.timeline.Boot.{executor, scheduler, timeout}
import com.neo.sk.timeline.core.UserManager.UserLogout
/**
  * User: sky
  * Date: 2018/4/8
  * Time: 11:11
  */
object UserActor {
  private val log = LoggerFactory.getLogger(this.getClass)

  import com.neo.sk.timeline.core.UserManager._
  trait Command
  case class TimeOut(msg: String) extends Command
  final case class SwitchBehavior(
                                   name: String,
                                   behavior: Behavior[Command],
                                   durationOpt: Option[FiniteDuration] = None,
                                   timeOut: TimeOut = TimeOut("busy time error")
                                 ) extends Command
  final case class RefreshFeed(sortType: Option[Int], pageSize: Option[Int], replyTo: Option[ActorRef[Option[List[UserFeedReq]]]]) extends Command

  final case object CleanFeed extends Command

  private final case object BehaviorChangeKey
  private final case object CleanFeedKey

  private val maxFeedLength = 50
  private val cleanFeedTime = 20.minutes


  def init(uid: Long): Behavior[Command] = {
    Behaviors.setup[Command] { ctx =>
      implicit val stashBuffer = StashBuffer[Command](Int.MaxValue)
      Behaviors.withTimers[Command] { implicit timer =>
        for {
          userInfoOpt <- UserDAO.getUserById(uid)
          feed <- UserDAO.getUserFeed(uid)
          follows <- FollowDAO.getFollows(uid)
        } yield {
          val favBoard = new mutable.HashSet[(Int, String)]()
          val favUser = new mutable.HashSet[(Int,Long)]()
          val favTopic = new mutable.HashSet[(Int, String, Long)]()
          val newFeed = new mutable.HashSet[((Int, PostBaseInfo), (Long, AuthorInfo))]()
          val newReplyFeed = new mutable.HashSet[((Int, PostBaseInfo), (Long, AuthorInfo))]()

          if (feed.nonEmpty) {
            feed.filter(_.postTime != 0).sortBy(_.postTime).reverse.take(maxFeedLength).foreach { f =>
              newFeed.add(((f.feedType, PostBaseInfo(f.origin, f.boardname, f.postId)), (f.postTime, AuthorInfo(f.authorId, f.authorName, f.origin))))
            }
            feed.filter(_.lastReplyTime != 0).sortBy(_.lastReplyTime).reverse.take(maxFeedLength).foreach { f =>
              newReplyFeed.add(((f.feedType, PostBaseInfo(f.origin, f.boardname, f.postId)), (f.lastReplyTime, AuthorInfo(f.authorId, f.authorName, f.origin))))
            }
          }
          follows._1.map(r=>
            favBoard.add(r.origin,r.boardName)
          )
          follows._2.map(r=>
            favUser.add(r.origin,r.followId)
          )
          follows._3.map(r=>
            favTopic.add(r.origin,r.boardName,r.topicId)
          )
          userInfoOpt match {
            case Some(u) =>
              timer.startPeriodicTimer(CleanFeedKey, CleanFeed, cleanFeedTime)
              ctx.self ! RefreshFeed(None, None, None)
              ctx.self ! SwitchBehavior("idle", idle(
                UserActorInfo(uid, u.userId, u.bbsId,u.headImg,
                  favBoard,
                  favUser,
                  favTopic,
                  newFeed,
                  newReplyFeed)))
            case None =>
              log.warn(s"${ctx.self.path} getUserById error when init,error:$uid is not exist")
          }

        }
        switchBehavior(ctx, "busy", busy(), Some(3.minutes), TimeOut("init"))
      }
    }
  }

  def idle(user: UserActorInfo)(implicit stashBuffer: StashBuffer[Command], timer: TimerScheduler[Command]): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case UserLogout(_,replyTo)=>
          replyTo ! "OK"
          Behaviors.stopped

        case UserFollowBoardMsg(_,boardName,origin)=>
          user.favBoards.add(origin, boardName)
          Behaviors.same

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

}
