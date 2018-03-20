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
  lazy val schema: profile.SchemaDescription = Array(tAdmin.schema, tAppSnapshot.schema, ttimelineAddRecords.schema, ttimelineRedRecords.schema, ttimelineTradeRecords.schema, tUserSnapshot.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tAdmin
   *  @param adminname Database column adminname SqlType(varchar), PrimaryKey, Length(255,true)
   *  @param passwordmd5 Database column passwordmd5 SqlType(varchar), Length(255,true)
   *  @param registertime Database column registertime SqlType(int8) */
  final case class rAdmin(adminname: String, passwordmd5: String, registertime: Long)
  /** GetResult implicit for fetching rAdmin objects using plain SQL queries */
  implicit def GetResultrAdmin(implicit e0: GR[String], e1: GR[Long]): GR[rAdmin] = GR{
    prs => import prs._
    rAdmin.tupled((<<[String], <<[String], <<[Long]))
  }
  /** Table description of table admin. Objects of this class serve as prototypes for rows in queries. */
  class tAdmin(_tableTag: Tag) extends profile.api.Table[rAdmin](_tableTag, "admin") {
    def * = (adminname, passwordmd5, registertime) <> (rAdmin.tupled, rAdmin.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(adminname), Rep.Some(passwordmd5), Rep.Some(registertime)).shaped.<>({r=>import r._; _1.map(_=> rAdmin.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column adminname SqlType(varchar), PrimaryKey, Length(255,true) */
    val adminname: Rep[String] = column[String]("adminname", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column passwordmd5 SqlType(varchar), Length(255,true) */
    val passwordmd5: Rep[String] = column[String]("passwordmd5", O.Length(255,varying=true))
    /** Database column registertime SqlType(int8) */
    val registertime: Rep[Long] = column[Long]("registertime")
  }
  /** Collection-like TableQuery object for table tAdmin */
  lazy val tAdmin = new TableQuery(tag => new tAdmin(tag))

  /** Entity class storing rows of table tAppSnapshot
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param appName Database column app_name SqlType(varchar), Length(255,true)
   *  @param timeline Database column timeline SqlType(int8)
   *  @param createTime Database column create_time SqlType(int8) */
  final case class rAppSnapshot(id: Int, appName: String, timeline: Long, createTime: Long)
  /** GetResult implicit for fetching rAppSnapshot objects using plain SQL queries */
  implicit def GetResultrAppSnapshot(implicit e0: GR[Int], e1: GR[String], e2: GR[Long]): GR[rAppSnapshot] = GR{
    prs => import prs._
    rAppSnapshot.tupled((<<[Int], <<[String], <<[Long], <<[Long]))
  }
  /** Table description of table app_snapshot. Objects of this class serve as prototypes for rows in queries. */
  class tAppSnapshot(_tableTag: Tag) extends profile.api.Table[rAppSnapshot](_tableTag, "app_snapshot") {
    def * = (id, appName, timeline, createTime) <> (rAppSnapshot.tupled, rAppSnapshot.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(appName), Rep.Some(timeline), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rAppSnapshot.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column app_name SqlType(varchar), Length(255,true) */
    val appName: Rep[String] = column[String]("app_name", O.Length(255,varying=true))
    /** Database column timeline SqlType(int8) */
    val timeline: Rep[Long] = column[Long]("timeline")
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")

    /** Uniqueness Index over (appName) (database name app_snapshot_open_id_index) */
    val index1 = index("app_snapshot_open_id_index", appName, unique=true)
  }
  /** Collection-like TableQuery object for table tAppSnapshot */
  lazy val tAppSnapshot = new TableQuery(tag => new tAppSnapshot(tag))

  /** Entity class storing rows of table ttimelineAddRecords
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param appId Database column app_id SqlType(int4)
   *  @param userId Database column user_id SqlType(varchar), Length(255,true)
   *  @param timeline Database column timeline SqlType(int4)
   *  @param dataType Database column data_type SqlType(int4)
   *  @param createTime Database column create_time SqlType(int8) */
  final case class rtimelineAddRecords(id: Long, appId: Int, userId: String, timeline: Int, dataType: Int, createTime: Long)
  /** GetResult implicit for fetching rtimelineAddRecords objects using plain SQL queries */
  implicit def GetResultrtimelineAddRecords(implicit e0: GR[Long], e1: GR[Int], e2: GR[String]): GR[rtimelineAddRecords] = GR{
    prs => import prs._
    rtimelineAddRecords.tupled((<<[Long], <<[Int], <<[String], <<[Int], <<[Int], <<[Long]))
  }
  /** Table description of table timeline_add_records. Objects of this class serve as prototypes for rows in queries. */
  class ttimelineAddRecords(_tableTag: Tag) extends profile.api.Table[rtimelineAddRecords](_tableTag, "timeline_add_records") {
    def * = (id, appId, userId, timeline, dataType, createTime) <> (rtimelineAddRecords.tupled, rtimelineAddRecords.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(appId), Rep.Some(userId), Rep.Some(timeline), Rep.Some(dataType), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rtimelineAddRecords.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column app_id SqlType(int4) */
    val appId: Rep[Int] = column[Int]("app_id")
    /** Database column user_id SqlType(varchar), Length(255,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(255,varying=true))
    /** Database column timeline SqlType(int4) */
    val timeline: Rep[Int] = column[Int]("timeline")
    /** Database column data_type SqlType(int4) */
    val dataType: Rep[Int] = column[Int]("data_type")
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")

    /** Index over (appId) (database name timeline_add_records_app_index) */
    val index1 = index("timeline_add_records_app_index", appId)
    /** Index over (userId) (database name timeline_add_records_user_index) */
    val index2 = index("timeline_add_records_user_index", userId)
    /** Index over (appId) (database name timeline_red_records_app_index) */
    val index3 = index("timeline_red_records_app_index", appId)
    /** Index over (userId) (database name timeline_red_records_user_index) */
    val index4 = index("timeline_red_records_user_index", userId)
  }
  /** Collection-like TableQuery object for table ttimelineAddRecords */
  lazy val ttimelineAddRecords = new TableQuery(tag => new ttimelineAddRecords(tag))

  /** Entity class storing rows of table ttimelineRedRecords
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(varchar), Length(255,true)
   *  @param appId Database column app_id SqlType(int4)
   *  @param timeline Database column timeline SqlType(int4)
   *  @param dataType Database column data_type SqlType(int4)
   *  @param createTime Database column create_time SqlType(int8) */
  final case class rtimelineRedRecords(id: Long, userId: String, appId: Int, timeline: Int, dataType: Int, createTime: Long)
  /** GetResult implicit for fetching rtimelineRedRecords objects using plain SQL queries */
  implicit def GetResultrtimelineRedRecords(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rtimelineRedRecords] = GR{
    prs => import prs._
    rtimelineRedRecords.tupled((<<[Long], <<[String], <<[Int], <<[Int], <<[Int], <<[Long]))
  }
  /** Table description of table timeline_red_records. Objects of this class serve as prototypes for rows in queries. */
  class ttimelineRedRecords(_tableTag: Tag) extends profile.api.Table[rtimelineRedRecords](_tableTag, "timeline_red_records") {
    def * = (id, userId, appId, timeline, dataType, createTime) <> (rtimelineRedRecords.tupled, rtimelineRedRecords.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(appId), Rep.Some(timeline), Rep.Some(dataType), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rtimelineRedRecords.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(varchar), Length(255,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(255,varying=true))
    /** Database column app_id SqlType(int4) */
    val appId: Rep[Int] = column[Int]("app_id")
    /** Database column timeline SqlType(int4) */
    val timeline: Rep[Int] = column[Int]("timeline")
    /** Database column data_type SqlType(int4) */
    val dataType: Rep[Int] = column[Int]("data_type")
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")
  }
  /** Collection-like TableQuery object for table ttimelineRedRecords */
  lazy val ttimelineRedRecords = new TableQuery(tag => new ttimelineRedRecords(tag))

  /** Entity class storing rows of table ttimelineTradeRecords
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param appId Database column app_id SqlType(int4)
   *  @param userId Database column user_id SqlType(varchar), Length(255,true)
   *  @param objUser Database column obj_user SqlType(varchar), Length(255,true)
   *  @param totaltimeline Database column total_timeline SqlType(int4)
   *  @param fee Database column fee SqlType(int4)
   *  @param objtimeline Database column obj_timeline SqlType(int4)
   *  @param dataType Database column data_type SqlType(int4)
   *  @param createTime Database column create_time SqlType(int8) */
  final case class rtimelineTradeRecords(id: Long, appId: Int, userId: String, objUser: String, totaltimeline: Int, fee: Int, objtimeline: Int, dataType: Int, createTime: Long)
  /** GetResult implicit for fetching rtimelineTradeRecords objects using plain SQL queries */
  implicit def GetResultrtimelineTradeRecords(implicit e0: GR[Long], e1: GR[Int], e2: GR[String]): GR[rtimelineTradeRecords] = GR{
    prs => import prs._
    rtimelineTradeRecords.tupled((<<[Long], <<[Int], <<[String], <<[String], <<[Int], <<[Int], <<[Int], <<[Int], <<[Long]))
  }
  /** Table description of table timeline_trade_records. Objects of this class serve as prototypes for rows in queries. */
  class ttimelineTradeRecords(_tableTag: Tag) extends profile.api.Table[rtimelineTradeRecords](_tableTag, "timeline_trade_records") {
    def * = (id, appId, userId, objUser, totaltimeline, fee, objtimeline, dataType, createTime) <> (rtimelineTradeRecords.tupled, rtimelineTradeRecords.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(appId), Rep.Some(userId), Rep.Some(objUser), Rep.Some(totaltimeline), Rep.Some(fee), Rep.Some(objtimeline), Rep.Some(dataType), Rep.Some(createTime)).shaped.<>({r=>import r._; _1.map(_=> rtimelineTradeRecords.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column app_id SqlType(int4) */
    val appId: Rep[Int] = column[Int]("app_id")
    /** Database column user_id SqlType(varchar), Length(255,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(255,varying=true))
    /** Database column obj_user SqlType(varchar), Length(255,true) */
    val objUser: Rep[String] = column[String]("obj_user", O.Length(255,varying=true))
    /** Database column total_timeline SqlType(int4) */
    val totaltimeline: Rep[Int] = column[Int]("total_timeline")
    /** Database column fee SqlType(int4) */
    val fee: Rep[Int] = column[Int]("fee")
    /** Database column obj_timeline SqlType(int4) */
    val objtimeline: Rep[Int] = column[Int]("obj_timeline")
    /** Database column data_type SqlType(int4) */
    val dataType: Rep[Int] = column[Int]("data_type")
    /** Database column create_time SqlType(int8) */
    val createTime: Rep[Long] = column[Long]("create_time")

    /** Index over (appId) (database name timeline_trade_app_index) */
    val index1 = index("timeline_trade_app_index", appId)
    /** Index over (objUser) (database name timeline_trade_obj_index) */
    val index2 = index("timeline_trade_obj_index", objUser)
    /** Index over (userId) (database name timeline_trade_user_index) */
    val index3 = index("timeline_trade_user_index", userId)
  }
  /** Collection-like TableQuery object for table ttimelineTradeRecords */
  lazy val ttimelineTradeRecords = new TableQuery(tag => new ttimelineTradeRecords(tag))

  /** Entity class storing rows of table tUserSnapshot
   *  @param id Database column id SqlType(bigserial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(varchar), Length(255,true)
   *  @param appId Database column app_id SqlType(int4)
   *  @param timeline Database column timeline SqlType(int4)
   *  @param lastRecordId Database column last_record_id SqlType(int8)
   *  @param timestamp Database column timestamp SqlType(int8) */
  final case class rUserSnapshot(id: Long, userId: String, appId: Int, timeline: Int, lastRecordId: Long, timestamp: Long)
  /** GetResult implicit for fetching rUserSnapshot objects using plain SQL queries */
  implicit def GetResultrUserSnapshot(implicit e0: GR[Long], e1: GR[String], e2: GR[Int]): GR[rUserSnapshot] = GR{
    prs => import prs._
    rUserSnapshot.tupled((<<[Long], <<[String], <<[Int], <<[Int], <<[Long], <<[Long]))
  }
  /** Table description of table user_snapshot. Objects of this class serve as prototypes for rows in queries. */
  class tUserSnapshot(_tableTag: Tag) extends profile.api.Table[rUserSnapshot](_tableTag, "user_snapshot") {
    def * = (id, userId, appId, timeline, lastRecordId, timestamp) <> (rUserSnapshot.tupled, rUserSnapshot.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(appId), Rep.Some(timeline), Rep.Some(lastRecordId), Rep.Some(timestamp)).shaped.<>({r=>import r._; _1.map(_=> rUserSnapshot.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(bigserial), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(varchar), Length(255,true) */
    val userId: Rep[String] = column[String]("user_id", O.Length(255,varying=true))
    /** Database column app_id SqlType(int4) */
    val appId: Rep[Int] = column[Int]("app_id")
    /** Database column timeline SqlType(int4) */
    val timeline: Rep[Int] = column[Int]("timeline")
    /** Database column last_record_id SqlType(int8) */
    val lastRecordId: Rep[Long] = column[Long]("last_record_id")
    /** Database column timestamp SqlType(int8) */
    val timestamp: Rep[Long] = column[Long]("timestamp")

    /** Index over (appId) (database name user_snapshot_app_index) */
    val index1 = index("user_snapshot_app_index", appId)
    /** Uniqueness Index over (userId) (database name user_snapshot_open_id_index) */
    val index2 = index("user_snapshot_open_id_index", userId, unique=true)
  }
  /** Collection-like TableQuery object for table tUserSnapshot */
  lazy val tUserSnapshot = new TableQuery(tag => new tUserSnapshot(tag))
}
