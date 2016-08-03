
scalaVersion in ThisBuild := "2.11.8"

// TODO The parser combinators lib needs to support multiple Scala/ScalaJS versions to enable this
//crossScalaVersions in ThisBuild := Seq("2.10.4", "2.11.8", "2.12.0-M5")

val globalSettings = Seq(
  name := "cron4s",
  organization := "com.github.alonsodomin",
  scalacOptions ++= Seq(
    "-language:postfixOps",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xfuture",
    "-Ywarn-dead-code",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials"
  )
)

lazy val commonJsSettings = Seq(
  scalaJSStage in Test := FastOptStage,
  scalaJSUseRhino in Global := false,
  coverageExcludedFiles := ".*"
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)

lazy val cron4s = (project in file(".")).
  settings(noPublishSettings).
  aggregate(cron4sJS, cron4sJVM)

lazy val cron4sJS = (project in file("js")).
  settings(noPublishSettings).
  settings(commonJsSettings: _*).
  aggregate(typesJS, coreJS)

lazy val cron4sJVM = (project in file("jvm")).
  settings(noPublishSettings).
  aggregate(typesJVM, coreJVM)

lazy val types = (crossProject.crossType(CrossType.Pure) in file("types")).
  settings(
    name := "types",
    moduleName := "cron4s-types"
  ).
  settings(globalSettings: _*).
  settings(commonJsSettings: _*)

lazy val typesJS = types.js
lazy val typesJVM = types.jvm

lazy val core = (crossProject in file("core")).
  settings(
    name := "core",
    moduleName := "cron4s-core"
  ).
  settings(globalSettings: _*).
  settings(commonJsSettings: _*).
  settings(Dependencies.core: _*).
  jvmSettings(Dependencies.coreJVM: _*).
  jsSettings(Dependencies.coreJS: _*).
  dependsOn(types)

lazy val coreJS = core.js
lazy val coreJVM = core.jvm


