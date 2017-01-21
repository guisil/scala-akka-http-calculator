package exercise

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionManagerSpec extends TestKit(ActorSystem("ExpressionManagerIntegrationSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  private val expressionManagerRef = system.actorOf(Props(classOf[ExpressionManager]), "expression-manager")

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }


  "An ExpressionManager" should {
    "Reply with the result of an expression containing parenthesis" in {
      expectExpressionResultMessage("(1)", 1)
      expectExpressionResultMessage("(1-1)*2", 0)
      expectExpressionResultMessage("(1-1)*(2+4)", 0)
      expectExpressionResultMessage("(1-1)*2+3*(1-3+4)+10/2", 11)
      expectExpressionResultMessage("(1-1)*(2-4*9)+3*(3*5+4)+10/(3+2)", 59)
    }

    "Reply with the result of an expression not containing parenthesis" in {
      expectExpressionResultMessage("1", 1)
      expectExpressionResultMessage("1-1", 0)
      expectExpressionResultMessage("1+4*2-3", 6)
      expectExpressionResultMessage("1-1*2-4*9+3*1-3+4+10/2", -28)
    }

    "Reply with an exception when the expression is invalid" in {
      expressionManagerRef ! StartEvaluation("1-")
      expectMsgClass[IllegalArgumentException](classOf[IllegalArgumentException])

      expressionManagerRef ! StartEvaluation("(1-2")
      expectMsgClass[IllegalArgumentException](classOf[IllegalArgumentException])

      expressionManagerRef ! StartEvaluation("(1-2+")
      expectMsgClass[IllegalArgumentException](classOf[IllegalArgumentException])

      expressionManagerRef ! StartEvaluation("1/0")
      expectMsgClass[IllegalArgumentException](classOf[IllegalArgumentException])
    }
  }

  private def expectExpressionResultMessage(expression: String, expectedResult: Double): Unit = {
    expressionManagerRef ! StartEvaluation(expression)
    expectMsg(EvaluationResult(expression, expectedResult))
  }
}
