package com.neo.sk.timeline.ptcl

import scala.collection.mutable

/**
  * User: sky
  * Date: 2018/4/8
  * Time: 17:00
  */
object UserProtocol {

  case class UserActorInfo(
                            uid: Long,
                            userId: String,
                            bbsId: String,
                            headImg: String,
                            favBoards: List[(Int, String)],
                            favUsers: List[Long],
                            favTopic: List[(Int, String, Long)],
                            newFeed: mutable.Queue[((String, PostBaseInfo), (Long, AuthorInfo))] = mutable.Queue(),
                            newReplyFeed: mutable.Queue[((String, PostBaseInfo), (Long, AuthorInfo))] = mutable.Queue()
                          )

  case class AuthorInfo(
                         authorId:String,
                         nickname:Option[String],
                         authorType:Int // 0：水木本身发帖用户 ,1水木plus用户
                       )

  case class PostBaseInfo(
                           origin:Int,
                           boardName:String,
                           postId:Long
                         )

  case class UserFeedReq(
                          post: PostBaseInfo,
                          time: Long
                        )
}
