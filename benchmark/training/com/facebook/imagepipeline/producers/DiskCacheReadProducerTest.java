/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import DiskCacheReadProducer.ENCODED_IMAGE_SIZE;
import DiskCacheReadProducer.EXTRA_CACHED_VALUE_FOUND;
import ImageRequest.CacheChoice.SMALL;
import Task.TaskCompletionSource;
import com.facebook.cache.common.MultiCacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;
import static DiskCacheReadProducer.PRODUCER_NAME;


/**
 * Checks basic properties of disk cache producer operation, that is:
 *   - it delegates to the {@link BufferedDiskCache#get(CacheKey key, AtomicBoolean isCancelled)}
 *   - it returns a 'copy' of the cached value
 *   - if {@link BufferedDiskCache#get(CacheKey key, AtomicBoolean isCancelled)} is unsuccessful,
 *   then it passes the request to the next producer in the sequence.
 *   - if the next producer returns the value, then it is put into the disk cache.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class DiskCacheReadProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    private static final Map EXPECTED_MAP_ON_CACHE_HIT = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "true");

    private static final Map EXPECTED_MAP_ON_CACHE_MISS = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");

    @Mock
    public CacheKeyFactory mCacheKeyFactory;

    @Mock
    public Producer mInputProducer;

    @Mock
    public Consumer mConsumer;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public Object mCallerContext;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Exception mException;

    private final BufferedDiskCache mDefaultBufferedDiskCache = Mockito.mock(BufferedDiskCache.class);

    private final BufferedDiskCache mSmallImageBufferedDiskCache = Mockito.mock(BufferedDiskCache.class);

    private SettableProducerContext mProducerContext;

    private SettableProducerContext mLowestLevelProducerContext;

    private final String mRequestId = "mRequestId";

    private MultiCacheKey mCacheKey;

    private PooledByteBuffer mIntermediatePooledByteBuffer;

    private PooledByteBuffer mFinalPooledByteBuffer;

    private CloseableReference<PooledByteBuffer> mIntermediateImageReference;

    private CloseableReference<PooledByteBuffer> mFinalImageReference;

    private EncodedImage mIntermediateEncodedImage;

    private EncodedImage mFinalEncodedImage;

    private TaskCompletionSource mTaskCompletionSource;

    private ArgumentCaptor<AtomicBoolean> mIsCancelled;

    private DiskCacheReadProducer mDiskCacheReadProducer;

    @Test
    public void testStartInputProducerIfNotEnabled() {
        Mockito.when(mImageRequest.isDiskCacheEnabled()).thenReturn(false);
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mInputProducer).produceResults(mConsumer, mProducerContext);
        Mockito.verifyNoMoreInteractions(mConsumer, mProducerListener, mCacheKeyFactory, mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache);
    }

    @Test
    public void testNotEnabledAndLowestLevel() {
        Mockito.when(mImageRequest.isDiskCacheEnabled()).thenReturn(false);
        mDiskCacheReadProducer.produceResults(mConsumer, mLowestLevelProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verifyNoMoreInteractions(mProducerListener, mInputProducer, mCacheKeyFactory, mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache);
    }

    @Test
    public void testDefaultDiskCacheGetSuccessful() {
        setupDiskCacheGetSuccess(mDefaultBufferedDiskCache);
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        ArgumentCaptor<HashMap> captor = ArgumentCaptor.forClass(HashMap.class);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(DiskCacheReadProducerTest.PRODUCER_NAME), captor.capture());
        Map<String, String> resultMap = captor.getValue();
        Assert.assertEquals("true", resultMap.get(EXTRA_CACHED_VALUE_FOUND));
        Assert.assertEquals("0", resultMap.get(ENCODED_IMAGE_SIZE));
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, true);
        Assert.assertFalse(EncodedImage.isValid(mFinalEncodedImage));
    }

    @Test
    public void testSmallImageDiskCacheGetSuccessful() {
        Mockito.when(mImageRequest.getCacheChoice()).thenReturn(SMALL);
        setupDiskCacheGetSuccess(mSmallImageBufferedDiskCache);
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        ArgumentCaptor<HashMap> captor = ArgumentCaptor.forClass(HashMap.class);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(DiskCacheReadProducerTest.PRODUCER_NAME), captor.capture());
        Map<String, String> resultMap = captor.getValue();
        Assert.assertEquals("true", resultMap.get(EXTRA_CACHED_VALUE_FOUND));
        Assert.assertEquals("0", resultMap.get(ENCODED_IMAGE_SIZE));
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, true);
        Assert.assertFalse(EncodedImage.isValid(mFinalEncodedImage));
    }

    @Test
    public void testDiskCacheGetSuccessfulNoExtraMap() {
        setupDiskCacheGetSuccess(mDefaultBufferedDiskCache);
        Mockito.when(mProducerListener.requiresExtraMap(mRequestId)).thenReturn(false);
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, true);
        Assert.assertFalse(EncodedImage.isValid(mFinalEncodedImage));
    }

    @Test
    public void testDiskCacheGetSuccessfulLowestLevelReached() {
        setupDiskCacheGetSuccess(mDefaultBufferedDiskCache);
        Mockito.when(mProducerListener.requiresExtraMap(mRequestId)).thenReturn(false);
        mDiskCacheReadProducer.produceResults(mConsumer, mLowestLevelProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, true);
        Assert.assertFalse(EncodedImage.isValid(mFinalEncodedImage));
    }

    @Test
    public void testDiskCacheGetFailureInputProducerSuccess() {
        setupDiskCacheGetFailure(mDefaultBufferedDiskCache);
        setupInputProducerSuccess();
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, mException, null);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDiskCacheGetFailureInputProducerNotFound() {
        setupDiskCacheGetFailure(mDefaultBufferedDiskCache);
        setupInputProducerNotFound();
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
    }

    @Test
    public void testDiskCacheGetFailureInputProducerFailure() {
        setupDiskCacheGetFailure(mDefaultBufferedDiskCache);
        setupInputProducerFailure();
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, mException, null);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDiskCacheGetFailureLowestLevelReached() {
        setupDiskCacheGetFailure(mDefaultBufferedDiskCache);
        mDiskCacheReadProducer.produceResults(mConsumer, mLowestLevelProducerContext);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, mException, null);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.verify(mInputProducer).produceResults(mConsumer, mLowestLevelProducerContext);
    }

    @Test
    public void testDefaultDiskCacheGetNotFoundInputProducerSuccess() {
        setupDiskCacheGetNotFound(mDefaultBufferedDiskCache);
        setupInputProducerSuccess();
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mInputProducer).produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        ArgumentCaptor<HashMap> captor = ArgumentCaptor.forClass(HashMap.class);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(DiskCacheReadProducerTest.PRODUCER_NAME), captor.capture());
        Map<String, String> resultMap = captor.getValue();
        Assert.assertEquals("false", resultMap.get(EXTRA_CACHED_VALUE_FOUND));
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Assert.assertNull(resultMap.get(ENCODED_IMAGE_SIZE));
    }

    @Test
    public void testSmallImageDiskCacheGetNotFoundInputProducerSuccess() {
        Mockito.when(mImageRequest.getCacheChoice()).thenReturn(SMALL);
        setupDiskCacheGetNotFound(mSmallImageBufferedDiskCache);
        setupInputProducerSuccess();
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        ArgumentCaptor<HashMap> captor = ArgumentCaptor.forClass(HashMap.class);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(DiskCacheReadProducerTest.PRODUCER_NAME), captor.capture());
        Map<String, String> resultMap = captor.getValue();
        Assert.assertEquals("false", resultMap.get(EXTRA_CACHED_VALUE_FOUND));
        Assert.assertNull(resultMap.get(ENCODED_IMAGE_SIZE));
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDiskCacheGetNotFoundInputProducerSuccessNoExtraMap() {
        setupDiskCacheGetNotFound(mDefaultBufferedDiskCache);
        setupInputProducerSuccess();
        Mockito.when(mProducerListener.requiresExtraMap(mRequestId)).thenReturn(false);
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mInputProducer).produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDiskCacheGetNotFoundInputProducerNotFound() {
        setupDiskCacheGetNotFound(mDefaultBufferedDiskCache);
        setupInputProducerNotFound();
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
    }

    @Test
    public void testDiskCacheGetNotFoundInputProducerFailure() {
        setupDiskCacheGetNotFound(mDefaultBufferedDiskCache);
        setupInputProducerFailure();
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        ArgumentCaptor<HashMap> captor = ArgumentCaptor.forClass(HashMap.class);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.eq(DiskCacheReadProducerTest.PRODUCER_NAME), captor.capture());
        Map<String, String> resultMap = captor.getValue();
        Assert.assertEquals("false", resultMap.get(EXTRA_CACHED_VALUE_FOUND));
        Assert.assertNull(resultMap.get(ENCODED_IMAGE_SIZE));
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDiskCacheGetNotFoundLowestLevelReached() {
        setupDiskCacheGetNotFound(mDefaultBufferedDiskCache);
        Mockito.when(mProducerListener.requiresExtraMap(mRequestId)).thenReturn(false);
        mDiskCacheReadProducer.produceResults(mConsumer, mLowestLevelProducerContext);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.verify(mInputProducer).produceResults(mConsumer, mLowestLevelProducerContext);
    }

    @Test
    public void testGetExtraMap() {
        final Map<String, String> trueValue = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "true", ENCODED_IMAGE_SIZE, "123");
        Assert.assertEquals(trueValue, DiskCacheReadProducer.getExtraMap(mProducerListener, mRequestId, true, 123));
        final Map<String, String> falseValue = ImmutableMap.of(EXTRA_CACHED_VALUE_FOUND, "false");
        Assert.assertEquals(falseValue, DiskCacheReadProducer.getExtraMap(mProducerListener, mRequestId, false, 0));
    }

    @Test
    public void testDiskCacheGetCancelled() {
        setupDiskCacheGetWait(mDefaultBufferedDiskCache);
        mDiskCacheReadProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer, Mockito.never()).onCancellation();
        Assert.assertFalse(mIsCancelled.getValue().get());
        mProducerContext.cancel();
        Assert.assertTrue(mIsCancelled.getValue().get());
        mTaskCompletionSource.trySetCancelled();
        Mockito.verify(mConsumer).onCancellation();
        Mockito.verify(mInputProducer, Mockito.never()).produceResults(ArgumentMatchers.any(Consumer.class), ArgumentMatchers.eq(mProducerContext));
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithCancellation(mRequestId, DiskCacheReadProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener, Mockito.never()).onProducerFinishWithFailure(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Exception.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(mProducerListener, Mockito.never()).onProducerFinishWithSuccess(ArgumentMatchers.eq(mRequestId), ArgumentMatchers.any(String.class), ArgumentMatchers.any(Map.class));
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }
}

