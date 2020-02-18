package controllers

import javax.inject.Inject
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.Utils

/**
  * Created by yz on 2019/2/18
  */
class ToolController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def getServerTime = Action { implicit request =>
    val time = new DateTime()
    val timeStr = Utils.dataTime2String(time)
    Ok(Json.toJson(timeStr))
  }

}
