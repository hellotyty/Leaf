package com.sankuai.inf.leaf.autoconfigure;

import com.google.common.base.Preconditions;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import com.sankuai.inf.leaf.segment.model.SegmentBuffer;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas
 * @date 2022/3/13 16:17
 * @description
 * @version 1.0
 */
public class SegmentMonitorService {
    private Logger logger = LoggerFactory.getLogger(SegmentMonitorService.class);

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final SegmentIDGenImpl segmentIDGen;
    private final LeafProperty leafProperty;

    public SegmentMonitorService(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                 SegmentIDGenImpl segmentIDGen, LeafProperty leafProperty) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.segmentIDGen = segmentIDGen;
        this.leafProperty = leafProperty;
        registerCacheMapping();
        registerDbMapping();
        registerAddBizTagMapping();
        registerRemoveBizTagMapping();
    }

    @ResponseBody
    public Map<String, SegmentBufferResponse> getCache() {
        Map<String, SegmentBufferResponse> data = new HashMap<>();
        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enabled=true first");
        }
        Map<String, SegmentBuffer> cache = segmentIDGen.getCache();
        for (Map.Entry<String, SegmentBuffer> entry : cache.entrySet()) {
            SegmentBufferResponse response = new SegmentBufferResponse();
            SegmentBuffer buffer = entry.getValue();
            response.setInitOk(buffer.isInitOk());
            response.setKey(buffer.getKey());
            response.setPos(buffer.getCurrentPos());
            response.setNextReady(buffer.isNextReady());
            response.setMax0(buffer.getSegments()[0].getMax());
            response.setValue0(buffer.getSegments()[0].getValue().get());
            response.setStep0(buffer.getSegments()[0].getStep());

            response.setMax1(buffer.getSegments()[1].getMax());
            response.setValue1(buffer.getSegments()[1].getValue().get());
            response.setStep1(buffer.getSegments()[1].getStep());

            data.put(entry.getKey(), response);

        }
        logger.info("Cache info {}", data);
        return data;
    }

    @ResponseBody
    public List<LeafAlloc> getDb() {
        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enabled=true first");
        }
        List<LeafAlloc> items = segmentIDGen.getAllLeafAllocs();
        logger.info("DB info {}", items);
        return items;
    }

    @ResponseBody
    public LeafAlloc addBizTag(@RequestParam("bizTag") String bizTag, @RequestParam(value = "maxId") Long maxId,
                               @RequestParam("step") Integer step, @RequestParam("description") String description) {
        Preconditions
                .checkArgument(StringUtils.hasText(bizTag) && StringUtils.hasText(description),
                        "bizTag or description must not be null");
        Preconditions
                .checkArgument(maxId > 0 && step > 0, "maxId or step must be positive");

        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enabled=true first");
        }
        LeafAlloc temp = new LeafAlloc();
        temp.setKey(bizTag);
        temp.setStep(step);
        temp.setMaxId(maxId);
        temp.setDescription(description);
        temp.setUpdateTime(LocalDateTime.now());
        LeafAlloc leafAlloc = segmentIDGen.addLeafAlloc(temp);
        logger.info("add leafAlloc info {}", leafAlloc);
        return leafAlloc;
    }

    @ResponseBody
    public List<LeafAlloc> removeBizTag(@RequestParam("bizTag") String bizTag) {
        Preconditions
                .checkArgument(StringUtils.hasText(bizTag), "bizTag must not be null");

        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enabled=true first");
        }
        segmentIDGen.removeLeafAlloc(bizTag);
        logger.info("remove bizTag {}", bizTag);
        List<LeafAlloc> items = segmentIDGen.getAllLeafAllocs();
        logger.info("DB info {}", items);
        return items;
    }

    @SneakyThrows
    private void registerCacheMapping() {
        Class<?> entry = this.getClass();
        Method method = ReflectionUtils.findMethod(entry, "getCache");

        String basePath = leafProperty.getBasePath().startsWith("/") ? leafProperty.getBasePath().substring(1) :
                leafProperty.getBasePath();
        PatternsRequestCondition patterns = new PatternsRequestCondition(basePath + "/segment/cache");
        RequestMethodsRequestCondition methods = new RequestMethodsRequestCondition(RequestMethod.GET);
        RequestMappingInfo mapping = new RequestMappingInfo(patterns, methods, null, null, null, null, null);
        // spring 5.2.X
//        requestMappingHandlerMapping.registerMapping(mapping, this, method);
        // spring 5.1.x
        Method registerHandlerMethod =
                requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredMethod(
                        "registerHandlerMethod", Object.class, Method.class, Object.class);
        registerHandlerMethod.setAccessible(true);
        registerHandlerMethod.invoke(requestMappingHandlerMapping, this, method, mapping);
    }

    @SneakyThrows
    private void registerDbMapping() {
        Class<?> entry = this.getClass();
        Method method = ReflectionUtils.findMethod(entry, "getDb");

        String basePath = leafProperty.getBasePath().startsWith("/") ? leafProperty.getBasePath().substring(1) :
                leafProperty.getBasePath();
        PatternsRequestCondition patterns = new PatternsRequestCondition(basePath + "/segment/db");
        RequestMethodsRequestCondition methods = new RequestMethodsRequestCondition(RequestMethod.GET);
        RequestMappingInfo mapping = new RequestMappingInfo(patterns, methods, null, null, null, null, null);
        // spring 5.2.X
//        requestMappingHandlerMapping.registerMapping(mapping, this, method);
        // spring 5.1.x
        Method registerHandlerMethod =
                requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredMethod(
                        "registerHandlerMethod", Object.class, Method.class, Object.class);
        registerHandlerMethod.setAccessible(true);
        registerHandlerMethod.invoke(requestMappingHandlerMapping, this, method, mapping);
    }

    @SneakyThrows
    private void registerAddBizTagMapping() {
        Class<?> entry = this.getClass();
        Method method = ReflectionUtils.findMethod(entry, "addBizTag", String.class, Long.class, Integer.class,
                String.class);

        String basePath = leafProperty.getBasePath().startsWith("/") ? leafProperty.getBasePath().substring(1) :
                leafProperty.getBasePath();
        PatternsRequestCondition patterns = new PatternsRequestCondition(basePath + "/segment/biz-tag");
        RequestMethodsRequestCondition methods = new RequestMethodsRequestCondition(RequestMethod.POST);
        RequestMappingInfo mapping = new RequestMappingInfo(patterns, methods, null, null, null, null, null);
        // spring 5.2.X
//        requestMappingHandlerMapping.registerMapping(mapping, this, method);
        // spring 5.1.x
        Method registerHandlerMethod =
                requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().getDeclaredMethod(
                        "registerHandlerMethod", Object.class, Method.class, Object.class);
        registerHandlerMethod.setAccessible(true);
        registerHandlerMethod.invoke(requestMappingHandlerMapping, this, method, mapping);
    }

    @SneakyThrows
    private void registerRemoveBizTagMapping() {
        Class<?> entry = this.getClass();
        Method method = ReflectionUtils.findMethod(entry, "removeBizTag", String.class);

        String basePath = leafProperty.getBasePath().startsWith("/") ? leafProperty.getBasePath().substring(1) :
                leafProperty.getBasePath();
        PatternsRequestCondition patterns = new PatternsRequestCondition(basePath + "/segment/biz-tag");
        RequestMethodsRequestCondition methods = new RequestMethodsRequestCondition(RequestMethod.DELETE);
        RequestMappingInfo mapping = new RequestMappingInfo(patterns, methods, null, null, null, null, null);
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
