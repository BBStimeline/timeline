package com.neo.sk.timeline.front.common

import org.scalajs.dom._
import org.scalajs.dom.html.Div
import org.scalajs.dom.raw.HTMLElement


/**
  * User: Taoz
  * Date: 12/26/2016
  * Time: 1:36 PM
  */
trait Component[T <: HTMLElement] {

  protected lazy val selfDom: T = build()

  protected def build(): T

  final def render(): T = selfDom


}




trait Panel extends Component[Div] {

  def locationHash: String

  def mounted(): Unit = {}

  def unMounted(): Unit = {}

  def get: Div = selfDom

  def getRefresh: Div = build()
}

trait Page extends Panel

