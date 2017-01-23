package exercise

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props}

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionManager extends Actor {

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
    val withParenthesis = parenthesisExpressionPattern.findAllIn(expression)
    if (withParenthesis.isEmpty) {
      val addSubtractOnly = addOrSubtractWholeExpressionPattern.findAllIn(expression)
      if (addSubtractOnly.isEmpty) multiplyOrDivideExpressionPattern.findAllIn(expression)
      else addSubtractOnly
    } else withParenthesis
  }

  private def isCompleted(expression: String) = expression match {
    case numberExpressionPattern(_) => true
    case _ => false
  }

  private def getResult(expression: String) = expression match {
    case numberExpressionPattern(x) => x.replaceAll(parenthesisPat, "").toDouble
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

  private def prepareValueForExpression(value: Double) = if (value < 0) s"($value)" else value.toString


  override def receive = {
    case StartEvaluation(expression) =>
      println(s"ExpressionManager received StartEvaluation message with expression '${expression}', expressions being calculated: ${expressionsBeingCalculated}")
      originalExpression = expression
      currentExpression = expression.filter(!_.isSpaceChar)
      originalSenderRef = sender()
      if (isCompleted(currentExpression)) {
        originalSenderRef ! EvaluationResult(originalExpression, getResult(currentExpression))
      } else {
        triggerExpressionEvaluation(currentExpression)
      }

    case EvaluationResult(expression, result) =>
      println(s"ExpressionManager received EvaluationResult message with expression '${expression}' and result '${result}', expressions being calculated: ${expressionsBeingCalculated}")
      currentExpression = currentExpression.replace(expression, prepareValueForExpression(result))
      expressionsBeingCalculated -= 1
      if (expressionsBeingCalculated == 0) {
        if (isCompleted(currentExpression)) {
          originalSenderRef ! EvaluationResult(originalExpression, getResult(currentExpression))
        }
        else triggerExpressionEvaluation(currentExpression)
      }
  }
}
