package com.neo.sk.timeline.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import org.slf4j.LoggerFactory
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.timeline.Boot.{executor, timeout}
import com.neo.sk.timeline.ptcl.UserProtocol.UserFeedReq
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.{FeedPost, UserFeedRsp}
import com.neo.sk.timeline.shared.ptcl.PostProtocol.Post

import scala.concurrent.Future
import scala.util.{Failure, Success}
/**
  * User: sky
  * Date: 2018/4/14
  * Time: 18:00
  */
object BoardManager {
  val log = LoggerFactory.getLogger(this.getClass)
  sealed trait Command
  final case class GetTopicList(req:List[UserFeedReq],replyTo:ActorRef[UserFeedRsp]) extends Command

  /**通用消息*/
  case class GetTopicInfoReqMsg(origin:Int,board:String,topicId:Long,postId:Long,replyTo:ActorRef[GetTopicInfoRsp]) extends Command with BoardActor.Command with PostActor.Command
  case class GetTopicInfoRsp(topic:Post)/*extends Command with BoardActor.Command with PostActor.Command*/

  val behavior: Behavior[Command] = init()

  private def init(): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx,msg) =>
      msg match {
        case msg:GetTopicList=>
          val rspFuture = Future.sequence(msg.req.map{t =>getBoard(ctx,t.origin,t.board) ? (GetTopicInfoReqMsg(t.origin,t.board,t.topicId,t.postId,_:ActorRef[GetTopicInfoRsp]))})
          rspFuture.map{topics=>
            msg.replyTo ! UserFeedRsp(normalPost = topics.map(r=>FeedPost(r.topic,r.topic.postTime)))
          }
          Behaviors.same

        case x=>
          log.warn(s"unknown msg: $x")
          Behaviors.unhandled
      }
    }
  }

  private def getBoard(ctx: ActorContext[Command],origin:Int,boardName:String):ActorRef[BoardActor.Command] = {
    val childName = s"${origin}_$boardName"
    ctx.child(childName).getOrElse{
      ctx.spawn(BoardActor.create(origin,boardName),childName)
    }.upcast[BoardActor.Command]
  }


}
