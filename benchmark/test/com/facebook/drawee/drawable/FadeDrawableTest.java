/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.drawable;


import Drawable.Callback;
import FadeDrawable.TRANSITION_NONE;
import FadeDrawable.TRANSITION_RUNNING;
import FadeDrawable.TRANSITION_STARTING;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link FadeDrawable} with the default configuration.
 *
 * @see FadeDrawableAllOnTest for more tests
 */
@RunWith(RobolectricTestRunner.class)
public class FadeDrawableTest {
    private Drawable[] mLayers = new Drawable[]{ DrawableTestUtils.mockDrawable(), DrawableTestUtils.mockDrawable(), DrawableTestUtils.mockDrawable() };

    private FadeDrawableTest.FakeFadeDrawable mFadeDrawable;

    private Canvas mCanvas = Mockito.mock(Canvas.class);

    private Callback mCallback = Mockito.mock(Callback.class);

    @Test
    public void testIntrinsicDimensions() {
        Mockito.when(getIntrinsicWidth()).thenReturn(100);
        Mockito.when(getIntrinsicWidth()).thenReturn(200);
        Mockito.when(getIntrinsicWidth()).thenReturn(150);
        Mockito.when(getIntrinsicHeight()).thenReturn(400);
        Mockito.when(getIntrinsicHeight()).thenReturn(350);
        Mockito.when(getIntrinsicHeight()).thenReturn(300);
        Assert.assertEquals(200, getIntrinsicWidth());
        Assert.assertEquals(400, getIntrinsicHeight());
    }

