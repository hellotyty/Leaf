package com.sankuai.inf.leaf.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Thomas
 * @date 2022/3/13 21:05
 * @description
 * @version 1.0
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "leaf")
public class LeafProperty {

    private String name;

    private String basePath = "/leaf";

    private SegmentProperty segment;

    private SnowflakeProperty snowflake;

    @Getter
    @Setter
    @ToString
    public static class SegmentProperty {

        private boolean enabled;

        private String dataSourceName;

        private String[] autoInitBizTags;

        private boolean manageable;
    }

    @Getter
    @Setter
    @ToString
    public static class SnowflakeProperty {

        private boolean enabled;

        private String zkAddress;

        private int port;

        private boolean manageable;
    }
}
