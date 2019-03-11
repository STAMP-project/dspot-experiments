package com.jsoniter.any;


import com.jsoniter.JsonIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import junit.framework.TestCase;


public class TestList extends TestCase {
    public void test_size() {
        Any any = Any.wrap(Arrays.asList(1, 2, 3));
        TestCase.assertEquals(3, any.size());
    }

    public void test_to_boolean() {
        Any any = Any.wrap(Collections.emptyList());
        TestCase.assertFalse(any.toBoolean());
        any = Any.wrap(Arrays.asList("hello", 1));
        TestCase.assertTrue(any.toBoolean());
    }

    public void test_to_int() {
        Any any = Any.wrap(Collections.emptyList());
        TestCase.assertEquals(0, any.toInt());
        any = Any.wrap(Arrays.asList("hello", 1));
        TestCase.assertEquals(2, any.toInt());
    }

    public void test_get() {
        Any any = Any.wrap(Arrays.asList("hello", 1));
        TestCase.assertEquals("hello", any.get(0).toString());
    }

    public void test_get_from_nested() {
        Any any = Any.wrap(Arrays.asList(Collections.singletonList("hello"), Collections.singletonList("world")));
        TestCase.assertEquals("hello", any.get(0, 0).toString());
        TestCase.assertEquals("[\"hello\",\"world\"]", any.get('*', 0).toString());
    }

    public void test_iterator() {
        Any any = Any.wrap(Arrays.asList(1, 2, 3));
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (Any element : any) {
            list.add(element.toInt());
        }
        TestCase.assertEquals(Arrays.asList(1, 2, 3), list);
    }

    public void test_to_string() {
        TestCase.assertEquals("[1,2,3]", Any.wrap(Arrays.asList(1, 2, 3)).toString());
        Any any = Any.wrap(Arrays.asList(1, 2, 3));
        any.asList().add(Any.wrap(4));
        TestCase.assertEquals("[1,2,3,4]", any.toString());
    }

    public void test_for_each() {
        Any a = JsonIterator.deserialize("[]");
        Iterator<Any> iter = a.iterator();
        TestCase.assertFalse(iter.hasNext());
    }
}

