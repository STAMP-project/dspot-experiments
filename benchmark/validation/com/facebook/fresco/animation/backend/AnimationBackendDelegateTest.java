/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.fresco.animation.backend;


import AnimationBackend.INTRINSIC_DIMENSION_UNSET;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link AnimationBackendDelegate}
 */
@RunWith(RobolectricTestRunner.class)
public class AnimationBackendDelegateTest {
    private AnimationBackendDelegate<AnimationBackend> mAnimationBackendDelegate;

    private AnimationBackend mAnimationBackend;

    private Drawable mParent;

    private Canvas mCanvas;

    @Test
    public void testForwardProperties() {
        ColorFilter colorFilter = Mockito.mock(ColorFilter.class);
        Rect bounds = Mockito.mock(Rect.class);
        int alphaValue = 123;
        Mockito.verifyZeroInteractions(mAnimationBackend);
        // Set values to be persisted
        mAnimationBackendDelegate.setAlpha(alphaValue);
        mAnimationBackendDelegate.setColorFilter(colorFilter);
        mAnimationBackendDelegate.setBounds(bounds);
        // Verify that values have been restored
        Mockito.verify(mAnimationBackend).setAlpha(alphaValue);
        Mockito.verify(mAnimationBackend).setColorFilter(colorFilter);
        Mockito.verify(mAnimationBackend).setBounds(bounds);
    }

    @Test
    public void testGetProperties() {
        int width = 123;
        int height = 234;
        int sizeInBytes = 2000;
        int frameCount = 20;
        int loopCount = 1000;
        int frameDurationMs = 200;
        Mockito.when(mAnimationBackend.getIntrinsicWidth()).thenReturn(width);
        Mockito.when(mAnimationBackend.getIntrinsicHeight()).thenReturn(height);
        Mockito.when(mAnimationBackend.getSizeInBytes()).thenReturn(sizeInBytes);
        Mockito.when(mAnimationBackend.getFrameCount()).thenReturn(frameCount);
        Mockito.when(mAnimationBackend.getLoopCount()).thenReturn(loopCount);
        Mockito.when(mAnimationBackend.getFrameDurationMs(ArgumentMatchers.anyInt())).thenReturn(frameDurationMs);
        assertThat(mAnimationBackendDelegate.getIntrinsicWidth()).isEqualTo(width);
        assertThat(mAnimationBackendDelegate.getIntrinsicHeight()).isEqualTo(height);
        assertThat(mAnimationBackendDelegate.getSizeInBytes()).isEqualTo(sizeInBytes);
        assertThat(mAnimationBackendDelegate.getFrameCount()).isEqualTo(frameCount);
        assertThat(mAnimationBackendDelegate.getLoopCount()).isEqualTo(loopCount);
        assertThat(mAnimationBackendDelegate.getFrameDurationMs(1)).isEqualTo(frameDurationMs);
    }

    @Test
    public void testGetDefaultProperties() {
        // We don't set an animation backend
        mAnimationBackendDelegate.setAnimationBackend(null);
        assertThat(mAnimationBackendDelegate.getIntrinsicWidth()).isEqualTo(INTRINSIC_DIMENSION_UNSET);
        assertThat(mAnimationBackendDelegate.getIntrinsicHeight()).isEqualTo(INTRINSIC_DIMENSION_UNSET);
        assertThat(mAnimationBackendDelegate.getSizeInBytes()).isEqualTo(0);
        assertThat(mAnimationBackendDelegate.getFrameCount()).isEqualTo(0);
        assertThat(mAnimationBackendDelegate.getLoopCount()).isEqualTo(0);
        assertThat(mAnimationBackendDelegate.getFrameDurationMs(1)).isEqualTo(0);
    }

    @Test
    public void testSetAnimationBackend() {
        AnimationBackend backend2 = Mockito.mock(AnimationBackend.class);
        ColorFilter colorFilter = Mockito.mock(ColorFilter.class);
        Rect bounds = Mockito.mock(Rect.class);
        int alphaValue = 123;
        Mockito.verifyZeroInteractions(backend2);
        // Set values to be persisted
        mAnimationBackendDelegate.setAlpha(alphaValue);
        mAnimationBackendDelegate.setColorFilter(colorFilter);
        mAnimationBackendDelegate.setBounds(bounds);
        mAnimationBackendDelegate.setAnimationBackend(backend2);
        // Verify that values have been restored
        Mockito.verify(backend2).setAlpha(alphaValue);
        Mockito.verify(backend2).setColorFilter(colorFilter);
        Mockito.verify(backend2).setBounds(bounds);
    }

    @Test
    public void testDrawFrame() {
        mAnimationBackendDelegate.drawFrame(mParent, mCanvas, 1);
        Mockito.verify(mAnimationBackend).drawFrame(mParent, mCanvas, 1);
    }

    @Test
    public void testClear() {
        mAnimationBackendDelegate.clear();
        Mockito.verify(mAnimationBackend).clear();
    }
}

