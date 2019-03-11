package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class DefaultObjectDeserializerTest_collection extends TestCase {
    public void test_0() throws Exception {
        String input = "[{}]";
        List<HashMap> map = JSON.parseObject(input, getType());
        Assert.assertEquals(HashMap.class, map.get(0).getClass());
    }

    public void test_1() throws Exception {
        String input = "{}";
        DefaultObjectDeserializerTest_collection.BO<HashMap> map = JSON.parseObject(input, getType());
    }

    public void test_2() throws Exception {
        Exception error = null;
        try {
            String input = "{'map':{}}";
            DefaultObjectDeserializerTest_collection.MyMap<String, HashMap> map = JSON.parseObject(input, getType());
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class BO<T> {}

    public static class MyMap<K, V> extends HashMap {
        public MyMap() {
            throw new RuntimeException();
        }
    }
}

