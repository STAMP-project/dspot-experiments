/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.util;


import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test for {@link StringUtil}.
 */
@RunWith(JUnit4.class)
public class StringUtilTest {
    @Test
    public void testJoinEnglishList() throws Exception {
        assertThat(StringUtil.joinEnglishList(Collections.emptyList())).isEqualTo("nothing");
        assertThat(StringUtil.joinEnglishList(Arrays.asList("one"))).isEqualTo("one");
        assertThat(StringUtil.joinEnglishList(Arrays.asList("one", "two"))).isEqualTo("one or two");
        assertThat(StringUtil.joinEnglishList(Arrays.asList("one", "two"), "and")).isEqualTo("one and two");
        assertThat(StringUtil.joinEnglishList(Arrays.asList("one", "two", "three"))).isEqualTo("one, two or three");
        assertThat(StringUtil.joinEnglishList(Arrays.asList("one", "two", "three"), "and")).isEqualTo("one, two and three");
        assertThat(StringUtil.joinEnglishList(Arrays.asList("one", "two", "three"), "and", "'")).isEqualTo("'one', 'two' and 'three'");
    }

    @Test
    public void listItemsWithLimit() throws Exception {
        assertThat(StringUtil.listItemsWithLimit(new StringBuilder("begin/"), 3, ImmutableList.of("a", "b", "c")).append("/end").toString()).isEqualTo("begin/a, b, c/end");
        assertThat(StringUtil.listItemsWithLimit(new StringBuilder("begin/"), 3, ImmutableList.of("a", "b", "c", "d", "e")).append("/end").toString()).isEqualTo("begin/a, b, c ...(omitting 2 more item(s))/end");
    }

    @Test
    public void testIndent() throws Exception {
        assertThat(StringUtil.indent("", 0)).isEmpty();
        assertThat(StringUtil.indent("", 1)).isEmpty();
        assertThat(StringUtil.indent("a", 1)).isEqualTo("a");
        assertThat(StringUtil.indent("\na", 2)).isEqualTo("\n  a");
        assertThat(StringUtil.indent("a\nb", 2)).isEqualTo("a\n  b");
        assertThat(StringUtil.indent("a\nb\nc\nd", 1)).isEqualTo("a\n b\n c\n d");
        assertThat(StringUtil.indent("\n", 1)).isEqualTo("\n ");
    }

    @Test
    public void testStripSuffix() throws Exception {
        assertThat(StringUtil.stripSuffix("", "")).isEmpty();
        assertThat(StringUtil.stripSuffix("", "a")).isNull();
        assertThat(StringUtil.stripSuffix("a", "")).isEqualTo("a");
        assertThat(StringUtil.stripSuffix("aa", "a")).isEqualTo("a");
        assertThat(StringUtil.stripSuffix("ab", "c")).isNull();
    }

    @Test
    public void testCapitalize() throws Exception {
        assertThat(StringUtil.capitalize("")).isEmpty();
        assertThat(StringUtil.capitalize("joe")).isEqualTo("Joe");
        assertThat(StringUtil.capitalize("Joe")).isEqualTo("Joe");
        assertThat(StringUtil.capitalize("o")).isEqualTo("O");
        assertThat(StringUtil.capitalize("O")).isEqualTo("O");
    }

    @Test
    public void testEmptyToNull() {
        assertThat(StringUtil.emptyToNull(null)).isNull();
        assertThat(StringUtil.emptyToNull("")).isNull();
        assertThat(StringUtil.emptyToNull("a")).isEqualTo("a");
        assertThat(StringUtil.emptyToNull(" ")).isEqualTo(" ");
    }
}

