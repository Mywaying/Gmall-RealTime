package com.atguigu.realtime.util

import java.io.InputStream
import java.util.Properties

/**
 * @author myway
 * @create 2020-07-15 8:50
 */
object configUtil {

  def getProperty(fileName :String ,name : String) ={
    val is: InputStream = configUtil.getClass.getClassLoader.getResourceAsStream(fileName)
    val ps = new Properties()
    ps.load(is)
    val value: String = ps.getProperty(name)
    value
  }

  def getValue(name : String) ={
    getProperty("config.properties",name)
  }



}
