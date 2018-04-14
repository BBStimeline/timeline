
package com.neo.sk.timeline.front

//import com.neo.sk.timeline.front.pages._
import com.neo.sk.timeline.front.pages.{LoginPage, MainPage}
import mhtml._
import org.scalajs.dom
import org.scalajs.dom.raw.Event

import scala.xml.Node
/**
  * Created by thinker on 2017/11/14.
  */
trait Index {
  def app: Node
  def cancel: Unit = ()
  val pageName = this.getClass.getSimpleName
  val url = "#" + pageName

}

object Main {
  private val indexPage = Seq[Index](
    LoginPage,
    MainPage
  )

  def getActiveApp =
    indexPage.find(_.url == dom.window.location.hash.split("&")(0)).getOrElse(indexPage.head)

  val activeExample: Var[Index] = Var(getActiveApp)

  dom.window.onhashchange = { _: Event =>
    println("here change hash")
    println(dom.window.location.hash.split("&")(0))
    if(dom.window.location.hash != "#LoginPage"){
      dom.window.localStorage.setItem("current-hash",dom.window.location.hash)
    }
    activeExample.update { old =>
      old.cancel
      getActiveApp
    }
  }

  val mainApp =

      <div style="height: 100%; width: 100%; position: absolute;">
        {activeExample.map(_.app)}
      </div>



  def main(args: Array[String]): Unit ={
    mount(dom.document.body, mainApp)
  }

}
