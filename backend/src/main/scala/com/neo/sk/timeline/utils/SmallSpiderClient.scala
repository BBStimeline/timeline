package com.neo.sk.timeline.utils

import com.neo.sk.timeline.common.AppSettings
import com.neo.sk.timeline.ptcl.PostProtocol.OrderPostRsp
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * User: sky
  * Date: 2018/4/19
  * Time: 10:04
  */
object SmallSpiderClient extends HttpUtil{
  private val appId = AppSettings.smallSpiderAppId
  private val secureKey = AppSettings.smallSpiderSecureKey
  private val basePath = AppSettings.smallSpiderProtocol+"://"+AppSettings.smallSpiderDomain

  private val log = LoggerFactory.getLogger(this.getClass)

  case class AuthData(
                        appId: String, sn: String,
                        timestamp: String, nonce: String,
                        signature: String, data: String
                      )

/*  case class UserRequest(
                          userId: String,
                          onlyTopic: Int = 0, //1为主贴
                          page: Int = 1,
                          pageSize: Int = 30
                        )

  case class SpiderPost(
                         boardName: String, //板块名称
                         topicId: Long, //主贴话题id
                         postId: Long, //帖子id
                         quoteId: Long, //引文的帖子id
                         title: String, //标题
                         authorId: String, //作者id
                         postTime: String, //发帖时间
                         timestamp: Long, //发帖时间戳
                         content: String, //正文
                         quoteAuthor: String, //引文作者id
                         quoteTitle: String, //引文标题
                         quote: String, //引文内容
                         ip: String, //ip
                         img: List[String], //图片列表
                         mainPost: Boolean,
                         boardNameCn: String
                       )

  case class Pagination(
                         itemAllCount: Int,
                         itemPageCount: Int,
                         pageAllCount: Int, //总页数
                         pageCurrentCount: Int //当前页数
                       )

  case class Author(
                     userId: String,
                     userName: String,
                     userImage: String,
                     gender: Char
                   )

  case class SpiderUserRsp(
                            author: Author,
                            posts: List[SpiderPost],
                            errCode: Int = 0,
                            msg: String = "ok"
                          )

  case class PostSearch(
                            boardName: String,
                            postId: Long
                          )

  case class PostSearchReq(
                            posts: List[PostSearch],
                            contentType:String = "text"
                          )

  case class SpiderPostsRsp(
                            posts: List[SpiderPost],
                            errCode: Int = 0,
                            msg: String = "ok"
                          )

  case class OrderPostReq(
                         boardName:String,
                         postId:Option[Long],
                         length:Option[Int],
                         contentType:String="text"
                         )*/
  

  case class PostsListRst(
                           id:Long,
                           count:Int,
                           contentType:String = "text"
                         )

