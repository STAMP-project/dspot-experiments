/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.generic;


import ScaleType.CENTER;
import ScaleType.CENTER_CROP;
import ScaleType.CENTER_INSIDE;
import ScaleType.FIT_CENTER;
import ScaleType.FIT_END;
import ScaleType.FIT_START;
import ScaleType.FOCUS_CROP;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import com.facebook.drawee.drawable.AndroidGraphicsTestUtils;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class GenericDraweeHierarchyBuilderTest {
    private final Drawable mBackgroundDrawable1 = Mockito.mock(BitmapDrawable.class);

    private final Drawable mBackgroundDrawable2 = Mockito.mock(BitmapDrawable.class);

    private final Drawable mOverlayDrawable1 = Mockito.mock(BitmapDrawable.class);

    private final Drawable mOverlayDrawable2 = Mockito.mock(BitmapDrawable.class);

    private final BitmapDrawable mPlaceholderDrawable1 = Mockito.mock(BitmapDrawable.class);

    private final BitmapDrawable mFailureDrawable1 = Mockito.mock(BitmapDrawable.class);

    private final BitmapDrawable mRetryDrawable1 = Mockito.mock(BitmapDrawable.class);

    private final BitmapDrawable mPlaceholderDrawable2 = Mockito.mock(BitmapDrawable.class);

    private final BitmapDrawable mFailureDrawable2 = Mockito.mock(BitmapDrawable.class);

    private final BitmapDrawable mRetryDrawable2 = Mockito.mock(BitmapDrawable.class);

    private final BitmapDrawable mProgressBarDrawable1 = Mockito.mock(BitmapDrawable.class);

    private final BitmapDrawable mProgressBarDrawable2 = Mockito.mock(BitmapDrawable.class);

    private final BitmapDrawable mPressedStateDrawable = Mockito.mock(BitmapDrawable.class);

    private final PointF mFocusPoint = Mockito.mock(PointF.class);

    private final RoundingParams mRoundingParams = Mockito.mock(RoundingParams.class);

    @Test
    public void testBuilder() throws Exception {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(null);
        // test defaults
        testInitialState(builder);
        // test fade duration
        builder.setFadeDuration(100);
        Assert.assertEquals(100, builder.getFadeDuration());
        // test desired aspect ratio
        builder.setDesiredAspectRatio(1.33F);
        Assert.assertEquals(1.33F, builder.getDesiredAspectRatio(), 0);
        // test image setters without modifying scale type (default expected)
        builder.setPlaceholderImage(mPlaceholderDrawable1);
        builder.setRetryImage(mRetryDrawable1);
        builder.setFailureImage(mFailureDrawable1);
        builder.setProgressBarImage(mProgressBarDrawable1);
        Assert.assertEquals(mPlaceholderDrawable1, builder.getPlaceholderImage());
        Assert.assertEquals(CENTER_INSIDE, builder.getPlaceholderImageScaleType());
        Assert.assertEquals(mRetryDrawable1, builder.getRetryImage());
        Assert.assertEquals(CENTER_INSIDE, builder.getRetryImageScaleType());
        Assert.assertEquals(mFailureDrawable1, builder.getFailureImage());
        Assert.assertEquals(CENTER_INSIDE, builder.getFailureImageScaleType());
        Assert.assertEquals(mProgressBarDrawable1, builder.getProgressBarImage());
        Assert.assertEquals(CENTER_INSIDE, builder.getProgressBarImageScaleType());
        // test image setters with explicit scale type
        builder.setPlaceholderImage(mPlaceholderDrawable2, CENTER);
        builder.setRetryImage(mRetryDrawable2, FIT_CENTER);
        builder.setFailureImage(mFailureDrawable2, FIT_END);
        builder.setProgressBarImage(mProgressBarDrawable2, CENTER_CROP);
        Assert.assertEquals(mPlaceholderDrawable2, builder.getPlaceholderImage());
        Assert.assertEquals(CENTER, builder.getPlaceholderImageScaleType());
        Assert.assertEquals(mRetryDrawable2, builder.getRetryImage());
        Assert.assertEquals(FIT_CENTER, builder.getRetryImageScaleType());
        Assert.assertEquals(mFailureDrawable2, builder.getFailureImage());
        Assert.assertEquals(FIT_END, builder.getFailureImageScaleType());
        Assert.assertEquals(mProgressBarDrawable2, builder.getProgressBarImage());
        Assert.assertEquals(CENTER_CROP, builder.getProgressBarImageScaleType());
        // test image setters without modifying scale type (previous scaletype expected)
        builder.setPlaceholderImage(mPlaceholderDrawable1);
        builder.setRetryImage(mRetryDrawable1);
        builder.setFailureImage(mFailureDrawable1);
        builder.setProgressBarImage(mProgressBarDrawable1);
        Assert.assertEquals(mPlaceholderDrawable1, builder.getPlaceholderImage());
        Assert.assertEquals(CENTER, builder.getPlaceholderImageScaleType());
        Assert.assertEquals(mRetryDrawable1, builder.getRetryImage());
        Assert.assertEquals(FIT_CENTER, builder.getRetryImageScaleType());
        Assert.assertEquals(mFailureDrawable1, builder.getFailureImage());
        Assert.assertEquals(FIT_END, builder.getFailureImageScaleType());
        Assert.assertEquals(mProgressBarDrawable1, builder.getProgressBarImage());
        Assert.assertEquals(CENTER_CROP, builder.getProgressBarImageScaleType());
        // test actual image scale type
        builder.setActualImageScaleType(FIT_START);
        Assert.assertEquals(FIT_START, builder.getActualImageScaleType());
        // test actual image focus point
        builder.setActualImageFocusPoint(mFocusPoint);
        AndroidGraphicsTestUtils.assertEquals(mFocusPoint, builder.getActualImageFocusPoint(), 0.0F);
        builder.setActualImageScaleType(FOCUS_CROP);
        Assert.assertSame(FOCUS_CROP, builder.getActualImageScaleType());
        // test backgrounds & overlays
        builder.setOverlays(Arrays.asList(mOverlayDrawable1, mOverlayDrawable2));
        Assert.assertArrayEquals(builder.getOverlays().toArray(), new Drawable[]{ mOverlayDrawable1, mOverlayDrawable2 });
        builder.setBackground(mBackgroundDrawable2);
        builder.setOverlay(mOverlayDrawable2);
        builder.setPressedStateOverlay(mPressedStateDrawable);
        Assert.assertSame(builder.getBackground(), mBackgroundDrawable2);
        Assert.assertArrayEquals(builder.getOverlays().toArray(), new Drawable[]{ mOverlayDrawable2 });
        Assert.assertEquals(builder.getPressedStateOverlay().getClass(), StateListDrawable.class);
        // test clearing backgrounds & overlays
        builder.setBackground(null);
        Assert.assertNull(builder.getBackground());
        builder.setOverlay(null);
        Assert.assertNull(builder.getOverlays());
        builder.setPressedStateOverlay(null);
        Assert.assertNull(builder.getPressedStateOverlay());
        // test rounding params
        builder.setRoundingParams(mRoundingParams);
        Assert.assertEquals(mRoundingParams, builder.getRoundingParams());
        // test reset
        builder.reset();
        testInitialState(builder);
    }
}

