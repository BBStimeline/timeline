package com.neo.sk.timeline.utils

import scala.xml.PCData

/**
 * User: Liboren's.
 * Date: 2015/12/4.
 * Time: 15:50.
 */
object XmlUtil {

  /**统一下单接口请求参数**/
  def buildUnifiedOrderResponse(param:UnifiedOrderParam) = {
    <xml>
      <appid>{new PCData(param.appid)}</appid>
      <body>{new PCData(param.body)}</body>
      <device_info>{new PCData(param.device_info)}</device_info>
      <fee_type>{new PCData(param.fee_type)}</fee_type>
      <mch_id>{new PCData(param.mch_id)}</mch_id>
      <nonce_str>{new PCData(param.nonce_str)}</nonce_str>
      <notify_url>{new PCData(param.notify_url)}</notify_url>
      {
        if(param.openid.isDefined){
          <openid>{new PCData(param.openid.get)}</openid>
        }
      }
      <out_trade_no>{new PCData(param.out_trade_no)}</out_trade_no>
      {
        if(param.product_id.isDefined){
          <product_id>{new PCData(param.product_id.get)}</product_id>
        }
      }
      <spbill_create_ip>{new PCData(param.spbill_create_ip)}</spbill_create_ip>
      <time_expire>{new PCData(param.time_expire)}</time_expire>
      <time_start>{new PCData(param.time_start)}</time_start>
      <total_fee>{param.total_fee}</total_fee>
      <trade_type>{new PCData(param.trade_type)}</trade_type>
      <sign>{new PCData(param.sign)}</sign>
    </xml>
  }

  /**查询订单接口请求参数**/
  def buildSearchOrderResponse(param:SearchOrderParam) = {
    <xml>
      <appid>{new PCData(param.appid)}</appid>
      <mch_id>{new PCData(param.mch_id)}</mch_id>
			<!-- <transaction_id>{new PCData(param.transaction_id)}</transaction_id> -->
			<out_trade_no>{new PCData(param.out_trade_no)}</out_trade_no>
      <nonce_str>{new PCData(param.nonce_str)}</nonce_str>
      <sign>{new PCData(param.sign)}</sign>
    </xml>
  }

  /**支付结果返回参数**/
  def buildInformResponse(code:String,msg:String) = {
    <xml>
      <return_code>{new PCData(code)}</return_code>
      <return_msg>{new PCData(msg)}</return_msg>
    </xml>
  }

  /**退款接口参数**/
  def buildRefundResponse(param:RefundParam) = {
    <xml>
      <appid>{new PCData(param.appid)}</appid>
      <mch_id>{new PCData(param.mch_id)}</mch_id>
      <nonce_str>{new PCData(param.nonce_str)}</nonce_str>
      <op_user_id>{new PCData(param.op_user_id)}</op_user_id>
      <transaction_id>{new PCData(param.transactionId)}</transaction_id>
      <out_refund_no>{new PCData(param.out_refund_no)}</out_refund_no>
      <out_trade_no>{new PCData(param.out_trade_no)}</out_trade_no>
      <refund_fee>{new PCData(param.refund_fee)}</refund_fee>
      <total_fee>{new PCData(param.total_fee)}</total_fee>
      <sign>{new PCData(param.sign)}</sign>
    </xml>
  }

  def buildRefundQueryResponse(param:RefundQueryParam) = {
    <xml>
      <appid>{new PCData(param.appid)}</appid>
      <mch_id>{new PCData(param.mch_id)}</mch_id>
      <nonce_str>{new PCData(param.nonce_str)}</nonce_str>
      <out_refund_no>{new PCData(param.out_refund_no)}</out_refund_no>
      <sign>{new PCData(param.sign)}</sign>
    </xml>
  }
/*******订单关闭接口请求参数*********/
  def buildCloseOrderResponse(param:CloseOrderParam) = {
    <xml>
      <appid>{new PCData(param.appid)}</appid>
      <mch_id>{new PCData(param.mch_id)}</mch_id>
      <nonce_str>{new PCData(param.nonce_str)}</nonce_str>
      <out_trade_no>{new PCData(param.out_trade_no)}</out_trade_no>
      <sign>{new PCData(param.sign)}</sign>
    </xml>
  }

  def buildDownloadBillReponse(param:DownloadBillParam) = {
    <xml>
      <appid>{new PCData(param.appid)}</appid>
      <mch_id>{new PCData(param.mch_id)}</mch_id>
      <bill_date>{new PCData(param.bill_date)}</bill_date>
      <bill_type>{new PCData(param.bill_type)}</bill_type>
      <mch_id>{new PCData(param.mch_id)}</mch_id>
      <nonce_str>{new PCData(param.nonce_str)}</nonce_str>
      <sign>{new PCData(param.sign)}</sign>
    </xml>
  }

