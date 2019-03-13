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


import GridLayoutInfo.OVERRIDE_SIZE;
import GridLayoutInfo.ViewportFiller;
import GridLayoutManager.SpanSizeLookup;
import LayoutInfo.RenderInfoCollection;
import SizeSpec.AT_MOST;
import androidx.recyclerview.widget.GridLayoutManager;
import com.facebook.litho.SizeSpec;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(ComponentsTestRunner.class)
public class GridLayoutInfoTest {
    @Test
    public void testOrientation() {
        final GridLayoutInfo verticalGridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(VERTICAL, 2);
        assertThat(verticalGridLayoutInfo.getScrollDirection()).isEqualTo(VERTICAL);
        final GridLayoutInfo horizontalGridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(HORIZONTAL, 2);
        assertThat(horizontalGridLayoutInfo.getScrollDirection()).isEqualTo(HORIZONTAL);
    }

    @Test
    public void testLayoutManagerIsGrid() {
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(VERTICAL, 2);
        assertThat(gridLayoutInfo.getLayoutManager()).isInstanceOf(GridLayoutManager.class);
    }

    @Test
    public void testApproximateRangeVertical() {
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(VERTICAL, 3);
        int rangeSize = gridLayoutInfo.approximateRangeSize(10, 10, 30, 100);
        assertThat(rangeSize).isEqualTo(30);
    }

    @Test
    public void testApproximateRangeHorizontal() {
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(HORIZONTAL, 2);
        int rangeSize = gridLayoutInfo.approximateRangeSize(15, 10, 100, 20);
        assertThat(rangeSize).isEqualTo(14);
    }

