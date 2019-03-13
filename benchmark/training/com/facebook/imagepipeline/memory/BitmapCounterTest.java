/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.memory;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(RobolectricTestRunner.class)
public class BitmapCounterTest {
    private static final int MAX_COUNT = 4;

    private static final int MAX_SIZE = (BitmapCounterTest.MAX_COUNT) + 1;

    private BitmapCounter mBitmapCounter;

    @Test
    public void testBasic() {
        assertState(0, 0);
        Assert.assertTrue(mBitmapCounter.increase(bitmapForSize(1)));
        assertState(1, 1);
        Assert.assertTrue(mBitmapCounter.increase(bitmapForSize(2)));
        assertState(2, 3);
        mBitmapCounter.decrease(bitmapForSize(1));
        assertState(1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecreaseTooMuch() {
        Assert.assertTrue(mBitmapCounter.increase(bitmapForSize(1)));
        mBitmapCounter.decrease(bitmapForSize(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecreaseTooMany() {
        Assert.assertTrue(mBitmapCounter.increase(bitmapForSize(2)));
        mBitmapCounter.decrease(bitmapForSize(1));
        mBitmapCounter.decrease(bitmapForSize(1));
    }

    @Test
    public void testMaxSize() {
        Assert.assertTrue(mBitmapCounter.increase(bitmapForSize(BitmapCounterTest.MAX_SIZE)));
        assertState(1, BitmapCounterTest.MAX_SIZE);
    }

    @Test
    public void testMaxCount() {
        for (int i = 0; i < (BitmapCounterTest.MAX_COUNT); ++i) {
            mBitmapCounter.increase(bitmapForSize(1));
        }
        assertState(BitmapCounterTest.MAX_COUNT, BitmapCounterTest.MAX_COUNT);
    }

    @Test
    public void increaseTooBigObject() {
        Assert.assertFalse(mBitmapCounter.increase(bitmapForSize(((BitmapCounterTest.MAX_SIZE) + 1))));
        assertState(0, 0);
    }

    @Test
    public void increaseTooManyObjects() {
        for (int i = 0; i < (BitmapCounterTest.MAX_COUNT); ++i) {
            mBitmapCounter.increase(bitmapForSize(1));
        }
        Assert.assertFalse(mBitmapCounter.increase(bitmapForSize(1)));
        assertState(BitmapCounterTest.MAX_COUNT, BitmapCounterTest.MAX_COUNT);
    }
}

