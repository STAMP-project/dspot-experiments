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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class FlowableLastTest {
    @Test
    public void testLastWithElements() {
        Maybe<Integer> last = Flowable.just(1, 2, 3).lastElement();
        Assert.assertEquals(3, last.blockingGet().intValue());
    }

    @Test
    public void testLastWithNoElements() {
        Maybe<?> last = Flowable.empty().lastElement();
        Assert.assertNull(last.blockingGet());
    }

    @Test
    public void testLastMultiSubscribe() {
        Maybe<Integer> last = Flowable.just(1, 2, 3).lastElement();
        Assert.assertEquals(3, last.blockingGet().intValue());
        Assert.assertEquals(3, last.blockingGet().intValue());
    }

    @Test
    public void testLastViaFlowable() {
        Flowable.just(1, 2, 3).lastElement();
    }

    @Test
    public void testLast() {
        Maybe<Integer> maybe = Flowable.just(1, 2, 3).lastElement();
        MaybeObserver<Integer> observer = TestHelper.mockMaybeObserver();
        maybe.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(3);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastWithOneElement() {
        Maybe<Integer> maybe = Flowable.just(1).lastElement();
        MaybeObserver<Integer> observer = TestHelper.mockMaybeObserver();
        maybe.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(1);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastWithEmpty() {
        Maybe<Integer> maybe = Flowable.<Integer>empty().lastElement();
        MaybeObserver<Integer> observer = TestHelper.mockMaybeObserver();
        maybe.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer).onComplete();
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastWithPredicate() {
        Maybe<Integer> maybe = Flowable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).lastElement();
        MaybeObserver<Integer> observer = TestHelper.mockMaybeObserver();
        maybe.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(6);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastWithPredicateAndOneElement() {
        Maybe<Integer> maybe = Flowable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).lastElement();
        MaybeObserver<Integer> observer = TestHelper.mockMaybeObserver();
        maybe.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(2);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastWithPredicateAndEmpty() {
        Maybe<Integer> maybe = Flowable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).lastElement();
        MaybeObserver<Integer> observer = TestHelper.mockMaybeObserver();
        maybe.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer).onComplete();
        inOrder.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastOrDefault() {
        Single<Integer> single = Flowable.just(1, 2, 3).last(4);
        SingleObserver<Integer> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(3);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastOrDefaultWithOneElement() {
        Single<Integer> single = Flowable.just(1).last(2);
        SingleObserver<Integer> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(1);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastOrDefaultWithEmpty() {
        Single<Integer> single = Flowable.<Integer>empty().last(1);
        SingleObserver<Integer> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(1);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastOrDefaultWithPredicate() {
        Single<Integer> single = Flowable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).last(8);
        SingleObserver<Integer> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(6);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastOrDefaultWithPredicateAndOneElement() {
        Single<Integer> single = Flowable.just(1, 2).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).last(4);
        SingleObserver<Integer> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(2);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testLastOrDefaultWithPredicateAndEmpty() {
        Single<Integer> single = Flowable.just(1).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return (t1 % 2) == 0;
            }
        }).last(2);
        SingleObserver<Integer> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        InOrder inOrder = Mockito.inOrder(observer);
        inOrder.verify(observer, Mockito.times(1)).onSuccess(2);
        // inOrder.verify(observer, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void lastOrErrorNoElement() {
        Flowable.empty().lastOrError().test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void lastOrErrorOneElement() {
        Flowable.just(1).lastOrError().test().assertNoErrors().assertValue(1);
    }

    @Test
    public void lastOrErrorMultipleElements() {
        Flowable.just(1, 2, 3).lastOrError().test().assertNoErrors().assertValue(3);
    }

    @Test
    public void lastOrErrorError() {
        Flowable.error(new RuntimeException("error")).lastOrError().test().assertNoValues().assertErrorMessage("error").assertError(RuntimeException.class);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.never().lastElement().toFlowable());
        TestHelper.checkDisposed(Flowable.never().lastElement());
        TestHelper.checkDisposed(Flowable.just(1).lastOrError().toFlowable());
        TestHelper.checkDisposed(Flowable.just(1).lastOrError());
        TestHelper.checkDisposed(Flowable.just(1).last(2).toFlowable());
        TestHelper.checkDisposed(Flowable.just(1).last(2));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowableToMaybe(new Function<Flowable<Object>, MaybeSource<Object>>() {
            @Override
            public io.reactivex.MaybeSource<Object> apply(Flowable<Object> f) throws Exception {
                return f.lastElement();
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.lastElement().toFlowable();
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowableToSingle(new Function<Flowable<Object>, SingleSource<Object>>() {
            @Override
            public io.reactivex.SingleSource<Object> apply(Flowable<Object> f) throws Exception {
                return f.lastOrError();
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.lastOrError().toFlowable();
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowableToSingle(new Function<Flowable<Object>, SingleSource<Object>>() {
            @Override
            public io.reactivex.SingleSource<Object> apply(Flowable<Object> f) throws Exception {
                return f.last(2);
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.last(2).toFlowable();
            }
        });
    }

    @Test
    public void error() {
        Flowable.error(new TestException()).lastElement().test().assertFailure(TestException.class);
    }

    @Test
    public void errorLastOrErrorFlowable() {
        Flowable.error(new TestException()).lastOrError().toFlowable().test().assertFailure(TestException.class);
    }

    @Test
    public void emptyLastOrErrorFlowable() {
        Flowable.empty().lastOrError().toFlowable().test().assertFailure(NoSuchElementException.class);
    }
}

