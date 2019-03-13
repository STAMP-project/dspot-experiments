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


import io.reactivex.exceptions.TestException;
import io.reactivex.processors.PublishProcessor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class CompletableAwaitTest {
    @Test
    public void awaitInterrupted() {
        Thread.currentThread().interrupt();
        try {
            PublishProcessor.create().ignoreElements().blockingAwait();
            Assert.fail("Should have thrown RuntimeException");
        } catch (RuntimeException ex) {
            if (!((ex.getCause()) instanceof InterruptedException)) {
                Assert.fail(("Wrong cause: " + (ex.getCause())));
            }
        }
    }

    @Test
    public void awaitTimeoutInterrupted() {
        Thread.currentThread().interrupt();
        try {
            PublishProcessor.create().ignoreElements().blockingAwait(1, TimeUnit.SECONDS);
            Assert.fail("Should have thrown RuntimeException");
        } catch (RuntimeException ex) {
            if (!((ex.getCause()) instanceof InterruptedException)) {
                Assert.fail(("Wrong cause: " + (ex.getCause())));
            }
        }
    }

    @Test
    public void awaitTimeout() {
        Assert.assertFalse(PublishProcessor.create().ignoreElements().blockingAwait(100, TimeUnit.MILLISECONDS));
    }

    @Test
    public void blockingGet() {
        Assert.assertNull(Completable.complete().blockingGet());
    }

    @Test
    public void blockingGetTimeout() {
        Assert.assertNull(Completable.complete().blockingGet(1, TimeUnit.SECONDS));
    }

    @Test
    public void blockingGetError() {
        TestException ex = new TestException();
        Assert.assertSame(ex, Completable.error(ex).blockingGet());
    }

    @Test
    public void blockingGetErrorTimeout() {
        TestException ex = new TestException();
        Assert.assertSame(ex, Completable.error(ex).blockingGet(1, TimeUnit.SECONDS));
    }
}

