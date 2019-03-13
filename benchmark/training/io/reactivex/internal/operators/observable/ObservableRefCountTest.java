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
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.operators.observable.ObservableRefCount.RefConnection;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
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
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableRefCountTest {
    @Test
    public void testRefCountAsync() {
        final AtomicInteger subscribeCount = new AtomicInteger();
        final AtomicInteger nextCount = new AtomicInteger();
        Observable<Long> r = Observable.interval(0, 25, TimeUnit.MILLISECONDS).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) {
                subscribeCount.incrementAndGet();
            }
        }).doOnNext(new Consumer<Long>() {
            @Override
            public void accept(Long l) {
                nextCount.incrementAndGet();
            }
        }).publish().refCount();
        final AtomicInteger receivedCount = new AtomicInteger();
        Disposable d1 = r.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long l) {
                receivedCount.incrementAndGet();
            }
        });
        Disposable d2 = r.subscribe();
        // give time to emit
        try {
            Thread.sleep(260);
        } catch (InterruptedException e) {
        }
        // now unsubscribe
        d2.dispose();// unsubscribe s2 first as we're counting in 1 and there can be a race between unsubscribe and one Observer getting a value but not the other

        d1.dispose();
        System.out.println(("onNext: " + (nextCount.get())));
        // should emit once for both subscribers
        Assert.assertEquals(nextCount.get(), receivedCount.get());
        // only 1 subscribe
        Assert.assertEquals(1, subscribeCount.get());
    }

    @Test
    public void testRefCountSynchronous() {
        final AtomicInteger subscribeCount = new AtomicInteger();
        final AtomicInteger nextCount = new AtomicInteger();
        Observable<Integer> r = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) {
                subscribeCount.incrementAndGet();
            }
        }).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer l) {
                nextCount.incrementAndGet();
            }
        }).publish().refCount();
        final AtomicInteger receivedCount = new AtomicInteger();
        Disposable d1 = r.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer l) {
                receivedCount.incrementAndGet();
            }
        });
        Disposable d2 = r.subscribe();
        // give time to emit
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }
        // now unsubscribe
        d2.dispose();// unsubscribe s2 first as we're counting in 1 and there can be a race between unsubscribe and one Observer getting a value but not the other

        d1.dispose();
        System.out.println(("onNext Count: " + (nextCount.get())));
        // it will emit twice because it is synchronous
        Assert.assertEquals(nextCount.get(), ((receivedCount.get()) * 2));
        // it will subscribe twice because it is synchronous
        Assert.assertEquals(2, subscribeCount.get());
    }

    @Test
    public void testRefCountSynchronousTake() {
        final AtomicInteger nextCount = new AtomicInteger();
        Observable<Integer> r = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer l) {
                System.out.println(("onNext --------> " + l));
                nextCount.incrementAndGet();
            }
        }).take(4).publish().refCount();
        final AtomicInteger receivedCount = new AtomicInteger();
        r.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer l) {
                receivedCount.incrementAndGet();
            }
        });
        System.out.println(("onNext: " + (nextCount.get())));
        Assert.assertEquals(4, receivedCount.get());
        Assert.assertEquals(4, receivedCount.get());
    }

    @Test
    public void testRepeat() {
        final AtomicInteger subscribeCount = new AtomicInteger();
        final AtomicInteger unsubscribeCount = new AtomicInteger();
        Observable<Long> r = Observable.interval(0, 1, TimeUnit.MILLISECONDS).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) {
                System.out.println("******************************* Subscribe received");
                // when we are subscribed
                subscribeCount.incrementAndGet();
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() {
                System.out.println("******************************* Unsubscribe received");
                // when we are unsubscribed
                unsubscribeCount.incrementAndGet();
            }
        }).publish().refCount();
        for (int i = 0; i < 10; i++) {
            TestObserver<Long> to1 = new TestObserver<Long>();
            TestObserver<Long> to2 = new TestObserver<Long>();
            r.subscribe(to1);
            r.subscribe(to2);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
            to1.dispose();
            to2.dispose();
            to1.assertNoErrors();
            to2.assertNoErrors();
            Assert.assertTrue(((to1.valueCount()) > 0));
            Assert.assertTrue(((to2.valueCount()) > 0));
        }
        Assert.assertEquals(10, subscribeCount.get());
        Assert.assertEquals(10, unsubscribeCount.get());
    }

    @Test
    public void testConnectUnsubscribe() throws InterruptedException {
        final CountDownLatch unsubscribeLatch = new CountDownLatch(1);
        final CountDownLatch subscribeLatch = new CountDownLatch(1);
        Observable<Long> o = synchronousInterval().doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) {
                System.out.println("******************************* Subscribe received");
                // when we are subscribed
                subscribeLatch.countDown();
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() {
                System.out.println("******************************* Unsubscribe received");
                // when we are unsubscribed
                unsubscribeLatch.countDown();
            }
        });
        TestObserver<Long> observer = new TestObserver<Long>();
        o.publish().refCount().subscribeOn(Schedulers.newThread()).subscribe(observer);
        System.out.println("send unsubscribe");
        // wait until connected
        subscribeLatch.await();
        // now unsubscribe
        observer.dispose();
        System.out.println("DONE sending unsubscribe ... now waiting");
        if (!(unsubscribeLatch.await(3000, TimeUnit.MILLISECONDS))) {
            System.out.println(("Errors: " + (observer.errors())));
            if ((observer.errors().size()) > 0) {
                observer.errors().get(0).printStackTrace();
            }
            Assert.fail("timed out waiting for unsubscribe");
        }
        observer.assertNoErrors();
    }

    @Test
    public void testConnectUnsubscribeRaceConditionLoop() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            testConnectUnsubscribeRaceCondition();
        }
    }

    @Test
    public void testConnectUnsubscribeRaceCondition() throws InterruptedException {
        final AtomicInteger subUnsubCount = new AtomicInteger();
        Observable<Long> o = synchronousInterval().doOnDispose(new Action() {
            @Override
            public void run() {
                System.out.println("******************************* Unsubscribe received");
                // when we are unsubscribed
                subUnsubCount.decrementAndGet();
            }
        }).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) {
                System.out.println("******************************* SUBSCRIBE received");
                subUnsubCount.incrementAndGet();
            }
        });
        TestObserver<Long> observer = new TestObserver<Long>();
        o.publish().refCount().subscribeOn(Schedulers.computation()).subscribe(observer);
        System.out.println("send unsubscribe");
        // now immediately unsubscribe while subscribeOn is racing to subscribe
        observer.dispose();
        // this generally will mean it won't even subscribe as it is already unsubscribed by the time connect() gets scheduled
        // give time to the counter to update
        Thread.sleep(10);
        // make sure we wait a bit in case the counter is still nonzero
        int counter = 200;
        while (((subUnsubCount.get()) != 0) && ((counter--) != 0)) {
            Thread.sleep(10);
        } 
        // either we subscribed and then unsubscribed, or we didn't ever even subscribe
        Assert.assertEquals(0, subUnsubCount.get());
        System.out.println("DONE sending unsubscribe ... now waiting");
        System.out.println(("Errors: " + (observer.errors())));
        if ((observer.errors().size()) > 0) {
            observer.errors().get(0).printStackTrace();
        }
        observer.assertNoErrors();
    }

    @Test
    public void onlyFirstShouldSubscribeAndLastUnsubscribe() {
        final AtomicInteger subscriptionCount = new AtomicInteger();
        final AtomicInteger unsubscriptionCount = new AtomicInteger();
        Observable<Integer> o = unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observer) {
                subscriptionCount.incrementAndGet();
                observer.onSubscribe(Disposables.fromRunnable(new Runnable() {
                    @Override
                    public void run() {
                        unsubscriptionCount.incrementAndGet();
                    }
                }));
            }
        });
        Observable<Integer> refCounted = o.publish().refCount();
        Disposable first = refCounted.subscribe();
        Assert.assertEquals(1, subscriptionCount.get());
        Disposable second = refCounted.subscribe();
        Assert.assertEquals(1, subscriptionCount.get());
        first.dispose();
        Assert.assertEquals(0, unsubscriptionCount.get());
        second.dispose();
        Assert.assertEquals(1, unsubscriptionCount.get());
    }

    @Test
    public void testRefCount() {
        TestScheduler s = new TestScheduler();
        Observable<Long> interval = Observable.interval(100, TimeUnit.MILLISECONDS, s).publish().refCount();
        // subscribe list1
        final List<Long> list1 = new ArrayList<Long>();
        Disposable d1 = interval.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long t1) {
                list1.add(t1);
            }
        });
        s.advanceTimeBy(200, TimeUnit.MILLISECONDS);
        Assert.assertEquals(2, list1.size());
        Assert.assertEquals(0L, list1.get(0).longValue());
        Assert.assertEquals(1L, list1.get(1).longValue());
        // subscribe list2
        final List<Long> list2 = new ArrayList<Long>();
        Disposable d2 = interval.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long t1) {
                list2.add(t1);
            }
        });
        s.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        // list 1 should have 5 items
        Assert.assertEquals(5, list1.size());
        Assert.assertEquals(2L, list1.get(2).longValue());
        Assert.assertEquals(3L, list1.get(3).longValue());
        Assert.assertEquals(4L, list1.get(4).longValue());
        // list 2 should only have 3 items
        Assert.assertEquals(3, list2.size());
        Assert.assertEquals(2L, list2.get(0).longValue());
        Assert.assertEquals(3L, list2.get(1).longValue());
        Assert.assertEquals(4L, list2.get(2).longValue());
        // unsubscribe list1
        d1.dispose();
        // advance further
        s.advanceTimeBy(300, TimeUnit.MILLISECONDS);
        // list 1 should still have 5 items
        Assert.assertEquals(5, list1.size());
        // list 2 should have 6 items
        Assert.assertEquals(6, list2.size());
        Assert.assertEquals(5L, list2.get(3).longValue());
        Assert.assertEquals(6L, list2.get(4).longValue());
        Assert.assertEquals(7L, list2.get(5).longValue());
        // unsubscribe list2
        d2.dispose();
        // advance further
        s.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        // subscribing a new one should start over because the source should have been unsubscribed
        // subscribe list3
        final List<Long> list3 = new ArrayList<Long>();
        interval.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long t1) {
                list3.add(t1);
            }
        });
        s.advanceTimeBy(200, TimeUnit.MILLISECONDS);
        Assert.assertEquals(2, list3.size());
        Assert.assertEquals(0L, list3.get(0).longValue());
        Assert.assertEquals(1L, list3.get(1).longValue());
    }

    @Test
    public void testAlreadyUnsubscribedClient() {
        Observer<Integer> done = ObservableRefCountTest.DisposingObserver.INSTANCE;
        Observer<Integer> o = mockObserver();
        Observable<Integer> result = just(1).publish().refCount();
        result.subscribe(done);
        result.subscribe(o);
        Mockito.verify(o).onNext(1);
        Mockito.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testAlreadyUnsubscribedInterleavesWithClient() {
        ReplaySubject<Integer> source = ReplaySubject.create();
        Observer<Integer> done = ObservableRefCountTest.DisposingObserver.INSTANCE;
        Observer<Integer> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        Observable<Integer> result = source.publish().refCount();
        result.subscribe(o);
        source.onNext(1);
        result.subscribe(done);
        source.onNext(2);
        source.onComplete();
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(2);
        inOrder.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testConnectDisconnectConnectAndSubjectState() {
        Observable<Integer> o1 = just(10);
        Observable<Integer> o2 = just(20);
        Observable<Integer> combined = Observable.combineLatest(o1, o2, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).publish().refCount();
        TestObserver<Integer> to1 = new TestObserver<Integer>();
        TestObserver<Integer> to2 = new TestObserver<Integer>();
        combined.subscribe(to1);
        combined.subscribe(to2);
        to1.assertTerminated();
        to1.assertNoErrors();
        to1.assertValue(30);
        to2.assertTerminated();
        to2.assertNoErrors();
        to2.assertValue(30);
    }

    @Test(timeout = 10000)
    public void testUpstreamErrorAllowsRetry() throws InterruptedException {
        final AtomicInteger intervalSubscribed = new AtomicInteger();
        Observable<String> interval = interval(200, TimeUnit.MILLISECONDS).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) {
                System.out.println(("Subscribing to interval " + (intervalSubscribed.incrementAndGet())));
            }
        }).flatMap(new Function<Long, Observable<String>>() {
            @Override
            public Observable<String> apply(Long t1) {
                return io.reactivex.Observable.defer(new Callable<Observable<String>>() {
                    @Override
                    public Observable<String> call() {
                        return io.reactivex.Observable.<String>error(new Exception("Some exception"));
                    }
                });
            }
        }).onErrorResumeNext(new Function<Throwable, Observable<String>>() {
            @Override
            public Observable<String> apply(Throwable t1) {
                return io.reactivex.Observable.<String>error(t1);
            }
        }).publish().refCount();
        interval.doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t1) {
                System.out.println(("Observer 1 onError: " + t1));
            }
        }).retry(5).subscribe(new Consumer<String>() {
            @Override
            public void accept(String t1) {
                System.out.println(("Observer 1: " + t1));
            }
        });
        Thread.sleep(100);
        interval.doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t1) {
                System.out.println(("Observer 2 onError: " + t1));
            }
        }).retry(5).subscribe(new Consumer<String>() {
            @Override
            public void accept(String t1) {
                System.out.println(("Observer 2: " + t1));
            }
        });
        Thread.sleep(1300);
        System.out.println(intervalSubscribed.get());
        Assert.assertEquals(6, intervalSubscribed.get());
    }

    private enum DisposingObserver implements Observer<Integer> {

        INSTANCE;
        @Override
        public void onSubscribe(Disposable d) {
            d.dispose();
        }

        @Override
        public void onNext(Integer t) {
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onComplete() {
        }
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(just(1).publish().refCount());
    }

    @Test
    public void noOpConnect() {
        final int[] calls = new int[]{ 0 };
        Observable<Integer> o = new ConnectableObservable<Integer>() {
            @Override
            public void connect(Consumer<? super Disposable> connection) {
                (calls[0])++;
            }

            @Override
            protected void subscribeActual(Observer<? super Integer> observer) {
                observer.onSubscribe(Disposables.disposed());
            }
        }.refCount();
        o.test();
        o.test();
        Assert.assertEquals(1, calls[0]);
    }

    Observable<Object> source;

    @Test
    public void replayNoLeak() throws Exception {
        System.gc();
        Thread.sleep(100);
        long start = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        source = Observable.fromCallable(new java.util.concurrent.Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return new byte[(100 * 1000) * 1000];
            }
        }).replay(1).refCount();
        source.subscribe();
        System.gc();
        Thread.sleep(100);
        long after = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        source = null;
        Assert.assertTrue(String.format("%,3d -> %,3d%n", start, after), ((start + ((20 * 1000) * 1000)) > after));
    }

    @Test
    public void replayNoLeak2() throws Exception {
        System.gc();
        Thread.sleep(100);
        long start = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        source = Observable.fromCallable(new java.util.concurrent.Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return new byte[(100 * 1000) * 1000];
            }
        }).concatWith(never()).replay(1).refCount();
        Disposable d1 = source.subscribe();
        Disposable d2 = source.subscribe();
        d1.dispose();
        d2.dispose();
        d1 = null;
        d2 = null;
        System.gc();
        Thread.sleep(100);
        long after = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        source = null;
        Assert.assertTrue(String.format("%,3d -> %,3d%n", start, after), ((start + ((20 * 1000) * 1000)) > after));
    }

    static final class ExceptionData extends Exception {
        private static final long serialVersionUID = -6763898015338136119L;

        public final Object data;

        ExceptionData(Object data) {
            this.data = data;
        }
    }

    @Test
    public void publishNoLeak() throws Exception {
        System.gc();
        Thread.sleep(100);
        long start = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        source = Observable.fromCallable(new java.util.concurrent.Callable<Object>() {
            @Override
            public Object call() throws Exception {
                throw new ObservableRefCountTest.ExceptionData(new byte[(100 * 1000) * 1000]);
            }
        }).publish().refCount();
        source.subscribe(Functions.emptyConsumer(), Functions.emptyConsumer());
        System.gc();
        Thread.sleep(100);
        long after = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        source = null;
        Assert.assertTrue(String.format("%,3d -> %,3d%n", start, after), ((start + ((20 * 1000) * 1000)) > after));
    }

    @Test
    public void publishNoLeak2() throws Exception {
        System.gc();
        Thread.sleep(100);
        long start = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        source = Observable.fromCallable(new java.util.concurrent.Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return new byte[(100 * 1000) * 1000];
            }
        }).concatWith(never()).publish().refCount();
        Disposable d1 = source.test();
        Disposable d2 = source.test();
        d1.dispose();
        d2.dispose();
        d1 = null;
        d2 = null;
        System.gc();
        Thread.sleep(100);
        long after = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        source = null;
        Assert.assertTrue(String.format("%,3d -> %,3d%n", start, after), ((start + ((20 * 1000) * 1000)) > after));
    }

    @Test
    public void replayIsUnsubscribed() {
        ConnectableObservable<Integer> co = just(1).concatWith(<Integer>never()).replay();
        if (co instanceof Disposable) {
            Assert.assertTrue(isDisposed());
            Disposable connection = co.connect();
            Assert.assertFalse(isDisposed());
            connection.dispose();
            Assert.assertTrue(isDisposed());
        }
    }

    static final class BadObservableSubscribe extends ConnectableObservable<Object> {
        @Override
        public void connect(Consumer<? super Disposable> connection) {
            try {
                connection.accept(Disposables.empty());
            } catch (Throwable ex) {
                throw ExceptionHelper.wrapOrThrow(ex);
            }
        }

        @Override
        protected void subscribeActual(Observer<? super Object> observer) {
            throw new TestException("subscribeActual");
        }
    }

    static final class BadObservableDispose extends ConnectableObservable<Object> implements Disposable {
        @Override
        public void dispose() {
            throw new TestException("dispose");
        }

        @Override
        public boolean isDisposed() {
            return false;
        }

        @Override
        public void connect(Consumer<? super Disposable> connection) {
            try {
                connection.accept(Disposables.empty());
            } catch (Throwable ex) {
                throw ExceptionHelper.wrapOrThrow(ex);
            }
        }

        @Override
        protected void subscribeActual(Observer<? super Object> observer) {
            observer.onSubscribe(Disposables.empty());
        }
    }

    static final class BadObservableConnect extends ConnectableObservable<Object> {
        @Override
        public void connect(Consumer<? super Disposable> connection) {
            throw new TestException("connect");
        }

        @Override
        protected void subscribeActual(Observer<? super Object> observer) {
            observer.onSubscribe(Disposables.empty());
        }
    }

    @Test
    public void badSourceSubscribe() {
        ObservableRefCountTest.BadObservableSubscribe bo = new ObservableRefCountTest.BadObservableSubscribe();
        try {
            bo.refCount().test();
            Assert.fail("Should have thrown");
        } catch (NullPointerException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof TestException));
        }
    }

    @Test
    public void badSourceDispose() {
        ObservableRefCountTest.BadObservableDispose bo = new ObservableRefCountTest.BadObservableDispose();
        try {
            bo.refCount().test().cancel();
            Assert.fail("Should have thrown");
        } catch (TestException expected) {
        }
    }

    @Test
    public void badSourceConnect() {
        ObservableRefCountTest.BadObservableConnect bo = new ObservableRefCountTest.BadObservableConnect();
        try {
            bo.refCount().test();
            Assert.fail("Should have thrown");
        } catch (NullPointerException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof TestException));
        }
    }

    static final class BadObservableSubscribe2 extends ConnectableObservable<Object> {
        int count;

        @Override
        public void connect(Consumer<? super Disposable> connection) {
            try {
                connection.accept(Disposables.empty());
            } catch (Throwable ex) {
                throw ExceptionHelper.wrapOrThrow(ex);
            }
        }

        @Override
        protected void subscribeActual(Observer<? super Object> observer) {
            if ((++(count)) == 1) {
                observer.onSubscribe(Disposables.empty());
            } else {
                throw new TestException("subscribeActual");
            }
        }
    }

    @Test
    public void badSourceSubscribe2() {
        ObservableRefCountTest.BadObservableSubscribe2 bo = new ObservableRefCountTest.BadObservableSubscribe2();
        Observable<Object> o = bo.refCount();
        o.test();
        try {
            o.test();
            Assert.fail("Should have thrown");
        } catch (NullPointerException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof TestException));
        }
    }

    static final class BadObservableConnect2 extends ConnectableObservable<Object> implements Disposable {
        @Override
        public void connect(Consumer<? super Disposable> connection) {
            try {
                connection.accept(Disposables.empty());
            } catch (Throwable ex) {
                throw ExceptionHelper.wrapOrThrow(ex);
            }
        }

        @Override
        protected void subscribeActual(Observer<? super Object> observer) {
            observer.onSubscribe(Disposables.empty());
            observer.onComplete();
        }

        @Override
        public void dispose() {
            throw new TestException("dispose");
        }

        @Override
        public boolean isDisposed() {
            return false;
        }
    }

    @Test
    public void badSourceCompleteDisconnect() {
        ObservableRefCountTest.BadObservableConnect2 bo = new ObservableRefCountTest.BadObservableConnect2();
        try {
            bo.refCount().test();
            Assert.fail("Should have thrown");
        } catch (NullPointerException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof TestException));
        }
    }

    @Test(timeout = 7500)
    public void blockingSourceAsnycCancel() throws Exception {
        BehaviorSubject<Integer> bs = BehaviorSubject.createDefault(1);
        Observable<Integer> o = bs.replay(1).refCount();
        o.subscribe();
        final AtomicBoolean interrupted = new AtomicBoolean();
        o.switchMap(new Function<Integer, ObservableSource<? extends Object>>() {
            @Override
            public io.reactivex.ObservableSource<? extends Object> apply(Integer v) throws Exception {
                return create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                        while (!(emitter.isDisposed())) {
                            Thread.sleep(100);
                        } 
                        interrupted.set(true);
                    }
                });
            }
        }).take(500, TimeUnit.MILLISECONDS).test().awaitDone(5, TimeUnit.SECONDS).assertResult();
        Assert.assertTrue(interrupted.get());
    }

    @Test
    public void byCount() {
        final int[] subscriptions = new int[]{ 0 };
        Observable<Integer> source = refCount(2);
        for (int i = 0; i < 3; i++) {
            TestObserver<Integer> to1 = source.test();
            to1.assertEmpty();
            TestObserver<Integer> to2 = source.test();
            to1.assertResult(1, 2, 3, 4, 5);
            to2.assertResult(1, 2, 3, 4, 5);
        }
        Assert.assertEquals(3, subscriptions[0]);
    }

    @Test
    public void resubscribeBeforeTimeout() throws Exception {
        final int[] subscriptions = new int[]{ 0 };
        PublishSubject<Integer> ps = PublishSubject.create();
        Observable<Integer> source = ps.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                (subscriptions[0])++;
            }
        }).publish().refCount(500, TimeUnit.MILLISECONDS);
        TestObserver<Integer> to1 = source.test();
        Assert.assertEquals(1, subscriptions[0]);
        to1.cancel();
        Thread.sleep(100);
        to1 = source.test();
        Assert.assertEquals(1, subscriptions[0]);
        Thread.sleep(500);
        Assert.assertEquals(1, subscriptions[0]);
        ps.onNext(1);
        ps.onNext(2);
        ps.onNext(3);
        ps.onNext(4);
        ps.onNext(5);
        ps.onComplete();
        to1.assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void letitTimeout() throws Exception {
        final int[] subscriptions = new int[]{ 0 };
        PublishSubject<Integer> ps = PublishSubject.create();
        Observable<Integer> source = ps.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                (subscriptions[0])++;
            }
        }).publish().refCount(1, 100, TimeUnit.MILLISECONDS);
        TestObserver<Integer> to1 = source.test();
        Assert.assertEquals(1, subscriptions[0]);
        to1.cancel();
        Assert.assertTrue(ps.hasObservers());
        Thread.sleep(200);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void error() {
        Observable.<Integer>error(new IOException()).publish().refCount(500, TimeUnit.MILLISECONDS).test().assertFailure(IOException.class);
    }

    @Test
    public void comeAndGo() {
        PublishSubject<Integer> ps = PublishSubject.create();
        Observable<Integer> source = refCount(1);
        TestObserver<Integer> to1 = source.test();
        Assert.assertTrue(ps.hasObservers());
        for (int i = 0; i < 3; i++) {
            TestObserver<Integer> to2 = source.test();
            to1.cancel();
            to1 = to2;
        }
        to1.cancel();
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void unsubscribeSubscribeRace() {
        for (int i = 0; i < 1000; i++) {
            final Observable<Integer> source = refCount(1);
            final TestObserver<Integer> to1 = source.test();
            final TestObserver<Integer> to2 = new TestObserver<Integer>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to1.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    source.subscribe(to2);
                }
            };
            TestHelper.race(r1, r2, Schedulers.single());
            to2.withTag(("Round: " + i)).assertResult(1, 2, 3, 4, 5);
        }
    }

    static final class BadObservableDoubleOnX extends ConnectableObservable<Object> implements Disposable {
        @Override
        public void connect(Consumer<? super Disposable> connection) {
            try {
                connection.accept(Disposables.empty());
            } catch (Throwable ex) {
                throw ExceptionHelper.wrapOrThrow(ex);
            }
        }

        @Override
        protected void subscribeActual(Observer<? super Object> observer) {
            observer.onSubscribe(Disposables.empty());
            observer.onSubscribe(Disposables.empty());
            observer.onComplete();
            observer.onComplete();
            observer.onError(new TestException());
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isDisposed() {
            return false;
        }
    }

    @Test
    public void doubleOnX() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new ObservableRefCountTest.BadObservableDoubleOnX().refCount().test().assertResult();
            TestHelper.assertError(errors, 0, ProtocolViolationException.class);
            TestHelper.assertUndeliverable(errors, 1, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void doubleOnXCount() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            refCount(1).test().assertResult();
            TestHelper.assertError(errors, 0, ProtocolViolationException.class);
            TestHelper.assertUndeliverable(errors, 1, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void doubleOnXTime() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new ObservableRefCountTest.BadObservableDoubleOnX().refCount(5, TimeUnit.SECONDS, Schedulers.single()).test().assertResult();
            TestHelper.assertError(errors, 0, ProtocolViolationException.class);
            TestHelper.assertUndeliverable(errors, 1, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancelTerminateStateExclusion() {
        ObservableRefCount<Object> o = ((ObservableRefCount<Object>) (PublishSubject.create().publish().refCount()));
        o.cancel(null);
        o.cancel(new RefConnection(o));
        RefConnection rc = new RefConnection(o);
        o.connection = null;
        rc.subscriberCount = 0;
        o.timeout(rc);
        rc.subscriberCount = 1;
        o.timeout(rc);
        o.connection = rc;
        o.timeout(rc);
        rc.subscriberCount = 0;
        o.timeout(rc);
        // -------------------
        rc.subscriberCount = 2;
        rc.connected = false;
        o.connection = rc;
        o.cancel(rc);
        rc.subscriberCount = 1;
        rc.connected = false;
        o.connection = rc;
        o.cancel(rc);
        rc.subscriberCount = 2;
        rc.connected = true;
        o.connection = rc;
        o.cancel(rc);
        rc.subscriberCount = 1;
        rc.connected = true;
        o.connection = rc;
        o.cancel(rc);
        o.connection = rc;
        o.cancel(new RefConnection(o));
    }

    @Test
    public void replayRefCountShallBeThreadSafe() {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            Observable<Integer> observable = just(1).replay(1).refCount();
            TestObserver<Integer> observer1 = observable.subscribeOn(Schedulers.io()).test();
            TestObserver<Integer> observer2 = observable.subscribeOn(Schedulers.io()).test();
            observer1.withTag(("" + i)).awaitDone(5, TimeUnit.SECONDS).assertResult(1);
            observer2.withTag(("" + i)).awaitDone(5, TimeUnit.SECONDS).assertResult(1);
        }
    }

    static final class TestConnectableObservable<T> extends ConnectableObservable<T> implements Disposable {
        volatile boolean disposed;

        @Override
        public void dispose() {
            disposed = true;
        }

        @Override
        public boolean isDisposed() {
            return disposed;
        }

        @Override
        public void connect(Consumer<? super Disposable> connection) {
            // not relevant
        }

        @Override
        protected void subscribeActual(Observer<? super T> observer) {
            // not relevant
        }
    }

    @Test
    public void timeoutDisposesSource() {
        ObservableRefCount<Object> o = ((ObservableRefCount<Object>) (new ObservableRefCountTest.TestConnectableObservable<Object>().refCount()));
        RefConnection rc = new RefConnection(o);
        o.connection = rc;
        o.timeout(rc);
        Assert.assertTrue(isDisposed());
    }

    @Test
    public void disconnectBeforeConnect() {
        BehaviorSubject<Integer> subject = BehaviorSubject.create();
        Observable<Integer> observable = subject.replay(1).refCount();
        observable.takeUntil(just(1)).test();
        subject.onNext(2);
        observable.take(1).test().assertResult(2);
    }
}

