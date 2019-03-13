package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_long extends TestCase {
    public void test_asm() throws Exception {
        TestASM_long.V0 v = new TestASM_long.V0();
        String text = JSON.toJSONString(v);
        TestASM_long.V0 v1 = JSON.parseObject(text, TestASM_long.V0.class);
        Assert.assertEquals(v.getI(), v1.getI());
    }

    public static class V0 {
        private long i = 12;

        public long getI() {
            return i;
        }

        public void setI(long i) {
            this.i = i;
        }
    }
}

