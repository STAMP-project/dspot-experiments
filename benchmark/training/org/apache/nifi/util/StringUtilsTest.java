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
package org.apache.nifi.util;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class StringUtilsTest {
    @Test
    public void testIsBlank() {
        Assert.assertFalse(StringUtils.isBlank("0"));
        Assert.assertFalse(StringUtils.isBlank("\u0000"));
        Assert.assertFalse(StringUtils.isBlank(" \u0000 "));
        Assert.assertFalse(StringUtils.isBlank(" \u5678 "));
        Assert.assertTrue(StringUtils.isBlank(" "));
        Assert.assertTrue(StringUtils.isBlank(""));
    }

    @Test
    public void testStartsWith() {
        Assert.assertFalse(StringUtils.startsWith("!", "something"));
        Assert.assertTrue(StringUtils.startsWith("!!!!!test", "!!!!"));
        Assert.assertTrue(StringUtils.startsWith("!something", "!"));
        Assert.assertTrue(StringUtils.startsWith(null, null));
    }

    @Test
    public void testPadRight() {
        Assert.assertEquals("sample", StringUtils.padRight("sample", 0, '0'));
        Assert.assertEquals("sample0000", StringUtils.padRight("sample", 10, '0'));
        Assert.assertEquals("0000000000", StringUtils.padRight("", 10, '0'));
        Assert.assertNull(StringUtils.padRight(null, 0, '0'));
    }

    @Test
    public void testPadLeft() {
        Assert.assertEquals("sample", StringUtils.padLeft("sample", 0, '0'));
        Assert.assertEquals("0000sample", StringUtils.padLeft("sample", 10, '0'));
        Assert.assertEquals("0000000000", StringUtils.padLeft("", 10, '0'));
        Assert.assertNull(StringUtils.padLeft(null, 0, '0'));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertFalse(StringUtils.isEmpty("AAAAAAAA"));
        Assert.assertFalse(StringUtils.isEmpty(" "));
        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertTrue(StringUtils.isEmpty(null));
    }

    @Test
    public void testSubstringAfter() {
        Assert.assertEquals("", StringUtils.substringAfter("", ""));
        Assert.assertEquals("", StringUtils.substringAfter("", ">>"));
        Assert.assertEquals("after", StringUtils.substringAfter("substring>>after", ">>"));
        Assert.assertEquals("after>>another", StringUtils.substringAfter("substring>>after>>another", ">>"));
        Assert.assertEquals("", StringUtils.substringAfter("substring>>after", null));
        Assert.assertEquals("", StringUtils.substringAfter("substring.after", ">>"));
    }

    @Test
    public void testJoin() {
        final ArrayList<String> collection = new ArrayList<>();
        Assert.assertEquals("", StringUtils.join(collection, ","));
        collection.add("test1");
        Assert.assertEquals("test1", StringUtils.join(collection, ","));
        collection.add("test2");
        Assert.assertEquals("test1,test2", StringUtils.join(collection, ","));
        collection.add(null);
        Assert.assertEquals("test1,test2,null", StringUtils.join(collection, ","));
    }
}