    @Test
    public void testGetChildMeasureSpecVertical() {
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(VERTICAL, 3);
        final int sizeSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.getSpanSize()).thenReturn(2);
        final int heightSpec = gridLayoutInfo.getChildHeightSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getMode(heightSpec)).isEqualTo(SizeSpec.UNSPECIFIED);
        final int widthSpec = gridLayoutInfo.getChildWidthSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getSize(widthSpec)).isEqualTo(((200 / 3) * 2));
        assertThat(SizeSpec.getMode(widthSpec)).isEqualTo(SizeSpec.EXACTLY);
    }

    @Test
    public void testGetChildMeasureSpecOverride() {
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(VERTICAL, 3);
        final int sizeSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.getSpanSize()).thenReturn(2);
        Mockito.when(renderInfo.getCustomAttribute(OVERRIDE_SIZE)).thenReturn(20);
        final int heightSpec = gridLayoutInfo.getChildHeightSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getMode(heightSpec)).isEqualTo(SizeSpec.UNSPECIFIED);
        final int widthSpec = gridLayoutInfo.getChildWidthSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getSize(widthSpec)).isEqualTo(20);
        assertThat(SizeSpec.getMode(widthSpec)).isEqualTo(SizeSpec.EXACTLY);
    }

    @Test
    public void testGetChildMeasureSpecHorizontal() {
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(HORIZONTAL, 3);
        final int sizeSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.getSpanSize()).thenReturn(2);
        final int heightSpec = gridLayoutInfo.getChildHeightSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getSize(heightSpec)).isEqualTo(((200 / 3) * 2));
        assertThat(SizeSpec.getMode(heightSpec)).isEqualTo(SizeSpec.EXACTLY);
        final int widthSpec = gridLayoutInfo.getChildWidthSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getMode(widthSpec)).isEqualTo(SizeSpec.UNSPECIFIED);
    }

    @Test
    public void testComputeWrappedHeightOnVertical() {
        /* -------------------
        | 200 | 200 | 200 |
        -------------------
        | 200 | 200 | 200 |
        -------------------
        | 200 | 200 | 200 |
        -------------------
        | 200 |
        -------
         */
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(VERTICAL, 3);
        final int sizeSpec = SizeSpec.makeSizeSpec(1000, AT_MOST);
        final List<ComponentTreeHolder> componentTreeHolders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final ComponentTreeHolder holder = Mockito.mock(ComponentTreeHolder.class);
            Mockito.when(holder.getMeasuredHeight()).thenReturn(200);
            componentTreeHolders.add(holder);
        }
        int measuredHeight = gridLayoutInfo.computeWrappedHeight(SizeSpec.getSize(sizeSpec), componentTreeHolders);
        assertThat(measuredHeight).isEqualTo(800);
    }

    @Test
    public void testComputeWrappedHeightOnVerticalWrapped() {
        /* -------------------
        | 200 | 200 | 200 |
        -------------------
        | 200 | 200 | 200 |
        -------------------
        | 200 | 200 | 200 |
        ~~~~~~~~~~~~~~~~~~~
         */
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(VERTICAL, 3);
        final int sizeSpec = SizeSpec.makeSizeSpec(600, AT_MOST);
        final List<ComponentTreeHolder> componentTreeHolders = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final ComponentTreeHolder holder = Mockito.mock(ComponentTreeHolder.class);
            Mockito.when(holder.getMeasuredHeight()).thenReturn(200);
            componentTreeHolders.add(holder);
        }
        int measuredHeight = gridLayoutInfo.computeWrappedHeight(SizeSpec.getSize(sizeSpec), componentTreeHolders);
        assertThat(measuredHeight).isEqualTo(600);
    }

    @Test
    public void testVerticalViewportFiller() {
        final int spanCount = 3;
        final int itemHeight = 10;
        GridLayoutInfo.ViewportFiller viewportFiller = new GridLayoutInfo.ViewportFiller(100, 100, VERTICAL, spanCount);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.isFullSpan()).thenReturn(false);
        Mockito.when(renderInfo.getSpanSize()).thenReturn(1);
        for (int i = 0; i < 8; i++) {
            viewportFiller.add(renderInfo, 100, itemHeight);
        }
        assertThat(viewportFiller.getFill()).isEqualTo((itemHeight * spanCount));
    }

    @Test
    public void testVerticalViewportFillerWithFullSpan() {
        final int spanCount = 3;
        final int itemHeight = 10;
        GridLayoutInfo.ViewportFiller viewportFiller = new GridLayoutInfo.ViewportFiller(100, 100, VERTICAL, spanCount);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.isFullSpan()).thenReturn(true);
        Mockito.when(renderInfo.getSpanSize()).thenReturn(spanCount);
        for (int i = 0; i < 8; i++) {
            viewportFiller.add(renderInfo, 100, itemHeight);
        }
        assertThat(viewportFiller.getFill()).isEqualTo((itemHeight * 8));
    }

    @Test
    public void testVerticalViewportFillerWithDifferentSpan() {
        /* Test different spans (full span, partial span, no span)
        -------------
        |     A     |
        -------------
        |  B    | C |
        -------------
        | D | E | F |
        -------------
         */
        final int spanCount = 3;
        final int itemHeight = 10;
        GridLayoutInfo.ViewportFiller viewportFiller = new GridLayoutInfo.ViewportFiller(100, 100, VERTICAL, spanCount);
        final RenderInfo renderInfoA = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfoA.isFullSpan()).thenReturn(true);
        Mockito.when(renderInfoA.getSpanSize()).thenReturn(spanCount);
        viewportFiller.add(renderInfoA, (10 * spanCount), itemHeight);
        final RenderInfo renderInfoB = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfoB.isFullSpan()).thenReturn(false);
        Mockito.when(renderInfoB.getSpanSize()).thenReturn(2);
        viewportFiller.add(renderInfoB, (10 * 2), itemHeight);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.isFullSpan()).thenReturn(false);
        Mockito.when(renderInfo.getSpanSize()).thenReturn(1);
        for (int i = 0; i < 4; i++) {
            viewportFiller.add(renderInfo, 10, itemHeight);
        }
        assertThat(viewportFiller.getFill()).isEqualTo((itemHeight * 3));
    }

    @Test
    public void testHorizontalViewportFiller() {
        final int spanCount = 3;
        final int itemWidth = 10;
        GridLayoutInfo.ViewportFiller viewportFiller = new GridLayoutInfo.ViewportFiller(100, 100, HORIZONTAL, spanCount);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.isFullSpan()).thenReturn(false);
        Mockito.when(renderInfo.getSpanSize()).thenReturn(1);
        for (int i = 0; i < 8; i++) {
            viewportFiller.add(renderInfo, itemWidth, 100);
        }
        assertThat(viewportFiller.getFill()).isEqualTo((itemWidth * spanCount));
    }

    @Test
    public void testHorizontalViewportFillerWithFullSpan() {
        final int spanCount = 3;
        final int itemWidth = 10;
        GridLayoutInfo.ViewportFiller viewportFiller = new GridLayoutInfo.ViewportFiller(100, 100, HORIZONTAL, spanCount);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.isFullSpan()).thenReturn(true);
        Mockito.when(renderInfo.getSpanSize()).thenReturn(spanCount);
        for (int i = 0; i < 8; i++) {
            viewportFiller.add(renderInfo, itemWidth, 100);
        }
        assertThat(viewportFiller.getFill()).isEqualTo((itemWidth * 8));
    }

    @Test
    public void testHorizontalViewportFillerWithDifferentSpan() {
        /* Test different spans (full span, partial span, no span)
        -------------
        |   |   | D |
        |   | B |---|
        | A |   | E |
        |   |---|---|
        |   | C | F |
        -------------
         */
        final int spanCount = 3;
        final int itemWidth = 10;
        GridLayoutInfo.ViewportFiller viewportFiller = new GridLayoutInfo.ViewportFiller(100, 100, HORIZONTAL, spanCount);
        final RenderInfo renderInfoA = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfoA.isFullSpan()).thenReturn(true);
        Mockito.when(renderInfoA.getSpanSize()).thenReturn(spanCount);
        viewportFiller.add(renderInfoA, itemWidth, (10 * spanCount));
        final RenderInfo renderInfoB = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfoB.isFullSpan()).thenReturn(false);
        Mockito.when(renderInfoB.getSpanSize()).thenReturn(2);
        viewportFiller.add(renderInfoB, itemWidth, (10 * 2));
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.isFullSpan()).thenReturn(false);
        Mockito.when(renderInfo.getSpanSize()).thenReturn(1);
        for (int i = 0; i < 4; i++) {
            viewportFiller.add(renderInfo, itemWidth, 10);
        }
        assertThat(viewportFiller.getFill()).isEqualTo((itemWidth * 3));
    }

    @Test
    public void testFullSpanChildWidthSpec() {
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(VERTICAL, 3);
        final int sizeSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.isFullSpan()).thenReturn(true);
        final int heightSpec = gridLayoutInfo.getChildHeightSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getMode(heightSpec)).isEqualTo(SizeSpec.UNSPECIFIED);
        final int widthSpec = gridLayoutInfo.getChildWidthSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getSize(widthSpec)).isEqualTo(200);
        assertThat(SizeSpec.getMode(widthSpec)).isEqualTo(SizeSpec.EXACTLY);
    }

    @Test
    public void testFullSpanChildHeightSpec() {
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(HORIZONTAL, 3);
        final int sizeSpec = SizeSpec.makeSizeSpec(200, SizeSpec.EXACTLY);
        final RenderInfo renderInfo = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo.isFullSpan()).thenReturn(true);
        final int widthSpec = gridLayoutInfo.getChildWidthSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getMode(widthSpec)).isEqualTo(SizeSpec.UNSPECIFIED);
        final int heightSpec = gridLayoutInfo.getChildHeightSpec(sizeSpec, renderInfo);
        assertThat(SizeSpec.getSize(heightSpec)).isEqualTo(200);
        assertThat(SizeSpec.getMode(heightSpec)).isEqualTo(SizeSpec.EXACTLY);
    }

    @Test
    public void testGridSpanSizeLookup() {
        final GridLayoutInfo gridLayoutInfo = GridLayoutInfoTest.createGridLayoutInfo(HORIZONTAL, 3);
        final RenderInfo renderInfo1 = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo1.isFullSpan()).thenReturn(true);
        Mockito.when(renderInfo1.getSpanSize()).thenReturn(1);
        final RenderInfo renderInfo2 = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo2.isFullSpan()).thenReturn(false);
        Mockito.when(renderInfo2.getSpanSize()).thenReturn(2);
        final RenderInfo renderInfo3 = Mockito.mock(RenderInfo.class);
        Mockito.when(renderInfo3.isFullSpan()).thenReturn(false);
        Mockito.when(renderInfo3.getSpanSize()).thenReturn(1);
        final LayoutInfo.RenderInfoCollection renderInfoCollection = Mockito.mock(RenderInfoCollection.class);
        Mockito.when(renderInfoCollection.getRenderInfoAt(0)).thenReturn(renderInfo1);
        Mockito.when(renderInfoCollection.getRenderInfoAt(1)).thenReturn(renderInfo2);
        Mockito.when(renderInfoCollection.getRenderInfoAt(2)).thenReturn(renderInfo3);
        gridLayoutInfo.setRenderInfoCollection(renderInfoCollection);
        final GridLayoutManager.SpanSizeLookup spanSizeLookup = getSpanSizeLookup();
        assertThat(spanSizeLookup.getSpanSize(0)).isEqualTo(3);
        assertThat(spanSizeLookup.getSpanSize(1)).isEqualTo(2);
        assertThat(spanSizeLookup.getSpanSize(2)).isEqualTo(1);
    }
}

