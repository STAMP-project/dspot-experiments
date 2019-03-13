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


import io.reactivex.TestHelper;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableToSortedListTest {
    @Test
    public void testSortedListObservable() {
        Observable<Integer> w = just(1, 3, 2, 5, 4);
        Observable<List<Integer>> observable = w.toSortedList().toObservable();
        Observer<List<Integer>> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(Arrays.asList(1, 2, 3, 4, 5));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSortedListWithCustomFunctionFlowable() {
        Observable<Integer> w = just(1, 3, 2, 5, 4);
        Observable<List<Integer>> observable = w.toSortedList(new Comparator<Integer>() {
            @Override
            public int compare(Integer t1, Integer t2) {
                return t2 - t1;
            }
        }).toObservable();
        Observer<List<Integer>> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(Arrays.asList(5, 4, 3, 2, 1));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testWithFollowingFirstObservable() {
        Observable<Integer> o = just(1, 3, 2, 5, 4);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), o.toSortedList().toObservable().blockingFirst());
    }

    @Test
    public void sorted() {
        just(5, 1, 2, 4, 3).sorted().test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void sortedComparator() {
        just(5, 1, 2, 4, 3).sorted(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b - a;
            }
        }).test().assertResult(5, 4, 3, 2, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toSortedListCapacityObservable() {
        just(5, 1, 2, 4, 3).toSortedList(4).toObservable().test().assertResult(Arrays.asList(1, 2, 3, 4, 5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toSortedListComparatorCapacityObservable() {
        just(5, 1, 2, 4, 3).toSortedList(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b - a;
            }
        }, 4).toObservable().test().assertResult(Arrays.asList(5, 4, 3, 2, 1));
    }

    @Test
    public void testSortedList() {
        Observable<Integer> w = just(1, 3, 2, 5, 4);
        Single<List<Integer>> single = w.toSortedList();
        SingleObserver<List<Integer>> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(Arrays.asList(1, 2, 3, 4, 5));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSortedListWithCustomFunction() {
        Observable<Integer> w = just(1, 3, 2, 5, 4);
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
        Observable<Integer> o = just(1, 3, 2, 5, 4);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), o.toSortedList().blockingGet());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toSortedListCapacity() {
        just(5, 1, 2, 4, 3).toSortedList(4).test().assertResult(Arrays.asList(1, 2, 3, 4, 5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void toSortedListComparatorCapacity() {
        just(5, 1, 2, 4, 3).toSortedList(new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return b - a;
            }
        }, 4).test().assertResult(Arrays.asList(5, 4, 3, 2, 1));
    }
}

