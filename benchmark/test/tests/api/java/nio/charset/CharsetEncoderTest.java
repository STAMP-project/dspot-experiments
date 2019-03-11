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
package tests.api.java.nio.charset;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import junit.framework.TestCase;


/**
 * API unit test for java.nio.charset.CharsetEncoder
 */
public class CharsetEncoderTest extends TestCase {
    static final int MAX_BYTES = 3;

    static final float AVER_BYTES = 0.5F;

    // charset for mock class
    private static final Charset MOCKCS = new CharsetEncoderTest.MockCharset("CharsetEncoderTest_mock", new String[0]);

    Charset cs = CharsetEncoderTest.MOCKCS;

    // default encoder
    CharsetEncoder encoder;

    // default for Charset abstract class
    byte[] defaultReplacement = new byte[]{ 63 };

    // specific for Charset implementation subclass
    byte[] specifiedReplacement = new byte[]{ 63 };

    static final String unistr = " buffer";// \u8000\u8001\u00a5\u3000\r\n";


    byte[] unibytes = new byte[]{ 32, 98, 117, 102, 102, 101, 114 };

    byte[] unibytesWithRep = null;

    byte[] surrogate = new byte[0];

    public void testSpecificDefaultValue() {
        TestCase.assertTrue(((encoder.averageBytesPerChar()) == (CharsetEncoderTest.AVER_BYTES)));
        TestCase.assertTrue(((encoder.maxBytesPerChar()) == (CharsetEncoderTest.MAX_BYTES)));
    }

    public void testDefaultValue() {
        TestCase.assertEquals(CodingErrorAction.REPORT, encoder.malformedInputAction());
        TestCase.assertEquals(CodingErrorAction.REPORT, encoder.unmappableCharacterAction());
        TestCase.assertSame(encoder, encoder.onMalformedInput(CodingErrorAction.IGNORE));
        TestCase.assertSame(encoder, encoder.onUnmappableCharacter(CodingErrorAction.IGNORE));
        if ((encoder) instanceof CharsetEncoderTest.MockCharsetEncoder) {
            TestCase.assertTrue(Arrays.equals(encoder.replacement(), defaultReplacement));
        } else {
            TestCase.assertTrue(Arrays.equals(encoder.replacement(), specifiedReplacement));
        }
    }

