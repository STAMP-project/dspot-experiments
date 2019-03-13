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


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for {@link org.apache.commons.lang3.text.StrMatcher}.
 */
@Deprecated
public class StrMatcherTest {
    private static final char[] BUFFER1 = "0,1\t2 3\n\r\f\u0000\'\"".toCharArray();

    private static final char[] BUFFER2 = "abcdef".toCharArray();

    // -----------------------------------------------------------------------
    @Test
    public void testCommaMatcher() {
        final StrMatcher matcher = StrMatcher.commaMatcher();
        Assertions.assertSame(matcher, StrMatcher.commaMatcher());
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 0));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 1));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 2));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testTabMatcher() {
        final StrMatcher matcher = StrMatcher.tabMatcher();
        Assertions.assertSame(matcher, StrMatcher.tabMatcher());
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 2));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 3));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 4));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSpaceMatcher() {
        final StrMatcher matcher = StrMatcher.spaceMatcher();
        Assertions.assertSame(matcher, StrMatcher.spaceMatcher());
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 4));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 5));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 6));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSplitMatcher() {
        final StrMatcher matcher = StrMatcher.splitMatcher();
        Assertions.assertSame(matcher, StrMatcher.splitMatcher());
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 2));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 3));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 4));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 5));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 6));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 7));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 8));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 9));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 10));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testTrimMatcher() {
        final StrMatcher matcher = StrMatcher.trimMatcher();
        Assertions.assertSame(matcher, StrMatcher.trimMatcher());
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 2));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 3));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 4));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 5));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 6));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 7));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 8));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 9));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 10));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testSingleQuoteMatcher() {
        final StrMatcher matcher = StrMatcher.singleQuoteMatcher();
        Assertions.assertSame(matcher, StrMatcher.singleQuoteMatcher());
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 10));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 11));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 12));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testDoubleQuoteMatcher() {
        final StrMatcher matcher = StrMatcher.doubleQuoteMatcher();
        Assertions.assertSame(matcher, StrMatcher.doubleQuoteMatcher());
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 11));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 12));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testQuoteMatcher() {
        final StrMatcher matcher = StrMatcher.quoteMatcher();
        Assertions.assertSame(matcher, StrMatcher.quoteMatcher());
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 10));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 11));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER1, 12));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testNoneMatcher() {
        final StrMatcher matcher = StrMatcher.noneMatcher();
        Assertions.assertSame(matcher, StrMatcher.noneMatcher());
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 0));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 1));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 2));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 3));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 4));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 5));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 6));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 7));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 8));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 9));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 10));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 11));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER1, 12));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCharMatcher_char() {
        final StrMatcher matcher = StrMatcher.charMatcher('c');
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 0));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 1));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER2, 2));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 3));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 4));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 5));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCharSetMatcher_String() {
        final StrMatcher matcher = StrMatcher.charSetMatcher("ace");
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER2, 0));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 1));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER2, 2));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 3));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER2, 4));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 5));
        Assertions.assertSame(StrMatcher.noneMatcher(), StrMatcher.charSetMatcher(""));
        Assertions.assertSame(StrMatcher.noneMatcher(), StrMatcher.charSetMatcher(((String) (null))));
        Assertions.assertTrue(((StrMatcher.charSetMatcher("a")) instanceof StrMatcher.CharMatcher));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testCharSetMatcher_charArray() {
        final StrMatcher matcher = StrMatcher.charSetMatcher("ace".toCharArray());
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER2, 0));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 1));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER2, 2));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 3));
        Assertions.assertEquals(1, matcher.isMatch(StrMatcherTest.BUFFER2, 4));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 5));
        Assertions.assertSame(StrMatcher.noneMatcher(), StrMatcher.charSetMatcher());
        Assertions.assertSame(StrMatcher.noneMatcher(), StrMatcher.charSetMatcher(((char[]) (null))));
        Assertions.assertTrue(((StrMatcher.charSetMatcher("a".toCharArray())) instanceof StrMatcher.CharMatcher));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testStringMatcher_String() {
        final StrMatcher matcher = StrMatcher.stringMatcher("bc");
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 0));
        Assertions.assertEquals(2, matcher.isMatch(StrMatcherTest.BUFFER2, 1));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 2));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 3));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 4));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 5));
        Assertions.assertSame(StrMatcher.noneMatcher(), StrMatcher.stringMatcher(""));
        Assertions.assertSame(StrMatcher.noneMatcher(), StrMatcher.stringMatcher(null));
    }

    // -----------------------------------------------------------------------
    @Test
    public void testMatcherIndices() {
        // remember that the API contract is tight for the isMatch() method
        // all the onus is on the caller, so invalid inputs are not
        // the concern of StrMatcher, and are not bugs
        final StrMatcher matcher = StrMatcher.stringMatcher("bc");
        Assertions.assertEquals(2, matcher.isMatch(StrMatcherTest.BUFFER2, 1, 1, StrMatcherTest.BUFFER2.length));
        Assertions.assertEquals(2, matcher.isMatch(StrMatcherTest.BUFFER2, 1, 0, 3));
        Assertions.assertEquals(0, matcher.isMatch(StrMatcherTest.BUFFER2, 1, 0, 2));
    }
}

