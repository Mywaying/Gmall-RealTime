package com.atguigu.gmall.gmallpublisher.service;

import com.atguigu.gmall.gmallpublisher.mapper.MapperDAU;
import com.atguigu.gmall.gmallpublisher.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author myway
 * @create 2020-07-15 18:33
 */
@Service
public class PublisherServiceimpl implements PublisherService {

    @Autowired
    MapperDAU dau;

    @Override
    public Long getDau(String date) {
        Long dau = this.dau.getDau(date);
        return dau;
    }

    @Override
    public Map<String, Long> getHourDau(String date) {
        List<Map<String, Object>> hourDau = dau.getHourDau(date);
        Map<String, Long> result = new HashMap<String, Long>();
        for (Map<String, Object> stringObjectMap : hourDau) {
            String key = stringObjectMap.get("LOGHOUR").toString();
            Long count = (Long)stringObjectMap.get("COUNT");
            result.put(key,count);
        }
        return result;
    }

    @Autowired
    OrderMapper ordertotal;
    @Override
    public Double getTotalAmount(String date) {
        Double result = ordertotal.getTotalAmount(date);
        return result == null ? 0 : result;

    }

    @Override
    public Map<String, Double> getHourAmount(String date) {
        List<Map<String, Object>> hourAmountList = ordertotal.getHourAmount(date);
        HashMap<String, Double> resultMap = new HashMap<>();
        for (Map<String, Object> map : hourAmountList) {
            String key = (String) map.get("CREATE_HOUR");
            Double value = ((BigDecimal) map.get("SUM")).doubleValue();
            resultMap.put(key, value);
        }
        return resultMap;
    }
}
