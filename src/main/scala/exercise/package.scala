
/**
  * Created by guisil on 23/01/2017.
  */
package object exercise {

  val parenthesisPat = """\(|\)"""

  val baseNumberPat = """\-?\d+(?:\.\d+)*"""
  val baseNumberWithParenthesisPat = """\(""" + baseNumberPat + """\)"""

  val numberPat = baseNumberPat + """|""" + baseNumberWithParenthesisPat

  val numberExpressionPattern = ("""(""" + numberPat + """)""").r

  val addExpressionPattern = ("""(.*(?:""" + numberPat + """))\+((?:""" + numberPat + """).*)""").r
  val subtractExpressionPattern = ("""(.*(?:""" + numberPat + """))\-((?:""" + numberPat + """).*)""").r
  val multiplyExpressionPattern = ("""(.*(?:""" + numberPat + """))\*((?:""" + numberPat + """).*)""").r
  val divideExpressionPattern = ("""(.*(?:""" + numberPat + """))\/((?:""" + numberPat + """).*)""").r

  val baseNumberInLongerExpressionPat = """\d+(?:\.\d+)*"""
  val numberInLongerExpressionPat = baseNumberInLongerExpressionPat + """|""" + baseNumberWithParenthesisPat

  val parenthesisExpressionPattern = ("""\((""" + """(?:""" + numberInLongerExpressionPat + """)""" + """(?:[\+\-\*\/]""" + """(?:""" + numberInLongerExpressionPat + """))+)\)""").r
  val multiplyOrDivideExpressionPattern = ("""(""" + """(?:""" + numberInLongerExpressionPat + """)""" + """(?:[\*\/]""" + """(?:""" + numberInLongerExpressionPat + """))+)""").r
  val addOrSubtractWholeExpressionPattern = ("""^(""" + """(?:""" + numberInLongerExpressionPat + """)""" + """(?:[\+\-]""" + """(?:""" + numberInLongerExpressionPat + """))+)$""").r
}
