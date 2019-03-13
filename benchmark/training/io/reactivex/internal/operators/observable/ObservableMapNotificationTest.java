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
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.operators.observable.ObservableMapNotification.MapNotificationObserver;
import io.reactivex.observers.TestObserver;
import java.util.concurrent.Callable;
import org.junit.Test;


public class ObservableMapNotificationTest {
    @Test
    public void testJust() {
        TestObserver<Object> to = new TestObserver<Object>();
        Observable.just(1).flatMap(new io.reactivex.functions.Function<Integer, Observable<Object>>() {
            @Override
            public io.reactivex.Observable<Object> apply(Integer item) {
                return Observable.just(((Object) (item + 1)));
            }
        }, new io.reactivex.functions.Function<Throwable, Observable<Object>>() {
            @Override
            public io.reactivex.Observable<Object> apply(Throwable e) {
                return Observable.error(e);
            }
        }, new Callable<Observable<Object>>() {
            @Override
            public io.reactivex.Observable<Object> call() {
                return Observable.never();
            }
        }).subscribe(to);
        to.assertNoErrors();
        to.assertNotComplete();
        to.assertValue(2);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(new Observable<Integer>() {
            @SuppressWarnings({ "rawtypes", "unchecked" })
            @Override
            protected void subscribeActual(Observer<? super Integer> observer) {
                MapNotificationObserver mn = new MapNotificationObserver(observer, Functions.justFunction(Observable.just(1)), Functions.justFunction(Observable.just(2)), Functions.justCallable(Observable.just(3)));
                mn.onSubscribe(Disposables.empty());
            }
        });
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Observable<Object> o) throws Exception {
                return o.flatMap(Functions.justFunction(Observable.just(1)), Functions.justFunction(Observable.just(2)), Functions.justCallable(Observable.just(3)));
            }
        });
    }

    @Test
    public void onErrorCrash() {
        TestObserver<Integer> to = Observable.<Integer>error(new TestException("Outer")).flatMap(Functions.justFunction(Observable.just(1)), new io.reactivex.functions.Function<Throwable, Observable<Integer>>() {
            @Override
            public io.reactivex.Observable<Integer> apply(Throwable t) throws Exception {
                throw new TestException("Inner");
            }
        }, Functions.justCallable(Observable.just(3))).test().assertFailure(CompositeException.class);
        TestHelper.assertError(to, 0, TestException.class, "Outer");
        TestHelper.assertError(to, 1, TestException.class, "Inner");
    }
}

