package com.neo.sk.timeline.service

import akka.actor.Scheduler
import akka.actor.typed.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import org.slf4j.LoggerFactory

import scala.language.postfixOps

trait AdminService extends ServiceUtils with SessionBase {

  import io.circe._
  import io.circe.generic.auto._

  implicit val timeout: Timeout

  implicit val scheduler: Scheduler


  private val secretKey = "dsacsodaux84fsdcs4wc32xm"
  private val log = LoggerFactory.getLogger("com.neo.sk.timeline.service.AdminService")

  private val adminIndex:Route=pathEndOrSingleSlash {
    loggingAction{_=>
      getFromResource("html/admin.html")
    }
  }

  val adminRoutes: Route =
    pathPrefix("admin") {
      adminIndex
    }
}