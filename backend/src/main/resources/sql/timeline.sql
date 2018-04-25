--用户信息表
CREATE SEQUENCE user_id_seq START WITH 10000;
CREATE TABLE public.user (
  id              BIGINT PRIMARY KEY DEFAULT nextval('user_id_seq'),
  user_id         VARCHAR(255) NOT NULL,  --
  bbs_id          VARCHAR(255) DEFAULT '' NOT NULL,
  sha1_pwd        VARCHAR(255) NOT NULL ,
  create_time     BIGINT NOT NULL,
  session_key     VARCHAR(255) DEFAULT '' NOT NULL,
  last_login_time  BIGINT NOT NULL,  --上次登录时间
  mobile VARCHAR (63)  NOT NULL DEFAULT '',
  email  VARCHAR (150)  NOT NULL DEFAULT '',
  head_img   VARCHAR(500) NOT NULL  DEFAULT '',
  city VARCHAR(50) NOT NULL DEFAULT '',
  gender INT NOT NULL DEFAULT 0, --性别 1:男  2:女
  reply_cnt INT DEFAULT 0 NOT NULL,  --回复次数
  open_cnt INT DEFAULT 0 NOT NULL  --打开次数
);

---- 论坛用户信息表
CREATE SEQUENCE bbs_user_id_seq START WITH 20000;
CREATE TABLE public.bbs_user (
  id              BIGINT PRIMARY KEY DEFAULT nextval('bbs_user_id_seq'),
  bbs_id          VARCHAR (150) DEFAULT ''  NOT NULL,
  bbs_name        VARCHAR (150) DEFAULT ''  NOT NULL,
  head_url        VARCHAR (100) DEFAULT ''  NOT NULL,
  origin          INT NOT NULL ,---0:tianya;1:self
  update_time     BIGINT NOT NULL  DEFAULT 0
);
CREATE INDEX bbs_user_bbs_id_idx ON bbs_user(bbs_id);

--板块信息表
CREATE SEQUENCE board_id_seq START WITH 20000;
CREATE TABLE board(
  id             BIGINT PRIMARY KEY DEFAULT nextval('board_id_seq'),
  board_name     VARCHAR(50)            NOT NULL,
  board_name_cn  VARCHAR(50)            NOT NULL, --显示的名称
  origin         INT NOT NULL, --板块源
  first_spell    varchar(2) default 'Z' :: character varying        not null,
  post_today_num integer default 0                                  not null
);

--记录发帖事件以及帖子真实内容
CREATE SEQUENCE posts_id_seq START WITH 100000 INCREMENT BY 1;
CREATE TABLE posts (
  id            BIGINT PRIMARY KEY DEFAULT nextval('posts_id_seq'),
  origin        INT NOT NULL,--帖子源
  topic_id      BIGINT       NOT NULL,
  post_id       BIGINT       NOT NULL,
  is_main       bool          NOT NULL, --是否为主贴 1是 0否
  title         VARCHAR(127) NOT NULL, --帖子标题
  author_id     VARCHAR(64) DEFAULT '', --作者id
  author_name   VARCHAR(64)  NOT NULL, --作者name--bbsId or unionId
  content  TEXT         NOT NULL, --帖子文本内容
  imgs          TEXT         NOT NULL, --帖子图片url，以;分隔
  hestia_imgs   TEXT         NOT NULL, --帖子图片url，以;分隔
  post_time     BIGINT       NOT NULL, --发帖时间
  board_name    VARCHAR(31)  NOT NULL, --所属版面
  url           VARCHAR(127) NOT NULL, --所在url
  board_name_cn VARCHAR(31)  NOT NULL, --版面中文名称
  quote_id      BIGINT,
  update_time   BIGINT       NOT NULL,  --编辑时间
  state         int NOT NULL DEFAULT, 0--0:正常，1更新帖，2：用户删帖，3：管理员删帖
  pid           bigint default 0                                   not null
);
CREATE UNIQUE Index boardName_postId_index on posts(origin,board_name,post_id);
CREATE Index boardName_postId_id_index on posts(origin,board_name,post_id,id);
CREATE Index boardName_topicId_id_isMain_index on posts(origin,board_name,topic_id,id,is_main);
CREATE Index boardName_topicId_id_isMain_author_id_index on posts(origin,board_name,topic_id,id,is_main,author_id);
CREATE index eventId_index on posts(id);
CREATE index boardName_topicId_index on posts(origin,board_name,topic_id);
CREATE index boardName_isMain_postTime on posts(origin,board_name,is_main,post_time desc);
CREATE index boardName_postTime on posts(origin,board_name,post_time desc);

