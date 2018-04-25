package com.neo.sk.timeline.front.pages


import com.neo.sk.timeline.front.Index
import com.neo.sk.timeline.front.common.{PageRoute, Routes}
import com.neo.sk.timeline.front.components.CommonCheck
import com.neo.sk.timeline.front.utils.{Http, Shortcut, TimeTool}
import mhtml._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.TextArea
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.FeedPost

import scala.scalajs.js.Date
import scala.xml.Elem
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * User: sky
  * Date: 2018/3/26
  * Time: 15:56
  */
object MainPage extends Index {
  override val locationHashString="#/MainPage"

  val bottom = Var(emptyHTML)//指示滑到底部
  var lastItemTime=System.currentTimeMillis() //保存当前最后Item时间
  var sortType=2 //排序方式

  val w = dom.document.body.clientWidth
  val tabBarCon=Var(0)
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
  val enter=Var(
    <dev class="enter" left={(w/2-25)+"px"} top="40%"></dev>
  )
  val articleList=Var(
    <div height="100%">{enter}</div>
  )
  val tabBar=Var(
    <p class="new-article" style="position:fixed" onclick={()=>tabBarCon.update(r => if(r==1) 2 else if(r==2) 1 else 2 )}>{tabBarText}</p>
  )

  def makeList(list: List[FeedPost]) = {
    def row(post:FeedPost) = {
        <div class="article-item">
          <h4 class="topic">{post.post.tittle}</h4>
          <div class="name">
            <img ></img>
            <span>{post.post.mainPostAuthor.authorId+"    发表于 "+TimeTool.DateFormatter(new Date(post.post.mainPostTime),"yyyy-MM-dd hh:mm:ss")}</span>
          </div>
          <div class="content">{post.post.content.substring(0,100)}</div>
          {if(sortType==1) <div></div> else
          <div class="name" style="text-align: right;color: darkolivegreen;">
          <img ></img>
          <span>{post.post.author.authorName+"    回复于 "+TimeTool.DateFormatter(new Date(post.post.postTime),"yyyy-MM-dd hh:mm:ss")}</span>
          </div>
          }
        </div>
    }
    val h = (dom.document.body.clientHeight-70)+"px"
    <div class="article-list" id="article-list" height={h}>
      <HR width="100%" color="#987cb9 SIZE=3"></HR>
      {
      lastItemTime=list.last.time
      println(lastItemTime)
      list.map(p=>row(p))
      }
    </div>
  }

  def getTopicList(sort:Int,lastTime:Long)={
    Http.getAndParse[UserFollowProtocol.UserFeedRsp](Routes.UserRoutes.getFeedFlow(sortType,lastTime,10)).map {
      case Right(rsp) =>
        if (rsp.errCode == 0) {
          makeList(rsp.normalPost.get)
        } else if(rsp.errCode == 123456){
          Shortcut.redirect("#/LoginPage")
          <div><h5>get list error</h5></div>
        }else{
          println(s"get list error: ${rsp.msg}")
          <div><h5>get list error</h5></div>
        }
      case Left(error) =>
        println(s"get list error: $error")
        <div><h5>get list error</h5></div>
    }.foreach{elm => enter.update(_ => elm)
      document.body.scrollTop = 0
      document.documentElement.scrollTop = 0
    }
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
      if (rect1.top < documentHeight1) {
        getTopicList(sortType,lastItemTime)
      }
    }else{
      <div></div>
    }
  }

  override def render:Elem = {
    dom.window.addEventListener("touchmove", fetchNewDataPost, useCapture = false)
//    CommonCheck.checkSession
    getTopicList(sortType,System.currentTimeMillis())
    <div height="100%" style="background:url(../static/img/back-1.png);width:100%" backgroundSize="100% 100%" position="fixed">
      {articleList}
      {rotate}
      {tabBar}
    </div>
  }


}
