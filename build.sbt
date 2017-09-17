lazy val commonSettings = Seq(
  scalaVersion := "2.12.3",
  version := "1.0-SNAPSHOT"
)

lazy val scalaMetaSettings = Seq(
  libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0",
  addCompilerPlugin("org.scalameta" %  "paradise"  % "3.0.0-M10" cross CrossVersion.full)
)

lazy val deleteCompiledClass = taskKey[Unit]("delete compiled class files in target directory to enforce recompile")
deleteCompiledClass := IO.delete(baseDirectory.value / "target" / "scala-2.12" / "classes")

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(scalaMetaSettings: _*)
  .settings(
    name := "fileref",
    compile in Compile := {
      deleteCompiledClass.value
      (compile in Compile).value
    }
  )
  .aggregate(macros)
  .dependsOn(macros)

lazy val macros = (project in file("macros"))
  .settings(commonSettings: _*)
  .settings(scalaMetaSettings: _*)
  .settings(
    name := "fileref-macros"
  )
