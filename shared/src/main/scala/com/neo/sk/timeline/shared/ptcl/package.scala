package com.neo.sk.timeline.shared

/**
  * User: sky
  * Date: 2018/3/10
  * Time: 16:12
  */
package object ptcl {

  trait CommonRsp {
    val errCode: Int
    val msg: String
  }


  final case class ErrorRsp(
                             errCode: Int,
                             msg: String
                           ) extends CommonRsp

  final case class SuccessRsp(
                               errCode: Int = 0,
                               msg: String = "ok"
                             ) extends CommonRsp

  case class AdminConfirm(
                           adminName:String,
                           passWord:String
                         )

  //按红包查询
  case class RedElInfo(
                        openId:String,
                        totalFee:Int,
                        balance:Int,
                        createTime:Long,
                        expireTime:Long
                      )
  case class DrawRedElInfo(
                            openId:String,
                            fee:Int,
                            createTime:Long
                          )

  case class RedElQueryRSP(
                            errCode:Int,
                            msg:String="ok",
                            redInfo:RedElInfo,
                            drawList:List[DrawRedElInfo]
                          )

  case class UserRecordReq(
                            openId: String
                          )

  //统计页面
  case class Date(date: String)

  case class Statistic(
                        createSuccess: Int,
                        createFail: Int,
                        createFee: Int,
                        drawSuccess: Int,
                        drawFail: Int,
                        drawFee: Int,
                        withdrawSuccess: Int,
                        withdrawFail: Int,
                        withdrawFee: Int

                      )

  case class CreateElInfo(
                           id: Long,
                           elId: String,
                           totalFee: Int,
                           useBalance: Int,
                           createTime: Long,
                           finalStatus: Option[Int],
                           finalTime: Option[Long]
                         )

  case class DrawElInfo(
                         id: Long,
                         elId: String,
                         fee: Int,
                         createTime: Long,
                         finalStatus: Option[Int],
                         finalTime: Option[Long],
                         `type`: Int
                       )

  case class WithDrawInfo(
                           id: Long,
                           totalFee: Int,
                           realFee: Int,
                           createTime: Long,
                           finalStatus: Option[Int],
                           finalTime: Option[Long],
                           `type`: Int
                         )

  case class StatisticRsp(
                           statistic: Statistic,
                           errCode: Int = 0,
                           msg: String = "ok"
                         ) extends CommonRsp

  case class UserLinesRsp(
                           createElList: List[CreateElInfo],
                           drawElList: List[DrawElInfo],
                           withdrawList: List[WithDrawInfo],
                           errCode: Int = 0,
                           msg: String = "ok"
                         ) extends CommonRsp

  //用户行为管理页面
  case class WaitingRecordsReq(
                                pageType: Int,  //0: 上一页，1： 下一页
                                startId: Option[Long],
                                count: Int = 10
                              )

  case class WaitingRecordInfo(
                                id: Long,
                                openId: String,
                                fee: Int
                              )

  case class WaitingRecordsRsp(
                                records: List[WaitingRecordInfo],
                                totalFee: Option[Int] = None,
                                totalNum: Option[Int] = None,
                                errCode: Int = 0,
                                msg: String = "ok"
                              ) extends CommonRsp

  case class NoWaitingRecordsRsp(
                                  errCode: Int = 1,
                                  msg: String = "Invalid request"
                                ) extends CommonRsp

  //用户充值页面

  case class GenTradeNoRsp(
                            tradeNo: String,
                            errCode: Int = 0,
                            msg: String = "ok"
                          ) extends CommonRsp

  case class Recharge(
                       id: Long,
                       tradeNo: String,
                       openId: String,
                       balance: Int,
                       createTime: Long
                     )

  final case class RechargeReqList(rechargeReq: List[RechargeRecord])

  final case class RechargeRecord(
                                   id: Long,
                                   tradeNo: String,
                                   openId: String,
                                   balance: Int,
                                   createTime: Long
                                 )

  case class AdminRechargeReq(
                               openId: String,
                               balance: Int,
                               tradeNo: String
                             )
  case class CheckRechargeReqRsp(
                                  rechargeReqList: RechargeReqList,
                                  errCode: Int = 0,
                                  msg: String = "ok"
                                ) extends CommonRsp

  case class RechargeRecordsType(tp: Int)


}
