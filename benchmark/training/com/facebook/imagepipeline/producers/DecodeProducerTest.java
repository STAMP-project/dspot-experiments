/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import Consumer.IS_PLACEHOLDER;
import Consumer.NO_FLAGS;
import DecodeProducer.PRODUCER_NAME;
import DefaultImageFormats.WEBP_SIMPLE;
import EncodedImage.DEFAULT_SAMPLE_SIZE;
import ImmutableQualityInfo.FULL_QUALITY;
import JobScheduler.JobRunnable;
import android.media.ExifInterface;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegParser;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.Map;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@Config(manifest = Config.NONE)
@PrepareForTest({ JobScheduler.class, ProgressiveJpegParser.class, DecodeProducer.class })
public class DecodeProducerTest {
    private static final ImageDecodeOptions IMAGE_DECODE_OPTIONS = ImageDecodeOptions.newBuilder().setMinDecodeIntervalMs(100).build();

    private static final int PREVIEW_SCAN = 2;

    private static final int IGNORED_SCAN = 3;

    private static final int GOOD_ENOUGH_SCAN = 5;

    private static final int IMAGE_WIDTH = 200;

    private static final int IMAGE_HEIGHT = 100;

    private static final int IMAGE_SIZE = 1000;

    private static final int IMAGE_ROTATION_ANGLE = 0;

    private static final int IMAGE_EXIF_ORIENTATION = ExifInterface.ORIENTATION_NORMAL;

    private static final int MAX_BITMAP_SIZE = 2024;

    @Mock
    public ByteArrayPool mByteArrayPool;

    @Mock
    public Executor mExecutor;

    @Mock
    public ImageDecoder mImageDecoder;

    private ProgressiveJpegConfig mProgressiveJpegConfig;

    @Mock
    public Producer mInputProducer;

    private ImageRequest mImageRequest;

    private String mRequestId;

    private CloseableReference<PooledByteBuffer> mByteBufferRef;

    private EncodedImage mEncodedImage;

    @Mock
    public ProducerListener mProducerListener;

    private SettableProducerContext mProducerContext;

    @Mock
    public Consumer mConsumer;

    @Mock
    public ProgressiveJpegParser mProgressiveJpegParser;

    @Mock
    public JobScheduler mJobScheduler;

    private DecodeProducer mDecodeProducer;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Test
    public void testNewResult_Final() {
        setupNetworkUri();
        Consumer<EncodedImage> consumer = produceResults();
        Mockito.when(mJobScheduler.updateJob(mEncodedImage, IS_LAST)).thenReturn(true);
        consumer.onNewResult(mEncodedImage, IS_LAST);
        Mockito.verify(mJobScheduler).updateJob(mEncodedImage, IS_LAST);
        Mockito.verify(mJobScheduler).scheduleJob();
        Mockito.verifyZeroInteractions(mProgressiveJpegParser);
    }

    @Test
    public void testNewResult_Final_Local() {
        setupLocalUri();
        Consumer<EncodedImage> consumer = produceResults();
        Mockito.when(mJobScheduler.updateJob(mEncodedImage, IS_LAST)).thenReturn(true);
        consumer.onNewResult(mEncodedImage, IS_LAST);
        Mockito.verify(mJobScheduler).updateJob(mEncodedImage, IS_LAST);
        Mockito.verify(mJobScheduler).scheduleJob();
        Mockito.verifyZeroInteractions(mProgressiveJpegParser);
    }

    @Test
    public void testNewResult_Intermediate_NonJPEG() {
        mEncodedImage.setImageFormat(WEBP_SIMPLE);
        setupNetworkUri();
        Consumer<EncodedImage> consumer = produceResults();
        Mockito.when(mJobScheduler.updateJob(mEncodedImage, NO_FLAGS)).thenReturn(true);
        consumer.onNewResult(mEncodedImage, NO_FLAGS);
        InOrder inOrder = Mockito.inOrder(mJobScheduler);
        inOrder.verify(mJobScheduler).updateJob(mEncodedImage, NO_FLAGS);
        inOrder.verify(mJobScheduler).scheduleJob();
        Mockito.verifyZeroInteractions(mProgressiveJpegParser);
    }

