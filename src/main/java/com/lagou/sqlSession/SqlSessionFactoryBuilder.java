package com.lagou.sqlSession;

import com.lagou.config.XmlConfigBuilder;
import com.lagou.pojo.Configuration;
import org.dom4j.DocumentException;

import java.beans.PropertyVetoException;
import java.io.InputStream;

/**
 * @program: IPersistence
 * @description:
 * @author: lixin
 * @create: 2020-03-26 23:38
 **/
public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(InputStream in) throws DocumentException, PropertyVetoException {
        //第一：使用dom4j技术解析配置文件，将解析出来的内容封装到Configuration对象中
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder();
        Configuration configuration = xmlConfigBuilder.parseCnfig(in);


        //第二：创建sqlSessionFactory对象

        return null;
    }
}
