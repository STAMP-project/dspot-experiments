package com.jsoniter.output;


import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.spi.JsoniterSpi;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

import static EncodingMode.REFLECTION_MODE;


public class TestNested extends TestCase {
    static {
        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;

    private JsonStream stream;

    public static class TestObject1 {
        public String field1;

        public String field2;
    }

    public void test_array_of_objects() throws IOException {
        TestNested.TestObject1 obj1 = new TestNested.TestObject1();
        obj1.field1 = "1";
        obj1.field2 = "2";
        String output = JsonStream.serialize(new TestNested.TestObject1[]{ obj1 });
        TestCase.assertTrue(output.contains("field1"));
        TestCase.assertTrue(output.contains("field2"));
    }

    public void test_collection_of_objects() throws IOException {
        final TestNested.TestObject1 obj1 = new TestNested.TestObject1();
        obj1.field1 = "1";
        obj1.field2 = "2";
        String output = JsonStream.serialize(new com.jsoniter.spi.TypeLiteral<java.util.List<TestNested.TestObject1>>() {}, new ArrayList() {
            {
                add(obj1);
            }
        });
        TestCase.assertTrue(output.contains("field1"));
        TestCase.assertTrue(output.contains("field2"));
    }

    public static class TestObject2 {
        public TestNested.TestObject1[] objs;
    }

    public void test_object_of_array() throws IOException {
        if ((JsoniterSpi.getCurrentConfig().encodingMode()) != (REFLECTION_MODE)) {
            return;
        }
        JsonStream.setIndentionStep(2);
        try {
            TestNested.TestObject2 obj = new TestNested.TestObject2();
            obj.objs = new TestNested.TestObject1[1];
            obj.objs[0] = new TestNested.TestObject1();
            obj.objs[0].field1 = "1";
            obj.objs[0].field2 = "2";
            stream.writeVal(obj);
            stream.close();
            TestCase.assertEquals((("{\n" + ((((("  \"objs\": [\n" + "    {\n") + "      \"field1\": \"1\",\n") + "      \"field2\": \"2\"\n") + "    }\n") + "  ]\n")) + ("}".replace('\'', '"'))), baos.toString());
        } finally {
            JsonStream.setIndentionStep(0);
        }
    }

    public void test_map_of_objects() throws IOException {
        if ((JsoniterSpi.getCurrentConfig().encodingMode()) != (REFLECTION_MODE)) {
            return;
        }
        JsonStream.setIndentionStep(2);
        try {
            final TestNested.TestObject1 obj1 = new TestNested.TestObject1();
            obj1.field1 = "1";
            obj1.field2 = "2";
            stream.writeVal(new com.jsoniter.spi.TypeLiteral<Map<String, TestNested.TestObject1>>() {}, new HashMap() {
                {
                    put("hello", obj1);
                }
            });
            stream.close();
            TestCase.assertEquals((("{\n" + ((("  \"hello\": {\n" + "    \"field1\": \"1\",\n") + "    \"field2\": \"2\"\n") + "  }\n")) + ("}".replace('\'', '"'))), baos.toString());
        } finally {
            JsonStream.setIndentionStep(0);
        }
    }

    public static class TestObject3 {
        @JsonProperty(defaultValueToOmit = "void")
        public TestNested.TestObject3 reference;
    }

    public void test_recursive_class() {
        // recursive reference will not be supported
        // however recursive structure is supported
        TestNested.TestObject3 obj = new TestNested.TestObject3();
        TestCase.assertEquals("{\"reference\":null}", JsonStream.serialize(obj));
    }
}

