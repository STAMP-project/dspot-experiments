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
import Scannable.Attr.BUFFERED;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.CAPACITY;
import Scannable.Attr.ERROR;
import Scannable.Attr.LARGE_BUFFERED;
import Scannable.Attr.PARENT;
import Scannable.Attr.PREFETCH;
import Scannable.Attr.REQUESTED_FROM_DOWNSTREAM;
import Scannable.Attr.TERMINATED;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.FluxCreate.SerializedSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.WaitStrategy;


/**
 *
 *
 * @author Stephane Maldini
 */
public class TopicProcessorTest {
    @Test
    public void createSmokeTest() {
        // this build sequence has been reported as throwing an exception
        // with JDK9 (see https://github.com/reactor/reactor-core/issues/881)
        TopicProcessor.builder().share(true).build();
    }

    @Test
    public void testShutdownSuccessfulAfterAllDataIsRequested() throws InterruptedException {
        TopicProcessor<String> processor = TopicProcessor.<String>builder().name("processor").bufferSize(4).build();
        Publisher<String> publisher = Flux.fromArray(new String[]{ "1", "2", "3", "4", "5" });
        publisher.subscribe(processor);
        AssertSubscriber<String> subscriber = AssertSubscriber.create(0);
        processor.subscribe(subscriber);
        subscriber.request(1);
        Thread.sleep(250);
        processor.shutdown();
        Assert.assertFalse(processor.awaitAndShutdown(Duration.ofMillis(250)));
        subscriber.request(4);
        Assert.assertTrue(processor.awaitAndShutdown(Duration.ofMillis(250)));
    }

    @Test
    public void testForceShutdownWhileWaitingForRequest() throws InterruptedException {
        TopicProcessor<String> processor = TopicProcessor.<String>builder().name("processor").bufferSize(4).build();
        Publisher<String> publisher = Flux.fromArray(new String[]{ "1", "2", "3", "4", "5" });
        publisher.subscribe(processor);
        AssertSubscriber<String> subscriber = AssertSubscriber.create(0);
        processor.subscribe(subscriber);
        subscriber.request(1);
        Thread.sleep(250);
        processor.forceShutdown();
        Assert.assertTrue(processor.awaitAndShutdown(Duration.ofSeconds(1)));
    }

    @Test
    public void testForceShutdownWhileWaitingForInitialRequest() throws InterruptedException {
        TopicProcessor<String> processor = TopicProcessor.<String>builder().name("processor").bufferSize(4).build();
        Publisher<String> publisher = new TopicProcessorTest.CappedPublisher(2);
        publisher.subscribe(processor);
        AssertSubscriber<String> subscriber = AssertSubscriber.create(0);
        processor.subscribe(subscriber);
        processor.forceShutdown();
        Assert.assertTrue(processor.awaitAndShutdown(Duration.ofSeconds(5)));
    }

    /**
     * Publishes {@link #nItems} data items in total after that any subscription.request is no-op.
     */
    static class CappedPublisher implements Publisher<String> {
        private Subscriber<? super String> subscriber;

        private final int nItems;

        public CappedPublisher(int nItems) {
            this.nItems = nItems;
        }

        @Override
        public void subscribe(Subscriber<? super String> s) {
            subscriber = s;
            s.onSubscribe(new Subscription() {
                private int requested;

                @Override
                public void request(long n) {
                    long limit = Math.min(n, ((nItems) - (requested)));
                    for (int i = 0; i < limit; i++) {
                        subscriber.onNext(("" + i));
                    }
                    requested += limit;
                }

                @Override
                public void cancel() {
                }
            });
        }
    }

    @Test
    public void testForceShutdownWhileWaitingForMoreData() throws InterruptedException {
        TopicProcessor<String> processor = TopicProcessor.<String>builder().name("processor").bufferSize(4).build();
        Publisher<String> publisher = new TopicProcessorTest.CappedPublisher(2);
        publisher.subscribe(processor);
        AssertSubscriber<String> subscriber = AssertSubscriber.create(0);
        processor.subscribe(subscriber);
        subscriber.request(3);
        Thread.sleep(250);
        processor.forceShutdown();
        Assert.assertTrue(processor.awaitAndShutdown(Duration.ofSeconds(5)));
    }

