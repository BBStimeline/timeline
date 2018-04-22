package com.neo.sk.timeline.core

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer, TimerScheduler}
import org.slf4j.LoggerFactory
import akka.actor.typed.{ActorRef, Behavior}
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.models.dao.{FollowDAO, PostDAO, SynDataDAO}
import com.neo.sk.timeline.ptcl.UserProtocol._
import akka.actor.typed.scaladsl.AskPattern._
import com.neo.sk.timeline.Boot.{boardManager, executor, scheduler, timeout}
import com.neo.sk.timeline.common.AppSettings
import com.neo.sk.timeline.common.Constant.OriginType
import com.neo.sk.timeline.core.postInfo.BoardManager
import com.neo.sk.timeline.utils.SmallSpiderClient

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}
/**
  * User: sky
  * Date: 2018/4/19
  * Time: 10:04
  */
object SynDataActor {
  private val log = LoggerFactory.getLogger(this.getClass)

  trait Command
  private case object TimerKey
  private case object DelTimeKey
  private case object Timeout extends Command
  private case object DelTimeout extends Command
  case object StartSynData extends Command
  case object StopSynData extends Command

  private var id:Long=0
  private var count = AppSettings.synCount
  private val synTime= AppSettings.synTime.seconds
  private val synOutTime=AppSettings.synOutTime.hours
  private val isStart=AppSettings.isStart

  def behavior: Behavior[Command] ={
    Behaviors.setup[Command]{ctx=>
      Behaviors.withTimers{implicit timer =>
        if(isStart){
          SynDataDAO.getData(1).map{
            case Some(r)=>
              id=r
              log.info(s"start synData with id=$id")
              timer.startPeriodicTimer(TimerKey,Timeout,synTime)
              timer.startPeriodicTimer(DelTimeKey,DelTimeout,synOutTime)
            case None =>log.debug(s"dataBase being wrong")
          }
        }
        active
      }
    }

  }

  private def active(implicit timer: TimerScheduler[Command]): Behavior[Command] = {
    Behaviors.immutable[Command] { (ctx, msg) =>
      msg match {
        case StartSynData=>
          SynDataDAO.getData(1).onComplete({
            case Success(Some(r))=>
              id=r
              log.info(s"start synData with id=$id")
              timer.startPeriodicTimer(TimerKey,Timeout,synTime)
              timer.startPeriodicTimer(DelTimeKey,DelTimeout,synOutTime)
            case Failure(e)=>log.debug(s"get sendDataTime with $e")
            case Success(None) =>log.debug(s"dataBase being wrong")
          })
          Behaviors.same

        case Timeout =>
          SmallSpiderClient.getSynPosts(id,count).map{
            case Right(p) =>
              val now=System.currentTimeMillis()
              val list = p.map({x=>
                SlickTables.rPosts(x.id,OriginType.SMTH,x.topicId,x.postId,x.mainPost,
                  x.title,x.authorId,x.nickname,x.contentText,x.imgs,x.hestiaImgs,x.timestamp,x.boardName,x.url,x.boardNameCn,if(x.quoteId==0l) None else Some(x.quoteId),now,0,x.id)})
              PostDAO.insertList(list).map{re=>
                boardManager ! BoardManager.InsertPostList(list)
                id=p.last.id
                SynDataDAO.updateData(1, id).map { r =>
                  if (r < 0l) {
                    log.error(s"failed to save postId to record...postId=$id")
                  } else {
                    log.info("postId===" + id)
                  }
                }
                if(count>10) count=AppSettings.synCount else count+=2
              }.recover{
                case e=>
                  log.error(s"failed to insert posts with error $e")
              }
            case Left(e) =>
              if(count==500) count=250 else if(count>100) count=100 else if(count>10) count=10 else if(count>1) count=1 else id+=1
              log.info(s"fetch posts failed......count==$count")
          }
          Behaviors.same

        case DelTimeout =>
          PostDAO.removePostByTime.onComplete({
            case Success(t)=>
              log.info("delete overTime data")
            case Failure(e)=>
              log.error(s"get an error with $e")
          })
          Behaviors.same
        case StopSynData =>
          log.info("stop synData")
          timer.cancel(TimerKey)
          timer.cancel(DelTimeKey)
          Behaviors.same
      }
    }
  }
}
