/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.DO_NOT_CACHE_ENCODED;
import Consumer.IS_LAST;
import Consumer.IS_PARTIAL_RESULT;
import Consumer.NO_FLAGS;
import Consumer.Status;
import EncodedMemoryCacheProducer.EXTRA_CACHED_VALUE_FOUND;
import ImageRequest.RequestLevel.ENCODED_MEMORY_CACHE;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.MultiCacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static Config.NONE;
import static EncodedMemoryCacheProducer.PRODUCER_NAME;


/**
 * Checks basic properties of encoded memory cache producer operation, that is:
 *   - it delegates to the {@link MemoryCache#get(Object)}
 *   - if {@link MemoryCache#get(Object)} is unsuccessful, then it passes the
 *   request to the next producer in the sequence.
 *   - if the next producer returns the value, then it is put into the disk cache.
 *   - responses from the next producer are passed back down to the consumer.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class EncodedMemoryCacheProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    @Mock
    public MemoryCache<CacheKey, PooledByteBuffer> mMemoryCache;

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
    public Object mCallerContext;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Exception mException;

    private MultiCacheKey mCacheKey;

    private PooledByteBuffer mPooledByteBuffer1;

    private PooledByteBuffer mPooledByteBuffer2;

    private CloseableReference<PooledByteBuffer> mFinalImageReference;

    private CloseableReference<PooledByteBuffer> mIntermediateImageReference;

    private CloseableReference<PooledByteBuffer> mFinalImageReferenceClone;

    private EncodedImage mFinalEncodedImage;

    private EncodedImage mFinalEncodedImageFormatUnknown;

    private EncodedImage mIntermediateEncodedImage;

    private EncodedImage mFinalEncodedImageClone;

    private EncodedMemoryCacheProducer mEncodedMemoryCacheProducer;

    private final String mRequestId = "mRequestId";

    @Test
    public void testDisableMemoryCache() {
        setupEncodedMemoryCacheGetNotFound();
        setupInputProducerStreamingSuccess();
        Mockito.when(mImageRequest.isMemoryCacheEnabled()).thenReturn(false);
        mEncodedMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mMemoryCache, Mockito.never()).cache(ArgumentMatchers.any(CacheKey.class), ArgumentMatchers.any(CloseableReference.class));
    }

    @Test
    public void testEncodedMemoryCacheGetSuccessful() {
        setupEncodedMemoryCacheGetSuccess();
        Mockito.when(mProducerContext.getLowestPermittedRequestLevel()).thenReturn(ENCODED_MEMORY_CACHE);
        mEncodedMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mConsumer).onNewResult(argumentCaptor.capture(), ArgumentMatchers.eq(IS_LAST));
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertSame(mFinalEncodedImage.getUnderlyingReferenceTestOnly(), encodedImage.getUnderlyingReferenceTestOnly());
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "true");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME, true);
        Assert.assertFalse(mFinalImageReference.isValid());
    }

    @Test
    public void testEncodedMemoryCacheGetNotFoundInputProducerSuccess() {
        setupEncodedMemoryCacheGetNotFound();
        setupInputProducerStreamingSuccess();
        mEncodedMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mMemoryCache, Mockito.never()).cache(mCacheKey, mIntermediateImageReference);
        ArgumentCaptor<CloseableReference> argumentCaptor = ArgumentCaptor.forClass(CloseableReference.class);
        Mockito.verify(mMemoryCache).cache(ArgumentMatchers.eq(mCacheKey), argumentCaptor.capture());
        CloseableReference<PooledByteBuffer> capturedRef = ((CloseableReference<PooledByteBuffer>) (argumentCaptor.getValue()));
        Assert.assertSame(mFinalImageReference.getUnderlyingReferenceTestOnly(), capturedRef.getUnderlyingReferenceTestOnly());
        Mockito.verify(mConsumer).onNewResult(mIntermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Assert.assertTrue(EncodedImage.isValid(mFinalEncodedImageClone));
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testEncodedMemoryCacheGetNotFoundInputProducerSuccessButResultNotCacheable() {
        testInputProducerSuccessButResultNotCacheableDueToStatusFlags(DO_NOT_CACHE_ENCODED);
    }

    @Test
    public void testEncodedMemoryCacheGetNotFoundInputProducerSuccessButResultIsPartial() {
        testInputProducerSuccessButResultNotCacheableDueToStatusFlags(IS_PARTIAL_RESULT);
    }

    @Test
    public void testEncodedMemoryCacheGetNotFoundInputProducerSuccessButResultIsUnknownFormat() {
        setupEncodedMemoryCacheGetNotFound();
        setupInputProducerStreamingSuccessFormatUnknown();
        mEncodedMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mMemoryCache, Mockito.never()).cache(ArgumentMatchers.any(CacheKey.class), ArgumentMatchers.any(CloseableReference.class));
        Mockito.verify(mConsumer).onNewResult(mIntermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImageFormatUnknown, IS_LAST);
        Assert.assertTrue(EncodedImage.isValid(mFinalEncodedImageClone));
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testEncodedMemoryCacheGetNotFoundInputProducerNotFound() {
        setupEncodedMemoryCacheGetNotFound();
        setupInputProducerNotFound();
        mEncodedMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testEncodedMemoryCacheGetNotFoundInputProducerFailure() {
        setupEncodedMemoryCacheGetNotFound();
        setupInputProducerFailure();
        mEncodedMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testEncodedMemoryCacheGetNotFoundLowestLevelReached() {
        setupEncodedMemoryCacheGetNotFound();
        Mockito.when(mProducerContext.getLowestPermittedRequestLevel()).thenReturn(ENCODED_MEMORY_CACHE);
        mEncodedMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, EncodedMemoryCacheProducerTest.PRODUCER_NAME, false);
        Mockito.verifyNoMoreInteractions(mInputProducer);
    }

    private static class ProduceResultsNewResultAnswer implements Answer<Void> {
        private final List<EncodedImage> mResults;

        @Consumer.Status
        private final int mExtraStatusFlags;

        private ProduceResultsNewResultAnswer(List<EncodedImage> results, int extraStatusFlags) {
            mResults = results;
            mExtraStatusFlags = extraStatusFlags;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Consumer consumer = ((Consumer) (invocation.getArguments()[0]));
            Iterator<EncodedImage> iterator = mResults.iterator();
            while (iterator.hasNext()) {
                EncodedImage result = iterator.next();
                consumer.onNewResult(result, ((BaseConsumer.simpleStatusForIsLast((!(iterator.hasNext())))) | (mExtraStatusFlags)));
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

