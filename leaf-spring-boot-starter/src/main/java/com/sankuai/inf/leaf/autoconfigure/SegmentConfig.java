package com.sankuai.inf.leaf.autoconfigure;

import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.impl.IDAllocDaoImpl;
import org.apache.ibatis.reflection.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.yaml.snakeyaml.util.ArrayUtils;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Map;

/**
 * @author 80234613 唐圆
 * @date 2022/3/14 15:11
 * @descripton
 * @version 1.0
 */
@ConditionalOnProperty(prefix = "leaf.segment", name = "enabled", havingValue = "true")
@Configuration
public class SegmentConfig {
    private Logger logger = LoggerFactory.getLogger(SegmentConfig.class);

    @Bean
    public SegmentIDGenImpl segmentIDGen(LeafProperty leafProperty, Map<String, DataSource> dataSourceMap) {
        // Config dataSource
        DataSource dataSource = null;

        if (StringUtils.hasText(leafProperty.getSegment().getDataSourceName())) {
            dataSource = dataSourceMap.get(leafProperty.getSegment().getDataSourceName());
            if (dataSource == null) {
                throw new InitException("DataSource not exist");
            }
        } else {
            if (dataSourceMap.size() == 1) {
                for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
                    dataSource = entry.getValue();
                }
            } else {
                throw new InitException("DataSource size error");
            }
        }

        // Config Dao
        IDAllocDao dao = new IDAllocDaoImpl(dataSource);

        // Config ID Gen
        SegmentIDGenImpl idGen = new SegmentIDGenImpl();
        idGen.setDao(dao);
        if (idGen.init()) {
            logger.info("SegmentIDGenImpl Init Successfully");
        } else {
            throw new InitException("SegmentIDGenImpl Init Fail");
        }

        // Auto Init
        String[] autoInitBizTags = leafProperty.getSegment().getAutoInitBizTags();
        if (autoInitBizTags != null) {
            Arrays.stream(autoInitBizTags).forEach(i -> {
                Result result = idGen.get(i);
                if (Status.EXCEPTION == result.getStatus()) {
                    throw new InitException(String.format("BizTag %s Init Fail", i));
                }
            });
        }

        return idGen;
    }

    @Bean
    @ConditionalOnProperty(prefix = "leaf.segment", name = "monitorable", havingValue = "true", matchIfMissing = true)
    public SegmentMonitorService segmentMonitorService(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                                       SegmentIDGenImpl segmentIDGen, LeafProperty leafProperty) {
        return new SegmentMonitorService(requestMappingHandlerMapping, segmentIDGen, leafProperty);
    }
}
