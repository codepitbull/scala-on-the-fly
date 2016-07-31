package de.codepitbull.scala.onthefly

import org.scalatest.{FlatSpec, Matchers}

class OnTheFlyCompilerTest  extends FlatSpec with Matchers {

  "A test class" should "should be compiled" in {
    val compiler = new OnTheFlyCompiler(None)
    val script = "class Test{}"
    compiler.compileClass(script)
    compiler.findClass("Test") shouldBe defined
  }

  "Some test code" should "be compiled and evaluted" in {
    val compiler = new OnTheFlyCompiler(None)
    val script = "println(\"wtf\")"
    compiler.eval[Unit](script)
  }

  "A non-existent class for witch the classpath contains source-code" should "be automatically compiled and made available" in {
    val compiler = new OnTheFlyCompiler(None)
    compiler.findClass("de.codepitbull.scala.onthefly.TestClass") shouldBe defined
  }

  "A non-existent class for witch there is no source-code available on the classpath" should "result in a None-value" in {
    val compiler = new OnTheFlyCompiler(None)
    compiler.findClass("de.codepitbull.scala.onthefly.IDontExist") shouldBe None
  }
}
