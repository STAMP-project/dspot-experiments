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
 * Unit tests {@link org.apache.commons.lang3.StringUtils} - Empty/Blank methods
 */
public class StringUtilsEmptyBlankTest {
    @Test
    public void testIsEmpty() {
        Assertions.assertTrue(StringUtils.isEmpty(null));
        Assertions.assertTrue(StringUtils.isEmpty(""));
        Assertions.assertFalse(StringUtils.isEmpty(" "));
        Assertions.assertFalse(StringUtils.isEmpty("foo"));
        Assertions.assertFalse(StringUtils.isEmpty("  foo  "));
    }

    @Test
    public void testIsNotEmpty() {
        Assertions.assertFalse(StringUtils.isNotEmpty(null));
        Assertions.assertFalse(StringUtils.isNotEmpty(""));
        Assertions.assertTrue(StringUtils.isNotEmpty(" "));
        Assertions.assertTrue(StringUtils.isNotEmpty("foo"));
        Assertions.assertTrue(StringUtils.isNotEmpty("  foo  "));
    }

    @Test
    public void testIsAnyEmpty() {
        Assertions.assertTrue(StringUtils.isAnyEmpty(((String) (null))));
        Assertions.assertFalse(StringUtils.isAnyEmpty(((String[]) (null))));
        Assertions.assertTrue(StringUtils.isAnyEmpty(null, "foo"));
        Assertions.assertTrue(StringUtils.isAnyEmpty("", "bar"));
        Assertions.assertTrue(StringUtils.isAnyEmpty("bob", ""));
        Assertions.assertTrue(StringUtils.isAnyEmpty("  bob  ", null));
        Assertions.assertFalse(StringUtils.isAnyEmpty(" ", "bar"));
        Assertions.assertFalse(StringUtils.isAnyEmpty("foo", "bar"));
    }

    @Test
    public void testIsNoneEmpty() {
        Assertions.assertFalse(StringUtils.isNoneEmpty(((String) (null))));
        Assertions.assertTrue(StringUtils.isNoneEmpty(((String[]) (null))));
        Assertions.assertFalse(StringUtils.isNoneEmpty(null, "foo"));
        Assertions.assertFalse(StringUtils.isNoneEmpty("", "bar"));
        Assertions.assertFalse(StringUtils.isNoneEmpty("bob", ""));
        Assertions.assertFalse(StringUtils.isNoneEmpty("  bob  ", null));
        Assertions.assertTrue(StringUtils.isNoneEmpty(" ", "bar"));
        Assertions.assertTrue(StringUtils.isNoneEmpty("foo", "bar"));
    }

    @Test
    public void testIsAllEmpty() {
        Assertions.assertTrue(StringUtils.isAllEmpty());
        Assertions.assertTrue(StringUtils.isAllEmpty(new String[]{  }));
        Assertions.assertTrue(StringUtils.isAllEmpty(((String) (null))));
        Assertions.assertTrue(StringUtils.isAllEmpty(((String[]) (null))));
        Assertions.assertFalse(StringUtils.isAllEmpty(null, "foo"));
        Assertions.assertFalse(StringUtils.isAllEmpty("", "bar"));
        Assertions.assertFalse(StringUtils.isAllEmpty("bob", ""));
        Assertions.assertFalse(StringUtils.isAllEmpty("  bob  ", null));
        Assertions.assertFalse(StringUtils.isAllEmpty(" ", "bar"));
        Assertions.assertFalse(StringUtils.isAllEmpty("foo", "bar"));
        Assertions.assertTrue(StringUtils.isAllEmpty("", null));
    }

    @Test
    public void testIsBlank() {
        Assertions.assertTrue(StringUtils.isBlank(null));
        Assertions.assertTrue(StringUtils.isBlank(""));
        Assertions.assertTrue(StringUtils.isBlank(StringUtilsTest.WHITESPACE));
        Assertions.assertFalse(StringUtils.isBlank("foo"));
        Assertions.assertFalse(StringUtils.isBlank("  foo  "));
    }

    @Test
    public void testIsNotBlank() {
        Assertions.assertFalse(StringUtils.isNotBlank(null));
        Assertions.assertFalse(StringUtils.isNotBlank(""));
        Assertions.assertFalse(StringUtils.isNotBlank(StringUtilsTest.WHITESPACE));
        Assertions.assertTrue(StringUtils.isNotBlank("foo"));
        Assertions.assertTrue(StringUtils.isNotBlank("  foo  "));
    }

