package test

import java.io._
import java.net.URL
import java.util

import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.kernel.pdf.{PdfDocument, PdfReader, PdfWriter}
import io.github.cloudify.scala.spdf.{Landscape, Pdf, PdfConfig}
import org.jxls.common.Context
import org.jxls.util.{JxlsHelper, TransformerFactory}
import utils.{DesEncrypter, Utils}

import scala.beans.BeanProperty
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._
import scala.math.BigDecimal.RoundingMode


/**
  * Created by yz on 2018/9/3
  */
object Test {

  def main(args: Array[String]): Unit = {

    //    val array = ArrayBuffer("送检单位", "地址", "姓名", "性别", "送检科室", "申请医生", "门诊/住院号", "采样时间", "送检时间",
    //    "临床诊断", "样本类型", "样本状态")
    //    var values = ArrayBuffer("unit", "address", "name", "sex", "office", "doctor", "number", "sampleTime", "submitTime",
    //    "diagnose", "sampleType", "sampleStatus")
    //    array.zip(values).foreach{case(text,field)=>
    //      println(s"<th data-field='${field}' data-sortable='true'>${text}</th>")
    //    }
    val rs = (BigDecimal(0.7)*100).setScale(-1,RoundingMode.HALF_UP)
    println(rs)



    //    val pdf = new PdfDocument(new PdfReader(file), new PdfWriter(outFile))
    //    val form = PdfAcroForm.getAcroForm(pdf, true)
    //    val fields = form.getFormFields
    //    fields.get("name").setValue("yz")
    //    fields.get("sex").setValue("男")
    //    fields.get("age").setValue("24")
    //    fields.get("office").setValue("口腔科")
    //    fields.get("doctor").setValue("谢医生")
    //    fields.get("unit").setValue("绘云生物科技")
    //    fields.get("address").setValue("深圳市")
    //    fields.get("number").setValue("120410128")
    //    fields.get("sampleTime").setValue("2018-12-25")
    //    fields.get("submitTime").setValue("2018-12-26")
    //    fields.get("sampleId").setValue("37")
    //    fields.get("diagnose").setValue("肝癌晚期")
    //    fields.get("sampleType").setValue("血液")
    //    fields.get("sampleStatus").setValue("良好")
    //    form.flattenFields()
    //    pdf.close()


  }

}
