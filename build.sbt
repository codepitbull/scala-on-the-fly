publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

libraryDependencies ++= Vector (
  Library.scalaCompiler,
  Library.scalaLibrary,
  Library.scalaTest       % "test",
  Library.junit           % "test"
)
val sbtcp = taskKey[Unit]("sbt-classpath")

sbtcp := {
  val files: Seq[File] = (fullClasspath in Compile).value.files
  val sbtClasspath : String = files.map(x => x.getAbsolutePath).mkString(":")
  println("Set SBT classpath to 'sbt-classpath' environment variable")
  System.setProperty("sbt-classpath", sbtClasspath)
}

run <<= (run in Runtime).dependsOn(sbtcp)