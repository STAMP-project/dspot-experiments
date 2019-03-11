package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class FinalTest extends TestCase {
    public void test_final() throws Exception {
        FinalTest.VO vo = new FinalTest.VO();
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"value\":1001}", text);
        JSON.parseObject(text, FinalTest.VO.class);
        JSON.parseObject("{\"id\":1001,\"value\":1001}", FinalTest.VO.class);
    }

    public static class VO {
        public static final int id = 1001;

        public final int value = 1001;
    }
}

