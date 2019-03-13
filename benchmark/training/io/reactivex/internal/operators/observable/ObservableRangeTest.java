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
package io.reactivex.internal.operators.observable;


import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import io.reactivex.observers.ObserverFusion;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableRangeTest {
    @Test
    public void testRangeStartAt2Count3() {
        Observer<Integer> observer = mockObserver();
        Observable.range(2, 3).subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onNext(3);
        Mockito.verify(observer, Mockito.times(1)).onNext(4);
        Mockito.verify(observer, Mockito.never()).onNext(5);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testRangeUnsubscribe() {
        Observer<Integer> observer = mockObserver();
        final AtomicInteger count = new AtomicInteger();
        Observable.range(1, 1000).doOnNext(new io.reactivex.functions.Consumer<Integer>() {
            @Override
            public void accept(Integer t1) {
                count.incrementAndGet();
            }
        }).take(3).subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onNext(3);
        Mockito.verify(observer, Mockito.never()).onNext(4);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Assert.assertEquals(3, count.get());
    }

    @Test
    public void testRangeWithZero() {
        Observable.range(1, 0);
    }

    @Test
    public void testRangeWithOverflow2() {
        Observable.range(Integer.MAX_VALUE, 0);
    }

    @Test
    public void testRangeWithOverflow3() {
        Observable.range(1, Integer.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeWithOverflow4() {
        Observable.range(2, Integer.MAX_VALUE);
    }

    @Test
    public void testRangeWithOverflow5() {
        Assert.assertFalse(Observable.range(Integer.MIN_VALUE, 0).blockingIterable().iterator().hasNext());
    }

    @Test
    public void testNoBackpressure() {
        ArrayList<Integer> list = new ArrayList<Integer>(((Flowable.bufferSize()) * 2));
        for (int i = 1; i <= (((Flowable.bufferSize()) * 2) + 1); i++) {
            list.add(i);
        }
        Observable<Integer> o = Observable.range(1, list.size());
        TestObserver<Integer> to = new TestObserver<Integer>();
        o.subscribe(to);
        to.assertValueSequence(list);
        to.assertTerminated();
    }

    @Test
    public void testEmptyRangeSendsOnCompleteEagerlyWithRequestZero() {
        final AtomicBoolean completed = new AtomicBoolean(false);
        Observable.range(1, 0).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onStart() {
                // request(0);
            }

            @Override
            public void onComplete() {
                completed.set(true);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
            }
        });
        Assert.assertTrue(completed.get());
    }

    @Test(timeout = 1000)
    public void testNearMaxValueWithoutBackpressure() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(((Integer.MAX_VALUE) - 1), 2).subscribe(to);
        to.assertComplete();
        to.assertNoErrors();
        to.assertValues(((Integer.MAX_VALUE) - 1), Integer.MAX_VALUE);
    }

    @Test
    public void negativeCount() {
        try {
            Observable.range(1, (-1));
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("count >= 0 required but it was -1", ex.getMessage());
        }
    }

    @Test
    public void requestWrongFusion() {
        TestObserver<Integer> to = ObserverFusion.newTest(ASYNC);
        Observable.range(1, 5).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3, 4, 5);
    }
}

