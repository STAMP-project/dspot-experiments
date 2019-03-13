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
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


@SuppressWarnings("deprecation")
public class FlowableDematerializeTest {
    @Test
    public void simpleSelector() {
        Flowable<Notification<Integer>> notifications = Flowable.just(1, 2).materialize();
        Flowable<Integer> dematerialize = notifications.dematerialize(Functions.<Notification<Integer>>identity());
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        dematerialize.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void selectorCrash() {
        Flowable.just(1, 2).materialize().dematerialize(new io.reactivex.functions.Function<Notification<Integer>, Notification<Object>>() {
            @Override
            public io.reactivex.Notification<Object> apply(Notification<Integer> v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void selectorNull() {
        Flowable.just(1, 2).materialize().dematerialize(new io.reactivex.functions.Function<Notification<Integer>, Notification<Object>>() {
            @Override
            public io.reactivex.Notification<Object> apply(Notification<Integer> v) throws Exception {
                return null;
            }
        }).test().assertFailure(NullPointerException.class);
    }

    @Test
    public void testDematerialize1() {
        Flowable<Notification<Integer>> notifications = Flowable.just(1, 2).materialize();
        Flowable<Integer> dematerialize = notifications.dematerialize();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        dematerialize.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(2);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDematerialize2() {
        Throwable exception = new Throwable("test");
        Flowable<Integer> flowable = Flowable.error(exception);
        Flowable<Integer> dematerialize = flowable.materialize().dematerialize();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        dematerialize.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onError(exception);
        Mockito.verify(subscriber, Mockito.times(0)).onComplete();
        Mockito.verify(subscriber, Mockito.times(0)).onNext(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void testDematerialize3() {
        Exception exception = new Exception("test");
        Flowable<Integer> flowable = Flowable.error(exception);
        Flowable<Integer> dematerialize = flowable.materialize().dematerialize();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        dematerialize.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onError(exception);
        Mockito.verify(subscriber, Mockito.times(0)).onComplete();
        Mockito.verify(subscriber, Mockito.times(0)).onNext(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void testErrorPassThru() {
        Exception exception = new Exception("test");
        Flowable<Integer> flowable = Flowable.error(exception);
        Flowable<Integer> dematerialize = flowable.dematerialize();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        dematerialize.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onError(exception);
        Mockito.verify(subscriber, Mockito.times(0)).onComplete();
        Mockito.verify(subscriber, Mockito.times(0)).onNext(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void testCompletePassThru() {
        Flowable<Integer> flowable = Flowable.empty();
        Flowable<Integer> dematerialize = flowable.dematerialize();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(subscriber);
        dematerialize.subscribe(ts);
        System.out.println(ts.errors());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.times(0)).onNext(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void testHonorsContractWhenCompleted() {
        Flowable<Integer> source = Flowable.just(1);
        Flowable<Integer> result = source.materialize().dematerialize();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onNext(1);
        Mockito.verify(subscriber).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testHonorsContractWhenThrows() {
        Flowable<Integer> source = Flowable.error(new TestException());
        Flowable<Integer> result = source.materialize().dematerialize();
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(Integer.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(dematerialize());
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.dematerialize();
            }
        });
    }

    @Test
    public void eventsAfterDematerializedTerminal() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            dematerialize().test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 1, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void nonNotificationInstanceAfterDispose() {
        dematerialize().test().assertResult();
    }
}

