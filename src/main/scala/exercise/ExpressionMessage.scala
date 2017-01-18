package exercise

/**
  * Created by guisil on 18/01/2017.
  */
abstract class ExpressionMessage
case class StartEvaluation(expression: String) extends ExpressionMessage
case class EvaluateExpression(expression: String) extends ExpressionMessage
case class EvaluationResult(expression: String, result: Double) extends ExpressionMessage
