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
package io.reactivex.internal.operators.completable;


import io.reactivex.Completable;
import io.reactivex.exceptions.TestException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class CompletableFromRunnableTest {
    @Test(expected = NullPointerException.class)
    public void fromRunnableNull() {
        Completable.fromRunnable(null);
    }

    @Test
    public void fromRunnable() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                atomicInteger.incrementAndGet();
            }
        }).test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
    }

    @Test
    public void fromRunnableTwice() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                atomicInteger.incrementAndGet();
            }
        };
        Completable.fromRunnable(run).test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
        Completable.fromRunnable(run).test().assertResult();
        Assert.assertEquals(2, atomicInteger.get());
    }

    @Test
    public void fromRunnableInvokesLazy() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Completable completable = Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                atomicInteger.incrementAndGet();
            }
        });
        Assert.assertEquals(0, atomicInteger.get());
        completable.test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
    }

    @Test
    public void fromRunnableThrows() {
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                throw new UnsupportedOperationException();
            }
        }).test().assertFailure(UnsupportedOperationException.class);
    }

    @Test
    public void fromRunnableDisposed() {
        final AtomicInteger calls = new AtomicInteger();
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                calls.incrementAndGet();
            }
        }).test(true).assertEmpty();
        Assert.assertEquals(1, calls.get());
    }

    @Test
    public void fromRunnableErrorsDisposed() {
        final AtomicInteger calls = new AtomicInteger();
        Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                calls.incrementAndGet();
                throw new TestException();
            }
        }).test(true).assertEmpty();
        Assert.assertEquals(1, calls.get());
    }
}

