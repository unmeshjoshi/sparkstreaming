import sbt._

object Dependencies {

  val Version = "0.1-SNAPSHOT"
  val SparkStreaming = Seq(
    Libs.`junit` % Test,
    Libs.`junit-interface` % Test,
    Libs.`mockito-core` % Test,
    Libs.`scalatest` % Test,
    Libs.`hsqldb` % Test,
    Libs.`postgres`,
    SparkLibs.sparkCore % Provided,
    SparkLibs.sparkStreaming % Provided,
    SparkLibs.sparkSQL % Provided,
    SparkLibs.sparkHiveSQL % Provided,
    SparkLibs.sparkTestingBase % Test,
    SparkLibs.sparkStreamingKafka % Provided,
    SparkLibs.sparkStructuredStreamingKafka % Provided,
    Kafka.`scalatest-embedded-kafka` % Test
  )
}