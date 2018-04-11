package com.neo.sk.timeline.ptcl

import scala.collection.mutable
/**
  * User: sky
  * Date: 2018/4/8
  * Time: 16:59
  */
object DistributeProtocol {
  case class DisType(
                    board:Option[String]=None,
                    topicId:Option[Long]=None,
                    userId:Option[Long]=None,
                    userName:Option[String]=None,
                    origin:Int
                    )

  case class DisCache(
                     postList:mutable.HashSet[(Int,String,Long)],
                     followList:mutable.HashSet[Long]=mutable.HashSet(),
                     name:String,
                     variety:Int
                     )
}
