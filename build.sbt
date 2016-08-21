name := "BridgeTree"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
libraryDependencies += "ch.qos.logback" %  "logback-classic" % "1.1.7"
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
libraryDependencies += "junit" % "junit" % "4.12"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.4"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

assemblyJarName in assembly := "bridgetree.jar"