package exercise

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.actor.{ActorSystem, Props, Status}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionManagerSpec extends TestKit(ActorSystem("ExpressionManagerIntegrationSpec")) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll {

  private val listenerProbe = TestProbe()
  private val expressionManagerRef = system.actorOf(Props(classOf[ExpressionManager], listenerProbe.ref), "expression-manager")

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }


  "An ExpressionManager" should {
    "Send a message to the listener with the result of an expression containing parenthesis" in {
      expectExpressionResultMessage("(1)", 1)
      expectExpressionResultMessage("(1-1)*2", 0)
      expectExpressionResultMessage("(1-1)*(2+4)", 0)
      expectExpressionResultMessage("(1-1)*2+3*(1-3+4)+10/2", 11)
      expectExpressionResultMessage("(1-1)*(2-4*9)+3*(3*5+4)+10/(3+2)", 59)
    }

    "Send a message to the listener with the result of an expression not containing parenthesis" in {
      expectExpressionResultMessage("1", 1)
      expectExpressionResultMessage("1-1", 0)
      expectExpressionResultMessage("1+4*2-3", 6)
      expectExpressionResultMessage("1-1*2-4*9+3*1-3+4+10/2", -28)
    }

    "Send a message to the listener notifying a failure when the expression is invalid" in {
      expressionManagerRef ! StartEvaluation("1-")
      listenerProbe.expectMsgClass[Status.Failure](classOf[Status.Failure])

      expressionManagerRef ! StartEvaluation("(1-2")
      listenerProbe.expectMsgClass[Status.Failure](classOf[Status.Failure])

      expressionManagerRef ! StartEvaluation("(1-2+")
      listenerProbe.expectMsgClass[Status.Failure](classOf[Status.Failure])

      expressionManagerRef ! StartEvaluation("1/0")
      listenerProbe.expectMsgClass[Status.Failure](classOf[Status.Failure])
    }
  }

  private def expectExpressionResultMessage(expression: String, expectedResult: Double): Unit = {
    expressionManagerRef ! StartEvaluation(expression)
    listenerProbe.expectMsg(EvaluationResult(expression, expectedResult))
  }
}
