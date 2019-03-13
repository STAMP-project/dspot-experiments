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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.Action;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableConcatTest {
    @Test
    public void testConcat() {
        Observer<String> observer = mockObserver();
        final String[] o = new String[]{ "1", "3", "5", "7" };
        final String[] e = new String[]{ "2", "4", "6" };
        final Observable<String> odds = fromArray(o);
        final Observable<String> even = fromArray(e);
        Observable<String> concat = Observable.concat(odds, even);
        concat.subscribe(observer);
        Mockito.verify(observer, Mockito.times(7)).onNext(ArgumentMatchers.anyString());
    }

    @Test
    public void testConcatWithList() {
        Observer<String> observer = mockObserver();
        final String[] o = new String[]{ "1", "3", "5", "7" };
        final String[] e = new String[]{ "2", "4", "6" };
        final Observable<String> odds = fromArray(o);
        final Observable<String> even = fromArray(e);
        final List<Observable<String>> list = new java.util.ArrayList<Observable<String>>();
        list.add(odds);
        list.add(even);
        Observable<String> concat = Observable.concat(Observable.fromIterable(list));
        concat.subscribe(observer);
        Mockito.verify(observer, Mockito.times(7)).onNext(ArgumentMatchers.anyString());
    }

    @Test
    public void testConcatObservableOfObservables() {
        Observer<String> observer = mockObserver();
        final String[] o = new String[]{ "1", "3", "5", "7" };
        final String[] e = new String[]{ "2", "4", "6" };
        final Observable<String> odds = fromArray(o);
        final Observable<String> even = fromArray(e);
        Observable<Observable<String>> observableOfObservables = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? extends Observable<String>> observer) {
                observer.onSubscribe(Disposables.empty());
                // simulate what would happen in an Observable
                observer.onNext(odds);
                observer.onNext(even);
                observer.onComplete();
            }
        });
        Observable<String> concat = Observable.concat(observableOfObservables);
        concat.subscribe(observer);
        Mockito.verify(observer, Mockito.times(7)).onNext(ArgumentMatchers.anyString());
    }

    /**
     * Simple concat of 2 asynchronous observables ensuring it emits in correct order.
     */
    @Test
    public void testSimpleAsyncConcat() {
        Observer<String> observer = mockObserver();
        ObservableConcatTest.TestObservable<String> o1 = new ObservableConcatTest.TestObservable<String>("one", "two", "three");
        ObservableConcatTest.TestObservable<String> o2 = new ObservableConcatTest.TestObservable<String>("four", "five", "six");
        Observable.concat(Observable.unsafeCreate(o1), Observable.unsafeCreate(o2)).subscribe(observer);
        try {
            // wait for async observables to complete
            o1.t.join();
            o2.t.join();
        } catch (Throwable e) {
            throw new RuntimeException("failed waiting on threads");
        }
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        inOrder.verify(observer, Mockito.times(1)).onNext("four");
        inOrder.verify(observer, Mockito.times(1)).onNext("five");
        inOrder.verify(observer, Mockito.times(1)).onNext("six");
    }

    @Test
    public void testNestedAsyncConcatLoop() throws Throwable {
        for (int i = 0; i < 500; i++) {
            if ((i % 10) == 0) {
                System.out.println(("testNestedAsyncConcat >> " + i));
            }
            testNestedAsyncConcat();
        }
    }

    /**
     * Test an async Observable that emits more async Observables.
     *
     * @throws InterruptedException
     * 		if the test is interrupted
     */
    @Test
    public void testNestedAsyncConcat() throws InterruptedException {
        Observer<String> observer = mockObserver();
        final ObservableConcatTest.TestObservable<String> o1 = new ObservableConcatTest.TestObservable<String>("one", "two", "three");
        final ObservableConcatTest.TestObservable<String> o2 = new ObservableConcatTest.TestObservable<String>("four", "five", "six");
        final ObservableConcatTest.TestObservable<String> o3 = new ObservableConcatTest.TestObservable<String>("seven", "eight", "nine");
        final CountDownLatch allowThird = new CountDownLatch(1);
        final AtomicReference<Thread> parent = new AtomicReference<Thread>();
        final CountDownLatch parentHasStarted = new CountDownLatch(1);
        final CountDownLatch parentHasFinished = new CountDownLatch(1);
        Observable<Observable<String>> observableOfObservables = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(final Observer<? extends Observable<String>> observer) {
                final Disposable d = Disposables.empty();
                observer.onSubscribe(d);
                parent.set(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // emit first
                            if (!(d.isDisposed())) {
                                System.out.println("Emit o1");
                                observer.onNext(io.reactivex.Observable.unsafeCreate(o1));
                            }
                            // emit second
                            if (!(d.isDisposed())) {
                                System.out.println("Emit o2");
                                observer.onNext(io.reactivex.Observable.unsafeCreate(o2));
                            }
                            // wait until sometime later and emit third
                            try {
                                allowThird.await();
                            } catch ( e) {
                                observer.onError(e);
                            }
                            if (!(d.isDisposed())) {
                                System.out.println("Emit o3");
                                observer.onNext(io.reactivex.Observable.unsafeCreate(o3));
                            }
                        } catch ( e) {
                            observer.onError(e);
                        } finally {
                            System.out.println("Done parent Observable");
                            observer.onComplete();
                            parentHasFinished.countDown();
                        }
                    }
                }));
                parent.get().start();
                parentHasStarted.countDown();
            }
        });
        Observable.concat(observableOfObservables).subscribe(observer);
        // wait for parent to start
        parentHasStarted.await();
        try {
            // wait for first 2 async observables to complete
            System.out.println("Thread1 is starting ... waiting for it to complete ...");
            o1.waitForThreadDone();
            System.out.println("Thread2 is starting ... waiting for it to complete ...");
            o2.waitForThreadDone();
        } catch (Throwable e) {
            throw new RuntimeException("failed waiting on threads", e);
        }
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        inOrder.verify(observer, Mockito.times(1)).onNext("four");
        inOrder.verify(observer, Mockito.times(1)).onNext("five");
        inOrder.verify(observer, Mockito.times(1)).onNext("six");
        // we shouldn't have the following 3 yet
        inOrder.verify(observer, Mockito.never()).onNext("seven");
        inOrder.verify(observer, Mockito.never()).onNext("eight");
        inOrder.verify(observer, Mockito.never()).onNext("nine");
        // we should not be completed yet
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // now allow the third
        allowThird.countDown();
        try {
            // wait for 3rd to complete
            o3.waitForThreadDone();
        } catch (Throwable e) {
            throw new RuntimeException("failed waiting on threads", e);
        }
        try {
            // wait for the parent to complete
            if (!(parentHasFinished.await(5, TimeUnit.SECONDS))) {
                Assert.fail("Parent didn't finish within the time limit");
            }
        } catch (Throwable e) {
            throw new RuntimeException("failed waiting on threads", e);
        }
        inOrder.verify(observer, Mockito.times(1)).onNext("seven");
        inOrder.verify(observer, Mockito.times(1)).onNext("eight");
        inOrder.verify(observer, Mockito.times(1)).onNext("nine");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testBlockedObservableOfObservables() {
        Observer<String> observer = mockObserver();
        final String[] o = new String[]{ "1", "3", "5", "7" };
        final String[] e = new String[]{ "2", "4", "6" };
        final Observable<String> odds = fromArray(o);
        final Observable<String> even = fromArray(e);
        final CountDownLatch callOnce = new CountDownLatch(1);
        final CountDownLatch okToContinue = new CountDownLatch(1);
        @SuppressWarnings("unchecked")
        ObservableConcatTest.TestObservable<Observable<String>> observableOfObservables = new ObservableConcatTest.TestObservable<Observable<String>>(callOnce, okToContinue, odds, even);
        Observable<String> concatF = Observable.concat(Observable.unsafeCreate(observableOfObservables));
        concatF.subscribe(observer);
        try {
            // Block main thread to allow observables to serve up o1.
            callOnce.await();
        } catch (Throwable ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
        // The concated Observable should have served up all of the odds.
        Mockito.verify(observer, Mockito.times(1)).onNext("1");
        Mockito.verify(observer, Mockito.times(1)).onNext("3");
        Mockito.verify(observer, Mockito.times(1)).onNext("5");
        Mockito.verify(observer, Mockito.times(1)).onNext("7");
        try {
            // unblock observables so it can serve up o2 and complete
            okToContinue.countDown();
            observableOfObservables.t.join();
        } catch (Throwable ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
        }
        // The concatenated Observable should now have served up all the evens.
        Mockito.verify(observer, Mockito.times(1)).onNext("2");
        Mockito.verify(observer, Mockito.times(1)).onNext("4");
        Mockito.verify(observer, Mockito.times(1)).onNext("6");
    }

    @Test
    public void testConcatConcurrentWithInfinity() {
        final ObservableConcatTest.TestObservable<String> w1 = new ObservableConcatTest.TestObservable<String>("one", "two", "three");
        // This Observable will send "hello" MAX_VALUE time.
        final ObservableConcatTest.TestObservable<String> w2 = new ObservableConcatTest.TestObservable<String>("hello", Integer.MAX_VALUE);
        Observer<String> observer = mockObserver();
        @SuppressWarnings("unchecked")
        ObservableConcatTest.TestObservable<Observable<String>> observableOfObservables = new ObservableConcatTest.TestObservable<Observable<String>>(Observable.unsafeCreate(w1), Observable.unsafeCreate(w2));
        Observable<String> concatF = Observable.concat(Observable.unsafeCreate(observableOfObservables));
        concatF.take(50).subscribe(observer);
        // Wait for the thread to start up.
        try {
            w1.waitForThreadDone();
            w2.waitForThreadDone();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        inOrder.verify(observer, Mockito.times(47)).onNext("hello");
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testConcatNonBlockingObservables() {
        final CountDownLatch okToContinueW1 = new CountDownLatch(1);
        final CountDownLatch okToContinueW2 = new CountDownLatch(1);
        final ObservableConcatTest.TestObservable<String> w1 = new ObservableConcatTest.TestObservable<String>(null, okToContinueW1, "one", "two", "three");
        final ObservableConcatTest.TestObservable<String> w2 = new ObservableConcatTest.TestObservable<String>(null, okToContinueW2, "four", "five", "six");
        Observer<String> observer = mockObserver();
        Observable<Observable<String>> observableOfObservables = Observable.unsafeCreate(new ObservableSource<Observable<String>>() {
            @Override
            public void subscribe(Observer<? extends Observable<String>> observer) {
                observer.onSubscribe(Disposables.empty());
                // simulate what would happen in an Observable
                observer.onNext(io.reactivex.Observable.unsafeCreate(w1));
                observer.onNext(io.reactivex.Observable.unsafeCreate(w2));
                observer.onComplete();
            }
        });
        Observable<String> concat = Observable.concat(observableOfObservables);
        concat.subscribe(observer);
        Mockito.verify(observer, Mockito.times(0)).onComplete();
        try {
            // release both threads
            okToContinueW1.countDown();
            okToContinueW2.countDown();
            // wait for both to finish
            w1.t.join();
            w2.t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        inOrder.verify(observer, Mockito.times(1)).onNext("four");
        inOrder.verify(observer, Mockito.times(1)).onNext("five");
        inOrder.verify(observer, Mockito.times(1)).onNext("six");
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    /**
     * Test unsubscribing the concatenated Observable in a single thread.
     */
    @Test
    public void testConcatUnsubscribe() {
        final CountDownLatch callOnce = new CountDownLatch(1);
        final CountDownLatch okToContinue = new CountDownLatch(1);
        final ObservableConcatTest.TestObservable<String> w1 = new ObservableConcatTest.TestObservable<String>("one", "two", "three");
        final ObservableConcatTest.TestObservable<String> w2 = new ObservableConcatTest.TestObservable<String>(callOnce, okToContinue, "four", "five", "six");
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        final Observable<String> concat = Observable.concat(Observable.unsafeCreate(w1), Observable.unsafeCreate(w2));
        try {
            // Subscribe
            concat.subscribe(to);
            // Block main thread to allow Observable "w1" to complete and Observable "w2" to call onNext once.
            callOnce.await();
            // Unsubcribe
            to.dispose();
            // Unblock the Observable to continue.
            okToContinue.countDown();
            w1.t.join();
            w2.t.join();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        inOrder.verify(observer, Mockito.times(1)).onNext("four");
        inOrder.verify(observer, Mockito.never()).onNext("five");
        inOrder.verify(observer, Mockito.never()).onNext("six");
        inOrder.verify(observer, Mockito.never()).onComplete();
    }

    /**
     * All observables will be running in different threads so subscribe() is unblocked. CountDownLatch is only used in order to call unsubscribe() in a predictable manner.
     */
    @Test
    public void testConcatUnsubscribeConcurrent() {
        final CountDownLatch callOnce = new CountDownLatch(1);
        final CountDownLatch okToContinue = new CountDownLatch(1);
        final ObservableConcatTest.TestObservable<String> w1 = new ObservableConcatTest.TestObservable<String>("one", "two", "three");
        final ObservableConcatTest.TestObservable<String> w2 = new ObservableConcatTest.TestObservable<String>(callOnce, okToContinue, "four", "five", "six");
        Observer<String> observer = mockObserver();
        TestObserver<String> to = new TestObserver<String>(observer);
        @SuppressWarnings("unchecked")
        ObservableConcatTest.TestObservable<Observable<String>> observableOfObservables = new ObservableConcatTest.TestObservable<Observable<String>>(Observable.unsafeCreate(w1), Observable.unsafeCreate(w2));
        Observable<String> concatF = Observable.concat(Observable.unsafeCreate(observableOfObservables));
        concatF.subscribe(to);
        try {
            // Block main thread to allow Observable "w1" to complete and Observable "w2" to call onNext exactly once.
            callOnce.await();
            // "four" from w2 has been processed by onNext()
            to.dispose();
            // "five" and "six" will NOT be processed by onNext()
            // Unblock the Observable to continue.
            okToContinue.countDown();
            w1.t.join();
            w2.t.join();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        inOrder.verify(observer, Mockito.times(1)).onNext("four");
        inOrder.verify(observer, Mockito.never()).onNext("five");
        inOrder.verify(observer, Mockito.never()).onNext("six");
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    static class TestObservable<T> implements ObservableSource<T> {
        private final io.reactivex.disposables.Disposable upstream = new Disposable() {
            @Override
            public void dispose() {
                subscribed = false;
            }

            @Override
            public boolean isDisposed() {
                return subscribed;
            }
        };

        private final List<T> values;

        private Thread t;

        private int count;

        private volatile boolean subscribed = true;

        private final CountDownLatch once;

        private final CountDownLatch okToContinue;

        private final CountDownLatch threadHasStarted = new CountDownLatch(1);

        private final T seed;

        private final int size;

        TestObservable(T... values) {
            this(null, null, values);
        }

        TestObservable(CountDownLatch once, CountDownLatch okToContinue, T... values) {
            this.values = Arrays.asList(values);
            this.size = this.values.size();
            this.once = once;
            this.okToContinue = okToContinue;
            this.seed = null;
        }

        TestObservable(T seed, int size) {
            values = null;
            once = null;
            okToContinue = null;
            this.seed = seed;
            this.size = size;
        }

        @Override
        public void subscribe(final Observer<? super T> observer) {
            observer.onSubscribe(upstream);
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (((count) < (size)) && (subscribed)) {
                            if (null != (values)) {
                                observer.onNext(values.get(count));
                            } else {
                                observer.onNext(seed);
                            }
                            (count)++;
                            // Unblock the main thread to call unsubscribe.
                            if (null != (once)) {
                                once.countDown();
                            }
                            // Block until the main thread has called unsubscribe.
                            if (null != (okToContinue)) {
                                okToContinue.await(5, TimeUnit.SECONDS);
                            }
                        } 
                        if (subscribed) {
                            observer.onComplete();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Assert.fail(e.getMessage());
                    }
                }
            });
            t.start();
            threadHasStarted.countDown();
        }

        void waitForThreadDone() throws InterruptedException {
            threadHasStarted.await();
            t.join();
        }
    }

    @Test
    public void testMultipleObservers() {
        Observer<Object> o1 = mockObserver();
        Observer<Object> o2 = mockObserver();
        TestScheduler s = new TestScheduler();
        Observable<Long> timer = Observable.interval(500, TimeUnit.MILLISECONDS, s).take(2);
        Observable<Long> o = Observable.concat(timer, timer);
        o.subscribe(o1);
        o.subscribe(o2);
        InOrder inOrder1 = Mockito.inOrder(o1);
        InOrder inOrder2 = Mockito.inOrder(o2);
        s.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        inOrder1.verify(o1, Mockito.times(1)).onNext(0L);
        inOrder2.verify(o2, Mockito.times(1)).onNext(0L);
        s.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        inOrder1.verify(o1, Mockito.times(1)).onNext(1L);
        inOrder2.verify(o2, Mockito.times(1)).onNext(1L);
        s.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        inOrder1.verify(o1, Mockito.times(1)).onNext(0L);
        inOrder2.verify(o2, Mockito.times(1)).onNext(0L);
        s.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        inOrder1.verify(o1, Mockito.times(1)).onNext(1L);
        inOrder2.verify(o2, Mockito.times(1)).onNext(1L);
        inOrder1.verify(o1, Mockito.times(1)).onComplete();
        inOrder2.verify(o2, Mockito.times(1)).onComplete();
        Mockito.verify(o1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void concatVeryLongObservableOfObservables() {
        final int n = 10000;
        Observable<Observable<Integer>> source = range(0, n).map(new io.reactivex.functions.Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer v) {
                return io.reactivex.Observable.just(v);
            }
        });
        Single<List<Integer>> result = Observable.concat(source).toList();
        SingleObserver<List<Integer>> o = TestHelper.mockSingleObserver();
        InOrder inOrder = Mockito.inOrder(o);
        result.subscribe(o);
        List<Integer> list = new java.util.ArrayList<Integer>(n);
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        inOrder.verify(o).onSuccess(list);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void concatVeryLongObservableOfObservablesTakeHalf() {
        final int n = 10000;
        Observable<Observable<Integer>> source = range(0, n).map(new io.reactivex.functions.Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer v) {
                return io.reactivex.Observable.just(v);
            }
        });
        Single<List<Integer>> result = Observable.concat(source).take((n / 2)).toList();
        SingleObserver<List<Integer>> o = TestHelper.mockSingleObserver();
        InOrder inOrder = Mockito.inOrder(o);
        result.subscribe(o);
        List<Integer> list = new java.util.ArrayList<Integer>(n);
        for (int i = 0; i < (n / 2); i++) {
            list.add(i);
        }
        inOrder.verify(o).onSuccess(list);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testConcatOuterBackpressure() {
        Assert.assertEquals(1, ((int) (<Integer>empty().concatWith(Observable.just(1)).take(1).blockingSingle())));
    }

    // https://github.com/ReactiveX/RxJava/issues/1818
    @Test
    public void testConcatWithNonCompliantSourceDoubleOnComplete() {
        Observable<String> o = unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onNext("hello");
                observer.onComplete();
                observer.onComplete();
            }
        });
        TestObserver<String> to = new TestObserver<String>();
        Observable.concat(o, o).subscribe(to);
        to.awaitTerminalEvent(500, TimeUnit.MILLISECONDS);
        to.assertTerminated();
        to.assertNoErrors();
        to.assertValues("hello", "hello");
    }

    @Test(timeout = 30000)
    public void testIssue2890NoStackoverflow() throws InterruptedException {
        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final Scheduler sch = Schedulers.from(executor);
        io.reactivex.functions.Function<Integer, Observable<Integer>> func = new io.reactivex.functions.Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t) {
                Observable<Integer> o = io.reactivex.Observable.just(t).subscribeOn(sch);
                Subject<Integer> subject = UnicastSubject.create();
                o.subscribe(subject);
                return subject;
            }
        };
        int n = 5000;
        final AtomicInteger counter = new AtomicInteger();
        range(1, n).concatMap(func).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                // Consume after sleep for 1 ms
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // ignored
                }
                if (((counter.getAndIncrement()) % 100) == 0) {
                    System.out.println(("testIssue2890NoStackoverflow -> " + (counter.get())));
                }
            }

            @Override
            public void onComplete() {
                executor.shutdown();
            }

            @Override
            public void onError(Throwable e) {
                executor.shutdown();
            }
        });
        executor.awaitTermination(20000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(n, counter.get());
    }

    // (timeout = 100000)
    @Test
    public void concatMapRangeAsyncLoopIssue2876() {
        final long durationSeconds = 2;
        final long startTime = System.currentTimeMillis();
        for (int i = 0; ; i++) {
            // only run this for a max of ten seconds
            if (((System.currentTimeMillis()) - startTime) > (TimeUnit.SECONDS.toMillis(durationSeconds))) {
                return;
            }
            if ((i % 1000) == 0) {
                System.out.println(("concatMapRangeAsyncLoop > " + i));
            }
            TestObserver<Integer> to = new TestObserver<Integer>();
            range(0, 1000).concatMap(new io.reactivex.functions.Function<Integer, Observable<Integer>>() {
                @Override
                public Observable<Integer> apply(Integer t) {
                    return io.reactivex.Observable.fromIterable(Arrays.asList(t));
                }
            }).observeOn(Schedulers.computation()).subscribe(to);
            to.awaitTerminalEvent(2500, TimeUnit.MILLISECONDS);
            to.assertTerminated();
            to.assertNoErrors();
            Assert.assertEquals(1000, to.valueCount());
            Assert.assertEquals(((Integer) (999)), to.values().get(999));
        }
    }

    @Test
    public void concat3() {
        Observable.concat(Observable.just(1), Observable.just(2), Observable.just(3)).test().assertResult(1, 2, 3);
    }

    @Test
    public void concat4() {
        Observable.concat(Observable.just(1), Observable.just(2), Observable.just(3), Observable.just(4)).test().assertResult(1, 2, 3, 4);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatArrayDelayError() {
        Observable.concatArrayDelayError(Observable.just(1), Observable.just(2), Observable.just(3), Observable.just(4)).test().assertResult(1, 2, 3, 4);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatArrayDelayErrorWithError() {
        Observable.concatArrayDelayError(Observable.just(1), Observable.just(2), Observable.just(3).concatWith(<Integer>error(new TestException())), Observable.just(4)).test().assertFailure(TestException.class, 1, 2, 3, 4);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatIterableDelayError() {
        Observable.concatDelayError(Arrays.asList(Observable.just(1), Observable.just(2), Observable.just(3), Observable.just(4))).test().assertResult(1, 2, 3, 4);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatIterableDelayErrorWithError() {
        Observable.concatDelayError(Arrays.asList(Observable.just(1), Observable.just(2), Observable.just(3).concatWith(<Integer>error(new TestException())), Observable.just(4))).test().assertFailure(TestException.class, 1, 2, 3, 4);
    }

    @Test
    public void concatObservableDelayError() {
        Observable.concatDelayError(Observable.just(Observable.just(1), Observable.just(2), Observable.just(3), Observable.just(4))).test().assertResult(1, 2, 3, 4);
    }

    @Test
    public void concatObservableDelayErrorWithError() {
        Observable.concatDelayError(Observable.just(Observable.just(1), Observable.just(2), Observable.just(3).concatWith(<Integer>error(new TestException())), Observable.just(4))).test().assertFailure(TestException.class, 1, 2, 3, 4);
    }

    @Test
    public void concatObservableDelayErrorBoundary() {
        Observable.concatDelayError(Observable.just(Observable.just(1), Observable.just(2), Observable.just(3).concatWith(<Integer>error(new TestException())), Observable.just(4)), 2, false).test().assertFailure(TestException.class, 1, 2, 3);
    }

    @Test
    public void concatObservableDelayErrorTillEnd() {
        Observable.concatDelayError(Observable.just(Observable.just(1), Observable.just(2), Observable.just(3).concatWith(<Integer>error(new TestException())), Observable.just(4)), 2, true).test().assertFailure(TestException.class, 1, 2, 3, 4);
    }

    @Test
    public void concatMapDelayError() {
        Observable.just(Observable.just(1), Observable.just(2)).concatMapDelayError(io.reactivex.internal.functions.Functions.<Observable<Integer>>identity()).test().assertResult(1, 2);
    }

    @Test
    public void concatMapDelayErrorWithError() {
        Observable.just(Observable.just(1).concatWith(<Integer>error(new TestException())), Observable.just(2)).concatMapDelayError(io.reactivex.internal.functions.Functions.<Observable<Integer>>identity()).test().assertFailure(TestException.class, 1, 2);
    }

    @Test
    public void concatMapIterableBufferSize() {
        Observable.just(1, 2).concatMapIterable(new io.reactivex.functions.Function<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> apply(Integer v) throws Exception {
                return Arrays.asList(1, 2, 3, 4, 5);
            }
        }, 1).test().assertResult(1, 2, 3, 4, 5, 1, 2, 3, 4, 5);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void emptyArray() {
        Assert.assertSame(empty(), Observable.concatArrayDelayError());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void singleElementArray() {
        Assert.assertSame(never(), Observable.concatArrayDelayError(never()));
    }

    @Test
    public void concatMapDelayErrorEmptySource() {
        Assert.assertSame(empty(), <Object>empty().concatMapDelayError(new io.reactivex.functions.Function<Object, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Object v) throws Exception {
                return just(1);
            }
        }, 16, true));
    }

    @Test
    public void concatMapDelayErrorJustSource() {
        Observable.just(0).concatMapDelayError(new io.reactivex.functions.Function<Object, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Object v) throws Exception {
                return just(1);
            }
        }, 16, true).test().assertResult(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatArrayEmpty() {
        Assert.assertSame(empty(), concatArray());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void concatArraySingleElement() {
        Assert.assertSame(never(), Observable.concatArray(never()));
    }

    @Test
    public void concatMapErrorEmptySource() {
        Assert.assertSame(empty(), <Object>empty().concatMap(new io.reactivex.functions.Function<Object, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Object v) throws Exception {
                return just(1);
            }
        }, 16));
    }

    @Test
    public void concatMapJustSource() {
        Observable.just(0).concatMap(new io.reactivex.functions.Function<Object, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Object v) throws Exception {
                return just(1);
            }
        }, 16).test().assertResult(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noSubsequentSubscription() {
        final int[] calls = new int[]{ 0 };
        Observable<Integer> source = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> s) throws Exception {
                (calls[0])++;
                s.onNext(1);
                s.onComplete();
            }
        });
        Observable.concatArray(source, source).firstElement().test().assertResult(1);
        Assert.assertEquals(1, calls[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noSubsequentSubscriptionDelayError() {
        final int[] calls = new int[]{ 0 };
        Observable<Integer> source = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> s) throws Exception {
                (calls[0])++;
                s.onNext(1);
                s.onComplete();
            }
        });
        Observable.concatArrayDelayError(source, source).firstElement().test().assertResult(1);
        Assert.assertEquals(1, calls[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noSubsequentSubscriptionIterable() {
        final int[] calls = new int[]{ 0 };
        Observable<Integer> source = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> s) throws Exception {
                (calls[0])++;
                s.onNext(1);
                s.onComplete();
            }
        });
        Observable.concat(Arrays.asList(source, source)).firstElement().test().assertResult(1);
        Assert.assertEquals(1, calls[0]);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noSubsequentSubscriptionDelayErrorIterable() {
        final int[] calls = new int[]{ 0 };
        Observable<Integer> source = create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> s) throws Exception {
                (calls[0])++;
                s.onNext(1);
                s.onComplete();
            }
        });
        Observable.concatDelayError(Arrays.asList(source, source)).firstElement().test().assertResult(1);
        Assert.assertEquals(1, calls[0]);
    }

    @Test
    public void concatReportsDisposedOnComplete() {
        final Disposable[] disposable = new io.reactivex.disposables.Disposable[]{ null };
        Observable.concat(Observable.just(1), Observable.just(2)).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable[0] = d;
            }

            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertTrue(disposable[0].isDisposed());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void concatReportsDisposedOnCompleteDelayError() {
        final Disposable[] disposable = new io.reactivex.disposables.Disposable[]{ null };
        Observable.concatArrayDelayError(Observable.just(1), Observable.just(2)).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable[0] = d;
            }

            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertTrue(disposable[0].isDisposed());
    }

    @Test
    public void concatReportsDisposedOnError() {
        final Disposable[] disposable = new io.reactivex.disposables.Disposable[]{ null };
        Observable.concat(Observable.just(1), <Integer>error(new TestException())).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable[0] = d;
            }

            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertTrue(disposable[0].isDisposed());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void concatReportsDisposedOnErrorDelayError() {
        final Disposable[] disposable = new io.reactivex.disposables.Disposable[]{ null };
        Observable.concatArrayDelayError(Observable.just(1), <Integer>error(new TestException())).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable[0] = d;
            }

            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertTrue(disposable[0].isDisposed());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noCancelPreviousArray() {
        final AtomicInteger counter = new AtomicInteger();
        Observable<Integer> source = Observable.just(1).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        Observable.concatArray(source, source, source, source, source).test().assertResult(1, 1, 1, 1, 1);
        Assert.assertEquals(0, counter.get());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noCancelPreviousIterable() {
        final AtomicInteger counter = new AtomicInteger();
        Observable<Integer> source = Observable.just(1).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        Observable.concat(Arrays.asList(source, source, source, source, source)).test().assertResult(1, 1, 1, 1, 1);
        Assert.assertEquals(0, counter.get());
    }
}

