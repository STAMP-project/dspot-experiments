/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import AssetManager.ACCESS_STREAMING;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.testing.TestExecutorService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;
import static LocalAssetFetchProducer.PRODUCER_NAME;


/**
 * Basic tests for LocalResourceFetchProducer
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class LocalAssetFetchProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    private static final String TEST_FILENAME = "dummy_asset.jpg";

    private static final int TEST_DATA_LENGTH = 337;

    @Mock
    public AssetManager mAssetManager;

    @Mock
    public PooledByteBuffer mPooledByteBuffer;

    @Mock
    public AssetFileDescriptor mAssetFileDescriptor;

    @Mock
    public PooledByteBufferFactory mPooledByteBufferFactory;

    @Mock
    public Consumer<EncodedImage> mConsumer;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Exception mException;

    private TestExecutorService mExecutor;

    private SettableProducerContext mProducerContext;

    private final String mRequestId = "mRequestId";

    private LocalAssetFetchProducer mLocalAssetFetchProducer;

    private EncodedImage mCapturedEncodedImage;

    @Test
    public void testFetchAssetResource() throws Exception {
        PooledByteBuffer pooledByteBuffer = Mockito.mock(PooledByteBuffer.class);
        Mockito.when(mAssetManager.open(ArgumentMatchers.eq(LocalAssetFetchProducerTest.TEST_FILENAME), ArgumentMatchers.eq(ACCESS_STREAMING))).thenReturn(new ByteArrayInputStream(new byte[LocalAssetFetchProducerTest.TEST_DATA_LENGTH]));
        Mockito.when(mPooledByteBufferFactory.newByteBuffer(ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(LocalAssetFetchProducerTest.TEST_DATA_LENGTH))).thenReturn(pooledByteBuffer);
        mLocalAssetFetchProducer.produceResults(mConsumer, mProducerContext);
        mExecutor.runUntilIdle();
        Assert.assertEquals(2, mCapturedEncodedImage.getByteBufferRef().getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(pooledByteBuffer, mCapturedEncodedImage.getByteBufferRef().get());
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalAssetFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, LocalAssetFetchProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalAssetFetchProducerTest.PRODUCER_NAME, true);
    }

    @Test(expected = RuntimeException.class)
    public void testFetchLocalResourceFailsByThrowing() throws Exception {
        Mockito.when(mAssetManager.open(ArgumentMatchers.eq(LocalAssetFetchProducerTest.TEST_FILENAME), ArgumentMatchers.eq(ACCESS_STREAMING))).thenThrow(mException);
        mLocalAssetFetchProducer.produceResults(mConsumer, mProducerContext);
        mExecutor.runUntilIdle();
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalAssetFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(mRequestId, LocalAssetFetchProducerTest.PRODUCER_NAME, mException, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalAssetFetchProducerTest.PRODUCER_NAME, false);
    }
}

