package com.jsoniter.output;


import java.math.BigInteger;
import junit.framework.TestCase;


public class TestInteger extends TestCase {
    public void testBigInteger() {
        TestCase.assertEquals("100", JsonStream.serialize(new BigInteger("100")));
    }
}

