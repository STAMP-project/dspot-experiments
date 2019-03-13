/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import org.junit.Assert;
import org.junit.Test;


public class ThumbnailSizeCheckerTest {
    private static final int BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS = 1536;

    private static final int[] IMAGE_WIDTHS = new int[]{ 75, 300, 450 };

    private static final int[] IMAGE_HEIGHTS = new int[]{ 150, 300, 100 };

    private static final int[] REQUEST_WIDTHS = new int[]{ 100, 400, 600 };

    private static final int[] REQUEST_HEIGHTS = new int[]{ 200, 400, 133 };

    private static final int TEST_COUNT = ThumbnailSizeCheckerTest.IMAGE_WIDTHS.length;

    @Test
    public void testWithWidthAndHeightAndResizeOptionsNotMoreThan133PercentOfActual() {
        for (int i = 0; i < (ThumbnailSizeCheckerTest.TEST_COUNT); i++) {
            ResizeOptions resizeOptions = new ResizeOptions(ThumbnailSizeCheckerTest.REQUEST_WIDTHS[i], ThumbnailSizeCheckerTest.REQUEST_HEIGHTS[i]);
            Assert.assertTrue(ThumbnailSizeChecker.isImageBigEnough(ThumbnailSizeCheckerTest.IMAGE_WIDTHS[i], ThumbnailSizeCheckerTest.IMAGE_HEIGHTS[i], resizeOptions));
        }
    }

    @Test
    public void testWithWidthAndHeightAndResizeOptionsWithWidthMoreThan133PercentOfActual() {
        ThumbnailSizeCheckerTest.testWithWidthAndHeightNotBigEnoughForResizeOptions(1, 0);
    }

    @Test
    public void testWithWidthAndHeightAndResizeOptionsWithHeightMoreThan133PercentOfActual() {
        ThumbnailSizeCheckerTest.testWithWidthAndHeightNotBigEnoughForResizeOptions(0, 1);
    }

    @Test
    public void testWithLargeEnoughWidthAndHeightWhenNoResizeOptions() {
        Assert.assertTrue(ThumbnailSizeChecker.isImageBigEnough(ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS, ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS, null));
    }

    @Test
    public void testWithInsufficientWidthWhenNoResizeOptions() {
        Assert.assertFalse(ThumbnailSizeChecker.isImageBigEnough(((ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS) - 1), ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS, null));
    }

    @Test
    public void testWithInsufficientHeightWhenNoResizeOptions() {
        Assert.assertFalse(ThumbnailSizeChecker.isImageBigEnough(ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS, ((ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS) - 1), null));
    }

    @Test
    public void testWithImageAndResizeOptionsNotMoreThan133PercentOfActual() {
        ThumbnailSizeCheckerTest.testWithImageBigEnoughForResizeOptions(ThumbnailSizeCheckerTest.IMAGE_WIDTHS, ThumbnailSizeCheckerTest.IMAGE_HEIGHTS, 0);
    }

    @Test
    public void testWithRotatedImageAndResizeOptionsNotMoreThan133PercentOfActual() {
        ThumbnailSizeCheckerTest.testWithImageBigEnoughForResizeOptions(ThumbnailSizeCheckerTest.IMAGE_HEIGHTS, ThumbnailSizeCheckerTest.IMAGE_WIDTHS, 90);
    }

    @Test
    public void testWithImageAndResizeOptionsWithWidthMoreThan133PercentOfActual() {
        ThumbnailSizeCheckerTest.testWithImageNotBigEnoughForResizeOptions(ThumbnailSizeCheckerTest.IMAGE_WIDTHS, ThumbnailSizeCheckerTest.IMAGE_HEIGHTS, 0, 1, 0);
    }

    @Test
    public void testWithImageAndResizeOptionsWithHeightMoreThan133PercentOfActual() {
        ThumbnailSizeCheckerTest.testWithImageNotBigEnoughForResizeOptions(ThumbnailSizeCheckerTest.IMAGE_WIDTHS, ThumbnailSizeCheckerTest.IMAGE_HEIGHTS, 0, 0, 1);
    }

    @Test
    public void testWithRotatedImageAndResizeOptionsWithWidthMoreThan133PercentOfActual() {
        ThumbnailSizeCheckerTest.testWithImageNotBigEnoughForResizeOptions(ThumbnailSizeCheckerTest.IMAGE_HEIGHTS, ThumbnailSizeCheckerTest.IMAGE_WIDTHS, 90, 1, 0);
    }

    @Test
    public void testWithRotatedImageAndResizeOptionsWithHeightMoreThan133PercentOfActual() {
        ThumbnailSizeCheckerTest.testWithImageNotBigEnoughForResizeOptions(ThumbnailSizeCheckerTest.IMAGE_HEIGHTS, ThumbnailSizeCheckerTest.IMAGE_WIDTHS, 90, 0, 1);
    }

    @Test
    public void testWithLargeEnoughImageWhenNoResizeOptions() {
        for (int rotation = 0; rotation < 360; rotation += 90) {
            Assert.assertTrue(ThumbnailSizeChecker.isImageBigEnough(ThumbnailSizeCheckerTest.mockImage(ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS, ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS, rotation), null));
        }
    }

    @Test
    public void testImageWithInsufficientWidthWhenNoResizeOptions() {
        for (int rotation = 0; rotation < 360; rotation += 90) {
            EncodedImage mockImage = ThumbnailSizeCheckerTest.mockImage(((ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS) - 1), ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS, rotation);
            Assert.assertFalse(ThumbnailSizeChecker.isImageBigEnough(mockImage, null));
        }
    }

    @Test
    public void testImageWithInsufficientHeightWhenNoResizeOptions() {
        for (int rotation = 0; rotation < 360; rotation += 90) {
            EncodedImage mockImage = ThumbnailSizeCheckerTest.mockImage(ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS, ((ThumbnailSizeCheckerTest.BIG_ENOUGH_SIZE_FOR_NO_RESIZE_OPTIONS) - 1), rotation);
            Assert.assertFalse(ThumbnailSizeChecker.isImageBigEnough(mockImage, null));
        }
    }
}

