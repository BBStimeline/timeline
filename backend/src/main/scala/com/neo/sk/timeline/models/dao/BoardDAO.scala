package com.neo.sk.timeline.models.dao

import com.neo.sk.timeline.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.timeline.models.SlickTables._
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.common.Constant.{FeedType, UserFollowState}
import com.neo.sk.timeline.Boot.executor
import com.neo.sk.timeline.utils.EhCacheApi
import slick.dbio.DBIOAction
/**
  * User: sky
  * Date: 2018/4/10
  * Time: 13:56
  */
object BoardDAO {

  def addBoard(r:rBoard)={
    db.run(tBoard+=r)
  }

  def getBoard(origin:Int,board:String)={
    db.run(tBoard.filter(r=>r.origin===origin&&r.boardName===r.boardNameCn).result.headOption)
  }

  def getBoardList(list:Seq[(Int,String)])={
    val query=tBoard.filter{p=>
      val a=list.map{t=>
        p.origin===t._1&&p.boardName===t._2
      }
      a.reduceLeft(_||_)
    }.map(r=>(r.origin,r.boardName,r.boardNameCn))
    db.run(query.result)
  }

}
