package com.lagou.config;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * @program: IPersistence
 * @description:
 * @author: lixin
 * @create: 2020-03-30 21:22
 **/
public class XMLMapperBuilder {
    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        //由于id的唯一标识需要拿到namespace.id，所以这里通过文档根元素拿到namespace
        String namespace = rootElement.attributeValue("namespace");

        //注意返回泛型是Element
        List<Element> selectList = rootElement.selectNodes("//select");
        for (Element element : selectList) {
            MappedStatement mappedStatement = new MappedStatement();
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String paramterType = element.attributeValue("paramterType");
            String textTrim = element.getTextTrim();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParamterType(paramterType);
            mappedStatement.setSql(textTrim);
            String statementId = namespace+"."+id;
            configuration.getMappedStatementMap().put(statementId,mappedStatement);
        }
    }
}
