logLevel := Level.Info

//Plugin for Scalastyle
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

//Plugin for Scoverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")

// this v1.5.1 is preinstalled in current IDEA releases, used for code formatting
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")
