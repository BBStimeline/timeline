package com.neo.sk.timeline.front.pages.admin

import com.neo.sk.timeline.front.Index
import com.neo.sk.timeline.front.common.{PageRoute, Routes}
import com.neo.sk.timeline.front.utils.{Http, JsFunc, Shortcut}
import com.neo.sk.timeline.shared.ptcl.UserProtocol._
import mhtml._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.TextArea
import mhtml.Var
import org.scalajs.dom.html.Input
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.xml.{Elem, Node}
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.timeline.shared.ptcl._
/**
  * User: sky
  * Date: 2018/6/4
  * Time: 11:26
  */
object AdminMainPage extends Index {
  override val locationHashString="#/AdminLoginPage"
  val flagVar=Var(1)
  var appName=""
  var appId=""
  var appUrl=""
  var appSecureKey=""
  val addDiv= <div>
    <div class="pure-u-3-4 pure-form pure-form-aligned" style="paddingTop:60px">
      <div class="pure-control-group">
        <label>数据源名</label>
        <input class="pure-input-1" onchange={ (e: Event) =>
          appName = e.target.asInstanceOf[TextArea].value}>{appName}</input>
      </div>
      <div class="pure-control-group">
        <label>数据源Id</label>
        <input class="pure-input-1" onchange={ (e: Event) =>
          appId = e.target.asInstanceOf[TextArea].value}>{appId}</input>
      </div>
      <div class="pure-control-group">
        <label>数据源同步接口</label>
        <input class="pure-input-1" onchange={ (e: Event) =>
          appUrl = e.target.asInstanceOf[TextArea].value}>{appUrl}</input>
      </div>
      <div class="pure-control-group">
        <label>数据源同步鉴权</label>
        <input class="pure-input-1" onchange={ (e: Event) =>
          appSecureKey = e.target.asInstanceOf[TextArea].value}>{appSecureKey}</input>
      </div>
      <div class="pure-controls">
        <div class="btn-img" onclick={()=>window.alert("Ok")}>
          <img src="static/img/right.png"></img>
        </div>
      </div>
    </div>
  </div>

  val addBar=Var(<div></div>)

  val mainBar=Var(
    <div>
    <ul>
      <li>
        <span>水木论坛</span>
        <button class="loginButton" onclick={()=>startSys}>开始同步数据</button>
        <button class="loginButton" onclick={()=>stopSys}>停止同步数据</button>
      </li>
    </ul>
  </div>
  )
  val sysBar=flagVar.map{
    case 1=> Some("active")
    case _=> None
  }
  val dataBar=flagVar.map{
    case 2=> Some("active")
    case _=> None
  }

  def startSys:Unit={
    Http.getAndParse[SuccessRsp](Routes.AdminRoutes.startSys).map{
      case Right(rsp) =>
        if(rsp.errCode!=0){
          println(s"name or password error in login ${rsp.errCode} ")
          JsFunc.alert(s"${rsp.msg}")
        }else{
          window.alert("OK")
        }
      case Left(e)=>
        println(s"parse error in login $e ")
    }
  }

  def stopSys:Unit={
    Http.getAndParse[SuccessRsp](Routes.AdminRoutes.stopSys).map{
      case Right(rsp) =>
        if(rsp.errCode!=0){
          println(s"name or password error in login ${rsp.errCode} ")
          JsFunc.alert(s"${rsp.msg}")
        }else{
          window.alert("OK")
        }
      case Left(e)=>
        println(s"parse error in login $e ")
    }
  }

  def addData={
    addBar:=addDiv
  }


  def change(flag:Int)={
    flagVar.update(_=>flag)
    addBar:= <div></div>
    if(flag==1){
      mainBar:= <div>
        <ul>
          <li>
            <span>水木论坛</span>
            <button class="loginButton" onclick={()=>startSys}>开始同步数据</button>
            <button class="loginButton" onclick={()=>stopSys}>停止同步数据</button>
          </li>
        </ul>
      </div>
    }else{
      mainBar:= <div>
        <ul>
          <li>水木论坛</li>
        </ul>
        <button class="loginButton" onclick={()=>addData}>添加数据源</button>
        {addBar}
      </div>
    }
  }


  override def render:Elem  = {
    <div style="margin-top: 2%;background-color: #f8f8f8;width:100%;height:100%;overflow-x: hidden;">
      <h1>管理中心</h1>
      <div style="margin-top:4px;margin-left:5%;width:95%">
        <ul class="nav nav-tabs" style="width:90%; margin-left:5%;margin-top:20px">
          <li role="presentation" class={sysBar} onclick={()=>change(1)}><a>数据源同步</a></li>
          <li role="presentation" class={dataBar} onclick={()=>change(2)}><a>数据源管理</a></li>
        </ul>
        <div style="width:90%;height:100%;margin-left:5%;margin-top:20px">
        </div>
      </div>
      <div style="width:90%;height:100%;margin-left:5%;margin-top:20px">
        {mainBar}
      </div>
    </div>
  }
}
