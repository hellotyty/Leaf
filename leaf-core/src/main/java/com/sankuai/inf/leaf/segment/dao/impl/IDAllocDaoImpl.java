package com.sankuai.inf.leaf.segment.dao.impl;

import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.dao.IDAllocMapper;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

public class IDAllocDaoImpl implements IDAllocDao {

    SqlSessionFactory sqlSessionFactory;

    public IDAllocDaoImpl(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(IDAllocMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public List<LeafAlloc> getAllLeafAllocs() {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            return sqlSession.selectList("com.sankuai.inf.leaf.segment.dao.IDAllocMapper.getAllLeafAllocs");
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(String tag) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            LeafAlloc temp = new LeafAlloc();
            temp.setKey(tag);
            temp.setUpdateTime(LocalDateTime.now());
            sqlSession.update("com.sankuai.inf.leaf.segment.dao.IDAllocMapper.updateMaxId", temp);
            LeafAlloc result = sqlSession.selectOne("com.sankuai.inf.leaf.segment.dao.IDAllocMapper.getLeafAlloc", tag);
            sqlSession.commit();
            return result;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            sqlSession.update("com.sankuai.inf.leaf.segment.dao.IDAllocMapper.updateMaxIdByCustomStep", leafAlloc);
            LeafAlloc result = sqlSession.selectOne("com.sankuai.inf.leaf.segment.dao.IDAllocMapper.getLeafAlloc",
                    leafAlloc.getKey());
            sqlSession.commit();
            return result;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public LeafAlloc insertLeafAlloc(LeafAlloc leafAlloc) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            sqlSession.insert("com.sankuai.inf.leaf.segment.dao.IDAllocMapper.insertLeafAlloc", leafAlloc);
            LeafAlloc result = sqlSession.selectOne("com.sankuai.inf.leaf.segment.dao.IDAllocMapper.getLeafAlloc",
                    leafAlloc.getKey());
            sqlSession.commit();
            return result;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public int deleteLeafAlloc(String tag) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            int rows = sqlSession.delete("com.sankuai.inf.leaf.segment.dao.IDAllocMapper.deleteLeafAlloc", tag);
            sqlSession.commit();
            return rows;
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<String> getAllTags() {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            return sqlSession.selectList("com.sankuai.inf.leaf.segment.dao.IDAllocMapper.getAllTags");
        } finally {
            sqlSession.close();
        }
    }
}
