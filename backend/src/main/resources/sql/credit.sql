-- timeline系统数据库
-- 商户信息索引已注释
-- 加减分表内通知失败元素作用未确定


CREATE SEQUENCE record_id_seq START WITH 100001 INCREMENT BY 1; --用户行为id

-- 商户用户加分记录
CREATE TABLE timeline_add_records (
  id                BIGINT PRIMARY KEY DEFAULT nextval('record_id_seq'),
  app_id            INTEGER  NOT NULL ,--  商户
  user_id           VARCHAR(255) NOT NULL ,
  timeline            INTEGER  NOT NULL ,
  data_type         INTEGER  NOT NULL ,--1：阅读；2：发帖；3：回复
  create_time       BIGINT        NOT NULL
);
CREATE INDEX timeline_add_records_user_index
  ON timeline_add_records(user_id);
CREATE INDEX timeline_add_records_app_index
  ON timeline_add_records(app_id);

-- 用户消耗积分记录
CREATE TABLE timeline_red_records (
  id                BIGINT PRIMARY KEY DEFAULT nextval('record_id_seq'),
  user_id           VARCHAR(255) NOT NULL ,
  app_id            INTEGER  NOT NULL ,--  商户
  timeline            INTEGER  NOT NULL ,
  data_type         INTEGER  NOT NULL ,--礼物类型
  create_time       BIGINT        NOT NULL
  --  notify_status     INTEGER, --0：成功通知，-1：通知失败
  --  notify_time       BIGINT
);
CREATE INDEX timeline_red_records_user_index
  ON timeline_add_records(user_id);
CREATE INDEX timeline_red_records_app_index
  ON timeline_add_records(app_id);

-- 用户交易记录
CREATE TABLE timeline_trade_records (
  id                BIGINT PRIMARY KEY DEFAULT nextval('record_id_seq'),
  app_id            INTEGER  NOT NULL ,--  商户
  user_id           VARCHAR(255) NOT NULL ,
  obj_user          VARCHAR(255) NOT NULL ,
  total_timeline      INTEGER  NOT NULL ,
  fee               INTEGER  NOT NULL ,
  obj_timeline        INTEGER  NOT NULL ,
  data_type         INTEGER  NOT NULL ,
  create_time       BIGINT   NOT NULL
);
CREATE INDEX timeline_trade_user_index
  ON timeline_trade_records(user_id);
CREATE INDEX timeline_trade_app_index
  ON timeline_trade_records(app_id);
CREATE INDEX timeline_trade_obj_index
  ON timeline_trade_records(obj_user);
-- 用户宠物

-- 用户积分列表
CREATE SEQUENCE user_snapshot_id_seq START WITH 100001 INCREMENT BY 1;
CREATE TABLE user_snapshot (
  "id"           BIGINT PRIMARY KEY  DEFAULT nextval('user_snapshot_id_seq' :: REGCLASS) NOT NULL,
  user_id        VARCHAR(255)                                                            NOT NULL,
  app_id         INTEGER                                                                 NOT NULL,
  timeline         INTEGER                                                                 NOT NULL,
  last_record_id BIGINT                                                                  NOT NULL,
  timestamp      BIGINT                                                                  NOT NULL
  --   商户编号
);
ALTER SEQUENCE user_snapshot_id_seq OWNED BY user_snapshot.id;
CREATE UNIQUE INDEX user_snapshot_open_id_index
  ON user_snapshot (user_id);
CREATE INDEX user_snapshot_app_index
  ON user_snapshot (app_id);
-- 商户列表
CREATE SEQUENCE app_id_seq START WITH 10001 INCREMENT BY 1;
CREATE TABLE app_snapshot (
  "id"           INTEGER PRIMARY KEY  DEFAULT nextval('app_id_seq' :: REGCLASS) NOT NULL,
  app_name        VARCHAR(255)                                                            NOT NULL,
  timeline          BIGINT      NOT NULL,
  create_time      BIGINT                                                                  NOT NULL
);
CREATE UNIQUE INDEX app_snapshot_open_id_index
  ON app_snapshot (app_name);

-- 管理员列表
CREATE TABLE admin (
  adminName    VARCHAR(255) PRIMARY KEY NOT NULL,
  passwordMD5  VARCHAR(255)             NOT NULL,
  registerTime BIGINT NOT NULL
);
