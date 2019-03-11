package com.alibaba.json.bvt.parser.taobao;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class IntegerAsStringTest extends TestCase {
    public void test_0() throws Exception {
        IntegerAsStringTest.VO vo = JSON.parseObject("{\"value\":\"1001\"}", IntegerAsStringTest.VO.class);
        Assert.assertEquals(1001, vo.value.intValue());
    }

    public static class VO {
        public Integer value;
    }
}

