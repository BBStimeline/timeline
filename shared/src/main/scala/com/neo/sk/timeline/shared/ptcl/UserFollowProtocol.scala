package com.neo.sk.timeline.shared.ptcl

import com.neo.sk.timeline.shared.ptcl.PostProtocol.Post

/**
  * User: sky
  * Date: 2018/4/9
  * Time: 13:53
  */
object UserFollowProtocol {
  case class AddFollowBoardReq(
                                origin: Int, //数据来源，0:水木社区，1：水木plus自有版面
                                boardName: String,
                                boardTitle: String
                              )

  case class AddFollowTopicReq(
                                origin: Int,
                                boardName: String,
                                topicId: Long,
                                title: String,
                                time: Long,
                                author: String,
                                content: String
                              )

  case class AddFollowUserReq(
                               userId:String,
                               userName:String,
                               origin:Int
                             )

  case class UnFollowBoardReq(
                               origin: Int, //数据来源，smth:水木社区，smthPlus：水木plus自有版面
                               boardName: String,
                             )

  case class UnFollowTopicReq(
                               origin:Int,
                               boardName: String,
                               topicId: Long
                             )

  case class UnFollowUser(
                         origin:Int,
                         userId: String,
                         userName:String
                         )

  case class UserFeedRsp(
                          normalPost: List[FeedPost],
                          errCode: Int = 0,
                          msg: String = "ok"
                        )

  case class FeedPost(
                       post: Post,
                       time: Long
                     )
}
