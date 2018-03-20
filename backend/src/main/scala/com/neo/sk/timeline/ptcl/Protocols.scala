package com.neo.sk.timeline.ptcl

object Protocols {

  trait Request

  case class Plus(value: Int) extends Request

  case class Minus(value: Int) extends Request

  case class CreateRedEI(
    openId: String,
    redEnvelopeId: String,
    redElFee: Int, //红包金额 单位：分
    serviceFee: Int, //手续费
    useBalance: Int, //使用余额 单位：分
    notifyUrl: String, //异步通知url
    expireTime: Long
  ) extends Request

  case class WithDraw(
    openId: String,
    tradeNo: String,
    fee: Int, //单位：分，
    notifyUrl: String, //异步通知url
    `type`: Int = 1, //类型： 1：自动提现 or 2：手动提现
    des: String = "自动提现"//描述信息: 自动提现 or 手动提现
  ) extends Request

  case class DrawRedEl(
    openId: String,
    redEnvelopeId: String,
    fee: Int //单位：分，
  ) extends Request

  case class SearchBalanceReq(openId: String) extends Request

  case class CancelRedEl(redEnvelopeId:String) extends Request
  
  case class BalanceCheckReq(
    openId: String,
    startId: Option[Long],
    count: Option[Int]
  ) extends Request

  case class UserRechargeReq(
    openId: String,
    balance: Int,
    tradeNo: String
  ) extends Request



  trait Response {
    val errCode: Int
    val msg: String
  }

  case class Params(
    appId: String,
    timeStamp: String,
    nonceStr: String,
    pack: String,
    signType: String,
    paySign: String
  )
  
  case class BalanceLine(
    id: Long,
    time: Long,
    fee: Int,
    `type`: Int
  )

  case class RechargeRsp(errCode: Int)

  case class CommonRsp(errCode: Int = 0, msg: String = "ok") extends Response

  case class BalanceRsp(errCode: Int = 0, msg: String = "ok", balance: Int = 100) extends Response

  case class CreateRedEIRsp(errCode: Int = 0, msg: String = "ok", needWxPay: Int = 1, params: Option[Params] = None) extends Response

  case class BalanceCheckRsp(records: List[BalanceLine], errCode: Int = 0, msg: String = "ok") extends Response

  val SuccessRsp = CommonRsp()

  val SignatureError = CommonRsp(1000001, "signature error.")

  val RequestTimeout = CommonRsp(1000003, "request timestamp is too old.")

  val AppClientIdError = CommonRsp(1000002, "appClientId error.")

  val SearchBalanceError = CommonRsp(1000004, "search balance error")



}
