package com.jsoniter;


import Any.EntryIterator;
import ValueType.INVALID;
import ValueType.NULL;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.any.Any;
import com.jsoniter.fuzzy.MaybeEmptyArrayDecoder;
import com.jsoniter.spi.EmptyExtension;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import junit.framework.TestCase;


public class TestObject extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }

    public static class EmptyClass {}

    public void test_empty_class() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        TestCase.assertNotNull(iter.read(TestObject.EmptyClass.class));
    }

    public void test_empty_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        TestCase.assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        TestCase.assertNull(simpleObj.field1);
        iter.reset(iter.buf);
        Object obj = iter.read(Object.class);
        TestCase.assertEquals(0, ((Map) (obj)).size());
        iter.reset(iter.buf);
        Any any = iter.readAny();
        TestCase.assertEquals(0, any.size());
    }

    public void test_one_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ \'field1\'\r:\n\t\'hello\' }".replace('\'', '"'));
        TestCase.assertEquals("field1", iter.readObject());
        TestCase.assertEquals("hello", iter.readString());
        TestCase.assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        TestCase.assertEquals("hello", simpleObj.field1);
        TestCase.assertNull(simpleObj.field2);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        TestCase.assertEquals("hello", any.toString("field1"));
        TestCase.assertEquals(INVALID, any.get("field2").valueType());
        iter.reset(iter.buf);
        TestCase.assertEquals("hello", ((Map) (iter.read())).get("field1"));
    }

    public void test_two_fields() throws IOException {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' , 'field2': 'world' }".replace('\'', '"'));
        TestCase.assertEquals("field1", iter.readObject());
        TestCase.assertEquals("hello", iter.readString());
        TestCase.assertEquals("field2", iter.readObject());
        TestCase.assertEquals("world", iter.readString());
        TestCase.assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        TestCase.assertEquals("hello", simpleObj.field1);
        TestCase.assertEquals("world", simpleObj.field2);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        TestCase.assertEquals("hello", any.toString("field1"));
        TestCase.assertEquals("world", any.toString("field2"));
        iter.reset(iter.buf);
        final ArrayList<String> fields = new ArrayList<String>();
        iter.readObjectCB(new JsonIterator.ReadObjectCallback() {
            @Override
            public boolean handle(JsonIterator iter, String field, Object attachment) throws IOException {
                fields.add(field);
                iter.skip();
                return true;
            }
        }, null);
        TestCase.assertEquals(Arrays.asList("field1", "field2"), fields);
    }

    public void test_read_null() throws IOException {
        JsonIterator iter = JsonIterator.parse("null".replace('\'', '"'));
        TestCase.assertTrue(iter.readNull());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        TestCase.assertNull(simpleObj);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        TestCase.assertEquals(NULL, any.get().valueType());
    }

    public void test_native_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 100 }".replace('\'', '"'));
        ComplexObject complexObject = iter.read(ComplexObject.class);
        TestCase.assertEquals(100, complexObject.field1);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        TestCase.assertEquals(100, any.toInt("field1"));
    }

    public static class InheritedObject extends SimpleObject {
        public String inheritedField;
    }

    public void test_inheritance() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'inheritedField': 'hello'}".replace('\'', '"'));
        TestObject.InheritedObject inheritedObject = iter.read(TestObject.InheritedObject.class);
        TestCase.assertEquals("hello", inheritedObject.inheritedField);
    }

    public void test_incomplete_field_name() throws IOException {
        try {
            JsonIterator.parse("{\"abc").read(TestObject.InheritedObject.class);
            TestCase.fail();
        } catch (JsonException e) {
        }
    }

    public static interface IDependenceInjectedObject {
        String getSomeService();
    }

    public static class DependenceInjectedObject implements TestObject.IDependenceInjectedObject {
        private String someService;

        public DependenceInjectedObject(String someService) {
            this.someService = someService;
        }

        public String getSomeService() {
            return someService;
        }
    }

    public void test_object_creation() throws IOException {
        JsoniterSpi.registerExtension(new EmptyExtension() {
            @Override
            public boolean canCreate(Class clazz) {
                return (clazz.equals(TestObject.DependenceInjectedObject.class)) || (clazz.equals(TestObject.IDependenceInjectedObject.class));
            }

            @Override
            public Object create(Class clazz) {
                return new TestObject.DependenceInjectedObject("hello");
            }
        });
        TestObject.IDependenceInjectedObject obj = JsonIterator.deserialize("{}", TestObject.IDependenceInjectedObject.class);
        TestCase.assertEquals("hello", obj.getSomeService());
    }

    public static class TestObject5 {
        public enum MyEnum {

            HELLO,
            WORLD,
            WOW;}

        public TestObject.TestObject5.MyEnum field1;
    }

    public void test_enum() throws IOException {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        TestObject.TestObject5 obj = JsonIterator.deserialize("{\"field1\":\"HELLO\"}", TestObject.TestObject5.class);
        TestCase.assertEquals(TestObject.TestObject5.MyEnum.HELLO, obj.field1);
        try {
            JsonIterator.deserialize("{\"field1\":\"HELLO1\"}", TestObject.TestObject5.class);
            TestCase.fail();
        } catch (JsonException e) {
        }
        obj = JsonIterator.deserialize("{\"field1\":null}", TestObject.TestObject5.class);
        TestCase.assertNull(obj.field1);
        obj = JsonIterator.deserialize("{\"field1\":\"WOW\"}", TestObject.TestObject5.class);
        TestCase.assertEquals(TestObject.TestObject5.MyEnum.WOW, obj.field1);
    }

    public static class TestObject6_field1 {
        public int a;
    }

    public static class TestObject6 {
        @JsonProperty(decoder = MaybeEmptyArrayDecoder.class)
        public TestObject.TestObject6_field1 field1;
    }

    public void test_maybe_empty_array_field() {
        TestObject.TestObject6 obj = JsonIterator.deserialize("{\"field1\":[]}", TestObject.TestObject6.class);
        TestCase.assertNull(obj.field1);
        obj = JsonIterator.deserialize("{\"field1\":{\"a\":1}}", TestObject.TestObject6.class);
        TestCase.assertEquals(1, obj.field1.a);
    }

    public void test_iterator() {
        Any any = JsonIterator.deserialize("{\"field1\":1,\"field2\":2,\"field3\":3}");
        Any.EntryIterator iter = any.entries();
        TestCase.assertTrue(iter.next());
        TestCase.assertEquals("field1", iter.key());
        TestCase.assertEquals(1, iter.value().toInt());
        iter = any.entries();
        TestCase.assertTrue(iter.next());
        TestCase.assertEquals("field1", iter.key());
        TestCase.assertEquals(1, iter.value().toInt());
        TestCase.assertTrue(iter.next());
        TestCase.assertEquals("field2", iter.key());
        TestCase.assertEquals(2, iter.value().toInt());
        TestCase.assertTrue(iter.next());
        TestCase.assertEquals("field3", iter.key());
        TestCase.assertEquals(3, iter.value().toInt());
        TestCase.assertFalse(iter.next());
    }

    public static class PublicSuper {
        public String field1;
    }

    private static class PrivateSub extends TestObject.PublicSuper {}

    public static class TestObject7 {
        public TestObject.PrivateSub field1;

        public void setFieldXXX(TestObject.PrivateSub obj) {
        }
    }

    public void test_private_ref() throws IOException {
        TestObject.TestObject7 obj = JsonIterator.deserialize("{}", TestObject.TestObject7.class);
        TestCase.assertNull(obj.field1);
    }

    public static class TestObject8 {
        public String field1;

        @JsonProperty(from = { "field-1" })
        public void setField1(String obj) {
            field1 = "!!!" + obj;
        }
    }

    public void test_setter_is_preferred() throws IOException {
        TestObject.TestObject8 obj = JsonIterator.deserialize("{\"field-1\":\"hello\"}", TestObject.TestObject8.class);
        TestCase.assertEquals("!!!hello", obj.field1);
    }

    public static class TestObject9 {
        public int ??;
    }

    public void test_non_ascii_field() {
        TestObject.TestObject9 obj = JsonIterator.deserialize("{\"\u5b57\u6bb5\":100}", TestObject.TestObject9.class);
        TestCase.assertEquals(100, obj.??);
    }
}

