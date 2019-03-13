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
package org.apache.commons.lang3;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests CharSequenceUtils
 */
public class CharSequenceUtilsTest {
    // -----------------------------------------------------------------------
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new CharSequenceUtils());
        final Constructor<?>[] cons = CharSequenceUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(CharSequenceUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(CharSequenceUtils.class.getModifiers()));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSubSequence() {
        // 
        // null input
        // 
        Assertions.assertNull(CharSequenceUtils.subSequence(null, (-1)));
        Assertions.assertNull(CharSequenceUtils.subSequence(null, 0));
        Assertions.assertNull(CharSequenceUtils.subSequence(null, 1));
        // 
        // non-null input
        // 
        Assertions.assertEquals(StringUtils.EMPTY, CharSequenceUtils.subSequence(StringUtils.EMPTY, 0));
        Assertions.assertEquals("012", CharSequenceUtils.subSequence("012", 0));
        Assertions.assertEquals("12", CharSequenceUtils.subSequence("012", 1));
        Assertions.assertEquals("2", CharSequenceUtils.subSequence("012", 2));
        Assertions.assertEquals(StringUtils.EMPTY, CharSequenceUtils.subSequence("012", 3));
    }

    @Test
    public void testSubSequenceNegativeStart() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> CharSequenceUtils.subSequence(StringUtils.EMPTY, (-1)));
    }

    @Test
    public void testSubSequenceTooLong() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> CharSequenceUtils.subSequence(StringUtils.EMPTY, 1));
    }

    static class TestData {
        final String source;

        final boolean ignoreCase;

        final int toffset;

        final String other;

        final int ooffset;

        final int len;

        final boolean expected;

        final Class<? extends Throwable> throwable;

        TestData(final String source, final boolean ignoreCase, final int toffset, final String other, final int ooffset, final int len, final boolean expected) {
            this.source = source;
            this.ignoreCase = ignoreCase;
            this.toffset = toffset;
            this.other = other;
            this.ooffset = ooffset;
            this.len = len;
            this.expected = expected;
            this.throwable = null;
        }

        TestData(final String source, final boolean ignoreCase, final int toffset, final String other, final int ooffset, final int len, final Class<? extends Throwable> throwable) {
            this.source = source;
            this.ignoreCase = ignoreCase;
            this.toffset = toffset;
            this.other = other;
            this.ooffset = ooffset;
            this.len = len;
            this.expected = false;
            this.throwable = throwable;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(source).append("[").append(toffset).append("]");
            sb.append((ignoreCase ? " caseblind " : " samecase "));
            sb.append(other).append("[").append(ooffset).append("]");
            sb.append(" ").append(len).append(" => ");
            if ((throwable) != null) {
                sb.append(throwable);
            } else {
                sb.append(expected);
            }
            return sb.toString();
        }
    }

    private static final CharSequenceUtilsTest.TestData[] TEST_DATA = new CharSequenceUtilsTest.TestData[]{ // Source  IgnoreCase Offset Other  Offset Length Result
    new CharSequenceUtilsTest.TestData("", true, (-1), "", (-1), (-1), false), new CharSequenceUtilsTest.TestData("", true, 0, "", 0, 1, false), new CharSequenceUtilsTest.TestData("a", true, 0, "abc", 0, 0, true), new CharSequenceUtilsTest.TestData("a", true, 0, "abc", 0, 1, true), new CharSequenceUtilsTest.TestData("a", true, 0, null, 0, 0, NullPointerException.class), new CharSequenceUtilsTest.TestData(null, true, 0, null, 0, 0, NullPointerException.class), new CharSequenceUtilsTest.TestData(null, true, 0, "", 0, 0, NullPointerException.class), new CharSequenceUtilsTest.TestData("Abc", true, 0, "abc", 0, 3, true), new CharSequenceUtilsTest.TestData("Abc", false, 0, "abc", 0, 3, false), new CharSequenceUtilsTest.TestData("Abc", true, 1, "abc", 1, 2, true), new CharSequenceUtilsTest.TestData("Abc", false, 1, "abc", 1, 2, true), new CharSequenceUtilsTest.TestData("Abcd", true, 1, "abcD", 1, 2, true), new CharSequenceUtilsTest.TestData("Abcd", false, 1, "abcD", 1, 2, true) };

    private abstract static class RunTest {
        abstract boolean invoke();

        void run(final CharSequenceUtilsTest.TestData data, final String id) {
            if ((data.throwable) != null) {
                Assertions.assertThrows(data.throwable, this::invoke, ((id + " Expected ") + (data.throwable)));
            } else {
                final boolean stringCheck = invoke();
                Assertions.assertEquals(data.expected, stringCheck, ((id + " Failed test ") + data));
            }
        }
    }

    @Test
    public void testRegionMatches() {
        for (final CharSequenceUtilsTest.TestData data : CharSequenceUtilsTest.TEST_DATA) {
            new CharSequenceUtilsTest.RunTest() {
                @Override
                boolean invoke() {
                    return data.source.regionMatches(data.ignoreCase, data.toffset, data.other, data.ooffset, data.len);
                }
            }.run(data, "String");
            new CharSequenceUtilsTest.RunTest() {
                @Override
                boolean invoke() {
                    return CharSequenceUtils.regionMatches(data.source, data.ignoreCase, data.toffset, data.other, data.ooffset, data.len);
                }
            }.run(data, "CSString");
            new CharSequenceUtilsTest.RunTest() {
                @Override
                boolean invoke() {
                    return CharSequenceUtils.regionMatches(new StringBuilder(data.source), data.ignoreCase, data.toffset, data.other, data.ooffset, data.len);
                }
            }.run(data, "CSNonString");
        }
    }

    @Test
    public void testToCharArray() {
        final StringBuilder builder = new StringBuilder("abcdefg");
        final char[] expected = builder.toString().toCharArray();
        Assertions.assertArrayEquals(expected, CharSequenceUtils.toCharArray(builder));
        Assertions.assertArrayEquals(expected, CharSequenceUtils.toCharArray(builder.toString()));
    }
}

