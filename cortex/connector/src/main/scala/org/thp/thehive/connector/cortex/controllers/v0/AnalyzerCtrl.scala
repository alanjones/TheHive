package org.thp.thehive.connector.cortex.controllers.v0

import scala.concurrent.ExecutionContext

import play.api.libs.json.JsArray
import play.api.mvc.{Action, AnyContent, Results}

import akka.actor.ActorSystem
import javax.inject.{Inject, Singleton}
import org.thp.scalligraph.controllers.{EntryPoint, FieldsParser}
import org.thp.thehive.connector.cortex.controllers.v0.Conversion._
import org.thp.thehive.connector.cortex.services.AnalyzerSrv
import org.thp.thehive.controllers.v0.Conversion._

@Singleton
class AnalyzerCtrl @Inject() (
    entryPoint: EntryPoint,
    analyzerSrv: AnalyzerSrv,
    implicit val system: ActorSystem,
    implicit val ec: ExecutionContext
) {

  def list: Action[AnyContent] =
    entryPoint("list analyzer")
      .extract("range", FieldsParser.string.optional.on("range"))
      .asyncAuth { implicit request =>
        val range: Option[String] = request.body("range")
        analyzerSrv
          .listAnalyzer(range)
          .map { analyzers =>
            Results.Ok(JsArray(analyzers.map(_.toJson).toSeq))
          }
      }

  def listByType(dataType: String): Action[AnyContent] =
    entryPoint("list analyzer by dataType")
      .asyncAuth { implicit req =>
        analyzerSrv
          .listAnalyzerByType(dataType)
          .map { analyzers =>
            Results.Ok(JsArray(analyzers.map(_.toJson).toSeq))
          }
      }

  def getById(id: String): Action[AnyContent] =
    entryPoint("get analyzer by id")
      .asyncAuth { implicit req =>
        analyzerSrv
          .getAnalyzer(id)
          .map(a => Results.Ok(a.toJson))
      }
}
