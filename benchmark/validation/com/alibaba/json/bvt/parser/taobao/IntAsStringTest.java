package com.alibaba.json.bvt.parser.taobao;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class IntAsStringTest extends TestCase {
    public void test_0() throws Exception {
        IntAsStringTest.VO vo = JSON.parseObject("{\"value\":\"1001\"}", IntAsStringTest.VO.class);
        Assert.assertEquals(1001, vo.value);
    }

    public static class VO {
        public int value;
    }
}

