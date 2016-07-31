import sbt._

object Version {
  final val Scala           = "2.11.8"
  final val ScalaTest       = "2.2.6"
}

object Library {
  val scalaTest      = "org.scalatest"  %% "scalatest"        % Version.ScalaTest
  val scalaCompiler  = "org.scala-lang" %  "scala-compiler"   % Version.Scala
  val scalaLibrary   = "org.scala-lang" %  "scala-library"    % Version.Scala
}
