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


import MonoFilterWhen.FilterWhenInner;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import SignalType.CANCEL;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;


public class MonoFilterWhenTest {
    @Test
    public void normalFiltered() {
        StepVerifier.withVirtualTime(() -> Mono.just(1).filterWhen(( v) -> Mono.just(((v % 2) == 0)).delayElement(Duration.ofMillis(100)))).expectSubscription().expectNoEvent(Duration.ofMillis(100)).verifyComplete();
    }

    @Test
    public void normalNotFiltered() {
        StepVerifier.withVirtualTime(() -> Mono.just(2).filterWhen(( v) -> Mono.just(((v % 2) == 0)).delayElement(Duration.ofMillis(100)))).expectSubscription().expectNoEvent(Duration.ofMillis(100)).expectNext(2).verifyComplete();
    }

    @Test
    public void normalSyncFiltered() {
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> Mono.just(((v % 2) == 0)).hide())).verifyComplete();
    }

    @Test
    public void normalSyncNotFiltered() {
        StepVerifier.create(Mono.just(2).filterWhen(( v) -> Mono.just(((v % 2) == 0)).hide())).expectNext(2).verifyComplete();
    }

    @Test
    public void normalSyncFusedFiltered() {
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> Mono.just(((v % 2) == 0)))).verifyComplete();
    }

    @Test
    public void normalSyncFusedNotFiltered() {
        StepVerifier.create(Mono.just(2).filterWhen(( v) -> Mono.just(((v % 2) == 0)))).expectNext(2).verifyComplete();
    }

    @Test
    public void allEmpty() {
        StepVerifier.create(Mono.just(2).filterWhen(( v) -> Mono.<Boolean>empty().hide())).verifyComplete();
    }

    @Test
    public void allEmptyFused() {
        StepVerifier.create(Mono.just(2).filterWhen(( v) -> Mono.empty())).verifyComplete();
    }

    @Test
    public void empty() {
        StepVerifier.create(Mono.<Integer>empty().filterWhen(( v) -> Mono.just(true))).verifyComplete();
    }

    @Test
    public void emptyBackpressured() {
        StepVerifier.create(Mono.<Integer>empty().filterWhen(( v) -> Mono.just(true)), 0L).verifyComplete();
    }

    @Test
    public void error() {
        StepVerifier.create(Mono.<Integer>error(new IllegalStateException()).filterWhen(( v) -> Mono.just(true))).verifyError(IllegalStateException.class);
    }

    @Test
    public void errorBackpressured() {
        StepVerifier.create(Mono.<Integer>error(new IllegalStateException()).filterWhen(( v) -> Mono.just(true)), 0L).verifyError(IllegalStateException.class);
    }

    @Test
    public void backpressureExactlyOne() {
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> Mono.just(true)), 1L).expectNext(1).verifyComplete();
    }

    @Test
    public void oneAndErrorInner() {
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> ( s) -> {
            s.onSubscribe(Operators.emptySubscription());
            s.onNext(true);
            s.onError(new IllegalStateException());
        })).expectNext(1).expectComplete().verifyThenAssertThat().hasDroppedErrorsSatisfying(( c) -> assertThat(c).hasSize(1).element(0).isInstanceOf(.class));
    }

    @Test
    public void predicateThrows() {
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> {
            throw new IllegalStateException();
        })).verifyError(IllegalStateException.class);
    }

    @Test
    public void predicateNull() {
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> null)).verifyError(NullPointerException.class);
    }

    @Test
    public void predicateError() {
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> Mono.<Boolean>error(new IllegalStateException()).hide())).verifyError(IllegalStateException.class);
    }

    @Test
    public void predicateErrorFused() {
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> Mono.fromCallable(() -> {
            throw new IllegalStateException();
        }))).verifyError(IllegalStateException.class);
    }

    @Test
    public void take1Cancel() {
        AtomicLong onNextCount = new AtomicLong();
        AtomicReference<SignalType> endSignal = new AtomicReference<>();
        BaseSubscriber<Object> bs = new BaseSubscriber<Object>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                requestUnbounded();
            }

            @Override
            public void hookOnNext(Object t) {
                onNextCount.incrementAndGet();
                cancel();
                onComplete();
            }

            @Override
            protected void hookFinally(SignalType type) {
                endSignal.set(type);
            }
        };
        Mono.just(1).filterWhen(( v) -> Mono.just(true).hide()).subscribe(bs);
        assertThat(onNextCount.get()).isEqualTo(1);
        assertThat(endSignal.get()).isEqualTo(CANCEL);
    }

    @Test
    public void take1CancelBackpressured() {
        AtomicLong onNextCount = new AtomicLong();
        AtomicReference<SignalType> endSignal = new AtomicReference<>();
        BaseSubscriber<Object> bs = new BaseSubscriber<Object>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
            }

            @Override
            public void hookOnNext(Object t) {
                onNextCount.incrementAndGet();
                cancel();
                onComplete();
            }

            @Override
            protected void hookFinally(SignalType type) {
                endSignal.set(type);
            }
        };
        Mono.just(1).filterWhen(( v) -> Mono.just(true).hide()).subscribe(bs);
        assertThat(onNextCount.get()).isEqualTo(1);
        assertThat(endSignal.get()).isEqualTo(CANCEL);
    }

    @Test
    public void cancel() {
        final EmitterProcessor<Boolean> pp = EmitterProcessor.create();
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> pp)).thenCancel();
        assertThat(pp.hasDownstreams()).isFalse();
    }

    @Test
    public void innerFluxCancelled() {
        AtomicInteger cancelCount = new AtomicInteger();
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> Flux.just(true, false, false).doOnCancel(cancelCount::incrementAndGet))).expectNext(1).verifyComplete();
        assertThat(cancelCount.get()).isEqualTo(1);
    }

    @Test
    public void innerFluxOnlyConsidersFirstValue() {
        StepVerifier.create(Mono.just(1).filterWhen(( v) -> Flux.just(false, true, true))).verifyComplete();
    }

    @Test
    public void innerMonoNotCancelled() {
        AtomicInteger cancelCount = new AtomicInteger();
        StepVerifier.create(Mono.just(3).filterWhen(( v) -> Mono.just(true).doOnCancel(cancelCount::incrementAndGet))).expectNext(3).verifyComplete();
        assertThat(cancelCount.get()).isEqualTo(0);
    }

    @Test
    public void scanTerminatedOnlyTrueIfFilterTerminated() {
        AtomicReference<Subscriber> subscriber = new AtomicReference<>();
        TestPublisher<Boolean> filter = TestPublisher.create();
        new MonoFilterWhen(new Mono<Integer>() {
            @Override
            public void subscribe(CoreSubscriber<? super Integer> actual) {
                subscriber.set(actual);
                // NON-EMPTY SOURCE WILL TRIGGER FILTER SUBSCRIPTION
                actual.onNext(2);
                actual.onComplete();
            }
        }, ( w) -> filter).subscribe();
        assertThat(subscriber.get()).isNotNull().isInstanceOf(Scannable.class);
        Boolean terminated = ((Scannable) (subscriber.get())).scan(TERMINATED);
        assertThat(terminated).isFalse();
        filter.emit(Boolean.TRUE);
        terminated = ((Scannable) (subscriber.get())).scan(TERMINATED);
        assertThat(terminated).isTrue();
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<String> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        MonoFilterWhen.MonoFilterWhenMain<String> test = new MonoFilterWhen.MonoFilterWhenMain<>(actual, ( s) -> Mono.just(false));
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        // TERMINATED IS COVERED BY TEST ABOVE
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanFilterWhenInner() {
        CoreSubscriber<String> actual = new LambdaMonoSubscriber(null, ( e) -> {
        }, null, null);
        MonoFilterWhen.MonoFilterWhenMain<String> main = new MonoFilterWhen.MonoFilterWhenMain<>(actual, ( s) -> Mono.just(false));
        MonoFilterWhen.FilterWhenInner test = new MonoFilterWhen.FilterWhenInner(main, true);
        Subscription innerSubscription = Operators.emptySubscription();
        test.onSubscribe(innerSubscription);
        assertThat(test.scan(ACTUAL)).isSameAs(main);
        assertThat(test.scan(PARENT)).isSameAs(innerSubscription);
        assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(1L);
        assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(TERMINATED)).isTrue();
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(0L);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }
}