  def buildRedPaperReq(param: RedPaperParam) = {
    <xml>
      <sign>{new PCData(param.sign)}</sign>
      <mch_billno>{new PCData(param.mchBillNo)}</mch_billno>
      <mch_id>{new PCData(param.mchId)}</mch_id>
      <wxappid>{new PCData(param.wxAppId)}</wxappid>
      <send_name>{new PCData(param.sendName)}</send_name>
      <re_openid>{new PCData(param.reOpenId)}</re_openid>
      <total_amount>{new PCData(param.totalAmount)}</total_amount>
      <total_num>{new PCData(param.totalNum)}</total_num>
      <wishing>{new PCData(param.wishing)}</wishing>
      <client_ip>{new PCData(param.clientIp)}</client_ip>
      <act_name>{new PCData(param.actName)}</act_name>
      <remark>{new PCData(param.remark)}</remark>
      <scene_id>{new PCData(param.sceneId)}</scene_id>
      <nonce_str>{new PCData(param.nonceStr)}</nonce_str>
    </xml>
  }

  def buildRedPaperQuery(param: RedPaperQuery) = {
    <xml>
      <sign>{new PCData(param.sign)}</sign>
      <mch_billno>{new PCData(param.mchBillNo)}</mch_billno>
      <mch_id>{new PCData(param.mchId)}</mch_id>
      <appid>{new PCData(param.appId)}</appid>
      <bill_type>{new PCData(param.billType)}</bill_type>
      <nonce_str>{new PCData(param.nonceStr)}</nonce_str>
    </xml>
  }
	
	/**企业付款接口请求参数**/
	def buildMchTransfersResponse(param: mchTransfersParam) = {
		<xml>
			<mch_appid>{new PCData(param.mch_appid)}</mch_appid>
			<mchid>{new PCData(param.mchid)}</mchid>
      <device_info>{new PCData(param.device_info)}</device_info>
			<nonce_str>{new PCData(param.nonce_str)}</nonce_str>
			<partner_trade_no>{new PCData(param.partner_trade_no)}</partner_trade_no>
			<openid>{new PCData(param.openid)}</openid>
			<check_name>{new PCData(param.check_name)}</check_name>
			<amount>{new PCData(param.amount)}</amount>
			<desc>{new PCData(param.desc)}</desc>
			<spbill_create_ip>{new PCData(param.spbill_create_ip)}</spbill_create_ip>
			<sign>{new PCData(param.sign)}</sign>
		</xml>
	}
	
	/**查询企业付款接口请求参数**/
	def buildGetTransferInfoResponse(param: getTransferInfoParam) = {
		<xml>
			<sign>{new PCData(param.sign)}</sign>
			<partner_trade_no>{new PCData(param.partner_trade_no)}</partner_trade_no>
			<mch_id>{new PCData(param.mch_id)}</mch_id>
			<appid>{new PCData(param.appid)}</appid>
			<nonce_str>{new PCData(param.nonce_str)}</nonce_str>
		</xml>
	}
}

case class RedPaperQuery(
  sign: String,
  mchBillNo: String,
  mchId: String,
  appId: String,
  billType: String,
  nonceStr: String
)

case class RedPaperParam(
  sign: String,
  mchBillNo: String,
  mchId: String,
  wxAppId: String,
  sendName: String,
  reOpenId: String,
  totalAmount: String,
  totalNum: String,
  wishing: String,
  clientIp: String,
  actName: String,
  remark: String,
  sceneId: String,
  nonceStr: String
)

case class DownloadBillParam(
                            appid:String,
                            mch_id:String,
                            bill_date:String,
                            bill_type:String,
                            nonce_str:String,
                            sign:String
                              )

case class CloseOrderParam(
                          appid:String,
                          mch_id:String,
                          nonce_str:String,
                          out_trade_no:String,
                          sign:String
                            )

case class RefundQueryParam(
                           appid:String,
                           mch_id:String,
                           nonce_str:String,
                           out_refund_no:String,
                           sign:String
                             )

case class RefundParam(
                      appid:String,
                      mch_id:String,
                      nonce_str:String,
                      op_user_id:String,
                      transactionId:String,
                      out_refund_no:String,
                      out_trade_no:String,
                      refund_fee:String,
                      total_fee:String,
                      sign:String
                        )

case class UnifiedOrderParam(
                  appid:String,
                  mch_id:String,
                  device_info:String,
                  nonce_str:String,
                  sign:String,
                  body:String,
                  out_trade_no:String,
                  fee_type:String,
                  total_fee:String,
                  spbill_create_ip:String,
                  time_start:String,
                  time_expire:String,
//                  goods_tag:String,
                  notify_url:String,
                  trade_type:String,
                  product_id:Option[String],
//                  limit_pay:String,
                  openid:Option[String]
                  )

case class SearchOrderParam(
                              appid:String,
                              mch_id:String,
//                              transaction_id:String,
															out_trade_no:String,
															nonce_str:String,
                              sign:String
                            )

case class mchTransfersParam(
															mch_appid: String,
															mchid: String,
                              device_info: String,
															nonce_str: String,
															partner_trade_no: String,
															openid: String,
															check_name: String,
															amount: String,
															desc: String,
															spbill_create_ip: String,
															sign: String
														)

case class getTransferInfoParam(
																 sign: String,
																 partner_trade_no: String,
																 mch_id: String,
																 appid: String,
																 nonce_str: String
															 )