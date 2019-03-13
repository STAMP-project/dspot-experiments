package com.sun.btrace.aggregation;


import org.junit.Assert;
import org.junit.Test;


public class QuantizeTest {
    public QuantizeTest() {
    }

    @Test
    public void testLogBase2() {
        System.out.println("logBase2");
        long val = 1;
        for (int i = 0; i < 63; i++) {
            Assert.assertEquals(i, Quantize.logBase2(val));
            val <<= 1;
        }
    }
}

