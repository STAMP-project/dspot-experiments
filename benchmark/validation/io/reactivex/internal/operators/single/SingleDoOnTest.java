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
package io.reactivex.internal.operators.single;


import Functions.EMPTY_ACTION;
import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SingleDoOnTest {
    @Test
    public void doOnDispose() {
        final int[] count = new int[]{ 0 };
        Single.never().doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                (count[0])++;
            }
        }).test(true);
        Assert.assertEquals(1, count[0]);
    }

    @Test
    public void doOnError() {
        final Object[] event = new Object[]{ null };
        Single.error(new TestException()).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                event[0] = e;
            }
        }).test();
        Assert.assertTrue(event[0].toString(), ((event[0]) instanceof TestException));
    }

    @Test
    public void doOnSubscribe() {
        final int[] count = new int[]{ 0 };
        Single.never().doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                (count[0])++;
            }
        }).test();
        Assert.assertEquals(1, count[0]);
    }

    @Test
    public void doOnSuccess() {
        final Object[] event = new Object[]{ null };
        Single.just(1).doOnSuccess(new Consumer<Integer>() {
            @Override
            public void accept(Integer e) throws Exception {
                event[0] = e;
            }
        }).test();
        Assert.assertEquals(1, event[0]);
    }

    @Test
    public void doOnSubscribeNormal() {
        final int[] count = new int[]{ 0 };
        Single.just(1).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                (count[0])++;
            }
        }).test().assertResult(1);
        Assert.assertEquals(1, count[0]);
    }

    @Test
    public void doOnSubscribeError() {
        final int[] count = new int[]{ 0 };
        Single.error(new TestException()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                (count[0])++;
            }
        }).test().assertFailure(TestException.class);
        Assert.assertEquals(1, count[0]);
    }

    @Test
    public void doOnSubscribeJustCrash() {
        Single.just(1).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void doOnSubscribeErrorCrash() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Single.error(new TestException("Outer")).doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable d) throws Exception {
                    throw new TestException("Inner");
                }
            }).test().assertFailureAndMessage(TestException.class, "Inner");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Outer");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorSuccess() {
        final int[] call = new int[]{ 0 };
        Single.just(1).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable v) throws Exception {
                (call[0])++;
            }
        }).test().assertResult(1);
        Assert.assertEquals(0, call[0]);
    }

    @Test
    public void onErrorCrashes() {
        TestObserver<Object> to = Single.error(new TestException("Outer")).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable v) throws Exception {
                throw new TestException("Inner");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Outer");
        TestHelper.assertError(errors, 1, TestException.class, "Inner");
    }

    @Test
    public void doOnEventThrowsSuccess() {
        Single.just(1).doOnEvent(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer v, Throwable e) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void doOnEventThrowsError() {
        TestObserver<Integer> to = Single.<Integer>error(new TestException("Main")).doOnEvent(new BiConsumer<Integer, Throwable>() {
            @Override
            public void accept(Integer v, Throwable e) throws Exception {
                throw new TestException("Inner");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Main");
        TestHelper.assertError(errors, 1, TestException.class, "Inner");
    }

    @Test
    public void doOnDisposeDispose() {
        final int[] calls = new int[]{ 0 };
        TestHelper.checkDisposed(PublishSubject.create().singleOrError().doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                (calls[0])++;
            }
        }));
        Assert.assertEquals(1, calls[0]);
    }

    @Test
    public void doOnDisposeSuccess() {
        final int[] calls = new int[]{ 0 };
        Single.just(1).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                (calls[0])++;
            }
        }).test().assertResult(1);
        Assert.assertEquals(0, calls[0]);
    }

    @Test
    public void doOnDisposeError() {
        final int[] calls = new int[]{ 0 };
        Single.error(new TestException()).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                (calls[0])++;
            }
        }).test().assertFailure(TestException.class);
        Assert.assertEquals(0, calls[0]);
    }

    @Test
    public void doOnDisposeDoubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeSingle(new Function<Single<Object>, SingleSource<Object>>() {
            @Override
            public io.reactivex.SingleSource<Object> apply(Single<Object> s) throws Exception {
                return s.doOnDispose(EMPTY_ACTION);
            }
        });
    }

    @Test
    public void doOnDisposeCrash() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            PublishSubject<Integer> ps = PublishSubject.create();
            ps.singleOrError().doOnDispose(new Action() {
                @Override
                public void run() throws Exception {
                    throw new TestException();
                }
            }).test().cancel();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void doOnSuccessErrors() {
        final int[] call = new int[]{ 0 };
        Single.error(new TestException()).doOnSuccess(new Consumer<Object>() {
            @Override
            public void accept(Object v) throws Exception {
                (call[0])++;
            }
        }).test().assertFailure(TestException.class);
        Assert.assertEquals(0, call[0]);
    }

    @Test
    public void doOnSuccessCrash() {
        Single.just(1).doOnSuccess(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void onSubscribeCrash() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Disposable bs = Disposables.empty();
            new Single<Integer>() {
                @Override
                protected void subscribeActual(SingleObserver<? super Integer> observer) {
                    observer.onSubscribe(bs);
                    observer.onError(new TestException("Second"));
                    observer.onSuccess(1);
                }
            }.doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable d) throws Exception {
                    throw new TestException("First");
                }
            }).test().assertFailureAndMessage(TestException.class, "First");
            Assert.assertTrue(bs.isDisposed());
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

