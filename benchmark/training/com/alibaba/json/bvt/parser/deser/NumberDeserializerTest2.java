package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class NumberDeserializerTest2 extends TestCase {
    public void test_double2() throws Exception {
        Assert.assertTrue((123.0 == (JSON.parseObject("123B", double.class))));
        Assert.assertTrue((123.0 == (JSON.parseObject("123B", Double.class).doubleValue())));
    }
}

