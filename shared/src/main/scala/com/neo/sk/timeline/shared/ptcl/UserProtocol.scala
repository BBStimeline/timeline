package com.neo.sk.timeline.shared.ptcl

import com.neo.sk.timeline.shared.ptcl.CommonRsp
/**
  * User: sky
  * Date: 2018/4/8
  * Time: 11:21
  */
object UserProtocol {

  case class AdminConfirm(
                           adminName:String,
                           passWord:String
                         )

  case class UserSignReq(
                        userId:String,
                        pwd:String,
                        mail:String,
                        city:String,
                        gender:Int
                        )

  case class UserLoginReq(
                         userId:String,
                         pwd:String
                         )

  case class UserLoginRsp(
                           errCode: Int,
                           msg: String) extends CommonRsp
}
