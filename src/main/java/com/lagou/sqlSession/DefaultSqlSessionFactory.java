package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;

/**
 * @program: IPersistence
 * @description:
 * @author: lixin
 * @create: 2020-03-30 23:25
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory{

    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
