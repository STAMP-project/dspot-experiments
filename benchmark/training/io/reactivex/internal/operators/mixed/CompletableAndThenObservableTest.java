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
import io.reactivex.observers.TestObserver;
import org.junit.Assert;
import org.junit.Test;


public class CompletableAndThenObservableTest {
    @Test
    public void cancelMain() {
        CompletableSubject cs = CompletableSubject.create();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = cs.andThen(ps).test();
        Assert.assertTrue(cs.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        to.cancel();
        Assert.assertFalse(cs.hasObservers());
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void cancelOther() {
        CompletableSubject cs = CompletableSubject.create();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = cs.andThen(ps).test();
        Assert.assertTrue(cs.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        cs.onComplete();
        Assert.assertFalse(cs.hasObservers());
        Assert.assertTrue(ps.hasObservers());
        to.cancel();
        Assert.assertFalse(cs.hasObservers());
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void errorMain() {
        CompletableSubject cs = CompletableSubject.create();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = cs.andThen(ps).test();
        Assert.assertTrue(cs.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        cs.onError(new TestException());
        Assert.assertFalse(cs.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void errorOther() {
        CompletableSubject cs = CompletableSubject.create();
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = cs.andThen(ps).test();
        Assert.assertTrue(cs.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        cs.onComplete();
        Assert.assertFalse(cs.hasObservers());
        Assert.assertTrue(ps.hasObservers());
        ps.onError(new TestException());
        Assert.assertFalse(cs.hasObservers());
        Assert.assertFalse(ps.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void isDisposed() {
        TestHelper.checkDisposed(Completable.never().andThen(Observable.never()));
    }
}

