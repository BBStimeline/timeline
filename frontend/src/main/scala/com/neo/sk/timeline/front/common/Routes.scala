package com.neo.sk.timeline.front.common

object Routes {

  object UserRoutes{
    private val baseUrl = "/timeline/user"
    val login= baseUrl +"/userLogin"

    val signUp = baseUrl + "/userSign"

    /**检查Session*/
    val checkSession = baseUrl + "/checkSession"

    val getLastTime = baseUrl +"/getLastTime"

    def getFeedFlow(sort:Int,lastTime:Long,pageSize:Int,up:Boolean) = baseUrl + s"/getFeedFlow?sortType=$sort&itemTime=$lastTime&pageSize=$pageSize&up=$up"


  }

  object FollowRoutes{
    private val baseUrl = "/timeline/follow"
    val addFollowBoard = baseUrl + "/addFollowBoard"
    val unFollowBoard = baseUrl + "/unFollowBoard"
  }

  object BoardRoutes{
    private val baseUrl = "/timeline/board"
    val hotBoards = baseUrl + "/hotBoards"
  }

}

object PageRoute{
  val loginPage   =  "#/LoginPage"
  val mainPage    =  "#/MainPage"
  val timelinePage  =  "#/timelinePage"
  val recordsPage =  "#/RecordsPage"
}