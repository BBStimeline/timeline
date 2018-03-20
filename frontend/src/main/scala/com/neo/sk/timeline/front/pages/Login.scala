package com.neo.sk.timeline.front.pages

import com.neo.sk.timeline.front.Routes
import com.neo.sk.timeline.front.common.Page
import com.neo.sk.timeline.front.utils.{Http, Shortcut}
import com.neo.sk.timeline.shared.ptcl.{AdminConfirm, SuccessRsp}
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.KeyboardEvent
//import scalatags.JsDom.short.{*, button, div, fieldset, form, h1, input, label, s}

import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scalatags.JsDom.short._

object Login extends Page {
  val loginUrl = "/timeline/admin/rsf/loginSubmit"
  override def locationHash: String = ""

  val accountBox = input(*.placeholder := "name").render
  val passwordBox = input(*.`type` := "password", *.placeholder := "password.").render
  passwordBox.onkeypress = { e: KeyboardEvent =>
    if (e.charCode == KeyCode.Enter) {
      e.preventDefault()
      submitButton.click()
    }
  }
  val submitButton = button(*.cls := "loginButton", *.marginTop := "10px", *.fontSize.small)("登录").render
  val registerButton = button(*.cls := "registerButton", *.marginTop := "10px", *.fontSize.small)("注册").render


  submitButton.onclick = { e: MouseEvent =>
    val account = accountBox.value
    val password = passwordBox.value
    val bodyStr = AdminConfirm(account, password).asJson.noSpaces
    Http.postJsonAndParse[SuccessRsp](loginUrl, bodyStr).foreach {

      case Right(rsp) if rsp.errCode == 0 =>
        println(s"login request sent success, result: $rsp")
        Shortcut.redirect(Routes.home)
      case Right(rsp) =>
        println(s"${rsp.msg}")
        dom.window.alert(s"error: ${rsp.msg}")
      case Left(error) =>
        println(s"request sent complete, but error happen: $error")
        dom.window.alert(s"error: $error")
    }
    e.preventDefault()
  }


  override def build(): Div = {
    div(*.id := "merchant")(
      h1("管理员登录"),
      div(*.cls := "merchant-box")(
        form(*.cls := "pure-form pure-form-aligned")(
          fieldset(
            div(*.cls := "pure-control-group")(
              label(*.`for` := "name")("账户："),
              accountBox
            ),
            div(*.cls := "pure-control-group")(
              label(*.`for` := "password")("密码："),
              passwordBox
            ),
            submitButton
           // registerButton
          )
        )
      )
    ).render
  }
}
