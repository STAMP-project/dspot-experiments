/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import com.facebook.imagepipeline.common.ResizeOptions;
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
public class ThumbnailBranchProducerTest {
    private static final int[] THUMBNAIL_WIDTHS = new int[]{ 100, 400, 800 };

    private static final int[] THUMBNAIL_HEIGHTS = new int[]{ 100, 600, 400 };

    private static final EncodedImage THROW_FAILURE = Mockito.mock(EncodedImage.class);

    @Mock
    private ProducerContext mProducerContext;

    @Mock
    private ImageRequest mImageRequest;

    @Mock
    private Consumer<EncodedImage> mImageConsumer;

    private ThumbnailProducer<EncodedImage>[] mThumbnailProducers;

    private ThumbnailBranchProducer mProducer;

    @Test
    public void testNullReturnedIfNoResizeOptions() {
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onNewResult(null, IS_LAST);
        Mockito.verifyZeroInteractions(((Object[]) (mThumbnailProducers)));
    }

    @Test
    public void testFirstProducerUsedIfSufficientForResizeOptions() {
        mockRequestWithResizeOptions(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0]);
        EncodedImage firstImage = ThumbnailBranchProducerTest.mockEncodedImage(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0], 0);
        mockProducersToProduce(firstImage);
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onNewResult(firstImage, IS_LAST);
        Mockito.verifyZeroInteractions(mThumbnailProducers[1], mThumbnailProducers[2]);
    }

    @Test
    public void testSecondProducerUsedIfSufficientForResizeOptions() {
        mockRequestWithResizeOptions(((ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0]) + 50), ((ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0]) + 50));
        EncodedImage secondImage = ThumbnailBranchProducerTest.mockEncodedImage(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[1], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[1], 0);
        mockProducersToProduce(ThumbnailBranchProducerTest.mockEncodedImage(((ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0]) + 50), ((ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0]) + 50), 0), secondImage);
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onNewResult(secondImage, IS_LAST);
        Mockito.verifyZeroInteractions(mThumbnailProducers[2]);
    }

    @Test
    public void testFinalProducerUsedIfFirstTwoReturnNullOrFailure() {
        mockRequestWithResizeOptions(((ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0]) - 50), ((ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0]) - 50));
        EncodedImage thirdImage = ThumbnailBranchProducerTest.mockEncodedImage(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[2], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[2], 0);
        mockProducersToProduce(ThumbnailBranchProducerTest.THROW_FAILURE, null, thirdImage);
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onNewResult(thirdImage, IS_LAST);
        verifyAllProducersRequestedForResults();
    }

    @Test
    public void testNullReturnedIfNoProducerSufficientForResizeOptions() {
        int width = (ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[2]) + 50;
        int height = (ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[2]) + 50;
        mockRequestWithResizeOptions(width, height);
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onNewResult(null, IS_LAST);
        ResizeOptions resizeOptions = new ResizeOptions(width, height);
        Mockito.verify(mThumbnailProducers[0]).canProvideImageForSize(resizeOptions);
        Mockito.verify(mThumbnailProducers[1]).canProvideImageForSize(resizeOptions);
        Mockito.verify(mThumbnailProducers[2]).canProvideImageForSize(resizeOptions);
        Mockito.verifyNoMoreInteractions(((Object[]) (mThumbnailProducers)));
    }

    @Test
    public void testNullReturnedIfAllProducersFailOrReturnNullEndingWithNull() {
        int width = (ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0]) - 10;
        int height = (ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0]) - 10;
        mockRequestWithResizeOptions(width, height);
        mockProducersToProduce(null, ThumbnailBranchProducerTest.THROW_FAILURE, null);
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onNewResult(null, IS_LAST);
        verifyAllProducersRequestedForResults();
    }

    @Test
    public void testFailureReturnedIfAllProducersFailOrReturnNullEndingWithFailure() {
        int width = ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0];
        int height = ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0];
        mockRequestWithResizeOptions(width, height);
        mockProducersToProduce(null, null, ThumbnailBranchProducerTest.THROW_FAILURE);
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onFailure(ArgumentMatchers.any(Throwable.class));
        verifyAllProducersRequestedForResults();
    }

    @Test
    public void testFinalProducerUsedIfFirstTwoReturnTooSmallImages() {
        int desiredWidth = (ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0]) - 50;
        int desiredHeight = (ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0]) - 50;
        mockRequestWithResizeOptions(desiredWidth, desiredHeight);
        EncodedImage thirdImage = ThumbnailBranchProducerTest.mockEncodedImage(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[2], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[2], 0);
        mockProducersToProduce(ThumbnailBranchProducerTest.mockEncodedImage((desiredWidth / 2), (desiredHeight / 2), 0), ThumbnailBranchProducerTest.mockEncodedImage((desiredWidth / 2), (desiredHeight / 2), 0), thirdImage);
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onNewResult(thirdImage, IS_LAST);
        verifyAllProducersRequestedForResults();
    }

    @Test
    public void testSecondProducerUsedIfImageBigEnoughWhenRotated() {
        mockRequestWithResizeOptions(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[1], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[1]);
        EncodedImage secondImage = ThumbnailBranchProducerTest.mockEncodedImage((((ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[1]) * 3) / 4), (((ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[1]) * 3) / 4), 90);
        mockProducersToProduce(ThumbnailBranchProducerTest.mockEncodedImage(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0], 0), secondImage);
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onNewResult(secondImage, IS_LAST);
        Mockito.verifyZeroInteractions(mThumbnailProducers[2]);
    }

    @Test
    public void testNullReturnedIfLastImageNotBigEnoughWhenRotated() {
        mockRequestWithResizeOptions(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[2], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[2]);
        mockProducersToProduce(ThumbnailBranchProducerTest.mockEncodedImage(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[0], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[0], 0), ThumbnailBranchProducerTest.mockEncodedImage(ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[1], ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[1], 0), ThumbnailBranchProducerTest.mockEncodedImage(((ThumbnailBranchProducerTest.THUMBNAIL_HEIGHTS[2]) / 2), ((ThumbnailBranchProducerTest.THUMBNAIL_WIDTHS[2]) / 2), 90));
        mProducer.produceResults(mImageConsumer, mProducerContext);
        Mockito.verify(mImageConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mThumbnailProducers[2]).produceResults(ArgumentMatchers.any(Consumer.class), ArgumentMatchers.any(ProducerContext.class));
    }

    private interface ConsumerCallback {
        void callback(Consumer<EncodedImage> consumer);
    }
}

