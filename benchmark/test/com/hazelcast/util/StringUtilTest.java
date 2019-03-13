/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.util;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class StringUtilTest extends HazelcastTestSupport {
    @Test
    public void testVersionPattern() {
        Assert.assertTrue(StringUtil.VERSION_PATTERN.matcher("3.1").matches());
        Assert.assertTrue(StringUtil.VERSION_PATTERN.matcher("3.1-SNAPSHOT").matches());
        Assert.assertTrue(StringUtil.VERSION_PATTERN.matcher("3.1-RC").matches());
        Assert.assertTrue(StringUtil.VERSION_PATTERN.matcher("3.1-RC1-SNAPSHOT").matches());
        Assert.assertTrue(StringUtil.VERSION_PATTERN.matcher("3.1.1").matches());
        Assert.assertTrue(StringUtil.VERSION_PATTERN.matcher("3.1.1-RC").matches());
        Assert.assertTrue(StringUtil.VERSION_PATTERN.matcher("3.1.1-SNAPSHOT").matches());
        Assert.assertTrue(StringUtil.VERSION_PATTERN.matcher("3.1.1-RC1-SNAPSHOT").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("${project.version}").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("project.version").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("3").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("3.RC").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("3.SNAPSHOT").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("3-RC").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("3-SNAPSHOT").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("3.").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("3.1.RC").matches());
        Assert.assertFalse(StringUtil.VERSION_PATTERN.matcher("3.1.SNAPSHOT").matches());
    }

    @Test
    public void getterIntoProperty_whenNull_returnNull() throws Exception {
        Assert.assertEquals("", StringUtil.getterIntoProperty(""));
    }

    @Test
    public void getterIntoProperty_whenEmpty_returnEmptyString() throws Exception {
        Assert.assertEquals("", StringUtil.getterIntoProperty(""));
    }

    @Test
    public void getterIntoProperty_whenGet_returnUnchanged() throws Exception {
        Assert.assertEquals("get", StringUtil.getterIntoProperty("get"));
    }

    @Test
    public void getterIntoProperty_whenGetFoo_returnFoo() throws Exception {
        Assert.assertEquals("foo", StringUtil.getterIntoProperty("getFoo"));
    }

    @Test
    public void getterIntoProperty_whenGetF_returnF() throws Exception {
        Assert.assertEquals("f", StringUtil.getterIntoProperty("getF"));
    }

    @Test
    public void getterIntoProperty_whenGetNumber_returnNumber() throws Exception {
        Assert.assertEquals("8", StringUtil.getterIntoProperty("get8"));
    }

    @Test
    public void getterIntoProperty_whenPropertyIsLowerCase_DoNotChange() throws Exception {
        Assert.assertEquals("getfoo", StringUtil.getterIntoProperty("getfoo"));
    }

    @Test
    public void test_lowerCaseFirstChar() {
        Assert.assertEquals("", StringUtil.lowerCaseFirstChar(""));
        Assert.assertEquals(".", StringUtil.lowerCaseFirstChar("."));
        Assert.assertEquals(" ", StringUtil.lowerCaseFirstChar(" "));
        Assert.assertEquals("a", StringUtil.lowerCaseFirstChar("a"));
        Assert.assertEquals("a", StringUtil.lowerCaseFirstChar("A"));
        Assert.assertEquals("aBC", StringUtil.lowerCaseFirstChar("ABC"));
        Assert.assertEquals("abc", StringUtil.lowerCaseFirstChar("Abc"));
    }

    @Test
    public void testSplitByComma() throws Exception {
        Assert.assertNull(StringUtil.splitByComma(null, true));
        Assert.assertArrayEquals(arr(""), StringUtil.splitByComma("", true));
        Assert.assertArrayEquals(arr(""), StringUtil.splitByComma(" ", true));
        Assert.assertArrayEquals(arr(), StringUtil.splitByComma(" ", false));
        Assert.assertArrayEquals(arr("a"), StringUtil.splitByComma("a", true));
        Assert.assertArrayEquals(arr("a"), StringUtil.splitByComma("a", false));
        Assert.assertArrayEquals(arr("aa", "bbb", "c"), StringUtil.splitByComma("aa,bbb,c", true));
        Assert.assertArrayEquals(arr("aa", "bbb", "c", ""), StringUtil.splitByComma(" aa\t,\nbbb   ,\r c,  ", true));
        Assert.assertArrayEquals(arr("aa", "bbb", "c"), StringUtil.splitByComma("  aa ,\n,\r\tbbb  ,c , ", false));
    }

    @Test
    public void testArrayIntersection() throws Exception {
        Assert.assertArrayEquals(arr("test"), StringUtil.intersection(arr("x", "test", "y", "z"), arr("a", "b", "test")));
        Assert.assertArrayEquals(arr(""), StringUtil.intersection(arr("", "z"), arr("a", "")));
        Assert.assertArrayEquals(arr(), StringUtil.intersection(arr("", "z"), arr("a")));
    }

    @Test
    public void testArraySubraction() throws Exception {
        Assert.assertNull(StringUtil.subtraction(null, arr("a", "test", "b", "a")));
        Assert.assertArrayEquals(arr("a", "test", "b", "a"), StringUtil.subtraction(arr("a", "test", "b", "a"), null));
        Assert.assertArrayEquals(arr("test"), StringUtil.subtraction(arr("a", "test", "b", "a"), arr("a", "b")));
        Assert.assertArrayEquals(arr(), StringUtil.subtraction(arr(), arr("a", "b")));
        Assert.assertArrayEquals(arr("a", "b"), StringUtil.subtraction(arr("a", "b"), arr()));
        Assert.assertArrayEquals(arr(), StringUtil.subtraction(arr("a", "test", "b", "a"), arr("a", "b", "test")));
    }

    @Test
    public void testEqualsIgnoreCase() throws Exception {
        Assert.assertFalse(StringUtil.equalsIgnoreCase(null, null));
        Assert.assertFalse(StringUtil.equalsIgnoreCase(null, "a"));
        Assert.assertFalse(StringUtil.equalsIgnoreCase("a", null));
        Assert.assertTrue(StringUtil.equalsIgnoreCase("TEST", "test"));
        Assert.assertTrue(StringUtil.equalsIgnoreCase("test", "TEST"));
        Assert.assertFalse(StringUtil.equalsIgnoreCase("test", "TEST2"));
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr"));
        try {
            Assert.assertTrue(StringUtil.equalsIgnoreCase("EXIT", "exit"));
            Assert.assertFalse(StringUtil.equalsIgnoreCase("ex?t", "EXIT"));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }
}

