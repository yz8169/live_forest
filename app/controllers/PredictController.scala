package controllers

import java.io._
import java.net.URL

import dao._
import io.github.cloudify.scala.spdf.{Landscape, Pdf, PdfConfig, Portrait}
import javax.inject.Inject
import models.Tables.MissionRow
import org.apache.commons.io.FileUtils
import org.joda.time.DateTime
import org.jxls.common.Context
import org.jxls.util.JxlsHelper
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import tool.{DivData, FormTool, ImageInfo, Tool}
import utils.Utils
import models.Tables._
import org.apache.commons.lang3.StringUtils

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yz on 2018/12/12
  */
class PredictController @Inject()(cc: ControllerComponents, formTool: FormTool, tool: Tool,
                                  missionDao: MissionDao, extraDataDao: ExtraDataDao,
                                  trashDao: TrashDao) extends AbstractController(cc) {

  def predictBefore = Action { implicit request =>
    println("17")
    Ok(views.html.user.predict())
  }

  def batchPredictBefore = Action { implicit request =>
    Ok(views.html.user.batchPredict())
  }

  def predict = Action.async { implicit request =>
    val data = formTool.predictForm.bindFromRequest().get
    val userId = tool.getUserId
    val tmpDir = tool.createTempDirectory("tmpDir")
    val inputFile = new File(tmpDir, "input.txt")
    val lines = ArrayBuffer(ArrayBuffer("SampleID", "Age", "AST", "ALT", "PLT", "Tyr", "TCA"))
    lines += ArrayBuffer(data.sampleId, data.age, data.ast, data.alt, data.plt, data.tyr, data.tca)
    Utils.lines2File(inputFile, lines.map(_.mkString("\t")))
    Utils.txt2Xlsx(inputFile, new File(tmpDir, "input.xlsx"))
    FileUtils.copyFileToDirectory(new File(Utils.dataDir, "LiveForest_load.RData"), tmpDir)
    val linuxCommand =
      s"""
         |dos2unix *
         |Rscript ${Utils.dosPath2Dos(Utils.rPath)}/predict.R
         |python ${Utils.dosPath2Dos(Utils.pyPath)}/pieChart1.py &
         |python ${Utils.dosPath2Dos(Utils.pyPath)}/pieChart2.py &
         |python ${Utils.dosPath2Dos(Utils.pyPath)}/pieChart3.py &
         |wait
       """.stripMargin
    val windowsCommand =
      s"""
         |Rscript ${Utils.dosPath2Dos(Utils.rPath)}/predict.R
         |python ${Utils.dosPath2Dos(Utils.pyPath)}/pieChart1.py |python ${Utils.dosPath2Dos(Utils.pyPath)}/pieChart2.py | python ${Utils.dosPath2Dos(Utils.pyPath)}/pieChart3.py
       """.stripMargin
    val command = if (Utils.isWindows) windowsCommand else linuxCommand
    val startTime = System.currentTimeMillis()
    val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(command))
    if (execCommand.isSuccess) {
      val div1Str = FileUtils.readFileToString(new File(tmpDir, "div1.txt")) + Utils.pyScript
      val div2Str = FileUtils.readFileToString(new File(tmpDir, "div2.txt")) + Utils.pyScript
      val div3Str = FileUtils.readFileToString(new File(tmpDir, "div3.txt")) + Utils.pyScript
      val outFile = new File(tmpDir, "out.txt")
      val map = tool.getPredictValue(outFile)
      val time=new DateTime()
      val row = MissionRow(0, userId, data.age, data.ast, data.alt, data.plt, data.tyr,
        data.tca, map("results"), time)
      missionDao.insertAndRetunId(row).flatMap { missionId =>
        val extraRow = tool.getEmptyExtraDataRow(missionId, userId).copy(sampleId = data.sampleId, name = data.name,
          checkDate = Utils.dataTime2String(time))
        val trashRow = TrashRow(missionId, userId, true)
        extraDataDao.insertOrUpdate(extraRow).zip(trashDao.insert(trashRow)).map { x =>
          val missionIdDir = tool.getMissionIdDirById(missionId)
          val outFile = new File(tmpDir, "out.txt")
          FileUtils.copyFileToDirectory(outFile, missionIdDir)
          FileUtils.copyFileToDirectory(new File(tmpDir, "div1.txt"), missionIdDir)
          FileUtils.copyFileToDirectory(new File(tmpDir, "div2.txt"), missionIdDir)
          FileUtils.copyFileToDirectory(new File(tmpDir, "div3.txt"), missionIdDir)
          val resultData = tool.getResultData(outFile)
          tool.deleteDirectory(tmpDir)
          val extraDataJson = Utils.getJsonByT(extraRow)
          val missionJson = tool.getMissionJson(row.copy(id = missionId),resultData)
          val divData = DivData(div1Str, div2Str, div3Str)
          val (divStr, pStr) = tool.getFrontResultInfo(resultData, divData)
          val json = Json.obj("div" -> divStr, "pStr" -> pStr, "case" -> resultData.caseDouble,
            "control" -> resultData.control, "fibrosis" -> resultData.fibrosis, "cirrhosis" -> resultData.cirrhosis,
            "early" -> resultData.early, "late" -> resultData.late, "extraData" -> extraDataJson,
            "mission" -> missionJson)
          Ok(json)
        }

      }
    } else {
      tool.deleteDirectory(tmpDir)
      Utils.result2Future(Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr)))
    }
  }

  def batchPredict = Action.async(parse.multipartFormData) { implicit request =>
    val tmpDir = tool.createTempDirectory("tmpDir")
    val inputFile = new File(tmpDir, "input.xlsx")
    val file = request.body.file("file").get
    file.ref.moveTo(inputFile, replace = true)
    val userId = tool.getUserId
    val missionIds = ArrayBuffer[Int]()
    val time = new DateTime()
    FileUtils.copyFileToDirectory(new File(Utils.dataDir, "LiveForest_load.RData"), tmpDir)
    val command =
      s"""
         |Rscript ${(Utils.rPath)}/predict.R
       """.stripMargin
    val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(command))
    if (execCommand.isSuccess) {
      val outFile = new File(tmpDir, "out.txt")
      val outLines = Utils.file2Lines(outFile)
      val threadNum = 4
      val f = outLines.drop(1).map { line =>
        val newLines = ArrayBuffer(outLines.head, line)
        val map = tool.getPredictValue(newLines)
        val resultData = tool.getResultData(newLines)
        val orinalData = tool.getOrinalData(newLines)
        val row = MissionRow(0, userId, orinalData.age, orinalData.ast, orinalData.alt, orinalData.plt, orinalData.tyr,
          orinalData.tca, map("results"), time)
        val f = missionDao.insertAndRetunId(row).flatMap { missionId =>
          val missionIdDir = tool.getMissionIdDirById(missionId)
          val outFile = new File(missionIdDir, "out.txt")
          Utils.lines2File(outFile, newLines)
          val extraData = tool.getExtraData(outFile)
          val checkDate=if(StringUtils.isEmpty(extraData.checkDate)) Utils.dataTime2String(time) else extraData.checkDate
          val extraRow = tool.getEmptyExtraDataRow(missionId, userId).copy(sampleId = orinalData.sampleId, name = extraData.name,
            unit = extraData.unit, address = extraData.address, sex = extraData.sex, office = extraData.office,
            doctor = extraData.doctor, number = extraData.number, sampleTime = extraData.sampleTime, submitTime = extraData.submitTime,
            sampleType = extraData.sampleType, sampleStatus = extraData.sampleStatus,
            title = extraData.title, reporter = extraData.reporter, checker = extraData.checker, checkDate =checkDate,
            reportDate = extraData.reportDate
          )
          val trashRow = TrashRow(missionId, userId, true)
          extraDataDao.insertOrUpdate(extraRow).zip(trashDao.insert(trashRow)).map { x =>
            missionId
          }

        }
        val missionId = Utils.execFuture(f)
        missionIds += missionId
      }
      tool.deleteDirectory(tmpDir)
      missionDao.selectByMissionIds(missionIds).zip(extraDataDao.selectByMissionIds(missionIds)).map { case (x, extraRows) =>
        val missionRows = x
        val extraMap = extraRows.map(x => (x.id, x)).toMap
        val t2s = missionRows.map(x => (x, extraMap.getOrElse(x.id, tool.getEmptyExtraDataRow(x.id, x.userId))))
        val array = Utils.getArrayByT2s(t2s)
        Ok(Json.toJson(array))
      }
    } else {
      tool.deleteDirectory(tmpDir)
      Utils.result2Future(Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr)))
    }

  }

  def downloadExampleFile = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.path, s"example/${data.fileName}")
    Ok.sendFile(file).withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> s"attachment; filename=${
        file.getName
      }",
      CONTENT_TYPE -> "application/x-download"
    )
  }

  def exportExcel = Action { implicit request =>
    val data = formTool.basicInfoForm.bindFromRequest().get
    val file = new File(Utils.templateDir, s"live_forest_template.xlsx")
    val tmpDir = tool.createTempDirectory("tmpDir")
    val outFile = new File(tmpDir, "live_forest_out.xlsx")
    val is: InputStream = new FileInputStream(file)
    val os: OutputStream = new FileOutputStream(outFile)
    val context = new Context()
    context.putVar("basicInfo", data)
    JxlsHelper.getInstance().processTemplate(is, os, context)
    is.close()
    os.close()
    Ok.sendFile(outFile, onClose = () => {
      tool.deleteDirectory(tmpDir)
    }).withHeaders(
      CONTENT_DISPOSITION -> s"attachment; filename=${
        outFile.getName
      }",
      CONTENT_TYPE -> "application/x-download"
    )
  }

  def pdfTest = Action { implicit request =>
    val tmpDir = tool.createTempDirectory("tmpDir")
    val startTime = System.currentTimeMillis()
    val outFile = new File(tmpDir, "live_boost_out.pdf")
    val pdf = Pdf(new PdfConfig {
      orientation := Portrait
      pageSize := "A4"
      pageSize := "Letter"
      marginTop := "1in"
      marginBottom := "1in"
      marginLeft := "0in"
      marginRight := "0in"
    })
    pdf.run(views.html.user.htmlTest().toString(), outFile)

    Ok("success!")
  }

  def export = Action.async { implicit request =>
    val data = formTool.basicInfoForm.bindFromRequest().get
    val missionId = formTool.missionIdForm.bindFromRequest().get.missionId
    val userId = tool.getUserId
    val tmpDir = tool.createTempDirectory("tmpDir")
    val outPdfFile = new File(tmpDir, "live_forest_out.pdf")
    val pdf = Pdf(new PdfConfig {
      orientation := Portrait
      pageSize := "A4"
      pageSize := "Letter"
      marginTop := "1in"
      marginBottom := "1in"
      marginLeft := "0in"
      marginRight := "0in"
    })
    missionDao.selectByMissionId(missionId).flatMap { row =>
      val missionIdDir = tool.getMissionIdDirById(missionId)
      val outFile = new File(missionIdDir, "out.txt")
      val predict1File = new File(missionIdDir, "predict1.png")
      if (!predict1File.exists()) {
        val linuxCommand =
          s"""
             |dos2unix *
             |python ${Utils.dosPath2Unix(Utils.pyPath)}/piePngChart1.py &
             |python ${Utils.dosPath2Unix(Utils.pyPath)}/piePngChart2.py &
             |python ${Utils.dosPath2Unix(Utils.pyPath)}/piePngChart3.py &
             |wait
               """.stripMargin
        val windowsCommand =
          s"""
             |python ${Utils.dosPath2Dos(Utils.pyPath)}/piePngChart1.py | python ${Utils.dosPath2Dos(Utils.pyPath)}/piePngChart2.py | python ${Utils.dosPath2Dos(Utils.pyPath)}/piePngChart3.py
                         """.stripMargin
        val command = if (Utils.isWindows) windowsCommand else linuxCommand
        Utils.callScript(missionIdDir, shBuffer = ArrayBuffer(command))
      }
      val base641 = Utils.getBase64Str(predict1File)
      val base642 = Utils.getBase64Str(new File(missionIdDir, "predict2.png"))
      val base643 = Utils.getBase64Str(new File(missionIdDir, "predict3.png"))
      val imageInfo = ImageInfo(base641, base642, base643)
      val resultInfo = tool.getResultInfo(outFile, imageInfo)
      pdf.run(views.html.user.html(data, row, imageInfo, resultInfo).toString(), outPdfFile)
      val bytes = FileUtils.readFileToByteArray(outPdfFile)
      val extraDataRow = ExtraDataRow(row.id, row.userId, data.sample, data.unit, data.address, data.name, data.sex,
        data.office, data.doctor, data.number, data.sampleTime, data.submitTime, data.sampleType, data.sampleStatus,
        data.title, data.danger, data.reporter, data.checker, data.checkDate, data.reportDate)
      extraDataDao.insertOrUpdate(extraDataRow).map { x =>
        Ok(bytes).as("application/pdf")
      }

    }

  }

  def toHtml = Action { implicit request =>
    Ok(views.html.user.htmlTest())
  }

  def predictResult = Action.async { implicit request =>
    val data = formTool.missionIdForm.bindFromRequest().get
    missionDao.selectByMissionId(data.missionId).zip(extraDataDao.selectByMissionId(data.missionId)).map { case (row, extraRow) =>
      val missionIdDir = tool.getMissionIdDirById(data.missionId)
      val div1File = new File(missionIdDir, "div1.txt")
      if (!div1File.exists()) {
        val linuxCommand =
          s"""
             |dos2unix *
             |python ${Utils.dosPath2Unix(Utils.pyPath)}/pieChart1.py &
             |python ${Utils.dosPath2Unix(Utils.pyPath)}/pieChart2.py &
             |python ${Utils.dosPath2Unix(Utils.pyPath)}/pieChart3.py &
             |wait
               """.stripMargin
        val windowsCommand =
          s"""
             |python ${Utils.dosPath2Dos(Utils.pyPath)}/pieChart1.py | python ${Utils.dosPath2Dos(Utils.pyPath)}/pieChart2.py | python ${Utils.dosPath2Dos(Utils.pyPath)}/pieChart3.py
                         """.stripMargin
        val command = if (Utils.isWindows) windowsCommand else linuxCommand
        Utils.callScript(missionIdDir, shBuffer = ArrayBuffer(command))
      }
      val div1Str = FileUtils.readFileToString(div1File) + Utils.pyScript
      val div2Str = FileUtils.readFileToString(new File(missionIdDir, "div2.txt")) + Utils.pyScript
      val div3Str = FileUtils.readFileToString(new File(missionIdDir, "div3.txt")) + Utils.pyScript
      val divData = DivData(div1Str, div2Str, div3Str)
      val outFile = new File(missionIdDir, "out.txt")
      val resultData = tool.getResultData(outFile)
      val extraDataJson = Utils.getJsonByT(extraRow)
      val missionJson = tool.getMissionJson(row, resultData)
      val (divStr, pStr) = tool.getFrontResultInfo(resultData, divData)
      val json = Json.obj("div" -> divStr, "case" -> resultData.caseDouble,
        "control" -> resultData.control, "fibrosis" -> resultData.fibrosis, "cirrhosis" -> resultData.cirrhosis,
        "early" -> resultData.early, "late" -> resultData.late, "extraData" -> extraDataJson, "mission" -> missionJson,
        "pStr" -> pStr
      )
      Ok(json)
    }

  }

  def fileCheck = Action(parse.multipartFormData) { implicit request =>
    val tmpDir = tool.createTempDirectory("tmpDir")
    val dataFile = new File(tmpDir, "data.xlsx")
    val file = request.body.file("file").get
    file.ref.moveTo(dataFile, replace = true)
    val myMessage = tool.fileCheck(dataFile)
    tool.deleteDirectory(tmpDir)
    Ok(Json.obj("valid" -> myMessage.valid, "message" -> myMessage.message))
  }

}
