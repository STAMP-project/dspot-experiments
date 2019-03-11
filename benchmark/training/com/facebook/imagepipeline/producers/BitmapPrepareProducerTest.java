/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.producers;


import android.graphics.Bitmap;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BitmapPrepareProducerTest {
    public static final int MIN_BITMAP_SIZE_BYTES = 1000;

    public static final int MAX_BITMAP_SIZE_BYTES = 2000;

    @Mock
    public Producer<CloseableReference<CloseableImage>> mInputProducer;

    @Mock
    public Consumer<CloseableReference<CloseableImage>> mConsumer;

    @Mock
    public ProducerContext mProducerContext;

    private CloseableReference<CloseableImage> mImageReference;

    @Mock
    private CloseableStaticBitmap mCloseableStaticBitmap;

    @Mock
    private Bitmap mBitmap;

    private BitmapPrepareProducer mBitmapPrepareProducer;

    @Test
    public void testProduceResults_whenCalled_thenInputProducerCalled() {
        createBitmapPrepareProducer(false);
        mBitmapPrepareProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mInputProducer, Mockito.times(1)).produceResults(ArgumentMatchers.any(Consumer.class), ArgumentMatchers.eq(mProducerContext));
    }

    @Test
    public void testProduceResults_whenPrefetch_andPreparePrefetchNotEnabled_thenPassThrough() {
        createBitmapPrepareProducer(false);
        Mockito.when(mProducerContext.isPrefetch()).thenReturn(true);
        mBitmapPrepareProducer.produceResults(mConsumer, mProducerContext);
        // note: the given consumer is used and not the BitmapPrepareConsumer
        Mockito.verify(mInputProducer, Mockito.times(1)).produceResults(ArgumentMatchers.eq(mConsumer), ArgumentMatchers.eq(mProducerContext));
    }

    @Test
    public void testProduceResults_whenPrefetch_andPreparePrefetchEnabled_thenNotPassThrough() {
        createBitmapPrepareProducer(true);
        Mockito.when(mProducerContext.isPrefetch()).thenReturn(true);
        mBitmapPrepareProducer.produceResults(mConsumer, mProducerContext);
        // note: the given consumer is used and not the BitmapPrepareConsumer
        Mockito.verify(mInputProducer, Mockito.never()).produceResults(ArgumentMatchers.eq(mConsumer), ArgumentMatchers.eq(mProducerContext));
    }

    @Test
    public void testProduceResults_whenPrefetch_andPreparePrefetchNotEnabled_thenBitmapPrepareToDrawNotCalled() {
        createBitmapPrepareProducer(false);
        Mockito.when(mProducerContext.isPrefetch()).thenReturn(true);
        mBitmapPrepareProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mBitmap, Mockito.never()).prepareToDraw();
    }

    @Test
    public void testProduceResults_whenPrefetch_andPreparePrefetchEnabled_thenBitmapPrepareToDrawCalled() {
        createBitmapPrepareProducer(true);
        Mockito.when(mProducerContext.isPrefetch()).thenReturn(true);
        mBitmapPrepareProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mBitmap, Mockito.times(1)).prepareToDraw();
    }

    @Test
    public void testProduceResults_whenNotPrefetch_thenBitmapPrepareToDrawCalled() {
        createBitmapPrepareProducer(false);
        Mockito.when(mProducerContext.isPrefetch()).thenReturn(false);
        mBitmapPrepareProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mBitmap, Mockito.times(1)).prepareToDraw();
    }

    @Test
    public void testProduceResults_whenNotPrefetchButBitmapTooSmall_thenBitmapPrepareToDrawNotCalled() {
        createBitmapPrepareProducer(false);
        Mockito.when(mProducerContext.isPrefetch()).thenReturn(false);
        // 100 * 9 = 900 (< MIN_BITMAP_SIZE_BYTES)
        Mockito.when(mBitmap.getRowBytes()).thenReturn(100);
        Mockito.when(mBitmap.getHeight()).thenReturn(9);
        mBitmapPrepareProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mBitmap, Mockito.never()).prepareToDraw();
    }

    @Test
    public void testProduceResults_whenNotPrefetchButBitmapTooLarge_thenBitmapPrepareToDrawNotCalled() {
        createBitmapPrepareProducer(false);
        Mockito.when(mProducerContext.isPrefetch()).thenReturn(false);
        // 100 * 21 = 2100 (> MAX_BITMAP_SIZE_BYTES)
        Mockito.when(mBitmap.getRowBytes()).thenReturn(100);
        Mockito.when(mBitmap.getHeight()).thenReturn(21);
        Mockito.when(mBitmap.getByteCount()).thenReturn(((BitmapPrepareProducerTest.MAX_BITMAP_SIZE_BYTES) + 1));
        mBitmapPrepareProducer.produceResults(mConsumer, mProducerContext);
        Mockito.verify(mBitmap, Mockito.never()).prepareToDraw();
    }
}

