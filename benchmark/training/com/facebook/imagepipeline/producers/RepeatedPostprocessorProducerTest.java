/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.NO_FLAGS;
import PostprocessorProducer.NAME;
import PostprocessorProducer.POSTPROCESSOR;
import android.graphics.Bitmap;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.producers.PostprocessorProducer.RepeatedPostprocessorConsumer;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.RepeatedPostprocessor;
import com.facebook.imagepipeline.request.RepeatedPostprocessorRunner;
import com.facebook.imagepipeline.testing.TestExecutorService;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class RepeatedPostprocessorProducerTest {
    private static final String POSTPROCESSOR_NAME = "postprocessor_name";

    private static final Map<String, String> mExtraMap = ImmutableMap.of(POSTPROCESSOR, RepeatedPostprocessorProducerTest.POSTPROCESSOR_NAME);

    @Mock
    public PlatformBitmapFactory mPlatformBitmapFactory;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Producer<CloseableReference<CloseableImage>> mInputProducer;

    @Mock
    public Consumer<CloseableReference<CloseableImage>> mConsumer;

    @Mock
    public RepeatedPostprocessor mPostprocessor;

    @Mock
    public ResourceReleaser<Bitmap> mBitmapResourceReleaser;

    @Mock
    public ImageRequest mImageRequest;

    private SettableProducerContext mProducerContext;

    private String mRequestId = "mRequestId";

    private Bitmap mSourceBitmap;

    private CloseableStaticBitmap mSourceCloseableStaticBitmap;

    private CloseableReference<CloseableImage> mSourceCloseableImageRef;

    private Bitmap mDestinationBitmap;

    private CloseableReference<Bitmap> mDestinationCloseableBitmapRef;

    private TestExecutorService mTestExecutorService;

    private PostprocessorProducer mPostprocessorProducer;

    private List<CloseableReference<CloseableImage>> mResults;

    private InOrder mInOrder;

    @Test
    public void testIntermediateImageIsNotProcessed() {
        RepeatedPostprocessorConsumer postprocessorConsumer = produceResults();
        RepeatedPostprocessorRunner repeatedPostprocessorRunner = getRunner();
        setupNewSourceImage();
        postprocessorConsumer.onNewResult(mSourceCloseableImageRef, NO_FLAGS);
        mSourceCloseableImageRef.close();
        mTestExecutorService.runUntilIdle();
        mInOrder.verifyNoMoreInteractions();
        Assert.assertEquals(0, mResults.size());
        Mockito.verify(mSourceCloseableStaticBitmap).close();
        // final result should be post-processed
        performNewResult(postprocessorConsumer, true);
        verifyNewResultProcessed(0);
        performCancelAndVerifyOnCancellation();
        Mockito.verify(mSourceCloseableStaticBitmap).close();
    }

    @Test
    public void testPostprocessSuccessful() {
        RepeatedPostprocessorConsumer postprocessorConsumer = produceResults();
        RepeatedPostprocessorRunner repeatedPostprocessorRunner = getRunner();
        performNewResult(postprocessorConsumer, true);
        verifyNewResultProcessed(0);
        performCancelAndVerifyOnCancellation();
        Mockito.verify(mSourceCloseableStaticBitmap).close();
    }

    @Test
    public void testMultiplePostprocessThenClose() {
        RepeatedPostprocessorConsumer postprocessorConsumer = produceResults();
        RepeatedPostprocessorRunner repeatedPostprocessorRunner = getRunner();
        performNewResult(postprocessorConsumer, true);
        verifyNewResultProcessed(0);
        performUpdate(repeatedPostprocessorRunner, true);
        verifyNewResultProcessed(1);
        performUpdate(repeatedPostprocessorRunner, true);
        verifyNewResultProcessed(2);
        performUpdate(repeatedPostprocessorRunner, true);
        verifyNewResultProcessed(3);
        performCancelAndVerifyOnCancellation();
        Mockito.verify(mSourceCloseableStaticBitmap).close();
        // Can't update now that the request is cancelled.
        performUpdate(repeatedPostprocessorRunner, true);
        mInOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCannotPostprocessAfterPostprocessFailure() {
        RepeatedPostprocessorConsumer postprocessorConsumer = produceResults();
        RepeatedPostprocessorRunner repeatedPostprocessorRunner = getRunner();
        performNewResult(postprocessorConsumer, true);
        verifyNewResultProcessed(0);
        performFailure(repeatedPostprocessorRunner);
        mInOrder.verify(mProducerListener).onProducerStart(mRequestId, NAME);
        mInOrder.verify(mPostprocessor).process(mSourceBitmap, mPlatformBitmapFactory);
        mInOrder.verify(mProducerListener).requiresExtraMap(mRequestId);
        mInOrder.verify(mProducerListener).onProducerFinishWithFailure(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(NAME), ArgumentMatchers.any(RuntimeException.class), ArgumentMatchers.eq(RepeatedPostprocessorProducerTest.mExtraMap));
        mInOrder.verify(mConsumer).onFailure(ArgumentMatchers.any(RuntimeException.class));
        mInOrder.verifyNoMoreInteractions();
        // Can't update now that the request has failed.
        performUpdate(repeatedPostprocessorRunner, true);
        mInOrder.verifyNoMoreInteractions();
        performCancelAfterFinished();
        Mockito.verify(mSourceCloseableStaticBitmap).close();
    }

    @Test
    public void testUpdateBeforeNewResultDoesNothing() {
        RepeatedPostprocessorConsumer postprocessorConsumer = produceResults();
        RepeatedPostprocessorRunner repeatedPostprocessorRunner = getRunner();
        performUpdate(repeatedPostprocessorRunner, true);
        performUpdate(repeatedPostprocessorRunner, true);
        mInOrder.verifyNoMoreInteractions();
        performNewResult(postprocessorConsumer, true);
        verifyNewResultProcessed(0);
    }

    @Test
    public void runPostprocessAgainWhenDirty() {
        final RepeatedPostprocessorConsumer postprocessorConsumer = produceResults();
        final RepeatedPostprocessorRunner repeatedPostprocessorRunner = getRunner();
        performNewResult(postprocessorConsumer, false);
        Bitmap destBitmap0 = mDestinationBitmap;
        performUpdateDuringTheNextPostprocessing(repeatedPostprocessorRunner);
        mTestExecutorService.runNextPendingCommand();
        verifyNewResultProcessed(0, destBitmap0);
        Bitmap destBitmap1 = mDestinationBitmap;
        performUpdateDuringTheNextPostprocessing(repeatedPostprocessorRunner);
        mTestExecutorService.runNextPendingCommand();
        verifyNewResultProcessed(1, destBitmap1);
        Bitmap destBitmap2 = mDestinationBitmap;
        mTestExecutorService.runNextPendingCommand();
        verifyNewResultProcessed(2, destBitmap2);
    }
}

