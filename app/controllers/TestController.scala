package controllers

import dao._
import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.Utils

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import models.Tables._


/**
  * Created by yz on 2019/1/2
  */
class TestController @Inject()(cc: ControllerComponents, missionDao: MissionDao, trashDao: TrashDao,
                               userDao: UserDao, pdfInfoDao: PdfInfoDao) extends AbstractController(cc) {

  def refreshTrash = Action.async { implicit request =>
    missionDao.selectAll.zip(trashDao.selectAll).flatMap { case (x, trashs) =>
      val rows = x.map(y => TrashRow(y.id, y.userId, true)).filterNot(y => trashs.map(_.id).contains(y.id))
      trashDao.insertAll(rows)
    }.map { x =>
      Ok("success!")
    }
  }

  def refreshPdfInfo = Action.async { implicit request =>
    userDao.selectAll.zip(pdfInfoDao.selectAll).flatMap { case (users, pdfInfos) =>
      val rows = users.map(user => PdfInfoRow(user.id, "深圳绘云医学检验实验室", "", "", "", "")).
        filterNot(pdfInfo => pdfInfos.map(_.userId).contains(pdfInfo.userId))
      pdfInfoDao.insertAll(rows)
    }.map { x =>
      Ok("success!")
    }
  }


  def test = Action.async {
    val buffer = (1 to 8).toBuffer
    buffer.foreach { i =>
      missionDao.selectAll(25).map { x =>
        println(s"${i} before")
        Thread.sleep(5000)
        println(s"${i} after")
      }.map { x =>
        Future {
          println(s"future ${i} before")

          println(s"future ${i} after")
        }
      }
    }
    Utils.result2Future(Ok("success!"))
  }


}
