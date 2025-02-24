package com.sankuai.inf.leaf.server;

import com.sankuai.inf.leaf.autoconfigure.LeafProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class LeafServerApplication {
    private static Logger LOGGER = LoggerFactory.getLogger(LeafServerApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(LeafServerApplication.class, args);
        LOGGER.info("LeafProperty: {}", context.getBean(LeafProperty.class));
    }
}
