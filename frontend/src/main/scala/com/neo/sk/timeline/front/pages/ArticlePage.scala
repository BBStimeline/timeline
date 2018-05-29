package com.neo.sk.timeline.front.pages

import com.neo.sk.timeline.front.Index
import com.neo.sk.timeline.front.common.{PageRoute, Routes}
import com.neo.sk.timeline.front.components.CommonCheck
import com.neo.sk.timeline.front.utils.{Http, JsFunc, Shortcut, TimeTool}
import com.neo.sk.timeline.shared.ptcl.PostProtocol.{GetPostListReq, GetPostListRsp, PostInfo, TopicInfo}
import mhtml._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.{Input, TextArea}
import com.neo.sk.timeline.shared.ptcl.{SuccessRsp, UserFollowProtocol}
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.{AddFollowTopicReq, AddFollowUserReq, FeedPost}

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
  * Time: 16:26
  */
object ArticlePage extends Index {
  override val locationHashString="#/ArticlePage"

  val h = dom.document.body.clientHeight
  val w = dom.document.body.clientWidth
  private var origin=0
  private var board=""
  private var topicId=0l

  val title:Var[Node]=Var(<div></div>)
  val postList:Var[Node]=Var(<div></div>)

//  left: "+(w/2+30)+"px;
  val tabBar=Var(
    <p style={"position: fixed;position: absolute;bottom: 40px;right: 0px;background: darkgrey;box-shadow: 0 0 4px rgba(0,0,0,0.2);text-align: center;border-radius: 10px 0 10px 0;color: #94cfd6;font-size: 32px;line-height: 30px;height: 50px;overflow: hidden;z-index: 6;width: 50px;"} onclick={()=>
      MainPage.fromOther=true
      Shortcut.redirect("#/MainPage")}>
      <img src="static/img/return.png" style="height: 50px;width: 50px;"></img>
    </p>
  )

  def getArticlePage(origin1:String,board1:String,topicId1:String)={
    origin=origin1.toInt
    board=board1
    topicId=topicId1.toLong
    render
  }

  def followTopic(origin:Int,board:String,topicId:Long):Unit={
    val confirm = dom.window.confirm(s"真的关注该话题吗！")
    if (confirm) {
      val bodyStr = AddFollowTopicReq(origin, board, topicId).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](Routes.FollowRoutes.addFollowTopic, bodyStr).map {
        case Right(rsp) =>
          if (rsp.errCode != 0) {
            println(s"name or password error in login ${rsp.errCode} ")
            JsFunc.alert(s"${rsp.msg}")
          } else {
            JsFunc.alert(rsp.msg)
          }
        case Left(e) =>
          JsFunc.alert(e.getMessage)
      }
    }
  }

  def followUser(origin:Int,userId:String,userName:String):Unit={
    val confirm = dom.window.confirm(s"真的关注${userName}吗！")
    if (confirm) {
      val bodyStr = AddFollowUserReq(userId, userName, origin).asJson.noSpaces
      Http.postJsonAndParse[SuccessRsp](Routes.FollowRoutes.addFollowUser, bodyStr).map {
        case Right(rsp) =>
          if (rsp.errCode != 0) {
            println(s"name or password error in login ${rsp.errCode} ")
            JsFunc.alert(s"${rsp.msg}")
          } else {
            JsFunc.alert(rsp.msg)
          }
        case Left(e) =>
          JsFunc.alert(e.getMessage)
      }
    }
  }

  def makeList(list: List[PostInfo]):Node= {
    def row(post:PostInfo) = {
      if(post.isMain) title := (<h2 style="margin:10px 20px;" onclick={()=>followTopic(post.origin,post.boardName,post.topicId)}>{post.tittle.replaceAll("Re: ","")}</h2>)
      <div class="article-item" onclick={()=>followUser(post.origin,post.author.authorId,post.author.authorName)}>
        <h4 class="topic" style="font-size: 12px;">{
          val quoteId=post.quoteId.getOrElse(post.topicId)
          if(quoteId!=post.topicId&&list.exists(_.postId == quoteId))
            list.filter(_.postId==quoteId).head.content.substring(0,55).replaceAll("&nbsp;", "").replaceAll("&gt;", ">").replaceAll("<([\\s\\S])+?>", "").replaceAll("<a([\\s\\S])+?\\/a>", "").replaceAll("<\\/font>", "")
          else ""
          }
        </h4>
        <div class="content">{post.content.replaceAll("&nbsp;", "").replaceAll("&gt;", ">").replaceAll("<([\\s\\S])+?>", "").replaceAll("<a([\\s\\S])+?\\/a>", "").replaceAll("<\\/font>", "")}</div>
        <div class="name" style="text-align: right;color: darkolivegreen;">
          <span class="name" >{post.author.authorName}</span>
          <span>{"    發表于 "+TimeTool.DateFormatter(new Date(post.postTime),"yyyy-MM-dd hh:mm:ss")}</span>
        </div>
      </div>
    }
    <div class="post-list">
      {
      list.map(p=>row(p))
      }
    </div>
  }

  def getPostList={
    val bodyStr =GetPostListReq(origin,board,topicId).asJson.noSpaces
    Http.postJsonAndParse[GetPostListRsp](Routes.PostRoutes.getPostList,bodyStr).map{
      case Right(rsp) =>
        if(rsp.errCode!=0){
          println(s"name or password error in login ${rsp.errCode} ")
          JsFunc.alert(s"${rsp.msg}")
        }else{
          postList:= makeList(rsp.list.get)
        }
      case Left(e)=>
        println(s"parse error in login $e ")
    }
  }



  override def render={
    println(origin+"-"+board+"-"+topicId)
    getPostList
    <div style={"height:"+(h-30)+"px;background:url(static/img/back-1.png);width:"+(w-30)+"px;position:fixed;padding:15px"}>
      <div class="post" style={"max-height:"+h+"px;overflow-x:hidden"}>
        {title}
        {postList}
        {tabBar}
      </div>
    </div>
  }

}
