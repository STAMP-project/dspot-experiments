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
package org.apache.camel.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link AntPathMatcher}.
 */
public class AntPathMatcherTest extends Assert {
    @Test
    public void test() {
        AntPathMatcher matcher = new AntPathMatcher();
        Assert.assertTrue(matcher.match("*.txt", "blah.txt"));
        Assert.assertFalse(matcher.match("*.txt", "foo/blah.txt"));
        Assert.assertTrue(matcher.match("???.txt", "abc.txt"));
        Assert.assertTrue(matcher.match("abc.t?t", "abc.tnt"));
        Assert.assertFalse(matcher.match("???.txt", "abcd.txt"));
        Assert.assertTrue(matcher.match("**/*.txt", "blah.txt"));
        Assert.assertTrue(matcher.match("**/*.txt", "foo/blah.txt"));
        Assert.assertTrue(matcher.match("**/*.txt", "foo/bar/blah.txt"));
        Assert.assertTrue(matcher.match("foo/**/*.txt", "foo/bar/blah.txt"));
        Assert.assertTrue(matcher.match("foo/**/*.??", "foo/bar/blah.gz"));
        Assert.assertTrue(matcher.match("foo/**/*.txt", "foo/blah.txt"));
        Assert.assertFalse(matcher.match("foo/**/*.txt", "blah/blah.txt"));
    }

    @Test
    public void testCaseSensitive() {
        AntPathMatcher matcher = new AntPathMatcher();
        Assert.assertTrue(matcher.match("foo/**/*.txt", "foo/blah.txt", true));
        Assert.assertTrue(matcher.match("foo/**/*.txt", "foo/blah.txt", false));
        Assert.assertTrue(matcher.match("foo/**/*.txt", "foo/BLAH.txt"));
        Assert.assertFalse(matcher.match("FOO/**/*.txt", "foo/blah.txt"));
        Assert.assertFalse(matcher.match("foo/**/*.TXT", "foo/blah.txt"));
        Assert.assertTrue(matcher.match("foo/**/*.TXT", "foo/blah.txt", false));
        Assert.assertTrue(matcher.match("FOO/**/*.txt", "foo/blah.txt", false));
        Assert.assertFalse(matcher.match("FOO/**/*.txt", "foo/blah.txt", true));
        Assert.assertFalse(matcher.match("FOO/**/*.txt", "foo/blah.txt", true));
    }
}

