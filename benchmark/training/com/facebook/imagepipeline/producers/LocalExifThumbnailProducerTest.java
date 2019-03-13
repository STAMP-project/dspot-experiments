/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import Consumer.IS_LAST;
import DefaultImageFormats.JPEG;
import android.content.ContentResolver;
import android.media.ExifInterface;
import android.net.Uri;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.testing.TestExecutorService;
import com.facebook.imageutils.BitmapUtil;
import com.facebook.imageutils.JfifUtil;
import java.io.File;
import java.util.concurrent.Executor;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import static Config.NONE;


@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
@Config(manifest = NONE)
@PrepareForTest({ JfifUtil.class, BitmapUtil.class })
public class LocalExifThumbnailProducerTest {
    private static final int WIDTH = 10;

    private static final int HEIGHT = 20;

    private static final int ORIENTATION = 8;

    private static final int ANGLE = 270;

    @Mock
    public ExifInterface mExifInterface;

    @Mock
    public ImageRequest mImageRequest;

    @Mock
    public ProducerListener mProducerListener;

    @Mock
    public Consumer<EncodedImage> mConsumer;

    @Mock
    public ProducerContext mProducerContext;

    @Mock
    public PooledByteBufferFactory mPooledByteBufferFactory;

    @Mock
    public PooledByteBuffer mThumbnailByteBuffer;

    @Mock
    public File mFile;

    @Mock
    public ContentResolver mContentResolver;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private final Uri mUri = Uri.parse("/dummy/path");

    private byte[] mThumbnailBytes;

    private TestExecutorService mTestExecutorService;

    private LocalExifThumbnailProducerTest.TestLocalExifThumbnailProducer mTestLocalExifThumbnailProducer;

    private EncodedImage mCapturedEncodedImage;

    @Test
    public void testFindExifThumbnail() {
        mTestLocalExifThumbnailProducer.produceResults(mConsumer, mProducerContext);
        mTestExecutorService.runUntilIdle();
        // Should have 2 references open: The cloned reference when the argument is
        // captured by EncodedImage and the one that is created when
        // getByteBufferRef is called on EncodedImage
        Assert.assertEquals(2, mCapturedEncodedImage.getByteBufferRef().getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertSame(mThumbnailByteBuffer, mCapturedEncodedImage.getByteBufferRef().get());
        Assert.assertEquals(JPEG, mCapturedEncodedImage.getImageFormat());
        Assert.assertEquals(LocalExifThumbnailProducerTest.WIDTH, mCapturedEncodedImage.getWidth());
        Assert.assertEquals(LocalExifThumbnailProducerTest.HEIGHT, mCapturedEncodedImage.getHeight());
        Assert.assertEquals(LocalExifThumbnailProducerTest.ANGLE, mCapturedEncodedImage.getRotationAngle());
    }

    @Test
    public void testNoExifThumbnail() {
        Mockito.when(mExifInterface.hasThumbnail()).thenReturn(false);
        mTestLocalExifThumbnailProducer.produceResults(mConsumer, mProducerContext);
        mTestExecutorService.runUntilIdle();
        Mockito.verify(mConsumer).onNewResult(null, IS_LAST);
    }

    private class TestLocalExifThumbnailProducer extends LocalExifThumbnailProducer {
        private TestLocalExifThumbnailProducer(Executor executor, PooledByteBufferFactory pooledByteBufferFactory, ContentResolver contentResolver) {
            super(executor, pooledByteBufferFactory, contentResolver);
        }

        @Override
        ExifInterface getExifInterface(Uri uri) {
            if (uri.equals(mUri)) {
                return mExifInterface;
            }
            return null;
        }
    }
}

