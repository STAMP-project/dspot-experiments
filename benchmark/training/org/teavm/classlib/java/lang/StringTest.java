/**
 * Copyright 2013 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang;


import java.io.UnsupportedEncodingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class StringTest {
    @Test
    public void charsExtracted() {
        String str = "123";
        Assert.assertEquals('1', str.charAt(0));
        Assert.assertEquals('2', str.charAt(1));
        Assert.assertEquals('3', str.charAt(2));
    }

    @Test
    public void lengthComputed() {
        String str = "123";
        Assert.assertEquals(3, str.length());
    }

    @Test
    public void stringCreatedFromChars() {
        String str = new String(new char[]{ '1', '2', '3' });
        Assert.assertEquals("123", str);
    }

    @Test
    public void stringsAreEqual() {
        String a = new String(new char[]{ '1', '2', '3' });
        String b = new String(new char[]{ '1', '2', '3' });
        Assert.assertEquals(a, b);
    }

    @Test
    public void stringsAreNotEqual() {
        String a = new String(new char[]{ '1', '2', '3' });
        String b = new String(new char[]{ '1', '4', '3' });
        Assert.assertNotEquals(a, b);
    }

    @Test
    public void stringCharactersRead() {
        char[] buffer = new char[4];
        "123".getChars(0, 3, buffer, 0);
        Assert.assertArrayEquals(new char[]{ '1', '2', '3', '\u0000' }, buffer);
    }

    @Test
    public void stringIsEqualToBuilder() {
        Assert.assertTrue("123".contentEquals(new StringBuilder().append('1').append('2').append('3')));
    }

    @Test
    public void comparesSameStrings() {
        String a = "123";
        String b = new String(a);
        Assert.assertTrue(((a.compareTo(b)) == 0));
    }

    @Test
    public void comparesToPrecedingStrings() {
        Assert.assertTrue((("abc".compareTo("abbc")) > 0));
    }

    @Test
    public void comparesToSuccessorStrings() {
        Assert.assertTrue((("abc".compareTo("abdc")) < 0));
    }

    @Test
    public void startsWithWorks() {
        Assert.assertTrue("123".startsWith("12"));
    }

    @Test
    public void regionsMatched() {
        Assert.assertTrue("12345".regionMatches(2, "23456", 1, 2));
    }

    @Test
    public void endsWithWorkds() {
        Assert.assertTrue("12345".endsWith("45"));
    }

    @Test
    public void exposesSupplementaryCodePoint() {
        String str = new String(new char[]{ ((char) (56178)), ((char) (56972)) });
        Assert.assertEquals(969356, str.codePointAt(0));
        Assert.assertEquals(56972, str.codePointAt(1));
    }

    @Test
    public void exposesWrongSurrogates() {
        String str = new String(new char[]{ ((char) (56972)), ((char) (56178)) });
        Assert.assertEquals(56972, str.codePointAt(0));
        Assert.assertEquals(56178, str.codePointAt(1));
    }

    @Test
    public void exposesSupplementaryCodePointBefore() {
        String str = new String(new char[]{ ((char) (56178)), ((char) (56972)) });
        Assert.assertEquals(969356, str.codePointBefore(2));
        Assert.assertEquals(56178, str.codePointBefore(1));
    }

    @Test
    public void countsCodePoints() {
        String str = new String(new char[]{ ((char) (56178)), ((char) (56972)), 'a', 'b' });
        Assert.assertEquals(3, str.codePointCount(0, 4));
        Assert.assertEquals(1, str.codePointCount(0, 2));
        Assert.assertEquals(2, str.codePointCount(2, 4));
        Assert.assertEquals(3, str.codePointCount(1, 4));
    }

    @Test
    public void givesOffsetByCodePoint() {
        String str = new String(new char[]{ ((char) (56178)), ((char) (56972)), 'a', 'b' });
        Assert.assertEquals(2, str.offsetByCodePoints(0, 1));
        Assert.assertEquals(2, str.offsetByCodePoints(1, 1));
        Assert.assertEquals(4, str.offsetByCodePoints(0, 3));
        Assert.assertEquals(4, str.offsetByCodePoints(1, 3));
    }

    @Test
    public void findsCodePoint() {
        String str = new String(new char[]{ 'a', 'b', ((char) (56178)), ((char) (56972)), 'c', ((char) (56178)), ((char) (56972)), 'c', 'd' });
        Assert.assertEquals(2, str.indexOf(969356));
        Assert.assertEquals(4, str.indexOf('c'));
    }

    @Test
    public void findsCodePointBackward() {
        String str = new String(new char[]{ 'a', 'b', ((char) (56178)), ((char) (56972)), 'c', ((char) (56178)), ((char) (56972)), 'c', 'd' });
        Assert.assertEquals(5, str.lastIndexOf(969356));
        Assert.assertEquals(7, str.lastIndexOf('c'));
    }

    @Test
    public void findsString() {
        Assert.assertEquals(1, "abcdbcd".indexOf("bc"));
        Assert.assertEquals(3, "abcdbcd".indexOf("dbcd"));
        Assert.assertEquals((-1), "abcdbcd".indexOf("bb"));
    }

    @Test
    public void findsStringBackward() {
        Assert.assertEquals(4, "abcdbcd".lastIndexOf("bc"));
        Assert.assertEquals((-1), "abcdbcd".lastIndexOf("bb"));
    }

    @Test
    public void concatenatesStrings() {
        Assert.assertEquals("abcd", "ab".concat("cd"));
    }

    @Test
    public void replacesCharacter() {
        Assert.assertEquals("abbdbbd", "abcdbcd".replace('c', 'b'));
    }

    @Test
    public void containsWorks() {
        Assert.assertTrue("abcd".contains("bc"));
    }

    @Test
    public void sequenceReplaced() {
        Assert.assertEquals("ba", "aaa".replace("aa", "b"));
    }

    @Test
    public void trimWorks() {
        Assert.assertEquals("ab", "  ab   ".trim());
        Assert.assertEquals("ab", "ab".trim());
        Assert.assertEquals("", "  ".trim());
    }

    @Test
    public void convertedToCharArray() {
        char[] array = "123".toCharArray();
        Assert.assertEquals(3, array.length);
        Assert.assertArrayEquals(new char[]{ '1', '2', '3' }, array);
    }

    @Test
    public void createdFromByteArray() throws UnsupportedEncodingException {
        byte[] bytes = new byte[]{ 49, 50, 51 };
        Assert.assertEquals("123", new String(bytes, "UTF-8"));
    }

    @Test
    public void createdFromUTF8ByteArray() throws UnsupportedEncodingException {
        byte[] bytes = new byte[]{ 65, -62, -69, -32, -82, -69, -16, -66, -78, -69 };
        Assert.assertEquals("A\u00bb\u0bbb\ud8bb\udcbb", new String(bytes, "UTF-8"));
    }

    @Test
    public void createFromLongUTF8ByteArray() throws UnsupportedEncodingException {
        byte[] bytes = new byte[16384];
        for (int i = 0; i < (bytes.length);) {
            bytes[(i++)] = -16;
            bytes[(i++)] = -66;
            bytes[(i++)] = -78;
            bytes[(i++)] = -69;
        }
        String str = new String(bytes, "UTF-8");
        Assert.assertEquals('\ud8bb', str.charAt(8190));
        Assert.assertEquals('\udcbb', str.charAt(8191));
    }

    @Test
    public void getByteArray() throws UnsupportedEncodingException {
        byte[] bytes = "123".getBytes("UTF-8");
        Assert.assertArrayEquals(new byte[]{ 49, 50, 51 }, bytes);
        Assert.assertEquals(3, bytes.length);
    }

    @Test
    public void getUTF8ByteArray() throws UnsupportedEncodingException {
        byte[] bytes = "A\u00bb\u0bbb\ud8bb\udcbb".getBytes("UTF-8");
        Assert.assertArrayEquals(new byte[]{ 65, -62, -69, -32, -82, -69, -16, -66, -78, -69 }, bytes);
    }

    @Test
    public void getUTF8ByteArrayOfLongString() throws UnsupportedEncodingException {
        char[] chars = new char[8192];
        for (int i = 0; i < (chars.length);) {
            chars[(i++)] = '\ud8bb';
            chars[(i++)] = '\udcbb';
        }
        byte[] bytes = new String(chars).getBytes("UTF-8");
        Assert.assertEquals((-16), bytes[16380]);
        Assert.assertEquals((-66), bytes[16381]);
        Assert.assertEquals((-78), bytes[16382]);
        Assert.assertEquals((-69), bytes[16383]);
    }

    @Test
    public void createsStringFromCodePoints() {
        int[] codePoints = new int[]{ 97, 98, 969356, 99, 969356, 99, 100 };
        String str = new String(codePoints, 0, codePoints.length);
        Assert.assertEquals('a', str.charAt(0));
        Assert.assertEquals('b', str.charAt(1));
        Assert.assertEquals(56178, str.charAt(2));
        Assert.assertEquals(56972, str.charAt(3));
        Assert.assertEquals('c', str.charAt(4));
        Assert.assertEquals(56178, str.charAt(5));
        Assert.assertEquals(56972, str.charAt(6));
        Assert.assertEquals('c', str.charAt(7));
        Assert.assertEquals('d', str.charAt(8));
    }

    @Test
    public void makesLowerCase() {
        Assert.assertEquals("foo bar", "FoO bAr".toLowerCase());
    }

    @Test
    public void interns() {
        Assert.assertSame("xabc".substring(1).intern(), "abcx".substring(0, 3).intern());
        Assert.assertSame("xabc".substring(1).intern(), "abc");
    }

    @Test
    public void internsConstants() {
        Assert.assertSame("abc", ("a" + "bc").intern());
    }
}

