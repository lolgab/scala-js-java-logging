import sbt.Keys._
import org.scalajs.sbtplugin.cross.CrossProject

crossScalaVersions := Seq("2.10.4", "2.11.8", "2.12.0")

val commonSettings: Seq[Setting[_]] = Seq(
  version := "0.1.1-SNAPSHOT",
  organization := "org.scala-js",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings"),

  homepage := Some(url("http://scala-js.org/")),
  licenses += ("BSD New",
      url("https://github.com/scala-js/scala-js-java-logging/blob/master/LICENSE")),
  scmInfo := Some(ScmInfo(
      url("https://github.com/scala-js/scala-js-java-logging"),
      "scm:git:git@github.com:scala-js/scala-js-java-logging.git",
      Some("scm:git:git@github.com:scala-js/scala-js-java-logging.git")))
)

lazy val root: Project = project.in(file(".")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings).
  settings(
    name := "scalajs-java-logging",

    mappings in (Compile, packageBin) ~= {
      _.filter(!_._2.endsWith(".class"))
    },
    exportJars := true,

    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
        <developers>
          <developer>
            <id>sjrd</id>
            <name>Sébastien Doeraene</name>
            <url>https://github.com/sjrd/</url>
          </developer>
          <developer>
            <id>gzm0</id>
            <name>Tobias Schlatter</name>
            <url>https://github.com/gzm0/</url>
          </developer>
          <developer>
            <id>nicolasstucki</id>
            <name>Nicolas Stucki</name>
            <url>https://github.com/nicolasstucki/</url>
          </developer>
        </developers>
    ),
    pomIncludeRepository := { _ => false }
  )

lazy val testSuite = CrossProject(
  jvmId = "testSuiteJVM",
  jsId = "testSuite",
  base = file("testSuite"),
  crossType = CrossType.Full
).
  jsConfigure(_.enablePlugins(ScalaJSJUnitPlugin)).
  settings(commonSettings: _*).
  settings(
    testOptions +=
      Tests.Argument(TestFramework("com.novocode.junit.JUnitFramework"), "-v", "-a")
  ).
  jsSettings(
    name := "java.logging testSuite on JS"
  ).
  jsConfigure(_.dependsOn(root)).
  jvmSettings(
    name := "java.logging testSuite on JVM",
    libraryDependencies +=
      "com.novocode" % "junit-interface" % "0.9" % "test"
  )

lazy val testSuiteJS = testSuite.js
lazy val testSuiteJVM = testSuite.jvm
