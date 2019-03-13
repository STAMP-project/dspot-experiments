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
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableSkipLastTest {
    @Test
    public void testSkipLastEmpty() {
        Flowable<String> flowable = Flowable.<String>empty().skipLast(2);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipLast1() {
        Flowable<String> flowable = Flowable.fromIterable(Arrays.asList("one", "two", "three")).skipLast(2);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        flowable.subscribe(subscriber);
        inOrder.verify(subscriber, Mockito.never()).onNext("two");
        inOrder.verify(subscriber, Mockito.never()).onNext("three");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipLast2() {
        Flowable<String> flowable = Flowable.fromIterable(Arrays.asList("one", "two")).skipLast(2);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipLastWithZeroCount() {
        Flowable<String> w = Flowable.just("one", "two");
        Flowable<String> flowable = w.skipLast(0);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipLastWithBackpressure() {
        Flowable<Integer> f = Flowable.range(0, ((Flowable.bufferSize()) * 2)).skipLast(((Flowable.bufferSize()) + 10));
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        f.observeOn(Schedulers.computation()).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) - 10), ts.valueCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSkipLastWithNegativeCount() {
        Flowable.just("one").skipLast((-1));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).skipLast(1));
    }

    @Test
    public void error() {
        Flowable.error(new TestException()).skipLast(1).test().assertFailure(TestException.class);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.skipLast(1);
            }
        });
    }
}

