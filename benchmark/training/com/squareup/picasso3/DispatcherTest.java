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


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import com.squareup.picasso3.NetworkRequestHandler.ContentLengthException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static LoadedFrom.MEMORY;


@RunWith(RobolectricTestRunner.class)
public class DispatcherTest {
    @Mock
    Context context;

    @Mock
    ConnectivityManager connectivityManager;

    @Mock
    PicassoExecutorService service;

    @Mock
    ExecutorService serviceMock;

    final PlatformLruCache cache = new PlatformLruCache(2048);

    @Mock
    Stats stats;

    private Dispatcher dispatcher;

    final Bitmap bitmap1 = TestUtils.makeBitmap();

    final Bitmap bitmap2 = TestUtils.makeBitmap();

    @Test
    public void shutdownStopsService() {
        dispatcher.shutdown();
        Mockito.verify(service).shutdown();
    }

    @Test
    public void shutdownUnregistersReceiver() {
        dispatcher.shutdown();
        Mockito.verify(context).unregisterReceiver(dispatcher.receiver);
    }

    @Test
    public void performSubmitWithNewRequestQueuesHunter() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        dispatcher.performSubmit(action);
        assertThat(dispatcher.hunterMap).hasSize(1);
        Mockito.verify(service).submit(ArgumentMatchers.any(BitmapHunter.class));
    }

    @Test
    public void performSubmitWithTwoDifferentRequestsQueuesHunters() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_2, TestUtils.URI_2);
        dispatcher.performSubmit(action1);
        dispatcher.performSubmit(action2);
        assertThat(dispatcher.hunterMap).hasSize(2);
        Mockito.verify(service, Mockito.times(2)).submit(ArgumentMatchers.any(BitmapHunter.class));
    }

    @Test
    public void performSubmitWithExistingRequestAttachesToHunter() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        dispatcher.performSubmit(action1);
        dispatcher.performSubmit(action2);
        assertThat(dispatcher.hunterMap).hasSize(1);
        Mockito.verify(service).submit(ArgumentMatchers.any(BitmapHunter.class));
    }

    @Test
    public void performSubmitWithShutdownServiceIgnoresRequest() {
        Mockito.when(service.isShutdown()).thenReturn(true);
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        dispatcher.performSubmit(action);
        assertThat(dispatcher.hunterMap).isEmpty();
        Mockito.verify(service, Mockito.never()).submit(ArgumentMatchers.any(BitmapHunter.class));
    }

    @Test
    public void performSubmitWithShutdownAttachesRequest() {
        BitmapHunter hunter = TestUtils.mockHunter(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), new RequestHandler.Result(bitmap1, MEMORY));
        dispatcher.hunterMap.put(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), hunter);
        Mockito.when(service.isShutdown()).thenReturn(true);
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        dispatcher.performSubmit(action);
        assertThat(dispatcher.hunterMap).hasSize(1);
        Mockito.verify(hunter).attach(action);
        Mockito.verify(service, Mockito.never()).submit(ArgumentMatchers.any(BitmapHunter.class));
    }

    @Test
    public void performSubmitWithFetchAction() {
        String pausedTag = "pausedTag";
        dispatcher.pausedTags.add(pausedTag);
        assertThat(dispatcher.pausedActions).isEmpty();
        FetchAction fetchAction1 = new FetchAction(TestUtils.mockPicasso(), tag(pausedTag).build(), null);
        FetchAction fetchAction2 = new FetchAction(TestUtils.mockPicasso(), tag(pausedTag).build(), null);
        dispatcher.performSubmit(fetchAction1);
        dispatcher.performSubmit(fetchAction2);
        assertThat(dispatcher.pausedActions).hasSize(2);
    }

    @Test
    public void performCancelWithFetchActionWithCallback() {
        String pausedTag = "pausedTag";
        dispatcher.pausedTags.add(pausedTag);
        assertThat(dispatcher.pausedActions).isEmpty();
        Callback callback = TestUtils.mockCallback();
        FetchAction fetchAction1 = new FetchAction(TestUtils.mockPicasso(), tag(pausedTag).build(), callback);
        dispatcher.performCancel(fetchAction1);
        fetchAction1.cancel();
        assertThat(dispatcher.pausedActions).isEmpty();
    }

    @Test
    public void performCancelDetachesRequestAndCleansUp() {
        BitmapTarget target = TestUtils.mockTarget();
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, target);
        BitmapHunter hunter = TestUtils.mockHunter(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), new RequestHandler.Result(bitmap1, MEMORY));
        hunter.attach(action);
        Mockito.when(hunter.cancel()).thenReturn(true);
        dispatcher.hunterMap.put(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), hunter);
        dispatcher.failedActions.put(target, action);
        dispatcher.performCancel(action);
        Mockito.verify(hunter).detach(action);
        Mockito.verify(hunter).cancel();
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.failedActions).isEmpty();
    }

    @Test
    public void performCancelMultipleRequestsDetachesOnly() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        BitmapHunter hunter = TestUtils.mockHunter(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), new RequestHandler.Result(bitmap1, MEMORY));
        hunter.attach(action1);
        hunter.attach(action2);
        dispatcher.hunterMap.put(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), hunter);
        dispatcher.performCancel(action1);
        Mockito.verify(hunter).detach(action1);
        Mockito.verify(hunter).cancel();
        assertThat(dispatcher.hunterMap).hasSize(1);
    }

    @Test
    public void performCancelUnqueuesAndDetachesPausedRequest() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockTarget(), "tag");
        BitmapHunter hunter = TestUtils.mockHunter(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), new RequestHandler.Result(bitmap1, MEMORY), action);
        dispatcher.hunterMap.put(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), hunter);
        dispatcher.pausedTags.add("tag");
        dispatcher.pausedActions.put(action.getTarget(), action);
        dispatcher.performCancel(action);
        assertThat(dispatcher.pausedTags).containsExactly("tag");
        assertThat(dispatcher.pausedActions).isEmpty();
        Mockito.verify(hunter).detach(action);
    }

    @Test
    public void performCompleteSetsResultInCache() {
        Request data = build();
        Action action = DispatcherTest.noopAction(data);
        BitmapHunter hunter = new BitmapHunter(TestUtils.mockPicasso(), dispatcher, cache, stats, action, DispatcherTest.EMPTY_REQUEST_HANDLER);
        hunter.result = new RequestHandler.Result(bitmap1, MEMORY);
        dispatcher.performComplete(hunter);
        assertThat(cache.get(hunter.getKey())).isSameAs(hunter.result.getBitmap());
    }

    @Test
    public void performCompleteWithNoStoreMemoryPolicy() {
        Request data = build();
        Action action = DispatcherTest.noopAction(data);
        BitmapHunter hunter = new BitmapHunter(TestUtils.mockPicasso(), dispatcher, cache, stats, action, DispatcherTest.EMPTY_REQUEST_HANDLER);
        hunter.result = new RequestHandler.Result(bitmap1, MEMORY);
        dispatcher.performComplete(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test
    public void performCompleteCleansUpAndPostsToMain() {
        Request data = build();
        Action action = DispatcherTest.noopAction(data);
        BitmapHunter hunter = new BitmapHunter(TestUtils.mockPicasso(), dispatcher, cache, stats, action, DispatcherTest.EMPTY_REQUEST_HANDLER);
        hunter.result = new RequestHandler.Result(bitmap1, MEMORY);
        dispatcher.performComplete(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        // TODO verify post to main thread.
    }

    @Test
    public void performCompleteCleansUpAndDoesNotPostToMainIfCancelled() {
        Request data = build();
        Action action = DispatcherTest.noopAction(data);
        BitmapHunter hunter = new BitmapHunter(TestUtils.mockPicasso(), dispatcher, cache, stats, action, DispatcherTest.EMPTY_REQUEST_HANDLER);
        hunter.result = new RequestHandler.Result(bitmap1, MEMORY);
        hunter.future = new FutureTask(Mockito.mock(Runnable.class), null);
        hunter.future.cancel(false);
        dispatcher.performComplete(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        // TODO verify no main thread interactions.
    }

    @Test
    public void performErrorCleansUpAndPostsToMain() {
        BitmapHunter hunter = TestUtils.mockHunter(((TestUtils.URI_KEY_1) + (Request.KEY_SEPARATOR)), new RequestHandler.Result(bitmap1, MEMORY));
        dispatcher.hunterMap.put(hunter.getKey(), hunter);
        dispatcher.performError(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        // TODO verify post to main thread.
    }

    @Test
    public void performErrorCleansUpAndDoesNotPostToMainIfCancelled() {
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY));
        Mockito.when(hunter.isCancelled()).thenReturn(true);
        dispatcher.hunterMap.put(hunter.getKey(), hunter);
        dispatcher.performError(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        // TODO verify no main thread interactions.
    }

    @Test
    public void performRetrySkipsIfHunterIsCancelled() {
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_2, new RequestHandler.Result(bitmap1, MEMORY));
        Mockito.when(hunter.isCancelled()).thenReturn(true);
        dispatcher.performRetry(hunter);
        Mockito.verifyZeroInteractions(service);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.failedActions).isEmpty();
    }

    @Test
    public void performRetryForContentLengthResetsNetworkPolicy() {
        NetworkInfo networkInfo = TestUtils.mockNetworkInfo(true);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        BitmapHunter hunter = new BitmapHunter(TestUtils.mockPicasso(), dispatcher, cache, stats, TestUtils.mockAction(TestUtils.URI_KEY_2, TestUtils.URI_2), DispatcherTest.RETRYING_REQUEST_HANDLER);
        hunter.exception = new ContentLengthException("304 error");
        dispatcher.performRetry(hunter);
        assertThat(NetworkPolicy.shouldReadFromDiskCache(hunter.data.networkPolicy)).isFalse();
    }

    @Test
    public void performRetryDoesNotMarkForReplayIfNotSupported() {
        NetworkInfo networkInfo = TestUtils.mockNetworkInfo(true);
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY), TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1));
        Mockito.when(hunter.supportsReplay()).thenReturn(false);
        Mockito.when(hunter.shouldRetry(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(NetworkInfo.class))).thenReturn(false);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        dispatcher.performRetry(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.failedActions).isEmpty();
        Mockito.verify(service, Mockito.never()).submit(hunter);
    }

    @Test
    public void performRetryDoesNotMarkForReplayIfNoNetworkScanning() {
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY), TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1));
        Mockito.when(hunter.shouldRetry(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(NetworkInfo.class))).thenReturn(false);
        Mockito.when(hunter.supportsReplay()).thenReturn(true);
        Dispatcher dispatcher = createDispatcher(false);
        dispatcher.performRetry(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.failedActions).isEmpty();
        Mockito.verify(service, Mockito.never()).submit(hunter);
    }

    @Test
    public void performRetryMarksForReplayIfSupportedScansNetworkChangesAndShouldNotRetry() {
        NetworkInfo networkInfo = TestUtils.mockNetworkInfo(true);
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockTarget());
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY), action);
        Mockito.when(hunter.supportsReplay()).thenReturn(true);
        Mockito.when(hunter.shouldRetry(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(NetworkInfo.class))).thenReturn(false);
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        dispatcher.performRetry(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.failedActions).hasSize(1);
        Mockito.verify(service, Mockito.never()).submit(hunter);
    }

    @Test
    public void performRetryRetriesIfNoNetworkScanning() {
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY), TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1));
        Mockito.when(hunter.shouldRetry(ArgumentMatchers.anyBoolean(), ArgumentMatchers.isNull(NetworkInfo.class))).thenReturn(true);
        Dispatcher dispatcher = createDispatcher(false);
        dispatcher.performRetry(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.failedActions).isEmpty();
        Mockito.verify(service).submit(hunter);
    }

    @Test
    public void performRetryMarksForReplayIfSupportsReplayAndShouldNotRetry() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockTarget());
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY), action);
        Mockito.when(hunter.shouldRetry(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(NetworkInfo.class))).thenReturn(false);
        Mockito.when(hunter.supportsReplay()).thenReturn(true);
        dispatcher.performRetry(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.failedActions).hasSize(1);
        Mockito.verify(service, Mockito.never()).submit(hunter);
    }

    @Test
    public void performRetryRetriesIfShouldRetry() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockTarget());
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY), action);
        Mockito.when(hunter.shouldRetry(ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(NetworkInfo.class))).thenReturn(true);
        dispatcher.performRetry(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.failedActions).isEmpty();
        Mockito.verify(service).submit(hunter);
    }

    @Test
    public void performRetrySkipIfServiceShutdown() {
        Mockito.when(service.isShutdown()).thenReturn(true);
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY));
        dispatcher.performRetry(hunter);
        Mockito.verify(service, Mockito.never()).submit(hunter);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.failedActions).isEmpty();
    }

    @Test
    public void performAirplaneModeChange() {
        assertThat(dispatcher.airplaneMode).isFalse();
        dispatcher.performAirplaneModeChange(true);
        assertThat(dispatcher.airplaneMode).isTrue();
        dispatcher.performAirplaneModeChange(false);
        assertThat(dispatcher.airplaneMode).isFalse();
    }

    @Test
    public void performNetworkStateChangeWithNullInfoIgnores() {
        Dispatcher dispatcher = createDispatcher(serviceMock);
        dispatcher.performNetworkStateChange(null);
        Mockito.verifyZeroInteractions(service);
    }

    @Test
    public void performNetworkStateChangeWithDisconnectedInfoIgnores() {
        Dispatcher dispatcher = createDispatcher(serviceMock);
        NetworkInfo info = TestUtils.mockNetworkInfo();
        Mockito.when(info.isConnectedOrConnecting()).thenReturn(false);
        dispatcher.performNetworkStateChange(info);
        Mockito.verifyZeroInteractions(service);
    }

    @Test
    public void performNetworkStateChangeWithConnectedInfoDifferentInstanceIgnores() {
        Dispatcher dispatcher = createDispatcher(serviceMock);
        NetworkInfo info = TestUtils.mockNetworkInfo(true);
        dispatcher.performNetworkStateChange(info);
        Mockito.verifyZeroInteractions(service);
    }

    @Test
    public void performPauseAndResumeUpdatesListOfPausedTags() {
        dispatcher.performPauseTag("tag");
        assertThat(dispatcher.pausedTags).containsExactly("tag");
        dispatcher.performResumeTag("tag");
        assertThat(dispatcher.pausedTags).isEmpty();
    }

    @Test
    public void performPauseTagIsIdempotent() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockTarget(), "tag");
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY), action);
        dispatcher.hunterMap.put(TestUtils.URI_KEY_1, hunter);
        dispatcher.pausedTags.add("tag");
        dispatcher.performPauseTag("tag");
        Mockito.verify(hunter, Mockito.never()).getAction();
    }

    @Test
    public void performPauseTagQueuesNewRequestDoesNotSubmit() {
        dispatcher.performPauseTag("tag");
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, "tag");
        dispatcher.performSubmit(action);
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.pausedActions).hasSize(1);
        assertThat(dispatcher.pausedActions.containsValue(action)).isTrue();
        Mockito.verify(service, Mockito.never()).submit(ArgumentMatchers.any(BitmapHunter.class));
    }

    @Test
    public void performPauseTagDoesNotQueueUnrelatedRequest() {
        dispatcher.performPauseTag("tag");
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, "anothertag");
        dispatcher.performSubmit(action);
        assertThat(dispatcher.hunterMap).hasSize(1);
        assertThat(dispatcher.pausedActions).isEmpty();
        Mockito.verify(service).submit(ArgumentMatchers.any(BitmapHunter.class));
    }

    @Test
    public void performPauseDetachesRequestAndCancelsHunter() {
        Action action = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, "tag");
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY), action);
        Mockito.when(hunter.cancel()).thenReturn(true);
        dispatcher.hunterMap.put(TestUtils.URI_KEY_1, hunter);
        dispatcher.performPauseTag("tag");
        assertThat(dispatcher.hunterMap).isEmpty();
        assertThat(dispatcher.pausedActions).hasSize(1);
        assertThat(dispatcher.pausedActions.containsValue(action)).isTrue();
        Mockito.verify(hunter).detach(action);
        Mockito.verify(hunter).cancel();
    }

    @Test
    public void performPauseOnlyDetachesPausedRequest() {
        Action action1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockTarget(), "tag1");
        Action action2 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1, TestUtils.mockTarget(), "tag2");
        BitmapHunter hunter = TestUtils.mockHunter(TestUtils.URI_KEY_1, new RequestHandler.Result(bitmap1, MEMORY));
        Mockito.when(hunter.getActions()).thenReturn(Arrays.asList(action1, action2));
        dispatcher.hunterMap.put(TestUtils.URI_KEY_1, hunter);
        dispatcher.performPauseTag("tag1");
        assertThat(dispatcher.hunterMap).hasSize(1);
        assertThat(dispatcher.hunterMap.containsValue(hunter)).isTrue();
        assertThat(dispatcher.pausedActions).hasSize(1);
        assertThat(dispatcher.pausedActions.containsValue(action1)).isTrue();
        Mockito.verify(hunter).detach(action1);
        Mockito.verify(hunter, Mockito.never()).detach(action2);
    }

    @Test
    public void performResumeTagIsIdempotent() {
        dispatcher.performResumeTag("tag");
        // TODO verify no main thread interactions.
    }

    @Test
    public void performNetworkStateChangeFlushesFailedHunters() {
        PicassoExecutorService service = Mockito.mock(PicassoExecutorService.class);
        NetworkInfo info = TestUtils.mockNetworkInfo(true);
        Dispatcher dispatcher = createDispatcher(service);
        Action failedAction1 = TestUtils.mockAction(TestUtils.URI_KEY_1, TestUtils.URI_1);
        Action failedAction2 = TestUtils.mockAction(TestUtils.URI_KEY_2, TestUtils.URI_2);
        dispatcher.failedActions.put(TestUtils.URI_KEY_1, failedAction1);
        dispatcher.failedActions.put(TestUtils.URI_KEY_2, failedAction2);
        dispatcher.performNetworkStateChange(info);
        Mockito.verify(service, Mockito.times(2)).submit(ArgumentMatchers.any(BitmapHunter.class));
        assertThat(dispatcher.failedActions).isEmpty();
    }

    @Test
    public void nullIntentOnReceiveDoesNothing() {
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        Dispatcher.NetworkBroadcastReceiver receiver = new Dispatcher.NetworkBroadcastReceiver(dispatcher);
        receiver.onReceive(context, null);
        Mockito.verifyZeroInteractions(dispatcher);
    }

    @Test
    public void nullExtrasOnReceiveConnectivityAreOk() {
        ConnectivityManager connectivityManager = Mockito.mock(ConnectivityManager.class);
        NetworkInfo networkInfo = TestUtils.mockNetworkInfo();
        Mockito.when(connectivityManager.getActiveNetworkInfo()).thenReturn(networkInfo);
        Mockito.when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager);
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        Dispatcher.NetworkBroadcastReceiver receiver = new Dispatcher.NetworkBroadcastReceiver(dispatcher);
        receiver.onReceive(context, new Intent(CONNECTIVITY_ACTION));
        Mockito.verify(dispatcher).dispatchNetworkStateChange(networkInfo);
    }

    @Test
    public void nullExtrasOnReceiveAirplaneDoesNothing() {
        Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
        Dispatcher.NetworkBroadcastReceiver receiver = new Dispatcher.NetworkBroadcastReceiver(dispatcher);
        receiver.onReceive(context, new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED));
        Mockito.verifyZeroInteractions(dispatcher);
    }

    @Test
    public void correctExtrasOnReceiveAirplaneDispatches() {
        setAndVerifyAirplaneMode(false);
        setAndVerifyAirplaneMode(true);
    }

    private static final RequestHandler RETRYING_REQUEST_HANDLER = new RequestHandler() {
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
        }

        @Override
        int getRetryCount() {
            return 1;
        }

        @Override
        boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
            return true;
        }
    };

    private static final RequestHandler EMPTY_REQUEST_HANDLER = new RequestHandler() {
        @Override
        public boolean canHandleRequest(@NonNull
        Request data) {
            return false;
        }

        @Override
        public void load(@NonNull
        Picasso picasso, @NonNull
        Request request, @NonNull
        Callback callback) {
        }
    };
}

