package com.alibaba.json.bvt.parser.taobao;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongObjectAsStringTest extends TestCase {
    public void test_0() throws Exception {
        LongObjectAsStringTest.VO vo = JSON.parseObject("{\"value\":\"1001\"}", LongObjectAsStringTest.VO.class);
        Assert.assertEquals(1001, vo.value.intValue());
    }

    public static class VO {
        public Long value;
    }
}

