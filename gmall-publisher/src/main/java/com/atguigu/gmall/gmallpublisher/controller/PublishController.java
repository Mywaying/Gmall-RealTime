package com.atguigu.gmall.gmallpublisher.controller;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.gmallpublisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author myway
 * @create 2020-07-15 18:42
 */
@RestController
public class PublishController {

    @Autowired
    PublisherService service;

    @GetMapping("/realtime-total")
    public String realtimeTotal(@RequestParam("date") String date){
        Long dau = service.getDau(date);
        Double totalAmount = service.getTotalAmount(date);
        ArrayList<Map<String,String>> result = new ArrayList<>();
        Map<String, String> map1 = new HashMap<>();
        map1.put("id", "dau");
        map1.put("name", "新增日活");
        map1.put("value", dau.toString());
        result.add(map1);

        Map<String, String> map2 = new HashMap<>();
        map2.put("id", "new_mid");
        map2.put("name", "新增设备");
        map2.put("value", "223");
        result.add(map2);

        Map<String, String> map3 = new HashMap<>();
        map3.put("id", "order_amount");
        map3.put("name", "新增交易额");
        map3.put("value", totalAmount.toString());
        result.add(map3);

        return JSON.toJSONString(result);
    }


    @GetMapping("/realtime-hour")
    public String getRealtimeHour(String id, String date) {

        if ("dau".equals(id)) {
            Map<String, Long> today = service.getHourDau(date);
            Map<String, Long> yesterday = service.getHourDau(getYesterday(date));
            /*
            {"yesterday":{"11":383,"12":123,"17":88,"19":200 },
                "today":{"12":38,"13":1233,"17":123,"19":688 }}
             */
            Map<String, Map<String, Long>> result = new HashMap<>();
            result.put("today", today);
            result.put("yesterday", yesterday);

            return JSON.toJSONString(result);
        } else if("order_amount".equals(id)){
            Map<String, Double> today = service.getHourAmount(date);
            Map<String, Double> yesterday = service.getHourAmount(getYesterday(date));
            /*
                {"yesterday":{"11":383,"12":123,"17":88,"19":200 },
                    "today":{"12":38,"13":1233,"17":123,"19":688 }}

            */
            Map<String, Map<String, Double>> result = new HashMap<>();
            result.put("today", today);
            result.put("yesterday", yesterday);
            return JSON.toJSONString(result);
        } else {
            return null;
        }

}

    private String getYesterday(String date) {
        return LocalDate.parse(date).plusDays(-1).toString();
    }
    }
