package com.neo.sk.timeline.front.pages


import com.neo.sk.timeline.front.Index
import com.neo.sk.timeline.front.common.{PageRoute, Routes}
import com.neo.sk.timeline.front.components.CommonCheck
import com.neo.sk.timeline.front.utils.{Http, JsFunc, Shortcut, TimeTool}
import com.neo.sk.timeline.shared.ptcl.PostProtocol.Post
import mhtml._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.{Input, TextArea}
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.FeedPost

import scala.scalajs.js.Date
import scala.xml.{Elem, Node}
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.Unit
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * User: sky
  * Date: 2018/3/26
  * Time: 15:56
  */
object MainPage extends Index {
  override val locationHashString="#/MainPage"

  val bottom = Var(emptyHTML)//指示滑到底部
  var lastItemTime1=System.currentTimeMillis() //保存当前最后Item时间
  var lastItemTime2=System.currentTimeMillis() //保存当前最后Item时间
  var firstItemTime1=System.currentTimeMillis() //保存当前第一Item时间
  var firstItemTime2=System.currentTimeMillis() //保存当前第一Item时间
  var sortType=1 //排序方式

  val w = dom.document.body.clientWidth
  var list = List.empty[FeedPost]
  val tabBarCon=Var(0)
  var isFetching = 0

  val orderList:Var[Node] = Var(emptyHTML) //排序方式

  val tabBarText=tabBarCon.map{
    case 1 => "+"
    case 2 => "-"
    case _ => "+"
  }
  val rotateClass=tabBarCon.map{
    case 1 => "outer rotate-out"
    case 2 => "outer rotate-in"
    case _ => "outer"
  }
  val enter:Var[Node]=Var(
    <div class="enter" left={(w/2-25)+"px"} top="40%"></div>
  )

  val articleList=Var(
    <div height="100%">{enter}</div>
  )
  val tabBar=Var(
    <p class="new-article" style="position:fixed" onclick={()=>tabBarCon.update(r => if(r==1) 2 else if(r==2) 1 else 2 )}>{tabBarText}</p>
  )

  def makeList(list: List[FeedPost]):Node= {
    def row(post:FeedPost) = {
        <div class="article-item">
          <h4 class="topic">{post.post.tittle}</h4>
          <div class="name">
            <span>{post.post.mainPostAuthor.authorId+"    发表于 "+TimeTool.DateFormatter(new Date(post.post.mainPostTime),"yyyy-MM-dd hh:mm:ss")}</span>
          </div>
          <div class="content">{post.post.content.substring(0,100)}</div>
          {if(sortType==1) <div></div> else
          <div class="name" style="text-align: right;color: darkolivegreen;">
          <span>{post.post.author.authorName+"    回复于 "+TimeTool.DateFormatter(new Date(post.post.postTime),"yyyy-MM-dd hh:mm:ss")}</span>
          </div>
          }
        </div>
    }
    val h = (dom.document.body.clientHeight-70)+"px"
    <div class="article-list" id="article-list" height={h}>
      <HR width="100%" id="hrLine" color="#987cb9 SIZE=3"></HR>
       {if(sortType==1) {
         lastItemTime1=list.last.time
         firstItemTime1=list.head.time
       } else {
         lastItemTime2=list.last.time
         firstItemTime2=list.head.time
       }
       println("lastTime1"+lastItemTime1)
       println("lastTime2"+lastItemTime2)
       println("firstTime1"+firstItemTime1)
       println("firstTime2"+firstItemTime2)
       list.map(p=>row(p))
      }
    </div>
  }

