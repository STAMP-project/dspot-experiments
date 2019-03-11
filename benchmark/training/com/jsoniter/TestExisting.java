package com.jsoniter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;


public class TestExisting extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
    }

    public static class TestObj1 {
        public String field1;

        public String field2;
    }

    public void test_direct_reuse() throws IOException {
        TestExisting.TestObj1 testObj = new TestExisting.TestObj1();
        testObj.field2 = "world";
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' }".replace('\'', '"'));
        TestExisting.TestObj1 oldObj = testObj;
        testObj = iter.read(testObj);
        TestCase.assertEquals("hello", testObj.field1);
        TestCase.assertEquals(System.identityHashCode(oldObj), System.identityHashCode(testObj));
    }

    public static class TestObj2 {
        public String field3;

        public TestExisting.TestObj1 field4;
    }

    public void test_indirect_reuse() throws IOException {
        TestExisting.TestObj2 testObj = new TestExisting.TestObj2();
        testObj.field4 = new TestExisting.TestObj1();
        testObj.field4.field1 = "world";
        JsonIterator iter = JsonIterator.parse("{ 'field3' : 'hello', 'field4': {'field2': 'hello'} }".replace('\'', '"'));
        TestExisting.TestObj2 oldObj = testObj;
        testObj = iter.read(testObj);
        TestCase.assertEquals("hello", testObj.field3);
        TestCase.assertEquals("hello", testObj.field4.field2);
        TestCase.assertEquals(System.identityHashCode(oldObj), System.identityHashCode(testObj));
    }

    public void test_reuse_list() throws IOException {
        List list1 = new ArrayList();
        JsonIterator iter = JsonIterator.parse("[1]");
        List list2 = iter.read(new com.jsoniter.spi.TypeLiteral<List<Integer>>() {}, list1);
        TestCase.assertEquals(System.identityHashCode(list2), System.identityHashCode(list1));
    }

    public void test_reuse_linked_list() throws IOException {
        LinkedList list1 = new LinkedList();
        JsonIterator iter = JsonIterator.parse("[1]");
        List list2 = iter.read(new com.jsoniter.spi.TypeLiteral<LinkedList<Integer>>() {}, list1);
        TestCase.assertEquals(System.identityHashCode(list2), System.identityHashCode(list1));
    }

    public void test_reuse_map() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' }".replace('\'', '"'));
        HashMap<String, Object> map1 = new HashMap<String, Object>();
        map1.put("a", "b");
        HashMap<String, Object> map2 = iter.read(map1);
        TestCase.assertEquals("b", map2.get("a"));
    }
}

