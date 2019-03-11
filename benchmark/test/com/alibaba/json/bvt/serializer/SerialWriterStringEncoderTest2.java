package com.alibaba.json.bvt.serializer;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerialWriterStringEncoderTest2 extends TestCase {
    public void test_error_0() throws Exception {
        Charset charset = Charset.forName("UTF-8");
        CharsetEncoder charsetEncoder = new SerialWriterStringEncoderTest2.MockCharsetEncoder2(charset);
        Exception error = null;
        char[] chars = "abc".toCharArray();
        try {
            SerialWriterStringEncoderTest2.encode(charsetEncoder, chars, 0, chars.length);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Charset charset = Charset.forName("UTF-8");
        CharsetEncoder realEncoder = charset.newEncoder();
        CharsetEncoder charsetEncoder = new SerialWriterStringEncoderTest2.MockCharsetEncoder(charset, realEncoder);
        Exception error = null;
        char[] chars = "abc".toCharArray();
        try {
            SerialWriterStringEncoderTest2.encode(charsetEncoder, chars, 0, chars.length);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class MockCharsetEncoder extends CharsetEncoder {
        private CharsetEncoder raw;

        protected MockCharsetEncoder(Charset cs, CharsetEncoder raw) {
            super(cs, raw.averageBytesPerChar(), raw.maxBytesPerChar());
            this.raw = raw;
        }

        @Override
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            return raw.encode(in, out, false);
        }

        protected CoderResult implFlush(ByteBuffer out) {
            return CoderResult.malformedForLength(1);
        }
    }

    public static class MockCharsetEncoder2 extends CharsetEncoder {
        protected MockCharsetEncoder2(Charset cs) {
            super(cs, 2, 2);
        }

        @Override
        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            return CoderResult.OVERFLOW;
        }
    }
}

