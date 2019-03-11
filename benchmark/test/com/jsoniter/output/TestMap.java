package com.jsoniter.output;


import EncodingMode.DYNAMIC_MODE;
import EncodingMode.REFLECTION_MODE;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class TestMap extends TestCase {
    static {
        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;

    private JsonStream stream;

    public void test() throws IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("hello", "world");
        stream.writeVal(map);
        stream.close();
        TestCase.assertEquals("{'hello':'world'}".replace('\'', '"'), baos.toString());
    }

    public void test_empty() throws IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        stream.writeVal(map);
        stream.close();
        TestCase.assertEquals("{}".replace('\'', '"'), baos.toString());
    }

    public void test_null() throws IOException {
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<HashMap>() {}, null);
        stream.close();
        TestCase.assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public void test_value_is_null() throws IOException {
        HashMap<String, int[]> obj = new HashMap<String, int[]>();
        obj.put("hello", null);
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<Map<String, int[]>>() {}, obj);
        stream.close();
        TestCase.assertEquals("{\"hello\":null}", baos.toString());
    }

    public void test_integer_key() throws IOException {
        HashMap<Integer, Object> obj = new HashMap<Integer, Object>();
        obj.put(100, null);
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<Map<Integer, Object>>() {}, obj);
        stream.close();
        TestCase.assertEquals("{\"100\":null}", baos.toString());
    }

    public static enum EnumKey {

        KeyA,
        KeyB;}

    public void test_enum_key() throws IOException {
        HashMap<TestMap.EnumKey, Object> obj = new HashMap<TestMap.EnumKey, Object>();
        obj.put(TestMap.EnumKey.KeyA, null);
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<Map<TestMap.EnumKey, Object>>() {}, obj);
        stream.close();
        TestCase.assertEquals("{\"KeyA\":null}", baos.toString());
    }

    public static class TestObject1 {
        public int Field;
    }

    public void test_MapKeyCodec() {
        JsoniterSpi.registerMapKeyEncoder(TestMap.TestObject1.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                TestMap.TestObject1 mapKey = ((TestMap.TestObject1) (obj));
                stream.writeVal(String.valueOf(mapKey.Field));
            }
        });
        HashMap<TestMap.TestObject1, Object> obj = new HashMap<TestMap.TestObject1, Object>();
        obj.put(new TestMap.TestObject1(), null);
        String output = JsonStream.serialize(new com.jsoniter.spi.TypeLiteral<Map<TestMap.TestObject1, Object>>() {}, obj);
        TestCase.assertEquals("{\"0\":null}", output);
    }

    public void test_indention_with_empty_map() {
        Config config = JsoniterSpi.getCurrentConfig().copyBuilder().indentionStep(2).encodingMode(REFLECTION_MODE).build();
        TestCase.assertEquals("{}", JsonStream.serialize(config, new HashMap<String, String>()));
        config = JsoniterSpi.getCurrentConfig().copyBuilder().indentionStep(2).encodingMode(DYNAMIC_MODE).build();
        TestCase.assertEquals("{}", JsonStream.serialize(config, new HashMap<String, String>()));
    }

    public void test_int_as_map_key() {
        HashMap<Integer, String> m = new HashMap<Integer, String>();
        m.put(1, "2");
        TestCase.assertEquals("{\"1\":\"2\"}", JsonStream.serialize(new com.jsoniter.spi.TypeLiteral<Map<Integer, String>>() {}, m));
    }

    public void test_object_key() {
        HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
        m.put(1, 2);
        TestCase.assertEquals("{\"1\":2}", JsonStream.serialize(m));
    }

    public void test_multiple_keys() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("destination", "test_destination_value");
        map.put("amount", new BigDecimal("0.0000101101"));
        map.put("password", "test_pass");
        final String serialized = JsonStream.serialize(map);
        TestCase.assertEquals((-1), serialized.indexOf("::"));
    }
}

