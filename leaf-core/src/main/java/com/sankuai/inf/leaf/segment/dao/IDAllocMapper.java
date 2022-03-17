package com.sankuai.inf.leaf.segment.dao;

import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface IDAllocMapper {

    @Select("SELECT BIZ_TAG, MAX_ID, STEP, DESCRIPTION, UPDATE_TIME FROM LEAF_ALLOC")
    @Results(value = {
            @Result(column = "BIZ_TAG", property = "key"),
            @Result(column = "MAX_ID", property = "maxId"),
            @Result(column = "STEP", property = "step"),
            @Result(column = "DESCRIPTION", property = "description"),
            @Result(column = "UPDATE_TIME", property = "updateTime")
    })
    List<LeafAlloc> getAllLeafAllocs();

    @Select("SELECT BIZ_TAG, MAX_ID, STEP, DESCRIPTION, UPDATE_TIME FROM LEAF_ALLOC WHERE BIZ_TAG = #{tag}")
    @Results(value = {
            @Result(column = "BIZ_TAG", property = "key"),
            @Result(column = "MAX_ID", property = "maxId"),
            @Result(column = "STEP", property = "step"),
            @Result(column = "DESCRIPTION", property = "description"),
            @Result(column = "UPDATE_TIME", property = "updateTime")
    })
    LeafAlloc getLeafAlloc(@Param("tag") String tag);

    @Update(value = "UPDATE LEAF_ALLOC SET MAX_ID = MAX_ID + STEP, UPDATE_TIME = #{updateTime} WHERE BIZ_TAG = #{key}")
    int updateMaxId(@Param("leafAlloc") LeafAlloc leafAlloc);

    @Insert(value = "INSERT INTO LEAF_ALLOC (BIZ_TAG, MAX_ID, STEP, DESCRIPTION, UPDATE_TIME) VALUES (#{key}, " +
            "#{maxId}, #{step}, #{description}, #{updateTime})")
    int insertLeafAlloc(@Param("leafAlloc") LeafAlloc leafAlloc);

    @Delete(value = "DELETE FROM LEAF_ALLOC WHERE BIZ_TAG = #{tag}")
    int deleteLeafAlloc(@Param("tag") String tag);

    @Update(value = "UPDATE LEAF_ALLOC SET MAX_ID = MAX_ID + #{step}, UPDATE_TIME = #{updateTime} WHERE BIZ_TAG = " +
            "#{key}")
    int updateMaxIdByCustomStep(@Param("leafAlloc") LeafAlloc leafAlloc);

    @Select("SELECT BIZ_TAG FROM LEAF_ALLOC")
    List<String> getAllTags();
}
