package com.sankuai.inf.leaf.segment.dao;

import com.sankuai.inf.leaf.segment.model.LeafAlloc;

import java.util.List;

public interface IDAllocDao {
     List<LeafAlloc> getAllLeafAllocs();
     LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);
     LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);
     LeafAlloc insertLeafAlloc(LeafAlloc leafAlloc);
     int deleteLeafAlloc(String tag);
     List<String> getAllTags();
}
