name := "Scala On The Fly Compiler"

libraryDependencies ++= Vector (
  Library.scalaCompiler,
  Library.scalaLibrary,
  Library.scalaTest       % "test",
  Library.junit           % "test"
)