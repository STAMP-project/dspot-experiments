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
package org.apache.harmony.tests.java.lang;


import com.google.j2objc.util.ReflectionUtil;
import java.io.Serializable;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;
import org.apache.harmony.testframework.serialization.SerializationTest.SerializableAssert;


public class StringBufferTest extends TestCase {
    /**
     * java.lang.StringBuffer#setLength(int)
     */
    public void test_setLengthI() {
        // Regression for HARMONY-90
        StringBuffer buffer = new StringBuffer("abcde");
        try {
            buffer.setLength((-1));
            TestCase.fail("Assert 0: IndexOutOfBoundsException must be thrown");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        TestCase.assertEquals("abcde", buffer.toString());
        buffer.setLength(1);
        buffer.append('f');
        TestCase.assertEquals("af", buffer.toString());
        buffer = new StringBuffer("abcde");
        TestCase.assertEquals("cde", buffer.substring(2));
        buffer.setLength(3);
        buffer.append('f');
        TestCase.assertEquals("abcf", buffer.toString());
        buffer = new StringBuffer("abcde");
        buffer.setLength(2);
        try {
            buffer.charAt(3);
            TestCase.fail("should throw IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        buffer = new StringBuffer();
        buffer.append("abcdefg");
        buffer.setLength(2);
        buffer.setLength(5);
        for (int i = 2; i < 5; i++) {
            TestCase.assertEquals(0, buffer.charAt(i));
        }
        buffer = new StringBuffer();
        buffer.append("abcdefg");
        buffer.delete(2, 4);
        buffer.setLength(7);
        TestCase.assertEquals('a', buffer.charAt(0));
        TestCase.assertEquals('b', buffer.charAt(1));
        TestCase.assertEquals('e', buffer.charAt(2));
        TestCase.assertEquals('f', buffer.charAt(3));
        TestCase.assertEquals('g', buffer.charAt(4));
        for (int i = 5; i < 7; i++) {
            TestCase.assertEquals(0, buffer.charAt(i));
        }
        buffer = new StringBuffer();
        buffer.append("abcdefg");
        buffer.replace(2, 5, "z");
        buffer.setLength(7);
        for (int i = 5; i < 7; i++) {
            TestCase.assertEquals(0, buffer.charAt(i));
        }
    }

    /**
     * java.lang.StringBuffer#toString()
     */
    public void test_toString() throws Exception {
        StringBuffer buffer = new StringBuffer();
        TestCase.assertEquals("", buffer.toString());
        buffer.append("abcde");
        TestCase.assertEquals("abcde", buffer.toString());
        buffer.setLength(1000);
        byte[] bytes = buffer.toString().getBytes("GB18030");
        for (int i = 5; i < (bytes.length); i++) {
            TestCase.assertEquals(0, bytes[i]);
        }
        buffer.setLength(5);
        buffer.append("fghij");
        TestCase.assertEquals("abcdefghij", buffer.toString());
    }

    /**
     * StringBuffer.StringBuffer(CharSequence);
     */
    public void test_constructorLjava_lang_CharSequence() {
        try {
            new StringBuffer(((CharSequence) (null)));
            TestCase.fail("Assert 0: NPE must be thrown.");
        } catch (NullPointerException e) {
        }
        TestCase.assertEquals("Assert 1: must equal 'abc'.", "abc", new StringBuffer(((CharSequence) ("abc"))).toString());
    }

    public void test_trimToSize() {
        StringBuffer buffer = new StringBuffer(25);
        buffer.append("abc");
        int origCapacity = buffer.capacity();
        buffer.trimToSize();
        int trimCapacity = buffer.capacity();
        TestCase.assertTrue("Assert 0: capacity must be smaller.", (trimCapacity < origCapacity));
        TestCase.assertEquals("Assert 1: length must still be 3", 3, buffer.length());
        TestCase.assertEquals("Assert 2: value must still be 'abc'.", "abc", buffer.toString());
    }

    /**
     * java.lang.StringBuffer.append(CharSequence)
     */
    public void test_appendLjava_lang_CharSequence() {
        StringBuffer sb = new StringBuffer();
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
     * java.lang.StringBuffer.append(char[], int, int)
     */
    public void test_append$CII_2() {
        StringBuffer obj = new StringBuffer();
        try {
            obj.append(new char[0], (-1), (-1));
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * java.lang.StringBuffer.append(char[], int, int)
     */
    public void test_append$CII_3() throws Exception {
        StringBuffer obj = new StringBuffer();
        try {
            obj.append(((char[]) (null)), (-1), (-1));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.lang.StringBuffer.insert(int, CharSequence)
     */
    public void test_insertILjava_lang_CharSequence() {
        final String fixture = "0000";
        StringBuffer sb = new StringBuffer(fixture);
        TestCase.assertSame(sb, sb.insert(0, ((CharSequence) ("ab"))));
        TestCase.assertEquals("ab0000", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuffer(fixture);
        TestCase.assertSame(sb, sb.insert(2, ((CharSequence) ("ab"))));
        TestCase.assertEquals("00ab00", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuffer(fixture);
        TestCase.assertSame(sb, sb.insert(4, ((CharSequence) ("ab"))));
        TestCase.assertEquals("0000ab", sb.toString());
        TestCase.assertEquals(6, sb.length());
        sb = new StringBuffer(fixture);
        TestCase.assertSame(sb, sb.insert(4, ((CharSequence) (null))));
        TestCase.assertEquals("0000null", sb.toString());
        TestCase.assertEquals(8, sb.length());
        try {
            sb = new StringBuffer(fixture);
            sb.insert((-1), ((CharSequence) ("ab")));
            TestCase.fail("no IOOBE, negative index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        try {
            sb = new StringBuffer(fixture);
            sb.insert(5, ((CharSequence) ("ab")));
            TestCase.fail("no IOOBE, index too large index");
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
    }

    /**
     * java.lang.StringBuffer.insert(int, char)
     */
    public void test_insertIC() {
        StringBuffer obj = new StringBuffer();
        try {
            obj.insert((-1), ' ');
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * java.lang.StringBuffer.appendCodePoint(int)'
     */
    public void test_appendCodePointI() {
        StringBuffer sb = new StringBuffer();
        sb.appendCodePoint(65536);
        TestCase.assertEquals("\ud800\udc00", sb.toString());
        sb.append("fixture");
        TestCase.assertEquals("\ud800\udc00fixture", sb.toString());
        sb.appendCodePoint(1114111);
        TestCase.assertEquals("\ud800\udc00fixture\udbff\udfff", sb.toString());
    }

    /**
     * java.lang.StringBuffer.codePointAt(int)
     */
    public void test_codePointAtI() {
        StringBuffer sb = new StringBuffer("abc");
        TestCase.assertEquals('a', sb.codePointAt(0));
        TestCase.assertEquals('b', sb.codePointAt(1));
        TestCase.assertEquals('c', sb.codePointAt(2));
        sb = new StringBuffer("\ud800\udc00");
        TestCase.assertEquals(65536, sb.codePointAt(0));
        TestCase.assertEquals('\udc00', sb.codePointAt(1));
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
     * java.lang.StringBuffer.codePointBefore(int)
     */
    public void test_codePointBeforeI() {
        StringBuffer sb = new StringBuffer("abc");
        TestCase.assertEquals('a', sb.codePointBefore(1));
        TestCase.assertEquals('b', sb.codePointBefore(2));
        TestCase.assertEquals('c', sb.codePointBefore(3));
        sb = new StringBuffer("\ud800\udc00");
        TestCase.assertEquals(65536, sb.codePointBefore(2));
        TestCase.assertEquals('\ud800', sb.codePointBefore(1));
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
     * java.lang.StringBuffer.codePointCount(int, int)
     */
    public void test_codePointCountII() {
        TestCase.assertEquals(1, new StringBuffer("\ud800\udc00").codePointCount(0, 2));
        TestCase.assertEquals(1, new StringBuffer("\ud800\udc01").codePointCount(0, 2));
        TestCase.assertEquals(1, new StringBuffer("\ud801\udc01").codePointCount(0, 2));
        TestCase.assertEquals(1, new StringBuffer("\udbff\udfff").codePointCount(0, 2));
        TestCase.assertEquals(3, new StringBuffer("a\ud800\udc00b").codePointCount(0, 4));
        TestCase.assertEquals(4, new StringBuffer("a\ud800\udc00b\ud800").codePointCount(0, 5));
        StringBuffer sb = new StringBuffer("abc");
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
     * java.lang.StringBuffer.getChars(int, int, char[], int)
     */
    public void test_getCharsII$CI() {
        StringBuffer obj = new StringBuffer();
        try {
            obj.getChars(0, 0, new char[0], (-1));
            TestCase.fail("ArrayIndexOutOfBoundsException expected");
        } catch (ArrayIndexOutOfBoundsException e) {
            // expected
        }
    }

    /**
     * java.lang.StringBuffer.offsetByCodePoints(int, int)'
     */
    public void test_offsetByCodePointsII() {
        int result = new StringBuffer("a\ud800\udc00b").offsetByCodePoints(0, 2);
        TestCase.assertEquals(3, result);
        result = new StringBuffer("abcd").offsetByCodePoints(3, (-1));
        TestCase.assertEquals(2, result);
        result = new StringBuffer("a\ud800\udc00b").offsetByCodePoints(0, 3);
        TestCase.assertEquals(4, result);
        result = new StringBuffer("a\ud800\udc00b").offsetByCodePoints(3, (-1));
        TestCase.assertEquals(1, result);
        result = new StringBuffer("a\ud800\udc00b").offsetByCodePoints(3, 0);
        TestCase.assertEquals(3, result);
        result = new StringBuffer("\ud800\udc00bc").offsetByCodePoints(3, 0);
        TestCase.assertEquals(3, result);
        result = new StringBuffer("a\udc00bc").offsetByCodePoints(3, (-1));
        TestCase.assertEquals(2, result);
        result = new StringBuffer("a\ud800bc").offsetByCodePoints(3, (-1));
        TestCase.assertEquals(2, result);
        StringBuffer sb = new StringBuffer("abc");
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

    // comparator for StringBuffer objects
    private static final SerializableAssert STRING_BUFFER_COMPARATOR = new SerializableAssert() {
        public void assertDeserialized(Serializable initial, Serializable deserialized) {
            StringBuffer init = ((StringBuffer) (initial));
            StringBuffer desr = ((StringBuffer) (deserialized));
            // serializable fields are: 'count', 'shared', 'value'
            // serialization of 'shared' is not verified
            // 'count' + 'value' should result in required string
            TestCase.assertEquals("toString", init.toString(), desr.toString());
        }
    };

    /**
     * serialization/deserialization.
     */
    public void testSerializationSelf() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        SerializationTest.verifySelf(new StringBuffer("0123456789"), StringBufferTest.STRING_BUFFER_COMPARATOR);
    }

    /**
     * serialization/deserialization compatibility with RI.
     */
    public void testSerializationCompatibility() throws Exception {
        if (ReflectionUtil.isJreReflectionStripped()) {
            return;
        }
        SerializationTest.verifyGolden(this, new StringBuffer("0123456789"), StringBufferTest.STRING_BUFFER_COMPARATOR);
    }
}

