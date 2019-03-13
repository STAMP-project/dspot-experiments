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
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.Action;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class ObservableUnsubscribeOnTest {
    @Test(timeout = 5000)
    public void unsubscribeWhenSubscribeOnAndUnsubscribeOnAreOnSameThread() throws InterruptedException {
        ObservableUnsubscribeOnTest.UIEventLoopScheduler uiEventLoop = new ObservableUnsubscribeOnTest.UIEventLoopScheduler();
        try {
            final ObservableUnsubscribeOnTest.ThreadSubscription subscription = new ObservableUnsubscribeOnTest.ThreadSubscription();
            final AtomicReference<Thread> subscribeThread = new AtomicReference<Thread>();
            Observable<Integer> w = Observable.unsafeCreate(new ObservableSource<Integer>() {
                @Override
                public void subscribe(Observer<? super Integer> t1) {
                    subscribeThread.set(Thread.currentThread());
                    t1.onSubscribe(subscription);
                    t1.onNext(1);
                    t1.onNext(2);
                    // observeOn will prevent canceling the upstream upon its termination now
                    // this call is racing for that state in this test
                    // not doing it will make sure the unsubscribeOn always gets through
                    // t1.onComplete();
                }
            });
            TestObserver<Integer> observer = new TestObserver<Integer>();
            w.subscribeOn(uiEventLoop).observeOn(Schedulers.computation()).unsubscribeOn(uiEventLoop).take(2).subscribe(observer);
            observer.awaitTerminalEvent(5, TimeUnit.SECONDS);
            Thread unsubscribeThread = subscription.getThread();
            Assert.assertNotNull(unsubscribeThread);
            Assert.assertNotSame(Thread.currentThread(), unsubscribeThread);
            Assert.assertNotNull(subscribeThread.get());
            Assert.assertNotSame(Thread.currentThread(), subscribeThread.get());
            // True for Schedulers.newThread()
            System.out.println(("unsubscribeThread: " + unsubscribeThread));
            System.out.println(("subscribeThread.get(): " + (subscribeThread.get())));
            Assert.assertTrue(unsubscribeThread.toString(), (unsubscribeThread == (uiEventLoop.getThread())));
            observer.assertValues(1, 2);
            observer.assertTerminated();
        } finally {
            shutdown();
        }
    }

    @Test(timeout = 5000)
    public void unsubscribeWhenSubscribeOnAndUnsubscribeOnAreOnDifferentThreads() throws InterruptedException {
        ObservableUnsubscribeOnTest.UIEventLoopScheduler uiEventLoop = new ObservableUnsubscribeOnTest.UIEventLoopScheduler();
        try {
            final ObservableUnsubscribeOnTest.ThreadSubscription subscription = new ObservableUnsubscribeOnTest.ThreadSubscription();
            final AtomicReference<Thread> subscribeThread = new AtomicReference<Thread>();
            Observable<Integer> w = Observable.unsafeCreate(new ObservableSource<Integer>() {
                @Override
                public void subscribe(Observer<? super Integer> t1) {
                    subscribeThread.set(Thread.currentThread());
                    t1.onSubscribe(subscription);
                    t1.onNext(1);
                    t1.onNext(2);
                    // observeOn will prevent canceling the upstream upon its termination now
                    // this call is racing for that state in this test
                    // not doing it will make sure the unsubscribeOn always gets through
                    // t1.onComplete();
                }
            });
            TestObserver<Integer> observer = new TestObserver<Integer>();
            w.subscribeOn(Schedulers.newThread()).observeOn(Schedulers.computation()).unsubscribeOn(uiEventLoop).take(2).subscribe(observer);
            observer.awaitTerminalEvent(1, TimeUnit.SECONDS);
            Thread unsubscribeThread = subscription.getThread();
            Assert.assertNotNull(unsubscribeThread);
            Assert.assertNotSame(Thread.currentThread(), unsubscribeThread);
            Assert.assertNotNull(subscribeThread.get());
            Assert.assertNotSame(Thread.currentThread(), subscribeThread.get());
            // True for Schedulers.newThread()
            System.out.println(("UI Thread: " + (uiEventLoop.getThread())));
            System.out.println(("unsubscribeThread: " + unsubscribeThread));
            System.out.println(("subscribeThread.get(): " + (subscribeThread.get())));
            Assert.assertSame(unsubscribeThread, uiEventLoop.getThread());
            observer.assertValues(1, 2);
            observer.assertTerminated();
        } finally {
            shutdown();
        }
    }

    private static class ThreadSubscription extends AtomicBoolean implements Disposable {
        private static final long serialVersionUID = -5011338112974328771L;

        private volatile Thread thread;

        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void dispose() {
            set(true);
            System.out.println(("unsubscribe invoked: " + (Thread.currentThread())));
            thread = Thread.currentThread();
            latch.countDown();
        }

        @Override
        public boolean isDisposed() {
            return get();
        }

        public Thread getThread() throws InterruptedException {
            latch.await();
            return thread;
        }
    }

    public static class UIEventLoopScheduler extends Scheduler {
        private final io.reactivex.Scheduler eventLoop;

        private volatile Thread t;

        public UIEventLoopScheduler() {
            eventLoop = Schedulers.single();
            /* DON'T DO THIS IN PRODUCTION CODE */
            final CountDownLatch latch = new CountDownLatch(1);
            eventLoop.scheduleDirect(new Runnable() {
                @Override
                public void run() {
                    t = Thread.currentThread();
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException("failed to initialize and get inner thread");
            }
        }

        @NonNull
        @Override
        public Worker createWorker() {
            return eventLoop.createWorker();
        }

        public Thread getThread() {
            return t;
        }
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.just(1).unsubscribeOn(Schedulers.single()));
    }

    @Test
    public void normal() {
        final int[] calls = new int[]{ 0 };
        Observable.just(1).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                (calls[0])++;
            }
        }).unsubscribeOn(Schedulers.single()).test().assertResult(1);
        Assert.assertEquals(0, calls[0]);
    }

    @Test
    public void error() {
        final int[] calls = new int[]{ 0 };
        Observable.error(new TestException()).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                (calls[0])++;
            }
        }).unsubscribeOn(Schedulers.single()).test().assertFailure(TestException.class);
        Assert.assertEquals(0, calls[0]);
    }

    @Test
    public void signalAfterDispose() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.unsubscribeOn(Schedulers.single()).take(1).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

