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


import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.ObserverFusion;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.UnicastSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import org.junit.Assert;
import org.junit.Test;

import static QueueFuseable.ASYNC;
import static QueueFuseable.BOUNDARY;
import static QueueFuseable.SYNC;


public class ObservableDoFinallyTest implements Action {
    int calls;

    @Test
    public void normalJust() {
        just(1).doFinally(this).test().assertResult(1);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void normalEmpty() {
        empty().doFinally(this).test().assertResult();
        Assert.assertEquals(1, calls);
    }

    @Test
    public void normalError() {
        error(new TestException()).doFinally(this).test().assertFailure(TestException.class);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void normalTake() {
        range(1, 10).doFinally(this).take(5).test().assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Observable<Object> f) throws Exception {
                return f.doFinally(ObservableDoFinallyTest.this);
            }
        });
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Observable<Object> f) throws Exception {
                return f.doFinally(ObservableDoFinallyTest.this).filter(Functions.alwaysTrue());
            }
        });
    }

    @Test
    public void syncFused() {
        TestObserver<Integer> to = ObserverFusion.newTest(SYNC);
        range(1, 5).doFinally(this).subscribe(to);
        ObserverFusion.assertFusion(to, SYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void syncFusedBoundary() {
        TestObserver<Integer> to = ObserverFusion.newTest(((SYNC) | (BOUNDARY)));
        range(1, 5).doFinally(this).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void asyncFused() {
        TestObserver<Integer> to = ObserverFusion.newTest(ASYNC);
        UnicastSubject<Integer> up = UnicastSubject.create();
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        up.doFinally(this).subscribe(to);
        ObserverFusion.assertFusion(to, ASYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void asyncFusedBoundary() {
        TestObserver<Integer> to = ObserverFusion.newTest(((ASYNC) | (BOUNDARY)));
        UnicastSubject<Integer> up = UnicastSubject.create();
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        up.doFinally(this).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void normalJustConditional() {
        just(1).doFinally(this).filter(Functions.alwaysTrue()).test().assertResult(1);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void normalEmptyConditional() {
        empty().doFinally(this).filter(Functions.alwaysTrue()).test().assertResult();
        Assert.assertEquals(1, calls);
    }

    @Test
    public void normalErrorConditional() {
        error(new TestException()).doFinally(this).filter(Functions.alwaysTrue()).test().assertFailure(TestException.class);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void normalTakeConditional() {
        range(1, 10).doFinally(this).filter(Functions.alwaysTrue()).take(5).test().assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void syncFusedConditional() {
        TestObserver<Integer> to = ObserverFusion.newTest(SYNC);
        range(1, 5).doFinally(this).filter(Functions.alwaysTrue()).subscribe(to);
        ObserverFusion.assertFusion(to, SYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void nonFused() {
        TestObserver<Integer> to = ObserverFusion.newTest(SYNC);
        range(1, 5).hide().doFinally(this).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void nonFusedConditional() {
        TestObserver<Integer> to = ObserverFusion.newTest(SYNC);
        range(1, 5).hide().doFinally(this).filter(Functions.alwaysTrue()).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void syncFusedBoundaryConditional() {
        TestObserver<Integer> to = ObserverFusion.newTest(((SYNC) | (BOUNDARY)));
        range(1, 5).doFinally(this).filter(Functions.alwaysTrue()).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void asyncFusedConditional() {
        TestObserver<Integer> to = ObserverFusion.newTest(ASYNC);
        UnicastSubject<Integer> up = UnicastSubject.create();
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        up.doFinally(this).filter(Functions.alwaysTrue()).subscribe(to);
        ObserverFusion.assertFusion(to, ASYNC).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test
    public void asyncFusedBoundaryConditional() {
        TestObserver<Integer> to = ObserverFusion.newTest(((ASYNC) | (BOUNDARY)));
        UnicastSubject<Integer> up = UnicastSubject.create();
        TestHelper.emit(up, 1, 2, 3, 4, 5);
        up.doFinally(this).filter(Functions.alwaysTrue()).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3, 4, 5);
        Assert.assertEquals(1, calls);
    }

    @Test(expected = NullPointerException.class)
    public void nullAction() {
        just(1).doFinally(null);
    }

    @Test
    public void actionThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            just(1).doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    throw new TestException();
                }
            }).test().assertResult(1).cancel();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void actionThrowsConditional() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            just(1).doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    throw new TestException();
                }
            }).filter(Functions.alwaysTrue()).test().assertResult(1).cancel();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void clearIsEmpty() {
        range(1, 5).doFinally(this).subscribe(new java.util.Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                @SuppressWarnings("unchecked")
                QueueDisposable<Integer> qd = ((QueueDisposable<Integer>) (d));
                qd.requestFusion(QueueFuseable.ANY);
                assertFalse(qd.isEmpty());
                try {
                    assertEquals(1, qd.poll().intValue());
                } catch ( ex) {
                    throw new <ex>RuntimeException();
                }
                assertFalse(qd.isEmpty());
                qd.clear();
                assertTrue(qd.isEmpty());
                qd.dispose();
            }

            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertEquals(1, calls);
    }

    @Test
    public void clearIsEmptyConditional() {
        range(1, 5).doFinally(this).filter(Functions.alwaysTrue()).subscribe(new java.util.Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                @SuppressWarnings("unchecked")
                QueueDisposable<Integer> qd = ((QueueDisposable<Integer>) (d));
                qd.requestFusion(QueueFuseable.ANY);
                assertFalse(qd.isEmpty());
                assertFalse(qd.isDisposed());
                try {
                    assertEquals(1, qd.poll().intValue());
                } catch ( ex) {
                    throw new <ex>RuntimeException();
                }
                assertFalse(qd.isEmpty());
                qd.clear();
                assertTrue(qd.isEmpty());
                qd.dispose();
                assertTrue(qd.isDisposed());
            }

            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        Assert.assertEquals(1, calls);
    }

    @Test
    public void eventOrdering() {
        final List<String> list = new ArrayList<String>();
        error(new TestException()).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                list.add("dispose");
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                list.add("finally");
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object v) throws Exception {
                list.add("onNext");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                list.add("onError");
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                list.add("onComplete");
            }
        });
        Assert.assertEquals(Arrays.asList("onError", "finally"), list);
    }

    @Test
    public void eventOrdering2() {
        final List<String> list = new ArrayList<String>();
        just(1).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                list.add("dispose");
            }
        }).doFinally(new Action() {
            @Override
            public void run() throws Exception {
                list.add("finally");
            }
        }).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object v) throws Exception {
                list.add("onNext");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                list.add("onError");
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                list.add("onComplete");
            }
        });
        Assert.assertEquals(Arrays.asList("onNext", "onComplete", "finally"), list);
    }
}

