package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_short extends TestCase {
    public void test_asm() throws Exception {
        TestASM_short.V0 v = new TestASM_short.V0();
        String text = JSON.toJSONString(v);
        TestASM_short.V0 v1 = JSON.parseObject(text, TestASM_short.V0.class);
        Assert.assertEquals(v.getI(), v1.getI());
    }

    public static class V0 {
        private short i = 12;

        public short getI() {
            return i;
        }

        public void setI(short i) {
            this.i = i;
        }
    }
}

