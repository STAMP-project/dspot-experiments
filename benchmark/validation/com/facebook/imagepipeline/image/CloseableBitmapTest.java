/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.image;


import android.graphics.Bitmap;
import com.facebook.common.references.ResourceReleaser;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Basic tests for closeable bitmap
 */
@RunWith(RobolectricTestRunner.class)
public class CloseableBitmapTest {
    @Mock
    public Bitmap mBitmap;

    @Mock
    public ResourceReleaser<Bitmap> mResourceReleaser;

    private CloseableStaticBitmap mCloseableStaticBitmap;

    @Test
    public void testBasic() throws Exception {
        Assert.assertFalse(mCloseableStaticBitmap.isClosed());
        Assert.assertSame(mBitmap, mCloseableStaticBitmap.getUnderlyingBitmap());
        // close it now
        mCloseableStaticBitmap.close();
        Assert.assertTrue(mCloseableStaticBitmap.isClosed());
        Assert.assertNull(mCloseableStaticBitmap.getUnderlyingBitmap());
        Mockito.verify(mResourceReleaser).release(mBitmap);
        // close it again
        mCloseableStaticBitmap.close();
        Assert.assertTrue(mCloseableStaticBitmap.isClosed());
        Assert.assertNull(mCloseableStaticBitmap.getUnderlyingBitmap());
    }

    @Test
    public void testFinalize() throws Throwable {
        mCloseableStaticBitmap.finalize();
        Assert.assertTrue(mCloseableStaticBitmap.isClosed());
        Assert.assertNull(mCloseableStaticBitmap.getUnderlyingBitmap());
        Mockito.verify(mResourceReleaser).release(mBitmap);
    }
}

