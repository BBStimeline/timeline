package com.neo.sk.timeline.ptcl

import com.neo.sk.timeline.ptcl.UserProtocol.AuthorInfo

import scala.collection.mutable
/**
  * User: sky
  * Date: 2018/4/8
  * Time: 16:59
  */
object DistributeProtocol {
  case class DisType(
                    board:Option[String]=None,
                    topicId:Option[Long]=None,
                    userId:Option[String]=None,
                    userName:Option[String]=None,
                    origin:Int
                    )

  case class DisCache(
                       newPost:mutable.HashMap[(Int,String,Long,Long),(Long,Long,Option[AuthorInfo])]=mutable.HashMap(),
                       newReplyPost:mutable.HashMap[(Int,String,Long,Long),(Long,Long,Option[AuthorInfo])]=mutable.HashMap(),//_.4目前没有价值
                       followList:mutable.HashSet[Long]=mutable.HashSet(),
                       name:String,
                       variety:Int
                     )

  case class FeedListInfo(
                           feedType:Int,
                           newPosts: List[(Int, String, Long, Long, Long,Long,Option[AuthorInfo])],  //origin, boardName, topicId,postTime, postId, replyPostTime
                           newReplyPosts: List[(Int, String, Long, Long ,Long,Long,Option[AuthorInfo])]
                         )

}
