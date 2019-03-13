/**
 * Copyright (c) 2011-2018 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Attr.ACTUAL;
import Attr.ACTUAL_METADATA;
import Attr.BUFFERED;
import Attr.CANCELLED;
import Attr.CAPACITY;
import Attr.DELAY_ERROR;
import Attr.ERROR;
import Attr.LARGE_BUFFERED;
import Attr.NAME;
import Attr.PARENT;
import Attr.PREFETCH;
import Attr.REQUESTED_FROM_DOWNSTREAM;
import Attr.RUN_ON;
import Attr.TAGS;
import Attr.TERMINATED;
import FluxUsingWhen.CancelInner;
import FluxUsingWhen.CommitInner;
import FluxUsingWhen.RollbackInner;
import Fuseable.QueueSubscription;
import Operators.DeferredSubscription;
import SignalType.ON_COMPLETE;
import SignalType.ON_NEXT;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxUsingWhen.ResourceSubscriber;
import reactor.core.publisher.FluxUsingWhen.UsingWhenSubscriber;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.test.publisher.TestPublisher;
import reactor.util.annotation.Nullable;


@RunWith(JUnitParamsRunner.class)
public class FluxUsingWhenTest {
    @Test
    public void nullResourcePublisherRejected() {
        assertThatNullPointerException().isThrownBy(() -> Flux.usingWhen(null, ( tr) -> Mono.empty(), ( tr) -> Mono.empty(), ( tr) -> Mono.empty())).withMessage("resourceSupplier").withNoCause();
    }

    @Test
    public void emptyResourcePublisherDoesntApplyCallback() {
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        Flux<String> test = Flux.usingWhen(Flux.empty().hide(), ( tr) -> Mono.just("unexpected"), ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)));
        StepVerifier.create(test).verifyComplete();
        assertThat(commitDone).isFalse();
        assertThat(rollbackDone).isFalse();
    }

    @Test
    public void emptyResourceCallableDoesntApplyCallback() {
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        Flux<String> test = Flux.usingWhen(Flux.empty(), ( tr) -> Mono.just("unexpected"), ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)));
        StepVerifier.create(test).verifyComplete();
        assertThat(commitDone).isFalse();
        assertThat(rollbackDone).isFalse();
    }

    @Test
    public void errorResourcePublisherDoesntApplyCallback() {
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        Flux<String> test = Flux.usingWhen(Flux.error(new IllegalStateException("boom")).hide(), ( tr) -> Mono.just("unexpected"), ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)));
        StepVerifier.create(test).verifyErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("boom").hasNoCause().hasNoSuppressedExceptions());
        assertThat(commitDone).isFalse();
        assertThat(rollbackDone).isFalse();
    }

    @Test
    public void errorResourceCallableDoesntApplyCallback() {
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        Flux<String> test = Flux.usingWhen(Flux.error(new IllegalStateException("boom")), ( tr) -> Mono.just("unexpected"), ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)));
        StepVerifier.create(test).verifyErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("boom").hasNoCause().hasNoSuppressedExceptions());
        assertThat(commitDone).isFalse();
        assertThat(rollbackDone).isFalse();
    }

    @Test
    public void errorResourcePublisherAfterEmitIsDropped() {
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        TestPublisher<String> testPublisher = TestPublisher.createCold();
        testPublisher.next("Resource").error(new IllegalStateException("boom"));
        Flux<String> test = Flux.usingWhen(testPublisher, Mono::just, ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)));
        StepVerifier.create(test).expectNext("Resource").expectComplete().verifyThenAssertThat(Duration.ofSeconds(2)).hasDroppedErrorWithMessage("boom").hasNotDroppedElements();
        assertThat(commitDone).isTrue();
        assertThat(rollbackDone).isFalse();
        testPublisher.assertCancelled();
    }

    @Test
    public void secondResourceInPublisherIsDropped() {
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        TestPublisher<String> testPublisher = TestPublisher.createCold();
        testPublisher.emit("Resource", "boom");
        Flux<String> test = Flux.usingWhen(testPublisher, Mono::just, ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)));
        StepVerifier.create(test).expectNext("Resource").expectComplete().verifyThenAssertThat(Duration.ofSeconds(2)).hasDropped("boom").hasNotDroppedErrors();
        assertThat(commitDone).isTrue();
        assertThat(rollbackDone).isFalse();
        testPublisher.assertCancelled();
    }

    @Test
    public void fluxResourcePublisherIsCancelled() {
        AtomicBoolean cancelled = new AtomicBoolean();
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        Flux<String> resourcePublisher = Flux.just("Resource", "Something Else").doOnCancel(() -> cancelled.set(true));
        Flux<String> test = Flux.usingWhen(resourcePublisher, Mono::just, ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)));
        StepVerifier.create(test).expectNext("Resource").expectComplete().verifyThenAssertThat().hasNotDroppedErrors();
        assertThat(commitDone).isTrue();
        assertThat(rollbackDone).isFalse();
        assertThat(cancelled).as("resource publisher was cancelled").isTrue();
    }

    @Test
    public void monoResourcePublisherIsNotCancelled() {
        AtomicBoolean cancelled = new AtomicBoolean();
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        Mono<String> resourcePublisher = Mono.just("Resource").doOnCancel(() -> cancelled.set(true));
        Flux<String> test = Flux.usingWhen(resourcePublisher, Flux::just, ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)));
        StepVerifier.create(test).expectNext("Resource").expectComplete().verifyThenAssertThat().hasNotDroppedErrors();
        assertThat(commitDone).isTrue();
        assertThat(rollbackDone).isFalse();
        assertThat(cancelled).as("resource publisher was not cancelled").isFalse();
    }

    @Test
    public void lateFluxResourcePublisherIsCancelledOnCancel() {
        AtomicBoolean resourceCancelled = new AtomicBoolean();
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        AtomicBoolean cancelDone = new AtomicBoolean();
        Flux<String> resourcePublisher = Flux.<String>never().doOnCancel(() -> resourceCancelled.set(true));
        StepVerifier.create(Flux.usingWhen(resourcePublisher, Flux::just, ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)), ( tr) -> Mono.fromRunnable(() -> cancelDone.set(true)))).expectSubscription().expectNoEvent(Duration.ofMillis(100)).thenCancel().verify(Duration.ofSeconds(1));
        assertThat(commitDone).as("commitDone").isFalse();
        assertThat(rollbackDone).as("rollbackDone").isFalse();
        assertThat(cancelDone).as("cancelDone").isFalse();
        assertThat(resourceCancelled).as("resource cancelled").isTrue();
    }

    @Test
    public void lateMonoResourcePublisherIsCancelledOnCancel() {
        AtomicBoolean resourceCancelled = new AtomicBoolean();
        AtomicBoolean commitDone = new AtomicBoolean();
        AtomicBoolean rollbackDone = new AtomicBoolean();
        AtomicBoolean cancelDone = new AtomicBoolean();
        Mono<String> resourcePublisher = Mono.<String>never().doOnCancel(() -> resourceCancelled.set(true));
        Mono<String> usingWhen = Mono.usingWhen(resourcePublisher, Mono::just, ( tr) -> Mono.fromRunnable(() -> commitDone.set(true)), ( tr) -> Mono.fromRunnable(() -> rollbackDone.set(true)), ( tr) -> Mono.fromRunnable(() -> cancelDone.set(true)));
        StepVerifier.create(usingWhen).expectSubscription().expectNoEvent(Duration.ofMillis(100)).thenCancel().verify(Duration.ofSeconds(1));
        assertThat(commitDone).as("commitDone").isFalse();
        assertThat(rollbackDone).as("rollbackDone").isFalse();
        assertThat(cancelDone).as("cancelDone").isFalse();
        assertThat(resourceCancelled).as("resource cancelled").isTrue();
    }

    @Test
    public void blockOnNeverResourceCanBeCancelled() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Disposable disposable = Flux.usingWhen(Flux.<String>never(), Flux::just, Flux::just, Flux::just, Flux::just).doFinally(( f) -> latch.countDown()).subscribe();
        assertThat(latch.await(500, TimeUnit.MILLISECONDS)).as("hangs before dispose").isFalse();
        disposable.dispose();
        assertThat(latch.await(100, TimeUnit.MILLISECONDS)).as("terminates after dispose").isTrue();
    }

    @Test
    public void failToGenerateClosureAppliesRollback() {
        FluxUsingWhenTest.TestResource testResource = new FluxUsingWhenTest.TestResource();
        Flux<String> test = Flux.usingWhen(Mono.just(testResource), ( tr) -> {
            throw new UnsupportedOperationException("boom");
        }, FluxUsingWhenTest.TestResource::commit, FluxUsingWhenTest.TestResource::rollback);
        StepVerifier.create(test).verifyErrorSatisfies(( e) -> assertThat(e).hasMessage("boom"));
        testResource.commitProbe.assertWasNotSubscribed();
        testResource.rollbackProbe.assertWasSubscribed();
    }

    @Test
    public void nullClosureAppliesRollback() {
        FluxUsingWhenTest.TestResource testResource = new FluxUsingWhenTest.TestResource();
        Flux<String> test = Flux.usingWhen(Mono.just(testResource), ( tr) -> null, FluxUsingWhenTest.TestResource::commit, FluxUsingWhenTest.TestResource::rollback);
        StepVerifier.create(test).verifyErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class).hasMessage("The resourceClosure function returned a null value"));
        testResource.commitProbe.assertWasNotSubscribed();
        testResource.rollbackProbe.assertWasSubscribed();
    }

    @Test
    public void apiAsyncCleanup() {
        final AtomicReference<FluxUsingWhenTest.TestResource> ref = new AtomicReference<>();
        Flux<String> flux = Flux.usingWhen(Mono.fromCallable(FluxUsingWhenTest.TestResource::new), ( d) -> {
            ref.set(d);
            return d.data().concatWithValues("work in transaction");
        }, FluxUsingWhenTest.TestResource::commit);
        StepVerifier.create(flux).expectNext("Transaction started").expectNext("work in transaction").verifyComplete();
        assertThat(ref.get()).isNotNull().matches(( tr) -> tr.commitProbe.wasSubscribed(), "commit method used").matches(( tr) -> !(tr.rollbackProbe.wasSubscribed()), "no rollback");
    }

    @Test
    public void apiAsyncCleanupFailure() {
        final RuntimeException rollbackCause = new IllegalStateException("boom");
        final AtomicReference<FluxUsingWhenTest.TestResource> ref = new AtomicReference<>();
        Flux<String> flux = Flux.usingWhen(Mono.fromCallable(FluxUsingWhenTest.TestResource::new), ( d) -> {
            ref.set(d);
            return d.data().concatWithValues("work in transaction").concatWith(Mono.error(rollbackCause));
        }, FluxUsingWhenTest.TestResource::commitError);
        StepVerifier.create(flux).expectNext("Transaction started").expectNext("work in transaction").verifyErrorSatisfies(( e) -> assertThat(e).hasMessage("Async resource cleanup failed after onError").hasCauseInstanceOf(.class).hasSuppressedException(rollbackCause));
        assertThat(ref.get()).isNotNull().matches(( tr) -> tr.commitProbe.wasSubscribed(), "commit method used despite error").matches(( tr) -> !(tr.rollbackProbe.wasSubscribed()), "no rollback");
    }

    @Test
    public void normalHasNoQueueOperations() {
        final FluxPeekFuseableTest.AssertQueueSubscription<String> assertQueueSubscription = new FluxPeekFuseableTest.AssertQueueSubscription<>();
        assertQueueSubscription.offer("foo");
        UsingWhenSubscriber<String, String> test = new UsingWhenSubscriber(new LambdaSubscriber(null, null, null, null), "resource", ( it) -> Mono.empty(), ( it) -> Mono.empty(), null, Mockito.mock(DeferredSubscription.class));
        test.onSubscribe(assertQueueSubscription);
        assertThat(test).isNotInstanceOf(QueueSubscription.class);
    }

    // == tests checking callbacks don't pile up ==
    @Test
    public void noCancelCallbackAfterComplete() {
        LongAdder cleanupCount = new LongAdder();
        Flux<String> flux = // 10 for completion
        // 100 for error
        // 1000 for cancel
        Flux.usingWhen(Mono.defer(() -> Mono.just("foo")), Mono::just, ( s) -> Mono.fromRunnable(() -> cleanupCount.add(10)), ( s) -> Mono.fromRunnable(() -> cleanupCount.add(100)), ( s) -> Mono.fromRunnable(() -> cleanupCount.add(1000)));
        flux.subscribe(new reactor.core.CoreSubscriber<Object>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
                subscription = s;
            }

            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
                subscription.cancel();
            }
        });
        assertThat(cleanupCount.sum()).isEqualTo(10);
    }

    @Test
    public void noCancelCallbackAfterError() {
        LongAdder cleanupCount = new LongAdder();
        Flux<String> flux = // 10 for completion
        // 100 for error
        // 1000 for cancel
        Flux.usingWhen(Mono.just("foo"), ( v) -> Mono.error(new IllegalStateException("boom")), ( s) -> Mono.fromRunnable(() -> cleanupCount.add(10)), ( s) -> Mono.fromRunnable(() -> cleanupCount.add(100)), ( s) -> Mono.fromRunnable(() -> cleanupCount.add(1000)));
        flux.subscribe(new reactor.core.CoreSubscriber<Object>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
                subscription = s;
            }

            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onError(Throwable t) {
                subscription.cancel();
            }

            @Override
            public void onComplete() {
            }
        });
        assertThat(cleanupCount.sum()).isEqualTo(100);
    }

    @Test
    public void noCompleteCallbackAfterCancel() throws InterruptedException {
        AtomicBoolean cancelled = new AtomicBoolean();
        LongAdder cleanupCount = new LongAdder();
        Publisher<String> badPublisher = ( s) -> s.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                new Thread(() -> {
                    s.onNext("foo1");
                    try {
                        Thread.sleep(100);
                    } catch ( e) {
                    }
                    s.onComplete();
                }).start();
            }

            @Override
            public void cancel() {
                cancelled.set(true);
            }
        });
        Flux<String> flux = // 10 for completion
        // 100 for error
        // 1000 for cancel
        Flux.usingWhen(Mono.just("foo"), ( v) -> badPublisher, ( s) -> Mono.fromRunnable(() -> cleanupCount.add(10)), ( s) -> Mono.fromRunnable(() -> cleanupCount.add(100)), ( s) -> Mono.fromRunnable(() -> cleanupCount.add(1000)));
        flux.subscribe(new reactor.core.CoreSubscriber<String>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
                subscription = s;
            }

            @Override
            public void onNext(String o) {
                subscription.cancel();
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        Thread.sleep(300);
        assertThat(cleanupCount.sum()).isEqualTo(1000);
        assertThat(cancelled).as("source cancelled").isTrue();
    }

    @Test
    public void noErrorCallbackAfterCancel() throws InterruptedException {
        AtomicBoolean cancelled = new AtomicBoolean();
        LongAdder cleanupCount = new LongAdder();
        Publisher<String> badPublisher = ( s) -> s.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                new Thread(() -> {
                    s.onNext("foo1");
                    try {
                        Thread.sleep(100);
                    } catch ( e) {
                    }
                    s.onError(new IllegalStateException("boom"));
                }).start();
            }

            @Override
            public void cancel() {
                cancelled.set(true);
            }
        });
        Flux<String> flux = // 10 for completion
        // 100 for error
        // 1000 for cancel
        Flux.usingWhen(Mono.just("foo"), ( v) -> badPublisher, ( s) -> Mono.fromRunnable(() -> cleanupCount.add(10)), ( s) -> Mono.fromRunnable(() -> cleanupCount.add(100)), ( s) -> Mono.fromRunnable(() -> cleanupCount.add(1000)));
        flux.subscribe(new reactor.core.CoreSubscriber<String>() {
            Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
                subscription = s;
            }

            @Override
            public void onNext(String o) {
                subscription.cancel();
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        Thread.sleep(300);
        assertThat(cleanupCount.sum()).isEqualTo(1000);
        assertThat(cancelled).as("source cancelled").isTrue();
    }

    // == scanUnsafe tests ==
    @Test
    public void scanOperator() {
        FluxUsingWhen<Object, Object> op = new FluxUsingWhen(Mono.empty(), Mono::just, Mono::just, Mono::just, Mono::just);
        assertThat(op.scanUnsafe(ACTUAL)).isSameAs(op.scanUnsafe(ACTUAL_METADATA)).isSameAs(op.scanUnsafe(BUFFERED)).isSameAs(op.scanUnsafe(CAPACITY)).isSameAs(op.scanUnsafe(CANCELLED)).isSameAs(op.scanUnsafe(DELAY_ERROR)).isSameAs(op.scanUnsafe(ERROR)).isSameAs(op.scanUnsafe(LARGE_BUFFERED)).isSameAs(op.scanUnsafe(NAME)).isSameAs(op.scanUnsafe(PARENT)).isSameAs(op.scanUnsafe(RUN_ON)).isSameAs(op.scanUnsafe(PREFETCH)).isSameAs(op.scanUnsafe(REQUESTED_FROM_DOWNSTREAM)).isSameAs(op.scanUnsafe(TERMINATED)).isSameAs(op.scanUnsafe(TAGS)).isNull();
    }

    @Test
    public void scanResourceSubscriber() {
        reactor.core.CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        ResourceSubscriber<String, Integer> op = new ResourceSubscriber(actual, ( s) -> Flux.just(s.length()), Mono::just, Mono::just, Mono::just, true);
        final Subscription parent = Operators.emptySubscription();
        op.onSubscribe(parent);
        assertThat(op.scan(PARENT)).as("PARENT").isSameAs(parent);
        assertThat(op.scan(ACTUAL)).as("ACTUAL").isSameAs(actual);
        assertThat(op.scan(PREFETCH)).as("PREFETCH").isEqualTo(Integer.MAX_VALUE);
        assertThat(op.scan(TERMINATED)).as("TERMINATED").isFalse();
        op.resourceProvided = true;
        assertThat(op.scan(TERMINATED)).as("TERMINATED resourceProvided").isTrue();
        assertThat(op.scanUnsafe(CANCELLED)).as("CANCELLED not supported").isNull();
    }

    @Test
    public void scanUsingWhenSubscriber() {
        reactor.core.CoreSubscriber<? super Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        UsingWhenSubscriber<Integer, String> op = new UsingWhenSubscriber(actual, "RESOURCE", Mono::just, Mono::just, Mono::just, null);
        final Subscription parent = Operators.emptySubscription();
        op.onSubscribe(parent);
        assertThat(op.scan(PARENT)).as("PARENT").isSameAs(parent);
        assertThat(op.scan(ACTUAL)).as("ACTUAL").isSameAs(actual).isSameAs(op.actual());
        assertThat(op.scan(TERMINATED)).as("pre TERMINATED").isFalse();
        assertThat(op.scan(CANCELLED)).as("pre CANCELLED").isFalse();
        op.deferredError(new IllegalStateException("boom"));
        assertThat(op.scan(TERMINATED)).as("TERMINATED with error").isTrue();
        assertThat(op.scan(ERROR)).as("ERROR").hasMessage("boom");
        op.cancel();
        assertThat(op.scan(CANCELLED)).as("CANCELLED").isTrue();
    }

    @Test
    public void scanCommitInner() {
        reactor.core.CoreSubscriber<? super Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        UsingWhenSubscriber<Integer, String> up = new UsingWhenSubscriber(actual, "RESOURCE", Mono::just, Mono::just, Mono::just, null);
        final Subscription parent = Operators.emptySubscription();
        up.onSubscribe(parent);
        FluxUsingWhen.CommitInner op = new FluxUsingWhen.CommitInner(up);
        assertThat(op.scan(PARENT)).as("PARENT").isSameAs(up);
        assertThat(op.scan(ACTUAL)).as("ACTUAL").isSameAs(up.actual);
        assertThat(op.scan(TERMINATED)).as("TERMINATED before").isFalse();
        op.onError(new IllegalStateException("boom"));
        assertThat(op.scan(TERMINATED)).as("TERMINATED by error").isSameAs(up.scan(TERMINATED)).isTrue();
        assertThat(up.scan(ERROR)).as("parent ERROR").hasMessage("Async resource cleanup failed after onComplete").hasCause(new IllegalStateException("boom"));
        assertThat(op.scanUnsafe(PREFETCH)).as("PREFETCH not supported").isNull();
    }

    @Test
    public void scanRollbackInner() {
        reactor.core.CoreSubscriber<? super Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        UsingWhenSubscriber<Integer, String> up = new UsingWhenSubscriber(actual, "RESOURCE", Mono::just, Mono::just, Mono::just, null);
        final Subscription parent = Operators.emptySubscription();
        up.onSubscribe(parent);
        FluxUsingWhen.RollbackInner op = new FluxUsingWhen.RollbackInner(up, new IllegalStateException("rollback cause"));
        assertThat(op.scan(PARENT)).as("PARENT").isSameAs(up);
        assertThat(op.scan(ACTUAL)).as("ACTUAL").isSameAs(up.actual);
        assertThat(op.scan(TERMINATED)).as("TERMINATED before").isFalse();
        op.onComplete();
        assertThat(op.scan(TERMINATED)).as("TERMINATED by complete").isSameAs(up.scan(TERMINATED)).isTrue();
        assertThat(up.scan(ERROR)).as("parent ERROR").hasMessage("rollback cause");
        assertThat(op.scanUnsafe(PREFETCH)).as("PREFETCH not supported").isNull();
    }

    @Test
    public void scanCancelInner() {
        reactor.core.CoreSubscriber<? super Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        UsingWhenSubscriber<Integer, String> up = new UsingWhenSubscriber(actual, "RESOURCE", Mono::just, Mono::just, Mono::just, null);
        final Subscription parent = Operators.emptySubscription();
        up.onSubscribe(parent);
        FluxUsingWhen.CancelInner op = new FluxUsingWhen.CancelInner(up);
        assertThat(op.scan(PARENT)).as("PARENT").isSameAs(up);
        assertThat(op.scan(ACTUAL)).as("ACTUAL").isSameAs(up.actual);
        assertThat(op.scanUnsafe(PREFETCH)).as("PREFETCH not supported").isNull();
    }

    // == utility test classes ==
    static class TestResource {
        private static final Duration DELAY = Duration.ofMillis(100);

        final Level level;

        PublisherProbe<Integer> commitProbe = PublisherProbe.empty();

        PublisherProbe<Integer> rollbackProbe = PublisherProbe.empty();

        TestResource() {
            this.level = Level.FINE;
        }

        TestResource(Level level) {
            this.level = level;
        }

        public Flux<String> data() {
            return Flux.just("Transaction started");
        }

        public Flux<Integer> commit() {
            System.out.println("commit");
            this.commitProbe = PublisherProbe.of(Flux.just(3, 2, 1).log("commit method used", level, ON_NEXT, ON_COMPLETE));
            return commitProbe.flux();
        }

        public Flux<Integer> commitDelay() {
            this.commitProbe = PublisherProbe.of(Flux.just(3, 2, 1).delayElements(FluxUsingWhenTest.TestResource.DELAY).log("commit method used", level, ON_NEXT, ON_COMPLETE));
            return commitProbe.flux();
        }

        public Flux<Integer> commitError() {
            this.commitProbe = PublisherProbe.of(// results in divide by 0
            Flux.just(3, 2, 1).delayElements(FluxUsingWhenTest.TestResource.DELAY).map(( i) -> 100 / (i - 1)).log("commit method used", level, ON_NEXT, ON_COMPLETE));
            return commitProbe.flux();
        }

        @Nullable
        public Flux<Integer> commitNull() {
            return null;
        }

        public Flux<Integer> rollback() {
            this.rollbackProbe = PublisherProbe.of(Flux.just(5, 4, 3, 2, 1).log("rollback method used", level, ON_NEXT, ON_COMPLETE));
            return rollbackProbe.flux();
        }

        public Flux<Integer> rollbackDelay() {
            this.rollbackProbe = PublisherProbe.of(Flux.just(5, 4, 3, 2, 1).delayElements(FluxUsingWhenTest.TestResource.DELAY).log("rollback method used", level, ON_NEXT, ON_COMPLETE));
            return rollbackProbe.flux();
        }

        public Flux<Integer> rollbackError() {
            this.rollbackProbe = PublisherProbe.of(// results in divide by 0
            Flux.just(5, 4, 3, 2, 1).delayElements(FluxUsingWhenTest.TestResource.DELAY).map(( i) -> 100 / (i - 1)).log("rollback method used", level, ON_NEXT, ON_COMPLETE));
            return rollbackProbe.flux();
        }

        @Nullable
        public Flux<Integer> rollbackNull() {
            return null;
        }
    }
}

