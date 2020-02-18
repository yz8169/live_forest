package tool

import java.io.File
import java.nio.file.Files

import dao.ModeDao
import javax.inject.Inject
import models.Tables.UserRow
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import play.api.mvc.RequestHeader
import utils.{DesEncrypter, Utils}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import models.Tables._
import play.api.libs.json.Json

import scala.math.BigDecimal.RoundingMode

/**
  * Created by yz on 2018/8/23
  */
class Tool @Inject()(modeDao: ModeDao) {

  def getUserName(implicit request: RequestHeader) = {
    request.session.get("user").get
  }

  def getTimes(times: Option[Int]) = times.map(_.toString).getOrElse("不限")

  def dataTime2String(dateTime: Option[DateTime]) = dateTime.map(_.toString("yyyy-MM-dd HH:mm:ss")).getOrElse("不限")

  def isb(user: UserRow) = user.remainTimes.map(_ > 0).getOrElse(true) && user.startTime.map(_.isBeforeNow).getOrElse(true) &&
    user.endTime.map(_.isAfterNow).getOrElse(true)

  val newPasswordDesEncrypter = new DesEncrypter("newPassword12345")

  val registerDesEncrypter = new DesEncrypter("register12345678")

  def createTempDirectory(prefix: String) = {
    if (isTestMode) Utils.testDir else Files.createTempDirectory(prefix).toFile
  }

  def isTestMode = {
    val mode = Utils.execFuture(modeDao.select)
    if (mode.test == "t") true else false
  }

  def deleteDirectory(direcotry: File) = {
    if (!isTestMode) Utils.deleteDirectory(direcotry)
  }

  def getPredictValue(file: File): Map[String, String] = {
    val lines = Utils.file2Lines(file)
    getPredictValue(lines)
  }

  def getPredictValue(lines: mutable.Buffer[String]): Map[String, String] = {
    val headers = lines.head.split("\t")
    headers.zip(lines(1).split("\t")).toMap
  }

  def getResultData(file: File) = {
    val map = getPredictValue(file)
    ResultData(BigDecimal(map("Case")), BigDecimal(map("Control")), BigDecimal(map("Fibrosis")), BigDecimal(map("Cirrhosis")),
      BigDecimal(map("Early")), BigDecimal(map("Late")))
  }

  def getExtraData(file: File) = {
    val map = getPredictValue(file)
    ExtraDataF(getValue(map, "送检单位"), getValue(map, "地址"), getValue(map, "姓名"), getValue(map, "性别"),
      getValue(map, "送检科室"), getValue(map, "申请医生"), getValue(map, "门诊/住院号"), getValue(map, "采样时间"),
      getValue(map, "送检时间"), getValue(map, "样本类型"), getValue(map, "样本状态"),
      getValue(map, "标题"), getValue(map, "报告者"), getValue(map, "审核者"), getValue(map, "检验日期"),
      getValue(map, "报告日期")
    )
  }

  def getValue(map: Map[String, String], header: String) = {
    val str = map.getOrElse(header, "NA")
    if (str == "NA") "" else str
  }


  def getResultData(lines: mutable.Buffer[String]) = {
    val map = getPredictValue(lines)
    ResultData(map("Case").toDouble, map("Control").toDouble, map("Fibrosis").toDouble, map("Cirrhosis").toDouble,
      map("Early").toDouble, map("Late").toDouble)
  }

  def getOrinalData(file: File) = {
    val map = getPredictValue(file)
    OrignalData(map("SampleID"), map("Age"), map("AST"), map("ALT"),
      map("PLT"), map("Tyr"), map("TCA"))
  }

  def getOrinalData(lines: mutable.Buffer[String]) = {
    val map = getPredictValue(lines)
    OrignalData(map("SampleID"), map("Age"), map("AST"), map("ALT"),
      map("PLT"), map("Tyr"), map("TCA"))
  }

  def getShow(resultData: ResultData) = {
    var show = "1"
    if (resultData.caseDouble > 0.5) {
      show = "2"
      if (resultData.cirrhosis <= 0.5) {
        show = "3"
      }
    }
    show
  }

  def getResultInfo(resultData: ResultData, imageInfo: ImageInfo): ResultInfo = {
    var p1Str = s"非慢性肝病预测概率为：${scale100(resultData.control)}%;慢性肝病预测概率为：${scale100(resultData.caseDouble)}%。"
    var p2Str = s"肝纤维化预测概率为：${scale100(resultData.fibrosis)}%;肝硬化预测概率为：${scale100(resultData.cirrhosis)}%。"
    var p3Str = s"早期肝纤维化预测概率为：${scale100(resultData.early)}%;晚期肝纤维化预测概率为：${scale100(resultData.late)}%。"
    val show = getShow(resultData)
    val (base64, pStr) = show match {
      case "1" => (imageInfo.base641, p1Str)
      case "2" => (imageInfo.base642, p2Str)
      case "3" => (imageInfo.base643, p3Str)
    }
    ResultInfo(pStr, base64)
  }