    /* Class under test for constructor CharsetEncoder(Charset, float, float) */
    public void testCharsetEncoderCharsetfloatfloat() {
        // default value
        encoder = new CharsetEncoderTest.MockCharsetEncoder(cs, ((float) (CharsetEncoderTest.AVER_BYTES)), CharsetEncoderTest.MAX_BYTES);
        TestCase.assertSame(encoder.charset(), cs);
        TestCase.assertTrue(((encoder.averageBytesPerChar()) == (CharsetEncoderTest.AVER_BYTES)));
        TestCase.assertTrue(((encoder.maxBytesPerChar()) == (CharsetEncoderTest.MAX_BYTES)));
        TestCase.assertEquals(CodingErrorAction.REPORT, encoder.malformedInputAction());
        TestCase.assertEquals(CodingErrorAction.REPORT, encoder.unmappableCharacterAction());
        TestCase.assertEquals(new String(encoder.replacement()), new String(defaultReplacement));
        TestCase.assertSame(encoder, encoder.onMalformedInput(CodingErrorAction.IGNORE));
        TestCase.assertSame(encoder, encoder.onUnmappableCharacter(CodingErrorAction.IGNORE));
        // normal case
        CharsetEncoder ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 1, CharsetEncoderTest.MAX_BYTES);
        TestCase.assertSame(ec.charset(), cs);
        TestCase.assertEquals(1.0, ec.averageBytesPerChar(), 0);
        TestCase.assertTrue(((ec.maxBytesPerChar()) == (CharsetEncoderTest.MAX_BYTES)));
        /* ------------------------ Exceptional cases ------------------------- */
        // NullPointerException: null charset
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(null, 1, CharsetEncoderTest.MAX_BYTES);
            TestCase.fail("should throw null pointer exception");
        } catch (NullPointerException e) {
        }
        ec = new CharsetEncoderTest.MockCharsetEncoder(new CharsetEncoderTest.MockCharset("mock", new String[0]), 1, CharsetEncoderTest.MAX_BYTES);
        // Commented out since the comment is wrong since MAX_BYTES > 1
        // // OK: average length less than max length
        // ec = new MockCharsetEncoder(cs, MAX_BYTES, 1);
        // assertTrue(ec.averageBytesPerChar() == MAX_BYTES);
        // assertTrue(ec.maxBytesPerChar() == 1);
        // Illegal Argument: zero length
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 0, CharsetEncoderTest.MAX_BYTES);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 1, 0);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        // Illegal Argument: negative length
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, (-1), CharsetEncoderTest.MAX_BYTES);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 1, (-1));
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    /* Class under test for constructor CharsetEncoder(Charset, float, float,
    byte[])
     */
    public void testCharsetEncoderCharsetfloatfloatbyteArray() {
        byte[] ba = getLegalByteArray();
        // normal case
        CharsetEncoder ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 1, CharsetEncoderTest.MAX_BYTES, ba);
        TestCase.assertSame(ec.charset(), cs);
        TestCase.assertEquals(1.0, ec.averageBytesPerChar(), 0.0);
        TestCase.assertTrue(((ec.maxBytesPerChar()) == (CharsetEncoderTest.MAX_BYTES)));
        TestCase.assertSame(ba, ec.replacement());
        /* ------------------------ Exceptional cases ------------------------- */
        // NullPointerException: null charset
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(null, 1, CharsetEncoderTest.MAX_BYTES, ba);
            TestCase.fail("should throw null pointer exception");
        } catch (NullPointerException e) {
        }
        // Illegal Argument: null byte array
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 1, CharsetEncoderTest.MAX_BYTES, null);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        // Illegal Argument: empty byte array
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 1, CharsetEncoderTest.MAX_BYTES, new byte[0]);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        // Illegal Argument: byte array is longer than max length
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 1, CharsetEncoderTest.MAX_BYTES, new byte[]{ 1, 2, CharsetEncoderTest.MAX_BYTES, 4 });
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        // Commented out since the comment is wrong since MAX_BYTES > 1
        // This test throws IllegalArgumentException on Harmony and RI
        // // OK: average length less than max length
        // ec = new MockCharsetEncoder(cs, MAX_BYTES, ba.length, ba);
        // assertTrue(ec.averageBytesPerChar() == MAX_BYTES);
        // assertTrue(ec.maxBytesPerChar() == ba.length);
        // Illegal Argument: zero length
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 0, CharsetEncoderTest.MAX_BYTES, ba);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 1, 0, ba);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        // Illegal Argument: negative length
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, (-1), CharsetEncoderTest.MAX_BYTES, ba);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            ec = new CharsetEncoderTest.MockCharsetEncoder(cs, 1, (-1), ba);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    /* Class under test for boolean canEncode(char) */
    public void testCanEncodechar() throws CharacterCodingException {
        // for non-mapped char
        TestCase.assertTrue(encoder.canEncode('\uc2c0'));
        // surrogate char for unicode
        // 1st byte: d800-dbff
        // 2nd byte: dc00-dfff
        TestCase.assertTrue(encoder.canEncode('\ud800'));
        // valid surrogate pair
        TestCase.assertTrue(encoder.canEncode('\udc00'));
    }

    /* -----------------------------------------
    Class under test for illegal state case
    methods which can change internal states are two encode, flush, two canEncode, reset
    -----------------------------------------
     */
    // Normal case: just after reset, and it also means reset can be done
    // anywhere
    public void testResetIllegalState() throws CharacterCodingException {
        TestCase.assertSame(encoder, encoder.reset());
        encoder.canEncode('\ud901');
        TestCase.assertSame(encoder, encoder.reset());
        encoder.canEncode("\ud901\udc00");
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("aaa"));
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("aaa"), ByteBuffer.allocate(3), false);
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("aaa"), ByteBuffer.allocate(3), true);
        TestCase.assertSame(encoder, encoder.reset());
    }

    public void testFlushIllegalState() throws CharacterCodingException {
        CharBuffer in = CharBuffer.wrap("aaa");
        ByteBuffer out = ByteBuffer.allocate(5);
        // Illegal state: after reset.
        encoder.reset();
        try {
            encoder.flush(out);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        // Normal case: after encode with endOfInput is true
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(in, out, true);
        out.rewind();
        CoderResult result = encoder.flush(out);
        // Good state: flush twice
        encoder.flush(out);
        // Illegal state: flush after encode with endOfInput is false
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(in, out, false);
        try {
            encoder.flush(out);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    public void testFlushAfterConstructing() {
        ByteBuffer out = ByteBuffer.allocate(5);
        // Illegal state: flush after instance created
        try {
            encoder.flush(out);
            TestCase.fail("should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // Expected
        }
    }

    // test illegal states for encode facade
    public void testEncodeFacadeIllegalState() throws CharacterCodingException {
        // encode facade can be execute in anywhere
        CharBuffer in = CharBuffer.wrap("aaa");
        // Normal case: just created
        encoder.encode(in);
        in.rewind();
        // Normal case: just after encode facade
        encoder.encode(in);
        in.rewind();
        // Normal case: just after canEncode
        TestCase.assertSame(encoder, encoder.reset());
        encoder.canEncode("\ud902\udc00");
        encoder.encode(in);
        in.rewind();
        TestCase.assertSame(encoder, encoder.reset());
        encoder.canEncode('\ud902');
        encoder.encode(in);
        in.rewind();
        // Normal case: just after encode with that endOfInput is true
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState2"), ByteBuffer.allocate(30), true);
        encoder.encode(in);
        in.rewind();
        // Normal case:just after encode with that endOfInput is false
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState3"), ByteBuffer.allocate(30), false);
        encoder.encode(in);
        in.rewind();
        // Normal case: just after flush
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState4"), ByteBuffer.allocate(30), true);
        encoder.flush(ByteBuffer.allocate(10));
        encoder.encode(in);
        in.rewind();
    }

    // test illegal states for two encode method with endOfInput is true
    public void testEncodeTrueIllegalState() throws CharacterCodingException {
        CharBuffer in = CharBuffer.wrap("aaa");
        ByteBuffer out = ByteBuffer.allocate(5);
        // Normal case: just created
        encoder.encode(in, out, true);
        in.rewind();
        out.rewind();
        in.rewind();
        out.rewind();
        // Normal case: just after encode with that endOfInput is true
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState2"), ByteBuffer.allocate(30), true);
        encoder.encode(in, out, true);
        in.rewind();
        out.rewind();
        // Normal case:just after encode with that endOfInput is false
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState3"), ByteBuffer.allocate(30), false);
        encoder.encode(in, out, true);
        in.rewind();
        out.rewind();
        // Illegal state: just after flush
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState4"), ByteBuffer.allocate(30), true);
        encoder.flush(ByteBuffer.allocate(10));
        try {
            encoder.encode(in, out, true);
            TestCase.fail("should illegal state");
        } catch (IllegalStateException e) {
        }
        // Normal case: after canEncode
        TestCase.assertSame(encoder, encoder.reset());
        encoder.canEncode("\ud906\udc00");
        encoder.encode(in, out, true);
        in.rewind();
        out.rewind();
        TestCase.assertSame(encoder, encoder.reset());
        encoder.canEncode('\ud905');
        encoder.encode(in, out, true);
    }

    // test illegal states for two encode method with endOfInput is false
    public void testEncodeFalseIllegalState() throws CharacterCodingException {
        CharBuffer in = CharBuffer.wrap("aaa");
        ByteBuffer out = ByteBuffer.allocate(5);
        // Normal case: just created
        encoder.encode(in, out, false);
        in.rewind();
        out.rewind();
        // Illegal state: just after encode facade
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState1"));
        try {
            encoder.encode(in, out, false);
            TestCase.fail("should illegal state");
        } catch (IllegalStateException e) {
        }
        // Illegal state: just after encode with that endOfInput is true
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState2"), ByteBuffer.allocate(30), true);
        try {
            encoder.encode(in, out, false);
            TestCase.fail("should illegal state");
        } catch (IllegalStateException e) {
        }
        // Normal case:just after encode with that endOfInput is false
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState3"), ByteBuffer.allocate(30), false);
        encoder.encode(in, out, false);
        in.rewind();
        out.rewind();
        // Illegal state: just after flush
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState4"), ByteBuffer.allocate(30), true);
        encoder.flush(ByteBuffer.allocate(10));
        try {
            encoder.encode(in, out, false);
            TestCase.fail("should illegal state");
        } catch (IllegalStateException e) {
        }
        // Normal case: after canEncode
        TestCase.assertSame(encoder, encoder.reset());
        encoder.canEncode("\ud906\udc00");
        encoder.encode(in, out, false);
        in.rewind();
        out.rewind();
        TestCase.assertSame(encoder, encoder.reset());
        encoder.canEncode('\ud905');
        encoder.encode(in, out, false);
    }

    // test illegal states for two canEncode methods
    public void testCanEncodeIllegalState() throws CharacterCodingException {
        // Normal case: just created
        encoder.canEncode("\ud900\udc00");
        encoder.canEncode('\ud900');
        // Illegal state: just after encode with that endOfInput is true
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState2"), ByteBuffer.allocate(30), true);
        try {
            encoder.canEncode("\ud903\udc00");
            TestCase.fail("should throw illegal state exception");
        } catch (IllegalStateException e) {
        }
        // Illegal state:just after encode with that endOfInput is false
        TestCase.assertSame(encoder, encoder.reset());
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState3"), ByteBuffer.allocate(30), false);
        try {
            encoder.canEncode("\ud904\udc00");
            TestCase.fail("should throw illegal state exception");
        } catch (IllegalStateException e) {
        }
        // Normal case: just after flush
        encoder.encode(CharBuffer.wrap("testCanEncodeIllegalState4"), ByteBuffer.allocate(30), true);
        encoder.flush(ByteBuffer.allocate(10));
        encoder.canEncode("\ud905\udc00");
        encoder.canEncode('\ud906');
        // Normal case: after reset again
        TestCase.assertSame(encoder, encoder.reset());
        encoder.canEncode("\ud906\udc00");
        encoder.canEncode('\ud905');
    }

    /* --------------------------------- illegal state test end
    ---------------------------------
     */
    /* Class under test for boolean canEncode(CharSequence) */
    public void testCanEncodeCharSequence() {
        // for non-mapped char
        TestCase.assertTrue(encoder.canEncode("\uc2c0"));
        // surrogate char for unicode
        // 1st byte: d800-dbff
        // 2nd byte: dc00-dfff
        // valid surrogate pair
        TestCase.assertTrue(encoder.canEncode("\ud800\udc00"));
        // invalid surrogate pair
        TestCase.assertTrue(encoder.canEncode("\ud800\udb00"));
    }

    public void test_canEncode_char_ICUBug() {
        // The RI doesn't allow this, but icu4c does.
        TestCase.assertTrue(encoder.canEncode('\ud800'));
    }

    public void test_canEncode_CharSequence_ICUBug() {
        // The RI doesn't allow this, but icu4c does.
        TestCase.assertTrue(encoder.canEncode("\ud800"));
    }

    public void test_canEncode_empty() throws Exception {
        TestCase.assertTrue(encoder.canEncode(""));
    }

    public void test_canEncode_null() throws Exception {
        try {
            encoder.canEncode(null);
            TestCase.fail();
        } catch (NullPointerException e) {
        }
    }

    /* Class under test for Charset charset() */
    public void testCharset() {
        try {
            encoder = new CharsetEncoderTest.MockCharsetEncoder(Charset.forName("gbk"), 1, CharsetEncoderTest.MAX_BYTES);
            // assertSame(encoder.charset(), Charset.forName("gbk"));
        } catch (UnsupportedCharsetException e) {
            System.err.println("Don't support GBK encoding, ignore current test");
        }
    }

    /* Class under test for ByteBuffer encode(CharBuffer) */
    public void testEncodeCharBuffer() throws CharacterCodingException {
        // Null pointer
        try {
            encoder.encode(null);
            TestCase.fail("should throw null pointer exception");
        } catch (NullPointerException e) {
        }
        // empty input buffer
        ByteBuffer out = encoder.encode(CharBuffer.wrap(""));
        TestCase.assertEquals(out.position(), 0);
        assertByteArray(out, new byte[0]);
        // assertByteArray(out, surrogate);
        // normal case
        out = encoder.encode(CharBuffer.wrap(CharsetEncoderTest.unistr));
        TestCase.assertEquals(out.position(), 0);
        assertByteArray(out, addSurrogate(unibytes));
        // Regression test for harmony-3378
        Charset cs = Charset.forName("UTF-8");
        CharsetEncoder encoder = cs.newEncoder();
        encoder.onMalformedInput(CodingErrorAction.REPLACE);
        encoder = encoder.replaceWith(new byte[]{ ((byte) (239)), ((byte) (191)), ((byte) (189)) });
        CharBuffer in = CharBuffer.wrap("\ud800");
        out = encoder.encode(in);
        TestCase.assertNotNull(out);
    }

    public void testEncodeCharBufferException() throws CharacterCodingException {
        ByteBuffer out;
        CharBuffer in;
        // MalformedException:
        in = getMalformedCharBuffer();
        encoder.onMalformedInput(CodingErrorAction.REPORT);
        encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        if (in != null) {
            try {
                // regression test for Harmony-1379
                encoder.encode(in);
                TestCase.fail("should throw MalformedInputException");
            } catch (MalformedInputException e) {
            }
            encoder.reset();
            in.rewind();
            encoder.onMalformedInput(CodingErrorAction.IGNORE);
            out = encoder.encode(in);
            assertByteArray(out, addSurrogate(unibytes));
            encoder.reset();
            in.rewind();
            encoder.onMalformedInput(CodingErrorAction.REPLACE);
            out = encoder.encode(in);
            assertByteArray(out, addSurrogate(unibytesWithRep));
        }
        // Unmapped Exception:
        in = getUnmapCharBuffer();
        encoder.onMalformedInput(CodingErrorAction.REPORT);
        encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        if (in != null) {
            encoder.reset();
            try {
                encoder.encode(in);
                TestCase.fail("should throw UnmappableCharacterException");
            } catch (UnmappableCharacterException e) {
            }
            encoder.reset();
            in.rewind();
            encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
            out = encoder.encode(in);
            assertByteArray(out, unibytes);
            encoder.reset();
            in.rewind();
            encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            out = encoder.encode(in);
            assertByteArray(out, unibytesWithRep);
        }
        // RuntimeException
        try {
            encoder.encode(getExceptionCharBuffer());
            TestCase.fail("should throw runtime exception");
        } catch (RuntimeException e) {
        }
    }

    /* Class under test for CoderResult encode(CharBuffer, ByteBuffer, boolean) */
    public void testEncodeCharBufferByteBufferboolean() throws CharacterCodingException {
        ByteBuffer out = ByteBuffer.allocate(200);
        CharBuffer in = CharBuffer.wrap(CharsetEncoderTest.unistr);
        // Null pointer
        try {
            encoder.encode(null, out, true);
            TestCase.fail("should throw null pointer exception");
        } catch (NullPointerException e) {
        }
        try {
            encoder.encode(in, null, true);
            TestCase.fail("should throw null pointer exception");
        } catch (NullPointerException e) {
        }
        // normal case, one complete operation
        TestCase.assertSame(encoder, encoder.reset());
        in.rewind();
        out.rewind();
        TestCase.assertSame(CoderResult.UNDERFLOW, encoder.encode(in, out, true));
        TestCase.assertEquals(out.limit(), 200);
        TestCase.assertTrue(((out.position()) > 0));
        TestCase.assertTrue(((out.remaining()) > 0));
        TestCase.assertEquals(out.capacity(), 200);
        assertByteArray(out, addSurrogate(unibytes));
        in.rewind();
        encoder.flush(out);
        // normal case, one complete operation, but call twice, first time set
        // endOfInput to false
        TestCase.assertSame(encoder, encoder.reset());
        in.rewind();
        out = ByteBuffer.allocate(200);
        TestCase.assertSame(CoderResult.UNDERFLOW, encoder.encode(in, out, false));
        TestCase.assertEquals(out.limit(), 200);
        TestCase.assertTrue(((out.position()) > 0));
        TestCase.assertTrue(((out.remaining()) > 0));
        TestCase.assertEquals(out.capacity(), 200);
        assertByteArray(out, addSurrogate(unibytes));
        in.rewind();
        TestCase.assertSame(CoderResult.UNDERFLOW, encoder.encode(in, out, false));
        in.rewind();
        TestCase.assertSame(CoderResult.UNDERFLOW, encoder.encode(in, out, true));
        TestCase.assertEquals(out.limit(), 200);
        TestCase.assertTrue(((out.position()) > 0));
        TestCase.assertTrue(((out.remaining()) > 0));
        TestCase.assertEquals(out.capacity(), 200);
        assertByteArray(out, addSurrogate(duplicateByteArray(unibytes, 3)));
        // overflow
        out = ByteBuffer.allocate(4);
        TestCase.assertSame(encoder, encoder.reset());
        in.rewind();
        out.rewind();
        TestCase.assertSame(CoderResult.OVERFLOW, encoder.encode(in, out, true));
        TestCase.assertEquals(out.limit(), 4);
        TestCase.assertEquals(out.position(), 4);
        TestCase.assertEquals(out.remaining(), 0);
        TestCase.assertEquals(out.capacity(), 4);
        ByteBuffer temp = ByteBuffer.allocate(200);
        out.flip();
        temp.put(out);
        out = temp;
        TestCase.assertSame(CoderResult.UNDERFLOW, encoder.encode(in, out, true));
        TestCase.assertEquals(out.limit(), 200);
        TestCase.assertTrue(((out.position()) > 0));
        TestCase.assertTrue(((out.remaining()) > 0));
        TestCase.assertEquals(out.capacity(), 200);
        assertByteArray(out, addSurrogate(unibytes));
        TestCase.assertSame(encoder, encoder.reset());
        in.rewind();
        out = ByteBuffer.allocate(4);
        TestCase.assertSame(CoderResult.OVERFLOW, encoder.encode(in, out, false));
        TestCase.assertEquals(out.limit(), 4);
        TestCase.assertEquals(out.position(), 4);
        TestCase.assertEquals(out.remaining(), 0);
        TestCase.assertEquals(out.capacity(), 4);
        temp = ByteBuffer.allocate(200);
        out.flip();
        temp.put(out);
        out = temp;
        TestCase.assertSame(CoderResult.UNDERFLOW, encoder.encode(in, out, false));
        TestCase.assertEquals(out.limit(), 200);
        TestCase.assertTrue(((out.position()) > 0));
        TestCase.assertTrue(((out.remaining()) > 0));
        TestCase.assertEquals(out.capacity(), 200);
        assertByteArray(out, addSurrogate(unibytes));
    }

    public void testEncodeCharBufferByteBufferbooleanExceptionFalse() throws CharacterCodingException {
        implTestEncodeCharBufferByteBufferbooleanException(false);
    }

    public void testEncodeCharBufferByteBufferbooleanExceptionTrue() throws CharacterCodingException {
        implTestEncodeCharBufferByteBufferbooleanException(true);
    }

    /* Class under test for CoderResult flush(ByteBuffer) */
    public void testFlush() throws CharacterCodingException {
        ByteBuffer out = ByteBuffer.allocate(6);
        CharBuffer in = CharBuffer.wrap("aaa");
        TestCase.assertEquals(in.remaining(), 3);
        // by encode facade, so that internal state will be wrong
        encoder.encode(CharBuffer.wrap("testFlush"), ByteBuffer.allocate(20), true);
        TestCase.assertSame(CoderResult.UNDERFLOW, encoder.flush(ByteBuffer.allocate(50)));
    }

    /* test isLegalReplacement(byte[]) */
    public void test_isLegalReplacement_null() {
        try {
            encoder.isLegalReplacement(null);
            TestCase.fail("should throw null pointer exception");
        } catch (NullPointerException e) {
        }
    }

    public void test_isLegalReplacement_good() {
        TestCase.assertTrue(encoder.isLegalReplacement(specifiedReplacement));
    }

    public void test_isLegalReplacement_bad() {
        TestCase.assertTrue(encoder.isLegalReplacement(new byte[200]));
        byte[] ba = getIllegalByteArray();
        if (ba != null) {
            TestCase.assertFalse(encoder.isLegalReplacement(ba));
        }
    }

    public void test_isLegalReplacement_empty_array() {
        // ISO, ASC, GB, UTF8 encoder will throw exception in RI
        // others will pass
        TestCase.assertTrue(encoder.isLegalReplacement(new byte[0]));
    }

    public void testOnMalformedInput() {
        TestCase.assertSame(CodingErrorAction.REPORT, encoder.malformedInputAction());
        try {
            encoder.onMalformedInput(null);
            TestCase.fail("should throw null pointer exception");
        } catch (IllegalArgumentException e) {
        }
        encoder.onMalformedInput(CodingErrorAction.IGNORE);
        TestCase.assertSame(CodingErrorAction.IGNORE, encoder.malformedInputAction());
    }

    public void testOnUnmappableCharacter() {
        TestCase.assertSame(CodingErrorAction.REPORT, encoder.unmappableCharacterAction());
        try {
            encoder.onUnmappableCharacter(null);
            TestCase.fail("should throw null pointer exception");
        } catch (IllegalArgumentException e) {
        }
        encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        TestCase.assertSame(CodingErrorAction.IGNORE, encoder.unmappableCharacterAction());
    }

    public void testReplacement() {
        try {
            encoder.replaceWith(null);
            TestCase.fail("should throw null pointer exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            encoder.replaceWith(new byte[0]);
            TestCase.fail("should throw null pointer exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            encoder.replaceWith(new byte[100]);
            TestCase.fail("should throw null pointer exception");
        } catch (IllegalArgumentException e) {
        }
        byte[] nr = getLegalByteArray();
        TestCase.assertSame(encoder, encoder.replaceWith(nr));
        TestCase.assertSame(nr, encoder.replacement());
        nr = getIllegalByteArray();
        try {
            encoder.replaceWith(new byte[100]);
            TestCase.fail();
        } catch (IllegalArgumentException e) {
        }
    }

    /* Mock subclass of CharsetEncoder For protected method test */
    public static class MockCharsetEncoder extends CharsetEncoder {
        boolean flushed = false;

        public boolean isFlushed() {
            boolean result = flushed;
            flushed = false;
            return result;
        }

        public boolean isLegalReplacement(byte[] ba) {
            if ((ba.length) == 155) {
                // specified magic number, return false
                return false;
            }
            return super.isLegalReplacement(ba);
        }

        public MockCharsetEncoder(Charset cs, float aver, float max) {
            super(cs, aver, max);
        }

        public MockCharsetEncoder(Charset cs, float aver, float max, byte[] replacement) {
            super(cs, aver, max, replacement);
        }

        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            int inPosition = in.position();
            char[] input = new char[in.remaining()];
            in.get(input);
            String result = new String(input);
            if (result.startsWith("malform")) {
                // reset the cursor to the error position
                in.position(inPosition);
                // in.position(0);
                // set the error length
                return CoderResult.malformedForLength("malform".length());
            } else
                if (result.startsWith("unmap")) {
                    // reset the cursor to the error position
                    in.position(inPosition);
                    // in.position(0);
                    // set the error length
                    return CoderResult.unmappableForLength("unmap".length());
                } else
                    if (result.startsWith("runtime")) {
                        // reset the cursor to the error position
                        in.position(0);
                        // set the error length
                        throw new RuntimeException("runtime");
                    }


            int inLeft = input.length;
            int outLeft = out.remaining();
            CoderResult r = CoderResult.UNDERFLOW;
            int length = inLeft;
            if (outLeft < inLeft) {
                r = CoderResult.OVERFLOW;
                length = outLeft;
                in.position((inPosition + outLeft));
            }
            for (int i = 0; i < length; i++) {
                out.put(((byte) (input[i])));
            }
            return r;
        }

        protected CoderResult implFlush(ByteBuffer out) {
            CoderResult result = super.implFlush(out);
            int length = 0;
            if ((out.remaining()) >= 5) {
                length = 5;
                result = CoderResult.UNDERFLOW;
                flushed = true;
                // for (int i = 0; i < length; i++) {
                // out.put((byte)'f');
                // }
            } else {
                length = out.remaining();
                result = CoderResult.OVERFLOW;
            }
            return result;
        }

        protected void implReplaceWith(byte[] ba) {
            TestCase.assertSame(ba, replacement());
        }
    }

    /* mock charset for test encoder initialization */
    public static class MockCharset extends Charset {
        protected MockCharset(String arg0, String[] arg1) {
            super(arg0, arg1);
        }

        public boolean contains(Charset arg0) {
            return false;
        }

        public CharsetDecoder newDecoder() {
            return new CharsetDecoderTest.MockCharsetDecoder(this, ((float) (CharsetEncoderTest.AVER_BYTES)), CharsetEncoderTest.MAX_BYTES);
        }

        public CharsetEncoder newEncoder() {
            return new CharsetEncoderTest.MockCharsetEncoder(this, ((float) (CharsetEncoderTest.AVER_BYTES)), CharsetEncoderTest.MAX_BYTES);
        }
    }
}

