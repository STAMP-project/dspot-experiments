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
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableRetryTest {
    @Test
    public void iterativeBackoff() {
        Observer<String> consumer = mockObserver();
        Observable<String> producer = Observable.unsafeCreate(new ObservableSource<String>() {
            private AtomicInteger count = new AtomicInteger(4);

            long last = System.currentTimeMillis();

            @Override
            public void subscribe(Observer<? super String> t1) {
                t1.onSubscribe(Disposables.empty());
                System.out.println((((count.get()) + " @ ") + (String.valueOf(((last) - (System.currentTimeMillis()))))));
                last = System.currentTimeMillis();
                if ((count.getAndDecrement()) == 0) {
                    t1.onNext("hello");
                    t1.onComplete();
                } else {
                    t1.onError(new RuntimeException());
                }
            }
        });
        TestObserver<String> to = new TestObserver<String>(consumer);
        producer.retryWhen(new Function<Observable<? extends Throwable>, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Observable<? extends Throwable> attempts) {
                // Worker w = Schedulers.computation().createWorker();
                return attempts.map(new Function<Throwable, io.reactivex.internal.operators.observable.Tuple>() {
                    @Override
                    public io.reactivex.internal.operators.observable.Tuple apply(Throwable n) {
                        return new io.reactivex.internal.operators.observable.Tuple(new Long(1), n);
                    }
                }).scan(new BiFunction<io.reactivex.internal.operators.observable.Tuple, io.reactivex.internal.operators.observable.Tuple, io.reactivex.internal.operators.observable.Tuple>() {
                    @Override
                    public io.reactivex.internal.operators.observable.Tuple apply(io.reactivex.internal.operators.observable.Tuple t, io.reactivex.internal.operators.observable.Tuple n) {
                        return new io.reactivex.internal.operators.observable.Tuple((t.count + n.count), n.n);
                    }
                }).flatMap(new Function<io.reactivex.internal.operators.observable.Tuple, Observable<Long>>() {
                    @Override
                    public Observable<Long> apply(io.reactivex.internal.operators.observable.Tuple t) {
                        System.out.println(("Retry # " + t.count));
                        return t.count > 20 ? io.reactivex.Observable.<Long>error(t.n) : io.reactivex.Observable.timer((t.count * 1L), TimeUnit.MILLISECONDS);
                    }
                }).cast(.class);
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        InOrder inOrder = Mockito.inOrder(consumer);
        inOrder.verify(consumer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(consumer, Mockito.times(1)).onNext("hello");
        inOrder.verify(consumer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    public static class Tuple {
        Long count;

        Throwable n;

        Tuple(Long c, Throwable n) {
            count = c;
            this.n = n;
        }
    }

    @Test
    public void testRetryIndefinitely() {
        Observer<String> observer = mockObserver();
        int numRetries = 20;
        Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(numRetries));
        origin.retry().subscribe(new TestObserver<String>(observer));
        InOrder inOrder = Mockito.inOrder(observer);
        // should show 3 attempts
        inOrder.verify(observer, Mockito.times((numRetries + 1))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(observer, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSchedulingNotificationHandler() {
        Observer<String> observer = mockObserver();
        int numRetries = 2;
        Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(numRetries));
        TestObserver<String> to = new TestObserver<String>(observer);
        origin.retryWhen(new Function<Observable<? extends Throwable>, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Observable<? extends Throwable> t1) {
                return t1.observeOn(io.reactivex.schedulers.Schedulers.computation()).map(new Function<Throwable, Object>() {
                    @Override
                    public Object apply(Throwable t1) {
                        return 1;
                    }
                }).startWith(1);
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) {
                e.printStackTrace();
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        InOrder inOrder = Mockito.inOrder(observer);
        // should show 3 attempts
        inOrder.verify(observer, Mockito.times((1 + numRetries))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(observer, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOnNextFromNotificationHandler() {
        Observer<String> observer = mockObserver();
        int numRetries = 2;
        Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(numRetries));
        origin.retryWhen(new Function<Observable<? extends Throwable>, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Observable<? extends Throwable> t1) {
                return t1.map(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable t1) {
                        return 0;
                    }
                }).startWith(0).cast(.class);
            }
        }).subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        // should show 3 attempts
        inOrder.verify(observer, Mockito.times((numRetries + 1))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(observer, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOnCompletedFromNotificationHandler() {
        Observer<String> observer = mockObserver();
        Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(1));
        TestObserver<String> to = new TestObserver<String>(observer);
        origin.retryWhen(new Function<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> apply(Observable<? extends Throwable> t1) {
                return io.reactivex.Observable.empty();
            }
        }).subscribe(to);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.notNull())));
        inOrder.verify(observer, Mockito.never()).onNext("beginningEveryTime");
        inOrder.verify(observer, Mockito.never()).onNext("onSuccessOnly");
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Exception.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testOnErrorFromNotificationHandler() {
        Observer<String> observer = mockObserver();
        Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(2));
        origin.retryWhen(new Function<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> apply(Observable<? extends Throwable> t1) {
                return io.reactivex.Observable.error(new RuntimeException());
            }
        }).subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer).onSubscribe(((Disposable) (ArgumentMatchers.notNull())));
        inOrder.verify(observer, Mockito.never()).onNext("beginningEveryTime");
        inOrder.verify(observer, Mockito.never()).onNext("onSuccessOnly");
        inOrder.verify(observer, Mockito.never()).onComplete();
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSingleSubscriptionOnFirst() throws Exception {
        final AtomicInteger inc = new AtomicInteger(0);
        ObservableSource<Integer> onSubscribe = new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observer) {
                observer.onSubscribe(Disposables.empty());
                final int emit = inc.incrementAndGet();
                observer.onNext(emit);
                observer.onComplete();
            }
        };
        int first = Observable.unsafeCreate(onSubscribe).retryWhen(new Function<Observable<? extends Throwable>, Observable<?>>() {
            @Override
            public Observable<?> apply(Observable<? extends Throwable> attempt) {
                return attempt.zipWith(io.reactivex.Observable.just(1), new BiFunction<Throwable, Integer, Void>() {
                    @Override
                    public Void apply(Throwable o, Integer integer) {
                        return null;
                    }
                });
            }
        }).blockingFirst();
        Assert.assertEquals("Observer did not receive the expected output", 1, first);
        Assert.assertEquals("Subscribe was not called once", 1, inc.get());
    }

    @Test
    public void testOriginFails() {
        Observer<String> observer = mockObserver();
        Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(1));
        origin.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext("beginningEveryTime");
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        inOrder.verify(observer, Mockito.never()).onNext("onSuccessOnly");
        inOrder.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testRetryFail() {
        int numRetries = 1;
        int numFailures = 2;
        Observer<String> observer = mockObserver();
        Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(numFailures));
        origin.retry(numRetries).subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        // should show 2 attempts (first time fail, second time (1st retry) fail)
        inOrder.verify(observer, Mockito.times((1 + numRetries))).onNext("beginningEveryTime");
        // should only retry once, fail again and emit onError
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        // no success
        inOrder.verify(observer, Mockito.never()).onNext("onSuccessOnly");
        inOrder.verify(observer, Mockito.never()).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testRetrySuccess() {
        int numFailures = 1;
        Observer<String> observer = mockObserver();
        Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(numFailures));
        origin.retry(3).subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        // should show 3 attempts
        inOrder.verify(observer, Mockito.times((1 + numFailures))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(observer, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testInfiniteRetry() {
        int numFailures = 20;
        Observer<String> observer = mockObserver();
        Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(numFailures));
        origin.retry().subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        // should show 3 attempts
        inOrder.verify(observer, Mockito.times((1 + numFailures))).onNext("beginningEveryTime");
        // should have no errors
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        inOrder.verify(observer, Mockito.times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    /* Checks in a simple and synchronous way that retry resubscribes
    after error. This test fails against 0.16.1-0.17.4, hangs on 0.17.5 and
    passes in 0.17.6 thanks to fix for issue #1027.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRetrySubscribesAgainAfterError() throws Exception {
        // record emitted values with this action
        Consumer<Integer> record = Mockito.mock(io.reactivex.Consumer.class);
        InOrder inOrder = Mockito.inOrder(record);
        // always throw an exception with this action
        Consumer<Integer> throwException = Mockito.mock(io.reactivex.Consumer.class);
        Mockito.doThrow(new RuntimeException()).when(throwException).accept(Mockito.anyInt());
        // create a retrying Observable based on a PublishSubject
        PublishSubject<Integer> subject = PublishSubject.create();
        // subscribe and ignore
        // retry on error
        // throw a RuntimeException
        // record item
        subject.doOnNext(record).doOnNext(throwException).retry().subscribe();
        inOrder.verifyNoMoreInteractions();
        subject.onNext(1);
        inOrder.verify(record).accept(1);
        subject.onNext(2);
        inOrder.verify(record).accept(2);
        subject.onNext(3);
        inOrder.verify(record).accept(3);
        inOrder.verifyNoMoreInteractions();
    }

    public static class FuncWithErrors implements ObservableSource<String> {
        private final int numFailures;

        private final AtomicInteger count = new AtomicInteger(0);

        FuncWithErrors(int count) {
            this.numFailures = count;
        }

        @Override
        public void subscribe(final Observer<? super String> o) {
            o.onSubscribe(Disposables.empty());
            o.onNext("beginningEveryTime");
            int i = count.getAndIncrement();
            if (i < (numFailures)) {
                o.onError(new RuntimeException(("forced failure: " + (i + 1))));
            } else {
                o.onNext("onSuccessOnly");
                o.onComplete();
            }
        }
    }

    @Test
    public void testUnsubscribeFromRetry() {
        PublishSubject<Integer> subject = PublishSubject.create();
        final AtomicInteger count = new AtomicInteger(0);
        Disposable sub = subject.retry().subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer n) {
                count.incrementAndGet();
            }
        });
        subject.onNext(1);
        sub.dispose();
        subject.onNext(2);
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void testRetryAllowsSubscriptionAfterAllSubscriptionsUnsubscribed() throws InterruptedException {
        final AtomicInteger subsCount = new AtomicInteger(0);
        ObservableSource<String> onSubscribe = new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                subsCount.incrementAndGet();
                observer.onSubscribe(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        subsCount.decrementAndGet();
                    }
                }));
            }
        };
        Observable<String> stream = Observable.unsafeCreate(onSubscribe);
        Observable<String> streamWithRetry = stream.retry();
        Disposable sub = streamWithRetry.subscribe();
        Assert.assertEquals(1, subsCount.get());
        sub.dispose();
        Assert.assertEquals(0, subsCount.get());
        streamWithRetry.subscribe();
        Assert.assertEquals(1, subsCount.get());
    }

    @Test
    public void testSourceObservableCallsUnsubscribe() throws InterruptedException {
        final AtomicInteger subsCount = new AtomicInteger(0);
        final TestObserver<String> to = new TestObserver<String>();
        ObservableSource<String> onSubscribe = new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                BooleanSubscription bs = new BooleanSubscription();
                // if isUnsubscribed is true that means we have a bug such as
                // https://github.com/ReactiveX/RxJava/issues/1024
                if (!(bs.isCancelled())) {
                    subsCount.incrementAndGet();
                    observer.onError(new RuntimeException("failed"));
                    // it unsubscribes the child directly
                    // this simulates various error/completion scenarios that could occur
                    // or just a source that proactively triggers cleanup
                    // FIXME can't unsubscribe child
                    // s.unsubscribe();
                    bs.cancel();
                } else {
                    observer.onError(new RuntimeException());
                }
            }
        };
        Observable.unsafeCreate(onSubscribe).retry(3).subscribe(to);
        Assert.assertEquals(4, subsCount.get());// 1 + 3 retries

    }

    @Test
    public void testSourceObservableRetry1() throws InterruptedException {
        final AtomicInteger subsCount = new AtomicInteger(0);
        final TestObserver<String> to = new TestObserver<String>();
        ObservableSource<String> onSubscribe = new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                subsCount.incrementAndGet();
                observer.onError(new RuntimeException("failed"));
            }
        };
        Observable.unsafeCreate(onSubscribe).retry(1).subscribe(to);
        Assert.assertEquals(2, subsCount.get());
    }

    @Test
    public void testSourceObservableRetry0() throws InterruptedException {
        final AtomicInteger subsCount = new AtomicInteger(0);
        final TestObserver<String> to = new TestObserver<String>();
        ObservableSource<String> onSubscribe = new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                subsCount.incrementAndGet();
                observer.onError(new RuntimeException("failed"));
            }
        };
        Observable.unsafeCreate(onSubscribe).retry(0).subscribe(to);
        Assert.assertEquals(1, subsCount.get());
    }

    static final class SlowObservable implements ObservableSource<Long> {
        final AtomicInteger efforts = new AtomicInteger(0);

        final AtomicInteger active = new AtomicInteger(0);

        final AtomicInteger maxActive = new AtomicInteger(0);

        final AtomicInteger nextBeforeFailure;

        final String context;

        private final int emitDelay;

        SlowObservable(int emitDelay, int countNext, String context) {
            this.emitDelay = emitDelay;
            this.nextBeforeFailure = new AtomicInteger(countNext);
            this.context = context;
        }

        @Override
        public void subscribe(final Observer<? super Long> observer) {
            final AtomicBoolean terminate = new AtomicBoolean(false);
            observer.onSubscribe(Disposables.fromRunnable(new Runnable() {
                @Override
                public void run() {
                    terminate.set(true);
                    active.decrementAndGet();
                }
            }));
            efforts.getAndIncrement();
            active.getAndIncrement();
            maxActive.set(Math.max(active.get(), maxActive.get()));
            final Thread thread = new Thread(context) {
                @Override
                public void run() {
                    long nr = 0;
                    try {
                        while (!(terminate.get())) {
                            Thread.sleep(emitDelay);
                            if ((nextBeforeFailure.getAndDecrement()) > 0) {
                                observer.onNext((nr++));
                            } else {
                                active.decrementAndGet();
                                observer.onError(new RuntimeException("expected-failed"));
                                break;
                            }
                        } 
                    } catch (InterruptedException t) {
                    }
                }
            };
            thread.start();
        }
    }

    /**
     * Observer for listener on seperate thread.
     */
    static final class AsyncObserver<T> extends DefaultObserver<T> {
        protected CountDownLatch latch = new CountDownLatch(1);

        protected Observer<T> target;

        /**
         * Wrap existing Observer.
         *
         * @param target
         * 		the target nbp subscriber
         */
        AsyncObserver(Observer<T> target) {
            this.target = target;
        }

        /**
         * Wait.
         */
        public void await() {
            try {
                latch.await();
            } catch (InterruptedException e) {
                Assert.fail("Test interrupted");
            }
        }

        // Observer implementation
        @Override
        public void onComplete() {
            target.onComplete();
            latch.countDown();
        }

        @Override
        public void onError(Throwable t) {
            target.onError(t);
            latch.countDown();
        }

        @Override
        public void onNext(T v) {
            target.onNext(v);
        }
    }

    @Test(timeout = 10000)
    public void testUnsubscribeAfterError() {
        Observer<Long> observer = mockObserver();
        // Observable that always fails after 100ms
        ObservableRetryTest.SlowObservable so = new ObservableRetryTest.SlowObservable(100, 0, "testUnsubscribeAfterError");
        Observable<Long> o = Observable.unsafeCreate(so).retry(5);
        ObservableRetryTest.AsyncObserver<Long> async = new ObservableRetryTest.AsyncObserver<Long>(observer);
        o.subscribe(async);
        async.await();
        InOrder inOrder = Mockito.inOrder(observer);
        // Should fail once
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(observer, Mockito.never()).onComplete();
        Assert.assertEquals("Start 6 threads, retry 5 then fail on 6", 6, so.efforts.get());
        Assert.assertEquals("Only 1 active subscription", 1, so.maxActive.get());
    }

    @Test(timeout = 10000)
    public void testTimeoutWithRetry() {
        Observer<Long> observer = mockObserver();
        // Observable that sends every 100ms (timeout fails instead)
        ObservableRetryTest.SlowObservable so = new ObservableRetryTest.SlowObservable(100, 10, "testTimeoutWithRetry");
        Observable<Long> o = Observable.unsafeCreate(so).timeout(80, TimeUnit.MILLISECONDS).retry(5);
        ObservableRetryTest.AsyncObserver<Long> async = new ObservableRetryTest.AsyncObserver<Long>(observer);
        o.subscribe(async);
        async.await();
        InOrder inOrder = Mockito.inOrder(observer);
        // Should fail once
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(observer, Mockito.never()).onComplete();
        Assert.assertEquals("Start 6 threads, retry 5 then fail on 6", 6, so.efforts.get());
    }

    // (timeout = 15000)
    @Test
    public void testRetryWithBackpressure() throws InterruptedException {
        final int NUM_LOOPS = 1;
        for (int j = 0; j < NUM_LOOPS; j++) {
            final int NUM_RETRIES = (Flowable.bufferSize()) * 2;
            for (int i = 0; i < 400; i++) {
                Observer<String> observer = mockObserver();
                Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(NUM_RETRIES));
                TestObserver<String> to = new TestObserver<String>(observer);
                origin.retry().observeOn(io.reactivex.schedulers.Schedulers.computation()).subscribe(to);
                to.awaitTerminalEvent(5, TimeUnit.SECONDS);
                InOrder inOrder = Mockito.inOrder(observer);
                // should have no errors
                Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
                // should show NUM_RETRIES attempts
                inOrder.verify(observer, Mockito.times((NUM_RETRIES + 1))).onNext("beginningEveryTime");
                // should have a single success
                inOrder.verify(observer, Mockito.times(1)).onNext("onSuccessOnly");
                // should have a single successful onComplete
                inOrder.verify(observer, Mockito.times(1)).onComplete();
                inOrder.verifyNoMoreInteractions();
            }
        }
    }

    // (timeout = 15000)
    @Test
    public void testRetryWithBackpressureParallel() throws InterruptedException {
        final int NUM_LOOPS = 1;
        final int NUM_RETRIES = (Flowable.bufferSize()) * 2;
        int ncpu = Runtime.getRuntime().availableProcessors();
        ExecutorService exec = Executors.newFixedThreadPool(Math.max((ncpu / 2), 2));
        try {
            for (int r = 0; r < NUM_LOOPS; r++) {
                if ((r % 10) == 0) {
                    System.out.println(("testRetryWithBackpressureParallelLoop -> " + r));
                }
                final AtomicInteger timeouts = new AtomicInteger();
                final Map<Integer, List<String>> data = new ConcurrentHashMap<Integer, List<String>>();
                int m = 5000;
                final CountDownLatch cdl = new CountDownLatch(m);
                for (int i = 0; i < m; i++) {
                    final int j = i;
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            final AtomicInteger nexts = new AtomicInteger();
                            try {
                                Observable<String> origin = Observable.unsafeCreate(new ObservableRetryTest.FuncWithErrors(NUM_RETRIES));
                                TestObserver<String> to = new TestObserver<String>();
                                origin.retry().observeOn(io.reactivex.schedulers.Schedulers.computation()).subscribe(to);
                                to.awaitTerminalEvent(2500, TimeUnit.MILLISECONDS);
                                List<String> onNextEvents = new ArrayList<String>(to.values());
                                if ((onNextEvents.size()) != (NUM_RETRIES + 2)) {
                                    for (Throwable t : to.errors()) {
                                        onNextEvents.add(t.toString());
                                    }
                                    for (long err = to.completions(); err != 0; err--) {
                                        onNextEvents.add("onComplete");
                                    }
                                    data.put(j, onNextEvents);
                                }
                            } catch (Throwable t) {
                                timeouts.incrementAndGet();
                                System.out.println(((((j + " | ") + (cdl.getCount())) + " !!! ") + (nexts.get())));
                            }
                            cdl.countDown();
                        }
                    });
                }
                cdl.await();
                Assert.assertEquals(0, timeouts.get());
                if ((data.size()) > 0) {
                    Assert.fail(("Data content mismatch: " + (ObservableRetryTest.allSequenceFrequency(data))));
                }
            }
        } finally {
            exec.shutdown();
        }
    }

    // (timeout = 3000)
    @Test
    public void testIssue1900() throws InterruptedException {
        Observer<String> observer = mockObserver();
        final int NUM_MSG = 1034;
        final AtomicInteger count = new AtomicInteger();
        Observable<String> origin = range(0, NUM_MSG).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer t1) {
                return "msg: " + (count.incrementAndGet());
            }
        });
        origin.retry().groupBy(new Function<String, String>() {
            @Override
            public String apply(String t1) {
                return t1;
            }
        }).flatMap(new Function<io.reactivex.observables.GroupedObservable<String, String>, Observable<String>>() {
            @Override
            public Observable<String> apply(GroupedObservable<String, String> t1) {
                return t1.take(1);
            }
        }).subscribe(new TestObserver<String>(observer));
        InOrder inOrder = Mockito.inOrder(observer);
        // should show 3 attempts
        inOrder.verify(observer, Mockito.times(NUM_MSG)).onNext(ArgumentMatchers.any(String.class));
        // // should have no errors
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        // inOrder.verify(observer, times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    // (timeout = 3000)
    @Test
    public void testIssue1900SourceNotSupportingBackpressure() {
        Observer<String> observer = mockObserver();
        final int NUM_MSG = 1034;
        final AtomicInteger count = new AtomicInteger();
        Observable<String> origin = unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(Observer<? super String> o) {
                o.onSubscribe(Disposables.empty());
                for (int i = 0; i < NUM_MSG; i++) {
                    o.onNext(("msg:" + (count.incrementAndGet())));
                }
                o.onComplete();
            }
        });
        origin.retry().groupBy(new Function<String, String>() {
            @Override
            public String apply(String t1) {
                return t1;
            }
        }).flatMap(new Function<io.reactivex.observables.GroupedObservable<String, String>, Observable<String>>() {
            @Override
            public Observable<String> apply(GroupedObservable<String, String> t1) {
                return t1.take(1);
            }
        }).subscribe(new TestObserver<String>(observer));
        InOrder inOrder = Mockito.inOrder(observer);
        // should show 3 attempts
        inOrder.verify(observer, Mockito.times(NUM_MSG)).onNext(ArgumentMatchers.any(String.class));
        // // should have no errors
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // should have a single success
        // inOrder.verify(observer, times(1)).onNext("onSuccessOnly");
        // should have a single successful onComplete
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void retryPredicate() {
        just(1).concatWith(Observable.<Integer>error(new TestException())).retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable v) throws Exception {
                return true;
            }
        }).take(5).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void retryUntil() {
        just(1).concatWith(Observable.<Integer>error(new TestException())).retryUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return false;
            }
        }).take(5).test().assertResult(1, 1, 1, 1, 1);
    }

    @Test
    public void retryLongPredicateInvalid() {
        try {
            just(1).retry((-99), new Predicate<Throwable>() {
                @Override
                public boolean test(Throwable e) throws Exception {
                    return true;
                }
            });
            Assert.fail("Should have thrown");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("times >= 0 required but it was -99", ex.getMessage());
        }
    }

    @Test
    public void shouldDisposeInnerObservable() {
        final PublishSubject<Object> subject = PublishSubject.create();
        final Disposable disposable = Observable.error(new RuntimeException("Leak")).retryWhen(new Function<Observable<Throwable>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Throwable> errors) throws Exception {
                return errors.switchMap(new Function<Throwable, ObservableSource<Object>>() {
                    @Override
                    public ObservableSource<Object> apply(Throwable ignore) throws Exception {
                        return subject;
                    }
                });
            }
        }).subscribe();
        Assert.assertTrue(subject.hasObservers());
        disposable.dispose();
        Assert.assertFalse(subject.hasObservers());
    }

    @Test
    public void noCancelPreviousRetry() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Observable<Integer> source = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> call() throws Exception {
                if ((times.getAndIncrement()) < 4) {
                    return error(new TestException());
                }
                return Observable.just(1);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retry(5).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRetryWhile() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Observable<Integer> source = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> call() throws Exception {
                if ((times.getAndIncrement()) < 4) {
                    return error(new TestException());
                }
                return Observable.just(1);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retry(5, io.reactivex.internal.functions.Functions.alwaysTrue()).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRetryWhile2() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Observable<Integer> source = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> call() throws Exception {
                if ((times.getAndIncrement()) < 4) {
                    return error(new TestException());
                }
                return Observable.just(1);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer a, Throwable b) throws Exception {
                return a < 5;
            }
        }).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRetryUntil() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Observable<Integer> source = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> call() throws Exception {
                if ((times.getAndIncrement()) < 4) {
                    return error(new TestException());
                }
                return Observable.just(1);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retryUntil(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() throws Exception {
                return false;
            }
        }).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRepeatWhen() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Observable<Integer> source = defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> call() throws Exception {
                if ((times.get()) < 4) {
                    return error(new TestException());
                }
                return Observable.just(1);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> e) throws Exception {
                return e.takeWhile(new Predicate<Object>() {
                    @Override
                    public boolean test(Object v) throws Exception {
                        return (times.getAndIncrement()) < 4;
                    }
                });
            }
        }).test().assertResult(1);
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void noCancelPreviousRepeatWhen2() {
        final AtomicInteger counter = new AtomicInteger();
        final AtomicInteger times = new AtomicInteger();
        Observable<Integer> source = Observable.<Integer>error(new TestException()).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                counter.getAndIncrement();
            }
        });
        source.retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> e) throws Exception {
                return e.takeWhile(new Predicate<Object>() {
                    @Override
                    public boolean test(Object v) throws Exception {
                        return (times.getAndIncrement()) < 4;
                    }
                });
            }
        }).test().assertResult();
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void repeatFloodNoSubscriptionError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        final TestException error = new TestException();
        try {
            final PublishSubject<Integer> source = PublishSubject.create();
            final PublishSubject<Integer> signaller = PublishSubject.create();
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                TestObserver<Integer> to = source.take(1).map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer v) throws Exception {
                        throw error;
                    }
                }).retryWhen(new Function<Observable<Throwable>, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Observable<Throwable> v) throws Exception {
                        return signaller;
                    }
                }).test();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                            source.onNext(1);
                        }
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                            signaller.onNext(1);
                        }
                    }
                };
                TestHelper.race(r1, r2);
                to.dispose();
            }
            if (!(errors.isEmpty())) {
                for (Throwable e : errors) {
                    e.printStackTrace();
                }
                Assert.fail((errors + ""));
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

