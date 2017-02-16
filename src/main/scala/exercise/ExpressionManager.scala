package exercise

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}

/**
  * Created by guisil on 18/01/2017.
  */
class ExpressionManager extends Actor {

  private var originalExpression = ""
  private var currentExpression = ""
  private var expressionsBeingCalculated = 0

  private var originalSenderRef: ActorRef = _

  override val supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case e: IllegalArgumentException =>
      notifyFailure(e)
      Stop
    case e: MatchError =>
      notifyFailure(e)
      Stop
  }


  private def determineExpressionsToEvaluate(expression: String) =  {
    val withParenthesis = ParenthesisExpressionPattern.findAllIn(expression)
    if (withParenthesis.isEmpty) {
      val addSubtractOnly = AddOrSubtractWholeExpressionPattern.findAllIn(expression)
      if (addSubtractOnly.isEmpty) MultiplyOrDivideExpressionPattern.findAllIn(expression)
      else addSubtractOnly
    } else withParenthesis
  }

  private def isCompleted(expression: String) = expression match {
    case NumberExpressionPattern(_) => true
    case _ => false
  }

  private def getResult(expression: String) = expression match {
    case NumberExpressionPattern(x) => x.replaceAll(ParenthesisPat, "").toDouble
  }

  private def triggerExpressionEvaluation(expression: String) = {
    val expressionsToEvaluate = determineExpressionsToEvaluate(expression)
    if (expressionsToEvaluate.isEmpty) notifyFailure(new IllegalArgumentException("Illegal expression"))
    expressionsToEvaluate.foreach { exp =>
      context.actorOf(Props[ExpressionEvaluator]) ! EvaluateExpression(exp, exp.replaceAll("""\(|\)""", ""))
      expressionsBeingCalculated += 1
    }
  }

  private def notifyFailure(e: Exception): Unit = {
    originalSenderRef ! e
  }

  private def prepareValueForExpression(value: Double) = if (value < 0) s"($value)" else value.toString


  override def receive: Receive = {
    case StartEvaluation(expression) =>
      println(s"ExpressionManager received StartEvaluation message with expression '$expression'," +
        s" expressions being calculated: $expressionsBeingCalculated")
      originalExpression = expression
      currentExpression = expression.filter(!_.isSpaceChar)
      originalSenderRef = sender()
      if (isCompleted(currentExpression)) {
        originalSenderRef ! EvaluationResult(originalExpression, getResult(currentExpression))
      } else {
        triggerExpressionEvaluation(currentExpression)
      }

    case EvaluationResult(expression, result) =>
      println(s"ExpressionManager received EvaluationResult message with expression '$expression' and result '$result'," +
        s" expressions being calculated: $expressionsBeingCalculated")
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
