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


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import junit.framework.TestCase;


/**
 * API unit test for java.nio.CharsetDecoder
 */
public class CharsetDecoderTest extends TestCase {
    protected static final int MAX_BYTES = 3;

    protected static final double AVER_BYTES = 0.5;

    // default charset
    private static final Charset MOCKCS = new CharsetEncoderTest.MockCharset("mock", new String[0]);

    Charset cs = CharsetDecoderTest.MOCKCS;

    // default decoder
    protected static CharsetDecoder decoder;

    String bom = "";

    // FIXME: give up this tests
    // /*
    // * test default value
    // */
    // public void testDefaultCharsPerByte() {
    // assertTrue(decoder.averageCharsPerByte() == AVER_BYTES);
    // assertTrue(decoder.maxCharsPerByte() == MAX_BYTES);
    // }
    public void testDefaultValues() {
        TestCase.assertSame(cs, CharsetDecoderTest.decoder.charset());
        try {
            CharsetDecoderTest.decoder.detectedCharset();
            TestCase.fail("should unsupported");
        } catch (UnsupportedOperationException e) {
        }
        try {
            TestCase.assertTrue(CharsetDecoderTest.decoder.isCharsetDetected());
            TestCase.fail("should unsupported");
        } catch (UnsupportedOperationException e) {
        }
        TestCase.assertFalse(CharsetDecoderTest.decoder.isAutoDetecting());
        TestCase.assertSame(CodingErrorAction.REPORT, CharsetDecoderTest.decoder.malformedInputAction());
        TestCase.assertSame(CodingErrorAction.REPORT, CharsetDecoderTest.decoder.unmappableCharacterAction());
        TestCase.assertEquals(CharsetDecoderTest.decoder.replacement(), "\ufffd");
    }

