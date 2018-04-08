package com.neo.sk.timeline.models.dao

import com.neo.sk.timeline.utils.DBUtil.db
import slick.jdbc.PostgresProfile.api._
import com.neo.sk.timeline.models.SlickTables._
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.common.Constant.UserFollowState
import com.neo.sk.timeline.Boot.executor
/**
  * User: sky
  * Date: 2018/4/8
  * Time: 10:18
  */
object FollowDAO {

  def getBoardFollowers(boardName: String,origin:Int) = db.run{
    tUserFollowBoard.filter(r=>r.boardName === boardName&&r.origin===origin).result
  }

  def getTopicFollowers(boardName: String,origin:Int) = db.run{
    tUserFollowTopic.filter(r =>r.boardName === boardName &&r.origin===origin).result
  }

  def listFollowBoard(userId: Long) = db.run{
    tUserFollowBoard.filter(_.userId === userId).result
  }

  def listFollowTopic(userId: Long) = db.run{
    tUserFollowTopic.filter(_.userId === userId).result
  }

  def listFollowUser(userId: Long) = db.run{
    tUserFollowUser.filter(_.userId === userId).result
  }

  def checkFollowBoard(userId: Long, origin: Int, boardName: String) = db.run{
    tUserFollowBoard.filter(i => i.userId === userId && i.origin === origin && i.boardName === boardName).exists.result
  }

  def checkFollowTopic(userId: Long, origin: Int, boardName: String, topicId: Long) = db.run{
    tUserFollowTopic.filter(i => i.userId === userId && i.origin === origin && i.boardName === boardName && i.topicId === topicId).exists.result
  }

  def checkFollowUser(userId: Long, followUser: Long) = db.run{
    tUserFollowUser.filter(i => i.userId === userId && i.followId === followUser).exists.result
  }

  def addFollowBoard(board: rUserFollowBoard) = db.run{
    tUserFollowBoard.returning(tUserFollowBoard.map(_.id)) += board
  }

  def addFollowTopic(topic: rUserFollowTopic) = db.run{
    tUserFollowTopic.returning(tUserFollowTopic.map(_.id)) += topic
  }

  def addFollowUser(user: rUserFollowUser) = db.run{
    tUserFollowUser.returning(tUserFollowUser.map(_.id)) += user
  }

  def markFollow(id: Long, followType: Int) = {
    followType match {
      case 0 =>
        db.run{
          tUserFollowBoard.filter(_.id === id).map(_.state).update(UserFollowState.IMPORTANT)
        }

      case 1 =>
        db.run{
          tUserFollowTopic.filter(_.id === id).map(_.state).update(UserFollowState.IMPORTANT)
        }

      case 2 =>
        db.run{
          tUserFollowUser.filter(_.id === id).map(_.state).update(UserFollowState.IMPORTANT)
        }
    }
  }

  def unMarkFollow(id: Long, followType: Int) = {
    followType match {
      case 0 =>
        db.run{
          tUserFollowBoard.filter(_.id === id).map(_.state).update(UserFollowState.NORMAL)
        }

      case 1 =>
        db.run{
          tUserFollowTopic.filter(_.id === id).map(_.state).update(UserFollowState.NORMAL)
        }

      case 2 =>
        db.run{
          tUserFollowUser.filter(_.id === id).map(_.state).update(UserFollowState.NORMAL)
        }
    }
  }

  def unFollowBoard(userId: Long, origin: Int, boardName: String) = db.run{
    tUserFollowBoard.filter(i => i.userId === userId && i.origin === origin && i.boardName === boardName).delete
  }

  def unFollowTopic(userId: Long, origin: Int, boardName: String, topicId: Long) = db.run{
    tUserFollowTopic.filter(i => i.userId === userId && i.origin === origin && i.boardName === boardName && i.topicId === topicId).delete
  }

  def unFollowUser(userId: Long, followId: Long) ={
    val action = for{
      r1 <- tUser.filter(_.id === followId).result.head
      r2 <- tUserFollowUser.filter(i => i.userId === userId && i.followId === followId).delete
    } yield{
      (r1, r2)
    }
    db.run(action.transactionally)
  }

  def unFollow(id: Long, followType: Int) = {
    followType match {
      case 0 =>
        db.run{
          tUserFollowBoard.filter(_.id === id).delete
        }

      case 1 =>
        db.run{
          tUserFollowTopic.filter(_.id === id).delete
        }

      case 2 =>
        db.run{
          tUserFollowUser.filter(_.id === id).delete
        }
    }
  }

  def getFollowUser(uid: Long) = db.run(
    tUserFollowUser.filter(_.followId === uid).map(_.userId).result
  )

//  def getFollowedUser() = db.run(
//    tUserFollowUser.map(i => (i.followId, i.followBbsId)).result
//  )
//
//  def isUserFollow(uid: Long, bbsId: String) = db.run(
//    tUserFollowUser.filter(i => i.userId === uid && i.followBbsId === bbsId).exists.result
//  )

  def getUserFollowBoard(userId: Long) = db.run{
    tUserFollowBoard.filter(i => i.userId === userId && i.origin === "smth").result
  }

  def getFollows(uid: Long) = {
    val actions = for{
      r1 <- tUserFollowBoard.filter(_.userId === uid).result
      r2 <- tUserFollowUser.filter(_.userId === uid).result
      r3 <-  tUserFollowTopic.filter(_.userId === uid).result
    } yield {
      (r1, r2, r3)
    }

    db.run(actions.transactionally)
  }
}
