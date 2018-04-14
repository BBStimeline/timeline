package com.neo.sk.timeline.shared.ptcl

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
                               userId:Long,
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
                         userId: Long,
                         userName:String
                         )
}
