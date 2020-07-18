package com.atguigu.realtime.app

import com.atguigu.common.Constant
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @author myway
 * @create 2020-07-17 14:16
 */
trait BaseApp {

  def run(ssc: StreamingContext,topic: String)

  def main(args: Array[String]): Unit = {
    //1.streamingContext
    val sparkConf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("order_log")
    val ssc : StreamingContext = new StreamingContext(sparkConf,Seconds(3))
//    val topic = Constant.TOPIC_ORDER_INFO
    val topic = Constant.START_TOPIC
    run(ssc,topic)
    //5.采集
    ssc.start()

    //6.阻塞
    ssc.awaitTermination()
  }
}
