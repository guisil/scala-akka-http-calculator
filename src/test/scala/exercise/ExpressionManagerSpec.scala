package exercise

import org.scalatest.{FlatSpec, Matchers}
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionManagerSpec extends FlatSpec with Matchers {

  implicit val system = ActorSystem()
  implicit val timeout = Timeout(5 seconds)

  "An ExpressionManager" should "match expressions with parenthesis" in {
    val managerRef = TestActorRef[ExpressionManager]
    val manager = managerRef.underlyingActor

    assert{
      val result = manager.determineExpressionsToEvaluate("(1)").toList
      result.length == 1 && result.contains("(1)")
    }
    assert{
      val result = manager.determineExpressionsToEvaluate("(1-1)*2").toList
      result.length == 1 && result.contains("(1-1)")
    }
    assert{
      val result = manager.determineExpressionsToEvaluate("(1-1)*(2+4)").toList
      result.length == 2 && result.contains("(1-1)") && result.contains("(2+4)")
    }
    assert{
      val result = manager.determineExpressionsToEvaluate("(1-1)*2+3*(1-3+4)+10/2").toList
      result.length == 2 && result.contains("(1-1)") && result.contains("(1-3+4)")
    }
    assert {
      val result = manager.determineExpressionsToEvaluate("(1-1)*(2-4*9)+3*(1-3+4)+10/2").toList
      result.length == 3 && result.contains("(1-1)") && result.contains("(2-4*9)") && result.contains("(1-3+4)")
    }
  }

  "it" should "match expressions without parenthesis" in {
    val managerRef = TestActorRef[ExpressionManager]
    val manager = managerRef.underlyingActor

    assert{
      val result = manager.determineExpressionsToEvaluate("1").toList
      result.length == 1 && result.contains("1")
    }
    assert{
      val result = manager.determineExpressionsToEvaluate("1-1").toList
      result.length == 1 && result.contains("1-1")
    }
    assert{
      val result = manager.determineExpressionsToEvaluate("1-1*2-4").toList
      result.length == 1 && result.contains("1-1*2-4")
    }
    assert{
      val result = manager.determineExpressionsToEvaluate("1-1*2-4*9+3*1-3+4+10/2").toList
      result.length == 1 && result.contains("1-1*2-4*9+3*1-3+4+10/2")
    }
  }

  "it" should "not match invalid expressions" in {
    val managerRef = TestActorRef[ExpressionManager]
    val manager = managerRef.underlyingActor

    assert{
      val result = manager.determineExpressionsToEvaluate("1-").toList
      result.isEmpty
    }
    assert{
      val result = manager.determineExpressionsToEvaluate("(1-2+)").toList
      result.isEmpty
    }
    assert{
      val result = manager.determineExpressionsToEvaluate("(1-2").toList
      result.isEmpty
    }
  }
}
