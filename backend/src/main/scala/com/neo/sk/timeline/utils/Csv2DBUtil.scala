package com.neo.sk.timeline.utils

import java.io.{BufferedReader, File, FileInputStream, InputStreamReader}
import com.neo.sk.timeline.models.dao.BoardDAO.addBoard
import com.neo.sk.timeline.models.SlickTables.rBoard
import scala.util.Random

/**
  * User: sky
  * Date: 2018/4/20
  * Time: 17:02
  */
object Csv2DBUtil {
  def getFakeTop() = {
    val file = new File("F:\\MyJava\\MyD\\timeline\\backend\\src\\main\\resources\\galaxy_public_board.csv")
    if (file.isFile && file.exists) {
      try {
        val in = new FileInputStream("F:\\MyJava\\MyD\\timeline\\backend\\src\\main\\resources\\galaxy_public_board.csv")
        val inReader = new InputStreamReader(in, "UTF-8")
        val bufferedReader = new BufferedReader(inReader)

        bufferedReader.lines().forEach { line =>
          val target = line.split(",")
          addBoard(rBoard(0l,target(1),target(2),if(target(5)=="smth") 0 else 1,target(6)))
//          println((0l,target(1),target(2),if(target(5)=="smth") 0 else 1))
        }
      } catch {
        case e: Exception =>
          println("get history exception:" + e.getStackTrace)
      }
    } else {
      println("fakTop10.txt isn't exists.")
    }
  }


  def main(args: Array[String]): Unit = {
    getFakeTop()
    Thread.sleep(10000)
  }
}
