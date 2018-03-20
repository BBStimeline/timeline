package com.neo.sk.timeline.utils

import java.text.SimpleDateFormat
/**
  * Created by TangYaruo on 2017/11/5.
  */
object TimeUtil {

  def date2TimeStamp(date: String): Long = {
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime
  }

  def timeStamp2Date(timestamp: Long) = {
    new SimpleDateFormat("yyyyMMddHHmmss").format(timestamp)
  }

  def timeStamp2yyyyMMdd(timestamp: Long) = {
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp)
  }

  def timestamp2DateOnly(timestamp: Long): String = {
    new SimpleDateFormat("yyyy/MM/dd").format(timestamp)
  }

  def date2Timestamp(date: String): Long = {
    new SimpleDateFormat("yyyy/MM/dd").parse(date).getTime
  }

  def getTodayZeroStamp: Long = {
    val curTime = System.currentTimeMillis()
    val date = TimeUtil.timestamp2DateOnly(curTime).split("/").take(3).mkString("-")
    val zeroStamp = TimeUtil.date2TimeStamp(date + " 00:00:00")
    zeroStamp
  }


}
