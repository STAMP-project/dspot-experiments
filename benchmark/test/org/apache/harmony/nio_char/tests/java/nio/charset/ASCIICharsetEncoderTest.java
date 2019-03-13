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


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import junit.framework.TestCase;


public class ASCIICharsetEncoderTest extends TestCase {
    // charset for ascii
    private static final Charset cs = Charset.forName("ascii");

    private static final CharsetEncoder encoder = ASCIICharsetEncoderTest.cs.newEncoder();

    private static final int MAXCODEPOINT = 127;

    public void testCanEncodeCharSequence() {
        // normal case for ascCS
        TestCase.assertTrue(ASCIICharsetEncoderTest.encoder.canEncode("w"));
        TestCase.assertFalse(ASCIICharsetEncoderTest.encoder.canEncode("\uc2a3"));
        TestCase.assertFalse(ASCIICharsetEncoderTest.encoder.canEncode("\ud800\udc00"));
        try {
            ASCIICharsetEncoderTest.encoder.canEncode(null);
        } catch (NullPointerException e) {
        }
        TestCase.assertTrue(ASCIICharsetEncoderTest.encoder.canEncode(""));
    }

    public void testCanEncodeSurrogate() {
        TestCase.assertFalse(ASCIICharsetEncoderTest.encoder.canEncode('\ud800'));
        TestCase.assertFalse(ASCIICharsetEncoderTest.encoder.canEncode("\udc00"));
    }

    public void testCanEncodechar() throws CharacterCodingException {
        TestCase.assertTrue(ASCIICharsetEncoderTest.encoder.canEncode('w'));
        TestCase.assertFalse(ASCIICharsetEncoderTest.encoder.canEncode('\uc2a3'));
    }

    public void testSpecificDefaultValue() {
        TestCase.assertEquals(1.0, ASCIICharsetEncoderTest.encoder.averageBytesPerChar(), 0.0);
        TestCase.assertEquals(1.0, ASCIICharsetEncoderTest.encoder.maxBytesPerChar(), 0.0);
    }

