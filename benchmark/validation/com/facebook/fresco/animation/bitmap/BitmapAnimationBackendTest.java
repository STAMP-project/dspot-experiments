/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.fresco.animation.bitmap;


import AnimationBackend.INTRINSIC_DIMENSION_UNSET;
import AnimationInformation.LOOP_COUNT_INFINITE;
import Bitmap.Config;
import Bitmap.Config.ARGB_8888;
import BitmapAnimationBackend.FRAME_TYPE_CACHED;
import BitmapAnimationBackend.FRAME_TYPE_CREATED;
import BitmapAnimationBackend.FRAME_TYPE_FALLBACK;
import BitmapAnimationBackend.FRAME_TYPE_REUSED;
import BitmapAnimationBackend.FrameListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.fresco.animation.backend.AnimationInformation;
import com.facebook.fresco.animation.bitmap.preparation.BitmapFramePreparationStrategy;
import com.facebook.fresco.animation.bitmap.preparation.BitmapFramePreparer;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests {@link BitmapAnimationBackend}
 */
@RunWith(RobolectricTestRunner.class)
public class BitmapAnimationBackendTest {
    @Mock
    public PlatformBitmapFactory mPlatformBitmapFactory;

    @Mock
    public BitmapFrameCache mBitmapFrameCache;

    @Mock
    public AnimationInformation mAnimationInformation;

    @Mock
    public BitmapFrameRenderer mBitmapFrameRenderer;

    @Mock
    public Rect mBounds;

    @Mock
    public Drawable mParentDrawable;

    @Mock
    public Canvas mCanvas;

    @Mock
    public Bitmap mBitmap;

    @Mock
    public ResourceReleaser<Bitmap> mBitmapResourceReleaser;

    @Mock
    public FrameListener mFrameListener;

    @Mock
    public BitmapFramePreparationStrategy mBitmapFramePreparationStrategy;

    @Mock
    public BitmapFramePreparer mBitmapFramePreparer;

    @Captor
    public ArgumentCaptor<CloseableReference<Bitmap>> mCapturedBitmapReference;

    private CloseableReference<Bitmap> mBitmapRefererence;

    private BitmapAnimationBackend mBitmapAnimationBackend;

    @Test
    public void testSetBounds() {
        mBitmapAnimationBackend.setBounds(mBounds);
        Mockito.verify(mBitmapFrameRenderer).setBounds(mBounds);
    }

    @Test
    public void testSetBoundsUpdatesIntrinsicDimensionsWhenBackendDimensionsUnset() {
        int boundsWidth = 160;
        int boundsHeight = 90;
        int backendIntrinsicWidth = AnimationBackend.INTRINSIC_DIMENSION_UNSET;
        int backendIntrinsicHeight = AnimationBackend.INTRINSIC_DIMENSION_UNSET;
        setupBoundsAndRendererDimensions(boundsWidth, boundsHeight, backendIntrinsicWidth, backendIntrinsicHeight);
        mBitmapAnimationBackend.setBounds(mBounds);
        assertThat(mBitmapAnimationBackend.getIntrinsicWidth()).isEqualTo(boundsWidth);
        assertThat(mBitmapAnimationBackend.getIntrinsicHeight()).isEqualTo(boundsHeight);
    }

    @Test
    public void testSetBoundsUpdatesIntrinsicDimensionsWhenBackendDimensionWidthSet() {
        int boundsWidth = 160;
        int boundsHeight = 90;
        int backendIntrinsicWidth = 260;
        int backendIntrinsicHeight = AnimationBackend.INTRINSIC_DIMENSION_UNSET;
        setupBoundsAndRendererDimensions(boundsWidth, boundsHeight, backendIntrinsicWidth, backendIntrinsicHeight);
        mBitmapAnimationBackend.setBounds(mBounds);
        assertThat(mBitmapAnimationBackend.getIntrinsicWidth()).isEqualTo(backendIntrinsicWidth);
        assertThat(mBitmapAnimationBackend.getIntrinsicHeight()).isEqualTo(boundsHeight);
    }

    @Test
    public void testSetBoundsUpdatesIntrinsicDimensionsWhenBackendDimensionHeightSet() {
        int boundsWidth = 160;
        int boundsHeight = 90;
        int backendIntrinsicWidth = AnimationBackend.INTRINSIC_DIMENSION_UNSET;
        int backendIntrinsicHeight = 260;
        setupBoundsAndRendererDimensions(boundsWidth, boundsHeight, backendIntrinsicWidth, backendIntrinsicHeight);
        mBitmapAnimationBackend.setBounds(mBounds);
        assertThat(mBitmapAnimationBackend.getIntrinsicWidth()).isEqualTo(boundsWidth);
        assertThat(mBitmapAnimationBackend.getIntrinsicHeight()).isEqualTo(backendIntrinsicHeight);
    }

