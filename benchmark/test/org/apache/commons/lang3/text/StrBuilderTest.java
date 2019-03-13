/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.text;


import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.CharBuffer;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests for {@link org.apache.commons.lang3.text.StrBuilder}.
 */
@Deprecated
public class StrBuilderTest {
    // -----------------------------------------------------------------------
    @Test
    public void testConstructors() {
        final StrBuilder sb0 = new StrBuilder();
        Assertions.assertEquals(32, sb0.capacity());
        Assertions.assertEquals(0, sb0.length());
        Assertions.assertEquals(0, sb0.size());
        final StrBuilder sb1 = new StrBuilder(32);
        Assertions.assertEquals(32, sb1.capacity());
        Assertions.assertEquals(0, sb1.length());
        Assertions.assertEquals(0, sb1.size());
        final StrBuilder sb2 = new StrBuilder(0);
        Assertions.assertEquals(32, sb2.capacity());
        Assertions.assertEquals(0, sb2.length());
        Assertions.assertEquals(0, sb2.size());
        final StrBuilder sb3 = new StrBuilder((-1));
        Assertions.assertEquals(32, sb3.capacity());
        Assertions.assertEquals(0, sb3.length());
        Assertions.assertEquals(0, sb3.size());
        final StrBuilder sb4 = new StrBuilder(1);
        Assertions.assertEquals(1, sb4.capacity());
        Assertions.assertEquals(0, sb4.length());
        Assertions.assertEquals(0, sb4.size());
        final StrBuilder sb5 = new StrBuilder(null);
        Assertions.assertEquals(32, sb5.capacity());
        Assertions.assertEquals(0, sb5.length());
        Assertions.assertEquals(0, sb5.size());
        final StrBuilder sb6 = new StrBuilder("");
        Assertions.assertEquals(32, sb6.capacity());
        Assertions.assertEquals(0, sb6.length());
        Assertions.assertEquals(0, sb6.size());
        final StrBuilder sb7 = new StrBuilder("foo");
        Assertions.assertEquals(35, sb7.capacity());
        Assertions.assertEquals(3, sb7.length());
        Assertions.assertEquals(3, sb7.size());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testChaining() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertSame(sb, sb.setNewLineText(null));
        Assertions.assertSame(sb, sb.setNullText(null));
        Assertions.assertSame(sb, sb.setLength(1));
        Assertions.assertSame(sb, sb.setCharAt(0, 'a'));
        Assertions.assertSame(sb, sb.ensureCapacity(0));
        Assertions.assertSame(sb, sb.minimizeCapacity());
        Assertions.assertSame(sb, sb.clear());
        Assertions.assertSame(sb, sb.reverse());
        Assertions.assertSame(sb, sb.trim());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReadFromReader() throws Exception {
        String s = "";
        for (int i = 0; i < 100; ++i) {
            final StrBuilder sb = new StrBuilder();
            final int len = sb.readFrom(new StringReader(s));
            Assertions.assertEquals(s.length(), len);
            Assertions.assertEquals(s, sb.toString());
            s += Integer.toString(i);
        }
    }

    @Test
    public void testReadFromReaderAppendsToEnd() throws Exception {
        final StrBuilder sb = new StrBuilder("Test");
        sb.readFrom(new StringReader(" 123"));
        Assertions.assertEquals("Test 123", sb.toString());
    }

    @Test
    public void testReadFromCharBuffer() throws Exception {
        String s = "";
        for (int i = 0; i < 100; ++i) {
            final StrBuilder sb = new StrBuilder();
            final int len = sb.readFrom(CharBuffer.wrap(s));
            Assertions.assertEquals(s.length(), len);
            Assertions.assertEquals(s, sb.toString());
            s += Integer.toString(i);
        }
    }

    @Test
    public void testReadFromCharBufferAppendsToEnd() throws Exception {
        final StrBuilder sb = new StrBuilder("Test");
        sb.readFrom(CharBuffer.wrap(" 123"));
        Assertions.assertEquals("Test 123", sb.toString());
    }

    @Test
    public void testReadFromReadable() throws Exception {
        String s = "";
        for (int i = 0; i < 100; ++i) {
            final StrBuilder sb = new StrBuilder();
            final int len = sb.readFrom(new StrBuilderTest.MockReadable(s));
            Assertions.assertEquals(s.length(), len);
            Assertions.assertEquals(s, sb.toString());
            s += Integer.toString(i);
        }
    }

    @Test
    public void testReadFromReadableAppendsToEnd() throws Exception {
        final StrBuilder sb = new StrBuilder("Test");
        sb.readFrom(new StrBuilderTest.MockReadable(" 123"));
        Assertions.assertEquals("Test 123", sb.toString());
    }

    private static class MockReadable implements Readable {
        private final CharBuffer src;

        MockReadable(final String src) {
            this.src = CharBuffer.wrap(src);
        }

        @Override
        public int read(final CharBuffer cb) throws IOException {
            return src.read(cb);
        }
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetSetNewLineText() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertNull(sb.getNewLineText());
        sb.setNewLineText("#");
        Assertions.assertEquals("#", sb.getNewLineText());
        sb.setNewLineText("");
        Assertions.assertEquals("", sb.getNewLineText());
        sb.setNewLineText(null);
        Assertions.assertNull(sb.getNewLineText());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testGetSetNullText() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertNull(sb.getNullText());
        sb.setNullText("null");
        Assertions.assertEquals("null", sb.getNullText());
        sb.setNullText("");
        Assertions.assertNull(sb.getNullText());
        sb.setNullText("NULL");
        Assertions.assertEquals("NULL", sb.getNullText());
        sb.setNullText(null);
        Assertions.assertNull(sb.getNullText());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCapacityAndLength() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals(32, sb.capacity());
        Assertions.assertEquals(0, sb.length());
        Assertions.assertEquals(0, sb.size());
        Assertions.assertTrue(sb.isEmpty());
        sb.minimizeCapacity();
        Assertions.assertEquals(0, sb.capacity());
        Assertions.assertEquals(0, sb.length());
        Assertions.assertEquals(0, sb.size());
        Assertions.assertTrue(sb.isEmpty());
        sb.ensureCapacity(32);
        Assertions.assertTrue(((sb.capacity()) >= 32));
        Assertions.assertEquals(0, sb.length());
        Assertions.assertEquals(0, sb.size());
        Assertions.assertTrue(sb.isEmpty());
        sb.append("foo");
        Assertions.assertTrue(((sb.capacity()) >= 32));
        Assertions.assertEquals(3, sb.length());
        Assertions.assertEquals(3, sb.size());
        Assertions.assertFalse(sb.isEmpty());
        sb.clear();
        Assertions.assertTrue(((sb.capacity()) >= 32));
        Assertions.assertEquals(0, sb.length());
        Assertions.assertEquals(0, sb.size());
        Assertions.assertTrue(sb.isEmpty());
        sb.append("123456789012345678901234567890123");
        Assertions.assertTrue(((sb.capacity()) > 32));
        Assertions.assertEquals(33, sb.length());
        Assertions.assertEquals(33, sb.size());
        Assertions.assertFalse(sb.isEmpty());
        sb.ensureCapacity(16);
        Assertions.assertTrue(((sb.capacity()) > 16));
        Assertions.assertEquals(33, sb.length());
        Assertions.assertEquals(33, sb.size());
        Assertions.assertFalse(sb.isEmpty());
        sb.minimizeCapacity();
        Assertions.assertEquals(33, sb.capacity());
        Assertions.assertEquals(33, sb.length());
        Assertions.assertEquals(33, sb.size());
        Assertions.assertFalse(sb.isEmpty());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.setLength((-1)), "setLength(-1) expected StringIndexOutOfBoundsException");
        sb.setLength(33);
        Assertions.assertEquals(33, sb.capacity());
        Assertions.assertEquals(33, sb.length());
        Assertions.assertEquals(33, sb.size());
        Assertions.assertFalse(sb.isEmpty());
        sb.setLength(16);
        Assertions.assertTrue(((sb.capacity()) >= 16));
        Assertions.assertEquals(16, sb.length());
        Assertions.assertEquals(16, sb.size());
        Assertions.assertEquals("1234567890123456", sb.toString());
        Assertions.assertFalse(sb.isEmpty());
        sb.setLength(32);
        Assertions.assertTrue(((sb.capacity()) >= 32));
        Assertions.assertEquals(32, sb.length());
        Assertions.assertEquals(32, sb.size());
        Assertions.assertEquals("1234567890123456\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000", sb.toString());
        Assertions.assertFalse(sb.isEmpty());
        sb.setLength(0);
        Assertions.assertTrue(((sb.capacity()) >= 32));
        Assertions.assertEquals(0, sb.length());
        Assertions.assertEquals(0, sb.size());
        Assertions.assertTrue(sb.isEmpty());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testLength() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals(0, sb.length());
        sb.append("Hello");
        Assertions.assertEquals(5, sb.length());
    }

