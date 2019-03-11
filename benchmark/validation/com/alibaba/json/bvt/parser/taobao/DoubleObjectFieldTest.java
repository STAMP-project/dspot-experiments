package com.alibaba.json.bvt.parser.taobao;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class DoubleObjectFieldTest extends TestCase {
    public void test_0() throws Exception {
        DoubleObjectFieldTest.VO vo = JSON.parseObject("{\"value\":1001}", DoubleObjectFieldTest.VO.class);
        Assert.assertTrue((1001.0 == (vo.value)));
    }

    public static class VO {
        public Double value;
    }
}

