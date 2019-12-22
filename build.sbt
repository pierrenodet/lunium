lazy val commonSettings = Seq(

  // Resolvers
  resolvers += Resolver.sonatypeRepo("public"),
  resolvers += Resolver.sonatypeRepo("snapshots"),

  // Publishing
  organization := "com.github.pierrenodet",
  homepage := Some(url(s"https://github.com/pierrenodet/lunium")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "pierrenodet",
      "Pierre Nodet",
      "nodet.pierre@gmail.com",
      url("https://github.com/pierrenodet")
    )
  ),

  // Headers
  headerLicense := Some(
    HeaderLicense.Custom(
      """|Copyright 2020 Pierre Nodet
         |
         |Licensed under the Apache License, Version 2.0 (the "License");
         |you may not use this file except in compliance with the License.
         |You may obtain a copy of the License at
         |
         |    http://www.apache.org/licenses/LICENSE-2.0
         |
         |Unless required by applicable law or agreed to in writing, software
         |distributed under the License is distributed on an "AS IS" BASIS,
         |WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         |See the License for the specific language governing permissions and
         |limitations under the License.""".stripMargin
    )
  ),

  // Compilation
  scalaVersion       := "2.13.1",
  crossScalaVersions := Seq("2.11.12","2.12.10", scalaVersion.value),
  scalacOptions -= "-language:experimental.macros", // doesn't work cross-version
  Compile / doc     / scalacOptions --= Seq("-Xfatal-warnings"),
  Compile / doc     / scalacOptions ++= Seq(
    "-groups",
    "-sourcepath", (baseDirectory in LocalRootProject).value.getAbsolutePath,
    "-doc-source-url", "https://github.com/tpolecat/skunk/blob/v" + version.value + "€{FILE_PATH}.scala",
  ),
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),

)

lazy val lunium = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalaUnidocPlugin)
  .settings(commonSettings)
  .settings(publish / skip := true)
  .dependsOn(core,selenium,zelenium,umbreon,tests,examples)
  .aggregate(core,selenium,zelenium,umbreon,tests,examples)


lazy val core = project
  .in(file("modules/core"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    name := "lunium-core",
    description := "Lunium, zero dependency, tagless and bifunctor based library for WebDrivers"
  )

lazy val selenium = project
  .in(file("modules/selenium"))
  .dependsOn(core)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    name := "lunium-selenium",
    description := "Syntax interop with Selenium",
    libraryDependencies ++= Seq(
      "org.seleniumhq.selenium" % "selenium-java" % "3.141.59"
    )
  )

lazy val umbreon = project
  .in(file("modules/umbreon"))
  .dependsOn(core,selenium)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    name := "lunium-umbreon",
    description := "Cats Effect based intepreters and Cats Typeclasses Instances for Lunium",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.0.0",
      "org.typelevel" %% "cats-effect" % "2.0.0"
    )
  )

lazy val zelenium = project
  .in(file("modules/zelenium"))
  .dependsOn(core,selenium)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    name := "lunium-zelenium",
    description := "Scalaz and ZIO BiFunctors for the win",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "1.0.0-RC17",
    )
  )

lazy val tests = project
  .in(file("modules/tests"))
  .dependsOn(core,umbreon,zelenium)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    parallelExecution  := false,
    publish / skip := true,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.1.0" % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.1" % Test
    )
  )

lazy val examples = project
  .in(file("modules/examples"))
  .dependsOn(core,umbreon,zelenium)
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
    )
  )

lazy val docs = project
  .in(file("modules/lunium-docs"))
  .dependsOn(core,umbreon,zelenium)
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
  .settings(commonSettings)
  .settings(
    moduleName := "lunium-docs",
    skip in publish := true,
    mdocVariables := Map("VERSION" -> version.value),
    mdocIn := new File("modules/docs"),
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inProjects(core,umbreon,zelenium),
    target in (ScalaUnidoc, unidoc) := (baseDirectory in LocalRootProject).value / "website" / "static" / "api",
    cleanFiles += (target in (ScalaUnidoc, unidoc)).value,
    docusaurusCreateSite := docusaurusCreateSite.dependsOn(unidoc in Compile).value
  )
