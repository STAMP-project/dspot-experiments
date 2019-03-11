package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestASM_char extends TestCase {
    public void test_asm() throws Exception {
        TestASM_char.V0 v = new TestASM_char.V0();
        String text = JSON.toJSONString(v);
        TestASM_char.V0 v1 = JSON.parseObject(text, TestASM_char.V0.class);
        Assert.assertEquals(v.getValue(), v1.getValue());
    }

    public static class V0 {
        private char value = '?';

        public char getValue() {
            return value;
        }

        public void setValue(char i) {
            this.value = i;
        }
    }
}

