package com.neo.sk.timeline.front

/**
  * Created by TangYaruo on 2017/11/2.
  */
object Routes {

  private val baseUrl = "/timeline/admin"

  val statistic = baseUrl + "/statistic"
  val userRecords = baseUrl + "/userRecords"
  val getStatistics = baseUrl + "/getStatistics"
  val getUserRecords = baseUrl + "/getUserRecords"
  val home = baseUrl + "/home"
  val redElQuery = baseUrl + "/rsf/redElQuery"
  val waitingRecords = baseUrl + "/waitingRecords"
  val withdrawRetry = baseUrl + "/withdrawRetry"
  val management = baseUrl + "/management"
  val redElRecords = baseUrl + "/redElRecords"
  val userRecharge = baseUrl + "/userRecharge"
  val genTradeNo = baseUrl + "/genTradeNo"
  val rechargeConfirm = baseUrl + "/rechargeConfirm"
  val checkRechargeReqList = baseUrl + "/checkRechargeReqList"
  val rechargeReq = baseUrl + "/rechargeReq"


}
