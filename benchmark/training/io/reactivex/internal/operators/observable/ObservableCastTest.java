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


public class ObservableCastTest {
    @Test
    public void testCast() {
        Observable<?> source = Observable.just(1, 2);
        Observable<Integer> observable = source.cast(Integer.class);
        Observer<Integer> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onNext(1);
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testCastWithWrongType() {
        Observable<?> source = Observable.just(1, 2);
        Observable<Boolean> observable = source.cast(Boolean.class);
        Observer<Boolean> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(ClassCastException.class));
    }
}

