publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
fork:=true

libraryDependencies ++= Vector (
  Library.scalaCompiler,
  Library.scalaLibrary,
  Library.scalaReflect,
  Library.scalaTest       % "test",
  Library.junit           % "test"
)