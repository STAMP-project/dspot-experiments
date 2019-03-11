package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class AsmParserTest1 extends TestCase {
    public void test_asm() throws Exception {
        AsmParserTest1.A a = JSON.parseObject("{\"f1\":123}", AsmParserTest1.A.class);
        Assert.assertEquals(123, a.getF1());
        Assert.assertNotNull(a.getF2());
    }

    public static class A {
        private int f1;

        private List<AsmParserTest1.B> f2 = new ArrayList<AsmParserTest1.B>();

        public int getF1() {
            return f1;
        }

        public void setF1(int f1) {
            this.f1 = f1;
        }

        public List<AsmParserTest1.B> getF2() {
            return f2;
        }

        public void setF2(List<AsmParserTest1.B> f2) {
            this.f2 = f2;
        }
    }

    public static class B {}
}

