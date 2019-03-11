/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import Drawable.Callback;
import ScaleType.CENTER;
import ScaleType.CENTER_CROP;
import ScaleType.CENTER_INSIDE;
import ScaleType.FIT_CENTER;
import ScaleType.FIT_END;
import ScaleType.FIT_START;
import ScaleType.FIT_XY;
import ScaleType.FOCUS_CROP;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ScaleTypeDrawableTest {
    private Drawable mUnderlyingDrawable = Mockito.mock(Drawable.class);

    private PointF mFocusPoint = new PointF(0.1F, 0.4F);

    private Callback mCallback = Mockito.mock(Callback.class);

    private Rect mViewBounds = new Rect(10, 10, 410, 310);

    private ScaleTypeDrawable mScaleTypeDrawable;

    @Test
    public void testIntrinsicDimensions() {
        Mockito.when(mUnderlyingDrawable.getIntrinsicWidth()).thenReturn(100);
        Mockito.when(mUnderlyingDrawable.getIntrinsicHeight()).thenReturn(200);
        Assert.assertEquals(100, mScaleTypeDrawable.getIntrinsicWidth());
        Assert.assertEquals(200, mScaleTypeDrawable.getIntrinsicHeight());
    }

    @Test
    public void testBasics() {
        // initial state
        Assert.assertEquals(mUnderlyingDrawable, mScaleTypeDrawable.getCurrent());
        Assert.assertEquals(CENTER, mScaleTypeDrawable.getScaleType());
        Assert.assertEquals(null, mScaleTypeDrawable.getFocusPoint());
        mScaleTypeDrawable.setScaleType(FIT_XY);
        Assert.assertEquals(FIT_XY, mScaleTypeDrawable.getScaleType());
        mScaleTypeDrawable.setScaleType(FOCUS_CROP);
        Assert.assertEquals(FOCUS_CROP, mScaleTypeDrawable.getScaleType());
        mScaleTypeDrawable.setFocusPoint(mFocusPoint);
        AndroidGraphicsTestUtils.assertEquals(mFocusPoint, mScaleTypeDrawable.getFocusPoint(), 0.0F);
    }

    @Test
    public void testConfigureBounds_NoIntrinsicDimensions() {
        testConfigureBounds_NoIntrinsicDimensions(FIT_XY, mViewBounds);
        testConfigureBounds_NoIntrinsicDimensions(FIT_START, mViewBounds);
        testConfigureBounds_NoIntrinsicDimensions(FIT_CENTER, mViewBounds);
        testConfigureBounds_NoIntrinsicDimensions(FIT_END, mViewBounds);
        testConfigureBounds_NoIntrinsicDimensions(CENTER, mViewBounds);
        testConfigureBounds_NoIntrinsicDimensions(CENTER_INSIDE, mViewBounds);
        testConfigureBounds_NoIntrinsicDimensions(CENTER_CROP, mViewBounds);
        testConfigureBounds_NoIntrinsicDimensions(FOCUS_CROP, mViewBounds);
    }

    @Test
    public void testConfigureBounds_SameAsView() {
        testConfigureBounds_SameAsView(FIT_XY, mViewBounds);
        testConfigureBounds_SameAsView(FIT_START, mViewBounds);
        testConfigureBounds_SameAsView(FIT_CENTER, mViewBounds);
        testConfigureBounds_SameAsView(FIT_END, mViewBounds);
        testConfigureBounds_SameAsView(CENTER, mViewBounds);
        testConfigureBounds_SameAsView(CENTER_INSIDE, mViewBounds);
        testConfigureBounds_SameAsView(CENTER_CROP, mViewBounds);
        testConfigureBounds_SameAsView(FOCUS_CROP, mViewBounds);
    }

    @Test
    public void testConfigureBounds_FIT_XY() {
        mScaleTypeDrawable.setScaleType(FIT_XY);
        mScaleTypeDrawable.setBounds(mViewBounds);
        Mockito.reset(mUnderlyingDrawable);
        Mockito.when(mUnderlyingDrawable.getIntrinsicWidth()).thenReturn(40);
        Mockito.when(mUnderlyingDrawable.getIntrinsicHeight()).thenReturn(30);
        mScaleTypeDrawable.configureBounds();
        Mockito.verify(mUnderlyingDrawable).getIntrinsicWidth();
        Mockito.verify(mUnderlyingDrawable).getIntrinsicHeight();
        Mockito.verify(mUnderlyingDrawable).setBounds(mViewBounds);
        Assert.assertEquals(null, mScaleTypeDrawable.mDrawMatrix);
        Mockito.verifyNoMoreInteractions(mUnderlyingDrawable);
    }

    /**
     * Underlying drawable's aspect ratio is bigger than view's, so it has to be slided horizontally
     * after scaling.
     */
    @Test
    public void testConfigureBounds_CENTER_CROP_H() {
        Rect bounds = new Rect(10, 10, 410, 310);
        int width = 400;
        int height = 200;
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(1.5F, 1.5F);
        expectedMatrix.postTranslate((-89), 10);
        testConfigureBounds(bounds, width, height, CENTER_CROP, null, expectedMatrix);
    }

    /**
     * Underlying drawable's aspect ratio is smaller than view's, so it has to be slided vertically
     * after scaling.
     */
    @Test
    public void testConfigureBounds_CENTER_CROP_V() {
        Rect bounds = new Rect(10, 10, 410, 310);
        int width = 200;
        int height = 300;
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(2.0F, 2.0F);
        expectedMatrix.postTranslate(10, (-139));
        testConfigureBounds(bounds, width, height, CENTER_CROP, null, expectedMatrix);
    }

    /**
     * Underlying drawable's aspect ratio is bigger than view's, so it has to be slided horizontally
     * after scaling. Focus point is too much left, so it cannot be completely centered. Left-most
     * part of the image is displayed.
     */
    @Test
    public void testConfigureBounds_FOCUS_CROP_HL() {
        Rect bounds = new Rect(10, 10, 410, 310);
        int width = 400;
        int height = 200;
        PointF focusPoint = new PointF(0.1F, 0.5F);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(1.5F, 1.5F);
        expectedMatrix.postTranslate(10, 10);
        testConfigureBounds(bounds, width, height, FOCUS_CROP, focusPoint, expectedMatrix);
    }

    /**
     * Underlying drawable's aspect ratio is bigger than view's, so it has to be slided horizontally
     * after scaling. Focus point is at 40% and it can be completely centered.
     */
    @Test
    public void testConfigureBounds_FOCUS_CROP_HC() {
        Rect bounds = new Rect(10, 10, 410, 310);
        int width = 400;
        int height = 200;
        PointF focusPoint = new PointF(0.4F, 0.5F);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(1.5F, 1.5F);
        expectedMatrix.postTranslate((-29), 10);
        testConfigureBounds(bounds, width, height, FOCUS_CROP, focusPoint, expectedMatrix);
    }

    /**
     * Underlying drawable's aspect ratio is bigger than view's, so it has to be slided horizontally
     * after scaling. Focus point is too much right, so it cannot be completely centered. Right-most
     * part of the image is displayed.
     */
    @Test
    public void testConfigureBounds_FOCUS_CROP_HR() {
        Rect bounds = new Rect(10, 10, 410, 310);
        int width = 400;
        int height = 200;
        PointF focusPoint = new PointF(0.9F, 0.5F);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(1.5F, 1.5F);
        expectedMatrix.postTranslate((-189), 10);
        testConfigureBounds(bounds, width, height, FOCUS_CROP, focusPoint, expectedMatrix);
    }

    /**
     * Underlying drawable's aspect ratio is smaller than view's, so it has to be slided vertically
     * after scaling. Focus point is too much top, so it cannot be completely centered. Top-most
     * part of the image is displayed.
     */
    @Test
    public void testConfigureBounds_FOCUS_CROP_VT() {
        Rect bounds = new Rect(10, 10, 410, 310);
        int width = 200;
        int height = 300;
        PointF focusPoint = new PointF(0.5F, 0.1F);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(2.0F, 2.0F);
        expectedMatrix.postTranslate(10, 10);
        testConfigureBounds(bounds, width, height, FOCUS_CROP, focusPoint, expectedMatrix);
    }

    /**
     * Underlying drawable's aspect ratio is smaller than view's, so it has to be slided vertically
     * after scaling. Focus point is at 40% and it can be completely centered.
     */
    @Test
    public void testConfigureBounds_FOCUS_CROP_VC() {
        Rect bounds = new Rect(10, 10, 410, 310);
        int width = 200;
        int height = 300;
        PointF focusPoint = new PointF(0.5F, 0.4F);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(2.0F, 2.0F);
        expectedMatrix.postTranslate(10, (-79));
        testConfigureBounds(bounds, width, height, FOCUS_CROP, focusPoint, expectedMatrix);
        // expected bounds of the actual image after the scaling has been performed (without cropping)
        testActualImageBounds(new RectF(10.0F, (-79.0F), 410.0F, 521.0F));
    }

    /**
     * Underlying drawable's aspect ratio is smaller than view's, so it has to be slided vertically
     * after scaling. Focus point is too much bottom, so it cannot be completely centered. Bottom-most
     * part of the image is displayed.
     */
    @Test
    public void testConfigureBounds_FOCUS_CROP_VB() {
        Rect bounds = new Rect(10, 10, 410, 310);
        int width = 200;
        int height = 300;
        PointF focusPoint = new PointF(0.5F, 0.9F);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(2.0F, 2.0F);
        expectedMatrix.postTranslate(10, (-289));
        testConfigureBounds(bounds, width, height, FOCUS_CROP, focusPoint, expectedMatrix);
    }

    @Test
    public void testConfigureBoundsNoOpWhenScaleTypeNotChanged() {
        Rect bounds = new Rect(10, 10, 410, 310);
        int width = 200;
        int height = 300;
        PointF focusPoint = new PointF(0.5F, 0.9F);
        Matrix expectedMatrix = new Matrix();
        expectedMatrix.setScale(2.0F, 2.0F);
        expectedMatrix.postTranslate(10, (-289));
        testConfigureBounds(bounds, width, height, FOCUS_CROP, focusPoint, expectedMatrix);
        mScaleTypeDrawable.setScaleType(FOCUS_CROP);
        Mockito.verifyNoMoreInteractions(mUnderlyingDrawable);
    }
}

