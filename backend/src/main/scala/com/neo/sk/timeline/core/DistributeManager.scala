package com.neo.sk.timeline.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import org.slf4j.LoggerFactory
import akka.actor.typed.{ActorRef, Behavior}
import scala.collection.mutable
import com.neo.sk.timeline.ptcl.DistributeProtocol.DisType
/**
  * User: sky
  * Date: 2018/4/9
  * Time: 15:16
  */
object DistributeManager {
  val log = LoggerFactory.getLogger(this.getClass)

  sealed trait Command
  final case class NotifyFollowObject(name:String,variety:Int,userId:Long,param:DisType) extends Command with DistributeActor.Command

  final case class AddPost()

  private val objectHash:mutable.HashMap[(String,Int),DisType]=mutable.HashMap() //(name,type)

  val behavior: Behavior[Command] = idle()

  def idle(): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case msg:NotifyFollowObject=>
          objectHash.put((msg.name,msg.variety),msg.param)
          getDistributeActor(ctx,msg.name,msg.variety,msg.param) ! msg
          Behaviors.same
        case x=>
          log.warn(s"unknown msg: $x")
          Behaviors.unhandled
      }
    }
  }

  private def getDistributeActor(ctx: ActorContext[Command], name:String, variety:Int,param:DisType) = {
    val childName = s"distributeActor--$variety--$name"
    ctx.child(childName).getOrElse {
      ctx.spawn(DistributeActor.init(variety,param), childName)
    }.upcast[DistributeActor.Command]
  }
}