--userFeed流
CREATE TABLE public.user_feed (
  id         SERIAL8 PRIMARY KEY NOT NULL,
  user_id    BIGINT DEFAULT 0  NOT NULL,
  origin     INT NOT NULL DEFAULT 0,
  boardName  VARCHAR(100) DEFAULT ''  NOT NULL,
  topic_id   BIGINT NOT NULL DEFAULT 0,
  post_id    BIGINT NOT NULL  DEFAULT 0,
  post_time  BIGINT NOT NULL  DEFAULT 0,
  last_reply_time BIGINT NOT NULL  DEFAULT 0,
  author_id    VARCHAR(64) ,
  author_name  VARCHAR(100),
  feed_type    INT DEFAULT 0 NOT NULL
);
CREATE INDEX user_feed_user_id_idx ON user_feed(user_id);

--帖子快照
CREATE TABLE topic_snapshot(
  origin    INTEGER NOT NULL,--板块源
  board_name     VARCHAR(50)            NOT NULL,
  topic_id      BIGINT       NOT NULL,
  last_post_id       BIGINT       NOT NULL,
  last_reply_author VARCHAR(64) NOT NULL ,
  last_reply_time BIGINT NOT NULL ,
  vote_up_num   int NOT NULL DEFAULT 0,
  vote_down_num   int NOT NULL DEFAULT 0,
  reply_author_num int NOT NULL DEFAULT 0,
  reply_post_num INT NOT NULL DEFAULT 0,
  visit_num int NOT NULL DEFAULT 0,
  post_time BIGINT DEFAULT 0 NOT NULL,
  PRIMARY KEY (origin,board_name,topic_id)
);
CREATE index boardName_replyTime_replyIndex ON topic_snapshot(origin,board_name,last_reply_time desc);
CREATE index replyTime_replyIndex ON topic_snapshot(last_reply_time desc);

--用户关注用户
CREATE TABLE public.user_follow_user(
  id SERIAL8 PRIMARY KEY NOT NULL,
  user_id BIGINT NOT NULL DEFAULT 0,
  follow_id VARCHAR(64) DEFAULT '' NOT NULL,
  follow_name VARCHAR(200) NOT NULL DEFAULT '',
  create_time BIGINT NOT NULL,
  origin INT NOT NULL ,
  state INT NOT NULL DEFAULT 0
);
CREATE INDEX user_follow_user_user_id_idx ON user_follow_user(user_id);

--用户关注版面
CREATE TABLE public.user_follow_board
(
  id SERIAL8 PRIMARY KEY NOT NULL,
  user_id BIGINT NOT NULL DEFAULT 0,
  board_name  VARCHAR(255) DEFAULT ''  NOT NULL,
  board_title VARCHAR(255) DEFAULT ''  NOT NULL,
  create_time BIGINT NOT NULL,
  state INT NOT NULL DEFAULT 0,
  origin INT NOT NULL DEFAULT 0
);
CREATE INDEX user_follow_board_user_id_idx ON user_follow_board(user_id);


--用户关注话题
CREATE TABLE public.user_follow_topic
(
  id SERIAL8 PRIMARY KEY NOT NULL,
  user_id BIGINT NOT NULL DEFAULT 0,
  board_name  VARCHAR(255) DEFAULT ''  NOT NULL,
  topic_id    BIGINT DEFAULT 0  NOT NULL,
  create_time BIGINT NOT NULL,
  state INT NOT NULL DEFAULT 0,
  origin INT NOT NULL DEFAULT 0
);
CREATE INDEX user_follow_topic_user_id_idx ON user_follow_topic(user_id);


CREATE TABLE syn_data(
  id SERIAL8 PRIMARY KEY NOT NULL,
  data  BIGINT DEFAULT 0  NOT NULL,
  note VARCHAR(100) NOT NULL DEFAULT ''
);