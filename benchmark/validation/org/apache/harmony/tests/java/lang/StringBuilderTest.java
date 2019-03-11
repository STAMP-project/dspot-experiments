/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.harmony.tests.java.lang;


import java.util.Arrays;
import junit.framework.TestCase;


// import org.apache.harmony.testframework.serialization.SerializationTest;
// import org.apache.harmony.testframework.serialization.SerializationTest.SerializableAssert;
public class StringBuilderTest extends TestCase {
    /**
     * java.lang.StringBuilder.StringBuilder()
     */
    public void test_Constructor() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertNotNull(sb);
        TestCase.assertEquals(16, sb.capacity());
    }

    /**
     * java.lang.StringBuilder.StringBuilder(int)
     */
    public void test_ConstructorI() {
        StringBuilder sb = new StringBuilder(24);
        TestCase.assertNotNull(sb);
        TestCase.assertEquals(24, sb.capacity());
        try {
            new StringBuilder((-1));
            TestCase.fail("no exception");
        } catch (NegativeArraySizeException e) {
            // Expected
        }
        TestCase.assertNotNull(new StringBuilder(0));
    }

    /**
     * java.lang.StringBuilder.StringBuilder(String)
     */
    public void test_ConstructorLjava_lang_String() {
        StringBuilder sb = new StringBuilder("fixture");
        TestCase.assertEquals("fixture", sb.toString());
        TestCase.assertEquals((("fixture".length()) + 16), sb.capacity());
        try {
            new StringBuilder(((String) (null)));
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.StringBuilder.append(boolean)
     */
    public void test_appendZ() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(true));
        TestCase.assertEquals("true", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(false));
        TestCase.assertEquals("false", sb.toString());
    }

    /**
     * java.lang.StringBuilder.append(char)
     */
    public void test_appendC() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append('a'));
        TestCase.assertEquals("a", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append('b'));
        TestCase.assertEquals("b", sb.toString());
    }

    /**
     * java.lang.StringBuilder.append(char[])
     */
    public void test_append$C() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(new char[]{ 'a', 'b' }));
        TestCase.assertEquals("ab", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(new char[]{ 'c', 'd' }));
        TestCase.assertEquals("cd", sb.toString());
        try {
            sb.append(((char[]) (null)));
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.append(char[], int, int)
     */
    public void test_append$CII() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(new char[]{ 'a', 'b' }, 0, 2));
        TestCase.assertEquals("ab", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(new char[]{ 'c', 'd' }, 0, 2));
        TestCase.assertEquals("cd", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(new char[]{ 'a', 'b', 'c', 'd' }, 0, 2));
        TestCase.assertEquals("ab", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(new char[]{ 'a', 'b', 'c', 'd' }, 2, 2));
        TestCase.assertEquals("cd", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(new char[]{ 'a', 'b', 'c', 'd' }, 2, 0));
        TestCase.assertEquals("", sb.toString());
        try {
            sb.append(((char[]) (null)), 0, 2);
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            sb.append(new char[]{ 'a', 'b', 'c', 'd' }, (-1), 2);
            TestCase.fail("no IOOBE, negative offset");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.append(new char[]{ 'a', 'b', 'c', 'd' }, 0, (-1));
            TestCase.fail("no IOOBE, negative length");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.append(new char[]{ 'a', 'b', 'c', 'd' }, 2, 3);
            TestCase.fail("no IOOBE, offset and length overflow");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.append(CharSequence)
     */
    public void test_appendLjava_lang_CharSequence() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(((CharSequence) ("ab"))));
        TestCase.assertEquals("ab", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(((CharSequence) ("cd"))));
        TestCase.assertEquals("cd", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(((CharSequence) (null))));
        TestCase.assertEquals("null", sb.toString());
    }

    /**
     * java.lang.StringBuilder.append(double)
     */
    public void test_appendD() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(1.0));
        TestCase.assertEquals(String.valueOf(1.0), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(0.0));
        TestCase.assertEquals(String.valueOf(0.0), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append((-1.0)));
        TestCase.assertEquals(String.valueOf((-1.0)), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Double.NaN));
        TestCase.assertEquals(String.valueOf(Double.NaN), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Double.NEGATIVE_INFINITY));
        TestCase.assertEquals(String.valueOf(Double.NEGATIVE_INFINITY), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Double.POSITIVE_INFINITY));
        TestCase.assertEquals(String.valueOf(Double.POSITIVE_INFINITY), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Double.MIN_VALUE));
        TestCase.assertEquals(String.valueOf(Double.MIN_VALUE), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Double.MAX_VALUE));
        TestCase.assertEquals(String.valueOf(Double.MAX_VALUE), sb.toString());
    }

    /**
     * java.lang.StringBuilder.append(float)
     */
    public void test_appendF() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(1.0F));
        TestCase.assertEquals(String.valueOf(1.0F), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(0.0F));
        TestCase.assertEquals(String.valueOf(0.0F), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append((-1.0F)));
        TestCase.assertEquals(String.valueOf((-1.0F)), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Float.NaN));
        TestCase.assertEquals(String.valueOf(Float.NaN), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Float.NEGATIVE_INFINITY));
        TestCase.assertEquals(String.valueOf(Float.NEGATIVE_INFINITY), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Float.POSITIVE_INFINITY));
        TestCase.assertEquals(String.valueOf(Float.POSITIVE_INFINITY), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Float.MIN_VALUE));
        TestCase.assertEquals(String.valueOf(Float.MIN_VALUE), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Float.MAX_VALUE));
        TestCase.assertEquals(String.valueOf(Float.MAX_VALUE), sb.toString());
    }

    /**
     * java.lang.StringBuilder.append(int)
     */
    public void test_appendI() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(1));
        TestCase.assertEquals(String.valueOf(1), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(0));
        TestCase.assertEquals(String.valueOf(0), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append((-1)));
        TestCase.assertEquals(String.valueOf((-1)), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Integer.MIN_VALUE));
        TestCase.assertEquals(String.valueOf(Integer.MIN_VALUE), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Integer.MAX_VALUE));
        TestCase.assertEquals(String.valueOf(Integer.MAX_VALUE), sb.toString());
    }

    /**
     * java.lang.StringBuilder.append(long)
     */
    public void test_appendL() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(1L));
        TestCase.assertEquals(String.valueOf(1L), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(0L));
        TestCase.assertEquals(String.valueOf(0L), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append((-1L)));
        TestCase.assertEquals(String.valueOf((-1L)), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Integer.MIN_VALUE));
        TestCase.assertEquals(String.valueOf(Integer.MIN_VALUE), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(Integer.MAX_VALUE));
        TestCase.assertEquals(String.valueOf(Integer.MAX_VALUE), sb.toString());
    }

    /**
     * java.lang.StringBuilder.append(Object)'
     */
    public void test_appendLjava_lang_Object() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(StringBuilderTest.Fixture.INSTANCE));
        TestCase.assertEquals(StringBuilderTest.Fixture.INSTANCE.toString(), sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(((Object) (null))));
        TestCase.assertEquals("null", sb.toString());
    }

    /**
     * java.lang.StringBuilder.append(String)
     */
    public void test_appendLjava_lang_String() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append("ab"));
        TestCase.assertEquals("ab", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append("cd"));
        TestCase.assertEquals("cd", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(((String) (null))));
        TestCase.assertEquals("null", sb.toString());
    }

    /**
     * java.lang.StringBuilder.append(StringBuffer)
     */
    public void test_appendLjava_lang_StringBuffer() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertSame(sb, sb.append(new StringBuffer("ab")));
        TestCase.assertEquals("ab", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(new StringBuffer("cd")));
        TestCase.assertEquals("cd", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.append(((StringBuffer) (null))));
        TestCase.assertEquals("null", sb.toString());
    }

    /**
     * java.lang.StringBuilder.appendCodePoint(int)'
     */
    public void test_appendCodePointI() {
        StringBuilder sb = new StringBuilder();
        sb.appendCodePoint(65536);
        TestCase.assertEquals("\ud800\udc00", sb.toString());
        sb.append("fixture");
        TestCase.assertEquals("\ud800\udc00fixture", sb.toString());
        sb.appendCodePoint(1114111);
        TestCase.assertEquals("\ud800\udc00fixture\udbff\udfff", sb.toString());
    }

    /**
     * java.lang.StringBuilder.capacity()'
     */
    public void test_capacity() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertEquals(16, sb.capacity());
        sb.append("0123456789ABCDEF0123456789ABCDEF");
        TestCase.assertTrue(((sb.capacity()) > 16));
    }

    /**
     * java.lang.StringBuilder.charAt(int)'
     */
    public void test_charAtI() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        for (int i = 0; i < (fixture.length()); i++) {
            TestCase.assertEquals(((char) ('0' + i)), sb.charAt(i));
        }
        try {
            sb.charAt((-1));
            TestCase.fail("no IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.charAt(fixture.length());
            TestCase.fail("no IOOBE, equal to length");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.charAt(((fixture.length()) + 1));
            TestCase.fail("no IOOBE, greater than length");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * java.lang.StringBuilder.codePointAt(int)
     */
    public void test_codePointAtI() {
        StringBuilder sb = new StringBuilder("abc");
        TestCase.assertEquals('a', sb.codePointAt(0));
        TestCase.assertEquals('b', sb.codePointAt(1));
        TestCase.assertEquals('c', sb.codePointAt(2));
        sb = new StringBuilder("\ud800\udc00");
        TestCase.assertEquals(65536, sb.codePointAt(0));
        TestCase.assertEquals('\udc00', sb.codePointAt(1));
        sb = new StringBuilder();
        sb.append("abc");
        try {
            sb.codePointAt((-1));
            TestCase.fail("No IOOBE on negative index.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.codePointAt(sb.length());
            TestCase.fail("No IOOBE on index equal to length.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.codePointAt(((sb.length()) + 1));
            TestCase.fail("No IOOBE on index greater than length.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * java.lang.StringBuilder.codePointBefore(int)
     */
    public void test_codePointBeforeI() {
        StringBuilder sb = new StringBuilder("abc");
        TestCase.assertEquals('a', sb.codePointBefore(1));
        TestCase.assertEquals('b', sb.codePointBefore(2));
        TestCase.assertEquals('c', sb.codePointBefore(3));
        sb = new StringBuilder("\ud800\udc00");
        TestCase.assertEquals(65536, sb.codePointBefore(2));
        TestCase.assertEquals('\ud800', sb.codePointBefore(1));
        sb = new StringBuilder();
        sb.append("abc");
        try {
            sb.codePointBefore(0);
            TestCase.fail("No IOOBE on zero index.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.codePointBefore((-1));
            TestCase.fail("No IOOBE on negative index.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.codePointBefore(((sb.length()) + 1));
            TestCase.fail("No IOOBE on index greater than length.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * java.lang.StringBuilder.codePointCount(int, int)
     */
    public void test_codePointCountII() {
        TestCase.assertEquals(1, new StringBuilder("\ud800\udc00").codePointCount(0, 2));
        TestCase.assertEquals(1, new StringBuilder("\ud800\udc01").codePointCount(0, 2));
        TestCase.assertEquals(1, new StringBuilder("\ud801\udc01").codePointCount(0, 2));
        TestCase.assertEquals(1, new StringBuilder("\udbff\udfff").codePointCount(0, 2));
        TestCase.assertEquals(3, new StringBuilder("a\ud800\udc00b").codePointCount(0, 4));
        TestCase.assertEquals(4, new StringBuilder("a\ud800\udc00b\ud800").codePointCount(0, 5));
        StringBuilder sb = new StringBuilder();
        sb.append("abc");
        try {
            sb.codePointCount((-1), 2);
            TestCase.fail("No IOOBE for negative begin index.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.codePointCount(0, 4);
            TestCase.fail("No IOOBE for end index that's too large.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.codePointCount(3, 2);
            TestCase.fail("No IOOBE for begin index larger than end index.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * java.lang.StringBuilder.delete(int, int)
     */
    public void test_deleteII() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.delete(0, 0));
        TestCase.assertEquals(fixture, sb.toString());
        TestCase.assertSame(sb, sb.delete(5, 5));
        TestCase.assertEquals(fixture, sb.toString());
        TestCase.assertSame(sb, sb.delete(0, 1));
        TestCase.assertEquals("123456789", sb.toString());
        TestCase.assertEquals(9, sb.length());
        TestCase.assertSame(sb, sb.delete(0, sb.length()));
        TestCase.assertEquals("", sb.toString());
        TestCase.assertEquals(0, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.delete(0, 11));
        TestCase.assertEquals("", sb.toString());
        TestCase.assertEquals(0, sb.length());
        try {
            new StringBuilder(fixture).delete((-1), 2);
            TestCase.fail("no SIOOBE, negative start");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            new StringBuilder(fixture).delete(11, 12);
            TestCase.fail("no SIOOBE, start too far");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            new StringBuilder(fixture).delete(13, 12);
            TestCase.fail("no SIOOBE, start larger than end");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        // HARMONY 6212
        sb = new StringBuilder();
        sb.append("abcde");
        String str = sb.toString();
        sb.delete(0, sb.length());
        sb.append("YY");
        TestCase.assertEquals("abcde", str);
        TestCase.assertEquals("YY", sb.toString());
    }

    /**
     * java.lang.StringBuilder.deleteCharAt(int)
     */
    public void test_deleteCharAtI() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.deleteCharAt(0));
        TestCase.assertEquals("123456789", sb.toString());
        TestCase.assertEquals(9, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.deleteCharAt(5));
        TestCase.assertEquals("012346789", sb.toString());
        TestCase.assertEquals(9, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.deleteCharAt(9));
        TestCase.assertEquals("012345678", sb.toString());
        TestCase.assertEquals(9, sb.length());
        try {
            new StringBuilder(fixture).deleteCharAt((-1));
            TestCase.fail("no SIOOBE, negative index");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            new StringBuilder(fixture).deleteCharAt(fixture.length());
            TestCase.fail("no SIOOBE, index equals length");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            new StringBuilder(fixture).deleteCharAt(((fixture.length()) + 1));
            TestCase.fail("no SIOOBE, index exceeds length");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.ensureCapacity(int)'
     */
    public void test_ensureCapacityI() {
        StringBuilder sb = new StringBuilder(5);
        TestCase.assertEquals(5, sb.capacity());
        sb.ensureCapacity(10);
        TestCase.assertEquals(12, sb.capacity());
        sb.ensureCapacity(26);
        TestCase.assertEquals(26, sb.capacity());
        sb.ensureCapacity(55);
        TestCase.assertEquals(55, sb.capacity());
    }

    /**
     * java.lang.StringBuilder.getChars(int, int, char[], int)'
     */
    public void test_getCharsII$CI() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        char[] dst = new char[10];
        sb.getChars(0, 10, dst, 0);
        TestCase.assertTrue(Arrays.equals(fixture.toCharArray(), dst));
        Arrays.fill(dst, '\u0000');
        sb.getChars(0, 5, dst, 0);
        char[] fixtureChars = new char[10];
        fixture.getChars(0, 5, fixtureChars, 0);
        TestCase.assertTrue(Arrays.equals(fixtureChars, dst));
        Arrays.fill(dst, '\u0000');
        Arrays.fill(fixtureChars, '\u0000');
        sb.getChars(0, 5, dst, 5);
        fixture.getChars(0, 5, fixtureChars, 5);
        TestCase.assertTrue(Arrays.equals(fixtureChars, dst));
        Arrays.fill(dst, '\u0000');
        Arrays.fill(fixtureChars, '\u0000');
        sb.getChars(5, 10, dst, 1);
        fixture.getChars(5, 10, fixtureChars, 1);
        TestCase.assertTrue(Arrays.equals(fixtureChars, dst));
        try {
            sb.getChars(0, 10, null, 0);
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            sb.getChars((-1), 10, dst, 0);
            TestCase.fail("no IOOBE, srcBegin negative");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.getChars(0, 10, dst, (-1));
            TestCase.fail("no IOOBE, dstBegin negative");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.getChars(5, 4, dst, 0);
            TestCase.fail("no IOOBE, srcBegin > srcEnd");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.getChars(0, 11, dst, 0);
            TestCase.fail("no IOOBE, srcEnd > length");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.getChars(0, 10, dst, 5);
            TestCase.fail("no IOOBE, dstBegin and src size too large for what's left in dst");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.indexOf(String)
     */
    public void test_indexOfLjava_lang_String() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertEquals(0, sb.indexOf("0"));
        TestCase.assertEquals(0, sb.indexOf("012"));
        TestCase.assertEquals((-1), sb.indexOf("02"));
        TestCase.assertEquals(8, sb.indexOf("89"));
        try {
            sb.indexOf(null);
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.indexOf(String, int)
     */
    public void test_IndexOfStringInt() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertEquals(0, sb.indexOf("0"));
        TestCase.assertEquals(0, sb.indexOf("012"));
        TestCase.assertEquals((-1), sb.indexOf("02"));
        TestCase.assertEquals(8, sb.indexOf("89"));
        TestCase.assertEquals(0, sb.indexOf("0"), 0);
        TestCase.assertEquals(0, sb.indexOf("012"), 0);
        TestCase.assertEquals((-1), sb.indexOf("02"), 0);
        TestCase.assertEquals(8, sb.indexOf("89"), 0);
        TestCase.assertEquals((-1), sb.indexOf("0"), 5);
        TestCase.assertEquals((-1), sb.indexOf("012"), 5);
        TestCase.assertEquals((-1), sb.indexOf("02"), 0);
        TestCase.assertEquals(8, sb.indexOf("89"), 5);
        try {
            sb.indexOf(null, 0);
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, boolean)
     */
    public void test_insertIZ() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, true));
        TestCase.assertEquals("true0000", sb.toString());
        TestCase.assertEquals(8, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, false));
        TestCase.assertEquals("false0000", sb.toString());
        TestCase.assertEquals(9, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, false));
        TestCase.assertEquals("00false00", sb.toString());
        TestCase.assertEquals(9, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, false));
        TestCase.assertEquals("0000false", sb.toString());
        TestCase.assertEquals(9, sb.length());
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), false);
            TestCase.fail("no SIOOBE, negative index");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, false);
            TestCase.fail("no SIOOBE, index too large index");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, char)
     */
    public void test_insertIC() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, 'a'));
        TestCase.assertEquals("a0000", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, 'b'));
        TestCase.assertEquals("b0000", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, 'b'));
        TestCase.assertEquals("00b00", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, 'b'));
        TestCase.assertEquals("0000b", sb.toString());
        TestCase.assertEquals(5, sb.length());
        // FIXME this fails on Sun JRE 5.0_5
        // try {
        // sb = new StringBuilder(fixture);
        // sb.insert(-1, 'a');
        // fail("no SIOOBE, negative index");
        // } catch (StringIndexOutOfBoundsException e) {
        // // Expected
        // }
        /* FIXME This fails on Sun JRE 5.0_5, but that seems like a bug, since
        the 'insert(int, char[]) behaves this way.
         */
        // try {
        // sb = new StringBuilder(fixture);
        // sb.insert(5, 'a');
        // fail("no SIOOBE, index too large index");
        // } catch (StringIndexOutOfBoundsException e) {
        // // Expected
        // }
    }

    /**
     * java.lang.StringBuilder.insert(int, char)
     */
    public void test_insertIC_2() {
        StringBuilder obj = new StringBuilder();
        try {
            obj.insert((-1), '?');
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, char[])'
     */
    public void test_insertI$C() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, new char[]{ 'a', 'b' }));
        TestCase.assertEquals("ab0000", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, new char[]{ 'a', 'b' }));
        TestCase.assertEquals("00ab00", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, new char[]{ 'a', 'b' }));
        TestCase.assertEquals("0000ab", sb.toString());
        TestCase.assertEquals(6, sb.length());
        /* TODO This NPE is the behavior on Sun's JRE 5.0_5, but it's
        undocumented. The assumption is that this method behaves like
        String.valueOf(char[]), which does throw a NPE too, but that is also
        undocumented.
         */
        try {
            sb.insert(0, ((char[]) (null)));
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), new char[]{ 'a', 'b' });
            TestCase.fail("no SIOOBE, negative index");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, new char[]{ 'a', 'b' });
            TestCase.fail("no SIOOBE, index too large index");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, char[], int, int)
     */
    public void test_insertI$CII() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, new char[]{ 'a', 'b' }, 0, 2));
        TestCase.assertEquals("ab0000", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, new char[]{ 'a', 'b' }, 0, 1));
        TestCase.assertEquals("a0000", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, new char[]{ 'a', 'b' }, 0, 2));
        TestCase.assertEquals("00ab00", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, new char[]{ 'a', 'b' }, 0, 1));
        TestCase.assertEquals("00a00", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, new char[]{ 'a', 'b' }, 0, 2));
        TestCase.assertEquals("0000ab", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, new char[]{ 'a', 'b' }, 0, 1));
        TestCase.assertEquals("0000a", sb.toString());
        TestCase.assertEquals(5, sb.length());
        /* TODO This NPE is the behavior on Sun's JRE 5.0_5, but it's
        undocumented. The assumption is that this method behaves like
        String.valueOf(char[]), which does throw a NPE too, but that is also
        undocumented.
         */
        try {
            sb.insert(0, ((char[]) (null)), 0, 2);
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), new char[]{ 'a', 'b' }, 0, 2);
            TestCase.fail("no SIOOBE, negative index");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, new char[]{ 'a', 'b' }, 0, 2);
            TestCase.fail("no SIOOBE, index too large index");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, new char[]{ 'a', 'b' }, (-1), 2);
            TestCase.fail("no SIOOBE, negative offset");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, new char[]{ 'a', 'b' }, 0, (-1));
            TestCase.fail("no SIOOBE, negative length");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, new char[]{ 'a', 'b' }, 0, 3);
            TestCase.fail("no SIOOBE, too long");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, CharSequence)
     */
    public void test_insertILjava_lang_CharSequence() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, ((CharSequence) ("ab"))));
        TestCase.assertEquals("ab0000", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, ((CharSequence) ("ab"))));
        TestCase.assertEquals("00ab00", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, ((CharSequence) ("ab"))));
        TestCase.assertEquals("0000ab", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, ((CharSequence) (null))));
        TestCase.assertEquals("0000null", sb.toString());
        TestCase.assertEquals(8, sb.length());
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), ((CharSequence) ("ab")));
            TestCase.fail("no IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, ((CharSequence) ("ab")));
            TestCase.fail("no IOOBE, index too large index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, double)
     */
    public void test_insertID() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, (-1.0)));
        TestCase.assertEquals("-1.00000", sb.toString());
        TestCase.assertEquals(8, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, 0.0));
        TestCase.assertEquals("0.00000", sb.toString());
        TestCase.assertEquals(7, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, 1.0));
        TestCase.assertEquals("001.000", sb.toString());
        TestCase.assertEquals(7, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, 2.0));
        TestCase.assertEquals("00002.0", sb.toString());
        TestCase.assertEquals(7, sb.length());
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), 1.0);
            TestCase.fail("no IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, 1.0);
            TestCase.fail("no IOOBE, index too large index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, float)
     */
    public void test_insertIF() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, (-1.0F)));
        TestCase.assertEquals("-1.00000", sb.toString());
        TestCase.assertEquals(8, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, 0.0F));
        TestCase.assertEquals("0.00000", sb.toString());
        TestCase.assertEquals(7, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, 1.0F));
        TestCase.assertEquals("001.000", sb.toString());
        TestCase.assertEquals(7, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, 2.0F));
        TestCase.assertEquals("00002.0", sb.toString());
        TestCase.assertEquals(7, sb.length());
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), 1.0F);
            TestCase.fail("no IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, 1.0F);
            TestCase.fail("no IOOBE, index too large index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, int)
     */
    public void test_insertII() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, (-1)));
        TestCase.assertEquals("-10000", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, 0));
        TestCase.assertEquals("00000", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, 1));
        TestCase.assertEquals("00100", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, 2));
        TestCase.assertEquals("00002", sb.toString());
        TestCase.assertEquals(5, sb.length());
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), 1);
            TestCase.fail("no IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, 1);
            TestCase.fail("no IOOBE, index too large index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, long)
     */
    public void test_insertIJ() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, (-1L)));
        TestCase.assertEquals("-10000", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, 0L));
        TestCase.assertEquals("00000", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, 1L));
        TestCase.assertEquals("00100", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, 2L));
        TestCase.assertEquals("00002", sb.toString());
        TestCase.assertEquals(5, sb.length());
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), 1L);
            TestCase.fail("no IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, 1L);
            TestCase.fail("no IOOBE, index too large index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, Object)
     */
    public void test_insertILjava_lang_Object() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, StringBuilderTest.Fixture.INSTANCE));
        TestCase.assertEquals("fixture0000", sb.toString());
        TestCase.assertEquals(11, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, StringBuilderTest.Fixture.INSTANCE));
        TestCase.assertEquals("00fixture00", sb.toString());
        TestCase.assertEquals(11, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, StringBuilderTest.Fixture.INSTANCE));
        TestCase.assertEquals("0000fixture", sb.toString());
        TestCase.assertEquals(11, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, ((Object) (null))));
        TestCase.assertEquals("0000null", sb.toString());
        TestCase.assertEquals(8, sb.length());
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), StringBuilderTest.Fixture.INSTANCE);
            TestCase.fail("no IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, StringBuilderTest.Fixture.INSTANCE);
            TestCase.fail("no IOOBE, index too large index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.insert(int, String)
     */
    public void test_insertILjava_lang_String() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(0, "fixture"));
        TestCase.assertEquals("fixture0000", sb.toString());
        TestCase.assertEquals(11, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(2, "fixture"));
        TestCase.assertEquals("00fixture00", sb.toString());
        TestCase.assertEquals(11, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, "fixture"));
        TestCase.assertEquals("0000fixture", sb.toString());
        TestCase.assertEquals(11, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.insert(4, ((Object) (null))));
        TestCase.assertEquals("0000null", sb.toString());
        TestCase.assertEquals(8, sb.length());
        try {
            sb = new StringBuilder(fixture);
            sb.insert((-1), "fixture");
            TestCase.fail("no IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.insert(5, "fixture");
            TestCase.fail("no IOOBE, index too large index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.lastIndexOf(String)
     */
    public void test_lastIndexOfLjava_lang_String() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertEquals(0, sb.lastIndexOf("0"));
        TestCase.assertEquals(0, sb.lastIndexOf("012"));
        TestCase.assertEquals((-1), sb.lastIndexOf("02"));
        TestCase.assertEquals(8, sb.lastIndexOf("89"));
        try {
            sb.lastIndexOf(null);
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.lastIndexOf(String, int)
     */
    public void test_lastIndexOfLjava_lang_StringI() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertEquals(0, sb.lastIndexOf("0"));
        TestCase.assertEquals(0, sb.lastIndexOf("012"));
        TestCase.assertEquals((-1), sb.lastIndexOf("02"));
        TestCase.assertEquals(8, sb.lastIndexOf("89"));
        TestCase.assertEquals(0, sb.lastIndexOf("0"), 0);
        TestCase.assertEquals(0, sb.lastIndexOf("012"), 0);
        TestCase.assertEquals((-1), sb.lastIndexOf("02"), 0);
        TestCase.assertEquals(8, sb.lastIndexOf("89"), 0);
        TestCase.assertEquals((-1), sb.lastIndexOf("0"), 5);
        TestCase.assertEquals((-1), sb.lastIndexOf("012"), 5);
        TestCase.assertEquals((-1), sb.lastIndexOf("02"), 0);
        TestCase.assertEquals(8, sb.lastIndexOf("89"), 5);
        try {
            sb.lastIndexOf(null, 0);
            TestCase.fail("no NPE");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.length()
     */
    public void test_length() {
        StringBuilder sb = new StringBuilder();
        TestCase.assertEquals(0, sb.length());
        sb.append("0000");
        TestCase.assertEquals(4, sb.length());
    }

    /**
     * java.lang.StringBuilder.offsetByCodePoints(int, int)'
     */
    public void test_offsetByCodePointsII() {
        int result = new StringBuilder("a\ud800\udc00b").offsetByCodePoints(0, 2);
        TestCase.assertEquals(3, result);
        result = new StringBuilder("abcd").offsetByCodePoints(3, (-1));
        TestCase.assertEquals(2, result);
        result = new StringBuilder("a\ud800\udc00b").offsetByCodePoints(0, 3);
        TestCase.assertEquals(4, result);
        result = new StringBuilder("a\ud800\udc00b").offsetByCodePoints(3, (-1));
        TestCase.assertEquals(1, result);
        result = new StringBuilder("a\ud800\udc00b").offsetByCodePoints(3, 0);
        TestCase.assertEquals(3, result);
        result = new StringBuilder("\ud800\udc00bc").offsetByCodePoints(3, 0);
        TestCase.assertEquals(3, result);
        result = new StringBuilder("a\udc00bc").offsetByCodePoints(3, (-1));
        TestCase.assertEquals(2, result);
        result = new StringBuilder("a\ud800bc").offsetByCodePoints(3, (-1));
        TestCase.assertEquals(2, result);
        StringBuilder sb = new StringBuilder();
        sb.append("abc");
        try {
            sb.offsetByCodePoints((-1), 1);
            TestCase.fail("No IOOBE for negative index.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.offsetByCodePoints(0, 4);
            TestCase.fail("No IOOBE for offset that's too large.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.offsetByCodePoints(3, (-4));
            TestCase.fail("No IOOBE for offset that's too small.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.offsetByCodePoints(3, 1);
            TestCase.fail("No IOOBE for index that's too large.");
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            sb.offsetByCodePoints(4, (-1));
            TestCase.fail("No IOOBE for index that's too large.");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * java.lang.StringBuilder.replace(int, int, String)'
     */
    public void test_replaceIILjava_lang_String() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.replace(1, 3, "11"));
        TestCase.assertEquals("0110", sb.toString());
        TestCase.assertEquals(4, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.replace(1, 2, "11"));
        TestCase.assertEquals("01100", sb.toString());
        TestCase.assertEquals(5, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.replace(4, 5, "11"));
        TestCase.assertEquals("000011", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.replace(4, 6, "11"));
        TestCase.assertEquals("000011", sb.toString());
        TestCase.assertEquals(6, sb.length());
        // FIXME Undocumented NPE in Sun's JRE 5.0_5
        try {
            sb.replace(1, 2, null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.replace((-1), 2, "11");
            TestCase.fail("No SIOOBE, negative start");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.replace(5, 2, "11");
            TestCase.fail("No SIOOBE, start > length");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuilder(fixture);
            sb.replace(3, 2, "11");
            TestCase.fail("No SIOOBE, start > end");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        // Regression for HARMONY-348
        StringBuilder buffer = new StringBuilder("1234567");
        buffer.replace(2, 6, "XXX");
        TestCase.assertEquals("12XXX7", buffer.toString());
    }

    /**
     * java.lang.StringBuilder.reverse()
     */
    public void test_reverse() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertSame(sb, sb.reverse());
        TestCase.assertEquals("9876543210", sb.toString());
        sb = new StringBuilder("012345678");
        TestCase.assertSame(sb, sb.reverse());
        TestCase.assertEquals("876543210", sb.toString());
        sb.setLength(1);
        TestCase.assertSame(sb, sb.reverse());
        TestCase.assertEquals("8", sb.toString());
        sb.setLength(0);
        TestCase.assertSame(sb, sb.reverse());
        TestCase.assertEquals("", sb.toString());
        String str;
        str = "a";
        reverseTest(str, str, str);
        str = "ab";
        reverseTest(str, "ba", str);
        str = "abcdef";
        reverseTest(str, "fedcba", str);
        str = "abcdefg";
        reverseTest(str, "gfedcba", str);
        /* TODO(tball): update illegal Unicode characters.
        str = "\ud800\udc00";
        reverseTest(str, str, str);

        str = "\udc00\ud800";
        reverseTest(str, "\ud800\udc00", "\ud800\udc00");

        str = "a\ud800\udc00";
        reverseTest(str, "\ud800\udc00a", str);

        str = "ab\ud800\udc00";
        reverseTest(str, "\ud800\udc00ba", str);

        str = "abc\ud800\udc00";
        reverseTest(str, "\ud800\udc00cba", str);

        str = "\ud800\udc00\udc01\ud801\ud802\udc02";
        reverseTest(str, "\ud802\udc02\ud801\udc01\ud800\udc00",
        "\ud800\udc00\ud801\udc01\ud802\udc02");

        str = "\ud800\udc00\ud801\udc01\ud802\udc02";
        reverseTest(str, "\ud802\udc02\ud801\udc01\ud800\udc00", str);

        str = "\ud800\udc00\udc01\ud801a";
        reverseTest(str, "a\ud801\udc01\ud800\udc00",
        "\ud800\udc00\ud801\udc01a");

        str = "a\ud800\udc00\ud801\udc01";
        reverseTest(str, "\ud801\udc01\ud800\udc00a", str);

        str = "\ud800\udc00\udc01\ud801ab";
        reverseTest(str, "ba\ud801\udc01\ud800\udc00",
        "\ud800\udc00\ud801\udc01ab");

        str = "ab\ud800\udc00\ud801\udc01";
        reverseTest(str, "\ud801\udc01\ud800\udc00ba", str);

        str = "\ud800\udc00\ud801\udc01";
        reverseTest(str, "\ud801\udc01\ud800\udc00", str);

        str = "a\ud800\udc00z\ud801\udc01";
        reverseTest(str, "\ud801\udc01z\ud800\udc00a", str);

        str = "a\ud800\udc00bz\ud801\udc01";
        reverseTest(str, "\ud801\udc01zb\ud800\udc00a", str);

        str = "abc\ud802\udc02\ud801\udc01\ud800\udc00";
        reverseTest(str, "\ud800\udc00\ud801\udc01\ud802\udc02cba", str);

        str = "abcd\ud802\udc02\ud801\udc01\ud800\udc00";
        reverseTest(str, "\ud800\udc00\ud801\udc01\ud802\udc02dcba", str);
         */
    }

    /**
     * java.lang.StringBuilder.setCharAt(int, char)
     */
    public void test_setCharAtIC() {
        final String fixture = "0000";
        StringBuilder sb = new StringBuilder(fixture);
        sb.setCharAt(0, 'A');
        TestCase.assertEquals("A000", sb.toString());
        sb.setCharAt(1, 'B');
        TestCase.assertEquals("AB00", sb.toString());
        sb.setCharAt(2, 'C');
        TestCase.assertEquals("ABC0", sb.toString());
        sb.setCharAt(3, 'D');
        TestCase.assertEquals("ABCD", sb.toString());
        try {
            sb.setCharAt((-1), 'A');
            TestCase.fail("No IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.setCharAt(4, 'A');
            TestCase.fail("No IOOBE, index == length");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.setCharAt(5, 'A');
            TestCase.fail("No IOOBE, index > length");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.setLength(int)'
     */
    public void test_setLengthI() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        sb.setLength(5);
        TestCase.assertEquals(5, sb.length());
        TestCase.assertEquals("01234", sb.toString());
        sb.setLength(6);
        TestCase.assertEquals(6, sb.length());
        TestCase.assertEquals("01234\u0000", sb.toString());
        sb.setLength(0);
        TestCase.assertEquals(0, sb.length());
        TestCase.assertEquals("", sb.toString());
        try {
            sb.setLength((-1));
            TestCase.fail("No IOOBE, negative length.");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        sb = new StringBuilder("abcde");
        TestCase.assertEquals("abcde", sb.toString());
        sb.setLength(1);
        sb.append('g');
        TestCase.assertEquals("ag", sb.toString());
        sb = new StringBuilder("abcde");
        sb.setLength(3);
        sb.append('g');
        TestCase.assertEquals("abcg", sb.toString());
        sb = new StringBuilder("abcde");
        sb.setLength(2);
        try {
            sb.charAt(3);
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        sb = new StringBuilder();
        sb.append("abcdefg");
        sb.setLength(2);
        sb.setLength(5);
        for (int i = 2; i < 5; i++) {
            TestCase.assertEquals(0, sb.charAt(i));
        }
        sb = new StringBuilder();
        sb.append("abcdefg");
        sb.delete(2, 4);
        sb.setLength(7);
        TestCase.assertEquals('a', sb.charAt(0));
        TestCase.assertEquals('b', sb.charAt(1));
        TestCase.assertEquals('e', sb.charAt(2));
        TestCase.assertEquals('f', sb.charAt(3));
        TestCase.assertEquals('g', sb.charAt(4));
        for (int i = 5; i < 7; i++) {
            TestCase.assertEquals(0, sb.charAt(i));
        }
        sb = new StringBuilder();
        sb.append("abcdefg");
        sb.replace(2, 5, "z");
        sb.setLength(7);
        for (int i = 5; i < 7; i++) {
            TestCase.assertEquals(0, sb.charAt(i));
        }
    }

    /**
     * java.lang.StringBuilder.subSequence(int, int)
     */
    public void test_subSequenceII() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        CharSequence ss = sb.subSequence(0, 5);
        TestCase.assertEquals("01234", ss.toString());
        ss = sb.subSequence(0, 0);
        TestCase.assertEquals("", ss.toString());
        try {
            sb.subSequence((-1), 1);
            TestCase.fail("No IOOBE, negative start.");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.subSequence(0, (-1));
            TestCase.fail("No IOOBE, negative end.");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.subSequence(0, ((fixture.length()) + 1));
            TestCase.fail("No IOOBE, end > length.");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.subSequence(3, 2);
            TestCase.fail("No IOOBE, start > end.");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.substring(int)
     */
    public void test_substringI() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        String ss = sb.substring(0);
        TestCase.assertEquals(fixture, ss);
        ss = sb.substring(10);
        TestCase.assertEquals("", ss);
        try {
            sb.substring((-1));
            TestCase.fail("No SIOOBE, negative start.");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.substring(0, (-1));
            TestCase.fail("No SIOOBE, negative end.");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.substring(((fixture.length()) + 1));
            TestCase.fail("No SIOOBE, start > length.");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.substring(int, int)
     */
    public void test_substringII() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        String ss = sb.substring(0, 5);
        TestCase.assertEquals("01234", ss);
        ss = sb.substring(0, 0);
        TestCase.assertEquals("", ss);
        try {
            sb.substring((-1), 1);
            TestCase.fail("No SIOOBE, negative start.");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.substring(0, (-1));
            TestCase.fail("No SIOOBE, negative end.");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.substring(0, ((fixture.length()) + 1));
            TestCase.fail("No SIOOBE, end > length.");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb.substring(3, 2);
            TestCase.fail("No SIOOBE, start > end.");
        } catch (StringIndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuilder.toString()'
     */
    public void test_toString() throws Exception {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertEquals(fixture, sb.toString());
        sb.setLength(0);
        sb.append("abcde");
        TestCase.assertEquals("abcde", sb.toString());
        sb.setLength(1000);
        byte[] bytes = sb.toString().getBytes("GB18030");
        for (int i = 5; i < (bytes.length); i++) {
            TestCase.assertEquals(0, bytes[i]);
        }
        sb.setLength(5);
        sb.append("fghij");
        TestCase.assertEquals("abcdefghij", sb.toString());
    }

    /**
     * java.lang.StringBuilder.trimToSize()'
     */
    public void test_trimToSize() {
        final String fixture = "0123456789";
        StringBuilder sb = new StringBuilder(fixture);
        TestCase.assertTrue(((sb.capacity()) > (fixture.length())));
        TestCase.assertEquals(fixture.length(), sb.length());
        TestCase.assertEquals(fixture, sb.toString());
        int prevCapacity = sb.capacity();
        sb.trimToSize();
        TestCase.assertTrue((prevCapacity > (sb.capacity())));
        TestCase.assertEquals(fixture.length(), sb.length());
        TestCase.assertEquals(fixture, sb.toString());
    }

    // comparator for StringBuilder objects
    /* private static final SerializableAssert STRING_BILDER_COMPARATOR = new SerializableAssert() {
    public void assertDeserialized(Serializable initial,
    Serializable deserialized) {

    StringBuilder init = (StringBuilder) initial;
    StringBuilder desr = (StringBuilder) deserialized;

    assertEquals("toString", init.toString(), desr.toString());
    }
    };
     */
    /**
     * serialization/deserialization.
     */
    public void testSerializationSelf() throws Exception {
        // SerializationTest.verifySelf(new StringBuilder("0123456789"),
        // STRING_BILDER_COMPARATOR);
    }

    /**
     * serialization/deserialization compatibility with RI.
     */
    public void testSerializationCompatibility() throws Exception {
        // SerializationTest.verifyGolden(this, new StringBuilder("0123456789"),
        // STRING_BILDER_COMPARATOR);
    }

    private static final class Fixture {
        static final StringBuilderTest.Fixture INSTANCE = new StringBuilderTest.Fixture();

        private Fixture() {
            super();
        }

        @Override
        public String toString() {
            return "fixture";
        }
    }
}

