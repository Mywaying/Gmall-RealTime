package com.atguigu.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.atguigu.common.Constant
import com.atguigu.realtime.bean.startupLog
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import com.atguigu.realtime.util.MyKafkaUtil._
import com.atguigu.realtime.util.RedisUtil
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import redis.clients.jedis.Jedis

/**
 * @author myway
 * @create 2020-07-14 16:39
 */
object DauApp {
  def main(args: Array[String]): Unit = {
    //1.streamingContext
    val sparkConf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("springlog")
    val ssc : StreamingContext = new StreamingContext(sparkConf,Seconds(3))
    //2. 创建kafka数据流
    val kafkastream = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String,String](topics,kafkaParams))
    val stream: DStream[String] = kafkastream.map(record => record.value())
    //3.转换
   val startupLogStream: DStream[startupLog] = stream.map(json=>JSON.parseObject(json,classOf[startupLog]))
    //4.去重
    val filterDstream: DStream[startupLog] = startupLogStream.transform(
      logs => {
        val client: Jedis = RedisUtil.getClient
        val mids: util.Set[String] = client.smembers(Constant.START_TOPIC + ":" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
        client.close()
        val midsBD: Broadcast[util.Set[String]] = ssc.sparkContext.broadcast(mids)
        logs
          .filter(log => {
            !midsBD.value.contains(log.mid)
          })
          .map(log => (log.mid, log))
          .groupByKey()
          .map {
            case (mid, logs) => {
              logs.toList.sortBy(_.ts).head
            }
          }

      }
    )
    //5.第一次出现的写入Redis
    filterDstream.foreachRDD(
      rdd=>{

        rdd.foreachPartition(logs=>{
          val client: Jedis = RedisUtil.getClient
          logs.foreach(log => {client.sadd(Constant.START_TOPIC + ":" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()),log.mid)})
          client.close()
        }
        )

        //6.接入Phoenix
        import org.apache.phoenix.spark._
        rdd.saveToPhoenix("GMALL_DAU",
          Seq("MID", "UID", "APPID", "AREA", "OS", "CHANNEL", "LOGTYPE", "VERSION", "TS", "LOGDATE", "LOGHOUR"),
          zkUrl = Some("hadoop102,hadoop103,hadoop104:2181"))
      }

    )


    //4输出
    stream.print(1000)

    //5.采集
    ssc.start()

    //6.阻塞
    ssc.awaitTermination()
    
  }
}
