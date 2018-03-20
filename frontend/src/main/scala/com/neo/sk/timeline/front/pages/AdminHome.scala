package com.neo.sk.timeline.front.pages

import com.neo.sk.timeline.front.Routes
import com.neo.sk.timeline.front.common.Page
import org.scalajs.dom.html.Div
import com.neo.sk.timeline.shared.ptcl._
import scalatags.JsDom.short._
/**
  * Created by TangYaruo on 2017/11/6.
  */
object AdminHome extends Page {
  override def locationHash: String = ""

  val title = h1(*.textAlign := "center")("hello")
  val content = p(*.textAlign := "center")("welcome to 2017.")
  val listGroup = div(*.cls := "list-group")(
    a(*.href := Routes.userRecords, *.cls := "list-group-item list-group-item-success")(
      "查询用户记录"
    ),
    a(*.href := Routes.redElRecords, *.cls := "list-group-item list-group-item-info")(
      "查询红包记录"
    ),
    a(*.href := Routes.statistic, *.cls := "list-group-item list-group-item-danger")(
      "按日统计数据"
    ),
    a(*.href := Routes.management, *.cls := "list-group-item list-group-item-warning")(
      "用户提现管理"
    ),
    a(*.href := Routes.userRecharge, *.cls := "list-group-item list-group-item-info")(
      "用户余额充值"
    )
  )

  override def build(): Div = {
    div(
      div(*.cls := "jumbotron")(
        h1("Hello, manager!"),
        p("Here, you can easily manage the payment system of red envelopes. Wish you a happy experience!"),
        p("For details, please click the button below."),
        br,
        p(
          listGroup
        )
      )
//      div(*.cls := "container", *.padding := "100px", *.height := "100%")(
//        div(*.cls := "row", *.style := "background-color: #FFFFFF")(
//          div(*.cls := "col-md-6 col-md-offset-3", *.padding := "20px", *.textAlign.center)(
//            listGroup
//          )
//        )
//      )

    ).render
  }

}
