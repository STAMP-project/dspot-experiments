package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_boolean extends TestCase {
    public void test_asm() throws Exception {
        TestASM_boolean.V0 v = new TestASM_boolean.V0();
        String text = JSON.toJSONString(v);
        TestASM_boolean.V0 v1 = JSON.parseObject(text, TestASM_boolean.V0.class);
        Assert.assertEquals(v.isValue(), v1.isValue());
    }

    public static class V0 {
        private boolean value = true;

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }
}

