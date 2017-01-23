lazy val root = (project in file(".")).
  settings(
    name := "scala-akka-http-calculator",
    version := "1.0",
    scalaVersion := "2.12.1",
    scalacOptions += "-feature",
    mainClass in Compile := Some("exercise.CalculatorService")
  )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.16" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.4.16",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.1" % "test",
  "com.typesafe.akka" %% "akka-http" % "10.0.1",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.1"
)