package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.MySQLJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Account.schema, ExtraData.schema, Mission.schema, Mode.schema, PdfInfo.schema, Trash.schema, User.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Account
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param account Database column account SqlType(VARCHAR), Length(255,true)
   *  @param password Database column password SqlType(VARCHAR), Length(255,true)
   *  @param email Database column email SqlType(VARCHAR), Length(255,true) */
  case class AccountRow(id: Int, account: String, password: String, email: String)
  /** GetResult implicit for fetching AccountRow objects using plain SQL queries */
  implicit def GetResultAccountRow(implicit e0: GR[Int], e1: GR[String]): GR[AccountRow] = GR{
    prs => import prs._
    AccountRow.tupled((<<[Int], <<[String], <<[String], <<[String]))
  }
  /** Table description of table account. Objects of this class serve as prototypes for rows in queries. */
  class Account(_tableTag: Tag) extends profile.api.Table[AccountRow](_tableTag, Some("live_forest"), "account") {
    def * = (id, account, password, email) <> (AccountRow.tupled, AccountRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(account), Rep.Some(password), Rep.Some(email)).shaped.<>({r=>import r._; _1.map(_=> AccountRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column account SqlType(VARCHAR), Length(255,true) */
    val account: Rep[String] = column[String]("account", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
    /** Database column email SqlType(VARCHAR), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Account */
  lazy val Account = new TableQuery(tag => new Account(tag))

  /** Entity class storing rows of table ExtraData
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param userId Database column user_id SqlType(INT)
   *  @param sampleId Database column sample_id SqlType(VARCHAR), Length(255,true)
   *  @param unit Database column unit SqlType(VARCHAR), Length(255,true)
   *  @param address Database column address SqlType(VARCHAR), Length(255,true)
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param sex Database column sex SqlType(VARCHAR), Length(255,true)
   *  @param office Database column office SqlType(VARCHAR), Length(255,true)
   *  @param doctor Database column doctor SqlType(VARCHAR), Length(255,true)
   *  @param number Database column number SqlType(VARCHAR), Length(255,true)
   *  @param sampleTime Database column sample_time SqlType(VARCHAR), Length(255,true)
   *  @param submitTime Database column submit_time SqlType(VARCHAR), Length(255,true)
   *  @param sampleType Database column sample_type SqlType(VARCHAR), Length(255,true)
   *  @param sampleStatus Database column sample_status SqlType(VARCHAR), Length(255,true)
   *  @param title Database column title SqlType(VARCHAR), Length(255,true)
   *  @param danger Database column danger SqlType(VARCHAR), Length(255,true)
   *  @param reporter Database column reporter SqlType(VARCHAR), Length(255,true)
   *  @param checker Database column checker SqlType(VARCHAR), Length(255,true)
   *  @param checkDate Database column check_date SqlType(VARCHAR), Length(255,true)
   *  @param reportDate Database column report_date SqlType(VARCHAR), Length(255,true) */
  case class ExtraDataRow(id: Int, userId: Int, sampleId: String, unit: String, address: String, name: String, sex: String, office: String, doctor: String, number: String, sampleTime: String, submitTime: String, sampleType: String, sampleStatus: String, title: String, danger: String, reporter: String, checker: String, checkDate: String, reportDate: String)
  /** GetResult implicit for fetching ExtraDataRow objects using plain SQL queries */
  implicit def GetResultExtraDataRow(implicit e0: GR[Int], e1: GR[String]): GR[ExtraDataRow] = GR{
    prs => import prs._
    ExtraDataRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table extra_data. Objects of this class serve as prototypes for rows in queries. */
  class ExtraData(_tableTag: Tag) extends profile.api.Table[ExtraDataRow](_tableTag, Some("live_forest"), "extra_data") {
    def * = (id, userId, sampleId, unit, address, name, sex, office, doctor, number, sampleTime, submitTime, sampleType, sampleStatus, title, danger, reporter, checker, checkDate, reportDate) <> (ExtraDataRow.tupled, ExtraDataRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(sampleId), Rep.Some(unit), Rep.Some(address), Rep.Some(name), Rep.Some(sex), Rep.Some(office), Rep.Some(doctor), Rep.Some(number), Rep.Some(sampleTime), Rep.Some(submitTime), Rep.Some(sampleType), Rep.Some(sampleStatus), Rep.Some(title), Rep.Some(danger), Rep.Some(reporter), Rep.Some(checker), Rep.Some(checkDate), Rep.Some(reportDate)).shaped.<>({r=>import r._; _1.map(_=> ExtraDataRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get, _20.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column sample_id SqlType(VARCHAR), Length(255,true) */
    val sampleId: Rep[String] = column[String]("sample_id", O.Length(255,varying=true))
    /** Database column unit SqlType(VARCHAR), Length(255,true) */
    val unit: Rep[String] = column[String]("unit", O.Length(255,varying=true))
    /** Database column address SqlType(VARCHAR), Length(255,true) */
    val address: Rep[String] = column[String]("address", O.Length(255,varying=true))
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column sex SqlType(VARCHAR), Length(255,true) */
    val sex: Rep[String] = column[String]("sex", O.Length(255,varying=true))
    /** Database column office SqlType(VARCHAR), Length(255,true) */
    val office: Rep[String] = column[String]("office", O.Length(255,varying=true))
    /** Database column doctor SqlType(VARCHAR), Length(255,true) */
    val doctor: Rep[String] = column[String]("doctor", O.Length(255,varying=true))
    /** Database column number SqlType(VARCHAR), Length(255,true) */
    val number: Rep[String] = column[String]("number", O.Length(255,varying=true))
    /** Database column sample_time SqlType(VARCHAR), Length(255,true) */
    val sampleTime: Rep[String] = column[String]("sample_time", O.Length(255,varying=true))
    /** Database column submit_time SqlType(VARCHAR), Length(255,true) */
    val submitTime: Rep[String] = column[String]("submit_time", O.Length(255,varying=true))
    /** Database column sample_type SqlType(VARCHAR), Length(255,true) */
    val sampleType: Rep[String] = column[String]("sample_type", O.Length(255,varying=true))
    /** Database column sample_status SqlType(VARCHAR), Length(255,true) */
    val sampleStatus: Rep[String] = column[String]("sample_status", O.Length(255,varying=true))
    /** Database column title SqlType(VARCHAR), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column danger SqlType(VARCHAR), Length(255,true) */
    val danger: Rep[String] = column[String]("danger", O.Length(255,varying=true))
    /** Database column reporter SqlType(VARCHAR), Length(255,true) */
    val reporter: Rep[String] = column[String]("reporter", O.Length(255,varying=true))
    /** Database column checker SqlType(VARCHAR), Length(255,true) */
    val checker: Rep[String] = column[String]("checker", O.Length(255,varying=true))
    /** Database column check_date SqlType(VARCHAR), Length(255,true) */
    val checkDate: Rep[String] = column[String]("check_date", O.Length(255,varying=true))
    /** Database column report_date SqlType(VARCHAR), Length(255,true) */
    val reportDate: Rep[String] = column[String]("report_date", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table ExtraData */
  lazy val ExtraData = new TableQuery(tag => new ExtraData(tag))

  /** Entity class storing rows of table Mission
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(INT)
   *  @param age Database column age SqlType(VARCHAR), Length(255,true)
   *  @param ast Database column ast SqlType(VARCHAR), Length(255,true)
   *  @param alt Database column alt SqlType(VARCHAR), Length(255,true)
   *  @param plt Database column plt SqlType(VARCHAR), Length(255,true)
   *  @param tyr Database column tyr SqlType(VARCHAR), Length(255,true)
   *  @param tca Database column tca SqlType(VARCHAR), Length(255,true)
   *  @param result Database column result SqlType(VARCHAR), Length(255,true)
   *  @param endTime Database column end_time SqlType(DATETIME) */
  case class MissionRow(id: Int, userId: Int, age: String, ast: String, alt: String, plt: String, tyr: String, tca: String, result: String, endTime: DateTime)
  /** GetResult implicit for fetching MissionRow objects using plain SQL queries */
  implicit def GetResultMissionRow(implicit e0: GR[Int], e1: GR[String], e2: GR[DateTime]): GR[MissionRow] = GR{
    prs => import prs._
    MissionRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[DateTime]))
  }
  /** Table description of table mission. Objects of this class serve as prototypes for rows in queries. */
  class Mission(_tableTag: Tag) extends profile.api.Table[MissionRow](_tableTag, Some("live_forest"), "mission") {
    def * = (id, userId, age, ast, alt, plt, tyr, tca, result, endTime) <> (MissionRow.tupled, MissionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(age), Rep.Some(ast), Rep.Some(alt), Rep.Some(plt), Rep.Some(tyr), Rep.Some(tca), Rep.Some(result), Rep.Some(endTime)).shaped.<>({r=>import r._; _1.map(_=> MissionRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column age SqlType(VARCHAR), Length(255,true) */
    val age: Rep[String] = column[String]("age", O.Length(255,varying=true))
    /** Database column ast SqlType(VARCHAR), Length(255,true) */
    val ast: Rep[String] = column[String]("ast", O.Length(255,varying=true))
    /** Database column alt SqlType(VARCHAR), Length(255,true) */
    val alt: Rep[String] = column[String]("alt", O.Length(255,varying=true))
    /** Database column plt SqlType(VARCHAR), Length(255,true) */
    val plt: Rep[String] = column[String]("plt", O.Length(255,varying=true))
    /** Database column tyr SqlType(VARCHAR), Length(255,true) */
    val tyr: Rep[String] = column[String]("tyr", O.Length(255,varying=true))
    /** Database column tca SqlType(VARCHAR), Length(255,true) */
    val tca: Rep[String] = column[String]("tca", O.Length(255,varying=true))
    /** Database column result SqlType(VARCHAR), Length(255,true) */
    val result: Rep[String] = column[String]("result", O.Length(255,varying=true))
    /** Database column end_time SqlType(DATETIME) */
    val endTime: Rep[DateTime] = column[DateTime]("end_time")
  }
  /** Collection-like TableQuery object for table Mission */
  lazy val Mission = new TableQuery(tag => new Mission(tag))

  /** Entity class storing rows of table Mode
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param test Database column test SqlType(VARCHAR), Length(255,true) */
  case class ModeRow(id: Int, test: String)
  /** GetResult implicit for fetching ModeRow objects using plain SQL queries */
  implicit def GetResultModeRow(implicit e0: GR[Int], e1: GR[String]): GR[ModeRow] = GR{
    prs => import prs._
    ModeRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table mode. Objects of this class serve as prototypes for rows in queries. */
  class Mode(_tableTag: Tag) extends profile.api.Table[ModeRow](_tableTag, Some("live_forest"), "mode") {
    def * = (id, test) <> (ModeRow.tupled, ModeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(test)).shaped.<>({r=>import r._; _1.map(_=> ModeRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column test SqlType(VARCHAR), Length(255,true) */
    val test: Rep[String] = column[String]("test", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Mode */
  lazy val Mode = new TableQuery(tag => new Mode(tag))

  /** Entity class storing rows of table PdfInfo
   *  @param userId Database column user_id SqlType(INT), PrimaryKey
   *  @param title Database column title SqlType(VARCHAR), Length(255,true)
   *  @param unit Database column unit SqlType(VARCHAR), Length(255,true)
   *  @param address Database column address SqlType(VARCHAR), Length(255,true)
   *  @param reporter Database column reporter SqlType(VARCHAR), Length(255,true)
   *  @param checker Database column checker SqlType(VARCHAR), Length(255,true) */
  case class PdfInfoRow(userId: Int, title: String, unit: String, address: String, reporter: String, checker: String)
  /** GetResult implicit for fetching PdfInfoRow objects using plain SQL queries */
  implicit def GetResultPdfInfoRow(implicit e0: GR[Int], e1: GR[String]): GR[PdfInfoRow] = GR{
    prs => import prs._
    PdfInfoRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table pdf_info. Objects of this class serve as prototypes for rows in queries. */
  class PdfInfo(_tableTag: Tag) extends profile.api.Table[PdfInfoRow](_tableTag, Some("live_forest"), "pdf_info") {
    def * = (userId, title, unit, address, reporter, checker) <> (PdfInfoRow.tupled, PdfInfoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(title), Rep.Some(unit), Rep.Some(address), Rep.Some(reporter), Rep.Some(checker)).shaped.<>({r=>import r._; _1.map(_=> PdfInfoRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT), PrimaryKey */
    val userId: Rep[Int] = column[Int]("user_id", O.PrimaryKey)
    /** Database column title SqlType(VARCHAR), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column unit SqlType(VARCHAR), Length(255,true) */
    val unit: Rep[String] = column[String]("unit", O.Length(255,varying=true))
    /** Database column address SqlType(VARCHAR), Length(255,true) */
    val address: Rep[String] = column[String]("address", O.Length(255,varying=true))
    /** Database column reporter SqlType(VARCHAR), Length(255,true) */
    val reporter: Rep[String] = column[String]("reporter", O.Length(255,varying=true))
    /** Database column checker SqlType(VARCHAR), Length(255,true) */
    val checker: Rep[String] = column[String]("checker", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table PdfInfo */
  lazy val PdfInfo = new TableQuery(tag => new PdfInfo(tag))

  /** Entity class storing rows of table Trash
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param userId Database column user_id SqlType(INT)
   *  @param exist Database column exist SqlType(BIT) */
  case class TrashRow(id: Int, userId: Int, exist: Boolean)
  /** GetResult implicit for fetching TrashRow objects using plain SQL queries */
  implicit def GetResultTrashRow(implicit e0: GR[Int], e1: GR[Boolean]): GR[TrashRow] = GR{
    prs => import prs._
    TrashRow.tupled((<<[Int], <<[Int], <<[Boolean]))
  }
  /** Table description of table trash. Objects of this class serve as prototypes for rows in queries. */
  class Trash(_tableTag: Tag) extends profile.api.Table[TrashRow](_tableTag, Some("live_forest"), "trash") {
    def * = (id, userId, exist) <> (TrashRow.tupled, TrashRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(userId), Rep.Some(exist)).shaped.<>({r=>import r._; _1.map(_=> TrashRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column exist SqlType(BIT) */
    val exist: Rep[Boolean] = column[Boolean]("exist")
  }
  /** Collection-like TableQuery object for table Trash */
  lazy val Trash = new TableQuery(tag => new Trash(tag))

  /** Entity class storing rows of table User
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param fullName Database column full_name SqlType(VARCHAR), Length(255,true)
   *  @param password Database column password SqlType(VARCHAR), Length(255,true)
   *  @param unit Database column unit SqlType(VARCHAR), Length(255,true)
   *  @param email Database column email SqlType(VARCHAR), Length(255,true)
   *  @param phone Database column phone SqlType(VARCHAR), Length(255,true)
   *  @param remainTimes Database column remain_times SqlType(INT), Default(None)
   *  @param startTime Database column start_time SqlType(DATETIME), Default(None)
   *  @param endTime Database column end_time SqlType(DATETIME), Default(None)
   *  @param createTime Database column create_time SqlType(DATETIME)
   *  @param active Database column active SqlType(INT) */
  case class UserRow(id: Int, name: String, fullName: String, password: String, unit: String, email: String, phone: String, remainTimes: Option[Int] = None, startTime: Option[DateTime] = None, endTime: Option[DateTime] = None, createTime: DateTime, active: Int)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Int]], e3: GR[Option[DateTime]], e4: GR[DateTime]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<?[Int], <<?[DateTime], <<?[DateTime], <<[DateTime], <<[Int]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends profile.api.Table[UserRow](_tableTag, Some("live_forest"), "user") {
    def * = (id, name, fullName, password, unit, email, phone, remainTimes, startTime, endTime, createTime, active) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(fullName), Rep.Some(password), Rep.Some(unit), Rep.Some(email), Rep.Some(phone), remainTimes, startTime, endTime, Rep.Some(createTime), Rep.Some(active)).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8, _9, _10, _11.get, _12.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column full_name SqlType(VARCHAR), Length(255,true) */
    val fullName: Rep[String] = column[String]("full_name", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
    /** Database column unit SqlType(VARCHAR), Length(255,true) */
    val unit: Rep[String] = column[String]("unit", O.Length(255,varying=true))
    /** Database column email SqlType(VARCHAR), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column phone SqlType(VARCHAR), Length(255,true) */
    val phone: Rep[String] = column[String]("phone", O.Length(255,varying=true))
    /** Database column remain_times SqlType(INT), Default(None) */
    val remainTimes: Rep[Option[Int]] = column[Option[Int]]("remain_times", O.Default(None))
    /** Database column start_time SqlType(DATETIME), Default(None) */
    val startTime: Rep[Option[DateTime]] = column[Option[DateTime]]("start_time", O.Default(None))
    /** Database column end_time SqlType(DATETIME), Default(None) */
    val endTime: Rep[Option[DateTime]] = column[Option[DateTime]]("end_time", O.Default(None))
    /** Database column create_time SqlType(DATETIME) */
    val createTime: Rep[DateTime] = column[DateTime]("create_time")
    /** Database column active SqlType(INT) */
    val active: Rep[Int] = column[Int]("active")

    /** Uniqueness Index over (name) (database name name_uniq) */
    val index1 = index("name_uniq", name, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}
