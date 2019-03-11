/**
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.support;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dan Garrette
 * @since 2.0
 */
public class PatternMatcherTests {
    private static Map<String, Integer> map = new HashMap<>();

    static {
        PatternMatcherTests.map.put("an*", 3);
        PatternMatcherTests.map.put("a*", 2);
        PatternMatcherTests.map.put("big*", 4);
    }

    private static Map<String, Integer> defaultMap = new HashMap<>();

    static {
        PatternMatcherTests.defaultMap.put("an", 3);
        PatternMatcherTests.defaultMap.put("a", 2);
        PatternMatcherTests.defaultMap.put("big*", 4);
        PatternMatcherTests.defaultMap.put("big?*", 5);
        PatternMatcherTests.defaultMap.put("*", 1);
    }

    @Test
    public void testMatchNoWildcardYes() {
        Assert.assertTrue(PatternMatcher.match("abc", "abc"));
    }

    @Test
    public void testMatchNoWildcardNo() {
        Assert.assertFalse(PatternMatcher.match("abc", "ab"));
    }

    @Test
    public void testMatchSingleYes() {
        Assert.assertTrue(PatternMatcher.match("a?c", "abc"));
    }

    @Test
    public void testMatchSingleNo() {
        Assert.assertFalse(PatternMatcher.match("a?c", "ab"));
    }

    @Test
    public void testMatchSingleWildcardNo() {
        Assert.assertTrue(PatternMatcher.match("a?*", "abc"));
    }

    @Test
    public void testMatchStarYes() {
        Assert.assertTrue(PatternMatcher.match("a*c", "abdegc"));
    }

    @Test
    public void testMatchTwoStars() {
        Assert.assertTrue(PatternMatcher.match("a*d*", "abcdeg"));
    }

    @Test
    public void testMatchPastEnd() {
        Assert.assertFalse(PatternMatcher.match("a*de", "abcdeg"));
    }

    @Test
    public void testMatchPastEndTwoStars() {
        Assert.assertTrue(PatternMatcher.match("a*d*g*", "abcdeg"));
    }

    @Test
    public void testMatchStarAtEnd() {
        Assert.assertTrue(PatternMatcher.match("ab*", "ab"));
    }

    @Test
    public void testMatchStarNo() {
        Assert.assertFalse(PatternMatcher.match("a*c", "abdeg"));
    }

    @Test
    public void testMatchPrefixSubsumed() {
        Assert.assertEquals(2, new PatternMatcher(PatternMatcherTests.map).match("apple").intValue());
    }

    @Test
    public void testMatchPrefixSubsuming() {
        Assert.assertEquals(3, new PatternMatcher(PatternMatcherTests.map).match("animal").intValue());
    }

    @Test
    public void testMatchPrefixUnrelated() {
        Assert.assertEquals(4, new PatternMatcher(PatternMatcherTests.map).match("biggest").intValue());
    }

    @Test(expected = IllegalStateException.class)
    public void testMatchPrefixNoMatch() {
        new PatternMatcher(PatternMatcherTests.map).match("bat");
    }

    @Test
    public void testMatchPrefixDefaultValueUnrelated() {
        Assert.assertEquals(5, new PatternMatcher(PatternMatcherTests.defaultMap).match("biggest").intValue());
    }

    @Test
    public void testMatchPrefixDefaultValueEmptyString() {
        Assert.assertEquals(1, new PatternMatcher(PatternMatcherTests.defaultMap).match("").intValue());
    }

    @Test
    public void testMatchPrefixDefaultValueNoMatch() {
        Assert.assertEquals(1, new PatternMatcher(PatternMatcherTests.defaultMap).match("bat").intValue());
    }
}

