name := "BridgeTree"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.+"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.+"
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
libraryDependencies += "junit" % "junit" % "4.+"
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.+"
libraryDependencies += "org.rogach" %% "scallop" % "2.+"
libraryDependencies += "com.twitter" %% "util-collection" % "6.+"


testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

assemblyJarName in assembly := "bridgetree.jar"
