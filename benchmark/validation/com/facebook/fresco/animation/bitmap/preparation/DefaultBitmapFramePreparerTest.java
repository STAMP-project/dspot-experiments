/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.fresco.animation.bitmap.preparation;


import Bitmap.Config;
import BitmapAnimationBackend.FRAME_TYPE_CREATED;
import BitmapAnimationBackend.FRAME_TYPE_REUSED;
import android.graphics.Bitmap;
import com.facebook.common.references.CloseableReference;
import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.fresco.animation.bitmap.BitmapFrameCache;
import com.facebook.fresco.animation.bitmap.BitmapFrameRenderer;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.testing.FakeClock;
import com.facebook.imagepipeline.testing.TestExecutorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests {@link DefaultBitmapFramePreparer}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CloseableReference.class)
public class DefaultBitmapFramePreparerTest {
    private static final int FRAME_COUNT = 10;

    private static final int BACKEND_INTRINSIC_WIDTH = 160;

    private static final int BACKEND_INTRINSIC_HEIGHT = 90;

    private static final Config BITMAP_CONFIG = Config.ARGB_8888;

    @Mock
    public AnimationBackend mAnimationBackend;

    @Mock
    public BitmapFrameCache mBitmapFrameCache;

    @Mock
    public PlatformBitmapFactory mPlatformBitmapFactory;

    @Mock
    public BitmapFrameRenderer mBitmapFrameRenderer;

    @Mock
    public CloseableReference<Bitmap> mBitmapReference;

    @Mock
    public Bitmap mBitmap;

    private FakeClock mFakeClock;

    private TestExecutorService mExecutorService;

    private DefaultBitmapFramePreparer mDefaultBitmapFramePreparer;

    @Test
    public void testPrepareFrame_whenBitmapAlreadyCached_thenDoNothing() {
        Mockito.when(mBitmapFrameCache.contains(1)).thenReturn(true);
        Mockito.when(mBitmapFrameRenderer.renderFrame(1, mBitmap)).thenReturn(true);
        mDefaultBitmapFramePreparer.prepareFrame(mBitmapFrameCache, mAnimationBackend, 1);
        assertThat(mExecutorService.getScheduledQueue().isIdle()).isTrue();
        Mockito.verify(mBitmapFrameCache).contains(1);
        Mockito.verifyNoMoreInteractions(mBitmapFrameCache);
        Mockito.verifyZeroInteractions(mPlatformBitmapFactory, mBitmapFrameRenderer, mBitmapReference);
    }

    @Test
    public void testPrepareFrame_whenNoBitmapAvailable_thenDoNothing() {
        mDefaultBitmapFramePreparer.prepareFrame(mBitmapFrameCache, mAnimationBackend, 1);
        Mockito.verify(mBitmapFrameCache).contains(1);
        Mockito.verifyNoMoreInteractions(mBitmapFrameCache);
        Mockito.reset(mBitmapFrameCache);
        mExecutorService.getScheduledQueue().runNextPendingCommand();
        Mockito.verify(mBitmapFrameCache).contains(1);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(1, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT);
        Mockito.verifyNoMoreInteractions(mBitmapFrameCache);
        Mockito.verify(mPlatformBitmapFactory).createBitmap(DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT, DefaultBitmapFramePreparerTest.BITMAP_CONFIG);
        Mockito.verifyZeroInteractions(mBitmapFrameRenderer);
    }

    @Test
    public void testPrepareFrame_whenReusedBitmapAvailable_thenCacheReusedBitmap() {
        Mockito.when(mBitmapFrameCache.getBitmapToReuseForFrame(1, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT)).thenReturn(mBitmapReference);
        Mockito.when(mBitmapFrameRenderer.renderFrame(1, mBitmap)).thenReturn(true);
        mDefaultBitmapFramePreparer.prepareFrame(mBitmapFrameCache, mAnimationBackend, 1);
        mExecutorService.getScheduledQueue().runNextPendingCommand();
        Mockito.verify(mBitmapFrameCache, Mockito.times(2)).contains(1);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(1, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT);
        Mockito.verify(mBitmapFrameRenderer).renderFrame(1, mBitmap);
        Mockito.verify(mBitmapFrameCache).onFramePrepared(1, mBitmapReference, FRAME_TYPE_REUSED);
        Mockito.verifyZeroInteractions(mPlatformBitmapFactory);
    }

