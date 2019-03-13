/**
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.core.lock;


import io.atomix.core.AbstractPrimitiveTest;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Distributed lock test.
 */
public class DistributedLockTest extends AbstractPrimitiveTest {
    /**
     * Tests locking and unlocking a lock.
     */
    @Test
    public void testLockUnlock() throws Throwable {
        DistributedLock lock = atomix().lockBuilder("test-lock-unlock").withProtocol(protocol()).build();
        lock.lock();
        Assert.assertTrue(lock.isLocked());
        lock.unlock();
        Assert.assertFalse(lock.isLocked());
    }

    /**
     * Tests releasing a lock when the client's session is closed.
     */
    @Test
    public void testReleaseOnClose() throws Throwable {
        DistributedLock lock1 = atomix().lockBuilder("test-lock-on-close").withProtocol(protocol()).build();
        DistributedLock lock2 = atomix().lockBuilder("test-lock-on-close").withProtocol(protocol()).build();
        lock1.lock();
        CompletableFuture<Void> future = lock2.async().lock();
        lock1.close();
        future.get(10, TimeUnit.SECONDS);
    }

    /**
     * Tests attempting to acquire a lock.
     */
    @Test
    public void testTryLockFail() throws Throwable {
        DistributedLock lock1 = atomix().lockBuilder("test-try-lock-fail").withProtocol(protocol()).build();
        DistributedLock lock2 = atomix().lockBuilder("test-try-lock-fail").withProtocol(protocol()).build();
        lock1.lock();
        Assert.assertFalse(lock2.tryLock());
    }

    /**
     * Tests attempting to acquire a lock.
     */
    @Test
    public void testTryLockSucceed() throws Throwable {
        DistributedLock lock = atomix().lockBuilder("test-try-lock-succeed").withProtocol(protocol()).build();
        Assert.assertTrue(lock.tryLock());
    }

    /**
     * Tests attempting to acquire a lock with a timeout.
     */
    @Test
    public void testTryLockFailWithTimeout() throws Throwable {
        DistributedLock lock1 = atomix().lockBuilder("test-try-lock-fail-with-timeout").withProtocol(protocol()).build();
        DistributedLock lock2 = atomix().lockBuilder("test-try-lock-fail-with-timeout").withProtocol(protocol()).build();
        lock1.lock();
        Assert.assertFalse(lock2.tryLock(Duration.ofSeconds(1)));
    }

    /**
     * Tests attempting to acquire a lock with a timeout.
     */
    @Test
    public void testTryLockSucceedWithTimeout() throws Throwable {
        DistributedLock lock1 = atomix().lockBuilder("test-try-lock-succeed-with-timeout").withProtocol(protocol()).build();
        DistributedLock lock2 = atomix().lockBuilder("test-try-lock-succeed-with-timeout").withProtocol(protocol()).build();
        lock1.lock();
        CompletableFuture<Boolean> future = lock2.async().tryLock(Duration.ofSeconds(1));
        lock1.unlock();
        Assert.assertTrue(future.get(10, TimeUnit.SECONDS));
    }

    /**
     * Tests unlocking a lock with a blocking call in the event thread.
     */
    @Test
    public void testBlockingUnlock() throws Throwable {
        DistributedLock lock1 = atomix().lockBuilder("test-blocking-unlock").withProtocol(protocol()).build();
        DistributedLock lock2 = atomix().lockBuilder("test-blocking-unlock").withProtocol(protocol()).build();
        lock1.async().lock().thenRun(() -> lock1.unlock());
        lock2.lock();
    }
}

