package exercise

import akka.actor.Actor

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionEvaluator extends Actor {

  private val numberPattern = """(?:\s*(\-?\d+(?:\.\d+)*)\s*)""".r
  private val addPattern = """(.*(?:\s*(?:\-?\d+(?:\.\d+)*)\s*))\+((?:\s*(?:\-?\d+(?:\.\d+)*)\s*).*)""".r
  private val subtractPattern = """(.*(?:\s*(?:\-?\d+(?:\.\d+)*)\s*))\-((?:\s*(?:\-?\d+(?:\.\d+)*)\s*).*)""".r
  private val multiplyPattern = """(.*(?:\s*(?:\-?\d+(?:\.\d+)*)\s*))\*((?:\s*(?:\-?\d+(?:\.\d+)*)\s*).*)""".r
  private val dividePattern = """(.*(?:\s*(?:\-?\d+(?:\.\d+)*)\s*))\/((?:\s*(?:\-?\d+(?:\.\d+)*)\s*).*)""".r


  def evaluateExpression(expr: String): Double = expr match {
    case numberPattern(x) => x.toDouble
    case addPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) + evaluateExpression(y)}")
    case subtractPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) - evaluateExpression(y)}")
    case multiplyPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) * evaluateExpression(y)}")
    case dividePattern(x, y) if y != "0" => evaluateExpression(s"${evaluateExpression(x) / evaluateExpression(y)}")
    case dividePattern(_, _) => throw new IllegalArgumentException("Division by zero")
  }

  override def receive = {
    case EvaluateExpression(matchedExpression, expression) =>
      println(s"ExpressionEvaluator received EvaluateExpression message with matched expression '${matchedExpression}' and expression '${expression}'")
      sender() ! EvaluationResult(matchedExpression, evaluateExpression(expression))
  }
}
