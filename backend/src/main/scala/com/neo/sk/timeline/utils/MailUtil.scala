package com.neo.sk.timeline.utils

import java.io.File
import java.util.Properties
import javax.mail.MessagingException
import javax.mail.internet.MimeMessage
import javax.security.auth.Subject

import com.neo.sk.timeline.common.AppSettings
import com.neo.sk.timeline.common.AppSettings.{mailFrom, mailHost, mailPassword, mailPort, mailUsername}
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.{JavaMailSenderImpl, MimeMessageHelper}


trait Email {
  def subject: String

  def from: String

  def to: String

  def text: String
}

trait EMailProduceService {
  def email: Email
}

trait EMailSendService {
  def send(email: Email)
}

trait ServerConfig {
  def host: String

  def username: String

  def password: String

  def port: Int

  def defaultEncoding: String
}

trait CommonEmailConfig {
  def from: String
}

trait Config extends ServerConfig with CommonEmailConfig {
  def properties = {
    val p = new Properties
    p.setProperty("mail.smtp.auth", true.toString)
    p.setProperty("mail.smtp.starttls.enable", true.toString)
    p.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    p
  }

  override def toString: String = {
    val sb = new StringBuilder
    sb.append(getClass.getName).append("{")
    sb append "\nhost: " append host
    sb append "\nusername: " append username
    sb append "\npassword: " append password
    sb append "\nport: " append port
    sb append "\ndefaultEncoding: " append defaultEncoding
    sb append "\nfrom: " append from
    sb append "\n}"
    sb.toString
  }
}

private class MyEMailProduceService(subject1: String, to1: String, text1: String)(implicit val config: Config) extends EMailProduceService {
  def email: Email = {
    try {
      new Email {
        def subject = subject1

        def from = config.from

        def to = to1

        def text = text1

        //        def files = config.files.head :: Nil
      }
    } catch {
      case e: Exception => throw e
    }
  }
}

private object YamlConfigImpl extends Config {
  def host = mailHost

  def username = mailUsername

  def password = mailPassword

  def port = mailPort

  def defaultEncoding = AppSettings.defaultEncoding

  def from = mailFrom
}

trait SpringEMailSendService extends EMailSendService {
  def mailSender: JavaMailSenderImpl

  def send(email: Email) = {
    mailSender.send(createMimeMessage(email))
  }

  def createMimeMessage(email: Email): MimeMessage = {
    val message = mailSender.createMimeMessage
    //    val attachmented = !email.files.isEmpty
    // 附件 需设置第二个参数为true
    val mimeMessageHelper = new MimeMessageHelper(message, "UTF-8")
    try {
      mimeMessageHelper.setFrom(email.from)
      mimeMessageHelper.setSubject(email.subject)
      mimeMessageHelper.setTo(email.to)
      // 第二个参数为true,支持HTML
      mimeMessageHelper.setText(email.text, true)

    } catch {
      case e: MessagingException =>
        e.printStackTrace()
    }

    message
  }
}

private class MySpringEMailSendService(implicit val config: Config) extends SpringEMailSendService {
  lazy val mailSender = {
    val sender = new JavaMailSenderImpl
    sender.setHost(config.host)
    sender.setUsername(config.username)
    sender.setPassword(config.password)
    sender.setPort(config.port)
    sender.setDefaultEncoding(config.defaultEncoding)
    sender.setJavaMailProperties(config.properties)
    sender
  }
}

private class MyEmailClient(mailProduceService: EMailProduceService)(implicit val mailSendService: EMailSendService) {
  def run() = try {
    mailSendService.send(mailProduceService.email)
  } catch {
    case e: Exception => e.printStackTrace()
  }
}

object MailUtil {

  private val log = LoggerFactory.getLogger(this.getClass)

  implicit private lazy val config: Config = YamlConfigImpl

  implicit private lazy val eMailSendService: EMailSendService = new MySpringEMailSendService

  def setMailSend(subject: String, to: String, text: String) = {
    val eMailProduceService: EMailProduceService = new MyEMailProduceService(subject, to, text)
    log.info(s"正在发送邮件...to..$to")
    new MyEmailClient(eMailProduceService).run()
    log.info(s"发送完毕!")
  }

}


object main {

  def main(args: Array[String]): Unit = {
    val text="<tbody><tr>\n  " +
      "        \t<td>\n       " +
      "          \t<div style=\"padding:50px 30px 0;\">\n          " +
      "              <strong style=\"font-family:'Microsoft Yahei';font-size:14px;\">亲爱的 <a href=\"mailto:864916251@qq.com\" target=\"_blank\">864916251@qq<wbr>.com</a></strong>\n      " +
      "                 <p style=\"color:#333;padding-top:20px;\">点击以下链接即可马上激活你的微博帐号：</p>\n         " +
      "                 <p><a href=\"http://passport.sina.cn/bindname/activate?entry=wapsso&amp;email=864916251@qq.com&amp;check=&amp;rand=419f80657c7f78f090b56290d1ae2f5a\" style=\"word-break:break-all;word-wrap:break-word\" target=\"_blank\">http://passport.sina.cn/bindname/activate?entry=wapsso&amp;email=864916251@qq.com&amp;check=&amp;rand=419f80657c7f78f090b56290d1ae2f5a</a></p>\n      " +
      "                 <p style=\"color:#333;padding-top:20px;\">本链接</p>\n     " +
      "                 </div>\n   " +
      "           </td>\n   " +
      "           </tr>\n    <tr>\n  " +
      "          \t<td>\n    " +
      "              <div style=\"padding:35px 30px 0;\">\n       " +
      "                  <p>小提示：</p>\n           " +
      "                  <p>1.如果你错误的接收到此邮件，请不要慌张，也无需执行任何操作来取消帐户！该帐户将不会启动。</p>\n   " +
      "                  <p>2.这只是一封系统自动发送的邮件，请不要直接回复。</p>\n       " +
      "               </div>\n  " +
      "             </td>\n " +
      "             </tr>\n    <tr>\n    \t<td>\n      " +
      "               <div style=\"padding:35px 30px 0;\">\n        " +
      "                   <p>微博</p>\n           " +
      "                   <p>手机访问：<a href=\"http://m.weibo.cn\" target=\"_blank\">http://m.weibo.cn</a></p><p>\n    </p>  " +
      "                   <p>电脑访问：<a href=\"http://weibo.com\" target=\"_blank\">http://weibo.com</a></p>\n    " +
      "               </div>\n    " +
      "              </td>\n   " +
      "        </tr>\n</tbody>"
    MailUtil.setMailSend("test", "1101953696@qq.com", text)
  }
}