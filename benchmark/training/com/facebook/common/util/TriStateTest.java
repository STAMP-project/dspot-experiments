/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.util;


import TriState.NO;
import TriState.UNSET;
import TriState.YES;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link TriState}.
 */
public class TriStateTest {
    @Test
    public void testIsSet() {
        Assert.assertTrue(YES.isSet());
        Assert.assertTrue(NO.isSet());
        Assert.assertFalse(UNSET.isSet());
    }

    @Test
    public void testValueOf() {
        Assert.assertEquals(YES, TriState.valueOf(true));
        Assert.assertEquals(NO, TriState.valueOf(false));
    }

    @Test
    public void testAsBooleanValidValues() {
        Assert.assertTrue(YES.asBoolean());
        Assert.assertFalse(NO.asBoolean());
    }

    @Test(expected = IllegalStateException.class)
    public void testAsBooleanInvalidValues() {
        UNSET.asBoolean();
    }

    @Test
    public void testAsBooleanDefault() {
        Assert.assertTrue(YES.asBoolean(false));
        Assert.assertFalse(NO.asBoolean(true));
        Assert.assertTrue(UNSET.asBoolean(true));
        Assert.assertFalse(UNSET.asBoolean(false));
    }

    @Test
    public void testAsBooleanObject() {
        Assert.assertSame(Boolean.TRUE, YES.asBooleanObject());
        Assert.assertSame(Boolean.FALSE, NO.asBooleanObject());
        Assert.assertNull(UNSET.asBooleanObject());
    }
}