    /* test constructor */
    public void testCharsetDecoder() {
        // default value
        CharsetDecoderTest.decoder = new CharsetDecoderTest.MockCharsetDecoder(cs, ((float) (CharsetDecoderTest.AVER_BYTES)), CharsetDecoderTest.MAX_BYTES);
        // normal case
        CharsetDecoder ec = new CharsetDecoderTest.MockCharsetDecoder(cs, 1, CharsetDecoderTest.MAX_BYTES);
        TestCase.assertSame(ec.charset(), cs);
        TestCase.assertEquals(1.0, ec.averageCharsPerByte(), 0.0);
        TestCase.assertTrue(((ec.maxCharsPerByte()) == (CharsetDecoderTest.MAX_BYTES)));
        /* ------------------------ Exceptional cases ------------------------- */
        // Normal case: null charset
        ec = new CharsetDecoderTest.MockCharsetDecoder(null, 1, CharsetDecoderTest.MAX_BYTES);
        TestCase.assertNull(ec.charset());
        TestCase.assertEquals(1.0, ec.averageCharsPerByte(), 0.0);
        TestCase.assertTrue(((ec.maxCharsPerByte()) == (CharsetDecoderTest.MAX_BYTES)));
        ec = new CharsetDecoderTest.MockCharsetDecoder(new CharsetEncoderTest.MockCharset("mock", new String[0]), 1, CharsetDecoderTest.MAX_BYTES);
        // Commented out since the comment is wrong since MAX_BYTES > 1
        // // OK: average length less than max length
        // ec = new MockCharsetDecoder(cs, MAX_BYTES, 1);
        // assertTrue(ec.averageCharsPerByte() == MAX_BYTES);
        // assertTrue(ec.maxCharsPerByte() == 1);
        // Illegal Argument: zero length
        try {
            ec = new CharsetDecoderTest.MockCharsetDecoder(cs, 0, CharsetDecoderTest.MAX_BYTES);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            ec = new CharsetDecoderTest.MockCharsetDecoder(cs, 1, 0);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        // Illegal Argument: negative length
        try {
            ec = new CharsetDecoderTest.MockCharsetDecoder(cs, (-1), CharsetDecoderTest.MAX_BYTES);
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            ec = new CharsetDecoderTest.MockCharsetDecoder(cs, 1, (-1));
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    /* test onMalformedInput */
    public void testOnMalformedInput() {
        TestCase.assertSame(CodingErrorAction.REPORT, CharsetDecoderTest.decoder.malformedInputAction());
        try {
            CharsetDecoderTest.decoder.onMalformedInput(null);
            TestCase.fail("should throw null pointer exception");
        } catch (IllegalArgumentException e) {
        }
        CharsetDecoderTest.decoder.onMalformedInput(CodingErrorAction.IGNORE);
        TestCase.assertSame(CodingErrorAction.IGNORE, CharsetDecoderTest.decoder.malformedInputAction());
    }

    /* test unmappableCharacter */
    public void testOnUnmappableCharacter() {
        TestCase.assertSame(CodingErrorAction.REPORT, CharsetDecoderTest.decoder.unmappableCharacterAction());
        try {
            CharsetDecoderTest.decoder.onUnmappableCharacter(null);
            TestCase.fail("should throw null pointer exception");
        } catch (IllegalArgumentException e) {
        }
        CharsetDecoderTest.decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        TestCase.assertSame(CodingErrorAction.IGNORE, CharsetDecoderTest.decoder.unmappableCharacterAction());
    }

    /* test replaceWith */
    public void testReplaceWith() {
        try {
            CharsetDecoderTest.decoder.replaceWith(null);
            TestCase.fail("should throw null pointer exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            CharsetDecoderTest.decoder.replaceWith("");
            TestCase.fail("should throw null pointer exception");
        } catch (IllegalArgumentException e) {
        }
        try {
            CharsetDecoderTest.decoder.replaceWith("testReplaceWith");
            TestCase.fail("should throw illegal argument exception");
        } catch (IllegalArgumentException e) {
        }
        CharsetDecoderTest.decoder.replaceWith("a");
        TestCase.assertSame("a", CharsetDecoderTest.decoder.replacement());
    }

    /* Class under test for CharBuffer decode(ByteBuffer) */
    public void testDecodeByteBuffer() throws CharacterCodingException {
        implTestDecodeByteBuffer();
    }

    public void testDecodeByteBufferException() throws UnsupportedEncodingException, CharacterCodingException {
        CharBuffer out;
        ByteBuffer in;
        String replaceStr = (CharsetDecoderTest.decoder.replacement()) + (getString());
        // MalformedException:
        CharsetDecoderTest.decoder.onMalformedInput(CodingErrorAction.REPORT);
        CharsetDecoderTest.decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        in = getMalformedByteBuffer();
        if (in != null) {
            try {
                CharBuffer buffer = CharsetDecoderTest.decoder.decode(in);
                TestCase.assertTrue(((buffer.remaining()) > 0));
                TestCase.fail("should throw MalformedInputException");
            } catch (MalformedInputException e) {
            }
            CharsetDecoderTest.decoder.reset();
            in.rewind();
            CharsetDecoderTest.decoder.onMalformedInput(CodingErrorAction.IGNORE);
            out = CharsetDecoderTest.decoder.decode(in);
            assertCharBufferValue(getString(), out);
            CharsetDecoderTest.decoder.reset();
            in.rewind();
            CharsetDecoderTest.decoder.onMalformedInput(CodingErrorAction.REPLACE);
            out = CharsetDecoderTest.decoder.decode(in);
            assertCharBufferValue(replaceStr, out);
        }
        // Unmapped Exception:
        CharsetDecoderTest.decoder.onMalformedInput(CodingErrorAction.REPORT);
        CharsetDecoderTest.decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        in = getUnmappedByteBuffer();
        if (in != null) {
            try {
                CharsetDecoderTest.decoder.decode(in);
                TestCase.fail("should throw UnmappableCharacterException");
            } catch (UnmappableCharacterException e) {
            }
            CharsetDecoderTest.decoder.reset();
            in.rewind();
            CharsetDecoderTest.decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
            out = CharsetDecoderTest.decoder.decode(in);
            assertCharBufferValue(getString(), out);
            CharsetDecoderTest.decoder.reset();
            in.rewind();
            CharsetDecoderTest.decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            out = CharsetDecoderTest.decoder.decode(in);
            assertCharBufferValue(replaceStr, out);
        }
        // RuntimeException
        try {
            CharsetDecoderTest.decoder.decode(getExceptionByteArray());
            TestCase.fail("should throw runtime exception");
        } catch (RuntimeException e) {
        }
    }

    /* Class under test for CoderResult decode(ByteBuffer, CharBuffer, boolean) */
    public void testDecodeByteBufferCharBuffer() {
        implTestDecodeByteBufferCharBuffer(getByteBuffer());
    }

    public void testDecodeByteBufferCharBufferReadOnly() {
        implTestDecodeByteBufferCharBuffer(getByteBuffer());
    }

    public void testDecodeCharBufferByteBufferUnmappedException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferUnmappedException(getUnmappedByteBuffer(), true);
    }

    public void testDecodeCharBufferByteIncompleteBufferUnmappedException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferUnmappedException(getUnmappedByteBuffer(), false);
    }

    public void testDecodeCharBufferByteReadOnlyBufferUnmappedException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferUnmappedException(readOnly(getUnmappedByteBuffer()), true);
    }

    public void testDecodeCharBufferByteReadOnlyIncompleteBufferUnmappedException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferUnmappedException(readOnly(getUnmappedByteBuffer()), false);
    }