    @Test
    public void testSetBoundsUpdatesIntrinsicDimensionsWhenBackendDimensionsSet() {
        int boundsWidth = 160;
        int boundsHeight = 90;
        int backendIntrinsicWidth = 260;
        int backendIntrinsicHeight = 300;
        setupBoundsAndRendererDimensions(boundsWidth, boundsHeight, backendIntrinsicWidth, backendIntrinsicHeight);
        mBitmapAnimationBackend.setBounds(mBounds);
        assertThat(mBitmapAnimationBackend.getIntrinsicWidth()).isEqualTo(backendIntrinsicWidth);
        assertThat(mBitmapAnimationBackend.getIntrinsicHeight()).isEqualTo(backendIntrinsicHeight);
    }

    @Test
    public void testSetBoundsUpdatesIntrinsicDimensionsWhenBackendDimensionsUnsetAndNullBounds() {
        int boundsWidth = 160;
        int boundsHeight = 90;
        int backendIntrinsicWidth = AnimationBackend.INTRINSIC_DIMENSION_UNSET;
        int backendIntrinsicHeight = AnimationBackend.INTRINSIC_DIMENSION_UNSET;
        setupBoundsAndRendererDimensions(boundsWidth, boundsHeight, backendIntrinsicWidth, backendIntrinsicHeight);
        mBitmapAnimationBackend.setBounds(null);
        assertThat(mBitmapAnimationBackend.getIntrinsicWidth()).isEqualTo(INTRINSIC_DIMENSION_UNSET);
        assertThat(mBitmapAnimationBackend.getIntrinsicHeight()).isEqualTo(INTRINSIC_DIMENSION_UNSET);
    }

    @Test
    public void testSetBoundsUpdatesIntrinsicDimensionsWhenBackendDimensionsSetAndNullBounds() {
        int boundsWidth = 160;
        int boundsHeight = 90;
        int backendIntrinsicWidth = 260;
        int backendIntrinsicHeight = 300;
        setupBoundsAndRendererDimensions(boundsWidth, boundsHeight, backendIntrinsicWidth, backendIntrinsicHeight);
        mBitmapAnimationBackend.setBounds(null);
        assertThat(mBitmapAnimationBackend.getIntrinsicWidth()).isEqualTo(backendIntrinsicWidth);
        assertThat(mBitmapAnimationBackend.getIntrinsicHeight()).isEqualTo(backendIntrinsicHeight);
    }

    @Test
    public void testSetBoundsUpdatesIntrinsicDimensionsWhenBackendWidthSetAndNullBounds() {
        int boundsWidth = 160;
        int boundsHeight = 90;
        int backendIntrinsicWidth = 260;
        int backendIntrinsicHeight = AnimationBackend.INTRINSIC_DIMENSION_UNSET;
        setupBoundsAndRendererDimensions(boundsWidth, boundsHeight, backendIntrinsicWidth, backendIntrinsicHeight);
        mBitmapAnimationBackend.setBounds(null);
        assertThat(mBitmapAnimationBackend.getIntrinsicWidth()).isEqualTo(backendIntrinsicWidth);
        assertThat(mBitmapAnimationBackend.getIntrinsicHeight()).isEqualTo(backendIntrinsicHeight);
    }

    @Test
    public void testSetBoundsUpdatesIntrinsicDimensionsWhenBackendHeightSetAndNullBounds() {
        int boundsWidth = 160;
        int boundsHeight = 90;
        int backendIntrinsicWidth = AnimationBackend.INTRINSIC_DIMENSION_UNSET;
        int backendIntrinsicHeight = 400;
        setupBoundsAndRendererDimensions(boundsWidth, boundsHeight, backendIntrinsicWidth, backendIntrinsicHeight);
        mBitmapAnimationBackend.setBounds(null);
        assertThat(mBitmapAnimationBackend.getIntrinsicWidth()).isEqualTo(backendIntrinsicWidth);
        assertThat(mBitmapAnimationBackend.getIntrinsicHeight()).isEqualTo(backendIntrinsicHeight);
    }

    @Test
    public void testGetFrameCount() {
        Mockito.when(mAnimationInformation.getFrameCount()).thenReturn(123);
        assertThat(mBitmapAnimationBackend.getFrameCount()).isEqualTo(123);
    }

    @Test
    public void testGetLoopCount() {
        Mockito.when(mAnimationInformation.getLoopCount()).thenReturn(LOOP_COUNT_INFINITE);
        assertThat(mBitmapAnimationBackend.getLoopCount()).isEqualTo(LOOP_COUNT_INFINITE);
        Mockito.when(mAnimationInformation.getLoopCount()).thenReturn(123);
        assertThat(mBitmapAnimationBackend.getLoopCount()).isEqualTo(123);
    }

