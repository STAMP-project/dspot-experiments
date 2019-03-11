package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_dubbo_long extends TestCase {
    public void test_0() throws Exception {
        Long val = 2345L;
        String text = JSON.toJSONString(val, WriteClassName);
        Assert.assertEquals(val, JSON.parseObject(text, long.class));
    }
}

