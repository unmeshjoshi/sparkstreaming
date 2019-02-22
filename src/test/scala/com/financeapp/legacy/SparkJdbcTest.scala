package com.financeapp.legacy

import java.util.Properties

import org.apache.spark.sql.{RelationalGroupedDataset, SparkSession}
import org.scalatest.FunSuite

class SparkJdbcTest extends FunSuite {

  test("should read rdbms froom spark") {
    val repository = new CustomerRepository()
//    repository.initialize()
    repository.add(Customer("george", "1 main street", "1"))
    repository.add(Customer("cindy", "1 main street", "2"))

    val spark = SparkSession
      .builder().master("local")
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._
    val p = new Properties()
    p.put("user", "postgres")
    p.put("password", "password")
    p.put("numPartitions", "10")
    // .option("partitionColumn", "owner_id")
    //      .option("lowerBound", 1)
    //      .option("upperBound", 10000)
//    val df = spark.read.jdbc("jdbc:hsqldb:mem:customer", "customers", p)
    val df = spark.read.jdbc("jdbc:postgresql://172.17.0.2:5432/?user=postgres", "customers", p)
    val rows = df.collectAsList()
    println(rows)
  }

}
