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


import MemoryPolicy.NO_CACHE;
import RequestHandler.Result;
import android.R.drawable.picture_frame;
import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.RemoteViews;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;


@RunWith(RobolectricTestRunner.class)
public class RequestCreatorTest {
    @Mock
    Picasso picasso;

    @Captor
    ArgumentCaptor<Action> actionCaptor;

    final Bitmap bitmap = TestUtils.makeBitmap();

    @Test
    public void getOnMainCrashes() throws IOException {
        try {
            get();
            Assert.fail("Calling get() on main thread should throw exception");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void loadWithShutdownCrashes() {
        picasso.shutdown = true;
        try {
            fetch();
            Assert.fail("Should have crashed with a shutdown picasso.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void getReturnsNullIfNullUriAndResourceId() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Bitmap[] result = new Bitmap[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result[0] = get();
                } catch (IOException e) {
                    Assert.fail(e.getMessage());
                } finally {
                    latch.countDown();
                }
            }
        }).start();
        latch.await();
        assertThat(result[0]).isNull();
        Mockito.verifyZeroInteractions(picasso);
    }

    @Test
    public void fetchSubmitsFetchRequest() {
        fetch();
        Mockito.verify(picasso).submit(actionCaptor.capture());
        assertThat(actionCaptor.getValue()).isInstanceOf(FetchAction.class);
    }

    @Test
    public void fetchWithFitThrows() {
        try {
            fetch();
            Assert.fail("Calling fetch() with fit() should throw an exception");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void fetchWithDefaultPriority() {
        fetch();
        Mockito.verify(picasso).submit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.LOW);
    }

    @Test
    public void fetchWithCustomPriority() {
        fetch();
        Mockito.verify(picasso).submit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.HIGH);
    }

    @Test
    public void fetchWithCache() {
        Mockito.when(picasso.quickMemoryCacheCheck(TestUtils.URI_KEY_1)).thenReturn(bitmap);
        fetch();
        Mockito.verify(picasso, Mockito.never()).enqueueAndSubmit(ArgumentMatchers.any(Action.class));
    }

    @Test
    public void fetchWithMemoryPolicyNoCache() {
        fetch();
        Mockito.verify(picasso, Mockito.never()).quickMemoryCacheCheck(TestUtils.URI_KEY_1);
        Mockito.verify(picasso).submit(actionCaptor.capture());
    }

