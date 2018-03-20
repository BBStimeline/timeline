/*
由于系统还未上线，对之前的数据进行清理，然后重建数据表。
鉴于之前sql比较混乱，整理一下。
*/
CREATE SEQUENCE record_id_seq START WITH 602001 INCREMENT BY 1;

CREATE TABLE create_red_envelope_records (
  id                BIGINT PRIMARY KEY DEFAULT nextval('record_id_seq'),
  open_id           VARCHAR(255)  NOT NULL,
  red_envelope_id   VARCHAR(255)  NOT NULL,
  total_fee         INTEGER       NOT NULL, --单位：分
  use_balance       INTEGER       NOT NULL,
  need_pay          INTEGER       NOT NULL,
  create_time       BIGINT        NOT NULL,
  expire_time       BIGINT        NOT NULL,
  notify_url        VARCHAR(1023) NOT NULL,
  service_fee       INTEGER       NOT NULL,
  wx_order_id       BIGINT,
  wx_preorder_id    VARCHAR(255),
  create_order_time BIGINT,
  final_status      INTEGER, --0：创建成功，-1：创建失败
  final_status_time BIGINT,
  notify_status     INTEGER, --0：成功通知，-1：通知失败
  notify_time       BIGINT,
  is_used_up        INTEGER
);
CREATE UNIQUE INDEX create_red_envelope_records_red_envelope_id_index
  ON create_red_envelope_records (red_envelope_id);
CREATE INDEX create_red_envelope_records_open_id_index
  ON create_red_envelope_records (open_id);

CREATE TABLE draw_red_envelope_records (
  id                BIGINT PRIMARY KEY DEFAULT nextval('record_id_seq'),
  open_id           VARCHAR(255) NOT NULL,
  red_envelope_id   VARCHAR(255) NOT NULL,
  fee               INTEGER      NOT NULL, --单位：分
  create_time       BIGINT       NOT NULL,
  type              INTEGER      NOT NULL, --直接打款：0，存到余额：1，未领退还到余额：2
  final_status      INTEGER,
  final_status_time BIGINT
);
CREATE INDEX draw_red_envelope_records_red_envelope_id_index
  ON draw_red_envelope_records (red_envelope_id);
CREATE INDEX draw_red_envelope_records_open_id_index
  ON draw_red_envelope_records (open_id);

CREATE TABLE withdraw_records (
  id                BIGINT PRIMARY KEY DEFAULT nextval('record_id_seq'),
  in_trade_no       VARCHAR(255)            NOT NULL, --drogon订单号
  open_id           VARCHAR(255)            NOT NULL,
  total_fee         INTEGER                 NOT NULL,
  real_withdraw_fee INTEGER                 NOT NULL, --真正提取金额
  service_fee       INTEGER                 NOT NULL, --手续费
  create_time       BIGINT                  NOT NULL,
  notify_url        VARCHAR(1023)           NOT NULL,
  type              INTEGER DEFAULT 1       NOT NULL, --1：自动提现 or 2：手动提现
  des               VARCHAR(255) DEFAULT '' NOT NULL,
  wx_mch_order_id   BIGINT,
  create_order_time BIGINT,
  final_status      INTEGER,
  final_status_time BIGINT,
  notify_status     INTEGER, --0：成功通知，-1：通知失败
  notify_time       BIGINT
);
CREATE INDEX withdraw_records_open_id_index
  ON withdraw_records (open_id);
CREATE INDEX withdraw_records_in_trade_no_index
  ON withdraw_records (in_trade_no);

