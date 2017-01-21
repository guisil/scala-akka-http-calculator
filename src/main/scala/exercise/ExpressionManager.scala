package exercise

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props}

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionManager extends Actor {

  private val expressionPatt = """((?:\s*(?:\-?\d+(?:\.\d+)*)\s*)(?:[\+\-\*\/](?:\s*(?:\-?\d+(?:\.\d+)*)\s*))*)"""
  private val parenthesisPatt = """\(""" + expressionPatt + """\)"""
  private val expressionPattern = ("""^""" + expressionPatt + """$""").r
  private val parenthesisPattern = parenthesisPatt.r.unanchored
  private val numberPattern = """^(?:\s*(\-?\d+(?:\.\d+)*)\s*)$""".r

  private var originalExpression = ""
  private var currentExpression = ""
  private var expressionsBeingCalculated = 0

  private var originalSenderRef: ActorRef = _


  override val supervisorStrategy = OneForOneStrategy() {
    case e: IllegalArgumentException =>
      notifyFailure(e)
      Stop
    case e: MatchError =>
      notifyFailure(e)
      Stop
  }


  private def determineExpressionsToEvaluate(expression: String) =  {
    val withParenthesis = parenthesisPattern.findAllIn(expression)
    if (withParenthesis.isEmpty) expressionPattern.findAllIn(expression)
    else withParenthesis
  }

  private def isCompleted(expression: String) = expression match {
    case numberPattern(_) => true
    case _ => false
  }

  private def getResult(expression: String) = expression match {
    case numberPattern(x) => x.toDouble
  }

  private def triggerExpressionEvaluation(expression: String) = {
    val expressionsToEvaluate = determineExpressionsToEvaluate(expression)
    if (expressionsToEvaluate.isEmpty) notifyFailure(new IllegalArgumentException("Illegal expression"))
    for (exp <- expressionsToEvaluate) {
      context.actorOf(Props[ExpressionEvaluator]) ! EvaluateExpression(exp, exp.replaceAll("""\(|\)""", ""))
      expressionsBeingCalculated += 1
    }
  }

  private def notifyFailure(e: Exception): Unit = {
    originalSenderRef ! e
  }

  override def receive = {
    case StartEvaluation(expression) =>
      println(s"ExpressionManager received StartEvaluation message with expression '${expression}', expressions being calculated: ${expressionsBeingCalculated}")
      originalExpression = expression
      currentExpression = expression
      originalSenderRef = sender()
      triggerExpressionEvaluation(expression)

    case EvaluationResult(expression, result) =>
      println(s"ExpressionManager received EvaluationResult message with expression '${expression}' and result '${result}', expressions being calculated: ${expressionsBeingCalculated}")
      currentExpression = currentExpression.replace(expression, result.toString)
      expressionsBeingCalculated -= 1
      if (expressionsBeingCalculated == 0) {
        if (isCompleted(currentExpression)) {
          originalSenderRef ! EvaluationResult(originalExpression, getResult(currentExpression))
        }
        else triggerExpressionEvaluation(currentExpression)
      }
  }
}
