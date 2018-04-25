package com.neo.sk.timeline.models

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object SlickTables extends {
  val profile = slick.jdbc.PostgresProfile
} with SlickTables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait SlickTables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(tBbsUser.schema, tBoard.schema, tPosts.schema, tSynData.schema, tTopicSnapshot.schema, tUser.schema, tUserFeed.schema, tUserFollowBoard.schema, tUserFollowTopic.schema, tUserFollowUser.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tBbsUser
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param bbsId Database column bbs_id SqlType(varchar), Length(150,true), Default()
   *  @param bbsName Database column bbs_name SqlType(varchar), Length(150,true), Default()
   *  @param headUrl Database column head_url SqlType(varchar), Length(100,true), Default()
   *  @param origin Database column origin SqlType(int4)
   *  @param updateTime Database column update_time SqlType(int8), Default(0) */
  final case class rBbsUser(id: Long, bbsId: String = "", bbsName: String = "", headUrl: String = "", origin: Int, updateTime: Long = 0L)
  /** GetResult implicit for fetching rBbsUser objects using plain SQL queries */
  implicit def GetResultrBbsUser(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rBbsUser] = GR{
    prs => import prs._
    rBbsUser.tupled((<<[Long], <<[String], <<[String], <<[String], <<[Int], <<[Long]))
  }
  /** Table description of table bbs_user. Objects of this class serve as prototypes for rows in queries. */
  class tBbsUser(_tableTag: Tag) extends profile.api.Table[rBbsUser](_tableTag, "bbs_user") {
    def * = (id, bbsId, bbsName, headUrl, origin, updateTime) <> (rBbsUser.tupled, rBbsUser.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(bbsId), Rep.Some(bbsName), Rep.Some(headUrl), Rep.Some(origin), Rep.Some(updateTime)).shaped.<>({r=>import r._; _1.map(_=> rBbsUser.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column bbs_id SqlType(varchar), Length(150,true), Default() */
    val bbsId: Rep[String] = column[String]("bbs_id", O.Length(150,varying=true), O.Default(""))
    /** Database column bbs_name SqlType(varchar), Length(150,true), Default() */
    val bbsName: Rep[String] = column[String]("bbs_name", O.Length(150,varying=true), O.Default(""))
    /** Database column head_url SqlType(varchar), Length(100,true), Default() */
    val headUrl: Rep[String] = column[String]("head_url", O.Length(100,varying=true), O.Default(""))
    /** Database column origin SqlType(int4) */
    val origin: Rep[Int] = column[Int]("origin")
    /** Database column update_time SqlType(int8), Default(0) */
    val updateTime: Rep[Long] = column[Long]("update_time", O.Default(0L))
  }
  /** Collection-like TableQuery object for table tBbsUser */
  lazy val tBbsUser = new TableQuery(tag => new tBbsUser(tag))

  /** Entity class storing rows of table tBoard
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param boardName Database column board_name SqlType(varchar), Length(50,true)
   *  @param boardNameCn Database column board_name_cn SqlType(varchar), Length(50,true)
   *  @param origin Database column origin SqlType(int4)
   *  @param firstSpell Database column first_spell SqlType(varchar), Length(2,true), Default(Z)
   *  @param postTodayNum Database column post_today_num SqlType(int4), Default(0) */
  final case class rBoard(id: Long, boardName: String, boardNameCn: String, origin: Int, firstSpell: String = "Z", postTodayNum: Int = 0)
  /** GetResult implicit for fetching rBoard objects using plain SQL queries */
  implicit def GetResultrBoard(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rBoard] = GR{
    prs => import prs._
    rBoard.tupled((<<[Long], <<[String], <<[String], <<[Int], <<[String], <<[Int]))
  }
  /** Table description of table board. Objects of this class serve as prototypes for rows in queries. */
  class tBoard(_tableTag: Tag) extends profile.api.Table[rBoard](_tableTag, "board") {
    def * = (id, boardName, boardNameCn, origin, firstSpell, postTodayNum) <> (rBoard.tupled, rBoard.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(boardName), Rep.Some(boardNameCn), Rep.Some(origin), Rep.Some(firstSpell), Rep.Some(postTodayNum)).shaped.<>({r=>import r._; _1.map(_=> rBoard.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column board_name SqlType(varchar), Length(50,true) */
    val boardName: Rep[String] = column[String]("board_name", O.Length(50,varying=true))
    /** Database column board_name_cn SqlType(varchar), Length(50,true) */
    val boardNameCn: Rep[String] = column[String]("board_name_cn", O.Length(50,varying=true))
    /** Database column origin SqlType(int4) */
    val origin: Rep[Int] = column[Int]("origin")
    /** Database column first_spell SqlType(varchar), Length(2,true), Default(Z) */
    val firstSpell: Rep[String] = column[String]("first_spell", O.Length(2,varying=true), O.Default("Z"))
    /** Database column post_today_num SqlType(int4), Default(0) */
    val postTodayNum: Rep[Int] = column[Int]("post_today_num", O.Default(0))
  }
  /** Collection-like TableQuery object for table tBoard */
  lazy val tBoard = new TableQuery(tag => new tBoard(tag))

  /** Entity class storing rows of table tPosts
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param origin Database column origin SqlType(int4)
   *  @param topicId Database column topic_id SqlType(int8)
   *  @param postId Database column post_id SqlType(int8)
   *  @param isMain Database column is_main SqlType(bool)
   *  @param title Database column title SqlType(varchar), Length(127,true)
   *  @param authorId Database column author_id SqlType(varchar), Length(64,true), Default()
   *  @param authorName Database column author_name SqlType(varchar), Length(64,true)
   *  @param content Database column content SqlType(text)
   *  @param imgs Database column imgs SqlType(text)
   *  @param hestiaImgs Database column hestia_imgs SqlType(text)
   *  @param postTime Database column post_time SqlType(int8)
   *  @param boardName Database column board_name SqlType(varchar), Length(31,true)
   *  @param url Database column url SqlType(varchar), Length(127,true)
   *  @param boardNameCn Database column board_name_cn SqlType(varchar), Length(31,true)
   *  @param quoteId Database column quote_id SqlType(int8), Default(None)
   *  @param updateTime Database column update_time SqlType(int8)
   *  @param state Database column state SqlType(int4), Default(0)
   *  @param pid Database column pid SqlType(int8), Default(0) */
  final case class rPosts(id: Long, origin: Int, topicId: Long, postId: Long, isMain: Boolean, title: String, authorId: String = "", authorName: String, content: String, imgs: String, hestiaImgs: String, postTime: Long, boardName: String, url: String, boardNameCn: String, quoteId: Option[Long] = None, updateTime: Long, state: Int = 0, pid: Long = 0L)
  /** GetResult implicit for fetching rPosts objects using plain SQL queries */
  implicit def GetResultrPosts(implicit e0: GR[Long], e1: GR[Int], e2: GR[Boolean], e3: GR[String], e4: GR[Option[Long]]): GR[rPosts] = GR{
    prs => import prs._
    rPosts.tupled((<<[Long], <<[Int], <<[Long], <<[Long], <<[Boolean], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Long], <<[String], <<[String], <<[String], <<?[Long], <<[Long], <<[Int], <<[Long]))
  }
  /** Table description of table posts. Objects of this class serve as prototypes for rows in queries. */
  class tPosts(_tableTag: Tag) extends profile.api.Table[rPosts](_tableTag, "posts") {
    def * = (id, origin, topicId, postId, isMain, title, authorId, authorName, content, imgs, hestiaImgs, postTime, boardName, url, boardNameCn, quoteId, updateTime, state, pid) <> (rPosts.tupled, rPosts.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(origin), Rep.Some(topicId), Rep.Some(postId), Rep.Some(isMain), Rep.Some(title), Rep.Some(authorId), Rep.Some(authorName), Rep.Some(content), Rep.Some(imgs), Rep.Some(hestiaImgs), Rep.Some(postTime), Rep.Some(boardName), Rep.Some(url), Rep.Some(boardNameCn), quoteId, Rep.Some(updateTime), Rep.Some(state), Rep.Some(pid)).shaped.<>({r=>import r._; _1.map(_=> rPosts.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16, _17.get, _18.get, _19.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column origin SqlType(int4) */
    val origin: Rep[Int] = column[Int]("origin")
    /** Database column topic_id SqlType(int8) */
    val topicId: Rep[Long] = column[Long]("topic_id")
    /** Database column post_id SqlType(int8) */
    val postId: Rep[Long] = column[Long]("post_id")
    /** Database column is_main SqlType(bool) */
    val isMain: Rep[Boolean] = column[Boolean]("is_main")
    /** Database column title SqlType(varchar), Length(127,true) */
    val title: Rep[String] = column[String]("title", O.Length(127,varying=true))
    /** Database column author_id SqlType(varchar), Length(64,true), Default() */
    val authorId: Rep[String] = column[String]("author_id", O.Length(64,varying=true), O.Default(""))
    /** Database column author_name SqlType(varchar), Length(64,true) */
    val authorName: Rep[String] = column[String]("author_name", O.Length(64,varying=true))
    /** Database column content SqlType(text) */
    val content: Rep[String] = column[String]("content")
    /** Database column imgs SqlType(text) */
    val imgs: Rep[String] = column[String]("imgs")
    /** Database column hestia_imgs SqlType(text) */
    val hestiaImgs: Rep[String] = column[String]("hestia_imgs")
    /** Database column post_time SqlType(int8) */
    val postTime: Rep[Long] = column[Long]("post_time")
    /** Database column board_name SqlType(varchar), Length(31,true) */
    val boardName: Rep[String] = column[String]("board_name", O.Length(31,varying=true))
    /** Database column url SqlType(varchar), Length(127,true) */
    val url: Rep[String] = column[String]("url", O.Length(127,varying=true))
    /** Database column board_name_cn SqlType(varchar), Length(31,true) */
    val boardNameCn: Rep[String] = column[String]("board_name_cn", O.Length(31,varying=true))
    /** Database column quote_id SqlType(int8), Default(None) */
    val quoteId: Rep[Option[Long]] = column[Option[Long]]("quote_id", O.Default(None))
    /** Database column update_time SqlType(int8) */
    val updateTime: Rep[Long] = column[Long]("update_time")
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column pid SqlType(int8), Default(0) */
    val pid: Rep[Long] = column[Long]("pid", O.Default(0L))
  }
  /** Collection-like TableQuery object for table tPosts */
  lazy val tPosts = new TableQuery(tag => new tPosts(tag))

  /** Entity class storing rows of table tSynData
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param data Database column data SqlType(int8), Default(0)
   *  @param note Database column note SqlType(varchar), Length(100,true), Default() */
  final case class rSynData(id: Long, data: Long = 0L, note: String = "")
  /** GetResult implicit for fetching rSynData objects using plain SQL queries */
  implicit def GetResultrSynData(implicit e0: GR[Long], e1: GR[String]): GR[rSynData] = GR{
    prs => import prs._
    rSynData.tupled((<<[Long], <<[Long], <<[String]))
  }
  /** Table description of table syn_data. Objects of this class serve as prototypes for rows in queries. */
  class tSynData(_tableTag: Tag) extends profile.api.Table[rSynData](_tableTag, "syn_data") {
    def * = (id, data, note) <> (rSynData.tupled, rSynData.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(data), Rep.Some(note)).shaped.<>({r=>import r._; _1.map(_=> rSynData.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column data SqlType(int8), Default(0) */
    val data: Rep[Long] = column[Long]("data", O.Default(0L))
    /** Database column note SqlType(varchar), Length(100,true), Default() */
    val note: Rep[String] = column[String]("note", O.Length(100,varying=true), O.Default(""))
  }
  /** Collection-like TableQuery object for table tSynData */
  lazy val tSynData = new TableQuery(tag => new tSynData(tag))

  /** Entity class storing rows of table tTopicSnapshot
   *  @param origin Database column origin SqlType(int4)
   *  @param boardName Database column board_name SqlType(varchar), Length(50,true)
   *  @param topicId Database column topic_id SqlType(int8)
   *  @param lastPostId Database column last_post_id SqlType(int8)
   *  @param lastReplyAuthor Database column last_reply_author SqlType(varchar), Length(64,true)
   *  @param lastReplyTime Database column last_reply_time SqlType(int8)
   *  @param voteUpNum Database column vote_up_num SqlType(int4), Default(0)
   *  @param voteDownNum Database column vote_down_num SqlType(int4), Default(0)
   *  @param replyAuthorNum Database column reply_author_num SqlType(int4), Default(0)
   *  @param replyPostNum Database column reply_post_num SqlType(int4), Default(0)
   *  @param visitNum Database column visit_num SqlType(int4), Default(0)
   *  @param postTime Database column post_time SqlType(int8), Default(0) */
  final case class rTopicSnapshot(origin: Int, boardName: String, topicId: Long, lastPostId: Long, lastReplyAuthor: String, lastReplyTime: Long, voteUpNum: Int = 0, voteDownNum: Int = 0, replyAuthorNum: Int = 0, replyPostNum: Int = 0, visitNum: Int = 0, postTime: Long = 0L)
  /** GetResult implicit for fetching rTopicSnapshot objects using plain SQL queries */
  implicit def GetResultrTopicSnapshot(implicit e0: GR[Int], e1: GR[String], e2: GR[Long]): GR[rTopicSnapshot] = GR{
    prs => import prs._
    rTopicSnapshot.tupled((<<[Int], <<[String], <<[Long], <<[Long], <<[String], <<[Long], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Long]))
  }
  /** Table description of table topic_snapshot. Objects of this class serve as prototypes for rows in queries. */
  class tTopicSnapshot(_tableTag: Tag) extends profile.api.Table[rTopicSnapshot](_tableTag, "topic_snapshot") {
    def * = (origin, boardName, topicId, lastPostId, lastReplyAuthor, lastReplyTime, voteUpNum, voteDownNum, replyAuthorNum, replyPostNum, visitNum, postTime) <> (rTopicSnapshot.tupled, rTopicSnapshot.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(origin), Rep.Some(boardName), Rep.Some(topicId), Rep.Some(lastPostId), Rep.Some(lastReplyAuthor), Rep.Some(lastReplyTime), Rep.Some(voteUpNum), Rep.Some(voteDownNum), Rep.Some(replyAuthorNum), Rep.Some(replyPostNum), Rep.Some(visitNum), Rep.Some(postTime)).shaped.<>({r=>import r._; _1.map(_=> rTopicSnapshot.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column origin SqlType(int4) */
    val origin: Rep[Int] = column[Int]("origin")
    /** Database column board_name SqlType(varchar), Length(50,true) */
    val boardName: Rep[String] = column[String]("board_name", O.Length(50,varying=true))
    /** Database column topic_id SqlType(int8) */
    val topicId: Rep[Long] = column[Long]("topic_id")
    /** Database column last_post_id SqlType(int8) */
    val lastPostId: Rep[Long] = column[Long]("last_post_id")
    /** Database column last_reply_author SqlType(varchar), Length(64,true) */
    val lastReplyAuthor: Rep[String] = column[String]("last_reply_author", O.Length(64,varying=true))
    /** Database column last_reply_time SqlType(int8) */
    val lastReplyTime: Rep[Long] = column[Long]("last_reply_time")
    /** Database column vote_up_num SqlType(int4), Default(0) */
    val voteUpNum: Rep[Int] = column[Int]("vote_up_num", O.Default(0))
    /** Database column vote_down_num SqlType(int4), Default(0) */
    val voteDownNum: Rep[Int] = column[Int]("vote_down_num", O.Default(0))
    /** Database column reply_author_num SqlType(int4), Default(0) */
    val replyAuthorNum: Rep[Int] = column[Int]("reply_author_num", O.Default(0))
    /** Database column reply_post_num SqlType(int4), Default(0) */
    val replyPostNum: Rep[Int] = column[Int]("reply_post_num", O.Default(0))
    /** Database column visit_num SqlType(int4), Default(0) */
    val visitNum: Rep[Int] = column[Int]("visit_num", O.Default(0))
    /** Database column post_time SqlType(int8), Default(0) */
    val postTime: Rep[Long] = column[Long]("post_time", O.Default(0L))

    /** Primary key of tTopicSnapshot (database name topic_statistics_snapshot_pkey) */
    val pk = primaryKey("topic_statistics_snapshot_pkey", (origin, boardName, topicId))
  }
  /** Collection-like TableQuery object for table tTopicSnapshot */
  lazy val tTopicSnapshot = new TableQuery(tag => new tTopicSnapshot(tag))

  /** Entity class storing rows of table tUser
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(varchar), Length(255,true)
   *  @param bbsId Database column bbs_id SqlType(varchar), Length(255,true), Default()
   *  @param sha1Pwd Database column sha1_pwd SqlType(varchar), Length(255,true)
   *  @param createTime Database column create_time SqlType(int8)
   *  @param sessionKey Database column session_key SqlType(varchar), Length(255,true), Default()
   *  @param lastLoginTime Database column last_login_time SqlType(int8)
   *  @param mobile Database column mobile SqlType(varchar), Length(63,true), Default()
   *  @param email Database column email SqlType(varchar), Length(150,true), Default()
   *  @param headImg Database column head_img SqlType(varchar), Length(500,true), Default()
   *  @param city Database column city SqlType(varchar), Length(50,true), Default()
   *  @param gender Database column gender SqlType(int4), Default(0)
   *  @param replyCnt Database column reply_cnt SqlType(int4), Default(0)
   *  @param openCnt Database column open_cnt SqlType(int4), Default(0)
   *  @param firstItemTime1 Database column first_item_time1 SqlType(int8), Default(0)
   *  @param firstItemTime2 Database column first_item_time2 SqlType(int8), Default(0) */
  final case class rUser(id: Long, userId: String, bbsId: String = "", sha1Pwd: String, createTime: Long, sessionKey: String = "", lastLoginTime: Long, mobile: String = "", email: String = "", headImg: String = "", city: String = "", gender: Int = 0, replyCnt: Int = 0, openCnt: Int = 0, firstItemTime1: Long = 0L, firstItemTime2: Long = 0L)
  /** GetResult implicit for fetching rUser objects using plain SQL queries */
  implicit def GetResultrUser(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rUser] = GR{
    prs => import prs._
    rUser.tupled((<<[Long], <<[String], <<[String], <<[String], <<[Long], <<[String], <<[Long], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Int], <<[Int], <<[Long], <<[Long]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class tUser(_tableTag: Tag) extends profile.api.Table[rUser](_tableTag, "user") {
    def * = (id, userId, bbsId, sha1Pwd, createTime, sessionKey, lastLoginTime, mobile, email, headImg, city, gender, replyCnt, openCnt, firstItemTime1, firstItemTime2) <> (rUser.tupled, rUser.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(bbsId), Rep.Some(sha1Pwd), Rep.Some(createTime), Rep.Some(sessionKey), Rep.Some(lastLoginTime), Rep.Some(mobile), Rep.Some(email), Rep.Some(headImg), Rep.Some(city), Rep.Some(gender), Rep.Some(replyCnt), Rep.Some(openCnt), Rep.Some(firstItemTime1), Rep.Some(firstItemTime2)).shaped.<>({r=>import r._; _1.map(_=> rUser.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(varchar), Length(255,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(255,varying=true))
    /** Database column bbs_id SqlType(varchar), Length(255,true), Default() */
    val bbsId: Rep[String] = column[String]("bbs_id", O.Length(255,varying=true), O.Default(""))
    /** Database column sha1_pwd SqlType(varchar), Length(255,true) */
    val sha1Pwd: Rep[String] = column[String]("sha1_pwd", O.Length(255,varying=true))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column session_key SqlType(varchar), Length(255,true), Default() */
    val sessionKey: Rep[String] = column[String]("session_key", O.Length(255,varying=true), O.Default(""))
    /** Database column last_login_time SqlType(int8) */
    val lastLoginTime: Rep[Long] = column[Long]("last_login_time")
    /** Database column mobile SqlType(varchar), Length(63,true), Default() */
    val mobile: Rep[String] = column[String]("mobile", O.Length(63,varying=true), O.Default(""))
    /** Database column email SqlType(varchar), Length(150,true), Default() */
    val email: Rep[String] = column[String]("email", O.Length(150,varying=true), O.Default(""))
    /** Database column head_img SqlType(varchar), Length(500,true), Default() */
    val headImg: Rep[String] = column[String]("head_img", O.Length(500,varying=true), O.Default(""))
    /** Database column city SqlType(varchar), Length(50,true), Default() */
    val city: Rep[String] = column[String]("city", O.Length(50,varying=true), O.Default(""))
    /** Database column gender SqlType(int4), Default(0) */
    val gender: Rep[Int] = column[Int]("gender", O.Default(0))
    /** Database column reply_cnt SqlType(int4), Default(0) */
    val replyCnt: Rep[Int] = column[Int]("reply_cnt", O.Default(0))
    /** Database column open_cnt SqlType(int4), Default(0) */
    val openCnt: Rep[Int] = column[Int]("open_cnt", O.Default(0))
    /** Database column first_item_time1 SqlType(int8), Default(0) */
    val firstItemTime1: Rep[Long] = column[Long]("first_item_time1", O.Default(0L))
    /** Database column first_item_time2 SqlType(int8), Default(0) */
    val firstItemTime2: Rep[Long] = column[Long]("first_item_time2", O.Default(0L))
  }
  /** Collection-like TableQuery object for table tUser */
  lazy val tUser = new TableQuery(tag => new tUser(tag))

  /** Entity class storing rows of table tUserFeed
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(int8), Default(0)
   *  @param origin Database column origin SqlType(int4), Default(0)
   *  @param boardname Database column boardname SqlType(varchar), Length(100,true), Default()
   *  @param topicId Database column topic_id SqlType(int8), Default(0)
   *  @param postId Database column post_id SqlType(int8), Default(0)
   *  @param postTime Database column post_time SqlType(int8), Default(0)
   *  @param lastReplyTime Database column last_reply_time SqlType(int8), Default(0)
   *  @param feedType Database column feed_type SqlType(int4), Default(0)
   *  @param authorId Database column author_id SqlType(varchar), Length(64,true), Default(Some())
   *  @param authorName Database column author_name SqlType(varchar), Length(100,true), Default(None) */
  final case class rUserFeed(id: Long, userId: Long = 0L, origin: Int = 0, boardname: String = "", topicId: Long = 0L, postId: Long = 0L, postTime: Long = 0L, lastReplyTime: Long = 0L, feedType: Int = 0, authorId: Option[String] = Some(""), authorName: Option[String] = None)
  /** GetResult implicit for fetching rUserFeed objects using plain SQL queries */
  implicit def GetResultrUserFeed(implicit e0: GR[Long], e1: GR[Int], e2: GR[String], e3: GR[Option[String]]): GR[rUserFeed] = GR{
    prs => import prs._
    rUserFeed.tupled((<<[Long], <<[Long], <<[Int], <<[String], <<[Long], <<[Long], <<[Long], <<[Long], <<[Int], <<?[String], <<?[String]))
  }
  /** Table description of table user_feed. Objects of this class serve as prototypes for rows in queries. */
  class tUserFeed(_tableTag: Tag) extends profile.api.Table[rUserFeed](_tableTag, "user_feed") {
    def * = (id, userId, origin, boardname, topicId, postId, postTime, lastReplyTime, feedType, authorId, authorName) <> (rUserFeed.tupled, rUserFeed.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(origin), Rep.Some(boardname), Rep.Some(topicId), Rep.Some(postId), Rep.Some(postTime), Rep.Some(lastReplyTime), Rep.Some(feedType), authorId, authorName).shaped.<>({r=>import r._; _1.map(_=> rUserFeed.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10, _11)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(int8), Default(0) */
    val userId: Rep[Long] = column[Long]("user_id", O.Default(0L))
    /** Database column origin SqlType(int4), Default(0) */
    val origin: Rep[Int] = column[Int]("origin", O.Default(0))
    /** Database column boardname SqlType(varchar), Length(100,true), Default() */
    val boardname: Rep[String] = column[String]("boardname", O.Length(100,varying=true), O.Default(""))
    /** Database column topic_id SqlType(int8), Default(0) */
    val topicId: Rep[Long] = column[Long]("topic_id", O.Default(0L))
    /** Database column post_id SqlType(int8), Default(0) */
    val postId: Rep[Long] = column[Long]("post_id", O.Default(0L))
    /** Database column post_time SqlType(int8), Default(0) */
    val postTime: Rep[Long] = column[Long]("post_time", O.Default(0L))
    /** Database column last_reply_time SqlType(int8), Default(0) */
    val lastReplyTime: Rep[Long] = column[Long]("last_reply_time", O.Default(0L))
    /** Database column feed_type SqlType(int4), Default(0) */
    val feedType: Rep[Int] = column[Int]("feed_type", O.Default(0))
    /** Database column author_id SqlType(varchar), Length(64,true), Default(Some()) */
    val authorId: Rep[Option[String]] = column[Option[String]]("author_id", O.Length(64,varying=true), O.Default(Some("")))
    /** Database column author_name SqlType(varchar), Length(100,true), Default(None) */
    val authorName: Rep[Option[String]] = column[Option[String]]("author_name", O.Length(100,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table tUserFeed */
  lazy val tUserFeed = new TableQuery(tag => new tUserFeed(tag))

  /** Entity class storing rows of table tUserFollowBoard
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(int8), Default(0)
   *  @param boardName Database column board_name SqlType(varchar), Length(255,true), Default()
   *  @param boardTitle Database column board_title SqlType(varchar), Length(255,true), Default()
   *  @param createTime Database column create_time SqlType(int8)
   *  @param state Database column state SqlType(int4), Default(0)
   *  @param origin Database column origin SqlType(int4), Default(0) */
  final case class rUserFollowBoard(id: Long, userId: Long = 0L, boardName: String = "", boardTitle: String = "", createTime: Long, state: Int = 0, origin: Int = 0)
  /** GetResult implicit for fetching rUserFollowBoard objects using plain SQL queries */
  implicit def GetResultrUserFollowBoard(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rUserFollowBoard] = GR{
    prs => import prs._
    rUserFollowBoard.tupled((<<[Long], <<[Long], <<[String], <<[String], <<[Long], <<[Int], <<[Int]))
  }
  /** Table description of table user_follow_board. Objects of this class serve as prototypes for rows in queries. */
  class tUserFollowBoard(_tableTag: Tag) extends profile.api.Table[rUserFollowBoard](_tableTag, "user_follow_board") {
    def * = (id, userId, boardName, boardTitle, createTime, state, origin) <> (rUserFollowBoard.tupled, rUserFollowBoard.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(boardName), Rep.Some(boardTitle), Rep.Some(createTime), Rep.Some(state), Rep.Some(origin)).shaped.<>({r=>import r._; _1.map(_=> rUserFollowBoard.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(int8), Default(0) */
    val userId: Rep[Long] = column[Long]("user_id", O.Default(0L))
    /** Database column board_name SqlType(varchar), Length(255,true), Default() */
    val boardName: Rep[String] = column[String]("board_name", O.Length(255,varying=true), O.Default(""))
    /** Database column board_title SqlType(varchar), Length(255,true), Default() */
    val boardTitle: Rep[String] = column[String]("board_title", O.Length(255,varying=true), O.Default(""))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column origin SqlType(int4), Default(0) */
    val origin: Rep[Int] = column[Int]("origin", O.Default(0))
  }
  /** Collection-like TableQuery object for table tUserFollowBoard */
  lazy val tUserFollowBoard = new TableQuery(tag => new tUserFollowBoard(tag))

  /** Entity class storing rows of table tUserFollowTopic
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(int8), Default(0)
   *  @param boardName Database column board_name SqlType(varchar), Length(255,true), Default()
   *  @param topicId Database column topic_id SqlType(int8), Default(0)
   *  @param createTime Database column create_time SqlType(int8)
   *  @param state Database column state SqlType(int4), Default(0)
   *  @param origin Database column origin SqlType(int4), Default(0) */
  final case class rUserFollowTopic(id: Long, userId: Long = 0L, boardName: String = "", topicId: Long = 0L, createTime: Long, state: Int = 0, origin: Int = 0)
  /** GetResult implicit for fetching rUserFollowTopic objects using plain SQL queries */
  implicit def GetResultrUserFollowTopic(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rUserFollowTopic] = GR{
    prs => import prs._
    rUserFollowTopic.tupled((<<[Long], <<[Long], <<[String], <<[Long], <<[Long], <<[Int], <<[Int]))
  }
  /** Table description of table user_follow_topic. Objects of this class serve as prototypes for rows in queries. */
  class tUserFollowTopic(_tableTag: Tag) extends profile.api.Table[rUserFollowTopic](_tableTag, "user_follow_topic") {
    def * = (id, userId, boardName, topicId, createTime, state, origin) <> (rUserFollowTopic.tupled, rUserFollowTopic.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(boardName), Rep.Some(topicId), Rep.Some(createTime), Rep.Some(state), Rep.Some(origin)).shaped.<>({r=>import r._; _1.map(_=> rUserFollowTopic.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(int8), Default(0) */
    val userId: Rep[Long] = column[Long]("user_id", O.Default(0L))
    /** Database column board_name SqlType(varchar), Length(255,true), Default() */
    val boardName: Rep[String] = column[String]("board_name", O.Length(255,varying=true), O.Default(""))
    /** Database column topic_id SqlType(int8), Default(0) */
    val topicId: Rep[Long] = column[Long]("topic_id", O.Default(0L))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
    /** Database column origin SqlType(int4), Default(0) */
    val origin: Rep[Int] = column[Int]("origin", O.Default(0))
  }
  /** Collection-like TableQuery object for table tUserFollowTopic */
  lazy val tUserFollowTopic = new TableQuery(tag => new tUserFollowTopic(tag))

  /** Entity class storing rows of table tUserFollowUser
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(int8), Default(0)
   *  @param followId Database column follow_id SqlType(varchar), Length(64,true), Default()
   *  @param followName Database column follow_name SqlType(varchar), Length(200,true), Default()
   *  @param createTime Database column create_time SqlType(int8)
   *  @param origin Database column origin SqlType(int4)
   *  @param state Database column state SqlType(int4), Default(0) */
  final case class rUserFollowUser(id: Long, userId: Long = 0L, followId: String = "", followName: String = "", createTime: Long, origin: Int, state: Int = 0)
  /** GetResult implicit for fetching rUserFollowUser objects using plain SQL queries */
  implicit def GetResultrUserFollowUser(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rUserFollowUser] = GR{
    prs => import prs._
    rUserFollowUser.tupled((<<[Long], <<[Long], <<[String], <<[String], <<[Long], <<[Int], <<[Int]))
  }
  /** Table description of table user_follow_user. Objects of this class serve as prototypes for rows in queries. */
  class tUserFollowUser(_tableTag: Tag) extends profile.api.Table[rUserFollowUser](_tableTag, "user_follow_user") {
    def * = (id, userId, followId, followName, createTime, origin, state) <> (rUserFollowUser.tupled, rUserFollowUser.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(followId), Rep.Some(followName), Rep.Some(createTime), Rep.Some(origin), Rep.Some(state)).shaped.<>({r=>import r._; _1.map(_=> rUserFollowUser.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(int8), Default(0) */
    val userId: Rep[Long] = column[Long]("user_id", O.Default(0L))
    /** Database column follow_id SqlType(varchar), Length(64,true), Default() */
    val followId: Rep[String] = column[String]("follow_id", O.Length(64,varying=true), O.Default(""))
    /** Database column follow_name SqlType(varchar), Length(200,true), Default() */
    val followName: Rep[String] = column[String]("follow_name", O.Length(200,varying=true), O.Default(""))
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
    /** Database column origin SqlType(int4) */
    val origin: Rep[Int] = column[Int]("origin")
    /** Database column state SqlType(int4), Default(0) */
    val state: Rep[Int] = column[Int]("state", O.Default(0))
  }
  /** Collection-like TableQuery object for table tUserFollowUser */
  lazy val tUserFollowUser = new TableQuery(tag => new tUserFollowUser(tag))
}
