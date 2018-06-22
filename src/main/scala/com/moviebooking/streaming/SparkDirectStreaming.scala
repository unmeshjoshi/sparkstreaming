package com.moviebooking.streaming

import java.lang

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkDirectStreaming extends App {

  def processStream(kafkaBootstrapServers: String, kafkaTopic: String) = {
    val topics = Set(kafkaTopic)
    val sparkConf = new SparkConf().setAppName("Spark").setMaster("local")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> kafkaBootstrapServers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "use_a_separate_group_id_for_each_stream",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: lang.Boolean)
    )

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    stream.foreachRDD { rdd: RDD[ConsumerRecord[String, String]] =>

      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

      rdd.foreach((record: ConsumerRecord[String, String]) ⇒ {
        println(s"key=>value ${record.key()} => ${record.value()}")
      })

      // begin your transaction

      // update results
      // update offsets where the end of existing offsets matches the beginning of this batch of offsets
      // assert that offsets were updated correctly

      // end your transaction
    }

    ssc.start()
    ssc.awaitTermination()
  }

}
