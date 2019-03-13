package com.jsoniter.output;


import ValueType.ARRAY;
import ValueType.BOOLEAN;
import ValueType.NULL;
import ValueType.NUMBER;
import ValueType.OBJECT;
import ValueType.STRING;
import com.jsoniter.spi.JsonException;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.rules.ExpectedException;


public class TestAny extends TestCase {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    static {
        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    public void test_int() {
        Any any = Any.wrap(100);
        TestCase.assertEquals(NUMBER, any.valueType());
        TestCase.assertEquals("100", JsonStream.serialize(any));
        TestCase.assertEquals(Integer.valueOf(100), any.object());
        TestCase.assertEquals(100, any.toInt());
        TestCase.assertEquals(100L, any.toLong());
        TestCase.assertEquals(100.0, any.toDouble());
        TestCase.assertEquals(100.0F, any.toFloat());
        TestCase.assertEquals("100", any.toString());
        TestCase.assertEquals(true, any.toBoolean());
        any.set(101);
        TestCase.assertEquals("101", any.toString());
    }

    public void test_long() {
        Any any = Any.wrap(100L);
        TestCase.assertEquals(NUMBER, any.valueType());
        TestCase.assertEquals("100", JsonStream.serialize(any));
        TestCase.assertEquals(100, any.toInt());
        TestCase.assertEquals(100L, any.toLong());
        TestCase.assertEquals(100.0, any.toDouble());
        TestCase.assertEquals(100.0F, any.toFloat());
        TestCase.assertEquals("100", any.toString());
        TestCase.assertEquals(true, any.toBoolean());
        any.set(101L);
        TestCase.assertEquals("101", any.toString());
    }

    public void test_float() {
        Any any = Any.wrap(100.0F);
        TestCase.assertEquals(NUMBER, any.valueType());
        TestCase.assertEquals("100", JsonStream.serialize(any));
        TestCase.assertEquals(100, any.toInt());
        TestCase.assertEquals(100L, any.toLong());
        TestCase.assertEquals(100.0, any.toDouble());
        TestCase.assertEquals(100.0F, any.toFloat());
        TestCase.assertEquals("100.0", any.toString());
        TestCase.assertEquals(true, any.toBoolean());
        any.set(101.0F);
        TestCase.assertEquals("101.0", any.toString());
    }

    public void test_double() {
        Any any = Any.wrap(100.0);
        TestCase.assertEquals(NUMBER, any.valueType());
        TestCase.assertEquals("100", JsonStream.serialize(any));
        TestCase.assertEquals(100, any.toInt());
        TestCase.assertEquals(100L, any.toLong());
        TestCase.assertEquals(100.0, any.toDouble());
        TestCase.assertEquals(100.0F, any.toFloat());
        TestCase.assertEquals("100.0", any.toString());
        TestCase.assertEquals(true, any.toBoolean());
        any.set(101.0);
        TestCase.assertEquals("101.0", any.toString());
    }

    public void test_null() {
        Any any = Any.wrap(((Object) (null)));
        TestCase.assertEquals(NULL, any.valueType());
        TestCase.assertEquals("null", JsonStream.serialize(any));
        TestCase.assertEquals(false, any.toBoolean());
        TestCase.assertEquals("null", any.toString());
    }

    public void test_boolean() {
        Any any = Any.wrap(true);
        TestCase.assertEquals(BOOLEAN, any.valueType());
        TestCase.assertEquals("true", JsonStream.serialize(any));
        TestCase.assertEquals(1, any.toInt());
        TestCase.assertEquals(1L, any.toLong());
        TestCase.assertEquals(1.0F, any.toFloat());
        TestCase.assertEquals(1.0, any.toDouble());
        TestCase.assertEquals("true", any.toString());
    }

    public void test_string() {
        Any any = Any.wrap("hello");
        TestCase.assertEquals(STRING, any.valueType());
        TestCase.assertEquals("\"hello\"", JsonStream.serialize(any));
        any.set("100");
        TestCase.assertEquals(100, any.toInt());
        TestCase.assertEquals(100L, any.toLong());
        TestCase.assertEquals(100.0F, any.toFloat());
        TestCase.assertEquals(100.0, any.toDouble());
        TestCase.assertEquals(true, any.toBoolean());
        TestCase.assertEquals("100", any.toString());
    }

    public void test_list() {
        Any any = Any.wrap(Arrays.asList(1, 2, 3));
        TestCase.assertEquals(ARRAY, any.valueType());
        TestCase.assertEquals("[1,2,3]", JsonStream.serialize(any));
        TestCase.assertEquals(Integer.valueOf(1), any.get(0).object());
        TestCase.assertEquals(true, any.toBoolean());
        TestCase.assertEquals("[1,2,3]", any.toString());
    }

    public void test_array() {
        Any any = Any.wrap(new int[]{ 1, 2, 3 });
        TestCase.assertEquals(ARRAY, any.valueType());
        TestCase.assertEquals("[1,2,3]", JsonStream.serialize(any));
        TestCase.assertEquals(Integer.valueOf(1), any.get(0).object());
        TestCase.assertEquals(true, any.toBoolean());
        TestCase.assertEquals("[1,2,3]", any.toString());
    }

    public void test_not_found() {
        Any any = Any.wrap(new int[]{ 1, 2, 3 });
        exception.expect(JsonException.class);
        any.get("not", "found", "path");
    }

    public static class MyClass {
        public Object field1;

        public com.jsoniter.any.Any field2;
    }

    public void test_object() {
        Any any = Any.wrap(new Object());
        TestCase.assertEquals(OBJECT, any.valueType());
        TestCase.assertEquals("{}", JsonStream.serialize(new Object()));
    }
}

