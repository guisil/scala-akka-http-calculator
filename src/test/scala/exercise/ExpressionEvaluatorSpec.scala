package exercise

import org.scalatest.{FlatSpec, Matchers}
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionEvaluatorSpec extends FlatSpec with Matchers {

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(5 seconds)


  "An ExpressionEvaluator" should "evaluate individual numbers" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    assert((evaluatorRef ? EvaluateExpression("(1)", "1")).value.get.get == EvaluationResult("(1)", 1))
    assert((evaluatorRef ? EvaluateExpression("(-1)", "(-1)")).value.get.get == EvaluationResult("(-1)", -1))
    assert((evaluatorRef ? EvaluateExpression("1", "1")).value.get.get == EvaluationResult("1", 1))
    assert((evaluatorRef ? EvaluateExpression("-1", "-1")).value.get.get == EvaluationResult("-1", -1))
    assert((evaluatorRef ? EvaluateExpression("8 ", "8")).value.get.get == EvaluationResult("8 ", 8))
    assert((evaluatorRef ? EvaluateExpression(" 33", "33")).value.get.get == EvaluationResult(" 33", 33))
    assert((evaluatorRef ? EvaluateExpression("394819.8", "394819.8")).value.get.get == EvaluationResult("394819.8", 394819.8))
  }

  it should "evaluate addition expressions" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    assert((evaluatorRef ? EvaluateExpression("(1+1)", "1+1")).value.get.get == EvaluationResult("(1+1)", 2))
    assert((evaluatorRef ? EvaluateExpression("1 +1", "1+1")).value.get.get == EvaluationResult("1 +1", 2))
    assert((evaluatorRef ? EvaluateExpression("-1 +1", "-1+1")).value.get.get == EvaluationResult("-1 +1", 0))
    assert((evaluatorRef ? EvaluateExpression("2+ 8", "2+8")).value.get.get == EvaluationResult("2+ 8", 10))
    assert((evaluatorRef ? EvaluateExpression("11+432", "11+432")).value.get.get == EvaluationResult("11+432", 443))
    assert((evaluatorRef ? EvaluateExpression(" 7+3", "7+3")).value.get.get == EvaluationResult(" 7+3", 10))
    assert((evaluatorRef ? EvaluateExpression("7*(-3)", "7*(-3)")).value.get.get == EvaluationResult("7*(-3)", -21))
    assert((evaluatorRef ? EvaluateExpression("3+5+2+9", "3+5+2+9")).value.get.get == EvaluationResult("3+5+2+9", 19))
  }

  it should "evaluate subtraction expressions" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    assert((evaluatorRef ? EvaluateExpression("1-1", "1-1")).value.get.get == EvaluationResult("1-1", 0))
    assert((evaluatorRef ? EvaluateExpression("2-8", "2-8")).value.get.get == EvaluationResult("2-8", -6))
    assert((evaluatorRef ? EvaluateExpression("115 -21", "115-21")).value.get.get == EvaluationResult("115 -21", 94))
    assert((evaluatorRef ? EvaluateExpression(" 7 - 3", "7-3")).value.get.get == EvaluationResult(" 7 - 3", 4))
    assert((evaluatorRef ? EvaluateExpression("5-2-10-1", "5-2-10-1")).value.get.get == EvaluationResult("5-2-10-1", -8))
  }

  it should "evaluate multiplication expressions" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    assert((evaluatorRef ? EvaluateExpression("1*1","1*1")).value.get.get == EvaluationResult("1*1", 1))
    assert((evaluatorRef ? EvaluateExpression("2*8", "2*8")).value.get.get == EvaluationResult("2*8", 16))
    assert((evaluatorRef ? EvaluateExpression("100 * 2", "100*2")).value.get.get == EvaluationResult("100 * 2", 200))
    assert((evaluatorRef ? EvaluateExpression(" 7 *3 ", "7*3")).value.get.get == EvaluationResult(" 7 *3 ", 21))
    assert((evaluatorRef ? EvaluateExpression("2*4*10*3", "2*4*10*3")).value.get.get == EvaluationResult("2*4*10*3", 240))
  }

  it should "evaluate division expressions" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    assert((evaluatorRef ? EvaluateExpression("3/1", "3/1")).value.get.get == EvaluationResult("3/1", 3))
    assert((evaluatorRef ? EvaluateExpression("8/4", "8/4")).value.get.get == EvaluationResult("8/4", 2))
    assert((evaluatorRef ? EvaluateExpression("100 / 2", "100/2")).value.get.get == EvaluationResult("100 / 2", 50))
    assert((evaluatorRef ? EvaluateExpression(" 3 /5 ", "3/5")).value.get.get == EvaluationResult(" 3 /5 ", 0.6))
    assert((evaluatorRef ? EvaluateExpression("140/2/7/5", "140/2/7/5")).value.get.get == EvaluationResult("140/2/7/5", 2))
  }

  it should "evaluate expressions containing mixed operations" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    assert((evaluatorRef ? EvaluateExpression("1+1-2", "1+1-2")).value.get.get == EvaluationResult("1+1-2", 0))
    assert((evaluatorRef ? EvaluateExpression("2+8-1", "2+8-1")).value.get.get == EvaluationResult("2+8-1", 9))
    assert((evaluatorRef ? EvaluateExpression("21 - 11+ 432", "21-11+432")).value.get.get == EvaluationResult("21 - 11+ 432", 442))
    assert((evaluatorRef ? EvaluateExpression("2 - 7 + 3+2-1", "2-7+3+2-1")).value.get.get == EvaluationResult("2 - 7 + 3+2-1", -1))
    assert((evaluatorRef ? EvaluateExpression("200-4*10+5", "200-4*10+5")).value.get.get == EvaluationResult("200-4*10+5", 165))
    assert((evaluatorRef ? EvaluateExpression("1-3 +4 /10*5", "1-3+4/10*5")).value.get.get == EvaluationResult("1-3 +4 /10*5", 0))
    assert((evaluatorRef ? EvaluateExpression("0.0*-34.0+3*19.0+10/5.0", "0.0*-34.0+3*19.0+10/5.0")).value.get.get == EvaluationResult("0.0*-34.0+3*19.0+10/5.0", 59))
    assert((evaluatorRef ? EvaluateExpression("1-1*2-4*9+3*1-3+4+10/2", "1-1*2-4*9+3*1-3+4+10/2")).value.get.get == EvaluationResult("1-1*2-4*9+3*1-3+4+10/2", -28))
  }

  it should "fail for invalid expressions" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    assertThrows[MatchError](evaluatorRef.receive(EvaluateExpression("1+", "1+")))
    assertThrows[MatchError](evaluatorRef.receive(EvaluateExpression("2+2-", "2+2-")))
    assertThrows[MatchError](evaluatorRef.receive(EvaluateExpression("* 21 - 11+ 432", "*21-11+432")))
    assertThrows[MatchError](evaluatorRef.receive(EvaluateExpression("2 - 7 + 3+2-1 /", "2-7+3+2-1/")))
    assertThrows[MatchError](evaluatorRef.receive(EvaluateExpression("**+/", "**+/")))
    assertThrows[MatchError](evaluatorRef.receive(EvaluateExpression("1-*1*2-4*9+3*1-3+4+10/2", "1-*1*2-4*9+3*1-3+4+10/2")))
  }

  it should "fail for division by zero" in {
    val evaluatorRef = TestActorRef[ExpressionEvaluator]
    assertThrows[IllegalArgumentException](evaluatorRef.receive(EvaluateExpression("1/0", "1/0")))
  }
}
