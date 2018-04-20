package com.neo.sk.timeline.models.dao

import com.neo.sk.timeline.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.timeline.models.SlickTables._
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.common.Constant.{FeedType, OriginType, UserFollowState}
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
  private val delTime= 1524111235000l

  private val postDetailCache = EhCacheApi.createCache[SlickTables.tPosts#TableElementType]("postDetailCache",300,300)

  def insert(line:rPosts)={
    db.run(tPosts.returning(tPosts.map(_.id))+=line)
  }

  def insertList(list:List[rPosts])={
    db.run(tPosts.returning(tPosts.map(_.id))++=list)
  }

  def getLastTopicByUser(userId:String, userName:String, origin:Int, num:Int)={
    db.run(tPosts.filter(r=>r.authorId===userId&&r.origin===origin).sortBy(_.postTime.desc).take(num).result)
  }

  def getUserByPostId(topics:Seq[(Int,String,Long)])={
    val query=tPosts.filter{p=>
      val a=topics.map{t=>
        p.origin===t._1&&p.boardName===t._2&&p.topicId===t._3
      }
      a.reduceLeft(_||_)
    }.map(r=>(r.topicId,r.postId))
    db.run(query.result)
  }

  def removePostByTime={
    db.run(tPosts.filter(r=>r.origin===OriginType.SMTH&&r.postTime<delTime).delete)
  }

}
