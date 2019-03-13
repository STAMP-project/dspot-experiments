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
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;


public class ObservableSequenceEqualTest {
    @Test
    public void test1Observable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three"), Observable.just("one", "two", "three")).toObservable();
        verifyResult(o, true);
    }

    @Test
    public void test2Observable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three"), Observable.just("one", "two", "three", "four")).toObservable();
        verifyResult(o, false);
    }

    @Test
    public void test3Observable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three", "four"), Observable.just("one", "two", "three")).toObservable();
        verifyResult(o, false);
    }

    @Test
    public void testWithError1Observable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.concat(Observable.just("one"), Observable.<String>error(new TestException())), Observable.just("one", "two", "three")).toObservable();
        verifyError(o);
    }

    @Test
    public void testWithError2Observable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three"), Observable.concat(Observable.just("one"), Observable.<String>error(new TestException()))).toObservable();
        verifyError(o);
    }

    @Test
    public void testWithError3Observable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.concat(Observable.just("one"), Observable.<String>error(new TestException())), Observable.concat(Observable.just("one"), Observable.<String>error(new TestException()))).toObservable();
        verifyError(o);
    }

    @Test
    public void testWithEmpty1Observable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.<String>empty(), Observable.just("one", "two", "three")).toObservable();
        verifyResult(o, false);
    }

    @Test
    public void testWithEmpty2Observable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three"), Observable.<String>empty()).toObservable();
        verifyResult(o, false);
    }

    @Test
    public void testWithEmpty3Observable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.<String>empty(), Observable.<String>empty()).toObservable();
        verifyResult(o, true);
    }

    @Test
    public void testWithEqualityErrorObservable() {
        Observable<Boolean> o = Observable.sequenceEqual(Observable.just("one"), Observable.just("one"), new io.reactivex.functions.BiPredicate<String, String>() {
            @Override
            public boolean test(String t1, String t2) {
                throw new TestException();
            }
        }).toObservable();
        verifyError(o);
    }

    @Test
    public void prefetchObservable() {
        Observable.sequenceEqual(Observable.range(1, 20), Observable.range(1, 20), 2).toObservable().test().assertResult(true);
    }

    @Test
    public void disposedObservable() {
        TestHelper.checkDisposed(Observable.sequenceEqual(Observable.just(1), Observable.just(2)).toObservable());
    }

    @Test
    public void test1() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three"), Observable.just("one", "two", "three"));
        verifyResult(o, true);
    }

    @Test
    public void test2() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three"), Observable.just("one", "two", "three", "four"));
        verifyResult(o, false);
    }

    @Test
    public void test3() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three", "four"), Observable.just("one", "two", "three"));
        verifyResult(o, false);
    }

    @Test
    public void testWithError1() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.concat(Observable.just("one"), Observable.<String>error(new TestException())), Observable.just("one", "two", "three"));
        verifyError(o);
    }

    @Test
    public void testWithError2() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three"), Observable.concat(Observable.just("one"), Observable.<String>error(new TestException())));
        verifyError(o);
    }

    @Test
    public void testWithError3() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.concat(Observable.just("one"), Observable.<String>error(new TestException())), Observable.concat(Observable.just("one"), Observable.<String>error(new TestException())));
        verifyError(o);
    }

    @Test
    public void testWithEmpty1() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.<String>empty(), Observable.just("one", "two", "three"));
        verifyResult(o, false);
    }

    @Test
    public void testWithEmpty2() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.just("one", "two", "three"), Observable.<String>empty());
        verifyResult(o, false);
    }

    @Test
    public void testWithEmpty3() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.<String>empty(), Observable.<String>empty());
        verifyResult(o, true);
    }

    @Test
    public void testWithEqualityError() {
        Single<Boolean> o = Observable.sequenceEqual(Observable.just("one"), Observable.just("one"), new io.reactivex.functions.BiPredicate<String, String>() {
            @Override
            public boolean test(String t1, String t2) {
                throw new TestException();
            }
        });
        verifyError(o);
    }

    @Test
    public void prefetch() {
        Observable.sequenceEqual(Observable.range(1, 20), Observable.range(1, 20), 2).test().assertResult(true);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(Observable.sequenceEqual(Observable.just(1), Observable.just(2)));
    }

    @Test
    public void simpleInequal() {
        Observable.sequenceEqual(Observable.just(1), Observable.just(2)).test().assertResult(false);
    }

    @Test
    public void simpleInequalObservable() {
        Observable.sequenceEqual(Observable.just(1), Observable.just(2)).toObservable().test().assertResult(false);
    }

    @Test
    public void onNextCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final TestObserver<Boolean> to = Observable.sequenceEqual(Observable.never(), ps).test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ps.onNext(1);
                }
            };
            TestHelper.race(r1, r2);
            to.assertEmpty();
        }
    }

    @Test
    public void onNextCancelRaceObservable() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final TestObserver<Boolean> to = Observable.sequenceEqual(Observable.never(), ps).toObservable().test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ps.onNext(1);
                }
            };
            TestHelper.race(r1, r2);
            to.assertEmpty();
        }
    }
}

