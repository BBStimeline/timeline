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

  val userRoutes: Route =
    pathPrefix("user") {
      userIndex
    }
}
