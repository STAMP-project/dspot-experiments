package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class DefaultObjectDeserializerTest9 extends TestCase {
    public <T> void test_1() throws Exception {
        T[] list = JSON.parseObject("[{}]", new com.alibaba.fastjson.TypeReference<T[]>() {});
        Assert.assertEquals(1, list.length);
        Assert.assertNotNull(list[0]);
        Assert.assertTrue(((list[0]) instanceof Map));
    }
}

