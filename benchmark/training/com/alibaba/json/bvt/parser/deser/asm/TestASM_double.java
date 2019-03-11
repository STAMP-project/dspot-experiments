package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_double extends TestCase {
    public void test_asm() throws Exception {
        TestASM_double.V0 v = new TestASM_double.V0();
        String text = JSON.toJSONString(v);
        TestASM_double.V0 v1 = JSON.parseObject(text, TestASM_double.V0.class);
        Assert.assertTrue(((v.getValue()) == (v1.getValue())));
    }

    public static class V0 {
        private double value = 32.5F;

        public double getValue() {
            return value;
        }

        public void setValue(double i) {
            this.value = i;
        }
    }
}

