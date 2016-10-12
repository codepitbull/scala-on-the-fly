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

  private val reporter = new ConsoleReporter(settings)

  private val global = new Global(settings, reporter)

  def compileSources(list: List[SourceFile]): Unit = {
    resetContexts
    val run = new global.Run
    run.compileSources(list)
  }

  def resetContexts: Unit = {
    reporter.reset()
  }
}

object ReusableCompiler {
  def apply(settings: Settings): ReusableCompiler =
    new ReusableCompiler(settings)
}
