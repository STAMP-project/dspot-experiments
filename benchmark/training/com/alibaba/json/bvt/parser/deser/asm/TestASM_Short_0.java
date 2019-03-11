package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_Short_0 extends TestCase {
    public void test_asm() throws Exception {
        TestASM_Short_0.V0 v = new TestASM_Short_0.V0();
        String text = JSON.toJSONString(v);
        TestASM_Short_0.V0 v1 = JSON.parseObject(text, TestASM_Short_0.V0.class);
        Assert.assertEquals(v.getI(), v1.getI());
    }

    public static class V0 {
        private Short i = 12;

        public Short getI() {
            return i;
        }

        public void setI(Short i) {
            this.i = i;
        }
    }
}

