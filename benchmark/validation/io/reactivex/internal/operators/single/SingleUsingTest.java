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


import io.reactivex.Consumer;
import io.reactivex.Function;
import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


public class SingleUsingTest {
    Function<Disposable, Single<Integer>> mapper = new Function<Disposable, Single<Integer>>() {
        @Override
        public io.reactivex.Single<Integer> apply(Disposable d) throws Exception {
            return Single.just(1);
        }
    };

    Function<Disposable, Single<Integer>> mapperThrows = new Function<Disposable, Single<Integer>>() {
        @Override
        public io.reactivex.Single<Integer> apply(Disposable d) throws Exception {
            throw new TestException("Mapper");
        }
    };

    Consumer<Disposable> disposer = new Consumer<Disposable>() {
        @Override
        public void accept(Disposable d) throws Exception {
            d.dispose();
        }
    };

    Consumer<Disposable> disposerThrows = new Consumer<Disposable>() {
        @Override
        public void accept(Disposable d) throws Exception {
            throw new TestException("Disposer");
        }
    };

    @Test
    public void resourceSupplierThrows() {
        Single.using(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                throw new TestException();
            }
        }, Functions.justFunction(Single.just(1)), Functions.emptyConsumer()).test().assertFailure(TestException.class);
    }

    @Test
    public void normalEager() {
        Single.using(Functions.justCallable(1), Functions.justFunction(Single.just(1)), Functions.emptyConsumer()).test().assertResult(1);
    }

    @Test
    public void normalNonEager() {
        Single.using(Functions.justCallable(1), Functions.justFunction(Single.just(1)), Functions.emptyConsumer(), false).test().assertResult(1);
    }

    @Test
    public void errorEager() {
        Single.using(Functions.justCallable(1), Functions.justFunction(Single.error(new TestException())), Functions.emptyConsumer()).test().assertFailure(TestException.class);
    }

    @Test
    public void errorNonEager() {
        Single.using(Functions.justCallable(1), Functions.justFunction(Single.error(new TestException())), Functions.emptyConsumer(), false).test().assertFailure(TestException.class);
    }

    @Test
    public void eagerMapperThrowsDisposerThrows() {
        TestObserver<Integer> to = Single.using(Functions.justCallable(Disposables.empty()), mapperThrows, disposerThrows).test().assertFailure(CompositeException.class);
        List<Throwable> ce = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(ce, 0, TestException.class, "Mapper");
        TestHelper.assertError(ce, 1, TestException.class, "Disposer");
    }

    @Test
    public void noneagerMapperThrowsDisposerThrows() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Single.using(Functions.justCallable(Disposables.empty()), mapperThrows, disposerThrows, false).test().assertFailureAndMessage(TestException.class, "Mapper");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Disposer");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void resourceDisposedIfMapperCrashes() {
        Disposable d = Disposables.empty();
        Single.using(Functions.justCallable(d), mapperThrows, disposer).test().assertFailure(TestException.class);
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void resourceDisposedIfMapperCrashesNonEager() {
        Disposable d = Disposables.empty();
        Single.using(Functions.justCallable(d), mapperThrows, disposer, false).test().assertFailure(TestException.class);
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void dispose() {
        Disposable d = Disposables.empty();
        Single.using(Functions.justCallable(d), mapper, disposer, false).test(true);
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void disposerThrowsEager() {
        Single.using(Functions.justCallable(Disposables.empty()), mapper, disposerThrows).test().assertFailure(TestException.class);
    }

    @Test
    public void disposerThrowsNonEager() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Single.using(Functions.justCallable(Disposables.empty()), mapper, disposerThrows, false).test().assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Disposer");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void errorAndDisposerThrowsEager() {
        TestObserver<Integer> to = Single.using(Functions.justCallable(Disposables.empty()), new Function<Disposable, SingleSource<Integer>>() {
            @Override
            public io.reactivex.SingleSource<Integer> apply(Disposable v) throws Exception {
                return Single.<Integer>error(new TestException("Mapper-run"));
            }
        }, disposerThrows).test().assertFailure(CompositeException.class);
        List<Throwable> ce = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(ce, 0, TestException.class, "Mapper-run");
        TestHelper.assertError(ce, 1, TestException.class, "Disposer");
    }

    @Test
    public void errorAndDisposerThrowsNonEager() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Single.using(Functions.justCallable(Disposables.empty()), new Function<Disposable, SingleSource<Integer>>() {
                @Override
                public io.reactivex.SingleSource<Integer> apply(Disposable v) throws Exception {
                    return Single.<Integer>error(new TestException("Mapper-run"));
                }
            }, disposerThrows, false).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Disposer");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void successDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            Disposable d = Disposables.empty();
            final TestObserver<Integer> to = Single.using(Functions.justCallable(d), new Function<Disposable, SingleSource<Integer>>() {
                @Override
                public io.reactivex.SingleSource<Integer> apply(Disposable v) throws Exception {
                    return pp.single((-99));
                }
            }, disposer).test();
            pp.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
            Assert.assertTrue(d.isDisposed());
        }
    }

    @Test
    public void doubleOnSubscribe() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Single.using(Functions.justCallable(1), new Function<Integer, SingleSource<Integer>>() {
                @Override
                public io.reactivex.SingleSource<Integer> apply(Integer v) throws Exception {
                    return new Single<Integer>() {
                        @Override
                        protected void subscribeActual(SingleObserver<? super Integer> observer) {
                            observer.onSubscribe(Disposables.empty());
                            Assert.assertFalse(isDisposed());
                            Disposable d = Disposables.empty();
                            observer.onSubscribe(d);
                            Assert.assertTrue(d.isDisposed());
                            Assert.assertFalse(isDisposed());
                            observer.onSuccess(1);
                            Assert.assertTrue(isDisposed());
                        }
                    };
                }
            }, Functions.emptyConsumer()).test().assertResult(1);
            TestHelper.assertError(errors, 0, IllegalStateException.class, "Disposable already set!");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void errorDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            Disposable d = Disposables.empty();
            final TestObserver<Integer> to = Single.using(Functions.justCallable(d), new Function<Disposable, SingleSource<Integer>>() {
                @Override
                public io.reactivex.SingleSource<Integer> apply(Disposable v) throws Exception {
                    return pp.single((-99));
                }
            }, disposer).test();
            final TestException ex = new TestException();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onError(ex);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
            Assert.assertTrue(d.isDisposed());
        }
    }
}

