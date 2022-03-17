package com.sankuai.inf.leaf.server.service;

import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.snowflake.SnowflakeIDGenImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("SnowflakeService")
public class SnowflakeService {
    private Logger logger = LoggerFactory.getLogger(SnowflakeService.class);

    private final SnowflakeIDGenImpl idGen;

    public SnowflakeService(@Autowired(required = false) SnowflakeIDGenImpl idGen) {
        this.idGen = idGen;
    }

    public Result getId(String key) {
        return idGen.get(key);
    }
}
