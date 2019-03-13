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


import FluxBufferTimeout.BufferTimeoutSubscriber;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.CAPACITY;
import Scannable.Attr.PARENT;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.RUN_ON;
import Scannable.Attr.TERMINATED;
import Scheduler.Worker;
import TestPublisher.Violation.REQUEST_OVERFLOW;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Scannable;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.TestPublisher;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.test.util.RaceTestUtils;


public class FluxBufferTimeoutTest {
    @Test
    public void bufferWithTimeoutAccumulateOnTimeOrSize() {
        StepVerifier.withVirtualTime(this::scenario_bufferWithTimeoutAccumulateOnTimeOrSize).thenAwait(Duration.ofMillis(1500)).assertNext(( s) -> assertThat(s).containsExactly(1, 2, 3, 4, 5)).thenAwait(Duration.ofMillis(2000)).assertNext(( s) -> assertThat(s).containsExactly(6)).verifyComplete();
    }

    @Test
    public void bufferWithTimeoutAccumulateOnTimeOrSize2() {
        StepVerifier.withVirtualTime(this::scenario_bufferWithTimeoutAccumulateOnTimeOrSize2).thenAwait(Duration.ofMillis(1500)).assertNext(( s) -> assertThat(s).containsExactly(1, 2, 3, 4, 5)).thenAwait(Duration.ofMillis(2000)).assertNext(( s) -> assertThat(s).containsExactly(6)).verifyComplete();
    }

    @Test
    public void bufferWithTimeoutThrowingExceptionOnTimeOrSizeIfDownstreamDemandIsLow() {
        StepVerifier.withVirtualTime(this::scenario_bufferWithTimeoutThrowingExceptionOnTimeOrSizeIfDownstreamDemandIsLow, 0).expectSubscription().expectNoEvent(Duration.ofMillis(300)).thenRequest(1).expectNoEvent(Duration.ofMillis(100)).assertNext(( s) -> assertThat(s).containsExactly(1)).expectNoEvent(Duration.ofMillis(300)).verifyErrorSatisfies(( e) -> assertThat(e).hasMessage("Could not emit buffer due to lack of requests").isInstanceOf(.class));
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<List<String>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        final Scheduler.Worker worker = Schedulers.elastic().createWorker();
        BufferTimeoutSubscriber<String, List<String>> test = new BufferTimeoutSubscriber<String, List<String>>(actual, 123, 1000, worker, ArrayList::new);
        try {
            Subscription subscription = Operators.emptySubscription();
            test.onSubscribe(subscription);
            test.requested = 3L;
            test.index = 100;
            assertThat(test.scan(RUN_ON)).isSameAs(worker);
            assertThat(test.scan(PARENT)).isSameAs(subscription);
            assertThat(test.scan(ACTUAL)).isSameAs(actual);
            assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(3L);
            assertThat(test.scan(CAPACITY)).isEqualTo(123);
            assertThat(test.scan(BUFFERED)).isEqualTo(23);
            assertThat(test.scan(CANCELLED)).isFalse();
            assertThat(test.scan(TERMINATED)).isFalse();
            test.onError(new IllegalStateException("boom"));
            assertThat(test.scan(CANCELLED)).isFalse();
            assertThat(test.scan(TERMINATED)).isTrue();
        } finally {
            worker.dispose();
        }
    }

    @Test
    public void scanOperator() {
        final Flux<List<Integer>> flux = Flux.just(1).bufferTimeout(3, Duration.ofSeconds(1));
        assertThat(flux).isInstanceOf(Scannable.class);
        assertThat(((Scannable) (flux)).scan(RUN_ON)).isSameAs(Schedulers.parallel());
    }

