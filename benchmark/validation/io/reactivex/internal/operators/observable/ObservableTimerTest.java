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
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.operators.observable.ObservableTimer.TimerObserver;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.Observer;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ObservableTimerTest {
    @Mock
    Observer<Object> observer;

    @Mock
    Observer<Long> observer2;

    TestScheduler scheduler;

    @Test
    public void testTimerOnce() {
        Observable.timer(100, TimeUnit.MILLISECONDS, scheduler).subscribe(observer);
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        Mockito.verify(observer, Mockito.times(1)).onNext(0L);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testTimerPeriodically() {
        TestObserver<Long> to = new TestObserver<Long>();
        Observable.interval(100, 100, TimeUnit.MILLISECONDS, scheduler).subscribe(to);
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValue(0L);
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValues(0L, 1L);
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValues(0L, 1L, 2L);
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValues(0L, 1L, 2L, 3L);
        to.dispose();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        to.assertValues(0L, 1L, 2L, 3L);
        to.assertNotComplete();
        to.assertNoErrors();
    }

    @Test
    public void testInterval() {
        Observable<Long> w = Observable.interval(1, TimeUnit.SECONDS, scheduler);
        TestObserver<Long> to = new TestObserver<Long>();
        w.subscribe(to);
        to.assertNoValues();
        to.assertNoErrors();
        to.assertNotComplete();
        scheduler.advanceTimeTo(2, TimeUnit.SECONDS);
        to.assertValues(0L, 1L);
        to.assertNoErrors();
        to.assertNotComplete();
        to.dispose();
        scheduler.advanceTimeTo(4, TimeUnit.SECONDS);
        to.assertValues(0L, 1L);
        to.assertNoErrors();
        to.assertNotComplete();
    }

    @Test
    public void testWithMultipleSubscribersStartingAtSameTime() {
        Observable<Long> w = Observable.interval(1, TimeUnit.SECONDS, scheduler);
        TestObserver<Long> to1 = new TestObserver<Long>();
        TestObserver<Long> to2 = new TestObserver<Long>();
        w.subscribe(to1);
        w.subscribe(to2);
        to1.assertNoValues();
        to2.assertNoValues();
        scheduler.advanceTimeTo(2, TimeUnit.SECONDS);
        to1.assertValues(0L, 1L);
        to1.assertNoErrors();
        to1.assertNotComplete();
        to2.assertValues(0L, 1L);
        to2.assertNoErrors();
        to2.assertNotComplete();
        to1.dispose();
        to2.dispose();
        scheduler.advanceTimeTo(4, TimeUnit.SECONDS);
        to1.assertValues(0L, 1L);
        to1.assertNoErrors();
        to1.assertNotComplete();
        to2.assertValues(0L, 1L);
        to2.assertNoErrors();
        to2.assertNotComplete();
    }

    @Test
    public void testWithMultipleStaggeredSubscribers() {
        Observable<Long> w = Observable.interval(1, TimeUnit.SECONDS, scheduler);
        TestObserver<Long> to1 = new TestObserver<Long>();
        w.subscribe(to1);
        to1.assertNoErrors();
        scheduler.advanceTimeTo(2, TimeUnit.SECONDS);
        TestObserver<Long> to2 = new TestObserver<Long>();
        w.subscribe(to2);
        to1.assertValues(0L, 1L);
        to1.assertNoErrors();
        to1.assertNotComplete();
        to2.assertNoValues();
        scheduler.advanceTimeTo(4, TimeUnit.SECONDS);
        to1.assertValues(0L, 1L, 2L, 3L);
        to2.assertValues(0L, 1L);
        to1.dispose();
        to2.dispose();
        to1.assertValues(0L, 1L, 2L, 3L);
        to1.assertNoErrors();
        to1.assertNotComplete();
        to2.assertValues(0L, 1L);
        to2.assertNoErrors();
        to2.assertNotComplete();
    }

    @Test
    public void testWithMultipleStaggeredSubscribersAndPublish() {
        ConnectableObservable<Long> w = Observable.interval(1, TimeUnit.SECONDS, scheduler).publish();
        TestObserver<Long> to1 = new TestObserver<Long>();
        w.subscribe(to1);
        w.connect();
        to1.assertNoValues();
        scheduler.advanceTimeTo(2, TimeUnit.SECONDS);
        TestObserver<Long> to2 = new TestObserver<Long>();
        w.subscribe(to2);
        to1.assertValues(0L, 1L);
        to1.assertNoErrors();
        to1.assertNotComplete();
        to2.assertNoValues();
        scheduler.advanceTimeTo(4, TimeUnit.SECONDS);
        to1.assertValues(0L, 1L, 2L, 3L);
        to2.assertValues(2L, 3L);
        to1.dispose();
        to2.dispose();
        to1.assertValues(0L, 1L, 2L, 3L);
        to1.assertNoErrors();
        to1.assertNotComplete();
        to2.assertValues(2L, 3L);
        to2.assertNoErrors();
        to2.assertNotComplete();
    }

    @Test
    public void testOnceObserverThrows() {
        Observable<Long> source = Observable.timer(100, TimeUnit.MILLISECONDS, scheduler);
        source.safeSubscribe(new DefaultObserver<Long>() {
            @Override
            public void onNext(Long t) {
                throw new TestException();
            }

            @Override
            public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override
            public void onComplete() {
                observer.onComplete();
            }
        });
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        Mockito.verify(observer).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testPeriodicObserverThrows() {
        Observable<Long> source = Observable.interval(100, 100, TimeUnit.MILLISECONDS, scheduler);
        InOrder inOrder = Mockito.inOrder(observer);
        source.safeSubscribe(new DefaultObserver<Long>() {
            @Override
            public void onNext(Long t) {
                if (t > 0) {
                    throw new TestException();
                }
                observer.onNext(t);
            }

            @Override
            public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override
            public void onComplete() {
                observer.onComplete();
            }
        });
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        inOrder.verify(observer).onNext(0L);
        inOrder.verify(observer).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(Observable.timer(1, TimeUnit.DAYS));
    }

    @Test
    public void timerDelayZero() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            for (int i = 0; i < 1000; i++) {
                Observable.timer(0, TimeUnit.MILLISECONDS).blockingFirst();
            }
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void timerInterruptible() throws Exception {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        try {
            for (Scheduler s : new Scheduler[]{ Schedulers.single(), Schedulers.computation(), Schedulers.newThread(), Schedulers.io(), Schedulers.from(exec) }) {
                final AtomicBoolean interrupted = new AtomicBoolean();
                TestObserver<Long> to = Observable.timer(1, TimeUnit.MILLISECONDS, s).map(new io.reactivex.functions.Function<Long, Long>() {
                    @Override
                    public Long apply(Long v) throws Exception {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex) {
                            interrupted.set(true);
                        }
                        return v;
                    }
                }).test();
                Thread.sleep(500);
                to.cancel();
                Thread.sleep(500);
                Assert.assertTrue(s.getClass().getSimpleName(), interrupted.get());
            }
        } finally {
            exec.shutdown();
        }
    }

    @Test
    public void cancelledAndRun() {
        TestObserver<Long> to = new TestObserver<Long>();
        to.onSubscribe(Disposables.empty());
        TimerObserver tm = new TimerObserver(to);
        tm.dispose();
        tm.run();
        to.assertEmpty();
    }
}

