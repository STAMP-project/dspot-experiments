/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


// endregion
public class ParseTextUtilsTest {
    // region testJoin
    @Test
    public void testJoinMultipleItems() {
        String joined = ParseTextUtils.join(",", Arrays.asList("one", "two", "three"));
        Assert.assertEquals("one,two,three", joined);
    }

    @Test
    public void testJoinSingleItem() {
        String joined = ParseTextUtils.join(",", Collections.singletonList("one"));
        Assert.assertEquals("one", joined);
    }

    // endregion
    // region testIsEmpty
    @Test
    public void testEmptyStringIsEmpty() {
        Assert.assertTrue(ParseTextUtils.isEmpty(""));
    }

    @Test
    public void testNullStringIsEmpty() {
        Assert.assertTrue(ParseTextUtils.isEmpty(null));
    }

    @Test
    public void testStringIsNotEmpty() {
        Assert.assertFalse(ParseTextUtils.isEmpty("not empty"));
    }

    // endregion
    // region testEquals
    @Test
    public void testEqualsNull() {
        Assert.assertTrue(ParseTextUtils.equals(null, null));
    }

    @Test
    public void testNotEqualsNull() {
        Assert.assertFalse(ParseTextUtils.equals("not null", null));
        Assert.assertFalse(ParseTextUtils.equals(null, "not null"));
    }

    @Test
    public void testEqualsString() {
        String same = "Hello, world!";
        Assert.assertTrue(ParseTextUtils.equals(same, same));
        Assert.assertTrue(ParseTextUtils.equals(same, (same + "")));// Hack to compare different instances

    }

    @Test
    public void testNotEqualsString() {
        Assert.assertFalse(ParseTextUtils.equals("grantland", "nlutsenko"));
    }
}

