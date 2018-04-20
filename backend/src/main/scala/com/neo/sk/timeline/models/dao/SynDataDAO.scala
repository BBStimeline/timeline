package com.neo.sk.timeline.models.dao

import com.neo.sk.timeline.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.timeline.models.SlickTables._
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.common.Constant.{UserFollowState, OriginType}
import com.neo.sk.timeline.Boot.executor
/**
  * User: sky
  * Date: 2018/4/19
  * Time: 11:43
  */
object SynDataDAO {
  def getData(id: Long) = db.run(tSynData.filter(_.id === id).map(_.data).result.headOption)

  def updateData(id: Long, data: Long) = db.run(tSynData.filter(_.id === id).map(_.data).update(data))
}
