package filters

import akka.stream.Materializer
import controllers.routes
import javax.inject.Inject
import play.api.mvc.{Filter, RequestHeader, Result, Results}
import utils.Utils

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by yz on 2018/7/30
  */
class UserFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter{

  override def apply(f: RequestHeader => Future[Result])(rh: RequestHeader): Future[Result] = {
    if (rh.session.get("user").isEmpty && rh.path.contains("/user/") && !rh.path.contains("/assets/") &&
      !rh.path.contains("/login")) {
      Future.successful(Results.Redirect(routes.AppController.loginBefore()).flashing("info"->"请先登录!",
        "class"->Utils.errorClass))
    } else {
      f(rh)
    }
  }
}
