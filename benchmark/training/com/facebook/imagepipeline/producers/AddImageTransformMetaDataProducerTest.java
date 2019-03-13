/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import Consumer.NO_FLAGS;
import DefaultImageFormats.JPEG;
import DefaultImageFormats.WEBP_SIMPLE;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imageformat.ImageFormatChecker;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imageutils.BitmapUtil;
import com.facebook.imageutils.ImageMetaData;
import com.facebook.imageutils.JfifUtil;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;


@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@PrepareForTest({ ImageFormatChecker.class, JfifUtil.class, BitmapUtil.class })
@Config(manifest = NONE)
public class AddImageTransformMetaDataProducerTest {
    @Mock
    public Producer<EncodedImage> mInputProducer;

    @Mock
    public Consumer<EncodedImage> mConsumer;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public Exception mException;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private AddImageTransformMetaDataProducer mAddMetaDataProducer;

    private Consumer<EncodedImage> mAddMetaDataConsumer;

    private CloseableReference<PooledByteBuffer> mIntermediateResultBufferRef;

    private CloseableReference<PooledByteBuffer> mFinalResultBufferRef;

    private EncodedImage mIntermediateResult;

    private EncodedImage mFinalResult;

    @Test
    public void testOnNewResultLastNotJpeg() {
        Mockito.when(ImageFormatChecker.getImageFormat_WrapIOException(ArgumentMatchers.any(InputStream.class))).thenReturn(WEBP_SIMPLE);
        mAddMetaDataConsumer.onNewResult(mFinalResult, IS_LAST);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(IS_LAST));
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertTrue(EncodedImage.isValid(encodedImage));
        Assert.assertEquals(WEBP_SIMPLE, encodedImage.getImageFormat());
        Assert.assertEquals(0, encodedImage.getRotationAngle());
        Assert.assertEquals((-1), encodedImage.getWidth());
        Assert.assertEquals((-1), encodedImage.getHeight());
    }

    @Test
    public void testOnNewResultNotLastNotJpeg() {
        Mockito.when(ImageFormatChecker.getImageFormat_WrapIOException(ArgumentMatchers.any(InputStream.class))).thenReturn(WEBP_SIMPLE);
        Mockito.when(BitmapUtil.decodeDimensions(ArgumentMatchers.any(InputStream.class))).thenReturn(null);
        mAddMetaDataConsumer.onNewResult(mIntermediateResult, NO_FLAGS);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(NO_FLAGS));
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertTrue(EncodedImage.isValid(encodedImage));
        Assert.assertEquals(WEBP_SIMPLE, encodedImage.getImageFormat());
        Assert.assertEquals(0, encodedImage.getRotationAngle());
        Assert.assertEquals((-1), encodedImage.getWidth());
        Assert.assertEquals((-1), encodedImage.getHeight());
    }

    @Test
    public void testOnNewResultNotLast_DimensionsNotFound() {
        int rotationAngle = 180;
        int orientation = 1;
        Mockito.when(ImageFormatChecker.getImageFormat_WrapIOException(ArgumentMatchers.any(InputStream.class))).thenReturn(JPEG);
        Mockito.when(JfifUtil.getAutoRotateAngleFromOrientation(orientation)).thenReturn(rotationAngle);
        Mockito.when(JfifUtil.getOrientation(ArgumentMatchers.any(InputStream.class))).thenReturn(orientation);
        Mockito.when(BitmapUtil.decodeDimensionsAndColorSpace(ArgumentMatchers.any(InputStream.class))).thenReturn(new ImageMetaData((-1), (-1), null));
        mAddMetaDataConsumer.onNewResult(mIntermediateResult, NO_FLAGS);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(NO_FLAGS));
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertTrue(EncodedImage.isValid(encodedImage));
        Assert.assertEquals((-1), encodedImage.getRotationAngle());
        Assert.assertEquals((-1), encodedImage.getWidth());
        Assert.assertEquals((-1), encodedImage.getHeight());
    }

    @Test
    public void testOnNewResultNotLast_RotationNotFound() {
        Mockito.when(ImageFormatChecker.getImageFormat_WrapIOException(ArgumentMatchers.any(InputStream.class))).thenReturn(JPEG);
        Mockito.when(JfifUtil.getOrientation(ArgumentMatchers.any(InputStream.class))).thenReturn(0);
        Mockito.when(BitmapUtil.decodeDimensionsAndColorSpace(ArgumentMatchers.any(InputStream.class))).thenReturn(new ImageMetaData((-1), (-1), null));
        mAddMetaDataConsumer.onNewResult(mIntermediateResult, NO_FLAGS);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(NO_FLAGS));
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertTrue(EncodedImage.isValid(encodedImage));
        Assert.assertEquals((-1), encodedImage.getRotationAngle());
        Assert.assertEquals((-1), encodedImage.getWidth());
        Assert.assertEquals((-1), encodedImage.getHeight());
    }

    @Test
    public void testOnNewResultNotLastAndJpeg() {
        int rotationAngle = 180;
        int orientation = 1;
        int width = 10;
        int height = 20;
        Mockito.when(ImageFormatChecker.getImageFormat_WrapIOException(ArgumentMatchers.any(InputStream.class))).thenReturn(JPEG);
        Mockito.when(JfifUtil.getAutoRotateAngleFromOrientation(orientation)).thenReturn(rotationAngle);
        Mockito.when(JfifUtil.getOrientation(ArgumentMatchers.any(InputStream.class))).thenReturn(orientation);
        Mockito.when(BitmapUtil.decodeDimensionsAndColorSpace(ArgumentMatchers.any(InputStream.class))).thenReturn(new ImageMetaData(width, height, null));
        mAddMetaDataConsumer.onNewResult(mFinalResult, IS_LAST);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(IS_LAST));
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertTrue(EncodedImage.isValid(encodedImage));
        Assert.assertEquals(JPEG, encodedImage.getImageFormat());
        Assert.assertEquals(rotationAngle, encodedImage.getRotationAngle());
        Assert.assertEquals(width, encodedImage.getWidth());
        Assert.assertEquals(height, encodedImage.getHeight());
    }

    @Test
    public void testOnNewResultLastAndJpeg() {
        int rotationAngle = 180;
        int orientation = 1;
        int width = 10;
        int height = 20;
        Mockito.when(ImageFormatChecker.getImageFormat_WrapIOException(ArgumentMatchers.any(InputStream.class))).thenReturn(JPEG);
        Mockito.when(JfifUtil.getAutoRotateAngleFromOrientation(orientation)).thenReturn(rotationAngle);
        Mockito.when(JfifUtil.getOrientation(ArgumentMatchers.any(InputStream.class))).thenReturn(orientation);
        Mockito.when(BitmapUtil.decodeDimensionsAndColorSpace(ArgumentMatchers.any(InputStream.class))).thenReturn(new ImageMetaData(width, height, null));
        mAddMetaDataConsumer.onNewResult(mFinalResult, IS_LAST);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(IS_LAST));
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertTrue(EncodedImage.isValid(encodedImage));
        Assert.assertEquals(JPEG, encodedImage.getImageFormat());
        Assert.assertEquals(rotationAngle, encodedImage.getRotationAngle());
        Assert.assertEquals(width, encodedImage.getWidth());
        Assert.assertEquals(height, encodedImage.getHeight());
    }

    @Test
    public void testOnFailure() {
        mAddMetaDataConsumer.onFailure(mException);
        Mockito.verify(mConsumer).onFailure(mException);
    }

    @Test
    public void testOnCancellation() {
        mAddMetaDataConsumer.onCancellation();
        Mockito.verify(mConsumer).onCancellation();
    }

    @Test
    public void testOnNullResult() {
        mAddMetaDataConsumer.onNewResult(null, IS_LAST);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(IS_LAST));
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertEquals(encodedImage, null);
    }
}

