/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.memory;


import Config.ARGB_4444;
import Config.ARGB_8888;
import Config.RGB_565;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Bitmap.org.robolectric.annotation.Config;
import com.facebook.imagepipeline.testing.MockBitmapFactory;
import com.facebook.soloader.SoLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.RobolectricTestRunner;


/**
 * Basic tests for BitmapPool
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@Config(manifest = org.robolectric.annotation.Config.class)
public class BitmapPoolTest {
    static {
        SoLoader.setInTestMode();
    }

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    public BucketsBitmapPool mPool;

    @Test
    public void testFree() throws Exception {
        Bitmap bitmap = mPool.alloc(12);
        mPool.free(bitmap);
        Mockito.verify(bitmap).recycle();
    }

    // tests out the getBucketedSize method
    @Test
    public void testGetBucketedSize() throws Exception {
        Assert.assertEquals(12, ((int) (mPool.getBucketedSize(12))));
        Assert.assertEquals(56, ((int) (mPool.getBucketedSize(56))));
    }

    // tests out the getBucketedSizeForValue method
    @Test
    public void testGetBucketedSizeForValue() throws Exception {
        Bitmap bitmap1 = mPool.alloc(12);
        Bitmap bitmap2 = mPool.alloc(56);
        Bitmap bitmap3 = MockBitmapFactory.create(7, 8, RGB_565);
        Bitmap bitmap4 = MockBitmapFactory.create(7, 8, ARGB_8888);
        Assert.assertEquals(12, ((int) (mPool.getBucketedSizeForValue(bitmap1))));
        Assert.assertEquals(56, ((int) (mPool.getBucketedSizeForValue(bitmap2))));
        Assert.assertEquals(112, ((int) (mPool.getBucketedSizeForValue(bitmap3))));
        Assert.assertEquals(224, ((int) (mPool.getBucketedSizeForValue(bitmap4))));
    }

    @Test
    public void testGetSizeInBytes() throws Exception {
        Assert.assertEquals(48, mPool.getSizeInBytes(48));
        Assert.assertEquals(224, mPool.getSizeInBytes(224));
    }

    // Test out bitmap reusability
    @Test
    public void testIsReusable() throws Exception {
        Bitmap b1 = mPool.alloc(12);
        Assert.assertTrue(mPool.isReusable(b1));
        Bitmap b2 = MockBitmapFactory.create(3, 4, Bitmap.Config.ARGB_8888);
        Assert.assertTrue(mPool.isReusable(b2));
        Bitmap b3 = MockBitmapFactory.create(3, 4, ARGB_4444);
        Assert.assertTrue(mPool.isReusable(b3));
        Bitmap b4 = MockBitmapFactory.create(3, 4, Bitmap.Config.ARGB_8888);
        Mockito.doReturn(true).when(b4).isRecycled();
        Assert.assertFalse(mPool.isReusable(b4));
        Bitmap b5 = MockBitmapFactory.create(3, 4, Bitmap.Config.ARGB_8888);
        Mockito.doReturn(false).when(b5).isMutable();
        Assert.assertFalse(mPool.isReusable(b5));
    }
}

