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


import io.reactivex.exceptions.TestException;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;


public class BlockingMultiObserverTest {
    @Test
    public void dispose() {
        BlockingMultiObserver<Integer> bmo = new BlockingMultiObserver<Integer>();
        bmo.dispose();
        Disposable d = Disposables.empty();
        bmo.onSubscribe(d);
    }

    @Test
    public void blockingGetDefault() {
        final BlockingMultiObserver<Integer> bmo = new BlockingMultiObserver<Integer>();
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                bmo.onSuccess(1);
            }
        }, 100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, bmo.blockingGet(0).intValue());
    }

    @Test
    public void blockingAwait() {
        final BlockingMultiObserver<Integer> bmo = new BlockingMultiObserver<Integer>();
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                bmo.onSuccess(1);
            }
        }, 100, TimeUnit.MILLISECONDS);
        Assert.assertTrue(bmo.blockingAwait(1, TimeUnit.MINUTES));
    }

    @Test
    public void blockingGetDefaultInterrupt() {
        final BlockingMultiObserver<Integer> bmo = new BlockingMultiObserver<Integer>();
        Thread.currentThread().interrupt();
        try {
            bmo.blockingGet(0);
            Assert.fail("Should have thrown");
        } catch (RuntimeException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof InterruptedException));
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void blockingGetErrorInterrupt() {
        final BlockingMultiObserver<Integer> bmo = new BlockingMultiObserver<Integer>();
        Thread.currentThread().interrupt();
        try {
            Assert.assertTrue(((bmo.blockingGetError()) instanceof InterruptedException));
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void blockingGetErrorTimeoutInterrupt() {
        final BlockingMultiObserver<Integer> bmo = new BlockingMultiObserver<Integer>();
        Thread.currentThread().interrupt();
        try {
            bmo.blockingGetError(1, TimeUnit.MINUTES);
            Assert.fail("Should have thrown");
        } catch (RuntimeException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof InterruptedException));
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void blockingGetErrorDelayed() {
        final BlockingMultiObserver<Integer> bmo = new BlockingMultiObserver<Integer>();
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                bmo.onError(new TestException());
            }
        }, 100, TimeUnit.MILLISECONDS);
        Assert.assertTrue(((bmo.blockingGetError()) instanceof TestException));
    }

    @Test
    public void blockingGetErrorTimeoutDelayed() {
        final BlockingMultiObserver<Integer> bmo = new BlockingMultiObserver<Integer>();
        Schedulers.single().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                bmo.onError(new TestException());
            }
        }, 100, TimeUnit.MILLISECONDS);
        Assert.assertTrue(((bmo.blockingGetError(1, TimeUnit.MINUTES)) instanceof TestException));
    }

    @Test
    public void blockingGetErrorTimedOut() {
        final BlockingMultiObserver<Integer> bmo = new BlockingMultiObserver<Integer>();
        try {
            Assert.assertNull(bmo.blockingGetError(1, TimeUnit.NANOSECONDS));
            Assert.fail("Should have thrown");
        } catch (RuntimeException expected) {
            Assert.assertEquals(TimeoutException.class, expected.getCause().getClass());
            Assert.assertEquals(ExceptionHelper.timeoutMessage(1, TimeUnit.NANOSECONDS), expected.getCause().getMessage());
        }
    }
}

