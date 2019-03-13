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
import io.reactivex.functions.Action;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class CompletableFromActionTest {
    @Test(expected = NullPointerException.class)
    public void fromActionNull() {
        Completable.fromAction(null);
    }

    @Test
    public void fromAction() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                atomicInteger.incrementAndGet();
            }
        }).test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
    }

    @Test
    public void fromActionTwice() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Action run = new Action() {
            @Override
            public void run() throws Exception {
                atomicInteger.incrementAndGet();
            }
        };
        Completable.fromAction(run).test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
        Completable.fromAction(run).test().assertResult();
        Assert.assertEquals(2, atomicInteger.get());
    }

    @Test
    public void fromActionInvokesLazy() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Completable completable = Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                atomicInteger.incrementAndGet();
            }
        });
        Assert.assertEquals(0, atomicInteger.get());
        completable.test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
    }

    @Test
    public void fromActionThrows() {
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                throw new UnsupportedOperationException();
            }
        }).test().assertFailure(UnsupportedOperationException.class);
    }

    @Test
    public void fromActionDisposed() {
        final AtomicInteger calls = new AtomicInteger();
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                calls.incrementAndGet();
            }
        }).test(true).assertEmpty();
        Assert.assertEquals(1, calls.get());
    }

    @Test
    public void fromActionErrorsDisposed() {
        final AtomicInteger calls = new AtomicInteger();
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                calls.incrementAndGet();
                throw new TestException();
            }
        }).test(true).assertEmpty();
        Assert.assertEquals(1, calls.get());
    }
}

