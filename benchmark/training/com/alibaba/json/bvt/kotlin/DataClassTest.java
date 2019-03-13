package com.alibaba.json.bvt.kotlin;


import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


/**
 * Created by wenshao on 05/08/2017.
 */
public class DataClassTest extends TestCase {
    public void test_user() throws Exception {
        DataClassTest.ExtClassLoader classLoader = new DataClassTest.ExtClassLoader();
        Class clazz = classLoader.loadClass("DataClass");
        String json = "{\"aa\":1001,\"bb\":1002}";
        Object obj = JSON.parseObject(json, clazz);
        TestCase.assertEquals("{\"aa\":1001,\"bb\":1002}", JSON.toJSONString(obj));
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/DataClass.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("DataClass", bytes, 0, bytes.length);
            }
        }
    }
}