    @Test
    public void intoTargetWithNullThrows() {
        try {
            new RequestCreator(picasso, TestUtils.URI_1, 0).into(((BitmapTarget) (null)));
            Assert.fail("Calling into() with null Target should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoTargetWithFitThrows() {
        try {
            BitmapTarget target = TestUtils.mockTarget();
            fit().into(target);
            Assert.fail("Calling into() target with fit() should throw exception");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void intoTargetNoPlaceholderCallsWithNull() {
        BitmapTarget target = TestUtils.mockTarget();
        new RequestCreator(picasso, TestUtils.URI_1, 0).noPlaceholder().into(target);
        Mockito.verify(target).onPrepareLoad(null);
    }

    @Test
    public void intoTargetWithNullUriAndResourceIdSkipsAndCancels() {
        BitmapTarget target = TestUtils.mockTarget();
        Drawable placeHolderDrawable = Mockito.mock(Drawable.class);
        new RequestCreator(picasso, null, 0).placeholder(placeHolderDrawable).into(target);
        Mockito.verify(picasso).cancelRequest(target);
        Mockito.verify(target).onPrepareLoad(placeHolderDrawable);
        Mockito.verifyNoMoreInteractions(picasso);
    }

    @Test
    public void intoTargetWithQuickMemoryCacheCheckDoesNotSubmit() {
        Mockito.when(picasso.quickMemoryCacheCheck(TestUtils.URI_KEY_1)).thenReturn(bitmap);
        BitmapTarget target = TestUtils.mockTarget();
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(target);
        Mockito.verify(target).onBitmapLoaded(bitmap, LoadedFrom.MEMORY);
        Mockito.verify(picasso).cancelRequest(target);
        Mockito.verify(picasso, Mockito.never()).enqueueAndSubmit(ArgumentMatchers.any(Action.class));
    }

    @Test
    public void intoTargetWithSkipMemoryPolicy() {
        BitmapTarget target = TestUtils.mockTarget();
        new RequestCreator(picasso, TestUtils.URI_1, 0).memoryPolicy(NO_CACHE).into(target);
        Mockito.verify(picasso, Mockito.never()).quickMemoryCacheCheck(TestUtils.URI_KEY_1);
    }

    @Test
    public void intoTargetAndNotInCacheSubmitsTargetRequest() {
        BitmapTarget target = TestUtils.mockTarget();
        Drawable placeHolderDrawable = Mockito.mock(Drawable.class);
        new RequestCreator(picasso, TestUtils.URI_1, 0).placeholder(placeHolderDrawable).into(target);
        Mockito.verify(target).onPrepareLoad(placeHolderDrawable);
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue()).isInstanceOf(BitmapTargetAction.class);
    }

    @Test
    public void targetActionWithDefaultPriority() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.NORMAL);
    }

    @Test
    public void targetActionWithCustomPriority() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).priority(Priority.HIGH).into(TestUtils.mockTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.HIGH);
    }

    @Test
    public void targetActionWithDefaultTag() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().getTag()).isEqualTo(actionCaptor.getValue());
    }

    @Test
    public void targetActionWithCustomTag() {
        tag("tag").into(TestUtils.mockTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().getTag()).isEqualTo("tag");
    }

    @Test
    public void intoImageViewWithNullThrows() {
        try {
            new RequestCreator(picasso, TestUtils.URI_1, 0).into(((ImageView) (null)));
            Assert.fail("Calling into() with null ImageView should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoImageViewWithNullUriAndResourceIdSkipsAndCancels() {
        ImageView target = TestUtils.mockImageViewTarget();
        new RequestCreator(picasso, null, 0).into(target);
        Mockito.verify(picasso).cancelRequest(target);
        Mockito.verify(picasso, Mockito.never()).quickMemoryCacheCheck(ArgumentMatchers.anyString());
        Mockito.verify(picasso, Mockito.never()).enqueueAndSubmit(ArgumentMatchers.any(Action.class));
    }

    @Test
    public void intoImageViewWithQuickMemoryCacheCheckDoesNotSubmit() {
        PlatformLruCache cache = new PlatformLruCache(0);
        Picasso picasso = Mockito.spy(new Picasso(RuntimeEnvironment.application, Mockito.mock(Dispatcher.class), TestUtils.UNUSED_CALL_FACTORY, null, cache, null, TestUtils.NO_TRANSFORMERS, TestUtils.NO_HANDLERS, Mockito.mock(Stats.class), ARGB_8888, false, false));
        Mockito.doReturn(bitmap).when(picasso).quickMemoryCacheCheck(TestUtils.URI_KEY_1);
        ImageView target = TestUtils.mockImageViewTarget();
        Callback callback = TestUtils.mockCallback();
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(target, callback);
        Mockito.verify(target).setImageDrawable(ArgumentMatchers.any(PicassoDrawable.class));
        Mockito.verify(callback).onSuccess();
        Mockito.verify(picasso).cancelRequest(target);
        Mockito.verify(picasso, Mockito.never()).enqueueAndSubmit(ArgumentMatchers.any(Action.class));
    }

    @Test
    public void intoImageViewSetsPlaceholderDrawable() {
        PlatformLruCache cache = new PlatformLruCache(0);
        Picasso picasso = Mockito.spy(new Picasso(RuntimeEnvironment.application, Mockito.mock(Dispatcher.class), TestUtils.UNUSED_CALL_FACTORY, null, cache, null, TestUtils.NO_TRANSFORMERS, TestUtils.NO_HANDLERS, Mockito.mock(Stats.class), ARGB_8888, false, false));
        ImageView target = TestUtils.mockImageViewTarget();
        Drawable placeHolderDrawable = Mockito.mock(Drawable.class);
        new RequestCreator(picasso, TestUtils.URI_1, 0).placeholder(placeHolderDrawable).into(target);
        Mockito.verify(target).setImageDrawable(placeHolderDrawable);
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue()).isInstanceOf(ImageViewAction.class);
    }

    @Test
    public void intoImageViewNoPlaceholderDrawable() {
        PlatformLruCache cache = new PlatformLruCache(0);
        Picasso picasso = Mockito.spy(new Picasso(RuntimeEnvironment.application, Mockito.mock(Dispatcher.class), TestUtils.UNUSED_CALL_FACTORY, null, cache, null, TestUtils.NO_TRANSFORMERS, TestUtils.NO_HANDLERS, Mockito.mock(Stats.class), ARGB_8888, false, false));
        ImageView target = TestUtils.mockImageViewTarget();
        new RequestCreator(picasso, TestUtils.URI_1, 0).noPlaceholder().into(target);
        Mockito.verifyNoMoreInteractions(target);
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue()).isInstanceOf(ImageViewAction.class);
    }

    @Test
    public void intoImageViewSetsPlaceholderWithResourceId() {
        PlatformLruCache cache = new PlatformLruCache(0);
        Picasso picasso = Mockito.spy(new Picasso(RuntimeEnvironment.application, Mockito.mock(Dispatcher.class), TestUtils.UNUSED_CALL_FACTORY, null, cache, null, TestUtils.NO_TRANSFORMERS, TestUtils.NO_HANDLERS, Mockito.mock(Stats.class), ARGB_8888, false, false));
        ImageView target = TestUtils.mockImageViewTarget();
        new RequestCreator(picasso, TestUtils.URI_1, 0).placeholder(picture_frame).into(target);
        ArgumentCaptor<Drawable> drawableCaptor = ArgumentCaptor.forClass(Drawable.class);
        Mockito.verify(target).setImageDrawable(drawableCaptor.capture());
        assertThat(shadowOf(drawableCaptor.getValue()).getCreatedFromResId()).isEqualTo(picture_frame);
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue()).isInstanceOf(ImageViewAction.class);
    }

    @Test
    public void cancelNotOnMainThreadCrashes() throws InterruptedException {
        Mockito.doCallRealMethod().when(picasso).cancelRequest(ArgumentMatchers.any(BitmapTarget.class));
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new RequestCreator(picasso, null, 0).into(TestUtils.mockTarget());
                    Assert.fail("Should have thrown IllegalStateException");
                } catch (IllegalStateException ignored) {
                } finally {
                    latch.countDown();
                }
            }
        }).start();
        latch.await();
    }

    @Test
    public void intoNotOnMainThreadCrashes() throws InterruptedException {
        Mockito.doCallRealMethod().when(picasso).enqueueAndSubmit(ArgumentMatchers.any(Action.class));
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockImageViewTarget());
                    Assert.fail("Should have thrown IllegalStateException");
                } catch (IllegalStateException ignored) {
                } finally {
                    latch.countDown();
                }
            }
        }).start();
        latch.await();
    }

    @Test
    public void intoImageViewAndNotInCacheSubmitsImageViewRequest() {
        ImageView target = TestUtils.mockImageViewTarget();
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(target);
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue()).isInstanceOf(ImageViewAction.class);
    }

    @Test
    public void intoImageViewWithFitAndNoDimensionsQueuesDeferredImageViewRequest() {
        ImageView target = TestUtils.mockFitImageViewTarget(true);
        Mockito.when(target.getWidth()).thenReturn(0);
        Mockito.when(target.getHeight()).thenReturn(0);
        fit().into(target);
        Mockito.verify(picasso, Mockito.never()).enqueueAndSubmit(ArgumentMatchers.any(Action.class));
        Mockito.verify(picasso).defer(ArgumentMatchers.eq(target), ArgumentMatchers.any(DeferredRequestCreator.class));
    }

    @Test
    public void intoImageViewWithFitAndDimensionsQueuesImageViewRequest() {
        ImageView target = TestUtils.mockFitImageViewTarget(true);
        Mockito.when(target.getWidth()).thenReturn(100);
        Mockito.when(target.getHeight()).thenReturn(100);
        fit().into(target);
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue()).isInstanceOf(ImageViewAction.class);
    }

    @Test
    public void intoImageViewWithSkipMemoryCachePolicy() {
        ImageView target = TestUtils.mockImageViewTarget();
        new RequestCreator(picasso, TestUtils.URI_1, 0).memoryPolicy(NO_CACHE).into(target);
        Mockito.verify(picasso, Mockito.never()).quickMemoryCacheCheck(TestUtils.URI_KEY_1);
    }

    @Test
    public void intoImageViewWithFitAndResizeThrows() {
        try {
            ImageView target = TestUtils.mockImageViewTarget();
            fit().resize(10, 10).into(target);
            Assert.fail("Calling into() ImageView with fit() and resize() should throw exception");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void imageViewActionWithDefaultPriority() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockImageViewTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.NORMAL);
    }

    @Test
    public void imageViewActionWithCustomPriority() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).priority(Priority.HIGH).into(TestUtils.mockImageViewTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.HIGH);
    }

    @Test
    public void imageViewActionWithDefaultTag() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockImageViewTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().getTag()).isEqualTo(actionCaptor.getValue());
    }

    @Test
    public void imageViewActionWithCustomTag() {
        tag("tag").into(TestUtils.mockImageViewTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().getTag()).isEqualTo("tag");
    }

    @Test
    public void intoRemoteViewsWidgetQueuesAppWidgetAction() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockRemoteViews(), 0, new int[]{ 1, 2, 3 });
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue()).isInstanceOf(RemoteViewsAction.AppWidgetAction.class);
    }

    @Test
    public void intoRemoteViewsNotificationQueuesNotificationAction() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockRemoteViews(), 0, 0, TestUtils.mockNotification());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue()).isInstanceOf(RemoteViewsAction.NotificationAction.class);
    }

    @Test
    public void intoRemoteViewsNotificationWithNullRemoteViewsThrows() {
        try {
            new RequestCreator(picasso, TestUtils.URI_1, 0).into(null, 0, 0, TestUtils.mockNotification());
            Assert.fail("Calling into() with null RemoteViews should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoRemoteViewsWidgetWithPlaceholderDrawableThrows() {
        try {
            new RequestCreator(picasso, TestUtils.URI_1, 0).placeholder(new ColorDrawable(0)).into(TestUtils.mockRemoteViews(), 0, new int[]{ 1, 2, 3 });
            Assert.fail("Calling into() with placeholder drawable should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoRemoteViewsWidgetWithErrorDrawableThrows() {
        try {
            new RequestCreator(picasso, TestUtils.URI_1, 0).error(new ColorDrawable(0)).into(TestUtils.mockRemoteViews(), 0, new int[]{ 1, 2, 3 });
            Assert.fail("Calling into() with error drawable should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoRemoteViewsNotificationWithPlaceholderDrawableThrows() {
        try {
            new RequestCreator(picasso, TestUtils.URI_1, 0).placeholder(new ColorDrawable(0)).into(TestUtils.mockRemoteViews(), 0, 0, TestUtils.mockNotification());
            Assert.fail("Calling into() with error drawable should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoRemoteViewsNotificationWithErrorDrawableThrows() {
        try {
            new RequestCreator(picasso, TestUtils.URI_1, 0).error(new ColorDrawable(0)).into(TestUtils.mockRemoteViews(), 0, 0, TestUtils.mockNotification());
            Assert.fail("Calling into() with error drawable should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoRemoteViewsWidgetWithNullRemoteViewsThrows() {
        try {
            into(null, 0, new int[]{ 1, 2, 3 });
            Assert.fail("Calling into() with null RemoteViews should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoRemoteViewsWidgetWithNullAppWidgetIdsThrows() {
        try {
            new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockRemoteViews(), 0, null);
            Assert.fail("Calling into() with null appWidgetIds should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoRemoteViewsNotificationWithNullNotificationThrows() {
        try {
            new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockRemoteViews(), 0, 0, ((Notification) (null)));
            Assert.fail("Calling into() with null Notification should throw exception");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void intoRemoteViewsWidgetWithFitThrows() {
        try {
            RemoteViews remoteViews = TestUtils.mockRemoteViews();
            fit().into(remoteViews, 1, new int[]{ 1, 2, 3 });
            Assert.fail("Calling fit() into remote views should throw exception");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void intoRemoteViewsNotificationWithFitThrows() {
        try {
            RemoteViews remoteViews = TestUtils.mockRemoteViews();
            fit().into(remoteViews, 1, 1, TestUtils.mockNotification());
            Assert.fail("Calling fit() into remote views should throw exception");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void intoTargetNoResizeWithCenterInsideOrCenterCropThrows() {
        try {
            centerInside().into(TestUtils.mockTarget());
            Assert.fail("Center inside with unknown width should throw exception.");
        } catch (IllegalStateException ignored) {
        }
        try {
            centerCrop().into(TestUtils.mockTarget());
            Assert.fail("Center inside with unknown height should throw exception.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void appWidgetActionWithDefaultPriority() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockRemoteViews(), 0, new int[]{ 1, 2, 3 });
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.NORMAL);
    }

    @Test
    public void appWidgetActionWithCustomPriority() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).priority(Priority.HIGH).into(TestUtils.mockRemoteViews(), 0, new int[]{ 1, 2, 3 });
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.HIGH);
    }

    @Test
    public void notificationActionWithDefaultPriority() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockRemoteViews(), 0, 0, TestUtils.mockNotification());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.NORMAL);
    }

    @Test
    public void notificationActionWithCustomPriority() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).priority(Priority.HIGH).into(TestUtils.mockRemoteViews(), 0, 0, TestUtils.mockNotification());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.priority).isEqualTo(Priority.HIGH);
    }

    @Test
    public void appWidgetActionWithDefaultTag() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockRemoteViews(), 0, new int[]{ 1, 2, 3 });
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().getTag()).isEqualTo(actionCaptor.getValue());
    }

    @Test
    public void appWidgetActionWithCustomTag() {
        tag("tag").into(TestUtils.mockRemoteViews(), 0, new int[]{ 1, 2, 3 });
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().getTag()).isEqualTo("tag");
    }

    @Test
    public void notificationActionWithDefaultTag() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockRemoteViews(), 0, 0, TestUtils.mockNotification());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().getTag()).isEqualTo(actionCaptor.getValue());
    }

    @Test
    public void notificationActionWithCustomTag() {
        tag("tag").into(TestUtils.mockRemoteViews(), 0, 0, TestUtils.mockNotification());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().getTag()).isEqualTo("tag");
    }

    @Test
    public void nullMemoryPolicy() {
        try {
            new RequestCreator().memoryPolicy(null);
            Assert.fail("Null memory policy should throw exception.");
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void nullAdditionalMemoryPolicy() {
        try {
            new RequestCreator().memoryPolicy(NO_CACHE, ((MemoryPolicy[]) (null)));
            Assert.fail("Null additional memory policy should throw exception.");
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void nullMemoryPolicyAssholeStyle() {
        try {
            new RequestCreator().memoryPolicy(NO_CACHE, new MemoryPolicy[]{ null });
            Assert.fail("Null additional memory policy should throw exception.");
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void nullNetworkPolicy() {
        try {
            new RequestCreator().networkPolicy(null);
            Assert.fail("Null network policy should throw exception.");
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void nullAdditionalNetworkPolicy() {
        try {
            new RequestCreator().networkPolicy(NetworkPolicy.NO_CACHE, ((NetworkPolicy[]) (null)));
            Assert.fail("Null additional network policy should throw exception.");
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void nullNetworkPolicyAssholeStyle() {
        try {
            new RequestCreator().networkPolicy(NetworkPolicy.NO_CACHE, new NetworkPolicy[]{ null });
            Assert.fail("Null additional network policy should throw exception.");
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    public void invalidResize() {
        try {
            new RequestCreator().resize((-1), 10);
            Assert.fail("Negative width should throw exception.");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            new RequestCreator().resize(10, (-1));
            Assert.fail("Negative height should throw exception.");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            new RequestCreator().resize(0, 0);
            Assert.fail("Zero dimensions should throw exception.");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void invalidCenterCrop() {
        try {
            centerCrop();
            Assert.fail("Calling center crop after center inside should throw exception.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void invalidCenterInside() {
        try {
            centerCrop();
            Assert.fail("Calling center inside after center crop should throw exception.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void invalidPlaceholderImage() {
        try {
            new RequestCreator().placeholder(0);
            Assert.fail("Resource ID of zero should throw exception.");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            new RequestCreator().placeholder(1).placeholder(new ColorDrawable(0));
            Assert.fail("Two placeholders should throw exception.");
        } catch (IllegalStateException ignored) {
        }
        try {
            new RequestCreator().placeholder(new ColorDrawable(0)).placeholder(1);
            Assert.fail("Two placeholders should throw exception.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void invalidNoPlaceholder() {
        try {
            new RequestCreator().noPlaceholder().placeholder(new ColorDrawable(0));
            Assert.fail("Placeholder after no placeholder should throw exception.");
        } catch (IllegalStateException ignored) {
        }
        try {
            new RequestCreator().noPlaceholder().placeholder(1);
            Assert.fail("Placeholder after no placeholder should throw exception.");
        } catch (IllegalStateException ignored) {
        }
        try {
            new RequestCreator().placeholder(1).noPlaceholder();
            Assert.fail("No placeholder after placeholder should throw exception.");
        } catch (IllegalStateException ignored) {
        }
        try {
            new RequestCreator().placeholder(new ColorDrawable(0)).noPlaceholder();
            Assert.fail("No placeholder after placeholder should throw exception.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void invalidErrorImage() {
        try {
            new RequestCreator().error(0);
            Assert.fail("Resource ID of zero should throw exception.");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            new RequestCreator().error(null);
            Assert.fail("Null drawable should throw exception.");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            new RequestCreator().error(1).error(new ColorDrawable(0));
            Assert.fail("Two placeholders should throw exception.");
        } catch (IllegalStateException ignored) {
        }
        try {
            new RequestCreator().error(new ColorDrawable(0)).error(1);
            Assert.fail("Two placeholders should throw exception.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void invalidPriority() {
        try {
            new RequestCreator().priority(null);
            Assert.fail("Null priority should throw exception.");
        } catch (NullPointerException ignored) {
        }
        try {
            new RequestCreator().priority(Priority.LOW).priority(Priority.HIGH);
            Assert.fail("Two priorities should throw exception.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test
    public void invalidTag() {
        try {
            new RequestCreator().tag(null);
            Assert.fail("Null tag should throw exception.");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            tag("tag2");
            Assert.fail("Two tags should throw exception.");
        } catch (IllegalStateException ignored) {
        }
    }

    @Test(expected = NullPointerException.class)
    public void nullTransformationsInvalid() {
        new RequestCreator().transform(((Transformation) (null)));
    }

    @Test(expected = NullPointerException.class)
    public void nullTransformationListInvalid() {
        new RequestCreator().transform(((List<Transformation>) (null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullKeyTransformationInvalid() {
        new RequestCreator().transform(new Transformation() {
            @Override
            public Result transform(RequestHandler.Result source) {
                return source;
            }

            @Override
            public String key() {
                return null;
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullKeyInTransformationListInvalid() {
        List<? extends Transformation> transformations = Collections.singletonList(new Transformation() {
            @Override
            public Result transform(RequestHandler.Result source) {
                return source;
            }

            @Override
            public String key() {
                return null;
            }
        });
        new RequestCreator().transform(transformations);
    }

    @Test
    public void transformationListImplementationValid() {
        List<TestTransformation> transformations = Collections.singletonList(new TestTransformation("test"));
        new RequestCreator().transform(transformations);
        // TODO verify something!
    }

    @Test
    public void nullTargetsInvalid() {
        try {
            new RequestCreator().into(((ImageView) (null)));
            Assert.fail("Null ImageView should throw exception.");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            new RequestCreator().into(((BitmapTarget) (null)));
            Assert.fail("Null Target should throw exception.");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void imageViewActionWithStableKey() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).stableKey(TestUtils.STABLE_1).into(TestUtils.mockImageViewTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.key).isEqualTo(TestUtils.STABLE_URI_KEY_1);
    }

    @Test
    public void imageViewActionWithStableKeyNull() {
        stableKey(null).into(TestUtils.mockImageViewTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.key).isEqualTo(TestUtils.URI_KEY_1);
    }

    @Test
    public void notPurgeable() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).into(TestUtils.mockImageViewTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.purgeable).isFalse();
    }

    @Test
    public void purgeable() {
        new RequestCreator(picasso, TestUtils.URI_1, 0).purgeable().into(TestUtils.mockImageViewTarget());
        Mockito.verify(picasso).enqueueAndSubmit(actionCaptor.capture());
        assertThat(actionCaptor.getValue().request.purgeable).isTrue();
    }
}

