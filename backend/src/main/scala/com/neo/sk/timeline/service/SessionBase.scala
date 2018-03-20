package com.neo.sk.timeline.service

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives.{extractRequestContext, redirect}
import akka.http.scaladsl.server.{Directive, Directive0, Directive1, RequestContext}
import akka.http.scaladsl.server.directives.BasicDirectives
import com.neo.sk.timeline.common.AppSettings
import com.neo.sk.timeline.utils.SessionSupport
import org.slf4j.LoggerFactory

/**
  * User: Taoz
  * Date: 12/4/2016
  * Time: 7:57 PM
  */

object SessionBase {
  private val logger = LoggerFactory.getLogger(this.getClass)

  val SessionTypeKey = "STKey"

  object UserSessionKey {
    val SESSION_TYPE = "userSession"
    val uid = "uid"
    val loginTime = "loginTime"
  }

  object AdminSessionKey {
    val SESSION_TYPE = "adminSession"
    val aid = "aid"
    val name = "name"
    val loginTime = "loginTime"
  }

  case class AdminSession(
                           aid: Long,
                           name: String,
                           loginTime: Long
                         ) {
    def toSessionMap = Map(
      SessionTypeKey -> AdminSessionKey.SESSION_TYPE,
      AdminSessionKey.aid -> aid.toString,
      AdminSessionKey.name -> name.toString,
      AdminSessionKey.loginTime -> loginTime.toString
    )

  }

  case class UserSession(
    uid: Long,
    loginTime: Long
  ) {
    def toSessionMap = Map(
      SessionTypeKey -> UserSessionKey.SESSION_TYPE,
      UserSessionKey.uid -> uid.toString,
      UserSessionKey.loginTime -> loginTime.toString
    )
  }

  implicit class SessionTransformer(sessionMap: Map[String, String]) {
    def toUserSession: Option[UserSession] = {
      logger.debug(s"toUserSession: change map to session, ${sessionMap.mkString(",")}")
      try {
        if (sessionMap.get(SessionTypeKey).exists(_.equals(UserSessionKey.SESSION_TYPE))) {
          Some(UserSession(
            sessionMap(UserSessionKey.uid).toLong,
            sessionMap(UserSessionKey.loginTime).toLong
          ))
        } else {
          logger.debug("no session type in the session")
          None
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          logger.warn(s"toUserSession: ${e.getMessage}")
          None
      }
    }

    def toAdminSession: Option[AdminSession] = {
      logger.debug(s"toAdminSession: change map to session, ${sessionMap.mkString(",")}")
      try {
        if (sessionMap.get(SessionTypeKey).exists(_.equals(AdminSessionKey.SESSION_TYPE))) {
          Some(AdminSession(
            sessionMap(AdminSessionKey.aid).toLong,
            sessionMap(AdminSessionKey.name),
            sessionMap(AdminSessionKey.loginTime).toLong
          ))
        } else {
          logger.debug("no session type in the session")
          None
        }
      } catch {
        case e: Exception =>
          e.printStackTrace()
          logger.warn(s"toAdminSession error: ${e.getMessage}")
          None
      }
    }
  }

}

trait SessionBase extends SessionSupport {

  import SessionBase._

  override val sessionEncoder = SessionSupport.PlaySessionEncoder
  override val sessionConfig = AppSettings.sessionConfig

  def loggingAction: Directive[Tuple1[RequestContext]] = extractRequestContext.map { ctx =>
    logger.info(s"Access uri: ${ctx.request.uri} from ip ${ctx.request.uri.authority.host.address}.")
    ctx
  }
  protected def setUserSession(userSession: UserSession): Directive0 = setSession(userSession.toSessionMap)

  protected val optionalUserSession: Directive1[Option[UserSession]] = optionalSession.flatMap {
    case Right(sessionMap) => BasicDirectives.provide(sessionMap.toUserSession)
    case Left(error) =>
      logger.debug(error)
      BasicDirectives.provide(None)
  }

  protected val optionalAdminSession: Directive1[Option[AdminSession]] = optionalSession.flatMap {
    case Right(sessionMap) => BasicDirectives.provide(sessionMap.toAdminSession)
    case Left(error) =>
      logger.debug(error)
      BasicDirectives.provide(None)
  }

  private val sessionTimeOut = 24 * 60 * 60 * 1000

  protected def setAdminSession(adminSession: AdminSession): Directive0 = setSession(adminSession.toSessionMap)

  protected def AdminAction(f: AdminSession => server.Route): server.Route = {
    optionalAdminSession {
      case Some(adminSession) =>
        if (System.currentTimeMillis() - adminSession.loginTime > sessionTimeOut) {
          logger.info("Login failed for Timeout !")
          redirect("/timeline/admin/login", StatusCodes.SeeOther)
        } else {
          f(AdminSession(adminSession.aid, adminSession.name, adminSession.loginTime))
        }
      case None =>
        redirect("/timeline/admin/login", StatusCodes.SeeOther)
    }
  }

}
