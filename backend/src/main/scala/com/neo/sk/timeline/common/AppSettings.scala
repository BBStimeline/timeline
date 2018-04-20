package com.neo.sk.timeline.common

import java.util.concurrent.TimeUnit

import com.neo.sk.timeline.utils.SessionSupport.SessionConfig
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

/**
  * User: Taoz
  * Date: 9/4/2015
  * Time: 4:29 PM
  */
object AppSettings {

  private implicit class RichConfig(config: Config) {
    val noneValue = "none"

    def getOptionalString(path: String): Option[String] =
      if (config.getAnyRef(path) == noneValue) None
      else Some(config.getString(path))

    def getOptionalLong(path: String): Option[Long] =
      if (config.getAnyRef(path) == noneValue) None
      else Some(config.getLong(path))

    def getOptionalDurationSeconds(path: String): Option[Long] =
      if (config.getAnyRef(path) == noneValue) None
      else Some(config.getDuration(path, TimeUnit.SECONDS))
  }


  val log = LoggerFactory.getLogger(this.getClass)
  val config = ConfigFactory.parseResources("product.conf").withFallback(ConfigFactory.load())

  val appConfig = config.getConfig("app")
  val dependence = config.getConfig("dependence")

  val weixinConfig = appConfig.getConfig("weixin")
  val weixinMchId = weixinConfig.getString("mchId")
  val weixinAppId = weixinConfig.getString("appId")
  val weixinKey = weixinConfig.getString("key")
  val weixinNotifyUrl = weixinConfig.getString("notifyUrl")
  val weixinIp = weixinConfig.getString("ip")
  val weixinSubject = weixinConfig.getString("subject")
  val weixinSpbillCreateIp = weixinConfig.getString("ip")
  val weixinDesc = weixinConfig.getString("desc")
  val withdrawLimit = weixinConfig.getInt("withdrawLimit")



  val httpInterface = appConfig.getString("http.interface")
  val httpPort = appConfig.getInt("http.port")


  val slickConfig = config.getConfig("slick.db")
  val slickUrl = slickConfig.getString("url")
  val slickUser = slickConfig.getString("user")
  val slickPassword = slickConfig.getString("password")
  val slickMaximumPoolSize = slickConfig.getInt("maximumPoolSize")
  val slickConnectTimeout = slickConfig.getInt("connectTimeout")
  val slickIdleTimeout = slickConfig.getInt("idleTimeout")
  val slickMaxLifetime = slickConfig.getInt("maxLifetime")

  val dependenceConfig = config.getConfig("dependence")
  val mchOrderCheckInterval = dependenceConfig.getInt("mchOrderCheckInterval")
  val userOrderRetryInterval = dependenceConfig.getInt("userOrderRetryInterval")
  val mchOrderRetryInterval = dependenceConfig.getInt("mchOrderRetryInterval")
  val userRechargeInterval = dependenceConfig.getLong("userRechargeInterval")
  val rechargeFeeMin = dependenceConfig.getInt("rechargeFeeMin")
  val rechargeFeeMax = dependenceConfig.getInt("rechargeFeeMax")
  val isTest = dependenceConfig.getBoolean("isTest")
  val authCheck = dependenceConfig.getBoolean("authCheck")


  val smallSpiderConfig = appConfig.getConfig("smallSpider")
  val smallSpiderProtocol = smallSpiderConfig.getString("protocol")
  val smallSpiderDomain = smallSpiderConfig.getString("domain")
  val smallSpiderAppId = smallSpiderConfig.getString("appId")
  val smallSpiderSecureKey = smallSpiderConfig.getString("secureKey")
  val isStart = smallSpiderConfig.getBoolean("isStart")
  val synCount=smallSpiderConfig.getInt("synDataCount")
  val synTime=smallSpiderConfig.getInt("synDataTime")
  val synOutTime=smallSpiderConfig.getInt("synOutTime")

  val sessionConfig = {
    val sConf = config.getConfig("session")
    SessionConfig(
      cookieName = sConf.getString("cookie.name"),
      serverSecret = sConf.getString("serverSecret"),
      domain = sConf.getOptionalString("cookie.domain"),
      path = sConf.getOptionalString("cookie.path"),
      secure = sConf.getBoolean("cookie.secure"),
      httpOnly = sConf.getBoolean("cookie.httpOnly"),
      maxAge = sConf.getOptionalDurationSeconds("cookie.maxAge"),
      sessionEncryptData = sConf.getBoolean("encryptData")
    )


  }

  val appSecureMap = {
    import collection.JavaConverters._
    val appIds = appConfig.getStringList("client.appIds").asScala
    val secureKeys = appConfig.getStringList("client.secureKeys").asScala
    require(appIds.length == secureKeys.length, "appIdList.length and secureKeys.length not equel.")
    appIds.zip(secureKeys).toMap
  }

  /**程序基本配置*/
  val timelineConfig = appConfig.getConfig("timeline")
  val defaultHeadImg=timelineConfig.getString("defaultHeadImg")




}
