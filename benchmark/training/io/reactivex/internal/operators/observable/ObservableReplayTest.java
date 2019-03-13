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
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.fuseable.HasUpstreamObservableSource;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableReplayTest {
    @Test
    public void testBufferedReplay() {
        PublishSubject<Integer> source = PublishSubject.create();
        ConnectableObservable<Integer> co = source.replay(3);
        co.connect();
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            source.onNext(1);
            source.onNext(2);
            source.onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onNext(1);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(3);
            source.onNext(4);
            source.onComplete();
            inOrder.verify(observer1, Mockito.times(1)).onNext(4);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onNext(4);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testBufferedWindowReplay() {
        PublishSubject<Integer> source = PublishSubject.create();
        TestScheduler scheduler = new TestScheduler();
        ConnectableObservable<Integer> co = source.replay(3, 100, TimeUnit.MILLISECONDS, scheduler);
        co.connect();
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            source.onNext(1);
            scheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS);
            source.onNext(2);
            scheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS);
            source.onNext(3);
            scheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS);
            inOrder.verify(observer1, Mockito.times(1)).onNext(1);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(3);
            source.onNext(4);
            source.onNext(5);
            scheduler.advanceTimeBy(90, TimeUnit.MILLISECONDS);
            inOrder.verify(observer1, Mockito.times(1)).onNext(4);
            inOrder.verify(observer1, Mockito.times(1)).onNext(5);
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            inOrder.verify(observer1, Mockito.times(1)).onNext(4);
            inOrder.verify(observer1, Mockito.times(1)).onNext(5);
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testWindowedReplay() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        ConnectableObservable<Integer> co = source.replay(100, TimeUnit.MILLISECONDS, scheduler);
        co.connect();
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            source.onNext(1);
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            source.onNext(2);
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            source.onNext(3);
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            source.onComplete();
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            inOrder.verify(observer1, Mockito.times(1)).onNext(1);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            inOrder.verify(observer1, Mockito.never()).onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testReplaySelector() {
        final Function<Integer, Integer> dbl = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                return t1 * 2;
            }
        };
        Function<Observable<Integer>, Observable<Integer>> selector = new Function<Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Observable<Integer> t1) {
                return t1.map(dbl);
            }
        };
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> co = source.replay(selector);
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            source.onNext(1);
            source.onNext(2);
            source.onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(4);
            inOrder.verify(observer1, Mockito.times(1)).onNext(6);
            source.onNext(4);
            source.onComplete();
            inOrder.verify(observer1, Mockito.times(1)).onNext(8);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testBufferedReplaySelector() {
        final Function<Integer, Integer> dbl = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                return t1 * 2;
            }
        };
        Function<Observable<Integer>, Observable<Integer>> selector = new Function<Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Observable<Integer> t1) {
                return t1.map(dbl);
            }
        };
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> co = source.replay(selector, 3);
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            source.onNext(1);
            source.onNext(2);
            source.onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(4);
            inOrder.verify(observer1, Mockito.times(1)).onNext(6);
            source.onNext(4);
            source.onComplete();
            inOrder.verify(observer1, Mockito.times(1)).onNext(8);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testWindowedReplaySelector() {
        final Function<Integer, Integer> dbl = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                return t1 * 2;
            }
        };
        Function<Observable<Integer>, Observable<Integer>> selector = new Function<Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Observable<Integer> t1) {
                return t1.map(dbl);
            }
        };
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> co = source.replay(selector, 100, TimeUnit.MILLISECONDS, scheduler);
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            source.onNext(1);
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            source.onNext(2);
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            source.onNext(3);
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            source.onComplete();
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(4);
            inOrder.verify(observer1, Mockito.times(1)).onNext(6);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            inOrder.verify(observer1, Mockito.times(1)).onComplete();
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void testBufferedReplayError() {
        PublishSubject<Integer> source = PublishSubject.create();
        ConnectableObservable<Integer> co = source.replay(3);
        co.connect();
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            source.onNext(1);
            source.onNext(2);
            source.onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onNext(1);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(3);
            source.onNext(4);
            source.onError(new RuntimeException("Forced failure"));
            inOrder.verify(observer1, Mockito.times(1)).onNext(4);
            inOrder.verify(observer1, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onComplete();
        }
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onNext(4);
            inOrder.verify(observer1, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onComplete();
        }
    }

    @Test
    public void testWindowedReplayError() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        ConnectableObservable<Integer> co = source.replay(100, TimeUnit.MILLISECONDS, scheduler);
        co.connect();
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            source.onNext(1);
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            source.onNext(2);
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            source.onNext(3);
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            source.onError(new RuntimeException("Forced failure"));
            scheduler.advanceTimeBy(60, TimeUnit.MILLISECONDS);
            inOrder.verify(observer1, Mockito.times(1)).onNext(1);
            inOrder.verify(observer1, Mockito.times(1)).onNext(2);
            inOrder.verify(observer1, Mockito.times(1)).onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onComplete();
        }
        {
            Observer<Object> observer1 = mockObserver();
            InOrder inOrder = Mockito.inOrder(observer1);
            co.subscribe(observer1);
            inOrder.verify(observer1, Mockito.never()).onNext(3);
            inOrder.verify(observer1, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
            inOrder.verifyNoMoreInteractions();
            Mockito.verify(observer1, Mockito.never()).onComplete();
        }
    }

    @Test
    public void testSynchronousDisconnect() {
        final AtomicInteger effectCounter = new AtomicInteger();
        Observable<Integer> source = Observable.just(1, 2, 3, 4).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) {
                effectCounter.incrementAndGet();
                System.out.println(("Sideeffect #" + v));
            }
        });
        Observable<Integer> result = source.replay(new Function<Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Observable<Integer> o) {
                return o.take(2);
            }
        });
        for (int i = 1; i < 3; i++) {
            effectCounter.set(0);
            System.out.printf("- %d -%n", i);
            result.subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer t1) {
                    System.out.println(t1);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t1) {
                    t1.printStackTrace();
                }
            }, new Action() {
                @Override
                public void run() {
                    System.out.println("Done");
                }
            });
            Assert.assertEquals(2, effectCounter.get());
        }
    }

    /* test the basic expectation of OperatorMulticast via replay */
    @SuppressWarnings("unchecked")
    @Test
    public void testIssue2191_UnsubscribeSource() throws Exception {
        // setup mocks
        Consumer<Integer> sourceNext = Mockito.mock(io.reactivex.Consumer.class);
        Action sourceCompleted = Mockito.mock(io.reactivex.disposables.Action.class);
        Action sourceUnsubscribed = Mockito.mock(io.reactivex.disposables.Action.class);
        Observer<Integer> spiedSubscriberBeforeConnect = mockObserver();
        Observer<Integer> spiedSubscriberAfterConnect = mockObserver();
        // Observable under test
        Observable<Integer> source = Observable.just(1, 2);
        ConnectableObservable<Integer> replay = source.doOnNext(sourceNext).doOnDispose(sourceUnsubscribed).doOnComplete(sourceCompleted).replay();
        replay.subscribe(spiedSubscriberBeforeConnect);
        replay.subscribe(spiedSubscriberBeforeConnect);
        replay.connect();
        replay.subscribe(spiedSubscriberAfterConnect);
        replay.subscribe(spiedSubscriberAfterConnect);
        Mockito.verify(spiedSubscriberBeforeConnect, Mockito.times(2)).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(spiedSubscriberAfterConnect, Mockito.times(2)).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        // verify interactions
        Mockito.verify(sourceNext, Mockito.times(1)).accept(1);
        Mockito.verify(sourceNext, Mockito.times(1)).accept(2);
        Mockito.verify(sourceCompleted, Mockito.times(1)).run();
        verifyObserverMock(spiedSubscriberBeforeConnect, 2, 4);
        verifyObserverMock(spiedSubscriberAfterConnect, 2, 4);
        // verify(sourceUnsubscribed, times(1)).run();
        Mockito.verifyNoMoreInteractions(sourceNext);
        Mockito.verifyNoMoreInteractions(sourceCompleted);
        Mockito.verifyNoMoreInteractions(sourceUnsubscribed);
        Mockito.verifyNoMoreInteractions(spiedSubscriberBeforeConnect);
        Mockito.verifyNoMoreInteractions(spiedSubscriberAfterConnect);
    }

    /**
     * Specifically test interaction with a Scheduler with subscribeOn.
     *
     * @throws Exception
     * 		functional interfaces are declared with throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testIssue2191_SchedulerUnsubscribe() throws Exception {
        // setup mocks
        Consumer<Integer> sourceNext = Mockito.mock(io.reactivex.Consumer.class);
        Action sourceCompleted = Mockito.mock(io.reactivex.disposables.Action.class);
        Action sourceUnsubscribed = Mockito.mock(io.reactivex.disposables.Action.class);
        final TestScheduler mockScheduler = new TestScheduler();
        Observer<Integer> mockObserverBeforeConnect = mockObserver();
        Observer<Integer> mockObserverAfterConnect = mockObserver();
        // Observable under test
        ConnectableObservable<Integer> replay = Observable.just(1, 2, 3).doOnNext(sourceNext).doOnDispose(sourceUnsubscribed).doOnComplete(sourceCompleted).subscribeOn(mockScheduler).replay();
        replay.subscribe(mockObserverBeforeConnect);
        replay.connect();
        replay.subscribe(mockObserverAfterConnect);
        Mockito.verify(mockObserverBeforeConnect).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(mockObserverAfterConnect).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        mockScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // verify interactions
        Mockito.verify(sourceNext, Mockito.times(1)).accept(1);
        Mockito.verify(sourceNext, Mockito.times(1)).accept(2);
        Mockito.verify(sourceNext, Mockito.times(1)).accept(3);
        Mockito.verify(sourceCompleted, Mockito.times(1)).run();
        verifyObserverMock(mockObserverBeforeConnect, 1, 3);
        verifyObserverMock(mockObserverAfterConnect, 1, 3);
        // FIXME not supported
        // verify(spiedWorker, times(1)).isUnsubscribed();
        // FIXME publish calls cancel too
        // verify(sourceUnsubscribed, times(1)).run();
        Mockito.verifyNoMoreInteractions(sourceNext);
        Mockito.verifyNoMoreInteractions(sourceCompleted);
        Mockito.verifyNoMoreInteractions(sourceUnsubscribed);
        Mockito.verifyNoMoreInteractions(mockObserverBeforeConnect);
        Mockito.verifyNoMoreInteractions(mockObserverAfterConnect);
    }

    /**
     * Specifically test interaction with a Scheduler with subscribeOn.
     *
     * @throws Exception
     * 		functional interfaces are declared with throws Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testIssue2191_SchedulerUnsubscribeOnError() throws Exception {
        // setup mocks
        Consumer<Integer> sourceNext = Mockito.mock(io.reactivex.Consumer.class);
        Action sourceCompleted = Mockito.mock(io.reactivex.disposables.Action.class);
        Consumer<Throwable> sourceError = Mockito.mock(io.reactivex.Consumer.class);
        Action sourceUnsubscribed = Mockito.mock(io.reactivex.disposables.Action.class);
        final TestScheduler mockScheduler = new TestScheduler();
        Observer<Integer> mockObserverBeforeConnect = mockObserver();
        Observer<Integer> mockObserverAfterConnect = mockObserver();
        // Observable under test
        Function<Integer, Integer> mockFunc = Mockito.mock(io.reactivex.Function.class);
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException();
        Mockito.when(mockFunc.apply(1)).thenReturn(1);
        Mockito.when(mockFunc.apply(2)).thenThrow(illegalArgumentException);
        ConnectableObservable<Integer> replay = Observable.just(1, 2, 3).map(mockFunc).doOnNext(sourceNext).doOnDispose(sourceUnsubscribed).doOnComplete(sourceCompleted).doOnError(sourceError).subscribeOn(mockScheduler).replay();
        replay.subscribe(mockObserverBeforeConnect);
        replay.connect();
        replay.subscribe(mockObserverAfterConnect);
        Mockito.verify(mockObserverBeforeConnect).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        Mockito.verify(mockObserverAfterConnect).onSubscribe(((Disposable) (ArgumentMatchers.any())));
        mockScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        // verify interactions
        Mockito.verify(sourceNext, Mockito.times(1)).accept(1);
        Mockito.verify(sourceError, Mockito.times(1)).accept(illegalArgumentException);
        verifyObserver(mockObserverBeforeConnect, 1, 1, illegalArgumentException);
        verifyObserver(mockObserverAfterConnect, 1, 1, illegalArgumentException);
        // FIXME no longer supported
        // verify(spiedWorker, times(1)).isUnsubscribed();
        // FIXME publish also calls cancel
        // verify(sourceUnsubscribed, times(1)).run();
        Mockito.verifyNoMoreInteractions(sourceNext);
        Mockito.verifyNoMoreInteractions(sourceCompleted);
        Mockito.verifyNoMoreInteractions(sourceError);
        Mockito.verifyNoMoreInteractions(sourceUnsubscribed);
        Mockito.verifyNoMoreInteractions(mockObserverBeforeConnect);
        Mockito.verifyNoMoreInteractions(mockObserverAfterConnect);
    }

    static class InprocessWorker extends Worker {
        private final io.reactivex.disposables.Disposable mockDisposable;

        public boolean unsubscribed;

        InprocessWorker(Disposable mockDisposable) {
            this.mockDisposable = mockDisposable;
        }

        @NonNull
        @Override
        public io.reactivex.disposables.Disposable schedule(@NonNull
        Runnable action) {
            action.run();
            return mockDisposable;// this subscription is returned but discarded

        }

        @NonNull
        @Override
        public io.reactivex.disposables.Disposable schedule(@NonNull
        Runnable action, long delayTime, @NonNull
        TimeUnit unit) {
            action.run();
            return mockDisposable;
        }

        @Override
        public void dispose() {
            unsubscribed = true;
        }

        @Override
        public boolean isDisposed() {
            return unsubscribed;
        }
    }

    @Test
    public void testBoundedReplayBuffer() {
        BoundedReplayBuffer<Integer> buf = new BoundedReplayBuffer<Integer>() {
            private static final long serialVersionUID = -5182053207244406872L;

            @Override
            void truncate() {
            }
        };
        buf.addLast(new Node(1));
        buf.addLast(new Node(2));
        buf.addLast(new Node(3));
        buf.addLast(new Node(4));
        buf.addLast(new Node(5));
        List<Integer> values = new ArrayList<Integer>();
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), values);
        buf.removeSome(2);
        buf.removeFirst();
        buf.removeSome(2);
        values.clear();
        buf.collect(values);
        Assert.assertTrue(values.isEmpty());
        buf.addLast(new Node(5));
        buf.addLast(new Node(6));
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(5, 6), values);
    }

    @Test
    public void testTimedAndSizedTruncation() {
        TestScheduler test = new TestScheduler();
        SizeAndTimeBoundReplayBuffer<Integer> buf = new SizeAndTimeBoundReplayBuffer<Integer>(2, 2000, TimeUnit.MILLISECONDS, test);
        List<Integer> values = new ArrayList<Integer>();
        buf.next(1);
        test.advanceTimeBy(1, TimeUnit.SECONDS);
        buf.next(2);
        test.advanceTimeBy(1, TimeUnit.SECONDS);
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(2), values);
        buf.next(3);
        buf.next(4);
        values.clear();
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(3, 4), values);
        test.advanceTimeBy(2, TimeUnit.SECONDS);
        buf.next(5);
        values.clear();
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(5), values);
        Assert.assertFalse(buf.hasCompleted());
        test.advanceTimeBy(2, TimeUnit.SECONDS);
        buf.complete();
        values.clear();
        buf.collect(values);
        Assert.assertTrue(values.isEmpty());
        Assert.assertEquals(1, buf.size);
        Assert.assertTrue(buf.hasCompleted());
        Assert.assertFalse(buf.hasError());
    }

    @Test
    public void testTimedAndSizedTruncationError() {
        TestScheduler test = new TestScheduler();
        SizeAndTimeBoundReplayBuffer<Integer> buf = new SizeAndTimeBoundReplayBuffer<Integer>(2, 2000, TimeUnit.MILLISECONDS, test);
        Assert.assertFalse(buf.hasCompleted());
        Assert.assertFalse(buf.hasError());
        List<Integer> values = new ArrayList<Integer>();
        buf.next(1);
        test.advanceTimeBy(1, TimeUnit.SECONDS);
        buf.next(2);
        test.advanceTimeBy(1, TimeUnit.SECONDS);
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(2), values);
        buf.next(3);
        buf.next(4);
        values.clear();
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(3, 4), values);
        test.advanceTimeBy(2, TimeUnit.SECONDS);
        buf.next(5);
        values.clear();
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(5), values);
        Assert.assertFalse(buf.hasCompleted());
        Assert.assertFalse(buf.hasError());
        test.advanceTimeBy(2, TimeUnit.SECONDS);
        buf.error(new TestException());
        values.clear();
        buf.collect(values);
        Assert.assertTrue(values.isEmpty());
        Assert.assertEquals(1, buf.size);
        Assert.assertFalse(buf.hasCompleted());
        Assert.assertTrue(buf.hasError());
    }

    @Test
    public void testSizedTruncation() {
        SizeBoundReplayBuffer<Integer> buf = new SizeBoundReplayBuffer<Integer>(2);
        List<Integer> values = new ArrayList<Integer>();
        buf.next(1);
        buf.next(2);
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(1, 2), values);
        buf.next(3);
        buf.next(4);
        values.clear();
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(3, 4), values);
        buf.next(5);
        values.clear();
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(4, 5), values);
        Assert.assertFalse(buf.hasCompleted());
        buf.complete();
        values.clear();
        buf.collect(values);
        Assert.assertEquals(Arrays.asList(4, 5), values);
        Assert.assertEquals(3, buf.size);
        Assert.assertTrue(buf.hasCompleted());
        Assert.assertFalse(buf.hasError());
    }

    @Test
    public void testColdReplayNoBackpressure() {
        Observable<Integer> source = range(0, 1000).replay().autoConnect();
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.subscribe(to);
        to.assertNoErrors();
        to.assertTerminated();
        List<Integer> onNextEvents = to.values();
        Assert.assertEquals(1000, onNextEvents.size());
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(((Integer) (i)), onNextEvents.get(i));
        }
    }

    @Test
    public void testCache() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger();
        Observable<String> o = Observable.unsafeCreate(new ObservableSource<String>() {
            @Override
            public void subscribe(final Observer<? super String> observer) {
                observer.onSubscribe(Disposables.empty());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        counter.incrementAndGet();
                        System.out.println("published Observable being executed");
                        observer.onNext("one");
                        observer.onComplete();
                    }
                }).start();
            }
        }).replay().autoConnect();
        // we then expect the following 2 subscriptions to get that same value
        final CountDownLatch latch = new CountDownLatch(2);
        // subscribe once
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String v) {
                Assert.assertEquals("one", v);
                System.out.println(("v: " + v));
                latch.countDown();
            }
        });
        // subscribe again
        o.subscribe(new Consumer<String>() {
            @Override
            public void accept(String v) {
                Assert.assertEquals("one", v);
                System.out.println(("v: " + v));
                latch.countDown();
            }
        });
        if (!(latch.await(1000, TimeUnit.MILLISECONDS))) {
            Assert.fail("subscriptions did not receive values");
        }
        Assert.assertEquals(1, counter.get());
    }

    @Test
    public void testUnsubscribeSource() throws Exception {
        Action unsubscribe = Mockito.mock(io.reactivex.disposables.Action.class);
        Observable<Integer> o = just(1).doOnDispose(unsubscribe).replay().autoConnect();
        o.subscribe();
        o.subscribe();
        o.subscribe();
        Mockito.verify(unsubscribe, Mockito.never()).run();
    }

    @Test
    public void testTake() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable<Integer> cached = range(1, 100).replay().autoConnect();
        cached.take(10).subscribe(to);
        to.assertNoErrors();
        to.assertTerminated();
        to.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // FIXME no longer assertable
        // ts.assertUnsubscribed();
    }

    @Test
    public void testAsync() {
        Observable<Integer> source = range(1, 10000);
        for (int i = 0; i < 100; i++) {
            TestObserver<Integer> to1 = new TestObserver<Integer>();
            Observable<Integer> cached = source.replay().autoConnect();
            cached.observeOn(Schedulers.computation()).subscribe(to1);
            to1.awaitTerminalEvent(2, TimeUnit.SECONDS);
            to1.assertNoErrors();
            to1.assertTerminated();
            Assert.assertEquals(10000, to1.values().size());
            TestObserver<Integer> to2 = new TestObserver<Integer>();
            cached.observeOn(Schedulers.computation()).subscribe(to2);
            to2.awaitTerminalEvent(2, TimeUnit.SECONDS);
            to2.assertNoErrors();
            to2.assertTerminated();
            Assert.assertEquals(10000, to2.values().size());
        }
    }

    @Test
    public void testAsyncComeAndGo() {
        Observable<Long> source = interval(1, 1, TimeUnit.MILLISECONDS).take(1000).subscribeOn(Schedulers.io());
        Observable<Long> cached = source.replay().autoConnect();
        Observable<Long> output = cached.observeOn(Schedulers.computation());
        List<TestObserver<Long>> list = new ArrayList<TestObserver<Long>>(100);
        for (int i = 0; i < 100; i++) {
            TestObserver<Long> to = new TestObserver<Long>();
            list.add(to);
            output.skip((i * 10)).take(10).subscribe(to);
        }
        List<Long> expected = new ArrayList<Long>();
        for (int i = 0; i < 10; i++) {
            expected.add(((long) (i - 10)));
        }
        int j = 0;
        for (TestObserver<Long> to : list) {
            to.awaitTerminalEvent(3, TimeUnit.SECONDS);
            to.assertNoErrors();
            to.assertTerminated();
            for (int i = j * 10; i < ((j * 10) + 10); i++) {
                expected.set((i - (j * 10)), ((long) (i)));
            }
            to.assertValueSequence(expected);
            j++;
        }
    }

    @Test
    public void testNoMissingBackpressureException() {
        final int m = (4 * 1000) * 1000;
        Observable<Integer> firehose = unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> t) {
                t.onSubscribe(Disposables.empty());
                for (int i = 0; i < m; i++) {
                    t.onNext(i);
                }
                t.onComplete();
            }
        });
        TestObserver<Integer> to = new TestObserver<Integer>();
        firehose.replay().autoConnect().observeOn(Schedulers.computation()).takeLast(100).subscribe(to);
        to.awaitTerminalEvent(3, TimeUnit.SECONDS);
        to.assertNoErrors();
        to.assertTerminated();
        Assert.assertEquals(100, to.values().size());
    }

    @Test
    public void testValuesAndThenError() {
        Observable<Integer> source = range(1, 10).concatWith(<Integer>error(new TestException())).replay().autoConnect();
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.subscribe(to);
        to.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        to.assertNotComplete();
        Assert.assertEquals(1, to.errors().size());
        TestObserver<Integer> to2 = new TestObserver<Integer>();
        source.subscribe(to2);
        to2.assertValues(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        to2.assertNotComplete();
        Assert.assertEquals(1, to2.errors().size());
    }

    @Test
    public void replayScheduler() {
        just(1).replay(Schedulers.computation()).autoConnect().test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void replayTime() {
        just(1).replay(1, TimeUnit.MINUTES).autoConnect().test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void replaySizeScheduler() {
        just(1).replay(1, Schedulers.computation()).autoConnect().test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void replaySizeAndTime() {
        just(1).replay(1, 1, TimeUnit.MILLISECONDS).autoConnect().test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void replaySelectorSizeScheduler() {
        just(1).replay(Functions.<Observable<Integer>>identity(), 1, Schedulers.io()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void replaySelectorScheduler() {
        just(1).replay(Functions.<Observable<Integer>>identity(), Schedulers.io()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void replaySelectorTime() {
        just(1).replay(Functions.<Observable<Integer>>identity(), 1, TimeUnit.MINUTES).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void replayMaxInt() {
        range(1, 2).replay(Integer.MAX_VALUE).autoConnect().test().assertResult(1, 2);
    }

    @Test
    public void source() {
        Observable<Integer> source = range(1, 3);
        Assert.assertSame(source, ((HasUpstreamObservableSource<?>) (source.replay())).source());
    }

    @Test
    public void connectRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ConnectableObservable<Integer> co = range(1, 3).replay();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    co.connect();
                }
            };
            TestHelper.race(r, r);
        }
    }

    @Test
    public void subscribeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ConnectableObservable<Integer> co = range(1, 3).replay();
            final TestObserver<Integer> to1 = new TestObserver<Integer>();
            final TestObserver<Integer> to2 = new TestObserver<Integer>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    co.subscribe(to1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    co.subscribe(to2);
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void addRemoveRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ConnectableObservable<Integer> co = range(1, 3).replay();
            final TestObserver<Integer> to1 = new TestObserver<Integer>();
            final TestObserver<Integer> to2 = new TestObserver<Integer>();
            co.subscribe(to1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to1.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    co.subscribe(to2);
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void cancelOnArrival() {
        range(1, 2).replay(Integer.MAX_VALUE).autoConnect().test(true).assertEmpty();
    }

    @Test
    public void cancelOnArrival2() {
        ConnectableObservable<Integer> co = PublishSubject.<Integer>create().replay(Integer.MAX_VALUE);
        co.test();
        co.autoConnect().test(true).assertEmpty();
    }

    @Test
    public void connectConsumerThrows() {
        ConnectableObservable<Integer> co = range(1, 2).replay();
        try {
            co.connect(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable t) throws Exception {
                    throw new TestException();
                }
            });
            Assert.fail("Should have thrown");
        } catch (TestException ex) {
            // expected
        }
        cancel();
        co.connect();
        co.test().assertResult(1, 2);
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? extends Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onError(new TestException("First"));
                    observer.onNext(1);
                    observer.onError(new TestException("Second"));
                    observer.onComplete();
                }
            }.replay().autoConnect().test().assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void subscribeOnNextRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final ConnectableObservable<Integer> co = ps.replay();
            final TestObserver<Integer> to1 = new TestObserver<Integer>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    co.subscribe(to1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        ps.onNext(j);
                    }
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void unsubscribeOnNextRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final ConnectableObservable<Integer> co = ps.replay();
            final TestObserver<Integer> to1 = new TestObserver<Integer>();
            co.subscribe(to1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to1.dispose();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        ps.onNext(j);
                    }
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void unsubscribeReplayRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final ConnectableObservable<Integer> co = range(1, 1000).replay();
            final TestObserver<Integer> to1 = new TestObserver<Integer>();
            co.connect();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    co.subscribe(to1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to1.dispose();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void reentrantOnNext() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
                super.onNext(t);
            }
        };
        ps.replay().autoConnect().subscribe(to);
        ps.onNext(1);
        to.assertResult(1, 2);
    }

    @Test
    public void reentrantOnNextBound() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                if (t == 1) {
                    ps.onNext(2);
                    ps.onComplete();
                }
                super.onNext(t);
            }
        };
        ps.replay(10).autoConnect().subscribe(to);
        ps.onNext(1);
        to.assertResult(1, 2);
    }

    @Test
    public void reentrantOnNextCancel() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                if (t == 1) {
                    ps.onNext(2);
                    cancel();
                }
                super.onNext(t);
            }
        };
        ps.replay().autoConnect().subscribe(to);
        ps.onNext(1);
        to.assertValues(1);
    }

    @Test
    public void reentrantOnNextCancelBounded() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                if (t == 1) {
                    ps.onNext(2);
                    cancel();
                }
                super.onNext(t);
            }
        };
        ps.replay(10).autoConnect().subscribe(to);
        ps.onNext(1);
        to.assertValues(1);
    }

    @Test
    public void delayedUpstreamOnSubscribe() {
        final Observer<?>[] sub = { null };
        new Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? extends Integer> observer) {
                sub[0] = observer;
            }
        }.replay().connect().dispose();
        Disposable bs = Disposables.empty();
        sub[0].onSubscribe(bs);
        Assert.assertTrue(bs.isDisposed());
    }

    @Test
    public void timedNoOutdatedData() {
        TestScheduler scheduler = new TestScheduler();
        Observable<Integer> source = just(1).replay(2, TimeUnit.SECONDS, scheduler).autoConnect();
        source.test().assertResult(1);
        source.test().assertResult(1);
        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        source.test().assertResult();
    }

    @Test
    public void replaySelectorReturnsNullScheduled() {
        just(1).replay(new Function<Observable<Integer>, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Observable<Integer> v) throws Exception {
                return null;
            }
        }, Schedulers.trampoline()).test().assertFailureAndMessage(NullPointerException.class, "The selector returned a null ObservableSource");
    }

    @Test
    public void replaySelectorReturnsNull() {
        just(1).replay(new Function<Observable<Integer>, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Observable<Integer> v) throws Exception {
                return null;
            }
        }).test().assertFailureAndMessage(NullPointerException.class, "The selector returned a null ObservableSource");
    }

    @Test
    public void replaySelectorConnectableReturnsNull() {
        ObservableReplay.multicastSelector(Functions.justCallable(((ConnectableObservable<Integer>) (null))), Functions.justFunction(just(1))).test().assertFailureAndMessage(NullPointerException.class, "The connectableFactory returned a null ConnectableObservable");
    }

    @Test
    public void noHeadRetentionCompleteSize() {
        PublishSubject<Integer> source = PublishSubject.create();
        ObservableReplay<Integer> co = ((ObservableReplay<Integer>) (source.replay(1)));
        co.connect();
        BoundedReplayBuffer<Integer> buf = ((BoundedReplayBuffer<Integer>) (co.current.get().buffer));
        source.onNext(1);
        source.onNext(2);
        source.onComplete();
        Assert.assertNull(buf.get().value);
        Object o = buf.get();
        buf.trimHead();
        Assert.assertSame(o, buf.get());
    }

    @Test
    public void noHeadRetentionErrorSize() {
        PublishSubject<Integer> source = PublishSubject.create();
        ObservableReplay<Integer> co = ((ObservableReplay<Integer>) (source.replay(1)));
        co.connect();
        BoundedReplayBuffer<Integer> buf = ((BoundedReplayBuffer<Integer>) (co.current.get().buffer));
        source.onNext(1);
        source.onNext(2);
        source.onError(new TestException());
        Assert.assertNull(buf.get().value);
        Object o = buf.get();
        buf.trimHead();
        Assert.assertSame(o, buf.get());
    }

    @Test
    public void noHeadRetentionSize() {
        PublishSubject<Integer> source = PublishSubject.create();
        ObservableReplay<Integer> co = ((ObservableReplay<Integer>) (source.replay(1)));
        co.connect();
        BoundedReplayBuffer<Integer> buf = ((BoundedReplayBuffer<Integer>) (co.current.get().buffer));
        source.onNext(1);
        source.onNext(2);
        Assert.assertNotNull(buf.get().value);
        buf.trimHead();
        Assert.assertNull(buf.get().value);
        Object o = buf.get();
        buf.trimHead();
        Assert.assertSame(o, buf.get());
    }

    @Test
    public void noHeadRetentionCompleteTime() {
        PublishSubject<Integer> source = PublishSubject.create();
        ObservableReplay<Integer> co = ((ObservableReplay<Integer>) (source.replay(1, TimeUnit.MINUTES, Schedulers.computation())));
        co.connect();
        BoundedReplayBuffer<Integer> buf = ((BoundedReplayBuffer<Integer>) (co.current.get().buffer));
        source.onNext(1);
        source.onNext(2);
        source.onComplete();
        Assert.assertNull(buf.get().value);
        Object o = buf.get();
        buf.trimHead();
        Assert.assertSame(o, buf.get());
    }

    @Test
    public void noHeadRetentionErrorTime() {
        PublishSubject<Integer> source = PublishSubject.create();
        ObservableReplay<Integer> co = ((ObservableReplay<Integer>) (source.replay(1, TimeUnit.MINUTES, Schedulers.computation())));
        co.connect();
        BoundedReplayBuffer<Integer> buf = ((BoundedReplayBuffer<Integer>) (co.current.get().buffer));
        source.onNext(1);
        source.onNext(2);
        source.onError(new TestException());
        Assert.assertNull(buf.get().value);
        Object o = buf.get();
        buf.trimHead();
        Assert.assertSame(o, buf.get());
    }

    @Test
    public void noHeadRetentionTime() {
        TestScheduler sch = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        ObservableReplay<Integer> co = ((ObservableReplay<Integer>) (source.replay(1, TimeUnit.MILLISECONDS, sch)));
        co.connect();
        BoundedReplayBuffer<Integer> buf = ((BoundedReplayBuffer<Integer>) (co.current.get().buffer));
        source.onNext(1);
        sch.advanceTimeBy(2, TimeUnit.MILLISECONDS);
        source.onNext(2);
        Assert.assertNotNull(buf.get().value);
        buf.trimHead();
        Assert.assertNull(buf.get().value);
        Object o = buf.get();
        buf.trimHead();
        Assert.assertSame(o, buf.get());
    }

    @Test
    public void noBoundedRetentionViaThreadLocal() throws Exception {
        Observable<byte[]> source = range(1, 200).map(new Function<Integer, byte[]>() {
            @Override
            public byte[] apply(Integer v) throws Exception {
                return new byte[1024 * 1024];
            }
        }).replay(new Function<Observable<byte[]>, Observable<byte[]>>() {
            @Override
            public Observable<byte[]> apply(final Observable<byte[]> o) throws Exception {
                return o.take(1).concatMap(new Function<byte[], Observable<byte[]>>() {
                    @Override
                    public Observable<byte[]> apply(byte[] v) throws Exception {
                        return o;
                    }
                });
            }
        }, 1).takeLast(1);
        System.out.println("Bounded Replay Leak check: Wait before GC");
        Thread.sleep(1000);
        System.out.println("Bounded Replay Leak check: GC");
        System.gc();
        Thread.sleep(500);
        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memHeap = memoryMXBean.getHeapMemoryUsage();
        long initial = memHeap.getUsed();
        System.out.printf("Bounded Replay Leak check: Starting: %.3f MB%n", ((initial / 1024.0) / 1024.0));
        final AtomicLong after = new AtomicLong();
        source.subscribe(new Consumer<byte[]>() {
            @Override
            public void accept(byte[] v) throws Exception {
                System.out.println("Bounded Replay Leak check: Wait before GC 2");
                Thread.sleep(1000);
                System.out.println("Bounded Replay Leak check:  GC 2");
                System.gc();
                Thread.sleep(500);
                after.set(memoryMXBean.getHeapMemoryUsage().getUsed());
            }
        });
        System.out.printf("Bounded Replay Leak check: After: %.3f MB%n", (((after.get()) / 1024.0) / 1024.0));
        if ((initial + ((100 * 1024) * 1024)) < (after.get())) {
            Assert.fail(((("Bounded Replay Leak check: Memory leak detected: " + ((initial / 1024.0) / 1024.0)) + " -> ") + (((after.get()) / 1024.0) / 1024.0)));
        }
    }
}

