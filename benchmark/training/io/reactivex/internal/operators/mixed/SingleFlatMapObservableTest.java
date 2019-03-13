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
package io.reactivex.internal.operators.mixed;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;


public class SingleFlatMapObservableTest {
    @Test
    public void cancelMain() {
        SingleSubject<Integer> ss = SingleSubject.create();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ss.flatMapObservable(Functions.justFunction(ps)).test();
        Assert.assertTrue(ss.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        to.cancel();
        Assert.assertFalse(ss.hasObservers());
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void cancelOther() {
        SingleSubject<Integer> ss = SingleSubject.create();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ss.flatMapObservable(Functions.justFunction(ps)).test();
        Assert.assertTrue(ss.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        ss.onSuccess(1);
        Assert.assertFalse(ss.hasObservers());
        Assert.assertTrue(ps.hasObservers());
        to.cancel();
        Assert.assertFalse(ss.hasObservers());
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void errorMain() {
        SingleSubject<Integer> ss = SingleSubject.create();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ss.flatMapObservable(Functions.justFunction(ps)).test();
        Assert.assertTrue(ss.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        ss.onError(new TestException());
        Assert.assertFalse(ss.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void errorOther() {
        SingleSubject<Integer> ss = SingleSubject.create();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ss.flatMapObservable(Functions.justFunction(ps)).test();
        Assert.assertTrue(ss.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        ss.onSuccess(1);
        Assert.assertFalse(ss.hasObservers());
        Assert.assertTrue(ps.hasObservers());
        ps.onError(new TestException());
        Assert.assertFalse(ss.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void mapperCrash() {
        Single.just(1).flatMapObservable(new io.reactivex.functions.Function<Integer, ObservableSource<? extends Object>>() {
            @Override
            public io.reactivex.ObservableSource<? extends Object> apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void isDisposed() {
        TestHelper.checkDisposed(Single.never().flatMapObservable(Functions.justFunction(Observable.never())));
    }
}

