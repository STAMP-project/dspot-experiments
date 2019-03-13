/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util;


import AsciiString.EMPTY_STRING;
import CharsetUtil.ISO_8859_1;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Random;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test character encoding and case insensitivity for the {@link AsciiString} class
 */
public class AsciiStringCharacterTest {
    private static final Random r = new Random();

    @Test
    public void testGetBytesStringBuilder() {
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < (1 << 16); ++i) {
            b.append("e?a?");
        }
        final String bString = b.toString();
        final Charset[] charsets = CharsetUtil.values();
        for (int i = 0; i < (charsets.length); ++i) {
            final Charset charset = charsets[i];
            byte[] expected = bString.getBytes(charset);
            byte[] actual = new AsciiString(b, charset).toByteArray();
            Assert.assertArrayEquals(("failure for " + charset), expected, actual);
        }
    }

    @Test
    public void testGetBytesString() {
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < (1 << 16); ++i) {
            b.append("e?a?");
        }
        final String bString = b.toString();
        final Charset[] charsets = CharsetUtil.values();
        for (int i = 0; i < (charsets.length); ++i) {
            final Charset charset = charsets[i];
            byte[] expected = bString.getBytes(charset);
            byte[] actual = new AsciiString(bString, charset).toByteArray();
            Assert.assertArrayEquals(("failure for " + charset), expected, actual);
        }
    }

    @Test
    public void testGetBytesAsciiString() {
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < (1 << 16); ++i) {
            b.append("e?a?");
        }
        final String bString = b.toString();
        // The AsciiString class actually limits the Charset to ISO_8859_1
        byte[] expected = bString.getBytes(ISO_8859_1);
        byte[] actual = new AsciiString(bString).toByteArray();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testComparisonWithString() {
        String string = "shouldn't fail";
        AsciiString ascii = new AsciiString(string.toCharArray());
        Assert.assertEquals(string, ascii.toString());
    }

    @Test
    public void subSequenceTest() {
        byte[] init = new byte[]{ 't', 'h', 'i', 's', ' ', 'i', 's', ' ', 'a', ' ', 't', 'e', 's', 't' };
        AsciiString ascii = new AsciiString(init);
        final int start = 2;
        final int end = init.length;
        AsciiString sub1 = ascii.subSequence(start, end, false);
        AsciiString sub2 = ascii.subSequence(start, end, true);
        Assert.assertEquals(sub1.hashCode(), sub2.hashCode());
        Assert.assertEquals(sub1, sub2);
        for (int i = start; i < end; ++i) {
            Assert.assertEquals(init[i], sub1.byteAt((i - start)));
        }
    }

    @Test
    public void testContains() {
        String[] falseLhs = new String[]{ null, "a", "aa", "aaa" };
        String[] falseRhs = new String[]{ null, "b", "ba", "baa" };
        for (int i = 0; i < (falseLhs.length); ++i) {
            for (int j = 0; j < (falseRhs.length); ++j) {
                AsciiStringCharacterTest.assertContains(falseLhs[i], falseRhs[i], false, false);
            }
        }
        AsciiStringCharacterTest.assertContains("", "", true, true);
        AsciiStringCharacterTest.assertContains("AsfdsF", "", true, true);
        AsciiStringCharacterTest.assertContains("", "b", false, false);
        AsciiStringCharacterTest.assertContains("a", "a", true, true);
        AsciiStringCharacterTest.assertContains("a", "b", false, false);
        AsciiStringCharacterTest.assertContains("a", "A", false, true);
        String b = "xyz";
        String a = b;
        AsciiStringCharacterTest.assertContains(a, b, true, true);
        a = "a" + b;
        AsciiStringCharacterTest.assertContains(a, b, true, true);
        a = b + "a";
        AsciiStringCharacterTest.assertContains(a, b, true, true);
        a = ("a" + b) + "a";
        AsciiStringCharacterTest.assertContains(a, b, true, true);
        b = "xYz";
        a = "xyz";
        AsciiStringCharacterTest.assertContains(a, b, false, true);
        b = "xYz";
        a = ("xyzxxxXyZ" + b) + "aaa";
        AsciiStringCharacterTest.assertContains(a, b, true, true);
        b = "foOo";
        a = "fooofoO";
        AsciiStringCharacterTest.assertContains(a, b, false, true);
        b = "Content-Equals: 10000";
        a = "content-equals: 1000";
        AsciiStringCharacterTest.assertContains(a, b, false, false);
        a += "0";
        AsciiStringCharacterTest.assertContains(a, b, false, true);
    }

    @Test
    public void testCaseSensitivity() {
        int i = 0;
        for (; i < 32; i++) {
            AsciiStringCharacterTest.doCaseSensitivity(i);
        }
        final int min = i;
        final int max = 4000;
        final int len = (AsciiStringCharacterTest.r.nextInt(((max - min) + 1))) + min;
        AsciiStringCharacterTest.doCaseSensitivity(len);
    }

    @Test
    public void caseInsensitiveHasherCharBuffer() {
        String s1 = new String("TRANSFER-ENCODING");
        char[] array = new char[128];
        final int offset = 100;
        for (int i = 0; i < (s1.length()); ++i) {
            array[(offset + i)] = s1.charAt(i);
        }
        CharBuffer buffer = CharBuffer.wrap(array, offset, s1.length());
        Assert.assertEquals(AsciiString.hashCode(s1), AsciiString.hashCode(buffer));
    }

    @Test
    public void testBooleanUtilityMethods() {
        Assert.assertTrue(new AsciiString(new byte[]{ 1 }).parseBoolean());
        Assert.assertFalse(EMPTY_STRING.parseBoolean());
        Assert.assertFalse(new AsciiString(new byte[]{ 0 }).parseBoolean());
        Assert.assertTrue(new AsciiString(new byte[]{ 5 }).parseBoolean());
        Assert.assertTrue(new AsciiString(new byte[]{ 2, 0 }).parseBoolean());
    }

    @Test
    public void testEqualsIgnoreCase() {
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase(null, null), CoreMatchers.is(true));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase(null, "foo"), CoreMatchers.is(false));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase("bar", null), CoreMatchers.is(false));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase("FoO", "fOo"), CoreMatchers.is(true));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase("FoO", "bar"), CoreMatchers.is(false));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase("Foo", "foobar"), CoreMatchers.is(false));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase("foobar", "Foo"), CoreMatchers.is(false));
        // Test variations (Ascii + String, Ascii + Ascii, String + Ascii)
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase(new AsciiString("FoO"), "fOo"), CoreMatchers.is(true));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase(new AsciiString("FoO"), new AsciiString("fOo")), CoreMatchers.is(true));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase("FoO", new AsciiString("fOo")), CoreMatchers.is(true));
        // Test variations (Ascii + String, Ascii + Ascii, String + Ascii)
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase(new AsciiString("FoO"), "bAr"), CoreMatchers.is(false));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase(new AsciiString("FoO"), new AsciiString("bAr")), CoreMatchers.is(false));
        Assert.assertThat(AsciiString.contentEqualsIgnoreCase("FoO", new AsciiString("bAr")), CoreMatchers.is(false));
    }

    @Test
    public void testIndexOfIgnoreCase() {
        Assert.assertEquals((-1), AsciiString.indexOfIgnoreCase(null, "abc", 1));
        Assert.assertEquals((-1), AsciiString.indexOfIgnoreCase("abc", null, 1));
        Assert.assertEquals(0, AsciiString.indexOfIgnoreCase("", "", 0));
        Assert.assertEquals(0, AsciiString.indexOfIgnoreCase("aabaabaa", "A", 0));
        Assert.assertEquals(2, AsciiString.indexOfIgnoreCase("aabaabaa", "B", 0));
        Assert.assertEquals(1, AsciiString.indexOfIgnoreCase("aabaabaa", "AB", 0));
        Assert.assertEquals(5, AsciiString.indexOfIgnoreCase("aabaabaa", "B", 3));
        Assert.assertEquals((-1), AsciiString.indexOfIgnoreCase("aabaabaa", "B", 9));
        Assert.assertEquals(2, AsciiString.indexOfIgnoreCase("aabaabaa", "B", (-1)));
        Assert.assertEquals(2, AsciiString.indexOfIgnoreCase("aabaabaa", "", 2));
        Assert.assertEquals((-1), AsciiString.indexOfIgnoreCase("abc", "", 9));
        Assert.assertEquals(0, AsciiString.indexOfIgnoreCase("?abaabaa", "?", 0));
    }

    @Test
    public void testIndexOfIgnoreCaseAscii() {
        Assert.assertEquals((-1), AsciiString.indexOfIgnoreCaseAscii(null, "abc", 1));
        Assert.assertEquals((-1), AsciiString.indexOfIgnoreCaseAscii("abc", null, 1));
        Assert.assertEquals(0, AsciiString.indexOfIgnoreCaseAscii("", "", 0));
        Assert.assertEquals(0, AsciiString.indexOfIgnoreCaseAscii("aabaabaa", "A", 0));
        Assert.assertEquals(2, AsciiString.indexOfIgnoreCaseAscii("aabaabaa", "B", 0));
        Assert.assertEquals(1, AsciiString.indexOfIgnoreCaseAscii("aabaabaa", "AB", 0));
        Assert.assertEquals(5, AsciiString.indexOfIgnoreCaseAscii("aabaabaa", "B", 3));
        Assert.assertEquals((-1), AsciiString.indexOfIgnoreCaseAscii("aabaabaa", "B", 9));
        Assert.assertEquals(2, AsciiString.indexOfIgnoreCaseAscii("aabaabaa", "B", (-1)));
        Assert.assertEquals(2, AsciiString.indexOfIgnoreCaseAscii("aabaabaa", "", 2));
        Assert.assertEquals((-1), AsciiString.indexOfIgnoreCaseAscii("abc", "", 9));
    }

    @Test
    public void testTrim() {
        Assert.assertEquals("", EMPTY_STRING.trim().toString());
        Assert.assertEquals("abc", new AsciiString("  abc").trim().toString());
        Assert.assertEquals("abc", new AsciiString("abc  ").trim().toString());
        Assert.assertEquals("abc", new AsciiString("  abc  ").trim().toString());
    }

    @Test
    public void testIndexOfChar() {
        Assert.assertEquals((-1), AsciiString.indexOf(null, 'a', 0));
        Assert.assertEquals((-1), AsciiString.of("").indexOf('a', 0));
        Assert.assertEquals((-1), AsciiString.of("abc").indexOf('d', 0));
        Assert.assertEquals((-1), AsciiString.of("aabaabaa").indexOf('A', 0));
        Assert.assertEquals(0, AsciiString.of("aabaabaa").indexOf('a', 0));
        Assert.assertEquals(1, AsciiString.of("aabaabaa").indexOf('a', 1));
        Assert.assertEquals(3, AsciiString.of("aabaabaa").indexOf('a', 2));
        Assert.assertEquals(3, AsciiString.of("aabdabaa").indexOf('d', 1));
        Assert.assertEquals(1, new AsciiString("abcd", 1, 2).indexOf('c', 0));
        Assert.assertEquals(2, new AsciiString("abcd", 1, 3).indexOf('d', 2));
        Assert.assertEquals(0, new AsciiString("abcd", 1, 2).indexOf('b', 0));
        Assert.assertEquals((-1), new AsciiString("abcd", 0, 2).indexOf('c', 0));
        Assert.assertEquals((-1), new AsciiString("abcd", 1, 3).indexOf('a', 0));
    }

    @Test
    public void testIndexOfCharSequence() {
        Assert.assertEquals(0, new AsciiString("abcd").indexOf("abcd", 0));
        Assert.assertEquals(0, new AsciiString("abcd").indexOf("abc", 0));
        Assert.assertEquals(1, new AsciiString("abcd").indexOf("bcd", 0));
        Assert.assertEquals(1, new AsciiString("abcd").indexOf("bc", 0));
        Assert.assertEquals(1, new AsciiString("abcdabcd").indexOf("bcd", 0));
        Assert.assertEquals(0, new AsciiString("abcd", 1, 2).indexOf("bc", 0));
        Assert.assertEquals(0, new AsciiString("abcd", 1, 3).indexOf("bcd", 0));
        Assert.assertEquals(1, new AsciiString("abcdabcd", 4, 4).indexOf("bcd", 0));
        Assert.assertEquals(3, new AsciiString("012345").indexOf("345", 3));
        Assert.assertEquals(3, new AsciiString("012345").indexOf("345", 0));
        // Test with empty string
        Assert.assertEquals(0, new AsciiString("abcd").indexOf("", 0));
        Assert.assertEquals(1, new AsciiString("abcd").indexOf("", 1));
        Assert.assertEquals(3, new AsciiString("abcd", 1, 3).indexOf("", 4));
        // Test not found
        Assert.assertEquals((-1), new AsciiString("abcd").indexOf("abcde", 0));
        Assert.assertEquals((-1), new AsciiString("abcdbc").indexOf("bce", 0));
        Assert.assertEquals((-1), new AsciiString("abcd", 1, 3).indexOf("abc", 0));
        Assert.assertEquals((-1), new AsciiString("abcd", 1, 2).indexOf("bd", 0));
        Assert.assertEquals((-1), new AsciiString("012345").indexOf("345", 4));
        Assert.assertEquals((-1), new AsciiString("012345").indexOf("abc", 3));
        Assert.assertEquals((-1), new AsciiString("012345").indexOf("abc", 0));
        Assert.assertEquals((-1), new AsciiString("012345").indexOf("abcdefghi", 0));
        Assert.assertEquals((-1), new AsciiString("012345").indexOf("abcdefghi", 4));
    }

    @Test
    public void testStaticIndexOfChar() {
        Assert.assertEquals((-1), AsciiString.indexOf(null, 'a', 0));
        Assert.assertEquals((-1), AsciiString.indexOf("", 'a', 0));
        Assert.assertEquals((-1), AsciiString.indexOf("abc", 'd', 0));
        Assert.assertEquals((-1), AsciiString.indexOf("aabaabaa", 'A', 0));
        Assert.assertEquals(0, AsciiString.indexOf("aabaabaa", 'a', 0));
        Assert.assertEquals(1, AsciiString.indexOf("aabaabaa", 'a', 1));
        Assert.assertEquals(3, AsciiString.indexOf("aabaabaa", 'a', 2));
        Assert.assertEquals(3, AsciiString.indexOf("aabdabaa", 'd', 1));
    }

    @Test
    public void testLastIndexOfCharSequence() {
        Assert.assertEquals(0, new AsciiString("abcd").lastIndexOf("abcd", 0));
        Assert.assertEquals(0, new AsciiString("abcd").lastIndexOf("abc", 0));
        Assert.assertEquals(1, new AsciiString("abcd").lastIndexOf("bcd", 0));
        Assert.assertEquals(1, new AsciiString("abcd").lastIndexOf("bc", 0));
        Assert.assertEquals(5, new AsciiString("abcdabcd").lastIndexOf("bcd", 0));
        Assert.assertEquals(0, new AsciiString("abcd", 1, 2).lastIndexOf("bc", 0));
        Assert.assertEquals(0, new AsciiString("abcd", 1, 3).lastIndexOf("bcd", 0));
        Assert.assertEquals(1, new AsciiString("abcdabcd", 4, 4).lastIndexOf("bcd", 0));
        Assert.assertEquals(3, new AsciiString("012345").lastIndexOf("345", 3));
        Assert.assertEquals(3, new AsciiString("012345").lastIndexOf("345", 0));
        // Test with empty string
        Assert.assertEquals(0, new AsciiString("abcd").lastIndexOf("", 0));
        Assert.assertEquals(1, new AsciiString("abcd").lastIndexOf("", 1));
        Assert.assertEquals(3, new AsciiString("abcd", 1, 3).lastIndexOf("", 4));
        // Test not found
        Assert.assertEquals((-1), new AsciiString("abcd").lastIndexOf("abcde", 0));
        Assert.assertEquals((-1), new AsciiString("abcdbc").lastIndexOf("bce", 0));
        Assert.assertEquals((-1), new AsciiString("abcd", 1, 3).lastIndexOf("abc", 0));
        Assert.assertEquals((-1), new AsciiString("abcd", 1, 2).lastIndexOf("bd", 0));
        Assert.assertEquals((-1), new AsciiString("012345").lastIndexOf("345", 4));
        Assert.assertEquals((-1), new AsciiString("012345").lastIndexOf("abc", 3));
        Assert.assertEquals((-1), new AsciiString("012345").lastIndexOf("abc", 0));
        Assert.assertEquals((-1), new AsciiString("012345").lastIndexOf("abcdefghi", 0));
        Assert.assertEquals((-1), new AsciiString("012345").lastIndexOf("abcdefghi", 4));
    }

    @Test
    public void testReplace() {
        AsciiString abcd = new AsciiString("abcd");
        Assert.assertEquals(new AsciiString("adcd"), abcd.replace('b', 'd'));
        Assert.assertEquals(new AsciiString("dbcd"), abcd.replace('a', 'd'));
        Assert.assertEquals(new AsciiString("abca"), abcd.replace('d', 'a'));
        Assert.assertSame(abcd, abcd.replace('x', 'a'));
        Assert.assertEquals(new AsciiString("cc"), new AsciiString("abcd", 1, 2).replace('b', 'c'));
        Assert.assertEquals(new AsciiString("bb"), new AsciiString("abcd", 1, 2).replace('c', 'b'));
        Assert.assertEquals(new AsciiString("bddd"), new AsciiString("abcdc", 1, 4).replace('c', 'd'));
        Assert.assertEquals(new AsciiString("xbcxd"), new AsciiString("abcada", 0, 5).replace('a', 'x'));
    }

    @Test
    public void testSubStringHashCode() {
        // two "123"s
        Assert.assertEquals(AsciiString.hashCode("123"), AsciiString.hashCode("a123".substring(1)));
    }

    @Test
    public void testIndexOf() {
        AsciiString foo = AsciiString.of("This is a test");
        int i1 = foo.indexOf(' ', 0);
        Assert.assertEquals(4, i1);
        int i2 = foo.indexOf(' ', (i1 + 1));
        Assert.assertEquals(7, i2);
        int i3 = foo.indexOf(' ', (i2 + 1));
        Assert.assertEquals(9, i3);
        Assert.assertTrue(((i3 + 1) < (foo.length())));
        int i4 = foo.indexOf(' ', (i3 + 1));
        Assert.assertEquals(i4, (-1));
    }
}

