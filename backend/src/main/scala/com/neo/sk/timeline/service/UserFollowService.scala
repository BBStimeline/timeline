package com.neo.sk.timeline.service

import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.neo.sk.timeline.core.UserManager._

import scala.concurrent.Future
import com.neo.sk.timeline.shared.ptcl.{ErrorRsp, SuccessRsp}
import org.slf4j.LoggerFactory
import io.circe.Error
import io.circe.generic.auto._
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.server.Route
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.models.dao.{FollowDAO, UserDAO}
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol._
import com.neo.sk.timeline.utils.SecureUtil
import com.neo.sk.timeline.Boot.{executor, userManager}

import scala.concurrent.duration._

/**
  * User: sky
  * Date: 2018/4/9
  * Time: 13:46
  */
trait UserFollowService extends ServiceUtils with SessionBase{
  private val log = LoggerFactory.getLogger(this.getClass)

  private val addFollowBoard = (path("addFollowBoard") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, AddFollowBoardReq]]) {
      case Right(req) =>
        UserAction{ u =>
          dealFutureResult {
            FollowDAO.checkFollowBoard(u.uid, req.origin, req.boardName).map {
              e =>
                if(e) {
                  log.info(s"user${u.uid} add follow board exception: board has been followed..")
                  complete(ErrorRsp(130004, "已经关注过了"))
                } else {
                  dealFutureResult {
                    FollowDAO.addFollowBoard(
                      SlickTables.rUserFollowBoard(-1l, u.uid, req.boardName, req.boardTitle, System.currentTimeMillis(), 0, req.origin)
                    ).map { r =>
                      userManager ! UserFollowBoardMsg(u.uid, req.boardName, req.origin)
                      complete(SuccessRsp())
                    }.recover {
                      case e: Exception =>
                        log.info(s"user${u.uid} add follow board exception.." + e.getMessage)
                        complete(ErrorRsp(130004, "add follow board error."))
                    }
                  }
                }
            }
          }
        }
      case Left(e) =>
        complete(ErrorRsp(130005, "parse error."))
    }
  }

//  private val addFollowUser = (path("addFollowUser") & post & pathEndOrSingleSlash) {
//    entity(as[Either[Error, AddFollowUserReq]]) {
//      case Right(req) =>
//        UserAction{ u =>
//          dealFutureResult {
//            FollowDAO.isUserFollow(u.uid, req.userId,req.origin).map{ exists =>
//              if(exists){
//                complete(ErrorRsp(130016, "已经关注过了"))
//              } else {
//                dealFutureResult {
//                  val future: Future[BaseUserInfo] = serviceActor ? (GetUserAccount(req.bbsId, _))
//                  future.map { data =>
//                    if (data.plusUserInfo.nonEmpty && data.bbsInfo.nonEmpty) {
//                      dealFutureResult {
//                        val bbsUser = data.bbsInfo.get
//                        FollowDAO.addFollowUser(SlickTables.rUserFollowUser(-1l, u.uid, req.bbsId, bbsUser.bbsName, bbsUser.faceUrl,
//                          System.currentTimeMillis(), 0, data.plusUserInfo.get.uid)).map { _ =>
//                          distributeManager ! DistributeManager.AddFollowedUser(data.plusUserInfo.get.uid, req.bbsId)
//                          userManager ! UserFollowUserMsg(u.uid,
//                            List(AuthorInfoWithType(data.plusUserInfo.get.uid.toString, UserType.SMTHPLUS), AuthorInfoWithType(u.bbsId, UserType.SMTH)))
//                          complete(AddFollowUserRsp(data.plusUserInfo.get.uid))
//                        }.recover {
//                          case e: Exception =>
//                            log.info(s"user${u.uid} add follow user exception.." + e.getMessage)
//                            complete(ErrorRsp(130008, "add follow user error."))
//                        }
//                      }
//                    } else {
//                      complete(ErrorRsp(130008, "add follow user error."))
//                    }
//                  }
//                }
//              }
//            }
//          }
//
//        }
//      case Left(e) =>
//        complete(ErrorRsp(130009, "parse error."))
//    }
//  }

  val followRoutes: Route =
    pathPrefix("follow") {
      addFollowBoard /*~ addFollowUser*/
    }
}