    @Test
    public void testGetFrameDuration() {
        Mockito.when(mAnimationInformation.getFrameDurationMs(1)).thenReturn(50);
        Mockito.when(mAnimationInformation.getFrameDurationMs(2)).thenReturn(100);
        assertThat(mBitmapAnimationBackend.getFrameDurationMs(1)).isEqualTo(50);
        assertThat(mBitmapAnimationBackend.getFrameDurationMs(2)).isEqualTo(100);
    }

    @Test
    public void testDrawCachedBitmap() {
        Mockito.when(mBitmapFrameCache.getCachedFrame(ArgumentMatchers.anyInt())).thenReturn(mBitmapRefererence);
        mBitmapAnimationBackend.drawFrame(mParentDrawable, mCanvas, 1);
        Mockito.verify(mFrameListener).onDrawFrameStart(mBitmapAnimationBackend, 1);
        Mockito.verify(mBitmapFrameCache).getCachedFrame(1);
        Mockito.verify(mCanvas).drawBitmap(ArgumentMatchers.eq(mBitmap), ArgumentMatchers.eq(0.0F), ArgumentMatchers.eq(0.0F), ArgumentMatchers.any(Paint.class));
        verifyFramePreparationStrategyCalled(1);
        verifyListenersAndCacheNotified(1, FRAME_TYPE_CACHED);
        assertReferencesClosed();
    }

