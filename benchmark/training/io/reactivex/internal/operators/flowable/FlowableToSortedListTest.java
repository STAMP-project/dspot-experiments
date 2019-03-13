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
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableToSortedListTest {
    @Test
    public void testSortedListFlowable() {
        Flowable<Integer> w = Flowable.just(1, 3, 2, 5, 4);
        Flowable<List<Integer>> flowable = w.toSortedList().toFlowable();
        Subscriber<List<Integer>> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(Arrays.asList(1, 2, 3, 4, 5));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSortedListWithCustomFunctionFlowable() {
        Flowable<Integer> w = Flowable.just(1, 3, 2, 5, 4);
        Flowable<List<Integer>> flowable = w.toSortedList(new Comparator<Integer>() {
            @Override
            public int compare(Integer t1, Integer t2) {
                return t2 - t1;
            }
        }).toFlowable();
        Subscriber<List<Integer>> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(Arrays.asList(5, 4, 3, 2, 1));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testWithFollowingFirstFlowable() {
        Flowable<Integer> f = Flowable.just(1, 3, 2, 5, 4);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), f.toSortedList().toFlowable().blockingFirst());
    }

    @Test
    public void testBackpressureHonoredFlowable() {
        Flowable<List<Integer>> w = Flowable.just(1, 3, 2, 5, 4).toSortedList().toFlowable();
        TestSubscriber<List<Integer>> ts = new TestSubscriber<List<Integer>>(0L);
        w.subscribe(ts);
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertNotComplete();
        ts.request(1);
        ts.assertValue(Arrays.asList(1, 2, 3, 4, 5));
        ts.assertNoErrors();
        ts.assertComplete();
        ts.request(1);
        ts.assertValue(Arrays.asList(1, 2, 3, 4, 5));
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void sorted() {
        Flowable.just(5, 1, 2, 4, 3).sorted().test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void sortedComparator() {
        Flowable.just(5, 1, 2, 4, 3).sorted(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b - a;
            }
        }).test().assertResult(5, 4, 3, 2, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toSortedListCapacityFlowable() {
        Flowable.just(5, 1, 2, 4, 3).toSortedList(4).toFlowable().test().assertResult(Arrays.asList(1, 2, 3, 4, 5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toSortedListComparatorCapacityFlowable() {
        Flowable.just(5, 1, 2, 4, 3).toSortedList(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b - a;
            }
        }, 4).toFlowable().test().assertResult(Arrays.asList(5, 4, 3, 2, 1));
    }

    @Test
    public void testSortedList() {
        Flowable<Integer> w = Flowable.just(1, 3, 2, 5, 4);
        Single<List<Integer>> single = w.toSortedList();
        SingleObserver<List<Integer>> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(Arrays.asList(1, 2, 3, 4, 5));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSortedListWithCustomFunction() {
        Flowable<Integer> w = Flowable.just(1, 3, 2, 5, 4);
        Single<List<Integer>> single = w.toSortedList(new Comparator<Integer>() {
            @Override
            public int compare(Integer t1, Integer t2) {
                return t2 - t1;
            }
        });
        SingleObserver<List<Integer>> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(Arrays.asList(5, 4, 3, 2, 1));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testWithFollowingFirst() {
        Flowable<Integer> f = Flowable.just(1, 3, 2, 5, 4);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), f.toSortedList().blockingGet());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toSortedListCapacity() {
        Flowable.just(5, 1, 2, 4, 3).toSortedList(4).test().assertResult(Arrays.asList(1, 2, 3, 4, 5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toSortedListComparatorCapacity() {
        Flowable.just(5, 1, 2, 4, 3).toSortedList(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b - a;
            }
        }, 4).test().assertResult(Arrays.asList(5, 4, 3, 2, 1));
    }
}

