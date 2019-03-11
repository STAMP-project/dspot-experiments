package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class TestDouble extends TestCase {
    public void test_doubleArray_2() throws Exception {
        double[] array = new double[]{ 1, 2 };
        TestDouble.A a = new TestDouble.A();
        a.setValue(array);
        String text = JSON.toJSONString(a);
        TestDouble.A a1 = JSON.parseObject(text, TestDouble.A.class);
    }

    public static class A {
        private double[] value;

        public double[] getValue() {
            return value;
        }

        public void setValue(double[] value) {
            this.value = value;
        }
    }
}

