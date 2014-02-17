package org.scalaide.core.ui.indentation

import org.junit.Test
import org.junit.Ignore

class ClassIndentationTests extends ScalaAutoIndentStrategyTest {

  @Test
  def classWithBraces() {
    """
    class A {^
    """ becomes
    """
    class A {
      ^
    }
    """ after linebreak
  }

  @Test
  def classWithoutBraces() {
    """
    class A^
    """ becomes
    """
    class A
    ^
    """ after linebreak
  }

  @Test
  def innerClass() {
    """
    class A {
      class B {^
    }
    """ becomes
    """
    class A {
      class B {
        ^
      }
    }
    """ after linebreak
  }

  @Test
  def classInMethod() {
    """
    class A {
      def f() {
        class B {^
      }
    }
    """ becomes
    """
    class A {
      def f() {
        class B {
          ^
        }
      }
    }
    """ after linebreak
  }

  @Test
  def ctor() {
    """
    class A(^)
    """ becomes
    """
    class A(
        ^)
    """ after linebreak
  }

  @Test
  def ctorWithArg() {
    """
    class A(i: Int,^)
    """ becomes
    """
    class A(i: Int,
        ^)
    """ after linebreak
  }

  @Ignore("wrong indentation; space not deleted")
  @Test
  def extendClass() {
    """
    class A(i: Int)
    class B(i: Int)^ extends A(i)
    """ becomes
    """
    class A(i: Int)
    class B(i: Int)
      ^extends A(i)
    """ after linebreak
  }

  @Ignore("wrong indentation; space not deleted")
  @Test
  def mixinTrait() {
    """
    trait T
    class A(i: Int)
    class B(i: Int)
      extends A(i)^ with T
    """ becomes
    """
    trait T
    class A(i: Int)
    class B(i: Int)
      extends A(i)
      ^with T
    """ after linebreak
  }

  /*  @Simon: it passes the test BUT it does NOT work properly in my Scala IDE!!
   *   My Scala IDE still suffers the issue as described in the ticket.
   */
  //  @Ignore("wrong indentation; incorrect (after val). See ticket #1000415")
  @Test
  def afterVal() {
    """
    val foo = "foo"^
    """ becomes
      """
    val foo = "foo"
    ^
    """ after linebreak
  }

  /*@Simon: The ticket is marked as fixed. TBut this test fails. 
  Although it works in my Scala IDE!! May it be a regression test?*/
  @Ignore("incorrect indentation inside comments. See ticket #1001384.")
  @Test
  def comment() {
    """
    /**
    *        hello^
    *        
    */
    """ becomes
      """
    /**
    *        hello
    *        ^
    *        
    */
    """ after linebreak
  }

  /*@Simon: It seems it is needed to extend ScalaAutoIndentStrategyTest to support "e" press hit.
   *  Is that correct? If so I can give it a try.   
   */
  @Ignore("incorrect indentatio. See ticket #1001306")
  @Test
  def ifElseReIndentationForElse() {
    """
    if (true)
    1
    else
    """ becomes
      """
    if (true)
    1
    els --> press e TODO handle "e" press hit.
    """ after linebreak
  }

  @Ignore("""wrong indentation; triple quoted strings. See ticket #1001670. 
      Alghouth currently it seems to work a little bit better than described in the ticket #1001670.""")
  @Test
  def afterTripleQuotes() {
    """
    val s = \\"\\"\\"a b^ c\\"\\"\\"
    """ becomes
      """
    val s = \\"\\"\\"a b
     ^c\\"\\"\\"
    """ after linebreak
  }

  //@Simon: Do you think is it feasible to extend ScalaAutoIndentStrategyTest to support Ctrl + space + choose a template?
  @Ignore("""wrong indentation; triple quoted strings. See ticket #1000626. 
      to reproduce: inside an indented context (such as a method body) type a keyword with
      a multiline expansion template (e.g. 'if') and hit ctrl-space and select the template to expand. 
      Actual Result: the template expands, but every line after the first has an indent of 0.""")
  @Test
  def insideAnIndentedContext() {
    """
    def myFunc(x: Int):Int {
    if^  //TODO Press Ctrl+pace+chooe "if" template.
    }
    """ becomes
      """
    def myFunc(x: Int):Int {
      if (condition) {
        ^ 
      }
    }
    """ after linebreak
  }

}