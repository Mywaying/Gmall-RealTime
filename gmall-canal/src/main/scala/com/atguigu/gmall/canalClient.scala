package com.atguigu.gmall

import java.net.InetSocketAddress
import java.util

import com.alibaba.fastjson.JSONObject
import com.alibaba.otter.canal.client.{CanalConnector, CanalConnectors}
import com.alibaba.otter.canal.protocol.CanalEntry.{EventType, RowChange}
import com.alibaba.otter.canal.protocol.{CanalEntry, Message}
import com.atguigu.common.Constant
import com.google.protobuf.ByteString

import scala.collection.JavaConverters._

/**
 * @author myway
 * @create 2020-07-17 11:19
 */
object canalClient {

  def handleData(rowdatas: util.List[CanalEntry.RowData], tableName: String, eventType: CanalEntry.EventType) = {
//    println(tableName)
    if (tableName=="order_info" && eventType == EventType.INSERT && rowdatas !=null && !rowdatas.isEmpty){
      for (rowdata <- rowdatas.asScala) {
        val obj = new JSONObject()
        val columns: util.List[CanalEntry.Column] = rowdata.getAfterColumnsList
        for (column <- columns.asScala) {
          val key: String = column.getName
          val value: String = column.getValue
          obj.put(key,value)
          }
        MyKafkaUtil.sendToKafka(Constant.TOPIC_ORDER_INFO,obj.toJSONString)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    //1.连接canal
    val address = new InetSocketAddress("hadoop102",11111)
    val connector: CanalConnector = CanalConnectors.newSingleConnector(address,"example","","")
    connector.connect()
    //2.拉取数据
    connector.subscribe("gmall0213.*")
    while (true){
      //100表示最多拉取由于100条sql导致变化的数据
      val message: Message = connector.get(100)
      val entries: util.List[CanalEntry.Entry] = message.getEntries


//      println(entries.toString)

      if (entries != null && !entries.isEmpty){
        for (entry <- entries.asScala) {
          //entry 类型
          if (entry != null && entry.hasEntryType && entry.getEntryType == CanalEntry.EntryType.ROWDATA){
            val value: ByteString = entry.getStoreValue
            val rowChange: RowChange = RowChange.parseFrom(value)
            val rowdatas: util.List[CanalEntry.RowData] = rowChange.getRowDatasList
           handleData(rowdatas,entry.getHeader.getTableName,rowChange.getEventType)
          }
        }
      }else{
        println("no data")
        Thread.sleep(3000)
      }
    }
    //3解析


  }
}