    @Test
    public void testDrawReusedBitmap() {
        Mockito.when(mBitmapFrameCache.getBitmapToReuseForFrame(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(mBitmapRefererence);
        Mockito.when(mBitmapFrameRenderer.renderFrame(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Bitmap.class))).thenReturn(true);
        mBitmapAnimationBackend.drawFrame(mParentDrawable, mCanvas, 1);
        Mockito.verify(mFrameListener).onDrawFrameStart(mBitmapAnimationBackend, 1);
        Mockito.verify(mBitmapFrameCache).getCachedFrame(1);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(1, 0, 0);
        Mockito.verify(mBitmapFrameRenderer).renderFrame(1, mBitmap);
        Mockito.verify(mCanvas).drawBitmap(ArgumentMatchers.eq(mBitmap), ArgumentMatchers.eq(0.0F), ArgumentMatchers.eq(0.0F), ArgumentMatchers.any(Paint.class));
        verifyFramePreparationStrategyCalled(1);
        verifyListenersAndCacheNotified(1, FRAME_TYPE_REUSED);
        assertReferencesClosed();
    }

    @Test
    public void testDrawNewBitmap() {
        Mockito.when(mPlatformBitmapFactory.createBitmap(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Config.class))).thenReturn(mBitmapRefererence);
        Mockito.when(mBitmapFrameRenderer.renderFrame(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Bitmap.class))).thenReturn(true);
        mBitmapAnimationBackend.drawFrame(mParentDrawable, mCanvas, 2);
        Mockito.verify(mFrameListener).onDrawFrameStart(mBitmapAnimationBackend, 2);
        Mockito.verify(mBitmapFrameCache).getCachedFrame(2);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(2, 0, 0);
        Mockito.verify(mPlatformBitmapFactory).createBitmap(0, 0, ARGB_8888);
        Mockito.verify(mBitmapFrameRenderer).renderFrame(2, mBitmap);
        Mockito.verify(mCanvas).drawBitmap(ArgumentMatchers.eq(mBitmap), ArgumentMatchers.eq(0.0F), ArgumentMatchers.eq(0.0F), ArgumentMatchers.any(Paint.class));
        verifyFramePreparationStrategyCalled(2);
        verifyListenersAndCacheNotified(2, FRAME_TYPE_CREATED);
        assertReferencesClosed();
    }

    @Test
    public void testDrawNewBitmapWithBounds() {
        int width = 160;
        int height = 90;
        Mockito.when(mBounds.width()).thenReturn(width);
        Mockito.when(mBounds.height()).thenReturn(height);
        Mockito.when(mPlatformBitmapFactory.createBitmap(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Config.class))).thenReturn(mBitmapRefererence);
        Mockito.when(mBitmapFrameRenderer.renderFrame(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Bitmap.class))).thenReturn(true);
        Mockito.when(mBitmapFrameRenderer.getIntrinsicWidth()).thenReturn(INTRINSIC_DIMENSION_UNSET);
        Mockito.when(mBitmapFrameRenderer.getIntrinsicHeight()).thenReturn(INTRINSIC_DIMENSION_UNSET);
        mBitmapAnimationBackend.setBounds(mBounds);
        mBitmapAnimationBackend.drawFrame(mParentDrawable, mCanvas, 2);
        Mockito.verify(mFrameListener).onDrawFrameStart(mBitmapAnimationBackend, 2);
        Mockito.verify(mBitmapFrameCache).getCachedFrame(2);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(2, width, height);
        Mockito.verify(mPlatformBitmapFactory).createBitmap(width, height, ARGB_8888);
        Mockito.verify(mBitmapFrameRenderer).renderFrame(2, mBitmap);
        Mockito.verify(mCanvas).drawBitmap(ArgumentMatchers.eq(mBitmap), ArgumentMatchers.isNull(Rect.class), ArgumentMatchers.eq(mBounds), ArgumentMatchers.any(Paint.class));
        verifyFramePreparationStrategyCalled(2);
        verifyListenersAndCacheNotified(2, FRAME_TYPE_CREATED);
        assertReferencesClosed();
    }

    @Test
    public void testDrawFallbackBitmapWhenCreateBitmapNotWorking() {
        Mockito.when(mBitmapFrameCache.getFallbackFrame(ArgumentMatchers.anyInt())).thenReturn(mBitmapRefererence);
        Mockito.when(mBitmapFrameRenderer.renderFrame(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Bitmap.class))).thenReturn(true);
        mBitmapAnimationBackend.drawFrame(mParentDrawable, mCanvas, 3);
        Mockito.verify(mFrameListener).onDrawFrameStart(mBitmapAnimationBackend, 3);
        Mockito.verify(mBitmapFrameCache).getCachedFrame(3);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(3, 0, 0);
        Mockito.verify(mPlatformBitmapFactory).createBitmap(0, 0, ARGB_8888);
        Mockito.verify(mBitmapFrameCache).getFallbackFrame(3);
        Mockito.verify(mCanvas).drawBitmap(ArgumentMatchers.eq(mBitmap), ArgumentMatchers.eq(0.0F), ArgumentMatchers.eq(0.0F), ArgumentMatchers.any(Paint.class));
        verifyFramePreparationStrategyCalled(3);
        verifyListenersNotifiedWithoutCache(3, FRAME_TYPE_FALLBACK);
        assertReferencesClosed();
    }

    @Test
    public void testDrawFallbackBitmapWhenRenderFrameNotWorking() {
        Mockito.when(mBitmapFrameCache.getFallbackFrame(ArgumentMatchers.anyInt())).thenReturn(mBitmapRefererence);
        // Return a different bitmap for PlatformBitmapFactory
        CloseableReference<Bitmap> temporaryBitmap = CloseableReference.of(mBitmap, mBitmapResourceReleaser);
        Mockito.when(mPlatformBitmapFactory.createBitmap(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(Config.class))).thenReturn(temporaryBitmap);
        Mockito.when(mBitmapFrameRenderer.renderFrame(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Bitmap.class))).thenReturn(false);
        mBitmapAnimationBackend.drawFrame(mParentDrawable, mCanvas, 3);
        Mockito.verify(mFrameListener).onDrawFrameStart(mBitmapAnimationBackend, 3);
        Mockito.verify(mBitmapFrameCache).getCachedFrame(3);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(3, 0, 0);
        Mockito.verify(mPlatformBitmapFactory).createBitmap(0, 0, ARGB_8888);
        // Verify that the bitmap has been closed
        assertThat(temporaryBitmap.isValid()).isFalse();
        Mockito.verify(mBitmapFrameCache).getFallbackFrame(3);
        Mockito.verify(mCanvas).drawBitmap(ArgumentMatchers.eq(mBitmap), ArgumentMatchers.eq(0.0F), ArgumentMatchers.eq(0.0F), ArgumentMatchers.any(Paint.class));
        verifyFramePreparationStrategyCalled(3);
        verifyListenersNotifiedWithoutCache(3, FRAME_TYPE_FALLBACK);
        assertReferencesClosed();
    }

    @Test
    public void testDrawNoFrame() {
        mBitmapAnimationBackend.drawFrame(mParentDrawable, mCanvas, 4);
        Mockito.verify(mFrameListener).onDrawFrameStart(mBitmapAnimationBackend, 4);
        Mockito.verify(mBitmapFrameCache).getCachedFrame(4);
        Mockito.verify(mBitmapFrameCache).getBitmapToReuseForFrame(4, 0, 0);
        Mockito.verify(mPlatformBitmapFactory).createBitmap(0, 0, ARGB_8888);
        Mockito.verify(mBitmapFrameCache).getFallbackFrame(4);
        Mockito.verifyNoMoreInteractions(mCanvas, mBitmapFrameCache);
        verifyFramePreparationStrategyCalled(4);
        Mockito.verify(mFrameListener).onFrameDropped(mBitmapAnimationBackend, 4);
    }
}

