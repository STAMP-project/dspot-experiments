package com.alibaba.json.bvt.parser.taobao;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongAsStringTest extends TestCase {
    public void test_0() throws Exception {
        LongAsStringTest.VO vo = JSON.parseObject("{\"value\":\"1001\"}", LongAsStringTest.VO.class);
        Assert.assertEquals(1001L, vo.value);
    }

    public static class VO {
        public long value;
    }
}