    @Test
    public void testForceShutdownAfterShutdown() throws InterruptedException {
        TopicProcessor<String> processor = TopicProcessor.<String>builder().name("processor").bufferSize(4).build();
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

    @Test
    public void testShutdown() {
        for (int i = 0; i < 1000; i++) {
            TopicProcessor<?> dispatcher = TopicProcessor.<String>builder().name("rb-test-dispose").bufferSize(16).build();
            dispatcher.awaitAndShutdown();
        }
    }

    @Test
    public void drainTest() throws Exception {
        final TopicProcessor<Integer> sink = TopicProcessor.<Integer>builder().name("topic").build();
        sink.onNext(1);
        sink.onNext(2);
        sink.onNext(3);
        sink.forceShutdown().subscribeWith(AssertSubscriber.create()).assertComplete().assertValues(1, 2, 3);
    }

    @Test
    public void chainedTopicProcessor() throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(2);
        try {
            TopicProcessor<String> bc = TopicProcessor.<String>builder().executor(es).bufferSize(16).build();
            int elems = 100;
            CountDownLatch latch = new CountDownLatch(elems);
            bc.subscribe(TopicProcessorTest.sub("spec1", latch));
            Flux.range(0, elems).map(( s) -> "hello " + s).subscribe(bc);
            Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        } finally {
            es.shutdown();
        }
    }

