/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.memory;


import Bitmap.Config.RGB_565;
import MemoryTrimType.OnAppBackgrounded;
import android.graphics.Bitmap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotSame;


@RunWith(RobolectricTestRunner.class)
public class LruBitmapPoolTest {
    private LruBitmapPool mPool;

    @Test
    public void testBitmapIsReused() {
        Bitmap expected = Bitmap.createBitmap(128, 128, RGB_565);
        mPool.release(expected);
        Bitmap actual = mPool.get(((128 * 128) * 2));
        Assert.assertSame(actual, expected);
    }

    @Test
    public void testBitmapWasTrimmed() {
        Bitmap expected = Bitmap.createBitmap(128, 128, RGB_565);
        mPool.release(expected);
        assertEquals(1, valueCount());
        mPool.trim(OnAppBackgrounded);
        Bitmap actual = mPool.get(((128 * 128) * 2));
        assertNotSame(actual, expected);
        assertEquals(0, valueCount());
    }

    @Test
    public void testUniqueObjects() {
        Bitmap one = Bitmap.createBitmap(4, 4, RGB_565);
        mPool.release(one);
        mPool.release(one);
        mPool.release(one);
        assertEquals(1, valueCount());
    }
}

