package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2019/1/17
  */
class PdfInfoDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def deleteById(id: Int): Future[Unit] = db.run(PdfInfo.filter(_.userId === id).delete).map(_ => ())

  def insert(row: PdfInfoRow): Future[Unit] = db.run(PdfInfo += row).map(_ => ())

  def selectAll = db.run(PdfInfo.result)

  def insertAll(rows: Seq[PdfInfoRow]) = db.run(PdfInfo ++= rows).map(_ => ())

  def selectById(id: Int) = db.run(PdfInfo.
    filter(_.userId === id).result.head)

  def update(row: PdfInfoRow) = db.run(PdfInfo.filter(_.userId === row.userId).
    update(row)).map(_ => ())





}
