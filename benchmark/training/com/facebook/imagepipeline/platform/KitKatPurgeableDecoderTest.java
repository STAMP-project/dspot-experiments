/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.platform;


import BitmapFactory.Options;
import Build.VERSION_CODES;
import MockBitmapFactory.DEFAULT_BITMAP_SIZE;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.common.TooManyBitmapsException;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.BitmapCounter;
import com.facebook.imagepipeline.memory.BitmapCounterProvider;
import com.facebook.imagepipeline.memory.FlexByteArrayPool;
import com.facebook.imagepipeline.nativecode.Bitmaps;
import com.facebook.imagepipeline.nativecode.DalvikPurgeableDecoder;
import com.facebook.imagepipeline.testing.MockBitmapFactory;
import com.facebook.soloader.SoLoader;
import java.util.ConcurrentModificationException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Tests for {@link KitKatPurgeableDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
@PrepareOnlyThisForTest({ BitmapCounterProvider.class, BitmapFactory.class, DalvikPurgeableDecoder.class, Bitmaps.class })
@Config(sdk = VERSION_CODES.KITKAT)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
public class KitKatPurgeableDecoderTest {
    protected static final Config DEFAULT_BITMAP_CONFIG = Config.ARGB_8888;

    protected FlexByteArrayPool mFlexByteArrayPool;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    static {
        SoLoader.setInTestMode();
    }

    protected static final int IMAGE_SIZE = 5;

    protected static final int LENGTH = 10;

    protected static final long POINTER = 1000L;

    protected static final int MAX_BITMAP_COUNT = 2;

    protected static final int MAX_BITMAP_SIZE = (KitKatPurgeableDecoderTest.MAX_BITMAP_COUNT) * (MockBitmapFactory.DEFAULT_BITMAP_SIZE);

    protected KitKatPurgeableDecoder mKitKatPurgeableDecoder;

    protected CloseableReference<PooledByteBuffer> mByteBufferRef;

    protected EncodedImage mEncodedImage;

    protected byte[] mInputBuf;

    protected byte[] mDecodeBuf;

    protected CloseableReference<byte[]> mDecodeBufRef;

    protected Bitmap mBitmap;

    protected BitmapCounter mBitmapCounter;

    @Test
    public void testDecode_Jpeg_Detailed() {
        Assume.assumeNotNull(mKitKatPurgeableDecoder);
        setUpJpegDecode();
        CloseableReference<Bitmap> result = mKitKatPurgeableDecoder.decodeJPEGFromEncodedImage(mEncodedImage, KitKatPurgeableDecoderTest.DEFAULT_BITMAP_CONFIG, null, KitKatPurgeableDecoderTest.IMAGE_SIZE);
        Mockito.verify(mFlexByteArrayPool).get(((KitKatPurgeableDecoderTest.IMAGE_SIZE) + 2));
        verifyStatic();
        BitmapFactory.decodeByteArray(ArgumentMatchers.same(mDecodeBuf), ArgumentMatchers.eq(0), ArgumentMatchers.eq(KitKatPurgeableDecoderTest.IMAGE_SIZE), ArgumentMatchers.argThat(new KitKatPurgeableDecoderTest.BitmapFactoryOptionsMatcher()));
        Assert.assertEquals(2, mByteBufferRef.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertEquals(mBitmap, result.get());
        Assert.assertTrue(result.isValid());
        Assert.assertEquals(1, mBitmapCounter.getCount());
        Assert.assertEquals(DEFAULT_BITMAP_SIZE, mBitmapCounter.getSize());
    }

    @Test
    public void testDecodeJpeg_incomplete() {
        Assume.assumeNotNull(mKitKatPurgeableDecoder);
        Mockito.when(mFlexByteArrayPool.get(((KitKatPurgeableDecoderTest.IMAGE_SIZE) + 2))).thenReturn(mDecodeBufRef);
        CloseableReference<Bitmap> result = mKitKatPurgeableDecoder.decodeJPEGFromEncodedImage(mEncodedImage, KitKatPurgeableDecoderTest.DEFAULT_BITMAP_CONFIG, null, KitKatPurgeableDecoderTest.IMAGE_SIZE);
        Mockito.verify(mFlexByteArrayPool).get(((KitKatPurgeableDecoderTest.IMAGE_SIZE) + 2));
        verifyStatic();
        BitmapFactory.decodeByteArray(ArgumentMatchers.same(mDecodeBuf), ArgumentMatchers.eq(0), ArgumentMatchers.eq(((KitKatPurgeableDecoderTest.IMAGE_SIZE) + 2)), ArgumentMatchers.argThat(new KitKatPurgeableDecoderTest.BitmapFactoryOptionsMatcher()));
        Assert.assertEquals(((byte) (255)), mDecodeBuf[5]);
        Assert.assertEquals(((byte) (217)), mDecodeBuf[6]);
        Assert.assertEquals(2, mByteBufferRef.getUnderlyingReferenceTestOnly().getRefCountTestOnly());
        Assert.assertEquals(mBitmap, result.get());
        Assert.assertTrue(result.isValid());
        Assert.assertEquals(1, mBitmapCounter.getCount());
        Assert.assertEquals(DEFAULT_BITMAP_SIZE, mBitmapCounter.getSize());
    }

    @Test(expected = TooManyBitmapsException.class)
    public void testHitBitmapLimit_static() {
        Assume.assumeNotNull(mKitKatPurgeableDecoder);
        mBitmapCounter.increase(MockBitmapFactory.createForSize(KitKatPurgeableDecoderTest.MAX_BITMAP_SIZE, KitKatPurgeableDecoderTest.DEFAULT_BITMAP_CONFIG));
        try {
            mKitKatPurgeableDecoder.decodeFromEncodedImage(mEncodedImage, KitKatPurgeableDecoderTest.DEFAULT_BITMAP_CONFIG, null);
        } finally {
            Mockito.verify(mBitmap).recycle();
            Assert.assertEquals(1, mBitmapCounter.getCount());
            Assert.assertEquals(KitKatPurgeableDecoderTest.MAX_BITMAP_SIZE, mBitmapCounter.getSize());
        }
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testPinBitmapFailure() {
        KitKatPurgeableDecoder decoder = Mockito.mock(KitKatPurgeableDecoder.class);
        PowerMockito.doThrow(new ConcurrentModificationException()).when(decoder).pinBitmap(ArgumentMatchers.any(Bitmap.class));
        decoder.pinBitmap(ArgumentMatchers.any(Bitmap.class));
        try {
            decoder.decodeFromEncodedImage(mEncodedImage, KitKatPurgeableDecoderTest.DEFAULT_BITMAP_CONFIG, null);
        } finally {
            Mockito.verify(mBitmap).recycle();
            Assert.assertEquals(0, mBitmapCounter.getCount());
            Assert.assertEquals(0, mBitmapCounter.getSize());
        }
    }

    private static class BitmapFactoryOptionsMatcher extends ArgumentMatcher<BitmapFactory.Options> {
        @Override
        public boolean matches(Object argument) {
            if (argument == null) {
                return false;
            }
            BitmapFactory.Options options = ((BitmapFactory.Options) (argument));
            return (options.inDither) && (options.inPurgeable);
        }
    }
}

