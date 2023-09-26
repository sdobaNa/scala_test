ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

val AkkaVersion = "2.8.0"
val AkkaHttpVersion = "10.5.0"
scalacOptions += "-Xasync"

lazy val root = (project in file("."))
  .settings(
    name := "scala"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.4.7" % Runtime,
  "com.typesafe.akka" %% "akka-http-xml" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.slick" %% "slick" % "3.4.1",
  "org.postgresql" % "postgresql" % "42.5.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.4.1",
  "com.github.tminglei" %% "slick-pg" % "0.21.1",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.21.1",
  "org.scala-lang.modules" %% "scala-async" % "1.0.1",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
)
