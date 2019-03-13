/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import LocalVideoThumbnailProducer.CREATED_THUMBNAIL;
import MediaStore.Images.Thumbnails.MICRO_KIND;
import MediaStore.Images.Thumbnails.MINI_KIND;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.testing.TestExecutorService;
import java.io.File;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static Config.NONE;
import static LocalVideoThumbnailProducer.PRODUCER_NAME;


/**
 * Basic tests for {@link LocalVideoThumbnailProducer}
 */
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@PrepareForTest(ThumbnailUtils.class)
@Config(manifest = NONE)
public class LocalVideoThumbnailProducerTest {
    private static final String PRODUCER_NAME = PRODUCER_NAME;

    private static final String TEST_FILENAME = "dummy.jpg";

    private static final Uri LOCAL_VIDEO_URI = Uri.parse("file:///dancing_hotdog.mp4");

    @Mock
    public PooledByteBufferFactory mPooledByteBufferFactory;

    @Mock
    public Consumer<CloseableReference<CloseableImage>> mConsumer;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Exception mException;

    @Mock
    public Bitmap mBitmap;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private TestExecutorService mExecutor;

    private SettableProducerContext mProducerContext;

    private final String mRequestId = "mRequestId";

    private File mFile;

    private LocalVideoThumbnailProducer mLocalVideoThumbnailProducer;

    private CloseableReference<CloseableStaticBitmap> mCloseableReference;

    @Test
    public void testLocalVideoThumbnailCancelled() {
        mLocalVideoThumbnailProducer.produceResults(mConsumer, mProducerContext);
        mProducerContext.cancel();
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithCancellation(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener, Mockito.never()).onUltimateProducerReached(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.verify(mConsumer).onCancellation();
    }

    @Test
    public void testLocalVideoMiniThumbnailSuccess() throws Exception {
        Mockito.when(mImageRequest.getPreferredWidth()).thenReturn(100);
        Mockito.when(mImageRequest.getPreferredHeight()).thenReturn(100);
        Mockito.when(mImageRequest.getSourceUri()).thenReturn(LocalVideoThumbnailProducerTest.LOCAL_VIDEO_URI);
        Mockito.when(ThumbnailUtils.createVideoThumbnail(mFile.getPath(), MINI_KIND)).thenReturn(mBitmap);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                mCloseableReference = ((CloseableReference) (invocation.getArguments()[0])).clone();
                return null;
            }
        }).when(mConsumer).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.eq(IS_LAST));
        mLocalVideoThumbnailProducer.produceResults(mConsumer, mProducerContext);
        mExecutor.runUntilIdle();
        Assert.assertEquals(1, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertEquals(mBitmap, mCloseableReference.getUnderlyingReferenceTestOnly().get().getUnderlyingBitmap());
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME, true);
    }

    @Test
    public void testLocalVideoMicroThumbnailSuccess() throws Exception {
        Mockito.when(mImageRequest.getSourceUri()).thenReturn(LocalVideoThumbnailProducerTest.LOCAL_VIDEO_URI);
        Mockito.when(mProducerListener.requiresExtraMap(mRequestId)).thenReturn(true);
        Mockito.when(ThumbnailUtils.createVideoThumbnail(mFile.getPath(), MICRO_KIND)).thenReturn(mBitmap);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                mCloseableReference = ((CloseableReference) (invocation.getArguments()[0])).clone();
                return null;
            }
        }).when(mConsumer).onNewResult(ArgumentMatchers.any(CloseableReference.class), ArgumentMatchers.eq(IS_LAST));
        mLocalVideoThumbnailProducer.produceResults(mConsumer, mProducerContext);
        mExecutor.runUntilIdle();
        Assert.assertEquals(1, mCloseableReference.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertEquals(mBitmap, mCloseableReference.getUnderlyingReferenceTestOnly().get().getUnderlyingBitmap());
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME);
        Map<String, String> thumbnailFoundMap = ImmutableMap.of(CREATED_THUMBNAIL, "true");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME, thumbnailFoundMap);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME, true);
    }

    @Test
    public void testLocalVideoMicroThumbnailReturnsNull() throws Exception {
        Mockito.when(mProducerListener.requiresExtraMap(mRequestId)).thenReturn(true);
        Mockito.when(ThumbnailUtils.createVideoThumbnail(mFile.getPath(), MICRO_KIND)).thenReturn(null);
        mLocalVideoThumbnailProducer.produceResults(mConsumer, mProducerContext);
        mExecutor.runUntilIdle();
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME);
        Map<String, String> thumbnailNotFoundMap = ImmutableMap.of(CREATED_THUMBNAIL, "false");
        Mockito.verify(mProducerListener).onProducerFinishWithSuccess(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME, thumbnailNotFoundMap);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME, false);
    }

    @Test(expected = RuntimeException.class)
    public void testFetchLocalFileFailsByThrowing() throws Exception {
        Mockito.when(ThumbnailUtils.createVideoThumbnail(mFile.getPath(), MICRO_KIND)).thenThrow(mException);
        Mockito.verify(mConsumer).onFailure(mException);
        Mockito.verify(mProducerListener).onProducerStart(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME);
        Mockito.verify(mProducerListener).onProducerFinishWithFailure(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME, mException, null);
        Mockito.verify(mProducerListener).onUltimateProducerReached(mRequestId, LocalVideoThumbnailProducerTest.PRODUCER_NAME, false);
    }
}

