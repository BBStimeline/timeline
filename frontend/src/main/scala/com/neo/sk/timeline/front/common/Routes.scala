package com.neo.sk.timeline.front.common

object Routes {

  object UserRoutes{
    private val baseUrl = "/timeline/user"
    val login= baseUrl +"/userLogin"

    val signUp = baseUrl + "/userSign"

    /**检查Session*/
    val checkSession = baseUrl + "/checkSession"

    def getFeedFlow(sort:Int,lastTime:Long,pageSize:Int) = baseUrl + s"/getFeedFlow?sortType=$sort&lastItemTime=$lastTime&pageSize=$pageSize"
  }

}

object PageRoute{
  val loginPage   =  "#/LoginPage"
  val mainPage    =  "#/MainPage"
  val timelinePage  =  "#/timelinePage"
  val recordsPage =  "#/RecordsPage"
}