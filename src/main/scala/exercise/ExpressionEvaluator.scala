package exercise

import akka.actor.Actor

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionEvaluator extends Actor {

  private def evaluateExpression(expr: String): Double = expr match {
    case numberExpressionPattern(x) => x.replaceAll(parenthesisPat, "").toDouble + 0.0
    case addExpressionPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) + evaluateExpression(y)}")
    case subtractExpressionPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) - evaluateExpression(y)}")
    case multiplyExpressionPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) * evaluateExpression(y)}")
    case divideExpressionPattern(x, y) if y != "0" => evaluateExpression(s"${evaluateExpression(x) / evaluateExpression(y)}")
    case divideExpressionPattern(_, _) => throw new IllegalArgumentException("Division by zero")
  }


  override def receive = {
    case EvaluateExpression(matchedExpression, expression) =>
      println(s"ExpressionEvaluator received EvaluateExpression message with matched expression '${matchedExpression}' and expression '${expression}'")
      sender() ! EvaluationResult(matchedExpression, evaluateExpression(expression))
  }
}
