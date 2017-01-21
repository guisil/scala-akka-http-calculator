package exercise

import akka.actor.Props
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{FlatSpec, Matchers}
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._

/**
  * Created by guisil on 21/01/2017.
  */
class CalculatorServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest with Service {

  override val expressionManager = system.actorOf(Props(classOf[ExpressionManager]), "expression-manager")


  "Service" should "respond to a valid expression" in {
    Post(s"/evaluate", ExpressionContainer("(1-1)*2+3*(1-3+4)+10/2")) ~> route ~> check {
      status shouldBe OK
      contentType shouldBe `application/json`
      responseAs[ResultContainer] shouldBe ResultContainer(11)
    }
  }

  it should "respond with bad request when an invalid expression is received" in {
    Post(s"/evaluate", ExpressionContainer("(1-1)*2+3*(1-3+4)+10/2+")) ~> route ~> check {
      status shouldBe BadRequest
      responseAs[String].length should be > 0
    }
  }
}
