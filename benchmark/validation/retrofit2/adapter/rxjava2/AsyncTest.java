/**
 * Copyright (C) 2017 Square, Inc.
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
package retrofit2.adapter.rxjava2;


import io.reactivex.Completable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.http.GET;


public final class AsyncTest {
    @Rule
    public final MockWebServer server = new MockWebServer();

    interface Service {
        @GET("/")
        Completable completable();
    }

    private AsyncTest.Service service;

    private List<Throwable> uncaughtExceptions = new ArrayList<>();

    @Test
    public void success() throws InterruptedException {
        TestObserver<Void> observer = new TestObserver();
        service.completable().subscribe(observer);
        Assert.assertFalse(observer.await(1, TimeUnit.SECONDS));
        server.enqueue(new MockResponse());
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS);
        observer.assertComplete();
    }

    @Test
    public void failure() throws InterruptedException {
        TestObserver<Void> observer = new TestObserver();
        service.completable().subscribe(observer);
        Assert.assertFalse(observer.await(1, TimeUnit.SECONDS));
        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
        observer.awaitTerminalEvent(1, TimeUnit.SECONDS);
        observer.assertError(IOException.class);
    }

    @Test
    public void throwingInOnCompleteDeliveredToPlugin() throws InterruptedException {
        server.enqueue(new MockResponse());
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> errorRef = new AtomicReference<>();
        RxJavaPlugins.setErrorHandler(new io.reactivex.functions.Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (!(errorRef.compareAndSet(null, throwable))) {
                    throw Exceptions.propagate(throwable);// Don't swallow secondary errors!

                }
                latch.countDown();
            }
        });
        TestObserver<Void> observer = new TestObserver();
        final RuntimeException e = new RuntimeException();
        service.completable().subscribe(new CompletableThrowingTest.ForwardingCompletableObserver(observer) {
            @Override
            public void onComplete() {
                throw e;
            }
        });
        latch.await(1, TimeUnit.SECONDS);
        assertThat(errorRef.get()).isSameAs(e);
    }

    @Test
    public void bodyThrowingInOnErrorDeliveredToPlugin() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(404));
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> pluginRef = new AtomicReference<>();
        RxJavaPlugins.setErrorHandler(new io.reactivex.functions.Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (!(pluginRef.compareAndSet(null, throwable))) {
                    throw Exceptions.propagate(throwable);// Don't swallow secondary errors!

                }
                latch.countDown();
            }
        });
        TestObserver<Void> observer = new TestObserver();
        final RuntimeException e = new RuntimeException();
        final AtomicReference<Throwable> errorRef = new AtomicReference<>();
        service.completable().subscribe(new CompletableThrowingTest.ForwardingCompletableObserver(observer) {
            @Override
            public void onError(Throwable throwable) {
                errorRef.set(throwable);
                throw e;
            }
        });
        latch.await(1, TimeUnit.SECONDS);
        // noinspection ThrowableResultOfMethodCallIgnored
        CompositeException composite = ((CompositeException) (pluginRef.get()));
        assertThat(composite.getExceptions()).containsExactly(errorRef.get(), e);
    }

    @Test
    public void bodyThrowingFatalInOnErrorPropagates() throws InterruptedException {
        server.enqueue(new MockResponse().setResponseCode(404));
        final CountDownLatch latch = new CountDownLatch(1);
        TestObserver<Void> observer = new TestObserver();
        final Error e = new OutOfMemoryError("Not real");
        service.completable().subscribe(new CompletableThrowingTest.ForwardingCompletableObserver(observer) {
            @Override
            public void onError(Throwable throwable) {
                throw e;
            }
        });
        latch.await(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, uncaughtExceptions.size());
        Assert.assertSame(e, uncaughtExceptions.remove(0));
    }
}

