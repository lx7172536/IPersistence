package com.lagou.sqlSession;

import com.lagou.config.BoundSql;
import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.utils.GenericTokenParser;
import com.lagou.utils.ParameterMapping;
import com.lagou.utils.ParameterMappingTokenHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: IPersistence
 * @description:
 * @author: lixin
 * @create: 2020-03-31 00:42
 **/
public class SimpleExecutor implements Executor {

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IntrospectionException, InstantiationException, InvocationTargetException {
        // 1.注册驱动，获取链接
        Connection connection = configuration.getDataSource().getConnection();

        // 2.获取sql语句
            //转换sql语句 转换过程中需要队#{}里面的值进行解析
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        // 3.获取预处理对象:preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        // 4.设置参数
            //获取参数的全路径
        String paramterType = mappedStatement.getParamterType();
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        Class<?> paramterClass = getClassType(paramterType);

        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            //通过反射取出实体对应属性
            Field declaredField = paramterClass.getDeclaredField(content);
            //通过设置属性的暴力访问拿到对应的值
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);

            //由于sql索引是从1开始 所以需要设置i+1
            preparedStatement.setObject(i+1,o);
        }

        // 5.执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
            //获取到返回实体的全路径并获取对应Class
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);
        Object o = resultTypeClass.newInstance();
        ArrayList<Object> objects = new ArrayList<>();

        // 6.封装返回结果集
        while (resultSet.next()){
            //获取到元数据 元数据包括每行的列数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <metaData.getColumnCount() ; i++) {
                //字段名
                String columnName = metaData.getColumnName(i);
                //字段值
                Object value = resultSet.getObject(columnName);

                //使用反射或者内省，根据数据库表和实体的对应关系，完成结果集的封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o,value);
            }
            objects.add(o);
        }

        return (List<E>) objects;
    }

    /**
    *功能描述：通过传入的paramterType拿到实体的全路径
    * @return: java.lang.Class<?>
    * @Author: lixin
    * @Date: 2020/4/2 0:14
    */
    private Class<?> getClassType(String paramterType) throws ClassNotFoundException {
        if(paramterType!=null && paramterType!= ""){
            Class<?> aClass = Class.forName(paramterType);
            return aClass;
        }
        return null;
    }

    /**
    *功能描述:完成对#{}的解析工作：1.将#{}使用?进行代替 2.解析出#{}里面的值进行存储
     * @param sql
    * @return: com.lagou.config.BoundSql
    * @Author: lixin
    * @Date: 2020/3/31 20:40
    */
    private BoundSql getBoundSql(String sql) {
        //标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        //标记解析器
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{","}",parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();

        BoundSql boundSql = new BoundSql(parseSql,parameterMappings);

        return boundSql;
    }
}
