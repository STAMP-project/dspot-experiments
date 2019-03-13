package com.jsoniter.output;


import com.jsoniter.annotation.JsonIgnore;
import java.io.IOException;
import junit.framework.TestCase;


public class TestAnnotationJsonIgnore extends TestCase {
    public static class TestObject1 {
        @JsonIgnore
        public int field1;
    }

    public void test_ignore() throws IOException {
        TestAnnotationJsonIgnore.TestObject1 obj = new TestAnnotationJsonIgnore.TestObject1();
        obj.field1 = 100;
        TestCase.assertEquals("{}", JsonStream.serialize(obj));
    }

    public static class TestObject2 {
        @JsonIgnore(ignoreEncoding = false)
        public int field1;
    }

    public void test_ignore_decoding_only() throws IOException {
        TestAnnotationJsonIgnore.TestObject2 obj = new TestAnnotationJsonIgnore.TestObject2();
        obj.field1 = 100;
        TestCase.assertEquals("{\"field1\":100}", JsonStream.serialize(obj));
    }

    public static class TestPrivateVariables {
        @JsonIgnore
        private String field1;

        public String getField1() {
            return field1;
        }
    }

    public void test_private_serialize() throws IOException {
        TestAnnotationJsonIgnore.TestPrivateVariables obj = new TestAnnotationJsonIgnore.TestPrivateVariables();
        obj.field1 = "hello";
        TestCase.assertEquals("{}", JsonStream.serialize(obj));
    }
}

