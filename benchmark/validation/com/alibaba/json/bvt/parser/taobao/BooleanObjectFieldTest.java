package com.alibaba.json.bvt.parser.taobao;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanObjectFieldTest extends TestCase {
    public void test_0() throws Exception {
        BooleanObjectFieldTest.VO vo = JSON.parseObject("{\"value\":true}", BooleanObjectFieldTest.VO.class);
        Assert.assertTrue(vo.value);
    }

    public static class VO {
        public Boolean value;
    }
}

