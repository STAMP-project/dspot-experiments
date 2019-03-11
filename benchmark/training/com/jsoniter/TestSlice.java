package com.jsoniter;


import com.jsoniter.spi.Slice;
import java.util.HashMap;
import junit.framework.TestCase;


public class TestSlice extends TestCase {
    public void test_equals() {
        TestCase.assertTrue(Slice.make("hello").equals(Slice.make("hello")));
        TestCase.assertTrue(Slice.make("hello").equals(new Slice("ahello".getBytes(), 1, 6)));
    }

    public void test_hashcode() {
        HashMap map = new HashMap();
        map.put(Slice.make("hello"), "hello");
        map.put(Slice.make("world"), "world");
        TestCase.assertEquals("hello", map.get(Slice.make("hello")));
        TestCase.assertEquals("world", map.get(Slice.make("world")));
    }
}

