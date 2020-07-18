package com.atguigu.realtime.app
import java.util

import com.alibaba.fastjson.JSON
import com.atguigu.realtime.bean.{AlertInfo, EventLog, OrderInfo}
import com.atguigu.realtime.util.MyKafkaUtil.kafkaParams
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

import scala.util.control.Breaks._

/**
 * @author myway
 * @create 2020-07-18 8:51
 */
// todo 需求：同一设备，5分钟内三次及以上用不同账号登录并领取优惠劵，
// todo 并且在登录到领劵过程中没有浏览商品。
// todo 同时达到以上要求则产生一条预警日志。
// todo 同一设备，每分钟只记录一次预警。
object AlertApp extends BaseApp {
  override def run(ssc: StreamingContext, topic: String): Unit = {
    val kafkastream = KafkaUtils.createDirectStream[String,String](ssc,PreferConsistent,Subscribe[String,String](Set(topic),kafkaParams))
    val stream: DStream[String] = kafkastream.map(record => record.value())
    //3.转换
    val orderinfoStream: DStream[EventLog] = stream.map(json=>JSON.parseObject(json,classOf[EventLog]))
    //4窗口
    val eventLogStream: DStream[EventLog] = orderinfoStream.window(Minutes(5),Seconds(6))
    //5按照设备ID分组
    val groupStream: DStream[(String, Iterable[EventLog])] = eventLogStream.map(event=>(event.mid,event)).groupByKey()
    //6产生预警信息
    val alertInfoStream: DStream[(Boolean, AlertInfo)] = groupStream.map {
      case (mid, log) => {
        //领取优惠券的用户
        val uidSet: util.HashSet[String] = new java.util.HashSet[String]
        //5分钟设备存储的事件
        val eventList: util.ArrayList[String] = new util.ArrayList[String]()
        //存储优惠券对应id
        val itemSet = new util.HashSet[String]()
        var isClickItem = false
        breakable(
          log.foreach(elem => {
            eventList.add(elem.eventId)
            //只关注领取优惠券的用户
            elem.eventId match {
              case "coupon" =>
                uidSet.add(elem.uid)
                itemSet.add(elem.itemId)
              case "clickItem" =>
                //一旦出现浏览商品，则不会产生预警信息
                isClickItem=true
                break
              case _ => //其他事件不作处理
            }
          }))
        (!isClickItem && uidSet.size() >= 3, AlertInfo(mid, uidSet, itemSet, eventList, System.currentTimeMillis()))
      }

    }
    //7.把数据写到es中
    alertInfoStream.print(100)
//    alertInfoStream.filter(_._1).foreachRDD(rdd=>{})


  }
}
