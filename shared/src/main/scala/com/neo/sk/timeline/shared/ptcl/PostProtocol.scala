package com.neo.sk.timeline.shared.ptcl

/**
  * User: sky
  * Date: 2018/4/15
  * Time: 15:10
  */
object PostProtocol {
  case class PostStatistics(
                             voteUpNum:Int,//顶数
                             voteDownNum:Int,//踩数
                             //下面的只有主贴才统计，评论不参与下列统计
                             replyAuthorNum:Int,//回复用户数
                             replyPostNum:Int//回复帖子数
                           )

  case class AuthorInfo(
                         authorId:Long,
                         authorName:String,
                         origin:Int // 0：水木本身发帖用户 ,1水木plus用户
                       )

  case class Post(
                   origin:Int,
                   boardName:String,
                   boardNameCn:String,
                   postId:Long,
                   topicId:Long,
                   tittle: String,
                   imgs:List[String],
                   hestiaImg:List[String],
                   content:Option[String],//文章内容
                   author: AuthorInfo,
                   postTime:Long,
                   statistics:Option[PostStatistics]=None,
                   myVote:Option[Int]=None,//0:没有投票，1：顶，-1：踩
                   isAttach: Option[Boolean]=None,
                   isDelete:Option[Boolean]=None,
                   isMain:Boolean
                 )

}
