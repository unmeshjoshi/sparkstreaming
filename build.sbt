import Settings._

val `sparkstreaming` = project
  .in(file("."))
  .enablePlugins(DeployApp, DockerPlugin)
  .settings(defaultSettings: _*)
  .settings(
    dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7",
    dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7",
    dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.7",

    libraryDependencies ++= Dependencies.SparkStreaming
)
