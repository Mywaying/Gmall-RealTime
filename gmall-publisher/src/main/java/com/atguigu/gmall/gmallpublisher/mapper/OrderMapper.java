package com.atguigu.gmall.gmallpublisher.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author myway
 * @create 2020-07-17 15:02
 */
public interface OrderMapper {
    Double getTotalAmount(String date);

    List<Map<String, Object>> getHourAmount(String date);
}
