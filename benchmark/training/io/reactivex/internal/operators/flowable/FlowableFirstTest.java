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
package io.reactivex.internal.operators.flowable;


import io.reactivex.MaybeObserver;
import io.reactivex.SingleObserver;
import io.reactivex.TestHelper;
import io.reactivex.functions.Predicate;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableFirstTest {
    Subscriber<String> w;

    SingleObserver<Object> wo;

    MaybeObserver<Object> wm;

    private static final Predicate<String> IS_D = new Predicate<String>() {
        @Override
        public boolean test(String value) {
            return "d".equals(value);
        }
    };

    @Test
    public void testFirstOrElseOfNoneFlowable() {
        Flowable<String> src = Flowable.empty();
        src.first("default").toFlowable().subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.times(1)).onNext("default");
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testFirstOrElseOfSomeFlowable() {
        Flowable<String> src = Flowable.just("a", "b", "c");
        src.first("default").toFlowable().subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.times(1)).onNext("a");
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testFirstOrElseWithPredicateOfNoneMatchingThePredicateFlowable() {
        Flowable<String> src = Flowable.just("a", "b", "c");
        src.filter(FlowableFirstTest.IS_D).first("default").toFlowable().subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.times(1)).onNext("default");
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testFirstOrElseWithPredicateOfSomeFlowable() {
        Flowable<String> src = Flowable.just("a", "b", "c", "d", "e", "f");
        src.filter(FlowableFirstTest.IS_D).first("default").toFlowable().subscribe(w);
        Mockito.verify(w, Mockito.times(1)).onNext(ArgumentMatchers.anyString());
        Mockito.verify(w, Mockito.times(1)).onNext("d");
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testFirstFlowable() {
        Flowable<Integer> flowable = Flowable.just(1, 2, 3).firstElement().toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithOneElementFlowable() {
        Flowable<Integer> flowable = Flowable.just(1).firstElement().toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithEmptyFlowable() {
        Flowable<Integer> flowable = Flowable.<Integer>empty().firstElement().toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber).onComplete();
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateFlowable() {
        Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement().toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateAndOneElementFlowable() {
        Flowable<Integer> flowable = Flowable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement().toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateAndEmptyFlowable() {
        Flowable<Integer> flowable = Flowable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement().toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber).onComplete();
        inOrder.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultFlowable() {
        Flowable<Integer> flowable = Flowable.just(1, 2, 3).first(4).toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithOneElementFlowable() {
        Flowable<Integer> flowable = Flowable.just(1).first(2).toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithEmptyFlowable() {
        Flowable<Integer> flowable = Flowable.<Integer>empty().first(1).toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(1);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateFlowable() {
        Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(8).toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateAndOneElementFlowable() {
        Flowable<Integer> flowable = Flowable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(4).toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateAndEmptyFlowable() {
        Flowable<Integer> flowable = Flowable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(2).toFlowable();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(2);
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrElseOfNone() {
        Flowable<String> src = Flowable.empty();
        src.first("default").subscribe(wo);
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyString());
        Mockito.verify(wo, Mockito.times(1)).onSuccess("default");
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstOrElseOfSome() {
        Flowable<String> src = Flowable.just("a", "b", "c");
        src.first("default").subscribe(wo);
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyString());
        Mockito.verify(wo, Mockito.times(1)).onSuccess("a");
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstOrElseWithPredicateOfNoneMatchingThePredicate() {
        Flowable<String> src = Flowable.just("a", "b", "c");
        src.filter(FlowableFirstTest.IS_D).first("default").subscribe(wo);
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyString());
        Mockito.verify(wo, Mockito.times(1)).onSuccess("default");
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstOrElseWithPredicateOfSome() {
        Flowable<String> src = Flowable.just("a", "b", "c", "d", "e", "f");
        src.filter(FlowableFirstTest.IS_D).first("default").subscribe(wo);
        Mockito.verify(wo, Mockito.times(1)).onSuccess(ArgumentMatchers.anyString());
        Mockito.verify(wo, Mockito.times(1)).onSuccess("d");
        Mockito.verify(wo, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirst() {
        Maybe<Integer> maybe = Flowable.just(1, 2, 3).firstElement();
        maybe.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithOneElement() {
        Maybe<Integer> maybe = Flowable.just(1).firstElement();
        maybe.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithEmpty() {
        Maybe<Integer> maybe = Flowable.<Integer>empty().firstElement();
        maybe.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm).onComplete();
        inOrder.verify(wm, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicate() {
        Maybe<Integer> maybe = Flowable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement();
        maybe.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateAndOneElement() {
        Maybe<Integer> maybe = Flowable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement();
        maybe.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstWithPredicateAndEmpty() {
        Maybe<Integer> maybe = Flowable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).firstElement();
        maybe.subscribe(wm);
        InOrder inOrder = Mockito.inOrder(wm);
        inOrder.verify(wm).onComplete();
        inOrder.verify(wm, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefault() {
        Single<Integer> single = Flowable.just(1, 2, 3).first(4);
        single.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithOneElement() {
        Single<Integer> single = Flowable.just(1).first(2);
        single.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithEmpty() {
        Single<Integer> single = Flowable.<Integer>empty().first(1);
        single.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicate() {
        Single<Integer> single = Flowable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(8);
        single.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateAndOneElement() {
        Single<Integer> single = Flowable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(4);
        single.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstOrDefaultWithPredicateAndEmpty() {
        Single<Integer> single = Flowable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).first(2);
        single.subscribe(wo);
        InOrder inOrder = Mockito.inOrder(wo);
        inOrder.verify(wo, Mockito.times(1)).onSuccess(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void firstOrErrorNoElement() {
        Flowable.empty().firstOrError().test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void firstOrErrorOneElement() {
        Flowable.just(1).firstOrError().test().assertNoErrors().assertValue(1);
    }

    @Test
    public void firstOrErrorMultipleElements() {
        Flowable.just(1, 2, 3).firstOrError().test().assertNoErrors().assertValue(1);
    }

    @Test
    public void firstOrErrorError() {
        Flowable.error(new RuntimeException("error")).firstOrError().test().assertNoValues().assertErrorMessage("error").assertError(RuntimeException.class);
    }

    @Test
    public void firstOrErrorNoElementFlowable() {
        Flowable.empty().firstOrError().toFlowable().test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void firstOrErrorOneElementFlowable() {
        Flowable.just(1).firstOrError().toFlowable().test().assertNoErrors().assertValue(1);
    }

    @Test
    public void firstOrErrorMultipleElementsFlowable() {
        Flowable.just(1, 2, 3).firstOrError().toFlowable().test().assertNoErrors().assertValue(1);
    }

    @Test
    public void firstOrErrorErrorFlowable() {
        Flowable.error(new RuntimeException("error")).firstOrError().toFlowable().test().assertNoValues().assertErrorMessage("error").assertError(RuntimeException.class);
    }
}

