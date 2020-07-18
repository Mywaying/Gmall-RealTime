package com.atguigu.realtime.util

import org.apache.kafka.common.serialization.StringDeserializer

/**
 * @author myway
 * @create 2020-07-14 16:40
 */
object MyKafkaUtil {

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "hadoop102:9092,hadoop103:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "gmall200213",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (true: java.lang.Boolean)
  )

  val topics = Set("gmall_startup")
}
