import sbt._

object Dependencies {

  val Version = "0.1-SNAPSHOT"
  val SparkStreaming = Seq(
    Libs.`junit` % Test,
    Libs.`junit-interface` % Test,
    Libs.`mockito-core` % Test,
    Libs.`scalatest` % Test,
    SparkLibs.sparkCore,
    SparkLibs.sparkStreaming,
    SparkLibs.sparkSQL,
    SparkLibs.sparkHiveSQL,
    SparkLibs.sparkTestingBase % Test,
    SparkLibs.sparkStreamingKafka,
    Kafka.`akka-stream-kafka`,
    Kafka.`kafkaStreamsScala`,
    Kafka.`scalatest-embedded-kafka` % Test
  )
}