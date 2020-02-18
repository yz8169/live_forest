package controllers

import java.net.{URLDecoder, URLEncoder}

import dao._
import javax.inject.Inject
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import tool._
import models.Tables._
import utils.{DesEncrypter, Utils}
import utils.Utils.{Info, Sender}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2018/8/20
  */
class AppController @Inject()(cc: ControllerComponents, formTool: FormTool, accountDao: AccountDao,
                              userDao: UserDao, tool: Tool, pdfInfoDao: PdfInfoDao) extends AbstractController(cc) {

  def loginBefore = Action { implicit request =>
    Ok(views.html.login())
  }

  def newPasswordActive = Action.async { implicit request =>
    val data = formTool.userNameForm.bindFromRequest().get
    userDao.selectByNameOrEmail(data.name).map(_.get).map { x =>
      Ok(views.html.newPasswordActive(x.email))
    }

  }

  def login = Action.async { implicit request =>
    val data = formTool.userForm.bindFromRequest().get
    accountDao.selectById1.zip(userDao.selectByUserData(data)).map { case (account, optionUser) =>
      if (data.name == account.account && data.password == account.password) {
        Redirect(routes.AdminController.limitManageBefore()).addingToSession("admin" -> data.name)
      } else if (optionUser.isDefined) {
        val user = optionUser.get
        if (user.active == 0) {
          val href = routes.AppController.registerActiveBefore() + s"?email=${user.email}"
          Redirect(routes.AppController.loginBefore()).flashing("info" -> s"账户未激活，请前往 <a href='${href}' target='_blank'>激活</a> !", "class" -> Utils.errorClass)
        } else {
          Redirect(routes.UserController.toHome()).addingToSession("user" -> user.name, "id" -> user.id.toString)
        }

      } else {
        Redirect(routes.AppController.loginBefore()).flashing("info" -> "用户名或密码错误!", "class" -> Utils.errorClass)
      }
    }
  }

  def registerBefore = Action { implicit request =>
    Ok(views.html.register())
  }

  def recoverPasswordBefore = Action { implicit request =>
    Ok(views.html.recoverPassword())
  }

  def userNameCheck = Action.async { implicit request =>
    val data = formTool.userNameForm.bindFromRequest.get
    userDao.selectByName(data.name).zip(accountDao.selectById1).map { case (optionUser, admin) =>
      optionUser match {
        case Some(y) => Ok(Json.obj("valid" -> false))
        case None =>
          val valid = if (data.name == admin.account) false else true
          Ok(Json.obj("valid" -> valid))
      }
    }
  }

  def emailCheck = Action.async { implicit request =>
    val data = formTool.emailForm.bindFromRequest.get
    userDao.selectByEmail(data.email).zip(accountDao.selectById1).map { case (optionUser, admin) =>
      optionUser match {
        case Some(y) => Ok(Json.obj("valid" -> false))
        case None =>
          val valid = if (data.email == admin.email) false else true
          Ok(Json.obj("valid" -> valid))
      }
    }
  }

  def phoneCheck = Action.async { implicit request =>
    val data = formTool.phoneForm.bindFromRequest.get
    userDao.selectByPhone(data.phone).zip(accountDao.selectById1).map { case (optionUser, admin) =>
      optionUser match {
        case Some(y) => Ok(Json.obj("valid" -> false))
        case None =>
          Ok(Json.obj("valid" -> true))
      }
    }
  }

  def register = Action.async { implicit request =>
    val data = formTool.registerUserForm.bindFromRequest().get
    val row = UserRow(0, data.name, data.fullName, data.password, data.unit, data.email,
      data.phone, Some(0), None, None, new DateTime(), 0)
    userDao.insertAndReturning(row).flatMap { userId =>
      val pdfInfoRow = PdfInfoRow(userId, "深圳绘云医学检验实验室", "", "", "", "")
      pdfInfoDao.insert(pdfInfoRow).map { x =>
        Redirect(s"${routes.AppController.registerActiveBefore()}?email=${data.email}")
      }
    }
  }

  def registerActiveBefore = Action { implicit request =>
    val data = formTool.emailForm.bindFromRequest().get
    Ok(views.html.registerActive(data.email))
  }

  def sendEmail = Action.async { implicit request =>
    val data = formTool.emailForm.bindFromRequest().get
    userDao.selectSomeByEmail(data.email).map { x =>
      Future {
        val codeEmail = tool.registerDesEncrypter.encrypt(data.email)
        val href = s"http://${request.host}${routes.AppController.registerActive()}?email=${Utils.urlEncode(codeEmail)}"
        val content =
          s"""
             |感谢您的注册！您需要验证您的电子邮件以后才能正常使用账户：<br>
             |----------------------------------<br>
             |用户名：${x.name}<br>
             |----------------------------------<br>
             |点击链接激活您的账户<br>
             |<a href="${href}">${href}</a><br>
             |激活成功后，请等待管理员授权后使用！<br>
             |如果您无法直接访问此链接，请复制粘贴此链接到浏览器地址栏进行访问！<br>
             |如果不是本人操作，请忽略此邮件。<br>
             |<br>
             |Regards,<br>
             |绘云生物科技
       """.stripMargin
        val info = Info("LiveForest账户激活通知", content)
        val inbox = data.email
        Utils.sendEmailBySsl(Utils.sender, info, inbox)
      }
      Ok("success!")
    }

  }

  def newPasswordBefore = Action.async { implicit request =>
    val data = formTool.emailForm.bindFromRequest().get
    val email = tool.newPasswordDesEncrypter.decrypt(data.email)
    userDao.selectSomeByEmail(email).map { x =>
      Ok(views.html.newPassword(x.name))

    }
  }

  def newPassword = Action.async { implicit request =>
    val data = formTool.newPasswordForm.bindFromRequest().get
    userDao.selectSomeByName(data.name).flatMap { x =>
      val row = x.copy(password = data.newPassword)
      userDao.update(row).map { y =>
        Redirect(routes.AppController.loginBefore()).flashing("info" -> s"${data.name}重置密码成功!", "class" -> Utils.successClass).removingFromSession("user")
      }
    }
  }

  def newPasswordEmailValid = Action.async { implicit request =>
    val data = formTool.emailForm.bindFromRequest().get
    userDao.selectSomeByEmail(data.email).map { x =>
      Future {
        val codeEmail = tool.newPasswordDesEncrypter.encrypt(data.email)
        val href = s"http://${request.host}${routes.AppController.newPasswordBefore()}?email=${Utils.urlEncode(codeEmail)}"
        val content =
          s"""
             |下面是您的原用户名信息，点击下面的链接重置密码：<br>
             |----------------------------------<br>
             |用户名：${x.name}<br>
             |----------------------------------<br>
             |点击链接重置修改密码。<br>
             |<a href="${href}">${href}</a><br>
             |如果您无法直接访问此链接，请复制粘贴此链接到浏览器地址栏进行访问！<br>
             |如果不是本人操作，请忽略此邮件。<br>
             |<br>
             |Regards,<br>
             |绘云生物科技
       """.stripMargin
        val info = Info("LiveForest找回密码通知", content)
        val inbox = data.email
        Utils.sendEmailBySsl(Utils.sender, info, inbox)
      }
      Ok("success!")
    }

  }

  def registerActive = Action.async { implicit request =>
    val data = formTool.emailForm.bindFromRequest().get
    val email = tool.registerDesEncrypter.decrypt(data.email)
    userDao.selectSomeByEmail(email).flatMap { x =>
      if (x.active == 1) {
        Utils.result2Future(Ok(views.html.activeSuccess(false)))
      } else {
        val row = x.copy(active = 1)
        userDao.update(row).map { y =>
          val data = row
          Future {
            val content =
              s"""
                 |有新用户注册成功！该用户信息如下：<br>
                 |用户名：${data.name}<br>
                 |姓名: ${data.fullName}<br>
                 |单位: ${data.unit}<br>
                 |邮箱: ${data.email}<br>
                 |手机: ${data.phone}
       """.stripMargin
            val info = Info("LiveForest新用户注册成功通知", content)
            val account = Utils.execFuture(accountDao.selectById1)
            val inbox = account.email
            Utils.sendEmailBySsl(Utils.sender, info, inbox)
          }
          Ok(views.html.activeSuccess(true))
        }
      }
    }

  }

  def validName = Action.async { implicit request =>
    val data = formTool.userNameForm.bindFromRequest().get
    userDao.selectByNameOrEmail(data.name).map { x =>
      val valid = if (x.isDefined) true else false
      Ok(Json.toJson(valid))
    }
  }


}
