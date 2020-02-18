package dao

import javax.inject.Inject
import models.Tables._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import tool.UserData

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by yz on 2018/7/17
  */
class UserDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def selectAll: Future[Seq[UserRow]] = db.run(User.result)

  def selectByName(name: String): Future[Option[UserRow]] = db.run(User.
    filter(_.name === name).result.headOption)

  def selectByNameOrEmail(name: String): Future[Option[UserRow]] = db.run(User.
    filter(x => x.name === name || x.email === name).result.headOption)

  def selectSomeByName(name: String): Future[UserRow] = db.run(User.
    filter(_.name === name).result.head)

  def selectByEmail(email: String): Future[Option[UserRow]] = db.run(User.
    filter(_.email === email).result.headOption)

  def selectByPhone(phone: String): Future[Option[UserRow]] = db.run(User.
    filter(_.phone === phone).result.headOption)

  def selectSomeByEmail(email: String): Future[UserRow] = db.run(User.
    filter(_.email === email).result.head)

  def selectByUserData(user: UserData): Future[Option[UserRow]] = db.run(User.
    filter(x => x.name === user.name || x.email === user.name || x.phone === user.name).
    filter(_.password === user.password).result.headOption)

  def insert(row: UserRow): Future[Unit] = db.run(User += row).map(_ => ())

  def insertAndReturning(row: UserRow) = db.run(User.returning(User.map(_.id)) += row)

  def deleteById(id: Int): Future[Unit] = db.run(User.filter(_.id === id).delete).map(_ => ())

  def selectById(id: Int): Future[UserRow] = db.run(User.
    filter(_.id === id).result.head)

  def update(row: UserData): Future[Unit] = db.run(User.filter(_.name === row.name).map(_.password).update(row.password)).
    map(_ => ())

  def update(row: UserRow): Future[Unit] = db.run(User.filter(_.id === row.id).
    update(row)).map(_ => ())

  //  def update1(row: UserRow): Future[Unit] = db.run(User.filter{x=>
  //    val y=x.email.like("%15%")
  //  }

}
