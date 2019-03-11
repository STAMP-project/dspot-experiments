/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.generic;


import RoundingParams.RoundingMethod.BITMAP_ONLY;
import RoundingParams.RoundingMethod.OVERLAY_COLOR;
import android.graphics.Color;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class RoundingParamsTest {
    private RoundingParams mRoundingParams;

    @Test
    public void testDefaults() {
        Assert.assertEquals(BITMAP_ONLY, mRoundingParams.getRoundingMethod());
        Assert.assertFalse(mRoundingParams.getRoundAsCircle());
        Assert.assertNull(mRoundingParams.getCornersRadii());
        Assert.assertEquals(0, mRoundingParams.getOverlayColor());
        Assert.assertFalse(mRoundingParams.getScaleDownInsideBorders());
        Assert.assertFalse(mRoundingParams.getPaintFilterBitmap());
    }

    @Test
    public void testSetCircle() {
        Assert.assertSame(mRoundingParams, mRoundingParams.setRoundAsCircle(true));
        Assert.assertTrue(mRoundingParams.getRoundAsCircle());
        Assert.assertSame(mRoundingParams, mRoundingParams.setRoundAsCircle(false));
        Assert.assertFalse(mRoundingParams.getRoundAsCircle());
    }

    @Test
    public void testSetRadii() {
        mRoundingParams.setCornersRadius(9);
        Assert.assertArrayEquals(new float[]{ 9, 9, 9, 9, 9, 9, 9, 9 }, mRoundingParams.getCornersRadii(), 0.0F);
        mRoundingParams.setCornersRadii(8, 7, 2, 1);
        Assert.assertArrayEquals(new float[]{ 8, 8, 7, 7, 2, 2, 1, 1 }, mRoundingParams.getCornersRadii(), 0.0F);
        mRoundingParams.setCornersRadii(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8 });
        Assert.assertArrayEquals(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8 }, mRoundingParams.getCornersRadii(), 0.0F);
    }

    @Test
    public void testSetRoundingMethod() {
        mRoundingParams.setRoundingMethod(OVERLAY_COLOR);
        Assert.assertEquals(OVERLAY_COLOR, mRoundingParams.getRoundingMethod());
        mRoundingParams.setRoundingMethod(BITMAP_ONLY);
        Assert.assertEquals(BITMAP_ONLY, mRoundingParams.getRoundingMethod());
    }

    @Test
    public void testSetOverlayColor() {
        mRoundingParams.setOverlayColor(-1072548778);
        Assert.assertEquals(-1072548778, mRoundingParams.getOverlayColor());
        Assert.assertEquals(OVERLAY_COLOR, mRoundingParams.getRoundingMethod());
    }

    @Test
    public void testSetBorder() {
        int borderColor = Color.RED;
        float borderWidth = 0.8F;
        mRoundingParams.setBorder(borderColor, borderWidth);
        Assert.assertEquals(borderColor, mRoundingParams.getBorderColor());
        Assert.assertEquals(borderWidth, mRoundingParams.getBorderWidth(), 0);
    }

    @Test
    public void testSetScaleDownInsideBorders() {
        Assert.assertSame(mRoundingParams, mRoundingParams.setScaleDownInsideBorders(true));
        Assert.assertTrue(mRoundingParams.getScaleDownInsideBorders());
        Assert.assertSame(mRoundingParams, mRoundingParams.setScaleDownInsideBorders(false));
        Assert.assertFalse(mRoundingParams.getScaleDownInsideBorders());
    }

    @Test
    public void testSetPaintFilterBitmap() {
        Assert.assertSame(mRoundingParams, mRoundingParams.setPaintFilterBitmap(true));
        Assert.assertTrue(mRoundingParams.getPaintFilterBitmap());
        Assert.assertSame(mRoundingParams, mRoundingParams.setPaintFilterBitmap(false));
        Assert.assertFalse(mRoundingParams.getPaintFilterBitmap());
    }

    @Test
    public void testFactoryMethods() {
        RoundingParams params1 = RoundingParams.asCircle();
        Assert.assertTrue(params1.getRoundAsCircle());
        RoundingParams params2 = RoundingParams.fromCornersRadius(9);
        Assert.assertFalse(params2.getRoundAsCircle());
        Assert.assertArrayEquals(new float[]{ 9, 9, 9, 9, 9, 9, 9, 9 }, params2.getCornersRadii(), 0.0F);
        RoundingParams params3 = RoundingParams.fromCornersRadii(8, 7, 2, 1);
        Assert.assertFalse(params3.getRoundAsCircle());
        Assert.assertArrayEquals(new float[]{ 8, 8, 7, 7, 2, 2, 1, 1 }, params3.getCornersRadii(), 0.0F);
        RoundingParams params4 = RoundingParams.fromCornersRadii(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8 });
        Assert.assertFalse(params4.getRoundAsCircle());
        Assert.assertArrayEquals(new float[]{ 1, 2, 3, 4, 5, 6, 7, 8 }, params4.getCornersRadii(), 0.0F);
    }
}

