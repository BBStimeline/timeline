package com.neo.sk.timeline.utils

import com.neo.sk.timeline.Boot.executor
import com.neo.sk.timeline.common.AppSettings
import com.neo.sk.timeline.ptcl.Protocols
import org.slf4j.LoggerFactory
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._

/**
  * Created by Zhong on 2017/10/19. 
  */
object NotifyClient extends HttpUtil{
  private val log = LoggerFactory.getLogger(this.getClass)

  private val drogonAppId = "drogon"

  private val drogonKey = AppSettings.appSecureMap(drogonAppId)

  private case class CreateRedElNotifyReq(
                                           openId: String,
                                           redEnvelopeId: String,
                                           status: String = "SUCCESS"
                                         )
  
  private case class WithdrawNotifyReq(
                                      tradeNo: String,
                                      status: String = "SUCCESS"
                                      )

  def createRedElNotify(openId: String, redElId: String, url: String, status: Int) = {
    val s = if (status == 0) "SUCCESS" else "FAIL"
    val data = CreateRedElNotifyReq(openId, redElId, s).asJson.noSpaces
    val jsonStr = genPostEnvelopeStr(data)
    postJsonRequestSend(s"createRedElNotify $redElId", url, Nil, jsonStr).map{
      case Right(rsp) =>
        decode[Protocols.CommonRsp](rsp) match {
          case Right(r) =>
            if (r.errCode == 0)
              Right("Ok")
            else
              Left("errCode error")

          case Left(e) =>
            log.error(s"createRedElNotify $redElId decode error: $e")
            Left("decode error")
        }

      case Left(e) =>
        log.error(s"createRedElNotify $redElId get left error: $e")
        Left("error")
    }.recover{
      case e =>
        log.error(s"createRedElNotify $redElId future error: $e")
        Left("error")
    }
  }
  
  def withdrawNotify(inTradeNo: String, url: String, status: Int) = {
    val s = if (status == 0) "SUCCESS" else "FAIL"
    val data = WithdrawNotifyReq(inTradeNo, s).asJson.noSpaces
    val jsonStr = genPostEnvelopeStr(data)
    postJsonRequestSend(s"withdrawNotify $inTradeNo", url, Nil, jsonStr).map {
      case Right(rsp) =>
        decode[Protocols.CommonRsp](rsp) match {
          case Right(r) =>
            if (r.errCode == 0)
              Right("Ok")
            else
              Left("errCode error")
      
          case Left(e) =>
            log.error(s"withdrawNotify $inTradeNo decode error: $e")
            Left("decode error")
        }
  
      case Left(e) =>
        log.error(s"withdrawNotify $inTradeNo get left error: $e")
        Left("error")
    }.recover {
      case e =>
        log.error(s"withdrawNotify $inTradeNo future error: $e")
        Left("error")
    }
  }

  private def genPostEnvelopeStr(data: String) =
    SecureUtil.genPostEnvelope(drogonAppId,
      System.nanoTime().toString,
      data,
      drogonKey
    ).asJson.noSpaces
}
