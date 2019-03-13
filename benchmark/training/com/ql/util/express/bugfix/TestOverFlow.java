package com.ql.util.express.bugfix;


import org.junit.Test;


public class TestOverFlow {
    public class Result {
        private int a = 0;

        private String b = "??????????";

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }

    private int count = 0;

    @Test
    public void testOverflow() {
        new TestOverFlow().testOverFlow();
    }
}