    public void testDecodeCharBufferByteBufferMalformedException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferMalformedException(getMalformedByteBuffer(), true);
    }

    public void testDecodeCharBufferByteIncompleteBufferMalformedException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferMalformedException(getMalformedByteBuffer(), false);
    }

    public void testDecodeCharBufferByteReadOnlyBufferMalformedException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferMalformedException(readOnly(getMalformedByteBuffer()), true);
    }

    public void testDecodeCharBufferByteReadOnlyIncompleteBufferMalformedException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferMalformedException(readOnly(getMalformedByteBuffer()), false);
    }

    public void testDecodeCharBufferByteBufferException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferException(getExceptionByteArray(), true);
    }

    public void testDecodeCharBufferByteIncompleteBufferException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferException(getExceptionByteArray(), false);
    }

    public void testDecodeCharBufferByteReadOnlyBufferException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferException(readOnly(getExceptionByteArray()), true);
    }

    public void testDecodeCharBufferByteReadOnlyIncompleteBufferException() throws UnsupportedEncodingException, CharacterCodingException {
        implTestDecodeCharBufferByteBufferException(readOnly(getExceptionByteArray()), false);
    }

    /* test flush */
    public void testFlush() throws CharacterCodingException {
        CharBuffer out = CharBuffer.allocate(10);
        ByteBuffer in = ByteBuffer.wrap(new byte[]{ 12, 12 });
        CharsetDecoderTest.decoder.decode(in, out, true);
        TestCase.assertSame(CoderResult.UNDERFLOW, CharsetDecoderTest.decoder.flush(out));
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(((ByteBuffer) (in.rewind())), ((CharBuffer) (out.rewind())), true);
        TestCase.assertSame(CoderResult.UNDERFLOW, CharsetDecoderTest.decoder.flush(CharBuffer.allocate(10)));
    }

    /* ---------------------------------- methods to test illegal state
    -----------------------------------
     */
    // Normal case: just after reset, and it also means reset can be done
    // anywhere
    public void testResetIllegalState() throws CharacterCodingException {
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(getByteBuffer());
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(getByteBuffer(), CharBuffer.allocate(3), false);
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(getByteBuffer(), CharBuffer.allocate(3), true);
        CharsetDecoderTest.decoder.reset();
    }

    public void testFlushIllegalState() throws CharacterCodingException {
        ByteBuffer in = ByteBuffer.wrap(new byte[]{ 98, 98 });
        CharBuffer out = CharBuffer.allocate(5);
        // Illegal state: after reset.
        CharsetDecoderTest.decoder.reset();
        try {
            CharsetDecoderTest.decoder.flush(out);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
        // Normal case: after decode with endOfInput is true
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(in, out, true);
        out.rewind();
        CoderResult result = CharsetDecoderTest.decoder.flush(out);
        TestCase.assertSame(result, CoderResult.UNDERFLOW);
        // Good state: flush twice
        CharsetDecoderTest.decoder.flush(out);
        // Illegal state: flush after decode with endOfInput is false
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(in, out, false);
        try {
            CharsetDecoderTest.decoder.flush(out);
            TestCase.fail();
        } catch (IllegalStateException expected) {
        }
    }

    // test illegal states for decode facade
    public void testDecodeFacadeIllegalState() throws CharacterCodingException {
        // decode facade can be execute in anywhere
        ByteBuffer in = getByteBuffer();
        // Normal case: just created
        CharsetDecoderTest.decoder.decode(in);
        in.rewind();
        // Normal case: just after decode facade
        CharsetDecoderTest.decoder.decode(in);
        in.rewind();
        // Normal case: just after decode with that endOfInput is true
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(getByteBuffer(), CharBuffer.allocate(30), true);
        CharsetDecoderTest.decoder.decode(in);
        in.rewind();
        // Normal case:just after decode with that endOfInput is false
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(getByteBuffer(), CharBuffer.allocate(30), false);
        CharsetDecoderTest.decoder.decode(in);
        in.rewind();
        // Normal case: just after flush
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(getByteBuffer(), CharBuffer.allocate(30), true);
        CharsetDecoderTest.decoder.flush(CharBuffer.allocate(10));
        CharsetDecoderTest.decoder.decode(in);
        in.rewind();
    }

    // test illegal states for two decode method with endOfInput is true
    public void testDecodeTrueIllegalState() throws CharacterCodingException {
        ByteBuffer in = ByteBuffer.wrap(new byte[]{ 98, 98 });
        CharBuffer out = CharBuffer.allocate(100);
        // Normal case: just created
        CharsetDecoderTest.decoder.decode(in, out, true);
        in.rewind();
        out.rewind();
        // Normal case: just after decode with that endOfInput is true
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(in, CharBuffer.allocate(30), true);
        in.rewind();
        CharsetDecoderTest.decoder.decode(in, out, true);
        in.rewind();
        out.rewind();
        // Normal case:just after decode with that endOfInput is false
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(in, CharBuffer.allocate(30), false);
        in.rewind();
        CharsetDecoderTest.decoder.decode(in, out, true);
        in.rewind();
        out.rewind();
        // Illegal state: just after flush
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(in, CharBuffer.allocate(30), true);
        CharsetDecoderTest.decoder.flush(CharBuffer.allocate(10));
        in.rewind();
        try {
            CharsetDecoderTest.decoder.decode(in, out, true);
            TestCase.fail("should illegal state");
        } catch (IllegalStateException e) {
        }
        in.rewind();
        out.rewind();
    }

    // test illegal states for two decode method with endOfInput is false
    public void testDecodeFalseIllegalState() throws CharacterCodingException {
        ByteBuffer in = ByteBuffer.wrap(new byte[]{ 98, 98 });
        CharBuffer out = CharBuffer.allocate(5);
        // Normal case: just created
        CharsetDecoderTest.decoder.decode(in, out, false);
        in.rewind();
        out.rewind();
        // Illegal state: just after decode facade
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(in);
        in.rewind();
        try {
            CharsetDecoderTest.decoder.decode(in, out, false);
            TestCase.fail("should illegal state");
        } catch (IllegalStateException e) {
        }
        in.rewind();
        out.rewind();
        // Illegal state: just after decode with that endOfInput is true
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(in, CharBuffer.allocate(30), true);
        in.rewind();
        try {
            CharsetDecoderTest.decoder.decode(in, out, false);
            TestCase.fail("should illegal state");
        } catch (IllegalStateException e) {
        }
        in.rewind();
        out.rewind();
        // Normal case:just after decode with that endOfInput is false
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(in, CharBuffer.allocate(30), false);
        in.rewind();
        CharsetDecoderTest.decoder.decode(in, out, false);
        in.rewind();
        out.rewind();
        // Illegal state: just after flush
        CharsetDecoderTest.decoder.reset();
        CharsetDecoderTest.decoder.decode(in, CharBuffer.allocate(30), true);
        in.rewind();
        CharsetDecoderTest.decoder.flush(CharBuffer.allocate(10));
        try {
            CharsetDecoderTest.decoder.decode(in, out, false);
            TestCase.fail("should illegal state");
        } catch (IllegalStateException e) {
        }
    }

    /* --------------------------------- illegal state test end
    ---------------------------------
     */
    public void testImplFlush() {
        CharsetDecoderTest.decoder = new CharsetDecoderTest.MockCharsetDecoder(cs, 1, 3);
        TestCase.assertEquals(CoderResult.UNDERFLOW, ((CharsetDecoderTest.MockCharsetDecoder) (CharsetDecoderTest.decoder)).pubImplFlush(null));
    }

    public void testImplOnMalformedInput() {
        CharsetDecoderTest.decoder = new CharsetDecoderTest.MockCharsetDecoder(cs, 1, 3);
        TestCase.assertEquals(CoderResult.UNDERFLOW, ((CharsetDecoderTest.MockCharsetDecoder) (CharsetDecoderTest.decoder)).pubImplFlush(null));
    }

    public void testImplOnUnmappableCharacter() {
        CharsetDecoderTest.decoder = new CharsetDecoderTest.MockCharsetDecoder(cs, 1, 3);
        ((CharsetDecoderTest.MockCharsetDecoder) (CharsetDecoderTest.decoder)).pubImplOnUnmappableCharacter(null);
    }

    public void testImplReplaceWith() {
        CharsetDecoderTest.decoder = new CharsetDecoderTest.MockCharsetDecoder(cs, 1, 3);
        ((CharsetDecoderTest.MockCharsetDecoder) (CharsetDecoderTest.decoder)).pubImplReplaceWith(null);
    }

    public void testImplReset() {
        CharsetDecoderTest.decoder = new CharsetDecoderTest.MockCharsetDecoder(cs, 1, 3);
        ((CharsetDecoderTest.MockCharsetDecoder) (CharsetDecoderTest.decoder)).pubImplReset();
    }

    /* mock decoder */
    public static class MockCharsetDecoder extends CharsetDecoder {
        public MockCharsetDecoder(Charset cs, float ave, float max) {
            super(cs, ave, max);
        }

        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            int inPosition = in.position();
            byte[] input = new byte[in.remaining()];
            in.get(input);
            String result;
            try {
                result = new String(input, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e);
            }
            if (result.startsWith("malform")) {
                // reset the cursor to the error position
                in.position(inPosition);
                // set the error length
                return CoderResult.malformedForLength("malform".length());
            } else
                if (result.startsWith("unmap")) {
                    // reset the cursor to the error position
                    in.position(inPosition);
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
                out.put(((char) (input[i])));
            }
            return r;
        }

        protected CoderResult implFlush(CharBuffer out) {
            CoderResult result = super.implFlush(out);
            if ((out.remaining()) >= 5) {
                // TODO
                // out.put("flush");
                result = CoderResult.UNDERFLOW;
            } else {
                // out.put("flush", 0, out.remaining());
                result = CoderResult.OVERFLOW;
            }
            return result;
        }

        public CoderResult pubImplFlush(CharBuffer out) {
            return super.implFlush(out);
        }

        public void pubImplOnMalformedInput(CodingErrorAction newAction) {
            super.implOnMalformedInput(newAction);
        }

        public void pubImplOnUnmappableCharacter(CodingErrorAction newAction) {
            super.implOnUnmappableCharacter(newAction);
        }

        public void pubImplReplaceWith(String newReplacement) {
            super.implReplaceWith(newReplacement);
        }

        public void pubImplReset() {
            super.implReset();
        }
    }
}

