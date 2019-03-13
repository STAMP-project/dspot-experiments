/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.fresco.animation.bitmap.wrapper;


import AnimationBackend.LOOP_COUNT_INFINITE;
import com.facebook.imagepipeline.animated.base.AnimatedDrawableBackend;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link AnimatedDrawableBackendAnimationInformation}.
 */
public class AnimatedDrawableBackendAnimationInformationTest {
    private AnimatedDrawableBackend mAnimatedDrawableBackend;

    private AnimatedDrawableBackendAnimationInformation mAnimatedDrawableBackendAnimationInformation;

    @Test
    public void testGetFrameCount() throws Exception {
        Mockito.when(mAnimatedDrawableBackend.getFrameCount()).thenReturn(123);
        assertThat(mAnimatedDrawableBackendAnimationInformation.getFrameCount()).isEqualTo(123);
    }

    @Test
    public void testGetFrameDurationMs() throws Exception {
        Mockito.when(mAnimatedDrawableBackend.getDurationMsForFrame(1)).thenReturn(123);
        Mockito.when(mAnimatedDrawableBackend.getDurationMsForFrame(2)).thenReturn(200);
        assertThat(mAnimatedDrawableBackendAnimationInformation.getFrameDurationMs(1)).isEqualTo(123);
        assertThat(mAnimatedDrawableBackendAnimationInformation.getFrameDurationMs(2)).isEqualTo(200);
    }

    @Test
    public void testGetLoopCount() throws Exception {
        Mockito.when(mAnimatedDrawableBackend.getLoopCount()).thenReturn(123);
        assertThat(mAnimatedDrawableBackendAnimationInformation.getLoopCount()).isEqualTo(123);
    }

    @Test
    public void testGetLoopCountInfinite() throws Exception {
        Mockito.when(mAnimatedDrawableBackend.getLoopCount()).thenReturn(LOOP_COUNT_INFINITE);
        assertThat(mAnimatedDrawableBackendAnimationInformation.getLoopCount()).isEqualTo(LOOP_COUNT_INFINITE);
    }
}

