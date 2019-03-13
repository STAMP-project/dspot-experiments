package com.jsoniter.extra;


import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import junit.framework.TestCase;


public class TestBase64 extends TestCase {
    static {
        Base64Support.enable();
    }

    public void test_encode() {
        TestCase.assertEquals("\"YWJj\"", JsonStream.serialize("abc".getBytes()));
    }

    public void test_decode() {
        TestCase.assertEquals("abc", new String(JsonIterator.deserialize("\"YWJj\"", byte[].class)));
    }
}

