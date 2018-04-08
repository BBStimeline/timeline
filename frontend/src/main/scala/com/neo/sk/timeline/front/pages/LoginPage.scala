package com.neo.sk.timeline.front.pages

import com.neo.sk.timeline.front.Index
import com.neo.sk.timeline.front.common.{PageRoute, Routes}
import com.neo.sk.timeline.front.utils.{Http, JsFunc}
import mhtml._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.TextArea
import mhtml.Var
import org.scalajs.dom.html.Input
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.xml.Node
import scala.concurrent.ExecutionContext.Implicits.global
import com.neo.sk.timeline.shared.ptcl._
/**
  * User: sky
  * Date: 2018/3/26
  * Time: 15:39
  */
object LoginPage extends Index {

  def login():Unit ={
    //只有这样才能获取到Unput中的值
    val nametext=dom.window.document.getElementById("username").asInstanceOf[Input]
    val passwordtext=dom.window.document.getElementById("password").asInstanceOf[Input]
    val name=nametext.value
    val password=passwordtext.value
    if(!name.equals("") && !password.equals("")){
      val bodyStr =AdminConfirm(name,password).asJson.noSpaces
      Http.postJsonAndParse[ErrorRsp](Routes.login,bodyStr).map{
        case Right(rsp) =>
          if(rsp.errCode!=0){
            println(s"name or password error in login ${rsp.errCode} ")
            JsFunc.alert(s"${rsp.msg}")
            nametext.value=""
            passwordtext.value=""
          }else{
            dom.window.location.hash = PageRoute.mainPage
          }
        case Left(e)=>
          println(s"parse error in login $e ")
          JsFunc.alert("登录出错")
          nametext.value=""
          passwordtext.value=""
      }

    }else{
      JsFunc.alert("请输入完整用户名和密码")
      nametext.value=""
      passwordtext.value=""
    }

  }



  val UserName:Var[Node] =Var(
    <div class="pure-control-group" style="margin-top: 200px;">
      <label >账户：</label>
      <input type="text" id="username" placeholder="用户名" autofocus="true"></input>
    </div>
  )

  val PassWord:Var[Node] =Var(
    <div class="pure-control-group">
      <label >密码：</label>
      <input type="password" id="password" placeholder="密码" ></input>
    </div>
  )

  val LoginBtn:Var[Node]=Var(
    <div style="margin-top:10px;width:100%;">
      <button cls = "loginButton" marginTop = "10px" onclick={()=>login() } >
        登录
      </button>
    </div>
  )

  def app: xml.Node = {
    <div style="width: 100%;height: 100%;
    text-align: center;
    font-size: x-large;
    font-style: italic;
    color: tan;background-color: powderblue;position:absolute">
      {UserName}
      {PassWord}
      {LoginBtn}
    </div>
  }

}
