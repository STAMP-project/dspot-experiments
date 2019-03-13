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
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BranchOnSeparateImagesProducerTest {
    private static final int WIDTH = 10;

    private static final int HEIGHT = 20;

    @Mock
    public Consumer<EncodedImage> mConsumer;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public Exception mException;

    private Producer<EncodedImage> mInputProducer1;

    private Producer<EncodedImage> mInputProducer2;

    private Consumer<EncodedImage> mFirstProducerConsumer;

    private Consumer<EncodedImage> mSecondProducerConsumer;

    private EncodedImage mIntermediateResult;

    private EncodedImage mFirstProducerFinalResult;

    private EncodedImage mSecondProducerFinalResult;

    @Test
    public void testFirstProducerReturnsIntermediateResultThenGoodEnoughResult() {
        EncodedImage intermediateEncodedImage = new EncodedImage(mIntermediateResult.getByteBufferRef());
        intermediateEncodedImage.setImageFormat(JPEG);
        intermediateEncodedImage.setRotationAngle((-1));
        intermediateEncodedImage.setWidth(BranchOnSeparateImagesProducerTest.WIDTH);
        intermediateEncodedImage.setHeight(BranchOnSeparateImagesProducerTest.HEIGHT);
        mFirstProducerConsumer.onNewResult(intermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(intermediateEncodedImage, NO_FLAGS);
        EncodedImage finalEncodedImage = new EncodedImage(mFirstProducerFinalResult.getByteBufferRef());
        finalEncodedImage.setImageFormat(JPEG);
        finalEncodedImage.setRotationAngle((-1));
        finalEncodedImage.setWidth(BranchOnSeparateImagesProducerTest.WIDTH);
        finalEncodedImage.setHeight(BranchOnSeparateImagesProducerTest.HEIGHT);
        mFirstProducerConsumer.onNewResult(finalEncodedImage, IS_LAST);
        Mockito.verify(mConsumer).onNewResult(finalEncodedImage, IS_LAST);
        Mockito.verify(mInputProducer2, Mockito.never()).produceResults(ArgumentMatchers.any(Consumer.class), ArgumentMatchers.eq(mProducerContext));
    }

    @Test
    public void testFirstProducerResultNotGoodEnough() {
        EncodedImage firstProducerEncodedImage = new EncodedImage(mFirstProducerFinalResult.getByteBufferRef());
        firstProducerEncodedImage.setRotationAngle((-1));
        firstProducerEncodedImage.setWidth(((BranchOnSeparateImagesProducerTest.WIDTH) / 2));
        firstProducerEncodedImage.setHeight(((BranchOnSeparateImagesProducerTest.HEIGHT) / 2));
        mFirstProducerConsumer.onNewResult(firstProducerEncodedImage, IS_LAST);
        Mockito.verify(mConsumer).onNewResult(firstProducerEncodedImage, NO_FLAGS);
        EncodedImage intermediateEncodedImage = new EncodedImage(mIntermediateResult.getByteBufferRef());
        intermediateEncodedImage.setRotationAngle((-1));
        intermediateEncodedImage.setWidth(((BranchOnSeparateImagesProducerTest.WIDTH) / 2));
        intermediateEncodedImage.setHeight(((BranchOnSeparateImagesProducerTest.HEIGHT) / 2));
        mSecondProducerConsumer.onNewResult(intermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(intermediateEncodedImage, NO_FLAGS);
        EncodedImage secondProducerEncodedImage = new EncodedImage(mSecondProducerFinalResult.getByteBufferRef());
        secondProducerEncodedImage.setRotationAngle((-1));
        secondProducerEncodedImage.setWidth(((BranchOnSeparateImagesProducerTest.WIDTH) / 2));
        secondProducerEncodedImage.setHeight(((BranchOnSeparateImagesProducerTest.HEIGHT) / 2));
        mSecondProducerConsumer.onNewResult(secondProducerEncodedImage, IS_LAST);
        Mockito.verify(mConsumer).onNewResult(secondProducerEncodedImage, IS_LAST);
    }

    @Test
    public void testFirstProducerReturnsNull() {
        mFirstProducerConsumer.onNewResult(null, IS_LAST);
        Mockito.verify(mConsumer, Mockito.never()).onNewResult(ArgumentMatchers.isNull(EncodedImage.class), ArgumentMatchers.anyInt());
        EncodedImage finalEncodedImage = new EncodedImage(mIntermediateResult.getByteBufferRef());
        mSecondProducerConsumer.onNewResult(finalEncodedImage, IS_LAST);
        Mockito.verify(mConsumer).onNewResult(finalEncodedImage, IS_LAST);
    }

    @Test
    public void testFirstProducerFails() {
        mFirstProducerConsumer.onFailure(mException);
        Mockito.verify(mConsumer, Mockito.never()).onFailure(mException);
        EncodedImage finalEncodedImage = new EncodedImage(mIntermediateResult.getByteBufferRef());
        mSecondProducerConsumer.onNewResult(finalEncodedImage, IS_LAST);
        Mockito.verify(mConsumer).onNewResult(finalEncodedImage, IS_LAST);
    }

    @Test
    public void testFirstProducerCancellation() {
        mFirstProducerConsumer.onCancellation();
        Mockito.verify(mConsumer).onCancellation();
        Mockito.verify(mInputProducer2, Mockito.never()).produceResults(ArgumentMatchers.any(Consumer.class), ArgumentMatchers.eq(mProducerContext));
    }

    @Test
    public void testFirstProducerReturnsTwoResultsThumbnailsNotAllowed() {
        Mockito.when(mImageRequest.getLocalThumbnailPreviewsEnabled()).thenReturn(false);
        EncodedImage intermediateEncodedImage = new EncodedImage(mIntermediateResult.getByteBufferRef());
        intermediateEncodedImage.setImageFormat(JPEG);
        intermediateEncodedImage.setRotationAngle((-1));
        intermediateEncodedImage.setWidth(((BranchOnSeparateImagesProducerTest.WIDTH) / 2));
        intermediateEncodedImage.setHeight(((BranchOnSeparateImagesProducerTest.HEIGHT) / 2));
        mFirstProducerConsumer.onNewResult(intermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer, Mockito.never()).onNewResult(intermediateEncodedImage, NO_FLAGS);
        EncodedImage finalEncodedImage = new EncodedImage(mFirstProducerFinalResult.getByteBufferRef());
        finalEncodedImage.setImageFormat(JPEG);
        finalEncodedImage.setRotationAngle((-1));
        finalEncodedImage.setWidth(BranchOnSeparateImagesProducerTest.WIDTH);
        finalEncodedImage.setHeight(BranchOnSeparateImagesProducerTest.HEIGHT);
        mFirstProducerConsumer.onNewResult(finalEncodedImage, IS_LAST);
        Mockito.verify(mConsumer).onNewResult(finalEncodedImage, IS_LAST);
        Mockito.verify(mInputProducer2, Mockito.never()).produceResults(ArgumentMatchers.any(Consumer.class), ArgumentMatchers.eq(mProducerContext));
    }

    @Test
    public void testFirstProducerResultNotGoodEnoughThumbnailsNotAllowed() {
        Mockito.when(mImageRequest.getLocalThumbnailPreviewsEnabled()).thenReturn(false);
        EncodedImage firstProducerEncodedImage = new EncodedImage(mFirstProducerFinalResult.getByteBufferRef());
        firstProducerEncodedImage.setRotationAngle((-1));
        firstProducerEncodedImage.setWidth(((BranchOnSeparateImagesProducerTest.WIDTH) / 2));
        firstProducerEncodedImage.setHeight(((BranchOnSeparateImagesProducerTest.HEIGHT) / 2));
        mFirstProducerConsumer.onNewResult(firstProducerEncodedImage, IS_LAST);
        Mockito.verify(mConsumer, Mockito.never()).onNewResult(firstProducerEncodedImage, NO_FLAGS);
        EncodedImage intermediateEncodedImage = new EncodedImage(mIntermediateResult.getByteBufferRef());
        intermediateEncodedImage.setRotationAngle((-1));
        intermediateEncodedImage.setWidth(((BranchOnSeparateImagesProducerTest.WIDTH) / 2));
        intermediateEncodedImage.setHeight(((BranchOnSeparateImagesProducerTest.HEIGHT) / 2));
        mSecondProducerConsumer.onNewResult(intermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(intermediateEncodedImage, NO_FLAGS);
        EncodedImage secondProducerEncodedImage = new EncodedImage(mFirstProducerFinalResult.getByteBufferRef());
        secondProducerEncodedImage.setRotationAngle((-1));
        secondProducerEncodedImage.setWidth(((BranchOnSeparateImagesProducerTest.WIDTH) / 2));
        secondProducerEncodedImage.setHeight(((BranchOnSeparateImagesProducerTest.HEIGHT) / 2));
        mSecondProducerConsumer.onNewResult(secondProducerEncodedImage, IS_LAST);
        Mockito.verify(mConsumer).onNewResult(secondProducerEncodedImage, IS_LAST);
    }
}

