package com.jsoniter;


import com.jsoniter.extra.GsonCompatibilityMode;
import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsoniterSpi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class TestMap extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    public void test_object_key() throws IOException {
        Map<Object, Object> map = JsonIterator.deserialize("{\"\u4e2d\u6587\":null}", new com.jsoniter.spi.TypeLiteral<Map<Object, Object>>() {});
        TestCase.assertEquals(new HashMap<Object, Object>() {
            {
                put("??", null);
            }
        }, map);
    }

    public void test_string_key() throws IOException {
        Map<String, Object> map = JsonIterator.deserialize("{\"\u4e2d\u6587\":null}", new com.jsoniter.spi.TypeLiteral<Map<String, Object>>() {});
        TestCase.assertEquals(new HashMap<String, Object>() {
            {
                put("??", null);
            }
        }, map);
    }

    public void test_integer_key() throws IOException {
        Map<Integer, Object> map = JsonIterator.deserialize("{\"100\":null}", new com.jsoniter.spi.TypeLiteral<Map<Integer, Object>>() {});
        TestCase.assertEquals(new HashMap<Integer, Object>() {
            {
                put(100, null);
            }
        }, map);
    }

    public static enum EnumKey {

        KeyA,
        KeyB;}

    public void test_enum_key() {
        Map<TestMap.EnumKey, Object> map = JsonIterator.deserialize("{\"KeyA\":null}", new com.jsoniter.spi.TypeLiteral<Map<TestMap.EnumKey, Object>>() {});
        TestCase.assertEquals(new HashMap<TestMap.EnumKey, Object>() {
            {
                put(TestMap.EnumKey.KeyA, null);
            }
        }, map);
    }

    public static class TestObject1 {
        public int Field;
    }

    public void test_MapKeyCodec() {
        JsoniterSpi.registerMapKeyDecoder(TestMap.TestObject1.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                TestMap.TestObject1 obj = new TestMap.TestObject1();
                obj.Field = Integer.valueOf(iter.readString());
                return obj;
            }
        });
        Map<TestMap.TestObject1, Object> map = JsonIterator.deserialize("{\"100\":null}", new com.jsoniter.spi.TypeLiteral<Map<TestMap.TestObject1, Object>>() {});
        ArrayList<TestMap.TestObject1> keys = new ArrayList<TestMap.TestObject1>(map.keySet());
        TestCase.assertEquals(1, keys.size());
        TestCase.assertEquals(100, keys.get(0).Field);
        // in new config
        map = JsonIterator.deserialize(new GsonCompatibilityMode.Builder().build(), "{\"100\":null}", new com.jsoniter.spi.TypeLiteral<Map<TestMap.TestObject1, Object>>() {});
        keys = new ArrayList<TestMap.TestObject1>(map.keySet());
        TestCase.assertEquals(1, keys.size());
        TestCase.assertEquals(100, keys.get(0).Field);
    }
}

