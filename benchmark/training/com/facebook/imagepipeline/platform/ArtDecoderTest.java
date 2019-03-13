/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.platform;


import BitmapFactory.Options;
import Build.VERSION_CODES;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.BitmapPool;
import com.facebook.imagepipeline.testing.MockBitmapFactory;
import com.facebook.soloader.SoLoader;
import java.util.ConcurrentModificationException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link ArtDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
@PrepareOnlyThisForTest({ BitmapFactory.class, BitmapRegionDecoder.class })
@Config(sdk = VERSION_CODES.LOLLIPOP)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
public class ArtDecoderTest {
    private static final Config DEFAULT_BITMAP_CONFIG = Config.ARGB_8888;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    static {
        SoLoader.setInTestMode();
    }

    private static final int RANDOM_SEED = 10101;

    private static final int ENCODED_BYTES_LENGTH = 128;

    private BitmapPool mBitmapPool;

    private PooledByteBuffer mPooledByteBuffer;

    private CloseableReference<PooledByteBuffer> mByteBufferRef;

    private BitmapRegionDecoder mBitmapRegionDecoder;

    private ArtDecoder mArtDecoder;

    public Bitmap mBitmap;

    public Answer<Bitmap> mBitmapFactoryDefaultAnswer;

    private EncodedImage mEncodedImage;

    private byte[] mEncodedBytes;

    private byte[] mTempStorage;

    @Test
    public void testDecodeStaticDecodesFromStream() {
        mArtDecoder.decodeFromEncodedImage(mEncodedImage, ArtDecoderTest.DEFAULT_BITMAP_CONFIG, null);
        verifyDecodedFromStream();
    }

    @Test
    public void testDecodeStaticDoesNotLeak() {
        mArtDecoder.decodeFromEncodedImage(mEncodedImage, ArtDecoderTest.DEFAULT_BITMAP_CONFIG, null);
        verifyNoLeaks();
    }

    @Test
    public void testStaticImageUsesPooledByteBufferWithPixels() {
        CloseableReference<Bitmap> decodedImage = mArtDecoder.decodeFromEncodedImage(mEncodedImage, ArtDecoderTest.DEFAULT_BITMAP_CONFIG, null);
        closeAndVerifyClosed(decodedImage);
    }

    @Test(expected = NullPointerException.class)
    public void testPoolsReturnsNull() {
        Mockito.doReturn(null).when(mBitmapPool).get(ArgumentMatchers.anyInt());
        mArtDecoder.decodeFromEncodedImage(mEncodedImage, ArtDecoderTest.DEFAULT_BITMAP_CONFIG, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testBitmapFactoryReturnsNewBitmap() {
        whenBitmapFactoryDecodeStream().thenAnswer(mBitmapFactoryDefaultAnswer).thenReturn(MockBitmapFactory.create());
        try {
            mArtDecoder.decodeFromEncodedImage(mEncodedImage, ArtDecoderTest.DEFAULT_BITMAP_CONFIG, null);
        } finally {
            Mockito.verify(mBitmapPool).release(mBitmap);
        }
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testBitmapFactoryThrowsAnException() {
        whenBitmapFactoryDecodeStream().thenAnswer(mBitmapFactoryDefaultAnswer).thenThrow(new ConcurrentModificationException());
        try {
            mArtDecoder.decodeFromEncodedImage(mEncodedImage, ArtDecoderTest.DEFAULT_BITMAP_CONFIG, null);
        } finally {
            Mockito.verify(mBitmapPool).release(mBitmap);
        }
    }

    @Test
    public void testDecodeJpeg_allBytes_complete() {
        jpegTestCase(true, ArtDecoderTest.ENCODED_BYTES_LENGTH);
    }

    @Test
    public void testDecodeJpeg_notAllBytes_complete() {
        jpegTestCase(true, ((ArtDecoderTest.ENCODED_BYTES_LENGTH) / 2));
    }

    @Test
    public void testDecodeJpeg_allBytes_incomplete() {
        jpegTestCase(false, ArtDecoderTest.ENCODED_BYTES_LENGTH);
    }

    @Test
    public void testDecodeJpeg_notAllBytes_incomplete() {
        jpegTestCase(false, ((ArtDecoderTest.ENCODED_BYTES_LENGTH) / 2));
    }

    @Test
    public void testDecodeJpeg_regionDecodingEnabled() {
        Rect region = new Rect(0, 0, 200, 100);
        int size = MockBitmapFactory.bitmapSize(region.width(), region.height(), ArtDecoderTest.DEFAULT_BITMAP_CONFIG);
        Bitmap bitmap = MockBitmapFactory.create(region.width(), region.height(), ArtDecoderTest.DEFAULT_BITMAP_CONFIG);
        Mockito.when(mBitmapRegionDecoder.decodeRegion(ArgumentMatchers.any(Rect.class), ArgumentMatchers.any(Options.class))).thenReturn(bitmap);
        Mockito.doReturn(bitmap).when(mBitmapPool).get(size);
        CloseableReference<Bitmap> decodedImage = mArtDecoder.decodeFromEncodedImage(mEncodedImage, ArtDecoderTest.DEFAULT_BITMAP_CONFIG, region);
        Assert.assertTrue(((decodedImage.get().getWidth()) == (region.width())));
        Assert.assertTrue(((decodedImage.get().getHeight()) == (region.height())));
        closeAndVerifyClosed(decodedImage, bitmap);
        Mockito.verify(mBitmapRegionDecoder).recycle();
    }

    @Test
    public void testDecodeFromEncodedImage_regionDecodingEnabled() {
        Rect region = new Rect(0, 0, 200, 100);
        int size = MockBitmapFactory.bitmapSize(region.width(), region.height(), ArtDecoderTest.DEFAULT_BITMAP_CONFIG);
        Bitmap bitmap = MockBitmapFactory.create(region.width(), region.height(), ArtDecoderTest.DEFAULT_BITMAP_CONFIG);
        Mockito.when(mBitmapRegionDecoder.decodeRegion(ArgumentMatchers.any(Rect.class), ArgumentMatchers.any(Options.class))).thenReturn(bitmap);
        Mockito.doReturn(bitmap).when(mBitmapPool).get(size);
        CloseableReference<Bitmap> decodedImage = mArtDecoder.decodeFromEncodedImage(mEncodedImage, ArtDecoderTest.DEFAULT_BITMAP_CONFIG, region);
        Assert.assertTrue(((decodedImage.get().getWidth()) == (region.width())));
        Assert.assertTrue(((decodedImage.get().getHeight()) == (region.height())));
        closeAndVerifyClosed(decodedImage, bitmap);
        Mockito.verify(mBitmapRegionDecoder).recycle();
    }
}

