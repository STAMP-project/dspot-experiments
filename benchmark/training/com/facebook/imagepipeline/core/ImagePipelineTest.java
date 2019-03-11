/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.core;


import ImageRequest.CacheChoice.DEFAULT;
import ImageRequest.CacheChoice.SMALL;
import ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE;
import ImageRequest.RequestLevel.DISK_CACHE;
import ImageRequest.RequestLevel.FULL_FETCH;
import Priority.HIGH;
import Priority.MEDIUM;
import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.MultiCacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.common.internal.Predicate;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheKey;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue;
import com.facebook.imagepipeline.request.ImageRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for ImagePipeline
 */
@RunWith(RobolectricTestRunner.class)
public class ImagePipelineTest {
    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public ProducerSequenceFactory mProducerSequenceFactory;

    @Mock
    public CacheKeyFactory mCacheKeyFactory;

    @Mock
    public Object mCallerContext;

    private Supplier<Boolean> mPrefetchEnabledSupplier;

    private Supplier<Boolean> mSuppressBitmapPrefetchingSupplier;

    private Supplier<Boolean> mLazyDataSourceSupplier;

    private ImagePipeline mImagePipeline;

    private MemoryCache<CacheKey, CloseableImage> mBitmapMemoryCache;

    private MemoryCache<CacheKey, PooledByteBuffer> mEncodedMemoryCache;

    private BufferedDiskCache mMainDiskStorageCache;

    private BufferedDiskCache mSmallImageDiskStorageCache;

    private RequestListener mRequestListener1;

    private RequestListener mRequestListener2;

    private ThreadHandoffProducerQueue mThreadHandoffProducerQueue;

    @Test
    public void testPrefetchToDiskCacheWithPrefetchDisabled() {
        Mockito.when(mPrefetchEnabledSupplier.get()).thenReturn(false);
        DataSource<Void> dataSource = mImagePipeline.prefetchToDiskCache(mImageRequest, mCallerContext);
        Assert.assertTrue(dataSource.hasFailed());
        Mockito.verifyNoMoreInteractions(mProducerSequenceFactory, mRequestListener1, mRequestListener2);
    }

    @Test
    public void testPrefetchToBitmapCacheWithPrefetchDisabled() {
        Mockito.when(mPrefetchEnabledSupplier.get()).thenReturn(false);
        DataSource<Void> dataSource = mImagePipeline.prefetchToBitmapCache(mImageRequest, mCallerContext);
        Assert.assertTrue(dataSource.hasFailed());
        Mockito.verifyNoMoreInteractions(mProducerSequenceFactory, mRequestListener1, mRequestListener2);
    }

