package com.sankuai.inf.leaf.autoconfigure;

import lombok.SneakyThrows;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 80234613 唐圆
 * @date 2022/3/14 15:04
 * @descripton
 * @version 1.0
 */
public class SnowflakeManagementService {
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final LeafProperty leafProperty;

    public SnowflakeManagementService(RequestMappingHandlerMapping requestMappingHandlerMapping, LeafProperty leafProperty) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.leafProperty = leafProperty;
    }

    /**
     * the output is like this:
     * {
     *   "timestamp": "1567733700834(2019-09-06 09:35:00.834)",
     *   "sequenceId": "3448",
     *   "workerId": "39"
     * }
     */
    @ResponseBody
    public Map<String, String> decodeSnowflakeId(@RequestParam("snowflakeId") String snowflakeIdStr) {
        Map<String, String> map = new HashMap<>();
        try {
            long snowflakeId = Long.parseLong(snowflakeIdStr);

            long originTimestamp = (snowflakeId >> 22) + 1288834974657L;
            Date date = new Date(originTimestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            map.put("timestamp", String.valueOf(originTimestamp) + "(" + sdf.format(date) + ")");

            long workerId = (snowflakeId >> 12) ^ (snowflakeId >> 22 << 10);
            map.put("workerId", String.valueOf(workerId));

            long sequence = snowflakeId ^ (snowflakeId >> 12 << 12);
            map.put("sequenceId", String.valueOf(sequence));
        } catch (NumberFormatException e) {
            map.put("errorMsg", "snowflake Id反解析发生异常!");
        }
        return map;
    }

    @SneakyThrows
    private void registerDecodeSnowflakeIdMapping() {
        Class<?> entry = this.getClass();
        Method method = ReflectionUtils.findMethod(entry, "decodeSnowflakeId", String.class);

        String basePath = leafProperty.getBasePath().startsWith("/") ? leafProperty.getBasePath().substring(1) :
                leafProperty.getBasePath();
        PatternsRequestCondition patterns = new PatternsRequestCondition(basePath + "/snowflake/decodeId");
        RequestMethodsRequestCondition methods = new RequestMethodsRequestCondition(RequestMethod.GET);
        ParamsRequestCondition params = new ParamsRequestCondition("snowflakeIdStr");
        RequestMappingInfo mapping = new RequestMappingInfo(patterns, methods, params, null, null, null, null);
        // spring 5.2.X
//        requestMappingHandlerMapping.registerMapping(mapping, this, method);
        // spring 5.1.x
        Method registerHandlerMethod =
                requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredMethod(
                        "registerHandlerMethod", Object.class, Method.class, Object.class);
        registerHandlerMethod.setAccessible(true);
        registerHandlerMethod.invoke(requestMappingHandlerMapping, this, method, mapping);
    }
}
