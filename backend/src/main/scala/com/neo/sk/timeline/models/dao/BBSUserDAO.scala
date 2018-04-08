package com.neo.sk.timeline.models.dao

import com.neo.sk.timeline.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.timeline.models.SlickTables._
import com.neo.sk.timeline.models.{SlickTables, UserInfoDetail}
import com.neo.sk.timeline.common.Constant.UserOrigin
import com.neo.sk.timeline.Boot.executor
/**
  * User: sky
  * Date: 2018/4/8
  * Time: 10:35
  */
object BBSUserDAO {
  def getByBbsId(bbsId: String) = {
    val action = for {
      r1 <- tBbsUser.filter(_.bbsId === bbsId).result.headOption
      r2 <- tUser.filter(_.bbsId === bbsId).result.headOption
    } yield {
      (r1, r2)
    }
    db.run(action.transactionally)
  }

  def addUserAndBbsUser(bbsId: String, bbsInfo: UserInfoDetail) = {
    val now = System.currentTimeMillis()
    val action = for {
      r1 <- tBbsUser.returning(tBbsUser.map(_.id)) +=
        rBbsUser(-1l, bbsId, bbsInfo.user_name, bbsInfo.face_url, UserOrigin.TIANYA, now)
      r2 <- tUser.returning(tUser.map(_.id)) +=
        rUser(-1l, "", bbsId, "", -1l, "", now, "", "", "", "", 0, 0, 0)
    } yield {
      (r1, r2)
    }
    db.run(action.transactionally)
  }

  def addUser(bbsId: String) = db.run {
  val now = System.currentTimeMillis()
  tUser.returning(tUser.map(_.id)) +=
    rUser(-1l, "", bbsId, "", -1l, "", now, "", "", "", "", 0, 0, 0)
  }

  def addBbsUser(bbsInfo: UserInfoDetail) = db.run(
    tBbsUser.returning(tBbsUser.map(_.id)) +=
      rBbsUser(-1l, bbsInfo.bbs_id, bbsInfo.user_name, bbsInfo.face_url, UserOrigin.TIANYA, System.currentTimeMillis())
  )
}
