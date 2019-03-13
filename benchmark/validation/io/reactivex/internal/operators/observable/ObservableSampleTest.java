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


import Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableSampleTest {
    private TestScheduler scheduler;

    private Worker innerScheduler;

    private Observer<Long> observer;

    private Observer<Object> observer2;

    @Test
    public void testSample() {
        Observable<Long> source = Observable.unsafeCreate(new ObservableSource<Long>() {
            @Override
            public void subscribe(final Observer<? super Long> observer1) {
                observer1.onSubscribe(Disposables.empty());
                innerScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        observer1.onNext(1L);
                    }
                }, 1, TimeUnit.SECONDS);
                innerScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        observer1.onNext(2L);
                    }
                }, 2, TimeUnit.SECONDS);
                innerScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        observer1.onComplete();
                    }
                }, 3, TimeUnit.SECONDS);
            }
        });
        Observable<Long> sampled = source.sample(400L, TimeUnit.MILLISECONDS, scheduler);
        sampled.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        scheduler.advanceTimeTo(800L, TimeUnit.MILLISECONDS);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(Long.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(1200L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.times(1)).onNext(1L);
        Mockito.verify(observer, Mockito.never()).onNext(2L);
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(1600L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(1L);
        Mockito.verify(observer, Mockito.never()).onNext(2L);
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(2000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(1L);
        inOrder.verify(observer, Mockito.times(1)).onNext(2L);
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        scheduler.advanceTimeTo(3000L, TimeUnit.MILLISECONDS);
        inOrder.verify(observer, Mockito.never()).onNext(1L);
        inOrder.verify(observer, Mockito.never()).onNext(2L);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerNormal() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> sampler = PublishSubject.create();
        Observable<Integer> m = source.sample(sampler);
        m.subscribe(observer2);
        source.onNext(1);
        source.onNext(2);
        sampler.onNext(1);
        source.onNext(3);
        source.onNext(4);
        sampler.onNext(2);
        source.onComplete();
        sampler.onNext(3);
        InOrder inOrder = Mockito.inOrder(observer2);
        inOrder.verify(observer2, Mockito.never()).onNext(1);
        inOrder.verify(observer2, Mockito.times(1)).onNext(2);
        inOrder.verify(observer2, Mockito.never()).onNext(3);
        inOrder.verify(observer2, Mockito.times(1)).onNext(4);
        inOrder.verify(observer2, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerNoDuplicates() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> sampler = PublishSubject.create();
        Observable<Integer> m = source.sample(sampler);
        m.subscribe(observer2);
        source.onNext(1);
        source.onNext(2);
        sampler.onNext(1);
        sampler.onNext(1);
        source.onNext(3);
        source.onNext(4);
        sampler.onNext(2);
        sampler.onNext(2);
        source.onComplete();
        sampler.onNext(3);
        InOrder inOrder = Mockito.inOrder(observer2);
        inOrder.verify(observer2, Mockito.never()).onNext(1);
        inOrder.verify(observer2, Mockito.times(1)).onNext(2);
        inOrder.verify(observer2, Mockito.never()).onNext(3);
        inOrder.verify(observer2, Mockito.times(1)).onNext(4);
        inOrder.verify(observer2, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerTerminatingEarly() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> sampler = PublishSubject.create();
        Observable<Integer> m = source.sample(sampler);
        m.subscribe(observer2);
        source.onNext(1);
        source.onNext(2);
        sampler.onNext(1);
        sampler.onComplete();
        source.onNext(3);
        source.onNext(4);
        InOrder inOrder = Mockito.inOrder(observer2);
        inOrder.verify(observer2, Mockito.never()).onNext(1);
        inOrder.verify(observer2, Mockito.times(1)).onNext(2);
        inOrder.verify(observer2, Mockito.times(1)).onComplete();
        inOrder.verify(observer2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerEmitAndTerminate() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> sampler = PublishSubject.create();
        Observable<Integer> m = source.sample(sampler);
        m.subscribe(observer2);
        source.onNext(1);
        source.onNext(2);
        sampler.onNext(1);
        source.onNext(3);
        source.onComplete();
        sampler.onNext(2);
        sampler.onComplete();
        InOrder inOrder = Mockito.inOrder(observer2);
        inOrder.verify(observer2, Mockito.never()).onNext(1);
        inOrder.verify(observer2, Mockito.times(1)).onNext(2);
        inOrder.verify(observer2, Mockito.never()).onNext(3);
        inOrder.verify(observer2, Mockito.times(1)).onComplete();
        inOrder.verify(observer2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerEmptySource() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> sampler = PublishSubject.create();
        Observable<Integer> m = source.sample(sampler);
        m.subscribe(observer2);
        source.onComplete();
        sampler.onNext(1);
        InOrder inOrder = Mockito.inOrder(observer2);
        inOrder.verify(observer2, Mockito.times(1)).onComplete();
        Mockito.verify(observer2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void sampleWithSamplerSourceThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> sampler = PublishSubject.create();
        Observable<Integer> m = source.sample(sampler);
        m.subscribe(observer2);
        source.onNext(1);
        source.onError(new RuntimeException("Forced failure!"));
        sampler.onNext(1);
        InOrder inOrder = Mockito.inOrder(observer2);
        inOrder.verify(observer2, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer2, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void sampleWithSamplerThrows() {
        PublishSubject<Integer> source = PublishSubject.create();
        PublishSubject<Integer> sampler = PublishSubject.create();
        Observable<Integer> m = source.sample(sampler);
        m.subscribe(observer2);
        source.onNext(1);
        sampler.onNext(1);
        sampler.onError(new RuntimeException("Forced failure!"));
        InOrder inOrder = Mockito.inOrder(observer2);
        inOrder.verify(observer2, Mockito.times(1)).onNext(1);
        inOrder.verify(observer2, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testSampleUnsubscribe() {
        final Disposable upstream = Mockito.mock(io.reactivex.disposables.Disposable.class);
        Observable<Integer> o = Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observer) {
                observer.onSubscribe(upstream);
            }
        });
        o.throttleLast(1, TimeUnit.MILLISECONDS).subscribe().dispose();
        Mockito.verify(upstream).dispose();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().sample(1, TimeUnit.SECONDS, new TestScheduler()));
        TestHelper.checkDisposed(PublishSubject.create().sample(Observable.never()));
    }

    @Test
    public void error() {
        Observable.error(new TestException()).sample(1, TimeUnit.SECONDS).test().assertFailure(TestException.class);
    }

    @Test
    public void emitLastTimed() {
        Observable.just(1).sample(1, TimeUnit.DAYS, true).test().assertResult(1);
    }

    @Test
    public void emitLastTimedEmpty() {
        Observable.empty().sample(1, TimeUnit.DAYS, true).test().assertResult();
    }

    @Test
    public void emitLastTimedCustomScheduler() {
        Observable.just(1).sample(1, TimeUnit.DAYS, Schedulers.single(), true).test().assertResult(1);
    }

    @Test
    public void emitLastTimedRunCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final TestScheduler scheduler = new TestScheduler();
            final PublishSubject<Integer> ps = PublishSubject.create();
            TestObserver<Integer> to = ps.sample(1, TimeUnit.SECONDS, scheduler, true).test();
            ps.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
                }
            };
            TestHelper.race(r1, r2);
            to.assertResult(1);
        }
    }

    @Test
    public void emitLastOther() {
        Observable.just(1).sample(Observable.timer(1, TimeUnit.DAYS), true).test().assertResult(1);
    }

    @Test
    public void emitLastOtherEmpty() {
        Observable.empty().sample(Observable.timer(1, TimeUnit.DAYS), true).test().assertResult();
    }

    @Test
    public void emitLastOtherRunCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final PublishSubject<Integer> sampler = PublishSubject.create();
            TestObserver<Integer> to = ps.sample(sampler, true).test();
            ps.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sampler.onNext(1);
                }
            };
            TestHelper.race(r1, r2);
            to.assertResult(1);
        }
    }

    @Test
    public void emitLastOtherCompleteCompleteRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final PublishSubject<Integer> sampler = PublishSubject.create();
            TestObserver<Integer> to = ps.sample(sampler, true).test();
            ps.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    sampler.onComplete();
                }
            };
            TestHelper.race(r1, r2);
            to.assertResult(1);
        }
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, Observable<Object>>() {
            @Override
            public io.reactivex.Observable<Object> apply(Observable<Object> o) throws Exception {
                return o.sample(1, TimeUnit.SECONDS);
            }
        });
    }
}

