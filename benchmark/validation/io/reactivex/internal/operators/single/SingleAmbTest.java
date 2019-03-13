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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


public class SingleAmbTest {
    @Test
    public void ambWithFirstFires() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        TestObserver<Integer> to = pp1.single((-99)).ambWith(pp2.single((-99))).test();
        Assert.assertTrue(pp1.hasSubscribers());
        Assert.assertTrue(pp2.hasSubscribers());
        pp1.onNext(1);
        pp1.onComplete();
        Assert.assertFalse(pp1.hasSubscribers());
        Assert.assertFalse(pp2.hasSubscribers());
        to.assertResult(1);
    }

    @Test
    public void ambWithSecondFires() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        TestObserver<Integer> to = pp1.single((-99)).ambWith(pp2.single((-99))).test();
        Assert.assertTrue(pp1.hasSubscribers());
        Assert.assertTrue(pp2.hasSubscribers());
        pp2.onNext(2);
        pp2.onComplete();
        Assert.assertFalse(pp1.hasSubscribers());
        Assert.assertFalse(pp2.hasSubscribers());
        to.assertResult(2);
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 1000)
    public void ambIterableWithFirstFires() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        List<Single<Integer>> singles = Arrays.asList(pp1.single((-99)), pp2.single((-99)));
        TestObserver<Integer> to = Single.amb(singles).test();
        Assert.assertTrue(pp1.hasSubscribers());
        Assert.assertTrue(pp2.hasSubscribers());
        pp1.onNext(1);
        pp1.onComplete();
        Assert.assertFalse(pp1.hasSubscribers());
        Assert.assertFalse(pp2.hasSubscribers());
        to.assertResult(1);
    }

    @SuppressWarnings("unchecked")
    @Test(timeout = 1000)
    public void ambIterableWithSecondFires() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        List<Single<Integer>> singles = Arrays.asList(pp1.single((-99)), pp2.single((-99)));
        TestObserver<Integer> to = Single.amb(singles).test();
        Assert.assertTrue(pp1.hasSubscribers());
        Assert.assertTrue(pp2.hasSubscribers());
        pp2.onNext(2);
        pp2.onComplete();
        Assert.assertFalse(pp1.hasSubscribers());
        Assert.assertFalse(pp2.hasSubscribers());
        to.assertResult(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ambArrayEmpty() {
        Single.ambArray().test().assertFailure(NoSuchElementException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ambSingleSource() {
        Assert.assertSame(Single.never(), Single.ambArray(Single.never()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void error() {
        Single.ambArray(Single.error(new TestException()), Single.just(1)).test().assertFailure(TestException.class);
    }

    @Test
    public void nullSourceSuccessRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final Subject<Integer> ps = ReplaySubject.create();
                ps.onNext(1);
                @SuppressWarnings("unchecked")
                final Single<Integer> source = Single.ambArray(ps.singleOrError(), Single.<Integer>never(), Single.<Integer>never(), null);
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        source.test();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps.onComplete();
                    }
                };
                TestHelper.race(r1, r2);
                if (!(errors.isEmpty())) {
                    TestHelper.assertError(errors, 0, NullPointerException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void multipleErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final Subject<Integer> ps1 = PublishSubject.create();
                final Subject<Integer> ps2 = PublishSubject.create();
                Single.ambArray(ps1.singleOrError(), ps2.singleOrError()).test();
                final TestException ex = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ps1.onError(ex);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps2.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void successErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final Subject<Integer> ps1 = PublishSubject.create();
                final Subject<Integer> ps2 = PublishSubject.create();
                Single.ambArray(ps1.singleOrError(), ps2.singleOrError()).test();
                final TestException ex = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ps1.onNext(1);
                        ps1.onComplete();
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps2.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void manySources() {
        io.reactivex.Single<?>[] sources = new Single[32];
        Arrays.fill(sources, Single.never());
        sources[31] = Single.just(31);
        Single.amb(Arrays.asList(sources)).test().assertResult(31);
    }

    @Test
    public void ambWithOrder() {
        Single<Integer> error = Single.error(new RuntimeException());
        Single.just(1).ambWith(error).test().assertValue(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ambIterableOrder() {
        Single<Integer> error = Single.error(new RuntimeException());
        Single.amb(Arrays.asList(Single.just(1), error)).test().assertValue(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void ambArrayOrder() {
        Single<Integer> error = Single.error(new RuntimeException());
        Single.ambArray(Single.just(1), error).test().assertValue(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noWinnerSuccessDispose() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final AtomicBoolean interrupted = new AtomicBoolean();
            final CountDownLatch cdl = new CountDownLatch(1);
            Single.ambArray(Single.just(1).subscribeOn(Schedulers.single()).observeOn(Schedulers.computation()), Single.never()).subscribe(new io.reactivex.functions.BiConsumer<Object, Throwable>() {
                @Override
                public void accept(Object v, Throwable e) throws Exception {
                    Assert.assertNotNull(v);
                    Assert.assertNull(e);
                    interrupted.set(Thread.currentThread().isInterrupted());
                    cdl.countDown();
                }
            });
            Assert.assertTrue(cdl.await(500, TimeUnit.SECONDS));
            Assert.assertFalse("Interrupted!", interrupted.get());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void noWinnerErrorDispose() throws Exception {
        final TestException ex = new TestException();
        for (int i = 0; i < (TestHelper.RACE_LONG_LOOPS); i++) {
            final AtomicBoolean interrupted = new AtomicBoolean();
            final CountDownLatch cdl = new CountDownLatch(1);
            Single.ambArray(Single.error(ex).subscribeOn(Schedulers.single()).observeOn(Schedulers.computation()), Single.never()).subscribe(new io.reactivex.functions.BiConsumer<Object, Throwable>() {
                @Override
                public void accept(Object v, Throwable e) throws Exception {
                    Assert.assertNull(v);
                    Assert.assertNotNull(e);
                    interrupted.set(Thread.currentThread().isInterrupted());
                    cdl.countDown();
                }
            });
            Assert.assertTrue(cdl.await(500, TimeUnit.SECONDS));
            Assert.assertFalse("Interrupted!", interrupted.get());
        }
    }
}

