package com.financeapp.streaming

import org.apache.spark.SparkConf
import org.apache.spark.sql.streaming.StreamingQuery
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
      .checkpoint()




    import org.apache.spark.sql.ForeachWriter
    val writer = new ForeachWriter[(String, String)] {
      override def open(partitionId: Long, version: Long) = true
      override def process(value: (String, String)) = println(s"*****************************key=>value ${value}")
      override def close(errorOrNull: Throwable) = {}
    }


    val dataSet: Dataset[(String, String)] = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .as[(String, String)]
    val query: StreamingQuery = dataSet.writeStream.foreach(writer).start()
    query.awaitTermination()
  }
}
