package com.jsoniter;


import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import java.io.IOException;
import junit.framework.TestCase;


public class TestSpiPropertyDecoder extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    public static class TestObject1<A> {
        public String field;
    }

    public void test_PropertyDecoder() {
        JsoniterSpi.registerPropertyDecoder(TestSpiPropertyDecoder.TestObject1.class, "field", new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                iter.skip();
                return "hello";
            }
        });
        TestSpiPropertyDecoder.TestObject1 obj = JsonIterator.deserialize("{\"field\":100}", TestSpiPropertyDecoder.TestObject1.class);
        TestCase.assertEquals("hello", obj.field);
    }

    public void test_PropertyDecoder_for_type_literal() {
        TypeLiteral<TestSpiPropertyDecoder.TestObject1<Object>> typeLiteral = new TypeLiteral<TestSpiPropertyDecoder.TestObject1<Object>>() {};
        JsoniterSpi.registerPropertyDecoder(typeLiteral, "field", new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                iter.skip();
                return "world";
            }
        });
        TestSpiPropertyDecoder.TestObject1 obj = JsonIterator.deserialize("{\"field\":100}", typeLiteral);
        TestCase.assertEquals("world", obj.field);
    }
}

