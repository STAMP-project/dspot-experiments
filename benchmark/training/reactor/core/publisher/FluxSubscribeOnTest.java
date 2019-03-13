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


import Queues.SMALL_BUFFER_SIZE;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;


public class FluxSubscribeOnTest {
    /* @Test
    public void constructors() {
    ConstructorTestBuilder ctb = new ConstructorTestBuilder(FluxPublishOn.class);

    ctb.addRef("source", Flux.never());
    ctb.addRef("executor", Schedulers.single());
    ctb.addRef("schedulerFactory", (Callable<? extends Consumer<Runnable>>)() -> r -> { });

    ctb.test();
    }
     */
    @Test
    public void classic() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 1000).subscribeOn(Schedulers.fromExecutorService(ForkJoinPool.commonPool())).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValueCount(1000).assertNoError().assertComplete();
    }

    @Test
    public void classicBackpressured() throws Exception {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux.range(1, 1000).log().subscribeOn(Schedulers.fromExecutorService(ForkJoinPool.commonPool())).subscribe(ts);
        Thread.sleep(100);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(500);
        Thread.sleep(1000);
        ts.assertValueCount(500).assertNoError().assertNotComplete();
        ts.request(500);
        ts.await(Duration.ofSeconds(5));
        ts.assertValueCount(1000).assertNoError().assertComplete();
    }

    @Test
    public void classicJust() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.just(1).subscribeOn(Schedulers.fromExecutorService(ForkJoinPool.commonPool())).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertValues(1).assertNoError().assertComplete();
    }

    @Test
    public void classicJustBackpressured() throws Exception {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux.just(1).subscribeOn(Schedulers.fromExecutorService(ForkJoinPool.commonPool())).subscribe(ts);
        Thread.sleep(100);
        ts.assertNoValues().assertNoError().assertNotComplete();
        ts.request(500);
        ts.await(Duration.ofSeconds(5));
        ts.assertValues(1).assertNoError().assertComplete();
    }

    @Test
    public void classicEmpty() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.<Integer>empty().subscribeOn(Schedulers.fromExecutorService(ForkJoinPool.commonPool())).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertNoValues().assertNoError().assertComplete();
    }

    @Test
    public void classicEmptyBackpressured() throws Exception {
        AssertSubscriber<Integer> ts = AssertSubscriber.create(0);
        Flux.<Integer>empty().subscribeOn(Schedulers.fromExecutorService(ForkJoinPool.commonPool())).subscribe(ts);
        ts.await(Duration.ofSeconds(5));
        ts.assertNoValues().assertNoError().assertComplete();
    }

    @Test
    public void callableEvaluatedTheRightTime() {
        AtomicInteger count = new AtomicInteger();
        Mono<Integer> p = Mono.fromCallable(count::incrementAndGet).subscribeOn(Schedulers.fromExecutorService(ForkJoinPool.commonPool()));
        Assert.assertEquals(0, count.get());
        p.subscribeWith(AssertSubscriber.create()).await();
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void scanMainSubscriber() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxSubscribeOn.SubscribeOnSubscriber<Integer> test = new FluxSubscribeOn.SubscribeOnSubscriber<>(Flux.just(1), actual, Schedulers.single().createWorker(), true);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        assertThat(test.scan(PARENT)).isSameAs(parent);
        assertThat(test.scan(ACTUAL)).isSameAs(actual);
        test.requested = 35;
        assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(35L);
        assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scheduleRequestsByDefault() {
        Flux<Integer> test = // note there's no explicit parameter
        // note the create is away from subscribeOn
        Flux.<Integer>create(( sink) -> {
            for (int i = 1; i < 1001; i++) {
                sink.next(i);
                try {
                    Thread.sleep(1);
                } catch ( e) {
                    e.printStackTrace();
                }
            }
            sink.complete();
        }, OverflowStrategy.DROP).map(Flux.identityFunction()).subscribeOn(Schedulers.newSingle("test")).publishOn(Schedulers.elastic());
        StepVerifier.create(test).expectNextCount(SMALL_BUFFER_SIZE).expectComplete().verify(Duration.ofSeconds(5));
    }

    @Test
    public void forceNoScheduledRequests() {
        Flux<Integer> test = Flux.<Integer>create(( sink) -> {
            for (int i = 1; i < 1001; i++) {
                sink.next(i);
                try {
                    Thread.sleep(1);
                } catch ( e) {
                    e.printStackTrace();
                }
            }
            sink.complete();
        }, OverflowStrategy.DROP).map(Function.identity()).subscribeOn(Schedulers.single(), false).publishOn(Schedulers.elastic());
        AtomicInteger count = new AtomicInteger();
        StepVerifier.create(test).thenConsumeWhile(( t) -> (count.incrementAndGet()) != (-1)).expectComplete().verify(Duration.ofSeconds(5));
        assertThat(count.get()).isGreaterThan(SMALL_BUFFER_SIZE);
    }

    @Test
    public void forceScheduledRequests() {
        Flux<Integer> test = Flux.<Integer>create(( sink) -> {
            for (int i = 1; i < 1001; i++) {
                sink.next(i);
                try {
                    Thread.sleep(1);
                } catch ( e) {
                    e.printStackTrace();
                }
            }
            sink.complete();
        }, OverflowStrategy.DROP).map(Function.identity()).subscribeOn(Schedulers.single(), true).publishOn(Schedulers.elastic());
        AtomicInteger count = new AtomicInteger();
        StepVerifier.create(test).thenConsumeWhile(( t) -> (count.incrementAndGet()) != (-1)).expectComplete().verify(Duration.ofSeconds(5));
        assertThat(count.get()).isEqualTo(SMALL_BUFFER_SIZE);
    }

    @Test
    public void gh507() {
        Scheduler s = Schedulers.newSingle("subscribe");
        Scheduler s2 = Schedulers.newParallel("receive");
        AtomicBoolean interrupted = new AtomicBoolean();
        AtomicBoolean timedOut = new AtomicBoolean();
        try {
            Flux.from(((Publisher<String>) (( subscriber) -> {
                subscriber.onSubscribe(new Subscription() {
                    private int totalCount;

                    @Override
                    public void request(long n) {
                        for (int i = 0; i < n; i++) {
                            if (((totalCount)++) < 317) {
                                subscriber.onNext(String.valueOf(totalCount));
                            } else {
                                subscriber.onComplete();
                            }
                        }
                    }

                    @Override
                    public void cancel() {
                        // do nothing
                    }
                });
            }))).subscribeOn(s).limitRate(10).doOnNext(( d) -> {
                CountDownLatch latch = new CountDownLatch(1);
                Mono.fromCallable(() -> d).subscribeOn(s2).doFinally(( it) -> latch.countDown()).subscribe();
                try {
                    if (!(latch.await(5, TimeUnit.SECONDS))) {
                        timedOut.set(true);
                    }
                } catch ( e) {
                    interrupted.set(true);
                }
            }).blockLast(Duration.ofSeconds(2));
            assertThat(interrupted).as("interrupted").isFalse();
            assertThat(timedOut).as("latch timeout").isFalse();
        } finally {
            s.dispose();
            s2.dispose();
        }
    }
}

