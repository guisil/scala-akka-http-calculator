package exercise

import org.scalatest.{FlatSpec, Matchers}
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionEvaluatorSpec extends FlatSpec with Matchers {

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(5 seconds)

  "An ExpressionEvaluator" should "evaluate individual numbers" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    val evaluator = evaluatorRef.underlyingActor
    assert(evaluator.evaluateExpression("1") == 1)
    assert(evaluator.evaluateExpression(" 8") == 8)
    assert(evaluator.evaluateExpression("33 ") == 33)
    assert(evaluator.evaluateExpression("394819.8") == 394819.8)
  }

  "it" should "evaluate addition expressions" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    val evaluator = evaluatorRef.underlyingActor
    assert(evaluator.evaluateExpression("1+1") == 2)
    assert(evaluator.evaluateExpression("2+8") == 10)
    assert(evaluator.evaluateExpression("11+ 432") == 443)
    assert(evaluator.evaluateExpression(" 7 + 3") == 10)
    assert(evaluator.evaluateExpression("3+5 +2+ 9") == 19)
  }

  "it" should "evaluate subtraction expressions" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    val evaluator = evaluatorRef.underlyingActor
    assert(evaluator.evaluateExpression("1-1") == 0)
    assert(evaluator.evaluateExpression("2-8") == -6)
    assert(evaluator.evaluateExpression("115 -21") == 94)
    assert(evaluator.evaluateExpression(" 7 - 3") == 4)
    assert(evaluator.evaluateExpression("5-2 - 10 -1") == -8)
  }

  "it" should "evaluate multiplication expressions" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    val evaluator = evaluatorRef.underlyingActor
    assert(evaluator.evaluateExpression("1*1") == 1)
    assert(evaluator.evaluateExpression("2*8") == 16)
    assert(evaluator.evaluateExpression("100 * 2") == 200)
    assert(evaluator.evaluateExpression(" 7 *3 ") == 21)
    assert(evaluator.evaluateExpression("2*4 * 10* 3") == 240)
  }

  "it" should "evaluate division expressions" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    val evaluator = evaluatorRef.underlyingActor
    assert(evaluator.evaluateExpression("3/1") == 3)
    assert(evaluator.evaluateExpression("8/4") == 2)
    assert(evaluator.evaluateExpression("100 / 2") == 50)
    assert(evaluator.evaluateExpression(" 3 /5 ") == 0.6)
    assert(evaluator.evaluateExpression("140/2 / 7 /5") == 2)
  }

  "it" should "evaluate expressions containing mixed operations" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    val evaluator = evaluatorRef.underlyingActor
    assert(evaluator.evaluateExpression("1+1-2") == 0)
    assert(evaluator.evaluateExpression("2+8-1") == 9)
    assert(evaluator.evaluateExpression("21 - 11+ 432") == 442)
    assert(evaluator.evaluateExpression("2 - 7 + 3+2-1") == -1)
    assert(evaluator.evaluateExpression("200-4*10+5") == 165)
    assert(evaluator.evaluateExpression("1-3 +4 /10*5") == 0)
    assert(evaluator.evaluateExpression("1-1*2-4*9+3*1-3+4+10/2") == -28)
  }
}
