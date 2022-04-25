ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "trace4cats-iolocal-demo",
    libraryDependencies ++= List(
      "io.janstenpickle" %% "trace4cats-http4s-server" % "0.13.1",
      "io.janstenpickle" %% "trace4cats-jaeger-thrift-exporter" % "0.13.1",
      "io.janstenpickle" %% "trace4cats-base" % "0.13.1+28-784b7b3c",
      "io.janstenpickle" %% "trace4cats-base-io" % "0.13.1+28-784b7b3c",
      "io.janstenpickle" %% "trace4cats-core" % "0.13.1+28-784b7b3c",
      "io.janstenpickle" %% "trace4cats-inject" % "0.13.1+28-784b7b3c",
      "io.janstenpickle" %% "trace4cats-inject-io" % "0.13.1+28-784b7b3c",
      "io.janstenpickle" %% "trace4cats-model" % "0.13.1+28-784b7b3c",
      "org.http4s" %% "http4s-ember-server" % "0.23.11",
      "org.http4s" %% "http4s-circe" % "0.23.11",
      "org.http4s" %% "http4s-dsl" % "0.23.11"
    )
  )
