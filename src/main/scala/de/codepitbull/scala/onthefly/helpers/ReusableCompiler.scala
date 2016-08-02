package de.codepitbull.scala.onthefly.helpers

import scala.reflect.internal.util.{ Position, SourceFile }
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.{ Global, Settings }

/**
  * The Scala-compiler retains state between compilations. Especially the reporter is problematic as it
  * is checked for errors on each run and the run won't start if any error from the previous run is not cleared.
  * This class also takes of creating the required run-Instances as these aren't reuasble betwen executions.
  *
  * @author <a href="mailto:jochen.mader@codecentric.de">Jochen Mader</a
  */
class ReusableCompiler(settings: Settings) {

  var missingClasses = List[String]()

  private val reporter = new ConsoleReporter(settings) {
    override def error(pos: Position, msg: String): Unit = {
      //identifiy and record missing classes names
      if (msg.contains("is not a member")) {
        val parts = msg.split(" ")
        missingClasses = (parts(8) + "." + parts(1)) :: missingClasses
        println(missingClasses)
      }
      info0(pos, msg, ERROR, force = false)
    }
  }

  private val global = new Global(settings, reporter)

  def compileSources(list: List[SourceFile]): List[String] = {
    missingClasses = List[String]()
    reporter.reset()
    val run = new global.Run
    run.compileSources(list)
    missingClasses
  }
}

object ReusableCompiler {
  def apply(settings: Settings): ReusableCompiler =
    new ReusableCompiler(settings)
}
