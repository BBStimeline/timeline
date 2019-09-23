package com.neo.sk.timeline.front

import com.neo.sk.timeline.front.pages._
import com.neo.sk.timeline.front.pages.admin.{AdminLoginPage, AdminMainPage}
import mhtml._
import org.scalajs.dom

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.xml.Elem
import scala.language.implicitConversions
/**
  * User: sky
  * Date: 2018/4/20
  * Time: 14:13
  */
trait Index extends Component{
  val locationHashString: String
}

trait Component {
  def render: Elem
}

object Component {
  implicit def component2Element(comp: Component): Elem = comp.render
}

//@JSExportTopLevel("frontend.Main")
object Main {

//  @JSExport
  def main(args: Array[String]): Unit = {
    MainEnter.show()
  }

}


object MainEnter extends PageSwitcher {

  val currentPage: Rx[Elem] = currentHashVar.map {
    case Nil => LoginPage.render
    case "LoginPage" :: Nil => LoginPage.render
    case "MainPage" :: Nil => MainPage.render
    case "FollowListPage" ::Nil => FollowListPage.render
    case "ArticlePage" :: origin :: board :: topicId ::Nil => ArticlePage.getArticlePage(origin,board,topicId)
    case "PostArticle" :: Nil => PostArticle.render
    case "ReplyPost" :: board :: topicId :: quoteId ::Nil => new ReplyPost(board,topicId.toLong,quoteId.toLong).render
    case "AdminLoginPage" ::Nil=> AdminLoginPage.render
    case "AdminMainPage" ::Nil=> AdminMainPage.render
    case _ => <div>Error Page</div>
  }

  def show(): Cancelable = {
    switchPageByHash()
    mount(dom.document.body, {currentPage})
  }

}
