package com.jsoniter.output;


import com.jsoniter.annotation.JsonUnwrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class TestAnnotationJsonUnwrapper extends TestCase {
    private ByteArrayOutputStream baos;

    private JsonStream stream;

    public static class TestObject1 {
        @JsonUnwrapper
        public void unwrap(JsonStream stream) throws IOException {
            stream.writeObjectField("hello");
            stream.writeVal("world");
        }
    }

    public void test_unwrapper() throws IOException {
        TestAnnotationJsonUnwrapper.TestObject1 obj = new TestAnnotationJsonUnwrapper.TestObject1();
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{\"hello\":\"world\"}", baos.toString());
    }

    public static class TestObject2 {
        @JsonUnwrapper
        public Map<Integer, Object> getProperties() {
            HashMap<Integer, Object> properties = new HashMap<Integer, Object>();
            properties.put(100, "hello");
            return properties;
        }
    }

    public void test_unwrapper_with_map() throws IOException {
        TestAnnotationJsonUnwrapper.TestObject2 obj = new TestAnnotationJsonUnwrapper.TestObject2();
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{\"100\":\"hello\"}", baos.toString());
    }
}

