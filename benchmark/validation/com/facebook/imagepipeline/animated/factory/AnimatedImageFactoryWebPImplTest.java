/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.imagepipeline.animated.factory;


import Bitmap.Config;
import android.graphics.Bitmap;
import com.facebook.animated.webp.WebPImage;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.imagepipeline.animated.impl.AnimatedDrawableBackendProvider;
import com.facebook.imagepipeline.animated.impl.AnimatedImageCompositor;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.testing.MockBitmapFactory;
import com.facebook.imagepipeline.testing.TrivialBufferPooledByteBuffer;
import com.facebook.imagepipeline.testing.TrivialPooledByteBuffer;
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
@PrepareOnlyThisForTest({ WebPImage.class, AnimatedImageFactoryImpl.class, AnimatedImageCompositor.class })
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "androidx.*", "android.*" })
public class AnimatedImageFactoryWebPImplTest {
    private static final Config DEFAULT_BITMAP_CONFIG = Config.ARGB_8888;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

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

    private WebPImage mWebPImageMock;

    @Test
    public void testCreateDefaultsUsingPointer() {
        WebPImage mockWebPImage = Mockito.mock(WebPImage.class);
        // Expect a call to WebPImage.create
        TrivialPooledByteBuffer byteBuffer = AnimatedImageFactoryWebPImplTest.createByteBuffer();
        Mockito.when(mWebPImageMock.decode(byteBuffer.getNativePtr(), byteBuffer.size())).thenReturn(mockWebPImage);
        testCreateDefaults(mockWebPImage, byteBuffer);
    }

    @Test
    public void testCreateDefaultsUsingByteBuffer() {
        WebPImage mockWebPImage = Mockito.mock(WebPImage.class);
        // Expect a call to WebPImage.create
        TrivialBufferPooledByteBuffer byteBuffer = AnimatedImageFactoryWebPImplTest.createDirectByteBuffer();
        Mockito.when(mWebPImageMock.decode(byteBuffer.getByteBuffer())).thenReturn(mockWebPImage);
        testCreateDefaults(mockWebPImage, byteBuffer);
    }

    @Test
    public void testCreateWithPreviewBitmapUsingPointer() throws Exception {
        WebPImage mockWebPImage = Mockito.mock(WebPImage.class);
        Bitmap mockBitmap = MockBitmapFactory.create(50, 50, AnimatedImageFactoryWebPImplTest.DEFAULT_BITMAP_CONFIG);
        // Expect a call to WebPImage.create
        TrivialPooledByteBuffer byteBuffer = AnimatedImageFactoryWebPImplTest.createByteBuffer();
        Mockito.when(mWebPImageMock.decode(byteBuffer.getNativePtr(), byteBuffer.size())).thenReturn(mockWebPImage);
        Mockito.when(mockWebPImage.getWidth()).thenReturn(50);
        Mockito.when(mockWebPImage.getHeight()).thenReturn(50);
        testCreateWithPreviewBitmap(mockWebPImage, byteBuffer, mockBitmap);
    }

    @Test
    public void testCreateWithPreviewBitmapUsingByteBuffer() throws Exception {
        WebPImage mockWebPImage = Mockito.mock(WebPImage.class);
        Bitmap mockBitmap = MockBitmapFactory.create(50, 50, AnimatedImageFactoryWebPImplTest.DEFAULT_BITMAP_CONFIG);
        // Expect a call to WebPImage.create
        TrivialBufferPooledByteBuffer byteBuffer = AnimatedImageFactoryWebPImplTest.createDirectByteBuffer();
        Mockito.when(mWebPImageMock.decode(byteBuffer.getByteBuffer())).thenReturn(mockWebPImage);
        Mockito.when(mockWebPImage.getWidth()).thenReturn(50);
        Mockito.when(mockWebPImage.getHeight()).thenReturn(50);
        testCreateWithPreviewBitmap(mockWebPImage, byteBuffer, mockBitmap);
    }

    @Test
    public void testCreateWithDecodeAlFramesUsingPointer() throws Exception {
        WebPImage mockWebPImage = Mockito.mock(WebPImage.class);
        Bitmap mockBitmap1 = MockBitmapFactory.create(50, 50, AnimatedImageFactoryWebPImplTest.DEFAULT_BITMAP_CONFIG);
        Bitmap mockBitmap2 = MockBitmapFactory.create(50, 50, AnimatedImageFactoryWebPImplTest.DEFAULT_BITMAP_CONFIG);
        // Expect a call to WebPImage.create
        TrivialPooledByteBuffer byteBuffer = AnimatedImageFactoryWebPImplTest.createByteBuffer();
        Mockito.when(mWebPImageMock.decode(byteBuffer.getNativePtr(), byteBuffer.size())).thenReturn(mockWebPImage);
        Mockito.when(mockWebPImage.getWidth()).thenReturn(50);
        Mockito.when(mockWebPImage.getHeight()).thenReturn(50);
        testCreateWithDecodeAlFrames(mockWebPImage, byteBuffer, mockBitmap1, mockBitmap2);
    }

    @Test
    public void testCreateWithDecodeAlFramesUsingByteBuffer() throws Exception {
        WebPImage mockWebPImage = Mockito.mock(WebPImage.class);
        Bitmap mockBitmap1 = MockBitmapFactory.create(50, 50, AnimatedImageFactoryWebPImplTest.DEFAULT_BITMAP_CONFIG);
        Bitmap mockBitmap2 = MockBitmapFactory.create(50, 50, AnimatedImageFactoryWebPImplTest.DEFAULT_BITMAP_CONFIG);
        // Expect a call to WebPImage.create
        TrivialBufferPooledByteBuffer byteBuffer = AnimatedImageFactoryWebPImplTest.createDirectByteBuffer();
        Mockito.when(mWebPImageMock.decode(byteBuffer.getByteBuffer())).thenReturn(mockWebPImage);
        Mockito.when(mockWebPImage.getWidth()).thenReturn(50);
        Mockito.when(mockWebPImage.getHeight()).thenReturn(50);
        testCreateWithDecodeAlFrames(mockWebPImage, byteBuffer, mockBitmap1, mockBitmap2);
    }
}

