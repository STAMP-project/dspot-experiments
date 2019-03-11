package com.alibaba.json.bvt.parser.taobao;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class FloatObjectFieldTest extends TestCase {
    public void test_0() throws Exception {
        FloatObjectFieldTest.VO vo = JSON.parseObject("{\"value\":1001}", FloatObjectFieldTest.VO.class);
        Assert.assertTrue((1001.0F == (vo.value)));
    }

    public static class VO {
        public Float value;
    }
}

