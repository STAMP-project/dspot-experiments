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


import FluxGroupBy.GroupByMain;
import FluxGroupBy.UnicastGroupedFlux;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.Queues;


public class FluxGroupByTest extends FluxOperatorTest<String, GroupedFlux<Integer, String>> {
    @Test
    public void normal() {
        AssertSubscriber<GroupedFlux<Integer, Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 10).groupBy(( k) -> k % 2).subscribe(ts);
        ts.assertValueCount(2).assertNoError().assertComplete();
        AssertSubscriber<Integer> ts1 = AssertSubscriber.create();
        ts.values().get(0).subscribe(ts1);
        ts1.assertValues(1, 3, 5, 7, 9);
        AssertSubscriber<Integer> ts2 = AssertSubscriber.create();
        ts.values().get(1).subscribe(ts2);
        ts2.assertValues(2, 4, 6, 8, 10);
    }

    @Test
    public void normalValueSelector() {
        AssertSubscriber<GroupedFlux<Integer, Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 10).groupBy(( k) -> k % 2, ( v) -> -v).subscribe(ts);
        ts.assertValueCount(2).assertNoError().assertComplete();
        AssertSubscriber<Integer> ts1 = AssertSubscriber.create();
        ts.values().get(0).subscribe(ts1);
        ts1.assertValues((-1), (-3), (-5), (-7), (-9));
        AssertSubscriber<Integer> ts2 = AssertSubscriber.create();
        ts.values().get(1).subscribe(ts2);
        ts2.assertValues((-2), (-4), (-6), (-8), (-10));
    }

    @Test
    public void takeTwoGroupsOnly() {
        AssertSubscriber<GroupedFlux<Integer, Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 10).groupBy(( k) -> k % 3).take(2).subscribe(ts);
        ts.assertValueCount(2).assertNoError().assertComplete();
        AssertSubscriber<Integer> ts1 = AssertSubscriber.create();
        ts.values().get(0).subscribe(ts1);
        ts1.assertValues(1, 4, 7, 10);
        AssertSubscriber<Integer> ts2 = AssertSubscriber.create();
        ts.values().get(1).subscribe(ts2);
        ts2.assertValues(2, 5, 8);
    }

    @Test
    public void keySelectorNull() {
        AssertSubscriber<GroupedFlux<Integer, Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 10).groupBy(( k) -> ((Integer) (null))).subscribe(ts);
        ts.assertError(NullPointerException.class);
    }

    @Test
    public void valueSelectorNull() {
        AssertSubscriber<GroupedFlux<Integer, Integer>> ts = AssertSubscriber.create();
        Flux.range(1, 10).groupBy(( k) -> 1, ( v) -> ((Integer) (null))).subscribe(ts);
        ts.assertError(NullPointerException.class);
    }

    @Test
    public void error() {
        AssertSubscriber<GroupedFlux<Integer, Integer>> ts = AssertSubscriber.create();
        Flux.<Integer>error(new RuntimeException("forced failure")).groupBy(( k) -> k).subscribe(ts);
        ts.assertErrorMessage("forced failure");
    }

    @Test
    public void backpressure() {
        AssertSubscriber<GroupedFlux<Integer, Integer>> ts = AssertSubscriber.create(0L);
        Flux.range(1, 10).groupBy(( k) -> 1).subscribe(ts);
        ts.assertNoEvents();
        ts.request(1);
        ts.assertValueCount(1).assertNoError().assertComplete();
        AssertSubscriber<Integer> ts1 = AssertSubscriber.create(0L);
        ts.values().get(0).subscribe(ts1);
        ts1.assertNoEvents();
        ts1.request(10);
        ts1.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void flatMapBack() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).groupBy(( k) -> k % 2).flatMap(( g) -> g).subscribe(ts);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void flatMapBackHidden() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).groupBy(( k) -> k % 2).flatMap(( g) -> g.hide()).subscribe(ts);
        ts.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void concatMapBack() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).groupBy(( k) -> k % 2).concatMap(( g) -> g).subscribe(ts);
        ts.assertValues(1, 3, 5, 7, 9, 2, 4, 6, 8, 10);
    }

    @Test
    public void concatMapBackHidden() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 10).groupBy(( k) -> k % 2).hide().concatMap(( g) -> g).subscribe(ts);
        ts.assertValues(1, 3, 5, 7, 9, 2, 4, 6, 8, 10);
    }

    @Test
    public void empty() {
        AssertSubscriber<GroupedFlux<Integer, Integer>> ts = AssertSubscriber.create(0L);
        Flux.<Integer>empty().groupBy(( v) -> v).subscribe(ts);
        ts.assertValues();
    }

    @Test
    public void oneGroupLongMerge() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000000).groupBy(( v) -> 1).flatMap(( g) -> g).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void oneGroupLongMergeHidden() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000000).groupBy(( v) -> 1).flatMap(( g) -> g.hide()).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void twoGroupsLongMerge() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000000).groupBy(( v) -> v & 1).flatMap(( g) -> g).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void twoGroupsLongMergeHidden() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000000).groupBy(( v) -> v & 1).flatMap(( g) -> g.hide()).subscribe(ts);
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void twoGroupsLongAsyncMerge() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000000).groupBy(( v) -> v & 1).flatMap(( g) -> g).publishOn(Schedulers.fromExecutorService(forkJoinPool)).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void twoGroupsLongAsyncMergeHidden() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000000).groupBy(( v) -> v & 1).flatMap(( g) -> g.hide()).publishOn(Schedulers.fromExecutorService(forkJoinPool)).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValueCount(1000000).assertNoError().assertComplete();
    }

    @Test
    public void twoGroupsConsumeWithSubscribe() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        AssertSubscriber<Integer> ts1 = AssertSubscriber.create();
        AssertSubscriber<Integer> ts2 = AssertSubscriber.create();
        AssertSubscriber<Integer> ts3 = AssertSubscriber.create();
        ts3.onSubscribe(Operators.emptySubscription());
        Flux.range(0, 1000000).groupBy(( v) -> v & 1).subscribe(new reactor.core.CoreSubscriber<GroupedFlux<Integer, Integer>>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(GroupedFlux<Integer, Integer> t) {
                if ((t.key()) == 0) {
                    t.publishOn(Schedulers.fromExecutorService(forkJoinPool)).subscribe(ts1);
                } else {
                    t.publishOn(Schedulers.fromExecutorService(forkJoinPool)).subscribe(ts2);
                }
            }

            @Override
            public void onError(Throwable t) {
                ts3.onError(t);
            }

            @Override
            public void onComplete() {
                ts3.onComplete();
            }
        });
        ts1.await(Duration.ofSeconds(5));
        ts2.await(Duration.ofSeconds(5));
        ts3.await(Duration.ofSeconds(5));
        ts1.assertValueCount(500000).assertNoError().assertComplete();
        ts2.assertValueCount(500000).assertNoError().assertComplete();
        ts3.assertNoValues().assertNoError().assertComplete();
    }

    @Test
    public void twoGroupsConsumeWithSubscribePrefetchSmaller() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        AssertSubscriber<Integer> ts1 = AssertSubscriber.create();
        AssertSubscriber<Integer> ts2 = AssertSubscriber.create();
        AssertSubscriber<Integer> ts3 = AssertSubscriber.create();
        ts3.onSubscribe(Operators.emptySubscription());
        Flux.range(0, 1000000).groupBy(( v) -> v & 1).subscribe(new reactor.core.CoreSubscriber<GroupedFlux<Integer, Integer>>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(GroupedFlux<Integer, Integer> t) {
                if ((t.key()) == 0) {
                    t.publishOn(Schedulers.fromExecutorService(forkJoinPool), 32).subscribe(ts1);
                } else {
                    t.publishOn(Schedulers.fromExecutorService(forkJoinPool), 32).subscribe(ts2);
                }
            }

            @Override
            public void onError(Throwable t) {
                ts3.onError(t);
            }

            @Override
            public void onComplete() {
                ts3.onComplete();
            }
        });
        if (!(ts1.await(Duration.ofSeconds(5)).isTerminated())) {
            Assert.fail("main subscriber timed out");
        }
        if (!(ts2.await(Duration.ofSeconds(5)).isTerminated())) {
            Assert.fail("group 0 subscriber timed out");
        }
        if (!(ts3.await(Duration.ofSeconds(5)).isTerminated())) {
            Assert.fail("group 1 subscriber timed out");
        }
        ts1.assertValueCount(500000).assertNoError().assertComplete();
        ts2.assertValueCount(500000).assertNoError().assertComplete();
        ts3.assertNoValues().assertNoError().assertComplete();
    }

    @Test
    public void twoGroupsConsumeWithSubscribePrefetchBigger() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        AssertSubscriber<Integer> ts1 = AssertSubscriber.create();
        AssertSubscriber<Integer> ts2 = AssertSubscriber.create();
        AssertSubscriber<Integer> ts3 = AssertSubscriber.create();
        ts3.onSubscribe(Operators.emptySubscription());
        Flux.range(0, 1000000).groupBy(( v) -> v & 1).subscribe(new reactor.core.CoreSubscriber<GroupedFlux<Integer, Integer>>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(GroupedFlux<Integer, Integer> t) {
                if ((t.key()) == 0) {
                    t.publishOn(Schedulers.fromExecutorService(forkJoinPool), 1024).subscribe(ts1);
                } else {
                    t.publishOn(Schedulers.fromExecutorService(forkJoinPool), 1024).subscribe(ts2);
                }
            }

            @Override
            public void onError(Throwable t) {
                ts3.onError(t);
            }

            @Override
            public void onComplete() {
                ts3.onComplete();
            }
        });
        if (!(ts1.await(Duration.ofSeconds(5)).isTerminated())) {
            Assert.fail("main subscriber timed out");
        }
        if (!(ts2.await(Duration.ofSeconds(5)).isTerminated())) {
            Assert.fail("group 0 subscriber timed out");
        }
        if (!(ts3.await(Duration.ofSeconds(5)).isTerminated())) {
            Assert.fail("group 1 subscriber timed out");
        }
        ts1.assertValueCount(500000).assertNoError().assertComplete();
        ts2.assertValueCount(500000).assertNoError().assertComplete();
        ts3.assertNoValues().assertNoError().assertComplete();
    }

    @Test
    public void twoGroupsConsumeWithSubscribeHide() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        AssertSubscriber<Integer> ts1 = AssertSubscriber.create();
        AssertSubscriber<Integer> ts2 = AssertSubscriber.create();
        AssertSubscriber<Integer> ts3 = AssertSubscriber.create();
        ts3.onSubscribe(Operators.emptySubscription());
        Flux.range(0, 1000000).groupBy(( v) -> v & 1).subscribe(new reactor.core.CoreSubscriber<GroupedFlux<Integer, Integer>>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(GroupedFlux<Integer, Integer> t) {
                if ((t.key()) == 0) {
                    t.hide().publishOn(Schedulers.fromExecutorService(forkJoinPool)).subscribe(ts1);
                } else {
                    t.hide().publishOn(Schedulers.fromExecutorService(forkJoinPool)).subscribe(ts2);
                }
            }

            @Override
            public void onError(Throwable t) {
                ts3.onError(t);
            }

            @Override
            public void onComplete() {
                ts3.onComplete();
            }
        });
        ts1.await(Duration.ofSeconds(5));
        ts2.await(Duration.ofSeconds(5));
        ts3.await(Duration.ofSeconds(5));
        ts1.assertValueCount(500000).assertNoError().assertComplete();
        ts2.assertValueCount(500000).assertNoError().assertComplete();
        ts3.assertNoValues().assertNoError().assertComplete();
    }

    @Test
    public void groupsCompleteAsSoonAsMainCompletes() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(0, 20).groupBy(( i) -> i % 5).concatMap(( v) -> v, 2).subscribe(ts);
        ts.assertValues(0, 5, 10, 15, 1, 6, 11, 16, 2, 7, 12, 17, 3, 8, 13, 18, 4, 9, 14, 19).assertComplete().assertNoError();
    }

    @Test
    public void groupsCompleteAsSoonAsMainCompletesNoFusion() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(0, 20).groupBy(( i) -> i % 5).hide().concatMap(( v) -> v, 2).subscribe(ts);
        ts.assertValues(0, 5, 10, 15, 1, 6, 11, 16, 2, 7, 12, 17, 3, 8, 13, 18, 4, 9, 14, 19).assertComplete().assertNoError();
    }

    @Test
    public void prefetchIsUsed() {
        AtomicLong initialRequest = new AtomicLong();
        StepVerifier.create(Flux.range(1, 10).doOnRequest(( r) -> initialRequest.compareAndSet(0L, r)).groupBy(( i) -> i % 5, 11).concatMap(( v) -> v)).expectNextCount(10).verifyComplete();
        assertThat(initialRequest.get()).isEqualTo(11);
    }

    @Test
    public void prefetchMaxRequestsUnbounded() {
        AtomicLong initialRequest = new AtomicLong();
        StepVerifier.create(Flux.range(1, 10).doOnRequest(( r) -> initialRequest.compareAndSet(0L, r)).groupBy(( i) -> i % 5, Integer.MAX_VALUE).concatMap(( v) -> v)).expectNextCount(10).verifyComplete();
        assertThat(initialRequest.get()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void scanMain() {
        reactor.core.CoreSubscriber<GroupedFlux<Integer, String>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        GroupByMain<Integer, Integer, String> test = new FluxGroupBy.GroupByMain<>(actual, Queues.<GroupedFlux<Integer, String>>one().get(), Queues.one(), 123, ( i) -> i % 5, ( i) -> String.valueOf(i));
        Subscription sub = Operators.emptySubscription();
        test.onSubscribe(sub);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        assertThat(test.scan(PARENT)).isSameAs(sub);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(Long.MAX_VALUE);
        assertThat(test.scan(PREFETCH)).isSameAs(123);
        assertThat(test.scan(BUFFERED)).isSameAs(0);
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(ERROR)).isNull();
        test.error = new IllegalStateException("boom");
        assertThat(test.scan(ERROR)).isSameAs(test.error);
    }

    @Test
    public void scanUnicastGroupedFlux() {
        reactor.core.CoreSubscriber<GroupedFlux<Integer, String>> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        GroupByMain<Integer, Integer, String> main = new FluxGroupBy.GroupByMain<>(actual, Queues.<GroupedFlux<Integer, String>>one().get(), Queues.one(), 123, ( i) -> i % 5, ( i) -> String.valueOf(i));
        UnicastGroupedFlux<Integer, String> test = new UnicastGroupedFlux<Integer, String>(1, Queues.<String>one().get(), main, 123);
        reactor.core.CoreSubscriber<String> sub = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        test.subscribe(sub);
        assertThat(test.scan(ACTUAL)).isSameAs(sub);
        assertThat(test.scan(PARENT)).isSameAs(main);
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(Long.MAX_VALUE);
        assertThat(test.scan(BUFFERED)).isSameAs(0);
        assertThat(test.scan(CANCELLED)).isFalse();
        assertThat(test.scan(TERMINATED)).isFalse();
        assertThat(test.scan(ERROR)).isNull();
        test.error = new IllegalStateException("boom");
        assertThat(test.scan(ERROR)).isSameAs(test.error);
    }
}

