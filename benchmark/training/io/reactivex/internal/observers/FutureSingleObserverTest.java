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
package io.reactivex.internal.observers;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class FutureSingleObserverTest {
    @Test
    public void cancel() {
        final Future<?> f = Single.never().toFuture();
        Assert.assertFalse(f.isCancelled());
        Assert.assertFalse(f.isDone());
        f.cancel(true);
        Assert.assertTrue(f.isCancelled());
        Assert.assertTrue(f.isDone());
        try {
            f.get();
            Assert.fail("Should have thrown!");
        } catch (CancellationException ex) {
            // expected
        } catch (InterruptedException ex) {
            throw new AssertionError(ex);
        } catch (ExecutionException ex) {
            throw new AssertionError(ex);
        }
        try {
            f.get(5, TimeUnit.SECONDS);
            Assert.fail("Should have thrown!");
        } catch (CancellationException ex) {
            // expected
        } catch (InterruptedException ex) {
            throw new AssertionError(ex);
        } catch (ExecutionException ex) {
            throw new AssertionError(ex);
        } catch (TimeoutException ex) {
            throw new AssertionError(ex);
        }
    }

    @Test
    public void cancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final Future<?> f = Single.never().toFuture();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    f.cancel(true);
                }
            };
            TestHelper.race(r, r);
        }
    }

    @Test
    public void timeout() throws Exception {
        Future<?> f = Single.never().toFuture();
        try {
            f.get(100, TimeUnit.MILLISECONDS);
            Assert.fail("Should have thrown");
        } catch (TimeoutException expected) {
            Assert.assertEquals(ExceptionHelper.timeoutMessage(100, TimeUnit.MILLISECONDS), expected.getMessage());
        }
    }

    @Test
    public void dispose() {
        Future<Integer> f = Single.just(1).toFuture();
        ((io.reactivex.disposables.Disposable) (f)).dispose();
        Assert.assertTrue(isDisposed());
    }

    @Test
    public void errorGetWithTimeout() throws Exception {
        Future<?> f = Single.error(new TestException()).toFuture();
        try {
            f.get(5, TimeUnit.SECONDS);
            Assert.fail("Should have thrown");
        } catch (ExecutionException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof TestException));
        }
    }

    @Test
    public void normalGetWitHTimeout() throws Exception {
        Future<Integer> f = Single.just(1).toFuture();
        Assert.assertEquals(1, f.get(5, TimeUnit.SECONDS).intValue());
    }

    @Test
    public void getAwait() throws Exception {
        Future<Integer> f = Single.just(1).delay(100, TimeUnit.MILLISECONDS).toFuture();
        Assert.assertEquals(1, f.get(5, TimeUnit.SECONDS).intValue());
    }

    @Test
    public void onSuccessCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final Future<?> f = ps.single((-99)).toFuture();
            ps.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    f.cancel(true);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ps.onComplete();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void onErrorCancelRace() {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());
        try {
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                final PublishSubject<Integer> ps = PublishSubject.create();
                final Future<?> f = ps.single((-99)).toFuture();
                final TestException ex = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        f.cancel(true);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

