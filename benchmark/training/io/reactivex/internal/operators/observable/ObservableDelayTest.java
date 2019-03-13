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
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableDelayTest {
    private Observer<Long> observer;

    private Observer<Long> observer2;

    private TestScheduler scheduler;

    @Test
    public void testDelay() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        Observable<Long> delayed = source.delay(500L, TimeUnit.MILLISECONDS, scheduler);
        delayed.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(1499L, TimeUnit.MILLISECONDS);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(1500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(0L);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2400L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(1L);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3400L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(2L);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testLongDelay() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        Observable<Long> delayed = source.delay(5L, TimeUnit.SECONDS, scheduler);
        delayed.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(5999L, TimeUnit.MILLISECONDS);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(6000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(0L);
        scheduler.advanceTimeTo(6999L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        scheduler.advanceTimeTo(7000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(1L);
        scheduler.advanceTimeTo(7999L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        scheduler.advanceTimeTo(8000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(2L);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithError() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long value) {
                if (value == 1L) {
                    throw new RuntimeException("error!");
                }
                return value;
            }
        });
        Observable<Long> delayed = source.delay(1L, TimeUnit.SECONDS, scheduler);
        delayed.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(1999L, TimeUnit.MILLISECONDS);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        scheduler.advanceTimeTo(5000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithMultipleSubscriptions() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        Observable<Long> delayed = source.delay(500L, TimeUnit.MILLISECONDS, scheduler);
        delayed.subscribe(observer);
        delayed.subscribe(observer2);
        InOrder inOrder = Mockito.inOrder(observer);
        InOrder inOrder2 = Mockito.inOrder(observer2);
        scheduler.advanceTimeTo(1499L, TimeUnit.MILLISECONDS);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer2, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        scheduler.advanceTimeTo(1500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(0L);
        inOrder2.verify(observer2, Mockito.times(1)).onNext(0L);
        scheduler.advanceTimeTo(2499L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder2.verify(observer2, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        scheduler.advanceTimeTo(2500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(1L);
        inOrder2.verify(observer2, Mockito.times(1)).onNext(1L);
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer2, Mockito.never()).onComplete();
        scheduler.advanceTimeTo(3500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(2L);
        inOrder2.verify(observer2, Mockito.times(1)).onNext(2L);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder2.verify(observer2, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder2.verify(observer2, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelaySubscription() {
        Observable<Integer> result = Observable.just(1, 2, 3).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        result.subscribe(o);
        inOrder.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        inOrder.verify(o, Mockito.never()).onComplete();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        inOrder.verify(o, Mockito.times(1)).onNext(1);
        inOrder.verify(o, Mockito.times(1)).onNext(2);
        inOrder.verify(o, Mockito.times(1)).onNext(3);
        inOrder.verify(o, Mockito.times(1)).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelaySubscriptionDisposeBeforeTime() {
        Observable<Integer> result = Observable.just(1, 2, 3).delaySubscription(100, TimeUnit.MILLISECONDS, scheduler);
        Observer<Object> o = mockObserver();
        TestObserver<Object> to = new TestObserver<Object>(o);
        result.subscribe(to);
        to.dispose();
        scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableNormal1() {
        PublishSubject<Integer> source = PublishSubject.create();
        final List<PublishSubject<Integer>> delays = new ArrayList<PublishSubject<Integer>>();
        final int n = 10;
        for (int i = 0; i < n; i++) {
            PublishSubject<Integer> delay = PublishSubject.create();
            delays.add(delay);
        }
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return delays.get(t1);
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(delayFunc).subscribe(o);
        for (int i = 0; i < n; i++) {
            source.onNext(i);
            delays.get(i).onNext(i);
            inOrder.verify(o).onNext(i);
        }
        source.onComplete();
        inOrder.verify(o).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableSingleSend1() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(delayFunc).subscribe(o);
        source.onNext(1);
        delay.onNext(1);
        delay.onNext(2);
        inOrder.verify(o).onNext(1);
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableSourceThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(delayFunc).subscribe(o);
        source.onNext(1);
        source.onError(new TestException());
        delay.onNext(1);
        inOrder.verify(o).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithObservableDelayFunctionThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                throw new TestException();
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(delayFunc).subscribe(o);
        source.onNext(1);
        inOrder.verify(o).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithObservableDelayThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(delayFunc).subscribe(o);
        source.onNext(1);
        delay.onError(new TestException());
        inOrder.verify(o).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithObservableSubscriptionNormal() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Callable<Observable<Integer>> subFunc = new Callable<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return delay;
            }
        };
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(Observable.defer(subFunc), delayFunc).subscribe(o);
        source.onNext(1);
        delay.onNext(1);
        source.onNext(2);
        delay.onNext(2);
        inOrder.verify(o).onNext(2);
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithObservableSubscriptionFunctionThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Callable<Observable<Integer>> subFunc = new Callable<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                throw new TestException();
            }
        };
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(Observable.defer(subFunc), delayFunc).subscribe(o);
        source.onNext(1);
        delay.onNext(1);
        source.onNext(2);
        inOrder.verify(o).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithObservableSubscriptionThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Callable<Observable<Integer>> subFunc = new Callable<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return delay;
            }
        };
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(Observable.defer(subFunc), delayFunc).subscribe(o);
        source.onNext(1);
        delay.onError(new TestException());
        source.onNext(2);
        inOrder.verify(o).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithObservableEmptyDelayer() {
        PublishSubject<Integer> source = PublishSubject.create();
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return io.reactivex.Observable.empty();
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(delayFunc).subscribe(o);
        source.onNext(1);
        source.onComplete();
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableSubscriptionRunCompletion() {
        PublishSubject<Integer> source = PublishSubject.create();
        final PublishSubject<Integer> sdelay = PublishSubject.create();
        final PublishSubject<Integer> delay = PublishSubject.create();
        Callable<Observable<Integer>> subFunc = new Callable<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return sdelay;
            }
        };
        Function<Integer, Observable<Integer>> delayFunc = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return delay;
            }
        };
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.delay(Observable.defer(subFunc), delayFunc).subscribe(o);
        source.onNext(1);
        sdelay.onComplete();
        source.onNext(2);
        delay.onNext(2);
        inOrder.verify(o).onNext(2);
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testDelayWithObservableAsTimed() {
        Observable<Long> source = Observable.interval(1L, TimeUnit.SECONDS, scheduler).take(3);
        final Observable<Long> delayer = Observable.timer(500L, TimeUnit.MILLISECONDS, scheduler);
        Function<Long, Observable<Long>> delayFunc = new Function<Long, Observable<Long>>() {
            @Override
            public Observable<Long> apply(Long t1) {
                return delayer;
            }
        };
        Observable<Long> delayed = source.delay(delayFunc);
        delayed.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(1499L, TimeUnit.MILLISECONDS);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(1500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(0L);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2400L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(1L);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3400L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(ArgumentMatchers.anyLong());
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3500L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(2L);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayWithObservableReorder() {
        int n = 3;
        PublishSubject<Integer> source = PublishSubject.create();
        final List<PublishSubject<Integer>> subjects = new ArrayList<PublishSubject<Integer>>();
        for (int i = 0; i < n; i++) {
            subjects.add(PublishSubject.<Integer>create());
        }
        Observable<Integer> result = source.delay(new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer t1) {
                return subjects.get(t1);
            }
        });
        Observer<Object> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        result.subscribe(o);
        for (int i = 0; i < n; i++) {
            source.onNext(i);
        }
        source.onComplete();
        inOrder.verify(o, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        inOrder.verify(o, Mockito.never()).onComplete();
        for (int i = n - 1; i >= 0; i--) {
            subjects.get(i).onComplete();
            inOrder.verify(o).onNext(i);
        }
        inOrder.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDelayEmitsEverything() {
        Observable<Integer> source = range(1, 5);
        Observable<Integer> delayed = source.delay(500L, TimeUnit.MILLISECONDS, scheduler);
        delayed = delayed.doOnEach(new Consumer<Notification<Integer>>() {
            @Override
            public void accept(Notification<Integer> t1) {
                System.out.println(t1);
            }
        });
        TestObserver<Integer> observer = new TestObserver<Integer>();
        delayed.subscribe(observer);
        // all will be delivered after 500ms since range does not delay between them
        scheduler.advanceTimeBy(500L, TimeUnit.MILLISECONDS);
        observer.assertValues(1, 2, 3, 4, 5);
    }

    @Test
    public void testBackpressureWithTimedDelay() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(1, ((Flowable.bufferSize()) * 2)).delay(100, TimeUnit.MILLISECONDS).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            int c;

            @Override
            public Integer apply(Integer t) {
                if (((c)++) <= 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return t;
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 2), to.valueCount());
    }

    @Test
    public void testBackpressureWithSubscriptionTimedDelay() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(1, ((Flowable.bufferSize()) * 2)).delaySubscription(100, TimeUnit.MILLISECONDS).delay(100, TimeUnit.MILLISECONDS).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            int c;

            @Override
            public Integer apply(Integer t) {
                if (((c)++) <= 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return t;
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 2), to.valueCount());
    }

    @Test
    public void testBackpressureWithSelectorDelay() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(1, ((Flowable.bufferSize()) * 2)).delay(new Function<Integer, Observable<Long>>() {
            @Override
            public Observable<Long> apply(Integer i) {
                return io.reactivex.Observable.timer(100, TimeUnit.MILLISECONDS);
            }
        }).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            int c;

            @Override
            public Integer apply(Integer t) {
                if (((c)++) <= 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return t;
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 2), to.valueCount());
    }

    @Test
    public void testBackpressureWithSelectorDelayAndSubscriptionDelay() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(1, ((Flowable.bufferSize()) * 2)).delay(timer(500, TimeUnit.MILLISECONDS), new Function<Integer, Observable<Long>>() {
            @Override
            public Observable<Long> apply(Integer i) {
                return io.reactivex.Observable.timer(100, TimeUnit.MILLISECONDS);
            }
        }).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            int c;

            @Override
            public Integer apply(Integer t) {
                if (((c)++) <= 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
                return t;
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 2), to.valueCount());
    }

    @Test
    public void testErrorRunsBeforeOnNext() {
        TestScheduler test = new TestScheduler();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>();
        ps.delay(1, TimeUnit.SECONDS, test).subscribe(to);
        ps.onNext(1);
        test.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        ps.onError(new TestException());
        test.advanceTimeBy(1, TimeUnit.SECONDS);
        to.assertNoValues();
        to.assertError(TestException.class);
        to.assertNotComplete();
    }

    @Test
    public void testDelaySupplierSimple() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        Observable<Integer> source = range(1, 5);
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.delaySubscription(ps).subscribe(to);
        to.assertNoValues();
        to.assertNoErrors();
        to.assertNotComplete();
        ps.onNext(1);
        to.assertValues(1, 2, 3, 4, 5);
        to.assertComplete();
        to.assertNoErrors();
    }

    @Test
    public void testDelaySupplierCompletes() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        Observable<Integer> source = range(1, 5);
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.delaySubscription(ps).subscribe(to);
        to.assertNoValues();
        to.assertNoErrors();
        to.assertNotComplete();
        // FIXME should this complete the source instead of consuming it?
        ps.onComplete();
        to.assertValues(1, 2, 3, 4, 5);
        to.assertComplete();
        to.assertNoErrors();
    }

    @Test
    public void testDelaySupplierErrors() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        Observable<Integer> source = range(1, 5);
        TestObserver<Integer> to = new TestObserver<Integer>();
        source.delaySubscription(ps).subscribe(to);
        to.assertNoValues();
        to.assertNoErrors();
        to.assertNotComplete();
        ps.onError(new TestException());
        to.assertNoValues();
        to.assertNotComplete();
        to.assertError(TestException.class);
    }

    @Test
    public void delayWithTimeDelayError() throws Exception {
        just(1).concatWith(<Integer>error(new TestException())).delay(100, TimeUnit.MILLISECONDS, true).test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class, 1);
    }

    @Test
    public void testOnErrorCalledOnScheduler() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Thread> thread = new AtomicReference<Thread>();
        Observable.<String>error(new Exception()).delay(0, TimeUnit.MILLISECONDS, Schedulers.newThread()).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                thread.set(Thread.currentThread());
                latch.countDown();
            }
        }).onErrorResumeNext(<String>empty()).subscribe();
        latch.await();
        Assert.assertNotEquals(Thread.currentThread(), thread.get());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().delay(1, TimeUnit.SECONDS));
        TestHelper.checkDisposed(PublishSubject.create().delay(Functions.justFunction(never())));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.delay(1, TimeUnit.SECONDS);
            }
        });
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.delay(Functions.justFunction(io.reactivex.Observable.never()));
            }
        });
    }

    @Test
    public void onCompleteFinal() {
        TestScheduler scheduler = new TestScheduler();
        empty().delay(1, TimeUnit.MILLISECONDS, scheduler).subscribe(new DisposableObserver<Object>() {
            @Override
            public void onNext(Object value) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                throw new TestException();
            }
        });
        try {
            scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
            Assert.fail("Should have thrown");
        } catch (TestException ex) {
            // expected
        }
    }

    @Test
    public void onErrorFinal() {
        TestScheduler scheduler = new TestScheduler();
        error(new TestException()).delay(1, TimeUnit.MILLISECONDS, scheduler).subscribe(new DisposableObserver<Object>() {
            @Override
            public void onNext(Object value) {
            }

            @Override
            public void onError(Throwable e) {
                throw new TestException();
            }

            @Override
            public void onComplete() {
            }
        });
        try {
            scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
            Assert.fail("Should have thrown");
        } catch (TestException ex) {
            // expected
        }
    }

    @Test
    public void itemDelayReturnsNull() {
        just(1).delay(new Function<Integer, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Integer t) throws Exception {
                return null;
            }
        }).test().assertFailureAndMessage(NullPointerException.class, "The itemDelay returned a null ObservableSource");
    }
}

