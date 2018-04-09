package com.neo.sk.timeline.service

import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.neo.sk.timeline.core.UserManager._

import scala.concurrent.Future
import com.neo.sk.timeline.ptcl.UserProtocol._
import com.neo.sk.timeline.shared.ptcl.{ErrorRsp, SuccessRsp}
import org.slf4j.LoggerFactory
import akka.pattern.ask
import io.circe.Error
import io.circe.generic.auto._
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.server.Route
import com.neo.sk.timeline.common.AppSettings
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.models.dao.UserDAO
import com.neo.sk.timeline.service.ServiceUtils.CommonRsp
import com.neo.sk.timeline.service.SessionBase.UserSessionKey
import com.neo.sk.timeline.shared.ptcl.UserProtocol.{UserInfoDetail, UserLoginReq, UserLoginRsp, UserSignReq}
import com.neo.sk.timeline.utils.SecureUtil
import com.neo.sk.timeline.Boot.{executor}
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
                UserDAO.addUser(req.userId,now,"",req.img,req.city,req.gender,sha1Pwd).map(t=>
                  if(t>0l) complete(CommonRsp(0,"Ok")) else complete(ErrorRsp(10001,"注册失败"))
                )
              )
            }else{
              complete(10002,"本昵称已被使用")
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
                UserDAO.updateSession(r.get.id,sessionKey).map{r=>
                  if(r>0){
                    setSession(session){ ctx =>
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
      removeSession(ses){ctx =>
        log.info(s"user-----${user.userId}----logout")
        ctx.complete(CommonRsp(0,"OK"))
      }
    }
  }

  val userRoutes: Route =
    pathPrefix("user") {
      userIndex ~ userSign ~ userLogin ~ userLogout
    }
}
