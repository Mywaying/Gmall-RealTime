package com.atguigu.realtime.bean

import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author myway
 * @create 2020-07-15 8:59
 */
case class startupLog(mid: String,
                      uid: String,
                      appId: String,
                      area: String,
                      os: String,
                      channel: String,
                      logType: String,
                      version: String,
                      ts: Long,
                      var logDate: String,
                      var logHour: String
                     ){

  private val date = new Date(ts)
  logDate = new SimpleDateFormat("yyyy-MM-dd").format(date)
  logHour = new SimpleDateFormat("HH").format(date)
}

