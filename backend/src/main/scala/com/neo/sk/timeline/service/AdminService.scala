package com.neo.sk.timeline.service

import akka.actor.Scheduler
import akka.actor.typed.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.neo.sk.timeline.core.SynDataActor.{StartSynData, StopSynData}
import com.neo.sk.timeline.models.dao.SynDataDAO
import com.neo.sk.timeline.shared.ptcl.{ErrorRsp, SuccessRsp}
import org.slf4j.LoggerFactory
import com.neo.sk.timeline.Boot.{synDataActor,executor}

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

  private val startSynData=(path("startSynData") & get & pathEndOrSingleSlash) {
    synDataActor ! StopSynData
    synDataActor ! StartSynData
    complete(SuccessRsp())
  }

  private val stopSynData=(path("stopSynData") & get & pathEndOrSingleSlash) {
    synDataActor ! StopSynData
    complete(ErrorRsp(0, "Ok"))
  }

  val adminRoutes: Route =
    pathPrefix("admin") {
      adminIndex ~ startSynData ~ stopSynData
    }
}