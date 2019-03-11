/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import BitmapMemoryCacheProducer.EXTRA_CACHED_VALUE_FOUND;
import Consumer.IS_LAST;
import Consumer.IS_PARTIAL_RESULT;
import Consumer.NO_FLAGS;
import Consumer.Status;
import ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE;
import ImmutableQualityInfo.FULL_QUALITY;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheKey;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static BitmapMemoryCacheProducer.PRODUCER_NAME;
import static Config.NONE;
import static Consumer.IS_LAST;
import static Consumer.IS_PARTIAL_RESULT;


/**
 * Checks basic properties of bitmap memory cache producer operation, that is:
 *   - it delegates to the {@link MemoryCache#get(Object)}
 *   - if {@link MemoryCache#get(Object)} is unsuccessful, then it passes the
 *   request to the next producer in the sequence.
 *   - if the next producer returns the value of higher quality,
 *   then it is put into the bitmap cache.
 *   - responses from the next producer are passed back down to the consumer.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class BitmapMemoryCacheProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    private static final int INTERMEDIATE_SCAN_1 = 2;

    private static final int INTERMEDIATE_SCAN_2 = 5;

    @Mock
    public MemoryCache<CacheKey, CloseableImage> mMemoryCache;

    @Mock
    public CacheKeyFactory mCacheKeyFactory;

    @Mock
    public Producer mInputProducer;

    @Mock
    public Consumer mConsumer;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Exception mException;

    private final String mRequestId = "mRequestId";

    private BitmapMemoryCacheKey mBitmapMemoryCacheKey;

    private CloseableImage mCloseableImage1;

    private CloseableImage mCloseableImage2;

    private CloseableReference<CloseableImage> mFinalImageReference;

    private CloseableReference<CloseableImage> mIntermediateImageReference;

    private CloseableReference<CloseableImage> mFinalImageReferenceClone;

    private CloseableReference<CloseableImage> mIntermediateImageReferenceClone;

    private BitmapMemoryCacheProducer mBitmapMemoryCacheProducer;

    @Test
    public void testDisableMemoryCache() {
        setupBitmapMemoryCacheGetNotFound();
        setupInputProducerStreamingSuccess();
        Mockito.when(mMemoryCache.get(mBitmapMemoryCacheKey)).thenReturn(null);
        Mockito.when(mImageRequest.isMemoryCacheEnabled()).thenReturn(false);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mMemoryCache, Mockito.never()).cache(ArgumentMatchers.any(BitmapMemoryCacheKey.class), ArgumentMatchers.any(CloseableReference.class));
    }

    @Test
    public void testBitmapMemoryCacheGetSuccessful() {
        setupBitmapMemoryCacheGetSuccess();
        Mockito.when(mProducerContext.getLowestPermittedRequestLevel()).thenReturn(BITMAP_MEMORY_CACHE);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalImageReference, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "true");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, true);
        Assert.assertTrue((!(mFinalImageReference.isValid())));
    }

    /**
     * Verify that stateful image results, both intermediate and final, are never cached.
     */
    @Test
    public void testDoNotCacheStatefulImage() {
        Mockito.when(mCloseableImage1.isStateful()).thenReturn(true);
        Mockito.when(mCloseableImage2.isStateful()).thenReturn(true);
        setupBitmapMemoryCacheGetNotFound();
        setupInputProducerStreamingSuccess();
        Mockito.when(mMemoryCache.get(mBitmapMemoryCacheKey)).thenReturn(null);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mIntermediateImageReference, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalImageReference, IS_LAST);
        Mockito.verify(mMemoryCache, Mockito.never()).cache(ArgumentMatchers.any(BitmapMemoryCacheKey.class), ArgumentMatchers.any(CloseableReference.class));
    }

    @Test
    public void testDoNotCachePartialResults() {
        setupBitmapMemoryCacheGetNotFound();
        setupInputProducerStreamingSuccessWithStatusFlags(IS_PARTIAL_RESULT);
        Mockito.when(mMemoryCache.get(mBitmapMemoryCacheKey)).thenReturn(null);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mIntermediateImageReference, IS_PARTIAL_RESULT);
        Mockito.verify(mConsumer).onNewResult(mFinalImageReference, ((IS_LAST) | (IS_PARTIAL_RESULT)));
        Mockito.verify(mMemoryCache, Mockito.never()).cache(ArgumentMatchers.any(BitmapMemoryCacheKey.class), ArgumentMatchers.any(CloseableReference.class));
    }

    @Test
    public void testBitmapMemoryCacheGetIntermediateImage() {
        setupBitmapMemoryCacheGetIntermediateImage();
        setupInputProducerNotFound();
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mIntermediateImageReference, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Assert.assertTrue((!(mIntermediateImageReference.isValid())));
    }

    @Test
    public void testCacheIntermediateImageAsNoImagePresent() {
        setupBitmapMemoryCacheGetNotFound();
        setupInputProducerStreamingSuccess();
        Mockito.when(mMemoryCache.get(mBitmapMemoryCacheKey)).thenReturn(null);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mMemoryCache).cache(mBitmapMemoryCacheKey, mIntermediateImageReference);
        Mockito.verify(mMemoryCache).cache(mBitmapMemoryCacheKey, mFinalImageReference);
        Mockito.verify(mConsumer).onNewResult(mIntermediateImageReferenceClone, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalImageReferenceClone, IS_LAST);
        Assert.assertTrue((!(mIntermediateImageReferenceClone.isValid())));
        Assert.assertTrue((!(mFinalImageReferenceClone.isValid())));
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testCacheIntermediateImageAsBetterScan() {
        setupInputProducerStreamingSuccess();
        CloseableImage closeableImage = Mockito.mock(CloseableImage.class);
        Mockito.when(closeableImage.getQualityInfo()).thenReturn(ImmutableQualityInfo.of(BitmapMemoryCacheProducerTest.INTERMEDIATE_SCAN_1, false, false));
        CloseableReference<CloseableImage> closeableImageRef = CloseableReference.of(closeableImage);
        setupBitmapMemoryCacheGetSuccessOnSecondRead(closeableImageRef);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mMemoryCache).cache(mBitmapMemoryCacheKey, mIntermediateImageReference);
        Mockito.verify(mMemoryCache).cache(mBitmapMemoryCacheKey, mFinalImageReference);
        Mockito.verify(mConsumer).onNewResult(mIntermediateImageReferenceClone, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalImageReferenceClone, IS_LAST);
        Assert.assertTrue((!(mIntermediateImageReferenceClone.isValid())));
        Assert.assertTrue((!(mFinalImageReferenceClone.isValid())));
        Assert.assertEquals(0, closeableImageRef.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDontCacheIntermediateImageAsAlreadyHaveSameQuality() {
        setupInputProducerStreamingSuccess();
        CloseableImage closeableImage = Mockito.mock(CloseableImage.class);
        Mockito.when(closeableImage.getQualityInfo()).thenReturn(ImmutableQualityInfo.of(BitmapMemoryCacheProducerTest.INTERMEDIATE_SCAN_2, true, false));
        CloseableReference<CloseableImage> closeableImageRef = CloseableReference.of(closeableImage);
        setupBitmapMemoryCacheGetSuccessOnSecondRead(closeableImageRef);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mMemoryCache, Mockito.never()).cache(mBitmapMemoryCacheKey, mIntermediateImageReference);
        Mockito.verify(mMemoryCache).cache(mBitmapMemoryCacheKey, mFinalImageReference);
        Mockito.verify(mConsumer).onNewResult(closeableImageRef, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalImageReferenceClone, IS_LAST);
        Assert.assertTrue((!(mFinalImageReferenceClone.isValid())));
        Assert.assertEquals(0, closeableImageRef.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDontCacheIntermediateImageAsAlreadyHaveFullQuality() {
        setupBitmapMemoryCacheGetNotFound();
        setupInputProducerStreamingSuccess();
        CloseableImage closeableImage = Mockito.mock(CloseableImage.class);
        Mockito.when(closeableImage.getQualityInfo()).thenReturn(FULL_QUALITY);
        CloseableReference<CloseableImage> closeableImageRef = CloseableReference.of(closeableImage);
        setupBitmapMemoryCacheGetSuccessOnSecondRead(closeableImageRef);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mMemoryCache, Mockito.never()).cache(mBitmapMemoryCacheKey, mIntermediateImageReference);
        Mockito.verify(mMemoryCache).cache(mBitmapMemoryCacheKey, mFinalImageReference);
        Mockito.verify(mConsumer).onNewResult(closeableImageRef, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalImageReferenceClone, IS_LAST);
        Assert.assertTrue((!(mFinalImageReferenceClone.isValid())));
        Assert.assertEquals(0, closeableImageRef.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testBitmapMemoryCacheGetNotFoundInputProducerNotFound() {
        setupBitmapMemoryCacheGetNotFound();
        setupInputProducerNotFound();
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testBitmapMemoryCacheGetNotFoundLowestLevelReached() {
        setupBitmapMemoryCacheGetNotFound();
        Mockito.when(mProducerContext.getLowestPermittedRequestLevel()).thenReturn(BITMAP_MEMORY_CACHE);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, false);
        Mockito.verifyNoMoreInteractions(mInputProducer);
    }

    @Test
    public void testBitmapMemoryCacheGetIntermediateImageLowestLevelReached() {
        setupBitmapMemoryCacheGetIntermediateImage();
        Mockito.when(mProducerContext.getLowestPermittedRequestLevel()).thenReturn(BITMAP_MEMORY_CACHE);
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mIntermediateImageReference, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, false);
        Assert.assertTrue((!(mIntermediateImageReference.isValid())));
        Mockito.verifyNoMoreInteractions(mInputProducer);
    }

    @Test
    public void testBitmapMemoryCacheGetNotFoundInputProducerFailure() {
        setupBitmapMemoryCacheGetNotFound();
        setupInputProducerFailure();
        mBitmapMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    private static class ProduceResultsNewResultAnswer implements Answer<Void> {
        private final int mStatusFlags;

        private final List<CloseableReference<CloseableImage>> mResults;

        private ProduceResultsNewResultAnswer(@Consumer.Status
        final int statusFlags, List<CloseableReference<CloseableImage>> results) {
            mStatusFlags = statusFlags;
            mResults = results;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Consumer consumer = ((Consumer) (invocation.getArguments()[0]));
            Iterator<CloseableReference<CloseableImage>> iterator = mResults.iterator();
            while (iterator.hasNext()) {
                CloseableReference<CloseableImage> result = iterator.next();
                consumer.onNewResult(result, (iterator.hasNext() ? mStatusFlags : (Consumer.IS_LAST) | (mStatusFlags)));
            } 
            return null;
        }
    }

    private class ProduceResultsFailureAnswer implements Answer<Void> {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Consumer consumer = ((Consumer) (invocation.getArguments()[0]));
            consumer.onFailure(mException);
            return null;
        }
    }
}

