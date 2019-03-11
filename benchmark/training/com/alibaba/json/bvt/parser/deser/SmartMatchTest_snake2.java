package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class SmartMatchTest_snake2 extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"_id\":1001}";
        SmartMatchTest_snake2.VO vo = JSON.parseObject(text, SmartMatchTest_snake2.VO.class);
        Assert.assertEquals(1001, vo.id);
    }

    public static class VO {
        public int id;
    }
}

