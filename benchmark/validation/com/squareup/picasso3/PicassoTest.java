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


import RequestHandler.Result;
import RuntimeEnvironment.application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import com.squareup.picasso3.Picasso.RequestTransformer;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import okhttp3.Cache;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static LoadedFrom.MEMORY;


// Works around https://github.com/robolectric/robolectric/issues/2566.
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public final class PicassoTest {
    private static final int NUM_BUILTIN_HANDLERS = 8;

    private static final int NUM_BUILTIN_TRANSFORMERS = 0;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    Context context;

    @Mock
    Dispatcher dispatcher;

    @Mock
    RequestHandler requestHandler;

    final PlatformLruCache cache = new PlatformLruCache(2048);

    @Mock
    Picasso.Listener listener;

    @Mock
    Stats stats;

    private Picasso picasso;

    final Bitmap bitmap = TestUtils.makeBitmap();

    @Test
    public void submitWithTargetInvokesDispatcher() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        assertThat(picasso.targetToAction).isEmpty();
        picasso.enqueueAndSubmit(action);
        assertThat(picasso.targetToAction).hasSize(1);
        Mockito.verify(dispatcher).dispatchSubmit(action);
    }

    @Test
    public void submitWithSameActionDoesNotCancel() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        picasso.enqueueAndSubmit(action);
        Mockito.verify(dispatcher).dispatchSubmit(action);
        assertThat(picasso.targetToAction).hasSize(1);
        assertThat(picasso.targetToAction.containsValue(action)).isTrue();
        picasso.enqueueAndSubmit(action);
        Mockito.verify(action, Mockito.never()).cancel();
        Mockito.verify(dispatcher, Mockito.never()).dispatchCancel(action);
    }

    @Test
    public void quickMemoryCheckReturnsBitmapIfInCache() {
        cache.set(TestUtils.URI_KEY_1, bitmap);
        Bitmap cached = picasso.quickMemoryCacheCheck(TestUtils.URI_KEY_1);
        assertThat(cached).isEqualTo(bitmap);
        Mockito.verify(stats).dispatchCacheHit();
    }

    @Test
    public void quickMemoryCheckReturnsNullIfNotInCache() {
        Bitmap cached = picasso.quickMemoryCacheCheck(TestUtils.URI_KEY_1);
        assertThat(cached).isNull();
        Mockito.verify(stats).dispatchCacheMiss();
    }

    @Test
    public void completeInvokesSuccessOnAllSuccessfulRequests() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        Action action2 = TestUtils.mockCanceledAction();
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap, MEMORY));
        Mockito.when(hunter.getActions()).thenReturn(Arrays.asList(action1, action2));
        picasso.complete(hunter);
        verifyActionComplete(action1);
        Mockito.verify(action2, Mockito.never()).complete(ArgumentMatchers.any(Result.class));
    }

    @Test
    public void completeInvokesErrorOnAllFailedRequests() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        Action action2 = TestUtils.mockCanceledAction();
        Exception exception = Mockito.mock(Exception.class);
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, null);
        Mockito.when(hunter.getException()).thenReturn(exception);
        Mockito.when(hunter.getActions()).thenReturn(Arrays.asList(action1, action2));
        picasso.complete(hunter);
        Mockito.verify(action1).error(exception);
        Mockito.verify(action2, Mockito.never()).error(exception);
        Mockito.verify(listener).onImageLoadFailed(picasso, TestUtils.URI_1, exception);
    }

    @Test
    public void completeDeliversToSingle() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap, MEMORY));
        Mockito.when(hunter.getAction()).thenReturn(action);
        Mockito.when(hunter.getActions()).thenReturn(Collections.<Action>emptyList());
        picasso.complete(hunter);
        verifyActionComplete(action);
    }

    @Test
    public void completeWithReplayDoesNotRemove() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        action.willReplay = true;
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap, MEMORY));
        Mockito.when(hunter.getAction()).thenReturn(action);
        picasso.enqueueAndSubmit(action);
        assertThat(picasso.targetToAction).hasSize(1);
        picasso.complete(hunter);
        assertThat(picasso.targetToAction).hasSize(1);
        verifyActionComplete(action);
    }

    @Test
    public void completeDeliversToSingleAndMultiple() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockImageViewTarget());
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap, MEMORY));
        Mockito.when(hunter.getAction()).thenReturn(action);
        Mockito.when(hunter.getActions()).thenReturn(Arrays.asList(action2));
        picasso.complete(hunter);
        verifyActionComplete(action);
        verifyActionComplete(action2);
    }

    @Test
    public void completeSkipsIfNoActions() {
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap, MEMORY));
        picasso.complete(hunter);
        Mockito.verify(hunter).getAction();
        Mockito.verify(hunter).getActions();
        Mockito.verifyNoMoreInteractions(hunter);
    }

    @Test
    public void resumeActionTriggersSubmitOnPausedAction() {
        Request request = build();
        Action action = new Action(TestUtils.mockPicasso(), request) {
            @Override
            void complete(RequestHandler.Result result) {
                Assert.fail("Test execution should not call this method");
            }

            @Override
            void error(Exception e) {
                Assert.fail("Test execution should not call this method");
            }

            @NonNull
            @Override
            Object getTarget() {
                return this;
            }
        };
        picasso.resumeAction(action);
        Mockito.verify(dispatcher).dispatchSubmit(action);
    }

    @Test
    public void resumeActionImmediatelyCompletesCachedRequest() {
        cache.set(TestUtils.URI_KEY_1, bitmap);
        Request request = build();
        Action action = new Action(TestUtils.mockPicasso(), request) {
            @Override
            void complete(RequestHandler.Result result) {
                assertThat(result.getBitmap()).isEqualTo(bitmap);
                assertThat(result.getLoadedFrom()).isEqualTo(LoadedFrom.MEMORY);
            }

            @Override
            void error(Exception e) {
                Assert.fail("Reading from memory cache should not throw an exception");
            }

            @NonNull
            @Override
            Object getTarget() {
                return this;
            }
        };
        picasso.resumeAction(action);
    }

    @Test
    public void cancelExistingRequestWithUnknownTarget() {
        ImageView target = TestUtils.mockImageViewTarget();
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, target);
        picasso.cancelRequest(target);
        Mockito.verifyZeroInteractions(action, dispatcher);
    }

    @Test
    public void cancelExistingRequestWithNullImageView() {
        try {
            picasso.cancelRequest(((ImageView) (null)));
            Assert.fail("Canceling with a null ImageView should throw exception.");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void cancelExistingRequestWithNullTarget() {
        try {
            picasso.cancelRequest(((BitmapTarget) (null)));
            Assert.fail("Canceling with a null target should throw exception.");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void cancelExistingRequestWithImageViewTarget() {
        ImageView target = TestUtils.mockImageViewTarget();
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, target);
        picasso.enqueueAndSubmit(action);
        assertThat(picasso.targetToAction).hasSize(1);
        picasso.cancelRequest(target);
        assertThat(picasso.targetToAction).isEmpty();
        Mockito.verify(action).cancel();
        Mockito.verify(dispatcher).dispatchCancel(action);
    }

    @Test
    public void cancelExistingRequestWithDeferredImageViewTarget() {
        ImageView target = TestUtils.mockImageViewTarget();
        DeferredRequestCreator deferredRequestCreator = TestUtils.mockDeferredRequestCreator();
        picasso.targetToDeferredRequestCreator.put(target, deferredRequestCreator);
        picasso.cancelRequest(target);
        Mockito.verify(deferredRequestCreator).cancel();
        assertThat(picasso.targetToDeferredRequestCreator).isEmpty();
    }

    @Test
    public void enqueueingDeferredRequestCancelsThePreviousOne() {
        ImageView target = TestUtils.mockImageViewTarget();
        DeferredRequestCreator firstRequestCreator = TestUtils.mockDeferredRequestCreator();
        picasso.defer(target, firstRequestCreator);
        assertThat(picasso.targetToDeferredRequestCreator).containsKey(target);
        DeferredRequestCreator secondRequestCreator = TestUtils.mockDeferredRequestCreator();
        picasso.defer(target, secondRequestCreator);
        Mockito.verify(firstRequestCreator).cancel();
        assertThat(picasso.targetToDeferredRequestCreator).containsKey(target);
    }

    @Test
    public void cancelExistingRequestWithTarget() {
        BitmapTarget target = TestUtils.mockTarget();
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, target);
        picasso.enqueueAndSubmit(action);
        assertThat(picasso.targetToAction).hasSize(1);
        picasso.cancelRequest(target);
        assertThat(picasso.targetToAction).isEmpty();
        Mockito.verify(action).cancel();
        Mockito.verify(dispatcher).dispatchCancel(action);
    }

    @Test
    public void cancelExistingRequestWithNullRemoteViews() {
        try {
            picasso.cancelRequest(null, 0);
            Assert.fail("Canceling with a null RemoteViews should throw exception.");
        } catch (NullPointerException expected) {
        }
    }

    // This test fails on 23 so restore the default level.
    @Config(sdk = 16)
    @Test
    public void cancelExistingRequestWithRemoteViewTarget() {
        int layoutId = 0;
        int viewId = 1;
        RemoteViews remoteViews = new RemoteViews("packageName", layoutId);
        RemoteViewsAction.RemoteViewsTarget target = new RemoteViewsAction.RemoteViewsTarget(remoteViews, viewId);
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, target);
        picasso.enqueueAndSubmit(action);
        assertThat(picasso.targetToAction).hasSize(1);
        picasso.cancelRequest(remoteViews, viewId);
        assertThat(picasso.targetToAction).isEmpty();
        Mockito.verify(action).cancel();
        Mockito.verify(dispatcher).dispatchCancel(action);
    }

    @Test
    public void cancelNullTagThrows() {
        try {
            picasso.cancelTag(null);
            Assert.fail("Canceling with a null tag should throw exception.");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void cancelTagAllActions() {
        ImageView target = TestUtils.mockImageViewTarget();
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, target, "TAG");
        picasso.enqueueAndSubmit(action);
        assertThat(picasso.targetToAction).hasSize(1);
        picasso.cancelTag("TAG");
        assertThat(picasso.targetToAction).isEmpty();
        Mockito.verify(action).cancel();
    }

    @Test
    public void cancelTagAllDeferredRequests() {
        ImageView target = TestUtils.mockImageViewTarget();
        DeferredRequestCreator deferredRequestCreator = TestUtils.mockDeferredRequestCreator();
        Mockito.when(deferredRequestCreator.getTag()).thenReturn("TAG");
        picasso.defer(target, deferredRequestCreator);
        picasso.cancelTag("TAG");
        Mockito.verify(deferredRequestCreator).cancel();
    }

    @Test
    public void deferAddsToMap() {
        ImageView target = TestUtils.mockImageViewTarget();
        DeferredRequestCreator deferredRequestCreator = TestUtils.mockDeferredRequestCreator();
        assertThat(picasso.targetToDeferredRequestCreator).isEmpty();
        picasso.defer(target, deferredRequestCreator);
        assertThat(picasso.targetToDeferredRequestCreator).hasSize(1);
    }

    @Test
    public void shutdown() {
        cache.set("key", TestUtils.makeBitmap(1, 1));
        assertThat(cache.size()).isEqualTo(1);
        picasso.shutdown();
        assertThat(cache.size()).isEqualTo(0);
        Mockito.verify(stats).shutdown();
        Mockito.verify(dispatcher).shutdown();
        assertThat(picasso.shutdown).isTrue();
    }

    @Test
    public void shutdownClosesUnsharedCache() {
        Cache cache = new Cache(temporaryFolder.getRoot(), 100);
        Picasso picasso = new Picasso(context, dispatcher, TestUtils.UNUSED_CALL_FACTORY, cache, this.cache, listener, TestUtils.NO_TRANSFORMERS, TestUtils.NO_HANDLERS, stats, ARGB_8888, false, false);
        picasso.shutdown();
        assertThat(cache.isClosed()).isTrue();
    }

    @Test
    public void shutdownTwice() {
        cache.set("key", TestUtils.makeBitmap(1, 1));
        assertThat(cache.size()).isEqualTo(1);
        picasso.shutdown();
        picasso.shutdown();
        assertThat(cache.size()).isEqualTo(0);
        Mockito.verify(stats).shutdown();
        Mockito.verify(dispatcher).shutdown();
        assertThat(picasso.shutdown).isTrue();
    }

    @Test
    public void shutdownClearsDeferredRequests() {
        DeferredRequestCreator deferredRequestCreator = TestUtils.mockDeferredRequestCreator();
        ImageView target = TestUtils.mockImageViewTarget();
        picasso.targetToDeferredRequestCreator.put(target, deferredRequestCreator);
        picasso.shutdown();
        Mockito.verify(deferredRequestCreator).cancel();
        assertThat(picasso.targetToDeferredRequestCreator).isEmpty();
    }

    @Test
    public void throwWhenTransformRequestReturnsNull() {
        RequestTransformer brokenTransformer = new RequestTransformer() {
            @Override
            public Request transformRequest(Request request) {
                return null;
            }
        };
        Picasso picasso = new Picasso(context, dispatcher, TestUtils.UNUSED_CALL_FACTORY, null, cache, listener, Collections.singletonList(brokenTransformer), TestUtils.NO_HANDLERS, stats, ARGB_8888, false, false);
        Request request = build();
        try {
            picasso.transformRequest(request);
            Assert.fail("Returning null from transformRequest() should throw");
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessageThat().isEqualTo(((("Request transformer " + (brokenTransformer.getClass().getCanonicalName())) + " returned null for ") + request));
        }
    }

    @Test
    public void getSnapshotInvokesStats() {
        picasso.getSnapshot();
        Mockito.verify(stats).createSnapshot();
    }

    @Test
    public void enableIndicators() {
        assertThat(picasso.getIndicatorsEnabled()).isFalse();
        picasso.setIndicatorsEnabled(true);
        assertThat(picasso.getIndicatorsEnabled()).isTrue();
    }

    @Test
    public void loadThrowsWithInvalidInput() {
        try {
            picasso.load("");
            Assert.fail("Empty URL should throw exception.");
        } catch (IllegalArgumentException expected) {
        }
        try {
            picasso.load("      ");
            Assert.fail("Empty URL should throw exception.");
        } catch (IllegalArgumentException expected) {
        }
        try {
            picasso.load(0);
            Assert.fail("Zero resourceId should throw exception.");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void builderInvalidClient() {
        try {
            client(null);
            Assert.fail();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("client == null");
        }
        try {
            callFactory(null);
            Assert.fail();
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("factory == null");
        }
    }

    @Test
    public void builderInvalidCache() {
        try {
            withCacheSize((-1));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("maxByteCount < 0: -1");
        }
    }

    @Test
    public void builderNullRequestTransformer() {
        try {
            addRequestTransformer(null);
            Assert.fail("Null request transformer should throw exception.");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void builderNullRequestHandler() {
        try {
            addRequestHandler(null);
            Assert.fail("Null request handler should throw exception.");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void builderWithoutRequestHandler() {
        Picasso picasso = new Picasso.Builder(org.robolectric.RuntimeEnvironment.application).build();
        assertThat(picasso.getRequestHandlers()).isNotEmpty();
        assertThat(picasso.getRequestHandlers()).doesNotContain(requestHandler);
    }

    @Test
    public void builderWithRequestHandler() {
        Picasso picasso = new Picasso.Builder(org.robolectric.RuntimeEnvironment.application).addRequestHandler(requestHandler).build();
        assertThat(picasso.getRequestHandlers()).isNotNull();
        assertThat(picasso.getRequestHandlers()).isNotEmpty();
        assertThat(picasso.getRequestHandlers()).contains(requestHandler);
    }

    @Test
    public void builderInvalidContext() {
        try {
            new Picasso.Builder(((Context) (null)));
            Assert.fail("Null context should throw exception.");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void builderWithDebugIndicators() {
        Picasso picasso = new Picasso.Builder(org.robolectric.RuntimeEnvironment.application).indicatorsEnabled(true).build();
        assertThat(picasso.getIndicatorsEnabled()).isTrue();
    }

    @Test
    public void evictAll() {
        Picasso picasso = new Picasso.Builder(org.robolectric.RuntimeEnvironment.application).indicatorsEnabled(true).build();
        picasso.cache.set("key", Bitmap.createBitmap(1, 1, ALPHA_8));
        assertThat(picasso.cache.size()).isEqualTo(1);
        picasso.evictAll();
        assertThat(picasso.cache.size()).isEqualTo(0);
    }

    @Test
    public void invalidateString() {
        Request request = build();
        cache.set(request.key, TestUtils.makeBitmap(1, 1));
        assertThat(cache.size()).isEqualTo(1);
        picasso.invalidate("https://example.com");
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void invalidateFile() {
        Request request = build();
        cache.set(request.key, TestUtils.makeBitmap(1, 1));
        assertThat(cache.size()).isEqualTo(1);
        picasso.invalidate(new File("/foo/bar/baz"));
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void invalidateUri() {
        Request request = build();
        cache.set(request.key, TestUtils.makeBitmap(1, 1));
        assertThat(cache.size()).isEqualTo(1);
        picasso.invalidate(TestUtils.URI_1);
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void clonedRequestHandlersAreIndependent() {
        Picasso original = TestUtils.defaultPicasso(application, false, false);
        original.newBuilder().addRequestTransformer(TestUtils.NOOP_TRANSFORMER).addRequestHandler(TestUtils.NOOP_REQUEST_HANDLER).build();
        assertThat(original.requestTransformers).hasSize(PicassoTest.NUM_BUILTIN_TRANSFORMERS);
        assertThat(original.requestHandlers).hasSize(PicassoTest.NUM_BUILTIN_HANDLERS);
    }

    @Test
    public void cloneSharesStatefulInstances() {
        Picasso parent = TestUtils.defaultPicasso(application, true, true);
        Picasso child = parent.newBuilder().build();
        assertThat(child.context).isEqualTo(parent.context);
        assertThat(child.callFactory).isEqualTo(parent.callFactory);
        assertThat(child.dispatcher.service).isEqualTo(parent.dispatcher.service);
        assertThat(child.cache).isEqualTo(parent.cache);
        assertThat(child.listener).isEqualTo(parent.listener);
        assertThat(child.requestTransformers).isEqualTo(parent.requestTransformers);
        assertThat(child.requestHandlers).hasSize(parent.requestHandlers.size());
        for (int i = 0, n = child.requestHandlers.size(); i < n; i++) {
            assertThat(child.requestHandlers.get(i)).isInstanceOf(parent.requestHandlers.get(i).getClass());
        }
        assertThat(child.defaultBitmapConfig).isEqualTo(parent.defaultBitmapConfig);
        assertThat(child.indicatorsEnabled).isEqualTo(parent.indicatorsEnabled);
        assertThat(child.loggingEnabled).isEqualTo(parent.loggingEnabled);
        assertThat(child.targetToAction).isEqualTo(parent.targetToAction);
        assertThat(child.targetToDeferredRequestCreator).isEqualTo(parent.targetToDeferredRequestCreator);
    }
}

