package exercise

import akka.actor.Actor

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionManager extends Actor {

  private val expressionPatt = """((?:\s*(?:\-?\d+(?:\.\d+)*)\s*)(?:[\+\-\*\/](?:\s*(?:\-?\d+(?:\.\d+)*)\s*))*)"""
  private val parenthesisPatt = """\(""" + expressionPatt + """\)"""
  private val expressionPattern = ("""^""" + expressionPatt + """$""").r
  private val parenthesisPattern = parenthesisPatt.r.unanchored


  def determineExpressionsToEvaluate(expression: String) =  {
    val withParenthesis = parenthesisPattern.findAllIn(expression)
    if (withParenthesis.isEmpty) expressionPattern.findAllIn(expression)
    else withParenthesis
  }

  override def receive = {
    case StartEvaluation(expression) =>
      ???
    case EvaluationResult(expression, result) =>
      ???
    case _ => ???
  }
}