 /* def getUserPost(userId:String, page:Int, pageSize:Int = 30,onlyTopic:Int = 0) ={

    val url = basePath+"/smallspider/api/user"
    val sn = appId + System.currentTimeMillis().toString
    val data = UserRequest(
      userId,
      onlyTopic,
      page,
      pageSize
    ).asJson.noSpaces
    val (timestamp, nonce, signature) = SecureUtil.generateSignatureParameters(List(appId, sn,data), secureKey)

    val params = AuthData(appId, sn, timestamp, nonce, signature, data).asJson.noSpaces

    postJsonRequestSend(s"small spider clientUrl $url", url, Nil, params).map {
      case Right(str)=>
        decode[SpiderUserRsp](str) match{
          case Right(r)=>
            Some(r)

          case Left(e)=>
            log.error(s"parse data error $e")
            None
        }

      case Left(e)=>
        log.error(s"get user post $url failed:" + e)
        None
    }
  }

  //(boardName, postId)
  def searchPosts(postsList:List[(String, Long)]) ={

    val posts = postsList.map( i => PostSearch(i._1, i._2))

    val url = basePath+"/smallspider/api/searchPosts"
    val sn = appId + System.currentTimeMillis().toString

    val data = PostSearchReq(
      posts
    ).asJson.noSpaces

    val (timestamp, nonce, signature) = SecureUtil.generateSignatureParameters(List(appId, sn,data), secureKey)
    val params = AuthData(appId, sn, timestamp, nonce, signature, data).asJson.noSpaces

    postJsonRequestSend(s"small spider searchPosts $url", url, Nil, params).map {
      case Right(str)=>
        decode[SpiderPostsRsp](str) match{
          case Right(r)=>
            if(r.errCode == 0){
              Right(r.posts)
            } else {
              Left(r.msg)
            }

          case Left(e)=>
            log.error(s"parse data error $e")
            Left(e)
        }

      case Left(e)=>
        log.error(s"get user post $url failed:" + e)
        Left(e)
    }
  }

  def getOrderPosts(boardName:String,postId:Option[Long],length:Option[Int]) ={
    val url = basePath+"/smallspider/api/postsOfBoard"
    val sn = appId + System.currentTimeMillis().toString

    val data = OrderPostReq(
      boardName,
      postId,
      length,
      "text"
    ).asJson.noSpaces

    val (timestamp, nonce, signature) = SecureUtil.generateSignatureParameters(List(appId, sn,data), secureKey)
    val params = AuthData(appId, sn, timestamp, nonce, signature, data).asJson.noSpaces

    postJsonRequestSend(s"small spider postsOfBoard $url", url, Nil, params).map {
      case Right(str)=>
        decode[OrderPostRsp](str) match{
          case Right(r)=>
            if(r.errCode == 0){
              Right(r.posts)
            } else {
              Left(r.msg)
            }

          case Left(e)=>
            log.error(s"parse data error $e")
            Left(e)
        }

      case Left(e)=>
        log.error(s"get Board posts $url failed:" + e)
        Left(e)
    }
  }*/
  

  def getSynPosts(id:Long,count:Int) ={
    val url = basePath+"/smallspider/api/posts4Galaxy"
    val sn = appId + System.currentTimeMillis().toString
    val data = PostsListRst(id,count).asJson.noSpaces
    val (timestamp, nonce, signature) = SecureUtil.generateSignatureParameters(List(appId, sn,data), secureKey)

    val params = AuthData(appId, sn, timestamp, nonce, signature, data).asJson.noSpaces

    postJsonRequestSend(s"small spider list posts $url", url, Nil, params).map {
      case Right(str)=>
        decode[OrderPostRsp](str) match{
          case Right(r)=>
            if(r.errCode == 0){
              Right(r.posts)
            } else {
              Left(r.msg)
            }

          case Left(e)=>
            log.error(s"parse data error $e")
            Left(e)
        }

      case Left(e)=>
        log.error(s"get user post $url failed:" + e)
        Left(e)
    }
  }




  /**获取板块的帖子列表 */
 /* case class PostListData(
                           boardName:String,
                           page:Int,
                           length:Int = 30,
                           contentType:String = "text"
                         )
  case class PostListRsp(
                          onlineNum:Int,
                          postNum:Int,
                          boardMaster:String,
                          topPost:Option[List[Post]],
                          normalPost:List[Post],
                          page:Int,
                          errCode:Int,
                          msg:String
                         )

  def GetPostList(boardName:String, page:Int, length:Int = 30) = {
    val url = basePath+"/smallspider/api/postList"
    val sn = appId + System.currentTimeMillis().toString
    val data = PostListData(boardName,page,length).asJson.noSpaces
    val (timestamp, nonce, signature) =
      SecureUtil.generateSignatureParameters(List(appId, sn,data), secureKey)

    val params = AuthData(appId, sn, timestamp, nonce, signature, data).
      asJson.noSpaces

    postJsonRequestSend(s"small spider get postList $url", url, Nil, params).map {
      case Right(str)=>
        decode[PostListRsp](str) match{
          case Right(r)=>
            if(r.errCode == 0){
              Right(r)
            } else {
              Left(r.msg)
            }

          case Left(e)=>
            log.error(s"parse data error $e")
            Left(e)
        }

      case Left(e)=>
        log.error(s"get postList $url failed:" + e)
        Left(e)
    }
  }



  /**获取帖子详情*/
  case class PostDetailData(
                           boardName:String,
                           postId:Long,
                           page:Int,
                           length:Int = 30,
                           contentType:String = "text"
                         )
  case class PostDetailRsp(
                            pageNum: Int, //总页数
                            mainFloor: Option[MainFloor], //主贴
                            normalFloor: List[NormalFloor], //回帖
                            page: Int,  //当前页数
                            errCode: Int,
                            msg: String
                           )

