package com.neo.sk.timeline.front.pages

import com.neo.sk.timeline.front.Index
import com.neo.sk.timeline.front.common.{PageRoute, Routes}
import com.neo.sk.timeline.front.components.CommonCheck
import com.neo.sk.timeline.front.utils.{Http, JsFunc, Shortcut, TimeTool}
import com.neo.sk.timeline.shared.ptcl.PostProtocol.TopicInfo
import mhtml._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.{Input, TextArea}
import com.neo.sk.timeline.shared.ptcl.{SuccessRsp, UserFollowProtocol}
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol._

import scala.scalajs.js.Date
import scala.xml.{Elem, Node}
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.Unit
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * User: sky
  * Date: 2018/4/26
  * Time: 11:21
  */
object FollowListPage extends Index {
  override val locationHashString="#/BoardListPage"

  var hotList:List[(Int,String,String)]=Nil
  var myBoardList:List[(Int,String,String)]=Nil
  var myTopicList:List[(Int,String,Long)]=Nil
  var myUserList:List[(Int,String,String)]=Nil
  val hotBoardDivList=Var(List(<div></div>))
  val myBoardDivList=Var(List(<div></div>))
  val myTopicDivList=Var(List(<div></div>))
  val myUserDivList=Var(List(<div></div>))

  def makeBoardList(list: List[(Int,String,String)], add:Boolean)= {
    def row(board:(Int,String,String)) = {
      <button class="buttonCss" onclick={()=>
        if(add) addFollowBoard(board._1,board._2,board._3)
        else delFollowBoard(board._1,board._2,board._3)
      }>{board._3}</button>
    }

    {
      list.map(p => row(p))
    }
  }

  def makeTopicList(list: List[(Int,String,Long)])= {
    def row(topic:(Int,String,Long)) = {
      <button class="buttonCss" onclick={()=>
        delFollowTopic(topic._1,topic._2,topic._3)
      }>{topic._1+"-"+topic._2+"-"+topic._3}</button>
    }

    {
      list.map(p => row(p))
    }
  }

  def makeUserList(list: List[(Int,String,String)])= {
    def row(user:(Int,String,String)) = {
      <button class="buttonCss" onclick={()=>
        delFollowUser(user._1,user._2,user._3)
      }>{user._3}</button>
    }

    {
      list.map(p => row(p))
    }
  }

