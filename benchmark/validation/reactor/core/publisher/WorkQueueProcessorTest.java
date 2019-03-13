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
import Queues.XS_BUFFER_SIZE;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.CAPACITY;
import Scannable.Attr.ERROR;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.logging.Level;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.FluxCreate.SerializedSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.Logger;
import reactor.util.Loggers;
import reactor.util.concurrent.WaitStrategy;


/**
 *
 */
public class WorkQueueProcessorTest {
    static final Logger logger = Loggers.getLogger(WorkQueueProcessorTest.class);

    static final String e = "Element";

    static final String s = "Synchronizer";

    /* see https://github.com/reactor/reactor-core/issues/199 */
    @Test
    public void fixedThreadPoolWorkQueueRejectsSubscribers() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        WorkQueueProcessor<String> bc = WorkQueueProcessor.<String>builder().executor(executorService).bufferSize(16).build();
        CountDownLatch latch = new CountDownLatch(3);
        WorkQueueProcessorTest.TestWorkQueueSubscriber spec1 = new WorkQueueProcessorTest.TestWorkQueueSubscriber(latch, "spec1");
        WorkQueueProcessorTest.TestWorkQueueSubscriber spec2 = new WorkQueueProcessorTest.TestWorkQueueSubscriber(latch, "spec2");
        WorkQueueProcessorTest.TestWorkQueueSubscriber spec3 = new WorkQueueProcessorTest.TestWorkQueueSubscriber(latch, "spec3");
        bc.subscribe(spec1);
        bc.subscribe(spec2);
        bc.subscribe(spec3);
        bc.onNext("foo");
        bc.onComplete();
        Assert.assertThat(spec1.error, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(spec2.error, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(spec3.error, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(spec3.error.getMessage(), CoreMatchers.startsWith("The executor service could not accommodate another subscriber, detected limit 2"));
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            Assert.fail(e1.toString());
        }
    }

    /* see https://github.com/reactor/reactor-core/issues/199 */
    @Test
    public void forkJoinPoolWorkQueueRejectsSubscribers() {
        ExecutorService executorService = Executors.newWorkStealingPool(2);
        WorkQueueProcessor<String> bc = WorkQueueProcessor.<String>builder().executor(executorService).bufferSize(16).build();
        CountDownLatch latch = new CountDownLatch(2);
        WorkQueueProcessorTest.TestWorkQueueSubscriber spec1 = new WorkQueueProcessorTest.TestWorkQueueSubscriber(latch, "spec1");
        WorkQueueProcessorTest.TestWorkQueueSubscriber spec2 = new WorkQueueProcessorTest.TestWorkQueueSubscriber(latch, "spec2");
        WorkQueueProcessorTest.TestWorkQueueSubscriber spec3 = new WorkQueueProcessorTest.TestWorkQueueSubscriber(latch, "spec3");
        bc.subscribe(spec1);
        bc.subscribe(spec2);
        bc.subscribe(spec3);
        bc.onNext("foo");
        bc.onComplete();
        Assert.assertThat(spec1.error, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(spec2.error, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(spec3.error, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(spec3.error.getMessage(), CoreMatchers.is("The executor service could not accommodate another subscriber, detected limit 2"));
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            Assert.fail(e1.toString());
        }
    }

    @Test
    public void highRate() throws Exception {
        WorkQueueProcessor<String> queueProcessor = WorkQueueProcessor.<String>builder().share(true).name("Processor").bufferSize(256).waitStrategy(WaitStrategy.liteBlocking()).build();
        Scheduler timer = Schedulers.newSingle("Timer");
        queueProcessor.bufferTimeout(32, Duration.ofMillis(2), timer).subscribe(new reactor.core.CoreSubscriber<java.util.List<String>>() {
            int counter;

            @Override
            public void onComplete() {
                System.out.println(("Consumed in total: " + (counter)));
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onNext(java.util.List<String> strings) {
                int size = strings.size();
                counter += size;
                if (strings.contains(WorkQueueProcessorTest.s)) {
                    synchronized(WorkQueueProcessorTest.s) {
                        // logger.debug("Synchronizer!");
                        WorkQueueProcessorTest.s.notifyAll();
                    }
                }
            }

            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }
        });
        FluxSink<String> emitter = queueProcessor.sink();
        try {
            WorkQueueProcessorTest.submitInCurrentThread(emitter);
        } finally {
            WorkQueueProcessorTest.logger.debug("Finishing");
            emitter.complete();
            timer.dispose();
        }
        TimeUnit.SECONDS.sleep(1);
    }

    @Test(timeout = 15000L)
    public void cancelDoesNotHang() throws Exception {
        WorkQueueProcessor<String> wq = WorkQueueProcessor.create();
        Disposable d = wq.subscribe();
        Assert.assertTrue(((wq.downstreamCount()) == 1));
        d.dispose();
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 2)) {
        } 
    }

    @Test(timeout = 15000L)
    public void completeDoesNotHang() throws Exception {
        WorkQueueProcessor<String> wq = WorkQueueProcessor.create();
        wq.subscribe();
        Assert.assertTrue(((wq.downstreamCount()) == 1));
        wq.onComplete();
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 2)) {
        } 
    }

    @Test(timeout = 15000L)
    public void disposeSubscribeNoThreadLeak() throws Exception {
        WorkQueueProcessor<String> wq = WorkQueueProcessor.<String>builder().autoCancel(false).build();
        Disposable d = wq.subscribe();
        d.dispose();
        d = wq.subscribe();
        d.dispose();
        d = wq.subscribe();
        d.dispose();
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 2)) {
        } 
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberCold() throws Exception {
        AtomicInteger errors = new AtomicInteger(3);
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        wq.onNext(1);
        wq.onNext(2);
        wq.onNext(3);
        wq.onComplete();
        StepVerifier.create(wq.log("wq", Level.FINE).doOnNext(( e) -> onNextSignals.incrementAndGet()).<Integer>handle(( s1, sink) -> {
            if ((errors.decrementAndGet()) > 0) {
                sink.error(new RuntimeException());
            } else {
                sink.next(s1);
            }
        }).retry()).expectNext(1, 2, 3).verifyComplete();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.equalTo(5));
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberHot() throws Exception {
        AtomicInteger errors = new AtomicInteger(3);
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        StepVerifier.create(wq.doOnNext(( e) -> onNextSignals.incrementAndGet()).<Integer>handle(( s1, sink) -> {
            if ((errors.decrementAndGet()) > 0) {
                sink.error(new RuntimeException("expected"));
            } else {
                sink.next(s1);
            }
        }).retry()).then(() -> {
            wq.onNext(1);
            wq.onNext(2);
            wq.onNext(3);
        }).expectNext(3).thenCancel().verify();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.equalTo(3));
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberHotPoisonSignal() throws Exception {
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        StepVerifier.create(wq.doOnNext(( e) -> onNextSignals.incrementAndGet()).<Integer>handle(( s1, sink) -> {
            if (s1 == 1) {
                sink.error(new RuntimeException());
            } else {
                sink.next(s1);
            }
        }).retry()).then(() -> {
            wq.onNext(1);
            wq.onNext(2);
            wq.onNext(3);
        }).expectNext(2, 3).thenCancel().verify();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.equalTo(3));
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberHotPoisonSignal2() throws Exception {
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        StepVerifier.create(wq.log().doOnNext(( e) -> onNextSignals.incrementAndGet()).<Integer>handle(( s1, sink) -> {
            if (s1 == 2) {
                sink.error(new RuntimeException());
            } else {
                sink.next(s1);
            }
        }).retry()).then(() -> {
            wq.onNext(1);
            wq.onNext(2);
            wq.onNext(3);
        }).expectNext(1, 3).thenCancel().verify();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.equalTo(3));
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void retryNoThreadLeak() throws Exception {
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        wq.handle(( integer, sink) -> sink.error(new RuntimeException())).retry(10).subscribe();
        wq.onNext(1);
        wq.onNext(2);
        wq.onNext(3);
        wq.onComplete();
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void simpleTest() throws Exception {
        final TopicProcessor<Integer> sink = TopicProcessor.<Integer>builder().name("topic").build();
        final WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().name("queue").build();
        int elems = 10000;
        CountDownLatch latch = new CountDownLatch(elems);
        // List<Integer> list = new CopyOnWriteArrayList<>();
        AtomicLong count = new AtomicLong();
        AtomicLong errorCount = new AtomicLong();
        processor.log("wqp.fail1").subscribe(( d) -> {
            errorCount.incrementAndGet();
            throw Exceptions.failWithCancel();
        });
        processor.log("wqp.works").doOnNext(( d) -> count.incrementAndGet()).subscribe(( d) -> {
            latch.countDown();
            // list.add(d);
        });
        sink.subscribe(processor);
        for (int i = 0; i < elems; i++) {
            sink.onNext(i);
            if ((i % 1000) == 0) {
                processor.log("wqp.fail2").subscribe(( d) -> {
                    errorCount.incrementAndGet();
                    throw Exceptions.failWithCancel();
                });
            }
        }
        latch.await(5, TimeUnit.SECONDS);
        System.out.println(((("count " + count) + " errors: ") + errorCount));
        sink.onComplete();
        Assert.assertTrue(("Latch is " + (latch.getCount())), ((latch.getCount()) <= 1));
    }

    /* see https://github.com/reactor/reactor-core/issues/199 */
    @Test
    public void singleThreadWorkQueueDoesntRejectsSubscribers() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        WorkQueueProcessor<String> bc = WorkQueueProcessor.<String>builder().executor(executorService).bufferSize(2).build();
        CountDownLatch latch = new CountDownLatch(1);
        WorkQueueProcessorTest.TestWorkQueueSubscriber spec1 = new WorkQueueProcessorTest.TestWorkQueueSubscriber(latch, "spec1");
        WorkQueueProcessorTest.TestWorkQueueSubscriber spec2 = new WorkQueueProcessorTest.TestWorkQueueSubscriber(latch, "spec2");
        bc.subscribe(spec1);
        bc.subscribe(spec2);
        bc.onNext("foo");
        bc.onNext("bar");
        Executors.newSingleThreadScheduledExecutor().schedule(bc::onComplete, 200, TimeUnit.MILLISECONDS);
        try {
            bc.onNext("baz");
            Assert.fail("expected 3rd next to time out as newSingleThreadExecutor cannot be introspected");
        } catch (Throwable e) {
            Assert.assertTrue(("expected AlertException, got " + e), WaitStrategy.isAlert(e));
        }
    }

    /* see https://github.com/reactor/reactor-core/issues/199 */
    @Test(timeout = 4000)
    public void singleThreadWorkQueueSucceedsWithOneSubscriber() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        WorkQueueProcessor<String> bc = WorkQueueProcessor.<String>builder().executor(executorService).bufferSize(2).build();
        CountDownLatch latch = new CountDownLatch(1);
        WorkQueueProcessorTest.TestWorkQueueSubscriber spec1 = new WorkQueueProcessorTest.TestWorkQueueSubscriber(latch, "spec1");
        bc.subscribe(spec1);
        bc.onNext("foo");
        bc.onNext("bar");
        Executors.newSingleThreadScheduledExecutor().schedule(bc::onComplete, 200, TimeUnit.MILLISECONDS);
        bc.onNext("baz");
        try {
            latch.await(800, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e1) {
            Assert.fail(e1.toString());
        }
        Assert.assertNull(spec1.error);
    }

    @Test
    public void testBestEffortMaxSubscribers() {
        int expectedUnbounded = Integer.MAX_VALUE;
        int expectedUnknown = Integer.MIN_VALUE;
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor();
        ExecutorService executorService3 = Executors.newCachedThreadPool();
        ExecutorService executorService4 = Executors.newFixedThreadPool(2);
        ScheduledExecutorService executorService5 = Executors.newScheduledThreadPool(3);
        ExecutorService executorService6 = Executors.newWorkStealingPool(4);
        ExecutorService executorService7 = Executors.unconfigurableExecutorService(executorService4);
        ExecutorService executorService8 = Executors.unconfigurableScheduledExecutorService(executorService5);
        int maxSub1 = WorkQueueProcessor.bestEffortMaxSubscribers(executorService1);
        int maxSub2 = WorkQueueProcessor.bestEffortMaxSubscribers(executorService2);
        int maxSub3 = WorkQueueProcessor.bestEffortMaxSubscribers(executorService3);
        int maxSub4 = WorkQueueProcessor.bestEffortMaxSubscribers(executorService4);
        int maxSub5 = WorkQueueProcessor.bestEffortMaxSubscribers(executorService5);
        int maxSub6 = WorkQueueProcessor.bestEffortMaxSubscribers(executorService6);
        int maxSub7 = WorkQueueProcessor.bestEffortMaxSubscribers(executorService7);
        int maxSub8 = WorkQueueProcessor.bestEffortMaxSubscribers(executorService8);
        executorService1.shutdown();
        executorService2.shutdown();
        executorService3.shutdown();
        executorService4.shutdown();
        executorService5.shutdown();
        executorService6.shutdown();
        executorService7.shutdown();
        executorService8.shutdown();
        Assert.assertEquals("newSingleThreadExecutor", expectedUnknown, maxSub1);
        Assert.assertEquals("newSingleThreadScheduledExecutor", expectedUnknown, maxSub2);
        Assert.assertEquals("newCachedThreadPool", expectedUnbounded, maxSub3);
        Assert.assertEquals("newFixedThreadPool(2)", 2, maxSub4);
        Assert.assertEquals("newScheduledThreadPool(3)", expectedUnbounded, maxSub5);
        Assert.assertEquals("newWorkStealingPool(4)", 4, maxSub6);
        Assert.assertEquals("unconfigurableExecutorService", expectedUnknown, maxSub7);
        Assert.assertEquals("unconfigurableScheduledExecutorService", expectedUnknown, maxSub8);
    }

    private static class TestWorkQueueSubscriber extends BaseSubscriber<String> {
        private final CountDownLatch latch;

        private final String id;

        Throwable error;

        private TestWorkQueueSubscriber(CountDownLatch latch, String id) {
            this.latch = latch;
            this.id = id;
        }

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            requestUnbounded();
        }

        @Override
        protected void hookOnNext(String value) {
            System.out.println((((id) + " received: ") + value));
        }

        @Override
        protected void hookOnError(Throwable throwable) {
            error = throwable;
        }

        @Override
        protected void hookFinally(SignalType type) {
            System.out.println((((id) + " finished with: ") + type));
            latch.countDown();
        }
    }

    @Test
    public void chainedWorkQueueProcessor() throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(2);
        try {
            WorkQueueProcessor<String> bc = WorkQueueProcessor.<String>builder().executor(es).bufferSize(16).build();
            int elems = 18;
            CountDownLatch latch = new CountDownLatch(elems);
            bc.subscribe(TopicProcessorTest.sub("spec1", latch));
            Flux.range(0, elems).map(( s) -> "hello " + s).subscribe(bc);
            Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        } finally {
            es.shutdown();
        }
    }

    @Test
    public void testWorkQueueProcessorGetters() {
        final int TEST_BUFFER_SIZE = 16;
        WorkQueueProcessor<Object> processor = WorkQueueProcessor.builder().name("testProcessor").bufferSize(TEST_BUFFER_SIZE).build();
        Assert.assertEquals(TEST_BUFFER_SIZE, processor.getAvailableCapacity());
        processor.onNext(new Object());
        Assert.assertEquals((TEST_BUFFER_SIZE - 1), processor.getAvailableCapacity());
        processor.awaitAndShutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void failNonPowerOfTwo() {
        WorkQueueProcessor.builder().name("test").bufferSize(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failNullBufferSize() {
        WorkQueueProcessor.builder().name("test").bufferSize(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failNegativeBufferSize() {
        WorkQueueProcessor.builder().name("test").bufferSize((-1));
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberHotPublishOn() throws Exception {
        AtomicInteger errors = new AtomicInteger(3);
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        StepVerifier.create(wq.log("wq", Level.FINE).publishOn(Schedulers.parallel()).publish().autoConnect().doOnNext(( e) -> onNextSignals.incrementAndGet()).map(( s1) -> {
            if ((errors.decrementAndGet()) > 0) {
                throw new RuntimeException();
            } else {
                return s1;
            }
        }).log("afterMap", Level.FINE).retry()).then(() -> {
            wq.onNext(1);
            wq.onNext(2);
            wq.onNext(3);
        }).expectNext(3).thenCancel().verify();
        // Need to explicitly complete processor due to use of publish()
        wq.onComplete();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.equalTo(3));
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberHotPublishOnPrefetch1() throws Exception {
        AtomicInteger errors = new AtomicInteger(3);
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        StepVerifier.create(wq.publishOn(Schedulers.parallel(), 1).publish().autoConnect().log().doOnNext(( e) -> onNextSignals.incrementAndGet()).map(( s1) -> {
            if ((errors.decrementAndGet()) > 0) {
                throw new RuntimeException();
            } else {
                return s1;
            }
        }).log().retry()).then(() -> {
            wq.onNext(1);
            wq.onNext(2);
            wq.onNext(3);
        }).expectNext(3).thenCancel().verify();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.equalTo(3));
        // Need to explicitly complete processor due to use of publish()
        wq.onComplete();
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberHotPoisonSignalPublishOn() throws Exception {
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        StepVerifier.create(wq.publishOn(Schedulers.parallel()).publish().autoConnect().doOnNext(( e) -> onNextSignals.incrementAndGet()).<Integer>handle(( s1, sink) -> {
            if (s1 == 1) {
                sink.error(new RuntimeException());
            } else {
                sink.next(s1);
            }
        }).retry()).then(() -> {
            wq.onNext(1);
            wq.onNext(2);
            wq.onNext(3);
        }).expectNext(2, 3).thenCancel().verify();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.equalTo(3));
        // Need to explicitly complete processor due to use of publish()
        wq.onComplete();
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberHotPoisonSignalParallel() throws Exception {
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        Function<Flux<Integer>, Flux<Integer>> function = ( flux) -> flux.doOnNext(( e) -> onNextSignals.incrementAndGet()).handle(( s1, sink) -> {
            if (s1 == 1) {
                sink.error(new RuntimeException());
            } else {
                sink.next(s1);
            }
        });
        StepVerifier.create(wq.parallel(4).runOn(Schedulers.newParallel("par", 4)).transform(( flux) -> ParallelFlux.from(flux.groups().flatMap(( s) -> s.publish().autoConnect().transform(function), true, Queues.SMALL_BUFFER_SIZE, Queues.XS_BUFFER_SIZE))).sequential().retry()).then(() -> {
            wq.onNext(1);
            wq.onNext(2);
            wq.onNext(3);
        }).expectNextMatches(( d) -> (d == 2) || (d == 3)).expectNextMatches(( d) -> (d == 2) || (d == 3)).thenCancel().verify();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.either(CoreMatchers.equalTo(2)).or(CoreMatchers.equalTo(3)));
        // Need to explicitly complete processor due to use of publish()
        wq.onComplete();
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberHotPoisonSignalPublishOnPrefetch1() throws Exception {
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        StepVerifier.create(wq.publishOn(Schedulers.parallel(), 1).publish().autoConnect().doOnNext(( e) -> onNextSignals.incrementAndGet()).<Integer>handle(( s1, sink) -> {
            if (s1 == 1) {
                sink.error(new RuntimeException());
            } else {
                sink.next(s1);
            }
        }).retry()).then(() -> {
            wq.onNext(1);
            wq.onNext(2);
            wq.onNext(3);
        }).expectNext(2, 3).thenCancel().verify();
        // Need to explicitly complete processor due to use of publish()
        wq.onComplete();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.equalTo(3));
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    @Test
    public void retryErrorPropagatedFromWorkQueueSubscriberHotPoisonSignalFlatMap() throws Exception {
        WorkQueueProcessor<Integer> wq = WorkQueueProcessor.<Integer>builder().autoCancel(false).build();
        AtomicInteger onNextSignals = new AtomicInteger();
        StepVerifier.create(wq.publish().autoConnect().flatMap(( i) -> Mono.just(i).doOnNext(( e) -> onNextSignals.incrementAndGet()).<Integer>handle(( s1, sink) -> {
            if (s1 == 1) {
                sink.error(new RuntimeException());
            } else {
                sink.next(s1);
            }
        }).subscribeOn(Schedulers.parallel()), true, XS_BUFFER_SIZE, SMALL_BUFFER_SIZE).retry()).then(() -> {
            wq.onNext(1);
            wq.onNext(2);
            wq.onNext(3);
        }).expectNextMatches(( d) -> (d == 2) || (d == 3)).expectNextMatches(( d) -> (d == 2) || (d == 3)).thenAwait(Duration.ofMillis(10)).thenCancel().verify();
        wq.onComplete();
        Assert.assertThat(onNextSignals.get(), CoreMatchers.equalTo(3));
        while (((wq.downstreamCount()) != 0) && ((Thread.activeCount()) > 1)) {
        } 
    }

    // see https://github.com/reactor/reactor-core/issues/445
    @Test(timeout = 5000)
    public void testBufferSize1Shared() throws Exception {
        WorkQueueProcessor<String> broadcast = WorkQueueProcessor.<String>builder().share(true).name("share-name").bufferSize(1).autoCancel(true).build();
        int simultaneousSubscribers = 3000;
        CountDownLatch latch = new CountDownLatch(simultaneousSubscribers);
        Scheduler scheduler = Schedulers.single();
        FluxSink<String> sink = broadcast.sink();
        Flux<String> flux = broadcast.filter(Objects::nonNull).publishOn(scheduler).cache(1);
        for (int i = 0; i < simultaneousSubscribers; i++) {
            flux.subscribe(( s) -> latch.countDown());
        }
        sink.next("data");
        Assertions.assertThat(latch.await(4, TimeUnit.SECONDS)).overridingErrorMessage("Data not received").isTrue();
    }

    // see https://github.com/reactor/reactor-core/issues/445
    @Test(timeout = 5000)
    public void testBufferSize1Created() throws Exception {
        WorkQueueProcessor<String> broadcast = WorkQueueProcessor.<String>builder().share(true).name("share-name").bufferSize(1).autoCancel(true).build();
        int simultaneousSubscribers = 3000;
        CountDownLatch latch = new CountDownLatch(simultaneousSubscribers);
        Scheduler scheduler = Schedulers.single();
        FluxSink<String> sink = broadcast.sink();
        Flux<String> flux = broadcast.filter(Objects::nonNull).publishOn(scheduler).cache(1);
        for (int i = 0; i < simultaneousSubscribers; i++) {
            flux.subscribe(( s) -> latch.countDown());
        }
        sink.next("data");
        Assertions.assertThat(latch.await(4, TimeUnit.SECONDS)).overridingErrorMessage("Data not received").isTrue();
    }

    @Test
    public void testDefaultRequestTaskThreadName() {
        String mainName = "workQueueProcessorRequestTask";
        String expectedName = mainName + "[request-task]";
        WorkQueueProcessor<Object> processor = WorkQueueProcessor.builder().name(mainName).bufferSize(8).build();
        processor.requestTask(Operators.cancelledSubscription());
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        // cleanup to avoid visibility in other tests
        processor.forceShutdown();
        Condition<Thread> defaultRequestTaskThread = new Condition(( thread) -> (thread != null) && (expectedName.equals(thread.getName())), "a thread named \"%s\"", expectedName);
        Assertions.assertThat(threads).haveExactly(1, defaultRequestTaskThread);
    }

    @Test
    public void testCustomRequestTaskThreadNameCreate() {
        String expectedName = "workQueueProcessorRequestTaskCreate";
        // NOTE: the below single executor should not be used usually as requestTask assumes it immediately gets executed
        ExecutorService customTaskExecutor = Executors.newSingleThreadExecutor(( r) -> new Thread(r, expectedName));
        WorkQueueProcessor<Object> processor = WorkQueueProcessor.builder().executor(Executors.newCachedThreadPool()).requestTaskExecutor(customTaskExecutor).bufferSize(8).waitStrategy(WaitStrategy.liteBlocking()).autoCancel(true).build();
        processor.requestTask(Operators.cancelledSubscription());
        processor.subscribe();
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        // cleanup to avoid visibility in other tests
        customTaskExecutor.shutdownNow();
        processor.forceShutdown();
        Condition<Thread> customRequestTaskThread = new Condition(( thread) -> (thread != null) && (expectedName.equals(thread.getName())), "a thread named \"%s\"", expectedName);
        Assertions.assertThat(threads).haveExactly(1, customRequestTaskThread);
    }

    @Test
    public void testCustomRequestTaskThreadNameShare() {
        String expectedName = "workQueueProcessorRequestTaskShare";
        // NOTE: the below single executor should not be used usually as requestTask assumes it immediately gets executed
        ExecutorService customTaskExecutor = Executors.newSingleThreadExecutor(( r) -> new Thread(r, expectedName));
        WorkQueueProcessor<Object> processor = WorkQueueProcessor.builder().executor(Executors.newCachedThreadPool()).requestTaskExecutor(customTaskExecutor).bufferSize(8).waitStrategy(WaitStrategy.liteBlocking()).autoCancel(true).build();
        processor.requestTask(Operators.cancelledSubscription());
        processor.subscribe();
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        // cleanup to avoid visibility in other tests
        customTaskExecutor.shutdownNow();
        processor.forceShutdown();
        Condition<Thread> customRequestTaskThread = new Condition(( thread) -> (thread != null) && (expectedName.equals(thread.getName())), "a thread named \"%s\"", expectedName);
        Assertions.assertThat(threads).haveExactly(1, customRequestTaskThread);
    }

    @Test
    public void customRequestTaskThreadRejectsNull() {
        ExecutorService customTaskExecutor = null;
        Assertions.assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new WorkQueueProcessor<>(Thread::new, Executors.newCachedThreadPool(), customTaskExecutor, 8, WaitStrategy.liteBlocking(), true, true));
    }

    @Test
    public void createDefault() {
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.create();
        assertProcessor(processor, false, null, null, null, null, null, null);
    }

    @Test
    public void createOverrideAutoCancel() {
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, null, null, autoCancel, null, null);
    }

    @Test
    public void createOverrideName() {
        String name = "nameOverride";
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().name(name).build();
        assertProcessor(processor, false, name, null, null, null, null, null);
    }

    @Test
    public void createOverrideNameBufferSize() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.create(name, bufferSize);
        assertProcessor(processor, false, name, bufferSize, null, null, null, null);
    }

    @Test
    public void createOverrideNameBufferSizeAutoCancel() {
        String name = "nameOverride";
        int bufferSize = 1024;
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().name(name).bufferSize(bufferSize).autoCancel(autoCancel).build();
        assertProcessor(processor, false, name, bufferSize, null, autoCancel, null, null);
    }

    @Test
    public void createOverrideNameBufferSizeWaitStrategy() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().name(name).bufferSize(bufferSize).waitStrategy(waitStrategy).build();
        assertProcessor(processor, false, name, bufferSize, waitStrategy, null, null, null);
    }

    @Test
    public void createDefaultExecutorOverrideAll() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().name(name).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, false, name, bufferSize, waitStrategy, autoCancel, null, null);
    }

    @Test
    public void createOverrideExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().executor(executor).build();
        assertProcessor(processor, false, null, null, null, null, executor, null);
    }

    @Test
    public void createOverrideExecutorAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().executor(executor).autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, null, null, autoCancel, executor, null);
    }

    @Test
    public void createOverrideExecutorBufferSize() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().executor(executor).bufferSize(bufferSize).build();
        assertProcessor(processor, false, null, bufferSize, null, null, executor, null);
    }

    @Test
    public void createOverrideExecutorBufferSizeAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().executor(executor).bufferSize(bufferSize).autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, bufferSize, null, autoCancel, executor, null);
    }

    @Test
    public void createOverrideExecutorBufferSizeWaitStrategy() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().executor(executor).bufferSize(bufferSize).waitStrategy(waitStrategy).build();
        assertProcessor(processor, false, null, bufferSize, waitStrategy, null, executor, null);
    }

    @Test
    public void createOverrideExecutorBufferSizeWaitStrategyAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().executor(executor).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, bufferSize, waitStrategy, autoCancel, executor, null);
    }

    @Test
    public void createOverrideAll() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService requestTaskExecutor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().executor(executor).requestTaskExecutor(requestTaskExecutor).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, bufferSize, waitStrategy, autoCancel, executor, requestTaskExecutor);
    }

    @Test
    public void shareOverrideAutoCancel() {
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, null, null, autoCancel, null, null);
    }

    @Test
    public void shareOverrideNameBufferSize() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.share(name, bufferSize);
        assertProcessor(processor, true, name, bufferSize, null, null, null, null);
    }

    @Test
    public void shareOverrideNameBufferSizeWaitStrategy() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).name(name).bufferSize(bufferSize).waitStrategy(waitStrategy).build();
        assertProcessor(processor, true, name, bufferSize, waitStrategy, null, null, null);
    }

    @Test
    public void shareDefaultExecutorOverrideAll() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).name(name).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, true, name, bufferSize, waitStrategy, autoCancel, null, null);
    }

    @Test
    public void shareOverrideExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).executor(executor).build();
        assertProcessor(processor, true, null, null, null, null, executor, null);
    }

    @Test
    public void shareOverrideExecutorAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).executor(executor).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, null, null, autoCancel, executor, null);
    }

    @Test
    public void shareOverrideExecutorBufferSize() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).executor(executor).bufferSize(bufferSize).build();
        assertProcessor(processor, true, null, bufferSize, null, null, executor, null);
    }

    @Test
    public void shareOverrideExecutorBufferSizeAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).executor(executor).bufferSize(bufferSize).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, bufferSize, null, autoCancel, executor, null);
    }

    @Test
    public void shareOverrideExecutorBufferSizeWaitStrategy() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).executor(executor).bufferSize(bufferSize).waitStrategy(waitStrategy).build();
        assertProcessor(processor, true, null, bufferSize, waitStrategy, null, executor, null);
    }

    @Test
    public void shareOverrideExecutorBufferSizeWaitStrategyAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).executor(executor).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, bufferSize, waitStrategy, autoCancel, executor, null);
    }

    @Test
    public void shareOverrideAll() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService requestTaskExecutor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        WorkQueueProcessor<Integer> processor = WorkQueueProcessor.<Integer>builder().share(true).executor(executor).requestTaskExecutor(requestTaskExecutor).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, bufferSize, waitStrategy, autoCancel, executor, requestTaskExecutor);
    }

    @Test
    public void scanProcessor() {
        WorkQueueProcessor<String> test = WorkQueueProcessor.create("name", 16);
        Subscription subscription = Operators.emptySubscription();
        test.onSubscribe(subscription);
        Assertions.assertThat(test.scan(PARENT)).isEqualTo(subscription);
        Assertions.assertThat(test.scan(CAPACITY)).isEqualTo(16);
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        Assertions.assertThat(test.scan(ERROR)).isNull();
        test.onError(new IllegalStateException("boom"));
        Assertions.assertThat(test.scan(ERROR)).hasMessage("boom");
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanInner() {
        WorkQueueProcessor<String> main = WorkQueueProcessor.create("name", 16);
        reactor.core.CoreSubscriber<String> subscriber = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        WorkQueueProcessor.WorkQueueInner<String> test = new WorkQueueProcessor.WorkQueueInner<>(subscriber, main);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(main);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(subscriber);
        Assertions.assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        Assertions.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(0L);
        test.pendingRequest.set(123);
        Assertions.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(123L);
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        main.terminated = 1;
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void serializedSinkSingleProducer() throws Exception {
        WorkQueueProcessor<Integer> queueProcessor = WorkQueueProcessor.<Integer>builder().share(false).build();
        FluxSink<Integer> sink = queueProcessor.sink();
        Assertions.assertThat(sink).isInstanceOf(SerializedSink.class);
        sink = sink.next(1);
        Assertions.assertThat(sink).isInstanceOf(SerializedSink.class);
        sink = sink.onRequest(( n) -> {
        });
        Assertions.assertThat(sink).isInstanceOf(SerializedSink.class);
    }

    @Test
    public void nonSerializedSinkMultiProducer() throws Exception {
        int count = 1000;
        WorkQueueProcessor<Integer> queueProcessor = WorkQueueProcessor.<Integer>builder().share(true).build();
        WorkQueueProcessorTest.TestSubscriber subscriber = new WorkQueueProcessorTest.TestSubscriber(count);
        queueProcessor.subscribe(subscriber);
        FluxSink<Integer> sink = queueProcessor.sink();
        Assertions.assertThat(sink).isNotInstanceOf(SerializedSink.class);
        for (int i = 0; i < count; i++) {
            sink = sink.next(i);
            Assertions.assertThat(sink).isNotInstanceOf(SerializedSink.class);
        }
        subscriber.await(Duration.ofSeconds(5));
        Assert.assertNull("Unexpected exception in subscriber", subscriber.failure);
    }

    @Test
    public void serializedSinkMultiProducerWithOnRequest() throws Exception {
        int count = 1000;
        WorkQueueProcessor<Integer> queueProcessor = WorkQueueProcessor.<Integer>builder().share(true).build();
        WorkQueueProcessorTest.TestSubscriber subscriber = new WorkQueueProcessorTest.TestSubscriber(count);
        queueProcessor.subscribe(subscriber);
        FluxSink<Integer> sink = queueProcessor.sink();
        AtomicInteger next = new AtomicInteger();
        FluxSink<Integer> serializedSink = sink.onRequest(( n) -> {
            for (int i = 0; i < n; i++) {
                synchronized(s) {
                    // to ensure that elements are in order for testing
                    FluxSink<Integer> retSink = sink.next(next.getAndIncrement());
                    Assertions.assertThat(retSink).isInstanceOf(.class);
                }
            }
        });
        Assertions.assertThat(serializedSink).isInstanceOf(SerializedSink.class);
        subscriber.await(Duration.ofSeconds(5));
        sink.complete();
        Assert.assertNull("Unexpected exception in subscriber", subscriber.failure);
    }

    static class TestSubscriber implements reactor.core.CoreSubscriber<Integer> {
        final CountDownLatch latch;

        final AtomicInteger next;

        Throwable failure;

        TestSubscriber(int count) {
            latch = new CountDownLatch(count);
            next = new AtomicInteger();
        }

        public void await(Duration duration) throws InterruptedException {
            Assert.assertTrue(("Did not receive all, remaining=" + (latch.getCount())), latch.await(duration.toMillis(), TimeUnit.MILLISECONDS));
        }

        @Override
        public void onComplete() {
            try {
                Assert.assertEquals(0, latch.getCount());
            } catch (Throwable t) {
                failure = t;
            }
        }

        @Override
        public void onError(Throwable t) {
            failure = t;
        }

        @Override
        public void onNext(Integer n) {
            latch.countDown();
            try {
                Assert.assertEquals(next.getAndIncrement(), n.intValue());
            } catch (Throwable t) {
                failure = t;
            }
        }

        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }
    }

    @Test
    public void testForceShutdownAfterShutdown() throws InterruptedException {
        WorkQueueProcessor<String> processor = // eliminate the waitstrategy diff
        WorkQueueProcessor.<String>builder().name("processor").bufferSize(4).waitStrategy(WaitStrategy.phasedOffLiteLock(200, 100, TimeUnit.MILLISECONDS)).build();
        Publisher<String> publisher = Flux.fromArray(new String[]{ "1", "2", "3", "4", "5" });
        publisher.subscribe(processor);
        AssertSubscriber<String> subscriber = AssertSubscriber.create(0);
        processor.subscribe(subscriber);
        subscriber.request(1);
        Thread.sleep(250);
        processor.shutdown();
        Assert.assertFalse(processor.awaitAndShutdown(Duration.ofMillis(400)));
        processor.forceShutdown();
        Assert.assertTrue(processor.awaitAndShutdown(Duration.ofMillis(400)));
    }
}

