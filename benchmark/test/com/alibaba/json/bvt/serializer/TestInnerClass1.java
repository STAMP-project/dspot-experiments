package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestInnerClass1 extends TestCase {
    public void test_inner() throws Exception {
        TestInnerClass1.VO vo = new TestInnerClass1.VO();
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"value\":234}", text);
    }

    private class VO {
        private int value = 234;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

