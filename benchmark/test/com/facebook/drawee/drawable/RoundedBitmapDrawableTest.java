/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import Drawable.Callback;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class RoundedBitmapDrawableTest {
    private Resources mResources;

    private Bitmap mBitmap;

    private DisplayMetrics mDisplayMetrics;

    RoundedBitmapDrawable mRoundedBitmapDrawable;

    RoundedBitmapDrawable mRoundedBitmapDrawableWithNullBitmap;

    private final Callback mCallback = Mockito.mock(Callback.class);

    @Test
    public void testSetCircle() {
        mRoundedBitmapDrawable.setCircle(true);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedBitmapDrawable);
        Assert.assertTrue(mRoundedBitmapDrawable.isCircle());
    }

    @Test
    public void testSetRadii() {
        mRoundedBitmapDrawable.setRadii(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8 });
        Mockito.verify(mCallback).invalidateDrawable(mRoundedBitmapDrawable);
        Assert.assertArrayEquals(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8 }, mRoundedBitmapDrawable.getRadii(), 0);
    }

    @Test
    public void testSetRadius() {
        mRoundedBitmapDrawable.setRadius(9);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedBitmapDrawable);
        Assert.assertArrayEquals(new float[]{ 9, 9, 9, 9, 9, 9, 9, 9 }, mRoundedBitmapDrawable.getRadii(), 0);
    }

    @Test
    public void testSetBorder() {
        int color = 305419896;
        float width = 5;
        mRoundedBitmapDrawable.setBorder(color, width);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedBitmapDrawable);
        Assert.assertEquals(color, mRoundedBitmapDrawable.getBorderColor());
        Assert.assertEquals(width, mRoundedBitmapDrawable.getBorderWidth(), 0);
    }

    @Test
    public void testSetPadding() {
        float padding = 10;
        mRoundedBitmapDrawable.setPadding(padding);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedBitmapDrawable);
        Assert.assertEquals(padding, mRoundedBitmapDrawable.getPadding(), 0);
    }

    @Test
    public void testSetScaleDownInsideBorders() {
        mRoundedBitmapDrawable.setScaleDownInsideBorders(true);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedBitmapDrawable);
        Assert.assertTrue(mRoundedBitmapDrawable.getScaleDownInsideBorders());
    }

    @Test
    public void testSetPaintFilterBitmap() {
        mRoundedBitmapDrawable.setPaintFilterBitmap(true);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedBitmapDrawable);
        Assert.assertTrue(mRoundedBitmapDrawable.getPaintFilterBitmap());
    }

    @Test
    public void testShouldRoundDefault() {
        Assert.assertFalse(mRoundedBitmapDrawable.shouldRound());
        Assert.assertFalse(mRoundedBitmapDrawableWithNullBitmap.shouldRound());
    }

    @Test
    public void testShouldRoundRadius() {
        mRoundedBitmapDrawable.setRadius(5);
        Assert.assertTrue(mRoundedBitmapDrawable.shouldRound());
        mRoundedBitmapDrawable.setRadius(0);
        Assert.assertFalse(mRoundedBitmapDrawable.shouldRound());
        mRoundedBitmapDrawableWithNullBitmap.setRadius(5);
        Assert.assertFalse(mRoundedBitmapDrawableWithNullBitmap.shouldRound());
        mRoundedBitmapDrawableWithNullBitmap.setRadius(0);
        Assert.assertFalse(mRoundedBitmapDrawableWithNullBitmap.shouldRound());
    }

    @Test
    public void testShouldRoundRadii() {
        mRoundedBitmapDrawable.setRadii(new float[]{ 0, 0, 0, 0, 0, 0, 0, 1 });
        Assert.assertTrue(mRoundedBitmapDrawable.shouldRound());
        mRoundedBitmapDrawable.setRadii(new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 });
        Assert.assertFalse(mRoundedBitmapDrawable.shouldRound());
        mRoundedBitmapDrawableWithNullBitmap.setRadii(new float[]{ 0, 0, 0, 0, 0, 0, 0, 1 });
        Assert.assertFalse(mRoundedBitmapDrawableWithNullBitmap.shouldRound());
        mRoundedBitmapDrawableWithNullBitmap.setRadii(new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 });
        Assert.assertFalse(mRoundedBitmapDrawableWithNullBitmap.shouldRound());
    }

    @Test
    public void testShouldRoundCircle() {
        mRoundedBitmapDrawable.setCircle(true);
        Assert.assertTrue(mRoundedBitmapDrawable.shouldRound());
        mRoundedBitmapDrawable.setCircle(false);
        Assert.assertFalse(mRoundedBitmapDrawable.shouldRound());
        mRoundedBitmapDrawableWithNullBitmap.setCircle(true);
        Assert.assertFalse(mRoundedBitmapDrawableWithNullBitmap.shouldRound());
        mRoundedBitmapDrawableWithNullBitmap.setCircle(false);
        Assert.assertFalse(mRoundedBitmapDrawableWithNullBitmap.shouldRound());
    }

    @Test
    public void testShouldRoundBorder() {
        mRoundedBitmapDrawable.setBorder(-1, 1);
        Assert.assertTrue(mRoundedBitmapDrawable.shouldRound());
        mRoundedBitmapDrawable.setBorder(0, 0);
        Assert.assertFalse(mRoundedBitmapDrawable.shouldRound());
        mRoundedBitmapDrawableWithNullBitmap.setBorder(-1, 1);
        Assert.assertFalse(mRoundedBitmapDrawableWithNullBitmap.shouldRound());
        mRoundedBitmapDrawableWithNullBitmap.setBorder(0, 0);
        Assert.assertFalse(mRoundedBitmapDrawableWithNullBitmap.shouldRound());
    }

    @Test
    public void testPreservePaintOnDrawableCopy() {
        ColorFilter colorFilter = Mockito.mock(ColorFilter.class);
        Paint originalPaint = Mockito.mock(Paint.class);
        BitmapDrawable originalVersion = Mockito.mock(BitmapDrawable.class);
        originalPaint.setColorFilter(colorFilter);
        Mockito.when(originalVersion.getPaint()).thenReturn(originalPaint);
        RoundedBitmapDrawable roundedVersion = RoundedBitmapDrawable.fromBitmapDrawable(mResources, originalVersion);
        Assert.assertEquals(originalVersion.getPaint().getColorFilter(), roundedVersion.getPaint().getColorFilter());
    }
}

