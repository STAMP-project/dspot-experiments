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
package io.reactivex.internal.operators.observable;


import io.reactivex.Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableMergeTest {
    Observer<String> stringObserver;

    int count;

    @Test
    public void testMergeObservableOfObservables() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeTest.TestSynchronousObservable());
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeTest.TestSynchronousObservable());
        Observable<Observable<String>> observableOfObservables = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? extends Observable<String>> observer) {
                observer.onSubscribe(Disposables.empty());
                // simulate what would happen in an Observable
                observer.onNext(o1);
                observer.onNext(o2);
                observer.onComplete();
            }
        });
        Observable<String> m = Observable.merge(observableOfObservables);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
        Mockito.verify(stringObserver, Mockito.times(2)).onNext("hello");
    }

    @Test
    public void testMergeArray() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeTest.TestSynchronousObservable());
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeTest.TestSynchronousObservable());
        Observable<String> m = Observable.merge(o1, o2);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(2)).onNext("hello");
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testMergeList() {
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeTest.TestSynchronousObservable());
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeTest.TestSynchronousObservable());
        List<Observable<String>> listOfObservables = new java.util.ArrayList<Observable<String>>();
        listOfObservables.add(o1);
        listOfObservables.add(o2);
        Observable<String> m = Observable.merge(listOfObservables);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
        Mockito.verify(stringObserver, Mockito.times(2)).onNext("hello");
    }

    @Test(timeout = 1000)
    public void testUnSubscribeObservableOfObservables() throws InterruptedException {
        final AtomicBoolean unsubscribed = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(1);
        Observable<Observable<Long>> source = Observable.unsafeCreate(new ObservableSource<Observable<Long>>() {
            @Override
            public void subscribe(final Observer<? extends Observable<Long>> observer) {
                // verbose on purpose so I can track the inside of it
                final Disposable upstream = Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("*** unsubscribed");
                        unsubscribed.set(true);
                    }
                });
                observer.onSubscribe(upstream);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!(unsubscribed.get())) {
                            observer.onNext(io.reactivex.Observable.just(1L, 2L));
                        } 
                        System.out.println(("Done looping after unsubscribe: " + (unsubscribed.get())));
                        observer.onComplete();
                        // mark that the thread is finished
                        latch.countDown();
                    }
                }).start();
            }
        });
        final AtomicInteger count = new AtomicInteger();
        Observable.merge(source).take(6).blockingForEach(new Consumer<Long>() {
            @Override
            public void accept(Long v) {
                System.out.println(("Value: " + v));
                int c = count.incrementAndGet();
                if (c > 6) {
                    Assert.fail("Should be only 6");
                }
            }
        });
        latch.await(1000, TimeUnit.MILLISECONDS);
        System.out.println(("unsubscribed: " + (unsubscribed.get())));
        Assert.assertTrue(unsubscribed.get());
    }

    @Test
    public void testMergeArrayWithThreading() {
        final ObservableMergeTest.TestASynchronousObservable o1 = new ObservableMergeTest.TestASynchronousObservable();
        final ObservableMergeTest.TestASynchronousObservable o2 = new ObservableMergeTest.TestASynchronousObservable();
        Observable<String> m = Observable.merge(Observable.unsafeCreate(o1), Observable.unsafeCreate(o2));
        TestObserver<String> to = new TestObserver<String>(stringObserver);
        m.subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(2)).onNext("hello");
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSynchronizationOfMultipleSequencesLoop() throws Throwable {
        for (int i = 0; i < 100; i++) {
            System.out.println(("testSynchronizationOfMultipleSequencesLoop > " + i));
            testSynchronizationOfMultipleSequences();
        }
    }

    @Test
    public void testSynchronizationOfMultipleSequences() throws Throwable {
        final ObservableMergeTest.TestASynchronousObservable o1 = new ObservableMergeTest.TestASynchronousObservable();
        final ObservableMergeTest.TestASynchronousObservable o2 = new ObservableMergeTest.TestASynchronousObservable();
        // use this latch to cause onNext to wait until we're ready to let it go
        final CountDownLatch endLatch = new CountDownLatch(1);
        final AtomicInteger concurrentCounter = new AtomicInteger();
        final AtomicInteger totalCounter = new AtomicInteger();
        Observable<String> m = Observable.merge(Observable.unsafeCreate(o1), Observable.unsafeCreate(o2));
        m.subscribe(new DefaultObserver<String>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                throw new RuntimeException("failed", e);
            }

            @Override
            public void onNext(String v) {
                totalCounter.incrementAndGet();
                concurrentCounter.incrementAndGet();
                try {
                    // avoid deadlocking the main thread
                    if (Thread.currentThread().getName().equals("TestASynchronousObservable")) {
                        // wait here until we're done asserting
                        endLatch.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException("failed", e);
                } finally {
                    concurrentCounter.decrementAndGet();
                }
            }
        });
        // wait for both observables to send (one should be blocked)
        o1.onNextBeingSent.await();
        o2.onNextBeingSent.await();
        // I can't think of a way to know for sure that both threads have or are trying to send onNext
        // since I can't use a CountDownLatch for "after" onNext since I want to catch during it
        // but I can't know for sure onNext is invoked
        // so I'm unfortunately reverting to using a Thread.sleep to allow the process scheduler time
        // to make sure after o1.onNextBeingSent and o2.onNextBeingSent are hit that the following
        // onNext is invoked.
        int timeout = 20;
        while (((timeout--) > 0) && ((concurrentCounter.get()) != 1)) {
            Thread.sleep(100);
        } 
        try {
            // in try/finally so threads are released via latch countDown even if assertion fails
            Assert.assertEquals(1, concurrentCounter.get());
        } finally {
            // release so it can finish
            endLatch.countDown();
        }
        try {
            o1.t.join();
            o2.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(2, totalCounter.get());
        Assert.assertEquals(0, concurrentCounter.get());
    }

    /**
     * Unit test from OperationMergeDelayError backported here to show how these use cases work with normal merge.
     */
    @Test
    public void testError1() {
        // we are using synchronous execution to test this exactly rather than non-deterministic concurrent behavior
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeTest.TestErrorObservable("four", null, "six"));// we expect to lose "six"

        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeTest.TestErrorObservable("one", "two", "three"));// we expect to lose all of these since o1 is done first and fails

        Observable<String> m = Observable.merge(o1, o2);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringObserver, Mockito.never()).onComplete();
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("one");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("two");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("three");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("four");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("five");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("six");
    }

    /**
     * Unit test from OperationMergeDelayError backported here to show how these use cases work with normal merge.
     */
    @Test
    public void testError2() {
        // we are using synchronous execution to test this exactly rather than non-deterministic concurrent behavior
        final Observable<String> o1 = Observable.unsafeCreate(new ObservableMergeTest.TestErrorObservable("one", "two", "three"));
        final Observable<String> o2 = Observable.unsafeCreate(new ObservableMergeTest.TestErrorObservable("four", null, "six"));// we expect to lose "six"

        final Observable<String> o3 = Observable.unsafeCreate(new ObservableMergeTest.TestErrorObservable("seven", "eight", null));// we expect to lose all of these since o2 is done first and fails

        final Observable<String> o4 = Observable.unsafeCreate(new ObservableMergeTest.TestErrorObservable("nine"));// we expect to lose all of these since o2 is done first and fails

        Observable<String> m = Observable.merge(o1, o2, o3, o4);
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(NullPointerException.class));
        Mockito.verify(stringObserver, Mockito.never()).onComplete();
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("two");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("three");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("four");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("five");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("six");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("seven");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("eight");
        Mockito.verify(stringObserver, Mockito.times(0)).onNext("nine");
    }

    private static class TestSynchronousObservable implements ObservableSource<String> {
        @Override
        public void subscribe(Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            observer.onNext("hello");
            observer.onComplete();
        }
    }

    private static class TestASynchronousObservable implements ObservableSource<String> {
        Thread t;

        final CountDownLatch onNextBeingSent = new CountDownLatch(1);

        @Override
        public void subscribe(final Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    onNextBeingSent.countDown();
                    try {
                        observer.onNext("hello");
                        // I can't use a countDownLatch to prove we are actually sending 'onNext'
                        // since it will block if synchronized and I'll deadlock
                        observer.onComplete();
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                }
            }, "TestASynchronousObservable");
            t.start();
        }
    }

    private static class TestErrorObservable implements ObservableSource<String> {
        String[] valuesToReturn;

        TestErrorObservable(String... values) {
            valuesToReturn = values;
        }

        @Override
        public void subscribe(Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            for (String s : valuesToReturn) {
                if (s == null) {
                    System.out.println("throwing exception");
                    observer.onError(new NullPointerException());
                } else {
                    observer.onNext(s);
                }
            }
            observer.onComplete();
        }
    }

    @Test
    public void testUnsubscribeAsObservablesComplete() {
        TestScheduler scheduler1 = new TestScheduler();
        AtomicBoolean os1 = new AtomicBoolean(false);
        Observable<Long> o1 = createObservableOf5IntervalsOf1SecondIncrementsWithSubscriptionHook(scheduler1, os1);
        TestScheduler scheduler2 = new TestScheduler();
        AtomicBoolean os2 = new AtomicBoolean(false);
        Observable<Long> o2 = createObservableOf5IntervalsOf1SecondIncrementsWithSubscriptionHook(scheduler2, os2);
        TestObserver<Long> to = new TestObserver<Long>();
        Observable.merge(o1, o2).subscribe(to);
        // we haven't incremented time so nothing should be received yet
        to.assertNoValues();
        scheduler1.advanceTimeBy(3, TimeUnit.SECONDS);
        scheduler2.advanceTimeBy(2, TimeUnit.SECONDS);
        to.assertValues(0L, 1L, 2L, 0L, 1L);
        // not unsubscribed yet
        Assert.assertFalse(os1.get());
        Assert.assertFalse(os2.get());
        // advance to the end at which point it should complete
        scheduler1.advanceTimeBy(3, TimeUnit.SECONDS);
        to.assertValues(0L, 1L, 2L, 0L, 1L, 3L, 4L);
        Assert.assertTrue(os1.get());
        Assert.assertFalse(os2.get());
        // both should be completed now
        scheduler2.advanceTimeBy(3, TimeUnit.SECONDS);
        to.assertValues(0L, 1L, 2L, 0L, 1L, 3L, 4L, 2L, 3L, 4L);
        Assert.assertTrue(os1.get());
        Assert.assertTrue(os2.get());
        to.assertTerminated();
    }

    @Test
    public void testEarlyUnsubscribe() {
        for (int i = 0; i < 10; i++) {
            TestScheduler scheduler1 = new TestScheduler();
            AtomicBoolean os1 = new AtomicBoolean(false);
            Observable<Long> o1 = createObservableOf5IntervalsOf1SecondIncrementsWithSubscriptionHook(scheduler1, os1);
            TestScheduler scheduler2 = new TestScheduler();
            AtomicBoolean os2 = new AtomicBoolean(false);
            Observable<Long> o2 = createObservableOf5IntervalsOf1SecondIncrementsWithSubscriptionHook(scheduler2, os2);
            TestObserver<Long> to = new TestObserver<Long>();
            Observable.merge(o1, o2).subscribe(to);
            // we haven't incremented time so nothing should be received yet
            to.assertNoValues();
            scheduler1.advanceTimeBy(3, TimeUnit.SECONDS);
            scheduler2.advanceTimeBy(2, TimeUnit.SECONDS);
            to.assertValues(0L, 1L, 2L, 0L, 1L);
            // not unsubscribed yet
            Assert.assertFalse(os1.get());
            Assert.assertFalse(os2.get());
            // early unsubscribe
            to.dispose();
            Assert.assertTrue(os1.get());
            Assert.assertTrue(os2.get());
            to.assertValues(0L, 1L, 2L, 0L, 1L);
            // FIXME not happening anymore
            // ts.assertUnsubscribed();
        }
    }

    // (timeout = 10000)
    @Test
    public void testConcurrency() {
        Observable<Integer> o = range(1, 10000).subscribeOn(Schedulers.newThread());
        for (int i = 0; i < 10; i++) {
            Observable<Integer> merge = Observable.merge(o, o, o);
            TestObserver<Integer> to = new TestObserver<Integer>();
            merge.subscribe(to);
            to.awaitTerminalEvent(3, TimeUnit.SECONDS);
            to.assertTerminated();
            to.assertNoErrors();
            to.assertComplete();
            List<Integer> onNextEvents = to.values();
            Assert.assertEquals(30000, onNextEvents.size());
            // System.out.println("onNext: " + onNextEvents.size() + " onComplete: " + ts.getOnCompletedEvents().size());
        }
    }

    @Test
    public void testConcurrencyWithSleeping() {
        Observable<Integer> o = Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(final Observer<? super Integer> observer) {
                Worker inner = Schedulers.newThread().createWorker();
                final CompositeDisposable as = new CompositeDisposable();
                as.add(Disposables.empty());
                as.add(inner);
                observer.onSubscribe(as);
                inner.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < 100; i++) {
                                observer.onNext(1);
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            observer.onError(e);
                        }
                        as.dispose();
                        observer.onComplete();
                    }
                });
            }
        });
        for (int i = 0; i < 10; i++) {
            Observable<Integer> merge = Observable.merge(o, o, o);
            TestObserver<Integer> to = new TestObserver<Integer>();
            merge.subscribe(to);
            to.awaitTerminalEvent();
            to.assertComplete();
            List<Integer> onNextEvents = to.values();
            Assert.assertEquals(300, onNextEvents.size());
            // System.out.println("onNext: " + onNextEvents.size() + " onComplete: " + ts.getOnCompletedEvents().size());
        }
    }

    @Test
    public void testConcurrencyWithBrokenOnCompleteContract() {
        Observable<Integer> o = unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(final Observer<? super Integer> observer) {
                Worker inner = Schedulers.newThread().createWorker();
                final CompositeDisposable as = new CompositeDisposable();
                as.add(Disposables.empty());
                as.add(inner);
                observer.onSubscribe(as);
                inner.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < 10000; i++) {
                                observer.onNext(i);
                            }
                        } catch (Exception e) {
                            observer.onError(e);
                        }
                        as.dispose();
                        observer.onComplete();
                        observer.onComplete();
                        observer.onComplete();
                    }
                });
            }
        });
        for (int i = 0; i < 10; i++) {
            Observable<Integer> merge = Observable.merge(o, o, o);
            TestObserver<Integer> to = new TestObserver<Integer>();
            merge.subscribe(to);
            to.awaitTerminalEvent();
            to.assertNoErrors();
            to.assertComplete();
            List<Integer> onNextEvents = to.values();
            Assert.assertEquals(30000, onNextEvents.size());
            // System.out.println("onNext: " + onNextEvents.size() + " onComplete: " + ts.getOnCompletedEvents().size());
        }
    }

    @Test
    public void testBackpressureUpstream() throws InterruptedException {
        final AtomicInteger generated1 = new AtomicInteger();
        Observable<Integer> o1 = createInfiniteObservable(generated1).subscribeOn(Schedulers.computation());
        final AtomicInteger generated2 = new AtomicInteger();
        Observable<Integer> o2 = createInfiniteObservable(generated2).subscribeOn(Schedulers.computation());
        TestObserver<Integer> testObserver = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                System.err.println(((("TestObserver received => " + t) + "  on thread ") + (Thread.currentThread())));
                super.onNext(t);
            }
        };
        Observable.merge(o1.take(((Flowable.bufferSize()) * 2)), o2.take(((Flowable.bufferSize()) * 2))).subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        if ((testObserver.errors().size()) > 0) {
            testObserver.errors().get(0).printStackTrace();
        }
        testObserver.assertNoErrors();
        System.err.println(testObserver.values());
        Assert.assertEquals(((Flowable.bufferSize()) * 4), testObserver.values().size());
        // it should be between the take num and requested batch size across the async boundary
        System.out.println(("Generated 1: " + (generated1.get())));
        System.out.println(("Generated 2: " + (generated2.get())));
        Assert.assertTrue((((generated1.get()) >= ((Flowable.bufferSize()) * 2)) && ((generated1.get()) <= ((Flowable.bufferSize()) * 4))));
    }

    @Test
    public void testBackpressureUpstream2InLoop() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            System.err.flush();
            System.out.println("---");
            System.out.flush();
            testBackpressureUpstream2();
        }
    }

    @Test
    public void testBackpressureUpstream2() throws InterruptedException {
        final AtomicInteger generated1 = new AtomicInteger();
        Observable<Integer> o1 = createInfiniteObservable(generated1).subscribeOn(Schedulers.computation());
        TestObserver<Integer> testObserver = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
            }
        };
        Observable.merge(o1.take(((Flowable.bufferSize()) * 2)), just((-99))).subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        List<Integer> onNextEvents = testObserver.values();
        System.out.println(((("Generated 1: " + (generated1.get())) + " / received: ") + (onNextEvents.size())));
        System.out.println(onNextEvents);
        if ((testObserver.errors().size()) > 0) {
            testObserver.errors().get(0).printStackTrace();
        }
        testObserver.assertNoErrors();
        Assert.assertEquals((((Flowable.bufferSize()) * 2) + 1), onNextEvents.size());
        // it should be between the take num and requested batch size across the async boundary
        Assert.assertTrue((((generated1.get()) >= ((Flowable.bufferSize()) * 2)) && ((generated1.get()) <= ((Flowable.bufferSize()) * 3))));
    }

    /**
     * This is the same as the upstreams ones, but now adds the downstream as well by using observeOn.
     *
     * This requires merge to also obey the Product.request values coming from it's child Observer.
     *
     * @throws InterruptedException
     * 		if the test is interrupted
     */
    @Test(timeout = 10000)
    public void testBackpressureDownstreamWithConcurrentStreams() throws InterruptedException {
        final AtomicInteger generated1 = new AtomicInteger();
        Observable<Integer> o1 = createInfiniteObservable(generated1).subscribeOn(Schedulers.computation());
        final AtomicInteger generated2 = new AtomicInteger();
        Observable<Integer> o2 = createInfiniteObservable(generated2).subscribeOn(Schedulers.computation());
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                if (t < 100) {
                    try {
                        // force a slow consumer
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // System.err.println("TestObserver received => " + t + "  on thread " + Thread.currentThread());
                super.onNext(t);
            }
        };
        Observable.merge(o1.take(((Flowable.bufferSize()) * 2)), o2.take(((Flowable.bufferSize()) * 2))).observeOn(Schedulers.computation()).subscribe(to);
        to.awaitTerminalEvent();
        if ((to.errors().size()) > 0) {
            to.errors().get(0).printStackTrace();
        }
        to.assertNoErrors();
        System.err.println(to.values());
        Assert.assertEquals(((Flowable.bufferSize()) * 4), to.values().size());
        // it should be between the take num and requested batch size across the async boundary
        System.out.println(("Generated 1: " + (generated1.get())));
        System.out.println(("Generated 2: " + (generated2.get())));
        Assert.assertTrue((((generated1.get()) >= ((Flowable.bufferSize()) * 2)) && ((generated1.get()) <= ((Flowable.bufferSize()) * 4))));
    }

    /**
     * Currently there is no solution to this ... we can't exert backpressure on the outer Observable if we
     * can't know if the ones we've received so far are going to emit or not, otherwise we could starve the system.
     *
     * For example, 10,000 Observables are being merged (bad use case to begin with, but ...) and it's only one of them
     * that will ever emit. If backpressure only allowed the first 1,000 to be sent, we would hang and never receive an event.
     *
     * Thus, we must allow all Observables to be sent. The ScalarSynchronousObservable use case is an exception to this since
     * we can grab the value synchronously.
     *
     * @throws InterruptedException
     * 		if the await is interrupted
     */
    @Test(timeout = 5000)
    public void testBackpressureBothUpstreamAndDownstreamWithRegularObservables() throws InterruptedException {
        final AtomicInteger generated1 = new AtomicInteger();
        Observable<Observable<Integer>> o1 = createInfiniteObservable(generated1).map(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return io.reactivex.Observable.just(1, 2, 3);
            }
        });
        TestObserver<Integer> to = new TestObserver<Integer>() {
            int i;

            @Override
            public void onNext(Integer t) {
                if (((i)++) < 400) {
                    try {
                        // force a slow consumer
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // System.err.println("TestObserver received => " + t + "  on thread " + Thread.currentThread());
                super.onNext(t);
            }
        };
        Observable.merge(o1).observeOn(Schedulers.computation()).take(((Flowable.bufferSize()) * 2)).subscribe(to);
        to.awaitTerminalEvent();
        if ((to.errors().size()) > 0) {
            to.errors().get(0).printStackTrace();
        }
        to.assertNoErrors();
        System.out.println(("Generated 1: " + (generated1.get())));
        System.err.println(to.values());
        System.out.println("done1 testBackpressureBothUpstreamAndDownstreamWithRegularObservables ");
        Assert.assertEquals(((Flowable.bufferSize()) * 2), to.values().size());
        System.out.println("done2 testBackpressureBothUpstreamAndDownstreamWithRegularObservables ");
        // we can't restrict this ... see comment above
        // assertTrue(generated1.get() >= Observable.bufferSize() && generated1.get() <= Observable.bufferSize() * 4);
    }

    @Test
    public void merge1AsyncStreamOf1() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNAsyncStreamsOfN(1, 1).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(1, to.values().size());
    }

    @Test
    public void merge1AsyncStreamOf1000() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNAsyncStreamsOfN(1, 1000).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(1000, to.values().size());
    }

    @Test
    public void merge10AsyncStreamOf1000() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNAsyncStreamsOfN(10, 1000).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(10000, to.values().size());
    }

    @Test
    public void merge1000AsyncStreamOf1000() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNAsyncStreamsOfN(1000, 1000).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(1000000, to.values().size());
    }

    @Test
    public void merge2000AsyncStreamOf100() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNAsyncStreamsOfN(2000, 100).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(200000, to.values().size());
    }

    @Test
    public void merge100AsyncStreamOf1() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNAsyncStreamsOfN(100, 1).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(100, to.values().size());
    }

    @Test
    public void merge1SyncStreamOf1() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNSyncStreamsOfN(1, 1).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(1, to.values().size());
    }

    @Test
    public void merge1SyncStreamOf1000000() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNSyncStreamsOfN(1, 1000000).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(1000000, to.values().size());
    }

    @Test
    public void merge1000SyncStreamOf1000() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNSyncStreamsOfN(1000, 1000).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(1000000, to.values().size());
    }

    @Test
    public void merge10000SyncStreamOf10() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNSyncStreamsOfN(10000, 10).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(100000, to.values().size());
    }

    @Test
    public void merge1000000SyncStreamOf1() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        mergeNSyncStreamsOfN(1000000, 1).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(1000000, to.values().size());
    }

    @Test
    public void mergeManyAsyncSingle() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable<Observable<Integer>> os = range(1, 10000).map(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(final Integer i) {
                return io.reactivex.Observable.unsafeCreate(new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(Observer<? extends Integer> observer) {
                        observer.onSubscribe(Disposables.empty());
                        if (i < 500) {
                            try {
                                Thread.sleep(1);
                            } catch ( e) {
                                e.printStackTrace();
                            }
                        }
                        observer.onNext(i);
                        observer.onComplete();
                    }
                }).subscribeOn(Schedulers.computation()).cache();
            }
        });
        Observable.merge(os).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(10000, to.values().size());
    }

    Function<Integer, Observable<Integer>> toScalar = new Function<Integer, Observable<Integer>>() {
        @Override
        public Observable<Integer> apply(Integer v) {
            return io.reactivex.Observable.just(v);
        }
    };

    Function<Integer, Observable<Integer>> toHiddenScalar = new Function<Integer, Observable<Integer>>() {
        @Override
        public Observable<Integer> apply(Integer t) {
            return io.reactivex.Observable.just(t).hide();
        }
    };

    @Test
    public void testFastMergeFullScalar() {
        runMerge(toScalar, new TestObserver<Integer>());
    }

    @Test
    public void testFastMergeHiddenScalar() {
        runMerge(toHiddenScalar, new TestObserver<Integer>());
    }

    @Test
    public void testSlowMergeFullScalar() {
        for (final int req : new int[]{ 16, 32, 64, 128, 256 }) {
            TestObserver<Integer> to = new TestObserver<Integer>() {
                int remaining = req;

                @Override
                public void onNext(Integer t) {
                    super.onNext(t);
                    if ((--(remaining)) == 0) {
                        remaining = req;
                    }
                }
            };
            runMerge(toScalar, to);
        }
    }

    @Test
    public void testSlowMergeHiddenScalar() {
        for (final int req : new int[]{ 16, 32, 64, 128, 256 }) {
            TestObserver<Integer> to = new TestObserver<Integer>() {
                int remaining = req;

                @Override
                public void onNext(Integer t) {
                    super.onNext(t);
                    if ((--(remaining)) == 0) {
                        remaining = req;
                    }
                }
            };
            runMerge(toHiddenScalar, to);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mergeArray() {
        Observable.mergeArray(just(1), just(2)).test().assertResult(1, 2);
    }

    @Test
    public void mergeErrors() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Observable<Integer> source1 = error(new TestException("First"));
            Observable<Integer> source2 = error(new TestException("Second"));
            Observable.merge(source1, source2).test().assertFailureAndMessage(TestException.class, "First");
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

