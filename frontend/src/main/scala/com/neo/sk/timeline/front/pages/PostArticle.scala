package com.neo.sk.timeline.front.pages

import com.neo.sk.timeline.front.Index
import com.neo.sk.timeline.front.common.{PageRoute, Routes}
import com.neo.sk.timeline.front.components.CommonCheck
import com.neo.sk.timeline.front.utils.{Http, JsFunc, Shortcut, TimeTool}
import com.neo.sk.timeline.shared.ptcl.PostProtocol._
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
  * Date: 2018/6/4
  * Time: 10:10
  */
object PostArticle extends Index {
  override val locationHashString="#/PostArticle"
  var titleValue=""
  var contentValue=""
  def postArticle={
    val bodyStr = UserAddPost(titleValue,contentValue).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp]("", bodyStr).foreach {
      case Right(rsp) =>
        println(s"add request sent success, result: $rsp")
        Shortcut.redirect("")
      case Left(error) =>
        println(s"request sent complete, but error happen: $error")
        dom.window.alert(s"error: $error")
    }
  }

  override def render={
    <div>
      <div class="pure-u-1-8">
      </div>
      <div class="pure-u-3-4 pure-form pure-form-aligned" style="paddingTop:60px">
        <div class="pure-control-group">
          <label>标题</label>
          <input class="pure-input-1" onchange={ (e: Event) =>
            titleValue = e.target.asInstanceOf[TextArea].value}>{titleValue}</input>
        </div>
        <div class="pure-control-group">
          <label>内容</label>
          <textarea class="pure-input-1" onchange={ (e: Event) =>
            contentValue = e.target.asInstanceOf[TextArea].value}>{contentValue}</textarea>
        </div>
        <div class="pure-controls">
          <div class="btn-img" onclick={()=>postArticle}>
            <img src="static/img/right.png"></img>
          </div>
          <div class="btn-img" onclick={()=>window.history.back()}>
            <img src="static/img/close.png"></img>
          </div>
        </div>
      </div>

    </div>
  }
}
