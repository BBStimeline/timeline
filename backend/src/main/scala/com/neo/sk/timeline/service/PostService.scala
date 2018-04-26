package com.neo.sk.timeline.service

import akka.http.scaladsl.server.Directives._
import com.neo.sk.timeline.core.user.UserManager._

import scala.concurrent.Future
import com.neo.sk.timeline.shared.ptcl.{ErrorRsp, SuccessRsp}
import org.slf4j.LoggerFactory
import akka.pattern.ask
import io.circe.Error
import io.circe.generic.auto._
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.neo.sk.timeline.common.AppSettings
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.models.dao.{BoardDAO, PostDAO, TopicDAO, UserDAO}
import com.neo.sk.timeline.service.ServiceUtils.CommonRsp
import com.neo.sk.timeline.service.SessionBase.UserSessionKey
import com.neo.sk.timeline.shared.ptcl.UserProtocol._
import com.neo.sk.timeline.utils.SecureUtil
import com.neo.sk.timeline.Boot.{boardManager, executor, scheduler, timeout, userManager}
import com.neo.sk.timeline.core.postInfo.BoardManager
import com.neo.sk.timeline.core.postInfo.BoardManager.{GetPostList, GetTopicList}
import com.neo.sk.timeline.core.user.UserManager
import com.neo.sk.timeline.shared.ptcl.PostProtocol._
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.{FeedPost, GetHotBoardsListRsp, LastTimeRsp, UserFeedRsp}

import scala.concurrent.duration._
/**
  * User: sky
  * Date: 2018/4/26
  * Time: 17:10
  */
trait PostService extends ServiceUtils with SessionBase{
  private val log = LoggerFactory.getLogger(this.getClass)

  private val postList=(path("getPostList") & post & pathEndOrSingleSlash) {
    UserAction{u=>
      entity(as[Either[Error, GetPostListReq]]) {
        case Right(req) =>
          val future1:Future[List[SlickTables.rPosts]] =boardManager ? (GetPostList(req.origin,req.board,req.topicId,_))
          dealFutureResult(
            future1.map { ts =>
              val data = ts.map { p =>
                PostInfo(p.origin,p.boardName,p.boardNameCn,p.postId,p.topicId,p.quoteId,p.title,img2ImgList(p.imgs),img2ImgList(p.hestiaImgs),p.content,
                  AuthorInfo(p.authorId,p.authorName,p.origin),p.postTime,isMain = p.isMain
                )
              }
              complete(GetPostListRsp(Some(data)))
            }
          )
      }
    }
  }

  def img2ImgList(img:String)={
    img.split(";").toList
  }

  val postRoutes: Route =
    pathPrefix("post") {
      postList
    }

}
