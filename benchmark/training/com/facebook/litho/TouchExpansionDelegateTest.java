/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import MotionEvent.ACTION_DOWN;
import RuntimeEnvironment.application;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(ComponentsTestRunner.class)
public class TouchExpansionDelegateTest {
    private TouchExpansionDelegate mTouchDelegate;

    @Test
    public void testEmptyOnTouchEvent() {
        mTouchDelegate.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), ACTION_DOWN, 0, 0, 0));
    }

    @Test
    public void testTouchWithinBounds() {
        final View view = Mockito.mock(View.class);
        Mockito.when(view.getContext()).thenReturn(application);
        Mockito.when(view.getWidth()).thenReturn(4);
        Mockito.when(view.getHeight()).thenReturn(6);
        mTouchDelegate.registerTouchExpansion(0, view, new Rect(0, 0, 10, 10));
        MotionEvent event = obtain(uptimeMillis(), uptimeMillis(), ACTION_DOWN, 5, 5, 0);
        mTouchDelegate.onTouchEvent(event);
        Mockito.verify(view, Mockito.times(1)).dispatchTouchEvent(event);
        assertThat(event.getX()).isEqualTo(2.0F);
        assertThat(event.getY()).isEqualTo(3.0F);
    }

    @Test
    public void testTouchOutsideBounds() {
        final View view = Mockito.mock(View.class);
        Mockito.when(view.getContext()).thenReturn(application);
        mTouchDelegate.registerTouchExpansion(0, view, new Rect(0, 0, 10, 10));
        MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), ACTION_DOWN, 100, 100, 0);
        mTouchDelegate.onTouchEvent(event);
        Mockito.verify(view, Mockito.never()).dispatchTouchEvent(event);
    }

    @Test
    public void testUnregister() {
        final View view = Mockito.mock(View.class);
        Mockito.when(view.getContext()).thenReturn(application);
        mTouchDelegate.registerTouchExpansion(0, view, new Rect(0, 0, 10, 10));
        mTouchDelegate.unregisterTouchExpansion(0);
        MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), ACTION_DOWN, 5, 5, 0);
        mTouchDelegate.onTouchEvent(event);
        Mockito.verify(view, Mockito.never()).dispatchTouchEvent(event);
    }

    @Test
    public void testMove() {
        final View firstView = Mockito.mock(View.class);
        final View secondView = Mockito.mock(View.class);
        Mockito.when(firstView.getContext()).thenReturn(application);
        Mockito.when(secondView.getContext()).thenReturn(application);
        mTouchDelegate.registerTouchExpansion(0, firstView, new Rect(0, 0, 10, 10));
        mTouchDelegate.registerTouchExpansion(4, secondView, new Rect(0, 0, 10, 10));
        mTouchDelegate.moveTouchExpansionIndexes(0, 2);
        mTouchDelegate.unregisterTouchExpansion(2);
    }

    @Test
    public void testComplexMove() {
        final View firstView = Mockito.mock(View.class);
        final View secondView = Mockito.mock(View.class);
        Mockito.when(firstView.getContext()).thenReturn(application);
        Mockito.when(secondView.getContext()).thenReturn(application);
        mTouchDelegate.registerTouchExpansion(0, firstView, new Rect(0, 0, 10, 10));
        mTouchDelegate.registerTouchExpansion(4, secondView, new Rect(0, 0, 10, 10));
        mTouchDelegate.moveTouchExpansionIndexes(0, 4);
        mTouchDelegate.unregisterTouchExpansion(4);
        mTouchDelegate.unregisterTouchExpansion(4);
    }

    @Test
    public void testDrawingOrder() {
        final View view1 = Mockito.mock(View.class);
        Mockito.when(view1.getContext()).thenReturn(application);
        Mockito.when(view1.dispatchTouchEvent(ArgumentMatchers.any(MotionEvent.class))).thenReturn(true);
        mTouchDelegate.registerTouchExpansion(0, view1, new Rect(0, 0, 10, 10));
        final View view2 = Mockito.mock(View.class);
        Mockito.when(view2.getContext()).thenReturn(application);
        Mockito.when(view2.dispatchTouchEvent(ArgumentMatchers.any(MotionEvent.class))).thenReturn(true);
        mTouchDelegate.registerTouchExpansion(1, view2, new Rect(0, 0, 10, 10));
        MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), ACTION_DOWN, 5, 5, 0);
        mTouchDelegate.onTouchEvent(event);
        Mockito.verify(view1, Mockito.never()).dispatchTouchEvent(event);
        Mockito.verify(view2, Mockito.times(1)).dispatchTouchEvent(event);
    }
}

