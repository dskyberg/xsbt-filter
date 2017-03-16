sbtPlugin := true

version := "0.5.0"

name := "xsbt-filter"

organization := "org.dskyberg.sbt"

scalacOptions ++= Seq("-deprecation", "-unchecked")

licenses := Seq("New BSD License" -> url("http://opensource.org/licenses/BSD-3-Clause"))

homepage := Some(url("http://sdb.github.com/xsbt-filter/"))

ScriptedPlugin.scriptedSettings

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-XX:MaxMetaspaceSize=512M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false

publishMavenStyle := true

publishArtifact in Test := false

publishTo := {
  val nexus = "https://nexus.dev.confyrm.com/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "content/repositories/releases")
}

pomIncludeRepository := { x => false }

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

pomExtra := (
  <scm>
    <url>git@github.com:confyrm/auth-service.git</url>
    <connection>scm:git:git@github.com:confyrm/auth-service.git</connection>
  </scm>
  <developers>
    <developer>
      <id>david</id>
      <name>David Skyberg</name>
    </developer>
  </developers>)

