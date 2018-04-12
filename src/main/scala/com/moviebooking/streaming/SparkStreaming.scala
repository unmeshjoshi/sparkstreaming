package com.moviebooking.streaming

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object SparkStreaming extends App {
  val zkQuorum = "localhost:32181"
  val group = "group1"
  val topics = Set("seatAvailability")
  val numThreads = 2
  val sparkConf = new SparkConf().setAppName("KafkaWordCount").setMaster("local")
  val ssc = new StreamingContext(sparkConf, Seconds(2))
  ssc.checkpoint("checkpoint")

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:29092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "use_a_separate_group_id_for_each_stream",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  val stream = KafkaUtils.createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams)
  )

  private val value: DStream[(String, String)] = stream.map(record => (record.key, record.value))
//  value.map(v ⇒ println(v._2))
  value.print()

  ssc.start()
  ssc.awaitTermination()
}
