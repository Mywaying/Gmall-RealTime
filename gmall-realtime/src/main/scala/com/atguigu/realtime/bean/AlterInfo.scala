package com.atguigu.realtime.bean

/**
 * @author myway
 * @create 2020-07-18 9:37
 */
case class AlertInfo(mid: String,
                     uids: java.util.HashSet[String],
                     itemIds: java.util.HashSet[String],
                     events: java.util.List[String],
                     ts: Long)
