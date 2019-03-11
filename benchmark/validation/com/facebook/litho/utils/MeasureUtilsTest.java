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


import com.facebook.litho.Size;
import com.facebook.litho.SizeSpec;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static MeasureSpec.makeMeasureSpec;


@RunWith(ComponentsTestRunner.class)
public class MeasureUtilsTest {
    @Test
    public void testWidthExactlyHeightAtMost() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(10, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(30, SizeSpec.AT_MOST), 0.5F, size);
        assertThat(size.width).isEqualTo(10);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testWidthExactlyHeightUnspecified() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(10, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), 0.5F, size);
        assertThat(size.width).isEqualTo(10);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testWidthAtMostHeightExactly() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(30, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(10, SizeSpec.EXACTLY), 2.0F, size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(10);
    }

    @Test
    public void testWidthUnspecifiedHeightExactly() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(10, SizeSpec.EXACTLY), 2.0F, size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(10);
    }

    @Test
    public void testWidthAtMostHeightAtMostWidthSmaller() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(10, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), 0.5F, size);
        assertThat(size.width).isEqualTo(10);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testWidthAtMostHeightAtMostHeightSmaller() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(10, SizeSpec.AT_MOST), 2.0F, size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(10);
    }

    @Test
    public void testWidthAtMostHeightUnspecified() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), 1.0F, size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testWidthUnspecifiedHeightAtMost() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), 1.0F, size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testWithInstrinsicSize() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), 10, 10, 1.0F, size);
        assertThat(size.width).isEqualTo(10);
        assertThat(size.height).isEqualTo(10);
    }

    @Test
    public void testWidthExactlyHeightAtMostEqual() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), 1, size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testWidthAtMostHeightExactlyEqual() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY), 1, size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testWidthUnspecifiedHeightUnspecified() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), 10, size);
        assertThat(size.width).isEqualTo(0);
        assertThat(size.height).isEqualTo(0);
    }

    @Test
    public void testWidthExactlyHeightTooSmall() {
        final Size size = new Size();
        MeasureUtils.measureWithAspectRatio(SizeSpec.makeSizeSpec(10, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), 0.1F, size);
        assertThat(size.width).isEqualTo(10);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testWidthUnspecifiedHeightUnspecifiedEqual() {
        final Size size = new Size();
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), size);
        assertThat(size.width).isEqualTo(0);
        assertThat(size.height).isEqualTo(0);
    }

    @Test
    public void testWidthAtMostHeightAtMostEqual() {
        final Size size = new Size();
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(10, SizeSpec.AT_MOST), size);
        assertThat(size.width).isEqualTo(10);
        assertThat(size.height).isEqualTo(10);
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(30, SizeSpec.AT_MOST), size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void textAtMostUnspecifiedEqual() {
        final Size size = new Size();
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(20, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(10, SizeSpec.UNSPECIFIED), size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(20);
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(10, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(30, SizeSpec.AT_MOST), size);
        assertThat(size.width).isEqualTo(30);
        assertThat(size.height).isEqualTo(30);
    }

    @Test
    public void testExactlyUnspecifiedEqual() {
        final Size size = new Size();
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(10, SizeSpec.UNSPECIFIED), size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(20);
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(20, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(10, SizeSpec.EXACTLY), size);
        assertThat(size.width).isEqualTo(10);
        assertThat(size.height).isEqualTo(10);
    }

    @Test
    public void testExactlyAtMostSmallerEqual() {
        final Size size = new Size();
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(10, SizeSpec.AT_MOST), size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(10);
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(10, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY), size);
        assertThat(size.width).isEqualTo(10);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testExactlyAtMostLargerEqual() {
        final Size size = new Size();
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(30, SizeSpec.AT_MOST), size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(20);
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(30, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY), size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void textExactWidthExactHeightEqual() {
        final Size size = new Size();
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(20, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(10, SizeSpec.EXACTLY), size);
        assertThat(size.width).isEqualTo(20);
        assertThat(size.height).isEqualTo(10);
        MeasureUtils.measureWithEqualDimens(SizeSpec.makeSizeSpec(30, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(30, SizeSpec.EXACTLY), size);
        assertThat(size.width).isEqualTo(30);
        assertThat(size.height).isEqualTo(30);
    }

    @Test
    public void testGetViewMeasureSpecExactly() {
        assertThat(MeasureUtils.getViewMeasureSpec(SizeSpec.makeSizeSpec(10, SizeSpec.EXACTLY))).isEqualTo(MeasureUtils.getViewMeasureSpec(makeMeasureSpec(10, MeasureSpec.EXACTLY)));
    }

    @Test
    public void testGetViewMeasureSpecAtMost() {
        assertThat(MeasureUtils.getViewMeasureSpec(SizeSpec.makeSizeSpec(10, SizeSpec.AT_MOST))).isEqualTo(MeasureUtils.getViewMeasureSpec(makeMeasureSpec(10, MeasureSpec.AT_MOST)));
    }

    @Test
    public void testGetViewMeasureSpecUnspecified() {
        assertThat(MeasureUtils.getViewMeasureSpec(SizeSpec.makeSizeSpec(10, SizeSpec.UNSPECIFIED))).isEqualTo(MeasureUtils.getViewMeasureSpec(makeMeasureSpec(10, MeasureSpec.UNSPECIFIED)));
    }

    @Test
    public void testMeasureWithDesiredSizeAndExactlySpec() {
        final Size size = new Size();
        MeasureUtils.measureWithDesiredPx(SizeSpec.makeSizeSpec(50, SizeSpec.EXACTLY), SizeSpec.makeSizeSpec(30, SizeSpec.EXACTLY), 80, 20, size);
        assertThat(size.width).isEqualTo(50);
        assertThat(size.height).isEqualTo(30);
    }

    @Test
    public void testMeasureWithDesiredSizeAndLargerAtMostSpec() {
        final Size size = new Size();
        MeasureUtils.measureWithDesiredPx(SizeSpec.makeSizeSpec(81, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(21, SizeSpec.AT_MOST), 80, 20, size);
        assertThat(size.width).isEqualTo(80);
        assertThat(size.height).isEqualTo(20);
    }

    @Test
    public void testMeasureWithDesiredSizeAndSmallerAtMostSpec() {
        final Size size = new Size();
        MeasureUtils.measureWithDesiredPx(SizeSpec.makeSizeSpec(79, SizeSpec.AT_MOST), SizeSpec.makeSizeSpec(19, SizeSpec.EXACTLY), 80, 20, size);
        assertThat(size.width).isEqualTo(79);
        assertThat(size.height).isEqualTo(19);
    }

    @Test
    public void testMeasureWithDesiredSizeAndUnspecifiedSpec() {
        final Size size = new Size();
        MeasureUtils.measureWithDesiredPx(SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), SizeSpec.makeSizeSpec(0, SizeSpec.UNSPECIFIED), 80, 20, size);
        assertThat(size.width).isEqualTo(80);
        assertThat(size.height).isEqualTo(20);
    }
}

