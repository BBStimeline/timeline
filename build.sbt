

import xerial.sbt.Pack.{packExtraClasspath, packJvmOpts, packMain}

resolvers += Resolver.sonatypeRepo("snapshots")

val scalaV = "2.12.4"
//val scalaV = "2.11.8"

val projectName = "timeline"
val projectVersion = "0.0.1"





val projectMainClass = "com.neo.sk.timeline.Boot"

def commonSettings = Seq(
  version := projectVersion,
  scalaVersion := scalaV,
  scalacOptions ++= Seq(
    //"-deprecation",
    "-feature"
  )
)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(name := "shared")
  .settings(commonSettings: _*)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// Scala-Js frontend
lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(name := "frontend")
  .settings(commonSettings: _*)
  .settings(
    inConfig(Compile)(
      Seq(
        fullOptJS,
        fastOptJS,
        //        packageScalaJSLauncher,
        packageJSDependencies,
        packageMinifiedJSDependencies
      ).map(f => (crossTarget in f) ~= (_ / "sjsout"))
    ))
  .settings(skip in packageJSDependencies := false)
  .settings(

    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % "0.8.0",
      "io.circe" %%% "circe-generic" % "0.8.0",
      "io.circe" %%% "circe-parser" % "0.8.0",
      "org.scala-js" %%% "scalajs-dom" % "0.9.2",
      "me.chrons" %%% "diode" % "1.1.0",
      //      "io.suzaku" %%% "diode" % "1.1.2",
      //"com.lihaoyi" %%% "upickle" % upickleV,
      "com.lihaoyi" %%% "scalatags" % "0.6.5"
      //"org.scala-js" %%% "scalajs-java-time" % scalaJsJavaTime
      //"com.lihaoyi" %%% "utest" % "0.3.0" % "test"
    )
  )
  .dependsOn(sharedJs)

// Akka Http based backend
lazy val backend = (project in file("backend"))
  .settings(commonSettings: _*)
  .settings(
    mainClass in reStart := Some(projectMainClass),
    javaOptions in reStart += "-Xmx2g"
  )
  .settings(name := "backend")
  .settings(
    //pack
    // If you need to specify main classes manually, use packSettings and packMain
    packSettings,
    // [Optional] Creating `hello` command that calls org.mydomain.Hello#main(Array[String])
    packMain := Map("timeline" -> projectMainClass),
    packJvmOpts := Map("timeline" -> Seq("-Xmx256m", "-Xms128m")),
    packExtraClasspath := Map("timeline" -> Seq("."))
  )
  .settings(
    libraryDependencies ++= Dependencies.backendDependencies
  )
  .settings {
    (resourceGenerators in Compile) += Def.task {
      val fastJsOut = (fastOptJS in Compile in frontend).value.data
      val fastJsSourceMap = fastJsOut.getParentFile / (fastJsOut.getName + ".map")
      Seq(
        fastJsOut,
        fastJsSourceMap
      )
    }.taskValue
  }
  //    .settings(
  //      (resourceGenerators in Compile) += Def.task {
  //        val fullJsOut = (fullOptJS in Compile in frontend).value.data
  //        val fullJsSourceMap = fullJsOut.getParentFile / (fullJsOut.getName + ".map")
  //        Seq(
  //          fullJsOut,
  //          fullJsSourceMap
  //        )
  //      }.taskValue)
  .settings((resourceGenerators in Compile) += Def.task {
  Seq(
    (packageJSDependencies in Compile in frontend).value
    //(packageMinifiedJSDependencies in Compile in frontend).value
  )
}.taskValue)
  .settings(
    (resourceDirectories in Compile) += (crossTarget in frontend).value,
    watchSources ++= (watchSources in frontend).value
  )
  .dependsOn(sharedJvm)

lazy val root = (project in file("."))
  .aggregate(frontend, backend)
  .settings(name := projectName)





