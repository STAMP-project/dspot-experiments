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
package com.facebook.litho.utils;


import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.litho.LithoView;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.utils.IncrementalMountUtils.WrapperView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests {@link IncrementalMountUtils}
 */
@RunWith(ComponentsTestRunner.class)
public class IncrementalMountUtilsTest {
    private static final int SCROLLING_VIEW_WIDTH = 100;

    private static final int SCROLLING_VIEW_HEIGHT = 1000;

    public IncrementalMountUtilsTest.TestWrapperView mWrapperView = Mockito.mock(IncrementalMountUtilsTest.TestWrapperView.class);

    public LithoView mLithoView = Mockito.mock(LithoView.class);

    public ViewGroup mViewGroup = Mockito.mock(ViewGroup.class);

    private final Rect mMountedRect = new Rect();

    @Test
    public void testIncrementalMountForLithoViewVisibleAtTop() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, (-10), IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(0, 10, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 20));
    }

    @Test
    public void testIncrementalMountForLithoViewVisibleAtTopWithTranslationYPartialIn() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, (-10), IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10);
        IncrementalMountUtilsTest.setupViewTranslations(mLithoView, 0, 5);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(0, 5, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 20));
    }

    @Test
    public void testIncrementalMountForLithoViewVisibleAtTopWithTranslationYFullyOut() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, (-10), IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10);
        IncrementalMountUtilsTest.setupViewTranslations(mLithoView, 0, (-15));
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        Mockito.verify(mLithoView, Mockito.never()).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(true));
    }

    @Test
    public void testIncrementalMountForLithoViewVisibleAtLeft() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, (-10), 0, 10, IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(10, 0, 20, IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT));
    }

    @Test
    public void testIncrementalMountForLithoViewVisibleAtLeftWithTranslationXFullyIn() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, (-10), 0, 10, IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT);
        IncrementalMountUtilsTest.setupViewTranslations(mLithoView, 15, 0);
        IncrementalMountUtilsTest.setupLithoViewPreviousBounds(mLithoView, 20, IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        Mockito.verify(mLithoView, Mockito.never()).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(true));
    }

    @Test
    public void testIncrementalMountForLithoViewVisibleAtLeftWithTranslationXPartialOut() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, (-10), 0, 10, IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT);
        IncrementalMountUtilsTest.setupViewTranslations(mLithoView, (-7), 0);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(17, 0, 20, IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT));
    }

    @Test
    public void testIncrementalMountForLithoViewVisibleAtBottom() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, ((IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT) - 5), IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, ((IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT) + 5));
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(0, 0, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 5));
    }

    @Test
    public void testIncrementalMountForLithoViewVisibleAtRight() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, ((IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH) - 5), 0, ((IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH) + 5), IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(0, 0, 5, IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT));
    }

    @Test
    public void testIncrementalMountForLithoViewNewlyFullyVisible() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, 10, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 20);
        IncrementalMountUtilsTest.setupLithoViewPreviousBounds(mLithoView, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 5);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(0, 0, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10));
    }

    @Test
    public void testIncrementalMountForLithoViewAlreadyFullyVisible() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, 10, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 20);
        IncrementalMountUtilsTest.setupLithoViewPreviousBounds(mLithoView, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        Mockito.verify(mLithoView, Mockito.never()).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(true));
    }

    @Test
    public void testNoIncrementalMountWhenNotEnabled() {
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, ((IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT) - 5), IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, ((IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT) + 5));
        Mockito.when(mLithoView.isIncrementalMountEnabled()).thenReturn(false);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        Mockito.verify(mLithoView, Mockito.never()).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(true));
    }

    @Test
    public void testIncrementalMountForWrappedViewAtTop() {
        Mockito.when(mViewGroup.getChildAt(0)).thenReturn(mWrapperView);
        IncrementalMountUtilsTest.setupViewBounds(mWrapperView, 0, (-10), IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10);
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, 0, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 20);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(0, 10, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 20));
    }

    @Test
    public void testIncrementalMountForWrappedViewAtBottom() {
        Mockito.when(mViewGroup.getChildAt(0)).thenReturn(mWrapperView);
        IncrementalMountUtilsTest.setupViewBounds(mWrapperView, 0, ((IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT) - 5), IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, ((IncrementalMountUtilsTest.SCROLLING_VIEW_HEIGHT) + 5));
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, 0, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(0, 0, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 5));
    }

    @Test
    public void testIncrementalMountForWrappedLithoViewNewlyFullyVisible() {
        Mockito.when(mViewGroup.getChildAt(0)).thenReturn(mWrapperView);
        IncrementalMountUtilsTest.setupViewBounds(mWrapperView, 0, 10, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 20);
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, 0, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10);
        IncrementalMountUtilsTest.setupLithoViewPreviousBounds(mLithoView, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 5);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        verifyPerformIncrementalMountCalled(new Rect(0, 0, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10));
    }

    @Test
    public void testIncrementalMountForWrappedLithoViewAlreadyFullyVisible() {
        Mockito.when(mViewGroup.getChildAt(0)).thenReturn(mWrapperView);
        IncrementalMountUtilsTest.setupViewBounds(mWrapperView, 0, 10, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 20);
        IncrementalMountUtilsTest.setupViewBounds(mLithoView, 0, 0, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10);
        IncrementalMountUtilsTest.setupLithoViewPreviousBounds(mLithoView, IncrementalMountUtilsTest.SCROLLING_VIEW_WIDTH, 10);
        IncrementalMountUtils.performIncrementalMount(mViewGroup);
        Mockito.verify(mLithoView, Mockito.never()).performIncrementalMount(ArgumentMatchers.any(Rect.class), ArgumentMatchers.eq(true));
    }

    public static class TestWrapperView extends View implements WrapperView {
        public TestWrapperView(Context context) {
            super(context);
        }

        @Override
        public View getWrappedView() {
            return null;
        }
    }
}