    public void testMultiStepEncode() throws CharacterCodingException {
        ASCIICharsetEncoderTest.encoder.onMalformedInput(CodingErrorAction.REPORT);
        ASCIICharsetEncoderTest.encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            ASCIICharsetEncoderTest.encoder.encode(CharBuffer.wrap("\ud800\udc00"));
            TestCase.fail("should unmappable");
        } catch (UnmappableCharacterException e) {
        }
        ASCIICharsetEncoderTest.encoder.reset();
        ByteBuffer out = ByteBuffer.allocate(10);
        TestCase.assertTrue(ASCIICharsetEncoderTest.encoder.encode(CharBuffer.wrap("\ud800"), out, true).isMalformed());
        ASCIICharsetEncoderTest.encoder.flush(out);
        ASCIICharsetEncoderTest.encoder.reset();
        out = ByteBuffer.allocate(10);
        TestCase.assertSame(CoderResult.UNDERFLOW, ASCIICharsetEncoderTest.encoder.encode(CharBuffer.wrap("\ud800"), out, false));
        TestCase.assertTrue(ASCIICharsetEncoderTest.encoder.encode(CharBuffer.wrap("\udc00"), out, true).isMalformed());
    }

    public void testEncodeMapping() throws CharacterCodingException {
        ASCIICharsetEncoderTest.encoder.reset();
        for (int i = 0; i <= (ASCIICharsetEncoderTest.MAXCODEPOINT); i++) {
            char[] chars = Character.toChars(i);
            CharBuffer cb = CharBuffer.wrap(chars);
            ByteBuffer bb = ASCIICharsetEncoderTest.encoder.encode(cb);
            TestCase.assertEquals(i, bb.get(0));
        }
        CharBuffer cb = CharBuffer.wrap("\u0080");
        try {
            ASCIICharsetEncoderTest.encoder.encode(cb);
        } catch (UnmappableCharacterException e) {
            // expected
        }
        cb = CharBuffer.wrap("\ud800");
        try {
            ASCIICharsetEncoderTest.encoder.encode(cb);
        } catch (MalformedInputException e) {
            // expected
        }
        ByteBuffer bb = ByteBuffer.allocate(16);
        cb = CharBuffer.wrap("A");
        ASCIICharsetEncoderTest.encoder.reset();
        ASCIICharsetEncoderTest.encoder.encode(cb, bb, false);
        try {
            ASCIICharsetEncoderTest.encoder.encode(cb);
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testInternalState() {
        CharBuffer in = CharBuffer.wrap("A");
        ByteBuffer out = ByteBuffer.allocate(16);
        // normal encoding process
        ASCIICharsetEncoderTest.encoder.reset();
        ASCIICharsetEncoderTest.encoder.encode(in, out, false);
        in = CharBuffer.wrap("B");
        ASCIICharsetEncoderTest.encoder.encode(in, out, true);
        ASCIICharsetEncoderTest.encoder.flush(out);
    }

    // reset could be called at any time
    public void testInternalState_Reset() {
        CharsetEncoder newEncoder = ASCIICharsetEncoderTest.cs.newEncoder();
        // Init - > reset
        newEncoder.reset();
        // reset - > reset
        newEncoder.reset();
        // encoding - >reset
        {
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, false);
            newEncoder.reset();
        }
        // encoding end -> reset
        {
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            newEncoder.reset();
        }
        // flused -> reset
        {
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            newEncoder.flush(out);
            newEncoder.reset();
        }
    }

    public void testInternalState_Encoding() {
        CharsetEncoder newEncoder = ASCIICharsetEncoderTest.cs.newEncoder();
        // Init - > encoding
        {
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, false);
        }
        // reset - > encoding
        {
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.reset();
            newEncoder.encode(in, out, false);
        }
        // reset - > encoding - > encoding
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, false);
            in = CharBuffer.wrap("BC");
            newEncoder.encode(in, out, false);
        }
        // encoding_end - > encoding
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            in = CharBuffer.wrap("BC");
            try {
                newEncoder.encode(in, out, false);
                TestCase.fail("Should throw IllegalStateException");
            } catch (IllegalStateException e) {
                // expected
            }
        }
        // flushed - > encoding
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            newEncoder.flush(out);
            in = CharBuffer.wrap("BC");
            try {
                newEncoder.encode(in, out, false);
                TestCase.fail("Should throw IllegalStateException");
            } catch (IllegalStateException e) {
                // expected
            }
        }
    }

    public void testInternalState_Encoding_END() {
        CharsetEncoder newEncoder = ASCIICharsetEncoderTest.cs.newEncoder();
        // Init - >encoding_end
        {
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
        }
        // Reset -> encoding_end
        {
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.reset();
            newEncoder.encode(in, out, true);
        }
        // encoding -> encoding_end
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, false);
            in = CharBuffer.wrap("BC");
            newEncoder.encode(in, out, true);
        }
        // Reset -> encoding_end
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            in = CharBuffer.wrap("BC");
            newEncoder.encode(in, out, true);
        }
        // Flushed -> encoding_end
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            newEncoder.flush(out);
            in = CharBuffer.wrap("BC");
            try {
                newEncoder.encode(in, out, true);
                TestCase.fail("Should throw IllegalStateException");
            } catch (IllegalStateException e) {
                // expected
            }
        }
    }

    public void testInternalState_Flushed() {
        CharsetEncoder newEncoder = ASCIICharsetEncoderTest.cs.newEncoder();
        // init -> flushed
        {
            ByteBuffer out = ByteBuffer.allocate(16);
            try {
                newEncoder.flush(out);
                TestCase.fail("Should throw IllegalStateException");
            } catch (IllegalStateException e) {
                // expected
            }
        }
        // reset - > flushed
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            newEncoder.reset();
            try {
                newEncoder.flush(out);
                TestCase.fail("Should throw IllegalStateException");
            } catch (IllegalStateException e) {
                // expected
            }
        }
        // encoding - > flushed
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, false);
            try {
                newEncoder.flush(out);
                TestCase.fail("Should throw IllegalStateException");
            } catch (IllegalStateException e) {
                // expected
            }
        }
        // encoding_end -> flushed
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            newEncoder.flush(out);
        }
        // flushd - > flushed
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            newEncoder.flush(out);
            newEncoder.flush(out);
        }
    }

    public void testInternalState_Encode() throws CharacterCodingException {
        CharsetEncoder newEncoder = ASCIICharsetEncoderTest.cs.newEncoder();
        // Init - > encode
        {
            CharBuffer in = CharBuffer.wrap("A");
            newEncoder.encode(in);
        }
        // Reset - > encode
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            newEncoder.encode(in);
        }
        // Encoding -> encode
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, false);
            in = CharBuffer.wrap("BC");
            newEncoder.encode(in);
        }
        // Encoding_end -> encode
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            in = CharBuffer.wrap("BC");
            newEncoder.encode(in);
        }
        // Flushed -> reset
        {
            newEncoder.reset();
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.encode(in, out, true);
            in = CharBuffer.wrap("BC");
            newEncoder.flush(out);
            out = newEncoder.encode(in);
        }
    }

    public void testInternalState_from_Encode() throws CharacterCodingException {
        CharsetEncoder newEncoder = ASCIICharsetEncoderTest.cs.newEncoder();
        // Encode -> Reset
        {
            CharBuffer in = CharBuffer.wrap("A");
            newEncoder.encode(in);
            newEncoder.reset();
        }
        // Encode -> encoding
        {
            CharBuffer in = CharBuffer.wrap("A");
            newEncoder.encode(in);
            ByteBuffer out = ByteBuffer.allocate(16);
            try {
                newEncoder.encode(in, out, false);
                TestCase.fail("Should throw IllegalStateException");
            } catch (IllegalStateException e) {
                // expected
            }
        }
        // Encode -> Encoding_end
        {
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = ByteBuffer.allocate(16);
            newEncoder.reset();
            newEncoder.encode(in, out, false);
            newEncoder.encode(in, out, true);
        }
        // Encode -> Flushed
        {
            CharBuffer in = CharBuffer.wrap("A");
            ByteBuffer out = newEncoder.encode(in);
            newEncoder.flush(out);
        }
        // Encode - > encode
        {
            CharBuffer in = CharBuffer.wrap("A");
            newEncoder.encode(in);
            in = CharBuffer.wrap("BC");
            newEncoder.encode(in);
        }
    }
}

