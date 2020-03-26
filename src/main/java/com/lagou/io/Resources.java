package com.lagou.io;

import java.io.InputStream;

/**
 * @ClassNameResources
 * @Description
 * @Author
 * @Date2020/3/26 19:03
 * @Version V1.0
 **/
public class Resources {

    // 读取配置文件以流的方式存储到内存中
    public static InputStream getResourceAsStream(String path){
        InputStream resourceAsStream = Resources.class.getClassLoader().getResourceAsStream(path);
        return resourceAsStream;
    }

}
