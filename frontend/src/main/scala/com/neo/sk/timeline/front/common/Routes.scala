package com.neo.sk.timeline.front.common

object Routes {

  private val baseUrl = "/timeline/admin"

  val login= baseUrl +"/rsf/loginSubmit"

  val usertimeline = baseUrl + "/usertimeline"
  val recordList = baseUrl + "/recordList"
  val getUserRecords = baseUrl + "/rsf/getUserRecords"

  val searchRecord =baseUrl + s"/searchRecords"

  /**检查Session*/
  val checkSession = baseUrl + "/checkSession"
}

object PageRoute{
  val loginPage   =  "#LoginPage"
  val mainPage    =  "#MainPage"
  val timelinePage  =  "#timelinePage"
  val recordsPage =  "#RecordsPage"
}