import sbt._

/**
  * User: Taoz
  * Date: 6/13/2017
  * Time: 9:38 PM
  */
object Dependencies {




  val slickV = "3.2.0"
  val akkaV = "2.5.11"
  val akkaHttpV = "10.1.0"

  val circeVersion = "0.8.0"

  val scalaJsDomV = "0.9.2"
  val scalaTagsV = "0.6.5"
  val diodeV = "1.1.2"

  val httpclientVersion = "4.3.5"
  val httpcoreVersion = "4.3.2"


  val akkaSeq = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV withSources (),
    "com.typesafe.akka" %% "akka-actor-typed" % akkaV withSources (),
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV
  )

  val akkaHttpSeq = Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-xml" % akkaHttpV
  )

  val circeSeq = Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion
  )

  val apacheSeq = Seq(
    "org.apache.httpcomponents" % "httpclient" % httpclientVersion withSources(),
    "org.apache.httpcomponents" % "httpcore" % httpcoreVersion withSources(),
    "org.apache.httpcomponents" % "httpmime" % httpclientVersion withSources(),
    "org.apache.commons" % "commons-collections4" % "4.0"
  )

  val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
  val slick = "com.typesafe.slick" %% "slick" % "3.2.0"
  val slickCodeGen = "com.typesafe.slick" %% "slick-codegen" % "3.2.0"
  val scalikejdbc = "org.scalikejdbc" %% "scalikejdbc" % "2.5.0"
  val scalikejdbcConfig = "org.scalikejdbc" %% "scalikejdbc-config" % "2.5.0"

  val scalatags = "com.lihaoyi" %% "scalatags" % "0.6.5"
  val nscalaTime = "com.github.nscala-time" %% "nscala-time" % "2.16.0"
  val hikariCP = "com.zaxxer" % "HikariCP" % "2.6.2"
  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val codec = "commons-codec" % "commons-codec" % "1.10"
  val postgresql = "org.postgresql" % "postgresql" % "9.4.1208"
  val asynchttpclient = "org.asynchttpclient" % "async-http-client" % "2.0.32"
  val ehcache = "net.sf.ehcache" % "ehcache" % "2.10.4"

  val dom4j = "org.dom4j" % "dom4j" % "2.0.0"

  val spring_context_support = "org.springframework" % "spring-context-support" % "3.0.5.RELEASE"
  val javax_mail = "javax.mail" % "mail" % "1.4.3"
  val jyaml = "org.jyaml" % "jyaml" % "1.3"
  val snakeyaml = "org.yaml" % "snakeyaml" % "1.7"


  val backendDependencies =
    Dependencies.akkaSeq ++
    Dependencies.akkaHttpSeq ++
    Dependencies.circeSeq ++
    Dependencies.apacheSeq ++
    Seq(
      Dependencies.scalaXml,
      Dependencies.slick,
      Dependencies.slickCodeGen,
      Dependencies.scalikejdbc,
      Dependencies.scalikejdbcConfig,
      Dependencies.scalatags,
      Dependencies.nscalaTime,
      Dependencies.hikariCP,
      Dependencies.logback,
      Dependencies.codec,
      Dependencies.postgresql,
      Dependencies.asynchttpclient,
      Dependencies.ehcache,
      Dependencies.dom4j,
      Dependencies.jyaml,
      Dependencies.spring_context_support,
      Dependencies.javax_mail,
      Dependencies.snakeyaml
    )



}
