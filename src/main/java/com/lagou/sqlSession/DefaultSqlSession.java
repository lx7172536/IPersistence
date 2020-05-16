package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.List;

/**
 * @program: IPersistence
 * @description:
 * @author: lixin
 * @create: 2020-03-30 23:58
 **/
public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws SQLException, IllegalAccessException, IntrospectionException, InstantiationException, NoSuchFieldException, InvocationTargetException, ClassNotFoundException {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<Object> list = simpleExecutor.query(configuration, mappedStatement, params);
        return (List<E>) list;
    }

    @Override
    public <T> T  selectOne(String statementId, Object... params) throws SQLException, IllegalAccessException, IntrospectionException, InstantiationException, ClassNotFoundException, InvocationTargetException, NoSuchFieldException {
        List<Object> objects = selectList(statementId, params);
        if(objects.size()==1){
            return (T) objects.get(0);
        }else {
            throw new RuntimeException("查询结果为空或者查询结果过多");
        }
    }

    @Override
    public <T> T getMapper(final Class<?> mapperClass) {
        //使用JDK动态代理来为Dao接口生成代理对象
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSession.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //proxy：当前代理对象的应用  method：当前被调用方法的引用 args：传递的参数
                // 底层还是去执行JDBC代码 //根据不同情况，来调用selectList或者selectOne
                // 准备参数 1：statmentid:sql语句的唯一标识：namespace.id=接口全限定名.方法名
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();

                String statmentId = className+"."+methodName;

                //准备参数  2：args 即传过来的实体对象
                //获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();

                // 判断是否进行了 泛型类型参数化
                // 如果有泛型 则表示返回了list 否则返回实体
                if(genericReturnType instanceof ParameterizedType){
                    List<Object> objects = selectList(statmentId, args);
                    return  objects;
                }

                return selectOne(statmentId,args);
            }
        });

        return (T) proxyInstance;
    }
}
