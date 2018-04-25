package com.neo.sk.timeline

import akka.actor.ActorSystem
import akka.dispatch.MessageDispatcher
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.actor.typed.scaladsl.adapter._
import com.neo.sk.timeline.core.postInfo.BoardManager
import com.neo.sk.timeline.core.{DistributeManager, SynDataActor}
import com.neo.sk.timeline.core.user.UserManager
import com.neo.sk.timeline.service.HttpService

import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * User: Taoz
  * Date: 11/16/2016
  * Time: 1:00 AM
  */
object Boot extends HttpService {


  import concurrent.duration._
  import com.neo.sk.timeline.common.AppSettings._

  override implicit val system = ActorSystem("appSystem", config)
  // the executor should not be the default dispatcher.
  override implicit val executor: MessageDispatcher =
    system.dispatchers.lookup("akka.actor.my-blocking-dispatcher")

  override implicit val materializer = ActorMaterializer()

  override implicit val timeout = Timeout(20 seconds) // for actor asks

  override implicit val scheduler = system.scheduler

  val log: LoggingAdapter = Logging(system, getClass)

  val userManager = system.spawn(UserManager.behavior, "userManager")
  
  val distributeManager = system.spawn(DistributeManager.behavior, "distributeManager")

  val boardManager = system.spawn(BoardManager.behavior,"boardManager")

  val synDataActor = system.spawn(SynDataActor.behavior,"synDataActor")

  def main(args: Array[String]) {
    log.info("Starting.")
    val binding = Http().bindAndHandle(routes, httpInterface, httpPort)
    binding.onComplete {
      case Success(b) ⇒
        val localAddress = b.localAddress
        println(s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")
        println(s"Server is listening on http://localhost:${localAddress.getPort}/timeline/user/index")
      case Failure(e) ⇒
        println(s"Binding failed with ${e.getMessage}")
        system.terminate()
        System.exit(-1)
    }
  }



}
