/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import Consumer.NO_FLAGS;
import PostprocessedBitmapMemoryCacheProducer.VALUE_FOUND;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.Postprocessor;
import com.facebook.imagepipeline.request.RepeatedPostprocessor;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;
import static PostprocessedBitmapMemoryCacheProducer.PRODUCER_NAME;


/**
 * Checks basic properties of post-processed bitmap memory cache producer operation, that is:
 *   - it delegates to the {@link MemoryCache#get(Object)}
 *   - if {@link MemoryCache#get(Object)} is unsuccessful, then it passes the
 *   request to the next producer in the sequence.
 *   - if the next producer returns the value, then it is put into the bitmap cache.
 *   - responses from the next producer are passed back down to the consumer.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class PostprocessedBitmapMemoryCacheProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

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
    public Postprocessor mPostprocessor;

    @Mock
    public RepeatedPostprocessor mRepeatedPostprocessor;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Exception mException;

    @Mock
    public CloseableImage mImage1;

    @Mock
    public CloseableImage mImage2;

    private CloseableReference<CloseableImage> mImageRef1;

    private CloseableReference<CloseableImage> mImageRef2;

    private CloseableReference<CloseableImage> mImageRef1Clone;

    private CloseableReference<CloseableImage> mImageRef2Clone;

    private CacheKey mPostProcessorCacheKey;

    private CacheKey mPostprocessedBitmapCacheKey;

    private PostprocessedBitmapMemoryCacheProducer mMemoryCacheProducer;

    private final String mRequestId = "mRequestId";

    private final Map<String, String> mExtraOnHit = ImmutableMap.of(VALUE_FOUND, "true");

    private final Map<String, String> mExtraOnMiss = ImmutableMap.of(VALUE_FOUND, "false");

    @Test
    public void testDisableMemoryCache() {
        Mockito.when(mImageRequest.getPostprocessor()).thenReturn(mRepeatedPostprocessor);
        Mockito.when(mImageRequest.isMemoryCacheEnabled()).thenReturn(false);
        Consumer consumer = performCacheMiss();
        consumer.onNewResult(mImageRef1, NO_FLAGS);
        mImageRef1.close();
        Mockito.verify(mMemoryCache, Mockito.never()).cache(ArgumentMatchers.any(CacheKey.class), ArgumentMatchers.any(CloseableReference.class));
    }

    @Test
    public void testNoPostProcessor() {
        Mockito.when(mImageRequest.getPostprocessor()).thenReturn(null);
        mMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mInputProducer).produceResults(mConsumer, mProducerContext);
        Mockito.verifyNoMoreInteractions(mConsumer, mProducerListener);
    }

    @Test
    public void testNoPostProcessorCaching() {
        Mockito.when(mPostprocessor.getPostprocessorCacheKey()).thenReturn(null);
        mMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mInputProducer).produceResults(mConsumer, mProducerContext);
        Mockito.verifyNoMoreInteractions(mConsumer, mProducerListener);
    }

    @Test
    public void testCacheHit() {
        Mockito.when(mMemoryCache.get(mPostprocessedBitmapCacheKey)).thenReturn(mImageRef2Clone);
        mMemoryCacheProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mInputProducer, Mockito.never()).produceResults(ArgumentMatchers.any(Consumer.class), ArgumentMatchers.any(ProducerContext.class));
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, PostprocessedBitmapMemoryCacheProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, PostprocessedBitmapMemoryCacheProducerTest.PRODUCER_NAME, mExtraOnHit);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, PostprocessedBitmapMemoryCacheProducerTest.PRODUCER_NAME, true);
        Mockito.verify(mConsumer).onNewResult(mImageRef2Clone, IS_LAST);
        // reference must be closed after `consumer.onNewResult` returns
        Assert.assertFalse(mImageRef2Clone.isValid());
    }

    @Test
    public void testCacheMiss_UnderlyingNull() {
        Consumer consumer = performCacheMiss();
        consumer.onNewResult(null, IS_LAST);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
    }

    @Test
    public void testCacheMiss_UnderlyingSuccessCacheSucceeded() {
        Mockito.when(mMemoryCache.cache(mPostprocessedBitmapCacheKey, mImageRef2)).thenReturn(mImageRef2Clone);
        Consumer consumer = performCacheMiss();
        consumer.onNewResult(mImageRef1, NO_FLAGS);
        mImageRef1.close();
        consumer.onNewResult(mImageRef2, IS_LAST);
        mImageRef2.close();
        Mockito.verify(mConsumer).onNewResult(mImageRef2Clone, IS_LAST);
        // reference must be closed after `consumer.onNewResult` returns
        Assert.assertFalse(mImageRef2Clone.isValid());
    }

    @Test
    public void testCacheMiss_UnderlyingSuccessCacheFailed() {
        Mockito.when(mMemoryCache.cache(mPostprocessedBitmapCacheKey, mImageRef2)).thenReturn(null);
        Consumer consumer = performCacheMiss();
        consumer.onNewResult(mImageRef1, NO_FLAGS);
        mImageRef1.close();
        consumer.onNewResult(mImageRef2, IS_LAST);
        mImageRef2.close();
        Mockito.verify(mConsumer).onNewResult(mImageRef2, IS_LAST);
        // reference must be closed after `consumer.onNewResult` returns
        Assert.assertFalse(mImageRef2.isValid());
    }

    @Test
    public void testCacheMiss_UnderlyingFailure() {
        Consumer consumer = performCacheMiss();
        consumer.onFailure(mException);
        Mockito.verify(mConsumer).onFailure(mException);
    }

    @Test
    public void testRepeatedPostProcessor() {
        Mockito.when(mImageRequest.getPostprocessor()).thenReturn(mRepeatedPostprocessor);
        Mockito.when(mMemoryCache.cache(mPostprocessedBitmapCacheKey, mImageRef1)).thenReturn(mImageRef1Clone);
        Mockito.when(mMemoryCache.cache(mPostprocessedBitmapCacheKey, mImageRef2)).thenReturn(mImageRef2Clone);
        Consumer consumer = performCacheMiss();
        consumer.onNewResult(mImageRef1, NO_FLAGS);
        mImageRef1.close();
        consumer.onNewResult(mImageRef2, NO_FLAGS);
        mImageRef2.close();
        Mockito.verify(mConsumer).onNewResult(mImageRef1Clone, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mImageRef2Clone, NO_FLAGS);
        // reference must be closed after `consumer.onNewResult` returns
        Assert.assertFalse(mImageRef1Clone.isValid());
        Assert.assertFalse(mImageRef2Clone.isValid());
    }
}