    @Test
    public void testPrepareFrame_whenPlatformBitmapAvailable_thenCacheCreatedBitmap() {
        Mockito.when(mPlatformBitmapFactory.createBitmap(DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT, DefaultBitmapFramePreparerTest.BITMAP_CONFIG)).thenReturn(mBitmapReference);
        Mockito.when(mBitmapFrameRenderer.renderFrame(1, mBitmap)).thenReturn(true);
        mDefaultBitmapFramePreparer.prepareFrame(mBitmapFrameCache, mAnimationBackend, 1);
        mExecutorService.getScheduledQueue().runNextPendingCommand();
        Mockito.verify(mBitmapFrameCache, Mockito.times(2)).contains(1);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(1, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT);
        Mockito.verify(mPlatformBitmapFactory).createBitmap(DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT, DefaultBitmapFramePreparerTest.BITMAP_CONFIG);
        Mockito.verify(mBitmapFrameRenderer).renderFrame(1, mBitmap);
        Mockito.verify(mBitmapFrameCache).onFramePrepared(1, mBitmapReference, FRAME_TYPE_CREATED);
        Mockito.verifyNoMoreInteractions(mPlatformBitmapFactory);
    }

    @Test
    public void testPrepareFrame_whenReusedAndPlatformBitmapAvailable_thenCacheReusedBitmap() {
        Mockito.when(mBitmapFrameCache.getBitmapToReuseForFrame(1, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT)).thenReturn(mBitmapReference);
        Mockito.when(mPlatformBitmapFactory.createBitmap(DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT, DefaultBitmapFramePreparerTest.BITMAP_CONFIG)).thenReturn(mBitmapReference);
        Mockito.when(mBitmapFrameRenderer.renderFrame(1, mBitmap)).thenReturn(true);
        mDefaultBitmapFramePreparer.prepareFrame(mBitmapFrameCache, mAnimationBackend, 1);
        mExecutorService.getScheduledQueue().runNextPendingCommand();
        Mockito.verify(mBitmapFrameCache, Mockito.times(2)).contains(1);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(1, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT);
        Mockito.verify(mBitmapFrameRenderer).renderFrame(1, mBitmap);
        Mockito.verify(mBitmapFrameCache).onFramePrepared(1, mBitmapReference, FRAME_TYPE_REUSED);
        Mockito.verifyZeroInteractions(mPlatformBitmapFactory);
    }

    @Test
    public void testPrepareFrame_whenRenderingFails_thenDoNothing() {
        Mockito.when(mBitmapFrameCache.getBitmapToReuseForFrame(1, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT)).thenReturn(mBitmapReference);
        Mockito.when(mPlatformBitmapFactory.createBitmap(DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT, DefaultBitmapFramePreparerTest.BITMAP_CONFIG)).thenReturn(mBitmapReference);
        Mockito.when(mBitmapFrameRenderer.renderFrame(1, mBitmap)).thenReturn(false);
        mDefaultBitmapFramePreparer.prepareFrame(mBitmapFrameCache, mAnimationBackend, 1);
        mExecutorService.getScheduledQueue().runNextPendingCommand();
        Mockito.verify(mBitmapFrameCache, Mockito.times(2)).contains(1);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(1, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT);
        Mockito.verify(mPlatformBitmapFactory).createBitmap(DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_WIDTH, DefaultBitmapFramePreparerTest.BACKEND_INTRINSIC_HEIGHT, DefaultBitmapFramePreparerTest.BITMAP_CONFIG);
        Mockito.verify(mBitmapFrameRenderer, Mockito.times(2)).renderFrame(1, mBitmap);
        Mockito.verifyNoMoreInteractions(mBitmapFrameCache);
    }
}

