/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.fresco.animation.bitmap.cache;


import android.graphics.Bitmap;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link FrescoFrameCache}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CloseableReference.class)
public class FrescoFrameCacheTest {
    @Mock
    public CloseableReference<CloseableImage> mImageReference;

    @Mock
    public CloseableStaticBitmap mCloseableStaticBitmap;

    @Mock
    public CloseableReference<Bitmap> mBitmapReference;

    @Mock
    public CloseableReference<Bitmap> mBitmapReferenceClone;

    @Mock
    public Bitmap mUnderlyingBitmap;

    @Test
    public void testExtractAndClose() throws Exception {
        CloseableReference<Bitmap> extractedReference = FrescoFrameCache.convertToBitmapReferenceAndClose(mImageReference);
        assertThat(extractedReference).isNotNull();
        assertThat(extractedReference.get()).isEqualTo(mUnderlyingBitmap);
        Mockito.verify(mImageReference).close();
        extractedReference.close();
    }

    @Test
    public void testExtractAndClose_whenBitmapRecycled_thenReturnReference() throws Exception {
        Mockito.when(mUnderlyingBitmap.isRecycled()).thenReturn(true);
        CloseableReference<Bitmap> extractedReference = FrescoFrameCache.convertToBitmapReferenceAndClose(mImageReference);
        // We only detach the reference and do not care if the bitmap is valid
        assertThat(extractedReference).isNotNull();
        assertThat(extractedReference.get()).isEqualTo(mUnderlyingBitmap);
        Mockito.verify(mImageReference).close();
        extractedReference.close();
    }

    @Test
    public void testExtractAndClose_whenBitmapReferenceInvalid_thenReturnReference() throws Exception {
        Mockito.when(mBitmapReference.isValid()).thenReturn(false);
        CloseableReference<Bitmap> extractedReference = FrescoFrameCache.convertToBitmapReferenceAndClose(mImageReference);
        // We only detach the reference and do not care if the bitmap reference is valid
        assertThat(extractedReference).isNotNull();
        assertThat(extractedReference.get()).isEqualTo(mUnderlyingBitmap);
        extractedReference.close();
        Mockito.verify(mImageReference).close();
    }

    @Test
    public void testExtractAndClose_whenCloseableStaticBitmapClosed_thenReturnNull() throws Exception {
        Mockito.when(mCloseableStaticBitmap.isClosed()).thenReturn(true);
        Mockito.when(mCloseableStaticBitmap.cloneUnderlyingBitmapReference()).thenReturn(null);
        CloseableReference<Bitmap> extractedReference = FrescoFrameCache.convertToBitmapReferenceAndClose(mImageReference);
        // We only detach the reference and do not care if the bitmap is valid
        assertThat(extractedReference).isNull();
        Mockito.verify(mImageReference).close();
    }

    @Test
    public void testExtractAndClose_whenImageReferenceInvalid_thenReturnNull() throws Exception {
        Mockito.when(mImageReference.isValid()).thenReturn(false);
        CloseableReference<Bitmap> extractedReference = FrescoFrameCache.convertToBitmapReferenceAndClose(mImageReference);
        // We only detach the reference and do not care if the bitmap is valid
        assertThat(extractedReference).isNull();
        Mockito.verify(mImageReference).close();
    }

    @Test
    public void testExtractAndClose_whenInputNull_thenReturnNull() throws Exception {
        CloseableReference<Bitmap> extractedReference = FrescoFrameCache.convertToBitmapReferenceAndClose(null);
        assertThat(extractedReference).isNull();
        Mockito.verifyZeroInteractions(mImageReference);
    }

    @Test
    public void testExtractAndClose_whenCloseableStaticBitmapNull_thenReturnNull() throws Exception {
        Mockito.when(mImageReference.get()).thenReturn(null);
        CloseableReference<Bitmap> extractedReference = FrescoFrameCache.convertToBitmapReferenceAndClose(mImageReference);
        assertThat(extractedReference).isNull();
        Mockito.verify(mImageReference).close();
    }
}

