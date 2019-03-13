package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_Integer extends TestCase {
    public void test_asm() throws Exception {
        TestASM_Integer.V0 v = new TestASM_Integer.V0();
        String text = JSON.toJSONString(v);
        TestASM_Integer.V0 v1 = JSON.parseObject(text, TestASM_Integer.V0.class);
        Assert.assertEquals(v.getI(), v1.getI());
    }

    public static class V0 {
        private Integer i = 12;

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }
    }
}

