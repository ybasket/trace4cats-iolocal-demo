ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "trace4cats-iolocal-demo",
    libraryDependencies ++= List(
      "io.janstenpickle" %% "trace4cats-http4s-client" % "0.14.1",
      "io.janstenpickle" %% "trace4cats-http4s-server" % "0.14.1",
      "io.janstenpickle" %% "trace4cats-jaeger-thrift-exporter" % "0.14.0",
      "io.janstenpickle" %% "trace4cats-core" % "0.14.6",
      "io.janstenpickle" %% "trace4cats-iolocal" % "0.14.6",
      "org.http4s" %% "http4s-ember-client" % "0.23.23",
      "org.http4s" %% "http4s-ember-server" % "0.23.23",
      "org.http4s" %% "http4s-circe" % "0.23.23",
      "org.http4s" %% "http4s-dsl" % "0.23.23"
    ),
    Compile / mainClass := Some("demo.fibonacci.Main")
  )
