package com.neo.sk.timeline.ptcl

import com.neo.sk.timeline.ptcl.UserProtocol.AuthorInfo

/**
  * User: sky
  * Date: 2018/4/11
  * Time: 13:16
  */
object PostProtocol {
  case class PostEvent(
                      eventAction:Int,
                      eventData:String
                      )

  case class AddPost(
                      origin:Int,
                      boardName:String,
                      postId:Long,
                      topicId:Long,
                      postTime:Long,
                      author:AuthorInfo,
                      title:String,
                      content:String,
                      imgs:String,
                      hestiaImgs:String,
                      boardNameCn:String
                    )

  case class AddComment(
                         origin:String,
                         boardName:String,
                         postId:Long,
                         topicId:Long,
                         quoteId:Long,
                         postTime:Long,
                         author:AuthorInfo,
                         replyAuthor:AuthorInfo,
                         title:String,
                         content:String,
                         imgs:String,
                         hestiaImgs:String,
                         boardNameCn:String
                       )

  case class
}
