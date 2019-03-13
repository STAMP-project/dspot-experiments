/**
 * Copyright 2013-2018 Ray Tsang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdeferred2.impl;


import State.REJECTED;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import junitparams.JUnitParamsRunner;
import org.jdeferred2.CancellationHandler;
import org.jdeferred2.DeferredCallable;
import org.jdeferred2.DeferredRunnable;
import org.jdeferred2.Promise;
import org.jdeferred2.Promise.State;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JUnitParamsRunner.class)
public class CancelTaskTest extends AbstractDeferredTest {
    @Test
    public void explicitRejectionWithCancellationException() {
        final AtomicBoolean failWitness = new AtomicBoolean(false);
        DeferredObject<String, Throwable, Void> deferredObject = new DeferredObject<String, Throwable, Void>();
        Promise<String, Throwable, Void> promise = deferredObject.promise().then(new org.jdeferred2.DoneCallback<String>() {
            @Override
            public void onDone(String result) {
                Assert.fail("Shouldn't be called, because task was cancelled");
            }
        }).always(new org.jdeferred2.AlwaysCallback<String, Throwable>() {
            @Override
            public void onAlways(State state, String resolved, Throwable rejected) {
                Assert.assertEquals(REJECTED, state);
                Assert.assertNull(resolved);
                Assert.assertTrue((rejected instanceof CancellationException));
            }
        }).fail(new org.jdeferred2.FailCallback<Throwable>() {
            @Override
            public void onFail(Throwable result) {
                failWitness.set(true);
            }
        });
        deferredObject.reject(new CancellationException());
        Assert.assertTrue(promise.isRejected());
        Assert.assertTrue(failWitness.get());
    }

    public interface CancellationWitness extends CancellationHandler {
        boolean invoked();
    }

    public static class DefaultCancellationHandler implements CancelTaskTest.CancellationWitness {
        private volatile boolean invoked;

        @Override
        public void onCancel() {
            invoked = true;
        }

        @Override
        public boolean invoked() {
            return invoked;
        }
    }

    public static class CancellationHandlerCallable extends CancelTaskTest.DefaultCancellationHandler implements Callable<String> {
        @Override
        public String call() throws Exception {
            CancelTaskTest.simulateWork();
            return "Hello";
        }
    }

    public static class CancellationHandlerRunnable extends CancelTaskTest.DefaultCancellationHandler implements Runnable {
        @Override
        public void run() {
            CancelTaskTest.simulateWork();
        }
    }

    public static class CancellationHandlerDeferredCallable extends DeferredCallable<String, Void> implements CancelTaskTest.CancellationWitness {
        private volatile boolean invoked;

        @Override
        public String call() throws Exception {
            CancelTaskTest.simulateWork();
            return "Hello";
        }

        @Override
        public void onCancel() {
            invoked = true;
        }

        @Override
        public boolean invoked() {
            return invoked;
        }
    }

    public static class CancellationHandlerDeferredRunnable extends DeferredRunnable<Void> implements CancelTaskTest.CancellationWitness {
        private volatile boolean invoked;

        @Override
        public void run() {
            CancelTaskTest.simulateWork();
        }

        @Override
        public void onCancel() {
            invoked = true;
        }

        @Override
        public boolean invoked() {
            return invoked;
        }
    }
}

