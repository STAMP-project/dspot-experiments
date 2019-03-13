package com.jsoniter.output;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import junit.framework.TestCase;


public class TestNative extends TestCase {
    static {
        // JsonStream.setMode(EncodingMode.REFLECTION_MODE);
    }

    private ByteArrayOutputStream baos;

    private JsonStream stream;

    public void test_string() throws IOException {
        stream = new JsonStream(baos, 32);
        stream.writeVal("1234567890123456789012345678901234567890");
        stream.close();
        TestCase.assertEquals("'1234567890123456789012345678901234567890'".replace('\'', '"'), baos.toString());
    }

    public void test_slash() throws IOException {
        stream.writeVal("/\\");
        stream.close();
        TestCase.assertEquals("\"/\\\\\"", baos.toString());
    }

    public void test_escape() throws IOException {
        stream.writeVal("hel\nlo");
        stream.close();
        TestCase.assertEquals("\'hel\\nlo\'".replace('\'', '"'), baos.toString());
    }

    public void test_utf8() throws IOException {
        stream.writeVal("??");
        stream.close();
        TestCase.assertEquals("\"\\u4e2d\\u6587\"", baos.toString());
    }

    public void test_int() throws IOException {
        stream.writeVal(100);
        stream.close();
        TestCase.assertEquals("100", baos.toString());
    }

    public void test_boxed_int() throws IOException {
        Object val = Integer.valueOf(100);
        stream.writeVal(val);
        stream.close();
        TestCase.assertEquals("100", baos.toString());
    }

    public void test_negative_int() throws IOException {
        stream.writeVal((-100));
        stream.close();
        TestCase.assertEquals("-100", baos.toString());
    }

    public void test_small_int() throws IOException {
        stream.writeVal(3);
        stream.close();
        TestCase.assertEquals("3", baos.toString());
    }

    public void test_large_int() throws IOException {
        stream.writeVal(31415926);
        stream.close();
        TestCase.assertEquals("31415926", baos.toString());
    }

    public void test_long() throws IOException {
        stream.writeVal(100L);
        stream.close();
        TestCase.assertEquals("100", baos.toString());
    }

    public void test_negative_long() throws IOException {
        stream.writeVal((-100L));
        stream.close();
        TestCase.assertEquals("-100", baos.toString());
    }

    public void test_short() throws IOException {
        stream.writeVal(((short) (555)));
        stream.close();
        TestCase.assertEquals("555", baos.toString());
        TestCase.assertEquals("555", JsonStream.serialize(new Short(((short) (555)))));
    }

    public void test_no_decimal_float() throws IOException {
        stream.writeVal(100.0F);
        stream.close();
        TestCase.assertEquals("100", baos.toString());
    }

    public void test_float2() throws IOException {
        stream.writeVal(1.0E-6F);
        stream.close();
        TestCase.assertEquals("0.000001", baos.toString());
    }

    public void test_float3() throws IOException {
        stream.writeVal(1.0E-5F);
        stream.close();
        TestCase.assertEquals("0.00001", baos.toString());
    }

    public void test_big_float() throws IOException {
        stream.writeVal(((float) (83886079)));
        stream.close();
        TestCase.assertEquals("83886080", baos.toString());
    }

    public void test_double() throws IOException {
        stream.writeVal(1.001);
        stream.close();
        TestCase.assertEquals("1.001", baos.toString());
    }

    public void test_large_double() throws IOException {
        stream.writeVal(Double.MAX_VALUE);
        stream.close();
        TestCase.assertEquals("1.7976931348623157E308", baos.toString());
    }

    public void test_boolean() throws IOException {
        stream.writeVal(true);
        stream.writeVal(false);
        stream.close();
        TestCase.assertEquals("truefalse".replace('\'', '"'), baos.toString());
    }

    public void test_big_decimal() throws IOException {
        stream.writeVal(new BigDecimal("12.34"));
        stream.close();
        TestCase.assertEquals("12.34".replace('\'', '"'), baos.toString());
    }

    public void test_big_integer() throws IOException {
        stream.writeVal(new BigInteger("1234"));
        stream.close();
        TestCase.assertEquals("1234".replace('\'', '"'), baos.toString());
    }

    public void test_raw() throws IOException {
        stream = new JsonStream(baos, 32);
        String val = "1234567890123456789012345678901234567890";
        stream.writeRaw(val, val.length());
        stream.close();
        TestCase.assertEquals(val.replace('\'', '"'), baos.toString());
    }
}

