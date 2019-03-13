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


import java.util.Locale;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests {@link org.apache.commons.lang3.StringUtils} - Equals/IndexOf methods
 */
public class StringUtilsEqualsIndexOfTest {
    private static final String BAR = "bar";

    /**
     * Supplementary character U+20000
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    private static final String CharU20000 = "\ud840\udc00";

    /**
     * Supplementary character U+20001
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    private static final String CharU20001 = "\ud840\udc01";

    private static final String FOO = "foo";

    private static final String FOOBAR = "foobar";

    private static final String[] FOOBAR_SUB_ARRAY = new String[]{ "ob", "ba" };

    // The purpose of this class is to test StringUtils#equals(CharSequence, CharSequence)
    // with a CharSequence implementation whose equals(Object) override requires that the
    // other object be an instance of CustomCharSequence, even though, as char sequences,
    // `seq` may equal the other object.
    private static class CustomCharSequence implements CharSequence {
        private final CharSequence seq;

        CustomCharSequence(final CharSequence seq) {
            this.seq = seq;
        }

        @Override
        public char charAt(final int index) {
            return seq.charAt(index);
        }

        @Override
        public int length() {
            return seq.length();
        }

        @Override
        public CharSequence subSequence(final int start, final int end) {
            return new StringUtilsEqualsIndexOfTest.CustomCharSequence(seq.subSequence(start, end));
        }

        @Override
        public boolean equals(final Object obj) {
            if ((obj == null) || (!(obj instanceof StringUtilsEqualsIndexOfTest.CustomCharSequence))) {
                return false;
            }
            final StringUtilsEqualsIndexOfTest.CustomCharSequence other = ((StringUtilsEqualsIndexOfTest.CustomCharSequence) (obj));
            return seq.equals(other.seq);
        }

        @Override
        public int hashCode() {
            return seq.hashCode();
        }

        @Override
        public String toString() {
            return seq.toString();
        }
    }

    @Test
    public void testCustomCharSequence() {
        MatcherAssert.assertThat(new StringUtilsEqualsIndexOfTest.CustomCharSequence(StringUtilsEqualsIndexOfTest.FOO), IsNot.<CharSequence>not(StringUtilsEqualsIndexOfTest.FOO));
        MatcherAssert.assertThat(StringUtilsEqualsIndexOfTest.FOO, IsNot.<CharSequence>not(new StringUtilsEqualsIndexOfTest.CustomCharSequence(StringUtilsEqualsIndexOfTest.FOO)));
        Assertions.assertEquals(new StringUtilsEqualsIndexOfTest.CustomCharSequence(StringUtilsEqualsIndexOfTest.FOO), new StringUtilsEqualsIndexOfTest.CustomCharSequence(StringUtilsEqualsIndexOfTest.FOO));
    }

    @Test
    public void testEquals() {
        final CharSequence fooCs = new StringBuilder(StringUtilsEqualsIndexOfTest.FOO);
        final CharSequence barCs = new StringBuilder(StringUtilsEqualsIndexOfTest.BAR);
        final CharSequence foobarCs = new StringBuilder(StringUtilsEqualsIndexOfTest.FOOBAR);
        Assertions.assertTrue(StringUtils.equals(null, null));
        Assertions.assertTrue(StringUtils.equals(fooCs, fooCs));
        Assertions.assertTrue(StringUtils.equals(fooCs, new StringBuilder(StringUtilsEqualsIndexOfTest.FOO)));
        Assertions.assertTrue(StringUtils.equals(fooCs, new String(new char[]{ 'f', 'o', 'o' })));
        Assertions.assertTrue(StringUtils.equals(fooCs, new StringUtilsEqualsIndexOfTest.CustomCharSequence(StringUtilsEqualsIndexOfTest.FOO)));
        Assertions.assertTrue(StringUtils.equals(new StringUtilsEqualsIndexOfTest.CustomCharSequence(StringUtilsEqualsIndexOfTest.FOO), fooCs));
        Assertions.assertFalse(StringUtils.equals(fooCs, new String(new char[]{ 'f', 'O', 'O' })));
        Assertions.assertFalse(StringUtils.equals(fooCs, barCs));
        Assertions.assertFalse(StringUtils.equals(fooCs, null));
        Assertions.assertFalse(StringUtils.equals(null, fooCs));
        Assertions.assertFalse(StringUtils.equals(fooCs, foobarCs));
        Assertions.assertFalse(StringUtils.equals(foobarCs, fooCs));
    }

    @Test
    public void testEqualsOnStrings() {
        Assertions.assertTrue(StringUtils.equals(null, null));
        Assertions.assertTrue(StringUtils.equals(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertTrue(StringUtils.equals(StringUtilsEqualsIndexOfTest.FOO, new String(new char[]{ 'f', 'o', 'o' })));
        Assertions.assertFalse(StringUtils.equals(StringUtilsEqualsIndexOfTest.FOO, new String(new char[]{ 'f', 'O', 'O' })));
        Assertions.assertFalse(StringUtils.equals(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR));
        Assertions.assertFalse(StringUtils.equals(StringUtilsEqualsIndexOfTest.FOO, null));
        Assertions.assertFalse(StringUtils.equals(null, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertFalse(StringUtils.equals(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOOBAR));
        Assertions.assertFalse(StringUtils.equals(StringUtilsEqualsIndexOfTest.FOOBAR, StringUtilsEqualsIndexOfTest.FOO));
    }

    @Test
    public void testEqualsIgnoreCase() {
        Assertions.assertTrue(StringUtils.equalsIgnoreCase(null, null));
        Assertions.assertTrue(StringUtils.equalsIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertTrue(StringUtils.equalsIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, new String(new char[]{ 'f', 'o', 'o' })));
        Assertions.assertTrue(StringUtils.equalsIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, new String(new char[]{ 'f', 'O', 'O' })));
        Assertions.assertFalse(StringUtils.equalsIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR));
        Assertions.assertFalse(StringUtils.equalsIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, null));
        Assertions.assertFalse(StringUtils.equalsIgnoreCase(null, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertTrue(StringUtils.equalsIgnoreCase("", ""));
        Assertions.assertFalse(StringUtils.equalsIgnoreCase("abcd", "abcd "));
    }

    @Test
    public void testEqualsAny() {
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, new String[]{  }));
        Assertions.assertTrue(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertTrue(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR, new String(new char[]{ 'f', 'o', 'o' })));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR, new String(new char[]{ 'f', 'O', 'O' })));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR, null));
        Assertions.assertFalse(StringUtils.equalsAny(null, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOOBAR));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOOBAR, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertTrue(StringUtils.equalsAny(null, null, null));
        Assertions.assertFalse(StringUtils.equalsAny(null, StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR, StringUtilsEqualsIndexOfTest.FOOBAR));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, null, StringUtilsEqualsIndexOfTest.BAR));
        Assertions.assertTrue(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR, null, "", StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOO.toUpperCase(Locale.ROOT)));
        Assertions.assertFalse(StringUtils.equalsAny(null, ((CharSequence[]) (null))));
        Assertions.assertTrue(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, new StringUtilsEqualsIndexOfTest.CustomCharSequence("foo")));
        Assertions.assertTrue(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, new StringBuilder("foo")));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, new StringUtilsEqualsIndexOfTest.CustomCharSequence("fOo")));
        Assertions.assertFalse(StringUtils.equalsAny(StringUtilsEqualsIndexOfTest.FOO, new StringBuilder("fOo")));
    }

    @Test
    public void testEqualsAnyIgnoreCase() {
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, new String[]{  }));
        Assertions.assertTrue(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertTrue(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOO.toUpperCase(Locale.ROOT)));
        Assertions.assertTrue(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOO, new String(new char[]{ 'f', 'o', 'o' })));
        Assertions.assertTrue(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR, new String(new char[]{ 'f', 'O', 'O' })));
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR));
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR, null));
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(null, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOOBAR));
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOOBAR, StringUtilsEqualsIndexOfTest.FOO));
        Assertions.assertTrue(StringUtils.equalsAnyIgnoreCase(null, null, null));
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(null, StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR, StringUtilsEqualsIndexOfTest.FOOBAR));
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, null, StringUtilsEqualsIndexOfTest.BAR));
        Assertions.assertTrue(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.BAR, null, "", StringUtilsEqualsIndexOfTest.FOO.toUpperCase(Locale.ROOT), StringUtilsEqualsIndexOfTest.BAR));
        Assertions.assertTrue(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, StringUtilsEqualsIndexOfTest.FOO.toUpperCase(Locale.ROOT)));
        Assertions.assertFalse(StringUtils.equalsAnyIgnoreCase(null, ((CharSequence[]) (null))));
        Assertions.assertTrue(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, new StringUtilsEqualsIndexOfTest.CustomCharSequence("fOo")));
        Assertions.assertTrue(StringUtils.equalsAnyIgnoreCase(StringUtilsEqualsIndexOfTest.FOO, new StringBuilder("fOo")));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCompare_StringString() {
        Assertions.assertEquals(0, StringUtils.compare(null, null));
        Assertions.assertTrue(((StringUtils.compare(null, "a")) < 0));
        Assertions.assertTrue(((StringUtils.compare("a", null)) > 0));
        Assertions.assertEquals(0, StringUtils.compare("abc", "abc"));
        Assertions.assertTrue(((StringUtils.compare("a", "b")) < 0));
        Assertions.assertTrue(((StringUtils.compare("b", "a")) > 0));
        Assertions.assertTrue(((StringUtils.compare("a", "B")) > 0));
        Assertions.assertTrue(((StringUtils.compare("abc", "abd")) < 0));
        Assertions.assertTrue(((StringUtils.compare("ab", "abc")) < 0));
        Assertions.assertTrue(((StringUtils.compare("ab", "ab ")) < 0));
        Assertions.assertTrue(((StringUtils.compare("abc", "ab ")) > 0));
    }

    @Test
    public void testCompare_StringStringBoolean() {
        Assertions.assertEquals(0, StringUtils.compare(null, null, false));
        Assertions.assertTrue(((StringUtils.compare(null, "a", true)) < 0));
        Assertions.assertTrue(((StringUtils.compare(null, "a", false)) > 0));
        Assertions.assertTrue(((StringUtils.compare("a", null, true)) > 0));
        Assertions.assertTrue(((StringUtils.compare("a", null, false)) < 0));
        Assertions.assertEquals(0, StringUtils.compare("abc", "abc", false));
        Assertions.assertTrue(((StringUtils.compare("a", "b", false)) < 0));
        Assertions.assertTrue(((StringUtils.compare("b", "a", false)) > 0));
        Assertions.assertTrue(((StringUtils.compare("a", "B", false)) > 0));
        Assertions.assertTrue(((StringUtils.compare("abc", "abd", false)) < 0));
        Assertions.assertTrue(((StringUtils.compare("ab", "abc", false)) < 0));
        Assertions.assertTrue(((StringUtils.compare("ab", "ab ", false)) < 0));
        Assertions.assertTrue(((StringUtils.compare("abc", "ab ", false)) > 0));
    }

    @Test
    public void testCompareIgnoreCase_StringString() {
        Assertions.assertEquals(0, StringUtils.compareIgnoreCase(null, null));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase(null, "a")) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("a", null)) > 0));
        Assertions.assertEquals(0, StringUtils.compareIgnoreCase("abc", "abc"));
        Assertions.assertEquals(0, StringUtils.compareIgnoreCase("abc", "ABC"));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("a", "b")) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("b", "a")) > 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("a", "B")) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("A", "b")) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("abc", "ABD")) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("ab", "ABC")) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("ab", "AB ")) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("abc", "AB ")) > 0));
    }

    @Test
    public void testCompareIgnoreCase_StringStringBoolean() {
        Assertions.assertEquals(0, StringUtils.compareIgnoreCase(null, null, false));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase(null, "a", true)) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase(null, "a", false)) > 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("a", null, true)) > 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("a", null, false)) < 0));
        Assertions.assertEquals(0, StringUtils.compareIgnoreCase("abc", "abc", false));
        Assertions.assertEquals(0, StringUtils.compareIgnoreCase("abc", "ABC", false));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("a", "b", false)) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("b", "a", false)) > 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("a", "B", false)) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("A", "b", false)) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("abc", "ABD", false)) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("ab", "ABC", false)) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("ab", "AB ", false)) < 0));
        Assertions.assertTrue(((StringUtils.compareIgnoreCase("abc", "AB ", false)) > 0));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testIndexOf_char() {
        Assertions.assertEquals((-1), StringUtils.indexOf(null, ' '));
        Assertions.assertEquals((-1), StringUtils.indexOf("", ' '));
        Assertions.assertEquals(0, StringUtils.indexOf("aabaabaa", 'a'));
        Assertions.assertEquals(2, StringUtils.indexOf("aabaabaa", 'b'));
        Assertions.assertEquals(2, StringUtils.indexOf(new StringBuilder("aabaabaa"), 'b'));
    }

    @Test
    public void testIndexOf_charInt() {
        Assertions.assertEquals((-1), StringUtils.indexOf(null, ' ', 0));
        Assertions.assertEquals((-1), StringUtils.indexOf(null, ' ', (-1)));
        Assertions.assertEquals((-1), StringUtils.indexOf("", ' ', 0));
        Assertions.assertEquals((-1), StringUtils.indexOf("", ' ', (-1)));
        Assertions.assertEquals(0, StringUtils.indexOf("aabaabaa", 'a', 0));
        Assertions.assertEquals(2, StringUtils.indexOf("aabaabaa", 'b', 0));
        Assertions.assertEquals(5, StringUtils.indexOf("aabaabaa", 'b', 3));
        Assertions.assertEquals((-1), StringUtils.indexOf("aabaabaa", 'b', 9));
        Assertions.assertEquals(2, StringUtils.indexOf("aabaabaa", 'b', (-1)));
        Assertions.assertEquals(5, StringUtils.indexOf(new StringBuilder("aabaabaa"), 'b', 3));
        // LANG-1300 tests go here
        final int CODE_POINT = 132878;
        StringBuilder builder = new StringBuilder();
        builder.appendCodePoint(CODE_POINT);
        Assertions.assertEquals(0, StringUtils.indexOf(builder, CODE_POINT, 0));
        Assertions.assertEquals(0, StringUtils.indexOf(builder.toString(), CODE_POINT, 0));
        builder.appendCodePoint(CODE_POINT);
        Assertions.assertEquals(2, StringUtils.indexOf(builder, CODE_POINT, 1));
        Assertions.assertEquals(2, StringUtils.indexOf(builder.toString(), CODE_POINT, 1));
        // inner branch on the supplementary character block
        final char[] tmp = new char[]{ ((char) (55361)) };
        builder = new StringBuilder();
        builder.append(tmp);
        Assertions.assertEquals((-1), StringUtils.indexOf(builder, CODE_POINT, 0));
        Assertions.assertEquals((-1), StringUtils.indexOf(builder.toString(), CODE_POINT, 0));
        builder.appendCodePoint(CODE_POINT);
        Assertions.assertEquals(1, StringUtils.indexOf(builder, CODE_POINT, 0));
        Assertions.assertEquals(1, StringUtils.indexOf(builder.toString(), CODE_POINT, 0));
        Assertions.assertEquals((-1), StringUtils.indexOf(builder, CODE_POINT, 2));
        Assertions.assertEquals((-1), StringUtils.indexOf(builder.toString(), CODE_POINT, 2));
    }

    @Test
    public void testIndexOf_String() {
        Assertions.assertEquals((-1), StringUtils.indexOf(null, null));
        Assertions.assertEquals((-1), StringUtils.indexOf("", null));
        Assertions.assertEquals(0, StringUtils.indexOf("", ""));
        Assertions.assertEquals(0, StringUtils.indexOf("aabaabaa", "a"));
        Assertions.assertEquals(2, StringUtils.indexOf("aabaabaa", "b"));
        Assertions.assertEquals(1, StringUtils.indexOf("aabaabaa", "ab"));
        Assertions.assertEquals(0, StringUtils.indexOf("aabaabaa", ""));
        Assertions.assertEquals(2, StringUtils.indexOf(new StringBuilder("aabaabaa"), "b"));
    }

    @Test
    public void testIndexOf_StringInt() {
        Assertions.assertEquals((-1), StringUtils.indexOf(null, null, 0));
        Assertions.assertEquals((-1), StringUtils.indexOf(null, null, (-1)));
        Assertions.assertEquals((-1), StringUtils.indexOf(null, "", 0));
        Assertions.assertEquals((-1), StringUtils.indexOf(null, "", (-1)));
        Assertions.assertEquals((-1), StringUtils.indexOf("", null, 0));
        Assertions.assertEquals((-1), StringUtils.indexOf("", null, (-1)));
        Assertions.assertEquals(0, StringUtils.indexOf("", "", 0));
        Assertions.assertEquals(0, StringUtils.indexOf("", "", (-1)));
        Assertions.assertEquals(0, StringUtils.indexOf("", "", 9));
        Assertions.assertEquals(0, StringUtils.indexOf("abc", "", 0));
        Assertions.assertEquals(0, StringUtils.indexOf("abc", "", (-1)));
        Assertions.assertEquals(3, StringUtils.indexOf("abc", "", 9));
        Assertions.assertEquals(3, StringUtils.indexOf("abc", "", 3));
        Assertions.assertEquals(0, StringUtils.indexOf("aabaabaa", "a", 0));
        Assertions.assertEquals(2, StringUtils.indexOf("aabaabaa", "b", 0));
        Assertions.assertEquals(1, StringUtils.indexOf("aabaabaa", "ab", 0));
        Assertions.assertEquals(5, StringUtils.indexOf("aabaabaa", "b", 3));
        Assertions.assertEquals((-1), StringUtils.indexOf("aabaabaa", "b", 9));
        Assertions.assertEquals(2, StringUtils.indexOf("aabaabaa", "b", (-1)));
        Assertions.assertEquals(2, StringUtils.indexOf("aabaabaa", "", 2));
        // Test that startIndex works correctly, i.e. cannot match before startIndex
        Assertions.assertEquals(7, StringUtils.indexOf("12345678", "8", 5));
        Assertions.assertEquals(7, StringUtils.indexOf("12345678", "8", 6));
        Assertions.assertEquals(7, StringUtils.indexOf("12345678", "8", 7));// 7 is last index

        Assertions.assertEquals((-1), StringUtils.indexOf("12345678", "8", 8));
        Assertions.assertEquals(5, StringUtils.indexOf(new StringBuilder("aabaabaa"), "b", 3));
    }

    @Test
    public void testIndexOfAny_StringCharArray() {
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, ((char[]) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, new char[0]));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, 'a', 'b'));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("", ((char[]) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("", new char[0]));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("", 'a', 'b'));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("zzabyycdxx", ((char[]) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("zzabyycdxx", new char[0]));
        Assertions.assertEquals(0, StringUtils.indexOfAny("zzabyycdxx", 'z', 'a'));
        Assertions.assertEquals(3, StringUtils.indexOfAny("zzabyycdxx", 'b', 'y'));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("ab", 'z'));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testIndexOfAny_StringCharArrayWithSupplementaryChars() {
        Assertions.assertEquals(0, StringUtils.indexOfAny(((StringUtilsEqualsIndexOfTest.CharU20000) + (StringUtilsEqualsIndexOfTest.CharU20001)), StringUtilsEqualsIndexOfTest.CharU20000.toCharArray()));
        Assertions.assertEquals(2, StringUtils.indexOfAny(((StringUtilsEqualsIndexOfTest.CharU20000) + (StringUtilsEqualsIndexOfTest.CharU20001)), StringUtilsEqualsIndexOfTest.CharU20001.toCharArray()));
        Assertions.assertEquals(0, StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.CharU20000, StringUtilsEqualsIndexOfTest.CharU20000.toCharArray()));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.CharU20000, StringUtilsEqualsIndexOfTest.CharU20001.toCharArray()));
    }

    @Test
    public void testIndexOfAny_StringString() {
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, ((String) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, ""));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, "ab"));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("", ((String) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("", ""));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("", "ab"));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("zzabyycdxx", ((String) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("zzabyycdxx", ""));
        Assertions.assertEquals(0, StringUtils.indexOfAny("zzabyycdxx", "za"));
        Assertions.assertEquals(3, StringUtils.indexOfAny("zzabyycdxx", "by"));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("ab", "z"));
    }

    @Test
    public void testIndexOfAny_StringStringArray() {
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, ((String[]) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, StringUtilsEqualsIndexOfTest.FOOBAR_SUB_ARRAY));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, ((String[]) (null))));
        Assertions.assertEquals(2, StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, StringUtilsEqualsIndexOfTest.FOOBAR_SUB_ARRAY));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, new String[0]));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, new String[0]));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("", new String[0]));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, new String[]{ "llll" }));
        Assertions.assertEquals(0, StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, new String[]{ "" }));
        Assertions.assertEquals(0, StringUtils.indexOfAny("", new String[]{ "" }));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("", new String[]{ "a" }));
        Assertions.assertEquals((-1), StringUtils.indexOfAny("", new String[]{ null }));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, new String[]{ null }));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(null, new String[]{ null }));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testIndexOfAny_StringStringWithSupplementaryChars() {
        Assertions.assertEquals(0, StringUtils.indexOfAny(((StringUtilsEqualsIndexOfTest.CharU20000) + (StringUtilsEqualsIndexOfTest.CharU20001)), StringUtilsEqualsIndexOfTest.CharU20000));
        Assertions.assertEquals(2, StringUtils.indexOfAny(((StringUtilsEqualsIndexOfTest.CharU20000) + (StringUtilsEqualsIndexOfTest.CharU20001)), StringUtilsEqualsIndexOfTest.CharU20001));
        Assertions.assertEquals(0, StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.CharU20000, StringUtilsEqualsIndexOfTest.CharU20000));
        Assertions.assertEquals((-1), StringUtils.indexOfAny(StringUtilsEqualsIndexOfTest.CharU20000, StringUtilsEqualsIndexOfTest.CharU20001));
    }

    @Test
    public void testIndexOfAnyBut_StringCharArray() {
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut(null, ((char[]) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut(null));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut(null, 'a', 'b'));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("", ((char[]) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut(""));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("", 'a', 'b'));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("zzabyycdxx", ((char[]) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("zzabyycdxx"));
        Assertions.assertEquals(3, StringUtils.indexOfAnyBut("zzabyycdxx", 'z', 'a'));
        Assertions.assertEquals(0, StringUtils.indexOfAnyBut("zzabyycdxx", 'b', 'y'));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("aba", 'a', 'b'));
        Assertions.assertEquals(0, StringUtils.indexOfAnyBut("aba", 'z'));
    }

    @Test
    public void testIndexOfAnyBut_StringCharArrayWithSupplementaryChars() {
        Assertions.assertEquals(2, StringUtils.indexOfAnyBut(((StringUtilsEqualsIndexOfTest.CharU20000) + (StringUtilsEqualsIndexOfTest.CharU20001)), StringUtilsEqualsIndexOfTest.CharU20000.toCharArray()));
        Assertions.assertEquals(0, StringUtils.indexOfAnyBut(((StringUtilsEqualsIndexOfTest.CharU20000) + (StringUtilsEqualsIndexOfTest.CharU20001)), StringUtilsEqualsIndexOfTest.CharU20001.toCharArray()));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut(StringUtilsEqualsIndexOfTest.CharU20000, StringUtilsEqualsIndexOfTest.CharU20000.toCharArray()));
        Assertions.assertEquals(0, StringUtils.indexOfAnyBut(StringUtilsEqualsIndexOfTest.CharU20000, StringUtilsEqualsIndexOfTest.CharU20001.toCharArray()));
    }

    @Test
    public void testIndexOfAnyBut_StringString() {
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut(null, ((String) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut(null, ""));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut(null, "ab"));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("", ((String) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("", ""));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("", "ab"));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("zzabyycdxx", ((String) (null))));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut("zzabyycdxx", ""));
        Assertions.assertEquals(3, StringUtils.indexOfAnyBut("zzabyycdxx", "za"));
        Assertions.assertEquals(0, StringUtils.indexOfAnyBut("zzabyycdxx", "by"));
        Assertions.assertEquals(0, StringUtils.indexOfAnyBut("ab", "z"));
    }

    @Test
    public void testIndexOfAnyBut_StringStringWithSupplementaryChars() {
        Assertions.assertEquals(2, StringUtils.indexOfAnyBut(((StringUtilsEqualsIndexOfTest.CharU20000) + (StringUtilsEqualsIndexOfTest.CharU20001)), StringUtilsEqualsIndexOfTest.CharU20000));
        Assertions.assertEquals(0, StringUtils.indexOfAnyBut(((StringUtilsEqualsIndexOfTest.CharU20000) + (StringUtilsEqualsIndexOfTest.CharU20001)), StringUtilsEqualsIndexOfTest.CharU20001));
        Assertions.assertEquals((-1), StringUtils.indexOfAnyBut(StringUtilsEqualsIndexOfTest.CharU20000, StringUtilsEqualsIndexOfTest.CharU20000));
        Assertions.assertEquals(0, StringUtils.indexOfAnyBut(StringUtilsEqualsIndexOfTest.CharU20000, StringUtilsEqualsIndexOfTest.CharU20001));
    }

    @Test
    public void testIndexOfIgnoreCase_String() {
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase(null, null));
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase(null, ""));
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase("", null));
        Assertions.assertEquals(0, StringUtils.indexOfIgnoreCase("", ""));
        Assertions.assertEquals(0, StringUtils.indexOfIgnoreCase("aabaabaa", "a"));
        Assertions.assertEquals(0, StringUtils.indexOfIgnoreCase("aabaabaa", "A"));
        Assertions.assertEquals(2, StringUtils.indexOfIgnoreCase("aabaabaa", "b"));
        Assertions.assertEquals(2, StringUtils.indexOfIgnoreCase("aabaabaa", "B"));
        Assertions.assertEquals(1, StringUtils.indexOfIgnoreCase("aabaabaa", "ab"));
        Assertions.assertEquals(1, StringUtils.indexOfIgnoreCase("aabaabaa", "AB"));
        Assertions.assertEquals(0, StringUtils.indexOfIgnoreCase("aabaabaa", ""));
    }

    @Test
    public void testIndexOfIgnoreCase_StringInt() {
        Assertions.assertEquals(1, StringUtils.indexOfIgnoreCase("aabaabaa", "AB", (-1)));
        Assertions.assertEquals(1, StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 0));
        Assertions.assertEquals(1, StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 1));
        Assertions.assertEquals(4, StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 2));
        Assertions.assertEquals(4, StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 3));
        Assertions.assertEquals(4, StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 4));
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 5));
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 6));
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 7));
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 8));
        Assertions.assertEquals(1, StringUtils.indexOfIgnoreCase("aab", "AB", 1));
        Assertions.assertEquals(5, StringUtils.indexOfIgnoreCase("aabaabaa", "", 5));
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase("ab", "AAB", 0));
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase("aab", "AAB", 1));
        Assertions.assertEquals((-1), StringUtils.indexOfIgnoreCase("abc", "", 9));
    }

    @Test
    public void testLastIndexOf_char() {
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(null, ' '));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("", ' '));
        Assertions.assertEquals(7, StringUtils.lastIndexOf("aabaabaa", 'a'));
        Assertions.assertEquals(5, StringUtils.lastIndexOf("aabaabaa", 'b'));
        Assertions.assertEquals(5, StringUtils.lastIndexOf(new StringBuilder("aabaabaa"), 'b'));
    }

    @Test
    public void testLastIndexOf_charInt() {
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(null, ' ', 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(null, ' ', (-1)));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("", ' ', 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("", ' ', (-1)));
        Assertions.assertEquals(7, StringUtils.lastIndexOf("aabaabaa", 'a', 8));
        Assertions.assertEquals(5, StringUtils.lastIndexOf("aabaabaa", 'b', 8));
        Assertions.assertEquals(2, StringUtils.lastIndexOf("aabaabaa", 'b', 3));
        Assertions.assertEquals(5, StringUtils.lastIndexOf("aabaabaa", 'b', 9));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("aabaabaa", 'b', (-1)));
        Assertions.assertEquals(0, StringUtils.lastIndexOf("aabaabaa", 'a', 0));
        Assertions.assertEquals(2, StringUtils.lastIndexOf(new StringBuilder("aabaabaa"), 'b', 2));
        // LANG-1300 addition test
        final int CODE_POINT = 132878;
        StringBuilder builder = new StringBuilder();
        builder.appendCodePoint(CODE_POINT);
        Assertions.assertEquals(0, StringUtils.lastIndexOf(builder, CODE_POINT, 0));
        builder.appendCodePoint(CODE_POINT);
        Assertions.assertEquals(0, StringUtils.lastIndexOf(builder, CODE_POINT, 0));
        Assertions.assertEquals(0, StringUtils.lastIndexOf(builder, CODE_POINT, 1));
        Assertions.assertEquals(2, StringUtils.lastIndexOf(builder, CODE_POINT, 2));
        builder.append("aaaaa");
        Assertions.assertEquals(2, StringUtils.lastIndexOf(builder, CODE_POINT, 4));
        // inner branch on the supplementary character block
        final char[] tmp = new char[]{ ((char) (55361)) };
        builder = new StringBuilder();
        builder.append(tmp);
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(builder, CODE_POINT, 0));
        builder.appendCodePoint(CODE_POINT);
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(builder, CODE_POINT, 0));
        Assertions.assertEquals(1, StringUtils.lastIndexOf(builder, CODE_POINT, 1));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(builder.toString(), CODE_POINT, 0));
        Assertions.assertEquals(1, StringUtils.lastIndexOf(builder.toString(), CODE_POINT, 1));
    }

    @Test
    public void testLastIndexOf_String() {
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(null, null));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("", null));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("", "a"));
        Assertions.assertEquals(0, StringUtils.lastIndexOf("", ""));
        Assertions.assertEquals(8, StringUtils.lastIndexOf("aabaabaa", ""));
        Assertions.assertEquals(7, StringUtils.lastIndexOf("aabaabaa", "a"));
        Assertions.assertEquals(5, StringUtils.lastIndexOf("aabaabaa", "b"));
        Assertions.assertEquals(4, StringUtils.lastIndexOf("aabaabaa", "ab"));
        Assertions.assertEquals(4, StringUtils.lastIndexOf(new StringBuilder("aabaabaa"), "ab"));
    }

    @Test
    public void testLastIndexOf_StringInt() {
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(null, null, 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(null, null, (-1)));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(null, "", 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf(null, "", (-1)));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("", null, 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("", null, (-1)));
        Assertions.assertEquals(0, StringUtils.lastIndexOf("", "", 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("", "", (-1)));
        Assertions.assertEquals(0, StringUtils.lastIndexOf("", "", 9));
        Assertions.assertEquals(0, StringUtils.lastIndexOf("abc", "", 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("abc", "", (-1)));
        Assertions.assertEquals(3, StringUtils.lastIndexOf("abc", "", 9));
        Assertions.assertEquals(7, StringUtils.lastIndexOf("aabaabaa", "a", 8));
        Assertions.assertEquals(5, StringUtils.lastIndexOf("aabaabaa", "b", 8));
        Assertions.assertEquals(4, StringUtils.lastIndexOf("aabaabaa", "ab", 8));
        Assertions.assertEquals(2, StringUtils.lastIndexOf("aabaabaa", "b", 3));
        Assertions.assertEquals(5, StringUtils.lastIndexOf("aabaabaa", "b", 9));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("aabaabaa", "b", (-1)));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("aabaabaa", "b", 0));
        Assertions.assertEquals(0, StringUtils.lastIndexOf("aabaabaa", "a", 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("aabaabaa", "a", (-1)));
        // Test that fromIndex works correctly, i.e. cannot match after fromIndex
        Assertions.assertEquals(7, StringUtils.lastIndexOf("12345678", "8", 9));
        Assertions.assertEquals(7, StringUtils.lastIndexOf("12345678", "8", 8));
        Assertions.assertEquals(7, StringUtils.lastIndexOf("12345678", "8", 7));// 7 is last index

        Assertions.assertEquals((-1), StringUtils.lastIndexOf("12345678", "8", 6));
        Assertions.assertEquals((-1), StringUtils.lastIndexOf("aabaabaa", "b", 1));
        Assertions.assertEquals(2, StringUtils.lastIndexOf("aabaabaa", "b", 2));
        Assertions.assertEquals(2, StringUtils.lastIndexOf("aabaabaa", "ba", 2));
        Assertions.assertEquals(2, StringUtils.lastIndexOf("aabaabaa", "ba", 3));
        Assertions.assertEquals(2, StringUtils.lastIndexOf(new StringBuilder("aabaabaa"), "b", 3));
    }

    @Test
    public void testLastIndexOfAny_StringStringArray() {
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(null, ((CharSequence) (null))));// test both types of ...

        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(null, ((CharSequence[]) (null))));// ... varargs invocation

        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(null));// Missing varag

        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(null, StringUtilsEqualsIndexOfTest.FOOBAR_SUB_ARRAY));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, ((CharSequence) (null))));// test both types of ...

        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, ((CharSequence[]) (null))));// ... varargs invocation

        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR));// Missing vararg

        Assertions.assertEquals(3, StringUtils.lastIndexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, StringUtilsEqualsIndexOfTest.FOOBAR_SUB_ARRAY));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, new String[0]));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(null, new String[0]));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny("", new String[0]));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, new String[]{ "llll" }));
        Assertions.assertEquals(6, StringUtils.lastIndexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, new String[]{ "" }));
        Assertions.assertEquals(0, StringUtils.lastIndexOfAny("", new String[]{ "" }));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny("", new String[]{ "a" }));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny("", new String[]{ null }));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(StringUtilsEqualsIndexOfTest.FOOBAR, new String[]{ null }));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfAny(null, new String[]{ null }));
    }

    @Test
    public void testLastIndexOfIgnoreCase_String() {
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase(null, null));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase("", null));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase(null, ""));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase("", "a"));
        Assertions.assertEquals(0, StringUtils.lastIndexOfIgnoreCase("", ""));
        Assertions.assertEquals(8, StringUtils.lastIndexOfIgnoreCase("aabaabaa", ""));
        Assertions.assertEquals(7, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "a"));
        Assertions.assertEquals(7, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A"));
        Assertions.assertEquals(5, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "b"));
        Assertions.assertEquals(5, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B"));
        Assertions.assertEquals(4, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "ab"));
        Assertions.assertEquals(4, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "AB"));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase("ab", "AAB"));
        Assertions.assertEquals(0, StringUtils.lastIndexOfIgnoreCase("aab", "AAB"));
    }

    @Test
    public void testLastIndexOfIgnoreCase_StringInt() {
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase(null, null, 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase(null, null, (-1)));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase(null, "", 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase(null, "", (-1)));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase("", null, 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase("", null, (-1)));
        Assertions.assertEquals(0, StringUtils.lastIndexOfIgnoreCase("", "", 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase("", "", (-1)));
        Assertions.assertEquals(0, StringUtils.lastIndexOfIgnoreCase("", "", 9));
        Assertions.assertEquals(0, StringUtils.lastIndexOfIgnoreCase("abc", "", 0));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase("abc", "", (-1)));
        Assertions.assertEquals(3, StringUtils.lastIndexOfIgnoreCase("abc", "", 9));
        Assertions.assertEquals(7, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A", 8));
        Assertions.assertEquals(5, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 8));
        Assertions.assertEquals(4, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "AB", 8));
        Assertions.assertEquals(2, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 3));
        Assertions.assertEquals(5, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 9));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", (-1)));
        Assertions.assertEquals((-1), StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 0));
        Assertions.assertEquals(0, StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A", 0));
        Assertions.assertEquals(1, StringUtils.lastIndexOfIgnoreCase("aab", "AB", 1));
    }

    @Test
    public void testLastOrdinalIndexOf() {
        Assertions.assertEquals((-1), StringUtils.lastOrdinalIndexOf(null, "*", 42));
        Assertions.assertEquals((-1), StringUtils.lastOrdinalIndexOf("*", null, 42));
        Assertions.assertEquals(0, StringUtils.lastOrdinalIndexOf("", "", 42));
        Assertions.assertEquals(7, StringUtils.lastOrdinalIndexOf("aabaabaa", "a", 1));
        Assertions.assertEquals(6, StringUtils.lastOrdinalIndexOf("aabaabaa", "a", 2));
        Assertions.assertEquals(5, StringUtils.lastOrdinalIndexOf("aabaabaa", "b", 1));
        Assertions.assertEquals(2, StringUtils.lastOrdinalIndexOf("aabaabaa", "b", 2));
        Assertions.assertEquals(4, StringUtils.lastOrdinalIndexOf("aabaabaa", "ab", 1));
        Assertions.assertEquals(1, StringUtils.lastOrdinalIndexOf("aabaabaa", "ab", 2));
        Assertions.assertEquals(8, StringUtils.lastOrdinalIndexOf("aabaabaa", "", 1));
        Assertions.assertEquals(8, StringUtils.lastOrdinalIndexOf("aabaabaa", "", 2));
    }

    @Test
    public void testOrdinalIndexOf() {
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf(null, null, Integer.MIN_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("", null, Integer.MIN_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("", "", Integer.MIN_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "a", Integer.MIN_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "b", Integer.MIN_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "ab", Integer.MIN_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "", Integer.MIN_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf(null, null, (-1)));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("", null, (-1)));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("", "", (-1)));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "a", (-1)));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "b", (-1)));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "ab", (-1)));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "", (-1)));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf(null, null, 0));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("", null, 0));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("", "", 0));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "a", 0));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "b", 0));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "ab", 0));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "", 0));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf(null, null, 1));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("", null, 1));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("", "", 1));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("aabaabaa", "a", 1));
        Assertions.assertEquals(2, StringUtils.ordinalIndexOf("aabaabaa", "b", 1));
        Assertions.assertEquals(1, StringUtils.ordinalIndexOf("aabaabaa", "ab", 1));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("aabaabaa", "", 1));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf(null, null, 2));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("", null, 2));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("", "", 2));
        Assertions.assertEquals(1, StringUtils.ordinalIndexOf("aabaabaa", "a", 2));
        Assertions.assertEquals(5, StringUtils.ordinalIndexOf("aabaabaa", "b", 2));
        Assertions.assertEquals(4, StringUtils.ordinalIndexOf("aabaabaa", "ab", 2));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("aabaabaa", "", 2));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf(null, null, Integer.MAX_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("", null, Integer.MAX_VALUE));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("", "", Integer.MAX_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "a", Integer.MAX_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "b", Integer.MAX_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aabaabaa", "ab", Integer.MAX_VALUE));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("aabaabaa", "", Integer.MAX_VALUE));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 0));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 1));
        Assertions.assertEquals(1, StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 2));
        Assertions.assertEquals(2, StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 3));
        Assertions.assertEquals(3, StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 4));
        Assertions.assertEquals(4, StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 5));
        Assertions.assertEquals(5, StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 6));
        Assertions.assertEquals(6, StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 7));
        Assertions.assertEquals(7, StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 8));
        Assertions.assertEquals(8, StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 9));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aaaaaaaaa", "a", 10));
        // match at each possible position
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("aaaaaa", "aa", 1));
        Assertions.assertEquals(1, StringUtils.ordinalIndexOf("aaaaaa", "aa", 2));
        Assertions.assertEquals(2, StringUtils.ordinalIndexOf("aaaaaa", "aa", 3));
        Assertions.assertEquals(3, StringUtils.ordinalIndexOf("aaaaaa", "aa", 4));
        Assertions.assertEquals(4, StringUtils.ordinalIndexOf("aaaaaa", "aa", 5));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("aaaaaa", "aa", 6));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("ababab", "aba", 1));
        Assertions.assertEquals(2, StringUtils.ordinalIndexOf("ababab", "aba", 2));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("ababab", "aba", 3));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("abababab", "abab", 1));
        Assertions.assertEquals(2, StringUtils.ordinalIndexOf("abababab", "abab", 2));
        Assertions.assertEquals(4, StringUtils.ordinalIndexOf("abababab", "abab", 3));
        Assertions.assertEquals((-1), StringUtils.ordinalIndexOf("abababab", "abab", 4));
    }

    @Test
    public void testLANG1193() {
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("abc", "ab", 1));
    }

    // Non-overlapping test
    @Test
    public void testLANG1241_1() {
        // 0  3  6
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("abaabaab", "ab", 1));
        Assertions.assertEquals(3, StringUtils.ordinalIndexOf("abaabaab", "ab", 2));
        Assertions.assertEquals(6, StringUtils.ordinalIndexOf("abaabaab", "ab", 3));
    }

    // Overlapping matching test
    @Test
    public void testLANG1241_2() {
        // 0 2 4
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("abababa", "aba", 1));
        Assertions.assertEquals(2, StringUtils.ordinalIndexOf("abababa", "aba", 2));
        Assertions.assertEquals(4, StringUtils.ordinalIndexOf("abababa", "aba", 3));
        Assertions.assertEquals(0, StringUtils.ordinalIndexOf("abababab", "abab", 1));
        Assertions.assertEquals(2, StringUtils.ordinalIndexOf("abababab", "abab", 2));
        Assertions.assertEquals(4, StringUtils.ordinalIndexOf("abababab", "abab", 3));
    }
}

