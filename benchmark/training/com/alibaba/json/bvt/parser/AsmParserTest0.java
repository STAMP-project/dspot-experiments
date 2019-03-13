package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class AsmParserTest0 extends TestCase {
    public void test_asm() throws Exception {
        AsmParserTest0.A a = JSON.parseObject("{\"f1\":123}", AsmParserTest0.A.class);
        Assert.assertNotNull(a.getF2());
    }

    public static class A {
        private int f1;

        private AsmParserTest0.B f2 = new AsmParserTest0.B();

        public int getF1() {
            return f1;
        }

        public void setF1(int f1) {
            this.f1 = f1;
        }

        public AsmParserTest0.B getF2() {
            return f2;
        }

        public void setF2(AsmParserTest0.B f2) {
            this.f2 = f2;
        }
    }

    public static class B {}
}

