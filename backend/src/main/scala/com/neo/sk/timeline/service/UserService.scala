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
import com.neo.sk.timeline.models.dao.UserDAO
import com.neo.sk.timeline.service.ServiceUtils.CommonRsp
import com.neo.sk.timeline.service.SessionBase.UserSessionKey
import com.neo.sk.timeline.shared.ptcl.UserProtocol._
import com.neo.sk.timeline.utils.SecureUtil
import com.neo.sk.timeline.Boot.{boardManager, executor, scheduler, timeout, userManager}
import com.neo.sk.timeline.core.postInfo.BoardManager
import com.neo.sk.timeline.core.postInfo.BoardManager.GetTopicList
import com.neo.sk.timeline.core.user.UserManager
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.UserFeedRsp

import scala.concurrent.duration._

/**
  * User: sky
  * Date: 2018/4/9
  * Time: 10:04
  */
trait UserService extends ServiceUtils with SessionBase{
  private val log = LoggerFactory.getLogger(this.getClass)

  private val userIndex:Route=(path ("index") & get){
    getFromResource("html/index.html")
  }

  private val userSign = (path("userSign") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, UserSignReq]]) {
      case Right(req) =>
        val now=System.currentTimeMillis()
        dealFutureResult(
          UserDAO.isUserExist(req.userId).map(r=>
            if(r.isEmpty){
              val sha1Pwd=SecureUtil.getSecurePassword(req.userId,req.pwd)
              dealFutureResult(
                UserDAO.addUser(req.userId,now,"",req.img,req.city,req.gender,sha1Pwd).map { t =>
                  if (t > 0l){
                    val (sessionKey,keyCode,signature)=SecureUtil.appSafety
                    val session = Map(
                      SessionBase.SessionTypeKey -> UserSessionKey.SESSION_TYPE,
                      UserSessionKey.uid -> t.toString,
                      UserSessionKey.userId -> req.userId,
                      UserSessionKey.bbsId -> "guest",
                      UserSessionKey.loginTime -> System.currentTimeMillis().toString,
                      UserSessionKey.keyCode -> keyCode,
                      UserSessionKey.signature -> signature
                    )
                    val headImg=if(r.get.headImg=="") AppSettings.defaultHeadImg else r.get.headImg
                    val userDetail=UserInfoDetail(r.get.id,r.get.userId,r.get.bbsId,headImg)
                    dealFutureResult(
                      UserDAO.updateSession(t,sessionKey).map{u=>
                        if(u>0){
                          setSession(session){ ctx =>
                            userManager ! UserManager.UserLogin(t)
                            ctx.complete(UserSignRsp(Some(userDetail),0, "Ok"))
                          }
                        }else{
                          complete(ErrorRsp(10001,"更新用户签名失败"))
                        }
                      }
                    )
                  }  else complete(ErrorRsp(10001, "注册失败"))
                }
              )
            }else{
              complete(ErrorRsp(10002,"本昵称已被使用"))
            }
          )
        )
      case Left(e)=>
        complete(ErrorRsp(10001,s"解析错误+$e"))
    }
  }

  private val userLogin = (path("userLogin") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, UserLoginReq]]) {
      case Right(req) =>
        val (sessionKey,keyCode,signature)=SecureUtil.appSafety
        val sha1Pwd=SecureUtil.getSecurePassword(req.userId,req.pwd)
        dealFutureResult(
          UserDAO.userLogin(req.userId,sha1Pwd).map{r=>
            if(r.isEmpty) complete(CommonRsp(10001,"用户不存在或密码错误"))
            else {
              val session = Map(
                SessionBase.SessionTypeKey -> UserSessionKey.SESSION_TYPE,
                UserSessionKey.uid -> r.get.id.toString,
                UserSessionKey.userId -> req.userId,
                UserSessionKey.bbsId -> r.get.bbsId,
                UserSessionKey.loginTime -> System.currentTimeMillis().toString,
                UserSessionKey.keyCode -> keyCode,
                UserSessionKey.signature -> signature
              )
              val headImg=if(r.get.headImg=="") AppSettings.defaultHeadImg else r.get.headImg
              val userDetail=UserInfoDetail(r.get.id,r.get.userId,r.get.bbsId,headImg)
              dealFutureResult(
                UserDAO.updateSession(r.get.id,sessionKey).map{u=>
                  if(u>0){
                    setSession(session){ ctx =>
                      userManager ! UserManager.UserLogin(r.get.id)
                      ctx.complete(UserLoginRsp(Some(userDetail),0,"OK"))
                    }
                  }else{
                    complete(ErrorRsp(10001,"更新用户签名失败"))
                  }
                }
              )
            }
          }
        )
      case Left(e)=>
        complete(ErrorRsp(10001,s"解析错误+$e"))
    }
  }

  private val userLogout=(path("logout") & get & pathEndOrSingleSlash){
    UserAction{user=>
      val ses=Set(UserSessionKey.userId,UserSessionKey.uid)
      dealFutureResult {
        val future: Future[String] = userManager ? (UserLogout(user.uid,_))
        future.map {
          case "ok" =>
            removeSession(ses){ctx =>
              log.info(s"user-----${user.userId}----logout")
              ctx.complete(CommonRsp(0,"OK"))
            }
          case x@_ =>
            complete(ErrorRsp(120004, x))
        }.recover {
          case e: Exception =>
            log.info(s"user logout exception.." + e.getMessage)
            complete(ErrorRsp(120003, "网络异常,请稍后再试!"))
        }
      }
    }
  }

  private val getFeedFlow = (path("getFeedFlow") & get & pathEndOrSingleSlash) {
    UserAction{ u =>
      parameters(
        'sortType.as[Int],
        'lastItemTime.as[Long],
        'pageSize.as[Int]
      ) { case (sortType, lastItemTime, pageSize) =>
        dealFutureResult {
          val future: Future[Option[List[UserFeedReq]]] = userManager ? (GetUserFeed(u.uid, sortType, lastItemTime, pageSize, _))
          future.map {
            case Some(feeds) =>
//              val futureTopic:Future[UserFeedRsp] = boardManager ? (GetTopicList(feeds,_))
//              dealFutureResult {
//                futureTopic.map{topics=>
//                  complete(topics)
//                }
//              }
              complete(feeds)
            case None =>
              complete(ErrorRsp(120007, "sortType error...."))

          }.recover {
            case e: Exception =>
              log.info(s"postArt exception.." + e.getMessage)
              complete(ErrorRsp(120003, "网络异常,请稍后再试!"))
          }
        }
      }
    }
  }

  val userRoutes: Route =
    pathPrefix("user") {
      userIndex ~ userSign ~ userLogin ~ userLogout ~ getFeedFlow
    }
}
