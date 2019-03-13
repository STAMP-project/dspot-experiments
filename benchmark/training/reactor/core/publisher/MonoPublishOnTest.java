/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.RUN_ON;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Exceptions;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;


public class MonoPublishOnTest {
    @Test
    public void rejectedExecutionExceptionOnDataSignalExecutor() throws InterruptedException {
        int data = 1;
        final AtomicReference<Throwable> throwableInOnOperatorError = new AtomicReference<>();
        final AtomicReference<Object> dataInOnOperatorError = new AtomicReference<>();
        try {
            CountDownLatch hookLatch = new CountDownLatch(1);
            Hooks.onOperatorError(( t, d) -> {
                throwableInOnOperatorError.set(t);
                dataInOnOperatorError.set(d);
                hookLatch.countDown();
                return t;
            });
            ExecutorService executor = Executors.newCachedThreadPool();
            CountDownLatch latch = new CountDownLatch(1);
            AssertSubscriber<Integer> assertSubscriber = new AssertSubscriber<>();
            Mono.just(data).publishOn(Schedulers.fromExecutorService(executor)).doOnNext(( s) -> {
                try {
                    latch.await();
                } catch ( e) {
                }
            }).publishOn(Schedulers.fromExecutor(executor)).subscribe(assertSubscriber);
            executor.shutdownNow();
            assertSubscriber.assertNoValues().assertNoError().assertNotComplete();
            hookLatch.await();
            Assert.assertThat(throwableInOnOperatorError.get(), CoreMatchers.instanceOf(RejectedExecutionException.class));
            Assert.assertSame(dataInOnOperatorError.get(), data);
        } finally {
            Hooks.resetOnOperatorError();
        }
    }

    @Test
    public void rejectedExecutionExceptionOnErrorSignalExecutor() throws InterruptedException {
        int data = 1;
        Exception exception = new IllegalStateException();
        final AtomicReference<Throwable> throwableInOnOperatorError = new AtomicReference<>();
        final AtomicReference<Object> dataInOnOperatorError = new AtomicReference<>();
        try {
            CountDownLatch hookLatch = new CountDownLatch(2);
            Hooks.onOperatorError(( t, d) -> {
                throwableInOnOperatorError.set(t);
                dataInOnOperatorError.set(d);
                hookLatch.countDown();
                return t;
            });
            ExecutorService executor = Executors.newCachedThreadPool();
            CountDownLatch latch = new CountDownLatch(1);
            AssertSubscriber<Integer> assertSubscriber = new AssertSubscriber<>();
            Mono.just(data).publishOn(Schedulers.fromExecutorService(executor)).doOnNext(( s) -> {
                try {
                    latch.await();
                } catch ( e) {
                    throw Exceptions.propagate(exception);
                }
            }).publishOn(Schedulers.fromExecutor(executor)).subscribe(assertSubscriber);
            executor.shutdownNow();
            assertSubscriber.assertNoValues().assertNoError().assertNotComplete();
            hookLatch.await();
            Assert.assertThat(throwableInOnOperatorError.get(), CoreMatchers.instanceOf(RejectedExecutionException.class));
            Assert.assertSame(throwableInOnOperatorError.get().getSuppressed()[0], exception);
        } finally {
            Hooks.resetOnOperatorError();
        }
    }

    @Test
    public void rejectedExecutionExceptionOnDataSignalExecutorService() throws InterruptedException {
        int data = 1;
        final AtomicReference<Throwable> throwableInOnOperatorError = new AtomicReference<>();
        final AtomicReference<Object> dataInOnOperatorError = new AtomicReference<>();
        try {
            CountDownLatch hookLatch = new CountDownLatch(1);
            Hooks.onOperatorError(( t, d) -> {
                throwableInOnOperatorError.set(t);
                dataInOnOperatorError.set(d);
                hookLatch.countDown();
                return t;
            });
            ExecutorService executor = Executors.newCachedThreadPool();
            CountDownLatch latch = new CountDownLatch(1);
            AssertSubscriber<Integer> assertSubscriber = new AssertSubscriber<>();
            Mono.just(data).publishOn(Schedulers.fromExecutorService(executor)).doOnNext(( s) -> {
                try {
                    latch.await();
                } catch ( e) {
                }
            }).publishOn(Schedulers.fromExecutorService(executor)).subscribe(assertSubscriber);
            executor.shutdownNow();
            assertSubscriber.assertNoValues().assertNoError().assertNotComplete();
            hookLatch.await();
            Assert.assertThat(throwableInOnOperatorError.get(), CoreMatchers.instanceOf(RejectedExecutionException.class));
            Assert.assertSame(dataInOnOperatorError.get(), data);
        } finally {
            Hooks.resetOnOperatorError();
        }
    }

    @Test
    public void rejectedExecutionExceptionOnErrorSignalExecutorService() throws InterruptedException {
        int data = 1;
        Exception exception = new IllegalStateException();
        final AtomicReference<Throwable> throwableInOnOperatorError = new AtomicReference<>();
        final AtomicReference<Object> dataInOnOperatorError = new AtomicReference<>();
        try {
            CountDownLatch hookLatch = new CountDownLatch(2);
            Hooks.onOperatorError(( t, d) -> {
                throwableInOnOperatorError.set(t);
                dataInOnOperatorError.set(d);
                hookLatch.countDown();
                return t;
            });
            ExecutorService executor = Executors.newCachedThreadPool();
            CountDownLatch latch = new CountDownLatch(1);
            AssertSubscriber<Integer> assertSubscriber = new AssertSubscriber<>();
            Mono.just(data).publishOn(Schedulers.fromExecutorService(executor)).doOnNext(( s) -> {
                try {
                    latch.await();
                } catch ( e) {
                    throw Exceptions.propagate(exception);
                }
            }).publishOn(Schedulers.fromExecutorService(executor)).subscribe(assertSubscriber);
            executor.shutdownNow();
            assertSubscriber.assertNoValues().assertNoError().assertNotComplete();
            hookLatch.await();
            Assert.assertThat(throwableInOnOperatorError.get(), CoreMatchers.instanceOf(RejectedExecutionException.class));
            Assert.assertSame(throwableInOnOperatorError.get().getSuppressed()[0], exception);
        } finally {
            Hooks.resetOnOperatorError();
        }
    }

    @Test
    public void scanOperator() {
        MonoPublishOn<String> test = new MonoPublishOn(Mono.empty(), Schedulers.immediate());
        Assertions.assertThat(test.scan(RUN_ON)).isSameAs(Schedulers.immediate());
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<String> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        MonoPublishOn.PublishOnSubscriber<String> test = new MonoPublishOn.PublishOnSubscriber<>(actual, Schedulers.single());
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(RUN_ON)).isSameAs(Schedulers.single());
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanSubscriberError() {
        CoreSubscriber<String> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        MonoPublishOn.PublishOnSubscriber<String> test = new MonoPublishOn.PublishOnSubscriber<>(actual, Schedulers.single());
        Assertions.assertThat(test.scan(ERROR)).isNull();
        test.onError(new IllegalStateException("boom"));
        Assertions.assertThat(test.scan(ERROR)).hasMessage("boom");
    }

    @Test
    public void error() {
        StepVerifier.create(Mono.error(new RuntimeException("forced failure")).publishOn(Schedulers.single())).verifyErrorMessage("forced failure");
    }

    @Test
    public void errorHide() {
        StepVerifier.create(Mono.error(new RuntimeException("forced failure")).hide().publishOn(Schedulers.single())).verifyErrorMessage("forced failure");
    }
}

