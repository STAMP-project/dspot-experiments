/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import Drawable.Callback;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class SettableDrawableTest {
    private Drawable mUnderlyingDrawable0;

    private Drawable mUnderlyingDrawable1;

    private Drawable mUnderlyingDrawable2;

    private Drawable mUnderlyingDrawable3;

    private ForwardingDrawable mSettableDrawable;

    @Test
    public void testIntrinsicDimensions() {
        Mockito.when(mUnderlyingDrawable0.getIntrinsicWidth()).thenReturn(100);
        Mockito.when(mUnderlyingDrawable0.getIntrinsicHeight()).thenReturn(200);
        Mockito.when(mUnderlyingDrawable1.getIntrinsicWidth()).thenReturn(300);
        Mockito.when(mUnderlyingDrawable1.getIntrinsicHeight()).thenReturn(400);
        Assert.assertEquals(100, mSettableDrawable.getIntrinsicWidth());
        Assert.assertEquals(200, mSettableDrawable.getIntrinsicHeight());
        mSettableDrawable.setDrawable(mUnderlyingDrawable1);
        Assert.assertEquals(300, mSettableDrawable.getIntrinsicWidth());
        Assert.assertEquals(400, mSettableDrawable.getIntrinsicHeight());
    }

    @Test
    public void testGetCurrent() {
        // initial drawable is mUnderlyingDrawable0
        Assert.assertEquals(mUnderlyingDrawable0, mSettableDrawable.getCurrent());
        mSettableDrawable.setDrawable(mUnderlyingDrawable1);
        Assert.assertEquals(mUnderlyingDrawable1, mSettableDrawable.getCurrent());
        mSettableDrawable.setDrawable(mUnderlyingDrawable2);
        Assert.assertEquals(mUnderlyingDrawable2, mSettableDrawable.getCurrent());
        mSettableDrawable.setDrawable(mUnderlyingDrawable3);
        Assert.assertEquals(mUnderlyingDrawable3, mSettableDrawable.getCurrent());
    }

    @Test
    public void testSetCurrent() {
        Drawable.Callback callback = Mockito.mock(Callback.class);
        mSettableDrawable.setCallback(callback);
        mSettableDrawable.setDrawable(mUnderlyingDrawable1);
        Mockito.verify(mUnderlyingDrawable0).setCallback(null);
        Mockito.verify(mUnderlyingDrawable1).setCallback(ArgumentMatchers.isNotNull(Callback.class));
        Mockito.verify(callback).invalidateDrawable(mSettableDrawable);
    }
}