    @Test
    public void testIsAnyBlank() {
        Assertions.assertTrue(StringUtils.isAnyBlank(((String) (null))));
        Assertions.assertFalse(StringUtils.isAnyBlank(((String[]) (null))));
        Assertions.assertTrue(StringUtils.isAnyBlank(null, "foo"));
        Assertions.assertTrue(StringUtils.isAnyBlank(null, null));
        Assertions.assertTrue(StringUtils.isAnyBlank("", "bar"));
        Assertions.assertTrue(StringUtils.isAnyBlank("bob", ""));
        Assertions.assertTrue(StringUtils.isAnyBlank("  bob  ", null));
        Assertions.assertTrue(StringUtils.isAnyBlank(" ", "bar"));
        Assertions.assertFalse(StringUtils.isAnyBlank("foo", "bar"));
    }

    @Test
    public void testIsNoneBlank() {
        Assertions.assertFalse(StringUtils.isNoneBlank(((String) (null))));
        Assertions.assertTrue(StringUtils.isNoneBlank(((String[]) (null))));
        Assertions.assertFalse(StringUtils.isNoneBlank(null, "foo"));
        Assertions.assertFalse(StringUtils.isNoneBlank(null, null));
        Assertions.assertFalse(StringUtils.isNoneBlank("", "bar"));
        Assertions.assertFalse(StringUtils.isNoneBlank("bob", ""));
        Assertions.assertFalse(StringUtils.isNoneBlank("  bob  ", null));
        Assertions.assertFalse(StringUtils.isNoneBlank(" ", "bar"));
        Assertions.assertTrue(StringUtils.isNoneBlank("foo", "bar"));
    }

    @Test
    public void testIsAllBlank() {
        Assertions.assertTrue(StringUtils.isAllBlank(((String) (null))));
        Assertions.assertTrue(StringUtils.isAllBlank(((String[]) (null))));
        Assertions.assertTrue(StringUtils.isAllBlank(null, null));
        Assertions.assertTrue(StringUtils.isAllBlank(null, " "));
        Assertions.assertFalse(StringUtils.isAllBlank(null, "foo"));
        Assertions.assertFalse(StringUtils.isAllBlank("", "bar"));
        Assertions.assertFalse(StringUtils.isAllBlank("bob", ""));
        Assertions.assertFalse(StringUtils.isAllBlank("  bob  ", null));
        Assertions.assertFalse(StringUtils.isAllBlank(" ", "bar"));
        Assertions.assertFalse(StringUtils.isAllBlank("foo", "bar"));
    }

    @Test
    public void testFirstNonBlank() {
        Assertions.assertNull(StringUtils.firstNonBlank());
        Assertions.assertNull(StringUtils.firstNonBlank(((String[]) (null))));
        Assertions.assertNull(StringUtils.firstNonBlank(null, null, null));
        Assertions.assertNull(StringUtils.firstNonBlank(null, "", " "));
        Assertions.assertNull(StringUtils.firstNonBlank(null, null, " "));
        Assertions.assertEquals("zz", StringUtils.firstNonBlank(null, "zz"));
        Assertions.assertEquals("abc", StringUtils.firstNonBlank("abc"));
        Assertions.assertEquals("xyz", StringUtils.firstNonBlank(null, "xyz"));
        Assertions.assertEquals("xyz", StringUtils.firstNonBlank(null, "xyz", "abc"));
    }

    @Test
    public void testFirstNonEmpty() {
        Assertions.assertNull(StringUtils.firstNonEmpty());
        Assertions.assertNull(StringUtils.firstNonEmpty(((String[]) (null))));
        Assertions.assertNull(StringUtils.firstNonEmpty(null, null, null));
        Assertions.assertEquals(" ", StringUtils.firstNonEmpty(null, "", " "));
        Assertions.assertNull(StringUtils.firstNonEmpty(null, null, ""));
        Assertions.assertEquals("zz", StringUtils.firstNonEmpty(null, "zz"));
        Assertions.assertEquals("abc", StringUtils.firstNonEmpty("abc"));
        Assertions.assertEquals("xyz", StringUtils.firstNonEmpty(null, "xyz"));
        Assertions.assertEquals("xyz", StringUtils.firstNonEmpty(null, "xyz", "abc"));
    }
}

