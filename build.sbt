sbtPlugin := true

version := "0.5-SNAPSHOT"

name := "xsbt-filter"

organization := "com.github.sdb"

scalaVersion := "2.10.5"

scalacOptions ++= Seq("-deprecation", "-unchecked")

licenses := Seq("New BSD License" -> url("http://opensource.org/licenses/BSD-3-Clause"))

homepage := Some(url("http://sdb.github.com/xsbt-filter/"))

ScriptedPlugin.scriptedSettings

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-XX:MaxMetaspaceSize=512M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { x => false }

pomExtra := (
  <scm>
    <url>git@github.com:sdb/xsbt-filter.git</url>
    <connection>scm:git:git@github.com:sdb/xsbt-filter.git</connection>
  </scm>
  <developers>
    <developer>
      <id>sdb</id>
      <name>Stefan De Boey</name>
      <url>https://github.com/sdb</url>
    </developer>
  </developers>
)
