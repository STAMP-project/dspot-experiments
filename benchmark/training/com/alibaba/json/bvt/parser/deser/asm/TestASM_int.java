package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_int extends TestCase {
    public void test_asm() throws Exception {
        TestASM_int.V0 v = new TestASM_int.V0();
        String text = JSON.toJSONString(v);
        TestASM_int.V0 v1 = JSON.parseObject(text, TestASM_int.V0.class);
        Assert.assertEquals(v.getI(), v1.getI());
    }

    public static class V0 {
        private int i = 12;

        public int getI() {
            return i;
        }

        public TestASM_int.V0 setI(int i) {
            this.i = i;
            return this;
        }
    }
}

