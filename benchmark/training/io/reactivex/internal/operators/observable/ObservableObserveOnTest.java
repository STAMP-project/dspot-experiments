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


import ImmediateThinScheduler.INSTANCE;
import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import io.reactivex.annotations.Nullable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.operators.flowable.FlowableObserveOnTest;
import io.reactivex.internal.schedulers.ImmediateThinScheduler;
import io.reactivex.observers.ObserverFusion;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.Iterator;
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

import static QueueFuseable.ANY;
import static io.reactivex.observers.TestObserver.<init>;


public class ObservableObserveOnTest {
    /**
     * This is testing a no-op path since it uses Schedulers.immediate() which will not do scheduling.
     */
    @Test
    public void testObserveOn() {
        Observer<Integer> observer = mockObserver();
        Observable.just(1, 2, 3).observeOn(INSTANCE).subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onNext(3);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testOrdering() throws InterruptedException {
        // Observable<String> obs = Observable.just("one", null, "two", "three", "four");
        // FIXME null values not allowed
        Observable<String> obs = Observable.just("one", "null", "two", "three", "four");
        Observer<String> observer = mockObserver();
        InOrder inOrder = Mockito.inOrder(observer);
        TestObserver<String> to = new TestObserver<String>(observer);
        obs.observeOn(Schedulers.computation()).subscribe(to);
        to.awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);
        if ((to.errors().size()) > 0) {
            for (Throwable t : to.errors()) {
                t.printStackTrace();
            }
            Assert.fail("failed with exception");
        }
        inOrder.verify(observer, Mockito.times(1)).onNext("one");
        inOrder.verify(observer, Mockito.times(1)).onNext("null");
        inOrder.verify(observer, Mockito.times(1)).onNext("two");
        inOrder.verify(observer, Mockito.times(1)).onNext("three");
        inOrder.verify(observer, Mockito.times(1)).onNext("four");
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testThreadName() throws InterruptedException {
        System.out.println(("Main Thread: " + (Thread.currentThread().getName())));
        // FIXME null values not allowed
        // Observable<String> obs = Observable.just("one", null, "two", "three", "four");
        Observable<String> obs = Observable.just("one", "null", "two", "three", "four");
        Observer<String> observer = mockObserver();
        final String parentThreadName = Thread.currentThread().getName();
        final CountDownLatch completedLatch = new CountDownLatch(1);
        // assert subscribe is on main thread
        obs = obs.doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) {
                String threadName = Thread.currentThread().getName();
                System.out.println(((("Source ThreadName: " + threadName) + "  Expected => ") + parentThreadName));
                Assert.assertEquals(parentThreadName, threadName);
            }
        });
        // assert observe is on new thread
        obs.observeOn(Schedulers.newThread()).doOnNext(new Consumer<String>() {
            @Override
            public void accept(String t1) {
                String threadName = Thread.currentThread().getName();
                boolean correctThreadName = threadName.startsWith("RxNewThreadScheduler");
                System.out.println(((("ObserveOn ThreadName: " + threadName) + "  Correct => ") + correctThreadName));
                Assert.assertTrue(correctThreadName);
            }
        }).doAfterTerminate(new Action() {
            @Override
            public void run() {
                completedLatch.countDown();
            }
        }).subscribe(observer);
        if (!(completedLatch.await(1000, TimeUnit.MILLISECONDS))) {
            Assert.fail("timed out waiting");
        }
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(5)).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void observeOnTheSameSchedulerTwice() {
        Scheduler scheduler = ImmediateThinScheduler.INSTANCE;
        Observable<Integer> o = Observable.just(1, 2, 3);
        Observable<Integer> o2 = o.observeOn(scheduler);
        Observer<Object> observer1 = mockObserver();
        Observer<Object> observer2 = mockObserver();
        InOrder inOrder1 = Mockito.inOrder(observer1);
        InOrder inOrder2 = Mockito.inOrder(observer2);
        o2.subscribe(observer1);
        o2.subscribe(observer2);
        inOrder1.verify(observer1, Mockito.times(1)).onNext(1);
        inOrder1.verify(observer1, Mockito.times(1)).onNext(2);
        inOrder1.verify(observer1, Mockito.times(1)).onNext(3);
        inOrder1.verify(observer1, Mockito.times(1)).onComplete();
        Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder1.verifyNoMoreInteractions();
        inOrder2.verify(observer2, Mockito.times(1)).onNext(1);
        inOrder2.verify(observer2, Mockito.times(1)).onNext(2);
        inOrder2.verify(observer2, Mockito.times(1)).onNext(3);
        inOrder2.verify(observer2, Mockito.times(1)).onComplete();
        Mockito.verify(observer2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder2.verifyNoMoreInteractions();
    }

    @Test
    public void observeSameOnMultipleSchedulers() {
        TestScheduler scheduler1 = new TestScheduler();
        TestScheduler scheduler2 = new TestScheduler();
        Observable<Integer> o = Observable.just(1, 2, 3);
        Observable<Integer> o1 = o.observeOn(scheduler1);
        Observable<Integer> o2 = o.observeOn(scheduler2);
        Observer<Object> observer1 = mockObserver();
        Observer<Object> observer2 = mockObserver();
        InOrder inOrder1 = Mockito.inOrder(observer1);
        InOrder inOrder2 = Mockito.inOrder(observer2);
        o1.subscribe(observer1);
        o2.subscribe(observer2);
        scheduler1.advanceTimeBy(1, TimeUnit.SECONDS);
        scheduler2.advanceTimeBy(1, TimeUnit.SECONDS);
        inOrder1.verify(observer1, Mockito.times(1)).onNext(1);
        inOrder1.verify(observer1, Mockito.times(1)).onNext(2);
        inOrder1.verify(observer1, Mockito.times(1)).onNext(3);
        inOrder1.verify(observer1, Mockito.times(1)).onComplete();
        Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder1.verifyNoMoreInteractions();
        inOrder2.verify(observer2, Mockito.times(1)).onNext(1);
        inOrder2.verify(observer2, Mockito.times(1)).onNext(2);
        inOrder2.verify(observer2, Mockito.times(1)).onNext(3);
        inOrder2.verify(observer2, Mockito.times(1)).onComplete();
        Mockito.verify(observer2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder2.verifyNoMoreInteractions();
    }

    /**
     * Confirm that running on a NewThreadScheduler uses the same thread for the entire stream.
     */
    @Test
    public void testObserveOnWithNewThreadScheduler() {
        final AtomicInteger count = new AtomicInteger();
        final int _multiple = 99;
        range(1, 100000).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                return t1 * _multiple;
            }
        }).observeOn(Schedulers.newThread()).blockingForEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                Assert.assertEquals(((count.incrementAndGet()) * _multiple), t1.intValue());
                // FIXME toBlocking methods run on the current thread
                String name = Thread.currentThread().getName();
                Assert.assertFalse(("Wrong thread name: " + name), name.startsWith("Rx"));
            }
        });
    }

    /**
     * Confirm that running on a ThreadPoolScheduler allows multiple threads but is still ordered.
     */
    @Test
    public void testObserveOnWithThreadPoolScheduler() {
        final AtomicInteger count = new AtomicInteger();
        final int _multiple = 99;
        range(1, 100000).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                return t1 * _multiple;
            }
        }).observeOn(Schedulers.computation()).blockingForEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                Assert.assertEquals(((count.incrementAndGet()) * _multiple), t1.intValue());
                // FIXME toBlocking methods run on the caller's thread
                String name = Thread.currentThread().getName();
                Assert.assertFalse(("Wrong thread name: " + name), name.startsWith("Rx"));
            }
        });
    }

    /**
     * Attempts to confirm that when pauses exist between events, the ScheduledObserver
     * does not lose or reorder any events since the scheduler will not block, but will
     * be re-scheduled when it receives new events after each pause.
     *
     *
     * This is non-deterministic in proving success, but if it ever fails (non-deterministically)
     * it is a sign of potential issues as thread-races and scheduling should not affect output.
     */
    @Test
    public void testObserveOnOrderingConcurrency() {
        final AtomicInteger count = new AtomicInteger();
        final int _multiple = 99;
        range(1, 10000).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                if ((ObservableObserveOnTest.randomIntFrom0to100()) > 98) {
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return t1 * _multiple;
            }
        }).observeOn(Schedulers.computation()).blockingForEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                Assert.assertEquals(((count.incrementAndGet()) * _multiple), t1.intValue());
                // assertTrue(name.startsWith("RxComputationThreadPool"));
                // FIXME toBlocking now runs its methods on the caller thread
                String name = Thread.currentThread().getName();
                Assert.assertFalse(("Wrong thread name: " + name), name.startsWith("Rx"));
            }
        });
    }

    @Test
    public void testNonBlockingOuterWhileBlockingOnNext() throws InterruptedException {
        final CountDownLatch completedLatch = new CountDownLatch(1);
        final CountDownLatch nextLatch = new CountDownLatch(1);
        final AtomicLong completeTime = new AtomicLong();
        // use subscribeOn to make async, observeOn to move
        range(1, 2).subscribeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onComplete() {
                System.out.println("onComplete");
                completeTime.set(System.nanoTime());
                completedLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
                // don't let this thing finish yet
                try {
                    if (!(nextLatch.await(1000, TimeUnit.MILLISECONDS))) {
                        throw new RuntimeException("it shouldn't have timed out");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException("it shouldn't have failed");
                }
            }
        });
        long afterSubscribeTime = System.nanoTime();
        System.out.println(("After subscribe: " + (completedLatch.getCount())));
        Assert.assertEquals(1, completedLatch.getCount());
        nextLatch.countDown();
        completedLatch.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(((completeTime.get()) > afterSubscribeTime));
        System.out.println(("onComplete nanos after subscribe: " + ((completeTime.get()) - afterSubscribeTime)));
    }

    @Test
    public void testDelayedErrorDeliveryWhenSafeSubscriberUnsubscribes() {
        TestScheduler testScheduler = new TestScheduler();
        Observable<Integer> source = Observable.concat(<Integer>error(new TestException()), just(1));
        Observer<Integer> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.observeOn(testScheduler).subscribe(o);
        inOrder.verify(o, Mockito.never()).onError(ArgumentMatchers.any(TestException.class));
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        inOrder.verify(o).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verify(o, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        inOrder.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testAfterUnsubscribeCalledThenObserverOnNextNeverCalled() {
        final TestScheduler testScheduler = new TestScheduler();
        final Observer<Integer> observer = mockObserver();
        TestObserver<Integer> to = new TestObserver<Integer>(observer);
        Observable.just(1, 2, 3).observeOn(testScheduler).subscribe(to);
        to.dispose();
        testScheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        final InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Exception.class));
        inOrder.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testBackpressureWithTakeBefore() {
        final AtomicInteger generated = new AtomicInteger();
        Observable<Integer> o = fromIterable(new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    @Override
                    public void remove() {
                    }

                    @Override
                    public Integer next() {
                        return generated.getAndIncrement();
                    }

                    @Override
                    public boolean hasNext() {
                        return true;
                    }
                };
            }
        });
        TestObserver<Integer> to = new TestObserver<Integer>();
        o.take(7).observeOn(Schedulers.newThread()).subscribe(to);
        to.awaitTerminalEvent();
        to.assertValues(0, 1, 2, 3, 4, 5, 6);
        Assert.assertEquals(7, generated.get());
    }

    @Test
    public void testAsyncChild() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        range(0, 100000).observeOn(Schedulers.newThread()).observeOn(Schedulers.newThread()).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
    }

    @Test
    public void delayError() {
        range(1, 5).concatWith(<Integer>error(new TestException())).observeOn(Schedulers.computation(), true).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                if (v == 1) {
                    Thread.sleep(100);
                }
            }
        }).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class, 1, 2, 3, 4, 5);
    }

    @Test
    public void trampolineScheduler() {
        just(1).observeOn(Schedulers.trampoline()).test().assertResult(1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().observeOn(new TestScheduler()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.observeOn(new TestScheduler());
            }
        });
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestScheduler scheduler = new TestScheduler();
            TestObserver<Integer> to = new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? extends Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onComplete();
                    observer.onNext(1);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.observeOn(scheduler).test();
            scheduler.triggerActions();
            to.assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void inputSyncFused() {
        range(1, 5).observeOn(Schedulers.single()).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void inputAsyncFused() {
        UnicastSubject<Integer> us = UnicastSubject.create();
        TestObserver<Integer> to = us.observeOn(Schedulers.single()).test();
        TestHelper.emit(us, 1, 2, 3, 4, 5);
        to.awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void inputAsyncFusedError() {
        UnicastSubject<Integer> us = UnicastSubject.create();
        TestObserver<Integer> to = us.observeOn(Schedulers.single()).test();
        us.onError(new TestException());
        to.awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void inputAsyncFusedErrorDelayed() {
        UnicastSubject<Integer> us = UnicastSubject.create();
        TestObserver<Integer> to = us.observeOn(Schedulers.single(), true).test();
        us.onError(new TestException());
        to.awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void outputFused() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        range(1, 5).hide().observeOn(Schedulers.single()).subscribe(to);
        ObserverFusion.assertFusion(to, ASYNC).awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void outputFusedReject() {
        TestObserver<Integer> to = ObserverFusion.newTest(SYNC);
        range(1, 5).hide().observeOn(Schedulers.single()).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void inputOutputAsyncFusedError() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        UnicastSubject<Integer> us = UnicastSubject.create();
        us.observeOn(Schedulers.single()).subscribe(to);
        us.onError(new TestException());
        to.awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
        ObserverFusion.assertFusion(to, ASYNC).awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void inputOutputAsyncFusedErrorDelayed() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        UnicastSubject<Integer> us = UnicastSubject.create();
        us.observeOn(Schedulers.single(), true).subscribe(to);
        us.onError(new TestException());
        to.awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
        ObserverFusion.assertFusion(to, ASYNC).awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void outputFusedCancelReentrant() throws Exception {
        final UnicastSubject<Integer> us = UnicastSubject.create();
        final CountDownLatch cdl = new CountDownLatch(1);
        us.observeOn(Schedulers.single()).subscribe(new Observer<Integer>() {
            Disposable upstream;

            int count;

            @Override
            public void onSubscribe(Disposable d) {
                this.upstream = d;
                ((QueueDisposable<?>) (d)).requestFusion(QueueFuseable.ANY);
            }

            @Override
            public void onNext(Integer value) {
                if ((++(count)) == 1) {
                    us.onNext(2);
                    upstream.dispose();
                    cdl.countDown();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
        us.onNext(1);
        cdl.await();
    }

    @Test
    public void nonFusedPollThrows() {
        new Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? extends Integer> observer) {
                observer.onSubscribe(Disposables.empty());
                @SuppressWarnings("unchecked")
                ObserveOnObserver<Integer> oo = ((ObserveOnObserver<Integer>) (observer));
                oo.queue = new SimpleQueue<Integer>() {
                    @Override
                    public boolean offer(Integer value) {
                        return false;
                    }

                    @Override
                    public boolean offer(Integer v1, Integer v2) {
                        return false;
                    }

                    @Nullable
                    @Override
                    public Integer poll() throws Exception {
                        throw new TestException();
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public void clear() {
                    }
                };
                oo.clear();
                oo.schedule();
            }
        }.observeOn(Schedulers.single()).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class);
    }

    @Test
    public void outputFusedOneSignal() {
        final BehaviorSubject<Integer> bs = BehaviorSubject.createDefault(1);
        bs.observeOn(INSTANCE).concatMap(new Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) throws Exception {
                return Observable.just((v + 1));
            }
        }).subscribeWith(new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 2) {
                    bs.onNext(2);
                }
            }
        }).assertValuesOnly(2, 3);
    }

    @Test
    public void workerNotDisposedPrematurelyNormalInNormalOut() {
        FlowableObserveOnTest.DisposeTrackingScheduler s = new FlowableObserveOnTest.DisposeTrackingScheduler();
        Observable.concat(just(1).hide().observeOn(s), just(2)).test().assertResult(1, 2);
        Assert.assertEquals(1, s.disposedCount.get());
    }

    @Test
    public void workerNotDisposedPrematurelySyncInNormalOut() {
        FlowableObserveOnTest.DisposeTrackingScheduler s = new FlowableObserveOnTest.DisposeTrackingScheduler();
        Observable.concat(just(1).observeOn(s), just(2)).test().assertResult(1, 2);
        Assert.assertEquals(1, s.disposedCount.get());
    }

    @Test
    public void workerNotDisposedPrematurelyAsyncInNormalOut() {
        FlowableObserveOnTest.DisposeTrackingScheduler s = new FlowableObserveOnTest.DisposeTrackingScheduler();
        UnicastSubject<Integer> up = UnicastSubject.create();
        up.onNext(1);
        up.onComplete();
        Observable.concat(up.observeOn(s), just(2)).test().assertResult(1, 2);
        Assert.assertEquals(1, s.disposedCount.get());
    }

    static final class TestObserverFusedCanceling extends TestObserver<Integer> {
        TestObserverFusedCanceling() {
            super();
            initialFusionMode = ANY;
        }

        @Override
        public void onComplete() {
            cancel();
            super.onComplete();
        }
    }

    @Test
    public void workerNotDisposedPrematurelyNormalInAsyncOut() {
        FlowableObserveOnTest.DisposeTrackingScheduler s = new FlowableObserveOnTest.DisposeTrackingScheduler();
        TestObserver<Integer> to = new ObservableObserveOnTest.TestObserverFusedCanceling();
        just(1).hide().observeOn(s).subscribe(to);
        Assert.assertEquals(1, s.disposedCount.get());
    }
}

