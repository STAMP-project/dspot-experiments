package com.alibaba.json.bvt.parser.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class EmptyParseArrayTest extends TestCase {
    public void test_0() throws Exception {
        Assert.assertNull(JSON.parseArray("", EmptyParseArrayTest.VO.class));
    }

    public static class VO {}
}