  def getTopicList(sort:Int,up:Boolean)={
    val itemTime=if(sort==1){
      if(up) firstItemTime1 else lastItemTime1
    }else{
      if(up) firstItemTime2 else lastItemTime2
    }
    Http.getAndParse[UserFollowProtocol.UserFeedRsp](Routes.UserRoutes.getFeedFlow(sortType,itemTime,10,up)).map {
      case Right(rsp) =>
        if (rsp.errCode == 0) {
          list = if(up) List(rsp.normalPost.get,list).flatten else List(list,rsp.normalPost.get).flatten
          enter:= makeList(list)
          isFetching=0
        } else if(rsp.errCode == 123456){
          JsFunc.alert("Session Error")
          Shortcut.redirect("#/LoginPage")
        }else if(rsp.errCode == 120001){
          JsFunc.alert("No more Date")
          enter:=makeList(list)
        }else{
          println(s"get list error: ${rsp.msg}")
          enter := <div height="100%"><h5>get list error</h5></div>
        }
      case Left(error) =>
        println(s"get list error: $error")
        enter := <div height="100%"><h5>get list error</h5></div>
    }
//      document.body.scrollTop = 0
//      document.documentElement.scrollTop = 0
    }

/*  //tabBar的按钮
  val tab1 = img(*.src:="/hw1701a/static/image/return.png",*.left:="-60px",*.top:="20px").render
  val tab2 = img(*.src:="/hw1701a/static/image/search_white.png",*.left:="60px",*.top:="-60px").render
  val tab3 = img(*.src:="/hw1701a/static/image/brush.png",*.left:="180px",*.top:="20px").render
  tab1.onclick={
    e:MouseEvent=>
      //根据登录状态判定去登录or登出
      e.preventDefault()
      if(window.localStorage.getItem("user")!=null && window.localStorage.getItem("user").length()>0){
        val confirm = dom.window.confirm(s"真的要退出吗！")
        if (confirm) {
          Http.getAndParse[CommonRsp](UserRoute.logout).map{
            case Right(rsp) =>
              if (rsp.errCode == 0) {
                window.localStorage.setItem("user","")
                Shortcut.redirect(UserRoute.login)
              } else {
                JsFunc.alert(s"delete failed: ${rsp.msg}")
              }
            case Left(err) =>
              println(s"delete failed: ${err.getMessage}")
              JsFunc.alert(s"delete failed: ${err.getMessage}")
          }
        }
      }
      else {
        Shortcut.redirect(UserRoute.login)
      }
  }
  tab2.onclick={
    e:MouseEvent=>
      //去搜索
      e.preventDefault()
      Shortcut.redirect(UserRoute.search)
  }
  tab3.onclick={
    e:MouseEvent=>
      //根据登录状态判定去发帖or登录
      e.preventDefault()
      if(window.localStorage.getItem("user")!=null && window.localStorage.getItem("user").length()>0){
        Shortcut.redirect(UserRoute.postArticle)
      }
      else {
        Shortcut.redirect(UserRoute.login)
      }
  }*/

  //tabBar
  val rotate = Var(
    <div class={rotateClass} style={"position:fixed;left:"+(w/2-160)+"px"}>
      <img src="../static/img/return.png" style="left: -60px;top: 20px;"></img>
      <img src="../static/img/search_white.png" style="left: 60px;top: -60px;"></img>
      <img src="../static/img/brush.png" style="left: 180px;top: 20px;"></img>
    </div>
  )

  val fetchNewDataPost = { e:TouchEvent =>
    val lastItem = dom.window.document.getElementById("article-list").lastElementChild
    if(lastItem!= null) {
      val rect1 = lastItem.getBoundingClientRect()
      val documentHeight1 = document.documentElement.clientHeight
      println(rect1.top)
      println(documentHeight1)
      if (rect1.top < documentHeight1&&isFetching==0) {
        isFetching=1
        getTopicList(sortType,false)
      }
    }else{
      <div></div>
    }
  }

  def sortTypeChange={
    sortType=dom.document.getElementById("sortType").asInstanceOf[Input].value.toInt
    list = List.empty[FeedPost]
    enter:= <div class="enter" left={(w/2-25)+"px"} top="40%"></div>
    getTopicList(sortType,false)
    println(sortType)
  }

  def getUpTopicList:Unit={getTopicList(sortType,true)}

  def getLastTime={
    Http.getAndParse[UserFollowProtocol.LastTimeRsp](Routes.UserRoutes.getLastTime).map {
      case Right(rsp) =>
        if (rsp.errCode == 0) {
          lastItemTime1=rsp.first.get._1
          lastItemTime2=rsp.first.get._2
          if(lastItemTime1==0l) lastItemTime1=System.currentTimeMillis()
          if(lastItemTime2==0l) lastItemTime2=System.currentTimeMillis()
          getTopicList(sortType,false)
        } else if(rsp.errCode == 123456){
          JsFunc.alert("Session Error")
          Shortcut.redirect("#/LoginPage")
        }else{
          println(s"get list error: ${rsp.msg}")
        }
      case Left(error) =>
        println(s"get list error: $error")
    }
  }

  override def render:Elem = {
    dom.window.addEventListener("touchmove", fetchNewDataPost, useCapture = false)
//    CommonCheck.checkSession
    getLastTime
    <div height="100%" style="background:url(../static/img/back-1.png);width:100%" backgroundSize="100% 100%" position="fixed">
      <div>
        <select id="sortType" onchange={()=>sortTypeChange}>
          <option value ="1">发帖时间</option>
          <option value ="2">回帖时间</option>
        </select>
      </div>
      {articleList}
      <img src="../static/img/up.png" style={"position: fixed;height: 50px;width: 50px;right: 10px;bottom:"+ w/2 +"px;"} onclick={()=>document.body.scrollTop = 0
        document.documentElement.scrollTop = 0}></img>
      <img src="../static/img/down.png" style={"position: fixed;height: 50px;width: 50px;right: 10px;bottom:"+ (w/2+55) +"px;"} onclick={()=>getUpTopicList}></img>
      {rotate}
      {tabBar}
    </div>
  }


}
