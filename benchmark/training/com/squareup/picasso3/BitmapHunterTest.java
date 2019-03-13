/**
 * Copyright (C) 2013 Square, Inc.
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
package com.squareup.picasso3;


import RequestHandler.Callback;
import RequestHandler.Result;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.exifinterface.media.ExifInterface;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowBitmap;
import org.robolectric.shadows.ShadowMatrix;

import static LoadedFrom.MEMORY;


@RunWith(RobolectricTestRunner.class)
public final class BitmapHunterTest {
    @Mock
    Context context;

    @Mock
    Picasso picasso;

    final PlatformLruCache cache = new PlatformLruCache(2048);

    final Stats stats = new Stats(cache);

    @Mock
    Dispatcher dispatcher;

    final Bitmap bitmap = TestUtils.makeBitmap();

    @Test
    public void nullDecodeResponseIsError() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action, null);
        hunter.run();
        Mockito.verify(dispatcher).dispatchFailed(hunter);
    }

    @Test
    public void runWithResultDispatchComplete() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action, bitmap);
        hunter.run();
        Mockito.verify(dispatcher).dispatchComplete(hunter);
    }

    @Test
    public void runWithNoResultDispatchFailed() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action);
        hunter.run();
        Mockito.verify(dispatcher).dispatchFailed(hunter);
    }

    @Test
    public void responseExceptionDispatchFailed() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action, null, new NetworkRequestHandler.ResponseException(504, 0));
        hunter.run();
        Mockito.verify(dispatcher).dispatchFailed(hunter);
    }

    @Test
    public void outOfMemoryDispatchFailed() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        BitmapHunter hunter = new BitmapHunterTest.OOMBitmapHunter(picasso, dispatcher, cache, stats, action);
        hunter.run();
        Exception exception = hunter.getException();
        Mockito.verify(dispatcher).dispatchFailed(hunter);
        assertThat(hunter.getResult()).isNull();
        assertThat(exception).hasMessageThat().contains("BEGIN PICASSO STATS");
        assertThat(exception.getCause()).isInstanceOf(OutOfMemoryError.class);
    }

    @Test
    public void runWithIoExceptionDispatchRetry() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action, null, new IOException());
        hunter.run();
        Mockito.verify(dispatcher).dispatchRetry(hunter);
    }

    @Test
    public void huntDecodesWhenNotInCache() throws Exception {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunterTest.TestableBitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action, bitmap);
        RequestHandler.Result result = hunt();
        assertThat(cache.missCount()).isEqualTo(1);
        Request request = action.request;
        Mockito.verify(hunter.requestHandler).load(ArgumentMatchers.eq(picasso), ArgumentMatchers.eq(request), ArgumentMatchers.any(Callback.class));
        assertThat(result.getBitmap()).isEqualTo(bitmap);
    }

    @Test
    public void huntReturnsWhenResultInCache() throws Exception {
        cache.set(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), bitmap);
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunterTest.TestableBitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action, bitmap);
        RequestHandler.Result result = hunt();
        assertThat(cache.hitCount()).isEqualTo(1);
        Request request = action.request;
        Mockito.verify(hunter.requestHandler, Mockito.never()).load(ArgumentMatchers.eq(picasso), ArgumentMatchers.eq(request), ArgumentMatchers.any(Callback.class));
        assertThat(result.getBitmap()).isEqualTo(bitmap);
    }

    @Test
    public void huntUnrecognizedUri() throws Exception {
        Action action = TestUtils.mockAction(TestUtils.CUSTOM_URI_KEY, TestUtils.CUSTOM_URI);
        BitmapHunter hunter = BitmapHunter.forRequest(picasso, dispatcher, cache, stats, action);
        try {
            hunter.hunt();
            Assert.fail("Unrecognized URI should throw exception.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void huntDecodesWithRequestHandler() throws Exception {
        Action action = TestUtils.mockAction(TestUtils.CUSTOM_URI_KEY, TestUtils.CUSTOM_URI);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new BitmapHunterTest.CustomRequestHandler()), dispatcher, cache, stats, action);
        RequestHandler.Result result = hunter.hunt();
        assertThat(result.getBitmap()).isEqualTo(bitmap);
    }

    @Test
    public void attachSingleRequest() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action1);
        assertThat(hunter.action).isEqualTo(action1);
        hunter.detach(action1);
        hunter.attach(action1);
        assertThat(hunter.action).isEqualTo(action1);
        assertThat(hunter.actions).isNull();
    }

    @Test
    public void attachMultipleRequests() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action1);
        assertThat(hunter.actions).isNull();
        hunter.attach(action2);
        assertThat(hunter.actions).isNotNull();
        assertThat(hunter.actions).hasSize(1);
    }

    @Test
    public void detachSingleRequest() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action);
        assertThat(hunter.action).isNotNull();
        hunter.detach(action);
        assertThat(hunter.action).isNull();
    }

    @Test
    public void detachMultipleRequests() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action);
        hunter.attach(action2);
        hunter.detach(action2);
        assertThat(hunter.action).isNotNull();
        assertThat(hunter.actions).isNotNull();
        assertThat(hunter.actions).isEmpty();
        hunter.detach(action);
        assertThat(hunter.action).isNull();
    }

    @Test
    public void cancelSingleRequest() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action1);
        hunter.future = new FutureTask(Mockito.mock(Runnable.class), Mockito.mock(Object.class));
        assertThat(hunter.isCancelled()).isFalse();
        assertThat(hunter.cancel()).isFalse();
        hunter.detach(action1);
        assertThat(hunter.cancel()).isTrue();
        assertThat(hunter.isCancelled()).isTrue();
    }

    @Test
    public void cancelMultipleRequests() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunter hunter = new BitmapHunterTest.TestableBitmapHunter(picasso, dispatcher, cache, stats, action1);
        hunter.future = new FutureTask(Mockito.mock(Runnable.class), Mockito.mock(Object.class));
        hunter.attach(action2);
        assertThat(hunter.isCancelled()).isFalse();
        assertThat(hunter.cancel()).isFalse();
        hunter.detach(action1);
        hunter.detach(action2);
        assertThat(hunter.cancel()).isTrue();
        assertThat(hunter.isCancelled()).isTrue();
    }

    // ---------------------------------------
    @Test
    public void forContentProviderRequest() {
        Action action = TestUtils.mockAction(TestUtils.CONTENT_KEY_1, TestUtils.CONTENT_1_URL);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new ContentStreamRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(ContentStreamRequestHandler.class);
    }

    @Test
    public void forMediaStoreRequest() {
        Action action = TestUtils.mockAction(TestUtils.MEDIA_STORE_CONTENT_KEY_1, TestUtils.MEDIA_STORE_CONTENT_1_URL);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new MediaStoreRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(MediaStoreRequestHandler.class);
    }

    @Test
    public void forContactsPhotoRequest() {
        Action action = TestUtils.mockAction(TestUtils.CONTACT_KEY_1, TestUtils.CONTACT_URI_1);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new ContactsPhotoRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(ContactsPhotoRequestHandler.class);
    }

    @Test
    public void forContactsThumbnailPhotoRequest() {
        Action action = TestUtils.mockAction(TestUtils.CONTACT_PHOTO_KEY_1, TestUtils.CONTACT_PHOTO_URI_1);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new ContactsPhotoRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(ContactsPhotoRequestHandler.class);
    }

    @Test
    public void forNetworkRequest() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        NetworkRequestHandler requestHandler = new NetworkRequestHandler(TestUtils.UNUSED_CALL_FACTORY, stats);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(requestHandler), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isSameAs(requestHandler);
    }

    @Test
    public void forFileWithAuthorityRequest() {
        Action action = TestUtils.mockAction(TestUtils.FILE_KEY_1, TestUtils.FILE_1_URL);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new FileRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(FileRequestHandler.class);
    }

    @Test
    public void forAndroidBitmapResourceRequest() {
        Resources resources = TestUtils.mockResources(TestUtils.BITMAP_RESOURCE_VALUE);
        Mockito.when(context.getResources()).thenReturn(resources);
        Action action = TestUtils.mockAction(TestUtils.RESOURCE_ID_KEY_1, null, null, TestUtils.RESOURCE_ID_1);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new ResourceRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(ResourceRequestHandler.class);
    }

    @Test
    public void forAndroidBitmapResourceUriWithId() {
        Action action = TestUtils.mockAction(TestUtils.RESOURCE_ID_URI_KEY, TestUtils.RESOURCE_ID_URI);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new ResourceRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(ResourceRequestHandler.class);
    }

    @Test
    public void forAndroidBitmapResourceUriWithType() {
        Action action = TestUtils.mockAction(TestUtils.RESOURCE_TYPE_URI_KEY, TestUtils.RESOURCE_TYPE_URI);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new ResourceRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(ResourceRequestHandler.class);
    }

    @Test
    public void forAndroidXmlResourceRequest() {
        Resources resources = TestUtils.mockResources(TestUtils.XML_RESOURCE_VALUE);
        Mockito.when(context.getResources()).thenReturn(resources);
        Action action = TestUtils.mockAction(TestUtils.RESOURCE_ID_KEY_1, null, null, TestUtils.RESOURCE_ID_1);
        ResourceDrawableRequestHandler requestHandler = ResourceDrawableRequestHandler.create(context, TestUtils.makeLoaderWithDrawable(null));
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(requestHandler), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(ResourceDrawableRequestHandler.class);
    }

    @Test
    public void forAssetRequest() {
        Action action = TestUtils.mockAction(TestUtils.ASSET_KEY_1, TestUtils.ASSET_URI_1);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new AssetRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(AssetRequestHandler.class);
    }

    @Test
    public void forFileWithNoPathSegments() {
        Action action = TestUtils.mockAction("keykeykey", Uri.fromFile(new File("/")));
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new FileRequestHandler(context)), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(FileRequestHandler.class);
    }

    @Test
    public void forCustomRequest() {
        Action action = TestUtils.mockAction(TestUtils.CUSTOM_URI_KEY, TestUtils.CUSTOM_URI);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(new BitmapHunterTest.CustomRequestHandler()), dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isInstanceOf(BitmapHunterTest.CustomRequestHandler.class);
    }

    @Test
    public void forOverrideRequest() {
        Action action = TestUtils.mockAction(TestUtils.ASSET_KEY_1, TestUtils.ASSET_URI_1);
        RequestHandler handler = new AssetRequestHandler(context);
        List<RequestHandler> handlers = Collections.singletonList(handler);
        // Must use non-mock constructor because that is where Picasso's list of handlers is created.
        Picasso picasso = new Picasso(context, dispatcher, TestUtils.UNUSED_CALL_FACTORY, null, cache, null, TestUtils.NO_TRANSFORMERS, handlers, stats, ARGB_8888, false, false);
        BitmapHunter hunter = BitmapHunter.forRequest(picasso, dispatcher, cache, stats, action);
        assertThat(hunter.requestHandler).isEqualTo(handler);
    }

    @Test
    public void sequenceIsIncremented() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        Picasso picasso = TestUtils.mockPicasso();
        BitmapHunter hunter1 = BitmapHunter.forRequest(picasso, dispatcher, cache, stats, action);
        BitmapHunter hunter2 = BitmapHunter.forRequest(picasso, dispatcher, cache, stats, action);
        assertThat(hunter2.sequence).isGreaterThan(hunter1.sequence);
    }

    @Test
    public void getPriorityWithNoRequests() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        NetworkRequestHandler requestHandler = new NetworkRequestHandler(TestUtils.UNUSED_CALL_FACTORY, stats);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(requestHandler), dispatcher, cache, stats, action);
        hunter.detach(action);
        assertThat(hunter.getAction()).isNull();
        assertThat(hunter.getActions()).isNull();
        assertThat(hunter.getPriority()).isEqualTo(Priority.LOW);
    }

    @Test
    public void getPriorityWithSingleRequest() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, Priority.HIGH);
        NetworkRequestHandler requestHandler = new NetworkRequestHandler(TestUtils.UNUSED_CALL_FACTORY, stats);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(requestHandler), dispatcher, cache, stats, action);
        assertThat(hunter.getAction()).isEqualTo(action);
        assertThat(hunter.getActions()).isNull();
        assertThat(hunter.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    public void getPriorityWithMultipleRequests() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, Priority.NORMAL);
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, Priority.HIGH);
        NetworkRequestHandler requestHandler = new NetworkRequestHandler(TestUtils.UNUSED_CALL_FACTORY, stats);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(requestHandler), dispatcher, cache, stats, action1);
        hunter.attach(action2);
        assertThat(hunter.getAction()).isEqualTo(action1);
        assertThat(hunter.getActions()).containsExactly(action2);
        assertThat(hunter.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    public void getPriorityAfterDetach() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, Priority.NORMAL);
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, Priority.HIGH);
        NetworkRequestHandler requestHandler = new NetworkRequestHandler(TestUtils.UNUSED_CALL_FACTORY, stats);
        BitmapHunter hunter = BitmapHunter.forRequest(TestUtils.mockPicasso(requestHandler), dispatcher, cache, stats, action1);
        hunter.attach(action2);
        assertThat(hunter.getAction()).isEqualTo(action1);
        assertThat(hunter.getActions()).containsExactly(action2);
        assertThat(hunter.getPriority()).isEqualTo(Priority.HIGH);
        hunter.detach(action2);
        assertThat(hunter.getAction()).isEqualTo(action1);
        assertThat(hunter.getActions()).isEmpty();
        assertThat(hunter.getPriority()).isEqualTo(Priority.NORMAL);
    }

    @Test
    public void exifRotation() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, ExifInterface.ORIENTATION_ROTATE_90);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("rotate 90.0");
    }

    @Test
    public void exifRotationSizing() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, ExifInterface.ORIENTATION_ROTATE_90);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).contains("scale 1.0 0.5");
    }

    @Test
    public void exifRotationNoSizing() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, ExifInterface.ORIENTATION_ROTATE_90);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).contains("rotate 90.0");
    }

    @Test
    public void rotation90Sizing() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).contains("scale 1.0 0.5");
    }

    @Test
    public void rotation180Sizing() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).contains("scale 0.5 1.0");
    }

    @Test
    public void rotation90WithPivotSizing() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).contains("scale 1.0 0.5");
    }

    @Test
    public void exifVerticalFlip() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, ExifInterface.ORIENTATION_FLIP_VERTICAL);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPostOperations()).containsExactly("scale -1.0 1.0");
        assertThat(shadowMatrix.getPreOperations()).containsExactly("rotate 180.0");
    }

    @Test
    public void exifHorizontalFlip() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, ExifInterface.ORIENTATION_FLIP_HORIZONTAL);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPostOperations()).containsExactly("scale -1.0 1.0");
        assertThat(shadowMatrix.getPreOperations()).doesNotContain("rotate 180.0");
        assertThat(shadowMatrix.getPreOperations()).doesNotContain("rotate 90.0");
        assertThat(shadowMatrix.getPreOperations()).doesNotContain("rotate 270.0");
    }

    @Test
    public void exifTranspose() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, ExifInterface.ORIENTATION_TRANSPOSE);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPostOperations()).containsExactly("scale -1.0 1.0");
        assertThat(shadowMatrix.getPreOperations()).containsExactly("rotate 90.0");
    }

    @Test
    public void exifTransverse() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, ExifInterface.ORIENTATION_TRANSVERSE);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPostOperations()).containsExactly("scale -1.0 1.0");
        assertThat(shadowMatrix.getPreOperations()).containsExactly("rotate 270.0");
    }

    @Test
    public void keepsAspectRationWhileResizingWhenDesiredWidthIs0() {
        Request request = build();
        Bitmap source = Bitmap.createBitmap(40, 20, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(request, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 0.5 0.5");
    }

    @Test
    public void keepsAspectRationWhileResizingWhenDesiredHeightIs0() {
        Request request = build();
        Bitmap source = Bitmap.createBitmap(40, 20, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(request, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 0.5 0.5");
    }

    @Test
    public void centerCropResultMatchesTargetSize() {
        Request request = build();
        Bitmap source = Bitmap.createBitmap(640, 640, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(request, source, 0);
        assertThat(result.getWidth()).isEqualTo(1080);
        assertThat(result.getHeight()).isEqualTo(642);
    }

    @Test
    public void centerCropResultMatchesTargetSizeWhileDesiredWidthIs0() {
        Request request = build();
        Bitmap source = Bitmap.createBitmap(640, 640, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(request, source, 0);
        assertThat(result.getWidth()).isEqualTo(642);
        assertThat(result.getHeight()).isEqualTo(642);
    }

    @Test
    public void centerCropResultMatchesTargetSizeWhileDesiredHeightIs0() {
        Request request = build();
        Bitmap source = Bitmap.createBitmap(640, 640, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(request, source, 0);
        assertThat(result.getWidth()).isEqualTo(1080);
        assertThat(result.getHeight()).isEqualTo(1080);
    }

    @Test
    public void centerInsideResultMatchesTargetSizeWhileDesiredWidthIs0() {
        Request request = build();
        Bitmap source = Bitmap.createBitmap(640, 640, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(request, source, 0);
        assertThat(result.getWidth()).isEqualTo(642);
        assertThat(result.getHeight()).isEqualTo(642);
    }

    @Test
    public void centerInsideResultMatchesTargetSizeWhileDesiredHeightIs0() {
        Request request = build();
        Bitmap source = Bitmap.createBitmap(640, 640, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(request, source, 0);
        assertThat(result.getWidth()).isEqualTo(1080);
        assertThat(result.getHeight()).isEqualTo(1080);
    }

    @Test
    public void exifRotationWithManualRotation() {
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, ExifInterface.ORIENTATION_ROTATE_90);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("rotate 90.0");
        assertThat(shadowMatrix.getSetOperations()).containsEntry("rotate", "-45.0");
    }

    @Test
    public void rotation() {
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getSetOperations()).containsEntry("rotate", "-45.0");
    }

    @Test
    public void pivotRotation() {
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getSetOperations()).containsEntry("rotate", "-45.0 10.0 10.0");
    }

    @Test
    public void resize() {
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 2.0 1.5");
    }

    @Test
    public void centerCropTallTooSmall() {
        Bitmap source = Bitmap.createBitmap(10, 20, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        assertThat(shadowBitmap.getCreatedFromX()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromY()).isEqualTo(5);
        assertThat(shadowBitmap.getCreatedFromWidth()).isEqualTo(10);
        assertThat(shadowBitmap.getCreatedFromHeight()).isEqualTo(10);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 4.0 4.0");
    }

    @Test
    public void centerCropTallTooLarge() {
        Bitmap source = Bitmap.createBitmap(100, 200, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        assertThat(shadowBitmap.getCreatedFromX()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromY()).isEqualTo(50);
        assertThat(shadowBitmap.getCreatedFromWidth()).isEqualTo(100);
        assertThat(shadowBitmap.getCreatedFromHeight()).isEqualTo(100);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 0.5 0.5");
    }

    @Test
    public void centerCropWideTooSmall() {
        Bitmap source = Bitmap.createBitmap(20, 10, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        assertThat(shadowBitmap.getCreatedFromX()).isEqualTo(5);
        assertThat(shadowBitmap.getCreatedFromY()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromWidth()).isEqualTo(10);
        assertThat(shadowBitmap.getCreatedFromHeight()).isEqualTo(10);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 4.0 4.0");
    }

    @Test
    public void centerCropWithGravityHorizontalLeft() {
        Bitmap source = Bitmap.createBitmap(20, 10, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        assertThat(shadowBitmap.getCreatedFromX()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromY()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromWidth()).isEqualTo(10);
        assertThat(shadowBitmap.getCreatedFromHeight()).isEqualTo(10);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 4.0 4.0");
    }

    @Test
    public void centerCropWithGravityHorizontalRight() {
        Bitmap source = Bitmap.createBitmap(20, 10, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        assertThat(shadowBitmap.getCreatedFromX()).isEqualTo(10);
        assertThat(shadowBitmap.getCreatedFromY()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromWidth()).isEqualTo(10);
        assertThat(shadowBitmap.getCreatedFromHeight()).isEqualTo(10);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 4.0 4.0");
    }

    @Test
    public void centerCropWithGravityVerticalTop() {
        Bitmap source = Bitmap.createBitmap(10, 20, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        assertThat(shadowBitmap.getCreatedFromX()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromY()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromWidth()).isEqualTo(10);
        assertThat(shadowBitmap.getCreatedFromHeight()).isEqualTo(10);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 4.0 4.0");
    }

    @Test
    public void centerCropWithGravityVerticalBottom() {
        Bitmap source = Bitmap.createBitmap(10, 20, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        assertThat(shadowBitmap.getCreatedFromX()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromY()).isEqualTo(10);
        assertThat(shadowBitmap.getCreatedFromWidth()).isEqualTo(10);
        assertThat(shadowBitmap.getCreatedFromHeight()).isEqualTo(10);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 4.0 4.0");
    }

    @Test
    public void centerCropWideTooLarge() {
        Bitmap source = Bitmap.createBitmap(200, 100, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        assertThat(shadowBitmap.getCreatedFromX()).isEqualTo(50);
        assertThat(shadowBitmap.getCreatedFromY()).isEqualTo(0);
        assertThat(shadowBitmap.getCreatedFromWidth()).isEqualTo(100);
        assertThat(shadowBitmap.getCreatedFromHeight()).isEqualTo(100);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 0.5 0.5");
    }

    @Test
    public void centerInsideTallTooSmall() {
        Bitmap source = Bitmap.createBitmap(20, 10, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 2.5 2.5");
    }

    @Test
    public void centerInsideTallTooLarge() {
        Bitmap source = Bitmap.createBitmap(100, 50, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 0.5 0.5");
    }

    @Test
    public void centerInsideWideTooSmall() {
        Bitmap source = Bitmap.createBitmap(10, 20, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 2.5 2.5");
    }

    @Test
    public void centerInsideWideTooLarge() {
        Bitmap source = Bitmap.createBitmap(50, 100, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 0.5 0.5");
    }

    @Test
    public void onlyScaleDownOriginalBigger() {
        Bitmap source = Bitmap.createBitmap(100, 100, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 0.5 0.5");
    }

    @Test
    public void onlyScaleDownOriginalSmaller() {
        Bitmap source = Bitmap.createBitmap(50, 50, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        assertThat(result).isSameAs(source);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isNull();
        assertThat(shadowBitmap.getCreatedFromBitmap()).isNotSameAs(source);
    }

    @Test
    public void onlyScaleDownOriginalSmallerWidthIs0() {
        Bitmap source = Bitmap.createBitmap(50, 50, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        assertThat(result).isSameAs(source);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isNull();
    }

    @Test
    public void onlyScaleDownOriginalSmallerHeightIs0() {
        Bitmap source = Bitmap.createBitmap(50, 50, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        assertThat(result).isSameAs(source);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isNull();
    }

    @Test
    public void onlyScaleDownOriginalBiggerWidthIs0() {
        Bitmap source = Bitmap.createBitmap(50, 50, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 0.8 0.8");
    }

    @Test
    public void onlyScaleDownOriginalBiggerHeightIs0() {
        Bitmap source = Bitmap.createBitmap(50, 50, ARGB_8888);
        Request data = build();
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        ShadowBitmap shadowBitmap = shadowOf(result);
        assertThat(shadowBitmap.getCreatedFromBitmap()).isSameAs(source);
        Matrix matrix = shadowBitmap.getCreatedFromMatrix();
        ShadowMatrix shadowMatrix = shadowOf(matrix);
        assertThat(shadowMatrix.getPreOperations()).containsExactly("scale 0.8 0.8");
    }

    @Test
    public void reusedBitmapIsNotRecycled() {
        Request data = build();
        Bitmap source = Bitmap.createBitmap(10, 10, ARGB_8888);
        Bitmap result = MatrixTransformation.transformResult(data, source, 0);
        assertThat(result).isSameAs(source);
        assertThat(result.isRecycled()).isFalse();
    }

    @Test
    public void crashingOnTransformationThrows() {
        Transformation badTransformation = new Transformation() {
            @Override
            public Result transform(RequestHandler.Result source) {
                throw new NullPointerException("hello");
            }

            @Override
            public String key() {
                return "test";
            }
        };
        List<Transformation> transformations = Collections.singletonList(badTransformation);
        Bitmap original = Bitmap.createBitmap(10, 10, ARGB_8888);
        RequestHandler.Result result = new RequestHandler.Result(original, MEMORY, 0);
        Request data = build();
        try {
            BitmapHunter.applyTransformations(picasso, data, transformations, result);
            Assert.fail("Expected exception to be thrown.");
        } catch (RuntimeException e) {
            assertThat(e).hasMessageThat().isEqualTo((("Transformation " + (badTransformation.key())) + " crashed with exception."));
        }
    }

    @Test
    public void nullResultFromTransformationThrows() {
        Transformation badTransformation = new Transformation() {
            @Override
            public Result transform(RequestHandler.Result source) {
                return null;
            }

            @Override
            public String key() {
                return "test";
            }
        };
        List<Transformation> transformations = Collections.singletonList(badTransformation);
        Bitmap original = Bitmap.createBitmap(10, 10, ARGB_8888);
        RequestHandler.Result result = new RequestHandler.Result(original, MEMORY, 0);
        Request data = build();
        try {
            BitmapHunter.applyTransformations(picasso, data, transformations, result);
            Assert.fail("Expected exception to be thrown.");
        } catch (RuntimeException e) {
            assertThat(e).hasMessageThat().contains((("Transformation " + (badTransformation.key())) + " returned null"));
        }
    }

    @Test
    public void recycledTransformationBitmapThrows() {
        Transformation badTransformation = new Transformation() {
            @Override
            public Result transform(RequestHandler.Result source) {
                source.getBitmap().recycle();
                return source;
            }

            @Override
            public String key() {
                return "test";
            }
        };
        List<Transformation> transformations = Collections.singletonList(badTransformation);
        Bitmap original = Bitmap.createBitmap(10, 10, ARGB_8888);
        RequestHandler.Result result = new RequestHandler.Result(original, MEMORY, 0);
        Request data = build();
        try {
            BitmapHunter.applyTransformations(picasso, data, transformations, result);
            Assert.fail("Expected exception to be thrown.");
        } catch (RuntimeException e) {
            assertThat(e).hasMessageThat().isEqualTo((("Transformation " + (badTransformation.key())) + " returned a recycled Bitmap."));
        }
    }

    @Test
    public void transformDrawables() {
        final AtomicInteger transformationCount = new AtomicInteger();
        Transformation identity = new Transformation() {
            @Override
            public Result transform(RequestHandler.Result source) {
                transformationCount.incrementAndGet();
                return source;
            }

            @Override
            public String key() {
                return "test";
            }
        };
        List<Transformation> transformations = Arrays.asList(identity, identity, identity);
        Drawable original = new android.graphics.drawable.BitmapDrawable(Bitmap.createBitmap(10, 10, ARGB_8888));
        RequestHandler.Result result = new RequestHandler.Result(original, MEMORY);
        Request data = build();
        BitmapHunter.applyTransformations(picasso, data, transformations, result);
        assertThat(transformationCount.get()).isEqualTo(3);
    }

    private static class TestableBitmapHunter extends BitmapHunter {
        TestableBitmapHunter(Picasso picasso, Dispatcher dispatcher, PlatformLruCache cache, Stats stats, Action action) {
            this(picasso, dispatcher, cache, stats, action, null);
        }

        TestableBitmapHunter(Picasso picasso, Dispatcher dispatcher, PlatformLruCache cache, Stats stats, Action action, Bitmap result) {
            this(picasso, dispatcher, cache, stats, action, result, null);
        }

        TestableBitmapHunter(Picasso picasso, Dispatcher dispatcher, PlatformLruCache cache, Stats stats, Action action, Bitmap result, Exception exception) {
            super(picasso, dispatcher, cache, stats, action, Mockito.spy(new BitmapHunterTest.TestableRequestHandler(result, exception)));
        }
    }

    private static class TestableRequestHandler extends RequestHandler {
        private final Bitmap bitmap;

        private final Exception exception;

        TestableRequestHandler(Bitmap bitmap, Exception exception) {
            this.bitmap = bitmap;
            this.exception = exception;
        }

        @Override
        public boolean canHandleRequest(@NonNull
        Request data) {
            return true;
        }

        @Override
        public void load(@NonNull
        Picasso picasso, @NonNull
        Request request, @NonNull
        Callback callback) {
            if ((exception) != null) {
                callback.onError(exception);
            } else {
                callback.onSuccess(new Result(bitmap, LoadedFrom.MEMORY));
            }
        }

        @Override
        int getRetryCount() {
            return 1;
        }
    }

    private static class OOMBitmapHunter extends BitmapHunter {
        OOMBitmapHunter(Picasso picasso, Dispatcher dispatcher, PlatformLruCache cache, Stats stats, Action action) {
            super(picasso, dispatcher, cache, stats, action, Mockito.spy(new BitmapHunterTest.OOMRequestHandler()));
        }
    }

    private static class OOMRequestHandler extends BitmapHunterTest.TestableRequestHandler {
        OOMRequestHandler() {
            super(null, null);
        }

        @Override
        public void load(@NonNull
        Picasso picasso, @NonNull
        Request request, @NonNull
        Callback callback) {
            callback.onError(new OutOfMemoryError());
        }
    }

    private class CustomRequestHandler extends RequestHandler {
        @Override
        public boolean canHandleRequest(@NonNull
        Request data) {
            return TestUtils.CUSTOM_URI.getScheme().equals(data.uri.getScheme());
        }

        @Override
        public void load(@NonNull
        Picasso picasso, @NonNull
        Request request, @NonNull
        Callback callback) {
            callback.onSuccess(new Result(bitmap, LoadedFrom.MEMORY));
        }
    }
}

