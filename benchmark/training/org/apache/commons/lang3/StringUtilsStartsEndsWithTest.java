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
 * Unit tests {@link org.apache.commons.lang3.StringUtils} - StartsWith/EndsWith methods
 */
public class StringUtilsStartsEndsWithTest {
    private static final String foo = "foo";

    private static final String bar = "bar";

    private static final String foobar = "foobar";

    private static final String FOO = "FOO";

    private static final String BAR = "BAR";

    private static final String FOOBAR = "FOOBAR";

    // -----------------------------------------------------------------------
    /**
     * Test StringUtils.startsWith()
     */
    @Test
    public void testStartsWith() {
        Assertions.assertTrue(StringUtils.startsWith(null, null), "startsWith(null, null)");
        Assertions.assertFalse(StringUtils.startsWith(StringUtilsStartsEndsWithTest.FOOBAR, null), "startsWith(FOOBAR, null)");
        Assertions.assertFalse(StringUtils.startsWith(null, StringUtilsStartsEndsWithTest.FOO), "startsWith(null, FOO)");
        Assertions.assertTrue(StringUtils.startsWith(StringUtilsStartsEndsWithTest.FOOBAR, ""), "startsWith(FOOBAR, \"\")");
        Assertions.assertTrue(StringUtils.startsWith(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.foo), "startsWith(foobar, foo)");
        Assertions.assertTrue(StringUtils.startsWith(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.FOO), "startsWith(FOOBAR, FOO)");
        Assertions.assertFalse(StringUtils.startsWith(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.FOO), "startsWith(foobar, FOO)");
        Assertions.assertFalse(StringUtils.startsWith(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.foo), "startsWith(FOOBAR, foo)");
        Assertions.assertFalse(StringUtils.startsWith(StringUtilsStartsEndsWithTest.foo, StringUtilsStartsEndsWithTest.foobar), "startsWith(foo, foobar)");
        Assertions.assertFalse(StringUtils.startsWith(StringUtilsStartsEndsWithTest.bar, StringUtilsStartsEndsWithTest.foobar), "startsWith(foo, foobar)");
        Assertions.assertFalse(StringUtils.startsWith(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.bar), "startsWith(foobar, bar)");
        Assertions.assertFalse(StringUtils.startsWith(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.BAR), "startsWith(FOOBAR, BAR)");
        Assertions.assertFalse(StringUtils.startsWith(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.BAR), "startsWith(foobar, BAR)");
        Assertions.assertFalse(StringUtils.startsWith(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.bar), "startsWith(FOOBAR, bar)");
    }

