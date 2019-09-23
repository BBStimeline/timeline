package com.neo.sk.timeline.shared.ptcl

import com.neo.sk.timeline.shared.ptcl.CommonRsp
/**
  * User: sky
  * Date: 2018/4/8
  * Time: 11:21
  */
object UserProtocol {

  case class UserSignReq(
                        userId:String,
                        pwd:String,
                        mail:String
                        )

  case class UserLoginReq(
                         userId:String,
                         pwd:String
                         )

  case class UserInfoDetail(
                             uid:Long=0l,
                             userId: String="",
                             bbsId: String="",
                             face_url: String=""
                           )

  case class UserSignRsp(
                          userInfo:Option[UserInfoDetail],
                          errCode: Int,
                          msg: String
                        ) extends CommonRsp

  case class UserLoginRsp(
                           userInfo:Option[UserInfoDetail],
                           errCode: Int,
                           msg: String) extends CommonRsp

  case class AdminConfirm(
                           adminName:String,
                           passWord:String
                         )
}
