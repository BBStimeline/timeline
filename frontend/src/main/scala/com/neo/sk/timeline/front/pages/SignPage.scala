package com.neo.sk.timeline.front.pages

import com.neo.sk.timeline.front.Index
import com.neo.sk.timeline.front.common.{PageRoute, Routes}
import com.neo.sk.timeline.front.utils.{Http, JsFunc}
import com.neo.sk.timeline.shared.ptcl.UserProtocol.{AdminConfirm, UserSignReq}
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
  * Date: 2018/4/9
  * Time: 22:33
  */
object SignPage extends Index {
  val UserName:Var[Node] =Var(
    <div class="pure-control-group" style="margin-top: 10%;">
      <label >账户：</label>
      <input type="text" id="username" placeholder="用户名" autofocus="true"></input>
    </div>
  )

  val Mail:Var[Node] =Var(
    <div class="pure-control-group" >
      <label >邮箱：</label>
      <input type="text" id="mail" placeholder="邮箱" autofocus="true"></input>
    </div>
  )

  val City:Var[Node] =Var(
    <div class="pure-control-group" >
      <label >城市：</label>
      <input type="text" id="city" placeholder="城市" autofocus="true"></input>
    </div>
  )

  val Gender=Var(
    <div class="pure-control-group" >
      <label >性别：</label>
      <select>
        <option value ="0">男</option>
        <option value ="1">女</option>
      </select>
    </div>
  )

  val PassWord:Var[Node] =Var(
    <div class="pure-control-group" >
      <label >密码：</label>
      <input type="text" id="password" placeholder="密码" autofocus="true"></input>
    </div>
  )

  val SignBtn:Var[Node]=Var(
    <div style="margin-top:10px;width:100%;">
      <button cls = "SignButton" marginTop = "10px" onclick={()=>sign() } >
        注册
      </button>
    </div>
  )

  def sign():Unit ={
    //只有这样才能获取到Unput中的值
    val nametext=dom.window.document.getElementById("username").asInstanceOf[Input]
    val passwordtext=dom.window.document.getElementById("password").asInstanceOf[Input]
    val mailtext=dom.window.document.getElementById("mail").asInstanceOf[Input]
    val citytext=dom.window.document.getElementById("city").asInstanceOf[Input]
    val name=nametext.value
    val password=passwordtext.value
    val mail=mailtext.value
    val city=citytext.value
    if(!name.equals("") && !password.equals("")){
      val bodyStr =UserSignReq(name,password,mail,city,"",0).asJson.noSpaces
      Http.postJsonAndParse[ErrorRsp](Routes.UserRoutes.signUp,bodyStr).map{
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

  def app: xml.Node = {
    <div style="width: 100%;height: 100%;
    text-align: center;
    font-size: x-large;
    font-style: italic;
    color: tan;background-color: powderblue;position:absolute">
      {UserName}
      {Mail}
      {City}
      {Gender}
      {PassWord}
      {SignBtn}
    </div>
  }
}
