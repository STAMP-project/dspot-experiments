package com.jsoniter;


import ValueType.INVALID;
import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import junit.framework.TestCase;


public class TestReadAny extends TestCase {
    static {
        // JsonIterator.enableStreamingSupport();
    }

    public void test_read_any() throws IOException {
        JsonIterator iter = JsonIterator.parse("[0,1,2,3]");
        TestCase.assertEquals(3, iter.readAny().toInt(3));
    }

    public void test_bind_to_any() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field3': 100}".replace('\'', '"'));
        ComplexObject obj = iter.read(ComplexObject.class);
        System.out.println(obj.field3);
    }

    public void test_read_any_from_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'numbers': ['1', '2', ['3', '4']]}".replace('\'', '"'));
        TestCase.assertEquals(3, iter.readAny().toInt("numbers", 2, 0));
    }

    public void test_read_int() throws IOException {
        TestCase.assertEquals(100, JsonIterator.deserialize("100").toInt());
        TestCase.assertEquals(0, JsonIterator.deserialize("null").toInt());
        TestCase.assertEquals(100, JsonIterator.deserialize("\"100\"").toInt());
        TestCase.assertEquals(1, JsonIterator.deserialize("true").toInt());
        Any any = JsonIterator.deserialize("100");
        TestCase.assertEquals(Long.valueOf(100), any.object());
        TestCase.assertEquals(100, any.toInt());
        TestCase.assertEquals(100L, any.toLong());
        TestCase.assertEquals(100.0F, any.toFloat());
        TestCase.assertEquals(100.0, any.toDouble());
        TestCase.assertEquals("100", any.toString());
    }

    public void test_read_boolean() throws IOException {
        TestCase.assertEquals(true, JsonIterator.deserialize("100").toBoolean());
        TestCase.assertEquals(false, JsonIterator.deserialize("{}").toBoolean());
        TestCase.assertEquals(true, JsonIterator.deserialize("{\"field1\":100}").toBoolean());
        TestCase.assertEquals(false, JsonIterator.deserialize("null").toBoolean());
        TestCase.assertEquals(true, JsonIterator.deserialize("\"100\"").toBoolean());
        TestCase.assertEquals(true, JsonIterator.deserialize("true").toBoolean());
        TestCase.assertEquals(1, JsonIterator.deserialize("true").toInt());
        TestCase.assertEquals(0, JsonIterator.deserialize("false").toInt());
        TestCase.assertEquals("false", JsonIterator.deserialize("false").toString());
        TestCase.assertEquals(Boolean.FALSE, JsonIterator.deserialize("false").object());
    }

    public void test_read_int_array() throws IOException {
        JsonIterator iter = JsonIterator.parse("[100,101]");
        Any any = iter.readAny();
        TestCase.assertEquals(100, any.toInt(0));
        TestCase.assertEquals(101, any.toInt(1));
    }

    public void test_read_int_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\":100}");
        Any any = iter.readAny();
        TestCase.assertEquals(100, any.toInt("field1"));
    }

    public void test_read_float_as_int() throws IOException {
        JsonIterator iter = JsonIterator.parse("[\"100.1\",\"101.1\"]");
        Any any = iter.readAny();
        TestCase.assertEquals(100, any.toInt(0));
        TestCase.assertEquals(101, any.toInt(1));
    }

    public void test_read_string() throws IOException {
        TestCase.assertEquals("hello", JsonIterator.deserialize("\"hello\"").toString());
        TestCase.assertEquals("true", JsonIterator.deserialize("true").toString());
        TestCase.assertEquals("null", JsonIterator.deserialize("null").toString());
        TestCase.assertEquals("100", JsonIterator.deserialize("100").toString());
        TestCase.assertEquals(100, JsonIterator.deserialize("\"100\"").toInt());
        TestCase.assertEquals(true, JsonIterator.deserialize("\"hello\"").toBoolean());
    }

    public void test_read_int_as_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("100.5");
        Any any = iter.readAny();
        TestCase.assertEquals("100.5", any.toString());
    }

    public void test_get() throws IOException {
        TestCase.assertEquals("100.5", JsonIterator.deserialize("100.5").get().toString());
        TestCase.assertEquals("100.5", JsonIterator.deserialize("[100.5]").get(0).toString());
        TestCase.assertEquals(INVALID, JsonIterator.deserialize("null").get(0).valueType());
        TestCase.assertEquals(INVALID, JsonIterator.deserialize("[]").get(0).valueType());
        TestCase.assertEquals(INVALID, JsonIterator.deserialize("[]").get("hello").valueType());
        TestCase.assertEquals(INVALID, JsonIterator.deserialize("{}").get(0).valueType());
    }

    public void test_read_long() throws IOException {
        TestCase.assertEquals(100L, JsonIterator.deserialize("100").toLong());
        TestCase.assertEquals(100L, JsonIterator.deserialize("100.1").toLong());
        Any any = JsonIterator.deserialize("\"100.1\"");
        TestCase.assertEquals(100L, any.toLong());
        TestCase.assertEquals(100L, any.toLong());
    }

    public void test_read_float() throws IOException {
        TestCase.assertEquals(100.0F, JsonIterator.deserialize("100").toFloat());
        TestCase.assertEquals(100.1F, JsonIterator.deserialize("100.1").toFloat());
        TestCase.assertEquals(100.1F, JsonIterator.deserialize("\"100.1\"").toFloat());
    }

    public void test_size() throws IOException {
        TestCase.assertEquals(0, JsonIterator.deserialize("[]").size());
        TestCase.assertEquals(1, JsonIterator.deserialize("[1]").size());
        TestCase.assertEquals(2, JsonIterator.deserialize("[1,2]").size());
        TestCase.assertEquals(1, JsonIterator.deserialize("{\"field1\":1}").size());
    }

    public void test_keys() throws IOException {
        TestCase.assertEquals(new HashSet<Object>(Arrays.asList("field1")), JsonIterator.deserialize("{\"field1\":1}").keys());
        TestCase.assertEquals(new HashSet<Object>(Arrays.asList()), JsonIterator.deserialize("[3,5]").keys());
    }

    public void test_read_double() throws IOException {
        TestCase.assertEquals(100.0, JsonIterator.deserialize("100").toDouble());
        TestCase.assertEquals(100.1, JsonIterator.deserialize("100.1").toDouble());
        TestCase.assertEquals(100.1, JsonIterator.deserialize("\"100.1\"").toDouble());
    }

    public static class TestObject1 {
        public int field1;
    }

    public void test_read_class() throws IOException {
        TestReadAny.TestObject1 obj = JsonIterator.deserialize("{\"field1\": 100}").as(TestReadAny.TestObject1.class);
        TestCase.assertEquals(100, obj.field1);
    }

    public void test_read_multiple_field() throws IOException {
        Any any = JsonIterator.deserialize("{\"a\":1,\"b\":2,\"c\":3}");
        TestCase.assertEquals(2, any.toInt("b"));
        TestCase.assertEquals(1, any.toInt("a"));
        TestCase.assertEquals(3, any.toInt("c"));
        any = JsonIterator.deserialize("{\"a\":1,\"b\":2,\"c\":3}");
        TestCase.assertEquals(3, any.toInt("c"));
        TestCase.assertEquals(2, any.toInt("b"));
        TestCase.assertEquals(1, any.toInt("a"));
    }

    public void test_require_path() throws IOException {
        TestCase.assertNotNull(JsonIterator.deserialize("null").get());
        try {
            JsonIterator.deserialize("[]").get(0).object();
        } catch (JsonException e) {
            System.out.println(e);
        }
        try {
            Any.rewrap(new ArrayList<Any>()).get(0).object();
        } catch (JsonException e) {
            System.out.println(e);
        }
        try {
            JsonIterator.deserialize("{}").get("hello").object();
        } catch (JsonException e) {
            System.out.println(e);
        }
        try {
            Any.rewrap(new HashMap<String, Any>()).get("hello").object();
        } catch (JsonException e) {
            System.out.println(e);
        }
    }
}