    @Test
    public void testInitialState() {
        // initially only the fist layer is displayed and there is no transition
        Assert.assertEquals(TRANSITION_NONE, mFadeDrawable.mTransitionState);
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[0]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[1]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[2]);
    }

    @Test
    public void testFadeToLayer() {
        // start fade
        setTransitionDuration(100);
        fadeToLayer(1);
        Assert.assertEquals(100, mFadeDrawable.mDurationMs);
        Assert.assertEquals(TRANSITION_STARTING, mFadeDrawable.mTransitionState);
        Mockito.verify(mCallback).invalidateDrawable(mFadeDrawable);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[0]);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[1]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[2]);
        // alphas will change only when the next draw happens
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
    }

    @Test
    public void testFadeUpToLayer() {
        // start fade
        setTransitionDuration(100);
        fadeUpToLayer(1);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[0]);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[1]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[2]);
        Assert.assertEquals(100, mFadeDrawable.mDurationMs);
        Assert.assertEquals(TRANSITION_STARTING, mFadeDrawable.mTransitionState);
        Mockito.verify(mCallback).invalidateDrawable(mFadeDrawable);
        // alphas will change only when the next draw happens
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
    }

    @Test
    public void testFadeInLayer() {
        // start fade in
        setTransitionDuration(100);
        fadeInLayer(2);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[0]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[1]);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[2]);
        Assert.assertEquals(100, mFadeDrawable.mDurationMs);
        Assert.assertEquals(TRANSITION_STARTING, mFadeDrawable.mTransitionState);
        Mockito.verify(mCallback).invalidateDrawable(mFadeDrawable);
        // alphas will change only when the next draw happens
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
    }

    @Test
    public void testFadeOutLayer() {
        // start fade out
        setTransitionDuration(100);
        fadeOutLayer(0);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[0]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[1]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[2]);
        Assert.assertEquals(100, mFadeDrawable.mDurationMs);
        Assert.assertEquals(TRANSITION_STARTING, mFadeDrawable.mTransitionState);
        Mockito.verify(mCallback).invalidateDrawable(mFadeDrawable);
        // alphas will change only when the next draw happens
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
    }

    @Test
    public void testFadeOutAllLayers() {
        // start fade out
        setTransitionDuration(100);
        mFadeDrawable.mIsLayerOn[1] = true;
        mFadeDrawable.mIsLayerOn[2] = true;
        fadeOutAllLayers();
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[0]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[1]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[2]);
        Assert.assertEquals(100, mFadeDrawable.mDurationMs);
        Assert.assertEquals(TRANSITION_STARTING, mFadeDrawable.mTransitionState);
        Mockito.verify(mCallback).invalidateDrawable(mFadeDrawable);
        // alphas will change only when the next draw happens
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
    }

    @Test
    public void testImmediateTransition() {
        testImmediateTransition(true);
        testImmediateTransition(false);
    }

    @Test
    public void testZeroTransition() {
        testZeroTransition(true);
        testZeroTransition(false);
    }

    @Test
    public void testTransition() {
        testTransition(true);
        testTransition(false);
    }

    @Test
    public void testSetAlpha() {
        InOrder inOrder = Mockito.inOrder(mLayers[0], mLayers[1], mLayers[2], mCallback);
        // reset drawable
        reset();
        inOrder.verify(mCallback).invalidateDrawable(mFadeDrawable);
        // start animation
        setTransitionDuration(85);
        fadeUpToLayer(1);
        inOrder.verify(mCallback).invalidateDrawable(mFadeDrawable);
        // first frame
        mFadeDrawable.draw(mCanvas);
        inOrder.verify(mCallback, Mockito.atLeastOnce()).invalidateDrawable(mFadeDrawable);
        // setAlpha
        setAlpha(128);
        Assert.assertEquals(128, getAlpha());
        inOrder.verify(mCallback).invalidateDrawable(mFadeDrawable);
        // next frame
        mFadeDrawable.incrementCurrentTimeMs(17);
        mFadeDrawable.draw(mCanvas);
        Assert.assertEquals(128, getAlpha());
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(51, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
        Assert.assertEquals(TRANSITION_RUNNING, mFadeDrawable.mTransitionState);
        inOrder.verify(mLayers[0]).mutate();
        setAlpha(128);
        inOrder.verify(mLayers[0]).draw(mCanvas);
        inOrder.verify(mLayers[1]).mutate();
        setAlpha(25);
        inOrder.verify(mLayers[1]).draw(mCanvas);
        inOrder.verify(mCallback, Mockito.atLeastOnce()).invalidateDrawable(mFadeDrawable);
        inOrder.verifyNoMoreInteractions();
        // make sure the fade has finished, and verify that after that we don't invalidate
        mFadeDrawable.incrementCurrentTimeMs(1000);
        mFadeDrawable.draw(mCanvas);
        inOrder.verify(mCallback, Mockito.never()).invalidateDrawable(mFadeDrawable);
    }

    @Test
    public void testReset() {
        // go to some non-initial state
        fadeToLayer(2);
        finishTransitionImmediately();
        resetInteractions();
        reset();
        Assert.assertEquals(TRANSITION_NONE, mFadeDrawable.mTransitionState);
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[0]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[1]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[2]);
        Mockito.verify(mCallback).invalidateDrawable(mFadeDrawable);
    }

    @Test
    public void testBatchMode() {
        beginBatchMode();
        reset();
        fadeInLayer(1);
        fadeOutLayer(0);
        fadeOutAllLayers();
        fadeToLayer(2);
        fadeUpToLayer(1);
        finishTransitionImmediately();
        endBatchMode();
        Mockito.verify(mCallback, Mockito.times(1)).invalidateDrawable(mFadeDrawable);
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(255, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[0]);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[1]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[2]);
    }

    @Test
    public void testNoBatchMode() {
        reset();
        fadeInLayer(1);
        fadeOutLayer(0);
        fadeOutAllLayers();
        fadeToLayer(2);
        fadeUpToLayer(1);
        finishTransitionImmediately();
        Mockito.verify(mCallback, Mockito.times(7)).invalidateDrawable(mFadeDrawable);
        Assert.assertEquals(255, mFadeDrawable.mAlphas[0]);
        Assert.assertEquals(255, mFadeDrawable.mAlphas[1]);
        Assert.assertEquals(0, mFadeDrawable.mAlphas[2]);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[0]);
        Assert.assertEquals(true, mFadeDrawable.mIsLayerOn[1]);
        Assert.assertEquals(false, mFadeDrawable.mIsLayerOn[2]);
    }

    private static class FakeFadeDrawable extends FadeDrawable {
        private long mCurrentTimeMs;

        public FakeFadeDrawable(Drawable[] layers) {
            super(layers);
            mCurrentTimeMs = 0;
        }

        @Override
        protected long getCurrentTimeMs() {
            return mCurrentTimeMs;
        }

        void incrementCurrentTimeMs(long increment) {
            mCurrentTimeMs += increment;
        }
    }
}

