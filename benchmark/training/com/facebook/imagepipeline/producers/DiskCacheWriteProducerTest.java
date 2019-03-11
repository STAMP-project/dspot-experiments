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
import ImageRequest.CacheChoice.SMALL;
import com.facebook.cache.common.MultiCacheKey;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static Consumer.DO_NOT_CACHE_ENCODED;
import static Consumer.IS_LAST;
import static Consumer.IS_PARTIAL_RESULT;
import static DiskCacheWriteProducer.PRODUCER_NAME;


/**
 * Checks basic properties of disk cache producer operation, that is:
 *   - it delegates to the {@link BufferedDiskCache#get(CacheKey key, AtomicBoolean isCancelled)}
 *   - it returns a 'copy' of the cached value
 *   - if {@link BufferedDiskCache#get(CacheKey key, AtomicBoolean isCancelled)} is unsuccessful,
 *   then it passes the request to the next producer in the sequence.
 *   - if the next producer returns the value, then it is put into the disk cache.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class DiskCacheWriteProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

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

    private EncodedImage mFinalEncodedImageFormatUnknown;

    private EncodedImage mFinalEncodedImage;

    private DiskCacheWriteProducer mDiskCacheWriteProducer;

    @Test
    public void testDefaultDiskCacheInputProducerSuccess() {
        setupInputProducerSuccess();
        mDiskCacheWriteProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mDefaultBufferedDiskCache, Mockito.never()).put(mCacheKey, mIntermediateEncodedImage);
        ArgumentCaptor<EncodedImage> argumentCaptor = ArgumentCaptor.forClass(EncodedImage.class);
        Mockito.verify(mDefaultBufferedDiskCache).put(ArgumentMatchers.eq(mCacheKey), argumentCaptor.capture());
        EncodedImage encodedImage = argumentCaptor.getValue();
        Assert.assertSame(encodedImage.getByteBufferRef().getUnderlyingReferenceTestOnly(), mFinalImageReference.getUnderlyingReferenceTestOnly());
        Mockito.verify(mConsumer).onNewResult(mIntermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verifyZeroInteractions(mProducerListener);
    }

    @Test
    public void testSmallImageDiskCacheInputProducerSuccess() {
        Mockito.when(mImageRequest.getCacheChoice()).thenReturn(SMALL);
        setupInputProducerSuccess();
        mDiskCacheWriteProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mSmallImageBufferedDiskCache, Mockito.never()).put(mCacheKey, mIntermediateEncodedImage);
        Mockito.verify(mSmallImageBufferedDiskCache).put(mCacheKey, mFinalEncodedImage);
        Mockito.verify(mConsumer).onNewResult(mIntermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImage, IS_LAST);
        Mockito.verifyZeroInteractions(mProducerListener);
    }

    @Test
    public void testSmallImageDiskCacheInputProducerUnknownFormat() {
        Mockito.when(mImageRequest.getCacheChoice()).thenReturn(SMALL);
        setupInputProducerSuccessFormatUnknown();
        mDiskCacheWriteProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mSmallImageBufferedDiskCache, Mockito.never()).put(mCacheKey, mIntermediateEncodedImage);
        Mockito.verify(mSmallImageBufferedDiskCache, Mockito.never()).put(mCacheKey, mFinalEncodedImageFormatUnknown);
        Mockito.verify(mConsumer).onNewResult(mIntermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImageFormatUnknown, IS_LAST);
        Mockito.verifyZeroInteractions(mProducerListener);
    }

    @Test
    public void testDoesNotWriteToCacheIfPartialResult() {
        setupInputProducerSuccessWithStatusFlags(IS_PARTIAL_RESULT, mFinalEncodedImageFormatUnknown);
        mDiskCacheWriteProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mIntermediateEncodedImage, IS_PARTIAL_RESULT);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImageFormatUnknown, ((IS_LAST) | (IS_PARTIAL_RESULT)));
        Mockito.verifyZeroInteractions(mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache);
    }

    @Test
    public void testInputProducerNotFound() {
        setupInputProducerNotFound();
        mDiskCacheWriteProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verifyZeroInteractions(mProducerListener, mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache);
    }

    @Test
    public void testInputProducerFailure() {
        setupInputProducerFailure();
        mDiskCacheWriteProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verifyZeroInteractions(mProducerListener, mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache);
    }

    @Test
    public void testDoesNotWriteResultToCacheIfNotEnabled() {
        Mockito.when(mImageRequest.isDiskCacheEnabled()).thenReturn(false);
        setupInputProducerSuccessFormatUnknown();
        mDiskCacheWriteProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mIntermediateEncodedImage, NO_FLAGS);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImageFormatUnknown, IS_LAST);
        Mockito.verifyNoMoreInteractions(mProducerListener, mCacheKeyFactory, mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache);
    }

    @Test
    public void testDoesNotWriteResultToCacheIfResultStatusSaysNotTo() {
        setupInputProducerSuccessWithStatusFlags(DO_NOT_CACHE_ENCODED, mFinalEncodedImageFormatUnknown);
        mDiskCacheWriteProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mConsumer).onNewResult(mIntermediateEncodedImage, DO_NOT_CACHE_ENCODED);
        Mockito.verify(mConsumer).onNewResult(mFinalEncodedImageFormatUnknown, ((IS_LAST) | (DO_NOT_CACHE_ENCODED)));
        Mockito.verifyNoMoreInteractions(mProducerListener, mCacheKeyFactory, mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache);
    }

    @Test
    public void testLowestLevelReached() {
        Mockito.when(mProducerListener.requiresExtraMap(mRequestId)).thenReturn(false);
        mDiskCacheWriteProducer.produceResults(mConsumer, mLowestLevelProducerContext);
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verifyZeroInteractions(mInputProducer, mDefaultBufferedDiskCache, mSmallImageBufferedDiskCache, mProducerListener);
    }
}

