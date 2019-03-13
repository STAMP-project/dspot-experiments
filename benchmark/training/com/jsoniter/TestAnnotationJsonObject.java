package com.jsoniter;


import com.jsoniter.annotation.JsonExtraProperties;
import com.jsoniter.annotation.JsonObject;
import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;
import java.io.IOException;
import java.util.Map;
import junit.framework.TestCase;


public class TestAnnotationJsonObject extends TestCase {
    @JsonObject(asExtraForUnknownProperties = true)
    public static class TestObject9 {
        @JsonExtraProperties
        public Map<String, Any> extraProperties;
    }

    public void test_extra_properties() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": 100}");
        TestAnnotationJsonObject.TestObject9 obj = iter.read(TestAnnotationJsonObject.TestObject9.class);
        TestCase.assertEquals(100, obj.extraProperties.get("field1").toInt());
    }

    @JsonObject(asExtraForUnknownProperties = true)
    public static class TestObject13 {}

    public void test_unknown_properties() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field-1\": 100, \"field-1\": 101}");
        try {
            iter.read(TestAnnotationJsonObject.TestObject13.class);
            TestCase.fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
    }

    @JsonObject(unknownPropertiesBlacklist = { "field1" })
    public static class TestObject15 {}

    public void test_unknown_properties_blacklist() throws IOException {
        JsonIterator iter = JsonIterator.parse("{\"field1\": 100}");
        try {
            iter.read(TestAnnotationJsonObject.TestObject15.class);
            TestCase.fail();
        } catch (JsonException e) {
            System.out.println(e);
        }
    }

    @JsonObject(asExtraForUnknownProperties = true)
    public static class TestObject14 {
        public int id;
    }

    public void test_no_unknown_properties() throws IOException {
        String json = "{ \"id\": 100 }";
        TestAnnotationJsonObject.TestObject14 obj = JsonIterator.deserialize(json, TestAnnotationJsonObject.TestObject14.class);
        TestCase.assertEquals(100, obj.id);
    }
}

