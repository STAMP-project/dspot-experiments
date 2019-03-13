/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.subscribers;


import io.reactivex.Subscriber;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SerializedSubscriberTest {
    Subscriber<String> subscriber;

    @Test
    public void testSingleThreadedBasic() {
        SerializedSubscriberTest.TestSingleThreadedPublisher onSubscribe = new SerializedSubscriberTest.TestSingleThreadedPublisher("one", "two", "three");
        Flowable<String> w = Flowable.unsafeCreate(onSubscribe);
        Subscriber<String> aw = serializedSubscriber(subscriber);
        w.subscribe(aw);
        onSubscribe.waitToFinish();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        // verify(s, times(1)).unsubscribe();
    }

    @Test
    public void testMultiThreadedBasic() {
        SerializedSubscriberTest.TestMultiThreadedObservable onSubscribe = new SerializedSubscriberTest.TestMultiThreadedObservable("one", "two", "three");
        Flowable<String> w = Flowable.unsafeCreate(onSubscribe);
        SerializedSubscriberTest.BusySubscriber busySubscriber = new SerializedSubscriberTest.BusySubscriber();
        Subscriber<String> aw = serializedSubscriber(busySubscriber);
        w.subscribe(aw);
        onSubscribe.waitToFinish();
        Assert.assertEquals(3, busySubscriber.onNextCount.get());
        Assert.assertFalse(busySubscriber.onError);
        Assert.assertTrue(busySubscriber.onComplete);
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        // verify(s, times(1)).unsubscribe();
        // we can have concurrency ...
        Assert.assertTrue(((onSubscribe.maxConcurrentThreads.get()) > 1));
        // ... but the onNext execution should be single threaded
        Assert.assertEquals(1, busySubscriber.maxConcurrentThreads.get());
    }

    @Test(timeout = 1000)
    public void testMultiThreadedWithNPE() throws InterruptedException {
        SerializedSubscriberTest.TestMultiThreadedObservable onSubscribe = new SerializedSubscriberTest.TestMultiThreadedObservable("one", "two", "three", null);
        Flowable<String> w = Flowable.unsafeCreate(onSubscribe);
        SerializedSubscriberTest.BusySubscriber busySubscriber = new SerializedSubscriberTest.BusySubscriber();
        Subscriber<String> aw = serializedSubscriber(busySubscriber);
        w.subscribe(aw);
        onSubscribe.waitToFinish();
        busySubscriber.terminalEvent.await();
        System.out.println(((("OnSubscribe maxConcurrentThreads: " + (onSubscribe.maxConcurrentThreads.get())) + "  Subscriber maxConcurrentThreads: ") + (busySubscriber.maxConcurrentThreads.get())));
        // we can't know how many onNext calls will occur since they each run on a separate thread
        // that depends on thread scheduling so 0, 1, 2 and 3 are all valid options
        // assertEquals(3, busySubscriber.onNextCount.get());
        Assert.assertTrue(((busySubscriber.onNextCount.get()) < 4));
        Assert.assertTrue(busySubscriber.onError);
        // no onComplete because onError was invoked
        Assert.assertFalse(busySubscriber.onComplete);
        // non-deterministic because unsubscribe happens after 'waitToFinish' releases
        // so commenting out for now as this is not a critical thing to test here
        // verify(s, times(1)).unsubscribe();
        // we can have concurrency ...
        Assert.assertTrue(((onSubscribe.maxConcurrentThreads.get()) > 1));
        // ... but the onNext execution should be single threaded
        Assert.assertEquals(1, busySubscriber.maxConcurrentThreads.get());
    }

    @Test
    public void testMultiThreadedWithNPEinMiddle() {
        int n = 10;
        for (int i = 0; i < n; i++) {
            SerializedSubscriberTest.TestMultiThreadedObservable onSubscribe = new SerializedSubscriberTest.TestMultiThreadedObservable("one", "two", "three", null, "four", "five", "six", "seven", "eight", "nine");
            Flowable<String> w = Flowable.unsafeCreate(onSubscribe);
            SerializedSubscriberTest.BusySubscriber busySubscriber = new SerializedSubscriberTest.BusySubscriber();
            Subscriber<String> aw = serializedSubscriber(busySubscriber);
            w.subscribe(aw);
            onSubscribe.waitToFinish();
            System.out.println(((("OnSubscribe maxConcurrentThreads: " + (onSubscribe.maxConcurrentThreads.get())) + "  Subscriber maxConcurrentThreads: ") + (busySubscriber.maxConcurrentThreads.get())));
            // we can have concurrency ...
            Assert.assertTrue(((onSubscribe.maxConcurrentThreads.get()) > 1));
            // ... but the onNext execution should be single threaded
            Assert.assertEquals(1, busySubscriber.maxConcurrentThreads.get());
            // this should not be the full number of items since the error should stop it before it completes all 9
            System.out.println(("onNext count: " + (busySubscriber.onNextCount.get())));
            Assert.assertFalse(busySubscriber.onComplete);
            Assert.assertTrue(busySubscriber.onError);
            Assert.assertTrue(((busySubscriber.onNextCount.get()) < 9));
            // no onComplete because onError was invoked
            // non-deterministic because unsubscribe happens after 'waitToFinish' releases
            // so commenting out for now as this is not a critical thing to test here
            // verify(s, times(1)).unsubscribe();
        }
    }

    /**
     * A non-realistic use case that tries to expose thread-safety issues by throwing lots of out-of-order
     * events on many threads.
     */
    @Test
    public void runOutOfOrderConcurrencyTest() {
        ExecutorService tp = Executors.newFixedThreadPool(20);
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            SerializedSubscriberTest.TestConcurrencySubscriber tw = new SerializedSubscriberTest.TestConcurrencySubscriber();
            // we need Synchronized + SafeSubscriber to handle synchronization plus life-cycle
            Subscriber<String> w = serializedSubscriber(new SafeSubscriber<String>(tw));
            Future<?> f1 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 12000));
            Future<?> f2 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 5000));
            Future<?> f3 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 75000));
            Future<?> f4 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 13500));
            Future<?> f5 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 22000));
            Future<?> f6 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 15000));
            Future<?> f7 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 7500));
            Future<?> f8 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 23500));
            Future<?> f10 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete, f1, f2, f3, f4));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // ignore
            }
            Future<?> f11 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete, f4, f6, f7));
            Future<?> f12 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete, f4, f6, f7));
            Future<?> f13 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete, f4, f6, f7));
            Future<?> f14 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete, f4, f6, f7));
            // // the next 4 onError events should wait on same as f10
            Future<?> f15 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onError, f1, f2, f3, f4));
            Future<?> f16 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onError, f1, f2, f3, f4));
            Future<?> f17 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onError, f1, f2, f3, f4));
            Future<?> f18 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onError, f1, f2, f3, f4));
            SerializedSubscriberTest.waitOnThreads(f1, f2, f3, f4, f5, f6, f7, f8, f10, f11, f12, f13, f14, f15, f16, f17, f18);
            @SuppressWarnings("unused")
            int numNextEvents = tw.assertEvents(null);// no check of type since we don't want to test barging results here, just interleaving behavior

            // System.out.println("Number of events executed: " + numNextEvents);
            for (int i = 0; i < (errors.size()); i++) {
                TestHelper.assertUndeliverable(errors, i, RuntimeException.class);
            }
        } catch (Throwable e) {
            Assert.fail(("Concurrency test failed: " + (e.getMessage())));
            e.printStackTrace();
        } finally {
            tp.shutdown();
            try {
                tp.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void runConcurrencyTest() {
        ExecutorService tp = Executors.newFixedThreadPool(20);
        try {
            SerializedSubscriberTest.TestConcurrencySubscriber tw = new SerializedSubscriberTest.TestConcurrencySubscriber();
            // we need Synchronized + SafeSubscriber to handle synchronization plus life-cycle
            Subscriber<String> w = serializedSubscriber(new SafeSubscriber<String>(tw));
            w.onSubscribe(new BooleanSubscription());
            Future<?> f1 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 12000));
            Future<?> f2 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 5000));
            Future<?> f3 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 75000));
            Future<?> f4 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 13500));
            Future<?> f5 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 22000));
            Future<?> f6 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 15000));
            Future<?> f7 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 7500));
            Future<?> f8 = tp.submit(new SerializedSubscriberTest.OnNextThread(w, 23500));
            // 12000 + 5000 + 75000 + 13500 + 22000 + 15000 + 7500 + 23500 = 173500
            Future<?> f10 = tp.submit(new SerializedSubscriberTest.CompletionThread(w, SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete, f1, f2, f3, f4, f5, f6, f7, f8));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // ignore
            }
            SerializedSubscriberTest.waitOnThreads(f1, f2, f3, f4, f5, f6, f7, f8, f10);
            int numNextEvents = tw.assertEvents(null);// no check of type since we don't want to test barging results here, just interleaving behavior

            Assert.assertEquals(173500, numNextEvents);
            // System.out.println("Number of events executed: " + numNextEvents);
        } catch (Throwable e) {
            Assert.fail(("Concurrency test failed: " + (e.getMessage())));
            e.printStackTrace();
        } finally {
            tp.shutdown();
            try {
                tp.awaitTermination(25000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A thread that will pass data to onNext.
     */
    public static class OnNextThread implements Runnable {
        private final CountDownLatch latch;

        private final Subscriber<String> subscriber;

        private final int numStringsToSend;

        final AtomicInteger produced;

        private final CountDownLatch running;

        OnNextThread(Subscriber<String> subscriber, int numStringsToSend, CountDownLatch latch, CountDownLatch running) {
            this(subscriber, numStringsToSend, new AtomicInteger(), latch, running);
        }

        OnNextThread(Subscriber<String> subscriber, int numStringsToSend, AtomicInteger produced) {
            this(subscriber, numStringsToSend, produced, null, null);
        }

        OnNextThread(Subscriber<String> subscriber, int numStringsToSend, AtomicInteger produced, CountDownLatch latch, CountDownLatch running) {
            this.subscriber = subscriber;
            this.numStringsToSend = numStringsToSend;
            this.produced = produced;
            this.latch = latch;
            this.running = running;
        }

        OnNextThread(Subscriber<String> subscriber, int numStringsToSend) {
            this(subscriber, numStringsToSend, new AtomicInteger());
        }

        @Override
        public void run() {
            if ((running) != null) {
                running.countDown();
            }
            for (int i = 0; i < (numStringsToSend); i++) {
                subscriber.onNext((((Thread.currentThread().getId()) + "-") + i));
                if ((latch) != null) {
                    latch.countDown();
                }
                produced.incrementAndGet();
            }
        }
    }

    /**
     * A thread that will call onError or onNext.
     */
    public static class CompletionThread implements Runnable {
        private final Subscriber<String> subscriber;

        private final SerializedSubscriberTest.TestConcurrencySubscriberEvent event;

        private final Future<?>[] waitOnThese;

        CompletionThread(Subscriber<String> Subscriber, SerializedSubscriberTest.TestConcurrencySubscriberEvent event, Future<?>... waitOnThese) {
            this.subscriber = Subscriber;
            this.event = event;
            this.waitOnThese = waitOnThese;
        }

        @Override
        public void run() {
            /* if we have 'waitOnThese' futures, we'll wait on them before proceeding */
            if ((waitOnThese) != null) {
                for (Future<?> f : waitOnThese) {
                    try {
                        f.get();
                    } catch (Throwable e) {
                        System.err.println("Error while waiting on future in CompletionThread");
                    }
                }
            }
            /* send the event */
            if ((event) == (SerializedSubscriberTest.TestConcurrencySubscriberEvent.onError)) {
                subscriber.onError(new RuntimeException("mocked exception"));
            } else
                if ((event) == (SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete)) {
                    subscriber.onComplete();
                } else {
                    throw new IllegalArgumentException("Expecting either onError or onComplete");
                }

        }
    }

    enum TestConcurrencySubscriberEvent {

        onComplete,
        onError,
        onNext;}

    private static class TestConcurrencySubscriber extends DefaultSubscriber<String> {
        /**
         * Used to store the order and number of events received.
         */
        private final LinkedBlockingQueue<SerializedSubscriberTest.TestConcurrencySubscriberEvent> events = new LinkedBlockingQueue<SerializedSubscriberTest.TestConcurrencySubscriberEvent>();

        private final int waitTime;

        @SuppressWarnings("unused")
        TestConcurrencySubscriber(int waitTimeInNext) {
            this.waitTime = waitTimeInNext;
        }

        TestConcurrencySubscriber() {
            this.waitTime = 0;
        }

        @Override
        public void onComplete() {
            events.add(SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete);
        }

        @Override
        public void onError(Throwable e) {
            events.add(SerializedSubscriberTest.TestConcurrencySubscriberEvent.onError);
        }

        @Override
        public void onNext(String args) {
            events.add(SerializedSubscriberTest.TestConcurrencySubscriberEvent.onNext);
            // do some artificial work to make the thread scheduling/timing vary
            int s = 0;
            for (int i = 0; i < 20; i++) {
                s += s * i;
            }
            if ((waitTime) > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }

        /**
         * Assert the order of events is correct and return the number of onNext executions.
         *
         * @param expectedEndingEvent
         * 		the last event
         * @return int count of onNext calls
         * @throws IllegalStateException
         * 		If order of events was invalid.
         */
        public int assertEvents(SerializedSubscriberTest.TestConcurrencySubscriberEvent expectedEndingEvent) throws IllegalStateException {
            int nextCount = 0;
            boolean finished = false;
            for (SerializedSubscriberTest.TestConcurrencySubscriberEvent e : events) {
                if (e == (SerializedSubscriberTest.TestConcurrencySubscriberEvent.onNext)) {
                    if (finished) {
                        // already finished, we shouldn't get this again
                        throw new IllegalStateException("Received onNext but we're already finished.");
                    }
                    nextCount++;
                } else
                    if (e == (SerializedSubscriberTest.TestConcurrencySubscriberEvent.onError)) {
                        if (finished) {
                            // already finished, we shouldn't get this again
                            throw new IllegalStateException("Received onError but we're already finished.");
                        }
                        if ((expectedEndingEvent != null) && ((SerializedSubscriberTest.TestConcurrencySubscriberEvent.onError) != expectedEndingEvent)) {
                            throw new IllegalStateException(("Received onError ending event but expected " + expectedEndingEvent));
                        }
                        finished = true;
                    } else
                        if (e == (SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete)) {
                            if (finished) {
                                // already finished, we shouldn't get this again
                                throw new IllegalStateException("Received onComplete but we're already finished.");
                            }
                            if ((expectedEndingEvent != null) && ((SerializedSubscriberTest.TestConcurrencySubscriberEvent.onComplete) != expectedEndingEvent)) {
                                throw new IllegalStateException(("Received onComplete ending event but expected " + expectedEndingEvent));
                            }
                            finished = true;
                        }


            }
            return nextCount;
        }
    }

    /**
     * This spawns a single thread for the subscribe execution.
     */
    static class TestSingleThreadedPublisher implements Publisher<String> {
        final String[] values;

        private Thread t;

        TestSingleThreadedPublisher(final String... values) {
            this.values = values;
        }

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            System.out.println("TestSingleThreadedObservable subscribed to ...");
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("running TestSingleThreadedObservable thread");
                        for (String s : values) {
                            System.out.println(("TestSingleThreadedObservable onNext: " + s));
                            subscriber.onNext(s);
                        }
                        subscriber.onComplete();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            System.out.println("starting TestSingleThreadedObservable thread");
            t.start();
            System.out.println("done starting TestSingleThreadedObservable thread");
        }

        public void waitToFinish() {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This spawns a thread for the subscription, then a separate thread for each onNext call.
     */
    static class TestMultiThreadedObservable implements Publisher<String> {
        final String[] values;

        Thread t;

        AtomicInteger threadsRunning = new AtomicInteger();

        AtomicInteger maxConcurrentThreads = new AtomicInteger();

        ExecutorService threadPool;

        TestMultiThreadedObservable(String... values) {
            this.values = values;
            this.threadPool = Executors.newCachedThreadPool();
        }

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
            final NullPointerException npe = new NullPointerException();
            System.out.println("TestMultiThreadedObservable subscribed to ...");
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("running TestMultiThreadedObservable thread");
                        int j = 0;
                        for (final String s : values) {
                            final int fj = ++j;
                            threadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    threadsRunning.incrementAndGet();
                                    try {
                                        // perform onNext call
                                        System.out.println(((("TestMultiThreadedObservable onNext: " + s) + " on thread ") + (Thread.currentThread().getName())));
                                        if (s == null) {
                                            // force an error
                                            throw npe;
                                        } else {
                                            // allow the exception to queue up
                                            int sleep = (fj % 3) * 10;
                                            if (sleep != 0) {
                                                Thread.sleep(sleep);
                                            }
                                        }
                                        subscriber.onNext(s);
                                        // capture 'maxThreads'
                                        int concurrentThreads = threadsRunning.get();
                                        int maxThreads = maxConcurrentThreads.get();
                                        if (concurrentThreads > maxThreads) {
                                            maxConcurrentThreads.compareAndSet(maxThreads, concurrentThreads);
                                        }
                                    } catch (Throwable e) {
                                        subscriber.onError(e);
                                    } finally {
                                        threadsRunning.decrementAndGet();
                                    }
                                }
                            });
                        }
                        // we are done spawning threads
                        threadPool.shutdown();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                    // wait until all threads are done, then mark it as COMPLETED
                    try {
                        // wait for all the threads to finish
                        if (!(threadPool.awaitTermination(5, TimeUnit.SECONDS))) {
                            System.out.println("Threadpool did not terminate in time.");
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    subscriber.onComplete();
                }
            });
            System.out.println("starting TestMultiThreadedObservable thread");
            t.start();
            System.out.println("done starting TestMultiThreadedObservable thread");
        }

        public void waitToFinish() {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class BusySubscriber extends DefaultSubscriber<String> {
        volatile boolean onComplete;

        volatile boolean onError;

        AtomicInteger onNextCount = new AtomicInteger();

        AtomicInteger threadsRunning = new AtomicInteger();

        AtomicInteger maxConcurrentThreads = new AtomicInteger();

        final CountDownLatch terminalEvent = new CountDownLatch(1);

        @Override
        public void onComplete() {
            threadsRunning.incrementAndGet();
            try {
                onComplete = true;
            } finally {
                captureMaxThreads();
                threadsRunning.decrementAndGet();
                terminalEvent.countDown();
            }
        }

        @Override
        public void onError(Throwable e) {
            System.out.println((">>>>>>>>>>>>>>>>>>>> onError received: " + e));
            threadsRunning.incrementAndGet();
            try {
                onError = true;
            } finally {
                captureMaxThreads();
                threadsRunning.decrementAndGet();
                terminalEvent.countDown();
            }
        }

        @Override
        public void onNext(String args) {
            threadsRunning.incrementAndGet();
            try {
                onNextCount.incrementAndGet();
                try {
                    // simulate doing something computational
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                // capture 'maxThreads'
                captureMaxThreads();
                threadsRunning.decrementAndGet();
            }
        }

        protected void captureMaxThreads() {
            int concurrentThreads = threadsRunning.get();
            int maxThreads = maxConcurrentThreads.get();
            if (concurrentThreads > maxThreads) {
                maxConcurrentThreads.compareAndSet(maxThreads, concurrentThreads);
                if (concurrentThreads > 1) {
                    new RuntimeException("should not be greater than 1").printStackTrace();
                }
            }
        }
    }

    @Test
    public void testErrorReentry() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final AtomicReference<Subscriber<Integer>> serial = new AtomicReference<Subscriber<Integer>>();
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
                @Override
                public void onNext(Integer v) {
                    serial.get().onError(new TestException());
                    serial.get().onError(new TestException());
                    super.onNext(v);
                }
            };
            SerializedSubscriber<Integer> sobs = new SerializedSubscriber<Integer>(ts);
            sobs.onSubscribe(new BooleanSubscription());
            serial.set(sobs);
            sobs.onNext(1);
            ts.assertValue(1);
            ts.assertError(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void testCompleteReentry() {
        final AtomicReference<Subscriber<Integer>> serial = new AtomicReference<Subscriber<Integer>>();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer v) {
                serial.get().onComplete();
                serial.get().onComplete();
                super.onNext(v);
            }
        };
        SerializedSubscriber<Integer> sobs = new SerializedSubscriber<Integer>(ts);
        sobs.onSubscribe(new BooleanSubscription());
        serial.set(sobs);
        sobs.onNext(1);
        ts.assertValue(1);
        ts.assertComplete();
        ts.assertNoErrors();
    }

    @Test
    public void dispose() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        SerializedSubscriber<Integer> so = new SerializedSubscriber<Integer>(ts);
        BooleanSubscription bs = new BooleanSubscription();
        so.onSubscribe(bs);
        ts.cancel();
        Assert.assertTrue(bs.isCancelled());
    }

    @Test
    public void onCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final SerializedSubscriber<Integer> so = new SerializedSubscriber<Integer>(ts);
            BooleanSubscription bs = new BooleanSubscription();
            so.onSubscribe(bs);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    so.onComplete();
                }
            };
            TestHelper.race(r, r);
            ts.awaitDone(5, TimeUnit.SECONDS).assertResult();
        }
    }

    @Test
    public void onNextOnCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final SerializedSubscriber<Integer> so = new SerializedSubscriber<Integer>(ts);
            BooleanSubscription bs = new BooleanSubscription();
            so.onSubscribe(bs);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    so.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    so.onNext(1);
                }
            };
            TestHelper.race(r1, r2);
            ts.awaitDone(5, TimeUnit.SECONDS).assertNoErrors().assertComplete();
            Assert.assertTrue(((ts.valueCount()) <= 1));
        }
    }

    @Test
    public void onNextOnErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final SerializedSubscriber<Integer> so = new SerializedSubscriber<Integer>(ts);
            BooleanSubscription bs = new BooleanSubscription();
            so.onSubscribe(bs);
            final Throwable ex = new TestException();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    so.onError(ex);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    so.onNext(1);
                }
            };
            TestHelper.race(r1, r2);
            ts.awaitDone(5, TimeUnit.SECONDS).assertError(ex).assertNotComplete();
            Assert.assertTrue(((ts.valueCount()) <= 1));
        }
    }

    @Test
    public void onNextOnErrorRaceDelayError() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final SerializedSubscriber<Integer> so = new SerializedSubscriber<Integer>(ts, true);
            BooleanSubscription bs = new BooleanSubscription();
            so.onSubscribe(bs);
            final Throwable ex = new TestException();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    so.onError(ex);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    so.onNext(1);
                }
            };
            TestHelper.race(r1, r2);
            ts.awaitDone(5, TimeUnit.SECONDS).assertError(ex).assertNotComplete();
            Assert.assertTrue(((ts.valueCount()) <= 1));
        }
    }

    @Test
    public void startOnce() {
        List<Throwable> error = TestHelper.trackPluginErrors();
        try {
            TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
            final SerializedSubscriber<Integer> so = new SerializedSubscriber<Integer>(ts);
            so.onSubscribe(new BooleanSubscription());
            BooleanSubscription bs = new BooleanSubscription();
            so.onSubscribe(bs);
            Assert.assertTrue(bs.isCancelled());
            TestHelper.assertError(error, 0, IllegalStateException.class, "Subscription already set!");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteOnErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
                final SerializedSubscriber<Integer> so = new SerializedSubscriber<Integer>(ts);
                BooleanSubscription bs = new BooleanSubscription();
                so.onSubscribe(bs);
                final Throwable ex = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        so.onError(ex);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        so.onComplete();
                    }
                };
                TestHelper.race(r1, r2);
                ts.awaitDone(5, TimeUnit.SECONDS);
                if ((ts.completions()) != 0) {
                    ts.assertResult();
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                } else {
                    ts.assertFailure(TestException.class).assertError(ex);
                    Assert.assertTrue(("" + errors), errors.isEmpty());
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void nullOnNext() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        final SerializedSubscriber<Integer> so = new SerializedSubscriber<Integer>(ts);
        so.onSubscribe(new BooleanSubscription());
        so.onNext(null);
        ts.assertFailureAndMessage(NullPointerException.class, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
    }
}

