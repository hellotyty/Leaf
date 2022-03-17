package com.sankuai.inf.leaf.server.service;

import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.segment.SegmentIDGenImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SegmentService {
    private Logger logger = LoggerFactory.getLogger(SegmentService.class);

    private final SegmentIDGenImpl idGen;

    public SegmentService(@Autowired(required = false) SegmentIDGenImpl idGen) {
        this.idGen = idGen;
    }

    public Result getId(String key) {
        return idGen.get(key);
    }

    public SegmentIDGenImpl getIdGen() {
        return idGen;
    }
}
