package com.neo.sk.timeline.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import org.slf4j.LoggerFactory
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.models.dao.{FollowDAO, UserDAO}
import akka.actor.typed.scaladsl.AskPattern._

import scala.concurrent.duration._
import scala.collection.mutable
import com.neo.sk.timeline.Boot.{distributeManager, executor, scheduler, timeout}
import com.neo.sk.timeline.common.Constant.FeedType
import com.neo.sk.timeline.core.BoardManager.GetTopicInfoReqMsg

import scala.concurrent.Future
import scala.util.{Failure, Success}
/**
  * User: sky
  * Date: 2018/4/15
  * Time: 15:18
  */
object BoardActor {
  private val log = LoggerFactory.getLogger(this.getClass)
  trait Command
  private final case object BehaviorChangeKey
  case class TimeOut(msg:String) extends Command
  final case class SwitchBehavior(
                                   name: String,
                                   behavior: Behavior[Command],
                                   durationOpt: Option[FiniteDuration] = None,
                                   timeOut: TimeOut = TimeOut("busy time error")
                                 ) extends Command
  /**消息配置*/



  /**基础配置*/
  private final val InitTime = Some(5.minutes)

  def create(origin:Int,boardName:String):Behavior[Command] = {
    Behaviors.setup[Command]{
      ctx =>
        log.debug(s"${ctx.self.path} board=${origin+"-"+boardName} is starting...")
        implicit val stashBuffer = StashBuffer[Command](Int.MaxValue)
        Behaviors.withTimers[Command]{ implicit timer =>
          switchBehavior(ctx,"busy",busy(),InitTime,TimeOut("init"))
        }
    }
  }

  private def idle(boardInfo:SlickTables.rBoard,
                   toDayPosts:Int = 0)
                  (
                    implicit stashBuffer:StashBuffer[Command],
                    timer:TimerScheduler[Command]
                  ):Behavior[Command] = {
    Behaviors.immutable[Command]{ (ctx,msg) =>
      msg match {
        case msg:GetTopicInfoReqMsg=>
          getPost(ctx,msg.origin,msg.board,msg.topicId) ! msg
          Behaviors.same

        case x=>
          log.warn(s"unknown msg: $x")
          Behaviors.unhandled
      }
    }
  }

  private def busy()(
    implicit stashBuffer:StashBuffer[Command],
    timer:TimerScheduler[Command]
  ): Behavior[Command] =
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case SwitchBehavior(name, behavior,durationOpt,timeOut) =>
          switchBehavior(ctx,name,behavior,durationOpt,timeOut)

        case TimeOut(m) =>
          log.debug(s"${ctx.self.path} is time out when busy,msg=${m}")
          Behaviors.stopped

        case unknowMsg =>
          stashBuffer.stash(unknowMsg)
          Behavior.same

      }
    }

  private[this] def switchBehavior(ctx: ActorContext[Command],
                                   behaviorName: String, behavior: Behavior[Command], durationOpt: Option[FiniteDuration] = None,timeOut: TimeOut  = TimeOut("busy time error"))
                                  (implicit stashBuffer: StashBuffer[Command],
                                   timer:TimerScheduler[Command]) = {
    timer.cancel(BehaviorChangeKey)
    durationOpt.foreach(timer.startSingleTimer(BehaviorChangeKey,timeOut,_))
    stashBuffer.unstashAll(ctx,behavior)
  }

  private def getPost(ctx: ActorContext[Command],origin:Int,boardName:String,topicId:Long):ActorRef[PostActor.Command] = {
    val childName = s"${origin}_$boardName"
    ctx.child(childName).getOrElse{
      ctx.spawn(PostActor.create(origin,boardName,topicId),childName)
    }.upcast[PostActor.Command]
  }
}
