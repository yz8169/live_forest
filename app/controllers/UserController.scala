package controllers

import javax.inject.Inject
import play.api.mvc._
import tool._
import dao._
import models.Tables.PdfInfoRow
import org.joda.time.DateTime
import play.api.libs.json.Json
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
  * Created by yz on 2018/8/20
  */
class UserController @Inject()(cc: ControllerComponents, formTool: FormTool, userDao: UserDao,
                               accountDao: AccountDao, tool: Tool, pdfInfoDao: PdfInfoDao) extends AbstractController(cc) {

  def toIndex = Action.async { implicit request =>
    val name = tool.getUserName
    userDao.selectSomeByName(name).map { user =>
      val fb = tool.isb(user)
      Ok(views.html.user.index(fb))
    }

  }

  def toHome = Action { implicit request =>
    Ok(views.html.user.home())
  }

  def reduceTimes = Action.async { implicit request =>
    val name = tool.getUserName
    userDao.selectSomeByName(name).flatMap { x =>
      val times = x.remainTimes.map { times =>
        if (times <= 0) 0 else times - 1
      }
      val row = x.copy(remainTimes = times)
      userDao.update(row)
    }.map { x =>
      Ok("success")
    }
  }

  def logout = Action { implicit request =>
    Redirect(routes.AppController.loginBefore()).flashing("info" -> "退出登录成功!", "class" -> Utils.successClass).removingFromSession("user")
  }

  def changePasswordBefore = Action { implicit request =>
    Ok(views.html.user.changePassword())
  }

  def changePassword = Action.async { implicit request =>
    val data = formTool.changePasswordForm.bindFromRequest().get
    val name = request.session.get("user").get
    userDao.selectByName(name).flatMap { x =>
      val dbUser = x.get
      if (data.password == dbUser.password) {
        val row = UserData(name, data.newPassword)
        userDao.update(row).map { y =>
          Redirect(routes.AppController.loginBefore()).flashing("info" -> "密码修改成功!", "class" -> Utils.successClass).removingFromSession("user")
        }
      } else {
        Future.successful(Redirect(routes.UserController.changePasswordBefore()).flashing("info" -> "用户名或密码错误!", "class" -> Utils.errorClass))
      }
    }

  }

  def forestBefore = Action.async { implicit request =>
    val name = tool.getUserName
    userDao.selectSomeByName(name).map { user =>
      val b = tool.isb(user)
      Ok(views.html.user.forest(b))
    }
  }

  def getLimit = Action.async { implicit request =>
    val name = tool.getUserName
    userDao.selectSomeByName(name).map { user =>
      val b = tool.isb(user)
      Ok(Json.toJson(b))
    }
  }

  def detailInfoBefore = Action.async { implicit request =>
    val id = tool.getUserId
    userDao.selectById(id).map { x =>
      Ok(views.html.user.detailInfo(x))
    }

  }

  def getPdfInfo = Action.async { implicit request =>
    val id = tool.getUserId
    pdfInfoDao.selectById(id).map { x =>
      val json = Utils.getJsonByT(x)
      Ok(json)
    }

  }

  def updatePdfInfo = Action.async { implicit request =>
    val id = tool.getUserId
    val data = formTool.pdfInfoForm.bindFromRequest().get
    val pdfInfo = PdfInfoRow(id, data.title, data.unit, data.address, data.reporter, data.checker)
    pdfInfoDao.update(pdfInfo).map { x =>
      Ok(Json.toJson("success!"))
    }

  }


}
