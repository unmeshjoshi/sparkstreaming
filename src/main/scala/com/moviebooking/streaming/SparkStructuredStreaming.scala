package com.moviebooking.streaming

import org.apache.spark.SparkConf
import org.apache.spark.sql.{Dataset, SparkSession}

object SparkStructuredStreaming {

  def processStream(kafkaBootstrapServers: String, kafkaTopic: String) = {
    val sparkConf = new SparkConf().setAppName("Spark").setMaster("local")

    val spark = SparkSession
      .builder
      .config(sparkConf)
      .appName("StructuredKafkaProcessing")
      .getOrCreate()

    import spark.implicits._

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", kafkaTopic)
      .load()
    val value: Dataset[(String, String)] = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]
    value.rdd.foreach(rdd ⇒ {
      println(s"key=value ${rdd._1} => ${rdd._2}")
    }
    )
  }
}
