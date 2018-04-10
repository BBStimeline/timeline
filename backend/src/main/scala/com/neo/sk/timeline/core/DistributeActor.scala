package com.neo.sk.timeline.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import org.slf4j.LoggerFactory
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.timeline.models.dao.{FollowDAO, PostDAO, UserDAO}
import com.neo.sk.timeline.ptcl.UserProtocol._

import scala.concurrent.duration._
import scala.collection.mutable
import com.neo.sk.timeline.Boot.{executor, scheduler, timeout}
import com.neo.sk.timeline.core.UserManager.UserLogout
import com.neo.sk.timeline.ptcl.DistributeProtocol.{DisCache, DisType}
import com.neo.sk.timeline.common.Constant.FeedType._
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
  final case class SwitchBehavior(
                                   name: String,
                                   behavior: Behavior[Command],
                                   durationOpt: Option[FiniteDuration] = None,
                                   timeOut: TimeOut = TimeOut("busy time error")
                                 ) extends Command
  private final case object BehaviorChangeKey

  /**配置文件中参数*/
  private val boardBatch=500
  private val userBatch=50
  private val topicBatch=50

  def init(variety:Int,param:DisType): Behavior[Command] = {
    Behaviors.setup[Command] { ctx =>
      implicit val stashBuffer = StashBuffer[Command](Int.MaxValue)
      Behaviors.withTimers[Command] { implicit timer =>
        val futureEvent=if(variety==BOARDINT){
          val board=param.board.get
          val origin=param.origin
          PostDAO.getLastPostByBoard(board,origin,boardBatch)
        }else if(variety==USERINT){
          val userId=param.userId.get
          val userName=param.userName.get
          val origin=param.origin
          PostDAO.getLastPostByUser(userId,userName,origin,userBatch)
        }else{
          val board=param.board.get
          val topicId=param.topicId.get
          val origin=param.origin
          PostDAO.getLastPostByTopic(board,origin,topicId,topicBatch)
        }
        futureEvent.map{posts=>
          val postList:mutable.HashSet[(Int,String,Long)]=mutable.HashSet()
          posts.map(p=>
            postList.add((p.origin,p.boardName,p.topicId))
          )
          ctx.self ! SwitchBehavior("idle", idle(DisCache(postList)))
        }
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

        case x=>
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
