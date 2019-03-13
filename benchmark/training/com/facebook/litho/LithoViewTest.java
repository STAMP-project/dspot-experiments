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


import SizeSpec.AT_MOST;
import android.view.ViewGroup;
import com.facebook.litho.testing.TestDrawableComponent;
import com.facebook.litho.testing.assertj.LithoViewAssert;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowView;

import static MeasureSpec.makeMeasureSpec;


@RunWith(ComponentsTestRunner.class)
public class LithoViewTest {
    private LithoView mLithoView;

    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    @Test
    public void measureBeforeBeingAttached() {
        mLithoView.measure(makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mLithoView.layout(0, 0, mLithoView.getMeasuredWidth(), mLithoView.getMeasuredHeight());
        // View got measured.
        assertThat(mLithoView.getMeasuredWidth()).isGreaterThan(0);
        assertThat(mLithoView.getMeasuredHeight()).isGreaterThan(0);
        // Attaching will automatically mount since we already have a layout fitting our size.
        ShadowView shadow = shadowOf(mLithoView);
        shadow.callOnAttachedToWindow();
        assertThat(LithoViewTest.getInternalMountItems(mLithoView)).hasSize(2);
    }

    @Test
    public void testNullLithoViewDimensions() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return null;
            }
        };
        LithoView nullLithoView = new LithoView(application);
        nullLithoView.setComponent(component);
        nullLithoView.measure(makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        nullLithoView.layout(0, 0, nullLithoView.getMeasuredWidth(), nullLithoView.getMeasuredHeight());
        LithoViewAssert.assertThat(nullLithoView).hasMeasuredWidthOf(0).hasMeasuredHeightOf(0);
    }

    @Test
    public void testSuppressMeasureComponentTree() {
        final ComponentTree mockComponentTree = Mockito.mock(ComponentTree.class);
        final int width = 240;
        final int height = 400;
        mLithoView.setComponentTree(mockComponentTree);
        mLithoView.suppressMeasureComponentTree(true);
        mLithoView.measure(makeMeasureSpec(width, MeasureSpec.EXACTLY), makeMeasureSpec(height, MeasureSpec.EXACTLY));
        Mockito.verify(mockComponentTree, Mockito.never()).measure(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(int[].class), ArgumentMatchers.anyBoolean());
        LithoViewAssert.assertThat(mLithoView).hasMeasuredWidthOf(width).hasMeasuredHeightOf(height);
    }

    @Test
    public void testDontThrowWhenLayoutStateIsNull() {
        final ComponentTree mockComponentTree = Mockito.mock(ComponentTree.class);
        mLithoView.setComponentTree(mockComponentTree);
        mLithoView.requestLayout();
        mLithoView.performIncrementalMount();
    }

    /**
     * This verifies that the width is 0 with normal layout params.
     */
    @Test
    public void measureWithLayoutParams() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return TestDrawableComponent.create(c).widthPercent(100).heightPx(100).build();
            }
        };
        mLithoView = new LithoView(RuntimeEnvironment.application);
        mLithoView.setComponent(component);
        mLithoView.setLayoutParams(new ViewGroup.LayoutParams(0, 200));
        mLithoView.measure(makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), makeMeasureSpec(200, MeasureSpec.EXACTLY));
        mLithoView.layout(0, 0, mLithoView.getMeasuredWidth(), mLithoView.getMeasuredHeight());
        // View got measured.
        assertThat(mLithoView.getMeasuredWidth()).isEqualTo(0);
        assertThat(mLithoView.getMeasuredHeight()).isEqualTo(200);
        // Attaching will not mount anything as we have no width.
        ShadowView shadow = shadowOf(mLithoView);
        shadow.callOnAttachedToWindow();
        assertThat(LithoViewTest.getInternalMountItems(mLithoView)).isNull();
    }

    /**
     * This verifies that the width is correct with at most layout params.
     */
    @Test
    public void measureWithAtMostLayoutParams() {
        final Component component = new InlineLayoutSpec() {
            @Override
            protected Component onCreateLayout(ComponentContext c) {
                return TestDrawableComponent.create(c).widthPercent(50).heightPercent(10).build();
            }
        };
        mLithoView = new LithoView(RuntimeEnvironment.application);
        mLithoView.setComponent(component);
        mLithoView.setLayoutParams(new LithoViewTest.RecyclerViewLayoutManagerOverrideParams(SizeSpec.makeSizeSpec(100, AT_MOST), SizeSpec.makeSizeSpec(200, AT_MOST)));
        mLithoView.measure(makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        mLithoView.layout(0, 0, mLithoView.getMeasuredWidth(), mLithoView.getMeasuredHeight());
        // View got measured.
        assertThat(mLithoView.getMeasuredWidth()).isEqualTo(50);
        assertThat(mLithoView.getMeasuredHeight()).isEqualTo(20);
        // Attaching will automatically mount since we already have a layout fitting our size.
        ShadowView shadow = shadowOf(mLithoView);
        shadow.callOnAttachedToWindow();
        assertThat(LithoViewTest.getInternalMountItems(mLithoView)).hasSize(2);
    }

    @Test
    public void testCorrectsDoubleMeasureBug() {
        mLithoView = setupLithoViewForDoubleMeasureTest(411, 2.625F, 1080);
        mLithoView.measure(makeMeasureSpec(1079, MeasureSpec.EXACTLY), makeMeasureSpec(100, MeasureSpec.EXACTLY));
        assertThat(mLithoView.getMeasuredWidth()).isEqualTo(1080);
        assertThat(mLithoView.getMeasuredHeight()).isEqualTo(100);
    }

    @Test
    public void testCorrectsDoubleMeasureBugWithAtMost() {
        mLithoView = setupLithoViewForDoubleMeasureTest(411, 2.625F, 1080);
        mLithoView.measure(makeMeasureSpec(1079, MeasureSpec.AT_MOST), makeMeasureSpec(100, MeasureSpec.EXACTLY));
        assertThat(mLithoView.getMeasuredWidth()).isEqualTo(1080);
        assertThat(mLithoView.getMeasuredHeight()).isEqualTo(100);
    }

    @Test
    public void testNoCorrectionWhenBugIsNotMatched() {
        mLithoView = setupLithoViewForDoubleMeasureTest(411, 2.0F, 1080);
        mLithoView.measure(makeMeasureSpec(1079, MeasureSpec.EXACTLY), makeMeasureSpec(100, MeasureSpec.EXACTLY));
        assertThat(mLithoView.getMeasuredWidth()).isEqualTo(1079);
        assertThat(mLithoView.getMeasuredHeight()).isEqualTo(100);
    }

    @Test
    public void testNoCorrectionWhenBugIsNotMatched2() {
        mLithoView = setupLithoViewForDoubleMeasureTest(411, 2.625F, 1080);
        mLithoView.measure(makeMeasureSpec(1078, MeasureSpec.EXACTLY), makeMeasureSpec(100, MeasureSpec.EXACTLY));
        assertThat(mLithoView.getMeasuredWidth()).isEqualTo(1078);
        assertThat(mLithoView.getMeasuredHeight()).isEqualTo(100);
    }

    @Test
    public void testMeasureDoesNotComputeLayoutStateWhenSpecsAreExact() {
        mLithoView = new LithoView(RuntimeEnvironment.application);
        mLithoView.setComponent(TestDrawableComponent.create(mLithoView.getComponentContext()).build());
        mLithoView.measure(makeMeasureSpec(100, MeasureSpec.EXACTLY), makeMeasureSpec(100, MeasureSpec.EXACTLY));
        assertThat(mLithoView.getMeasuredWidth()).isEqualTo(100);
        assertThat(mLithoView.getMeasuredHeight()).isEqualTo(100);
        assertThat(mLithoView.getComponentTree().getMainThreadLayoutState()).isNull();
        mLithoView.layout(0, 0, 50, 50);
        final LayoutState layoutState = mLithoView.getComponentTree().getMainThreadLayoutState();
        assertThat(layoutState).isNotNull();
        assertThat(layoutState.isCompatibleSize(50, 50)).isTrue();
    }

    @Test
    public void testMeasureComputesLayoutStateWhenSpecsAreNotExact() {
        mLithoView = new LithoView(RuntimeEnvironment.application);
        mLithoView.setComponent(TestDrawableComponent.create(mLithoView.getComponentContext()).heightPx(100).build());
        mLithoView.measure(makeMeasureSpec(100, MeasureSpec.EXACTLY), makeMeasureSpec(100, MeasureSpec.AT_MOST));
        assertThat(mLithoView.getMeasuredWidth()).isEqualTo(100);
        assertThat(mLithoView.getMeasuredHeight()).isEqualTo(100);
        assertThat(mLithoView.getComponentTree().getMainThreadLayoutState()).isNotNull();
    }

    private static class RecyclerViewLayoutManagerOverrideParams extends ViewGroup.LayoutParams implements LithoView.LayoutManagerOverrideParams {
        private final int mWidthMeasureSpec;

        private final int mHeightMeasureSpec;

        private RecyclerViewLayoutManagerOverrideParams(int widthMeasureSpec, int heightMeasureSpec) {
            super(WRAP_CONTENT, WRAP_CONTENT);
            mWidthMeasureSpec = widthMeasureSpec;
            mHeightMeasureSpec = heightMeasureSpec;
        }

        @Override
        public int getWidthMeasureSpec() {
            return mWidthMeasureSpec;
        }

        @Override
        public int getHeightMeasureSpec() {
            return mHeightMeasureSpec;
        }

        @Override
        public boolean hasValidAdapterPosition() {
            return false;
        }
    }
}

