package com.alibaba.json.bvt.kotlin;


import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


public class Issue1547 extends TestCase {
    public void test_user() throws Exception {
        Issue1547.ExtClassLoader classLoader = new Issue1547.ExtClassLoader();
        Class clazz = classLoader.loadClass("Head");
        Object head = JSON.parseObject("{\"msg\":\"mmm\",\"code\":\"ccc\"}", clazz);
        TestCase.assertEquals("{\"code\":\"ccc\",\"msg\":\"mmm\"}", JSON.toJSONString(head));
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/issue1547/Head.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("Head", bytes, 0, bytes.length);
            }
        }
    }
}

