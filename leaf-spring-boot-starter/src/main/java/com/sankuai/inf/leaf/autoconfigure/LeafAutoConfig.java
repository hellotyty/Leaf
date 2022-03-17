package com.sankuai.inf.leaf.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Thomas
 * @date 2022/3/13 16:18
 * @description
 * @version 1.0
 */
@EnableConfigurationProperties(LeafProperty.class)
@Import({SegmentConfig.class, SnowflakeConfig.class})
@Configuration
public class LeafAutoConfig {

}
