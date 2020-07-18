package com.atguigu.gmall.gmalllogger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author myway
 * @create 2020-07-13 18:08
 */
// @Controller
@RestController   // 等价于: @Controller + @ResponseBody
public class LoggerController {
    //    @RequestMapping(value = "/log", method = RequestMethod.POST)
    //    @ResponseBody  //表示返回值是一个 字符串, 而不是 页面名
    @PostMapping("/log")  // 等价于: @RequestMapping(value = "/log", method = RequestMethod.POST)
    public String doLog(@RequestParam("log") String log) {

        log = addTs(log);

        saveToDisk(log);

        sendToKafka(log);

        return "success";
    }
    @Autowired
    KafkaTemplate kafka;
    private void sendToKafka(String log) {
        if (log.contains("\"startup\"")){
            kafka.send(Constant.START_TOPIC,log);
        }else {
            kafka.send(Constant.EVENT_TOPIC,log);
        }
    }

    Logger logger = LoggerFactory.getLogger(LoggerController.class);
    private void saveToDisk(String log) {
        logger.info(log);
    }

    private String addTs(String log) {
        JSONObject obj = JSON.parseObject(log);
        obj.put("ts", System.currentTimeMillis());
        return JSON.toJSONString(obj);
    }

    /**
     * 业务:
     *
     * 1. 给日志添加时间戳 (客户端的时间有可能不准, 所以使用服务器端的时间)

     * 2. 日志落盘
     *
     * 3. 日志发送 kafka
     */



}