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
import io.reactivex.exceptions.TestException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableToListTest {
    @Test
    public void testListObservable() {
        Observable<String> w = fromIterable(Arrays.asList("one", "two", "three"));
        Observable<List<String>> observable = w.toList().toObservable();
        Observer<List<String>> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(Arrays.asList("one", "two", "three"));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testListViaObservableObservable() {
        Observable<String> w = fromIterable(Arrays.asList("one", "two", "three"));
        Observable<List<String>> observable = w.toList().toObservable();
        Observer<List<String>> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(Arrays.asList("one", "two", "three"));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testListMultipleSubscribersObservable() {
        Observable<String> w = fromIterable(Arrays.asList("one", "two", "three"));
        Observable<List<String>> observable = w.toList().toObservable();
        Observer<List<String>> o1 = mockObserver();
        observable.subscribe(o1);
        Observer<List<String>> o2 = mockObserver();
        observable.subscribe(o2);
        List<String> expected = Arrays.asList("one", "two", "three");
        Mockito.verify(o1, Mockito.times(1)).onNext(expected);
        Mockito.verify(o1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o1, Mockito.times(1)).onComplete();
        Mockito.verify(o2, Mockito.times(1)).onNext(expected);
        Mockito.verify(o2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o2, Mockito.times(1)).onComplete();
    }

    @Test
    public void testListWithBlockingFirstObservable() {
        Observable<String> o = fromIterable(Arrays.asList("one", "two", "three"));
        List<String> actual = o.toList().toObservable().blockingFirst();
        Assert.assertEquals(Arrays.asList("one", "two", "three"), actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void capacityHintObservable() {
        range(1, 10).toList(4).toObservable().test().assertResult(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void testList() {
        Observable<String> w = fromIterable(Arrays.asList("one", "two", "three"));
        Single<List<String>> single = w.toList();
        SingleObserver<List<String>> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(Arrays.asList("one", "two", "three"));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testListViaObservable() {
        Observable<String> w = fromIterable(Arrays.asList("one", "two", "three"));
        Single<List<String>> single = w.toList();
        SingleObserver<List<String>> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(Arrays.asList("one", "two", "three"));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testListMultipleSubscribers() {
        Observable<String> w = fromIterable(Arrays.asList("one", "two", "three"));
        Single<List<String>> single = w.toList();
        SingleObserver<List<String>> o1 = TestHelper.mockSingleObserver();
        single.subscribe(o1);
        SingleObserver<List<String>> o2 = TestHelper.mockSingleObserver();
        single.subscribe(o2);
        List<String> expected = Arrays.asList("one", "two", "three");
        Mockito.verify(o1, Mockito.times(1)).onSuccess(expected);
        Mockito.verify(o1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o2, Mockito.times(1)).onSuccess(expected);
        Mockito.verify(o2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testListWithBlockingFirst() {
        Observable<String> o = fromIterable(Arrays.asList("one", "two", "three"));
        List<String> actual = o.toList().blockingGet();
        Assert.assertEquals(Arrays.asList("one", "two", "three"), actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void capacityHint() {
        range(1, 10).toList(4).test().assertResult(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(just(1).toList().toObservable());
        TestHelper.checkDisposed(just(1).toList());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void error() {
        Observable.error(new TestException()).toList().toObservable().test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorSingle() {
        Observable.error(new TestException()).toList().test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void collectionSupplierThrows() {
        just(1).toList(new Callable<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() throws Exception {
                throw new TestException();
            }
        }).toObservable().test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void collectionSupplierReturnsNull() {
        just(1).toList(new Callable<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() throws Exception {
                return null;
            }
        }).toObservable().test().assertFailure(NullPointerException.class).assertErrorMessage("The collectionSupplier returned a null collection. Null values are generally not allowed in 2.x operators and sources.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void singleCollectionSupplierThrows() {
        just(1).toList(new Callable<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void singleCollectionSupplierReturnsNull() {
        just(1).toList(new Callable<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() throws Exception {
                return null;
            }
        }).test().assertFailure(NullPointerException.class).assertErrorMessage("The collectionSupplier returned a null collection. Null values are generally not allowed in 2.x operators and sources.");
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, Observable<List<Object>>>() {
            @Override
            public Observable<List<Object>> apply(Observable<Object> f) throws Exception {
                return f.toList().toObservable();
            }
        });
        TestHelper.checkDoubleOnSubscribeObservableToSingle(new io.reactivex.functions.Function<Observable<Object>, Single<List<Object>>>() {
            @Override
            public Single<List<Object>> apply(Observable<Object> f) throws Exception {
                return f.toList();
            }
        });
    }
}