    @Test
    public void testSetLength() {
        final StrBuilder sb = new StrBuilder();
        sb.append("Hello");
        sb.setLength(2);// shorten

        Assertions.assertEquals("He", sb.toString());
        sb.setLength(2);// no change

        Assertions.assertEquals("He", sb.toString());
        sb.setLength(3);// lengthen

        Assertions.assertEquals("He\u0000", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.setLength((-1)), "setLength(-1) expected StringIndexOutOfBoundsException");
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCapacity() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals(sb.buffer.length, sb.capacity());
        sb.append("HelloWorldHelloWorldHelloWorldHelloWorld");
        Assertions.assertEquals(sb.buffer.length, sb.capacity());
    }

    @Test
    public void testEnsureCapacity() {
        final StrBuilder sb = new StrBuilder();
        sb.ensureCapacity(2);
        Assertions.assertTrue(((sb.capacity()) >= 2));
        sb.ensureCapacity((-1));
        Assertions.assertTrue(((sb.capacity()) >= 0));
        sb.append("HelloWorld");
        sb.ensureCapacity(40);
        Assertions.assertTrue(((sb.capacity()) >= 40));
    }

    @Test
    public void testMinimizeCapacity() {
        final StrBuilder sb = new StrBuilder();
        sb.minimizeCapacity();
        Assertions.assertEquals(0, sb.capacity());
        sb.append("HelloWorld");
        sb.minimizeCapacity();
        Assertions.assertEquals(10, sb.capacity());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSize() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals(0, sb.size());
        sb.append("Hello");
        Assertions.assertEquals(5, sb.size());
    }

