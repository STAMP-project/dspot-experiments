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


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import io.reactivex.observers.ObserverFusion;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableRangeLongTest {
    @Test
    public void testRangeStartAt2Count3() {
        Observer<Long> observer = mockObserver();
        Observable.rangeLong(2, 3).subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(2L);
        Mockito.verify(observer, Mockito.times(1)).onNext(3L);
        Mockito.verify(observer, Mockito.times(1)).onNext(4L);
        Mockito.verify(observer, Mockito.never()).onNext(5L);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testRangeUnsubscribe() {
        Observer<Long> observer = mockObserver();
        final AtomicInteger count = new AtomicInteger();
        Observable.rangeLong(1, 1000).doOnNext(new io.reactivex.functions.Consumer<Long>() {
            @Override
            public void accept(Long t1) {
                count.incrementAndGet();
            }
        }).take(3).subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(1L);
        Mockito.verify(observer, Mockito.times(1)).onNext(2L);
        Mockito.verify(observer, Mockito.times(1)).onNext(3L);
        Mockito.verify(observer, Mockito.never()).onNext(4L);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Assert.assertEquals(3, count.get());
    }

    @Test
    public void testRangeWithZero() {
        Observable.rangeLong(1L, 0L);
    }

    @Test
    public void testRangeWithOverflow2() {
        Observable.rangeLong(Long.MAX_VALUE, 0L);
    }

    @Test
    public void testRangeWithOverflow3() {
        Observable.rangeLong(1L, Long.MAX_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRangeWithOverflow4() {
        Observable.rangeLong(2L, Long.MAX_VALUE);
    }

    @Test
    public void testRangeWithOverflow5() {
        Assert.assertFalse(Observable.rangeLong(Long.MIN_VALUE, 0).blockingIterable().iterator().hasNext());
    }

    @Test
    public void testNoBackpressure() {
        ArrayList<Long> list = new ArrayList<Long>(((Flowable.bufferSize()) * 2));
        for (long i = 1; i <= (((Flowable.bufferSize()) * 2) + 1); i++) {
            list.add(i);
        }
        Observable<Long> o = Observable.rangeLong(1, list.size());
        TestObserver<Long> to = new TestObserver<Long>();
        o.subscribe(to);
        to.assertValueSequence(list);
        to.assertTerminated();
    }

    @Test
    public void testEmptyRangeSendsOnCompleteEagerlyWithRequestZero() {
        final AtomicBoolean completed = new AtomicBoolean(false);
        Observable.rangeLong(1L, 0L).subscribe(new DefaultObserver<Long>() {
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
            public void onNext(Long t) {
            }
        });
        Assert.assertTrue(completed.get());
    }

    @Test(timeout = 1000)
    public void testNearMaxValueWithoutBackpressure() {
        TestObserver<Long> to = new TestObserver<Long>();
        Observable.rangeLong(((Long.MAX_VALUE) - 1L), 2L).subscribe(to);
        to.assertComplete();
        to.assertNoErrors();
        to.assertValues(((Long.MAX_VALUE) - 1), Long.MAX_VALUE);
    }

    @Test
    public void negativeCount() {
        try {
            Observable.rangeLong(1L, (-1L));
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("count >= 0 required but it was -1", ex.getMessage());
        }
    }

    @Test
    public void countOne() {
        Observable.rangeLong(5495454L, 1L).test().assertResult(5495454L);
    }

    @Test
    public void noOverflow() {
        Observable.rangeLong(((Long.MAX_VALUE) - 1), 2);
        Observable.rangeLong(Long.MIN_VALUE, 2);
        Observable.rangeLong(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    @Test
    public void fused() {
        TestObserver<Long> to = ObserverFusion.newTest(ANY);
        Observable.rangeLong(1, 2).subscribe(to);
        ObserverFusion.assertFusion(to, SYNC).assertResult(1L, 2L);
    }

    @Test
    public void fusedReject() {
        TestObserver<Long> to = ObserverFusion.newTest(ASYNC);
        Observable.rangeLong(1, 2).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1L, 2L);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(Observable.rangeLong(1, 2));
    }

    @Test
    public void fusedClearIsEmpty() {
        TestHelper.checkFusedIsEmptyClear(Observable.rangeLong(1, 2));
    }
}

