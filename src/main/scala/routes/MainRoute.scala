package routes

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives
import service.{HistoryService, SecurityService, UtilService}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import tables.{History, Security}
import util.DateMarshalling._
import scala.util.{Failure, Success}
import scala.xml.NodeSeq

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val securityFormat: RootJsonFormat[Security] = jsonFormat4(Security.apply)
  implicit val historyFormat: RootJsonFormat[History] = jsonFormat5(History.apply)
}

object MainRoute extends Directives with JsonSupport {

  def getRoute() {

    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")

    lazy val topLevelRoute = concat(routeSecurity, historySecurity, utilRoute)

    lazy val routeSecurity =
      concat(
        get {
          concat(
            pathPrefix("security" / Segment) { secid =>
              complete(SecurityService.getOneById(secid))
            },
            path("security") {
              complete(SecurityService.getAll)
            }
          )
        },
        post {
          concat(
            path("security" / "import") {
              entity(as[NodeSeq]) { node =>
                onComplete(UtilService.importData("securities", node)) {
                  case Success(value) => complete("done")
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            },
            path("security") {
              entity(as[Security]) { his =>
                onComplete(SecurityService.createOne(his)) {
                  case Success(value) => complete("done")
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            },
            pathPrefix("security" / Segment) { secid =>
              entity(as[Security]) { sec =>
                onComplete(SecurityService.updateOne(secid, sec)) {
                  case Success(value) => complete("done")
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            }
          )
        },
        delete {
          pathPrefix("security" / Segment) { secid =>
            onComplete(SecurityService.deleteOne(secid)) {
              case Success(value) => complete("done")
              case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
            }
          }
        },
      )

    lazy val historySecurity =
      concat(
        get {
          concat(
            pathPrefix("history" / LongNumber) { id =>
              complete(HistoryService.getOneById(id))
            },
            path("history") {
              complete(HistoryService.getAll)
            }
          )
        },
        post {
          concat(
            path("history" / "import") {
              entity(as[NodeSeq]) { node =>
                onComplete(UtilService.importData("history", node)) {
                  case Success(value) => complete("done")
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            },
            path("history") {
              entity(as[History]) { his =>
                onComplete(HistoryService.createOne(his)) {
                  case Success(value) => complete("done")
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            },
            pathPrefix("history" / LongNumber) { id =>
              entity(as[History]) { his =>
                onComplete(HistoryService.updateOne(id, his)) {
                  case Success(value) => complete("done")
                  case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            }
          )
        },
        delete {
          pathPrefix("history" / LongNumber) { id =>
            onComplete(HistoryService.deleteOne(id)) {
              case Success(value) => complete("done")
              case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
            }
          }
        },
      )

    lazy val utilRoute =
      concat(
        get {
            path("security_history") {
              complete(UtilService.joinSecurityHistory)
            }
        },
      )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(topLevelRoute)

    println("Server started on port:8080 and host:localhost")
  }
}
