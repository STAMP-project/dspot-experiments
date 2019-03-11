/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import Color.GREEN;
import Drawable.Callback;
import Paint.Style.FILL;
import Paint.Style.STROKE;
import PixelFormat.OPAQUE;
import PixelFormat.TRANSLUCENT;
import PixelFormat.TRANSPARENT;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class RoundedColorDrawableTest {
    private Canvas mCanvas;

    private Callback mCallback;

    private RoundedColorDrawable mRoundedColorDrawable;

    @Test
    public void testInitialSetup() {
        Assert.assertEquals(GREEN, mRoundedColorDrawable.getColor());
        Assert.assertFalse(mRoundedColorDrawable.isCircle());
        Assert.assertArrayEquals(new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 }, mRoundedColorDrawable.getRadii(), 0.0F);
    }

    @Test
    public void testSetCircle() {
        mRoundedColorDrawable.setCircle(true);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedColorDrawable);
        Assert.assertTrue(mRoundedColorDrawable.isCircle());
    }

    @Test
    public void testSetRadii() {
        float[] radii = new float[]{ 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F };
        float[] expectedRadii = new float[]{ 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F };
        mRoundedColorDrawable.setRadii(radii);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedColorDrawable);
        Assert.assertArrayEquals(expectedRadii, mRoundedColorDrawable.getRadii(), 0.0F);
    }

    @Test
    public void testSetRadius() {
        float radius = 8.0F;
        float[] expectedRadii = new float[]{ 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F, 8.0F };
        mRoundedColorDrawable.setRadius(radius);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedColorDrawable);
        Assert.assertArrayEquals(expectedRadii, mRoundedColorDrawable.getRadii(), 0.0F);
    }

    @Test
    public void testSetColor() {
        int color = -1071500202;
        mRoundedColorDrawable.setColor(color);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedColorDrawable);
        Assert.assertEquals(color, mRoundedColorDrawable.getColor());
    }

    @Test
    public void testSetAlpha() {
        int alpha = 10;
        mRoundedColorDrawable.setAlpha(alpha);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedColorDrawable);
        Assert.assertEquals(alpha, mRoundedColorDrawable.getAlpha());
    }

    @Test
    public void testSetBorder() {
        int color = -1071500202;
        float width = 5;
        mRoundedColorDrawable.setBorder(color, width);
        Mockito.verify(mCallback, Mockito.times(2)).invalidateDrawable(mRoundedColorDrawable);
        Assert.assertEquals(color, mRoundedColorDrawable.getBorderColor());
        Assert.assertEquals(width, mRoundedColorDrawable.getBorderWidth(), 0);
    }

    @Test
    public void testSetPadding() {
        float padding = 10;
        mRoundedColorDrawable.setPadding(padding);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedColorDrawable);
        Assert.assertEquals(padding, mRoundedColorDrawable.getPadding(), 0);
    }

    @Test
    public void testSetScaleDownInsideBorders() {
        mRoundedColorDrawable.setScaleDownInsideBorders(true);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedColorDrawable);
        Assert.assertTrue(mRoundedColorDrawable.getScaleDownInsideBorders());
    }

    @Test
    public void testSetPaintFilterBitmap() {
        mRoundedColorDrawable.setPaintFilterBitmap(true);
        Mockito.verify(mCallback).invalidateDrawable(mRoundedColorDrawable);
        Assert.assertTrue(mRoundedColorDrawable.getPaintFilterBitmap());
    }

    @Test
    public void testDrawWithoutBorder() {
        int internalColor = -1071500202;
        int alpha = 10;
        int expectedInternalPaintColor = 119682134;
        mRoundedColorDrawable.setAlpha(alpha);
        mRoundedColorDrawable.setColor(internalColor);
        mRoundedColorDrawable.draw(mCanvas);
        ArgumentCaptor<Paint> argumentCaptor = ArgumentCaptor.forClass(Paint.class);
        Mockito.verify(mCanvas).drawPath(ArgumentMatchers.any(Path.class), argumentCaptor.capture());
        Paint internalPaint = argumentCaptor.getValue();
        Assert.assertEquals(expectedInternalPaintColor, internalPaint.getColor());
        Assert.assertEquals(FILL, internalPaint.getStyle());
    }

    @Test
    public void testDrawWithBorder() {
        int internalColor = -1071500202;
        int alpha = 10;
        int borderColor = -1072548778;
        int expectedBorderPaintColor = 118633558;
        float borderWidth = 5;
        mRoundedColorDrawable.setAlpha(alpha);
        mRoundedColorDrawable.setColor(internalColor);
        mRoundedColorDrawable.setBorder(borderColor, borderWidth);
        mRoundedColorDrawable.draw(mCanvas);
        ArgumentCaptor<Paint> argumentCaptor = ArgumentCaptor.forClass(Paint.class);
        Mockito.verify(mCanvas, Mockito.times(2)).drawPath(ArgumentMatchers.any(Path.class), argumentCaptor.capture());
        Assert.assertEquals(2, argumentCaptor.getAllValues().size());
        Paint borderPaint = argumentCaptor.getAllValues().get(1);
        Assert.assertEquals(expectedBorderPaintColor, borderPaint.getColor());
        Assert.assertEquals(STROKE, borderPaint.getStyle());
        Assert.assertEquals(borderWidth, borderPaint.getStrokeWidth(), 0);
    }

    @Test
    public void testGetOpacity() {
        mRoundedColorDrawable.setColor(-1879048193);
        mRoundedColorDrawable.setAlpha(255);
        Assert.assertEquals(TRANSLUCENT, mRoundedColorDrawable.getOpacity());
        mRoundedColorDrawable.setColor(0);
        mRoundedColorDrawable.setAlpha(255);
        Assert.assertEquals(TRANSPARENT, mRoundedColorDrawable.getOpacity());
        mRoundedColorDrawable.setColor(-1);
        mRoundedColorDrawable.setAlpha(255);
        Assert.assertEquals(OPAQUE, mRoundedColorDrawable.getOpacity());
        mRoundedColorDrawable.setAlpha(100);
        Assert.assertEquals(TRANSLUCENT, mRoundedColorDrawable.getOpacity());
        mRoundedColorDrawable.setAlpha(0);
        Assert.assertEquals(TRANSPARENT, mRoundedColorDrawable.getOpacity());
    }
}

