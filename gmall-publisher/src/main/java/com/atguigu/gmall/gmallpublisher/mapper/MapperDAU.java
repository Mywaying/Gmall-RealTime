package com.atguigu.gmall.gmallpublisher.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @author myway
 * @create 2020-07-15 18:05
 */
public interface MapperDAU {
    Long getDau(String date);

    List<Map<String, Object>> getHourDau(String date);
}
