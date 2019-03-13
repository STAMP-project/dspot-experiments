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


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.ObserverFusion;
import io.reactivex.subjects.UnicastSubject;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static QueueFuseable.ANY;
import static QueueFuseable.BOUNDARY;


public class ObservableFilterTest {
    @Test
    public void testFilter() {
        Observable<String> w = Observable.just("one", "two", "three");
        Observable<String> observable = w.filter(new Predicate<String>() {
            @Override
            public boolean test(String t1) {
                return t1.equals("two");
            }
        });
        Observer<String> observer = mockObserver();
        observable.subscribe(observer);
        Mockito.verify(observer, Mockito.never()).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.never()).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    // FIXME subscribers are not allowed to throw
    // @Test
    // public void testFatalError() {
    // try {
    // Observable.just(1)
    // .filter(new Predicate<Integer>() {
    // @Override
    // public boolean test(Integer t) {
    // return true;
    // }
    // })
    // .first()
    // .subscribe(new Consumer<Integer>() {
    // @Override
    // public void accept(Integer t) {
    // throw new TestException();
    // }
    // });
    // Assert.fail("No exception was thrown");
    // } catch (OnErrorNotImplementedException ex) {
    // if (!(ex.getCause() instanceof TestException)) {
    // Assert.fail("Failed to report the original exception, instead: " + ex.getCause());
    // }
    // }
    // }
    @Test
    public void dispose() {
        TestHelper.checkDisposed(Observable.range(1, 5).filter(Functions.alwaysTrue()));
    }

    @Test
    public void doubleOnSubscribe() {
        checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.filter(Functions.alwaysTrue());
            }
        });
    }

    @Test
    public void fusedSync() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        Observable.range(1, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return (v % 2) == 0;
            }
        }).subscribe(to);
        ObserverFusion.assertFusion(to, SYNC).assertResult(2, 4);
    }

    @Test
    public void fusedAsync() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        UnicastSubject<Integer> us = UnicastSubject.create();
        us.filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return (v % 2) == 0;
            }
        }).subscribe(to);
        TestHelper.emit(us, 1, 2, 3, 4, 5);
        ObserverFusion.assertFusion(to, ASYNC).assertResult(2, 4);
    }

    @Test
    public void fusedReject() {
        TestObserver<Integer> to = ObserverFusion.newTest(((ANY) | (BOUNDARY)));
        Observable.range(1, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return (v % 2) == 0;
            }
        }).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(2, 4);
    }

    @Test
    public void filterThrows() {
        Observable.range(1, 5).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }
}

