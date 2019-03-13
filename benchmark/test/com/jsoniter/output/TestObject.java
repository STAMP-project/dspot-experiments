package com.jsoniter.output;


import EncodingMode.DYNAMIC_MODE;
import EncodingMode.REFLECTION_MODE;
import com.jsoniter.annotation.JsonIgnore;
import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


public class TestObject extends TestCase {
    static {
        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;

    private JsonStream stream;

    public static class TestObject1 {
        public String field1;
    }

    public void test_field() throws IOException {
        TestObject.TestObject1 obj = new TestObject.TestObject1();
        obj.field1 = "hello";
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{'field1':'hello'}".replace('\'', '"'), baos.toString());
    }

    public static class TestObject2 {
        @JsonIgnore
        private String field1;

        public String getField1() {
            return field1;
        }
    }

    public void test_null() throws IOException {
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<TestObject.TestObject2>() {}, null);
        stream.close();
        TestCase.assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public static class TestObject3 {}

    public void test_empty_object() throws IOException {
        stream.writeVal(new TestObject.TestObject3());
        stream.close();
        TestCase.assertEquals("{}".replace('\'', '"'), baos.toString());
    }

    public static class TestObject4 {
        public String field1;
    }

    public void test_null_field() throws IOException {
        TestObject.TestObject4 obj = new TestObject.TestObject4();
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{\"field1\":null}".replace('\'', '"'), baos.toString());
    }

    public static enum MyEnum {

        HELLO;}

    public static class TestObject5 {
        public TestObject.MyEnum field1;
    }

    public void test_enum() throws IOException {
        TestObject.TestObject5 obj = new TestObject.TestObject5();
        obj.field1 = TestObject.MyEnum.HELLO;
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{'field1':'HELLO'}".replace('\'', '"'), baos.toString());
        Config cfg = new Config.Builder().encodingMode(DYNAMIC_MODE).indentionStep(2).build();
        TestCase.assertEquals(("{\n" + ("  \"field1\": \"HELLO\"\n" + "}")), JsonStream.serialize(cfg, obj));
    }

    public static class TestObject6 {
        public int[] field1;
    }

    public void test_array_field() throws IOException {
        TestObject.TestObject6 obj = new TestObject.TestObject6();
        obj.field1 = new int[]{ 1, 2, 3 };
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{\"field1\":[1,2,3]}", baos.toString());
    }

    public void test_array_field_is_null() throws IOException {
        TestObject.TestObject6 obj = new TestObject.TestObject6();
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{\"field1\":null}", baos.toString());
    }

    public static class TestObject7 {
        private int[] field1;

        @JsonProperty(defaultValueToOmit = "void")
        public int[] getField1() {
            return field1;
        }
    }

    public void test_array_field_is_null_via_getter() throws IOException {
        TestObject.TestObject7 obj = new TestObject.TestObject7();
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{\"field1\":null}", baos.toString());
    }

    public static class TestObject8 {
        @JsonProperty(nullable = false)
        public String[] field1;
    }

    public void test_not_nullable() {
        TestObject.TestObject8 obj = new TestObject.TestObject8();
        obj.field1 = new String[]{ "hello" };
        Config config = new Config.Builder().encodingMode(DYNAMIC_MODE).build();
        TestCase.assertEquals("{\"field1\":[\"hello\"]}", JsonStream.serialize(config, obj));
        try {
            JsonStream.serialize(config, new TestObject.TestObject8());
            TestCase.fail();
        } catch (NullPointerException ignore) {
        }
    }

    public static class TestObject9 {
        @JsonProperty(collectionValueNullable = false, defaultValueToOmit = "null")
        public String[] field1;

        @JsonProperty(collectionValueNullable = false, defaultValueToOmit = "null")
        public List<String> field2;

        @JsonProperty(collectionValueNullable = false, defaultValueToOmit = "null")
        public Set<String> field3;

        @JsonProperty(collectionValueNullable = false, defaultValueToOmit = "null")
        public Map<String, String> field4;
    }

    public void test_collection_value_not_nullable() {
        TestObject.TestObject9 obj = new TestObject.TestObject9();
        obj.field1 = new String[]{ "hello" };
        TestCase.assertEquals("{\"field1\":[\"hello\"]}", JsonStream.serialize(obj));
        Config config = new Config.Builder().encodingMode(DYNAMIC_MODE).build();
        obj = new TestObject.TestObject9();
        obj.field1 = new String[]{ null };
        try {
            JsonStream.serialize(config, obj);
            TestCase.fail();
        } catch (NullPointerException ignore) {
        }
        obj = new TestObject.TestObject9();
        obj.field2 = new ArrayList();
        obj.field2.add(null);
        try {
            JsonStream.serialize(config, obj);
            TestCase.fail();
        } catch (NullPointerException ignore) {
        }
        obj = new TestObject.TestObject9();
        obj.field3 = new HashSet<String>();
        obj.field3.add(null);
        try {
            JsonStream.serialize(config, obj);
            TestCase.fail();
        } catch (NullPointerException ignore) {
        }
        obj = new TestObject.TestObject9();
        obj.field4 = new HashMap<String, String>();
        obj.field4.put("hello", null);
        try {
            JsonStream.serialize(config, obj);
            TestCase.fail();
        } catch (NullPointerException ignore) {
        }
    }

    public static class TestObject10 {
        @JsonProperty(defaultValueToOmit = "void")
        public String field1;
    }

    public void test_not_omit_null() {
        TestCase.assertEquals("{\"field1\":null}", JsonStream.serialize(new TestObject.TestObject10()));
    }

    public static class TestObject11 {
        @JsonProperty(defaultValueToOmit = "null")
        public String field1;

        @JsonProperty(defaultValueToOmit = "null")
        public String field2;

        @JsonProperty(nullable = false)
        public Integer field3;
    }

    public void test_omit_null() {
        TestCase.assertEquals("{\"field3\":null}", JsonStream.serialize(new TestObject.TestObject11()));
        TestObject.TestObject11 obj = new TestObject.TestObject11();
        obj.field1 = "hello";
        TestCase.assertEquals("{\"field1\":\"hello\",\"field3\":null}", JsonStream.serialize(obj));
        obj = new TestObject.TestObject11();
        obj.field2 = "hello";
        TestCase.assertEquals("{\"field2\":\"hello\",\"field3\":null}", JsonStream.serialize(obj));
        obj = new TestObject.TestObject11();
        obj.field3 = 3;
        TestCase.assertEquals("{\"field3\":3}", JsonStream.serialize(obj));
    }

    public static class TestObject12 {
        public int field1;

        public int getField1() {
            return field1;
        }
    }

    public void test_name_conflict() throws IOException {
        TestObject.TestObject12 obj = new TestObject.TestObject12();
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{\"field1\":0}", baos.toString());
    }

    private static class TestObject13 {}

    public void test_private_class() {
        EncodingMode encodingMode = JsoniterSpi.getCurrentConfig().encodingMode();
        if (REFLECTION_MODE.equals(encodingMode)) {
            return;
        }
        try {
            JsonStream.serialize(new TestObject.TestObject13());
            TestCase.fail("should throw JsonException");
        } catch (JsonException ignore) {
        }
    }

    public static class TestObject14 {
        @JsonProperty(nullable = true, defaultValueToOmit = "null")
        public String field1;

        @JsonProperty(nullable = false)
        public String field2;

        @JsonProperty(nullable = true, defaultValueToOmit = "void")
        public String field3;
    }

    public void test_indention() {
        Config dynamicCfg = new Config.Builder().indentionStep(2).encodingMode(DYNAMIC_MODE).build();
        TestObject.TestObject14 obj = new TestObject.TestObject14();
        obj.field1 = "1";
        obj.field2 = "2";
        String output = JsonStream.serialize(dynamicCfg, obj);
        TestCase.assertEquals(("{\n" + ((("  \"field1\": \"1\",\n" + "  \"field2\": \"2\",\n") + "  \"field3\": null\n") + "}")), output);
        Config reflectionCfg = new Config.Builder().indentionStep(2).encodingMode(REFLECTION_MODE).build();
        output = JsonStream.serialize(reflectionCfg, obj);
        TestCase.assertEquals(("{\n" + ((("  \"field1\": \"1\",\n" + "  \"field2\": \"2\",\n") + "  \"field3\": null\n") + "}")), output);
    }

    public static class TestObject15 {
        @JsonProperty(defaultValueToOmit = "null")
        public Integer i1;

        @JsonProperty(defaultValueToOmit = "null")
        public Integer i2;
    }

    public void test_indention_with_empty_object() {
        Config config = JsoniterSpi.getCurrentConfig().copyBuilder().indentionStep(2).encodingMode(REFLECTION_MODE).build();
        TestCase.assertEquals("{}", JsonStream.serialize(config, new TestObject.TestObject15()));
        config = JsoniterSpi.getCurrentConfig().copyBuilder().indentionStep(2).encodingMode(DYNAMIC_MODE).build();
        TestCase.assertEquals("{}", JsonStream.serialize(config, new TestObject.TestObject15()));
    }

    public static class TestObject16 {
        @JsonProperty(defaultValueToOmit = "void")
        public Integer i;
    }

    public void test_missing_notFirst() {
        Config cfg = JsoniterSpi.getCurrentConfig().copyBuilder().indentionStep(2).encodingMode(DYNAMIC_MODE).build();
        TestCase.assertEquals(("{\n" + ("  \"i\": null\n" + "}")), JsonStream.serialize(cfg, new TestObject.TestObject16()));
    }

    public static class TestObject17 {
        public boolean b;

        public int i;

        public byte bt;

        public short s;

        public long l = 1;

        public float f;

        public double d = 1;

        public char e;
    }

    public void test_omit_default() {
        Config cfg = new Config.Builder().omitDefaultValue(true).build();
        TestCase.assertEquals("{\"l\":1,\"d\":1}", JsonStream.serialize(cfg, new TestObject.TestObject17()));
        cfg = new Config.Builder().omitDefaultValue(true).encodingMode(DYNAMIC_MODE).build();
        TestCase.assertEquals("{\"l\":1,\"d\":1}", JsonStream.serialize(cfg, new TestObject.TestObject17()));
    }

    public static class TestObject18 {
        @JsonProperty(defaultValueToOmit = "true")
        public boolean b = true;

        @JsonProperty(defaultValueToOmit = "true")
        public Boolean B = true;

        @JsonProperty(defaultValueToOmit = "1")
        public int i = 1;

        @JsonProperty(defaultValueToOmit = "1")
        public Integer I = 1;

        @JsonProperty(defaultValueToOmit = "1")
        public byte bt = 1;

        @JsonProperty(defaultValueToOmit = "1")
        public Byte BT = 1;

        @JsonProperty(defaultValueToOmit = "1")
        public short s = 1;

        @JsonProperty(defaultValueToOmit = "1")
        public Short S = 1;

        @JsonProperty(defaultValueToOmit = "1")
        public long l = 1L;

        @JsonProperty(defaultValueToOmit = "1")
        public Long L = 1L;

        @JsonProperty(defaultValueToOmit = "1")
        public float f = 1.0F;

        @JsonProperty(defaultValueToOmit = "1")
        public Float F = 1.0F;

        @JsonProperty(defaultValueToOmit = "1")
        public double d = 1.0;

        @JsonProperty(defaultValueToOmit = "1")
        public Double D = 1.0;

        @JsonProperty(defaultValueToOmit = "a")
        public char c = 'a';

        @JsonProperty(defaultValueToOmit = "a")
        public Character C = 'a';
    }

    public void test_omit_selft_defined() {
        Config cfg = new Config.Builder().omitDefaultValue(true).build();
        TestCase.assertEquals("{}", JsonStream.serialize(cfg, new TestObject.TestObject18()));
        cfg = new Config.Builder().omitDefaultValue(true).encodingMode(DYNAMIC_MODE).build();
        TestCase.assertEquals("{}", JsonStream.serialize(cfg, new TestObject.TestObject18()));
    }

    public static class TestObject19 {
        public transient int hello;

        public int getHello() {
            return hello;
        }
    }

    public void test_transient_field_getter() {
        String output = JsonStream.serialize(new TestObject.TestObject19());
        TestCase.assertEquals("{}", output);
    }
}

