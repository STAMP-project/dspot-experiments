package com.alibaba.json.bvt.kotlin;


import Feature.OrderedField;
import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


/**
 * Created by wenshao on 05/08/2017.
 */
public class Issue1483 extends TestCase {
    public void test_user() throws Exception {
        Issue1483.ExtClassLoader classLoader = new Issue1483.ExtClassLoader();
        Class clazz = classLoader.loadClass("Person");
        String json = "{\"age\":99,\"name\":\"robohorse\",\"desc\":\"xx\"}";
        Object obj = JSON.parseObject(json, clazz);
        TestCase.assertSame(clazz, obj.getClass());
        // 
        for (int i = 0; i < 10; ++i) {
            String text = JSON.parseObject(JSON.toJSONString(obj), OrderedField).toJSONString();
            TestCase.assertEquals("{\"age\":99,\"desc\":\"xx\",\"name\":\"robohorse\"}", text);
        }
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/Person.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("Person", bytes, 0, bytes.length);
            }
        }
    }
}

