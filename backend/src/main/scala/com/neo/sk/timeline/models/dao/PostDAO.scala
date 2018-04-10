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
  * Date: 2018/4/10
  * Time: 13:56
  */
object PostDAO {
  private val log = LoggerFactory.getLogger(this.getClass)

  private val postDetailCache = EhCacheApi.createCache[SlickTables.tPosts#TableElementType]("postDetailCache",300,300)

  def insert(line:rPosts)={
    db.run(tPosts.returning(tPosts.map(_.id))+=line)
  }

  def getLastPostByBoard(board:String,origin:Int,num:Int)={
    db.run(tPosts.filter(r=>r.boardName===board&&r.origin===origin).sortBy(_.postTime desc).take(num).result)
  }

  def getLastPostByTopic(board:String,origin:Int,topicId:Long,num:Int)={
    db.run(tPosts.filter(r=>r.boardName===board&&r.topicId===topicId&&r.origin===origin).sortBy(_.postTime desc).take(num).result)
  }

  def getLastPostByUser(userId:Long,userName:String,origin:Int,num:Int)={
    db.run(tPosts.filter(r=>r.authorId===userId&&r.origin===origin).sortBy(_.postTime desc).take(num).result)
  }

}
