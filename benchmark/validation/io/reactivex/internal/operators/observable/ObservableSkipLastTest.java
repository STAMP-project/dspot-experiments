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
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableSkipLastTest {
    @Test
    public void testSkipLastEmpty() {
        Observable<String> o = Observable.<String>empty().skipLast(2);
        Observer<String> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipLast1() {
        Observable<String> o = Observable.fromIterable(Arrays.asList("one", "two", "three")).skipLast(2);
        Observer<String> observer = mockObserver();
        InOrder inOrder = Mockito.inOrder(observer);
        o.subscribe(observer);
        inOrder.verify(observer, Mockito.never()).onNext("two");
        inOrder.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipLast2() {
        Observable<String> o = Observable.fromIterable(Arrays.asList("one", "two")).skipLast(2);
        Observer<String> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipLastWithZeroCount() {
        Observable<String> w = Observable.just("one", "two");
        Observable<String> observable = w.skipLast(0);
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipLastWithBackpressure() {
        Observable<Integer> o = Observable.range(0, ((Flowable.bufferSize()) * 2)).skipLast(((Flowable.bufferSize()) + 10));
        TestObserver<Integer> to = new TestObserver<Integer>();
        o.observeOn(Schedulers.computation()).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        Assert.assertEquals(((Flowable.bufferSize()) - 10), to.valueCount());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSkipLastWithNegativeCount() {
        Observable.just("one").skipLast((-1));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.just(1).skipLast(1));
    }

    @Test
    public void error() {
        Observable.error(new TestException()).skipLast(1).test().assertFailure(TestException.class);
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, Observable<Object>>() {
            @Override
            public io.reactivex.Observable<Object> apply(Observable<Object> o) throws Exception {
                return o.skipLast(1);
            }
        });
    }
}

