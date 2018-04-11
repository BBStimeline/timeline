package com.neo.sk.timeline.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, TimerScheduler}
import org.slf4j.LoggerFactory
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.timeline.ptcl.UserProtocol.UserFeedReq
import com.neo.sk.timeline.shared.ptcl.UserProtocol.UserLoginRsp
/**
  * User: sky
  * Date: 2018/4/8
  * Time: 11:11
  */
object UserManager {
  val log = LoggerFactory.getLogger(this.getClass)

  sealed trait Command extends UserActor.Command

  final case class UserLogout(uid: Long, replyTo:ActorRef[String]) extends Command
//
//  final case class PostArt(uid: Long, boardName: String, subject: String, content: String, replyTo:ActorRef[String]) extends Command
//
//  final case class PostReply(uid: Long, boardName: String, artNum: Long, subject: String, content: String, replyTo:ActorRef[String]) extends Command
//
//  final case class AddUserFeed(uid: Long, post:PostBaseInfo, author:AuthorInfo, createTime: Long, feedType: String) extends Command
//
//  final case class UpdateUserFeed(uid: Long, post:PostBaseInfo, author:AuthorInfo, lastReplyTime: Long, feedType: String) extends Command
//
  final case class GetUserFeed(uid: Long, sortType: Int, lastItemTime: Long, pageSize: Int, replyTo:ActorRef[Option[List[UserFeedReq]]]) extends Command
//
//  final case class UserFollowUserMsg(uid: Long, authorList: List[AuthorInfoWithType]) extends Command
//
  final case class UserFollowBoardMsg(uid: Long, boardName: String, origin:Int) extends Command
//
//  final case class UserFollowTopicMsg(uid: Long, post:PostBaseInfo, author:AuthorInfo, postTime: Long) extends Command
//
//  final case class UserUnFollowUserMsg(uid: Long, authorList: List[AuthorInfoWithType]) extends Command
//
//  final case class UserUnFollowBoardMsg(uid: Long, boardName: String, origin: String) extends Command
//
//  final case class UserUnFollowTopicMsg(uid: Long, post:PostBaseInfo) extends Command
//
//  final case class DeleteArt(uid: Long, boardName: String, postId: Long, replyTo:ActorRef[String]) extends Command
//
//  final case class EditArt(uid: Long, boardName: String, postId: Long, subject: String, content: String, replyTo:ActorRef[String]) extends Command

  private def getUserActor(ctx: ActorContext[Command], uid: Long) = {
    val childName = s"userActor-$uid"
    ctx.child(childName).getOrElse {
      ctx.spawn(UserActor.init(uid), childName)
    }.upcast[UserActor.Command]
  }

  val behavior: Behavior[Command] = idle()

  def idle(): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case UserLogout(uid,_)=>
          getUserActor(ctx,uid) ! msg
          Behaviors.same

        case UserFollowBoardMsg(uid,_,_)=>
          getUserActor(ctx,uid) ! msg
          Behaviors.same

        case msg:GetUserFeed=>
          getUserActor(ctx,msg.uid) ! msg
          Behaviors.same
        case x=>
          log.warn(s"unknown msg: $x")
          Behaviors.unhandled
      }
      }
    }
}
