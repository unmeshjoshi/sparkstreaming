package com.streaming.kafka

import java.util.{Properties, UUID}

import com.moviebooking.streaming.{Networks, SparkDirectStreaming, SparkStructuredStreaming}
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

import scala.concurrent.Future
import scala.concurrent.duration._

class SparkStreamTest extends FunSuite with BeforeAndAfterAll with Matchers with Eventually {
  implicit val patience = PatienceConfig(10.seconds, 1.seconds)

  implicit val embeddedKafkaConfig: EmbeddedKafkaConfig = createKafkaConfig

  override def afterAll(): Unit = {
    EmbeddedKafka.stop()
  }

  override protected def beforeAll(): Unit = {
    EmbeddedKafka.start()(embeddedKafkaConfig)
  }

  def createKafkaConfig: EmbeddedKafkaConfig = {
    val kafkaHost = new Networks().hostname()
    val kafkaPort = 9002

    def defaultBrokerProperties(hostName: String) = {
      val brokers = s"PLAINTEXT://$hostName:$kafkaPort"
      Map("listeners" → brokers, "advertised.listeners" → brokers)
    }

    EmbeddedKafkaConfig(customBrokerProperties = defaultBrokerProperties(kafkaHost))
  }

  def bootstrapServers = {
    val kafkaHost = new Networks().hostname()
    val kafkaPort = 9002

    s"${kafkaHost}:${kafkaPort}"
  }

  test("should consume messages from spark") {
    import scala.concurrent.ExecutionContext.Implicits.global
    Future {
      produceTestMessagesSync("memberTopic")
    }

//    SparkDirectStreaming.processStream(bootstrapServers, "memberTopic")
    SparkStructuredStreaming.processStream(bootstrapServers, "memberTopic")
  }

  private def produceTestMessagesSync(topic: String) = {

    val producer = createProducer

    for (i ← 0 to 10000) {
      val data = new ProducerRecord[String, String](topic, s"key${i % 2}", s"value ${i}")
      val value = producer.send(data)
      println(value.get().serializedValueSize()) //blocking send
    }
  }

  private def createProducer = {
    val props = new Properties()
    props.put("bootstrap.servers", bootstrapServers)
    props.put("application.id", UUID.randomUUID().toString)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)
    producer
  }


}
