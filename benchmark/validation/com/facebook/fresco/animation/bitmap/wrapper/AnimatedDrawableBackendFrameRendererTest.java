/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.fresco.animation.bitmap.wrapper;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import com.facebook.fresco.animation.bitmap.BitmapFrameCache;
import com.facebook.imagepipeline.animated.base.AnimatedDrawableBackend;
import com.facebook.imagepipeline.animated.base.AnimatedDrawableFrameInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link AnimatedDrawableBackendFrameRenderer}
 */
@RunWith(RobolectricTestRunner.class)
public class AnimatedDrawableBackendFrameRendererTest {
    private AnimatedDrawableBackendFrameRenderer mAnimatedDrawableBackendFrameRenderer;

    private AnimatedDrawableBackend mAnimatedDrawableBackend;

    private BitmapFrameCache mBitmapFrameCache;

    @Test
    public void testSetBounds() {
        Mockito.when(mAnimatedDrawableBackend.forNewBounds(ArgumentMatchers.any(Rect.class))).thenReturn(mAnimatedDrawableBackend);
        Rect bounds = Mockito.mock(Rect.class);
        mAnimatedDrawableBackendFrameRenderer.setBounds(bounds);
        Mockito.verify(mAnimatedDrawableBackend).forNewBounds(bounds);
    }

    @Test
    public void testGetIntrinsicWidth() {
        Mockito.when(mAnimatedDrawableBackend.getWidth()).thenReturn(123);
        assertThat(mAnimatedDrawableBackendFrameRenderer.getIntrinsicWidth()).isEqualTo(123);
        assertThat(mAnimatedDrawableBackendFrameRenderer.getIntrinsicHeight()).isNotEqualTo(123);
    }

    @Test
    public void testGetIntrinsicHeight() {
        Mockito.when(mAnimatedDrawableBackend.getHeight()).thenReturn(1200);
        assertThat(mAnimatedDrawableBackendFrameRenderer.getIntrinsicHeight()).isEqualTo(1200);
        assertThat(mAnimatedDrawableBackendFrameRenderer.getIntrinsicWidth()).isNotEqualTo(1200);
    }

    @Test
    public void testRenderFrame() {
        Mockito.when(mAnimatedDrawableBackend.getHeight()).thenReturn(1200);
        Bitmap bitmap = Mockito.mock(Bitmap.class);
        AnimatedDrawableFrameInfo animatedDrawableFrameInfo = Mockito.mock(AnimatedDrawableFrameInfo.class);
        Mockito.when(mAnimatedDrawableBackend.getFrameInfo(ArgumentMatchers.anyInt())).thenReturn(animatedDrawableFrameInfo);
        boolean rendered = mAnimatedDrawableBackendFrameRenderer.renderFrame(0, bitmap);
        assertThat(rendered).isTrue();
    }

    @Test
    public void testRenderFrameUnsuccessful() {
        int frameNumber = 0;
        Mockito.when(mAnimatedDrawableBackend.getHeight()).thenReturn(1200);
        Bitmap bitmap = Mockito.mock(Bitmap.class);
        AnimatedDrawableFrameInfo animatedDrawableFrameInfo = Mockito.mock(AnimatedDrawableFrameInfo.class);
        Mockito.when(mAnimatedDrawableBackend.getFrameInfo(ArgumentMatchers.anyInt())).thenReturn(animatedDrawableFrameInfo);
        Mockito.doThrow(new IllegalStateException()).when(mAnimatedDrawableBackend).renderFrame(ArgumentMatchers.eq(frameNumber), ArgumentMatchers.any(Canvas.class));
        boolean rendered = mAnimatedDrawableBackendFrameRenderer.renderFrame(frameNumber, bitmap);
        assertThat(rendered).isFalse();
    }
}

