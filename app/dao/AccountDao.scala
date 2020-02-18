package dao

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import models.Tables._
import scala.concurrent.ExecutionContext.Implicits.global

class AccountDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def selectById1: Future[AccountRow] = db.run(Account.filter(_.id === 1).result.head)

  def update(row: AccountRow): Future[Unit] = {
    db.run(Account.filter(_.id === row.id).update(row)).map(_ => ())
  }

  def updatePassword(password: String): Future[Unit] = {
    db.run(Account.filter(_.id === 1).map(_.password).update(password)).map(_ => ())
  }

  def updateEmail(email: String): Future[Unit] = {
    db.run(Account.filter(_.id === 1).map(_.email).update(email)).map(_ => ())
  }


}
