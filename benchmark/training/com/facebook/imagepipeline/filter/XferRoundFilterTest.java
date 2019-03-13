/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.filter;


import Bitmap.Config.ARGB_8888;
import android.graphics.Bitmap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class XferRoundFilterTest {
    private final Bitmap mBitmap = Bitmap.createBitmap(200, 200, ARGB_8888);

    @Test
    public void whenValidBitmap_thenRoundingReturnsWithoutError() {
        final Bitmap destBitmap = Bitmap.createBitmap(200, 200, ARGB_8888);
        Assert.assertNotNull(mBitmap);
        XferRoundFilter.xferRoundBitmap(destBitmap, mBitmap, true);
        Assert.assertNotNull(destBitmap);
        Assert.assertEquals(mBitmap.getConfig(), destBitmap.getConfig());
        Assert.assertEquals(mBitmap.getHeight(), destBitmap.getHeight());
        Assert.assertEquals(mBitmap.getWidth(), destBitmap.getWidth());
        destBitmap.recycle();
    }

    @Test(expected = NullPointerException.class)
    public void whenNullDestBitmap_thenRoundingReturnsWithError() {
        Assert.assertNotNull(mBitmap);
        XferRoundFilter.xferRoundBitmap(null, mBitmap, true);
    }

    @Test(expected = NullPointerException.class)
    public void whenNullSrcBitmap_thenRoundingReturnsWithError() {
        Bitmap dstBitmap = Mockito.mock(Bitmap.class);
        XferRoundFilter.xferRoundBitmap(dstBitmap, null, true);
    }
}

