/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.nio_char.tests.java.nio.charset;


import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderMalfunctionError;
import java.nio.charset.CoderResult;
import junit.framework.TestCase;


public class CharsetEncoderTest extends TestCase {
    /**
     *
     *
     * @unknown java.nio.charset.CharsetEncoder.CharsetEncoder(
    java.nio.charset.Charset, float, float)
     */
    public void test_ConstructorLjava_nio_charset_CharsetFF() {
        // Regression for HARMONY-141
        try {
            Charset cs = Charset.forName("UTF-8");// $NON-NLS-1$

            new CharsetEncoderTest.MockCharsetEncoderForHarmony141(cs, 1.1F, 1);
            TestCase.fail("Assert 0: Should throw IllegalArgumentException.");// $NON-NLS-1$

        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            Charset cs = Charset.forName("ISO8859-1");// $NON-NLS-1$

            new CharsetEncoderTest.MockCharsetEncoderForHarmony141(cs, 1.1F, 1, new byte[]{ 26 });
            TestCase.fail("Assert 1: Should throw IllegalArgumentException.");// $NON-NLS-1$

        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     *
     *
     * @unknown java.nio.charset.CharsetEncoder.CharsetEncoder(
    java.nio.charset.Charset, float, float)
     */
    public void test_ConstructorLjava_nio_charset_CharsetNull() {
        // Regression for HARMONY-491
        CharsetEncoder ech = new CharsetEncoderTest.MockCharsetEncoderForHarmony491(null, 1, 1);
        TestCase.assertNull(ech.charset());
    }

    /**
     * Helper for constructor tests
     */
    public static class MockCharsetEncoderForHarmony141 extends CharsetEncoder {
        protected MockCharsetEncoderForHarmony141(Charset cs, float averageBytesPerChar, float maxBytesPerChar) {
            super(cs, averageBytesPerChar, maxBytesPerChar);
        }

        public MockCharsetEncoderForHarmony141(Charset cs, float averageBytesPerChar, float maxBytesPerChar, byte[] replacement) {
            super(cs, averageBytesPerChar, maxBytesPerChar, replacement);
        }

        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            return null;
        }
    }

    public static class MockCharsetEncoderForHarmony491 extends CharsetEncoder {
        public MockCharsetEncoderForHarmony491(Charset arg0, float arg1, float arg2) {
            super(arg0, arg1, arg2);
        }

        protected CoderResult encodeLoop(CharBuffer arg0, ByteBuffer arg1) {
            return null;
        }

        public boolean isLegalReplacement(byte[] arg0) {
            return true;
        }
    }

    /* Test malfunction encode(CharBuffer) */
    public void test_EncodeLjava_nio_CharBuffer() throws Exception {
        CharsetEncoderTest.MockMalfunctionCharset cs = new CharsetEncoderTest.MockMalfunctionCharset("mock", null);
        try {
            cs.encode(CharBuffer.wrap("AB"));
            TestCase.fail("should throw CoderMalfunctionError");// NON-NLS-1$

        } catch (CoderMalfunctionError e) {
            // expected
        }
    }

    /* Mock charset class with malfunction decode & encode. */
    static final class MockMalfunctionCharset extends Charset {
        public MockMalfunctionCharset(String canonicalName, String[] aliases) {
            super(canonicalName, aliases);
        }

        public boolean contains(Charset cs) {
            return false;
        }

        public CharsetDecoder newDecoder() {
            return Charset.forName("UTF-8").newDecoder();
        }

        public CharsetEncoder newEncoder() {
            return new CharsetEncoderTest.MockMalfunctionEncoder(this);
        }
    }

    /* Mock encoder. encodeLoop always throws unexpected exception. */
    static class MockMalfunctionEncoder extends CharsetEncoder {
        public MockMalfunctionEncoder(Charset cs) {
            super(cs, 1, 3, new byte[]{ ((byte) ('?')) });
        }

        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            throw new BufferOverflowException();
        }
    }

    /* Test reserve bytes encode(CharBuffer,ByteBuffer,boolean) */
    public void test_EncodeLjava_nio_CharBufferLjava_nio_ByteBufferB() throws Exception {
        Charset utf8 = Charset.forName("utf-8");
        CharsetEncoder encoder = utf8.newEncoder();
        CharBuffer char1 = CharBuffer.wrap("\ud800");
        CharBuffer char2 = CharBuffer.wrap("\udc00");
        ByteBuffer bytes = ByteBuffer.allocate(4);
        encoder.reset();
        // If we supply just the high surrogate...
        CoderResult result = encoder.encode(char1, bytes, false);
        // ...we're not done...
        TestCase.assertTrue(result.isUnderflow());
        TestCase.assertEquals(4, bytes.remaining());
        // ...but if we then supply the low surrogate...
        result = encoder.encode(char2, bytes, true);
        TestCase.assertTrue(result.isUnderflow());
        // ...we're done. Note that the RI loses its state in
        // between the two characters, so it can't do this.
        TestCase.assertEquals(0, bytes.remaining());
        // Did we get the UTF-8 for U+10000?
        TestCase.assertEquals(4, bytes.limit());
        TestCase.assertEquals(((byte) (240)), bytes.get(0));
        TestCase.assertEquals(((byte) (144)), bytes.get(1));
        TestCase.assertEquals(((byte) (128)), bytes.get(2));
        TestCase.assertEquals(((byte) (128)), bytes.get(3));
        // See what we got in the output buffer by decoding and checking that we
        // get back the same surrogate pair.
        bytes.flip();
        CharBuffer chars = utf8.newDecoder().decode(bytes);
        TestCase.assertEquals(0, bytes.remaining());
        TestCase.assertEquals(2, chars.limit());
        TestCase.assertEquals(55296, chars.get(0));
        TestCase.assertEquals(56320, chars.get(1));
    }

    /**
     *
     *
     * @unknown {@link java.nio.charset.Charset#encode(java.nio.CharBuffer)
     */
    public void testUtf8Encoding() throws IOException {
        byte[] orig = new byte[]{ ((byte) (237)), ((byte) (160)), ((byte) (128)) };
        String s = new String(orig, "UTF-8");
        TestCase.assertEquals(1, s.length());
        TestCase.assertEquals(55296, s.charAt(0));
        Charset.forName("UTF-8").encode(CharBuffer.wrap(s));
        // ByteBuffer buf = <result>
        // for (byte o : orig) {
        // byte b = 0;
        // buf.get(b);
        // assertEquals(o, b);
        // }
    }
}

