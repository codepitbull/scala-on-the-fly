package de.codepitbull.scala.onthefly.helpers

import scala.io.Source._
import scala.reflect.internal.util.{ AbstractFileClassLoader, BatchSourceFile }
import scala.reflect.io.AbstractFile
import scala.util.Try

/**
  * A classloader that compiles missing classes if appropriate Scala-files are found on the classpath.
  *
  * @author <a href="mailto:jochen.mader@codecentric.de">Jochen Mader</a
  */
class CompilingClassLoader(override val root: AbstractFile,
                           parent: ClassLoader,
                           reusableCompiler: ReusableCompiler)
    extends AbstractFileClassLoader(root, parent) {
  protected override def findClass(name: String): Class[_] = {
    Try(super.findClass(name)).getOrElse({
      val res = this.getResourceAsStream(name.replace(".", "/") + ".scala")
      if (res == null)
        throw new ClassNotFoundException(name)
      else {
        val sourceFiles = List(
            new BatchSourceFile("(inline)",
                                fromInputStream(res).getLines().mkString("\n"))
        )
        reusableCompiler.compileSources(sourceFiles)
        findClass(name)
      }
    })
  }
}
