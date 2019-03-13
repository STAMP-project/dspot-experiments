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
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class FlowableTakeUntilTest {
    @Test
    public void testTakeUntil() {
        Subscription sSource = Mockito.mock(Subscription.class);
        Subscription sOther = Mockito.mock(Subscription.class);
        FlowableTakeUntilTest.TestObservable source = new FlowableTakeUntilTest.TestObservable(sSource);
        FlowableTakeUntilTest.TestObservable other = new FlowableTakeUntilTest.TestObservable(sOther);
        Subscriber<String> result = TestHelper.mockSubscriber();
        Flowable<String> stringObservable = Flowable.unsafeCreate(source).takeUntil(Flowable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        other.sendOnNext("three");
        source.sendOnNext("four");
        source.sendOnCompleted();
        other.sendOnCompleted();
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(result, Mockito.times(0)).onNext("three");
        Mockito.verify(result, Mockito.times(0)).onNext("four");
        Mockito.verify(sSource, Mockito.times(1)).cancel();
        Mockito.verify(sOther, Mockito.times(1)).cancel();
    }

    @Test
    public void testTakeUntilSourceCompleted() {
        Subscription sSource = Mockito.mock(Subscription.class);
        Subscription sOther = Mockito.mock(Subscription.class);
        FlowableTakeUntilTest.TestObservable source = new FlowableTakeUntilTest.TestObservable(sSource);
        FlowableTakeUntilTest.TestObservable other = new FlowableTakeUntilTest.TestObservable(sOther);
        Subscriber<String> result = TestHelper.mockSubscriber();
        Flowable<String> stringObservable = Flowable.unsafeCreate(source).takeUntil(Flowable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        source.sendOnCompleted();
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(sSource, Mockito.never()).cancel();
        Mockito.verify(sOther, Mockito.times(1)).cancel();
    }

    @Test
    public void testTakeUntilSourceError() {
        Subscription sSource = Mockito.mock(Subscription.class);
        Subscription sOther = Mockito.mock(Subscription.class);
        FlowableTakeUntilTest.TestObservable source = new FlowableTakeUntilTest.TestObservable(sSource);
        FlowableTakeUntilTest.TestObservable other = new FlowableTakeUntilTest.TestObservable(sOther);
        Throwable error = new Throwable();
        Subscriber<String> result = TestHelper.mockSubscriber();
        Flowable<String> stringObservable = Flowable.unsafeCreate(source).takeUntil(Flowable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        source.sendOnError(error);
        source.sendOnNext("three");
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(result, Mockito.times(0)).onNext("three");
        Mockito.verify(result, Mockito.times(1)).onError(error);
        Mockito.verify(sSource, Mockito.never()).cancel();
        Mockito.verify(sOther, Mockito.times(1)).cancel();
    }

    @Test
    public void testTakeUntilOtherError() {
        Subscription sSource = Mockito.mock(Subscription.class);
        Subscription sOther = Mockito.mock(Subscription.class);
        FlowableTakeUntilTest.TestObservable source = new FlowableTakeUntilTest.TestObservable(sSource);
        FlowableTakeUntilTest.TestObservable other = new FlowableTakeUntilTest.TestObservable(sOther);
        Throwable error = new Throwable();
        Subscriber<String> result = TestHelper.mockSubscriber();
        Flowable<String> stringObservable = Flowable.unsafeCreate(source).takeUntil(Flowable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        other.sendOnError(error);
        source.sendOnNext("three");
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(result, Mockito.times(0)).onNext("three");
        Mockito.verify(result, Mockito.times(1)).onError(error);
        Mockito.verify(result, Mockito.times(0)).onComplete();
        Mockito.verify(sSource, Mockito.times(1)).cancel();
        Mockito.verify(sOther, Mockito.never()).cancel();
    }

    /**
     * If the 'other' onCompletes then we unsubscribe from the source and onComplete.
     */
    @Test
    public void testTakeUntilOtherCompleted() {
        Subscription sSource = Mockito.mock(Subscription.class);
        Subscription sOther = Mockito.mock(Subscription.class);
        FlowableTakeUntilTest.TestObservable source = new FlowableTakeUntilTest.TestObservable(sSource);
        FlowableTakeUntilTest.TestObservable other = new FlowableTakeUntilTest.TestObservable(sOther);
        Subscriber<String> result = TestHelper.mockSubscriber();
        Flowable<String> stringObservable = Flowable.unsafeCreate(source).takeUntil(Flowable.unsafeCreate(other));
        stringObservable.subscribe(result);
        source.sendOnNext("one");
        source.sendOnNext("two");
        other.sendOnCompleted();
        source.sendOnNext("three");
        Mockito.verify(result, Mockito.times(1)).onNext("one");
        Mockito.verify(result, Mockito.times(1)).onNext("two");
        Mockito.verify(result, Mockito.times(0)).onNext("three");
        Mockito.verify(result, Mockito.times(1)).onComplete();
        Mockito.verify(sSource, Mockito.times(1)).cancel();
        Mockito.verify(sOther, Mockito.never()).cancel();// unsubscribed since SafeSubscriber unsubscribes after onComplete

    }

    private static class TestObservable implements Publisher<String> {
        io.reactivex.Subscriber<? super String> subscriber;

        Subscription upstream;

        TestObservable(Subscription s) {
            this.upstream = s;
        }

        /* used to simulate subscription */
        public void sendOnCompleted() {
            subscriber.onComplete();
        }

        /* used to simulate subscription */
        public void sendOnNext(String value) {
            subscriber.onNext(value);
        }

        /* used to simulate subscription */
        public void sendOnError(Throwable e) {
            subscriber.onError(e);
        }

        @Override
        public void subscribe(Subscriber<? super String> subscriber) {
            this.subscriber = subscriber;
            subscriber.onSubscribe(upstream);
        }
    }

    @Test
    public void testUntilFires() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> until = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.takeUntil(until).subscribe(ts);
        Assert.assertTrue(source.hasSubscribers());
        Assert.assertTrue(until.hasSubscribers());
        source.onNext(1);
        ts.assertValue(1);
        until.onNext(1);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertTerminated();
        Assert.assertFalse("Source still has observers", source.hasSubscribers());
        Assert.assertFalse("Until still has observers", until.hasSubscribers());
        Assert.assertFalse("TestSubscriber is unsubscribed", ts.isCancelled());
    }

    @Test
    public void testMainCompletes() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> until = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.takeUntil(until).subscribe(ts);
        Assert.assertTrue(source.hasSubscribers());
        Assert.assertTrue(until.hasSubscribers());
        source.onNext(1);
        source.onComplete();
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertTerminated();
        Assert.assertFalse("Source still has observers", source.hasSubscribers());
        Assert.assertFalse("Until still has observers", until.hasSubscribers());
        Assert.assertFalse("TestSubscriber is unsubscribed", ts.isCancelled());
    }

    @Test
    public void testDownstreamUnsubscribes() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        PublishProcessor<Integer> until = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        source.takeUntil(until).take(1).subscribe(ts);
        Assert.assertTrue(source.hasSubscribers());
        Assert.assertTrue(until.hasSubscribers());
        source.onNext(1);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertTerminated();
        Assert.assertFalse("Source still has observers", source.hasSubscribers());
        Assert.assertFalse("Until still has observers", until.hasSubscribers());
        Assert.assertFalse("TestSubscriber is unsubscribed", ts.isCancelled());
    }

    @Test
    public void testBackpressure() {
        PublishProcessor<Integer> until = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0L);
        Flowable.range(1, 10).takeUntil(until).subscribe(ts);
        Assert.assertTrue(until.hasSubscribers());
        ts.request(1);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertNotComplete();
        until.onNext(5);
        ts.assertComplete();
        ts.assertNoErrors();
        Assert.assertFalse("Until still has observers", until.hasSubscribers());
        Assert.assertFalse("TestSubscriber is unsubscribed", ts.isCancelled());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().takeUntil(Flowable.never()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Flowable<Integer> c) throws Exception {
                return c.takeUntil(Flowable.never());
            }
        });
    }

    @Test
    public void untilPublisherMainSuccess() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.takeUntil(other).test();
        Assert.assertTrue("Main no subscribers?", main.hasSubscribers());
        Assert.assertTrue("Other no subscribers?", other.hasSubscribers());
        main.onNext(1);
        main.onNext(2);
        main.onComplete();
        Assert.assertFalse("Main has subscribers?", main.hasSubscribers());
        Assert.assertFalse("Other has subscribers?", other.hasSubscribers());
        ts.assertResult(1, 2);
    }

    @Test
    public void untilPublisherMainComplete() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.takeUntil(other).test();
        Assert.assertTrue("Main no subscribers?", main.hasSubscribers());
        Assert.assertTrue("Other no subscribers?", other.hasSubscribers());
        main.onComplete();
        Assert.assertFalse("Main has subscribers?", main.hasSubscribers());
        Assert.assertFalse("Other has subscribers?", other.hasSubscribers());
        ts.assertResult();
    }

    @Test
    public void untilPublisherMainError() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.takeUntil(other).test();
        Assert.assertTrue("Main no subscribers?", main.hasSubscribers());
        Assert.assertTrue("Other no subscribers?", other.hasSubscribers());
        main.onError(new TestException());
        Assert.assertFalse("Main has subscribers?", main.hasSubscribers());
        Assert.assertFalse("Other has subscribers?", other.hasSubscribers());
        ts.assertFailure(TestException.class);
    }

    @Test
    public void untilPublisherOtherOnNext() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.takeUntil(other).test();
        Assert.assertTrue("Main no subscribers?", main.hasSubscribers());
        Assert.assertTrue("Other no subscribers?", other.hasSubscribers());
        other.onNext(1);
        Assert.assertFalse("Main has subscribers?", main.hasSubscribers());
        Assert.assertFalse("Other has subscribers?", other.hasSubscribers());
        ts.assertResult();
    }

    @Test
    public void untilPublisherOtherOnComplete() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.takeUntil(other).test();
        Assert.assertTrue("Main no subscribers?", main.hasSubscribers());
        Assert.assertTrue("Other no subscribers?", other.hasSubscribers());
        other.onComplete();
        Assert.assertFalse("Main has subscribers?", main.hasSubscribers());
        Assert.assertFalse("Other has subscribers?", other.hasSubscribers());
        ts.assertResult();
    }

    @Test
    public void untilPublisherOtherError() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.takeUntil(other).test();
        Assert.assertTrue("Main no subscribers?", main.hasSubscribers());
        Assert.assertTrue("Other no subscribers?", other.hasSubscribers());
        other.onError(new TestException());
        Assert.assertFalse("Main has subscribers?", main.hasSubscribers());
        Assert.assertFalse("Other has subscribers?", other.hasSubscribers());
        ts.assertFailure(TestException.class);
    }

    @Test
    public void untilPublisherDispose() {
        PublishProcessor<Integer> main = PublishProcessor.create();
        PublishProcessor<Integer> other = PublishProcessor.create();
        TestSubscriber<Integer> ts = main.takeUntil(other).test();
        Assert.assertTrue("Main no subscribers?", main.hasSubscribers());
        Assert.assertTrue("Other no subscribers?", other.hasSubscribers());
        ts.dispose();
        Assert.assertFalse("Main has subscribers?", main.hasSubscribers());
        Assert.assertFalse("Other has subscribers?", other.hasSubscribers());
        ts.assertEmpty();
    }
}

