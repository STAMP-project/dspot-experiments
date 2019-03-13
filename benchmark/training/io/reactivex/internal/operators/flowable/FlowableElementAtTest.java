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
package io.reactivex.internal.operators.flowable;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class FlowableElementAtTest {
    @Test
    public void testElementAtFlowable() {
        Assert.assertEquals(2, Flowable.fromArray(1, 2).elementAt(1).toFlowable().blockingSingle().intValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testElementAtWithMinusIndexFlowable() {
        Flowable.fromArray(1, 2).elementAt((-1));
    }

    @Test
    public void testElementAtWithIndexOutOfBoundsFlowable() {
        Assert.assertEquals((-100), Flowable.fromArray(1, 2).elementAt(2).toFlowable().blockingFirst((-100)).intValue());
    }

    @Test
    public void testElementAtOrDefaultFlowable() {
        Assert.assertEquals(2, elementAt(1, 0).toFlowable().blockingSingle().intValue());
    }

    @Test
    public void testElementAtOrDefaultWithIndexOutOfBoundsFlowable() {
        Assert.assertEquals(0, elementAt(2, 0).toFlowable().blockingSingle().intValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testElementAtOrDefaultWithMinusIndexFlowable() {
        elementAt((-1), 0);
    }

    @Test
    public void testElementAt() {
        Assert.assertEquals(2, Flowable.fromArray(1, 2).elementAt(1).blockingGet().intValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testElementAtWithMinusIndex() {
        Flowable.fromArray(1, 2).elementAt((-1));
    }

    @Test
    public void testElementAtWithIndexOutOfBounds() {
        Assert.assertNull(Flowable.fromArray(1, 2).elementAt(2).blockingGet());
    }

    @Test
    public void testElementAtOrDefault() {
        Assert.assertEquals(2, elementAt(1, 0).blockingGet().intValue());
    }

    @Test
    public void testElementAtOrDefaultWithIndexOutOfBounds() {
        Assert.assertEquals(0, elementAt(2, 0).blockingGet().intValue());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testElementAtOrDefaultWithMinusIndex() {
        elementAt((-1), 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void elementAtOrErrorNegativeIndex() {
        Flowable.empty().elementAtOrError((-1));
    }

    @Test
    public void elementAtOrErrorNoElement() {
        Flowable.empty().elementAtOrError(0).test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void elementAtOrErrorOneElement() {
        Flowable.just(1).elementAtOrError(0).test().assertNoErrors().assertValue(1);
    }

    @Test
    public void elementAtOrErrorMultipleElements() {
        Flowable.just(1, 2, 3).elementAtOrError(1).test().assertNoErrors().assertValue(2);
    }

    @Test
    public void elementAtOrErrorInvalidIndex() {
        Flowable.just(1, 2, 3).elementAtOrError(3).test().assertNoValues().assertError(NoSuchElementException.class);
    }

    @Test
    public void elementAtOrErrorError() {
        Flowable.error(new RuntimeException("error")).elementAtOrError(0).test().assertNoValues().assertErrorMessage("error").assertError(RuntimeException.class);
    }

    @Test
    public void elementAtIndex0OnEmptySource() {
        Flowable.empty().elementAt(0).test().assertResult();
    }

    @Test
    public void elementAtIndex0WithDefaultOnEmptySource() {
        elementAt(0, 5).test().assertResult(5);
    }

    @Test
    public void elementAtIndex1OnEmptySource() {
        Flowable.empty().elementAt(1).test().assertResult();
    }

    @Test
    public void elementAtIndex1WithDefaultOnEmptySource() {
        elementAt(1, 10).test().assertResult(10);
    }

    @Test
    public void elementAtOrErrorIndex1OnEmptySource() {
        Flowable.empty().elementAtOrError(1).test().assertFailure(NoSuchElementException.class);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Publisher<Object>>() {
            @Override
            public io.reactivex.Publisher<Object> apply(Flowable<Object> f) throws Exception {
                return f.elementAt(0).toFlowable();
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowableToMaybe(new io.reactivex.functions.Function<Flowable<Object>, Maybe<Object>>() {
            @Override
            public io.reactivex.Maybe<Object> apply(Flowable<Object> f) throws Exception {
                return f.elementAt(0);
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowableToSingle(new io.reactivex.functions.Function<Flowable<Object>, Single<Object>>() {
            @Override
            public io.reactivex.Single<Object> apply(Flowable<Object> f) throws Exception {
                return f.elementAt(0, 1);
            }
        });
    }

    @Test
    public void elementAtIndex1WithDefaultOnEmptySourceObservable() {
        elementAt(1, 10).toFlowable().test().assertResult(10);
    }

    @Test
    public void errorFlowable() {
        elementAt(1, 10).toFlowable().test().assertFailure(TestException.class);
    }

    @Test
    public void error() {
        elementAt(1, 10).test().assertFailure(TestException.class);
        Flowable.error(new TestException()).elementAt(1).test().assertFailure(TestException.class);
    }

    @Test
    public void badSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                    subscriber.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    subscriber.onNext(1);
                    subscriber.onNext(2);
                    subscriber.onError(new TestException());
                    subscriber.onComplete();
                }
            }.elementAt(0).toFlowable().test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
        TestHelper.checkBadSourceFlowable(new io.reactivex.functions.Function<Flowable<Integer>, Object>() {
            @Override
            public Object apply(Flowable<Integer> f) throws Exception {
                return f.elementAt(0);
            }
        }, false, null, 1);
        TestHelper.checkBadSourceFlowable(new io.reactivex.functions.Function<Flowable<Integer>, Object>() {
            @Override
            public Object apply(Flowable<Integer> f) throws Exception {
                return f.elementAt(0, 1);
            }
        }, false, null, 1, 1);
        TestHelper.checkBadSourceFlowable(new io.reactivex.functions.Function<Flowable<Integer>, Object>() {
            @Override
            public Object apply(Flowable<Integer> f) throws Exception {
                return f.elementAt(0).toFlowable();
            }
        }, false, null, 1);
        TestHelper.checkBadSourceFlowable(new io.reactivex.functions.Function<Flowable<Integer>, Object>() {
            @Override
            public Object apply(Flowable<Integer> f) throws Exception {
                return f.elementAt(0, 1).toFlowable();
            }
        }, false, null, 1, 1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().elementAt(0).toFlowable());
        TestHelper.checkDisposed(elementAt(0, 1).toFlowable());
        TestHelper.checkDisposed(PublishProcessor.create().elementAt(0));
        TestHelper.checkDisposed(elementAt(0, 1));
    }

    @Test
    public void badSourceObservable() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new java.util.Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? extends Integer> observer) {
                    observer.onSubscribe(io.reactivex.disposables.Disposables.empty());
                    observer.onNext(1);
                    observer.onNext(2);
                    observer.onError(new TestException());
                    observer.onComplete();
                }
            }.elementAt(0).toFlowable().test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badSource2() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            elementAt(0, 1).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

