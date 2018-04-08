package com.neo.sk.timeline

/**
  * User: sky
  * Date: 2018/4/8
  * Time: 10:40
  */
package object models {
  case class UserInfoDetail(
                             bbs_id: String,
                             user_name: String = "",
                             face_url: String = ""
                           )
}
