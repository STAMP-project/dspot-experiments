/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.fresco.animation.backend;


import AnimationBackendDelegateWithInactivityCheck.InactivityListener;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.facebook.imagepipeline.testing.FakeClock;
import com.facebook.imagepipeline.testing.TestScheduledExecutorService;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests {@link AnimationBackendDelegateWithInactivityCheck}
 */
public class AnimationBackendDelegateWithInactivityCheckTest {
    private AnimationBackendDelegate<AnimationBackend> mAnimationBackendDelegateWithInactivityCheck;

    private AnimationBackend mAnimationBackend;

    private InactivityListener mInactivityListener;

    private Drawable mParent;

    private Canvas mCanvas;

    private FakeClock mFakeClock;

    private TestScheduledExecutorService mTestScheduledExecutorService;

    @Test
    public void testNotifyInactive() {
        Mockito.verifyZeroInteractions(mInactivityListener);
        mAnimationBackendDelegateWithInactivityCheck.drawFrame(mParent, mCanvas, 0);
        Mockito.verifyZeroInteractions(mInactivityListener);
        mFakeClock.incrementBy(100);
        Mockito.verifyZeroInteractions(mInactivityListener);
        mFakeClock.incrementBy(AnimationBackendDelegateWithInactivityCheck.INACTIVITY_THRESHOLD_MS);
        Mockito.verify(mInactivityListener).onInactive();
    }
}

