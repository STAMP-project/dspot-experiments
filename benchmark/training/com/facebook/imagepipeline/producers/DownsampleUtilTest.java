/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import DefaultImageFormats.PNG;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.transcoder.DownsampleUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class DownsampleUtilTest {
    private static final int MAX_BITMAP_SIZE = 2024;

    private ImageRequest mImageRequest;

    private EncodedImage mEncodedImage;

    @Test
    public void testDetermineSampleSize_NullResizeOptions() {
        whenImageWidthAndHeight(0, 0);
        // Null resizeOptions
        Assert.assertEquals(1, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
    }

    @Test
    public void testDetermineSampleSize_NoEncodedImageDimensions() {
        whenImageWidthAndHeight(0, 0);
        whenRequestResizeWidthAndHeightWithExifRotation(1, 1);
        Assert.assertEquals(1, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
        // Width or height of the encoded image are 0
        mEncodedImage.setWidth(100);
        Assert.assertEquals(1, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
        mEncodedImage.setWidth(0);
        mEncodedImage.setHeight(100);
        Assert.assertEquals(1, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
    }

    @Test
    public void testDetermineSampleSize_JPEG() {
        whenImageWidthAndHeight(100, 100);
        whenRequestResizeWidthAndHeightWithExifRotation(50, 50);
        Assert.assertEquals(2, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
        whenRequestResizeWidthAndHeightWithExifRotation(50, 25);
        Assert.assertEquals(2, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
    }

    @Test
    public void testDetermineSampleSize_PNG() {
        whenImageWidthAndHeight(150, 150);
        mEncodedImage.setImageFormat(PNG);
        whenRequestResizeWidthAndHeightWithExifRotation(50, 50);
        Assert.assertEquals(3, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
    }

    @Test
    public void testDetermineSampleSize_WithRotation() {
        whenImageWidthHeightAndRotation(50, 100, 90);
        whenRequestResizeWidthAndHeightWithExifRotation(50, 25);
        Assert.assertEquals(2, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
        whenRequestResizeWidthAndHeightWithExifRotation(25, 50);
        Assert.assertEquals(1, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
    }

    @Test
    public void testDetermineSampleSize_WithRotationForcedByRequest() {
        whenImageWidthAndHeight(50, 100);
        // The rotation angles should be ignored as they're dealt with by the ResizeAndRotateProducer
        // 50,100 -> 50,25 = 1
        whenRequestResizeWidthHeightAndForcedRotation(50, 25, 90);
        Assert.assertEquals(1, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
        // 50,100 -> 25,50 = 2
        whenRequestResizeWidthHeightAndForcedRotation(25, 50, 270);
        Assert.assertEquals(2, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
        // 50,100 -> 10,20 = 5
        whenRequestResizeWidthHeightAndForcedRotation(10, 20, 180);
        Assert.assertEquals(5, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
    }

    @Test
    public void testDetermineSampleSize_OverMaxPossibleSize() {
        whenImageWidthAndHeight(4000, 4000);
        whenRequestResizeWidthAndHeightWithExifRotation(4000, 4000);
        Assert.assertEquals(2, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
        whenImageWidthAndHeight(8000, 8000);
        whenRequestResizeWidthAndHeightWithExifRotation(8000, 8000);
        Assert.assertEquals(4, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
    }

    @Test
    public void testDetermineSampleSize_CustomMaxPossibleSize() {
        whenImageWidthAndHeight(4000, 4000);
        whenRequestResizeWidthHeightAndMaxBitmapSize(4000, 4000, 4096);
        Assert.assertEquals(1, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
        whenImageWidthAndHeight(8000, 8000);
        whenRequestResizeWidthHeightAndMaxBitmapSize(8000, 8000, 4096);
        Assert.assertEquals(2, DownsampleUtil.determineSampleSize(mImageRequest.getRotationOptions(), mImageRequest.getResizeOptions(), mEncodedImage, DownsampleUtilTest.MAX_BITMAP_SIZE));
    }

    @Test
    public void testRatioToSampleSize() {
        Assert.assertEquals(1, DownsampleUtil.ratioToSampleSize(1.0F));
        Assert.assertEquals(1, DownsampleUtil.ratioToSampleSize(0.667F));
        Assert.assertEquals(2, DownsampleUtil.ratioToSampleSize(0.665F));
        Assert.assertEquals(2, DownsampleUtil.ratioToSampleSize(0.389F));
        Assert.assertEquals(3, DownsampleUtil.ratioToSampleSize(0.387F));
        Assert.assertEquals(3, DownsampleUtil.ratioToSampleSize(0.278F));
        Assert.assertEquals(4, DownsampleUtil.ratioToSampleSize(0.276F));
        Assert.assertEquals(4, DownsampleUtil.ratioToSampleSize(0.2167F));
        Assert.assertEquals(5, DownsampleUtil.ratioToSampleSize(0.2165F));
        Assert.assertEquals(5, DownsampleUtil.ratioToSampleSize(0.1778F));
        Assert.assertEquals(6, DownsampleUtil.ratioToSampleSize(0.1776F));
        Assert.assertEquals(6, DownsampleUtil.ratioToSampleSize(0.1508F));
        Assert.assertEquals(7, DownsampleUtil.ratioToSampleSize(0.1506F));
        Assert.assertEquals(7, DownsampleUtil.ratioToSampleSize(0.131F));
        Assert.assertEquals(8, DownsampleUtil.ratioToSampleSize(0.1308F));
    }

    @Test
    public void testRatioToSampleSizeJPEG() {
        Assert.assertEquals(1, DownsampleUtil.ratioToSampleSizeJPEG(1.0F));
        Assert.assertEquals(1, DownsampleUtil.ratioToSampleSizeJPEG(0.667F));
        Assert.assertEquals(2, DownsampleUtil.ratioToSampleSizeJPEG(0.665F));
        Assert.assertEquals(2, DownsampleUtil.ratioToSampleSizeJPEG(0.334F));
        Assert.assertEquals(4, DownsampleUtil.ratioToSampleSizeJPEG(0.332F));
        Assert.assertEquals(4, DownsampleUtil.ratioToSampleSizeJPEG(0.1667F));
        Assert.assertEquals(8, DownsampleUtil.ratioToSampleSizeJPEG(0.1665F));
        Assert.assertEquals(8, DownsampleUtil.ratioToSampleSizeJPEG(0.0834F));
        Assert.assertEquals(16, DownsampleUtil.ratioToSampleSizeJPEG(0.0832F));
    }

    @Test
    public void testRoundToPowerOfTwo() {
        Assert.assertEquals(1, DownsampleUtil.roundToPowerOfTwo(1));
        Assert.assertEquals(2, DownsampleUtil.roundToPowerOfTwo(2));
        Assert.assertEquals(4, DownsampleUtil.roundToPowerOfTwo(3));
        Assert.assertEquals(4, DownsampleUtil.roundToPowerOfTwo(4));
        Assert.assertEquals(8, DownsampleUtil.roundToPowerOfTwo(5));
        Assert.assertEquals(8, DownsampleUtil.roundToPowerOfTwo(6));
        Assert.assertEquals(8, DownsampleUtil.roundToPowerOfTwo(7));
        Assert.assertEquals(8, DownsampleUtil.roundToPowerOfTwo(8));
    }
}

