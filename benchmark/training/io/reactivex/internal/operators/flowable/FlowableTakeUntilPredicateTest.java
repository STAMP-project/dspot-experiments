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
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableTakeUntilPredicateTest {
    @Test
    public void takeEmpty() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        Flowable.empty().takeUntil(new Predicate<Object>() {
            @Override
            public boolean test(Object v) {
                return true;
            }
        }).subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber).onComplete();
    }

    @Test
    public void takeAll() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        Flowable.just(1, 2).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        }).subscribe(subscriber);
        Mockito.verify(subscriber).onNext(1);
        Mockito.verify(subscriber).onNext(2);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber).onComplete();
    }

    @Test
    public void takeFirst() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        Flowable.just(1, 2).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return true;
            }
        }).subscribe(subscriber);
        Mockito.verify(subscriber).onNext(1);
        Mockito.verify(subscriber, Mockito.never()).onNext(2);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber).onComplete();
    }

    @Test
    public void takeSome() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        Flowable.just(1, 2, 3).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                return t1 == 2;
            }
        }).subscribe(subscriber);
        Mockito.verify(subscriber).onNext(1);
        Mockito.verify(subscriber).onNext(2);
        Mockito.verify(subscriber, Mockito.never()).onNext(3);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber).onComplete();
    }

    @Test
    public void functionThrows() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                throw new TestException("Forced failure");
            }
        };
        Flowable.just(1, 2, 3).takeUntil(predicate).subscribe(subscriber);
        Mockito.verify(subscriber).onNext(1);
        Mockito.verify(subscriber, Mockito.never()).onNext(2);
        Mockito.verify(subscriber, Mockito.never()).onNext(3);
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void sourceThrows() {
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).concatWith(Flowable.just(2)).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        }).subscribe(subscriber);
        Mockito.verify(subscriber).onNext(1);
        Mockito.verify(subscriber, Mockito.never()).onNext(2);
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void backpressure() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(5L);
        Flowable.range(1, 1000).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) {
                return false;
            }
        }).subscribe(ts);
        ts.assertNoErrors();
        ts.assertValues(1, 2, 3, 4, 5);
        ts.assertNotComplete();
    }

    @Test
    public void testErrorIncludesLastValueAsCause() {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        final TestException e = new TestException("Forced failure");
        Predicate<String> predicate = new Predicate<String>() {
            @Override
            public boolean test(String t) {
                throw e;
            }
        };
        Flowable.just("abc").takeUntil(predicate).subscribe(ts);
        ts.assertTerminated();
        ts.assertNotComplete();
        ts.assertError(TestException.class);
        // FIXME last cause value is not saved
        // assertTrue(ts.errors().get(0).getCause().getMessage().contains("abc"));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().takeUntil(Functions.alwaysFalse()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.takeUntil(Functions.alwaysFalse());
            }
        });
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                    subscriber.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    subscriber.onComplete();
                    subscriber.onNext(1);
                    subscriber.onError(new TestException());
                    subscriber.onComplete();
                }
            }.takeUntil(Functions.alwaysFalse()).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

