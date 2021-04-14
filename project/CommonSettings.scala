import sbt.Keys._
import sbt.{Def, _}
import scoverage.ScoverageKeys

object CommonSettings {

  val projectSettings = Seq(
    scalaVersion := Dependencies.V.scala,
    resolvers+= "OJO Snapshots" at "https://oss.jfrog.org/oss-snapshot-local",
    fork in Test := true,
    parallelExecution in Test := false,
    checksums in update := Nil,
    ScoverageKeys.coverageExcludedFiles := ".*UserHTTPServer.*;.*FlywayService.*;.*FailurePropatingActor.*;"
  )

  def baseProject(name: String): Project = (
    Project(name, file(name))
      settings (projectSettings: _*)
    )

  // TODO this is currently hard coded, will check how to pass the path dynamically.
}
