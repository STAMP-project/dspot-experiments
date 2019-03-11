package com.alibaba.json.bvt.kotlin;


import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


/**
 * Created by wenshao on 05/08/2017.
 */
public class Issue1462 extends TestCase {
    public void test_user() throws Exception {
        Issue1462.ExtClassLoader classLoader = new Issue1462.ExtClassLoader();
        Class clazz = classLoader.loadClass("ObjectA");
        String json = "{\"a\":1001,\"b\":1002}";
        Object obj = JSON.parseObject(json, clazz);
        TestCase.assertEquals("{\"a\":\"b\",\"b\":\"b\"}", JSON.toJSONString(obj));
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/ObjectA.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("ObjectA", bytes, 0, bytes.length);
            }
        }
    }
}

