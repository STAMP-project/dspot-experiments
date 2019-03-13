package com.jsoniter;


import com.jsoniter.any.Any;
import java.io.IOException;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestNested extends TestCase {
    public void test_array_of_objects() throws IOException {
        JsonIterator iter = JsonIterator.parse("[{'field1':'11','field2':'12'},{'field1':'21','field2':'22'}]".replace('\'', '"'));
        SimpleObject[] objects = iter.read(SimpleObject[].class);
        Assert.assertArrayEquals(new SimpleObject[]{ new SimpleObject() {
            {
                field1 = "11";
                field2 = "12";
            }
        }, new SimpleObject() {
            {
                field1 = "21";
                field2 = "22";
            }
        } }, objects);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        TestCase.assertEquals("22", any.toString(1, "field2"));
    }

    public void test_get_all_array_elements_via_any() throws IOException {
        Any any = JsonIterator.deserialize(" [ { \"bar\": 1 }, {\"bar\": 3} ]");
        Any result = any.get('*', "bar");
        TestCase.assertEquals("[ 1, 3]", result.toString());
        any = Any.rewrap(any.asList());// make it not lazy

        result = any.get('*', "bar");
        TestCase.assertEquals("[ 1, 3]", result.toString());
    }

    public void test_get_all_with_some_invalid_path() throws IOException {
        Any any = JsonIterator.deserialize(" [ { \"bar\": 1 }, {\"foo\": 3} ]");
        Any result = any.get('*', "bar");
        TestCase.assertEquals("[ 1]", result.toString());
        any = Any.rewrap(any.asList());// make it not lazy

        result = any.get('*', "bar");
        TestCase.assertEquals("[ 1]", result.toString());
        any = JsonIterator.deserialize("{\"field1\":[1,2],\"field2\":[3]}");
        result = any.get('*', 1);
        TestCase.assertEquals("{\"field1\":2}", result.toString());
        any = Any.rewrap(any.asMap());// make it not lazy

        result = any.get('*', 1);
        TestCase.assertEquals("{\"field1\":2}", result.toString());
    }

    public static class TestObject3 {
        public com.jsoniter.output.TestNested.TestObject3 reference;
    }

    public void test_recursive_class() {
        // recursive reference will not be supported
        // however recursive structure is supported
        com.jsoniter.output.TestNested.TestObject3 obj = new com.jsoniter.output.TestNested.TestObject3();
        TestCase.assertNull(JsonIterator.deserialize("{\"reference\":null}", TestNested.TestObject3.class).reference);
    }
}

