package com.jsoniter.output;


import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import java.io.IOException;
import junit.framework.TestCase;


public class TestSpiPropertyEncoder extends TestCase {
    public static class TestObject1<A> {
        public String field1;
    }

    public void test_PropertyEncoder() throws IOException {
        JsoniterSpi.registerPropertyEncoder(TestSpiPropertyEncoder.TestObject1.class, "field1", new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                String str = ((String) (obj));
                stream.writeVal(Integer.valueOf(str));
            }
        });
        TestSpiPropertyEncoder.TestObject1 obj = new TestSpiPropertyEncoder.TestObject1();
        obj.field1 = "100";
        String output = JsonStream.serialize(obj);
        TestCase.assertEquals("{'field1':100}".replace('\'', '"'), output);
    }

    public void test_PropertyEncoder_for_type_literal() throws IOException {
        TypeLiteral<TestSpiPropertyEncoder.TestObject1<Object>> typeLiteral = new TypeLiteral<TestSpiPropertyEncoder.TestObject1<Object>>() {};
        JsoniterSpi.registerPropertyEncoder(typeLiteral, "field1", new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                String str = ((String) (obj));
                stream.writeVal(((Integer.valueOf(str)) + 1));
            }
        });
        TestSpiPropertyEncoder.TestObject1 obj = new TestSpiPropertyEncoder.TestObject1();
        obj.field1 = "100";
        String output = JsonStream.serialize(typeLiteral, obj);
        TestCase.assertEquals("{'field1':101}".replace('\'', '"'), output);
    }
}

