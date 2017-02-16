import scala.util.matching.Regex

/**
  * Created by guisil on 23/01/2017.
  */
package object exercise {

  val ParenthesisPat: String = """\(|\)"""

  val BaseNumberPat: String = """\-?\d+(?:\.\d+)*"""
  val BaseNumberWithParenthesisPat: String = """\(""" + BaseNumberPat + """\)"""

  val NumberPat: String = BaseNumberPat + """|""" + BaseNumberWithParenthesisPat

  val NumberExpressionPattern: Regex = ("""(""" + NumberPat + """)""").r

  val AddExpressionPattern: Regex = ("""(.*(?:""" + NumberPat + """))\+((?:""" + NumberPat + """).*)""").r
  val SubtractExpressionPattern: Regex = ("""(.*(?:""" + NumberPat + """))\-((?:""" + NumberPat + """).*)""").r
  val MultiplyExpressionPattern: Regex = ("""(.*(?:""" + NumberPat + """))\*((?:""" + NumberPat + """).*)""").r
  val DivideExpressionPattern: Regex = ("""(.*(?:""" + NumberPat + """))\/((?:""" + NumberPat + """).*)""").r

  val BaseNumberInLongerExpressionPat: String = """\d+(?:\.\d+)*"""
  val NumberInLongerExpressionPat: String = BaseNumberInLongerExpressionPat + """|""" + BaseNumberWithParenthesisPat

  val ParenthesisExpressionPattern: Regex = ("""\((""" + """(?:""" + NumberInLongerExpressionPat + """)""" +
    """(?:[\+\-\*\/]""" + """(?:""" + NumberInLongerExpressionPat + """))+)\)""").r
  val MultiplyOrDivideExpressionPattern: Regex = ("""(""" + """(?:""" + NumberInLongerExpressionPat + """)""" +
    """(?:[\*\/]""" + """(?:""" + NumberInLongerExpressionPat + """))+)""").r
  val AddOrSubtractWholeExpressionPattern: Regex = ("""^(""" + """(?:""" + NumberInLongerExpressionPat + """)""" +
    """(?:[\+\-]""" + """(?:""" + NumberInLongerExpressionPat + """))+)$""").r
}
