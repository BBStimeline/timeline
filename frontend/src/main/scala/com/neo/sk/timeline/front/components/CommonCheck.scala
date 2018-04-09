package com.neo.sk.timeline.front.components

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
  * Date: 2018/3/27
  * Time: 16:55
  */
object CommonCheck {

//  def checkSession:Unit={
//    Http.getAndParse[SuccessRsp](Routes.checkSession).map{
//      case Right(rsp) =>
//        if(rsp.errCode!=0){
//          println(s"checkSession error ")
//          JsFunc.alert(s"${rsp.msg}")
//          dom.window.location.hash = PageRoute.loginPage
//        }else{
//        }
//      case Left(e)=>
//        println(s"parse error in login $e ")
//        JsFunc.alert("验证出错")
//        dom.window.location.hash = PageRoute.loginPage
//    }
//  }

}
