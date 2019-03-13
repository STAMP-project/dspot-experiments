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
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableTakeUntilPredicateTest {
    @Test
    public void takeEmpty() {
        Observer<Object> o = mockObserver();
        Observable.empty().takeUntil(new Predicate<Object>() {
            @Override
            public boolean test(Object v) {
                return true;
            }
        }).subscribe(o);
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o).onComplete();
    }

    @Test
    public void takeAll() {
        Observer<Object> o = mockObserver();
        Observable.just(1, 2).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        }).subscribe(o);
        Mockito.verify(o).onNext(1);
        Mockito.verify(o).onNext(2);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o).onComplete();
    }

    @Test
    public void takeFirst() {
        Observer<Object> o = mockObserver();
        Observable.just(1, 2).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        }).subscribe(o);
        Mockito.verify(o).onNext(1);
        Mockito.verify(o, Mockito.never()).onNext(2);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o).onComplete();
    }

    @Test
    public void takeSome() {
        Observer<Object> o = mockObserver();
        Observable.just(1, 2, 3).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 == 2;
            }
        }).subscribe(o);
        Mockito.verify(o).onNext(1);
        Mockito.verify(o).onNext(2);
        Mockito.verify(o, Mockito.never()).onNext(3);
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o).onComplete();
    }

    @Test
    public void functionThrows() {
        Observer<Object> o = mockObserver();
        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                throw new TestException("Forced failure");
            }
        };
        Observable.just(1, 2, 3).takeUntil(predicate).subscribe(o);
        Mockito.verify(o).onNext(1);
        Mockito.verify(o, Mockito.never()).onNext(2);
        Mockito.verify(o, Mockito.never()).onNext(3);
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void sourceThrows() {
        Observer<Object> o = mockObserver();
        Observable.just(1).concatWith(Observable.<Integer>error(new TestException())).concatWith(Observable.just(2)).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        }).subscribe(o);
        Mockito.verify(o).onNext(1);
        Mockito.verify(o, Mockito.never()).onNext(2);
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testErrorIncludesLastValueAsCause() {
        TestObserver<String> to = new TestObserver<String>();
        final TestException e = new TestException("Forced failure");
        Predicate<String> predicate = new Predicate<String>() {
            @Override
            public boolean test(String t) {
                throw e;
            }
        };
        Observable.just("abc").takeUntil(predicate).subscribe(to);
        to.assertTerminated();
        to.assertNotComplete();
        to.assertError(TestException.class);
        // FIXME last cause value is not saved
        // assertTrue(ts.errors().get(0).getCause().getMessage().contains("abc"));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().takeUntil(Functions.alwaysFalse()));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.takeUntil(Functions.alwaysFalse());
            }
        });
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onComplete();
                    observer.onNext(1);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.takeUntil(Functions.alwaysFalse()).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

