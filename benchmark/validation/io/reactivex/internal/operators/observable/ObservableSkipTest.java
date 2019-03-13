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
import io.reactivex.observers.TestObserver;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableSkipTest {
    @Test
    public void testSkipNegativeElements() {
        Observable<String> skip = Observable.just("one", "two", "three").skip((-99));
        Observer<String> observer = mockObserver();
        skip.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipZeroElements() {
        Observable<String> skip = Observable.just("one", "two", "three").skip(0);
        Observer<String> observer = mockObserver();
        skip.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipOneElement() {
        Observable<String> skip = Observable.just("one", "two", "three").skip(1);
        Observer<String> observer = mockObserver();
        skip.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipTwoElements() {
        Observable<String> skip = Observable.just("one", "two", "three").skip(2);
        Observer<String> observer = mockObserver();
        skip.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext("one");
        Mockito.verify(observer, Mockito.never()).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipEmptyStream() {
        Observable<String> w = Observable.empty();
        Observable<String> skip = w.skip(1);
        Observer<String> observer = mockObserver();
        skip.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipMultipleObservers() {
        Observable<String> skip = Observable.just("one", "two", "three").skip(2);
        Observer<String> observer1 = mockObserver();
        skip.subscribe(observer1);
        Observer<String> observer2 = mockObserver();
        skip.subscribe(observer2);
        Mockito.verify(observer1, Mockito.times(1)).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(observer1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer1, Mockito.times(1)).onComplete();
        Mockito.verify(observer2, Mockito.times(1)).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(observer2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer2, Mockito.times(1)).onComplete();
    }

    @Test
    public void testSkipError() {
        Exception e = new Exception();
        Observable<String> ok = Observable.just("one");
        Observable<String> error = Observable.error(e);
        Observable<String> skip = Observable.concat(ok, error).skip(100);
        Observer<String> observer = mockObserver();
        skip.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(observer, Mockito.times(1)).onError(e);
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void testRequestOverflowDoesNotOccur() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.range(1, 10).skip(5).subscribe(to);
        to.assertTerminated();
        to.assertComplete();
        to.assertNoErrors();
        Assert.assertEquals(Arrays.asList(6, 7, 8, 9, 10), to.values());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.just(1).skip(2));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, Observable<Object>>() {
            @Override
            public io.reactivex.Observable<Object> apply(Observable<Object> o) throws Exception {
                return o.skip(1);
            }
        });
    }
}

