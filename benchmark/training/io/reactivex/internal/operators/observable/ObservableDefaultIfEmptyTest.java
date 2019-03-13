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


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableDefaultIfEmptyTest {
    @Test
    public void testDefaultIfEmpty() {
        Observable<Integer> source = Observable.just(1, 2, 3);
        Observable<Integer> observable = source.defaultIfEmpty(10);
        Observer<Integer> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext(10);
        Mockito.verify(observer).onNext(1);
        Mockito.verify(observer).onNext(2);
        Mockito.verify(observer).onNext(3);
        Mockito.verify(observer).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testDefaultIfEmptyWithEmpty() {
        Observable<Integer> source = Observable.empty();
        Observable<Integer> observable = source.defaultIfEmpty(10);
        Observer<Integer> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer).onNext(10);
        Mockito.verify(observer).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }
}

