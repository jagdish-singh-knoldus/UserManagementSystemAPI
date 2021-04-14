import sbt._

object Dependencies {

  object V {
    // Scala
    val scala = "2.12.7"
    val akkaHttp               = "10.1.7"
    val akka                   = "2.6.1"
    val akkaHttpCirce          = "1.26.0"
   val cors                   = "0.3.4"
    val slf4j                  = "1.7.25"
    val circeVersion           = "0.10.0"
    val logging                = "3.9.0"
    val logback                = "1.2.3"
    val pureConfig             = "0.14.0"
    val jansi                  = "1.12"
    val liftJson               = "3.3.0"
    val slick                  = "3.3.2"
    val postgres               = "42.2.5"
    val slickPg                = "0.18.0"
    val flywayCore             = "3.2.1"
    // Tests
    val scalaTest              = "3.0.5"
    val mockito                = "1.5.11"
    val scalaSti               = "3.0.1"
  }

  object Libraries {
    // Scala Akka Libraries
    val akka            = "com.typesafe.akka" %% "akka-stream" % V.akka
    val akkaHttp        = "com.typesafe.akka" %% "akka-http" % V.akkaHttp
    val akkaHttpCirce   = "de.heikoseeberger" %% "akka-http-circe" % V.akkaHttpCirce
     val cors            = "ch.megard" %% "akka-http-cors" % V.cors

    // logging
    val slf4j           = "org.slf4j" % "slf4j-simple" % V.slf4j
    val logging         = "com.typesafe.scala-logging" %% "scala-logging" % V.logging
    val logback         = "ch.qos.logback" % "logback-classic" % V.logback
    val pureConfig      = "com.github.pureconfig" %% "pureconfig" % V.pureConfig
    val jansi           = "org.fusesource.jansi" % "jansi" % V.jansi

    //flyway
    val flyway          =  "org.flywaydb" % "flyway-core" % V.flywayCore

    // Tests
    val akkaTestKit     = "com.typesafe.akka" %% "akka-testkit" % V.akka
    val akkaHttpTestKit = "com.typesafe.akka" %% "akka-http-testkit" % V.akkaHttp
    val akkaStreamTestKit = "com.typesafe.akka" %% "akka-stream-testkit" % V.akka

    val scalaTest       = "org.scalatest" %% "scalatest" % V.scalaTest
    val mockito         = "org.mockito" %% "mockito-scala-scalatest" % V.mockito
    val mock            =  "org.mockito" % "mockito-core" % "1.9.5"
    val scalaSti        = "org.clapper" %% "scalasti" % V.scalaSti

    // temp DB
    val slick           = "com.typesafe.slick" %% "slick" % V.slick
    val mysql        = "mysql" % "mysql-connector-java" % "8.0.23"
    val h2db   =  "com.h2database" % "h2" % "1.4.200" % Test

    val slickCirce =  "com.github.tminglei" %% "slick-pg_circe-json" % "0.19.4"

    // circe for paring
    val circe: Seq[ModuleID] = Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % V.circeVersion)

    val liftJson         =  "net.liftweb" %% "lift-json" % V.liftJson
  }

  val akkaDependencies: Seq[ModuleID] = Seq(Libraries.akkaHttp, Libraries.akka, Libraries.akkaHttpCirce,
      Libraries.cors)

  val logDependencies: Seq[ModuleID] = Seq(Libraries.logging, Libraries.slf4j, Libraries.pureConfig,
    Libraries.logback)

  val dbDependencies = Seq(Libraries.slick,
    Libraries.mysql,  Libraries.h2db)

  val commonModuleDependencies: Seq[sbt.ModuleID] = akkaDependencies ++ logDependencies ++
    Seq(Libraries.jansi,  Libraries.liftJson, Libraries.flyway) ++ Libraries.circe
}
