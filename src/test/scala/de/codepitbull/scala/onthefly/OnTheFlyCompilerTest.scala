package de.codepitbull.scala.onthefly

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{ FlatSpec, Matchers }

@RunWith(classOf[JUnitRunner])
class OnTheFlyCompilerTest extends FlatSpec with Matchers {

  "A test class" should "should be compiled" in {
    val compiler = new OnTheFlyCompiler(None)
    val script   = "class Test{}"
    compiler.compileClass(script)
    compiler.findClass("Test") shouldBe defined
  }

  "A method executed on onthefly-compile test class" should "return the expected value" in {
    val compiler = new OnTheFlyCompiler(None)
    val script = "import de.codepitbull.scala.onthefly.Extendthis\n" +
        "class Test extends Extendthis{\n" +
        "override def hello():String = \"hello\"\n" +
        "}"
    compiler.compileClass(script)
    compiler
      .findClass("Test")
      .get
      .newInstance()
      .asInstanceOf[Extendthis]
      .hello() shouldBe "hello"
  }

  "A class depending on another uncompiled class" should "compile and do stuff" in {
    val compiler = new OnTheFlyCompiler(None)
    val script = "import de.codepitbull.scala.onthefly.Extendthis\n" +
        "import de.codepitbull.scala.onthefly.NotCompiled\n" +
        "class Test extends Extendthis{\n" +
        "override def hello():String = \"hello\"\n" +
        "}"
    compiler.compileClass(script)
    compiler
      .findClass("Test")
      .get
      .newInstance()
      .asInstanceOf[Extendthis]
      .hello() shouldBe "hello"
  }

  "Some test code" should "be compiled and evaluted" in {
    val compiler = new OnTheFlyCompiler(None)
    val script   = "println(\"you should see me\")"
    compiler.eval[Unit](script)
  }

  "A non-existent class for witch the classpath contains source-code" should "be automatically compiled and made available" in {
    val compiler = new OnTheFlyCompiler(None)
    compiler
      .findClass("de.codepitbull.scala.onthefly.TestClass") shouldBe defined
  }

  "TestClass" should "be compiled and evaluted" in {
    val compiler = new OnTheFlyCompiler(None)
    val script =
      "import de.codepitbull.scala.onthefly.TestClass\nprintln(new TestClass())"
    compiler.eval[Unit](script)
  }

  "A non-existent class for witch there is no source-code available on the classpath" should "result in a None-value" in {
    val compiler = new OnTheFlyCompiler(None)
    compiler
      .findClass("de.codepitbull.scala.onthefly.IDontExist") shouldBe None
  }
}
