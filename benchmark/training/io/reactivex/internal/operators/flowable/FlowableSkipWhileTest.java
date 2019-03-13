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
import io.reactivex.internal.functions.Functions;
import io.reactivex.processors.PublishProcessor;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableSkipWhileTest {
    Subscriber<Integer> w = TestHelper.mockSubscriber();

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
        Flowable<Integer> src = Flowable.just(1, 2, 3, 4, 5);
        src.skipWhile(FlowableSkipWhileTest.INDEX_LESS_THAN_THREE).subscribe(w);
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.times(1)).onNext(4);
        inOrder.verify(w, Mockito.times(1)).onNext(5);
        inOrder.verify(w, Mockito.times(1)).onComplete();
        inOrder.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSkipEmpty() {
        Flowable<Integer> src = Flowable.empty();
        src.skipWhile(FlowableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipEverything() {
        Flowable<Integer> src = Flowable.just(1, 2, 3, 4, 3, 2, 1);
        src.skipWhile(FlowableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipNothing() {
        Flowable<Integer> src = Flowable.just(5, 3, 1);
        src.skipWhile(FlowableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.times(1)).onNext(5);
        inOrder.verify(w, Mockito.times(1)).onNext(3);
        inOrder.verify(w, Mockito.times(1)).onNext(1);
        inOrder.verify(w, Mockito.times(1)).onComplete();
        inOrder.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSkipSome() {
        Flowable<Integer> src = Flowable.just(1, 2, 3, 4, 5, 3, 1, 5);
        src.skipWhile(FlowableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
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
        Flowable<Integer> src = Flowable.just(1, 2, 42, 5, 3, 1);
        src.skipWhile(FlowableSkipWhileTest.LESS_THAN_FIVE).subscribe(w);
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyInt());
        inOrder.verify(w, Mockito.never()).onComplete();
        inOrder.verify(w, Mockito.times(1)).onError(ArgumentMatchers.any(RuntimeException.class));
    }

    @Test
    public void testSkipManySubscribers() {
        Flowable<Integer> src = Flowable.range(1, 10).skipWhile(FlowableSkipWhileTest.LESS_THAN_FIVE);
        int n = 5;
        for (int i = 0; i < n; i++) {
            Subscriber<Object> subscriber = TestHelper.mockSubscriber();
            InOrder inOrder = Mockito.inOrder(subscriber);
            src.subscribe(subscriber);
            for (int j = 5; j < 10; j++) {
                inOrder.verify(subscriber).onNext(j);
            }
            inOrder.verify(subscriber).onComplete();
            Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().skipWhile(Functions.alwaysFalse()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.skipWhile(Functions.alwaysFalse());
            }
        });
    }

    @Test
    public void error() {
        Flowable.error(new TestException()).skipWhile(Functions.alwaysFalse()).test().assertFailure(TestException.class);
    }
}