  def delFollowBoard(origin:Int,board:String,boardTitle:String):Unit={
    val confirm = dom.window.confirm(s"确定取关板块！")
    if (confirm) {
      val bodyStr = UnFollowBoardReq(origin, board, boardTitle).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](Routes.FollowRoutes.unFollowBoard, bodyStr).map {
        case Right(rsp) =>
          if (rsp.errCode != 0) {
            println(s"name or password error in login ${rsp.errCode} ")
            JsFunc.alert(s"${rsp.msg}")
          } else {
            val a = myBoardList.filter(r => r._2 != board)
            myBoardList = a
            hotList = hotList.toSet.&~(myBoardList.toSet).toList
            hotBoardDivList := makeBoardList(hotList, true)
            myBoardDivList := makeBoardList(myBoardList, false)
            JsFunc.alert(rsp.msg)
          }
        case Left(e) =>
          JsFunc.alert(e.getMessage)
      }
    }
  }

  def delFollowTopic(origin:Int,board:String,topicId:Long):Unit={
    val confirm = dom.window.confirm(s"确定取关话题！")
    if (confirm) {
      val bodyStr = UnFollowTopicReq(origin, board, topicId).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](Routes.FollowRoutes.unFollowTopic, bodyStr).map {
        case Right(rsp) =>
          if (rsp.errCode != 0) {
            println(s"name or password error in login ${rsp.errCode} ")
            JsFunc.alert(s"${rsp.msg}")
          } else {
            val a = myTopicList.filter(r => !(r._1 == origin && r._2 == board && r._3 == topicId))
            myTopicList = a
            myTopicDivList := makeTopicList(myTopicList)
            JsFunc.alert(rsp.msg)
          }
        case Left(e) =>
          JsFunc.alert(e.getMessage)
      }
    }
  }

  def delFollowUser(origin:Int,userId:String,userName:String):Unit={
    val confirm = dom.window.confirm(s"确定取关用户！")
    if (confirm) {
      val bodyStr = UnFollowUser(origin, userId, userName).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](Routes.FollowRoutes.unFollowUser, bodyStr).map {
        case Right(rsp) =>
          if (rsp.errCode != 0) {
            println(s"name or password error in login ${rsp.errCode} ")
            JsFunc.alert(s"${rsp.msg}")
          } else {
            val a = myUserList.filter(r => !(r._1 == origin && r._2 == userId))
            myUserList = a
            myUserDivList := makeUserList(myUserList)
            JsFunc.alert(rsp.msg)
          }
        case Left(e) =>
          JsFunc.alert(e.getMessage)
      }
    }
  }

  def addFollowBoard(origin:Int,board:String,boardTitle:String):Unit={
    val bodyStr =AddFollowBoardReq(origin,board,boardTitle).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](Routes.FollowRoutes.addFollowBoard,bodyStr).map{
      case Right(rsp) =>
        if(rsp.errCode!=0){
          println(s"name or password error in login ${rsp.errCode} ")
          JsFunc.alert(s"${rsp.msg}")
        }else{
          myBoardList::=(origin,board,boardTitle)
          hotList= hotList.toSet.&~(myBoardList.toSet).toList
          hotBoardDivList:=makeBoardList(hotList,true)
          myBoardDivList:=makeBoardList(myBoardList,false)
          JsFunc.alert(rsp.msg)
        }
      case Left(e)=>
        JsFunc.alert(e.getMessage)
    }
  }

  def getHotBoards:Unit={
    Http.getAndParse[GetHotBoardsListRsp](Routes.BoardRoutes.hotBoards).map{
      case Right(rsp) =>
        if(rsp.errCode!=0){
          println(s"name or password error in login ${rsp.errCode} ")
          JsFunc.alert(s"${rsp.msg}")
        }else{
          val myb=rsp.myBoards.getOrElse(Nil).toSet
          val hot=rsp.hotBoards.getOrElse(Nil).toSet
          hotList=(hot.&~(myb)).toList
          myBoardList=myb.toList
          hotBoardDivList:=makeBoardList(hotList,true)
          myBoardDivList:=makeBoardList(myBoardList,false)
          myTopicDivList:=makeTopicList(rsp.myTopic.getOrElse(Nil))
          myUserDivList:=makeUserList(rsp.myUsers.getOrElse(Nil))
        }
      case Left(e)=>
        JsFunc.alert(e.getMessage)
    }
  }




  override def render:Elem = {
    getHotBoards
    <div style="background:url(static/img/back-2.png);width:100%;height:100%" backgroundSize="100% 100%">
      <div style="width:100%;text-align: center">
        <div style="width:100%;hight:30%">
          <p style="font-size: 23px;color: slategray;">已关注板块</p>
          {myBoardDivList}
        </div>
        <HR width="100%" id="hrLine" color="#987cb9 SIZE=3"></HR>
        <div style="width:100%;hight:30%">
          <p style="font-size: 23px;color: slategray;">已关注话题</p>
          {myTopicDivList}
        </div>
        <HR width="100%" id="hrLine" color="#987cb9 SIZE=3"></HR>
        <div style="width:100%;hight:30%">
          <p style="font-size: 23px;color: slategray;">已关注用户</p>
          {myUserDivList}
        </div>
        <HR width="100%" id="hrLine" color="#987cb9 SIZE=3"></HR>
        <div style="width:100%;hight:30%">
          <p style="font-size: 23px;color: slategray;">热门板块点击关注</p>
          {hotBoardDivList}
        </div>

        <div style="position: fixed;bottom: 10px;left: 20px;" onclick={()=>Shortcut.redirect("#/MainPage")}>
          <img src="static/img/return.png" style="height:50px;width:50px"></img>
        </div>
      </div>
    </div>
  }

}
