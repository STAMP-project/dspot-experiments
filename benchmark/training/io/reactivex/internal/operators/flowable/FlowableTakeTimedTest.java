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
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.TestScheduler;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableTakeTimedTest {
    @Test
    public void testTakeTimed() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> source = PublishProcessor.create();
        Flowable<Integer> result = source.take(1, TimeUnit.SECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.onNext(4);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(2);
        inOrder.verify(subscriber).onNext(3);
        inOrder.verify(subscriber).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onNext(4);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testTakeTimedErrorBeforeTime() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> source = PublishProcessor.create();
        Flowable<Integer> result = source.take(1, TimeUnit.SECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        source.onError(new TestException());
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.onNext(4);
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(2);
        inOrder.verify(subscriber).onNext(3);
        inOrder.verify(subscriber).onError(ArgumentMatchers.any(TestException.class));
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(4);
    }

    @Test
    public void testTakeTimedErrorAfterTime() {
        TestScheduler scheduler = new TestScheduler();
        PublishProcessor<Integer> source = PublishProcessor.create();
        Flowable<Integer> result = source.take(1, TimeUnit.SECONDS, scheduler);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        source.onNext(1);
        source.onNext(2);
        source.onNext(3);
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
        source.onNext(4);
        source.onError(new TestException());
        InOrder inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber).onNext(1);
        inOrder.verify(subscriber).onNext(2);
        inOrder.verify(subscriber).onNext(3);
        inOrder.verify(subscriber).onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onNext(4);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(TestException.class));
    }

    @Test
    public void timedDefaultScheduler() {
        Flowable.range(1, 5).take(1, TimeUnit.MINUTES).test().awaitDone(5, TimeUnit.SECONDS).assertResult(1, 2, 3, 4, 5);
    }
}