    @Test
    public void testTopicProcessorGetters() {
        final int TEST_BUFFER_SIZE = 16;
        TopicProcessor<Object> processor = TopicProcessor.builder().name("testProcessor").bufferSize(TEST_BUFFER_SIZE).build();
        Assert.assertEquals(TEST_BUFFER_SIZE, processor.getAvailableCapacity());
        processor.awaitAndShutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void failNullBufferSize() {
        TopicProcessor.builder().name("test").bufferSize(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failNonPowerOfTwo() {
        TopicProcessor.builder().name("test").bufferSize(3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failNegativeBufferSize() {
        TopicProcessor.builder().name("test").bufferSize((-1));
    }

    // see https://github.com/reactor/reactor-core/issues/445
    @Test(timeout = 5000)
    public void testBufferSize1Shared() throws Exception {
        TopicProcessor<String> broadcast = TopicProcessor.<String>builder().name("share-name").bufferSize(1).autoCancel(true).share(true).build();
        int simultaneousSubscribers = 3000;
        CountDownLatch latch = new CountDownLatch(simultaneousSubscribers);
        Scheduler scheduler = Schedulers.single();
        FluxSink<String> sink = broadcast.sink();
        Flux<String> flux = broadcast.filter(Objects::nonNull).publishOn(scheduler).cache(1);
        for (int i = 0; i < simultaneousSubscribers; i++) {
            flux.subscribe(( s) -> latch.countDown());
        }
        sink.next("data");
        Assert.assertThat(latch.await(4, TimeUnit.SECONDS)).overridingErrorMessage("Data not received").isTrue();
    }

    // see https://github.com/reactor/reactor-core/issues/445
    @Test(timeout = 5000)
    public void testBufferSize1Created() throws Exception {
        TopicProcessor<String> broadcast = TopicProcessor.<String>builder().name("share-name").bufferSize(1).autoCancel(true).build();
        int simultaneousSubscribers = 3000;
        CountDownLatch latch = new CountDownLatch(simultaneousSubscribers);
        Scheduler scheduler = Schedulers.single();
        FluxSink<String> sink = broadcast.sink();
        Flux<String> flux = broadcast.filter(Objects::nonNull).publishOn(scheduler).cache(1);
        for (int i = 0; i < simultaneousSubscribers; i++) {
            flux.subscribe(( s) -> latch.countDown());
        }
        sink.next("data");
        Assert.assertThat(latch.await(4, TimeUnit.SECONDS)).overridingErrorMessage("Data not received").isTrue();
    }

    @Test
    public void testDefaultRequestTaskThreadName() {
        String mainName = "topicProcessorRequestTask";
        String expectedName = mainName + "[request-task]";
        TopicProcessor<Object> processor = TopicProcessor.builder().name(mainName).bufferSize(8).build();
        processor.requestTask(Operators.cancelledSubscription());
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        // cleanup to avoid visibility in other tests
        processor.forceShutdown();
        Condition<Thread> defaultRequestTaskThread = new Condition(( thread) -> (thread != null) && (expectedName.equals(thread.getName())), "a thread named \"%s\"", expectedName);
        Assertions.assertThat(threads).haveExactly(1, defaultRequestTaskThread);
    }

    @Test
    public void testCustomRequestTaskThreadName() {
        String expectedName = "topicProcessorRequestTaskCreate";
        // NOTE: the below single executor should not be used usually as requestTask assumes it immediately gets executed
        ExecutorService customTaskExecutor = Executors.newSingleThreadExecutor(( r) -> new Thread(r, expectedName));
        TopicProcessor<Object> processor = TopicProcessor.builder().executor(Executors.newCachedThreadPool()).requestTaskExecutor(customTaskExecutor).bufferSize(8).waitStrategy(WaitStrategy.liteBlocking()).autoCancel(true).build();
        processor.requestTask(Operators.cancelledSubscription());
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        // cleanup to avoid visibility in other tests
        customTaskExecutor.shutdownNow();
        processor.forceShutdown();
        Condition<Thread> customRequestTaskThread = new Condition(( thread) -> (thread != null) && (expectedName.equals(thread.getName())), "a thread named \"%s\"", expectedName);
        Assertions.assertThat(threads).haveExactly(1, customRequestTaskThread);
    }

    @Test
    public void testCustomRequestTaskThreadShare() {
        String expectedName = "topicProcessorRequestTaskShare";
        // NOTE: the below single executor should not be used usually as requestTask assumes it immediately gets executed
        ExecutorService customTaskExecutor = Executors.newSingleThreadExecutor(( r) -> new Thread(r, expectedName));
        TopicProcessor<Object> processor = TopicProcessor.builder().share(true).executor(Executors.newCachedThreadPool()).requestTaskExecutor(customTaskExecutor).bufferSize(8).waitStrategy(WaitStrategy.liteBlocking()).autoCancel(true).build();
        processor.requestTask(Operators.cancelledSubscription());
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
        Assertions.assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> new TopicProcessor<>(Thread::new, Executors.newCachedThreadPool(), customTaskExecutor, 8, WaitStrategy.liteBlocking(), true, true, Object::new));
    }

    @Test
    public void createDefault() {
        TopicProcessor<Integer> processor = TopicProcessor.create();
        assertProcessor(processor, false, null, null, null, null, null, null);
    }

    @Test
    public void createOverrideAutoCancel() {
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, null, null, autoCancel, null, null);
    }

    @Test
    public void createOverrideName() {
        String name = "nameOverride";
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().name(name).build();
        assertProcessor(processor, false, name, null, null, null, null, null);
    }

    @Test
    public void createOverrideNameBufferSize() {
        String name = "nameOverride";
        int bufferSize = 1024;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().name(name).bufferSize(bufferSize).build();
        assertProcessor(processor, false, name, bufferSize, null, null, null, null);
    }

    @Test
    public void createOverrideNameBufferSizeAutoCancel() {
        String name = "nameOverride";
        int bufferSize = 1024;
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().name(name).bufferSize(bufferSize).autoCancel(autoCancel).build();
        assertProcessor(processor, false, name, bufferSize, null, autoCancel, null, null);
    }

    @Test
    public void createOverrideNameBufferSizeWaitStrategy() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().name(name).bufferSize(bufferSize).waitStrategy(waitStrategy).build();
        assertProcessor(processor, false, name, bufferSize, waitStrategy, null, null, null);
    }

    @Test
    public void createDefaultExecutorOverrideAll() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().name(name).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, false, name, bufferSize, waitStrategy, autoCancel, null, null);
    }

