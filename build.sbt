name := "Scala On The Fly Compiler"

version := "1.0.0"

organization := "de.codepitbull.scala.onthefly"

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
fork:=true

libraryDependencies ++= Vector (
  Library.scalaCompiler,
  Library.scalaLibrary,
  Library.scalaTest       % "test",
  Library.junit           % "test"
)