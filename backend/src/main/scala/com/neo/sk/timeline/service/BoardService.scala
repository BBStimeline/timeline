package com.neo.sk.timeline.service

import akka.http.scaladsl.server.Directives._
import com.neo.sk.timeline.core.user.UserManager._

import scala.concurrent.Future
import com.neo.sk.timeline.ptcl.UserProtocol._
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
import com.neo.sk.timeline.core.postInfo.BoardManager.GetTopicList
import com.neo.sk.timeline.core.user.UserManager
import com.neo.sk.timeline.shared.ptcl.PostProtocol.{AuthorInfo, Post}
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.{FeedPost, GetHotBoardsListRsp, LastTimeRsp, UserFeedRsp}

import scala.concurrent.duration._
/**
  * User: sky
  * Date: 2018/4/26
  * Time: 13:56
  */
trait BoardService extends ServiceUtils with SessionBase{
  private val log = LoggerFactory.getLogger(this.getClass)

  private val hotBoards=(path("hotBoards") & get & pathEndOrSingleSlash) {
    UserAction{u=>
      val future1:Future[List[(Int,String)]] = userManager ? (GetUserFollowBoard(u.uid,_))
      val future2=TopicDAO.getHotBoard
      dealFutureResult(
        for{
          myBoards<- future1
          hotBoards<- future2
        } yield {
          val boards=myBoards.map(r=>r).toSet++:hotBoards.map(_._1).toSet
          dealFutureResult(
            BoardDAO.getBoardList(boards.toSeq).map{bs=>
              val hotBs=hotBoards.map(r=>bs.filter(b=>b._1==r._1._1&&b._2==r._1._2).head).toList
              val myBs=myBoards.map(r=>bs.filter(b=>b._1==r._1&&b._2==r._2).head)
              complete(GetHotBoardsListRsp(Some(hotBs),Some(myBs)))
            }
          )
        }
      )
    }
  }

  val boardRoutes: Route =
    pathPrefix("board") {
      hotBoards
    }

}
