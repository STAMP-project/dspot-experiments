package com.alibaba.json.bvt;


import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


public class TestExternal5 extends TestCase {
    ParserConfig confg = ParserConfig.global;

    public void test_0() throws Exception {
        TestExternal5.ExtClassLoader classLoader = new TestExternal5.ExtClassLoader();
        Class<?> clazz = classLoader.loadClass("com.alibaba.dubbo.demo.MyEsbResultModel2");
        Method method = clazz.getMethod("setReturnValue", new Class[]{ Serializable.class });
        Object obj = clazz.newInstance();
        method.invoke(obj, "AAAA");
        {
            String text = JSON.toJSONString(obj);
            System.out.println(text);
        }
        String text = JSON.toJSONString(obj, WriteClassName, WriteMapNullValue);
        System.out.println(text);
        Object object = JSON.parseObject(text, clazz, confg);
        TestCase.assertEquals("a1", clazz.getName(), object.getClass().getName());
        Object object2 = JSON.parse(text, confg);
        TestCase.assertEquals(("a2 " + text), clazz.getName(), object2.getClass().getName());
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("external/MyEsbResultModel2.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("com.alibaba.dubbo.demo.MyEsbResultModel2", bytes, 0, bytes.length);
            }
        }
    }
}

