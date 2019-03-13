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
import io.reactivex.internal.subscribers.FutureSubscriber;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class FutureObserverTest {
    FutureObserver<Integer> fo;

    @Test
    public void cancel2() {
        fo.dispose();
        Assert.assertFalse(fo.isCancelled());
        Assert.assertFalse(fo.isDisposed());
        Assert.assertFalse(fo.isDone());
        for (int i = 0; i < 2; i++) {
            fo.cancel((i == 0));
            Assert.assertTrue(fo.isCancelled());
            Assert.assertTrue(fo.isDisposed());
            Assert.assertTrue(fo.isDone());
        }
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            fo.onNext(1);
            fo.onError(new TestException("First"));
            fo.onError(new TestException("Second"));
            fo.onComplete();
            Assert.assertTrue(fo.isCancelled());
            Assert.assertTrue(fo.isDisposed());
            Assert.assertTrue(fo.isDone());
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
            TestHelper.assertUndeliverable(errors, 1, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancel() throws Exception {
        Assert.assertFalse(fo.isDone());
        Assert.assertFalse(fo.isCancelled());
        fo.cancel(false);
        Assert.assertTrue(fo.isDone());
        Assert.assertTrue(fo.isCancelled());
        try {
            fo.get();
            Assert.fail("Should have thrown");
        } catch (CancellationException ex) {
            // expected
        }
        try {
            fo.get(1, TimeUnit.MILLISECONDS);
            Assert.fail("Should have thrown");
        } catch (CancellationException ex) {
            // expected
        }
    }

    @Test
    public void onError() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            fo.onError(new TestException("One"));
            fo.onError(new TestException("Two"));
            try {
                fo.get(5, TimeUnit.MILLISECONDS);
            } catch (ExecutionException ex) {
                Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof TestException));
                Assert.assertEquals("One", ex.getCause().getMessage());
            }
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Two");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onNext() throws Exception {
        fo.onNext(1);
        fo.onComplete();
        Assert.assertEquals(1, fo.get(5, TimeUnit.MILLISECONDS).intValue());
    }

    @Test
    public void onSubscribe() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Disposable d1 = Disposables.empty();
            fo.onSubscribe(d1);
            Disposable d2 = Disposables.empty();
            fo.onSubscribe(d2);
            Assert.assertFalse(d1.isDisposed());
            Assert.assertTrue(d2.isDisposed());
            TestHelper.assertError(errors, 0, IllegalStateException.class, "Disposable already set!");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final FutureSubscriber<Integer> fo = new FutureSubscriber<Integer>();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    fo.cancel(false);
                }
            };
            TestHelper.race(r, r);
        }
    }

    @Test
    public void await() throws Exception {
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                fo.onNext(1);
                fo.onComplete();
            }
        }, 100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, fo.get(5, TimeUnit.SECONDS).intValue());
    }

    @Test
    public void onErrorCancelRace() {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());
        try {
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                final FutureSubscriber<Integer> fo = new FutureSubscriber<Integer>();
                final TestException ex = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        fo.cancel(false);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        fo.onError(ex);
                    }
                };
                TestHelper.race(r1, r2);
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteCancelRace() {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());
        try {
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                final FutureSubscriber<Integer> fo = new FutureSubscriber<Integer>();
                if ((i % 3) == 0) {
                    fo.onSubscribe(new BooleanSubscription());
                }
                if ((i % 2) == 0) {
                    fo.onNext(1);
                }
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        fo.cancel(false);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        fo.onComplete();
                    }
                };
                TestHelper.race(r1, r2);
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorOnComplete() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            fo.onError(new TestException("One"));
            fo.onComplete();
            try {
                fo.get(5, TimeUnit.MILLISECONDS);
            } catch (ExecutionException ex) {
                Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof TestException));
                Assert.assertEquals("One", ex.getCause().getMessage());
            }
            TestHelper.assertUndeliverable(errors, 0, NoSuchElementException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteOnError() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            fo.onComplete();
            fo.onError(new TestException("One"));
            try {
                Assert.assertNull(fo.get(5, TimeUnit.MILLISECONDS));
            } catch (ExecutionException ex) {
                Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof NoSuchElementException));
            }
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancelOnError() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            fo.cancel(true);
            fo.onError(new TestException("One"));
            try {
                fo.get(5, TimeUnit.MILLISECONDS);
                Assert.fail("Should have thrown");
            } catch (CancellationException ex) {
                // expected
            }
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancelOnComplete() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            fo.cancel(true);
            fo.onComplete();
            try {
                fo.get(5, TimeUnit.MILLISECONDS);
                Assert.fail("Should have thrown");
            } catch (CancellationException ex) {
                // expected
            }
            TestHelper.assertUndeliverable(errors, 0, NoSuchElementException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onNextThenOnCompleteTwice() throws Exception {
        fo.onNext(1);
        fo.onComplete();
        fo.onComplete();
        Assert.assertEquals(1, fo.get(5, TimeUnit.MILLISECONDS).intValue());
    }

    @Test(expected = InterruptedException.class)
    public void getInterrupted() throws Exception {
        Thread.currentThread().interrupt();
        fo.get();
    }

    @Test
    public void completeAsync() throws Exception {
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                fo.onNext(1);
                fo.onComplete();
            }
        }, 500, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, fo.get().intValue());
    }

    @Test
    public void getTimedOut() throws Exception {
        try {
            fo.get(1, TimeUnit.NANOSECONDS);
            Assert.fail("Should have thrown");
        } catch (TimeoutException expected) {
            Assert.assertEquals(ExceptionHelper.timeoutMessage(1, TimeUnit.NANOSECONDS), expected.getMessage());
        }
    }
}

