package com.sankuai.inf.leaf.autoconfigure;

import com.sankuai.inf.leaf.snowflake.SnowflakeIDGenImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author 80234613 唐圆
 * @date 2022/3/14 15:11
 * @descripton
 * @version 1.0
 */
@ConditionalOnProperty(prefix = "leaf.snowflake", name = "enabled", havingValue = "true")
@Configuration
public class SnowflakeConfig {
    private Logger logger = LoggerFactory.getLogger(SnowflakeConfig.class);

    @Bean
    public SnowflakeIDGenImpl snowflakeIDGen(LeafProperty leafProperty) {
        SnowflakeIDGenImpl idGen = new SnowflakeIDGenImpl(leafProperty.getSnowflake().getZkAddress(),
                leafProperty.getSnowflake().getPort(), leafProperty.getName());
        if (idGen.init()) {
            logger.info("SnowflakeIDGenImpl Init Successfully");
            return idGen;
        } else {
            throw new InitException("SnowflakeIDGenImpl Init Fail");
        }
    }

    @Bean
    @ConditionalOnProperty(prefix = "leaf.snowflake", name = "monitorable", havingValue = "true", matchIfMissing = true)
    public SnowflakeMonitorService snowflakeMonitorService(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                                           LeafProperty leafProperty) {
        return new SnowflakeMonitorService(requestMappingHandlerMapping, leafProperty);
    }
}
