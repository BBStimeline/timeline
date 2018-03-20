package com.neo.sk.timeline.front.utils

import scala.scalajs.js.Date

object MyUtil {
  def dataFormatDefault(timestamp: Long, format: String = "yyyy-MM-dd HH:mm:ss"): String = {
    DateFormatter(new Date(timestamp), format)
  }

  def timestamp2Date(timestamp: Long, format: String = "yyyy/MM/dd"): String = {
    DateFormatter(new Date(timestamp), format)
  }

  def DateFormatter(date: Date, `type`: String): String = {
    val y = date.getFullYear()
    val m = date.getMonth() + 1
    val d = date.getDate()
    val h = date.getHours()
    val mi = date.getMinutes()
    val s = date.getSeconds()
    val mS = if (m < 10)
      "0" + m
    else
      m
    val dS = if (d < 10)
      "0" + d
    else
      d
    val hS = if (h < 10)
      "0" + h
    else
      h
    val miS = if (mi < 10)
      "0" + mi
    else
      mi
    val sS = if (s < 10)
      "0" + s
    else
      s
    `type` match {
      case "YYYY-MM-DD hh:mm:ss" =>
        y + "-" + mS + "-" + dS + " " + hS + ":" + miS + ":" + sS
      case "YYYY-MM-DD hh:mm" =>
        y + "-" + mS + "-" + dS + " " + hS + ":" + miS
      case "YYYY-MM-DD" =>
        y + "-" + mS + "-" + dS
      case "YYYY-MM" =>
        y + "-" + mS
      case "MM-DD" =>
        mS + "-" + dS
      case "hh:mm" =>
        hS + ":" + miS
      case "yyyy/MM/dd" =>
        y + "/" + mS + "/" + dS
      case x =>
        y + "-" + mS + "-" + dS + " " + hS + ":" + miS + ":" + sS
    }
  }

}
