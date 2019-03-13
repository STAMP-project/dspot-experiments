package com.jsoniter;


import com.jsoniter.annotation.JsonCreator;
import com.jsoniter.annotation.JsonMissingProperties;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.fuzzy.StringIntDecoder;
import com.jsoniter.output.JsonStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import junit.framework.TestCase;


public class TestAnnotationJsonProperty extends TestCase {
    public static class TestObject1 {
        @JsonProperty(from = { "field-1" })
        public int field1;
    }

    public void test_rename() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field-1': 100}".replace('\'', '"'));
        TestAnnotationJsonProperty.TestObject1 obj = iter.read(TestAnnotationJsonProperty.TestObject1.class);
        TestCase.assertEquals(100, obj.field1);
    }

    public static class TestObject2 {
        @JsonProperty(required = true)
        public int field1;

        @JsonMissingProperties
        public List<String> missingProperties;
    }

    public void test_required_properties() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        TestAnnotationJsonProperty.TestObject2 obj = iter.read(TestAnnotationJsonProperty.TestObject2.class);
        TestCase.assertEquals(Arrays.asList("field1"), obj.missingProperties);
    }

    public static class TestObject3 {
        @JsonProperty(decoder = StringIntDecoder.class)
        public int field1;
    }

    public void test_property_decoder() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": \"100\"}");
        TestAnnotationJsonProperty.TestObject3 obj = iter.read(TestAnnotationJsonProperty.TestObject3.class);
        TestCase.assertEquals(100, obj.field1);
    }

    public static class TestObject4 {
        @JsonProperty(decoder = StringIntDecoder.class)
        public Integer field1;
    }

    public void test_integer_property_decoder() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": \"100\"}");
        TestAnnotationJsonProperty.TestObject4 obj = iter.read(TestAnnotationJsonProperty.TestObject4.class);
        TestCase.assertEquals(Integer.valueOf(100), obj.field1);
    }

    public static class TestObject5 {
        @JsonProperty(from = { "field_1", "field-1" })
        public int field1;
    }

    public void test_bind_from_multiple_names() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field-1\": 100, \"field-1\": 101}");
        TestAnnotationJsonProperty.TestObject5 obj = iter.read(TestAnnotationJsonProperty.TestObject5.class);
        TestCase.assertEquals(101, obj.field1);
    }

    public static class TestObject6 {
        @JsonProperty(required = true)
        public int field1;

        @JsonMissingProperties
        public List<String> missingProperties;
    }

    public void test_required_properties_not_missing() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": 100}");
        TestAnnotationJsonProperty.TestObject6 obj = iter.read(TestAnnotationJsonProperty.TestObject6.class);
        TestCase.assertNull(obj.missingProperties);
        TestCase.assertEquals(100, obj.field1);
    }

    public static class TestObject7 {
        @JsonProperty(implementation = LinkedList.class)
        public List<Integer> values;
    }

    public void test_specify_property() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"values\": [100]}");
        TestAnnotationJsonProperty.TestObject7 obj = iter.read(TestAnnotationJsonProperty.TestObject7.class);
        TestCase.assertEquals(Arrays.asList(100), obj.values);
        TestCase.assertEquals(LinkedList.class, obj.values.getClass());
    }

    public static class TestObject8 {
        public String error;

        @JsonProperty(value = "rs", required = true)
        public boolean result;

        @JsonProperty(value = "code", required = true)
        public int code2;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("code=");
            builder.append(code2);
            builder.append(" rs=");
            builder.append(result);
            return builder.toString();
        }
    }

    public void test_required() throws IOException {
        String test = "{\"rs\":true,\"code\":200}";
        TestAnnotationJsonProperty.TestObject8 entity = JsonIterator.deserialize(test, TestAnnotationJsonProperty.TestObject8.class);
        TestCase.assertEquals(200, entity.code2);
    }

    public static class TestObject9 {
        private String field1 = "hello";

        public String getField1() {
            return field1;
        }

        @JsonProperty("field-1")
        public void setField1(String field1) {
            this.field1 = field1;
        }
    }

    public void test_getter_and_setter() throws IOException {
        String test = "{\"field-1\":\"hi\"}";
        TestAnnotationJsonProperty.TestObject9 entity = JsonIterator.deserialize(test, TestAnnotationJsonProperty.TestObject9.class);
        TestCase.assertEquals("hi", entity.getField1());
    }

    public static class TestObject10 {
        private int field;

        @JsonCreator
        public TestObject10(@JsonProperty("hello")
        int field) {
            this.field = field;
        }

        public int getField() {
            return field;
        }
    }

    public void test_creator_with_json_property() {
        String input = "{\"hello\":100}";
        TestAnnotationJsonProperty.TestObject10 obj = JsonIterator.deserialize(input, TestAnnotationJsonProperty.TestObject10.class);
        TestCase.assertEquals(100, obj.field);
        TestCase.assertEquals("{\"field\":100}", JsonStream.serialize(obj));
    }

    public static class TestObject11 {
        @JsonProperty("hello")
        public int field;

        public int getField() {
            return field;
        }

        public void setField(int field) {
            this.field = field;
        }
    }

    public void test_field_and_getter_setter() {
        String input = "{\"hello\":100}";
        TestAnnotationJsonProperty.TestObject11 obj = JsonIterator.deserialize(input, TestAnnotationJsonProperty.TestObject11.class);
        TestCase.assertEquals(100, obj.field);
    }
}

