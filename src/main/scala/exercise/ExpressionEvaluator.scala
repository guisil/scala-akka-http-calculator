package exercise

import akka.actor.Actor

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionEvaluator extends Actor {

  private def evaluateExpression(expr: String): Double = expr match {
    case NumberExpressionPattern(x) => x.replaceAll(ParenthesisPat, "").toDouble + 0.0
    case AddExpressionPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) + evaluateExpression(y)}")
    case SubtractExpressionPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) - evaluateExpression(y)}")
    case MultiplyExpressionPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) * evaluateExpression(y)}")
    case DivideExpressionPattern(x, y) if y != "0" => evaluateExpression(s"${evaluateExpression(x) / evaluateExpression(y)}")
    case DivideExpressionPattern(_, _) => throw new IllegalArgumentException("Division by zero")
  }


  override def receive: Receive = {
    case EvaluateExpression(matchedExpression, expression) =>
      println(s"ExpressionEvaluator received EvaluateExpression message with matched expression '$matchedExpression'" +
        s" and expression '$expression'")
      sender() ! EvaluationResult(matchedExpression, evaluateExpression(expression))
  }
}
