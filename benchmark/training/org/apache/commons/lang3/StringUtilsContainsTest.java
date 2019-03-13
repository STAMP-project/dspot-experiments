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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;


/**
 * Unit tests {@link org.apache.commons.lang3.StringUtils} - Contains methods
 */
public class StringUtilsContainsTest {
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

    /**
     * Incomplete supplementary character U+20000, high surrogate only.
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    private static final String CharUSuppCharHigh = "\udc00";

    /**
     * Incomplete supplementary character U+20000, low surrogate only.
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    private static final String CharUSuppCharLow = "\ud840";

    @Test
    public void testContains_Char() {
        Assertions.assertFalse(StringUtils.contains(null, ' '));
        Assertions.assertFalse(StringUtils.contains("", ' '));
        Assertions.assertFalse(StringUtils.contains("", null));
        Assertions.assertFalse(StringUtils.contains(null, null));
        Assertions.assertTrue(StringUtils.contains("abc", 'a'));
        Assertions.assertTrue(StringUtils.contains("abc", 'b'));
        Assertions.assertTrue(StringUtils.contains("abc", 'c'));
        Assertions.assertFalse(StringUtils.contains("abc", 'z'));
    }

    @Test
    public void testContains_String() {
        Assertions.assertFalse(StringUtils.contains(null, null));
        Assertions.assertFalse(StringUtils.contains(null, ""));
        Assertions.assertFalse(StringUtils.contains(null, "a"));
        Assertions.assertFalse(StringUtils.contains("", null));
        Assertions.assertTrue(StringUtils.contains("", ""));
        Assertions.assertFalse(StringUtils.contains("", "a"));
        Assertions.assertTrue(StringUtils.contains("abc", "a"));
        Assertions.assertTrue(StringUtils.contains("abc", "b"));
        Assertions.assertTrue(StringUtils.contains("abc", "c"));
        Assertions.assertTrue(StringUtils.contains("abc", "abc"));
        Assertions.assertFalse(StringUtils.contains("abc", "z"));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContains_StringWithBadSupplementaryChars() {
        // Test edge case: 1/2 of a (broken) supplementary char
        Assertions.assertFalse(StringUtils.contains(StringUtilsContainsTest.CharUSuppCharHigh, StringUtilsContainsTest.CharU20001));
        Assertions.assertFalse(StringUtils.contains(StringUtilsContainsTest.CharUSuppCharLow, StringUtilsContainsTest.CharU20001));
        Assertions.assertFalse(StringUtils.contains(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharHigh));
        Assertions.assertEquals(0, StringUtilsContainsTest.CharU20001.indexOf(StringUtilsContainsTest.CharUSuppCharLow));
        Assertions.assertTrue(StringUtils.contains(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharLow));
        Assertions.assertTrue(StringUtils.contains((((StringUtilsContainsTest.CharU20001) + (StringUtilsContainsTest.CharUSuppCharLow)) + "a"), "a"));
        Assertions.assertTrue(StringUtils.contains((((StringUtilsContainsTest.CharU20001) + (StringUtilsContainsTest.CharUSuppCharHigh)) + "a"), "a"));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContains_StringWithSupplementaryChars() {
        Assertions.assertTrue(StringUtils.contains(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20000));
        Assertions.assertTrue(StringUtils.contains(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20001));
        Assertions.assertTrue(StringUtils.contains(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20000));
        Assertions.assertFalse(StringUtils.contains(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20001));
    }

    @Test
    public void testContainsAny_StringCharArray() {
        Assertions.assertFalse(StringUtils.containsAny(null, ((char[]) (null))));
        Assertions.assertFalse(StringUtils.containsAny(null, new char[0]));
        Assertions.assertFalse(StringUtils.containsAny(null, 'a', 'b'));
        Assertions.assertFalse(StringUtils.containsAny("", ((char[]) (null))));
        Assertions.assertFalse(StringUtils.containsAny("", new char[0]));
        Assertions.assertFalse(StringUtils.containsAny("", 'a', 'b'));
        Assertions.assertFalse(StringUtils.containsAny("zzabyycdxx", ((char[]) (null))));
        Assertions.assertFalse(StringUtils.containsAny("zzabyycdxx", new char[0]));
        Assertions.assertTrue(StringUtils.containsAny("zzabyycdxx", 'z', 'a'));
        Assertions.assertTrue(StringUtils.containsAny("zzabyycdxx", 'b', 'y'));
        Assertions.assertTrue(StringUtils.containsAny("zzabyycdxx", 'z', 'y'));
        Assertions.assertFalse(StringUtils.containsAny("ab", 'z'));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContainsAny_StringCharArrayWithBadSupplementaryChars() {
        // Test edge case: 1/2 of a (broken) supplementary char
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharUSuppCharHigh, StringUtilsContainsTest.CharU20001.toCharArray()));
        Assertions.assertFalse(StringUtils.containsAny((("abc" + (StringUtilsContainsTest.CharUSuppCharHigh)) + "xyz"), StringUtilsContainsTest.CharU20001.toCharArray()));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharUSuppCharLow.indexOf(StringUtilsContainsTest.CharU20001));
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharUSuppCharLow, StringUtilsContainsTest.CharU20001.toCharArray()));
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharHigh.toCharArray()));
        Assertions.assertEquals(0, StringUtilsContainsTest.CharU20001.indexOf(StringUtilsContainsTest.CharUSuppCharLow));
        Assertions.assertTrue(StringUtils.containsAny(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharLow.toCharArray()));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContainsAny_StringCharArrayWithSupplementaryChars() {
        Assertions.assertTrue(StringUtils.containsAny(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20000.toCharArray()));
        Assertions.assertTrue(StringUtils.containsAny((("a" + (StringUtilsContainsTest.CharU20000)) + (StringUtilsContainsTest.CharU20001)), "a".toCharArray()));
        Assertions.assertTrue(StringUtils.containsAny((((StringUtilsContainsTest.CharU20000) + "a") + (StringUtilsContainsTest.CharU20001)), "a".toCharArray()));
        Assertions.assertTrue(StringUtils.containsAny((((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)) + "a"), "a".toCharArray()));
        Assertions.assertTrue(StringUtils.containsAny(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20001.toCharArray()));
        Assertions.assertTrue(StringUtils.containsAny(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20000.toCharArray()));
        // Sanity check:
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001));
        Assertions.assertEquals(0, StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001.charAt(0)));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001.charAt(1)));
        // Test:
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20001.toCharArray()));
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharU20000.toCharArray()));
    }

    @Test
    public void testContainsAny_StringString() {
        Assertions.assertFalse(StringUtils.containsAny(null, ((String) (null))));
        Assertions.assertFalse(StringUtils.containsAny(null, ""));
        Assertions.assertFalse(StringUtils.containsAny(null, "ab"));
        Assertions.assertFalse(StringUtils.containsAny("", ((String) (null))));
        Assertions.assertFalse(StringUtils.containsAny("", ""));
        Assertions.assertFalse(StringUtils.containsAny("", "ab"));
        Assertions.assertFalse(StringUtils.containsAny("zzabyycdxx", ((String) (null))));
        Assertions.assertFalse(StringUtils.containsAny("zzabyycdxx", ""));
        Assertions.assertTrue(StringUtils.containsAny("zzabyycdxx", "za"));
        Assertions.assertTrue(StringUtils.containsAny("zzabyycdxx", "by"));
        Assertions.assertTrue(StringUtils.containsAny("zzabyycdxx", "zy"));
        Assertions.assertFalse(StringUtils.containsAny("ab", "z"));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContainsAny_StringWithBadSupplementaryChars() {
        // Test edge case: 1/2 of a (broken) supplementary char
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharUSuppCharHigh, StringUtilsContainsTest.CharU20001));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharUSuppCharLow.indexOf(StringUtilsContainsTest.CharU20001));
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharUSuppCharLow, StringUtilsContainsTest.CharU20001));
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharHigh));
        Assertions.assertEquals(0, StringUtilsContainsTest.CharU20001.indexOf(StringUtilsContainsTest.CharUSuppCharLow));
        Assertions.assertTrue(StringUtils.containsAny(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharLow));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContainsAny_StringWithSupplementaryChars() {
        Assertions.assertTrue(StringUtils.containsAny(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20000));
        Assertions.assertTrue(StringUtils.containsAny(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20001));
        Assertions.assertTrue(StringUtils.containsAny(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20000));
        // Sanity check:
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001));
        Assertions.assertEquals(0, StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001.charAt(0)));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001.charAt(1)));
        // Test:
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20001));
        Assertions.assertFalse(StringUtils.containsAny(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharU20000));
    }

    @Test
    public void testContainsAny_StringStringArray() {
        Assertions.assertFalse(StringUtils.containsAny(null, ((String[]) (null))));
        Assertions.assertFalse(StringUtils.containsAny(null, new String[0]));
        Assertions.assertFalse(StringUtils.containsAny(null, new String[]{ "hello" }));
        Assertions.assertFalse(StringUtils.containsAny("", ((String[]) (null))));
        Assertions.assertFalse(StringUtils.containsAny("", new String[0]));
        Assertions.assertFalse(StringUtils.containsAny("", new String[]{ "hello" }));
        Assertions.assertFalse(StringUtils.containsAny("hello, goodbye", ((String[]) (null))));
        Assertions.assertFalse(StringUtils.containsAny("hello, goodbye", new String[0]));
        Assertions.assertTrue(StringUtils.containsAny("hello, goodbye", new String[]{ "hello", "goodbye" }));
        Assertions.assertTrue(StringUtils.containsAny("hello, goodbye", new String[]{ "hello", "Goodbye" }));
        Assertions.assertFalse(StringUtils.containsAny("hello, goodbye", new String[]{ "Hello", "Goodbye" }));
        Assertions.assertFalse(StringUtils.containsAny("hello, goodbye", new String[]{ "Hello", null }));
        Assertions.assertFalse(StringUtils.containsAny("hello, null", new String[]{ "Hello", null }));
        // Javadoc examples:
        Assertions.assertTrue(StringUtils.containsAny("abcd", "ab", null));
        Assertions.assertTrue(StringUtils.containsAny("abcd", "ab", "cd"));
        Assertions.assertTrue(StringUtils.containsAny("abc", "d", "abc"));
    }

    @DefaultLocale(language = "de", country = "DE")
    @Test
    public void testContainsIgnoreCase_LocaleIndependence() {
        final Locale[] locales = new Locale[]{ Locale.ENGLISH, new Locale("tr"), Locale.getDefault() };
        final String[][] tdata = new String[][]{ new String[]{ "i", "I" }, new String[]{ "I", "i" }, new String[]{ "\u03c2", "\u03c3" }, new String[]{ "\u03a3", "\u03c2" }, new String[]{ "\u03a3", "\u03c3" } };
        final String[][] fdata = new String[][]{ new String[]{ "\u00df", "SS" } };
        for (final Locale testLocale : locales) {
            Locale.setDefault(testLocale);
            for (int j = 0; j < (tdata.length); j++) {
                Assertions.assertTrue(StringUtils.containsIgnoreCase(tdata[j][0], tdata[j][1]), (((((((Locale.getDefault()) + ": ") + j) + " ") + (tdata[j][0])) + " ") + (tdata[j][1])));
            }
            for (int j = 0; j < (fdata.length); j++) {
                Assertions.assertFalse(StringUtils.containsIgnoreCase(fdata[j][0], fdata[j][1]), (((((((Locale.getDefault()) + ": ") + j) + " ") + (fdata[j][0])) + " ") + (fdata[j][1])));
            }
        }
    }

    @Test
    public void testContainsIgnoreCase_StringString() {
        Assertions.assertFalse(StringUtils.containsIgnoreCase(null, null));
        // Null tests
        Assertions.assertFalse(StringUtils.containsIgnoreCase(null, ""));
        Assertions.assertFalse(StringUtils.containsIgnoreCase(null, "a"));
        Assertions.assertFalse(StringUtils.containsIgnoreCase(null, "abc"));
        Assertions.assertFalse(StringUtils.containsIgnoreCase("", null));
        Assertions.assertFalse(StringUtils.containsIgnoreCase("a", null));
        Assertions.assertFalse(StringUtils.containsIgnoreCase("abc", null));
        // Match len = 0
        Assertions.assertTrue(StringUtils.containsIgnoreCase("", ""));
        Assertions.assertTrue(StringUtils.containsIgnoreCase("a", ""));
        Assertions.assertTrue(StringUtils.containsIgnoreCase("abc", ""));
        // Match len = 1
        Assertions.assertFalse(StringUtils.containsIgnoreCase("", "a"));
        Assertions.assertTrue(StringUtils.containsIgnoreCase("a", "a"));
        Assertions.assertTrue(StringUtils.containsIgnoreCase("abc", "a"));
        Assertions.assertFalse(StringUtils.containsIgnoreCase("", "A"));
        Assertions.assertTrue(StringUtils.containsIgnoreCase("a", "A"));
        Assertions.assertTrue(StringUtils.containsIgnoreCase("abc", "A"));
        // Match len > 1
        Assertions.assertFalse(StringUtils.containsIgnoreCase("", "abc"));
        Assertions.assertFalse(StringUtils.containsIgnoreCase("a", "abc"));
        Assertions.assertTrue(StringUtils.containsIgnoreCase("xabcz", "abc"));
        Assertions.assertFalse(StringUtils.containsIgnoreCase("", "ABC"));
        Assertions.assertFalse(StringUtils.containsIgnoreCase("a", "ABC"));
        Assertions.assertTrue(StringUtils.containsIgnoreCase("xabcz", "ABC"));
    }

    @Test
    public void testContainsNone_CharArray() {
        final String str1 = "a";
        final String str2 = "b";
        final String str3 = "ab.";
        final char[] chars1 = new char[]{ 'b' };
        final char[] chars2 = new char[]{ '.' };
        final char[] chars3 = new char[]{ 'c', 'd' };
        final char[] emptyChars = new char[0];
        Assertions.assertTrue(StringUtils.containsNone(null, ((char[]) (null))));
        Assertions.assertTrue(StringUtils.containsNone("", ((char[]) (null))));
        Assertions.assertTrue(StringUtils.containsNone(null, emptyChars));
        Assertions.assertTrue(StringUtils.containsNone(str1, emptyChars));
        Assertions.assertTrue(StringUtils.containsNone("", emptyChars));
        Assertions.assertTrue(StringUtils.containsNone("", chars1));
        Assertions.assertTrue(StringUtils.containsNone(str1, chars1));
        Assertions.assertTrue(StringUtils.containsNone(str1, chars2));
        Assertions.assertTrue(StringUtils.containsNone(str1, chars3));
        Assertions.assertFalse(StringUtils.containsNone(str2, chars1));
        Assertions.assertTrue(StringUtils.containsNone(str2, chars2));
        Assertions.assertTrue(StringUtils.containsNone(str2, chars3));
        Assertions.assertFalse(StringUtils.containsNone(str3, chars1));
        Assertions.assertFalse(StringUtils.containsNone(str3, chars2));
        Assertions.assertTrue(StringUtils.containsNone(str3, chars3));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContainsNone_CharArrayWithBadSupplementaryChars() {
        // Test edge case: 1/2 of a (broken) supplementary char
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharUSuppCharHigh, StringUtilsContainsTest.CharU20001.toCharArray()));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharUSuppCharLow.indexOf(StringUtilsContainsTest.CharU20001));
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharUSuppCharLow, StringUtilsContainsTest.CharU20001.toCharArray()));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20001.indexOf(StringUtilsContainsTest.CharUSuppCharHigh));
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharHigh.toCharArray()));
        Assertions.assertEquals(0, StringUtilsContainsTest.CharU20001.indexOf(StringUtilsContainsTest.CharUSuppCharLow));
        Assertions.assertFalse(StringUtils.containsNone(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharLow.toCharArray()));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContainsNone_CharArrayWithSupplementaryChars() {
        Assertions.assertFalse(StringUtils.containsNone(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20000.toCharArray()));
        Assertions.assertFalse(StringUtils.containsNone(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20001.toCharArray()));
        Assertions.assertFalse(StringUtils.containsNone(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20000.toCharArray()));
        // Sanity check:
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001));
        Assertions.assertEquals(0, StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001.charAt(0)));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001.charAt(1)));
        // Test:
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20001.toCharArray()));
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharU20000.toCharArray()));
    }

    @Test
    public void testContainsNone_String() {
        final String str1 = "a";
        final String str2 = "b";
        final String str3 = "ab.";
        final String chars1 = "b";
        final String chars2 = ".";
        final String chars3 = "cd";
        Assertions.assertTrue(StringUtils.containsNone(null, ((String) (null))));
        Assertions.assertTrue(StringUtils.containsNone("", ((String) (null))));
        Assertions.assertTrue(StringUtils.containsNone(null, ""));
        Assertions.assertTrue(StringUtils.containsNone(str1, ""));
        Assertions.assertTrue(StringUtils.containsNone("", ""));
        Assertions.assertTrue(StringUtils.containsNone("", chars1));
        Assertions.assertTrue(StringUtils.containsNone(str1, chars1));
        Assertions.assertTrue(StringUtils.containsNone(str1, chars2));
        Assertions.assertTrue(StringUtils.containsNone(str1, chars3));
        Assertions.assertFalse(StringUtils.containsNone(str2, chars1));
        Assertions.assertTrue(StringUtils.containsNone(str2, chars2));
        Assertions.assertTrue(StringUtils.containsNone(str2, chars3));
        Assertions.assertFalse(StringUtils.containsNone(str3, chars1));
        Assertions.assertFalse(StringUtils.containsNone(str3, chars2));
        Assertions.assertTrue(StringUtils.containsNone(str3, chars3));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContainsNone_StringWithBadSupplementaryChars() {
        // Test edge case: 1/2 of a (broken) supplementary char
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharUSuppCharHigh, StringUtilsContainsTest.CharU20001));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharUSuppCharLow.indexOf(StringUtilsContainsTest.CharU20001));
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharUSuppCharLow, StringUtilsContainsTest.CharU20001));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20001.indexOf(StringUtilsContainsTest.CharUSuppCharHigh));
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharHigh));
        Assertions.assertEquals(0, StringUtilsContainsTest.CharU20001.indexOf(StringUtilsContainsTest.CharUSuppCharLow));
        Assertions.assertFalse(StringUtils.containsNone(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharUSuppCharLow));
    }

    /**
     * See http://www.oracle.com/technetwork/articles/javase/supplementary-142654.html
     */
    @Test
    public void testContainsNone_StringWithSupplementaryChars() {
        Assertions.assertFalse(StringUtils.containsNone(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20000));
        Assertions.assertFalse(StringUtils.containsNone(((StringUtilsContainsTest.CharU20000) + (StringUtilsContainsTest.CharU20001)), StringUtilsContainsTest.CharU20001));
        Assertions.assertFalse(StringUtils.containsNone(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20000));
        // Sanity check:
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001));
        Assertions.assertEquals(0, StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001.charAt(0)));
        Assertions.assertEquals((-1), StringUtilsContainsTest.CharU20000.indexOf(StringUtilsContainsTest.CharU20001.charAt(1)));
        // Test:
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharU20000, StringUtilsContainsTest.CharU20001));
        Assertions.assertTrue(StringUtils.containsNone(StringUtilsContainsTest.CharU20001, StringUtilsContainsTest.CharU20000));
    }

    @Test
    public void testContainsOnly_CharArray() {
        final String str1 = "a";
        final String str2 = "b";
        final String str3 = "ab";
        final char[] chars1 = new char[]{ 'b' };
        final char[] chars2 = new char[]{ 'a' };
        final char[] chars3 = new char[]{ 'a', 'b' };
        final char[] emptyChars = new char[0];
        Assertions.assertFalse(StringUtils.containsOnly(null, ((char[]) (null))));
        Assertions.assertFalse(StringUtils.containsOnly("", ((char[]) (null))));
        Assertions.assertFalse(StringUtils.containsOnly(null, emptyChars));
        Assertions.assertFalse(StringUtils.containsOnly(str1, emptyChars));
        Assertions.assertTrue(StringUtils.containsOnly("", emptyChars));
        Assertions.assertTrue(StringUtils.containsOnly("", chars1));
        Assertions.assertFalse(StringUtils.containsOnly(str1, chars1));
        Assertions.assertTrue(StringUtils.containsOnly(str1, chars2));
        Assertions.assertTrue(StringUtils.containsOnly(str1, chars3));
        Assertions.assertTrue(StringUtils.containsOnly(str2, chars1));
        Assertions.assertFalse(StringUtils.containsOnly(str2, chars2));
        Assertions.assertTrue(StringUtils.containsOnly(str2, chars3));
        Assertions.assertFalse(StringUtils.containsOnly(str3, chars1));
        Assertions.assertFalse(StringUtils.containsOnly(str3, chars2));
        Assertions.assertTrue(StringUtils.containsOnly(str3, chars3));
    }

    @Test
    public void testContainsOnly_String() {
        final String str1 = "a";
        final String str2 = "b";
        final String str3 = "ab";
        final String chars1 = "b";
        final String chars2 = "a";
        final String chars3 = "ab";
        Assertions.assertFalse(StringUtils.containsOnly(null, ((String) (null))));
        Assertions.assertFalse(StringUtils.containsOnly("", ((String) (null))));
        Assertions.assertFalse(StringUtils.containsOnly(null, ""));
        Assertions.assertFalse(StringUtils.containsOnly(str1, ""));
        Assertions.assertTrue(StringUtils.containsOnly("", ""));
        Assertions.assertTrue(StringUtils.containsOnly("", chars1));
        Assertions.assertFalse(StringUtils.containsOnly(str1, chars1));
        Assertions.assertTrue(StringUtils.containsOnly(str1, chars2));
        Assertions.assertTrue(StringUtils.containsOnly(str1, chars3));
        Assertions.assertTrue(StringUtils.containsOnly(str2, chars1));
        Assertions.assertFalse(StringUtils.containsOnly(str2, chars2));
        Assertions.assertTrue(StringUtils.containsOnly(str2, chars3));
        Assertions.assertFalse(StringUtils.containsOnly(str3, chars1));
        Assertions.assertFalse(StringUtils.containsOnly(str3, chars2));
        Assertions.assertTrue(StringUtils.containsOnly(str3, chars3));
    }

    @Test
    public void testContainsWhitespace() {
        Assertions.assertFalse(StringUtils.containsWhitespace(""));
        Assertions.assertTrue(StringUtils.containsWhitespace(" "));
        Assertions.assertFalse(StringUtils.containsWhitespace("a"));
        Assertions.assertTrue(StringUtils.containsWhitespace("a "));
        Assertions.assertTrue(StringUtils.containsWhitespace(" a"));
        Assertions.assertTrue(StringUtils.containsWhitespace("a\t"));
        Assertions.assertTrue(StringUtils.containsWhitespace("\n"));
    }
}

