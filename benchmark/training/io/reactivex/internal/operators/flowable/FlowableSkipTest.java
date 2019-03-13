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
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableSkipTest {
    @Test
    public void testSkipNegativeElements() {
        Flowable<String> skip = Flowable.just("one", "two", "three").skip((-99));
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        skip.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipZeroElements() {
        Flowable<String> skip = Flowable.just("one", "two", "three").skip(0);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        skip.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipOneElement() {
        Flowable<String> skip = Flowable.just("one", "two", "three").skip(1);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        skip.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext("one");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipTwoElements() {
        Flowable<String> skip = Flowable.just("one", "two", "three").skip(2);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        skip.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onNext("two");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipEmptyStream() {
        Flowable<String> w = Flowable.empty();
        Flowable<String> skip = w.skip(1);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        skip.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipMultipleObservers() {
        Flowable<String> skip = Flowable.just("one", "two", "three").skip(2);
        Subscriber<String> subscriber1 = TestHelper.mockSubscriber();
        skip.subscribe(subscriber1);
        Subscriber<String> subscriber2 = TestHelper.mockSubscriber();
        skip.subscribe(subscriber2);
        Mockito.verify(subscriber1, Mockito.times(1)).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber1, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber2, Mockito.times(1)).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber2, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipError() {
        Exception e = new Exception();
        Flowable<String> ok = Flowable.just("one");
        Flowable<String> error = Flowable.error(e);
        Flowable<String> skip = Flowable.concat(ok, error).skip(100);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        skip.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.times(1)).onError(e);
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void testBackpressureMultipleSmallAsyncRequests() throws InterruptedException {
        final AtomicLong requests = new AtomicLong(0);
        TestSubscriber<Long> ts = new TestSubscriber<Long>(0L);
        Flowable.interval(100, TimeUnit.MILLISECONDS).doOnRequest(new LongConsumer() {
            @Override
            public void accept(long n) {
                requests.addAndGet(n);
            }
        }).skip(4).subscribe(ts);
        Thread.sleep(100);
        ts.request(1);
        ts.request(1);
        Thread.sleep(100);
        ts.dispose();
        // FIXME not assertable anymore
        // ts.assertUnsubscribed();
        ts.assertNoErrors();
        Assert.assertEquals(6, requests.get());
    }

    @Test
    public void testRequestOverflowDoesNotOccur() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(((Long.MAX_VALUE) - 1));
        Flowable.range(1, 10).skip(5).subscribe(ts);
        ts.assertTerminated();
        ts.assertComplete();
        ts.assertNoErrors();
        Assert.assertEquals(Arrays.asList(6, 7, 8, 9, 10), ts.values());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).skip(2));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.skip(1);
            }
        });
    }
}

