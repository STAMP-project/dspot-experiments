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
import org.junit.Test;


public class ObservableCountTest {
    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.just(1).count());
        TestHelper.checkDisposed(Observable.just(1).count().toObservable());
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Long>>() {
            @Override
            public io.reactivex.ObservableSource<Long> apply(Observable<Object> o) throws Exception {
                return o.count().toObservable();
            }
        });
        checkDoubleOnSubscribeObservableToSingle(new io.reactivex.functions.Function<Observable<Object>, SingleSource<Long>>() {
            @Override
            public io.reactivex.SingleSource<Long> apply(Observable<Object> o) throws Exception {
                return o.count();
            }
        });
    }
}

