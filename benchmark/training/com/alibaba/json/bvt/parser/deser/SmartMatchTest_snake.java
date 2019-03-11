package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class SmartMatchTest_snake extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"person_id\":1001}";
        SmartMatchTest_snake.VO vo = JSON.parseObject(text, SmartMatchTest_snake.VO.class);
        Assert.assertEquals(1001, vo.personId);
    }

    public static class VO {
        public int personId;
    }
}

