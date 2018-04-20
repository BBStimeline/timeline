package com.neo.sk.timeline.front.pages

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
  * Date: 2018/3/26
  * Time: 15:39
  */
object LoginPage extends Index {
  override val locationHashString="#/LoginPage"

  def login():Unit ={
    //只有这样才能获取到Unput中的值
    val nametext=dom.window.document.getElementById("username").asInstanceOf[Input]
    val passwordtext=dom.window.document.getElementById("password").asInstanceOf[Input]
    val name=nametext.value
    val password=passwordtext.value
    if(!name.equals("") && !password.equals("")){
      val bodyStr =UserLoginReq(name,password).asJson.noSpaces
      Http.postJsonAndParse[UserLoginRsp](Routes.UserRoutes.login,bodyStr).map{
        case Right(rsp) =>
          if(rsp.errCode!=0){
            println(s"name or password error in login ${rsp.errCode} ")
            JsFunc.alert(s"${rsp.msg}")
            nametext.value=""
            passwordtext.value=""
          }else{
            dom.window.localStorage.setItem("userId",rsp.userInfo.getOrElse(UserInfoDetail()).userId)
            dom.window.localStorage.setItem("uId",rsp.userInfo.getOrElse(UserInfoDetail()).uid.toString)
            dom.window.localStorage.setItem("bbsId",rsp.userInfo.getOrElse(UserInfoDetail()).bbsId)
            dom.window.localStorage.setItem("face_url",rsp.userInfo.getOrElse(UserInfoDetail()).face_url)
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

  def sign():Unit ={
    //只有这样才能获取到Unput中的值
    val nametext=dom.window.document.getElementById("signName").asInstanceOf[Input]
    val passwordtext=dom.window.document.getElementById("signPwd").asInstanceOf[Input]
    val mailtext=dom.window.document.getElementById("mail").asInstanceOf[Input]
    val citytext=dom.window.document.getElementById("city").asInstanceOf[Input]
    val gendertext=dom.window.document.getElementById("gender").asInstanceOf[Input]
    val name=nametext.value
    val password=passwordtext.value
    val mail=mailtext.value
    val city=citytext.value
    if(!name.equals("") && !password.equals("")){
      val bodyStr =UserSignReq(name,password,mail,city,"",gendertext.value.toInt).asJson.noSpaces
      Http.postJsonAndParse[UserSignRsp](Routes.UserRoutes.signUp,bodyStr).map{
        case Right(rsp) =>
          if(rsp.errCode!=0){
            println(s"name or password error in login ${rsp.errCode} ")
            JsFunc.alert(s"${rsp.msg}")
            nametext.value=""
            passwordtext.value=""
          }else{
            dom.window.localStorage.setItem("userId",rsp.userInfo.getOrElse(UserInfoDetail()).userId)
            dom.window.localStorage.setItem("uId",rsp.userInfo.getOrElse(UserInfoDetail()).uid.toString)
            dom.window.localStorage.setItem("bbsId",rsp.userInfo.getOrElse(UserInfoDetail()).bbsId)
            dom.window.localStorage.setItem("face_url",rsp.userInfo.getOrElse(UserInfoDetail()).face_url)
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

  val divCheck1=Var(true)
  val divCheck2=Var(true)
  val loginClass=divCheck1.map{
    case true=> "login front"
    case _=> "login back"
  }
  val signClass=divCheck2.map{
    case true=> "logout back"
    case _=> "logout front"
  }

  val loginDiv:Var[Node]= Var(
    <div class={loginClass} onclick={()=>divClick(true)}>
      <h2 style="marginBottom:8px">SIGN IN</h2>
      <div class="pure-form">
        <div>
          <input type="text" id="username" placeholder="用户名" autofocus="true" class="pure-u-1"></input>
        </div>
        <div>
          <input type="password" id="password" placeholder="密码" class="pure-u-1"></input>
        </div>
        <p class="login-submit invalid" onclick={()=>login()}>SIGN IN</p>
      </div>
    </div>
  )

  val signDiv:Var[Node]= Var(
    <div class={signClass} onclick={()=>divClick(false)}>
      <h2 style="marginBottom:8px">CREATE NEW</h2>
      <div class="pure-form">
        <div>
          <input type="text" id="signName" placeholder="用户名" autofocus="true" class="pure-u-1"></input>
        </div>
        <div>
          <input type="password" id="signPwd" placeholder="密码" class="pure-u-1"></input>
        </div>
        <div>
          <input type="email" id="mail" placeholder="邮箱" autofocus="true" class="pure-u-1"></input>
        </div>
        <div>
          <input type="text" id="city" placeholder="城市" autofocus="true" class="pure-u-1"></input>
        </div>
        <div>
          <select id="gender" class="pure-u-1">
            <option value ="0">男</option>
            <option value ="1">女</option>
          </select>
        </div>
        <p class="login-submit invalid" onclick={()=>sign()}>CREATE NEW</p>
      </div>
    </div>
  )

  def divClick(a:Boolean)={
    if(a) {
      divCheck1.update{r =>if(r) r else !r}
      divCheck2.update(r =>if(r) r else !r)
    }
    else{
      divCheck1.update{r =>if(r) !r else r}
      divCheck2.update(r =>if(r) !r else r)
    }
  }

  val w = dom.document.body.clientWidth
  val h = (dom.document.body.clientHeight - 280)/2

  override def render:Elem = {
    <div style="background:url(../static/img/back-1.png);height:100%" backgroundSize="100% 100%" width={w+"px"}>
      <div width="100%" height={h+"px"}></div>
      <div class="pure-u-1-8"></div>
      <div class="pure-u-3-4" position="relative"></div>
      {loginDiv}
      {signDiv}
    </div>
  }

}
