package com.jsoniter.output;


import Encoder.StringIntEncoder;
import com.jsoniter.annotation.JsonProperty;
import java.io.IOException;
import junit.framework.TestCase;


public class TestAnnotationJsonProperty extends TestCase {
    static {
        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    public static class TestObject1 {
        @JsonProperty(to = { "field-1" })
        public String field1;
    }

    public void test_property() throws IOException {
        TestAnnotationJsonProperty.TestObject1 obj = new TestAnnotationJsonProperty.TestObject1();
        obj.field1 = "hello";
        String output = JsonStream.serialize(obj);
        TestCase.assertEquals("{\"field-1\":\"hello\"}", output);
    }

    public static class TestObject2 {
        @JsonProperty(encoder = StringIntEncoder.class)
        public int field1;
    }

    public void test_encoder() throws IOException {
        TestAnnotationJsonProperty.TestObject2 obj = new TestAnnotationJsonProperty.TestObject2();
        obj.field1 = 100;
        String output = JsonStream.serialize(obj);
        TestCase.assertEquals("{\"field1\":\"100\"}", output);
    }

    public static class TestObject3 {
        public String field1 = "hello";

        @JsonProperty("field-1")
        public String getField1() {
            return field1;
        }
    }

    public void test_getter() throws IOException {
        String output = JsonStream.serialize(new TestAnnotationJsonProperty.TestObject3());
        TestCase.assertEquals("{\"field-1\":\"hello\"}", output);
    }

    public static class TestObject4 {
        private String field1 = "hello";

        @JsonProperty("field-1")
        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }
    }

    public void test_getter_and_setter() throws IOException {
        String output = JsonStream.serialize(new TestAnnotationJsonProperty.TestObject4());
        TestCase.assertEquals("{\"field-1\":\"hello\"}", output);
    }
}

