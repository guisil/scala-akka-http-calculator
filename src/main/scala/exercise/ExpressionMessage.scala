package exercise

/**
  * Created by guisil on 18/01/2017.
  */
abstract class ExpressionMessage
case class StartEvaluation(expression: String) extends ExpressionMessage
case class EvaluateExpression(matchedExpression: String, expression: String) extends ExpressionMessage
case class EvaluationResult(matchedExpression: String, result: Double) extends ExpressionMessage
