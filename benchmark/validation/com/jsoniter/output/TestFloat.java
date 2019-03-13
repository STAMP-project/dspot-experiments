package com.jsoniter.output;


import java.math.BigDecimal;
import junit.framework.TestCase;


public class TestFloat extends TestCase {
    public void testBigDecimal() {
        TestCase.assertEquals("100.1", JsonStream.serialize(new BigDecimal("100.1")));
    }

    public void test_infinity() {
        TestCase.assertEquals("\"Infinity\"", JsonStream.serialize(Double.POSITIVE_INFINITY));
        TestCase.assertEquals("\"Infinity\"", JsonStream.serialize(Float.POSITIVE_INFINITY));
        TestCase.assertEquals("\"-Infinity\"", JsonStream.serialize(Double.NEGATIVE_INFINITY));
        TestCase.assertEquals("\"-Infinity\"", JsonStream.serialize(Float.NEGATIVE_INFINITY));
    }
}

