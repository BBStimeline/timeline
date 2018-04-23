package com.neo.sk.timeline.core.postInfo

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.timeline.Boot.{distributeManager, executor, scheduler, timeout}
import com.neo.sk.timeline.core.DistributeManager
import com.neo.sk.timeline.core.postInfo.PostActor
import com.neo.sk.timeline.ptcl.UserProtocol.UserFeedReq
import com.neo.sk.timeline.ptcl.PostProtocol.PostEvent
import com.neo.sk.timeline.shared.ptcl.PostProtocol.{Post}
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.{FeedPost, UserFeedRsp}
import org.slf4j.LoggerFactory
import com.neo.sk.timeline.models.SlickTables.rPosts

import scala.concurrent.Future
/**
  * User: sky
  * Date: 2018/4/14
  * Time: 18:00
  */
object BoardManager {
  val log = LoggerFactory.getLogger(this.getClass)
  sealed trait Command
  final case class GetTopicList(req:List[UserFeedReq],replyTo:ActorRef[UserFeedRsp]) extends Command
  final case class InsertPostList(list:List[rPosts]) extends Command

  /**通用消息*/
  case class GetTopicInfoReqMsg(origin:Int,board:String,topicId:Long,postId:Long,replyTo:ActorRef[Option[GetTopicInfoRsp]]) extends Command with BoardActor.Command with PostActor.Command
  case class GetTopicInfoRsp(topic:Post)/*extends Command with BoardActor.Command with PostActor.Command*/

  val behavior: Behavior[Command] = init()

  private def init(): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx,msg) =>
      msg match {
        case msg:GetTopicList=>
          val rspFuture = Future.sequence(msg.req.map{t =>getBoard(ctx,t.origin,t.board) ? (GetTopicInfoReqMsg(t.origin,t.board,t.topicId,t.postId,_:ActorRef[Option[GetTopicInfoRsp]]))})
          rspFuture.map{topics=>
            var normalPost:List[FeedPost]=Nil
            topics.foreach{t=>if(!t.isEmpty) normalPost::=FeedPost(t.get.topic,t.get.topic.postTime)}
            msg.replyTo ! UserFeedRsp(normalPost.sortBy(_.time).reverse)
          }
          Behaviors.same

        case msg:InsertPostList=>
          msg.list.foreach{ p=>
            getBoard(ctx,p.origin,p.boardName) ! BoardActor.InsertPost(p)
//            distributeManager ! DistributeManager.DealTask(PostEvent(p.origin,p.boardName,p.topicId,p.postId,p.postTime,p.authorId,p.authorName,p.isMain))
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
