/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import Drawable.Callback;
import RoundedCornersDrawable.Type;
import RoundedCornersDrawable.Type.OVERLAY_COLOR;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class RoundedCornersDrawableTest {
    private Drawable mUnderlyingDrawable;

    private RoundedCornersDrawable mRoundedCornersDrawable;

    private Callback mCallback;

    @Test
    public void testInitialSetup() {
        Assert.assertEquals(OVERLAY_COLOR, mRoundedCornersDrawable.mType);
        Assert.assertFalse(mRoundedCornersDrawable.isCircle());
        Assert.assertArrayEquals(new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, mRoundedCornersDrawable.getRadii(), 0);
        Assert.assertEquals(0, mRoundedCornersDrawable.mPaint.getColor());
    }

    @Test
    public void testSetType() {
        RoundedCornersDrawable.Type type = Type.CLIPPING;
        mRoundedCornersDrawable.setType(type);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedCornersDrawable);
        Assert.assertEquals(type, mRoundedCornersDrawable.mType);
    }

    @Test
    public void testSetCircle() {
        mRoundedCornersDrawable.setCircle(true);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedCornersDrawable);
        Assert.assertTrue(mRoundedCornersDrawable.isCircle());
    }

    @Test
    public void testSetRadii() {
        mRoundedCornersDrawable.setRadii(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8 });
        Mockito.verify(mCallback).invalidateDrawable(mRoundedCornersDrawable);
        Assert.assertArrayEquals(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8 }, mRoundedCornersDrawable.getRadii(), 0);
    }

    @Test
    public void testSetRadius() {
        mRoundedCornersDrawable.setRadius(9);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedCornersDrawable);
        Assert.assertArrayEquals(new float[]{ 9, 9, 9, 9, 9, 9, 9, 9 }, mRoundedCornersDrawable.getRadii(), 0);
    }

    @Test
    public void testSetOverlayColor() {
        int overlayColor = -1072548778;
        mRoundedCornersDrawable.setOverlayColor(overlayColor);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedCornersDrawable);
        Assert.assertEquals(overlayColor, mRoundedCornersDrawable.getOverlayColor());
    }

    @Test
    public void testSetBorder() {
        float borderWidth = 0.7F;
        int borderColor = Color.CYAN;
        mRoundedCornersDrawable.setBorder(borderColor, borderWidth);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedCornersDrawable);
        Assert.assertEquals(borderColor, mRoundedCornersDrawable.getBorderColor());
        Assert.assertEquals(borderWidth, mRoundedCornersDrawable.getBorderWidth(), 0);
    }

    @Test
    public void testSetPadding() {
        float padding = 10;
        mRoundedCornersDrawable.setPadding(padding);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedCornersDrawable);
        Assert.assertEquals(padding, mRoundedCornersDrawable.getPadding(), 0);
    }

    @Test
    public void testSetScaleDownInsideBorders() {
        mRoundedCornersDrawable.setScaleDownInsideBorders(true);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedCornersDrawable);
        Assert.assertTrue(mRoundedCornersDrawable.getScaleDownInsideBorders());
    }

    @Test
    public void testSetPaintFilterBitmap() {
        mRoundedCornersDrawable.setPaintFilterBitmap(true);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedCornersDrawable);
        Assert.assertTrue(mRoundedCornersDrawable.getPaintFilterBitmap());
    }
}

