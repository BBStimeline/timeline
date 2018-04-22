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

import scala.concurrent.Future
/**
  * User: sky
  * Date: 2018/4/10
  * Time: 13:56
  */
object PostDAO {
  private val log = LoggerFactory.getLogger(this.getClass)
  private val delTime= 1524111235000l

  private val postDetailCache = EhCacheApi.createCache[SlickTables.tPosts#TableElementType]("postDetailCache",300,300)

  def batchSearchPostDetailByCache(ls: Seq[(String, Long,Int)]) = {
    val keys = ls.map(t => (s"pd_${t._3}_${t._1}_${t._2}",t)).zipWithIndex
    val valueOpt = keys.map(k => (k,postDetailCache.get(k._1._1)))
    val cacheInfo = Future.sequence(valueOpt.filter(_._2.nonEmpty).map(_._2.get))
    val noCache = valueOpt.filter(_._2.isEmpty).map(_._1)
    //    println(noCache.map(_._1._1))
    val nocacheRst = if(noCache.nonEmpty) batchSearch(noCache.map(t => t._1._2)) else Future(Nil)
    cacheInfo.flatMap{ cacheRes =>
      nocacheRst.map{ nocacheInfo =>
        nocacheInfo.foreach{t =>
          postDetailCache.apply(s"pd_${t.origin}_${t.boardName}_${t.postId}",() => Future(t))}
        nocacheInfo.toList ::: cacheRes.toList
      }.map(_.toSeq)
    }.recover{
      case e:Exception =>
        log.error(s"get postDetail by cache failed,error:${e}")
        Nil
    }
  }

  def batchSearch(ls: Seq[(String, Long,Int)]) = { //boardName，postId,origin
    val query = tPosts.filter{ p =>
      val a = ls.map{
        l =>
          p.origin === l._3 && p.boardName === l._1 && p.postId === l._2
      }
      a.reduceLeft(_ || _)
    }
    db.run(query.result)
  }

  def insert(line:rPosts)={
    db.run(tPosts.returning(tPosts.map(_.id))+=line)
  }

  def insertList(list:List[rPosts])={
    db.run(tPosts++=list)
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

  def getPostsByBoardId(origin:Int,board:String,topicId:Long)={
    db.run(tPosts.filter(r=>r.origin===origin&&r.boardName===board&&r.topicId===topicId).result)
  }

}
