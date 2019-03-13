/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.drawee.gestures;


import GestureDetector.ClickListener;
import android.view.ViewConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link GestureDetector}
 */
@RunWith(RobolectricTestRunner.class)
public class GestureDetectorTest {
    private ClickListener mClickListener;

    private ViewConfiguration mViewConfiguration;

    private long mScaledTouchSlop;

    private long mLongPressTimeout;

    private GestureDetector mGestureDetector;

    @Test
    public void testInitialstate() {
        Assert.assertEquals(mScaledTouchSlop, mGestureDetector.mSingleTapSlopPx, 0.0F);
        Assert.assertEquals(false, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(false, mGestureDetector.mIsClickCandidate);
    }

    @Test
    public void testSetClickListener() {
        GestureDetector.ClickListener clickListener = Mockito.mock(ClickListener.class);
        mGestureDetector.setClickListener(clickListener);
        Assert.assertSame(clickListener, mGestureDetector.mClickListener);
        mGestureDetector.setClickListener(null);
        Assert.assertSame(null, mGestureDetector.mClickListener);
    }

    @Test
    public void testOnClick_NoListener() {
        MotionEvent event1 = obtain(1000, 1000, ACTION_DOWN, 100.0F, 100.0F, 0);
        MotionEvent event2 = obtain(1000, 1001, ACTION_UP, 100.0F, 100.0F, 0);
        mGestureDetector.setClickListener(mClickListener);
        mGestureDetector.onTouchEvent(event1);
        mGestureDetector.onTouchEvent(event2);
        Mockito.verify(mClickListener).onClick();
        mGestureDetector.setClickListener(null);
        mGestureDetector.onTouchEvent(event1);
        mGestureDetector.onTouchEvent(event2);
        Mockito.verifyNoMoreInteractions(mClickListener);
        event1.recycle();
        event2.recycle();
    }

    @Test
    public void testOnClick_Valid() {
        float s = mScaledTouchSlop;
        long T0 = 1000;
        long T1 = T0;
        MotionEvent event1 = obtain(T0, T1, ACTION_DOWN, 100.0F, 100.0F, 0);
        mGestureDetector.onTouchEvent(event1);
        Assert.assertEquals(true, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(true, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        long T2 = T0 + (((mLongPressTimeout) * 1) / 3);
        MotionEvent event2 = obtain(T0, T2, ACTION_MOVE, (100.0F + (s * 0.3F)), (100.0F - (s * 0.3F)), 0);
        mGestureDetector.onTouchEvent(event2);
        Assert.assertEquals(true, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(true, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        long T3 = T0 + (((mLongPressTimeout) * 2) / 3);
        MotionEvent event3 = obtain(T0, T3, ACTION_MOVE, (100.0F + (s * 0.6F)), (100.0F - (s * 0.6F)), 0);
        mGestureDetector.onTouchEvent(event3);
        Assert.assertEquals(true, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(true, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        long T4 = T0 + (mLongPressTimeout);
        MotionEvent event4 = obtain(T0, T4, ACTION_UP, (100.0F + s), (100.0F - s), 0);
        mGestureDetector.onTouchEvent(event4);
        Assert.assertEquals(false, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(false, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        Mockito.verify(mClickListener).onClick();
        event1.recycle();
        event2.recycle();
        event3.recycle();
        event4.recycle();
    }

    @Test
    public void testOnClick_ToFar() {
        float s = mScaledTouchSlop;
        long T0 = 1000;
        long T1 = T0;
        MotionEvent event1 = obtain(T0, T1, ACTION_DOWN, 100.0F, 100.0F, 0);
        mGestureDetector.onTouchEvent(event1);
        Assert.assertEquals(true, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(true, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        long T2 = T0 + (((mLongPressTimeout) * 1) / 3);
        MotionEvent event2 = obtain(T0, T2, ACTION_MOVE, (100.0F + (s * 0.5F)), (100.0F - (s * 0.5F)), 0);
        mGestureDetector.onTouchEvent(event2);
        Assert.assertEquals(true, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(true, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        // maximum allowed distance is `s` px, but here we went `s * 1.1` px away from down point
        long T3 = T0 + (((mLongPressTimeout) * 2) / 3);
        MotionEvent event3 = obtain(T0, T3, ACTION_MOVE, (100.0F + (s * 1.1F)), (100.0F - (s * 0.5F)), 0);
        mGestureDetector.onTouchEvent(event3);
        Assert.assertEquals(true, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(false, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        long T4 = T0 + (mLongPressTimeout);
        MotionEvent event4 = obtain(T0, T4, ACTION_UP, (100.0F + s), (100.0F - s), 0);
        mGestureDetector.onTouchEvent(event4);
        Assert.assertEquals(false, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(false, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        Mockito.verifyNoMoreInteractions(mClickListener);
        event1.recycle();
        event2.recycle();
        event3.recycle();
        event4.recycle();
    }

    @Test
    public void testOnClick_ToLong() {
        float s = mScaledTouchSlop;
        long T0 = 1000;
        long T1 = T0;
        MotionEvent event1 = obtain(T0, T1, ACTION_DOWN, 100.0F, 100.0F, 0);
        mGestureDetector.onTouchEvent(event1);
        Assert.assertEquals(true, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(true, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        long T2 = T0 + (((mLongPressTimeout) * 1) / 3);
        MotionEvent event2 = obtain(T0, T2, ACTION_MOVE, (100.0F + (s * 0.3F)), (100.0F - (s * 0.3F)), 0);
        mGestureDetector.onTouchEvent(event2);
        Assert.assertEquals(true, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(true, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        long T3 = T0 + (((mLongPressTimeout) * 2) / 3);
        MotionEvent event3 = obtain(T0, T3, ACTION_MOVE, (100.0F + (s * 0.6F)), (100.0F - (s * 0.6F)), 0);
        mGestureDetector.onTouchEvent(event3);
        Assert.assertEquals(true, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(true, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        // maximum allowed duration is mLongPressTimeout ms, but here we released 1 ms after that
        long T4 = (T0 + (mLongPressTimeout)) + 1;
        MotionEvent event4 = obtain(T0, T4, ACTION_UP, (100.0F + s), (100.0F - s), 0);
        mGestureDetector.onTouchEvent(event4);
        Assert.assertEquals(false, mGestureDetector.mIsCapturingGesture);
        Assert.assertEquals(false, mGestureDetector.mIsClickCandidate);
        Assert.assertEquals(event1.getEventTime(), mGestureDetector.mActionDownTime);
        Assert.assertEquals(event1.getX(), mGestureDetector.mActionDownX, 0.0F);
        Assert.assertEquals(event1.getY(), mGestureDetector.mActionDownY, 0.0F);
        Mockito.verifyNoMoreInteractions(mClickListener);
        event1.recycle();
        event2.recycle();
        event3.recycle();
        event4.recycle();
    }
}

