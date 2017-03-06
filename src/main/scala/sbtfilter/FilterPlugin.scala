package sbtfilter

import sbt._
import Keys._
import java.io.File

/* TODO
- configurable variable delimiters
- support web plugin
- watchSources
*/
trait FilterKeys {
  val filterExtraProps     = settingKey[Seq[(String, String)]]("Extra filter properties.")
  val filterBaseProjectProps   = taskKey[Seq[(String, String)]]("Project filter properties.")
  val filterBaseProjectProps2   = taskKey[Seq[(String, String)]]("Project filter properties.")
  val filterCompileProjectProps   = taskKey[Seq[(String, String)]]("Project filter properties.")
  val filterTestProjectProps   = taskKey[Seq[(String, String)]]("Project filter properties.")
  val filterSystemProps    = taskKey[Seq[(String, String)]]("System filter properties.")
  val filterManagedProps   = taskKey[Seq[(String, String)]]("Managed filter properties.")
  val filterUnmanagedProps = taskKey[Seq[(String, String)]]("Filter properties defined in filters.")
  val filterProps          = taskKey[Seq[(String, String)]]("All filter properties.")
  val filterResources      = taskKey[Seq[(File, File)] => Seq[(File, File)]]("Filters all resources.")
}

object FilterPlugin extends AutoPlugin {
  import FileFilter.globFilter

  object autoImport extends FilterKeys

  import autoImport._

  override def requires = plugins.JvmPlugin

  override lazy val projectSettings =
    baseFilterSettings ++
      inConfig(Compile)(filterConfigSettings) ++
      inConfig(Test)(filterConfigSettings)

  lazy val baseFilterSettings = Seq(
    filterExtraProps := Nil,
   filterBaseProjectProps <<= (
      organization, 
      name, 
      moduleName,
      description, 
      homepage,
      version, 
      scalaVersion, 
      scalaBinaryVersion,
      sbtVersion,
      sbtBinaryVersion,
      baseDirectory
    ) map {
      (
        o,
        n, 
        mn,
        d, 
        h,
        v, 
        scv, 
        scbv,
        sv,
        sbv,
        b
      ) =>
        Seq(
          "organization" -> o, 
          "name" -> n, 
          "moduleName" -> mn,
          "description" -> d,
          "homepage" -> h,
          "version" -> v, 
          "scalaVersion" -> scv, 
          "scalaBinaryVersion" -> scbv,
          "sbtVersion" -> sv,
          "sbtBinaryVersion" -> sbv,
          "baseDirectory" -> b
        ) .map { case (k, v) => (s"project.$k", s"$v") }
    },
   
    filterBaseProjectProps2 <<= (
      target,
      sourceDirectory,
      classDirectory in Compile
    ) map {
      (
        t,
        sd,
        cd
      ) =>
        Seq(
          "target" -> t,
          "sourceDirectory" -> sd,
          "compileClassDirectory" -> cd
        ) .map { case (k, v) => (s"project.$k", s"$v") }
    },

    filterCompileProjectProps <<= (
      scalaSource in Compile,
      javaSource in Compile,
      resourceDirectory in Compile
    ) map {
      (
        ss,
        js,
        rd
      ) =>
        Seq(
          "compileScalaSource" -> ss,
          "compileJavaSource" -> js,
          "compileResourceDirectory" -> rd
        ).map { 
           case (k, v) => (s"project.$k", s"$v") 
        }
    },
    filterTestProjectProps <<= (
      scalaSource in Test,
      javaSource in Test,
      resourceDirectory in Test
    ) map {
      (
        ss,
        js,
        rd
      ) =>
        Seq(
          "testScalaSource" -> ss,
          "testJavaSource" -> js,
          "testResourceDirectory" -> rd
        ).map { 
          case (k, v) => (s"project.$k", s"$v") 
          }
    },
    filterSystemProps := sys.props.toSeq)

  lazy val filterConfigSettings: Seq[Setting[_]] = Seq(
    includeFilter in filterResources := "*.properties" | "*.xml",
    excludeFilter in filterResources := HiddenFileFilter || ImageFileFilter,
    filterResources := filterTask.value,
    copyResources := {
      filterResources.value(copyResources.value)
    },
    filterManagedProps <<= (filterBaseProjectProps, filterBaseProjectProps2, filterCompileProjectProps, filterTestProjectProps, filterSystemProps) map (_ ++ _ ++ _++ _++ _),
    filterUnmanagedProps := Nil,
    filterProps <<= (filterExtraProps, filterManagedProps, filterUnmanagedProps) map (_ ++ _ ++ _))

  def filterTask = Def.task {

    val s = streams.value
    val incl = (includeFilter in filterResources).value
    val excl = (excludeFilter in filterResources).value
    val props = filterProps.value.toMap

    (mappings: Seq[(File, File)]) => {
      val filtered = mappings filter { case (src, _) =>
        println(src, incl.accept(src), excl.accept(src))
        incl.accept(src) && !excl.accept(src) && !src.isDirectory }

      val webXml = (target.value ** "WEB-INF" / "web.xml").get
      val inputFiles = filtered.map(_._2) ++ webXml

      Filter(s.log, inputFiles, props)

      println(incl)
      println(filtered.mkString("\n"))

      mappings
    }
  }


}
