package com.neo.sk.timeline.service

import akka.http.scaladsl.server.Directives._
import com.neo.sk.timeline.core.user.UserManager._

import scala.concurrent.Future
import com.neo.sk.timeline.ptcl.UserProtocol._
import com.neo.sk.timeline.shared.ptcl.{ErrorRsp, SuccessRsp}
import org.slf4j.LoggerFactory
import akka.pattern.ask
import io.circe.Error
import io.circe.generic.auto._
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.neo.sk.timeline.common.AppSettings
import com.neo.sk.timeline.models.SlickTables
import com.neo.sk.timeline.models.dao.{PostDAO, UserDAO}
import com.neo.sk.timeline.service.ServiceUtils.CommonRsp
import com.neo.sk.timeline.service.SessionBase.UserSessionKey
import com.neo.sk.timeline.shared.ptcl.UserProtocol._
import com.neo.sk.timeline.utils.SecureUtil
import com.neo.sk.timeline.Boot.{boardManager, executor, scheduler, timeout, userManager}
import com.neo.sk.timeline.core.postInfo.BoardManager
import com.neo.sk.timeline.core.postInfo.BoardManager.GetTopicList
import com.neo.sk.timeline.core.user.UserManager
import com.neo.sk.timeline.shared.ptcl.PostProtocol.{AuthorInfo, TopicInfo}
import com.neo.sk.timeline.shared.ptcl.UserFollowProtocol.{FeedPost, LastTimeRsp, UserFeedRsp}
import com.neo.sk.timeline.utils.MailUtil
import scala.concurrent.duration._

/**
  * User: sky
  * Date: 2018/4/9
  * Time: 10:04
  */
trait UserService extends ServiceUtils with SessionBase{
  private val log = LoggerFactory.getLogger(this.getClass)

  private val secretKey = "dsacsodaux84fsdcs4wc32xm"

  private val userIndex:Route=(path ("index") & get){
    getFromResource("html/index.html")
  }