    @Test
    public void createOverrideExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().executor(executor).build();
        assertProcessor(processor, false, null, null, null, null, executor, null);
    }

    @Test
    public void createOverrideExecutorAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().executor(executor).autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, null, null, autoCancel, executor, null);
    }

    @Test
    public void createOverrideExecutorBufferSize() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().executor(executor).bufferSize(bufferSize).build();
        assertProcessor(processor, false, null, bufferSize, null, null, executor, null);
    }

    @Test
    public void createOverrideExecutorBufferSizeAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().executor(executor).bufferSize(bufferSize).autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, bufferSize, null, autoCancel, executor, null);
    }

    @Test
    public void createOverrideExecutorBufferSizeWaitStrategy() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().executor(executor).bufferSize(bufferSize).waitStrategy(waitStrategy).build();
        assertProcessor(processor, false, null, bufferSize, waitStrategy, null, executor, null);
    }

    @Test
    public void createOverrideExecutorBufferSizeWaitStrategyAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().executor(executor).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, bufferSize, waitStrategy, autoCancel, executor, null);
    }

    @Test
    public void createOverrideAll() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService requestTaskExecutor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().executor(executor).requestTaskExecutor(requestTaskExecutor).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, false, null, bufferSize, waitStrategy, autoCancel, executor, requestTaskExecutor);
    }

    @Test
    public void shareOverrideAutoCancel() {
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, null, null, autoCancel, null, null);
    }

    @Test
    public void shareOverrideNameBufferSize() {
        String name = "nameOverride";
        int bufferSize = 1024;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).name(name).bufferSize(bufferSize).build();
        assertProcessor(processor, true, name, bufferSize, null, null, null, null);
    }

    @Test
    public void shareOverrideNameBufferSizeWaitStrategy() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).name(name).bufferSize(bufferSize).waitStrategy(waitStrategy).build();
        assertProcessor(processor, true, name, bufferSize, waitStrategy, null, null, null);
    }

    @Test
    public void shareDefaultExecutorOverrideAll() {
        String name = "nameOverride";
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).name(name).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, true, name, bufferSize, waitStrategy, autoCancel, null, null);
    }

    @Test
    public void shareOverrideExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).executor(executor).build();
        assertProcessor(processor, true, null, null, null, null, executor, null);
    }

    @Test
    public void shareOverrideExecutorAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).executor(executor).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, null, null, autoCancel, executor, null);
    }

    @Test
    public void shareOverrideExecutorBufferSize() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).executor(executor).bufferSize(bufferSize).build();
        assertProcessor(processor, true, null, bufferSize, null, null, executor, null);
    }

    @Test
    public void shareOverrideExecutorBufferSizeAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).executor(executor).bufferSize(bufferSize).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, bufferSize, null, autoCancel, executor, null);
    }

    @Test
    public void shareOverrideExecutorBufferSizeWaitStrategy() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).executor(executor).bufferSize(bufferSize).waitStrategy(waitStrategy).build();
        assertProcessor(processor, true, null, bufferSize, waitStrategy, null, executor, null);
    }

    @Test
    public void shareOverrideExecutorBufferSizeWaitStrategyAutoCancel() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).executor(executor).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, bufferSize, waitStrategy, autoCancel, executor, null);
    }

    @Test
    public void shareOverrideAll() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService requestTaskExecutor = Executors.newSingleThreadExecutor();
        int bufferSize = 1024;
        WaitStrategy waitStrategy = WaitStrategy.busySpin();
        boolean autoCancel = false;
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).executor(executor).requestTaskExecutor(requestTaskExecutor).bufferSize(bufferSize).waitStrategy(waitStrategy).autoCancel(autoCancel).build();
        assertProcessor(processor, true, null, bufferSize, waitStrategy, autoCancel, executor, requestTaskExecutor);
    }

    @Test
    public void scanProcessor() {
        TopicProcessor<String> test = TopicProcessor.create("name", 16);
        Subscription subscription = Operators.emptySubscription();
        test.onSubscribe(subscription);
        Assert.assertThat(test.scan(PARENT)).isEqualTo(subscription);
        Assert.assertThat(test.scan(CAPACITY)).isEqualTo(16);
        Assert.assertThat(test.scan(TERMINATED)).isFalse();
        Assert.assertThat(test.scan(ERROR)).isNull();
        test.onError(new IllegalStateException("boom"));
        Assert.assertThat(test.scan(ERROR)).hasMessage("boom");
        Assert.assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanInner() {
        TopicProcessor<String> main = TopicProcessor.create("name", 16);
        RingBuffer.Sequence sequence = RingBuffer.newSequence(123);
        CoreSubscriber<String> activated = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        TopicProcessor.TopicInner<String> test = new TopicProcessor.TopicInner<>(main, sequence, activated);
        Assert.assertThat(test.scan(PARENT)).isSameAs(main);
        Assert.assertThat(test.scan(ACTUAL)).isSameAs(activated);
        Assert.assertThat(test.scan(PREFETCH)).isEqualTo(Integer.MAX_VALUE);
        Assert.assertThat(test.scan(REQUESTED_FROM_DOWNSTREAM)).isEqualTo(123L);
        Assert.assertThat(test.scan(TERMINATED)).isFalse();
        Assert.assertThat(test.scan(CANCELLED)).isFalse();
        main.terminated = 1;
        Assert.assertThat(test.scan(TERMINATED)).isTrue();
        Assert.assertThat(test.scan(CANCELLED)).isFalse();
        test.cancel();
        Assert.assertThat(test.scan(CANCELLED)).isTrue();
    }

    @Test
    public void scanInnerBufferedSmallHasIntRealValue() {
        TopicProcessor<String> main = TopicProcessor.create("name", 16);
        RingBuffer.Sequence sequence = RingBuffer.newSequence(123);
        CoreSubscriber<String> sub = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        TopicProcessor.TopicInner<String> test = new TopicProcessor.TopicInner<>(main, sequence, sub);
        main.ringBuffer.getSequencer().cursor.set(((Integer.MAX_VALUE) + 5L));
        test.sequence.set(6L);
        Assert.assertThat(test.scan(BUFFERED)).isEqualTo(((Integer.MAX_VALUE) - 1));
        Assert.assertThat(test.scan(LARGE_BUFFERED)).isEqualTo(((Integer.MAX_VALUE) - 1L));
    }

    @Test
    public void scanInnerBufferedLargeHasIntMinValue() {
        TopicProcessor<String> main = TopicProcessor.create("name", 16);
        RingBuffer.Sequence sequence = RingBuffer.newSequence(123);
        CoreSubscriber<String> sub = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        TopicProcessor.TopicInner<String> test = new TopicProcessor.TopicInner<>(main, sequence, sub);
        main.ringBuffer.getSequencer().cursor.set(((Integer.MAX_VALUE) + 5L));
        test.sequence.set(2L);
        Assert.assertThat(test.scan(BUFFERED)).isEqualTo(Integer.MIN_VALUE);
        Assert.assertThat(test.scan(LARGE_BUFFERED)).isEqualTo(((Integer.MAX_VALUE) + 3L));
    }

    @Test
    public void serializedSinkSingleProducer() throws Exception {
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(false).build();
        FluxSink<Integer> sink = processor.sink();
        Assert.assertThat(sink).isInstanceOf(SerializedSink.class);
        sink = sink.next(1);
        Assert.assertThat(sink).isInstanceOf(SerializedSink.class);
        sink = sink.onRequest(( n) -> {
        });
        Assert.assertThat(sink).isInstanceOf(SerializedSink.class);
    }

    @Test
    public void nonSerializedSinkMultiProducer() throws Exception {
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).build();
        FluxSink<Integer> sink = processor.sink();
        Assert.assertThat(sink).isNotInstanceOf(SerializedSink.class);
        Assert.assertThat(sink.next(1)).isNotInstanceOf(SerializedSink.class);
    }

    @Test
    public void serializedSinkMultiProducerWithOnRequest() throws Exception {
        TopicProcessor<Integer> processor = TopicProcessor.<Integer>builder().share(true).build();
        FluxSink<Integer> sink = processor.sink();
        FluxSink<Integer> serializedSink = sink.onRequest(( n) -> {
            FluxSink<Integer> s = sink.next(1);
            assertThat(s).isInstanceOf(.class);
            s.next(2);
        });
        Assert.assertThat(serializedSink).isInstanceOf(SerializedSink.class);
        StepVerifier.create(processor).thenRequest(5).expectNext(1, 2).thenCancel().verify();
    }
}

