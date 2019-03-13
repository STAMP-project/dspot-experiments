package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestInnerClass extends TestCase {
    public void test_inner() throws Exception {
        TestInnerClass.VO vo = new TestInnerClass.VO();
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"value\":234}", text);
    }

    public class VO {
        private int value = 234;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

