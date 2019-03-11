package com.alibaba.json.bvt.kotlin;


import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;


public class Issue1543 extends TestCase {
    public void test_user() throws Exception {
        Issue1543.ExtClassLoader classLoader = new Issue1543.ExtClassLoader();
        Class clazzUser = classLoader.loadClass("User1");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", 1);
        map.put("name", "test1");
        JSON.parseObject(JSON.toJSONString(map), clazzUser);
    }

    public void test_cluster() throws Exception {
        Issue1543.ExtClassLoader classLoader = new Issue1543.ExtClassLoader();
        Class clazzCluster = classLoader.loadClass("Cluster");
        Object cluster = JSON.parseObject(JSON.toJSONString(Collections.singletonMap("cluster_enabled", 1)), clazzCluster);
        TestCase.assertEquals("{\"cluster_enabled\":1}", JSON.toJSONString(cluster));
    }

    public static class ExtClassLoader extends ClassLoader {
        public ExtClassLoader() throws IOException {
            super(Thread.currentThread().getContextClassLoader());
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/issue1543/User1.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("User1", bytes, 0, bytes.length);
            }
            {
                byte[] bytes;
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("kotlin/issue1543/Cluster.clazz");
                bytes = IOUtils.toByteArray(is);
                is.close();
                super.defineClass("Cluster", bytes, 0, bytes.length);
            }
        }
    }
}

