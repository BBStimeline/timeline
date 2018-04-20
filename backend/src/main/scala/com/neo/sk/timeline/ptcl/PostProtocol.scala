package com.neo.sk.timeline.ptcl

import com.neo.sk.timeline.ptcl.UserProtocol.AuthorInfo

/**
  * User: sky
  * Date: 2018/4/11
  * Time: 13:16
  */
object PostProtocol {
  case class PostEvent(
                      origin:Int,
                      board:String,
                      topicId:Long,
                      postId:Long,
                      postTime:Long,
                      authorId:String,
                      authorName:String,
                      isMain:Boolean
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

  /**同步数据*/
  case class OrderPostInfo(
                            id: Long, //唯一标识
                            boardName: String, //板块名称
                            topicId: Long, //主贴话题id
                            postId: Long, //帖子id
                            quoteId: Long, //引文的帖子id
                            url: String, //帖子url
                            title: String, //标题
                            authorId: String, //作者id
                            nickname: String, //作者昵称
                            timestamp: Long, //发帖时间戳
                            contentText: String, //正文text
                            contentHtml: String, //正文html
                            quoteAuthor: String, //引文作者id
                            quoteTitle: String, //引文标题
                            quote: String, //引文内容
                            ip: String, //ip
                            imgs: String, //图片列表
                            hestiaImgs: String, //图片服务器url
                            mainPost: Boolean,
                            boardNameCn: String //板块中文名称
                          )

  case class OrderPostRsp(
                           posts:List[OrderPostInfo],
                           errCode:Int,
                           msg:String
                         )

}
