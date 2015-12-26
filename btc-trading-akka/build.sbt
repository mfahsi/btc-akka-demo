name := """trading-app-akka-remoting"""

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
  
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

  libraryDependencies ++= {
  val akkaVersion       = "2.3.11"
  val scalaTestVersion  = "2.2.4"

  Seq(
    "com.typesafe.akka" %% "akka-actor"                           % akkaVersion,
    "com.typesafe.akka" %% "akka-remote"                          % akkaVersion,
    "org.scalatest"     %% "scalatest"                            % scalaTestVersion % "test",
    "com.typesafe.akka" %% "akka-testkit"                         % akkaVersion % "test"
    
  )
}

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")

fork in run := true

