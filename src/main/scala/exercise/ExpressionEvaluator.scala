package exercise

import akka.actor.Actor

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionEvaluator extends Actor {

  private val numberPattern = """(?:\s*(\-?\d+(?:\.\d+)*)\s*)""".r
  private val addPattern = """(.+)\+(.+)""".r
  private val subtractPattern = """(.+)\-(.+)""".r
  private val multiplyPattern = """(.+)\*(.+)""".r
  private val dividePattern = """(.+)\/(.+)""".r


  def evaluateExpression(expr: String): Double = expr match {
    case numberPattern(x) => x.toDouble
    case addPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) + evaluateExpression(y)}")
    case subtractPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) - evaluateExpression(y)}")
    case multiplyPattern(x, y) => evaluateExpression(s"${evaluateExpression(x) * evaluateExpression(y)}")
    case dividePattern(x, y) => evaluateExpression(s"${evaluateExpression(x) / evaluateExpression(y)}")
  }

  override def receive = {
    case EvaluateExpression(expression) =>
      ???
    case _ => ???
  }
}