  def GetPostDetail(boardName:String,postId:Long, page:Int, length:Int = 30) = {
    val url = basePath+"/smallspider/api/postDetail"
    val sn = appId + System.currentTimeMillis().toString
    val data = PostDetailData(boardName,postId,page,length).asJson.noSpaces
    val (timestamp, nonce, signature) =
      SecureUtil.generateSignatureParameters(List(appId, sn,data), secureKey)

    val params = AuthData(appId, sn, timestamp, nonce, signature, data).
      asJson.noSpaces

    postJsonRequestSend(s"small spider get postDetail $url", url, Nil, params).map {
      case Right(str)=>
        decode[PostDetailRsp](str) match{
          case Right(r)=>
            if(r.errCode == 0){
              Right(r)
            } else {
              Left(r.msg)
            }

          case Left(e)=>
            log.error(s"parse data error $e")
            Left(e)
        }

      case Left(e)=>
        log.error(s"get postDetail $url failed:" + e)
        Left(e)
    }
  }


  /**获取帖子详情*/
  case class PostDetailData2(
                             boardName:String,
                             postId:Long,
                             page:Int,
                             length:Int = 30,
                             contentType:String = "text"
                           )
  case class PostDetailRsp2(
                            pageNum: Int, //总页数
                            mainFloor: Option[MainFloor], //主贴
                            normalFloor: List[TreeNormalFloor], //回帖
                            page: Int,  //当前页数
                            errCode: Int,
                            msg: String
                          )

  def GetPostDetail2(boardName:String,postId:Long, page:Int, length:Int = 30) = {
    val url = basePath+"/smallspider/api/postDetail"
    val sn = appId + System.currentTimeMillis().toString
    val data = PostDetailData(boardName,postId,page,length).asJson.noSpaces
    val (timestamp, nonce, signature) =
      SecureUtil.generateSignatureParameters(List(appId, sn,data), secureKey)

    val params = AuthData(appId, sn, timestamp, nonce, signature, data).
      asJson.noSpaces

    postJsonRequestSend(s"small spider get postDetail2 $url", url, Nil, params).map {
      case Right(str)=>
        decode[PostDetailRsp2](str) match{
          case Right(r)=>
            if(r.errCode == 0){
              Right(r)
            } else {
              Left(r.msg)
            }

          case Left(e)=>
            log.error(s"parse data error $e")
            Left(e)
        }

      case Left(e)=>
        log.error(s"get postDetail $url failed:" + e)
        Left(e)
    }
  }

  def refreshPost(boardName: String, postId: Long) = {
    val url = basePath+"/smallspider/api/refreshPost"
    val sn = appId + System.currentTimeMillis().toString
    val data = PostSearch(boardName,postId).asJson.noSpaces
    val (timestamp, nonce, signature) =
      SecureUtil.generateSignatureParameters(List(appId, sn,data), secureKey)

    val params = AuthData(appId, sn, timestamp, nonce, signature, data).
      asJson.noSpaces

    postJsonRequestSend(s"small spider get refreshPost $url", url, Nil, params).map {
      case Right(str)=>
        decode[SuccessRsp](str) match{
          case Right(r)=>
            if(r.errCode == 0){
              Right(r)
            } else {
              Left(r.msg)
            }

          case Left(e)=>
            log.error(s"parse data error $e")
            Left(e)
        }

      case Left(e)=>
        log.error(s"get postDetail $url failed:" + e)
        Left(e)
    }
  }*/


  def main(args: Array[String]): Unit = {
   getSynPosts(1062976988l,2).map{r=>
     r.map{ps=>
       ps.map{t=>
//         println(t.topicId)
//         println(t.postId)
//         println(t.mainPost)
//         println(t.title)
//         println(t.authorId)
//         println(t.nickname)
//         println(t.contentText)
//         println(t.imgs)
//         println(t.hestiaImgs)
//         println(t.timestamp)
//         println(t.id)
         println(t)
       }
     }
   }
    Thread.sleep(10000)
  }

}
