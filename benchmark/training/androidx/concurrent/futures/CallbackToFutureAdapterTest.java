/**
 * Copyright 2018 The Android Open Source Project
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
package androidx.concurrent.futures;


import androidx.concurrent.futures.CallbackToFutureAdapter.Completer;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CallbackToFutureAdapterTest {
    private static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(10);

    private static final long GC_AWAIT_TIME_MS = 200L;

    @Test
    public void testSuccess() {
        final AtomicReference<Completer<String>> completerRef = new AtomicReference<>();
        ListenableFuture<String> future = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<String>() {
            @Override
            public Object attachCompleter(Completer<String> completer) {
                completerRef.set(completer);
                return "my special callback";
            }
        });
        assertThat(future.isDone()).isFalse();
        assertThat(future.toString()).contains("my special callback");
        completerRef.get().set("my special result");
        CallbackToFutureAdapterTest.assertFutureCompletedWith(future, "my special result");
    }

    @Test
    public void testCancellationListenersCalledOnFutureCancelled() {
        final AtomicReference<Completer<String>> completerRef = new AtomicReference<>();
        final AtomicBoolean wasCalled = new AtomicBoolean();
        ListenableFuture<String> future = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<String>() {
            @Override
            public Object attachCompleter(Completer<String> completer) {
                completer.addCancellationListener(new Runnable() {
                    @Override
                    public void run() {
                        wasCalled.set(true);
                    }
                }, DirectExecutor.INSTANCE);
                completerRef.set(completer);
                return null;
            }
        });
        assertThat(future.cancel(true)).isTrue();
        assertThat(wasCalled.get()).isTrue();
        assertThat(completerRef.get().setCancelled()).isFalse();
    }

    @Test
    public void testCancellationListenersNotCalledOnCompleterCancelled() {
        final AtomicBoolean wasCalled = new AtomicBoolean();
        ListenableFuture<String> future = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<String>() {
            @Override
            public Object attachCompleter(Completer<String> completer) {
                completer.addCancellationListener(new Runnable() {
                    @Override
                    public void run() {
                        wasCalled.set(true);
                    }
                }, DirectExecutor.INSTANCE);
                completer.setCancelled();
                return null;
            }
        });
        assertThat(future.isCancelled()).isTrue();
        assertThat(wasCalled.get()).isFalse();
    }

    /**
     * Verifies that there is no cycle in toString between the completer and the tag
     */
    @Test
    public void testNoRecursiveToString() {
        final AtomicReference<Runnable> callbackRef = new AtomicReference<>();
        ListenableFuture<String> future = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<String>() {
            @Override
            public Object attachCompleter(final Completer<String> completer) {
                Runnable callback = new Runnable() {
                    @Override
                    public void run() {
                        completer.set("my special result");
                    }

                    @Override
                    public String toString() {
                        return ("custom callback, completer=[" + completer) + "]";
                    }
                };
                callbackRef.set(callback);
                return callback;
            }
        });
        assertThat(future.isDone()).isFalse();
        assertThat(future.toString()).contains("custom callback");
        callbackRef.get().run();
        CallbackToFutureAdapterTest.assertFutureCompletedWith(future, "my special result");
    }
}

