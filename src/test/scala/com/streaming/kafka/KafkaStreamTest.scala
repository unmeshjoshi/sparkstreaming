package com.streaming.kafka

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscription, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer, StringSerializer}
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object KafkaStreamTest {

  implicit class RichFuture[T](val f: Future[T]) extends AnyVal {
    def await: T        = Await.result(f, 20.seconds)
    def done: Future[T] = Await.ready(f, 20.seconds)
  }
}

class KafkaStreamTest extends FunSuite with EmbeddedKafka with BeforeAndAfterAll {
  import KafkaStreamTest._

  private val kafkaPort       = 6001
  private val kafkaHost: String = new Networks().hostname()
  private val pubSubProperties                  = Map("bootstrap.servers" → s"${kafkaHost}:${kafkaPort}")
  private val brokers                           = s"PLAINTEXT://${kafkaHost}:${kafkaPort}"
  private val brokerProperties                  = Map("listeners" → brokers, "advertised.listeners" → brokers)
  val kafkaTopic = "seatAvailability"
  implicit val config = EmbeddedKafkaConfig(customConsumerProperties = pubSubProperties,
    customProducerProperties = pubSubProperties,
    customBrokerProperties = brokerProperties)


  override def beforeAll(): Unit = {
    EmbeddedKafka.start()(config)
  }

  override def afterAll(): Unit = {
    EmbeddedKafka.stop()
  }

  implicit val system = ActorSystem("EventPublisher")
  val producerSettings: ProducerSettings[String, String] =
    producerSettings(kafkaHost, kafkaPort)
  val kafkaProducer: KafkaProducer[String, String] =
    producerSettings.createKafkaProducer()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val kafkaProducerSink: Sink[ProducerRecord[String, String], Future[Done]] =
    Producer.plainSink(producerSettings, kafkaProducer)

  var counter            = 0

  private def eventGenerator() = {
    counter += 1
    new ProducerRecord(kafkaTopic,"key", "testmessage" + counter)
  }
  test("should publish to kafka") {
    Source.tick(1.second, 200 millis, ()).map(_ => eventGenerator()).to(kafkaProducerSink).run()
    Thread.sleep(3000)

    val consumer = consumerSettings(kafkaHost, kafkaPort)
    val subscription: Subscription = Subscriptions.assignment(new TopicPartition(kafkaTopic, 0))

    val kafkaConsumer = consumer.createKafkaConsumer()
    val eventStream = Consumer.plainSource(consumer, subscription)
    val eventualDone = eventStream.runForeach(consumerRecord ⇒ {
      consumerRecord.key()
      val bytes = consumerRecord.value()
      println(ByteString(bytes).utf8String)
    })
    eventualDone.await
  }

  private def producerSettings(host: String, port: Int)(implicit actorSystem: ActorSystem) =
    ProducerSettings(actorSystem, new StringSerializer, new StringSerializer)
      .withBootstrapServers(s"${host}:${port}")

  private def consumerSettings(host: String, port: Int)(implicit actorSystem: ActorSystem) =
    ConsumerSettings(actorSystem, new StringDeserializer, new ByteArrayDeserializer)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
      .withBootstrapServers(s"${host}:${port}")
}
