package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_Long_0 extends TestCase {
    public void test_asm() throws Exception {
        TestASM_Long_0.V0 v = new TestASM_Long_0.V0();
        String text = JSON.toJSONString(v);
        TestASM_Long_0.V0 v1 = JSON.parseObject(text, TestASM_Long_0.V0.class);
        Assert.assertEquals(v.getI(), v1.getI());
    }

    public static class V0 {
        private Long i = 12L;

        public Long getI() {
            return i;
        }

        public void setI(Long i) {
            this.i = i;
        }
    }
}

