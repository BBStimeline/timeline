package com.neo.sk.timeline.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import org.slf4j.LoggerFactory
import akka.actor.typed.{ActorRef, Behavior}
/**
  * User: sky
  * Date: 2018/4/9
  * Time: 15:16
  */
object BoardManager {
  val log = LoggerFactory.getLogger(this.getClass)

  sealed trait Command

  val behavior: Behavior[Command] = idle()

  def idle(): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case x=>
          log.warn(s"unknown msg: $x")
          Behaviors.unhandled
      }
    }
  }

  private def getBoardActor(ctx: ActorContext[Command], board:String, origin:Int) = {
    val childName = s"boardActor--$origin--$board"
    ctx.child(childName).getOrElse {
      ctx.spawn(BoardActor.init(board,origin), childName)
    }.upcast[UserActor.Command]
  }
}
