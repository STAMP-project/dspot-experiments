/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import android.content.ContentResolver;
import android.net.Uri;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.testing.TestExecutorService;
import java.io.InputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static QualifiedResourceFetchProducer.PRODUCER_NAME;


/**
 * Basic tests for QualifiedResourceFetchProducer
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class QualifiedResourceFetchProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    private static final String PACKAGE_NAME = "com.myapp.myplugin";

    private static final int RESOURCE_ID = 42;

    private static final String REQUEST_ID = "requestId";

    private static final String CALLER_CONTEXT = "callerContext";

    @Mock
    public PooledByteBufferFactory mPooledByteBufferFactory;

    @Mock
    public ContentResolver mContentResolver;

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

    private Uri mContentUri;

    private QualifiedResourceFetchProducer mQualifiedResourceFetchProducer;

    @Test
    public void testQualifiedResourceUri() throws Exception {
        PooledByteBuffer pooledByteBuffer = Mockito.mock(PooledByteBuffer.class);
        Mockito.when(mPooledByteBufferFactory.newByteBuffer(ArgumentMatchers.any(InputStream.class))).thenReturn(pooledByteBuffer);
        Mockito.when(mContentResolver.openInputStream(mContentUri)).thenReturn(Mockito.mock(InputStream.class));
        mQualifiedResourceFetchProducer.produceResults(mConsumer, mProducerContext);
        mExecutor.runUntilIdle();
        Mockito.verify(mPooledByteBufferFactory, Mockito.times(1)).newByteBuffer(ArgumentMatchers.any(InputStream.class));
        Mockito.verify(mContentResolver, Mockito.times(1)).openInputStream(mContentUri);
        Mockito.verify(mProducerListener).onProducerStart(QualifiedResourceFetchProducerTest.REQUEST_ID, QualifiedResourceFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(QualifiedResourceFetchProducerTest.REQUEST_ID, QualifiedResourceFetchProducerTest.PRODUCER_NAME, null);
    }
}

