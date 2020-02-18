package dao

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits._
import models.Tables._

import scala.concurrent.Future

/**
  * Created by yz on 2018/5/29
  */
class ModeDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def select = db.run(Mode.filter(_.id === 1).result.head)


}
