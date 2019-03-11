/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.util;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 0.9
 */
public class StringUtilsTest {
    @Test
    public void splitWithNullInput() {
        String line = null;
        String[] split = StringUtils.split(line);
        Assert.assertNull(split);
    }

    @Test
    public void splitWithCommas() {
        String line = "shall,we,play,a,game?";
        String[] split = StringUtils.split(line);
        Assert.assertNotNull(split);
        Assert.assertTrue(((split.length) == 5));
        Assert.assertEquals("shall", split[0]);
        Assert.assertEquals("we", split[1]);
        Assert.assertEquals("play", split[2]);
        Assert.assertEquals("a", split[3]);
        Assert.assertEquals("game?", split[4]);
    }

    @Test
    public void splitWithCommasAndSpaces() {
        String line = "shall,we ,    play, a,game?";
        String[] split = StringUtils.split(line);
        Assert.assertNotNull(split);
        Assert.assertTrue(((split.length) == 5));
        Assert.assertEquals("shall", split[0]);
        Assert.assertEquals("we", split[1]);
        Assert.assertEquals("play", split[2]);
        Assert.assertEquals("a", split[3]);
        Assert.assertEquals("game?", split[4]);
    }

    @Test
    public void splitWithQuotedCommasAndSpaces() {
        String line = "shall, \"we, play\", a, game?";
        String[] split = StringUtils.split(line);
        Assert.assertNotNull(split);
        Assert.assertTrue(((split.length) == 4));
        Assert.assertEquals("shall", split[0]);
        Assert.assertEquals("we, play", split[1]);
        Assert.assertEquals("a", split[2]);
        Assert.assertEquals("game?", split[3]);
    }

    @Test
    public void splitWithQuotedCommasAndSpacesAndDifferentQuoteChars() {
        String line = "authc, test[blah], test[1,2,3], test[]";
        String[] split = StringUtils.split(line, ',', '[', ']', false, true);
        Assert.assertNotNull(split);
        Assert.assertTrue(((split.length) == 4));
        Assert.assertEquals("authc", split[0]);
        Assert.assertEquals("testblah", split[1]);
        Assert.assertEquals("test1,2,3", split[2]);
        Assert.assertEquals("test", split[3]);
    }

    @Test
    public void splitWithQuotedCommasAndSpacesAndDifferentQuoteCharsWhileRetainingQuotes() {
        String line = "authc, test[blah], test[1,2,3], test[]";
        String[] split = StringUtils.split(line, ',', '[', ']', true, true);
        Assert.assertNotNull(split);
        Assert.assertTrue(((split.length) == 4));
        Assert.assertEquals("authc", split[0]);
        Assert.assertEquals("test[blah]", split[1]);
        Assert.assertEquals("test[1,2,3]", split[2]);
        Assert.assertEquals("test[]", split[3]);
    }

    @Test
    public void splitTestWithQuotedCommas() {
        String line = "authc, test[blah], test[\"1,2,3\"], test[]";
        String[] split = StringUtils.split(line);
        Assert.assertNotNull(split);
        Assert.assertTrue(((split.length) == 4));
        Assert.assertEquals("authc", split[0]);
        Assert.assertEquals("test[blah]", split[1]);
        Assert.assertEquals("test[1,2,3]", split[2]);
        Assert.assertEquals("test[]", split[3]);
    }

    @Test
    public void splitWithQuotedCommasAndSpacesAndEscapedQuotes() {
        String line = "shall, \"\"\"we, play\", a, \"\"\"game?";
        String[] split = StringUtils.split(line);
        Assert.assertNotNull(split);
        Assert.assertTrue(((split.length) == 4));
        Assert.assertEquals("shall", split[0]);
        Assert.assertEquals("\"we, play", split[1]);
        Assert.assertEquals("a", split[2]);
        Assert.assertEquals("\"game?", split[3]);
    }
}

