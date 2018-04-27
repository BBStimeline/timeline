package com.neo.sk.timeline.shared.ptcl

import com.neo.sk.timeline.shared.ptcl.PostProtocol.TopicInfo

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
                                topicId: Long
                              )

  case class AddFollowUserReq(
                               userId:String,
                               userName:String,
                               origin:Int
                             )

  case class UnFollowBoardReq(
                               origin: Int, //数据来源，smth:水木社区，smthPlus：水木plus自有版面
                               boardName: String,
                               boardTitle:String
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
                          normalPost: Option[List[FeedPost]],
                          errCode: Int = 0,
                          msg: String = "ok"
                        )

  case class FeedPost(
                       post: TopicInfo,
                       time: Long
                     )

  case class LastTimeRsp(
                        first:Option[(Long,Long)],
                        errCode:Int=0,
                        msg:String="OK"
                        )

  case class GetHotBoardsListRsp(
                                hotBoards:Option[List[(Int,String,String)]],
                                myBoards :Option[List[(Int,String,String)]],
                                myUsers:Option[List[(Int,String,String)]],
                                myTopic:Option[List[(Int,String,Long)]],
                                errCode:Int=0,
                                msg:String="OK"
                                )
}