    @Test
    public void testIsEmpty() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertTrue(sb.isEmpty());
        sb.append("Hello");
        Assertions.assertFalse(sb.isEmpty());
        sb.clear();
        Assertions.assertTrue(sb.isEmpty());
    }

    @Test
    public void testClear() {
        final StrBuilder sb = new StrBuilder();
        sb.append("Hello");
        sb.clear();
        Assertions.assertEquals(0, sb.length());
        Assertions.assertTrue(((sb.buffer.length) >= 5));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCharAt() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.charAt(0), "charAt(0) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.charAt((-1)), "charAt(-1) expected IndexOutOfBoundsException");
        sb.append("foo");
        Assertions.assertEquals('f', sb.charAt(0));
        Assertions.assertEquals('o', sb.charAt(1));
        Assertions.assertEquals('o', sb.charAt(2));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.charAt((-1)), "charAt(-1) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.charAt(3), "charAt(3) expected IndexOutOfBoundsException");
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSetCharAt() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.setCharAt(0, 'f'), "setCharAt(0, ) expected IndexOutOfBoundsException");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.setCharAt((-1), 'f'), "setCharAt(-1, ) expected IndexOutOfBoundsException");
        sb.append("foo");
        sb.setCharAt(0, 'b');
        sb.setCharAt(1, 'a');
        sb.setCharAt(2, 'r');
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.setCharAt(3, '!'), "setCharAt(3, ) expected IndexOutOfBoundsException");
        Assertions.assertEquals("bar", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDeleteCharAt() {
        final StrBuilder sb = new StrBuilder("abc");
        sb.deleteCharAt(0);
        Assertions.assertEquals("bc", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.deleteCharAt(1000));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testToCharArray() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals(ArrayUtils.EMPTY_CHAR_ARRAY, sb.toCharArray());
        char[] a = sb.toCharArray();
        Assertions.assertNotNull(a, "toCharArray() result is null");
        Assertions.assertEquals(0, a.length, "toCharArray() result is too large");
        sb.append("junit");
        a = sb.toCharArray();
        Assertions.assertEquals(5, a.length, "toCharArray() result incorrect length");
        Assertions.assertArrayEquals("junit".toCharArray(), a, "toCharArray() result does not match");
    }

    @Test
    public void testToCharArrayIntInt() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals(ArrayUtils.EMPTY_CHAR_ARRAY, sb.toCharArray(0, 0));
        sb.append("junit");
        char[] a = sb.toCharArray(0, 20);// too large test

        Assertions.assertEquals(5, a.length, "toCharArray(int, int) result incorrect length");
        Assertions.assertArrayEquals("junit".toCharArray(), a, "toCharArray(int, int) result does not match");
        a = sb.toCharArray(0, 4);
        Assertions.assertEquals(4, a.length, "toCharArray(int, int) result incorrect length");
        Assertions.assertArrayEquals("juni".toCharArray(), a, "toCharArray(int, int) result does not match");
        a = sb.toCharArray(0, 4);
        Assertions.assertEquals(4, a.length, "toCharArray(int, int) result incorrect length");
        Assertions.assertArrayEquals("juni".toCharArray(), a, "toCharArray(int, int) result does not match");
        a = sb.toCharArray(0, 1);
        Assertions.assertNotNull(a, "toCharArray(int, int) result is null");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.toCharArray((-1), 5), "no string index out of bound on -1");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.toCharArray(6, 5), "no string index out of bound on -1");
    }

    @Test
    public void testGetChars() {
        final StrBuilder sb = new StrBuilder();
        char[] input = new char[10];
        char[] a = sb.getChars(input);
        Assertions.assertSame(input, a);
        Assertions.assertArrayEquals(new char[10], a);
        sb.append("junit");
        a = sb.getChars(input);
        Assertions.assertSame(input, a);
        Assertions.assertArrayEquals(new char[]{ 'j', 'u', 'n', 'i', 't', 0, 0, 0, 0, 0 }, a);
        a = sb.getChars(null);
        Assertions.assertNotSame(input, a);
        Assertions.assertEquals(5, a.length);
        Assertions.assertArrayEquals("junit".toCharArray(), a);
        input = new char[5];
        a = sb.getChars(input);
        Assertions.assertSame(input, a);
        input = new char[4];
        a = sb.getChars(input);
        Assertions.assertNotSame(input, a);
    }

    @Test
    public void testGetCharsIntIntCharArrayInt() {
        final StrBuilder sb = new StrBuilder();
        sb.append("junit");
        char[] a = new char[5];
        sb.getChars(0, 5, a, 0);
        Assertions.assertArrayEquals(new char[]{ 'j', 'u', 'n', 'i', 't' }, a);
        final char[] b = new char[5];
        sb.getChars(0, 2, b, 3);
        Assertions.assertArrayEquals(new char[]{ 0, 0, 0, 'j', 'u' }, b);
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.getChars((-1), 0, b, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.getChars(0, (-1), b, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.getChars(0, 20, b, 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.getChars(4, 2, b, 0));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDeleteIntInt() {
        StrBuilder sb = new StrBuilder("abc");
        sb.delete(0, 1);
        Assertions.assertEquals("bc", sb.toString());
        sb.delete(1, 2);
        Assertions.assertEquals("b", sb.toString());
        sb.delete(0, 1);
        Assertions.assertEquals("", sb.toString());
        sb.delete(0, 1000);
        Assertions.assertEquals("", sb.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.delete(1, 2));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.delete((-1), 1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> new StrBuilder("anything").delete(2, 1));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDeleteAll_char() {
        StrBuilder sb = new StrBuilder("abcbccba");
        sb.deleteAll('X');
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.deleteAll('a');
        Assertions.assertEquals("bcbccb", sb.toString());
        sb.deleteAll('c');
        Assertions.assertEquals("bbb", sb.toString());
        sb.deleteAll('b');
        Assertions.assertEquals("", sb.toString());
        sb = new StrBuilder("");
        sb.deleteAll('b');
        Assertions.assertEquals("", sb.toString());
    }

    @Test
    public void testDeleteFirst_char() {
        StrBuilder sb = new StrBuilder("abcba");
        sb.deleteFirst('X');
        Assertions.assertEquals("abcba", sb.toString());
        sb.deleteFirst('a');
        Assertions.assertEquals("bcba", sb.toString());
        sb.deleteFirst('c');
        Assertions.assertEquals("bba", sb.toString());
        sb.deleteFirst('b');
        Assertions.assertEquals("ba", sb.toString());
        sb = new StrBuilder("");
        sb.deleteFirst('b');
        Assertions.assertEquals("", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDeleteAll_String() {
        StrBuilder sb = new StrBuilder("abcbccba");
        sb.deleteAll(((String) (null)));
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.deleteAll("");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.deleteAll("X");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.deleteAll("a");
        Assertions.assertEquals("bcbccb", sb.toString());
        sb.deleteAll("c");
        Assertions.assertEquals("bbb", sb.toString());
        sb.deleteAll("b");
        Assertions.assertEquals("", sb.toString());
        sb = new StrBuilder("abcbccba");
        sb.deleteAll("bc");
        Assertions.assertEquals("acba", sb.toString());
        sb = new StrBuilder("");
        sb.deleteAll("bc");
        Assertions.assertEquals("", sb.toString());
    }

    @Test
    public void testDeleteFirst_String() {
        StrBuilder sb = new StrBuilder("abcbccba");
        sb.deleteFirst(((String) (null)));
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.deleteFirst("");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.deleteFirst("X");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.deleteFirst("a");
        Assertions.assertEquals("bcbccba", sb.toString());
        sb.deleteFirst("c");
        Assertions.assertEquals("bbccba", sb.toString());
        sb.deleteFirst("b");
        Assertions.assertEquals("bccba", sb.toString());
        sb = new StrBuilder("abcbccba");
        sb.deleteFirst("bc");
        Assertions.assertEquals("abccba", sb.toString());
        sb = new StrBuilder("");
        sb.deleteFirst("bc");
        Assertions.assertEquals("", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDeleteAll_StrMatcher() {
        StrBuilder sb = new StrBuilder("A0xA1A2yA3");
        sb.deleteAll(((StrMatcher) (null)));
        Assertions.assertEquals("A0xA1A2yA3", sb.toString());
        sb.deleteAll(StrBuilderTest.A_NUMBER_MATCHER);
        Assertions.assertEquals("xy", sb.toString());
        sb = new StrBuilder("Ax1");
        sb.deleteAll(StrBuilderTest.A_NUMBER_MATCHER);
        Assertions.assertEquals("Ax1", sb.toString());
        sb = new StrBuilder("");
        sb.deleteAll(StrBuilderTest.A_NUMBER_MATCHER);
        Assertions.assertEquals("", sb.toString());
    }

    @Test
    public void testDeleteFirst_StrMatcher() {
        StrBuilder sb = new StrBuilder("A0xA1A2yA3");
        sb.deleteFirst(((StrMatcher) (null)));
        Assertions.assertEquals("A0xA1A2yA3", sb.toString());
        sb.deleteFirst(StrBuilderTest.A_NUMBER_MATCHER);
        Assertions.assertEquals("xA1A2yA3", sb.toString());
        sb = new StrBuilder("Ax1");
        sb.deleteFirst(StrBuilderTest.A_NUMBER_MATCHER);
        Assertions.assertEquals("Ax1", sb.toString());
        sb = new StrBuilder("");
        sb.deleteFirst(StrBuilderTest.A_NUMBER_MATCHER);
        Assertions.assertEquals("", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReplace_int_int_String() {
        StrBuilder sb = new StrBuilder("abc");
        sb.replace(0, 1, "d");
        Assertions.assertEquals("dbc", sb.toString());
        sb.replace(0, 1, "aaa");
        Assertions.assertEquals("aaabc", sb.toString());
        sb.replace(0, 3, "");
        Assertions.assertEquals("bc", sb.toString());
        sb.replace(1, 2, null);
        Assertions.assertEquals("b", sb.toString());
        sb.replace(1, 1000, "text");
        Assertions.assertEquals("btext", sb.toString());
        sb.replace(0, 1000, "text");
        Assertions.assertEquals("text", sb.toString());
        StrBuilder sb1 = new StrBuilder("atext");
        sb1.replace(1, 1, "ny");
        Assertions.assertEquals("anytext", sb1.toString());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.replace(2, 1, "anything"));
        StrBuilder sb2 = new StrBuilder();
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb2.replace(1, 2, "anything"));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb2.replace((-1), 1, "anything"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReplaceAll_char_char() {
        final StrBuilder sb = new StrBuilder("abcbccba");
        sb.replaceAll('x', 'y');
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll('a', 'd');
        Assertions.assertEquals("dbcbccbd", sb.toString());
        sb.replaceAll('b', 'e');
        Assertions.assertEquals("dececced", sb.toString());
        sb.replaceAll('c', 'f');
        Assertions.assertEquals("defeffed", sb.toString());
        sb.replaceAll('d', 'd');
        Assertions.assertEquals("defeffed", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReplaceFirst_char_char() {
        final StrBuilder sb = new StrBuilder("abcbccba");
        sb.replaceFirst('x', 'y');
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst('a', 'd');
        Assertions.assertEquals("dbcbccba", sb.toString());
        sb.replaceFirst('b', 'e');
        Assertions.assertEquals("decbccba", sb.toString());
        sb.replaceFirst('c', 'f');
        Assertions.assertEquals("defbccba", sb.toString());
        sb.replaceFirst('d', 'd');
        Assertions.assertEquals("defbccba", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReplaceAll_String_String() {
        StrBuilder sb = new StrBuilder("abcbccba");
        sb.replaceAll(((String) (null)), null);
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll(((String) (null)), "anything");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll("", null);
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll("", "anything");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll("x", "y");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll("a", "d");
        Assertions.assertEquals("dbcbccbd", sb.toString());
        sb.replaceAll("d", null);
        Assertions.assertEquals("bcbccb", sb.toString());
        sb.replaceAll("cb", "-");
        Assertions.assertEquals("b-c-", sb.toString());
        sb = new StrBuilder("abcba");
        sb.replaceAll("b", "xbx");
        Assertions.assertEquals("axbxcxbxa", sb.toString());
        sb = new StrBuilder("bb");
        sb.replaceAll("b", "xbx");
        Assertions.assertEquals("xbxxbx", sb.toString());
    }

    @Test
    public void testReplaceFirst_String_String() {
        StrBuilder sb = new StrBuilder("abcbccba");
        sb.replaceFirst(((String) (null)), null);
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst(((String) (null)), "anything");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst("", null);
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst("", "anything");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst("x", "y");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst("a", "d");
        Assertions.assertEquals("dbcbccba", sb.toString());
        sb.replaceFirst("d", null);
        Assertions.assertEquals("bcbccba", sb.toString());
        sb.replaceFirst("cb", "-");
        Assertions.assertEquals("b-ccba", sb.toString());
        sb = new StrBuilder("abcba");
        sb.replaceFirst("b", "xbx");
        Assertions.assertEquals("axbxcba", sb.toString());
        sb = new StrBuilder("bb");
        sb.replaceFirst("b", "xbx");
        Assertions.assertEquals("xbxb", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReplaceAll_StrMatcher_String() {
        StrBuilder sb = new StrBuilder("abcbccba");
        sb.replaceAll(((StrMatcher) (null)), null);
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll(((StrMatcher) (null)), "anything");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll(StrMatcher.noneMatcher(), null);
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll(StrMatcher.noneMatcher(), "anything");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll(StrMatcher.charMatcher('x'), "y");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceAll(StrMatcher.charMatcher('a'), "d");
        Assertions.assertEquals("dbcbccbd", sb.toString());
        sb.replaceAll(StrMatcher.charMatcher('d'), null);
        Assertions.assertEquals("bcbccb", sb.toString());
        sb.replaceAll(StrMatcher.stringMatcher("cb"), "-");
        Assertions.assertEquals("b-c-", sb.toString());
        sb = new StrBuilder("abcba");
        sb.replaceAll(StrMatcher.charMatcher('b'), "xbx");
        Assertions.assertEquals("axbxcxbxa", sb.toString());
        sb = new StrBuilder("bb");
        sb.replaceAll(StrMatcher.charMatcher('b'), "xbx");
        Assertions.assertEquals("xbxxbx", sb.toString());
        sb = new StrBuilder("A1-A2A3-A4");
        sb.replaceAll(StrBuilderTest.A_NUMBER_MATCHER, "***");
        Assertions.assertEquals("***-******-***", sb.toString());
        sb = new StrBuilder("Dear X, hello X.");
        sb.replaceAll(StrMatcher.stringMatcher("X"), "012345678901234567");
        Assertions.assertEquals("Dear 012345678901234567, hello 012345678901234567.", sb.toString());
    }

    @Test
    public void testReplaceFirst_StrMatcher_String() {
        StrBuilder sb = new StrBuilder("abcbccba");
        sb.replaceFirst(((StrMatcher) (null)), null);
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst(((StrMatcher) (null)), "anything");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst(StrMatcher.noneMatcher(), null);
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst(StrMatcher.noneMatcher(), "anything");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst(StrMatcher.charMatcher('x'), "y");
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replaceFirst(StrMatcher.charMatcher('a'), "d");
        Assertions.assertEquals("dbcbccba", sb.toString());
        sb.replaceFirst(StrMatcher.charMatcher('d'), null);
        Assertions.assertEquals("bcbccba", sb.toString());
        sb.replaceFirst(StrMatcher.stringMatcher("cb"), "-");
        Assertions.assertEquals("b-ccba", sb.toString());
        sb = new StrBuilder("abcba");
        sb.replaceFirst(StrMatcher.charMatcher('b'), "xbx");
        Assertions.assertEquals("axbxcba", sb.toString());
        sb = new StrBuilder("bb");
        sb.replaceFirst(StrMatcher.charMatcher('b'), "xbx");
        Assertions.assertEquals("xbxb", sb.toString());
        sb = new StrBuilder("A1-A2A3-A4");
        sb.replaceFirst(StrBuilderTest.A_NUMBER_MATCHER, "***");
        Assertions.assertEquals("***-A2A3-A4", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReplace_StrMatcher_String_int_int_int_VaryMatcher() {
        StrBuilder sb = new StrBuilder("abcbccba");
        sb.replace(null, "x", 0, sb.length(), (-1));
        Assertions.assertEquals("abcbccba", sb.toString());
        sb.replace(StrMatcher.charMatcher('a'), "x", 0, sb.length(), (-1));
        Assertions.assertEquals("xbcbccbx", sb.toString());
        sb.replace(StrMatcher.stringMatcher("cb"), "x", 0, sb.length(), (-1));
        Assertions.assertEquals("xbxcxx", sb.toString());
        sb = new StrBuilder("A1-A2A3-A4");
        sb.replace(StrBuilderTest.A_NUMBER_MATCHER, "***", 0, sb.length(), (-1));
        Assertions.assertEquals("***-******-***", sb.toString());
        sb = new StrBuilder();
        sb.replace(StrBuilderTest.A_NUMBER_MATCHER, "***", 0, sb.length(), (-1));
        Assertions.assertEquals("", sb.toString());
    }

    @Test
    public void testReplace_StrMatcher_String_int_int_int_VaryReplace() {
        StrBuilder sb = new StrBuilder("abcbccba");
        sb.replace(StrMatcher.stringMatcher("cb"), "cb", 0, sb.length(), (-1));
        Assertions.assertEquals("abcbccba", sb.toString());
        sb = new StrBuilder("abcbccba");
        sb.replace(StrMatcher.stringMatcher("cb"), "-", 0, sb.length(), (-1));
        Assertions.assertEquals("ab-c-a", sb.toString());
        sb = new StrBuilder("abcbccba");
        sb.replace(StrMatcher.stringMatcher("cb"), "+++", 0, sb.length(), (-1));
        Assertions.assertEquals("ab+++c+++a", sb.toString());
        sb = new StrBuilder("abcbccba");
        sb.replace(StrMatcher.stringMatcher("cb"), "", 0, sb.length(), (-1));
        Assertions.assertEquals("abca", sb.toString());
        sb = new StrBuilder("abcbccba");
        sb.replace(StrMatcher.stringMatcher("cb"), null, 0, sb.length(), (-1));
        Assertions.assertEquals("abca", sb.toString());
    }

    @Test
    public void testReplace_StrMatcher_String_int_int_int_VaryStartIndex() {
        StrBuilder sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, sb.length(), (-1));
        Assertions.assertEquals("-x--y-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 1, sb.length(), (-1));
        Assertions.assertEquals("aax--y-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 2, sb.length(), (-1));
        Assertions.assertEquals("aax--y-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 3, sb.length(), (-1));
        Assertions.assertEquals("aax--y-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 4, sb.length(), (-1));
        Assertions.assertEquals("aaxa-ay-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 5, sb.length(), (-1));
        Assertions.assertEquals("aaxaa-y-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 6, sb.length(), (-1));
        Assertions.assertEquals("aaxaaaay-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 7, sb.length(), (-1));
        Assertions.assertEquals("aaxaaaay-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 8, sb.length(), (-1));
        Assertions.assertEquals("aaxaaaay-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 9, sb.length(), (-1));
        Assertions.assertEquals("aaxaaaayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 10, sb.length(), (-1));
        Assertions.assertEquals("aaxaaaayaa", sb.toString());
        StrBuilder sb1 = new StrBuilder("aaxaaaayaa");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.replace(StrMatcher.stringMatcher("aa"), "-", 11, sb1.length(), (-1)));
        Assertions.assertEquals("aaxaaaayaa", sb1.toString());
        StrBuilder sb2 = new StrBuilder("aaxaaaayaa");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb2.replace(StrMatcher.stringMatcher("aa"), "-", (-1), sb2.length(), (-1)));
        Assertions.assertEquals("aaxaaaayaa", sb2.toString());
    }

    @Test
    public void testReplace_StrMatcher_String_int_int_int_VaryEndIndex() {
        StrBuilder sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 0, (-1));
        Assertions.assertEquals("aaxaaaayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 2, (-1));
        Assertions.assertEquals("-xaaaayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 3, (-1));
        Assertions.assertEquals("-xaaaayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 4, (-1));
        Assertions.assertEquals("-xaaaayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 5, (-1));
        Assertions.assertEquals("-x-aayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 6, (-1));
        Assertions.assertEquals("-x-aayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 7, (-1));
        Assertions.assertEquals("-x--yaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 8, (-1));
        Assertions.assertEquals("-x--yaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 9, (-1));
        Assertions.assertEquals("-x--yaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 10, (-1));
        Assertions.assertEquals("-x--y-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 1000, (-1));
        Assertions.assertEquals("-x--y-", sb.toString());
        StrBuilder sb1 = new StrBuilder("aaxaaaayaa");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb1.replace(StrMatcher.stringMatcher("aa"), "-", 2, 1, (-1)));
        Assertions.assertEquals("aaxaaaayaa", sb1.toString());
    }

    @Test
    public void testReplace_StrMatcher_String_int_int_int_VaryCount() {
        StrBuilder sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 10, (-1));
        Assertions.assertEquals("-x--y-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 10, 0);
        Assertions.assertEquals("aaxaaaayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 10, 1);
        Assertions.assertEquals("-xaaaayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 10, 2);
        Assertions.assertEquals("-x-aayaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 10, 3);
        Assertions.assertEquals("-x--yaa", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 10, 4);
        Assertions.assertEquals("-x--y-", sb.toString());
        sb = new StrBuilder("aaxaaaayaa");
        sb.replace(StrMatcher.stringMatcher("aa"), "-", 0, 10, 5);
        Assertions.assertEquals("-x--y-", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testReverse() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals("", sb.reverse().toString());
        sb.clear().append(true);
        Assertions.assertEquals("eurt", sb.reverse().toString());
        Assertions.assertEquals("true", sb.reverse().toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testTrim() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals("", sb.reverse().toString());
        sb.clear().append(" \u0000 ");
        Assertions.assertEquals("", sb.trim().toString());
        sb.clear().append(" \u0000 a b c");
        Assertions.assertEquals("a b c", sb.trim().toString());
        sb.clear().append("a b c \u0000 ");
        Assertions.assertEquals("a b c", sb.trim().toString());
        sb.clear().append(" \u0000 a b c \u0000 ");
        Assertions.assertEquals("a b c", sb.trim().toString());
        sb.clear().append("a b c");
        Assertions.assertEquals("a b c", sb.trim().toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testStartsWith() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertFalse(sb.startsWith("a"));
        Assertions.assertFalse(sb.startsWith(null));
        Assertions.assertTrue(sb.startsWith(""));
        sb.append("abc");
        Assertions.assertTrue(sb.startsWith("a"));
        Assertions.assertTrue(sb.startsWith("ab"));
        Assertions.assertTrue(sb.startsWith("abc"));
        Assertions.assertFalse(sb.startsWith("cba"));
    }

    @Test
    public void testEndsWith() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertFalse(sb.endsWith("a"));
        Assertions.assertFalse(sb.endsWith("c"));
        Assertions.assertTrue(sb.endsWith(""));
        Assertions.assertFalse(sb.endsWith(null));
        sb.append("abc");
        Assertions.assertTrue(sb.endsWith("c"));
        Assertions.assertTrue(sb.endsWith("bc"));
        Assertions.assertTrue(sb.endsWith("abc"));
        Assertions.assertFalse(sb.endsWith("cba"));
        Assertions.assertFalse(sb.endsWith("abcd"));
        Assertions.assertFalse(sb.endsWith(" abc"));
        Assertions.assertFalse(sb.endsWith("abc "));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSubSequenceIntInt() {
        final StrBuilder sb = new StrBuilder("hello goodbye");
        // Start index is negative
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.subSequence((-1), 5));
        // End index is negative
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.subSequence(2, (-1)));
        // End index greater than length()
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.subSequence(2, ((sb.length()) + 1)));
        // Start index greater then end index
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.subSequence(3, 2));
        // Normal cases
        Assertions.assertEquals("hello", sb.subSequence(0, 5));
        Assertions.assertEquals("hello goodbye".subSequence(0, 6), sb.subSequence(0, 6));
        Assertions.assertEquals("goodbye", sb.subSequence(6, 13));
        Assertions.assertEquals("hello goodbye".subSequence(6, 13), sb.subSequence(6, 13));
    }

    @Test
    public void testSubstringInt() {
        final StrBuilder sb = new StrBuilder("hello goodbye");
        Assertions.assertEquals("goodbye", sb.substring(6));
        Assertions.assertEquals("hello goodbye".substring(6), sb.substring(6));
        Assertions.assertEquals("hello goodbye", sb.substring(0));
        Assertions.assertEquals("hello goodbye".substring(0), sb.substring(0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.substring((-1)));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.substring(15));
    }

    @Test
    public void testSubstringIntInt() {
        final StrBuilder sb = new StrBuilder("hello goodbye");
        Assertions.assertEquals("hello", sb.substring(0, 5));
        Assertions.assertEquals("hello goodbye".substring(0, 6), sb.substring(0, 6));
        Assertions.assertEquals("goodbye", sb.substring(6, 13));
        Assertions.assertEquals("hello goodbye".substring(6, 13), sb.substring(6, 13));
        Assertions.assertEquals("goodbye", sb.substring(6, 20));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.substring((-1), 5));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> sb.substring(15, 20));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testMidString() {
        final StrBuilder sb = new StrBuilder("hello goodbye hello");
        Assertions.assertEquals("goodbye", sb.midString(6, 7));
        Assertions.assertEquals("hello", sb.midString(0, 5));
        Assertions.assertEquals("hello", sb.midString((-5), 5));
        Assertions.assertEquals("", sb.midString(0, (-1)));
        Assertions.assertEquals("", sb.midString(20, 2));
        Assertions.assertEquals("hello", sb.midString(14, 22));
    }

    @Test
    public void testRightString() {
        final StrBuilder sb = new StrBuilder("left right");
        Assertions.assertEquals("right", sb.rightString(5));
        Assertions.assertEquals("", sb.rightString(0));
        Assertions.assertEquals("", sb.rightString((-5)));
        Assertions.assertEquals("left right", sb.rightString(15));
    }

    @Test
    public void testLeftString() {
        final StrBuilder sb = new StrBuilder("left right");
        Assertions.assertEquals("left", sb.leftString(4));
        Assertions.assertEquals("", sb.leftString(0));
        Assertions.assertEquals("", sb.leftString((-5)));
        Assertions.assertEquals("left right", sb.leftString(15));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testContains_char() {
        final StrBuilder sb = new StrBuilder("abcdefghijklmnopqrstuvwxyz");
        Assertions.assertTrue(sb.contains('a'));
        Assertions.assertTrue(sb.contains('o'));
        Assertions.assertTrue(sb.contains('z'));
        Assertions.assertFalse(sb.contains('1'));
    }

    @Test
    public void testContains_String() {
        final StrBuilder sb = new StrBuilder("abcdefghijklmnopqrstuvwxyz");
        Assertions.assertTrue(sb.contains("a"));
        Assertions.assertTrue(sb.contains("pq"));
        Assertions.assertTrue(sb.contains("z"));
        Assertions.assertFalse(sb.contains("zyx"));
        Assertions.assertFalse(sb.contains(((String) (null))));
    }

    @Test
    public void testContains_StrMatcher() {
        StrBuilder sb = new StrBuilder("abcdefghijklmnopqrstuvwxyz");
        Assertions.assertTrue(sb.contains(StrMatcher.charMatcher('a')));
        Assertions.assertTrue(sb.contains(StrMatcher.stringMatcher("pq")));
        Assertions.assertTrue(sb.contains(StrMatcher.charMatcher('z')));
        Assertions.assertFalse(sb.contains(StrMatcher.stringMatcher("zy")));
        Assertions.assertFalse(sb.contains(((StrMatcher) (null))));
        sb = new StrBuilder();
        Assertions.assertFalse(sb.contains(StrBuilderTest.A_NUMBER_MATCHER));
        sb.append("B A1 C");
        Assertions.assertTrue(sb.contains(StrBuilderTest.A_NUMBER_MATCHER));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOf_char() {
        final StrBuilder sb = new StrBuilder("abab");
        Assertions.assertEquals(0, sb.indexOf('a'));
        // should work like String#indexOf
        Assertions.assertEquals("abab".indexOf('a'), sb.indexOf('a'));
        Assertions.assertEquals(1, sb.indexOf('b'));
        Assertions.assertEquals("abab".indexOf('b'), sb.indexOf('b'));
        Assertions.assertEquals((-1), sb.indexOf('z'));
    }

    @Test
    public void testIndexOf_char_int() {
        StrBuilder sb = new StrBuilder("abab");
        Assertions.assertEquals(0, sb.indexOf('a', (-1)));
        Assertions.assertEquals(0, sb.indexOf('a', 0));
        Assertions.assertEquals(2, sb.indexOf('a', 1));
        Assertions.assertEquals((-1), sb.indexOf('a', 4));
        Assertions.assertEquals((-1), sb.indexOf('a', 5));
        // should work like String#indexOf
        Assertions.assertEquals("abab".indexOf('a', 1), sb.indexOf('a', 1));
        Assertions.assertEquals(3, sb.indexOf('b', 2));
        Assertions.assertEquals("abab".indexOf('b', 2), sb.indexOf('b', 2));
        Assertions.assertEquals((-1), sb.indexOf('z', 2));
        sb = new StrBuilder("xyzabc");
        Assertions.assertEquals(2, sb.indexOf('z', 0));
        Assertions.assertEquals((-1), sb.indexOf('z', 3));
    }

    @Test
    public void testLastIndexOf_char() {
        final StrBuilder sb = new StrBuilder("abab");
        Assertions.assertEquals(2, sb.lastIndexOf('a'));
        // should work like String#lastIndexOf
        Assertions.assertEquals("abab".lastIndexOf('a'), sb.lastIndexOf('a'));
        Assertions.assertEquals(3, sb.lastIndexOf('b'));
        Assertions.assertEquals("abab".lastIndexOf('b'), sb.lastIndexOf('b'));
        Assertions.assertEquals((-1), sb.lastIndexOf('z'));
    }

    @Test
    public void testLastIndexOf_char_int() {
        StrBuilder sb = new StrBuilder("abab");
        Assertions.assertEquals((-1), sb.lastIndexOf('a', (-1)));
        Assertions.assertEquals(0, sb.lastIndexOf('a', 0));
        Assertions.assertEquals(0, sb.lastIndexOf('a', 1));
        // should work like String#lastIndexOf
        Assertions.assertEquals("abab".lastIndexOf('a', 1), sb.lastIndexOf('a', 1));
        Assertions.assertEquals(1, sb.lastIndexOf('b', 2));
        Assertions.assertEquals("abab".lastIndexOf('b', 2), sb.lastIndexOf('b', 2));
        Assertions.assertEquals((-1), sb.lastIndexOf('z', 2));
        sb = new StrBuilder("xyzabc");
        Assertions.assertEquals(2, sb.lastIndexOf('z', sb.length()));
        Assertions.assertEquals((-1), sb.lastIndexOf('z', 1));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOf_String() {
        final StrBuilder sb = new StrBuilder("abab");
        Assertions.assertEquals(0, sb.indexOf("a"));
        // should work like String#indexOf
        Assertions.assertEquals("abab".indexOf("a"), sb.indexOf("a"));
        Assertions.assertEquals(0, sb.indexOf("ab"));
        // should work like String#indexOf
        Assertions.assertEquals("abab".indexOf("ab"), sb.indexOf("ab"));
        Assertions.assertEquals(1, sb.indexOf("b"));
        Assertions.assertEquals("abab".indexOf("b"), sb.indexOf("b"));
        Assertions.assertEquals(1, sb.indexOf("ba"));
        Assertions.assertEquals("abab".indexOf("ba"), sb.indexOf("ba"));
        Assertions.assertEquals((-1), sb.indexOf("z"));
        Assertions.assertEquals((-1), sb.indexOf(((String) (null))));
    }

    @Test
    public void testIndexOf_String_int() {
        StrBuilder sb = new StrBuilder("abab");
        Assertions.assertEquals(0, sb.indexOf("a", (-1)));
        Assertions.assertEquals(0, sb.indexOf("a", 0));
        Assertions.assertEquals(2, sb.indexOf("a", 1));
        Assertions.assertEquals(2, sb.indexOf("a", 2));
        Assertions.assertEquals((-1), sb.indexOf("a", 3));
        Assertions.assertEquals((-1), sb.indexOf("a", 4));
        Assertions.assertEquals((-1), sb.indexOf("a", 5));
        Assertions.assertEquals((-1), sb.indexOf("abcdef", 0));
        Assertions.assertEquals(0, sb.indexOf("", 0));
        Assertions.assertEquals(1, sb.indexOf("", 1));
        // should work like String#indexOf
        Assertions.assertEquals("abab".indexOf("a", 1), sb.indexOf("a", 1));
        Assertions.assertEquals(2, sb.indexOf("ab", 1));
        // should work like String#indexOf
        Assertions.assertEquals("abab".indexOf("ab", 1), sb.indexOf("ab", 1));
        Assertions.assertEquals(3, sb.indexOf("b", 2));
        Assertions.assertEquals("abab".indexOf("b", 2), sb.indexOf("b", 2));
        Assertions.assertEquals(1, sb.indexOf("ba", 1));
        Assertions.assertEquals("abab".indexOf("ba", 2), sb.indexOf("ba", 2));
        Assertions.assertEquals((-1), sb.indexOf("z", 2));
        sb = new StrBuilder("xyzabc");
        Assertions.assertEquals(2, sb.indexOf("za", 0));
        Assertions.assertEquals((-1), sb.indexOf("za", 3));
        Assertions.assertEquals((-1), sb.indexOf(((String) (null)), 2));
    }

    @Test
    public void testLastIndexOf_String() {
        final StrBuilder sb = new StrBuilder("abab");
        Assertions.assertEquals(2, sb.lastIndexOf("a"));
        // should work like String#lastIndexOf
        Assertions.assertEquals("abab".lastIndexOf("a"), sb.lastIndexOf("a"));
        Assertions.assertEquals(2, sb.lastIndexOf("ab"));
        // should work like String#lastIndexOf
        Assertions.assertEquals("abab".lastIndexOf("ab"), sb.lastIndexOf("ab"));
        Assertions.assertEquals(3, sb.lastIndexOf("b"));
        Assertions.assertEquals("abab".lastIndexOf("b"), sb.lastIndexOf("b"));
        Assertions.assertEquals(1, sb.lastIndexOf("ba"));
        Assertions.assertEquals("abab".lastIndexOf("ba"), sb.lastIndexOf("ba"));
        Assertions.assertEquals((-1), sb.lastIndexOf("z"));
        Assertions.assertEquals((-1), sb.lastIndexOf(((String) (null))));
    }

    @Test
    public void testLastIndexOf_String_int() {
        StrBuilder sb = new StrBuilder("abab");
        Assertions.assertEquals((-1), sb.lastIndexOf("a", (-1)));
        Assertions.assertEquals(0, sb.lastIndexOf("a", 0));
        Assertions.assertEquals(0, sb.lastIndexOf("a", 1));
        Assertions.assertEquals(2, sb.lastIndexOf("a", 2));
        Assertions.assertEquals(2, sb.lastIndexOf("a", 3));
        Assertions.assertEquals(2, sb.lastIndexOf("a", 4));
        Assertions.assertEquals(2, sb.lastIndexOf("a", 5));
        Assertions.assertEquals((-1), sb.lastIndexOf("abcdef", 3));
        Assertions.assertEquals("abab".lastIndexOf("", 3), sb.lastIndexOf("", 3));
        Assertions.assertEquals("abab".lastIndexOf("", 1), sb.lastIndexOf("", 1));
        // should work like String#lastIndexOf
        Assertions.assertEquals("abab".lastIndexOf("a", 1), sb.lastIndexOf("a", 1));
        Assertions.assertEquals(0, sb.lastIndexOf("ab", 1));
        // should work like String#lastIndexOf
        Assertions.assertEquals("abab".lastIndexOf("ab", 1), sb.lastIndexOf("ab", 1));
        Assertions.assertEquals(1, sb.lastIndexOf("b", 2));
        Assertions.assertEquals("abab".lastIndexOf("b", 2), sb.lastIndexOf("b", 2));
        Assertions.assertEquals(1, sb.lastIndexOf("ba", 2));
        Assertions.assertEquals("abab".lastIndexOf("ba", 2), sb.lastIndexOf("ba", 2));
        Assertions.assertEquals((-1), sb.lastIndexOf("z", 2));
        sb = new StrBuilder("xyzabc");
        Assertions.assertEquals(2, sb.lastIndexOf("za", sb.length()));
        Assertions.assertEquals((-1), sb.lastIndexOf("za", 1));
        Assertions.assertEquals((-1), sb.lastIndexOf(((String) (null)), 2));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOf_StrMatcher() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals((-1), sb.indexOf(((StrMatcher) (null))));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.charMatcher('a')));
        sb.append("ab bd");
        Assertions.assertEquals(0, sb.indexOf(StrMatcher.charMatcher('a')));
        Assertions.assertEquals(1, sb.indexOf(StrMatcher.charMatcher('b')));
        Assertions.assertEquals(2, sb.indexOf(StrMatcher.spaceMatcher()));
        Assertions.assertEquals(4, sb.indexOf(StrMatcher.charMatcher('d')));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.noneMatcher()));
        Assertions.assertEquals((-1), sb.indexOf(((StrMatcher) (null))));
        sb.append(" A1 junction");
        Assertions.assertEquals(6, sb.indexOf(StrBuilderTest.A_NUMBER_MATCHER));
    }

    @Test
    public void testIndexOf_StrMatcher_int() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals((-1), sb.indexOf(((StrMatcher) (null)), 2));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.charMatcher('a'), 2));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.charMatcher('a'), 0));
        sb.append("ab bd");
        Assertions.assertEquals(0, sb.indexOf(StrMatcher.charMatcher('a'), (-2)));
        Assertions.assertEquals(0, sb.indexOf(StrMatcher.charMatcher('a'), 0));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.charMatcher('a'), 2));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.charMatcher('a'), 20));
        Assertions.assertEquals(1, sb.indexOf(StrMatcher.charMatcher('b'), (-1)));
        Assertions.assertEquals(1, sb.indexOf(StrMatcher.charMatcher('b'), 0));
        Assertions.assertEquals(1, sb.indexOf(StrMatcher.charMatcher('b'), 1));
        Assertions.assertEquals(3, sb.indexOf(StrMatcher.charMatcher('b'), 2));
        Assertions.assertEquals(3, sb.indexOf(StrMatcher.charMatcher('b'), 3));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.charMatcher('b'), 4));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.charMatcher('b'), 5));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.charMatcher('b'), 6));
        Assertions.assertEquals(2, sb.indexOf(StrMatcher.spaceMatcher(), (-2)));
        Assertions.assertEquals(2, sb.indexOf(StrMatcher.spaceMatcher(), 0));
        Assertions.assertEquals(2, sb.indexOf(StrMatcher.spaceMatcher(), 2));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.spaceMatcher(), 4));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.spaceMatcher(), 20));
        Assertions.assertEquals((-1), sb.indexOf(StrMatcher.noneMatcher(), 0));
        Assertions.assertEquals((-1), sb.indexOf(((StrMatcher) (null)), 0));
        sb.append(" A1 junction with A2");
        Assertions.assertEquals(6, sb.indexOf(StrBuilderTest.A_NUMBER_MATCHER, 5));
        Assertions.assertEquals(6, sb.indexOf(StrBuilderTest.A_NUMBER_MATCHER, 6));
        Assertions.assertEquals(23, sb.indexOf(StrBuilderTest.A_NUMBER_MATCHER, 7));
        Assertions.assertEquals(23, sb.indexOf(StrBuilderTest.A_NUMBER_MATCHER, 22));
        Assertions.assertEquals(23, sb.indexOf(StrBuilderTest.A_NUMBER_MATCHER, 23));
        Assertions.assertEquals((-1), sb.indexOf(StrBuilderTest.A_NUMBER_MATCHER, 24));
    }

    @Test
    public void testLastIndexOf_StrMatcher() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals((-1), sb.lastIndexOf(((StrMatcher) (null))));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.charMatcher('a')));
        sb.append("ab bd");
        Assertions.assertEquals(0, sb.lastIndexOf(StrMatcher.charMatcher('a')));
        Assertions.assertEquals(3, sb.lastIndexOf(StrMatcher.charMatcher('b')));
        Assertions.assertEquals(2, sb.lastIndexOf(StrMatcher.spaceMatcher()));
        Assertions.assertEquals(4, sb.lastIndexOf(StrMatcher.charMatcher('d')));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.noneMatcher()));
        Assertions.assertEquals((-1), sb.lastIndexOf(((StrMatcher) (null))));
        sb.append(" A1 junction");
        Assertions.assertEquals(6, sb.lastIndexOf(StrBuilderTest.A_NUMBER_MATCHER));
    }

    @Test
    public void testLastIndexOf_StrMatcher_int() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals((-1), sb.lastIndexOf(((StrMatcher) (null)), 2));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.charMatcher('a'), 2));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.charMatcher('a'), 0));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.charMatcher('a'), (-1)));
        sb.append("ab bd");
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.charMatcher('a'), (-2)));
        Assertions.assertEquals(0, sb.lastIndexOf(StrMatcher.charMatcher('a'), 0));
        Assertions.assertEquals(0, sb.lastIndexOf(StrMatcher.charMatcher('a'), 2));
        Assertions.assertEquals(0, sb.lastIndexOf(StrMatcher.charMatcher('a'), 20));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.charMatcher('b'), (-1)));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.charMatcher('b'), 0));
        Assertions.assertEquals(1, sb.lastIndexOf(StrMatcher.charMatcher('b'), 1));
        Assertions.assertEquals(1, sb.lastIndexOf(StrMatcher.charMatcher('b'), 2));
        Assertions.assertEquals(3, sb.lastIndexOf(StrMatcher.charMatcher('b'), 3));
        Assertions.assertEquals(3, sb.lastIndexOf(StrMatcher.charMatcher('b'), 4));
        Assertions.assertEquals(3, sb.lastIndexOf(StrMatcher.charMatcher('b'), 5));
        Assertions.assertEquals(3, sb.lastIndexOf(StrMatcher.charMatcher('b'), 6));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.spaceMatcher(), (-2)));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.spaceMatcher(), 0));
        Assertions.assertEquals(2, sb.lastIndexOf(StrMatcher.spaceMatcher(), 2));
        Assertions.assertEquals(2, sb.lastIndexOf(StrMatcher.spaceMatcher(), 4));
        Assertions.assertEquals(2, sb.lastIndexOf(StrMatcher.spaceMatcher(), 20));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrMatcher.noneMatcher(), 0));
        Assertions.assertEquals((-1), sb.lastIndexOf(((StrMatcher) (null)), 0));
        sb.append(" A1 junction with A2");
        Assertions.assertEquals((-1), sb.lastIndexOf(StrBuilderTest.A_NUMBER_MATCHER, 5));
        Assertions.assertEquals((-1), sb.lastIndexOf(StrBuilderTest.A_NUMBER_MATCHER, 6));// A matches, 1 is outside bounds

        Assertions.assertEquals(6, sb.lastIndexOf(StrBuilderTest.A_NUMBER_MATCHER, 7));
        Assertions.assertEquals(6, sb.lastIndexOf(StrBuilderTest.A_NUMBER_MATCHER, 22));
        Assertions.assertEquals(6, sb.lastIndexOf(StrBuilderTest.A_NUMBER_MATCHER, 23));// A matches, 2 is outside bounds

        Assertions.assertEquals(23, sb.lastIndexOf(StrBuilderTest.A_NUMBER_MATCHER, 24));
    }

    static final StrMatcher A_NUMBER_MATCHER = new StrMatcher() {
        @Override
        public int isMatch(final char[] buffer, int pos, final int bufferStart, final int bufferEnd) {
            if ((buffer[pos]) == 'A') {
                pos++;
                if (((pos < bufferEnd) && ((buffer[pos]) >= '0')) && ((buffer[pos]) <= '9')) {
                    return 2;
                }
            }
            return 0;
        }
    };

    // -----------------------------------------------------------------------
    @Test
    public void testAsTokenizer() {
        // from Javadoc
        final StrBuilder b = new StrBuilder();
        b.append("a b ");
        final StrTokenizer t = b.asTokenizer();
        final String[] tokens1 = t.getTokenArray();
        Assertions.assertEquals(2, tokens1.length);
        Assertions.assertEquals("a", tokens1[0]);
        Assertions.assertEquals("b", tokens1[1]);
        Assertions.assertEquals(2, t.size());
        b.append("c d ");
        final String[] tokens2 = t.getTokenArray();
        Assertions.assertEquals(2, tokens2.length);
        Assertions.assertEquals("a", tokens2[0]);
        Assertions.assertEquals("b", tokens2[1]);
        Assertions.assertEquals(2, t.size());
        Assertions.assertEquals("a", t.next());
        Assertions.assertEquals("b", t.next());
        t.reset();
        final String[] tokens3 = t.getTokenArray();
        Assertions.assertEquals(4, tokens3.length);
        Assertions.assertEquals("a", tokens3[0]);
        Assertions.assertEquals("b", tokens3[1]);
        Assertions.assertEquals("c", tokens3[2]);
        Assertions.assertEquals("d", tokens3[3]);
        Assertions.assertEquals(4, t.size());
        Assertions.assertEquals("a", t.next());
        Assertions.assertEquals("b", t.next());
        Assertions.assertEquals("c", t.next());
        Assertions.assertEquals("d", t.next());
        Assertions.assertEquals("a b c d ", t.getContent());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAsReader() throws Exception {
        final StrBuilder sb = new StrBuilder("some text");
        Reader reader = sb.asReader();
        Assertions.assertTrue(reader.ready());
        final char[] buf = new char[40];
        Assertions.assertEquals(9, reader.read(buf));
        Assertions.assertEquals("some text", new String(buf, 0, 9));
        Assertions.assertEquals((-1), reader.read());
        Assertions.assertFalse(reader.ready());
        Assertions.assertEquals(0, reader.skip(2));
        Assertions.assertEquals(0, reader.skip((-1)));
        Assertions.assertTrue(reader.markSupported());
        reader = sb.asReader();
        Assertions.assertEquals('s', reader.read());
        reader.mark((-1));
        char[] array = new char[3];
        Assertions.assertEquals(3, reader.read(array, 0, 3));
        Assertions.assertEquals('o', array[0]);
        Assertions.assertEquals('m', array[1]);
        Assertions.assertEquals('e', array[2]);
        reader.reset();
        Assertions.assertEquals(1, reader.read(array, 1, 1));
        Assertions.assertEquals('o', array[0]);
        Assertions.assertEquals('o', array[1]);
        Assertions.assertEquals('e', array[2]);
        Assertions.assertEquals(2, reader.skip(2));
        Assertions.assertEquals(' ', reader.read());
        Assertions.assertTrue(reader.ready());
        reader.close();
        Assertions.assertTrue(reader.ready());
        final Reader r = sb.asReader();
        final char[] arr = new char[3];
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> r.read(arr, (-1), 0));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> r.read(arr, 0, (-1)));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> r.read(arr, 100, 1));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> r.read(arr, 0, 100));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> r.read(arr, Integer.MAX_VALUE, Integer.MAX_VALUE));
        Assertions.assertEquals(0, r.read(arr, 0, 0));
        Assertions.assertEquals(0, arr[0]);
        Assertions.assertEquals(0, arr[1]);
        Assertions.assertEquals(0, arr[2]);
        r.skip(9);
        Assertions.assertEquals((-1), r.read(arr, 0, 1));
        r.reset();
        array = new char[30];
        Assertions.assertEquals(9, r.read(array, 0, 30));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAsWriter() throws Exception {
        final StrBuilder sb = new StrBuilder("base");
        final Writer writer = sb.asWriter();
        writer.write('l');
        Assertions.assertEquals("basel", sb.toString());
        writer.write(new char[]{ 'i', 'n' });
        Assertions.assertEquals("baselin", sb.toString());
        writer.write(new char[]{ 'n', 'e', 'r' }, 1, 2);
        Assertions.assertEquals("baseliner", sb.toString());
        writer.write(" rout");
        Assertions.assertEquals("baseliner rout", sb.toString());
        writer.write("ping that server", 1, 3);
        Assertions.assertEquals("baseliner routing", sb.toString());
        writer.flush();// no effect

        Assertions.assertEquals("baseliner routing", sb.toString());
        writer.close();// no effect

        Assertions.assertEquals("baseliner routing", sb.toString());
        writer.write(" hi");// works after close

        Assertions.assertEquals("baseliner routing hi", sb.toString());
        sb.setLength(4);// mix and match

        writer.write('d');
        Assertions.assertEquals("based", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testEqualsIgnoreCase() {
        final StrBuilder sb1 = new StrBuilder();
        final StrBuilder sb2 = new StrBuilder();
        Assertions.assertTrue(sb1.equalsIgnoreCase(sb1));
        Assertions.assertTrue(sb1.equalsIgnoreCase(sb2));
        Assertions.assertTrue(sb2.equalsIgnoreCase(sb2));
        sb1.append("abc");
        Assertions.assertFalse(sb1.equalsIgnoreCase(sb2));
        sb2.append("ABC");
        Assertions.assertTrue(sb1.equalsIgnoreCase(sb2));
        sb2.clear().append("abc");
        Assertions.assertTrue(sb1.equalsIgnoreCase(sb2));
        Assertions.assertTrue(sb1.equalsIgnoreCase(sb1));
        Assertions.assertTrue(sb2.equalsIgnoreCase(sb2));
        sb2.clear().append("aBc");
        Assertions.assertTrue(sb1.equalsIgnoreCase(sb2));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testEquals() {
        final StrBuilder sb1 = new StrBuilder();
        final StrBuilder sb2 = new StrBuilder();
        Assertions.assertTrue(sb1.equals(sb2));
        Assertions.assertTrue(sb1.equals(sb1));
        Assertions.assertTrue(sb2.equals(sb2));
        Assertions.assertEquals(sb1, ((Object) (sb2)));
        sb1.append("abc");
        Assertions.assertFalse(sb1.equals(sb2));
        Assertions.assertNotEquals(sb1, ((Object) (sb2)));
        sb2.append("ABC");
        Assertions.assertFalse(sb1.equals(sb2));
        Assertions.assertNotEquals(sb1, ((Object) (sb2)));
        sb2.clear().append("abc");
        Assertions.assertTrue(sb1.equals(sb2));
        Assertions.assertEquals(sb1, ((Object) (sb2)));
        Assertions.assertNotEquals(sb1, Integer.valueOf(1));
        Assertions.assertNotEquals("abc", sb1);
    }

    @Test
    public void test_LANG_1131_EqualsWithNullStrBuilder() {
        final StrBuilder sb = new StrBuilder();
        final StrBuilder other = null;
        Assertions.assertFalse(sb.equals(other));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testHashCode() {
        final StrBuilder sb = new StrBuilder();
        final int hc1a = sb.hashCode();
        final int hc1b = sb.hashCode();
        Assertions.assertEquals(0, hc1a);
        Assertions.assertEquals(hc1a, hc1b);
        sb.append("abc");
        final int hc2a = sb.hashCode();
        final int hc2b = sb.hashCode();
        Assertions.assertTrue((hc2a != 0));
        Assertions.assertEquals(hc2a, hc2b);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testToString() {
        final StrBuilder sb = new StrBuilder("abc");
        Assertions.assertEquals("abc", sb.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testToStringBuffer() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals(new StringBuffer().toString(), sb.toStringBuffer().toString());
        sb.append("junit");
        Assertions.assertEquals(new StringBuffer("junit").toString(), sb.toStringBuffer().toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testToStringBuilder() {
        final StrBuilder sb = new StrBuilder();
        Assertions.assertEquals(new StringBuilder().toString(), sb.toStringBuilder().toString());
        sb.append("junit");
        Assertions.assertEquals(new StringBuilder("junit").toString(), sb.toStringBuilder().toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testLang294() {
        final StrBuilder sb = new StrBuilder("\n%BLAH%\nDo more stuff\neven more stuff\n%BLAH%\n");
        sb.deleteAll("\n%BLAH%");
        Assertions.assertEquals("\nDo more stuff\neven more stuff\n", sb.toString());
    }

    @Test
    public void testIndexOfLang294() {
        final StrBuilder sb = new StrBuilder("onetwothree");
        sb.deleteFirst("three");
        Assertions.assertEquals((-1), sb.indexOf("three"));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testLang295() {
        final StrBuilder sb = new StrBuilder("onetwothree");
        sb.deleteFirst("three");
        Assertions.assertFalse(sb.contains('h'), "The contains(char) method is looking beyond the end of the string");
        Assertions.assertEquals((-1), sb.indexOf('h'), "The indexOf(char) method is looking beyond the end of the string");
    }

    // -----------------------------------------------------------------------
    @Test
    public void testLang412Right() {
        final StrBuilder sb = new StrBuilder();
        sb.appendFixedWidthPadRight(null, 10, '*');
        Assertions.assertEquals("**********", sb.toString(), "Failed to invoke appendFixedWidthPadRight correctly");
    }

    @Test
    public void testLang412Left() {
        final StrBuilder sb = new StrBuilder();
        sb.appendFixedWidthPadLeft(null, 10, '*');
        Assertions.assertEquals("**********", sb.toString(), "Failed to invoke appendFixedWidthPadLeft correctly");
    }

    @Test
    public void testAsBuilder() {
        final StrBuilder sb = new StrBuilder().appendAll("Lorem", " ", "ipsum", " ", "dolor");
        Assertions.assertEquals(sb.toString(), sb.build());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendCharBuffer() {
        final StrBuilder sb1 = new StrBuilder();
        final CharBuffer buf = CharBuffer.allocate(10);
        buf.append("0123456789");
        buf.flip();
        sb1.append(buf);
        Assertions.assertEquals("0123456789", sb1.toString());
        final StrBuilder sb2 = new StrBuilder();
        sb2.append(buf, 1, 8);
        Assertions.assertEquals("12345678", sb2.toString());
    }

    // -----------------------------------------------------------------------
    @Test
    public void testAppendToWriter() throws Exception {
        final StrBuilder sb = new StrBuilder("1234567890");
        final StringWriter writer = new StringWriter();
        writer.append("Test ");
        sb.appendTo(writer);
        Assertions.assertEquals("Test 1234567890", writer.toString());
    }

    @Test
    public void testAppendToStringBuilder() throws Exception {
        final StrBuilder sb = new StrBuilder("1234567890");
        final StringBuilder builder = new StringBuilder("Test ");
        sb.appendTo(builder);
        Assertions.assertEquals("Test 1234567890", builder.toString());
    }

    @Test
    public void testAppendToStringBuffer() throws Exception {
        final StrBuilder sb = new StrBuilder("1234567890");
        final StringBuffer buffer = new StringBuffer("Test ");
        sb.appendTo(buffer);
        Assertions.assertEquals("Test 1234567890", buffer.toString());
    }

    @Test
    public void testAppendToCharBuffer() throws Exception {
        final StrBuilder sb = new StrBuilder("1234567890");
        final String text = "Test ";
        final CharBuffer buffer = CharBuffer.allocate(((sb.size()) + (text.length())));
        buffer.put(text);
        sb.appendTo(buffer);
        buffer.flip();
        Assertions.assertEquals("Test 1234567890", buffer.toString());
    }
}

