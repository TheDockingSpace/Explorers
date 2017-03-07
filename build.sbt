import sbtcrossproject.{crossProject, CrossType}

enablePlugins(CopyPasteDetector)

organization in ThisBuild := "space.thedocking"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

val macwire     = "com.softwaremill.macwire" %% "macros"       % "2.2.5" % "provided"
val scalaTest   = "org.scalatest"            %% "scalatest"    % "3.0.1" % Test
val ammoniteOps = "com.lihaoyi"              %% "ammonite-ops" % "0.8.2"

lazy val `explorers` = (project in file("."))
  .aggregate(`explorers-api`,
             `explorers-impl`,
             `explorers-stream-api`,
             `explorers-stream-impl`)

lazy val `explorers-api` = (project in file("explorers-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `explorers-impl` = (project in file("explorers-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      ammoniteOps
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`explorers-api`)

lazy val `explorers-stream-api` = (project in file("explorers-stream-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )
  .dependsOn(`explorers-api`)

lazy val `explorers-stream-impl` = (project in file("explorers-stream-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      ammoniteOps
    )
  )
  .dependsOn(`explorers-stream-api`)

fork in run := true
