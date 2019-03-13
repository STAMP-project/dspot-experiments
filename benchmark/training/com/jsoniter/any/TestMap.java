package com.jsoniter.any;


import Any.EntryIterator;
import java.util.HashMap;
import junit.framework.TestCase;


public class TestMap extends TestCase {
    public void test_size() {
        Any any = Any.wrap(TestMap.mapOf("hello", 1, "world", 2));
        TestCase.assertEquals(2, any.size());
    }

    public void test_to_boolean() {
        Any any = Any.wrap(TestMap.mapOf());
        TestCase.assertFalse(any.toBoolean());
        any = Any.wrap(TestMap.mapOf("hello", 1));
        TestCase.assertTrue(any.toBoolean());
    }

    public void test_to_int() {
        Any any = Any.wrap(TestMap.mapOf());
        TestCase.assertEquals(0, any.toInt());
        any = Any.wrap(TestMap.mapOf("hello", 1));
        TestCase.assertEquals(1, any.toInt());
    }

    public void test_get() {
        Any any = Any.wrap(TestMap.mapOf("hello", 1, "world", 2));
        TestCase.assertEquals(2, any.get("world").toInt());
    }

    public void test_get_from_nested() {
        Any any = Any.wrap(TestMap.mapOf("a", TestMap.mapOf("b", "c"), "d", TestMap.mapOf("e", "f")));
        TestCase.assertEquals("c", any.get("a", "b").toString());
        TestCase.assertEquals("{\"a\":\"c\"}", any.get('*', "b").toString());
    }

    public void test_iterator() {
        Any any = Any.wrap(TestMap.mapOf("hello", 1, "world", 2));
        Any.EntryIterator iter = any.entries();
        HashMap<String, Object> map = new HashMap<String, Object>();
        while (iter.next()) {
            map.put(iter.key(), iter.value().toInt());
        } 
        TestCase.assertEquals(TestMap.mapOf("hello", 1, "world", 2), map);
    }

    public void test_to_string() {
        TestCase.assertEquals("{\"world\":2,\"hello\":1}", Any.wrap(TestMap.mapOf("hello", 1, "world", 2)).toString());
        Any any = Any.wrap(TestMap.mapOf("hello", 1, "world", 2));
        any.asMap().put("abc", Any.wrap(3));
        TestCase.assertEquals("{\"world\":2,\"abc\":3,\"hello\":1}", any.toString());
    }
}

