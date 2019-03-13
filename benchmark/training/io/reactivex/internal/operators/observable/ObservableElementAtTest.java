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
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Observable;
import org.junit.Assert;
import org.junit.Test;


public class ObservableElementAtTest {
    @Test
    public void testElementAtObservable() {
        Assert.assertEquals(2, fromArray(1, 2).elementAt(1).toObservable().blockingSingle().intValue());
    }

    @Test
    public void testElementAtWithIndexOutOfBoundsObservable() {
        Assert.assertEquals((-99), fromArray(1, 2).elementAt(2).toObservable().blockingSingle((-99)).intValue());
    }

    @Test
    public void testElementAtOrDefaultObservable() {
        Assert.assertEquals(2, fromArray(1, 2).elementAt(1, 0).toObservable().blockingSingle().intValue());
    }

    @Test
    public void testElementAtOrDefaultWithIndexOutOfBoundsObservable() {
        Assert.assertEquals(0, fromArray(1, 2).elementAt(2, 0).toObservable().blockingSingle().intValue());
    }

    @Test
    public void testElementAt() {
        Assert.assertEquals(2, fromArray(1, 2).elementAt(1).blockingGet().intValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testElementAtWithMinusIndex() {
        fromArray(1, 2).elementAt((-1));
    }

    @Test
    public void testElementAtWithIndexOutOfBounds() {
        Assert.assertNull(fromArray(1, 2).elementAt(2).blockingGet());
    }

    @Test
    public void testElementAtOrDefault() {
        Assert.assertEquals(2, fromArray(1, 2).elementAt(1, 0).blockingGet().intValue());
    }

    @Test
    public void testElementAtOrDefaultWithIndexOutOfBounds() {
        Assert.assertEquals(0, fromArray(1, 2).elementAt(2, 0).blockingGet().intValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testElementAtOrDefaultWithMinusIndex() {
        fromArray(1, 2).elementAt((-1), 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void elementAtOrErrorNegativeIndex() {
        empty().elementAtOrError((-1));
    }

    @Test
    public void elementAtOrErrorNoElement() {
        empty().elementAtOrError(0).test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void elementAtOrErrorOneElement() {
        Observable.just(1).elementAtOrError(0).test().assertNoErrors().assertValue(1);
    }

    @Test
    public void elementAtOrErrorMultipleElements() {
        just(1, 2, 3).elementAtOrError(1).test().assertNoErrors().assertValue(2);
    }

    @Test
    public void elementAtOrErrorInvalidIndex() {
        just(1, 2, 3).elementAtOrError(3).test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void elementAtOrErrorError() {
        Observable.error(new RuntimeException("error")).elementAtOrError(0).test().assertNoValues().assertErrorMessage("error").assertError(RuntimeException.class);
    }

    @Test
    public void elementAtIndex0OnEmptySource() {
        empty().elementAt(0).test().assertResult();
    }

    @Test
    public void elementAtIndex0WithDefaultOnEmptySource() {
        empty().elementAt(0, 5).test().assertResult(5);
    }

    @Test
    public void elementAtIndex1OnEmptySource() {
        empty().elementAt(1).test().assertResult();
    }

    @Test
    public void elementAtIndex1WithDefaultOnEmptySource() {
        empty().elementAt(1, 10).test().assertResult(10);
    }

    @Test
    public void elementAtOrErrorIndex1OnEmptySource() {
        empty().elementAtOrError(1).test().assertFailure(NoSuchElementException.class);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.create().elementAt(0).toObservable());
        TestHelper.checkDisposed(PublishSubject.create().elementAt(0));
        TestHelper.checkDisposed(PublishSubject.create().elementAt(0, 1).toObservable());
        TestHelper.checkDisposed(PublishSubject.create().elementAt(0, 1));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new io.reactivex.functions.Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.elementAt(0).toObservable();
            }
        });
        TestHelper.checkDoubleOnSubscribeObservableToMaybe(new io.reactivex.functions.Function<Observable<Object>, MaybeSource<Object>>() {
            @Override
            public MaybeSource<Object> apply(Observable<Object> o) throws Exception {
                return o.elementAt(0);
            }
        });
        TestHelper.checkDoubleOnSubscribeObservableToSingle(new io.reactivex.functions.Function<Observable<Object>, SingleSource<Object>>() {
            @Override
            public SingleSource<Object> apply(Observable<Object> o) throws Exception {
                return o.elementAt(0, 1);
            }
        });
    }

    @Test
    public void elementAtIndex1WithDefaultOnEmptySourceObservable() {
        empty().elementAt(1, 10).toObservable().test().assertResult(10);
    }

    @Test
    public void errorObservable() {
        error(new TestException()).elementAt(1, 10).toObservable().test().assertFailure(TestException.class);
    }

    @Test
    public void badSourceObservable() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? extends Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.elementAt(0).toObservable().test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? extends Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.elementAt(0).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badSource2() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? extends Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.elementAt(0, 1).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

