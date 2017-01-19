package exercise

import akka.actor.{Actor, Status}

/**
  * Created by guisil on 19/01/2017.
  */
class ResultListener extends Actor {

  override def receive = {
    case EvaluationResult(expression, result) =>
      println(s"FINAL RESULT RECEIVED: '${expression}' = '${result}'")
      context.system.terminate()
    case Status.Failure(e) =>
      println(s"EXCEPTION!!! ${e}")
      context.system.terminate()
  }
}
