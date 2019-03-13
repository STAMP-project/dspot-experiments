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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests {@link org.apache.commons.lang3.StringUtils} - Substring methods
 */
public class StringUtilsSubstringTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String BAZ = "baz";

    private static final String FOOBAR = "foobar";

    private static final String SENTENCE = "foo bar baz";

    // -----------------------------------------------------------------------
    @Test
    public void testSubstring_StringInt() {
        Assertions.assertNull(StringUtils.substring(null, 0));
        Assertions.assertEquals("", StringUtils.substring("", 0));
        Assertions.assertEquals("", StringUtils.substring("", 2));
        Assertions.assertEquals("", StringUtils.substring(StringUtilsSubstringTest.SENTENCE, 80));
        Assertions.assertEquals(StringUtilsSubstringTest.BAZ, StringUtils.substring(StringUtilsSubstringTest.SENTENCE, 8));
        Assertions.assertEquals(StringUtilsSubstringTest.BAZ, StringUtils.substring(StringUtilsSubstringTest.SENTENCE, (-3)));
        Assertions.assertEquals(StringUtilsSubstringTest.SENTENCE, StringUtils.substring(StringUtilsSubstringTest.SENTENCE, 0));
        Assertions.assertEquals("abc", StringUtils.substring("abc", (-4)));
        Assertions.assertEquals("abc", StringUtils.substring("abc", (-3)));
        Assertions.assertEquals("bc", StringUtils.substring("abc", (-2)));
        Assertions.assertEquals("c", StringUtils.substring("abc", (-1)));
        Assertions.assertEquals("abc", StringUtils.substring("abc", 0));
        Assertions.assertEquals("bc", StringUtils.substring("abc", 1));
        Assertions.assertEquals("c", StringUtils.substring("abc", 2));
        Assertions.assertEquals("", StringUtils.substring("abc", 3));
        Assertions.assertEquals("", StringUtils.substring("abc", 4));
    }

    @Test
    public void testSubstring_StringIntInt() {
        Assertions.assertNull(StringUtils.substring(null, 0, 0));
        Assertions.assertNull(StringUtils.substring(null, 1, 2));
        Assertions.assertEquals("", StringUtils.substring("", 0, 0));
        Assertions.assertEquals("", StringUtils.substring("", 1, 2));
        Assertions.assertEquals("", StringUtils.substring("", (-2), (-1)));
        Assertions.assertEquals("", StringUtils.substring(StringUtilsSubstringTest.SENTENCE, 8, 6));
        Assertions.assertEquals(StringUtilsSubstringTest.FOO, StringUtils.substring(StringUtilsSubstringTest.SENTENCE, 0, 3));
        Assertions.assertEquals("o", StringUtils.substring(StringUtilsSubstringTest.SENTENCE, (-9), 3));
        Assertions.assertEquals(StringUtilsSubstringTest.FOO, StringUtils.substring(StringUtilsSubstringTest.SENTENCE, 0, (-8)));
        Assertions.assertEquals("o", StringUtils.substring(StringUtilsSubstringTest.SENTENCE, (-9), (-8)));
        Assertions.assertEquals(StringUtilsSubstringTest.SENTENCE, StringUtils.substring(StringUtilsSubstringTest.SENTENCE, 0, 80));
        Assertions.assertEquals("", StringUtils.substring(StringUtilsSubstringTest.SENTENCE, 2, 2));
        Assertions.assertEquals("b", StringUtils.substring("abc", (-2), (-1)));
    }

    @Test
    public void testLeft_String() {
        Assertions.assertSame(null, StringUtils.left(null, (-1)));
        Assertions.assertSame(null, StringUtils.left(null, 0));
        Assertions.assertSame(null, StringUtils.left(null, 2));
        Assertions.assertEquals("", StringUtils.left("", (-1)));
        Assertions.assertEquals("", StringUtils.left("", 0));
        Assertions.assertEquals("", StringUtils.left("", 2));
        Assertions.assertEquals("", StringUtils.left(StringUtilsSubstringTest.FOOBAR, (-1)));
        Assertions.assertEquals("", StringUtils.left(StringUtilsSubstringTest.FOOBAR, 0));
        Assertions.assertEquals(StringUtilsSubstringTest.FOO, StringUtils.left(StringUtilsSubstringTest.FOOBAR, 3));
        Assertions.assertSame(StringUtilsSubstringTest.FOOBAR, StringUtils.left(StringUtilsSubstringTest.FOOBAR, 80));
    }

    @Test
    public void testRight_String() {
        Assertions.assertSame(null, StringUtils.right(null, (-1)));
        Assertions.assertSame(null, StringUtils.right(null, 0));
        Assertions.assertSame(null, StringUtils.right(null, 2));
        Assertions.assertEquals("", StringUtils.right("", (-1)));
        Assertions.assertEquals("", StringUtils.right("", 0));
        Assertions.assertEquals("", StringUtils.right("", 2));
        Assertions.assertEquals("", StringUtils.right(StringUtilsSubstringTest.FOOBAR, (-1)));
        Assertions.assertEquals("", StringUtils.right(StringUtilsSubstringTest.FOOBAR, 0));
        Assertions.assertEquals(StringUtilsSubstringTest.BAR, StringUtils.right(StringUtilsSubstringTest.FOOBAR, 3));
        Assertions.assertSame(StringUtilsSubstringTest.FOOBAR, StringUtils.right(StringUtilsSubstringTest.FOOBAR, 80));
    }

    @Test
    public void testMid_String() {
        Assertions.assertSame(null, StringUtils.mid(null, (-1), 0));
        Assertions.assertSame(null, StringUtils.mid(null, 0, (-1)));
        Assertions.assertSame(null, StringUtils.mid(null, 3, 0));
        Assertions.assertSame(null, StringUtils.mid(null, 3, 2));
        Assertions.assertEquals("", StringUtils.mid("", 0, (-1)));
        Assertions.assertEquals("", StringUtils.mid("", 0, 0));
        Assertions.assertEquals("", StringUtils.mid("", 0, 2));
        Assertions.assertEquals("", StringUtils.mid(StringUtilsSubstringTest.FOOBAR, 3, (-1)));
        Assertions.assertEquals("", StringUtils.mid(StringUtilsSubstringTest.FOOBAR, 3, 0));
        Assertions.assertEquals("b", StringUtils.mid(StringUtilsSubstringTest.FOOBAR, 3, 1));
        Assertions.assertEquals(StringUtilsSubstringTest.FOO, StringUtils.mid(StringUtilsSubstringTest.FOOBAR, 0, 3));
        Assertions.assertEquals(StringUtilsSubstringTest.BAR, StringUtils.mid(StringUtilsSubstringTest.FOOBAR, 3, 3));
        Assertions.assertEquals(StringUtilsSubstringTest.FOOBAR, StringUtils.mid(StringUtilsSubstringTest.FOOBAR, 0, 80));
        Assertions.assertEquals(StringUtilsSubstringTest.BAR, StringUtils.mid(StringUtilsSubstringTest.FOOBAR, 3, 80));
        Assertions.assertEquals("", StringUtils.mid(StringUtilsSubstringTest.FOOBAR, 9, 3));
        Assertions.assertEquals(StringUtilsSubstringTest.FOO, StringUtils.mid(StringUtilsSubstringTest.FOOBAR, (-1), 3));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSubstringBefore_StringString() {
        Assertions.assertEquals("foo", StringUtils.substringBefore("fooXXbarXXbaz", "XX"));
        Assertions.assertNull(StringUtils.substringBefore(null, null));
        Assertions.assertNull(StringUtils.substringBefore(null, ""));
        Assertions.assertNull(StringUtils.substringBefore(null, "XX"));
        Assertions.assertEquals("", StringUtils.substringBefore("", null));
        Assertions.assertEquals("", StringUtils.substringBefore("", ""));
        Assertions.assertEquals("", StringUtils.substringBefore("", "XX"));
        Assertions.assertEquals("foo", StringUtils.substringBefore("foo", null));
        Assertions.assertEquals("foo", StringUtils.substringBefore("foo", "b"));
        Assertions.assertEquals("f", StringUtils.substringBefore("foot", "o"));
        Assertions.assertEquals("", StringUtils.substringBefore("abc", "a"));
        Assertions.assertEquals("a", StringUtils.substringBefore("abcba", "b"));
        Assertions.assertEquals("ab", StringUtils.substringBefore("abc", "c"));
        Assertions.assertEquals("", StringUtils.substringBefore("abc", ""));
    }

    @Test
    public void testSubstringAfter_StringString() {
        Assertions.assertEquals("barXXbaz", StringUtils.substringAfter("fooXXbarXXbaz", "XX"));
        Assertions.assertNull(StringUtils.substringAfter(null, null));
        Assertions.assertNull(StringUtils.substringAfter(null, ""));
        Assertions.assertNull(StringUtils.substringAfter(null, "XX"));
        Assertions.assertEquals("", StringUtils.substringAfter("", null));
        Assertions.assertEquals("", StringUtils.substringAfter("", ""));
        Assertions.assertEquals("", StringUtils.substringAfter("", "XX"));
        Assertions.assertEquals("", StringUtils.substringAfter("foo", null));
        Assertions.assertEquals("ot", StringUtils.substringAfter("foot", "o"));
        Assertions.assertEquals("bc", StringUtils.substringAfter("abc", "a"));
        Assertions.assertEquals("cba", StringUtils.substringAfter("abcba", "b"));
        Assertions.assertEquals("", StringUtils.substringAfter("abc", "c"));
        Assertions.assertEquals("abc", StringUtils.substringAfter("abc", ""));
        Assertions.assertEquals("", StringUtils.substringAfter("abc", "d"));
    }

    @Test
    public void testSubstringBeforeLast_StringString() {
        Assertions.assertEquals("fooXXbar", StringUtils.substringBeforeLast("fooXXbarXXbaz", "XX"));
        Assertions.assertNull(StringUtils.substringBeforeLast(null, null));
        Assertions.assertNull(StringUtils.substringBeforeLast(null, ""));
        Assertions.assertNull(StringUtils.substringBeforeLast(null, "XX"));
        Assertions.assertEquals("", StringUtils.substringBeforeLast("", null));
        Assertions.assertEquals("", StringUtils.substringBeforeLast("", ""));
        Assertions.assertEquals("", StringUtils.substringBeforeLast("", "XX"));
        Assertions.assertEquals("foo", StringUtils.substringBeforeLast("foo", null));
        Assertions.assertEquals("foo", StringUtils.substringBeforeLast("foo", "b"));
        Assertions.assertEquals("fo", StringUtils.substringBeforeLast("foo", "o"));
        Assertions.assertEquals("abc\r\n", StringUtils.substringBeforeLast("abc\r\n", "d"));
        Assertions.assertEquals("abc", StringUtils.substringBeforeLast("abcdabc", "d"));
        Assertions.assertEquals("abcdabc", StringUtils.substringBeforeLast("abcdabcd", "d"));
        Assertions.assertEquals("a", StringUtils.substringBeforeLast("abc", "b"));
        Assertions.assertEquals("abc ", StringUtils.substringBeforeLast("abc \n", "\n"));
        Assertions.assertEquals("a", StringUtils.substringBeforeLast("a", null));
        Assertions.assertEquals("a", StringUtils.substringBeforeLast("a", ""));
        Assertions.assertEquals("", StringUtils.substringBeforeLast("a", "a"));
    }

    @Test
    public void testSubstringAfterLast_StringString() {
        Assertions.assertEquals("baz", StringUtils.substringAfterLast("fooXXbarXXbaz", "XX"));
        Assertions.assertNull(StringUtils.substringAfterLast(null, null));
        Assertions.assertNull(StringUtils.substringAfterLast(null, ""));
        Assertions.assertNull(StringUtils.substringAfterLast(null, "XX"));
        Assertions.assertEquals("", StringUtils.substringAfterLast("", null));
        Assertions.assertEquals("", StringUtils.substringAfterLast("", ""));
        Assertions.assertEquals("", StringUtils.substringAfterLast("", "a"));
        Assertions.assertEquals("", StringUtils.substringAfterLast("foo", null));
        Assertions.assertEquals("", StringUtils.substringAfterLast("foo", "b"));
        Assertions.assertEquals("t", StringUtils.substringAfterLast("foot", "o"));
        Assertions.assertEquals("bc", StringUtils.substringAfterLast("abc", "a"));
        Assertions.assertEquals("a", StringUtils.substringAfterLast("abcba", "b"));
        Assertions.assertEquals("", StringUtils.substringAfterLast("abc", "c"));
        Assertions.assertEquals("", StringUtils.substringAfterLast("", "d"));
        Assertions.assertEquals("", StringUtils.substringAfterLast("abc", ""));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSubstringBetween_StringString() {
        Assertions.assertNull(StringUtils.substringBetween(null, "tag"));
        Assertions.assertEquals("", StringUtils.substringBetween("", ""));
        Assertions.assertNull(StringUtils.substringBetween("", "abc"));
        Assertions.assertEquals("", StringUtils.substringBetween("    ", " "));
        Assertions.assertNull(StringUtils.substringBetween("abc", null));
        Assertions.assertEquals("", StringUtils.substringBetween("abc", ""));
        Assertions.assertNull(StringUtils.substringBetween("abc", "a"));
        Assertions.assertEquals("bc", StringUtils.substringBetween("abca", "a"));
        Assertions.assertEquals("bc", StringUtils.substringBetween("abcabca", "a"));
        Assertions.assertEquals("bar", StringUtils.substringBetween("\nbar\n", "\n"));
    }

    @Test
    public void testSubstringBetween_StringStringString() {
        Assertions.assertNull(StringUtils.substringBetween(null, "", ""));
        Assertions.assertNull(StringUtils.substringBetween("", null, ""));
        Assertions.assertNull(StringUtils.substringBetween("", "", null));
        Assertions.assertEquals("", StringUtils.substringBetween("", "", ""));
        Assertions.assertEquals("", StringUtils.substringBetween("foo", "", ""));
        Assertions.assertNull(StringUtils.substringBetween("foo", "", "]"));
        Assertions.assertNull(StringUtils.substringBetween("foo", "[", "]"));
        Assertions.assertEquals("", StringUtils.substringBetween("    ", " ", "  "));
        Assertions.assertEquals("bar", StringUtils.substringBetween("<foo>bar</foo>", "<foo>", "</foo>"));
    }

    /**
     * Tests the substringsBetween method that returns an String Array of substrings.
     */
    @Test
    public void testSubstringsBetween_StringStringString() {
        String[] results = StringUtils.substringsBetween("[one], [two], [three]", "[", "]");
        Assertions.assertEquals(3, results.length);
        Assertions.assertEquals("one", results[0]);
        Assertions.assertEquals("two", results[1]);
        Assertions.assertEquals("three", results[2]);
        results = StringUtils.substringsBetween("[one], [two], three", "[", "]");
        Assertions.assertEquals(2, results.length);
        Assertions.assertEquals("one", results[0]);
        Assertions.assertEquals("two", results[1]);
        results = StringUtils.substringsBetween("[one], [two], three]", "[", "]");
        Assertions.assertEquals(2, results.length);
        Assertions.assertEquals("one", results[0]);
        Assertions.assertEquals("two", results[1]);
        results = StringUtils.substringsBetween("[one], two], three]", "[", "]");
        Assertions.assertEquals(1, results.length);
        Assertions.assertEquals("one", results[0]);
        results = StringUtils.substringsBetween("one], two], [three]", "[", "]");
        Assertions.assertEquals(1, results.length);
        Assertions.assertEquals("three", results[0]);
        // 'ab hello ba' will match, but 'ab non ba' won't
        // this is because the 'a' is shared between the two and can't be matched twice
        results = StringUtils.substringsBetween("aabhellobabnonba", "ab", "ba");
        Assertions.assertEquals(1, results.length);
        Assertions.assertEquals("hello", results[0]);
        results = StringUtils.substringsBetween("one, two, three", "[", "]");
        Assertions.assertNull(results);
        results = StringUtils.substringsBetween("[one, two, three", "[", "]");
        Assertions.assertNull(results);
        results = StringUtils.substringsBetween("one, two, three]", "[", "]");
        Assertions.assertNull(results);
        results = StringUtils.substringsBetween("[one], [two], [three]", "[", null);
        Assertions.assertNull(results);
        results = StringUtils.substringsBetween("[one], [two], [three]", null, "]");
        Assertions.assertNull(results);
        results = StringUtils.substringsBetween("[one], [two], [three]", "", "");
        Assertions.assertNull(results);
        results = StringUtils.substringsBetween(null, "[", "]");
        Assertions.assertNull(results);
        results = StringUtils.substringsBetween("", "[", "]");
        Assertions.assertEquals(0, results.length);
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCountMatches_String() {
        Assertions.assertEquals(0, StringUtils.countMatches(null, null));
        Assertions.assertEquals(0, StringUtils.countMatches("blah", null));
        Assertions.assertEquals(0, StringUtils.countMatches(null, "DD"));
        Assertions.assertEquals(0, StringUtils.countMatches("x", ""));
        Assertions.assertEquals(0, StringUtils.countMatches("", ""));
        Assertions.assertEquals(3, StringUtils.countMatches("one long someone sentence of one", "one"));
        Assertions.assertEquals(0, StringUtils.countMatches("one long someone sentence of one", "two"));
        Assertions.assertEquals(4, StringUtils.countMatches("oooooooooooo", "ooo"));
    }

    @Test
    public void testCountMatches_char() {
        Assertions.assertEquals(0, StringUtils.countMatches(null, 'D'));
        Assertions.assertEquals(5, StringUtils.countMatches("one long someone sentence of one", ' '));
        Assertions.assertEquals(6, StringUtils.countMatches("one long someone sentence of one", 'o'));
        Assertions.assertEquals(4, StringUtils.countMatches("oooooooooooo", "ooo"));
    }
}

