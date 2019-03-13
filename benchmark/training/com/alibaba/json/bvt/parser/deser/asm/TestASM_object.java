package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_object extends TestCase {
    public void test_asm() throws Exception {
        TestASM_object.V0 v = new TestASM_object.V0();
        String text = JSON.toJSONString(v);
        TestASM_object.V0 v1 = JSON.parseObject(text, TestASM_object.V0.class);
        Assert.assertEquals(v.getValue().getValue(), v1.getValue().getValue());
    }

    public static class V0 {
        private TestASM_object.V1 value = new TestASM_object.V1();

        public TestASM_object.V1 getValue() {
            return value;
        }

        public void setValue(TestASM_object.V1 value) {
            this.value = value;
        }
    }

    public static class V1 {
        private int value = 1234;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

