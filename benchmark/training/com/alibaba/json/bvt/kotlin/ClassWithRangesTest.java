package com.alibaba.json.bvt.kotlin;


import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.InputStream;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


/**
 * Created by wenshao on 06/08/2017.
 */
public class ClassWithRangesTest extends TestCase {
    public void test_user() throws Exception {
        ClassWithRangesTest.ExtClassLoader classLoader = new ClassWithRangesTest.ExtClassLoader();
        Class clazz = classLoader.loadClass("ClassWithRanges");
        String json = "{\"ages\":{\"start\":18,\"end\":40},\"distance\":{\"start\":5,\"end\":50}}";
        Object obj = JSON.parseObject(json, clazz);
        TestCase.assertEquals("{\"ages\":{\"first\":18,\"last\":0,\"start\":18,\"step\":1},\"distance\":{\"first\":5,\"last\":0,\"start\":5,\"step\":1}}", JSON.toJSONString(obj));
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/ClassWithRanges.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("ClassWithRanges", bytes, 0, bytes.length);
            }
        }
    }
}

