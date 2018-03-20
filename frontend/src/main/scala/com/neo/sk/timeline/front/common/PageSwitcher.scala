package com.neo.sk.timeline.front.common

import org.scalajs.dom
import org.scalajs.dom.HashChangeEvent
import com.neo.sk.timeline.front.pages._

/**
  * User: Taoz
  * Date: 6/3/2017
  * Time: 1:46 PM
  */
object PageSwitcher {

  import scalatags.JsDom.short._

  private var currentPage: Page = _

  private val p1 = """/""".r

  private val bodyContent = div(*.height := "100%").render

  private val basePath =
    Option(dom.document.getElementById("basePath"))
      .map(_.innerHTML).getOrElse("/timeline/")

  def elementIdHtml(id: String): Option[String] = Option(dom.document.getElementById(id)).map(_.innerHTML)

  def getCurrentHash: String = dom.window.location.hash

  def setHash(hash: String): Unit = {
    dom.window.location.hash = hash
  }


  private[this] var internalTargetHash = ""


  //init.
  {
    dom.window.onhashchange = { _: HashChangeEvent =>
      //only handler browser history hash changed.
      if (internalTargetHash != getCurrentHash) {
        println(s"hash changed, new hash: $getCurrentHash")
        switchPageFirst()
      }
    }

    dom.document.body.appendChild(bodyContent)
  }

  def switchPageFirst(): Unit = {
    val path = elementIdHtml("pathname").getOrElse(dom.window.location.pathname)
    println(s"basePath: $basePath")
    println(s"full path: $path")

    val paths = if (path.startsWith(basePath)) {
      println(s"p1:${p1.split(path.substring(basePath.length))}")
      p1.split(path.substring(basePath.length))
    } else {
      Array[String]()
    }
    println(s"paths:${paths.foreach(println)}")
    println(s"valid paths: ${paths.mkString("[", ";", "]")}")

    val page = if (paths.length < 1) {
      println(s"paths.length error: $path")
      todo
    } else {

      //JsFunc.alert(s"here is Hub. full href: ${dom.document.location.href}")
      paths(0) match {
        case "admin" => paths(1) match {
          case "home" => AdminHome
          case "login" => Login
          case x => todo
        }
        //        case "test" => paths(1) match {
        //          case "carousel" => Carousel
        //        }
      }
    }

    switchToPage(page)
  }

  object todo extends Page {
    import scalatags.JsDom.short._
    import org.scalajs.dom.html.Div

    override def locationHash: String = ""

    override def build(): Div = {
      div(h1(s"TO DO PAGE")).render
    }
  }

  //  def todo(title: String) = h1(
  //    s"TO DO PAGE: $title"
  //  ).render


  def switchToPage(page: Page): Unit = {
    if (currentPage != null) {
      currentPage.unMounted()
    }
    currentPage = page
    bodyContent.textContent = ""
    if (getCurrentHash != page.locationHash) {
      dom.window.location.hash = page.locationHash
      internalTargetHash = page.locationHash
    }
    bodyContent.appendChild(page.get)
    currentPage.mounted()
  }

  def switchToPageRefresh(page: Page): Unit = {
    if (currentPage != null) {
      currentPage.unMounted()
    }
    currentPage = page
    bodyContent.textContent = ""
    if (getCurrentHash != page.locationHash) {
      dom.window.location.hash = page.locationHash
      internalTargetHash = page.locationHash
    }
    bodyContent.appendChild(page.getRefresh)
    currentPage.mounted()
  }

  def getCurrentPage: Page = currentPage

}
