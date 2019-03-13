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


import io.reactivex.exceptions.TestException;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class ObservableDeferTest {
    @Test
    public void testDefer() throws Throwable {
        Callable<Observable<String>> factory = Mockito.mock(Callable.class);
        Observable<String> firstObservable = Observable.just("one", "two");
        Observable<String> secondObservable = Observable.just("three", "four");
        Mockito.when(factory.call()).thenReturn(firstObservable, secondObservable);
        Observable<String> deferred = Observable.defer(factory);
        Mockito.verifyZeroInteractions(factory);
        Observer<String> firstObserver = mockObserver();
        deferred.subscribe(firstObserver);
        Mockito.verify(factory, Mockito.times(1)).call();
        Mockito.verify(firstObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(firstObserver, Mockito.times(1)).onNext("two");
        Mockito.verify(firstObserver, Mockito.times(0)).onNext("three");
        Mockito.verify(firstObserver, Mockito.times(0)).onNext("four");
        Mockito.verify(firstObserver, Mockito.times(1)).onComplete();
        Observer<String> secondObserver = mockObserver();
        deferred.subscribe(secondObserver);
        Mockito.verify(factory, Mockito.times(2)).call();
        Mockito.verify(secondObserver, Mockito.times(0)).onNext("one");
        Mockito.verify(secondObserver, Mockito.times(0)).onNext("two");
        Mockito.verify(secondObserver, Mockito.times(1)).onNext("three");
        Mockito.verify(secondObserver, Mockito.times(1)).onNext("four");
        Mockito.verify(secondObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testDeferFunctionThrows() throws Exception {
        Callable<Observable<String>> factory = Mockito.mock(Callable.class);
        Mockito.when(factory.call()).thenThrow(new TestException());
        Observable<String> result = Observable.defer(factory);
        Observer<String> o = mockObserver();
        result.subscribe(o);
        Mockito.verify(o).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(o, Mockito.never()).onNext(ArgumentMatchers.any(String.class));
        Mockito.verify(o, Mockito.never()).onComplete();
    }
}

