package com.atguigu.gmall

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

/**
 * @author myway
 * @create 2020-07-17 14:01
 */
object MyKafkaUtil {

  def sendToKafka(topic:String,Content:String)={
    val props = new Properties()
    props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"hadoop102:9092,hadoop103:9092,hadoop104:9092")
    props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,classOf[StringSerializer].getName)
    props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,classOf[StringSerializer].getName)
    val producer = new KafkaProducer[String,String](props)
    producer.send(new ProducerRecord[String,String](topic,Content))
  }
}
