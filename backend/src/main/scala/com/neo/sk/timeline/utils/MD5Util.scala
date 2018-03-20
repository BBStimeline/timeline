package com.neo.sk.timeline.utils

import org.apache.commons.codec.digest.DigestUtils
import org.slf4j.LoggerFactory

/**
  * Created by TangYaruo on 2017/10/18.
  */
object MD5Util {

  private val log = LoggerFactory.getLogger(this.getClass)

  def createSignature(item:List[(String,String)],key:String) = {
    val stringSignTemp = item.sortBy(_._1).map(m => m._1+"="+m._2).mkString("&")
    //    log.info("[createSignature]"+stringSignTemp+"&key="+key)
    DigestUtils.md5Hex(stringSignTemp+"&key="+key).toUpperCase // 最后加上商户秘钥
  }

  def createSignatureWithISO(item:List[(String,String)],key:String) = {
    val stringSignTemp = item.sortBy(_._1).map(m => m._1+"="+m._2).mkString("&")
    //    log.info("[createSignature]"+stringSignTemp+"&key="+key)
    DigestUtils.md5Hex((stringSignTemp+"&key="+key).getBytes("utf8")).toUpperCase // 最后加上商户秘钥
  }

  //(stringSignTemp+"&key="+key).getBytes("ISO-8859-1")
  
  def checkSignature(sign: String, item: List[(String,String)], key: String) = {
    val stringSignTemp = item.filter(_._1 != "sign").sortBy(_._1).map(m => m._1+"="+m._2).mkString("&")
    sign.equals(DigestUtils.md5Hex(stringSignTemp+"&key="+key).toUpperCase) // 最后加上商户秘钥
  }

  //(stringSignTemp+"&key="+key).getBytes("ISO-8859-1")


}
