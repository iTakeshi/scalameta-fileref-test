lazy val ScalaVersion = "2.12.3"
lazy val ScalaMacrosVersion = "2.0.0-96-9f738df2"

lazy val commonSettings = Seq(
  scalaVersion := ScalaVersion,
  version := "1.0-SNAPSHOT"
)

lazy val newMacroSettings = Seq(
  scalaVersion := ScalaVersion,
  resolvers += Resolver.bintrayRepo("scalamacros", "maven"),
  libraryDependencies += "org.scala-lang" % "scala-reflect" % ScalaVersion,
  addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full),
  addCompilerPlugin("org.scalamacros" % "scalac-plugin" % ScalaMacrosVersion cross CrossVersion.full)

)

lazy val deleteCompiledClass = taskKey[Unit]("delete compiled class files in target directory to enforce recompile")
deleteCompiledClass := IO.delete(baseDirectory.value / "target" / "scala-2.12" / "classes")

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(newMacroSettings: _*)
  .settings(
    name := "fileref",
    compile in Compile := {
      deleteCompiledClass.value
      (compile in Compile).value
    }
  )
  .dependsOn(macros)

lazy val macros = (project in file("macros"))
  .settings(commonSettings: _*)
  .settings(newMacroSettings: _*)
  .settings(
    name := "fileref-macros",
    libraryDependencies += "org.scalamacros" %% "scalamacros" % ScalaMacrosVersion
  )
