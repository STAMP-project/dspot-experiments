package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestPivateStaticClass extends TestCase {
    public void test_inner() throws Exception {
        TestPivateStaticClass.VO vo = new TestPivateStaticClass.VO();
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"value\":234}", text);
        TestPivateStaticClass.VO v1 = JSON.parseObject(text, TestPivateStaticClass.VO.class);
    }

    private static class VO {
        private int value = 234;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

