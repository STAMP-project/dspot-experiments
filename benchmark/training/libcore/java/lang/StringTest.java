/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.lang;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Locale;
import junit.framework.TestCase;


public class StringTest extends TestCase {
    public void testIsEmpty() {
        TestCase.assertTrue("".isEmpty());
        TestCase.assertFalse("x".isEmpty());
    }

    // The evil decoder keeps hold of the CharBuffer it wrote to.
    private static final class EvilCharsetDecoder extends CharsetDecoder {
        private static char[] chars;

        public EvilCharsetDecoder(Charset cs) {
            super(cs, 1.0F, 1.0F);
        }

        protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
            StringTest.EvilCharsetDecoder.chars = out.array();
            int inLength = in.remaining();
            for (int i = 0; i < inLength; ++i) {
                in.put(((byte) ('X')));
                out.put('Y');
            }
            return CoderResult.UNDERFLOW;
        }

        public static void corrupt() {
            for (int i = 0; i < (StringTest.EvilCharsetDecoder.chars.length); ++i) {
                StringTest.EvilCharsetDecoder.chars[i] = '$';
            }
        }
    }

    // The evil encoder tries to write to the CharBuffer it was given to
    // read from.
    private static final class EvilCharsetEncoder extends CharsetEncoder {
        public EvilCharsetEncoder(Charset cs) {
            super(cs, 1.0F, 1.0F);
        }

        protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
            int inLength = in.remaining();
            for (int i = 0; i < inLength; ++i) {
                in.put('x');
                out.put(((byte) ('y')));
            }
            return CoderResult.UNDERFLOW;
        }
    }

    private static final Charset EVIL_CHARSET = new Charset("evil", null) {
        public boolean contains(Charset charset) {
            return false;
        }

        public CharsetEncoder newEncoder() {
            return new StringTest.EvilCharsetEncoder(this);
        }

        public CharsetDecoder newDecoder() {
            return new StringTest.EvilCharsetDecoder(this);
        }
    };

    public void testGetBytes_MaliciousCharset() {
        try {
            String s = "hi";
            // Check that our encoder can't write to the input CharBuffer
            // it was given.
            s.getBytes(StringTest.EVIL_CHARSET);
            TestCase.fail();// We shouldn't have got here!

        } catch (ReadOnlyBufferException expected) {
            // We caught you trying to be naughty!
        }
    }

    public void testString_BII() throws Exception {
        byte[] bytes = "xa\u0666bx".getBytes("UTF-8");
        TestCase.assertEquals("a\u0666b", new String(bytes, 1, ((bytes.length) - 2)));
    }

    public void testString_BIIString() throws Exception {
        byte[] bytes = "xa\u0666bx".getBytes("UTF-8");
        TestCase.assertEquals("a\u0666b", new String(bytes, 1, ((bytes.length) - 2), "UTF-8"));
    }

    public void testString_BIICharset() throws Exception {
        byte[] bytes = "xa\u0666bx".getBytes("UTF-8");
        TestCase.assertEquals("a\u0666b", new String(bytes, 1, ((bytes.length) - 2), Charset.forName("UTF-8")));
    }

    public void testString_BCharset() throws Exception {
        byte[] bytes = "a\u0666b".getBytes("UTF-8");
        TestCase.assertEquals("a\u0666b", new String(bytes, Charset.forName("UTF-8")));
    }

    public void testStringFromCharset_MaliciousCharset() {
        Charset cs = StringTest.EVIL_CHARSET;
        byte[] bytes = new byte[]{ ((byte) ('h')), ((byte) ('i')) };
        final String result = new String(bytes, cs);
        TestCase.assertEquals("YY", result);// (Our decoder always outputs 'Y's.)

        // Check that even if the decoder messes with the output CharBuffer
        // after we've created a string from it, it doesn't affect the string.
        StringTest.EvilCharsetDecoder.corrupt();
        TestCase.assertEquals("YY", result);
    }

    public void test_getBytes_bad() throws Exception {
        // Check that we use '?' as the replacement byte for invalid characters.
        TestCase.assertEquals("[97, 63, 98]", Arrays.toString("a\u0666b".getBytes("US-ASCII")));
        TestCase.assertEquals("[97, 63, 98]", Arrays.toString("a\u0666b".getBytes(Charset.forName("US-ASCII"))));
    }

    public void test_getBytes_UTF_8() {
        // We have a fast path implementation of String.getBytes for UTF-8.
        Charset cs = Charset.forName("UTF-8");
        // Test the empty string.
        TestCase.assertEquals("[]", Arrays.toString("".getBytes(cs)));
        // Test one-byte characters.
        TestCase.assertEquals("[0]", Arrays.toString("\u0000".getBytes(cs)));
        TestCase.assertEquals("[127]", Arrays.toString("\u007f".getBytes(cs)));
        TestCase.assertEquals("[104, 105]", Arrays.toString("hi".getBytes(cs)));
        // Test two-byte characters.
        TestCase.assertEquals("[-62, -128]", Arrays.toString("\u0080".getBytes(cs)));
        TestCase.assertEquals("[-39, -90]", Arrays.toString("\u0666".getBytes(cs)));
        TestCase.assertEquals("[-33, -65]", Arrays.toString("\u07ff".getBytes(cs)));
        TestCase.assertEquals("[104, -39, -90, 105]", Arrays.toString("h\u0666i".getBytes(cs)));
        // Test three-byte characters.
        TestCase.assertEquals("[-32, -96, -128]", Arrays.toString("\u0800".getBytes(cs)));
        TestCase.assertEquals("[-31, -120, -76]", Arrays.toString("\u1234".getBytes(cs)));
        TestCase.assertEquals("[-17, -65, -65]", Arrays.toString("\uffff".getBytes(cs)));
        TestCase.assertEquals("[104, -31, -120, -76, 105]", Arrays.toString("h\u1234i".getBytes(cs)));
        // Test supplementary characters.
        // Minimum supplementary character: U+10000
        TestCase.assertEquals("[-16, -112, -128, -128]", Arrays.toString("\ud800\udc00".getBytes(cs)));
        // Random supplementary character: U+10381 Ugaritic letter beta
        TestCase.assertEquals("[-16, -112, -114, -127]", Arrays.toString("\ud800\udf81".getBytes(cs)));
        // Maximum supplementary character: U+10FFFF
        TestCase.assertEquals("[-12, -113, -65, -65]", Arrays.toString("\udbff\udfff".getBytes(cs)));
        // A high surrogate at end of string is an error replaced with '?'.
        TestCase.assertEquals("[104, 63]", Arrays.toString("h\ud800".getBytes(cs)));
        // A high surrogate not followed by a low surrogate is an error replaced with '?'.
        TestCase.assertEquals("[104, 63, 105]", Arrays.toString("h\ud800i".getBytes(cs)));
    }

    public void test_new_String_bad() throws Exception {
        // Check that we use U+FFFD as the replacement string for invalid bytes.
        TestCase.assertEquals("a\ufffdb", new String(new byte[]{ 97, -2, 98 }, "US-ASCII"));
        TestCase.assertEquals("a\ufffdb", new String(new byte[]{ 97, -2, 98 }, Charset.forName("US-ASCII")));
    }

    /**
     * Tests a widely assumed performance characteristic of String.substring():
     * that it reuses the original's backing array. Although behaviour should be
     * correct even if this test fails, many applications may suffer
     * significant performance degradation.
     */
    public void testSubstringSharesBackingArray() throws IllegalAccessException {
        String abcdefghij = "ABCDEFGHIJ";
        String cdefg = abcdefghij.substring(2, 7);
        TestCase.assertSame(getBackingArray(abcdefghij), getBackingArray(cdefg));
    }

    /**
     * Tests a widely assumed performance characteristic of string's copy
     * constructor: that it ensures the backing array is the same length as the
     * string. Although behaviour should be correct even if this test fails,
     * many applications may suffer significant performance degradation.
     */
    public void testStringCopiesAvoidHeapRetention() throws IllegalAccessException {
        String abcdefghij = "ABCDEFGHIJ";
        TestCase.assertSame(getBackingArray(abcdefghij), getBackingArray(new String(abcdefghij)));
        String cdefg = abcdefghij.substring(2, 7);
        TestCase.assertSame(getBackingArray(abcdefghij), getBackingArray(cdefg));
        TestCase.assertEquals(5, getBackingArray(new String(cdefg)).length);
    }

    /**
     * Test that strings interned manually and then later loaded as literals
     * maintain reference equality. http://b/3098960
     */
    public void testInternBeforeLiteralIsLoaded() throws Exception {
        String programmatic = Arrays.asList("5058", "9962", "1563", "5744").toString().intern();
        String literal = ((String) (Class.forName("libcore.java.lang.StringTest$HasLiteral").getDeclaredField("literal").get(null)));
        TestCase.assertEquals(System.identityHashCode(programmatic), System.identityHashCode(literal));
        TestCase.assertSame(programmatic, literal);
    }

    static class HasLiteral {
        static String literal = "[5058, 9962, 1563, 5744]";
    }

    private static final String COMBINING_DOT_ABOVE = "\u0307";

    private static final String LATIN_CAPITAL_I = "I";

    private static final String LATIN_CAPITAL_I_WITH_DOT_ABOVE = "\u0130";

    private static final String LATIN_SMALL_I = "i";

    private static final String LATIN_SMALL_DOTLESS_I = "\u0131";

    private static final String[] LATIN_I_VARIANTS = new String[]{ StringTest.LATIN_SMALL_I, StringTest.LATIN_SMALL_DOTLESS_I, StringTest.LATIN_CAPITAL_I, StringTest.LATIN_CAPITAL_I_WITH_DOT_ABOVE };

    public void testCaseMapping_tr_TR() {
        Locale trTR = new Locale("tr", "TR");
        TestCase.assertEquals(StringTest.LATIN_SMALL_I, StringTest.LATIN_SMALL_I.toLowerCase(trTR));
        TestCase.assertEquals(StringTest.LATIN_SMALL_I, StringTest.LATIN_CAPITAL_I_WITH_DOT_ABOVE.toLowerCase(trTR));
        TestCase.assertEquals(StringTest.LATIN_SMALL_DOTLESS_I, StringTest.LATIN_SMALL_DOTLESS_I.toLowerCase(trTR));
        TestCase.assertEquals(StringTest.LATIN_CAPITAL_I, StringTest.LATIN_CAPITAL_I.toUpperCase(trTR));
        TestCase.assertEquals(StringTest.LATIN_CAPITAL_I_WITH_DOT_ABOVE, StringTest.LATIN_CAPITAL_I_WITH_DOT_ABOVE.toUpperCase(trTR));
        TestCase.assertEquals(StringTest.LATIN_CAPITAL_I_WITH_DOT_ABOVE, StringTest.LATIN_SMALL_I.toUpperCase(trTR));
        TestCase.assertEquals(StringTest.LATIN_CAPITAL_I, StringTest.LATIN_SMALL_DOTLESS_I.toUpperCase(trTR));
        TestCase.assertEquals(StringTest.LATIN_SMALL_DOTLESS_I, StringTest.LATIN_CAPITAL_I.toLowerCase(trTR));
    }

    public void testCaseMapping_en_US() {
        Locale enUs = new Locale("en", "US");
        TestCase.assertEquals(StringTest.LATIN_CAPITAL_I, StringTest.LATIN_SMALL_I.toUpperCase(enUs));
        TestCase.assertEquals(StringTest.LATIN_CAPITAL_I, StringTest.LATIN_CAPITAL_I.toUpperCase(enUs));
        TestCase.assertEquals(StringTest.LATIN_CAPITAL_I_WITH_DOT_ABOVE, StringTest.LATIN_CAPITAL_I_WITH_DOT_ABOVE.toUpperCase(enUs));
        TestCase.assertEquals(StringTest.LATIN_SMALL_I, StringTest.LATIN_SMALL_I.toLowerCase(enUs));
        TestCase.assertEquals(StringTest.LATIN_SMALL_I, StringTest.LATIN_CAPITAL_I.toLowerCase(enUs));
        TestCase.assertEquals(StringTest.LATIN_SMALL_DOTLESS_I, StringTest.LATIN_SMALL_DOTLESS_I.toLowerCase(enUs));
        TestCase.assertEquals(StringTest.LATIN_CAPITAL_I, StringTest.LATIN_SMALL_DOTLESS_I.toUpperCase(enUs));
        // http://b/3325799: the RI fails this because it's using an obsolete version of the Unicode rules.
        // Android correctly preserves canonical equivalence. (See the separate test for tr_TR.)
        TestCase.assertEquals(((StringTest.LATIN_SMALL_I) + (StringTest.COMBINING_DOT_ABOVE)), StringTest.LATIN_CAPITAL_I_WITH_DOT_ABOVE.toLowerCase(enUs));
    }

    public void testEqualsIgnoreCase_tr_TR() {
        testEqualsIgnoreCase(new Locale("tr", "TR"));
    }

    public void testEqualsIgnoreCase_en_US() {
        testEqualsIgnoreCase(new Locale("en", "US"));
    }

    public void testRegionMatches_ignoreCase_en_US() {
        testRegionMatches_ignoreCase(new Locale("en", "US"));
    }

    public void testRegionMatches_ignoreCase_tr_TR() {
        testRegionMatches_ignoreCase(new Locale("tr", "TR"));
    }

    // http://code.google.com/p/android/issues/detail?id=15266
    public void test_replaceAll() throws Exception {
        TestCase.assertEquals("project_Id", "projectId".replaceAll("(?!^)(\\p{Upper})(?!$)", "_$1"));
    }

    // https://code.google.com/p/android/issues/detail?id=23831
    public void test_23831() throws Exception {
        byte[] bytes = new byte[]{ ((byte) (245)), ((byte) (169)), ((byte) (234)), ((byte) (33)) };
        String expected = "\ufffd\ufffd!";
        // Since we use icu4c for CharsetDecoder...
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.REPLACE);
        TestCase.assertEquals(expected, decoder.decode(ByteBuffer.wrap(bytes)).toString());
        // Our fast-path code in String should behave the same...
        TestCase.assertEquals(expected, new String(bytes, "UTF-8"));
    }

    // https://code.google.com/p/android/issues/detail?id=55129
    public void test_55129() throws Exception {
        TestCase.assertEquals("-h-e-l-l-o- -w-o-r-l-d-", "hello world".replace("", "-"));
        TestCase.assertEquals("-w-o-r-l-d-", "hello world".substring(6).replace("", "-"));
        TestCase.assertEquals("-*-w-*-o-*-r-*-l-*-d-*-", "hello world".substring(6).replace("", "-*-"));
    }

    // http://b/11571917
    public void test_String_getBytes() throws Exception {
        TestCase.assertEquals("[-126, -96]", Arrays.toString("?".getBytes("Shift_JIS")));
        TestCase.assertEquals("[-126, -87]", Arrays.toString("?".getBytes("Shift_JIS")));
        TestCase.assertEquals("[-105, 67]", Arrays.toString("?".getBytes("Shift_JIS")));
        TestCase.assertEquals("[36]", Arrays.toString("$".getBytes("Shift_JIS")));
        TestCase.assertEquals("[-29, -127, -117]", Arrays.toString("?".getBytes("UTF-8")));
    }
}

