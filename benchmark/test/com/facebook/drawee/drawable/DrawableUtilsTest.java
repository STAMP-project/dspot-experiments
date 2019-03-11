/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import Drawable.Callback;
import PixelFormat.OPAQUE;
import PixelFormat.TRANSLUCENT;
import PixelFormat.TRANSPARENT;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link ForwardingDrawable}
 */
@RunWith(RobolectricTestRunner.class)
public class DrawableUtilsTest {
    private final Rect mBounds = Mockito.mock(Rect.class);

    private final int mChangingConfigurations = 305419896;

    private final int mLevel = 3;

    private final boolean mIsVisible = true;

    private final int[] mState = new int[5];

    private final Callback mCallback = Mockito.mock(Callback.class);

    private final TransformCallback mTransformCallback = Mockito.mock(TransformCallback.class);

    @Test
    public void testCopyProperties() {
        testCopyProperties(Mockito.mock(Drawable.class), Mockito.mock(Drawable.class));
    }

    @Test
    public void testSetDrawableProperties() {
        DrawableProperties properties = new DrawableProperties();
        ColorFilter colorFilter = Mockito.mock(ColorFilter.class);
        properties.setAlpha(42);
        properties.setColorFilter(colorFilter);
        properties.setDither(true);
        properties.setFilterBitmap(true);
        Drawable drawableTo = Mockito.mock(Drawable.class);
        DrawableUtils.setDrawableProperties(drawableTo, properties);
        Mockito.verify(drawableTo).setAlpha(42);
        Mockito.verify(drawableTo).setColorFilter(colorFilter);
        Mockito.verify(drawableTo).setDither(true);
        Mockito.verify(drawableTo).setFilterBitmap(true);
    }

    @Test
    public void testSetDrawablePropertiesDefault() {
        DrawableProperties properties = new DrawableProperties();
        Drawable drawableTo = Mockito.mock(Drawable.class);
        DrawableUtils.setDrawableProperties(drawableTo, properties);
        Mockito.verify(drawableTo, Mockito.never()).setAlpha(ArgumentMatchers.anyInt());
        Mockito.verify(drawableTo, Mockito.never()).setColorFilter(ArgumentMatchers.any(ColorFilter.class));
        Mockito.verify(drawableTo, Mockito.never()).setDither(ArgumentMatchers.anyBoolean());
        Mockito.verify(drawableTo, Mockito.never()).setFilterBitmap(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testCopyProperties_Null() {
        Drawable drawableFrom = Mockito.mock(Drawable.class);
        Drawable drawableTo = Mockito.mock(Drawable.class);
        DrawableUtils.copyProperties(null, drawableFrom);
        DrawableUtils.copyProperties(drawableTo, null);
        Mockito.verifyNoMoreInteractions(drawableTo, drawableFrom);
    }

    @Test
    public void testCopyProperties_Same() {
        Drawable drawable = Mockito.mock(Drawable.class);
        DrawableUtils.copyProperties(drawable, drawable);
        Mockito.verifyNoMoreInteractions(drawable);
    }

    @Test
    public void testSetCallbacks() {
        Drawable drawable = Mockito.mock(Drawable.class);
        DrawableUtils.setCallbacks(drawable, mCallback, mTransformCallback);
        Mockito.verify(drawable).setCallback(mCallback);
    }

    @Test
    public void testSetCallbacks_TransformAwareDrawable() {
        ForwardingDrawable transformAwareDrawable = Mockito.mock(ForwardingDrawable.class);
        DrawableUtils.setCallbacks(transformAwareDrawable, mCallback, mTransformCallback);
        Mockito.verify(transformAwareDrawable).setCallback(mCallback);
        Mockito.verify(transformAwareDrawable).setTransformCallback(mTransformCallback);
    }

    @Test
    public void testSetCallbacks_NullCallback() {
        Drawable drawable = Mockito.mock(Drawable.class);
        DrawableUtils.setCallbacks(drawable, null, null);
        Mockito.verify(drawable).setCallback(null);
    }

    @Test
    public void testSetCallbacks_TransformAwareDrawable_NullCallback() {
        ForwardingDrawable transformAwareDrawable = Mockito.mock(ForwardingDrawable.class);
        DrawableUtils.setCallbacks(transformAwareDrawable, null, null);
        Mockito.verify(transformAwareDrawable).setCallback(null);
        Mockito.verify(transformAwareDrawable).setTransformCallback(null);
    }

    @Test
    public void testSetCallbacks_NullDrawable() {
        DrawableUtils.setCallbacks(null, mCallback, mTransformCallback);
    }

    @Test
    public void testMultiplyColorAlpha() {
        Assert.assertEquals(1193046, DrawableUtils.multiplyColorAlpha(-1072548778, 0));
        Assert.assertEquals(118633558, DrawableUtils.multiplyColorAlpha(-1072548778, 10));
        Assert.assertEquals(-1777191850, DrawableUtils.multiplyColorAlpha(-1072548778, 200));
        Assert.assertEquals(-1072548778, DrawableUtils.multiplyColorAlpha(-1072548778, 255));
    }

    @Test
    public void testGetOpacityFromColor() {
        Assert.assertEquals(TRANSPARENT, DrawableUtils.getOpacityFromColor(0));
        Assert.assertEquals(TRANSPARENT, DrawableUtils.getOpacityFromColor(1193046));
        Assert.assertEquals(TRANSPARENT, DrawableUtils.getOpacityFromColor(16777215));
        Assert.assertEquals(TRANSLUCENT, DrawableUtils.getOpacityFromColor(-1073741824));
        Assert.assertEquals(TRANSLUCENT, DrawableUtils.getOpacityFromColor(-1072548778));
        Assert.assertEquals(TRANSLUCENT, DrawableUtils.getOpacityFromColor(-1056964609));
        Assert.assertEquals(OPAQUE, DrawableUtils.getOpacityFromColor(-16777216));
        Assert.assertEquals(OPAQUE, DrawableUtils.getOpacityFromColor(-15584170));
        Assert.assertEquals(OPAQUE, DrawableUtils.getOpacityFromColor(-1));
    }
}

