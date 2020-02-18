package dao

import javax.inject.Inject
import models.Tables.Mission
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits._
import models.Tables._

import scala.concurrent.Future

/**
  * Created by yz on 2019/1/16
  */
class TrashDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def insert(row: TrashRow): Future[Unit] = db.run(Trash += row).map(_ => ())

  def insertAll(rows: Seq[TrashRow]): Future[Unit] = db.run(Trash ++= rows).map(_ => ())

  def deleteByIds(ids: Seq[Int]): Future[Unit] = db.run(Trash.filter(_.id.inSetBind(ids)).map(_.exist).
    update(false)).map(_ => ())

  def deleteById(id: Int): Future[Unit] = db.run(Trash.filter(_.id === id).map(_.exist).
    update(false)).map(_ => ())

  def selectAll(userId: Int): Future[Seq[Int]] = db.run(Trash.filter(_.userId === userId).filter(_.exist).map(_.id).result)

  def selectAll: Future[Seq[TrashRow]] = db.run(Trash.result)


}
