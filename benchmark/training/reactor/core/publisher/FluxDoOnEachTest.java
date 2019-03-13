/**
 * Copyright (c) 2011-2018 Pivotal Software Inc, All Rights Reserved.
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


import Fuseable.ASYNC;
import Fuseable.SYNC;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable.ConditionalSubscriber;
import reactor.core.publisher.FluxDoOnEach.DoOnEachConditionalSubscriber;
import reactor.core.publisher.FluxDoOnEach.DoOnEachFuseableConditionalSubscriber;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.util.context.Context;


@RunWith(JUnitParamsRunner.class)
public class FluxDoOnEachTest {
    @Test(expected = NullPointerException.class)
    public void nullSource() {
        new FluxDoOnEach(null, null);
    }

    private static final String sourceErrorMessage = "boomSource";

    // see https://github.com/reactor/reactor-core/issues/1056
    @Test
    public void fusion() {
        AtomicInteger invocationCount = new AtomicInteger();
        Flux<String> sourceWithFusionAsync = Flux.just("foo").publishOn(Schedulers.elastic()).flatMap(( v) -> Flux.just(("flatMap_" + v)).doOnEach(( sig) -> invocationCount.incrementAndGet()));
        StepVerifier.create(sourceWithFusionAsync).expectNoFusionSupport().expectNext("flatMap_foo").verifyComplete();
        assertThat(invocationCount).as("doOnEach invoked").hasValue(2);
    }

    @Test
    public void fusedSync() {
        AtomicReference<String> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        LongAdder state = new LongAdder();
        StepVerifier.create(Flux.just("sync").doOnEach(( s) -> {
            if (s.isOnNext()) {
                onNext.set(s.get());
                state.increment();
            } else
                if (s.isOnError()) {
                    onError.set(s.getThrowable());
                } else
                    if (s.isOnComplete()) {
                        onComplete.set(true);
                    }


        })).expectFusion(SYNC, SYNC).expectNext("sync").verifyComplete();
        assertThat(onNext).hasValue("sync");
        assertThat(onError).hasValue(null);
        assertThat(onComplete).isTrue();
    }

    @Test
    public void fusedSyncCallbackError() {
        AtomicReference<String> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        LongAdder state = new LongAdder();
        StepVerifier.create(Flux.just("sync").doOnEach(( s) -> {
            if (s.isOnNext()) {
                onNext.set(s.get());
                state.increment();
            } else
                if (s.isOnError()) {
                    onError.set(s.getThrowable());
                } else
                    if (s.isOnComplete()) {
                        throw new IllegalStateException("boom");
                    }


        })).expectFusion(SYNC, SYNC).expectNext("sync").verifyErrorMessage("boom");
        assertThat(onNext).hasValue("sync");
        assertThat(onError.get()).isNull();
        assertThat(onComplete).isFalse();
    }

    @Test
    public void fusedAsync() {
        AtomicReference<String> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        LongAdder state = new LongAdder();
        StepVerifier.create(Flux.just("foo").publishOn(Schedulers.immediate()).map(( s) -> s + "_async").doOnEach(( s) -> {
            if (s.isOnNext()) {
                onNext.set(s.get());
                state.increment();
            } else
                if (s.isOnError()) {
                    onError.set(s.getThrowable());
                } else
                    if (s.isOnComplete()) {
                        onComplete.set(true);
                    }


        })).expectFusion(ASYNC, ASYNC).expectNext("foo_async").verifyComplete();
        assertThat(onNext).hasValue("foo_async");
        assertThat(onError).hasValue(null);
        assertThat(onComplete).isTrue();
    }

    @Test
    public void fusedAsyncCallbackTransientError() {
        AtomicReference<String> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        LongAdder state = new LongAdder();
        StepVerifier.create(Flux.just("foo").publishOn(Schedulers.immediate()).map(( s) -> s + "_async").doOnEach(( s) -> {
            if (s.isOnNext()) {
                onNext.set(s.get());
                state.increment();
            } else
                if (s.isOnError()) {
                    onError.set(s.getThrowable());
                } else
                    if (s.isOnComplete()) {
                        throw new IllegalStateException("boom");
                    }


        })).expectFusion(ASYNC, ASYNC).expectNext("foo_async").verifyErrorMessage("boom");
        assertThat(onNext).hasValue("foo_async");
        assertThat(onError.get()).isNotNull().hasMessage("boom");
        assertThat(onComplete).isFalse();
    }

    @Test
    public void fusedAsyncCallbackErrorsOnTerminal() {
        AtomicReference<String> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        LongAdder state = new LongAdder();
        StepVerifier.create(Flux.just("foo").publishOn(Schedulers.immediate()).map(( s) -> s + "_async").doOnEach(( s) -> {
            if (s.isOnNext()) {
                onNext.set(s.get());
            } else {
                throw new IllegalStateException("boom");
            }
        })).expectFusion(ASYNC, ASYNC).expectNext("foo_async").verifyErrorMessage("boom");
        assertThat(onNext).hasValue("foo_async");
        assertThat(onError.get()).isNull();
        assertThat(onComplete).isFalse();
    }

    @Test
    public void conditionalTryOnNext() {
        ArrayList<Signal<Boolean>> signals = new ArrayList<>();
        ConditionalSubscriber<Boolean> actual = new FluxPeekFuseableTest.ConditionalAssertSubscriber<Boolean>() {
            @Override
            public boolean tryOnNext(Boolean v) {
                super.tryOnNext(v);
                return v;
            }
        };
        DoOnEachConditionalSubscriber<Boolean> test = new DoOnEachConditionalSubscriber(actual, signals::add, false);
        FluxPeekFuseableTest.AssertQueueSubscription<Boolean> qs = new FluxPeekFuseableTest.AssertQueueSubscription<>();
        test.onSubscribe(qs);
        assertThat(test.tryOnNext(true)).isTrue();
        assertThat(test.tryOnNext(false)).isFalse();
        test.onComplete();
        assertThat(signals).hasSize(2);
        assertThat(signals.get(0)).matches(Signal::isOnNext).matches(( s) -> (s.get()) == Boolean.TRUE);
        assertThat(signals.get(1)).matches(Signal::isOnComplete);
        List<Boolean> actualTryNext = ((FluxPeekFuseableTest.ConditionalAssertSubscriber<Boolean>) (actual)).next;
        assertThat(actualTryNext).hasSize(2);
        assertThat(actualTryNext.get(0)).isTrue();
        assertThat(actualTryNext.get(1)).isFalse();
    }

    @Test
    public void conditionalFuseableTryOnNext() {
        ArrayList<Signal<Boolean>> signals = new ArrayList<>();
        FluxPeekFuseableTest.ConditionalAssertSubscriber<Boolean> actual = new FluxPeekFuseableTest.ConditionalAssertSubscriber<Boolean>() {
            @Override
            public boolean tryOnNext(Boolean v) {
                super.tryOnNext(v);
                return v;
            }
        };
        DoOnEachFuseableConditionalSubscriber<Boolean> test = new DoOnEachFuseableConditionalSubscriber(actual, signals::add, false);
        FluxPeekFuseableTest.AssertQueueSubscription<Boolean> qs = new FluxPeekFuseableTest.AssertQueueSubscription<>();
        test.onSubscribe(qs);
        assertThat(test.tryOnNext(true)).isTrue();
        assertThat(test.tryOnNext(false)).isFalse();
        test.onComplete();
        assertThat(signals).hasSize(2);
        assertThat(signals.get(0)).matches(Signal::isOnNext).matches(( s) -> (s.get()) == Boolean.TRUE);
        assertThat(signals.get(1)).matches(Signal::isOnComplete);
        List<Boolean> actualTryNext = ((FluxPeekFuseableTest.ConditionalAssertSubscriber<Boolean>) (actual)).next;
        assertThat(actualTryNext).hasSize(2);
        assertThat(actualTryNext.get(0)).isTrue();
        assertThat(actualTryNext.get(1)).isFalse();
    }

    @Test
    public void nextCompleteAndErrorHaveContext() {
        Context context = Context.of("foo", "bar");
        List<Signal> signals = new ArrayList<>();
        StepVerifier.create(Flux.just("hello").doOnEach(signals::add), StepVerifierOptions.create().withInitialContext(context)).expectNext("hello").verifyComplete();
        assertThat(signals).allSatisfy(( signal) -> assertThat(signal.getContext().hasKey("foo")).as("has Context value").isTrue());
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxDoOnEach<Integer> peek = new FluxDoOnEach(Flux.just(1), ( s) -> {
        });
        FluxDoOnEach.DoOnEachSubscriber<Integer> test = new FluxDoOnEach.DoOnEachSubscriber<>(actual, peek.onSignal, false);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
    }

    // https://github.com/reactor/reactor-core/issues/1067
    @Test
    public void shallExecuteSideEffectsCallback() {
        Flux<Integer> result = // .doOnEach(sig -> System.out.println("SIGNAL beforeMap " + sig))// <- if enabled than everything is fine
        Mono.just(Arrays.asList(1, 2)).map(( x) -> x).doOnEach(( sig) -> {
            throw new RuntimeException("expected");
        }).flatMapIterable(Function.identity());
        StepVerifier.create(result).expectError().verify();
    }
}

