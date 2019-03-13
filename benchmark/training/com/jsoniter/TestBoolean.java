package com.jsoniter;


import java.io.IOException;
import junit.framework.TestCase;


public class TestBoolean extends TestCase {
    public void test_non_streaming() throws IOException {
        TestCase.assertTrue(JsonIterator.parse("true").readBoolean());
        TestCase.assertFalse(JsonIterator.parse("false").readBoolean());
        TestCase.assertTrue(JsonIterator.parse("null").readNull());
        TestCase.assertFalse(JsonIterator.parse("false").readNull());
    }
}

