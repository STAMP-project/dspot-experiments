package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_Byte_0 extends TestCase {
    public void test_asm() throws Exception {
        TestASM_Byte_0.V0 v = new TestASM_Byte_0.V0();
        String text = JSON.toJSONString(v);
        TestASM_Byte_0.V0 v1 = JSON.parseObject(text, TestASM_Byte_0.V0.class);
        Assert.assertEquals(v.getI(), v1.getI());
    }

    public static class V0 {
        private Byte i = 12;

        public Byte getI() {
            return i;
        }

        public void setI(Byte i) {
            this.i = i;
        }
    }
}

