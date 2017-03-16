package sbtfilter

import sbt._

object ImageFileFilter extends FileFilter {
  val formats = Seq("jpg", "jpeg", "png", "gif", "bmp")
  def accept(file: File) = formats contains file.ext.toLowerCase
}

object PropertyFileFilter extends FileFilter {
  val formats = Seq("properties")
  def accept(file: File) = "properties" == file.ext.toLowerCase
}

object XMLFileFilter extends FileFilter {
  def accept(file: File) = "xml" == file.ext.toLowerCase
}

object Filter {
  import scala.util.matching.Regex._
  import java.io.{ FileReader, BufferedReader, PrintWriter }

  val DollarPattern = """(^|[^\\])(\$\{)([^}]+?)(\})"""
  val HandlebarPattern = """(^|[^\\])(\{\{)([^}]+?)(\}\})"""
  //val pattern = """(^|[^\\])(\$\{[^}]+?\})""".r
  def replacer(log: Logger, props: Map[String, String], pattern: String)(m: Match): Option[String] = {
    //val name = m.group(2).drop(2).dropRight(1)
    val name = m.group(3)
    props.get(name).map(m.group(1) + filter(log, _, props, pattern))
  }

  def filter(log: Logger, line: String, props: Map[String, String], pattern: String): String =  
    try pattern.r.replaceSomeIn(line, replacer(log, props, pattern))
    catch {
      case e: Throwable =>
        log.error(s"Failed to filter line: $line")
        log.trace(e)
        throw e
    }
  
  def apply(log: Logger, files: Seq[File], props: Map[String, String], pattern: String) {
    IO.withTemporaryDirectory { dir =>
      files.foreach { src =>
        try {
          val dest = new File(dir, src.getName)
          val out = new PrintWriter(dest)
          val in = new BufferedReader(new FileReader(src))
          IO.foreachLine(in) { line => IO.writeLines(out, Seq(filter(log, line, props, pattern))) }
          in.close()
          out.close()
          IO.copyFile(dest, src, preserveLastModified = true)
          true
        } catch {
          case e: Throwable =>
            log.error(s"Failed to filter $src")
            log.trace(e)
            false
        }
      }
    }
  }
}
