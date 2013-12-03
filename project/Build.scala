package com.typesafe.atmos.sample

import sbt._
import Keys._
import com.typesafe.sbt.osgi.SbtOsgi._

object AtmosSample extends Build {
  val Organization = "com.typesafe.atmos.sample"
  val Version      = "1.4.0-SNAPSHOT"
  val ScalaVersion = "2.10.1"

  lazy val sample = Project(
    id = "atmos-sample",
    base = file("."),
    settings = defaultSettings ++ defaultOsgiSettings ++ Seq(
      libraryDependencies ++= Dependencies.tracedAkka,
      scalacOptions += "-language:postfixOps",
      javaOptions in run ++= Seq(
        "-javaagent:../lib/weaver/aspectjweaver.jar",
        "-Dorg.aspectj.tracing.factory=default",
        "-Djava.library.path=../lib/sigar"
      ),
      mainClass in (Compile, run) := Some("com.typesafe.atmos.sample.Sample"),
      OsgiKeys.exportPackage := Seq("com.typesafe.atmos.sample"),
      OsgiKeys.bundleActivator := Option("com.typesafe.atmos.sample.Activator"),
      OsgiKeys.bundleSymbolicName := "com.typesafe.atmos.sample.atmos-sample",
      Keys.fork in run := true
    )
  )

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version      := Version,
    scalaVersion := ScalaVersion,
    crossPaths   := false
  )

  lazy val defaultSettings = buildSettings ++ Seq(
    resolvers ++= Seq("Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
                      "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/")
  )
}

object Dependencies {

  object V {
    val Akka    = "2.1.4"
    val Atmos   = "1.3.1"
    val Logback = "1.0.7"
    val Felix   = "1.4.0"
  }


  val felix = "org.apache.felix" % "org.osgi.core" % V.Felix
  val akka = "com.typesafe.akka" %% "akka-osgi" % V.Akka
  val atmosTrace  = "com.typesafe.atmos.osgi" % ("trace-akka-osgi-" + V.Akka) % V.Atmos
  val logback    = "ch.qos.logback"     % "logback-classic"        % V.Logback

  val tracedAkka = Seq(akka, atmosTrace, logback, felix)
}
