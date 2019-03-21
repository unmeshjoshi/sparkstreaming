package com.financeapp

import java.util.Properties

import org.apache.spark.sql.SparkSession

object SparkApp extends App {
  val spark = SparkSession
    .builder().master("yarn")
    .appName("Spark SQL basic example")
    .config("spark.some.config.option", "some-value")
    .config("spark.driver.extraClassPath", "/vagrant/postgresql-42.2.5.jar")
    .getOrCreate()

  // For implicit conversions like converting RDDs to DataFrames
  import spark.implicits._
  val p = new Properties()
  p.put("user", "postgres")
  p.put("password", "password")
  p.put("numPartitions", "10")
  val df = spark.read.jdbc("jdbc:postgresql://172.17.0.2:5432/?user=postgres", "customers", p)
  df.write.save("/user/vagrant/customers")

}
