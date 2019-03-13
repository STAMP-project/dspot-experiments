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
package org.apache.dubbo.common.utils;


import Constants.GROUP_KEY;
import Constants.INTERFACE_KEY;
import Constants.VERSION_KEY;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StringUtilsTest {
    @Test
    public void testLength() throws Exception {
        MatcherAssert.assertThat(StringUtils.length(null), Matchers.equalTo(0));
        MatcherAssert.assertThat(StringUtils.length("abc"), Matchers.equalTo(3));
    }

    @Test
    public void testRepeat() throws Exception {
        MatcherAssert.assertThat(StringUtils.repeat(null, 2), Matchers.nullValue());
        MatcherAssert.assertThat(StringUtils.repeat("", 0), Matchers.equalTo(""));
        MatcherAssert.assertThat(StringUtils.repeat("", 2), Matchers.equalTo(""));
        MatcherAssert.assertThat(StringUtils.repeat("a", 3), Matchers.equalTo("aaa"));
        MatcherAssert.assertThat(StringUtils.repeat("ab", 2), Matchers.equalTo("abab"));
        MatcherAssert.assertThat(StringUtils.repeat("a", (-2)), Matchers.equalTo(""));
        MatcherAssert.assertThat(StringUtils.repeat(null, null, 2), Matchers.nullValue());
        MatcherAssert.assertThat(StringUtils.repeat(null, "x", 2), Matchers.nullValue());
        MatcherAssert.assertThat(StringUtils.repeat("", null, 0), Matchers.equalTo(""));
        MatcherAssert.assertThat(StringUtils.repeat("", "", 2), Matchers.equalTo(""));
        MatcherAssert.assertThat(StringUtils.repeat("", "x", 3), Matchers.equalTo("xx"));
        MatcherAssert.assertThat(StringUtils.repeat("?", ", ", 3), Matchers.equalTo("?, ?, ?"));
        MatcherAssert.assertThat(StringUtils.repeat('e', 0), Matchers.equalTo(""));
        MatcherAssert.assertThat(StringUtils.repeat('e', 3), Matchers.equalTo("eee"));
    }

    @Test
    public void testStripEnd() throws Exception {
        MatcherAssert.assertThat(StringUtils.stripEnd(null, "*"), Matchers.nullValue());
        MatcherAssert.assertThat(StringUtils.stripEnd("", null), Matchers.equalTo(""));
        MatcherAssert.assertThat(StringUtils.stripEnd("abc", ""), Matchers.equalTo("abc"));
        MatcherAssert.assertThat(StringUtils.stripEnd("abc", null), Matchers.equalTo("abc"));
        MatcherAssert.assertThat(StringUtils.stripEnd("  abc", null), Matchers.equalTo("  abc"));
        MatcherAssert.assertThat(StringUtils.stripEnd("abc  ", null), Matchers.equalTo("abc"));
        MatcherAssert.assertThat(StringUtils.stripEnd(" abc ", null), Matchers.equalTo(" abc"));
        MatcherAssert.assertThat(StringUtils.stripEnd("  abcyx", "xyz"), Matchers.equalTo("  abc"));
        MatcherAssert.assertThat(StringUtils.stripEnd("120.00", ".0"), Matchers.equalTo("12"));
    }

    @Test
    public void testReplace() throws Exception {
        MatcherAssert.assertThat(StringUtils.replace(null, "*", "*"), Matchers.nullValue());
        MatcherAssert.assertThat(StringUtils.replace("", "*", "*"), Matchers.equalTo(""));
        MatcherAssert.assertThat(StringUtils.replace("any", null, "*"), Matchers.equalTo("any"));
        MatcherAssert.assertThat(StringUtils.replace("any", "*", null), Matchers.equalTo("any"));
        MatcherAssert.assertThat(StringUtils.replace("any", "", "*"), Matchers.equalTo("any"));
        MatcherAssert.assertThat(StringUtils.replace("aba", "a", null), Matchers.equalTo("aba"));
        MatcherAssert.assertThat(StringUtils.replace("aba", "a", ""), Matchers.equalTo("b"));
        MatcherAssert.assertThat(StringUtils.replace("aba", "a", "z"), Matchers.equalTo("zbz"));
        MatcherAssert.assertThat(StringUtils.replace(null, "*", "*", 64), Matchers.nullValue());
        MatcherAssert.assertThat(StringUtils.replace("", "*", "*", 64), Matchers.equalTo(""));
        MatcherAssert.assertThat(StringUtils.replace("any", null, "*", 64), Matchers.equalTo("any"));
        MatcherAssert.assertThat(StringUtils.replace("any", "*", null, 64), Matchers.equalTo("any"));
        MatcherAssert.assertThat(StringUtils.replace("any", "", "*", 64), Matchers.equalTo("any"));
        MatcherAssert.assertThat(StringUtils.replace("any", "*", "*", 0), Matchers.equalTo("any"));
        MatcherAssert.assertThat(StringUtils.replace("abaa", "a", null, (-1)), Matchers.equalTo("abaa"));
        MatcherAssert.assertThat(StringUtils.replace("abaa", "a", "", (-1)), Matchers.equalTo("b"));
        MatcherAssert.assertThat(StringUtils.replace("abaa", "a", "z", 0), Matchers.equalTo("abaa"));
        MatcherAssert.assertThat(StringUtils.replace("abaa", "a", "z", 1), Matchers.equalTo("zbaa"));
        MatcherAssert.assertThat(StringUtils.replace("abaa", "a", "z", 2), Matchers.equalTo("zbza"));
    }

    @Test
    public void testIsBlank() throws Exception {
        Assertions.assertTrue(StringUtils.isBlank(null));
        Assertions.assertTrue(StringUtils.isBlank(""));
        Assertions.assertFalse(StringUtils.isBlank("abc"));
    }

    @Test
    public void testIsEmpty() throws Exception {
        Assertions.assertTrue(StringUtils.isEmpty(null));
        Assertions.assertTrue(StringUtils.isEmpty(""));
        Assertions.assertFalse(StringUtils.isEmpty("abc"));
    }

    @Test
    public void testIsNoneEmpty() throws Exception {
        Assertions.assertFalse(StringUtils.isNoneEmpty(null));
        Assertions.assertFalse(StringUtils.isNoneEmpty(""));
        Assertions.assertTrue(StringUtils.isNoneEmpty(" "));
        Assertions.assertTrue(StringUtils.isNoneEmpty("abc"));
        Assertions.assertTrue(StringUtils.isNoneEmpty("abc", "def"));
        Assertions.assertFalse(StringUtils.isNoneEmpty("abc", null));
        Assertions.assertFalse(StringUtils.isNoneEmpty("abc", ""));
        Assertions.assertTrue(StringUtils.isNoneEmpty("abc", " "));
    }

    @Test
    public void testIsAnyEmpty() throws Exception {
        Assertions.assertTrue(StringUtils.isAnyEmpty(null));
        Assertions.assertTrue(StringUtils.isAnyEmpty(""));
        Assertions.assertFalse(StringUtils.isAnyEmpty(" "));
        Assertions.assertFalse(StringUtils.isAnyEmpty("abc"));
        Assertions.assertFalse(StringUtils.isAnyEmpty("abc", "def"));
        Assertions.assertTrue(StringUtils.isAnyEmpty("abc", null));
        Assertions.assertTrue(StringUtils.isAnyEmpty("abc", ""));
        Assertions.assertFalse(StringUtils.isAnyEmpty("abc", " "));
    }

    @Test
    public void testIsNotEmpty() throws Exception {
        Assertions.assertFalse(StringUtils.isNotEmpty(null));
        Assertions.assertFalse(StringUtils.isNotEmpty(""));
        Assertions.assertTrue(StringUtils.isNotEmpty("abc"));
    }

    @Test
    public void testIsEquals() throws Exception {
        Assertions.assertTrue(StringUtils.isEquals(null, null));
        Assertions.assertFalse(StringUtils.isEquals(null, ""));
        Assertions.assertTrue(StringUtils.isEquals("abc", "abc"));
        Assertions.assertFalse(StringUtils.isEquals("abc", "ABC"));
    }

    @Test
    public void testIsInteger() throws Exception {
        Assertions.assertFalse(StringUtils.isInteger(null));
        Assertions.assertFalse(StringUtils.isInteger(""));
        Assertions.assertTrue(StringUtils.isInteger("123"));
    }

    @Test
    public void testParseInteger() throws Exception {
        MatcherAssert.assertThat(StringUtils.parseInteger(null), Matchers.equalTo(0));
        MatcherAssert.assertThat(StringUtils.parseInteger("123"), Matchers.equalTo(123));
    }

    @Test
    public void testIsJavaIdentifier() throws Exception {
        MatcherAssert.assertThat(StringUtils.isJavaIdentifier(""), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isJavaIdentifier("1"), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isJavaIdentifier("abc123"), Matchers.is(true));
        MatcherAssert.assertThat(StringUtils.isJavaIdentifier("abc(23)"), Matchers.is(false));
    }

    @Test
    public void testExceptionToString() throws Exception {
        MatcherAssert.assertThat(StringUtils.toString(new RuntimeException("abc")), Matchers.containsString("java.lang.RuntimeException: abc"));
    }

    @Test
    public void testExceptionToStringWithMessage() throws Exception {
        String s = StringUtils.toString("greeting", new RuntimeException("abc"));
        MatcherAssert.assertThat(s, Matchers.containsString("greeting"));
        MatcherAssert.assertThat(s, Matchers.containsString("java.lang.RuntimeException: abc"));
    }

    @Test
    public void testParseQueryString() throws Exception {
        MatcherAssert.assertThat(StringUtils.getQueryStringValue("key1=value1&key2=value2", "key1"), Matchers.equalTo("value1"));
        MatcherAssert.assertThat(StringUtils.getQueryStringValue("key1=value1&key2=value2", "key2"), Matchers.equalTo("value2"));
        MatcherAssert.assertThat(StringUtils.getQueryStringValue("", "key1"), Matchers.isEmptyOrNullString());
    }

    @Test
    public void testGetServiceKey() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GROUP_KEY, "dubbo");
        map.put(INTERFACE_KEY, "a.b.c.Foo");
        map.put(VERSION_KEY, "1.0.0");
        MatcherAssert.assertThat(StringUtils.getServiceKey(map), Matchers.equalTo("dubbo/a.b.c.Foo:1.0.0"));
    }

    @Test
    public void testToQueryString() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        String queryString = StringUtils.toQueryString(map);
        MatcherAssert.assertThat(queryString, Matchers.containsString("key1=value1"));
        MatcherAssert.assertThat(queryString, Matchers.containsString("key2=value2"));
    }

    @Test
    public void testJoin() throws Exception {
        String[] s = new String[]{ "1", "2", "3" };
        Assertions.assertEquals(StringUtils.join(s), "123");
        Assertions.assertEquals(StringUtils.join(s, ','), "1,2,3");
        Assertions.assertEquals(StringUtils.join(s, ","), "1,2,3");
    }

    @Test
    public void testSplit() throws Exception {
        String s = "d,1,2,4";
        Assertions.assertEquals(StringUtils.split(s, ',').length, 4);
    }

    @Test
    public void testTranslate() throws Exception {
        String s = "16314";
        Assertions.assertEquals(StringUtils.translate(s, "123456", "abcdef"), "afcad");
        Assertions.assertEquals(StringUtils.translate(s, "123456", "abcd"), "acad");
    }

    @Test
    public void testIsContains() throws Exception {
        MatcherAssert.assertThat(StringUtils.isContains("a,b, c", "b"), Matchers.is(true));
        MatcherAssert.assertThat(StringUtils.isContains("", "b"), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isContains(new String[]{ "a", "b", "c" }, "b"), Matchers.is(true));
        MatcherAssert.assertThat(StringUtils.isContains(((String[]) (null)), null), Matchers.is(false));
    }

    @Test
    public void testIsNumeric() throws Exception {
        MatcherAssert.assertThat(StringUtils.isNumeric("123", false), Matchers.is(true));
        MatcherAssert.assertThat(StringUtils.isNumeric("1a3", false), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isNumeric(null, false), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isNumeric("0", true), Matchers.is(true));
        MatcherAssert.assertThat(StringUtils.isNumeric("0.1", true), Matchers.is(true));
        MatcherAssert.assertThat(StringUtils.isNumeric("DUBBO", true), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isNumeric("", true), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isNumeric(" ", true), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isNumeric("   ", true), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isNumeric("123.3.3", true), Matchers.is(false));
        MatcherAssert.assertThat(StringUtils.isNumeric("123.", true), Matchers.is(true));
        MatcherAssert.assertThat(StringUtils.isNumeric(".123", true), Matchers.is(true));
        MatcherAssert.assertThat(StringUtils.isNumeric("..123", true), Matchers.is(false));
    }

    @Test
    public void testJoinCollectionString() throws Exception {
        List<String> list = new ArrayList<String>();
        Assertions.assertEquals("", StringUtils.join(list, ","));
        list.add("v1");
        Assertions.assertEquals("v1", StringUtils.join(list, "-"));
        list.add("v2");
        list.add("v3");
        String out = StringUtils.join(list, ":");
        Assertions.assertEquals("v1:v2:v3", out);
    }

    @Test
    public void testCamelToSplitName() throws Exception {
        Assertions.assertEquals("ab-cd-ef", StringUtils.camelToSplitName("abCdEf", "-"));
        Assertions.assertEquals("ab-cd-ef", StringUtils.camelToSplitName("AbCdEf", "-"));
        Assertions.assertEquals("ab-cd-ef", StringUtils.camelToSplitName("ab-cd-ef", "-"));
        Assertions.assertEquals("abcdef", StringUtils.camelToSplitName("abcdef", "-"));
    }

    @Test
    public void testToArgumentString() throws Exception {
        String s = StringUtils.toArgumentString(new Object[]{ "a", 0, Collections.singletonMap("enabled", true) });
        MatcherAssert.assertThat(s, Matchers.containsString("a,"));
        MatcherAssert.assertThat(s, Matchers.containsString("0,"));
        MatcherAssert.assertThat(s, Matchers.containsString("{\"enabled\":true}"));
    }

    @Test
    public void testTrim() {
        Assertions.assertEquals("left blank", StringUtils.trim(" left blank"));
        Assertions.assertEquals("right blank", StringUtils.trim("right blank "));
        Assertions.assertEquals("bi-side blank", StringUtils.trim(" bi-side blank "));
    }
}

