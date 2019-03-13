package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class ConcurrentHashMapDeserializerTest extends TestCase {
    public void test_null() throws Exception {
        Assert.assertEquals(null, JSON.parseObject("null", ConcurrentHashMap.class));
        Assert.assertEquals(null, JSON.parseObject("null", ConcurrentMap.class));
    }
}

