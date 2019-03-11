/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.util;


import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class HashCodeUtilTest {
    @Test
    public void testSimple() {
        testCase(1, 2, 3, 4, 5, 6);
    }

    @Test
    public void testRandom() {
        Random generator = new Random(123);
        testCase(generator.nextInt(), generator.nextInt(), generator.nextInt(), generator.nextInt(), generator.nextInt(), generator.nextInt());
    }

    @Test
    public void testNull() {
        testCase(1, null, 3, 4, 5, 6);
    }
}