    @Test
    public void testNewResult_Intermediate_Local() {
        setupLocalUri();
        Consumer<EncodedImage> consumer = produceResults();
        Mockito.when(mJobScheduler.updateJob(mEncodedImage, NO_FLAGS)).thenReturn(true);
        consumer.onNewResult(mEncodedImage, NO_FLAGS);
        Mockito.verify(mJobScheduler, Mockito.never()).updateJob(mEncodedImage, NO_FLAGS);
        Mockito.verify(mProgressiveJpegParser, Mockito.never()).parseMoreData(mEncodedImage);
        Mockito.verify(mJobScheduler, Mockito.never()).scheduleJob();
    }

    @Test
    public void testNewResult_Placeholder() {
        setupNetworkUri();
        Consumer<EncodedImage> consumer = produceResults();
        Mockito.when(mJobScheduler.updateJob(mEncodedImage, IS_PLACEHOLDER)).thenReturn(true);
        consumer.onNewResult(mEncodedImage, IS_PLACEHOLDER);
        Mockito.verify(mJobScheduler, Mockito.times(1)).updateJob(mEncodedImage, IS_PLACEHOLDER);
        Mockito.verify(mProgressiveJpegParser, Mockito.never()).parseMoreData(mEncodedImage);
        Mockito.verify(mJobScheduler, Mockito.times(1)).scheduleJob();
    }

