/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.platform;


import Bitmap.Config;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.BitmapCounter;
import com.facebook.imagepipeline.memory.BitmapCounterProvider;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link GingerbreadPurgeableDecoder}.
 */
@RunWith(RobolectricTestRunner.class)
@PrepareOnlyThisForTest({ BitmapCounterProvider.class, BitmapFactory.class, DalvikPurgeableDecoder.class, Bitmaps.class })
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
public class GingerbreadPurgeableDecoderTest {
    protected static final Config DEFAULT_BITMAP_CONFIG = Config.ARGB_8888;

    // protected FlexByteArrayPool mFlexByteArrayPool;
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    static {
        SoLoader.setInTestMode();
    }

    protected static final int IMAGE_SIZE = 5;

    protected static final int LENGTH = 10;

    protected static final long POINTER = 1000L;

    protected static final int MAX_BITMAP_COUNT = 2;

    protected static final int MAX_BITMAP_SIZE = (GingerbreadPurgeableDecoderTest.MAX_BITMAP_COUNT) * (MockBitmapFactory.DEFAULT_BITMAP_SIZE);

    protected GingerbreadPurgeableDecoder mGingerbreadPurgeableDecoder;

    protected CloseableReference<PooledByteBuffer> mByteBufferRef;

    protected EncodedImage mEncodedImage;

    protected byte[] mInputBuf;

    protected byte[] mDecodeBuf;

    protected CloseableReference<byte[]> mDecodeBufRef;

    protected Bitmap mBitmap;

    protected BitmapCounter mBitmapCounter;

    @Test(expected = ConcurrentModificationException.class)
    public void testPinBitmapFailure() {
        GingerbreadPurgeableDecoder decoder = Mockito.mock(GingerbreadPurgeableDecoder.class);
        PowerMockito.doThrow(new ConcurrentModificationException()).when(decoder).pinBitmap(ArgumentMatchers.any(Bitmap.class));
        decoder.pinBitmap(ArgumentMatchers.any(Bitmap.class));
        try {
            decoder.decodeFromEncodedImage(mEncodedImage, GingerbreadPurgeableDecoderTest.DEFAULT_BITMAP_CONFIG, null);
        } finally {
            Mockito.verify(mBitmap).recycle();
            Assert.assertEquals(0, mBitmapCounter.getCount());
            Assert.assertEquals(0, mBitmapCounter.getSize());
        }
    }

    @Test
    public void testDecode_Jpeg_Detailed() {
        Assume.assumeNotNull(mGingerbreadPurgeableDecoder);
    }

    @Test
    public void testDecodeJpeg_incomplete() {
        Assume.assumeNotNull(mGingerbreadPurgeableDecoder);
    }
}

