package com.alibaba.json.bvt.kotlin;


import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


/**
 * Created by wenshao on 06/08/2017.
 */
public class ClassWithPairTest extends TestCase {
    public void test_user() throws Exception {
        ClassWithPairTest.ExtClassLoader classLoader = new ClassWithPairTest.ExtClassLoader();
        Class clazz = classLoader.loadClass("ClassWithPair");
        String json = "{\"name\":{\"first\":\"shaojin\",\"second\":\"wen\"},\"age\":99}";
        Object obj = JSON.parseObject(json, clazz);
        TestCase.assertEquals("{\"age\":99,\"name\":{\"first\":\"shaojin\",\"second\":\"wen\"}}", JSON.toJSONString(obj));
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/ClassWithPair.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("ClassWithPair", bytes, 0, bytes.length);
            }
        }
    }
}

