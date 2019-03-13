package com.alibaba.json.bvt.kotlin;


import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


/**
 * Created by wenshao on 10/08/2017.
 */
public class ResponseKotlinTest extends TestCase {
    public void test_kotlin() throws Exception {
        ResponseKotlinTest.ExtClassLoader classLoader = new ResponseKotlinTest.ExtClassLoader();
        Class clazz = classLoader.loadClass("ResponseKotlin");
        String json = "{\"text\":\"robohorse\",\"value\":99}";
        Object obj = JSON.parseObject(json, clazz);
        TestCase.assertEquals("{\"text\":\"robohorse\",\"value\":99}", JSON.toJSONString(obj));
        String json2 = "{\"text\":\"robohorse\"}";
        Object obj2 = JSON.parseObject(json2, clazz);
        TestCase.assertEquals("{\"text\":\"robohorse\"}", JSON.toJSONString(obj2));
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/ResponseKotlin.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("ResponseKotlin", bytes, 0, bytes.length);
            }
        }
    }
}

