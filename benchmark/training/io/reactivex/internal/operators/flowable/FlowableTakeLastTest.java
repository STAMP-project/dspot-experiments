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


import io.reactivex.Flowable;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableTakeLastTest {
    @Test
    public void testTakeLastEmpty() {
        Flowable<String> w = Flowable.empty();
        Flowable<String> take = w.takeLast(2);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        take.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTakeLast1() {
        Flowable<String> w = Flowable.just("one", "two", "three");
        Flowable<String> take = w.takeLast(2);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        take.subscribe(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onNext("two");
        inOrder.verify(subscriber, Mockito.times(1)).onNext("three");
        Mockito.verify(subscriber, Mockito.never()).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTakeLast2() {
        Flowable<String> w = Flowable.just("one");
        Flowable<String> take = w.takeLast(10);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        take.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testTakeLastWithZeroCount() {
        Flowable<String> w = Flowable.just("one");
        Flowable<String> take = w.takeLast(0);
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        take.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onNext("one");
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testTakeLastWithNegativeCount() {
        Flowable.just("one").takeLast((-1));
    }

    @Test
    public void testBackpressure1() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, 100000).takeLast(1).observeOn(Schedulers.newThread()).map(newSlowProcessor()).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        ts.assertValue(100000);
    }

    @Test
    public void testBackpressure2() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, 100000).takeLast(((Flowable.bufferSize()) * 4)).observeOn(Schedulers.newThread()).map(newSlowProcessor()).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) * 4), ts.valueCount());
    }

    @Test
    public void testIssue1522() {
        // https://github.com/ReactiveX/RxJava/issues/1522
        Assert.assertEquals(0, Flowable.empty().count().toFlowable().filter(new Predicate<Long>() {
            @Override
            public boolean test(Long v) {
                return false;
            }
        }).toList().blockingGet().size());
    }

    @Test
    public void testIgnoreRequest1() {
        // If `takeLast` does not ignore `request` properly, StackOverflowError will be thrown.
        Flowable.range(0, 100000).takeLast(100000).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(Long.MAX_VALUE);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer integer) {
                request(Long.MAX_VALUE);
            }
        });
    }

    @Test
    public void testIgnoreRequest2() {
        // If `takeLast` does not ignore `request` properly, StackOverflowError will be thrown.
        Flowable.range(0, 100000).takeLast(100000).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer integer) {
                request(1);
            }
        });
    }

    @Test(timeout = 30000)
    public void testIgnoreRequest3() {
        // If `takeLast` does not ignore `request` properly, it will enter an infinite loop.
        Flowable.range(0, 100000).takeLast(100000).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(1);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer integer) {
                request(Long.MAX_VALUE);
            }
        });
    }

    @Test
    public void testIgnoreRequest4() {
        // If `takeLast` does not ignore `request` properly, StackOverflowError will be thrown.
        Flowable.range(0, 100000).takeLast(100000).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(Long.MAX_VALUE);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer integer) {
                request(1);
            }
        });
    }

    @Test
    public void testUnsubscribeTakesEffectEarlyOnFastPath() {
        final AtomicInteger count = new AtomicInteger();
        Flowable.range(0, 100000).takeLast(100000).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(Long.MAX_VALUE);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer integer) {
                count.incrementAndGet();
                cancel();
            }
        });
        Assert.assertEquals(1, count.get());
    }

    @Test(timeout = 10000)
    public void testRequestOverflow() {
        final List<Integer> list = new ArrayList<Integer>();
        Flowable.range(1, 100).takeLast(50).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(2);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
                list.add(t);
                request(((Long.MAX_VALUE) - 1));
            }
        });
        Assert.assertEquals(50, list.size());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.range(1, 10).takeLast(5));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.takeLast(5);
            }
        });
    }

    @Test
    public void error() {
        Flowable.error(new TestException()).takeLast(5).test().assertFailure(TestException.class);
    }

    @Test
    public void takeLastTake() {
        Flowable.range(1, 10).takeLast(5).take(2).test().assertResult(6, 7);
    }
}