    @Test
    public void shouldShowActualSubscriberDemand() {
        Subscription[] subscriptionsHolder = new Subscription[1];
        CoreSubscriber<List<String>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, ( s) -> subscriptionsHolder[0] = s);
        BufferTimeoutSubscriber<String, List<String>> test = new BufferTimeoutSubscriber<String, List<String>>(actual, 123, 1000, Schedulers.elastic().createWorker(), ArrayList::new);
        Subscription subscription = Operators.emptySubscription();
        test.onSubscribe(subscription);
        subscriptionsHolder[0].request(10);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(10L);
        subscriptionsHolder[0].request(5);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(15L);
    }

    @Test
    public void downstreamDemandShouldBeAbleToDecreaseOnFullBuffer() {
        Subscription[] subscriptionsHolder = new Subscription[1];
        CoreSubscriber<List<String>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, ( s) -> subscriptionsHolder[0] = s);
        BufferTimeoutSubscriber<String, List<String>> test = new BufferTimeoutSubscriber<String, List<String>>(actual, 5, 1000, Schedulers.elastic().createWorker(), ArrayList::new);
        Subscription subscription = Operators.emptySubscription();
        test.onSubscribe(subscription);
        subscriptionsHolder[0].request(1);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(1L);
        for (int i = 0; i < 5; i++) {
            test.onNext(String.valueOf(i));
        }
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(0L);
    }

    @Test
    public void downstreamDemandShouldBeAbleToDecreaseOnTimeSpan() {
        Subscription[] subscriptionsHolder = new Subscription[1];
        CoreSubscriber<List<String>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, ( s) -> subscriptionsHolder[0] = s);
        VirtualTimeScheduler timeScheduler = VirtualTimeScheduler.getOrSet();
        BufferTimeoutSubscriber<String, List<String>> test = new BufferTimeoutSubscriber<String, List<String>>(actual, 5, 100, timeScheduler.createWorker(), ArrayList::new);
        Subscription subscription = Operators.emptySubscription();
        test.onSubscribe(subscription);
        subscriptionsHolder[0].request(1);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(1L);
        timeScheduler.advanceTimeBy(Duration.ofMillis(100));
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(1L);
        test.onNext(String.valueOf("0"));
        timeScheduler.advanceTimeBy(Duration.ofMillis(100));
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(0L);
    }

    @Test
    public void scanSubscriberCancelled() {
        CoreSubscriber<List<String>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        BufferTimeoutSubscriber<String, List<String>> test = new BufferTimeoutSubscriber<String, List<String>>(actual, 123, 1000, Schedulers.elastic().createWorker(), ArrayList::new);
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(TERMINATED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
        assertThat(test.scan(TERMINATED)).isFalse();
    }

    @Test
    public void flushShouldNotRaceWithNext() {
        Set<Integer> seen = new HashSet<>();
        Consumer<List<Integer>> consumer = ( integers) -> {
            for (Integer i : integers) {
                if (!(seen.add(i))) {
                    throw new IllegalStateException(("Duplicate! " + i));
                }
            }
        };
        CoreSubscriber<List<Integer>> actual = new LambdaSubscriber(consumer, null, null, null);
        BufferTimeoutSubscriber<Integer, List<Integer>> test = new BufferTimeoutSubscriber<Integer, List<Integer>>(actual, 3, 1000, Schedulers.elastic().createWorker(), ArrayList::new);
        test.onSubscribe(Operators.emptySubscription());
        AtomicInteger counter = new AtomicInteger();
        for (int i = 0; i < 500; i++) {
            RaceTestUtils.race(() -> test.onNext(counter.getAndIncrement()), () -> test.flushCallback(null), Schedulers.elastic());
        }
    }

    // see https://github.com/reactor/reactor-core/issues/1247
    @Test
    public void rejectedOnNextLeadsToOnError() {
        Scheduler scheduler = Schedulers.newSingle("rejectedOnNextLeadsToOnError");
        scheduler.dispose();
        StepVerifier.create(Flux.just(1, 2, 3).bufferTimeout(4, Duration.ofMillis(500), scheduler)).expectError(RejectedExecutionException.class).verify(Duration.ofSeconds(1));
    }

    @Test
    public void discardOnCancel() {
        StepVerifier.create(Flux.just(1, 2, 3).concatWith(Mono.never()).bufferTimeout(10, Duration.ofMillis(100))).thenAwait(Duration.ofMillis(10)).thenCancel().verifyThenAssertThat().hasDiscardedExactly(1, 2, 3);
    }

    @Test
    public void discardOnFlushWithoutRequest() {
        TestPublisher<Integer> testPublisher = TestPublisher.createNoncompliant(REQUEST_OVERFLOW);
        StepVerifier.create(testPublisher.flux().bufferTimeout(10, Duration.ofMillis(200)), StepVerifierOptions.create().initialRequest(0)).then(() -> testPublisher.emit(1, 2, 3)).thenAwait(Duration.ofMillis(250)).expectErrorMatches(Exceptions::isOverflow).verifyThenAssertThat().hasDiscardedExactly(1, 2, 3);
    }

    @Test
    public void discardOnTimerRejected() {
        Scheduler scheduler = Schedulers.newSingle("discardOnTimerRejected");
        StepVerifier.create(Flux.just(1, 2, 3).doOnNext(( n) -> scheduler.dispose()).bufferTimeout(10, Duration.ofMillis(100), scheduler)).expectErrorSatisfies(( e) -> assertThat(e).isInstanceOf(.class)).verifyThenAssertThat().hasDiscardedExactly(1);
    }

    @Test
    public void discardOnError() {
        StepVerifier.create(Flux.just(1, 2, 3).concatWith(Mono.error(new IllegalStateException("boom"))).bufferTimeout(10, Duration.ofMillis(100))).expectErrorMessage("boom").verifyThenAssertThat().hasDiscardedExactly(1, 2, 3);
    }
}

