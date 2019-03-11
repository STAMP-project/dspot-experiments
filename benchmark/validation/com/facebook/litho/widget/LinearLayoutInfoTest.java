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
package com.facebook.litho.widget;


import LinearLayoutInfo.ViewportFiller;
import SizeSpec.AT_MOST;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.facebook.litho.SizeSpec;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


/**
 * Tests for {@link LinearLayoutInfo}
 */
@RunWith(ComponentsTestRunner.class)
public class LinearLayoutInfoTest {
    @Test
    public void testOrientations() {
        final LinearLayoutInfo verticalLinearLayoutInfo = new LinearLayoutInfo(application, VERTICAL, false);
        assertThat(VERTICAL).isEqualTo(verticalLinearLayoutInfo.getScrollDirection());
        final LinearLayoutInfo horizontalLinearLayoutInfo = new LinearLayoutInfo(application, HORIZONTAL, false);
        assertThat(HORIZONTAL).isEqualTo(horizontalLinearLayoutInfo.getScrollDirection());
    }

    @Test
    public void testGetLayoutManager() {
        final LinearLayoutInfo linearLayoutInfo = new LinearLayoutInfo(application, VERTICAL, false);
        assertThat(linearLayoutInfo.getLayoutManager()).isInstanceOf(LinearLayoutManager.class);
    }

    @Test
    public void testApproximateRangeVertical() {
        final LinearLayoutInfo linearLayoutInfo = new LinearLayoutInfo(application, VERTICAL, false);
        int rangeSize = linearLayoutInfo.approximateRangeSize(10, 10, 10, 100);
        assertThat(rangeSize).isEqualTo(10);
    }

    @Test
    public void testApproximateRangeHorizontal() {
        final LinearLayoutInfo linearLayoutInfo = new LinearLayoutInfo(application, HORIZONTAL, false);
        int rangeSize = linearLayoutInfo.approximateRangeSize(10, 10, 100, 10);
        assertThat(rangeSize).isEqualTo(10);
    }

    @Test
    public void testGetChildMeasureSpecVertical() {
        final LinearLayoutInfo linearLayoutInfo = new LinearLayoutInfo(application, VERTICAL, false);
        final int sizeSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        final int heightSpec = linearLayoutInfo.getChildHeightSpec(sizeSpec, null);
        assertThat(SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)).isEqualTo(heightSpec);
        final int widthSpec = linearLayoutInfo.getChildWidthSpec(sizeSpec, null);
        assertThat(sizeSpec).isEqualTo(widthSpec);
    }

    @Test
    public void testGetChildMeasureSpecHorizontal() {
        final LinearLayoutInfo linearLayoutInfo = new LinearLayoutInfo(application, HORIZONTAL, false);
        final int sizeSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        final int heightSpec = linearLayoutInfo.getChildHeightSpec(sizeSpec, null);
        assertThat(sizeSpec).isEqualTo(heightSpec);
        final int widthSpec = linearLayoutInfo.getChildWidthSpec(sizeSpec, null);
        assertThat(SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED)).isEqualTo(widthSpec);
    }

    @Test
    public void testComputeWrappedHeightOnVertical() {
        /* -------
        | 100 |
        ~~~~~~~
        ... x8
        ~~~~~~~
        | 100 |
        -------
         */
        final LinearLayoutInfo linearLayoutInfo = new LinearLayoutInfo(application, VERTICAL, false);
        final int sizeSpec = SizeSpec.makeSizeSpec(1200, AT_MOST);
        final List<ComponentTreeHolder> componentTreeHolders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final ComponentTreeHolder holder = Mockito.mock(ComponentTreeHolder.class);
            Mockito.when(holder.getMeasuredHeight()).thenReturn(100);
            Mockito.when(holder.isTreeValid()).thenReturn(true);
            componentTreeHolders.add(holder);
        }
        int measuredHeight = linearLayoutInfo.computeWrappedHeight(SizeSpec.getSize(sizeSpec), componentTreeHolders);
        assertThat(measuredHeight).isEqualTo(1000);
    }

    @Test
    public void testComputeWrappedHeightOnVerticalWrapped() {
        /* -------
        | 100 |
        ~~~~~~~
        ... x7
        ~~~~~~~
         */
        final LinearLayoutInfo linearLayoutInfo = new LinearLayoutInfo(application, VERTICAL, false);
        final int sizeSpec = SizeSpec.makeSizeSpec(800, AT_MOST);
        final List<ComponentTreeHolder> componentTreeHolders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final ComponentTreeHolder holder = Mockito.mock(ComponentTreeHolder.class);
            Mockito.when(holder.getMeasuredHeight()).thenReturn(100);
            Mockito.when(holder.isTreeValid()).thenReturn(true);
            componentTreeHolders.add(holder);
        }
        int measuredHeight = linearLayoutInfo.computeWrappedHeight(SizeSpec.getSize(sizeSpec), componentTreeHolders);
        assertThat(measuredHeight).isEqualTo(800);
    }

    @Test
    public void testViewportFiller() {
        LinearLayoutInfo.ViewportFiller viewportFiller = new LinearLayoutInfo.ViewportFiller(100, 100, VERTICAL);
        for (int i = 0; i < 8; i++) {
            viewportFiller.add(Mockito.mock(RenderInfo.class), 100, 10);
        }
        assertThat(viewportFiller.getFill()).isEqualTo(80);
    }
}

