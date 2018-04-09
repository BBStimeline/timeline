package com.neo.sk.timeline.front.common

object Routes {

  object UserRoutes{
    private val baseUrl = "/timeline/user"
    val login= baseUrl +"/userLogin"

    val signUp = baseUrl + "/userSign"

    /**检查Session*/
    val checkSession = baseUrl + "/checkSession"
  }



}

object PageRoute{
  val loginPage   =  "#LoginPage"
  val mainPage    =  "#MainPage"
  val timelinePage  =  "#timelinePage"
  val recordsPage =  "#RecordsPage"
}