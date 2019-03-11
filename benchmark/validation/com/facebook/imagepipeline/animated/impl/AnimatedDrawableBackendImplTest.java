/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.animated.impl;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import com.facebook.imagepipeline.animated.base.AnimatedImage;
import com.facebook.imagepipeline.animated.base.AnimatedImageFrame;
import com.facebook.imagepipeline.animated.base.AnimatedImageResult;
import com.facebook.imagepipeline.animated.util.AnimatedDrawableUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Bitmap.class, Rect.class })
public class AnimatedDrawableBackendImplTest {
    @Mock
    public AnimatedDrawableUtil mAnimatedDrawableUtil;

    @Mock
    public AnimatedImageResult mAnimatedImageResult;

    @Mock
    public Canvas mCanvas;

    @Mock
    public AnimatedImage mImage;

    @Mock
    public AnimatedImageFrame mFrame;

    @Mock
    public Bitmap mBitmap;

    @Mock
    public Rect mRect;

    @Test
    public void testSimple() {
        testBasic(128, 128, 512, 512, 128, 128);
    }

    @Test
    public void testNoUpscaling() {
        testBasic(128, 128, 16, 16, 16, 16);
    }

    @Test
    public void testNarrow() {
        testBasic(64, 128, 256, 256, 64, 64);
    }

    @Test
    public void testOffsets() {
        final int frameSide = 1024;
        final int canvasSide = 256;
        final int scale = frameSide / canvasSide;
        final int frameOffset = 512;
        Mockito.when(mFrame.getXOffset()).thenReturn(frameOffset);
        Mockito.when(mFrame.getYOffset()).thenReturn(frameOffset);
        testBasic(canvasSide, canvasSide, frameSide, frameSide, (frameSide / scale), (frameSide / scale));
        Mockito.verify(mCanvas).translate((frameOffset / scale), (frameOffset / scale));
    }
}