    @Test
    public void testNewResult_Intermediate_pJPEG() {
        setupNetworkUri();
        Consumer<EncodedImage> consumer = produceResults();
        InOrder inOrder = Mockito.inOrder(mJobScheduler, mProgressiveJpegParser);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        // preview scan; schedule
        Mockito.when(mJobScheduler.updateJob(mEncodedImage, NO_FLAGS)).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.parseMoreData(ArgumentMatchers.any(EncodedImage.class))).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.getBestScanNumber()).thenReturn(DecodeProducerTest.PREVIEW_SCAN);
        consumer.onNewResult(mEncodedImage, NO_FLAGS);
        inOrder.verify(mJobScheduler).updateJob(mEncodedImage, NO_FLAGS);
        inOrder.verify(mProgressiveJpegParser).parseMoreData(argumentCaptor.capture());
        inOrder.verify(mJobScheduler).scheduleJob();
        Assert.assertSame(getUnderlyingReferenceTestOnly(), mByteBufferRef.getUnderlyingReferenceTestOnly());
        // no data parsed; ignore
        PooledByteBuffer pooledByteBuffer2 = DecodeProducerTest.mockPooledByteBuffer(210);
        CloseableReference<PooledByteBuffer> ref2 = CloseableReference.of(pooledByteBuffer2);
        EncodedImage encodedImage2 = DecodeProducerTest.mockEncodedJpeg(ref2);
        Mockito.when(mJobScheduler.updateJob(encodedImage2, NO_FLAGS)).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.parseMoreData(encodedImage2)).thenReturn(false);
        Mockito.when(mProgressiveJpegParser.getBestScanNumber()).thenReturn(DecodeProducerTest.PREVIEW_SCAN);
        consumer.onNewResult(encodedImage2, NO_FLAGS);
        inOrder.verify(mJobScheduler).updateJob(encodedImage2, NO_FLAGS);
        inOrder.verify(mProgressiveJpegParser).parseMoreData(argumentCaptor.capture());
        inOrder.verify(mJobScheduler, Mockito.never()).scheduleJob();
        Assert.assertSame(getUnderlyingReferenceTestOnly(), ref2.getUnderlyingReferenceTestOnly());
        // same scan; ignore
        PooledByteBuffer pooledByteBuffer3 = DecodeProducerTest.mockPooledByteBuffer(220);
        CloseableReference<PooledByteBuffer> ref3 = CloseableReference.of(pooledByteBuffer3);
        EncodedImage encodedImage3 = DecodeProducerTest.mockEncodedJpeg(ref3);
        Mockito.when(mJobScheduler.updateJob(encodedImage3, NO_FLAGS)).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.parseMoreData(encodedImage3)).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.getBestScanNumber()).thenReturn(DecodeProducerTest.PREVIEW_SCAN);
        consumer.onNewResult(encodedImage3, NO_FLAGS);
        inOrder.verify(mJobScheduler).updateJob(encodedImage3, NO_FLAGS);
        inOrder.verify(mProgressiveJpegParser).parseMoreData(argumentCaptor.capture());
        inOrder.verify(mJobScheduler, Mockito.never()).scheduleJob();
        Assert.assertSame(getUnderlyingReferenceTestOnly(), ref3.getUnderlyingReferenceTestOnly());
        // scan not for decode; ignore
        PooledByteBuffer pooledByteBuffer4 = DecodeProducerTest.mockPooledByteBuffer(300);
        CloseableReference<PooledByteBuffer> ref4 = CloseableReference.of(pooledByteBuffer4);
        EncodedImage encodedImage4 = DecodeProducerTest.mockEncodedJpeg(ref4);
        Mockito.when(mJobScheduler.updateJob(encodedImage4, NO_FLAGS)).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.parseMoreData(encodedImage4)).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.getBestScanNumber()).thenReturn(DecodeProducerTest.IGNORED_SCAN);
        consumer.onNewResult(encodedImage4, NO_FLAGS);
        inOrder.verify(mJobScheduler).updateJob(encodedImage4, NO_FLAGS);
        inOrder.verify(mProgressiveJpegParser).parseMoreData(argumentCaptor.capture());
        inOrder.verify(mJobScheduler, Mockito.never()).scheduleJob();
        Assert.assertSame(getUnderlyingReferenceTestOnly(), ref4.getUnderlyingReferenceTestOnly());
        // good-enough scan; schedule
        PooledByteBuffer pooledByteBuffer5 = DecodeProducerTest.mockPooledByteBuffer(500);
        CloseableReference<PooledByteBuffer> ref5 = CloseableReference.of(pooledByteBuffer5);
        EncodedImage encodedImage5 = DecodeProducerTest.mockEncodedJpeg(ref5);
        Mockito.when(mJobScheduler.updateJob(encodedImage5, NO_FLAGS)).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.parseMoreData(encodedImage5)).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.getBestScanNumber()).thenReturn(DecodeProducerTest.GOOD_ENOUGH_SCAN);
        consumer.onNewResult(encodedImage5, NO_FLAGS);
        inOrder.verify(mJobScheduler).updateJob(encodedImage5, NO_FLAGS);
        inOrder.verify(mProgressiveJpegParser).parseMoreData(argumentCaptor.capture());
        inOrder.verify(mJobScheduler).scheduleJob();
        Assert.assertSame(getUnderlyingReferenceTestOnly(), ref5.getUnderlyingReferenceTestOnly());
    }

    @Test
    public void testFailure() {
        setupNetworkUri();
        Consumer<EncodedImage> consumer = produceResults();
        Exception exception = Mockito.mock(Exception.class);
        consumer.onFailure(exception);
        Mockito.verify(mConsumer).onFailure(exception);
    }

    @Test
    public void testCancellation() {
        setupNetworkUri();
        Consumer<EncodedImage> consumer = produceResults();
        consumer.onCancellation();
        Mockito.verify(mConsumer).onCancellation();
    }

    @Test
    public void testDecode_Final() throws Exception {
        setupNetworkUri();
        produceResults();
        JobScheduler.JobRunnable jobRunnable = getJobRunnable();
        jobRunnable.run(mEncodedImage, IS_LAST);
        InOrder inOrder = Mockito.inOrder(mProducerListener, mImageDecoder);
        inOrder.verify(mProducerListener).onProducerStart(mRequestId, PRODUCER_NAME);
        inOrder.verify(mImageDecoder).decode(mEncodedImage, DecodeProducerTest.IMAGE_SIZE, FULL_QUALITY, DecodeProducerTest.IMAGE_DECODE_OPTIONS);
        inOrder.verify(mProducerListener).onProducerFinishWithSuccess(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(PRODUCER_NAME), ArgumentMatchers.any(Map.class));
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDecode_Intermediate_pJPEG() throws Exception {
        setupNetworkUri();
        produceResults();
        JobScheduler.JobRunnable jobRunnable = getJobRunnable();
        Mockito.when(mProgressiveJpegParser.isJpeg()).thenReturn(true);
        Mockito.when(mProgressiveJpegParser.getBestScanEndOffset()).thenReturn(200);
        Mockito.when(mProgressiveJpegParser.getBestScanNumber()).thenReturn(DecodeProducerTest.PREVIEW_SCAN);
        jobRunnable.run(mEncodedImage, NO_FLAGS);
        InOrder inOrder = Mockito.inOrder(mProducerListener, mImageDecoder);
        inOrder.verify(mProducerListener).onProducerStart(mRequestId, PRODUCER_NAME);
        inOrder.verify(mImageDecoder).decode(mEncodedImage, 200, ImmutableQualityInfo.of(DecodeProducerTest.PREVIEW_SCAN, false, false), DecodeProducerTest.IMAGE_DECODE_OPTIONS);
        inOrder.verify(mProducerListener).onProducerFinishWithSuccess(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(PRODUCER_NAME), ArgumentMatchers.any(Map.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testDecode_Failure() throws Exception {
        setupNetworkUri();
        produceResults();
        JobScheduler.JobRunnable jobRunnable = getJobRunnable();
        Exception exception = new RuntimeException();
        Mockito.when(mImageDecoder.decode(mEncodedImage, DecodeProducerTest.IMAGE_SIZE, FULL_QUALITY, DecodeProducerTest.IMAGE_DECODE_OPTIONS)).thenThrow(exception);
        jobRunnable.run(mEncodedImage, IS_LAST);
        InOrder inOrder = Mockito.inOrder(mProducerListener, mImageDecoder);
        inOrder.verify(mProducerListener).onProducerStart(mRequestId, PRODUCER_NAME);
        inOrder.verify(mImageDecoder).decode(mEncodedImage, DecodeProducerTest.IMAGE_SIZE, FULL_QUALITY, DecodeProducerTest.IMAGE_DECODE_OPTIONS);
        inOrder.verify(mProducerListener).onProducerFinishWithFailure(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(PRODUCER_NAME), ArgumentMatchers.eq(exception), ArgumentMatchers.any(Map.class));
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDecode_WhenSmartResizingEnabledAndLocalUri_ThenPerformDownsampling() throws Exception {
        int resizedWidth = 10;
        int resizedHeight = 10;
        setupLocalUri(ResizeOptions.forDimensions(resizedWidth, resizedHeight));
        produceResults();
        JobScheduler.JobRunnable jobRunnable = getJobRunnable();
        jobRunnable.run(mEncodedImage, IS_LAST);
        // The sample size was modified, which means Downsampling has been performed
        Assert.assertNotEquals(mEncodedImage.getSampleSize(), DEFAULT_SAMPLE_SIZE);
    }

    @Test
    public void testDecode_WhenSmartResizingEnabledAndNetworkUri_ThenPerformNoDownsampling() throws Exception {
        int resizedWidth = 10;
        int resizedHeight = 10;
        setupNetworkUri(ResizeOptions.forDimensions(resizedWidth, resizedHeight));
        produceResults();
        JobScheduler.JobRunnable jobRunnable = getJobRunnable();
        jobRunnable.run(mEncodedImage, IS_LAST);
        // The sample size was not modified, which means Downsampling has not been performed
        Assert.assertEquals(mEncodedImage.getSampleSize(), DEFAULT_SAMPLE_SIZE);
    }
}

