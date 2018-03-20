package com.neo.sk.timeline.models.dao

import com.neo.sk.timeline.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.timeline.models.SlickTables._

object AdminDao {

  def getAdminByName(name:String)={
    db.run(tAdmin.filter(_.adminname===name).result.headOption)
  }

}
