/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import MediaStore.Images;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.testing.TestExecutorService;
import java.io.File;
import java.io.InputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import static Config.NONE;
import static LocalContentUriThumbnailFetchProducer.PRODUCER_NAME;


/**
 * Basic tests for LocalContentUriThumbnailFetchProducer
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@PrepareForTest({ LocalContentUriThumbnailFetchProducer.class, Images.class })
@Config(manifest = NONE)
public class LocalContentUriThumbnailFetchProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    private static final String THUMBNAIL_FILE_NAME = "////sdcard/thumb.jpg";

    private static final long THUMBNAIL_FILE_SIZE = 1374;

    @Rule
    public PowerMockRule mPowerMockRule = new PowerMockRule();

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

    @Mock
    public Cursor mCursor;

    @Mock
    public File mThumbnailFile;

    private TestExecutorService mExecutor;

    private SettableProducerContext mProducerContext;

    private final String mRequestId = "mRequestId";

    private Uri mContentUri;

    private LocalContentUriThumbnailFetchProducer mLocalContentUriThumbnailFetchProducer;

    @Test
    public void testLocalContentUriFetchCancelled() {
        mockResizeOptions(512, 384);
        produceResults();
        mProducerContext.cancel();
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalContentUriThumbnailFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithCancellation(mRequestId, LocalContentUriThumbnailFetchProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mConsumer).onCancellation();
        mExecutor.runUntilIdle();
        Mockito.verifyZeroInteractions(mPooledByteBufferFactory);
    }

    @Test
    public void testFetchLocalContentUri() throws Exception {
        mockResizeOptions(512, 384);
        PooledByteBuffer pooledByteBuffer = Mockito.mock(PooledByteBuffer.class);
        Mockito.when(mPooledByteBufferFactory.newByteBuffer(ArgumentMatchers.any(InputStream.class))).thenReturn(pooledByteBuffer);
        produceResultsAndRunUntilIdle();
        assertConsumerReceivesImage();
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalContentUriThumbnailFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, LocalContentUriThumbnailFetchProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalContentUriThumbnailFetchProducerTest.PRODUCER_NAME, true);
    }

    @Test(expected = RuntimeException.class)
    public void testFetchLocalContentUriFailsByThrowing() throws Exception {
        mockResizeOptions(512, 384);
        Mockito.when(mPooledByteBufferFactory.newByteBuffer(ArgumentMatchers.any(InputStream.class))).thenThrow(mException);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalContentUriThumbnailFetchProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(mRequestId, LocalContentUriThumbnailFetchProducerTest.PRODUCER_NAME, mException, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalContentUriThumbnailFetchProducerTest.PRODUCER_NAME, false);
    }

    @Test
    public void testIsLargerThanThumbnailMaxSize() {
        mockResizeOptions(1000, 384);
        produceResultsAndRunUntilIdle();
        assertConsumerReceivesNull();
    }

    @Test
    public void testWithoutResizeOptions() {
        produceResultsAndRunUntilIdle();
        assertConsumerReceivesNull();
    }
}

