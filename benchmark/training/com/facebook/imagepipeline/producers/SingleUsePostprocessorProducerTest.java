/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
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
import com.facebook.imagepipeline.producers.PostprocessorProducer.SingleUsePostprocessorConsumer;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.Postprocessor;
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
public class SingleUsePostprocessorProducerTest {
    private static final String POSTPROCESSOR_NAME = "postprocessor_name";

    private static final Map<String, String> mExtraMap = ImmutableMap.of(POSTPROCESSOR, SingleUsePostprocessorProducerTest.POSTPROCESSOR_NAME);

    @Mock
    public PlatformBitmapFactory mPlatformBitmapFactory;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Producer<CloseableReference<CloseableImage>> mInputProducer;

    @Mock
    public Consumer<CloseableReference<CloseableImage>> mConsumer;

    @Mock
    public Postprocessor mPostprocessor;

    @Mock
    public ResourceReleaser<Bitmap> mBitmapResourceReleaser;

    @Mock
    public ImageRequest mImageRequest;

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
        SingleUsePostprocessorConsumer postprocessorConsumer = produceResults();
        postprocessorConsumer.onNewResult(mSourceCloseableImageRef, NO_FLAGS);
        mSourceCloseableImageRef.close();
        mTestExecutorService.runUntilIdle();
        mInOrder.verifyNoMoreInteractions();
        Assert.assertEquals(0, mResults.size());
        Mockito.verify(mSourceCloseableStaticBitmap).close();
    }

    @Test
    public void testSuccess() {
        SingleUsePostprocessorConsumer postprocessorConsumer = produceResults();
        Mockito.doReturn(mDestinationCloseableBitmapRef).when(mPostprocessor).process(mSourceBitmap, mPlatformBitmapFactory);
        postprocessorConsumer.onNewResult(mSourceCloseableImageRef, IS_LAST);
        mSourceCloseableImageRef.close();
        mTestExecutorService.runUntilIdle();
        mInOrder.verify(mProducerListener).onProducerStart(mRequestId, NAME);
        mInOrder.verify(mPostprocessor).process(mSourceBitmap, mPlatformBitmapFactory);
        mInOrder.verify(mProducerListener).requiresExtraMap(mRequestId);
        mInOrder.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, NAME, SingleUsePostprocessorProducerTest.mExtraMap);
        mInOrder.verify(mConsumer).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.eq(IS_LAST));
        mInOrder.verifyNoMoreInteractions();
        Assert.assertEquals(1, mResults.size());
        CloseableReference<CloseableImage> res0 = mResults.get(0);
        Assert.assertTrue(CloseableReference.isValid(res0));
        Assert.assertSame(mDestinationBitmap, getUnderlyingBitmap());
        res0.close();
        Mockito.verify(mBitmapResourceReleaser).release(mDestinationBitmap);
        Mockito.verify(mSourceCloseableStaticBitmap).close();
    }

    @Test
    public void testFailure() {
        SingleUsePostprocessorConsumer postprocessorConsumer = produceResults();
        Mockito.doThrow(new RuntimeException()).when(mPostprocessor).process(ArgumentMatchers.eq(mSourceBitmap), ArgumentMatchers.eq(mPlatformBitmapFactory));
        postprocessorConsumer.onNewResult(mSourceCloseableImageRef, IS_LAST);
        mSourceCloseableImageRef.close();
        mTestExecutorService.runUntilIdle();
        mInOrder.verify(mProducerListener).onProducerStart(mRequestId, NAME);
        mInOrder.verify(mPostprocessor).process(mSourceBitmap, mPlatformBitmapFactory);
        mInOrder.verify(mProducerListener).requiresExtraMap(mRequestId);
        mInOrder.verify(mProducerListener).onProducerFinishWithFailure(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(NAME), ArgumentMatchers.any(RuntimeException.class), ArgumentMatchers.eq(SingleUsePostprocessorProducerTest.mExtraMap));
        mInOrder.verify(mConsumer).onFailure(ArgumentMatchers.any(IllegalStateException.class));
        mInOrder.verifyNoMoreInteractions();
        Assert.assertEquals(0, mResults.size());
        Mockito.verify(mSourceCloseableStaticBitmap).close();
    }
}

