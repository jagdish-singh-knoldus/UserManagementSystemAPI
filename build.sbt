import CommonSettings.baseProject
import Dependencies.{Libraries, commonModuleDependencies, dbDependencies}
import scoverage.ScoverageKeys

name := "User Management System"

Compile / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint")
Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

def compile(deps: Seq[ModuleID]): Seq[ModuleID] = deps map (_ % "compile")
def provided(deps: Seq[ModuleID]): Seq[ModuleID] = deps map (_ % "provided")
def test(deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")


lazy val root = (project.in(file(".")))
    .aggregate(crud)

val crud = (
  baseProject("crud")
    settings(libraryDependencies ++= compile(commonModuleDependencies) ++ Libraries.circe ++ test(Libraries.scalaTest, Libraries.akkaHttpTestKit,
      Libraries.mockito, Libraries.mock, Libraries.akkaStreamTestKit) ++ dbDependencies,
    ScoverageKeys.coverageMinimum := 90,
    ScoverageKeys.coverageFailOnMinimum := true
  ))

