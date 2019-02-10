package com.streaming.kafka

import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

class SparkDataframeJoinTest extends FunSuite with BeforeAndAfterAll with Matchers with Eventually {

  test("should group dataframes") {
    val sparkConf = new SparkConf().setAppName("Spark").setMaster("local")

    val spark: SparkSession = SparkSession
      .builder
      .config(sparkConf)
      .master("local[4]")
      .appName("StructuredKafkaProcessing")
      .getOrCreate()
    val schema = StructType(List(StructField("num", DataTypes.IntegerType), StructField("count", DataTypes.IntegerType)))
    val df = createDataframe(spark, List(Row(1, 2), Row(1, 3), Row(2, 4), Row(2, 5), Row(2, 6)), schema)
    println("************************************" + df.groupBy("num").count().collect().size)
  }

  private def createDataframe(spark: SparkSession, rows: List[Row], schema: StructType) = {
    val intRDD = spark.sparkContext.parallelize(rows)
    val df = spark.createDataFrame(intRDD, schema)
    df
  }
}
