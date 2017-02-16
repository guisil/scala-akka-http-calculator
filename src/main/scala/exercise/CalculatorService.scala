package exercise

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteResult.route2HandlerFlow
import akka.pattern.ask
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.io.StdIn
import scala.language.postfixOps

/**
  * Created by guisil on 19/01/2017.
  */
final case class ExpressionContainer(expression: String)
final case class ResultContainer(result: Double)


trait ExpressionJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val expressionFormat: RootJsonFormat[ExpressionContainer] = jsonFormat1(ExpressionContainer)
  implicit val resultFormat: RootJsonFormat[ResultContainer] = jsonFormat1(ResultContainer)
}


trait Service extends ExpressionJsonSupport {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val expressionManager: ActorRef


  val route: Route = {

    implicit val timeout = Timeout(5 seconds)

    path("evaluate") {
      post {
        entity(as[ExpressionContainer]) { container =>
          onSuccess(expressionManager ? StartEvaluation(container.expression)) {
            case response: EvaluationResult =>
              complete(StatusCodes.OK, ResultContainer(response.result))
            case e: Exception =>
                complete(StatusCodes.BadRequest, e.getMessage)
            case _ =>
              complete(StatusCodes.InternalServerError)
          }
        }
      }
    }
  }

}

object CalculatorService extends App with Service with CorsSupport {

  val host = "localhost"
  val port = 5555

  override implicit val system = ActorSystem("calculator-system")
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val expressionManager = system.actorOf(Props(classOf[ExpressionManager]), "expression-manager")

  val bindingFuture = Http().bindAndHandle(corsHandler(route), host, port)

  println(s"Waiting for requests at http://$host:$port/...\nHit RETURN to terminate")
  StdIn.readLine()

  bindingFuture.flatMap(_.unbind())
  system.terminate()
}
