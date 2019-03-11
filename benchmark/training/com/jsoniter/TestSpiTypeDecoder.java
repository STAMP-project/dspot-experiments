package com.jsoniter;


import com.jsoniter.spi.Decoder;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;


public class TestSpiTypeDecoder extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    public static class TestObject1 {
        public int field1;
    }

    public void test_TypeDecoder() throws IOException {
        JsoniterSpi.registerTypeDecoder(TestSpiTypeDecoder.TestObject1.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                iter.skip();
                TestSpiTypeDecoder.TestObject1 obj = new TestSpiTypeDecoder.TestObject1();
                obj.field1 = 101;
                return obj;
            }
        });
        TestSpiTypeDecoder.TestObject1 obj = JsonIterator.deserialize("{'field1': 100}".replace('\'', '"'), TestSpiTypeDecoder.TestObject1.class);
        TestCase.assertEquals(101, obj.field1);
    }

    public void test_TypeDecoder_for_generics() throws IOException {
        TypeLiteral<List<TestSpiTypeDecoder.TestObject1>> typeLiteral = new TypeLiteral<List<TestSpiTypeDecoder.TestObject1>>() {};
        JsoniterSpi.registerTypeDecoder(typeLiteral, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                iter.skip();
                TestSpiTypeDecoder.TestObject1 obj = new TestSpiTypeDecoder.TestObject1();
                obj.field1 = 101;
                return Arrays.asList(obj);
            }
        });
        List<TestSpiTypeDecoder.TestObject1> objs = JsonIterator.deserialize("{'field1': 100}".replace('\'', '"'), typeLiteral);
        TestCase.assertEquals(101, objs.get(0).field1);
    }

    public static class MyDate {
        Date date;
    }

    static {
        JsoniterSpi.registerTypeDecoder(TestSpiTypeDecoder.MyDate.class, new Decoder() {
            @Override
            public Object decode(final JsonIterator iter) throws IOException {
                return new TestSpiTypeDecoder.MyDate() {
                    {
                        date = new Date(iter.readLong());
                    }
                };
            }
        });
    }

    public void test_direct() throws IOException {
        JsonIterator iter = JsonIterator.parse("1481365190000");
        TestSpiTypeDecoder.MyDate date = iter.read(TestSpiTypeDecoder.MyDate.class);
        TestCase.assertEquals(1481365190000L, date.date.getTime());
    }

    public static class FieldWithMyDate {
        public TestSpiTypeDecoder.MyDate field;
    }

    public void test_as_field_type() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field': 1481365190000}".replace('\'', '"'));
        TestSpiTypeDecoder.FieldWithMyDate obj = iter.read(TestSpiTypeDecoder.FieldWithMyDate.class);
        TestCase.assertEquals(1481365190000L, obj.field.date.getTime());
    }

    public void test_as_array_element() throws IOException {
        JsonIterator iter = JsonIterator.parse("[1481365190000]");
        TestSpiTypeDecoder.MyDate[] dates = iter.read(TestSpiTypeDecoder.MyDate[].class);
        TestCase.assertEquals(1481365190000L, dates[0].date.getTime());
    }

    public static class MyList {
        public List<String> list;
    }

    public void test_list_or_single_element() {
        final TypeLiteral<List<String>> listOfString = new TypeLiteral<List<String>>() {};
        JsoniterSpi.registerTypeDecoder(TestSpiTypeDecoder.MyList.class, new Decoder() {
            @Override
            public Object decode(JsonIterator iter) throws IOException {
                ValueType valueType = iter.whatIsNext();
                TestSpiTypeDecoder.MyList myList = new TestSpiTypeDecoder.MyList();
                switch (valueType) {
                    case ARRAY :
                        myList.list = iter.read(listOfString);
                        return myList;
                    case STRING :
                        myList.list = new ArrayList<String>();
                        myList.list.add(iter.readString());
                        return myList;
                    default :
                        throw new JsonException("unexpected input");
                }
            }
        });
        TestSpiTypeDecoder.MyList list1 = JsonIterator.deserialize("\"hello\"", TestSpiTypeDecoder.MyList.class);
        TestCase.assertEquals("hello", list1.list.get(0));
        TestSpiTypeDecoder.MyList list2 = JsonIterator.deserialize("[\"hello\",\"world\"]", TestSpiTypeDecoder.MyList.class);
        TestCase.assertEquals("hello", list2.list.get(0));
        TestCase.assertEquals("world", list2.list.get(1));
    }
}