    @Test
    public void testPrefetchToBitmapCacheWithBitmapPrefetcherSuppressed() {
        Producer<Void> prefetchProducerSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getEncodedImagePrefetchProducerSequence(mImageRequest)).thenReturn(prefetchProducerSequence);
        Mockito.when(mSuppressBitmapPrefetchingSupplier.get()).thenReturn(true);
        DataSource<Void> dataSource = mImagePipeline.prefetchToBitmapCache(mImageRequest, mCallerContext);
        verifyPrefetchToDiskCache(dataSource, prefetchProducerSequence, MEDIUM);
    }

    @Test
    public void testPrefetchToDiskCacheDefaultPriority() {
        Producer<Void> prefetchProducerSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getEncodedImagePrefetchProducerSequence(mImageRequest)).thenReturn(prefetchProducerSequence);
        DataSource<Void> dataSource = mImagePipeline.prefetchToDiskCache(mImageRequest, mCallerContext);
        verifyPrefetchToDiskCache(dataSource, prefetchProducerSequence, MEDIUM);
    }

    @Test
    public void testPrefetchToDiskCacheCustomPriority() {
        Producer<Void> prefetchProducerSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getEncodedImagePrefetchProducerSequence(mImageRequest)).thenReturn(prefetchProducerSequence);
        DataSource<Void> dataSource = mImagePipeline.prefetchToDiskCache(mImageRequest, mCallerContext, MEDIUM);
        verifyPrefetchToDiskCache(dataSource, prefetchProducerSequence, MEDIUM);
    }

    @Test
    public void testPrefetchToBitmapCache() {
        Producer<Void> prefetchProducerSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getDecodedImagePrefetchProducerSequence(mImageRequest)).thenReturn(prefetchProducerSequence);
        DataSource<Void> dataSource = mImagePipeline.prefetchToBitmapCache(mImageRequest, mCallerContext);
        Assert.assertTrue((!(dataSource.isFinished())));
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "0", true);
        Mockito.verify(mRequestListener2).onRequestStart(mImageRequest, mCallerContext, "0", true);
        ArgumentCaptor<ProducerContext> producerContextArgumentCaptor = ArgumentCaptor.forClass(ProducerContext.class);
        Mockito.verify(prefetchProducerSequence).produceResults(ArgumentMatchers.any(Consumer.class), producerContextArgumentCaptor.capture());
        Assert.assertFalse(producerContextArgumentCaptor.getValue().isIntermediateResultExpected());
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getPriority(), MEDIUM);
    }

    @Test
    public void testFetchLocalEncodedImage() {
        Producer<CloseableReference<PooledByteBuffer>> encodedSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getEncodedImageProducerSequence(mImageRequest)).thenReturn(encodedSequence);
        Mockito.when(mImageRequest.getSourceUri()).thenReturn(Uri.parse("file:///local/file"));
        DataSource<CloseableReference<PooledByteBuffer>> dataSource = mImagePipeline.fetchEncodedImage(mImageRequest, mCallerContext);
        Assert.assertFalse(dataSource.isFinished());
        ArgumentCaptor<ImageRequest> argumentCaptor = ArgumentCaptor.forClass(ImageRequest.class);
        Mockito.verify(mRequestListener1).onRequestStart(argumentCaptor.capture(), ArgumentMatchers.eq(mCallerContext), ArgumentMatchers.eq("0"), ArgumentMatchers.eq(false));
        ImageRequest capturedImageRequest = argumentCaptor.getValue();
        Assert.assertSame(mImageRequest.getSourceUri(), capturedImageRequest.getSourceUri());
        Mockito.verify(mRequestListener2).onRequestStart(argumentCaptor.capture(), ArgumentMatchers.eq(mCallerContext), ArgumentMatchers.eq("0"), ArgumentMatchers.eq(false));
        capturedImageRequest = argumentCaptor.getValue();
        Assert.assertSame(mImageRequest.getSourceUri(), capturedImageRequest.getSourceUri());
        ArgumentCaptor<ProducerContext> producerContextArgumentCaptor = ArgumentCaptor.forClass(ProducerContext.class);
        Mockito.verify(encodedSequence).produceResults(ArgumentMatchers.any(Consumer.class), producerContextArgumentCaptor.capture());
        Assert.assertTrue(producerContextArgumentCaptor.getValue().isIntermediateResultExpected());
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getPriority(), HIGH);
    }

    @Test
    public void testFetchNetworkEncodedImage() {
        Producer<CloseableReference<PooledByteBuffer>> encodedSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getEncodedImageProducerSequence(mImageRequest)).thenReturn(encodedSequence);
        Mockito.when(mImageRequest.getSourceUri()).thenReturn(Uri.parse("http://test"));
        DataSource<CloseableReference<PooledByteBuffer>> dataSource = mImagePipeline.fetchEncodedImage(mImageRequest, mCallerContext);
        Assert.assertFalse(dataSource.isFinished());
        ArgumentCaptor<ImageRequest> argumentCaptor = ArgumentCaptor.forClass(ImageRequest.class);
        Mockito.verify(mRequestListener1).onRequestStart(argumentCaptor.capture(), ArgumentMatchers.eq(mCallerContext), ArgumentMatchers.eq("0"), ArgumentMatchers.eq(false));
        ImageRequest capturedImageRequest = argumentCaptor.getValue();
        Assert.assertSame(mImageRequest.getSourceUri(), capturedImageRequest.getSourceUri());
        Mockito.verify(mRequestListener2).onRequestStart(argumentCaptor.capture(), ArgumentMatchers.eq(mCallerContext), ArgumentMatchers.eq("0"), ArgumentMatchers.eq(false));
        capturedImageRequest = argumentCaptor.getValue();
        Assert.assertSame(mImageRequest.getSourceUri(), capturedImageRequest.getSourceUri());
        ArgumentCaptor<ProducerContext> producerContextArgumentCaptor = ArgumentCaptor.forClass(ProducerContext.class);
        Mockito.verify(encodedSequence).produceResults(ArgumentMatchers.any(Consumer.class), producerContextArgumentCaptor.capture());
        Assert.assertTrue(producerContextArgumentCaptor.getValue().isIntermediateResultExpected());
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getPriority(), HIGH);
    }

    @Test
    public void testFetchDecodedImage() {
        Producer<CloseableReference<CloseableImage>> decodedSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getDecodedImageProducerSequence(mImageRequest)).thenReturn(decodedSequence);
        DataSource<CloseableReference<CloseableImage>> dataSource = mImagePipeline.fetchDecodedImage(mImageRequest, mCallerContext);
        Assert.assertFalse(dataSource.isFinished());
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "0", false);
        Mockito.verify(mRequestListener2).onRequestStart(mImageRequest, mCallerContext, "0", false);
        ArgumentCaptor<ProducerContext> producerContextArgumentCaptor = ArgumentCaptor.forClass(ProducerContext.class);
        Mockito.verify(decodedSequence).produceResults(ArgumentMatchers.any(Consumer.class), producerContextArgumentCaptor.capture());
        Assert.assertTrue(producerContextArgumentCaptor.getValue().isIntermediateResultExpected());
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getPriority(), HIGH);
    }

    @Test
    public void testFetchDecodedImageWithRequestLevel() {
        Producer<CloseableReference<CloseableImage>> decodedSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getDecodedImageProducerSequence(mImageRequest)).thenReturn(decodedSequence);
        DataSource<CloseableReference<CloseableImage>> dataSource = mImagePipeline.fetchDecodedImage(mImageRequest, mCallerContext, DISK_CACHE);
        Assert.assertFalse(dataSource.isFinished());
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "0", false);
        Mockito.verify(mRequestListener2).onRequestStart(mImageRequest, mCallerContext, "0", false);
        ArgumentCaptor<ProducerContext> producerContextArgumentCaptor = ArgumentCaptor.forClass(ProducerContext.class);
        Mockito.verify(decodedSequence).produceResults(ArgumentMatchers.any(Consumer.class), producerContextArgumentCaptor.capture());
        Assert.assertTrue(producerContextArgumentCaptor.getValue().isIntermediateResultExpected());
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getPriority(), HIGH);
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getLowestPermittedRequestLevel(), DISK_CACHE);
    }

    @Test
    public void testFetchFromBitmapCacheDueToMethodCall() {
        Producer<CloseableReference<CloseableImage>> bitmapCacheSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getDecodedImageProducerSequence(mImageRequest)).thenReturn(bitmapCacheSequence);
        mImagePipeline.fetchImageFromBitmapCache(mImageRequest, mCallerContext);
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "0", false);
        Mockito.verify(mRequestListener2).onRequestStart(mImageRequest, mCallerContext, "0", false);
        ArgumentCaptor<ProducerContext> producerContextArgumentCaptor = ArgumentCaptor.forClass(ProducerContext.class);
        Mockito.verify(bitmapCacheSequence).produceResults(ArgumentMatchers.any(Consumer.class), producerContextArgumentCaptor.capture());
        Assert.assertTrue(producerContextArgumentCaptor.getValue().isIntermediateResultExpected());
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getPriority(), HIGH);
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getLowestPermittedRequestLevel(), BITMAP_MEMORY_CACHE);
    }

    @Test
    public void testFetchFromBitmapCacheDueToImageRequest() {
        Producer<CloseableReference<CloseableImage>> bitmapCacheSequence = Mockito.mock(Producer.class);
        Mockito.when(mImageRequest.getLowestPermittedRequestLevel()).thenReturn(BITMAP_MEMORY_CACHE);
        Mockito.when(mProducerSequenceFactory.getDecodedImageProducerSequence(mImageRequest)).thenReturn(bitmapCacheSequence);
        mImagePipeline.fetchDecodedImage(mImageRequest, mCallerContext);
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "0", false);
        Mockito.verify(mRequestListener2).onRequestStart(mImageRequest, mCallerContext, "0", false);
        ArgumentCaptor<ProducerContext> producerContextArgumentCaptor = ArgumentCaptor.forClass(ProducerContext.class);
        Mockito.verify(bitmapCacheSequence).produceResults(ArgumentMatchers.any(Consumer.class), producerContextArgumentCaptor.capture());
        Assert.assertTrue(producerContextArgumentCaptor.getValue().isIntermediateResultExpected());
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getPriority(), HIGH);
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getLowestPermittedRequestLevel(), BITMAP_MEMORY_CACHE);
    }

    @Test
    public void testGetBitmapCacheGetSupplier() {
        Supplier<DataSource<CloseableReference<CloseableImage>>> dataSourceSupplier = mImagePipeline.getDataSourceSupplier(mImageRequest, mCallerContext, BITMAP_MEMORY_CACHE);
        Producer<CloseableReference<CloseableImage>> bitmapCacheSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getDecodedImageProducerSequence(mImageRequest)).thenReturn(bitmapCacheSequence);
        dataSourceSupplier.get();
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "0", false);
        Mockito.verify(mRequestListener2).onRequestStart(mImageRequest, mCallerContext, "0", false);
        ArgumentCaptor<ProducerContext> producerContextArgumentCaptor = ArgumentCaptor.forClass(ProducerContext.class);
        Mockito.verify(bitmapCacheSequence).produceResults(ArgumentMatchers.any(Consumer.class), producerContextArgumentCaptor.capture());
        Assert.assertTrue(producerContextArgumentCaptor.getValue().isIntermediateResultExpected());
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getPriority(), HIGH);
    }

    @Test
    public void testGetFullFetchSupplier() {
        Supplier<DataSource<CloseableReference<CloseableImage>>> dataSourceSupplier = mImagePipeline.getDataSourceSupplier(mImageRequest, mCallerContext, FULL_FETCH);
        Producer<CloseableReference<CloseableImage>> decodedSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getDecodedImageProducerSequence(mImageRequest)).thenReturn(decodedSequence);
        DataSource<CloseableReference<CloseableImage>> dataSource = dataSourceSupplier.get();
        Assert.assertFalse(dataSource.isFinished());
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "0", false);
        Mockito.verify(mRequestListener2).onRequestStart(mImageRequest, mCallerContext, "0", false);
        ArgumentCaptor<ProducerContext> producerContextArgumentCaptor = ArgumentCaptor.forClass(ProducerContext.class);
        Mockito.verify(decodedSequence).produceResults(ArgumentMatchers.any(Consumer.class), producerContextArgumentCaptor.capture());
        Assert.assertTrue(producerContextArgumentCaptor.getValue().isIntermediateResultExpected());
        Assert.assertEquals(producerContextArgumentCaptor.getValue().getPriority(), HIGH);
    }

    @Test
    public void testIdIncrementsOnEachRequest() {
        Producer<CloseableReference<CloseableImage>> bitmapCacheSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getDecodedImageProducerSequence(mImageRequest)).thenReturn(bitmapCacheSequence);
        mImagePipeline.fetchImageFromBitmapCache(mImageRequest, mCallerContext);
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "0", false);
        mImagePipeline.fetchImageFromBitmapCache(mImageRequest, mCallerContext);
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "1", false);
    }

    @Test
    public void testEvictFromMemoryCache() {
        String uriString = "http://dummy/string";
        Uri uri = Uri.parse(uriString);
        mImagePipeline.evictFromMemoryCache(uri);
        CacheKey dummyCacheKey = Mockito.mock(CacheKey.class);
        ArgumentCaptor<Predicate> bitmapCachePredicateCaptor = ArgumentCaptor.forClass(Predicate.class);
        Mockito.verify(mBitmapMemoryCache).removeAll(bitmapCachePredicateCaptor.capture());
        Predicate<CacheKey> bitmapMemoryCacheKeyPredicate = bitmapCachePredicateCaptor.getValue();
        BitmapMemoryCacheKey bitmapMemoryCacheKey1 = Mockito.mock(BitmapMemoryCacheKey.class);
        BitmapMemoryCacheKey bitmapMemoryCacheKey2 = Mockito.mock(BitmapMemoryCacheKey.class);
        Mockito.when(bitmapMemoryCacheKey1.containsUri(uri)).thenReturn(true);
        Mockito.when(bitmapMemoryCacheKey2.containsUri(uri)).thenReturn(false);
        Assert.assertTrue(bitmapMemoryCacheKeyPredicate.apply(bitmapMemoryCacheKey1));
        Assert.assertFalse(bitmapMemoryCacheKeyPredicate.apply(bitmapMemoryCacheKey2));
        Assert.assertFalse(bitmapMemoryCacheKeyPredicate.apply(dummyCacheKey));
        ArgumentCaptor<Predicate> encodedMemoryCachePredicateCaptor = ArgumentCaptor.forClass(Predicate.class);
        Mockito.verify(mEncodedMemoryCache).removeAll(encodedMemoryCachePredicateCaptor.capture());
        Predicate<CacheKey> encodedMemoryCacheKeyPredicate = encodedMemoryCachePredicateCaptor.getValue();
        SimpleCacheKey simpleCacheKey1 = new SimpleCacheKey(uriString);
        SimpleCacheKey simpleCacheKey2 = new SimpleCacheKey("rubbish");
        Assert.assertTrue(encodedMemoryCacheKeyPredicate.apply(simpleCacheKey1));
        Assert.assertFalse(encodedMemoryCacheKeyPredicate.apply(simpleCacheKey2));
        Assert.assertFalse(encodedMemoryCacheKeyPredicate.apply(dummyCacheKey));
    }

    @Test
    public void testEvictFromDiskCache() {
        String uriString = "http://dummy/string";
        Uri uri = Uri.parse(uriString);
        CacheKey dummyCacheKey = Mockito.mock(CacheKey.class);
        List<CacheKey> list = new ArrayList<>();
        list.add(dummyCacheKey);
        MultiCacheKey multiKey = new MultiCacheKey(list);
        Mockito.when(mCacheKeyFactory.getEncodedCacheKey(ArgumentMatchers.any(ImageRequest.class), ArgumentMatchers.anyObject())).thenReturn(multiKey);
        mImagePipeline.evictFromDiskCache(uri);
        Mockito.verify(mMainDiskStorageCache).remove(multiKey);
        Mockito.verify(mSmallImageDiskStorageCache).remove(multiKey);
    }

    @Test
    public void testClearMemoryCaches() {
        String uriString = "http://dummy/string";
        Uri uri = Uri.parse(uriString);
        CacheKey dummyCacheKey = Mockito.mock(CacheKey.class);
        mImagePipeline.clearMemoryCaches();
        ArgumentCaptor<Predicate> bitmapCachePredicateCaptor = ArgumentCaptor.forClass(Predicate.class);
        Mockito.verify(mBitmapMemoryCache).removeAll(bitmapCachePredicateCaptor.capture());
        Predicate<CacheKey> bitmapMemoryCacheKeyPredicate = bitmapCachePredicateCaptor.getValue();
        BitmapMemoryCacheKey bitmapMemoryCacheKey1 = Mockito.mock(BitmapMemoryCacheKey.class);
        BitmapMemoryCacheKey bitmapMemoryCacheKey2 = Mockito.mock(BitmapMemoryCacheKey.class);
        Mockito.when(bitmapMemoryCacheKey1.containsUri(uri)).thenReturn(true);
        Mockito.when(bitmapMemoryCacheKey2.containsUri(uri)).thenReturn(false);
        Assert.assertTrue(bitmapMemoryCacheKeyPredicate.apply(bitmapMemoryCacheKey1));
        Assert.assertTrue(bitmapMemoryCacheKeyPredicate.apply(bitmapMemoryCacheKey2));
        Assert.assertTrue(bitmapMemoryCacheKeyPredicate.apply(dummyCacheKey));
        ArgumentCaptor<Predicate> encodedMemoryCachePredicateCaptor = ArgumentCaptor.forClass(Predicate.class);
        Mockito.verify(mEncodedMemoryCache).removeAll(encodedMemoryCachePredicateCaptor.capture());
        Predicate<CacheKey> encodedMemoryCacheKeyPredicate = encodedMemoryCachePredicateCaptor.getValue();
        SimpleCacheKey simpleCacheKey1 = new SimpleCacheKey(uriString);
        SimpleCacheKey simpleCacheKey2 = new SimpleCacheKey("rubbish");
        Assert.assertTrue(encodedMemoryCacheKeyPredicate.apply(simpleCacheKey1));
        Assert.assertTrue(encodedMemoryCacheKeyPredicate.apply(simpleCacheKey2));
        Assert.assertTrue(encodedMemoryCacheKeyPredicate.apply(dummyCacheKey));
    }

    @Test
    public void testIsInDiskCacheFromMainDiskCache() {
        Mockito.when(mImageRequest.getCacheChoice()).thenReturn(DEFAULT);
        Mockito.when(mMainDiskStorageCache.diskCheckSync(ArgumentMatchers.any(CacheKey.class))).thenReturn(true);
        Assert.assertTrue(mImagePipeline.isInDiskCacheSync(mImageRequest));
    }

    @Test
    public void testIsInDiskCacheFromSmallDiskCache() {
        Mockito.when(mImageRequest.getCacheChoice()).thenReturn(SMALL);
        Mockito.when(mSmallImageDiskStorageCache.diskCheckSync(ArgumentMatchers.any(CacheKey.class))).thenReturn(true);
        Assert.assertTrue(mImagePipeline.isInDiskCacheSync(mImageRequest));
    }

    @Test
    public void testClearDiskCaches() {
        mImagePipeline.clearDiskCaches();
        Mockito.verify(mMainDiskStorageCache).clearAll();
        Mockito.verify(mSmallImageDiskStorageCache).clearAll();
    }

    @Test
    public void testLocalRequestListenerIsCalled() {
        RequestListener localRequestListner = Mockito.mock(RequestListener.class);
        Mockito.when(mImageRequest.getRequestListener()).thenReturn(localRequestListner);
        Producer<CloseableReference<CloseableImage>> bitmapCacheSequence = Mockito.mock(Producer.class);
        Mockito.when(mProducerSequenceFactory.getDecodedImageProducerSequence(mImageRequest)).thenReturn(bitmapCacheSequence);
        mImagePipeline.fetchImageFromBitmapCache(mImageRequest, mCallerContext);
        Mockito.verify(localRequestListner).onRequestStart(mImageRequest, mCallerContext, "0", false);
        Mockito.verify(mRequestListener1).onRequestStart(mImageRequest, mCallerContext, "0", false);
        Mockito.verify(mRequestListener2).onRequestStart(mImageRequest, mCallerContext, "0", false);
    }
}

