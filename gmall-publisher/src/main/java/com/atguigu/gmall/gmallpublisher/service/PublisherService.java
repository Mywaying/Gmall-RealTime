package com.atguigu.gmall.gmallpublisher.service;

import java.util.List;
import java.util.Map;

/**
 * @author myway
 * @create 2020-07-15 18:30
 */
public interface PublisherService {

    Long getDau(String date);

    Map<String, Long> getHourDau(String date);

    Double getTotalAmount(String date);

    Map<String, Double> getHourAmount(String date);
}
