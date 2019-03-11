package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_byte extends TestCase {
    public void test_asm() throws Exception {
        TestASM_byte.V0 v = new TestASM_byte.V0();
        String text = JSON.toJSONString(v);
        TestASM_byte.V0 v1 = JSON.parseObject(text, TestASM_byte.V0.class);
        Assert.assertEquals(v.getI(), v1.getI());
    }

    public static class V0 {
        private byte i = 12;

        public byte getI() {
            return i;
        }

        public void setI(byte i) {
            this.i = i;
        }
    }
}

