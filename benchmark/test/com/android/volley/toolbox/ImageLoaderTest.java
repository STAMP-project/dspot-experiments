/**
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.volley.toolbox;


import ImageLoader.ImageCache;
import ImageLoader.ImageContainer;
import ImageLoader.ImageListener;
import ImageView.ScaleType;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ImageLoaderTest {
    private RequestQueue mRequestQueue;

    private ImageCache mImageCache;

    private ImageLoader mImageLoader;

    @Test
    public void isCachedChecksCache() throws Exception {
        Mockito.when(mImageCache.getBitmap(ArgumentMatchers.anyString())).thenReturn(null);
        Assert.assertFalse(mImageLoader.isCached("http://foo", 0, 0));
    }

    @Test
    public void getWithCacheHit() throws Exception {
        Bitmap bitmap = Bitmap.createBitmap(1, 1, null);
        ImageLoader.ImageListener listener = Mockito.mock(ImageListener.class);
        Mockito.when(mImageCache.getBitmap(ArgumentMatchers.anyString())).thenReturn(bitmap);
        ImageLoader.ImageContainer ic = mImageLoader.get("http://foo", listener);
        Assert.assertSame(bitmap, ic.getBitmap());
        Mockito.verify(listener).onResponse(ic, true);
    }

    @Test
    public void getWithCacheMiss() throws Exception {
        Mockito.when(mImageCache.getBitmap(ArgumentMatchers.anyString())).thenReturn(null);
        ImageLoader.ImageListener listener = Mockito.mock(ImageListener.class);
        // Ask for the image to be loaded.
        mImageLoader.get("http://foo", listener);
        // Second pass to test deduping logic.
        mImageLoader.get("http://foo", listener);
        // Response callback should be called both times.
        Mockito.verify(listener, Mockito.times(2)).onResponse(ArgumentMatchers.any(ImageContainer.class), ArgumentMatchers.eq(true));
        // But request should be enqueued only once.
        Mockito.verify(mRequestQueue, Mockito.times(1)).add(ArgumentMatchers.any(Request.class));
    }

    @Test
    public void publicMethods() throws Exception {
        // Catch API breaking changes.
        ImageLoader.getImageListener(null, (-1), (-1));
        mImageLoader.setBatchedResponseDelay(1000);
        Assert.assertNotNull(ImageLoader.class.getConstructor(RequestQueue.class, ImageCache.class));
        Assert.assertNotNull(ImageLoader.class.getMethod("getImageListener", ImageView.class, int.class, int.class));
        Assert.assertNotNull(ImageLoader.class.getMethod("isCached", String.class, int.class, int.class));
        Assert.assertNotNull(ImageLoader.class.getMethod("isCached", String.class, int.class, int.class, ScaleType.class));
        Assert.assertNotNull(ImageLoader.class.getMethod("get", String.class, ImageListener.class));
        Assert.assertNotNull(ImageLoader.class.getMethod("get", String.class, ImageListener.class, int.class, int.class));
        Assert.assertNotNull(ImageLoader.class.getMethod("get", String.class, ImageListener.class, int.class, int.class, ScaleType.class));
        Assert.assertNotNull(ImageLoader.class.getMethod("setBatchedResponseDelay", int.class));
        Assert.assertNotNull(ImageListener.class.getMethod("onResponse", ImageContainer.class, boolean.class));
    }
}

