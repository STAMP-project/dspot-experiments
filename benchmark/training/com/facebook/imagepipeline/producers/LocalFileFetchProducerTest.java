/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.testing.TestExecutorService;
import java.io.File;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;
import static LocalFileFetchProducer.PRODUCER_NAME;


/**
 * Basic tests for LocalFileFetchProducer
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class LocalFileFetchProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    private static final int INPUT_STREAM_LENGTH = 100;

    private static final String TEST_FILENAME = "dummy.jpg";

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

    private File mFile;

    private LocalFileFetchProducer mLocalFileFetchProducer;

    private EncodedImage mCapturedEncodedImage;

    @Test
    public void testLocalFileFetchCancelled() {
        mLocalFileFetchProducer.produceResults(mConsumer, mProducerContext);
        mProducerContext.cancel();
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalFileFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithCancellation(mRequestId, LocalFileFetchProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mConsumer).onCancellation();
        mExecutor.runUntilIdle();
        Mockito.verifyZeroInteractions(mPooledByteBufferFactory);
    }

    @Test
    public void testFetchLocalFile() throws Exception {
        PooledByteBuffer pooledByteBuffer = Mockito.mock(PooledByteBuffer.class);
        Mockito.when(mPooledByteBufferFactory.newByteBuffer(ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(LocalFileFetchProducerTest.INPUT_STREAM_LENGTH))).thenReturn(pooledByteBuffer);
        mLocalFileFetchProducer.produceResults(mConsumer, mProducerContext);
        mExecutor.runUntilIdle();
        Assert.assertEquals(2, mCapturedEncodedImage.getByteBufferRef().getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(pooledByteBuffer, mCapturedEncodedImage.getByteBufferRef().get());
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalFileFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, LocalFileFetchProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalFileFetchProducerTest.PRODUCER_NAME, true);
    }

    @Test(expected = RuntimeException.class)
    public void testFetchLocalFileFailsByThrowing() throws Exception {
        Mockito.when(mPooledByteBufferFactory.newByteBuffer(ArgumentMatchers.any(InputStream.class), ArgumentMatchers.eq(LocalFileFetchProducerTest.INPUT_STREAM_LENGTH))).thenThrow(mException);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalFileFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(mRequestId, LocalFileFetchProducerTest.PRODUCER_NAME, mException, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalFileFetchProducerTest.PRODUCER_NAME, false);
    }
}

