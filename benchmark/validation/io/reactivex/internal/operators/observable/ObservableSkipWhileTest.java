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
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableSkipWhileTest {
    Observer<Integer> w = mockObserver();

    private static final Predicate<Integer> LESS_THAN_FIVE = new Predicate<Integer>() {
        @Override
        public boolean test(Integer v) {
            if (v == 42) {
                throw new RuntimeException("that's not the answer to everything!");
            }
            return v < 5;
        }
    };

    private static final Predicate<Integer> INDEX_LESS_THAN_THREE = new Predicate<Integer>() {
        int index;

        @Override
        public boolean test(Integer value) {
            return ((index)++) < 3;
        }
    };

    @Test
    public void testSkipWithIndex() {
        Observable<Integer> src = Observable.just(1, 2, 3, 4, 5);
        src.skipWhile(ObservableSkipWhileTest.INDEX_LESS_THAN_THREE).subscribe(w);
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.times(1)).onNext(4);
        inOrder.verify(w, Mockito.times(1)).onNext(5);
        inOrder.verify(w, Mockito.times(1)).onComplete();
        inOrder.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSkipEmpty() {
        Observable<Integer> src = Observable.empty();
        src.skipWhile(ObservableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipEverything() {
        Observable<Integer> src = Observable.just(1, 2, 3, 4, 3, 2, 1);
        src.skipWhile(ObservableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipNothing() {
        Observable<Integer> src = Observable.just(5, 3, 1);
        src.skipWhile(ObservableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.times(1)).onNext(5);
        inOrder.verify(w, Mockito.times(1)).onNext(3);
        inOrder.verify(w, Mockito.times(1)).onNext(1);
        inOrder.verify(w, Mockito.times(1)).onComplete();
        inOrder.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSkipSome() {
        Observable<Integer> src = Observable.just(1, 2, 3, 4, 5, 3, 1, 5);
        src.skipWhile(ObservableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.times(1)).onNext(5);
        inOrder.verify(w, Mockito.times(1)).onNext(3);
        inOrder.verify(w, Mockito.times(1)).onNext(1);
        inOrder.verify(w, Mockito.times(1)).onNext(5);
        inOrder.verify(w, Mockito.times(1)).onComplete();
        inOrder.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSkipError() {
        Observable<Integer> src = Observable.just(1, 2, 42, 5, 3, 1);
        src.skipWhile(ObservableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        inOrder.verify(w, Mockito.never()).onComplete();
        inOrder.verify(w, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
    }

    @Test
    public void testSkipManySubscribers() {
        Observable<Integer> src = Observable.range(1, 10).skipWhile(ObservableSkipWhileTest.LESS_THAN_FIVE);
        int n = 5;
        for (int i = 0; i < n; i++) {
            Observer<Object> o = mockObserver();
            InOrder inOrder = Mockito.inOrder(o);
            src.subscribe(o);
            for (int j = 5; j < 10; j++) {
                inOrder.verify(o).onNext(j);
            }
            inOrder.verify(o).onComplete();
            Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().skipWhile(Functions.alwaysFalse()));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.skipWhile(Functions.alwaysFalse());
            }
        });
    }

    @Test
    public void error() {
        Observable.error(new TestException()).skipWhile(Functions.alwaysFalse()).test().assertFailure(TestException.class);
    }
}

