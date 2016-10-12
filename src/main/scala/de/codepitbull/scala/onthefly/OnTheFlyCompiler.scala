package de.codepitbull.scala.onthefly

import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

import de.codepitbull.scala.onthefly.helpers.{ CompilingClassLoader, ReusableCompiler }

import scala.collection.mutable
import scala.io.Source.fromInputStream
import scala.reflect.internal.util.BatchSourceFile
import scala.tools.nsc.Settings
import scala.tools.nsc.io.{ AbstractFile, VirtualDirectory }

/**
  * This compiler is used to compile both scriplets and scala-files contained in the classpath.
  * The basic idea is taken from here: Taken from https://eknet.org/main/dev/runtimecompilescala.html
  *
  * @author <a href="mailto:jochen.mader@codecentric.de">Jochen Mader</a
  */
class OnTheFlyCompiler(targetDir: Option[File]) {

  val reg  = "import\\s(.*)".r
  val that = this
  val target = targetDir match {
    case Some(dir) => AbstractFile.getDirectory(dir)
    case None      => new VirtualDirectory("(memory)", None)
  }

  val classCache = mutable.Map[String, Class[_]]()

  private val settings = new Settings()
  settings.deprecation.value = true // enable detailed deprecation warnings
  settings.unchecked.value = true   // enable detailed unchecked warnings
  settings.outputDirs.setSingleOutput(target)
  settings.usejavacp.value = true

  val compiler = ReusableCompiler(settings)

  val classLoader =
    new CompilingClassLoader(target, this.getClass.getClassLoader, compiler)

  /**
    * Compiles the code as a class into the class loader of this compiler.
    *
    * @param code
    * @return
    */
  def compileClass(code: String) = {
    reg.findAllIn(code).matchData.foreach { m =>
      findClass(m.group(1))
    }
    val sourceFiles = List(new BatchSourceFile("(inline)", code))
    compiler.compileSources(sourceFiles)
  }

  /**
    * Compiles the code-snippet as a class into the class loader of this compiler.
    *
    * @param code
    * @return
    */
  def compileScript(code: String): Class[_] = {
    val className = classNameForCode(code)
    findClass(className).getOrElse {
      val sourceFiles =
        List(new BatchSourceFile("(inline)", wrapCodeInClass(className, code)))
      val missingClasses = compiler.compileSources(sourceFiles)
      if (!missingClasses.isEmpty) {
        missingClasses.foreach(className => {
          val res = classLoader.getResourceAsStream(
              className.replace(".", "/") + ".scala"
          )
          if (res != null) {
            compileClass(fromInputStream(res).getLines().mkString("\n"))
          }
        })
        compiler.compileSources(sourceFiles)
      }
      findClass(className).get
    }
  }

  /**
    * Compiles the source string into the class loader and
    * evaluates it.
    *
    * @param code
    * @tparam T
    * @return
    */
  def eval[T](code: String): T = {
    val cls = compileScript(code)
    cls
      .getConstructor()
      .newInstance()
      .asInstanceOf[() => Any]
      .apply()
      .asInstanceOf[T]
  }

  /**
    * Checks if the given classname is available in the current classloader.
    * @param className
    * @return
    */
  def findClass(className: String): Option[Class[_]] = {
    synchronized {
      classCache.get(className).orElse {
        classLoader.tryToLoadClass(className) match {
          case Some(c) => Some(c)
          case None    => tryToCompileClass(className)
        }
      }
    }
  }

  def tryToCompileClass(className: String): Option[Class[_]] = {
    val res =
      classLoader.getResourceAsStream(className.replace(".", "/") + ".scala")
    if (res == null)
      None
    else {
      compileClass(fromInputStream(res).getLines().mkString("\n"))
      findClass(className)
    }
  }

  protected def classNameForCode(code: String): String = {
    val digest = MessageDigest.getInstance("SHA-1").digest(code.getBytes)
    "sha" + new BigInteger(1, digest).toString(16)
  }

  /*
   * Wrap source code in a new class with an apply method.
   */
  private def wrapCodeInClass(className: String, code: String) = {
    "class " + className + " extends (() => Any) {\n" +
    "  def apply() = {\n" +
    code + "\n" +
    "  }\n" +
    "}\n"
  }
}
