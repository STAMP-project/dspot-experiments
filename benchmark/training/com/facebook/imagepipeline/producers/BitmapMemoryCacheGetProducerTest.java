/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import BitmapMemoryCacheProducer.EXTRA_CACHED_VALUE_FOUND;
import Consumer.IS_LAST;
import ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheKey;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static BitmapMemoryCacheGetProducer.PRODUCER_NAME;
import static Config.NONE;


/**
 * Checks basic properties of bitmap memory cache producer operation, that is:
 *   - it delegates to the {@link MemoryCache#get(Object)}.
 *   - if get is successful, then returned reference is closed.
 *   - if {@link MemoryCache#get(Object)} is unsuccessful, then it passes the
 *   request to the next producer in the sequence.
 *   - responses from the next producer are passed directly to the consumer.
 *   - listener methods are called as expected.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class BitmapMemoryCacheGetProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    @Mock
    public MemoryCache<CacheKey, CloseableImage> mMemoryCache;

    @Mock
    public CacheKeyFactory mCacheKeyFactory;

    @Mock
    public Producer<CloseableReference<CloseableImage>> mInputProducer;

    @Mock
    public Consumer<CloseableReference<CloseableImage>> mConsumer;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Exception mException;

    @Mock
    public BitmapMemoryCacheKey mCacheKey;

    @Mock
    public Object mCallerContext;

    private final String mRequestId = "mRequestId";

    private CloseableImage mCloseableImage1;

    private CloseableReference<CloseableImage> mFinalImageReference;

    private BitmapMemoryCacheGetProducer mBitmapMemoryCacheGetProducer;

    @Test
    public void testBitmapMemoryCacheGetSuccessful() {
        setupBitmapCacheGetSuccess();
        Mockito.when(mProducerContext.getLowestPermittedRequestLevel()).thenReturn(BITMAP_MEMORY_CACHE);
        mBitmapMemoryCacheGetProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalImageReference, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "true");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME, true);
        Assert.assertTrue((!(mFinalImageReference.isValid())));
    }

    @Test
    public void testBitmapMemoryCacheGetNotFoundInputProducerSuccess() {
        setupBitmapCacheGetNotFound();
        setupInputProducerStreamingSuccess();
        mBitmapMemoryCacheGetProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalImageReference, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testBitmapMemoryCacheGetNotFoundInputProducerNotFound() {
        setupBitmapCacheGetNotFound();
        setupInputProducerNotFound();
        mBitmapMemoryCacheGetProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testBitmapMemoryCacheGetNotFoundInputProducerFailure() {
        setupBitmapCacheGetNotFound();
        setupInputProducerFailure();
        mBitmapMemoryCacheGetProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testBitmapMemoryCacheGetNotFoundLowestLevelReached() {
        setupBitmapCacheGetNotFound();
        Mockito.when(mProducerContext.getLowestPermittedRequestLevel()).thenReturn(BITMAP_MEMORY_CACHE);
        mBitmapMemoryCacheGetProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME);
        Map<String, String> extraMap = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME, extraMap);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, BitmapMemoryCacheGetProducerTest.PRODUCER_NAME, false);
        Mockito.verifyNoMoreInteractions(mInputProducer);
    }

    private static class ProduceResultsNewResultAnswer implements Answer<Void> {
        private final CloseableReference<CloseableImage> mResult;

        private ProduceResultsNewResultAnswer(CloseableReference<CloseableImage> result) {
            mResult = result;
        }

        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            Consumer consumer = ((Consumer) (invocation.getArguments()[0]));
            consumer.onNewResult(mResult, IS_LAST);
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

