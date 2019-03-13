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
package io.reactivex.internal.subscribers;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
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


public class FutureSubscriberTest {
    FutureSubscriber<Integer> fs;

    @Test
    public void cancel() throws Exception {
        Assert.assertFalse(fs.isDone());
        Assert.assertFalse(fs.isCancelled());
        fs.cancel();
        fs.cancel();
        fs.request(10);
        fs.request((-99));
        fs.cancel(false);
        Assert.assertTrue(fs.isDone());
        Assert.assertTrue(fs.isCancelled());
        try {
            fs.get();
            Assert.fail("Should have thrown");
        } catch (CancellationException ex) {
            // expected
        }
        try {
            fs.get(1, TimeUnit.MILLISECONDS);
            Assert.fail("Should have thrown");
        } catch (CancellationException ex) {
            // expected
        }
    }

    @Test
    public void onError() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            fs.onError(new TestException("One"));
            fs.onError(new TestException("Two"));
            try {
                fs.get(5, TimeUnit.MILLISECONDS);
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
        fs.onNext(1);
        fs.onComplete();
        Assert.assertEquals(1, fs.get(5, TimeUnit.MILLISECONDS).intValue());
    }

    @Test
    public void onSubscribe() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            BooleanSubscription s = new BooleanSubscription();
            fs.onSubscribe(s);
            BooleanSubscription s2 = new BooleanSubscription();
            fs.onSubscribe(s2);
            Assert.assertFalse(s.isCancelled());
            Assert.assertTrue(s2.isCancelled());
            TestHelper.assertError(errors, 0, IllegalStateException.class, "Subscription already set!");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final FutureSubscriber<Integer> fs = new FutureSubscriber<Integer>();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    fs.cancel(false);
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
                fs.onNext(1);
                fs.onComplete();
            }
        }, 100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, fs.get(5, TimeUnit.SECONDS).intValue());
    }

    @Test
    public void onErrorCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final FutureSubscriber<Integer> fs = new FutureSubscriber<Integer>();
            final TestException ex = new TestException();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    fs.cancel(false);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    fs.onError(ex);
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void onCompleteCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final FutureSubscriber<Integer> fs = new FutureSubscriber<Integer>();
            if ((i % 3) == 0) {
                fs.onSubscribe(new BooleanSubscription());
            }
            if ((i % 2) == 0) {
                fs.onNext(1);
            }
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    fs.cancel(false);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    fs.onComplete();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void onErrorOnComplete() throws Exception {
        fs.onError(new TestException("One"));
        fs.onComplete();
        try {
            fs.get(5, TimeUnit.MILLISECONDS);
        } catch (ExecutionException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof TestException));
            Assert.assertEquals("One", ex.getCause().getMessage());
        }
    }

    @Test
    public void onCompleteOnError() throws Exception {
        fs.onComplete();
        fs.onError(new TestException("One"));
        try {
            Assert.assertNull(fs.get(5, TimeUnit.MILLISECONDS));
        } catch (ExecutionException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof NoSuchElementException));
        }
    }

    @Test
    public void cancelOnError() throws Exception {
        fs.cancel(true);
        fs.onError(new TestException("One"));
        try {
            fs.get(5, TimeUnit.MILLISECONDS);
            Assert.fail("Should have thrown");
        } catch (CancellationException ex) {
            // expected
        }
    }

    @Test
    public void cancelOnComplete() throws Exception {
        fs.cancel(true);
        fs.onComplete();
        try {
            fs.get(5, TimeUnit.MILLISECONDS);
            Assert.fail("Should have thrown");
        } catch (CancellationException ex) {
            // expected
        }
    }

    @Test
    public void onNextThenOnCompleteTwice() throws Exception {
        fs.onNext(1);
        fs.onComplete();
        fs.onComplete();
        Assert.assertEquals(1, fs.get(5, TimeUnit.MILLISECONDS).intValue());
    }

    @Test
    public void completeAsync() throws Exception {
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                fs.onNext(1);
                fs.onComplete();
            }
        }, 500, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, fs.get().intValue());
    }

    @Test
    public void getTimedOut() throws Exception {
        try {
            fs.get(1, TimeUnit.NANOSECONDS);
            Assert.fail("Should have thrown");
        } catch (TimeoutException expected) {
            Assert.assertEquals(ExceptionHelper.timeoutMessage(1, TimeUnit.NANOSECONDS), expected.getMessage());
        }
    }
}

