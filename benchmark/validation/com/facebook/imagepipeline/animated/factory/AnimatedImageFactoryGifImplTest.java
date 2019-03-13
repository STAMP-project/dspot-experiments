/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.animated.factory;


import Bitmap.Config;
import android.graphics.Bitmap;
import com.facebook.animated.gif.GifImage;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.imagepipeline.animated.impl.AnimatedDrawableBackendProvider;
import com.facebook.imagepipeline.animated.impl.AnimatedImageCompositor;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.testing.MockBitmapFactory;
import com.facebook.imagepipeline.testing.TrivialBufferPooledByteBuffer;
import com.facebook.imagepipeline.testing.TrivialPooledByteBuffer;
import com.facebook.soloader.SoLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link AnimatedImageFactory}
 */
@RunWith(RobolectricTestRunner.class)
@PrepareOnlyThisForTest({ GifImage.class, AnimatedImageFactoryImpl.class, AnimatedImageCompositor.class })
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
public class AnimatedImageFactoryGifImplTest {
    private static final Config DEFAULT_BITMAP_CONFIG = Config.ARGB_8888;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    static {
        SoLoader.setInTestMode();
    }

    private static ResourceReleaser<PooledByteBuffer> FAKE_RESOURCE_RELEASER = new ResourceReleaser<PooledByteBuffer>() {
        @Override
        public void release(PooledByteBuffer value) {
        }
    };

    private static ResourceReleaser<Bitmap> FAKE_BITMAP_RESOURCE_RELEASER = new ResourceReleaser<Bitmap>() {
        @Override
        public void release(Bitmap value) {
        }
    };

    private AnimatedDrawableBackendProvider mMockAnimatedDrawableBackendProvider;

    private PlatformBitmapFactory mMockBitmapFactory;

    private AnimatedImageFactory mAnimatedImageFactory;

    private GifImage mGifImageMock;

    @Test
    public void testCreateDefaultsUsingPointer() {
        GifImage mockGifImage = Mockito.mock(GifImage.class);
        // Expect a call to GifImage.create
        TrivialPooledByteBuffer byteBuffer = createByteBuffer();
        Mockito.when(mGifImageMock.decode(byteBuffer.getNativePtr(), byteBuffer.size())).thenReturn(mockGifImage);
        testCreateDefaults(mockGifImage, byteBuffer);
    }

    @Test
    public void testCreateDefaultsUsingByteBuffer() {
        GifImage mockGifImage = Mockito.mock(GifImage.class);
        // Expect a call to GifImage.create
        TrivialBufferPooledByteBuffer byteBuffer = AnimatedImageFactoryGifImplTest.createDirectByteBuffer();
        Mockito.when(mGifImageMock.decode(byteBuffer.getByteBuffer())).thenReturn(mockGifImage);
        testCreateDefaults(mockGifImage, byteBuffer);
    }

    @Test
    public void testCreateWithPreviewBitmapUsingPointer() throws Exception {
        GifImage mockGifImage = Mockito.mock(GifImage.class);
        Bitmap mockBitmap = MockBitmapFactory.create(50, 50, AnimatedImageFactoryGifImplTest.DEFAULT_BITMAP_CONFIG);
        // Expect a call to WebPImage.create
        TrivialPooledByteBuffer byteBuffer = createByteBuffer();
        Mockito.when(mGifImageMock.decode(byteBuffer.getNativePtr(), byteBuffer.size())).thenReturn(mockGifImage);
        Mockito.when(mockGifImage.getWidth()).thenReturn(50);
        Mockito.when(mockGifImage.getHeight()).thenReturn(50);
        testCreateWithPreviewBitmap(mockGifImage, mockBitmap, byteBuffer);
    }

    @Test
    public void testCreateWithPreviewBitmapUsingByteBuffer() throws Exception {
        GifImage mockGifImage = Mockito.mock(GifImage.class);
        Bitmap mockBitmap = MockBitmapFactory.create(50, 50, AnimatedImageFactoryGifImplTest.DEFAULT_BITMAP_CONFIG);
        // Expect a call to WebPImage.create
        TrivialBufferPooledByteBuffer byteBuffer = AnimatedImageFactoryGifImplTest.createDirectByteBuffer();
        Mockito.when(mGifImageMock.decode(byteBuffer.getByteBuffer())).thenReturn(mockGifImage);
        Mockito.when(mockGifImage.getWidth()).thenReturn(50);
        Mockito.when(mockGifImage.getHeight()).thenReturn(50);
        testCreateWithPreviewBitmap(mockGifImage, mockBitmap, byteBuffer);
    }

    @Test
    public void testCreateWithDecodeAlFramesUsingPointer() throws Exception {
        GifImage mockGifImage = Mockito.mock(GifImage.class);
        Bitmap mockBitmap1 = MockBitmapFactory.create(50, 50, AnimatedImageFactoryGifImplTest.DEFAULT_BITMAP_CONFIG);
        Bitmap mockBitmap2 = MockBitmapFactory.create(50, 50, AnimatedImageFactoryGifImplTest.DEFAULT_BITMAP_CONFIG);
        // Expect a call to GifImage.create
        TrivialPooledByteBuffer byteBuffer = createByteBuffer();
        Mockito.when(mGifImageMock.decode(byteBuffer.getNativePtr(), byteBuffer.size())).thenReturn(mockGifImage);
        Mockito.when(mockGifImage.getWidth()).thenReturn(50);
        Mockito.when(mockGifImage.getHeight()).thenReturn(50);
        testCreateWithDecodeAlFrames(mockGifImage, mockBitmap1, mockBitmap2, byteBuffer);
    }

    @Test
    public void testCreateWithDecodeAlFramesUsingByteBuffer() throws Exception {
        GifImage mockGifImage = Mockito.mock(GifImage.class);
        Bitmap mockBitmap1 = MockBitmapFactory.create(50, 50, AnimatedImageFactoryGifImplTest.DEFAULT_BITMAP_CONFIG);
        Bitmap mockBitmap2 = MockBitmapFactory.create(50, 50, AnimatedImageFactoryGifImplTest.DEFAULT_BITMAP_CONFIG);
        // Expect a call to GifImage.create
        TrivialBufferPooledByteBuffer byteBuffer = AnimatedImageFactoryGifImplTest.createDirectByteBuffer();
        Mockito.when(mGifImageMock.decode(byteBuffer.getByteBuffer())).thenReturn(mockGifImage);
        Mockito.when(mockGifImage.getWidth()).thenReturn(50);
        Mockito.when(mockGifImage.getHeight()).thenReturn(50);
        testCreateWithDecodeAlFrames(mockGifImage, mockBitmap1, mockBitmap2, byteBuffer);
    }
}

