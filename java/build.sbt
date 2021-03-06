name := "java"

organization := "com.github.propi.rdfrules"

version := "1.0.0"

scalaVersion := "2.12.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8")
scalacOptions in (Compile, doc) += "-no-java-comments"
//scalacOptions += "-Xlog-implicits"

parallelExecution in Test := false

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")

resolvers ++= Seq("jitpack" at "https://jitpack.io")

libraryDependencies += organization.value %% "core" % version.value
libraryDependencies += "io.spray" %% "spray-json" % "1.3.4"
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6" % "test"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.7" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"