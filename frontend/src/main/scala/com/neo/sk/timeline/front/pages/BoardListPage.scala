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
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.{AddFollowBoardReq, FeedPost, GetHotBoardsListRsp, UnFollowBoardReq}

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
object BoardListPage extends Index {
  override val locationHashString="#/BoardListPage"

  var hotList:List[(Int,String,String)]=Nil
  var myList:List[(Int,String,String)]=Nil
  val hotBoardList=Var(List(<div></div>))
  val myBoardList=Var(List(<div></div>))

  def makeList(list: List[(Int,String,String)],add:Boolean)= {
    def row(board:(Int,String,String)) = {
      <button class="buttonCss" onclick={()=>
        if(add) addFollowBoard(board._1,board._2,board._3)
        else delFollowBoard(board._1,board._2)
      }>{board._3}</button>
    }

    {
      list.map(p => row(p))
    }
  }

  def delFollowBoard(origin:Int,board:String):Unit={
    val bodyStr =UnFollowBoardReq(origin,board).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](Routes.FollowRoutes.unFollowBoard,bodyStr).map{
      case Right(rsp) =>
        if(rsp.errCode!=0){
          println(s"name or password error in login ${rsp.errCode} ")
          JsFunc.alert(s"${rsp.msg}")
        }else{
          val a=myList.filter(r=> r._2 != board)
          myList=a
          hotList= hotList.toSet.&~(myList.toSet).toList
          hotBoardList:=makeList(hotList,true)
          myBoardList:=makeList(myList,false)
          JsFunc.alert(rsp.msg)
        }
      case Left(e)=>
        JsFunc.alert(e.getMessage)
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
          myList::=(origin,board,boardTitle)
          hotList= hotList.toSet.&~(myList.toSet).toList
          hotBoardList:=makeList(hotList,true)
          myBoardList:=makeList(myList,false)
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
          myList=myb.toList
          hotBoardList:=makeList(hotList,true)
          myBoardList:=makeList(myList,false)
          JsFunc.alert(rsp.msg)
        }
      case Left(e)=>
        JsFunc.alert(e.getMessage)
    }
  }




  override def render:Elem = {
    getHotBoards
    <div style="background:url(../static/img/back-2.png);width:100%;height:100%" backgroundSize="100% 100%">
      <div style="width:100%;text-align: center">
        <div style="width:100%;hight:30%">
          <p style="font-size: 23px;color: slategray;">已关注板块</p>
          {myBoardList}
        </div>
        <HR width="100%" id="hrLine" color="#987cb9 SIZE=3"></HR>
        <div style="width:100%;hight:30%">
          <p style="font-size: 23px;color: slategray;">热门板块点击关注</p>
          {hotBoardList}
        </div>

        <div style="position: fixed;bottom: 10px;left: 20px;" onclick={()=>Shortcut.redirect("#/MainPage")}>
          <img src="../static/img/return.png" style="height:50px;width:50px"></img>
        </div>
      </div>
    </div>
  }

}
