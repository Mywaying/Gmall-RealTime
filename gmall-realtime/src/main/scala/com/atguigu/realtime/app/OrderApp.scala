package com.atguigu.realtime.app
import com.alibaba.fastjson.JSON
import com.atguigu.realtime.bean.OrderInfo
import com.atguigu.realtime.util.MyKafkaUtil.kafkaParams
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

/**
 * @author myway
 * @create 2020-07-17 14:17
 */
object OrderApp extends BaseApp {
  override def run(ssc: StreamingContext, topic: String): Unit = {
    val kafkastream = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String,String](Set(topic),kafkaParams))
    val stream: DStream[String] = kafkastream.map(record => record.value())
    //3.转换
    val orderinfoStream: DStream[OrderInfo] = stream.map(json=>JSON.parseObject(json,classOf[OrderInfo]))
    orderinfoStream.foreachRDD(
      rdd=>{
        import org.apache.phoenix.spark._
//        println("HELLPJSDJF==================")
        rdd.saveToPhoenix(
          "GMALL_ORDER_INFO",
          Seq("ID", "PROVINCE_ID", "CONSIGNEE", "ORDER_COMMENT", "CONSIGNEE_TEL", "ORDER_STATUS", "PAYMENT_WAY", "USER_ID", "IMG_URL", "TOTAL_AMOUNT", "EXPIRE_TIME", "DELIVERY_ADDRESS", "CREATE_TIME", "OPERATE_TIME", "TRACKING_NO", "PARENT_ORDER_ID", "OUT_TRADE_NO", "TRADE_BODY", "CREATE_DATE", "CREATE_HOUR"),
          zkUrl = Option("hadoop102,hadoop103,hadoop104:2181")
        )
      }
    )
    orderinfoStream.print(1000)
  }
}
