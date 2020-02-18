package dao

import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.Tables._
import org.joda.time.DateTime


/**
  * Created by yz on 2018/12/27
  */
class MissionDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends
  HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  import com.github.tototoshi.slick.MySQLJodaSupport._

  def insert(row: MissionRow): Future[Unit] = db.run(Mission += row).map(_ => ())

  def insertAndRetunId(row: MissionRow): Future[Int] = db.run(Mission returning Mission.map(_.id) += row)

  def selectAll(userId: Int): Future[Seq[MissionRow]] = db.run(Mission.filter(_.userId === userId).sortBy(_.endTime.desc).result)

  def selectAll: Future[Seq[MissionRow]] = db.run(Mission.result)

  def selectByMissionId(missionId: Int): Future[MissionRow] = db.run(Mission.filter(_.id === missionId).result.head)

  def selectByMissionIds(missionIds: Seq[Int]): Future[Seq[MissionRow]] = db.run(Mission.
    filter(_.id.inSetBind(missionIds)).sortBy(_.endTime.desc).result)

  def deleteById(id: Int): Future[Unit] = db.run(Mission.filter(_.id === id).delete).map(_ => ())

  def deleteByIds(ids: Seq[Int]): Future[Unit] = db.run(Mission.filter(_.id.inSetBind(ids)).delete).map(_ => ())


}
