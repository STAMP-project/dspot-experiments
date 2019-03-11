/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
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
import org.robolectric.annotation.Config;

import static LocalResourceFetchProducer.PRODUCER_NAME;


/**
 * Basic tests for LocalResourceFetchProducer
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LocalResourceFetchProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    private static final int TEST_ID = 1337;

    private static final int TEST_DATA_LENGTH = 337;

    @Mock
    public Resources mResources;

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

    private LocalResourceFetchProducer mLocalResourceFetchProducer;

    private EncodedImage mCapturedEncodedImage;

    @Test
    public void testFetchLocalResource() throws Exception {
        PooledByteBuffer pooledByteBuffer = Mockito.mock(PooledByteBuffer.class);
        Mockito.when(mPooledByteBufferFactory.newByteBuffer(ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(LocalResourceFetchProducerTest.TEST_DATA_LENGTH))).thenReturn(pooledByteBuffer);
        Mockito.when(mResources.openRawResource(ArgumentMatchers.eq(LocalResourceFetchProducerTest.TEST_ID))).thenReturn(new ByteArrayInputStream(new byte[LocalResourceFetchProducerTest.TEST_DATA_LENGTH]));
        mLocalResourceFetchProducer.produceResults(mConsumer, mProducerContext);
        mExecutor.runUntilIdle();
        Assert.assertEquals(2, mCapturedEncodedImage.getByteBufferRef().getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(pooledByteBuffer, mCapturedEncodedImage.getByteBufferRef().get());
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalResourceFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, LocalResourceFetchProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalResourceFetchProducerTest.PRODUCER_NAME, true);
    }

    @Test(expected = RuntimeException.class)
    public void testFetchLocalResourceFailsByThrowing() throws Exception {
        Mockito.when(mResources.openRawResource(ArgumentMatchers.eq(LocalResourceFetchProducerTest.TEST_ID))).thenThrow(mException);
        mLocalResourceFetchProducer.produceResults(mConsumer, mProducerContext);
        mExecutor.runUntilIdle();
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalResourceFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(mRequestId, LocalResourceFetchProducerTest.PRODUCER_NAME, mException, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalResourceFetchProducerTest.PRODUCER_NAME, false);
    }
}