  def scale100(x: BigDecimal) = {
    val scale = x.scale - 2
    val finalScale = if (scale >= 0) scale else 0
    (x * 100).setScale(finalScale, RoundingMode.HALF_UP)
  }

  def getFrontResultInfo(resultData: ResultData, divData: DivData) = {
    var p1Str = s"非慢性肝病预测概率为：${scale100(resultData.control)}%，" +
      s"慢性肝病预测概率为：${scale100(resultData.caseDouble)}%，慢性肝病风险较低。"
    var p2Str = s"肝纤维化预测概率为：${scale100(resultData.fibrosis)}%，" +
      s"肝硬化预测概率为：${scale100(resultData.cirrhosis)}%，肝硬化风险较高。"
    var p3Str = s"早期肝纤维化预测概率为：${scale100(resultData.early)}%，" +
      s"晚期肝纤维化预测概率为：${scale100(resultData.late)}%，"
    if (resultData.early > 0.5) {
      p3Str += "具有早期肝纤维化的风险。"
    } else {
      p3Str += "晚期肝纤维化风险较高。"
    }
    var show = getShow(resultData)
    show match {
      case "1" => (divData.div1, p1Str)
      case "2" => (divData.div2, p2Str)
      case "3" => (divData.div3, p3Str)
    }
  }

  def getDanger(resultData: ResultData) = {
    var p1Str = s"慢性肝病风险较低。"
    var p2Str = s"肝硬化风险较高。"
    var p3Str = s""
    if (resultData.early > 0.5) {
      p3Str += "具有早期肝纤维化的风险。"
    } else {
      p3Str += "晚期肝纤维化风险较高。"
    }
    var show = getShow(resultData)
    val pStr = show match {
      case "1" => p1Str
      case "2" => p2Str
      case "3" => p3Str
    }
    pStr + "\n本结果仅供参考，肝纤维化、肝硬化程度请结合临床。"
  }

  def getMissionJson(row: MissionRow, resultData: ResultData) = {
    val missionMap = Utils.getMapByT(row) + ("danger" -> getDanger(resultData))
    Json.toJson(missionMap)
  }

  def getResultInfo(file: File, imageInfo: ImageInfo): ResultInfo = {
    val resultData = getResultData(file)
    getResultInfo(resultData, imageInfo)
  }

  def getUserId(implicit request: RequestHeader) = {
    request.session.get("id").get.toInt
  }

  def getUserIdDir(userId: Int) = {
    new File(Utils.userDir, userId.toString)
  }

  def getUserMissionDir(implicit request: RequestHeader) = {
    val userIdDir = getUserIdDir
    new File(userIdDir, "mission")
  }

  def getMissionIdDirById(missionId: Int)(implicit request: RequestHeader) = {
    val userMissionFile = getUserMissionDir
    new File(userMissionFile, missionId.toString)
  }

  def getUserIdDir(implicit request: RequestHeader) = {
    val userId = getUserId
    new File(Utils.userDir, userId.toString)
  }

  case class MyMessage(valid: Boolean, message: String)

  def fileCheck(file: File): MyMessage = {
    val lines = Utils.xlsx2Lines(file)
    val headers =lines.head.split("\t")
    if (StringUtils.isBlank(lines.head)) {
      return MyMessage(false, s"首行数据（表头）缺失!")
    }
    val repeatHeaders = headers.diff(headers.distinct)
    if (repeatHeaders.nonEmpty) {
      return MyMessage(false, s"文件表头 ${repeatHeaders.head} 重复!")
    }
    val hasHeaders = ArrayBuffer("Age", "AST", "ALT", "PLT", "Tyr", "TCA", "SampleID")
    val noExistHeaders = hasHeaders.diff(headers)
    if (noExistHeaders.nonEmpty) {
      return MyMessage(false, s"文件表头 ${noExistHeaders.head} 不存在!")
    }
    lines.drop(1).zipWithIndex.foreach { case (line, i) =>
      val columns = line.split("\t")
      val lineMap = headers.zip(columns).toMap
      if (columns.size > headers.size) {
        return MyMessage(false, s"文件第${i + 2}行列数不正确,存在多余列!")
      }
      columns.zipWithIndex.foreach { case (tmpColumn, j) =>
        val column = tmpColumn.toLowerCase()
        val header = headers(j)
        val doubleColumns = ArrayBuffer("Age", "AST", "ALT", "PLT", "Tyr", "TCA")
        if (doubleColumns.contains(header)) {
          if (!Utils.isDouble(column)) {
            return MyMessage(false, s"文件第${i + 2}行第${j + 1}列必须为实数!")
          }
        }
        val notEmpltyColumns = ArrayBuffer("SampleID", "Age", "AST", "ALT", "PLT", "Tyr", "TCA")
        if (notEmpltyColumns.contains(header)) {
          if (StringUtils.isEmpty(column)) {
            return MyMessage(false, s"文件第${i + 2}行第${j + 1}列为空!")
          }
        }

      }
    }
    MyMessage(true, "")
  }

  def getEmptyExtraDataRow(id: Int, userId: Int) = {
    ExtraDataRow(id, userId, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
  }

}
