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
import com.neo.sk.timeline.Boot.{executor, userManager, distributeManager}
import com.neo.sk.timeline.ptcl.UserProtocol.PostBaseInfo

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

  private val addFollowTopic = (path("addFollowTopic") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, AddFollowTopicReq]]) {
      case Right(req) =>
        UserAction{ u =>
          dealFutureResult {
            FollowDAO.checkFollowTopic(u.uid, req.origin, req.boardName, req.topicId).map {
              e =>
                if(e) {
                  log.info(s"user${u.uid} add follow topic exception: topic has been followed")
                  complete(ErrorRsp(130006, "已经关注过了"))
                }
                else {
                  dealFutureResult {
                    FollowDAO.addFollowTopic(SlickTables.rUserFollowTopic(-1l, u.uid, req.boardName, req.topicId,System.currentTimeMillis(), 0, req.origin)).map { r =>
                      userManager ! UserFollowTopicMsg(u.uid, PostBaseInfo(req.origin, req.boardName, req.topicId,req.time))
                      complete(SuccessRsp())
                    }.recover {
                      case e: Exception =>
                        log.info(s"user${u.uid} add follow topic exception.." + e.getMessage)
                        complete(ErrorRsp(130006, "add follow topic error."))
                    }
                  }
                }
            }
          }
        }
      case Left(e) =>
        complete(ErrorRsp(130007, "parse error."))
    }
  }

  private val addFollowUser = (path("addFollowUser") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, AddFollowUserReq]]) {
      case Right(req) =>
        UserAction{ u =>
          dealFutureResult {
            FollowDAO.isUserFollow(u.uid, req.userId,req.origin).map{ exists =>
              if(exists){
                complete(ErrorRsp(130016, "已经关注过了"))
              } else {
                dealFutureResult {
                  FollowDAO.addFollowUser(SlickTables.rUserFollowUser(-1l, u.uid, req.userId, req.userName,
                    System.currentTimeMillis(), req.origin,0)).map { _ =>
                    userManager ! UserFollowUserMsg(u.uid,req.userId,req.userName,req.origin)
                    complete(SuccessRsp())
                  }.recover {
                    case e: Exception =>
                      log.info(s"user${u.uid} add follow user exception.." + e.getMessage)
                      complete(ErrorRsp(130008, "add follow user error."))
                  }
                }
              }
            }
          }
        }
      case Left(e) =>
        complete(ErrorRsp(130009, "parse error."))
    }
  }

  private val unFollowBoard = (path("unFollowBoard") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, UnFollowBoardReq]]) {
      case Right(req) =>
        UserAction{ u =>
          dealFutureResult {
            FollowDAO.unFollowBoard(u.uid, req.origin, req.boardName).map { r =>
              userManager ! UserUnFollowBoardMsg(u.uid, req.boardName, req.origin)
              complete(SuccessRsp())
            }.recover {
              case e: Exception =>
                log.info(s"user${u.uid} unFollowBoard exception.." + e.getMessage)
                complete(ErrorRsp(130015, "unFollowBoard error."))
            }
          }
        }

      case Left(e) =>
        complete(ErrorRsp(130014, "parse error."))
    }
  }

  private val unFollowTopic = (path("unFollowTopic") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, UnFollowTopicReq]]) {
      case Right(req) =>
        UserAction{ u =>
          dealFutureResult {
            FollowDAO.unFollowTopic(u.uid, req.origin, req.boardName, req.topicId).map { r =>
              userManager ! UserUnFollowTopicMsg(u.uid, PostBaseInfo(req.origin, req.boardName, req.topicId,0l))
              complete(SuccessRsp())
            }.recover {
              case e: Exception =>
                log.info(s"user${u.uid} unFollowTopic exception.." + e.getMessage)
                complete(ErrorRsp(130016, "unFollowTopic error."))
            }
          }
        }

      case Left(e) =>
        complete(ErrorRsp(130017, "parse error."))
    }
  }

  private val unFollowUser = (path("unFollowUser") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, UnFollowUser]]) {
      case Right(req) =>
        UserAction{ u =>
          dealFutureResult {
            FollowDAO.unFollowUser(u.uid, req.userId).map { r =>
              userManager ! UserUnFollowUserMsg(u.uid,req.userId,req.userName,req.origin)
              complete(SuccessRsp())
            }.recover {
              case e: Exception =>
                log.info(s"user${u.uid} unFollowUser exception.." + e.getMessage)
                complete(ErrorRsp(130018, "unFollowUser error."))
            }
          }
        }
      case Left(e) =>
        complete(ErrorRsp(130019, "parse error."))
    }
  }

  val followRoutes: Route =
    pathPrefix("follow") {
      addFollowBoard ~ addFollowTopic ~ addFollowUser ~ unFollowBoard ~ unFollowTopic ~ unFollowUser
    }
}
