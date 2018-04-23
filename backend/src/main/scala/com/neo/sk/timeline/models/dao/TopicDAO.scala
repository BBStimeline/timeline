package com.neo.sk.timeline.models.dao

import com.neo.sk.timeline.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.timeline.models.SlickTables._
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.common.Constant.{FeedType, UserFollowState}
import com.neo.sk.timeline.Boot.executor
import com.neo.sk.timeline.utils.EhCacheApi
import org.slf4j.LoggerFactory
import slick.dbio.DBIOAction
/**
  * User: sky
  * Date: 2018/4/12
  * Time: 11:09
  */
object TopicDAO {
  def insertOrUpdateTopicSnap(a:SlickTables.rTopicSnapshot) = {
    db.run(tTopicSnapshot.insertOrUpdate(a))
  }


  def getPostListByPostTime(board:String, origin:Int, num:Int)={
    db.run(tTopicSnapshot.filter(r=>r.origin===origin&&r.boardName===board).sortBy(_.postTime.desc).take(num).result)
  }

  def getPostListByReplyTime(board:String, origin:Int, num:Int)={
    db.run(tTopicSnapshot.filter(r=>r.origin===origin&&r.boardName===board).sortBy(_.lastReplyTime.desc).take(num).result)
  }

  def getPostById(origin:Int,board:String,topicId:Long)={
    db.run(tTopicSnapshot.filter(r=>r.origin===origin&&r.boardName===board&&r.topicId===topicId).result.headOption)
  }

}