  private val userSign = (path("userSign") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, UserSignReq]]) {
      case Right(req) =>
        val now=System.currentTimeMillis()
        dealFutureResult(
          UserDAO.isUserExist(req.userId).map(r=>
            if(r.isEmpty){
              val sha1Pwd=SecureUtil.getSecurePassword(req.pwd,req.userId)
              val (sessionKey,keyCode,signature)=SecureUtil.appSafety
              dealFutureResult(
                UserDAO.addUser(req.userId,now,sessionKey,sha1Pwd,req.mail).map { t =>
                  if (t > 0l){
                    val session = Map(
                      SessionBase.SessionTypeKey -> UserSessionKey.SESSION_TYPE,
                      UserSessionKey.uid -> t.toString,
                      UserSessionKey.userId -> req.userId,
                      UserSessionKey.bbsId -> "guest",
                      UserSessionKey.loginTime -> System.currentTimeMillis().toString,
                      UserSessionKey.keyCode -> keyCode,
                      UserSessionKey.signature -> signature
                    )
                    val headImg=AppSettings.defaultHeadImg
                    val userDetail=UserInfoDetail(t,req.userId,"",headImg)
                    setSession(session){ ctx =>
                      userManager ! UserManager.UserLogin(t)
                      ctx.complete(UserSignRsp(Some(userDetail),0, "Ok"))
                    }
                  }  else complete(ErrorRsp(10001, "注册失败"))
                }
              )
            }else{
              complete(ErrorRsp(10002,"本昵称已被使用"))
            }
          )
        )
      case Left(e)=>
        complete(ErrorRsp(10001,s"解析错误+$e"))
    }
  }

  private val registerSubmit = (path("userSign") & pathEndOrSingleSlash & post) {
    loggingAction {
      _ =>
        entity(as[Either[Error, UserSignReq]]) {
          case Left(error) =>
            log.warn(s"some error: $error")
            complete(ErrorRsp(1002003, "Pattern error."))
          case Right(userInfo) =>
            dealFutureResult(
              UserDAO.isUserExist(userInfo.userId).map {
                userNameOption =>
                  if (userNameOption.isDefined) {
                    complete(ErrorRsp(1002006, "Nickname has existed!"))
                  } else {
                    dealFutureResult(
                      UserDAO.isMailExist(userInfo.mail).map {
                        userEmailOption =>
                          if (userEmailOption.isDefined) {
                            complete(ErrorRsp(1002007, "Email has been used!"))
                          } else {
                            val ls = List(userInfo.userId, userInfo.mail)
                            val rand = SecureUtil.generateSignature(ls, secretKey)
                            val pwdMd5 = SecureUtil.getSecurePassword(userInfo.pwd,userInfo.userId)
                            val registerTime = System.currentTimeMillis()
                            val subject = "账号激活"
                            val text = MailUtil.mailText(userInfo.userId,pwdMd5,userInfo.mail,registerTime,rand)
                            MailUtil.setMailSend(subject, userInfo.mail, text)
                            complete(SuccessRsp())
                          }
                      }
                    )
                  }
              }
            )
        }

    }

  }

  //  用户注册邮箱核查验证
  private val registerCheck = (path("registerCheck") & get) {
    loggingAction {
      _ =>
        parameters('nickName.as[String], 'password.as[String], 'email.as[String], 'time.as[Long], 'rand.as[String]) {
          (userId, password, email, time, rand) =>
            val effectiveTime = 24 * 60 * 60 * 1000
            if ((System.currentTimeMillis() - time) > effectiveTime) {
              log.info(s"The user's verification email has expired")
              complete(ErrorRsp(errCode = 1002008, "The user's verification information has expired"))
            } else {
              val ls = List(userId,email)
              val userRand = SecureUtil.generateSignature(ls, secretKey)
              if (userRand != rand) {
                log.info(s"register failed,user's rand doesn't match")
                complete(ErrorRsp(errCode = 1002009, "User's rand doesn't match"))
              } else {
                dealFutureResult(
                  UserDAO.getUserByNameOrEmail(userId, email).map {
                    userOption =>
                      if (userOption.isDefined) {
                        log.info(s"user has existed!")
                        complete(ErrorRsp(1002006, "user has existed!"))
                      } else {
                        log.info(s"$userId register success email:$email time:$time")
                        val (sessionKey,keyCode,signature)=SecureUtil.appSafety
                        dealFutureResult(
                          UserDAO.addUser(userId,System.currentTimeMillis(),sessionKey,password,email).map { t =>
                            if (t > 0l){
                              val session = Map(
                                SessionBase.SessionTypeKey -> UserSessionKey.SESSION_TYPE,
                                UserSessionKey.uid -> t.toString,
                                UserSessionKey.userId -> userId,
                                UserSessionKey.bbsId -> "guest",
                                UserSessionKey.loginTime -> System.currentTimeMillis().toString,
                                UserSessionKey.keyCode -> keyCode,
                                UserSessionKey.signature -> signature
                              )
                              val headImg=AppSettings.defaultHeadImg
                              setSession(session){ ctx =>
                                userManager ! UserManager.UserLogin(t)
                                ctx.redirect("/timeline/index", StatusCodes.SeeOther)
                              }
                            }  else complete(ErrorRsp(10001, "注册失败"))
                          }
                        )
                      }
                  }
                )
              }
            }
        }
    }
  }


  private val userLogin = (path("userLogin") & post & pathEndOrSingleSlash) {
    entity(as[Either[Error, UserLoginReq]]) {
      case Right(req) =>
        val (sessionKey,keyCode,signature)=SecureUtil.appSafety
        val sha1Pwd=SecureUtil.getSecurePassword(req.pwd,req.userId)
        dealFutureResult(
          UserDAO.userLogin(req.userId,sha1Pwd).map{r=>
            if(r.isEmpty) complete(CommonRsp(10001,"用户不存在或密码错误"))
            else {
              val session = Map(
                SessionBase.SessionTypeKey -> UserSessionKey.SESSION_TYPE,
                UserSessionKey.uid -> r.get.id.toString,
                UserSessionKey.userId -> req.userId,
                UserSessionKey.bbsId -> r.get.bbsId,
                UserSessionKey.loginTime -> System.currentTimeMillis().toString,
                UserSessionKey.keyCode -> keyCode,
                UserSessionKey.signature -> signature
              )
              val headImg=if(r.get.headImg=="") AppSettings.defaultHeadImg else r.get.headImg
              val userDetail=UserInfoDetail(r.get.id,r.get.userId,r.get.bbsId,headImg)
              dealFutureResult(
                UserDAO.updateSession(r.get.id,sessionKey).map{u=>
                  if(u>0){
                    setSession(session){ ctx =>
                      userManager ! UserManager.UserLogin(r.get.id)
                      ctx.complete(UserLoginRsp(Some(userDetail),0,"OK"))
                    }
                  }else{
                    complete(ErrorRsp(10001,"更新用户签名失败"))
                  }
                }
              )
            }
          }
        )
      case Left(e)=>
        complete(ErrorRsp(10001,s"解析错误+$e"))
    }
  }

  private val userLogout=(path("userLogout") & get & pathEndOrSingleSlash){
    UserAction{user=>
      val ses=Set(UserSessionKey.userId,UserSessionKey.uid)
      dealFutureResult {
        val future: Future[String] = userManager ? (UserLogout(user.uid,_))
        future.map {
          case "OK" =>
            removeSession(ses){ctx =>
              log.info(s"user-----${user.userId}----logout")
              ctx.complete(CommonRsp(0,"OK"))
            }
          case x@_ =>
            complete(ErrorRsp(120004, x))
        }.recover {
          case e: Exception =>
            log.info(s"user logout exception.." + e.getMessage)
            complete(ErrorRsp(120003, "网络异常,请稍后再试!"))
        }
      }
    }
  }

  private val getLastTime = (path("getLastTime") & get & pathEndOrSingleSlash) {
    UserAction{u=>
      val future: Future[(Long,Long)] = userManager ? (GetLastTime(u.uid,_))
      dealFutureResult(
        future.map{r=>
          complete(LastTimeRsp(Some(r)))
        }
      )
    }
  }

  private val getFeedFlow = (path("getFeedFlow") & get & pathEndOrSingleSlash) {
    UserAction{ u =>
      parameters(
        'sortType.as[Int],
        'itemTime.as[Long],
        'pageSize.as[Int],
        'up.as[Boolean]
      ) { case (sortType, lastItemTime, pageSize,up) =>
        dealFutureResult {
          val future: Future[Option[List[UserFeedReq]]] = userManager ? (GetUserFeed(u.uid, sortType, lastItemTime, pageSize,up, _))
          future.map {
            case Some(feeds) =>
              val futureTopic:Future[UserFeedRsp] = boardManager ? (GetTopicList(feeds,_))
              dealFutureResult {
                futureTopic.map{topics=>
                  if(topics.normalPost.getOrElse(Nil).size==0) complete(ErrorRsp(120001, "no more date")) else complete(topics)
                }.recover { case e:Exception =>
                  complete(ErrorRsp(120002, "getListError"))
                }
              }
//              val postIds=(feeds.map(r=>(r.board,r.topicId,r.origin)):::feeds.map(r=>(r.board,r.postId,r.origin))).toSet.toSeq
//              dealFutureResult(
//                PostDAO.batchSearchPostDetailByCache(postIds).map{ts=>
//                  val topics=feeds.map{t=>
//                    val topic=ts.filter(_.postId==t.topicId).head
//                    val post=ts.filter(_.postId==t.postId).head
//                    FeedPost(post2TopicInfo(t.origin,topic,post),post.postTime)
//                  }
//                  complete(UserFeedRsp(topics))
//                }
//              )
            case None =>
              complete(ErrorRsp(120007, "No more Data"))
          }.recover {
            case e: Exception =>
              log.info(s"postArt exception.." + e.getMessage)
              complete(ErrorRsp(120003, "网络异常,请稍后再试!"))
          }
        }
      }
    }
  }

  private val getTestFeedFlow = (path("getTestFeedFlow") & get & pathEndOrSingleSlash) {
    parameters(
      'id.as[Long],
      'sortType.as[Int],
      'itemTime.as[Long],
      'pageSize.as[Int],
      'up.as[Boolean]
    ) { case (uid,sortType, lastItemTime, pageSize,up) =>
      dealFutureResult {
        val future: Future[Option[List[UserFeedReq]]] = userManager ? (GetUserFeed(uid, sortType, lastItemTime, pageSize,up, _))
        future.map {
          case Some(feeds) =>
            val futureTopic:Future[UserFeedRsp] = boardManager ? (GetTopicList(feeds,_))
            dealFutureResult {
              futureTopic.map{topics=>
                if(topics.normalPost.getOrElse(Nil).size==0) complete(ErrorRsp(120001, "no more date")) else complete(topics)
              }.recover { case e:Exception =>
                complete(ErrorRsp(120002, "getListError"))
              }
            }
          case None =>
            complete(ErrorRsp(120007, "No more Data"))
        }.recover {
          case e: Exception =>
            log.info(s"postArt exception.." + e.getMessage)
            complete(ErrorRsp(120003, "网络异常,请稍后再试!"))
        }
      }
    }
  }

  private def img2ImgList(img:String)={
    img.split(";").toList
  }
  private def post2TopicInfo(origin:Int,t:SlickTables.rPosts,p:SlickTables.rPosts)={
    TopicInfo(
      origin,t.boardName,t.boardNameCn,p.postId,p.topicId,t.title,img2ImgList(p.imgs),
      img2ImgList(p.hestiaImgs),p.content,AuthorInfo(t.authorId,t.authorName,t.origin),AuthorInfo(p.authorId,p.authorName,p.origin),t.postTime,p.postTime,
      None,isMain = true
    )
  }

  val userRoutes: Route =
    pathPrefix("user") {
      registerSubmit ~ registerCheck ~ userLogin ~ userLogout ~ getFeedFlow ~ getLastTime ~ getTestFeedFlow
    }
}
