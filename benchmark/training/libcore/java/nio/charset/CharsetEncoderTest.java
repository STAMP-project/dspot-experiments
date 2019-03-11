/**
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.nio.charset;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import junit.framework.TestCase;


public class CharsetEncoderTest extends TestCase {
    // None of the harmony or jtreg tests actually check that replaceWith does the right thing!
    public void test_replaceWith() throws Exception {
        Charset ascii = Charset.forName("US-ASCII");
        CharsetEncoder e = ascii.newEncoder();
        e.onMalformedInput(CodingErrorAction.REPLACE);
        e.onUnmappableCharacter(CodingErrorAction.REPLACE);
        e.replaceWith("=".getBytes("US-ASCII"));
        String input = "hello\u0666world";
        String output = ascii.decode(e.encode(CharBuffer.wrap(input))).toString();
        TestCase.assertEquals("hello=world", output);
    }

    // For all the guaranteed built-in charsets, check that we have the right default replacements.
    public void test_defaultReplacementBytesIso_8859_1() throws Exception {
        assertReplacementBytesForEncoder("ISO-8859-1", new byte[]{ ((byte) ('?')) });
    }

    public void test_defaultReplacementBytesUs_Ascii() throws Exception {
        assertReplacementBytesForEncoder("US-ASCII", new byte[]{ ((byte) ('?')) });
    }

    public void test_defaultReplacementBytesUtf_16() throws Exception {
        assertReplacementBytesForEncoder("UTF-16", new byte[]{ ((byte) (255)), ((byte) (253)) });
    }

    public void test_defaultReplacementBytesUtf_16be() throws Exception {
        assertReplacementBytesForEncoder("UTF-16BE", new byte[]{ ((byte) (255)), ((byte) (253)) });
    }

    public void test_defaultReplacementBytesUtf_16le() throws Exception {
        assertReplacementBytesForEncoder("UTF-16LE", new byte[]{ ((byte) (253)), ((byte) (255)) });
    }

    public void test_defaultReplacementBytesUtf_8() throws Exception {
        assertReplacementBytesForEncoder("UTF-8", new byte[]{ ((byte) ('?')) });
    }

    public void testSurrogatePairAllAtOnce() throws Exception {
        // okay: surrogate pair seen all at once is decoded to U+20b9f.
        Charset cs = Charset.forName("UTF-32BE");
        CharsetEncoder e = cs.newEncoder();
        ByteBuffer bb = ByteBuffer.allocate(128);
        CoderResult cr = e.encode(CharBuffer.wrap(new char[]{ '\ud842', '\udf9f' }), bb, false);
        TestCase.assertEquals(CoderResult.UNDERFLOW, cr);
        TestCase.assertEquals(4, bb.position());
        TestCase.assertEquals(((byte) (0)), bb.get(0));
        TestCase.assertEquals(((byte) (2)), bb.get(1));
        TestCase.assertEquals(((byte) (11)), bb.get(2));
        TestCase.assertEquals(((byte) (159)), bb.get(3));
    }

    public void testMalformedSurrogatePair() throws Exception {
        // malformed: low surrogate first is detected as an error.
        Charset cs = Charset.forName("UTF-32BE");
        CharsetEncoder e = cs.newEncoder();
        ByteBuffer bb = ByteBuffer.allocate(128);
        CoderResult cr = e.encode(CharBuffer.wrap(new char[]{ '\udf9f' }), bb, false);
        TestCase.assertTrue(cr.toString(), cr.isMalformed());
        TestCase.assertEquals(1, cr.length());
    }

    public void testCharsetEncoderSplitSurrogates_IGNORE() throws Exception {
        testCharsetEncoderSplitSurrogates(CodingErrorAction.IGNORE);
    }

    public void testCharsetEncoderSplitSurrogates_REPORT() throws Exception {
        testCharsetEncoderSplitSurrogates(CodingErrorAction.REPORT);
    }

    public void testCharsetEncoderSplitSurrogates_REPLACE() throws Exception {
        testCharsetEncoderSplitSurrogates(CodingErrorAction.REPLACE);
    }

    public void testFlushWithoutEndOfInput() throws Exception {
        Charset cs = Charset.forName("UTF-32BE");
        CharsetEncoder e = cs.newEncoder();
        ByteBuffer bb = ByteBuffer.allocate(128);
        CoderResult cr = e.encode(CharBuffer.wrap(new char[]{ 'x' }), bb, false);
        TestCase.assertEquals(CoderResult.UNDERFLOW, cr);
        TestCase.assertEquals(4, bb.position());
        try {
            cr = e.flush(bb);
            TestCase.fail();
        } catch (IllegalStateException expected) {
            // You must call encode with endOfInput true before you can flush.
        }
        // We had a bug where we wouldn't reset inEnd before calling encode in implFlush.
        // That would result in flush outputting garbage.
        cr = e.encode(CharBuffer.wrap(new char[]{ 'x' }), bb, true);
        TestCase.assertEquals(CoderResult.UNDERFLOW, cr);
        TestCase.assertEquals(8, bb.position());
        cr = e.flush(bb);
        TestCase.assertEquals(CoderResult.UNDERFLOW, cr);
        TestCase.assertEquals(8, bb.position());
    }
}