    /**
     * Test StringUtils.testStartsWithIgnoreCase()
     */
    @Test
    public void testStartsWithIgnoreCase() {
        Assertions.assertTrue(StringUtils.startsWithIgnoreCase(null, null), "startsWithIgnoreCase(null, null)");
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, null), "startsWithIgnoreCase(FOOBAR, null)");
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(null, StringUtilsStartsEndsWithTest.FOO), "startsWithIgnoreCase(null, FOO)");
        Assertions.assertTrue(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, ""), "startsWithIgnoreCase(FOOBAR, \"\")");
        Assertions.assertTrue(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.foo), "startsWithIgnoreCase(foobar, foo)");
        Assertions.assertTrue(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.FOO), "startsWithIgnoreCase(FOOBAR, FOO)");
        Assertions.assertTrue(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.FOO), "startsWithIgnoreCase(foobar, FOO)");
        Assertions.assertTrue(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.foo), "startsWithIgnoreCase(FOOBAR, foo)");
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.foo, StringUtilsStartsEndsWithTest.foobar), "startsWithIgnoreCase(foo, foobar)");
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.bar, StringUtilsStartsEndsWithTest.foobar), "startsWithIgnoreCase(foo, foobar)");
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.bar), "startsWithIgnoreCase(foobar, bar)");
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.BAR), "startsWithIgnoreCase(FOOBAR, BAR)");
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.BAR), "startsWithIgnoreCase(foobar, BAR)");
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.bar), "startsWithIgnoreCase(FOOBAR, bar)");
    }

    @Test
    public void testStartsWithAny() {
        Assertions.assertFalse(StringUtils.startsWithAny(null, ((String[]) (null))));
        Assertions.assertFalse(StringUtils.startsWithAny(null, "abc"));
        Assertions.assertFalse(StringUtils.startsWithAny("abcxyz", ((String[]) (null))));
        Assertions.assertFalse(StringUtils.startsWithAny("abcxyz"));
        Assertions.assertTrue(StringUtils.startsWithAny("abcxyz", "abc"));
        Assertions.assertTrue(StringUtils.startsWithAny("abcxyz", null, "xyz", "abc"));
        Assertions.assertFalse(StringUtils.startsWithAny("abcxyz", null, "xyz", "abcd"));
        Assertions.assertTrue(StringUtils.startsWithAny("abcxyz", new String[]{ "" }));
        Assertions.assertFalse(StringUtils.startsWithAny("abcxyz", null, "xyz", "ABCX"));
        Assertions.assertFalse(StringUtils.startsWithAny("ABCXYZ", null, "xyz", "abc"));
        Assertions.assertTrue(StringUtils.startsWithAny("abcxyz", new StringBuilder("xyz"), new StringBuffer("abc")), "StringUtils.startsWithAny(abcxyz, StringBuilder(xyz), StringBuffer(abc))");
        Assertions.assertTrue(StringUtils.startsWithAny(new StringBuffer("abcxyz"), new StringBuilder("xyz"), new StringBuffer("abc")), "StringUtils.startsWithAny(StringBuffer(abcxyz), StringBuilder(xyz), StringBuffer(abc))");
    }

    /**
     * Test StringUtils.endsWith()
     */
    @Test
    public void testEndsWith() {
        Assertions.assertTrue(StringUtils.endsWith(null, null), "endsWith(null, null)");
        Assertions.assertFalse(StringUtils.endsWith(StringUtilsStartsEndsWithTest.FOOBAR, null), "endsWith(FOOBAR, null)");
        Assertions.assertFalse(StringUtils.endsWith(null, StringUtilsStartsEndsWithTest.FOO), "endsWith(null, FOO)");
        Assertions.assertTrue(StringUtils.endsWith(StringUtilsStartsEndsWithTest.FOOBAR, ""), "endsWith(FOOBAR, \"\")");
        Assertions.assertFalse(StringUtils.endsWith(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.foo), "endsWith(foobar, foo)");
        Assertions.assertFalse(StringUtils.endsWith(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.FOO), "endsWith(FOOBAR, FOO)");
        Assertions.assertFalse(StringUtils.endsWith(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.FOO), "endsWith(foobar, FOO)");
        Assertions.assertFalse(StringUtils.endsWith(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.foo), "endsWith(FOOBAR, foo)");
        Assertions.assertFalse(StringUtils.endsWith(StringUtilsStartsEndsWithTest.foo, StringUtilsStartsEndsWithTest.foobar), "endsWith(foo, foobar)");
        Assertions.assertFalse(StringUtils.endsWith(StringUtilsStartsEndsWithTest.bar, StringUtilsStartsEndsWithTest.foobar), "endsWith(foo, foobar)");
        Assertions.assertTrue(StringUtils.endsWith(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.bar), "endsWith(foobar, bar)");
        Assertions.assertTrue(StringUtils.endsWith(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.BAR), "endsWith(FOOBAR, BAR)");
        Assertions.assertFalse(StringUtils.endsWith(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.BAR), "endsWith(foobar, BAR)");
        Assertions.assertFalse(StringUtils.endsWith(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.bar), "endsWith(FOOBAR, bar)");
        // "alpha, beta, gamma, delta".endsWith("delta")
        Assertions.assertTrue(StringUtils.endsWith("\u03b1\u03b2\u03b3\u03b4", "\u03b4"), "endsWith(\u03b1\u03b2\u03b3\u03b4, \u03b4)");
        // "alpha, beta, gamma, delta".endsWith("gamma, DELTA")
        Assertions.assertFalse(StringUtils.endsWith("\u03b1\u03b2\u03b3\u03b4", "\u03b3\u0394"), "endsWith(\u03b1\u03b2\u03b3\u03b4, \u03b3\u0394)");
    }

    /**
     * Test StringUtils.endsWithIgnoreCase()
     */
    @Test
    public void testEndsWithIgnoreCase() {
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase(null, null), "endsWithIgnoreCase(null, null)");
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, null), "endsWithIgnoreCase(FOOBAR, null)");
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(null, StringUtilsStartsEndsWithTest.FOO), "endsWithIgnoreCase(null, FOO)");
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, ""), "endsWithIgnoreCase(FOOBAR, \"\")");
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.foo), "endsWithIgnoreCase(foobar, foo)");
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.FOO), "endsWithIgnoreCase(FOOBAR, FOO)");
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.FOO), "endsWithIgnoreCase(foobar, FOO)");
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.foo), "endsWithIgnoreCase(FOOBAR, foo)");
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.foo, StringUtilsStartsEndsWithTest.foobar), "endsWithIgnoreCase(foo, foobar)");
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.bar, StringUtilsStartsEndsWithTest.foobar), "endsWithIgnoreCase(foo, foobar)");
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.bar), "endsWithIgnoreCase(foobar, bar)");
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.BAR), "endsWithIgnoreCase(FOOBAR, BAR)");
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.foobar, StringUtilsStartsEndsWithTest.BAR), "endsWithIgnoreCase(foobar, BAR)");
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase(StringUtilsStartsEndsWithTest.FOOBAR, StringUtilsStartsEndsWithTest.bar), "endsWithIgnoreCase(FOOBAR, bar)");
        // javadoc
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase("abcdef", "def"));
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase("ABCDEF", "def"));
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase("ABCDEF", "cde"));
        // "alpha, beta, gamma, delta".endsWith("DELTA")
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase("\u03b1\u03b2\u03b3\u03b4", "\u0394"), "endsWith(\u03b1\u03b2\u03b3\u03b4, \u0394)");
        // "alpha, beta, gamma, delta".endsWith("GAMMA")
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase("\u03b1\u03b2\u03b3\u03b4", "\u0393"), "endsWith(\u03b1\u03b2\u03b3\u03b4, \u0393)");
    }

    @Test
    public void testEndsWithAny() {
        Assertions.assertFalse(StringUtils.endsWithAny(null, ((String) (null))), "StringUtils.endsWithAny(null, null)");
        Assertions.assertFalse(StringUtils.endsWithAny(null, new String[]{ "abc" }), "StringUtils.endsWithAny(null, new String[] {abc})");
        Assertions.assertFalse(StringUtils.endsWithAny("abcxyz", ((String) (null))), "StringUtils.endsWithAny(abcxyz, null)");
        Assertions.assertTrue(StringUtils.endsWithAny("abcxyz", new String[]{ "" }), "StringUtils.endsWithAny(abcxyz, new String[] {\"\"})");
        Assertions.assertTrue(StringUtils.endsWithAny("abcxyz", new String[]{ "xyz" }), "StringUtils.endsWithAny(abcxyz, new String[] {xyz})");
        Assertions.assertTrue(StringUtils.endsWithAny("abcxyz", new String[]{ null, "xyz", "abc" }), "StringUtils.endsWithAny(abcxyz, new String[] {null, xyz, abc})");
        Assertions.assertFalse(StringUtils.endsWithAny("defg", new String[]{ null, "xyz", "abc" }), "StringUtils.endsWithAny(defg, new String[] {null, xyz, abc})");
        Assertions.assertTrue(StringUtils.endsWithAny("abcXYZ", "def", "XYZ"));
        Assertions.assertFalse(StringUtils.endsWithAny("abcXYZ", "def", "xyz"));
        Assertions.assertTrue(StringUtils.endsWithAny("abcXYZ", "def", "YZ"));
        /* Type null of the last argument to method endsWithAny(CharSequence, CharSequence...)
        doesn't exactly match the vararg parameter type.
        Cast to CharSequence[] to confirm the non-varargs invocation,
        or pass individual arguments of type CharSequence for a varargs invocation.

        assertFalse(StringUtils.endsWithAny("abcXYZ", null)); // replace with specific types to avoid warning
         */
        Assertions.assertFalse(StringUtils.endsWithAny("abcXYZ", ((CharSequence) (null))));
        Assertions.assertFalse(StringUtils.endsWithAny("abcXYZ", ((CharSequence[]) (null))));
        Assertions.assertTrue(StringUtils.endsWithAny("abcXYZ", ""));
        Assertions.assertTrue(StringUtils.endsWithAny("abcxyz", new StringBuilder("abc"), new StringBuffer("xyz")), "StringUtils.endsWithAny(abcxyz, StringBuilder(abc), StringBuffer(xyz))");
        Assertions.assertTrue(StringUtils.endsWithAny(new StringBuffer("abcxyz"), new StringBuilder("abc"), new StringBuffer("xyz")), "StringUtils.endsWithAny(StringBuffer(abcxyz), StringBuilder(abc), StringBuffer(xyz))");
    }
}

