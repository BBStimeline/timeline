package com.neo.sk.timeline.front.pages

import com.neo.sk.timeline.front.Index
import com.neo.sk.timeline.front.common.{PageRoute, Routes}
import com.neo.sk.timeline.front.components.CommonCheck
import com.neo.sk.timeline.front.utils.Http
import mhtml._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.TextArea
/**
  * User: sky
  * Date: 2018/3/26
  * Time: 15:56
  */
object MainPage extends Index {
  val listGroup=Var(
    <div class="list-group">
      <a href={PageRoute.timelinePage} class="list-group-item list-group-item-success">金币管理页面</a>
      <a href={PageRoute.recordsPage} class="list-group-item list-group-item-info">记录查询页面</a>
    </div>
  )
  def app:xml.Node = {
//    CommonCheck.checkSession
    <div class="jumbotron">
      <h1>Hello, manager!</h1>
      <p>Here, you can easily manage the timeline system. Wish you a happy experience!</p>
      <p>For details, please click the button below.</p>
      <br></br>
      <p>{listGroup}</p>
    </div>
  }

}
