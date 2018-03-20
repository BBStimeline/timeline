package com.neo.sk.timeline.front

import scala.scalajs.js
import com.neo.sk.timeline.front.common.PageSwitcher

/**
  * User: Taoz
  * Date: 6/3/2017
  * Time: 1:03 PM
  */
object Main extends js.JSApp {

    @scala.scalajs.js.annotation.JSExport
    override def main(): Unit = {
      PageSwitcher.switchPageFirst()
    }

}
