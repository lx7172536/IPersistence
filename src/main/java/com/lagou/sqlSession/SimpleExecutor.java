package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.util.List;

/**
 * @program: IPersistence
 * @description:
 * @author: lixin
 * @create: 2020-03-31 00:42
 **/
public class SimpleExecutor implements Executor {

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) {
        return null;
    }
}
