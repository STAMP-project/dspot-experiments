package com.jsoniter.output;


import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import com.jsoniter.spi.TypeLiteral;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;


public class TestSpiTypeEncoder extends TestCase {
    static {
        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    public static class MyDate {
        Date date;
    }

    public void test_TypeEncoder() throws IOException {
        JsoniterSpi.registerTypeEncoder(TestSpiTypeEncoder.MyDate.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                TestSpiTypeEncoder.MyDate date = ((TestSpiTypeEncoder.MyDate) (obj));
                stream.writeVal(date.date.getTime());
            }
        });
        System.out.println(JsoniterSpi.getCurrentConfig().configName());
        TestSpiTypeEncoder.MyDate myDate = new TestSpiTypeEncoder.MyDate();
        myDate.date = new Date(1481365190000L);
        String output = JsonStream.serialize(myDate);
        TestCase.assertEquals("1481365190000", output);
    }

    public void test_TypeEncoder_for_type_literal() {
        TypeLiteral<List<TestSpiTypeEncoder.MyDate>> typeLiteral = new TypeLiteral<List<TestSpiTypeEncoder.MyDate>>() {};
        JsoniterSpi.registerTypeEncoder(typeLiteral, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                List<TestSpiTypeEncoder.MyDate> dates = ((List<TestSpiTypeEncoder.MyDate>) (obj));
                stream.writeVal(dates.get(0).date.getTime());
            }
        });
        TestSpiTypeEncoder.MyDate myDate = new TestSpiTypeEncoder.MyDate();
        myDate.date = new Date(1481365190000L);
        String output = JsonStream.serialize(typeLiteral, Collections.singletonList(myDate));
        TestCase.assertEquals("1481365190000", output);
    }
}

