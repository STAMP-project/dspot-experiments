/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.image;


import Bitmap.Config.ARGB_8888;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.imagepipeline.bitmaps.SimpleBitmapReleaser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static ImmutableQualityInfo.FULL_QUALITY;


@RunWith(RobolectricTestRunner.class)
public class CloseableStaticBitmapTest {
    private static final int WIDTH = 10;

    private static final int HEIGHT = 5;

    private Bitmap mBitmap;

    private CloseableStaticBitmap mCloseableStaticBitmap;

    @Test
    public void testWidthAndHeight() {
        assertThat(mCloseableStaticBitmap.getWidth()).isEqualTo(CloseableStaticBitmapTest.WIDTH);
        assertThat(mCloseableStaticBitmap.getHeight()).isEqualTo(CloseableStaticBitmapTest.HEIGHT);
    }

    @Test
    public void testWidthAndHeightWithRotatedImage() {
        // Reverse width and height as the rotation angle should put them back again
        mBitmap = Bitmap.createBitmap(CloseableStaticBitmapTest.HEIGHT, CloseableStaticBitmapTest.WIDTH, ARGB_8888);
        ResourceReleaser<Bitmap> releaser = SimpleBitmapReleaser.getInstance();
        mCloseableStaticBitmap = new CloseableStaticBitmap(mBitmap, releaser, FULL_QUALITY, 90, ExifInterface.ORIENTATION_ROTATE_90);
        assertThat(mCloseableStaticBitmap.getWidth()).isEqualTo(CloseableStaticBitmapTest.WIDTH);
        assertThat(mCloseableStaticBitmap.getHeight()).isEqualTo(CloseableStaticBitmapTest.HEIGHT);
    }

    @Test
    public void testWidthAndHeightWithInvertedOrientationImage() {
        // Reverse width and height as the inverted orienvation should put them back again
        mBitmap = Bitmap.createBitmap(CloseableStaticBitmapTest.HEIGHT, CloseableStaticBitmapTest.WIDTH, ARGB_8888);
        ResourceReleaser<Bitmap> releaser = SimpleBitmapReleaser.getInstance();
        mCloseableStaticBitmap = new CloseableStaticBitmap(mBitmap, releaser, FULL_QUALITY, 0, ExifInterface.ORIENTATION_TRANSPOSE);
        assertThat(mCloseableStaticBitmap.getWidth()).isEqualTo(CloseableStaticBitmapTest.WIDTH);
        assertThat(mCloseableStaticBitmap.getHeight()).isEqualTo(CloseableStaticBitmapTest.HEIGHT);
    }

    @Test
    public void testClose() {
        mCloseableStaticBitmap.close();
        assertThat(mCloseableStaticBitmap.isClosed()).isTrue();
        assertThat(mBitmap.isRecycled()).isTrue();
    }

    @Test
    public void testConvert() {
        CloseableReference<Bitmap> ref = mCloseableStaticBitmap.convertToBitmapReference();
        assertThat(ref.get()).isSameAs(mBitmap);
        assertThat(mCloseableStaticBitmap.isClosed()).isTrue();
    }

    @Test
    public void testCloneUnderlyingBitmapReference() {
        CloseableReference<Bitmap> clonedBitmapReference = mCloseableStaticBitmap.cloneUnderlyingBitmapReference();
        assertThat(clonedBitmapReference).isNotNull();
        assertThat(clonedBitmapReference.get()).isEqualTo(mBitmap);
    }

    @Test
    public void testCloneUnderlyingBitmapReference_whenBitmapClosed_thenReturnNull() {
        mCloseableStaticBitmap.close();
        CloseableReference<Bitmap> clonedBitmapReference = mCloseableStaticBitmap.cloneUnderlyingBitmapReference();
        assertThat(clonedBitmapReference).isNull();
    }
}

