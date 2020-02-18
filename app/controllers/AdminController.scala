package controllers

import java.io.File

import dao._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._
import utils.Utils

import scala.concurrent.ExecutionContext.Implicits.global
import tool._
import models.Tables._
import org.joda.time.format.DateTimeFormat
import utils.Utils.{Info, Sender}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

/**
  * Created by yz on 2018/8/20
  */
class AdminController @Inject()(cc: ControllerComponents, userDao: UserDao, formTool: FormTool,
                                accountDao: AccountDao, tool: Tool, missionDao: MissionDao,
                                extraDataDao: ExtraDataDao, trashDao: TrashDao,pdfInfoDao: PdfInfoDao) extends AbstractController(cc) {

  def limitManageBefore = Action { implicit request =>
    Ok(views.html.admin.limitManage())
  }

  def logout = Action { implicit request =>
    Redirect(routes.AppController.loginBefore()).flashing("info" -> "退出登录成功!", "class" -> Utils.successClass).removingFromSession("admin")
  }

  def getAllUser = Action.async { implicit request =>
    userDao.selectAll.map { x =>
      val array = Utils.getArrayByTs(x)
      Ok(Json.toJson(array))
    }
  }

  def addDays = Action.async { implicit request =>
    val data = formTool.addDaysForm.bindFromRequest().get
    userDao.selectByName(data.name).map(_.get).flatMap { x =>
      val row = x.copy(startTime = data.startTime, endTime = data.endTime, remainTimes = data.remainTimes)
      userDao.update(row).map { y =>
        val f = Future {
          val content =
            s"""
               |用户权限发生变动！请查看：<br>
               | 有效次数：${tool.getTimes(x.remainTimes)} --> ${tool.getTimes(data.remainTimes)}<br>
               | 有效期限:${tool.dataTime2String(x.startTime)} 至 ${tool.dataTime2String(x.endTime)} --> ${tool.dataTime2String(data.startTime)} 至 ${tool.dataTime2String(data.endTime)}<br>
               | Regards,<br>
               |绘云生物科技
         """.stripMargin
          val info = Info("LiveForest用户权限变动通知", content)
          val inbox = x.email
          Utils.sendEmailBySsl(Utils.sender, info, inbox)
        }
        f

      }
    }.map { x =>

      Redirect(routes.AdminController.getAllUser())
    }
  }

  def changePasswordBefore = Action { implicit request =>
    Ok(views.html.admin.changePassword())
  }

  def changePassword = Action.async { implicit request =>
    val data = formTool.changePasswordForm.bindFromRequest().get
    accountDao.selectById1.flatMap { x =>
      if (data.password == x.password) {
        accountDao.updatePassword(data.newPassword).map { y =>
          Redirect(routes.AppController.loginBefore()).flashing("info" -> "密码修改成功!", "class" -> Utils.successClass).removingFromSession("admin")
        }
      } else {
        Future.successful(Redirect(routes.AdminController.changePasswordBefore()).flashing("info" -> "用户名或密码错误!", "class" -> Utils.errorClass))
      }
    }
  }

  def getUserById = Action.async { implicit request =>
    val data = formTool.idForm.bindFromRequest().get
    userDao.selectById(data.id).map { x =>
      Ok(Utils.getJsonByT(x))
    }
  }

  def updateUser = Action.async { implicit request =>
    val data = formTool.userForm.bindFromRequest().get
    userDao.update(data).map { x =>
      Redirect(routes.AdminController.getAllUser())
    }
  }

  def deleteUserById = Action.async { implicit request =>
    val data = formTool.idForm.bindFromRequest().get
    userDao.deleteById(data.id).zip(pdfInfoDao.deleteById(data.id)).map { x =>
      Redirect(routes.AdminController.getAllUser())
    }
  }

  def changeEmailBefore = Action { implicit request =>
    Ok(views.html.admin.changeEmail())
  }

  def changeEmail = Action.async { implicit request =>
    val data = formTool.changeEmailForm.bindFromRequest().get
    accountDao.selectById1.flatMap { x =>
      if (data.password == x.password) {
        accountDao.updateEmail(data.email).map { y =>
          Redirect(routes.AdminController.changeEmailBefore()).flashing("info" -> "邮箱修改成功!", "class" -> Utils.successClass)
        }
      } else {
        Future.successful(Redirect(routes.AdminController.changeEmailBefore()).flashing("info" -> "密码错误!", "class" -> Utils.errorClass))
      }
    }
  }

  def detailInfoBefore = Action.async { implicit request =>
    accountDao.selectById1.map { x =>
      Ok(views.html.admin.detailInfo(x))
    }

  }

  def downloadDb = Action.async { implicit request =>
    missionDao.selectAll.zip(extraDataDao.selectAll).zip(userDao.selectAll).
      zip(trashDao.selectAll).map { case (((missionRows, extraRows), users), trashs) =>
      val extraMap = extraRows.map(x => (x.id, x)).toMap
      val userMap = users.map(x => (x.id, x.fullName)).toMap
      val trashMap = trashs.map { x =>
        val isDelete = if (x.exist) "否" else "是"
        (x.id, isDelete)
      }.toMap
      val lines = ArrayBuffer[ArrayBuffer[String]]()
      val headers = ArrayBuffer("用户", "样品号", "姓名", "年龄", "AST", "ALT", "PLT", "Tyr", "TCA", "结果", "时间", "是否删除",
        "送检单位", "地址", "性别", "送检科室", "申请医生", "门诊/住院号", "采样时间", "送检时间",
         "样本类型", "样本状态", "标题", "报告者", "审核者", "检验日期", "报告日期")
      lines += headers ++= missionRows.map { x =>
        val userName = userMap.getOrElse(x.userId, "已删")
        val isDelete = trashMap(x.id)
        val extra = extraMap.getOrElse(x.id, tool.getEmptyExtraDataRow(x.id, x.userId))
        ArrayBuffer(userName, extra.sampleId, extra.name, x.age, x.ast, x.alt, x.plt, x.tyr, x.tca, x.result, Utils.dataTime2String(x.endTime), isDelete,
          extra.unit, extra.address, extra.sex, extra.office, extra.doctor, extra.number, extra.sampleTime, extra.submitTime,
           extra.sampleType, extra.sampleStatus, extra.title, extra.reporter, extra.checker, extra.submitTime,
          extra.reportDate)
      }.sortBy(_ (0))
      val tmpDir = tool.createTempDirectory("tmpDir")
      val file = new File(tmpDir, "db.txt")
      Utils.lines2File(file, lines.map(_.mkString("\t")))
      Ok.sendFile(file, onClose = () => {
        Utils.deleteDirectory(tmpDir)
      }).withHeaders(
        CONTENT_DISPOSITION -> s"attachment; filename=${
          file.getName
        }",
        CONTENT_TYPE -> "application/x-download"
      )

    }
  }

}
