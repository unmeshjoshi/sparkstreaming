package com.streaming.kafka

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}
import org.scalatest.concurrent.Eventually

class SparkDataframeJoinTest extends FunSuite with BeforeAndAfterAll with Matchers with Eventually {

  test("should parallelize rdd") {
    val sparkConf = new SparkConf().setAppName("Spark").setMaster("local")

    val spark = SparkSession
      .builder
      .config(sparkConf)
      .appName("StructuredKafkaProcessing")
      .getOrCreate()


    val intRDD: RDD[Int] = spark.sparkContext.parallelize(List(1,1,2,3,4))
    intRDD.count()

  }
}