CREATE SEQUENCE wx_order_id_seq START WITH 410000 INCREMENT BY 1;
CREATE TABLE "public"."wx_order" (
  "id"              BIGINT DEFAULT nextval('wx_order_id_seq' :: REGCLASS) NOT NULL, --小程序付款订单号
  CONSTRAINT "order_pkey" PRIMARY KEY ("id"),
  in_trade_no       VARCHAR(255)                                          NOT NULL,
  out_trade_no      VARCHAR(255)                                          NOT NULL UNIQUE, --商户系统内部的订单号
  create_time       BIGINT                                                NOT NULL, --交易起始时间(订单生成时间)
  expire_time       BIGINT                                                NOT NULL, --交易结束时间(订单失效时间)
  fee               INTEGER                                               NOT NULL, --订单总金额
  status            INTEGER, --订单状态，0：创建成功，-1：创建失败
  update_time       BIGINT, --订单状态更新时间
  prepay_id         VARCHAR(255), --微信生成的预支付回话标识
  final_status      INTEGER,
  final_status_time BIGINT,
  open_id           VARCHAR(255)                                          NOT NULL
);
ALTER SEQUENCE wx_order_id_seq OWNED BY wx_order.id;
CREATE UNIQUE INDEX wx_order_out_trade_no_index
  ON wx_order (out_trade_no);
CREATE UNIQUE INDEX wx_order_in_trade_no_index
  ON wx_order (in_trade_no);

CREATE SEQUENCE wx_mch_order_id_seq START WITH 12000000 INCREMENT BY 1;
CREATE TABLE "public"."wx_mch_order" (
  "id"              BIGINT DEFAULT nextval('wx_mch_order_id_seq' :: REGCLASS) NOT NULL, --企业付款订单号
  CONSTRAINT "mch_order_pkey" PRIMARY KEY ("id"),
  in_trade_no       VARCHAR(255)                                              NOT NULL,
  partner_trade_no  VARCHAR(255)                                              NOT NULL UNIQUE, --商户订单号
  open_id           VARCHAR(255)                                              NOT NULL, --用户openid
  fee               INTEGER                                                   NOT NULL, --企业付款金额
  create_stamp      BIGINT                                                    NOT NULL, --交易起始时间(订单生成时间)
  status            INTEGER, --订单状态，0：创建成功，-1：创建失败, 2: 因为账户余额不足而未完成的订单
  payment_no        VARCHAR(255), --企业付款成功，返回的微信订单号
  payment_time      VARCHAR(255), --企业付款成功时间
  final_status      INTEGER,
  final_status_time BIGINT
);
ALTER SEQUENCE wx_mch_order_id_seq OWNED BY wx_mch_order.id;
CREATE INDEX wx_mch_order_in_trade_no_index
  ON wx_mch_order (in_trade_no);

CREATE SEQUENCE user_snapshot_id_seq START WITH 12000000 INCREMENT BY 1;
CREATE TABLE user_snapshot (
  "id"           BIGINT PRIMARY KEY  DEFAULT nextval('user_snapshot_id_seq' :: REGCLASS) NOT NULL,
  open_id        VARCHAR(255)                                                            NOT NULL,
  balance        INTEGER                                                                 NOT NULL,
  last_record_id BIGINT                                                                  NOT NULL,
  timestamp      BIGINT                                                                  NOT NULL
);
ALTER SEQUENCE user_snapshot_id_seq OWNED BY user_snapshot.id;
CREATE UNIQUE INDEX user_snapshot_open_id_index
  ON user_snapshot (open_id);

CREATE TABLE admin (
  adminName    VARCHAR(255) PRIMARY KEY NOT NULL,
  passwordMD5  VARCHAR(255)             NOT NULL,
  registerTime VARCHAR(255)             NOT NULL
);
INSERT INTO admin (
  adminname,
  passwordmd5,
  registertime
) VALUES ('timelineadmin', '19b6ebb4ec50eaaea9d0efc880193090f49f38ab', '2017-11-2 15:58');

/*update 20171128 用户充值记录表*/
CREATE TABLE recharge_records (
  id                BIGINT PRIMARY KEY DEFAULT nextval('record_id_seq'),
  trade_no          VARCHAR(255) NOT NULL,
  open_id           VARCHAR(255) NOT NULL,
  balance           INTEGER      NOT NULL, --需要充值的金额
  create_time       BIGINT       NOT NULL,
  final_status      INTEGER, --0：创建成功，-1：创建失败
  final_status_time BIGINT
);
CREATE UNIQUE INDEX recharge_records_trade_no_index
  ON recharge_records (trade_no);
CREATE INDEX recharge_records_open_id_index
  ON recharge_records (open_id);