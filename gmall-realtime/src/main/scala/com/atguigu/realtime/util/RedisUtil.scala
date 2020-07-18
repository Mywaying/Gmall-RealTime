package com.atguigu.realtime.util

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/**
 * @author myway
 * @create 2020-07-15 9:08
 */
object RedisUtil {

  val conf = new JedisPoolConfig
  conf.setMaxTotal(100)
  conf.setMaxIdle(30)
  conf.setMinIdle(10)
  conf.setBlockWhenExhausted(true)
  conf.setMaxWaitMillis(1000)
  conf.setTestOnCreate(true)
  conf.setTestOnBorrow(true)
  conf.setTestOnReturn(true)
  val pool = new JedisPool(conf,"hadoop102",6378)

  def getClient={
    pool.getResource
  }
}
