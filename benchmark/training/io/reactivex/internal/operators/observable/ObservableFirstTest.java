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


import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.functions.Predicate;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableFirstTest {
    Observer<String> w;

    SingleObserver<Object> wo;

    MaybeObserver<Object> wm;

    private static final Predicate<String> IS_D = new Predicate<String>() {
        @Override
        public boolean test(String value) {
            return "d".equals(value);
        }
    };

    @Test
    public void testFirstOrElseOfNoneObservable() {
        Observable<String> src = Observable.empty();
        src.first("default").toObservable().subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.times(1)).onNext("default");
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testFirstOrElseOfSomeObservable() {
        Observable<String> src = Observable.just("a", "b", "c");
        src.first("default").toObservable().subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.times(1)).onNext("a");
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testFirstOrElseWithPredicateOfNoneMatchingThePredicateObservable() {
        Observable<String> src = Observable.just("a", "b", "c");
        src.filter(ObservableFirstTest.IS_D).first("default").toObservable().subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.times(1)).onNext("default");
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testFirstOrElseWithPredicateOfSomeObservable() {
        Observable<String> src = Observable.just("a", "b", "c", "d", "e", "f");
        src.filter(ObservableFirstTest.IS_D).first("default").toObservable().subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.times(1)).onNext("d");
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testFirstObservable() {
        Observable<Integer> o = Observable.just(1, 2, 3).firstElement().toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(1);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithOneElementObservable() {
        Observable<Integer> o = Observable.just(1).firstElement().toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(1);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithEmptyObservable() {
        Observable<Integer> o = Observable.<Integer>empty().firstElement().toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer).onComplete();
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateObservable() {
        Observable<Integer> o = Observable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement().toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(2);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateAndOneElementObservable() {
        Observable<Integer> o = Observable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement().toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(2);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateAndEmptyObservable() {
        Observable<Integer> o = Observable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement().toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer).onComplete();
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultObservable() {
        Observable<Integer> o = Observable.just(1, 2, 3).first(4).toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(1);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithOneElementObservable() {
        Observable<Integer> o = Observable.just(1).first(2).toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(1);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithEmptyObservable() {
        Observable<Integer> o = Observable.<Integer>empty().first(1).toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(1);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateObservable() {
        Observable<Integer> o = Observable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(8).toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(2);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateAndOneElementObservable() {
        Observable<Integer> o = Observable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(4).toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(2);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateAndEmptyObservable() {
        Observable<Integer> o = Observable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(2).toObservable();
        Observer<Integer> observer = mockObserver();
        o.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onNext(2);
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrElseOfNone() {
        Observable<String> src = Observable.empty();
        src.first("default").subscribe(wo);
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyString());
        Mockito.verify(wo, Mockito.times(1)).onSuccess("default");
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstOrElseOfSome() {
        Observable<String> src = Observable.just("a", "b", "c");
        src.first("default").subscribe(wo);
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyString());
        Mockito.verify(wo, Mockito.times(1)).onSuccess("a");
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstOrElseWithPredicateOfNoneMatchingThePredicate() {
        Observable<String> src = Observable.just("a", "b", "c");
        src.filter(ObservableFirstTest.IS_D).first("default").subscribe(wo);
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyString());
        Mockito.verify(wo, Mockito.times(1)).onSuccess("default");
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstOrElseWithPredicateOfSome() {
        Observable<String> src = Observable.just("a", "b", "c", "d", "e", "f");
        src.filter(ObservableFirstTest.IS_D).first("default").subscribe(wo);
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyString());
        Mockito.verify(wo, Mockito.times(1)).onSuccess("d");
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirst() {
        Maybe<Integer> o = Observable.just(1, 2, 3).firstElement();
        o.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithOneElement() {
        Maybe<Integer> o = Observable.just(1).firstElement();
        o.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithEmpty() {
        Maybe<Integer> o = Observable.<Integer>empty().firstElement();
        o.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onComplete();
        inOrder.verify(wm, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicate() {
        Maybe<Integer> o = Observable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement();
        o.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateAndOneElement() {
        Maybe<Integer> o = Observable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement();
        o.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateAndEmpty() {
        Maybe<Integer> o = Observable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement();
        o.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onComplete();
        inOrder.verify(wm, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefault() {
        Single<Integer> o = Observable.just(1, 2, 3).first(4);
        o.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithOneElement() {
        Single<Integer> o = Observable.just(1).first(2);
        o.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithEmpty() {
        Single<Integer> o = Observable.<Integer>empty().first(1);
        o.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicate() {
        Single<Integer> o = Observable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(8);
        o.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateAndOneElement() {
        Single<Integer> o = Observable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(4);
        o.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateAndEmpty() {
        Single<Integer> o = Observable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(2);
        o.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void firstOrErrorNoElement() {
        Observable.empty().firstOrError().test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void firstOrErrorOneElement() {
        Observable.just(1).firstOrError().test().assertNoErrors().assertValue(1);
    }

    @Test
    public void firstOrErrorMultipleElements() {
        Observable.just(1, 2, 3).firstOrError().test().assertNoErrors().assertValue(1);
    }

    @Test
    public void firstOrErrorError() {
        Observable.error(new RuntimeException("error")).firstOrError().test().assertNoValues().assertErrorMessage("error").assertError(RuntimeException.class);
    }

    @Test
    public void firstOrErrorNoElementObservable() {
        Observable.empty().firstOrError().toObservable().test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void firstOrErrorOneElementObservable() {
        Observable.just(1).firstOrError().toObservable().test().assertNoErrors().assertValue(1);
    }

    @Test
    public void firstOrErrorMultipleElementsObservable() {
        Observable.just(1, 2, 3).firstOrError().toObservable().test().assertNoErrors().assertValue(1);
    }

    @Test
    public void firstOrErrorErrorObservable() {
        Observable.error(new RuntimeException("error")).firstOrError().toObservable().test().assertNoValues().assertErrorMessage("error").assertError(RuntimeException.class);
    }
}

