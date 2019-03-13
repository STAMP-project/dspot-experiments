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


import io.reactivex.exceptions.TestException;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableSkipTimedTest {
    @Test
    public void testSkipTimed() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> result = source.skip(1, TimeUnit.SECONDS, scheduler);
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.onNext(4);
        source.onNext(5);
        source.onNext(6);
        source.onComplete();
        InOrder inOrder = Mockito.inOrder(o);
        inOrder.verify(o, Mockito.never()).onNext(1);
        inOrder.verify(o, Mockito.never()).onNext(2);
        inOrder.verify(o, Mockito.never()).onNext(3);
        inOrder.verify(o).onNext(4);
        inOrder.verify(o).onNext(5);
        inOrder.verify(o).onNext(6);
        inOrder.verify(o).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSkipTimedFinishBeforeTime() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> result = source.skip(1, TimeUnit.SECONDS, scheduler);
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        source.onComplete();
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        InOrder inOrder = Mockito.inOrder(o);
        inOrder.verify(o).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSkipTimedErrorBeforeTime() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> result = source.skip(1, TimeUnit.SECONDS, scheduler);
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        source.onError(new TestException());
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        InOrder inOrder = Mockito.inOrder(o);
        inOrder.verify(o).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testSkipTimedErrorAfterTime() {
        TestScheduler scheduler = new TestScheduler();
        PublishSubject<Integer> source = PublishSubject.create();
        Observable<Integer> result = source.skip(1, TimeUnit.SECONDS, scheduler);
        Observer<Object> o = mockObserver();
        result.subscribe(o);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.onNext(4);
        source.onNext(5);
        source.onNext(6);
        source.onError(new TestException());
        InOrder inOrder = Mockito.inOrder(o);
        inOrder.verify(o, Mockito.never()).onNext(1);
        inOrder.verify(o, Mockito.never()).onNext(2);
        inOrder.verify(o, Mockito.never()).onNext(3);
        inOrder.verify(o).onNext(4);
        inOrder.verify(o).onNext(5);
        inOrder.verify(o).onNext(6);
        inOrder.verify(o).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void skipTimedDefaultScheduler() {
        Observable.just(1).skip(1, TimeUnit.MINUTES).test().awaitDone(5, TimeUnit.SECONDS).assertResult();
    }
}

