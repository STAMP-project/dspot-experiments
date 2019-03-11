/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import Drawable.Callback;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ArrayDrawableTest {
    private Drawable mUnderlyingDrawable0;

    private Drawable mUnderlyingDrawable1;

    private Drawable mUnderlyingDrawable2;

    private ArrayDrawable mArrayDrawable;

    @Test
    public void testIntrinsicDimensions() {
        Mockito.when(mUnderlyingDrawable0.getIntrinsicWidth()).thenReturn(100);
        Mockito.when(mUnderlyingDrawable1.getIntrinsicWidth()).thenReturn(200);
        Mockito.when(mUnderlyingDrawable2.getIntrinsicWidth()).thenReturn(150);
        Mockito.when(mUnderlyingDrawable0.getIntrinsicHeight()).thenReturn(500);
        Mockito.when(mUnderlyingDrawable1.getIntrinsicHeight()).thenReturn(300);
        Mockito.when(mUnderlyingDrawable2.getIntrinsicHeight()).thenReturn(400);
        Assert.assertEquals(200, mArrayDrawable.getIntrinsicWidth());
        Assert.assertEquals(500, mArrayDrawable.getIntrinsicHeight());
    }

    @Test
    public void testGetDrawable() {
        Assert.assertEquals(mUnderlyingDrawable0, mArrayDrawable.getDrawable(0));
        Assert.assertEquals(null, mArrayDrawable.getDrawable(1));
        Assert.assertEquals(mUnderlyingDrawable1, mArrayDrawable.getDrawable(2));
        Assert.assertEquals(null, mArrayDrawable.getDrawable(3));
        Assert.assertEquals(mUnderlyingDrawable2, mArrayDrawable.getDrawable(4));
    }

    @Test
    public void testDraw() {
        Canvas mockCanvas = Mockito.mock(Canvas.class);
        mArrayDrawable.draw(mockCanvas);
        Mockito.verify(mUnderlyingDrawable0).draw(mockCanvas);
        Mockito.verify(mUnderlyingDrawable1).draw(mockCanvas);
        Mockito.verify(mUnderlyingDrawable2).draw(mockCanvas);
    }

    @Test
    public void testOnBoundsChange() {
        Rect rectMock = Mockito.mock(Rect.class);
        mArrayDrawable.onBoundsChange(rectMock);
        Mockito.verify(mUnderlyingDrawable0).setBounds(rectMock);
        Mockito.verify(mUnderlyingDrawable1).setBounds(rectMock);
        Mockito.verify(mUnderlyingDrawable2).setBounds(rectMock);
    }

    @Test
    public void testSetAlpha() {
        mArrayDrawable.setAlpha(11);
        Mockito.verify(mUnderlyingDrawable0).setAlpha(11);
        Mockito.verify(mUnderlyingDrawable1).setAlpha(11);
        Mockito.verify(mUnderlyingDrawable2).setAlpha(11);
    }

    @Test
    public void testSetColorFilter() {
        ColorFilter colorFilter = Mockito.mock(ColorFilter.class);
        mArrayDrawable.setColorFilter(colorFilter);
        Mockito.verify(mUnderlyingDrawable0).setColorFilter(colorFilter);
        Mockito.verify(mUnderlyingDrawable1).setColorFilter(colorFilter);
        Mockito.verify(mUnderlyingDrawable2).setColorFilter(colorFilter);
    }

    @Test
    public void testSetDither() {
        testSetDither(true);
        testSetDither(false);
    }

    @Test
    public void testSetFilterBitmap() {
        testSetFilterBitmap(true);
        testSetFilterBitmap(false);
    }

    @Test
    public void testSetVisible() {
        testSetVisible(true, true);
        testSetVisible(true, false);
        testSetVisible(false, true);
        testSetVisible(false, false);
    }

    @Test
    public void testSetDrawableNonMutated() {
        Drawable newDrawable = Mockito.mock(Drawable.class);
        mArrayDrawable.setDrawable(2, newDrawable);
        Assert.assertSame(newDrawable, mArrayDrawable.getDrawable(2));
        Mockito.verify(mUnderlyingDrawable1).setCallback(ArgumentMatchers.isNull(Callback.class));
        Mockito.verify(newDrawable).setCallback(ArgumentMatchers.eq(mArrayDrawable));
        Mockito.verify(newDrawable, Mockito.never()).mutate();
    }

    @Test
    public void testSetDrawableMutated() {
        mArrayDrawable.setDrawable(2, null);
        int level = 10;
        int[] state = new int[]{ 1, 2, 3 };
        BitmapDrawable newDrawable = Mockito.mock(BitmapDrawable.class);
        Mockito.when(newDrawable.mutate()).thenReturn(newDrawable);
        Rect rect = new Rect(1, 2, 3, 4);
        mArrayDrawable.setBounds(rect);
        mArrayDrawable.setVisible(true, false);
        mArrayDrawable.setLevel(level);
        mArrayDrawable.setState(state);
        mArrayDrawable = ((ArrayDrawable) (mArrayDrawable.mutate()));
        mArrayDrawable.setDrawable(2, newDrawable);
        Mockito.verify(newDrawable).setBounds(ArgumentMatchers.eq(rect));
        Mockito.verify(newDrawable).setVisible(true, false);
        Mockito.verify(newDrawable).setLevel(level);
        Mockito.verify(newDrawable).setState(state);
        Mockito.verify(mUnderlyingDrawable1).setCallback(ArgumentMatchers.isNull(Callback.class));
        Mockito.verify(newDrawable).setCallback(ArgumentMatchers.eq(mArrayDrawable));
        Mockito.verify(newDrawable).mutate();
    }
}

