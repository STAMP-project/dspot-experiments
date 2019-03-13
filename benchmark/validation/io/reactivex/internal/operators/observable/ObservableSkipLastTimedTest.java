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
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableSkipLastTimedTest {
    @Test
    public void testSkipLastTimed() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        // FIXME the timeunit now matters due to rounding
        Observable<Integer> result = source.skipLast(1000, TimeUnit.MILLISECONDS, scheduler);
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        source.onNext(4);
        source.onNext(5);
        source.onNext(6);
        scheduler.advanceTimeBy(950, TimeUnit.MILLISECONDS);
        source.onComplete();
        InOrder inOrder = Mockito.inOrder(o);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(2);
        inOrder.verify(o).onNext(3);
        inOrder.verify(o, Mockito.never()).onNext(4);
        inOrder.verify(o, Mockito.never()).onNext(5);
        inOrder.verify(o, Mockito.never()).onNext(6);
        inOrder.verify(o).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSkipLastTimedErrorBeforeTime() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> result = source.skipLast(1, TimeUnit.SECONDS, scheduler);
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        source.onError(new TestException());
        scheduler.advanceTimeBy(1050, TimeUnit.MILLISECONDS);
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(o, Mockito.never()).onComplete();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void testSkipLastTimedCompleteBeforeTime() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> result = source.skipLast(1, TimeUnit.SECONDS, scheduler);
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        source.onComplete();
        InOrder inOrder = Mockito.inOrder(o);
        inOrder.verify(o).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSkipLastTimedWhenAllElementsAreValid() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> result = source.skipLast(1, TimeUnit.MILLISECONDS, scheduler);
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
        source.onComplete();
        InOrder inOrder = Mockito.inOrder(o);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(2);
        inOrder.verify(o).onNext(3);
        inOrder.verify(o).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void skipLastTimedDefaultScheduler() {
        Observable.just(1).concatWith(Observable.just(2).delay(500, TimeUnit.MILLISECONDS)).skipLast(300, TimeUnit.MILLISECONDS).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void skipLastTimedDefaultSchedulerDelayError() {
        Observable.just(1).concatWith(Observable.just(2).delay(500, TimeUnit.MILLISECONDS)).skipLast(300, TimeUnit.MILLISECONDS, true).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void skipLastTimedCustomSchedulerDelayError() {
        Observable.just(1).concatWith(Observable.just(2).delay(500, TimeUnit.MILLISECONDS)).skipLast(300, TimeUnit.MILLISECONDS, Schedulers.io(), true).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().skipLast(1, TimeUnit.DAYS));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.skipLast(1, TimeUnit.DAYS);
            }
        });
    }

    @Test
    public void onNextDisposeRace() {
        TestScheduler scheduler = new TestScheduler();
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final TestObserver<Integer> to = ps.skipLast(1, TimeUnit.DAYS, scheduler).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void errorDelayed() {
        Observable.error(new TestException()).skipLast(1, TimeUnit.DAYS, new TestScheduler(), true).test().assertFailure(TestException.class);
    }

    @Test
    public void take() {
        Observable.just(1).skipLast(0, TimeUnit.SECONDS).take(1).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1);
    }
}

