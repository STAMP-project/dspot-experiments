/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class MatrixDrawableTest {
    private Drawable mUnderlyingDrawable;

    private Matrix mMatrix1;

    private Matrix mMatrix2;

    private MatrixDrawable mMatrixDrawable;

    @Test
    public void testIntrinsicDimensions() {
        Mockito.when(mUnderlyingDrawable.getIntrinsicWidth()).thenReturn(100);
        Mockito.when(mUnderlyingDrawable.getIntrinsicHeight()).thenReturn(200);
        Assert.assertEquals(100, mMatrixDrawable.getIntrinsicWidth());
        Assert.assertEquals(200, mMatrixDrawable.getIntrinsicHeight());
    }

    @Test
    public void testSetMatrix() throws Exception {
        // initial state
        Assert.assertEquals(mUnderlyingDrawable, mMatrixDrawable.getCurrent());
        Assert.assertEquals(mMatrixDrawable.getMatrix(), mMatrix1);
        mMatrixDrawable.setMatrix(mMatrix2);
        Assert.assertEquals(mUnderlyingDrawable, mMatrixDrawable.getCurrent());
        Assert.assertEquals(mMatrixDrawable.getMatrix(), mMatrix2);
    }
}

