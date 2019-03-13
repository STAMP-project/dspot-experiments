/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.concurrent.semaphore;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public abstract class SemaphoreBasicTest extends HazelcastTestSupport {
    protected HazelcastInstance[] instances;

    protected ISemaphore semaphore;

    @Test(timeout = 30000)
    public void testAcquire() throws InterruptedException {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i++) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            semaphore.acquire();
        }
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test(timeout = 30000)
    public void testAcquire_whenNoPermits() throws InterruptedException {
        semaphore.init(0);
        final SemaphoreBasicTest.AcquireThread acquireThread = new SemaphoreBasicTest.AcquireThread(semaphore);
        acquireThread.start();
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(acquireThread.isAlive());
                Assert.assertEquals(0, semaphore.availablePermits());
            }
        }, 5);
    }

    @Test(timeout = 30000)
    public void testAcquire_whenNoPermits_andSemaphoreDestroyed() throws InterruptedException {
        SemaphoreBasicTest.AcquireThread thread = new SemaphoreBasicTest.AcquireThread(semaphore);
        thread.start();
        semaphore.destroy();
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testRelease() {
        int numberOfPermits = 20;
        for (int i = 0; i < numberOfPermits; i++) {
            Assert.assertEquals(i, semaphore.availablePermits());
            semaphore.release();
        }
        Assert.assertEquals(semaphore.availablePermits(), numberOfPermits);
    }

    @Test(timeout = 30000)
    public void testAllowNegativePermits() {
        Assert.assertTrue(semaphore.init(10));
        semaphore.reducePermits(15);
        Assert.assertEquals((-5), semaphore.availablePermits());
        semaphore.release(10);
        Assert.assertEquals(5, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testNegativePermitsJucCompatibility() {
        Assert.assertTrue(semaphore.init(0));
        semaphore.reducePermits(100);
        semaphore.release(10);
        Assert.assertEquals((-90), semaphore.availablePermits());
        Assert.assertEquals((-90), semaphore.drainPermits());
        semaphore.release(10);
        Assert.assertEquals(10, semaphore.availablePermits());
        Assert.assertEquals(10, semaphore.drainPermits());
    }

    @Test(timeout = 30000)
    public void testIncreasePermits() {
        Assert.assertTrue(semaphore.init(10));
        Assert.assertEquals(10, semaphore.availablePermits());
        semaphore.increasePermits(100);
        Assert.assertEquals(110, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testRelease_whenArgumentNegative() {
        try {
            semaphore.release((-5));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testRelease_whenBlockedAcquireThread() throws InterruptedException {
        semaphore.init(0);
        new Thread() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        semaphore.release();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(0, semaphore.availablePermits());
            }
        });
    }

    @Test(timeout = 30000)
    public void testMultipleAcquire() throws InterruptedException {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i += 5) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            semaphore.acquire(5);
        }
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test(timeout = 30000)
    public void testMultipleAcquire_whenNegative() throws InterruptedException {
        int numberOfPermits = 10;
        semaphore.init(numberOfPermits);
        try {
            for (int i = 0; i < numberOfPermits; i += 5) {
                semaphore.acquire((-5));
                Assert.fail();
            }
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(10, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testMultipleAcquire_whenNotEnoughPermits() throws InterruptedException {
        int numberOfPermits = 5;
        semaphore.init(numberOfPermits);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    semaphore.acquire(6);
                    Assert.assertEquals(5, semaphore.availablePermits());
                    semaphore.acquire(6);
                    Assert.assertEquals(5, semaphore.availablePermits());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(thread.isAlive());
                Assert.assertEquals(5, semaphore.availablePermits());
            }
        }, 5);
    }

    @Test(timeout = 30000)
    public void testMultipleRelease() {
        int numberOfPermits = 20;
        for (int i = 0; i < numberOfPermits; i += 5) {
            Assert.assertEquals(i, semaphore.availablePermits());
            semaphore.release(5);
        }
        Assert.assertEquals(semaphore.availablePermits(), numberOfPermits);
    }

    @Test(timeout = 30000)
    public void testMultipleRelease_whenNegative() throws InterruptedException {
        semaphore.init(0);
        try {
            semaphore.release((-5));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testMultipleRelease_whenBlockedAcquireThreads() throws InterruptedException {
        int numberOfPermits = 10;
        semaphore.init(numberOfPermits);
        semaphore.acquire(numberOfPermits);
        CountDownLatch latch1 = new CountDownLatch(1);
        Thread thread1 = new SemaphoreBasicTest.BlockAcquireThread(semaphore, latch1);
        thread1.start();
        semaphore.release();
        HazelcastTestSupport.assertOpenEventually(latch1);
    }

    @Test(timeout = 30000)
    public void testDrain() throws InterruptedException {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        semaphore.acquire(5);
        int drainedPermits = semaphore.drainPermits();
        Assert.assertEquals(drainedPermits, (numberOfPermits - 5));
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test(timeout = 30000)
    public void testDrain_whenNoPermits() throws InterruptedException {
        semaphore.init(0);
        Assert.assertEquals(0, semaphore.drainPermits());
    }

    @Test(timeout = 30000)
    public void testReduce() {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i += 5) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            semaphore.reducePermits(5);
        }
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test(timeout = 30000)
    public void testReduce_whenArgumentNegative() {
        try {
            semaphore.reducePermits((-5));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testIncrease_whenArgumentNegative() {
        try {
            semaphore.increasePermits((-5));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testTryAcquire() {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i++) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            Assert.assertEquals(semaphore.tryAcquire(), true);
        }
        Assert.assertFalse(semaphore.tryAcquire());
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test(timeout = 30000)
    public void testTryAcquireMultiple() {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i += 5) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            Assert.assertEquals(semaphore.tryAcquire(5), true);
        }
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test(timeout = 30000)
    public void testTryAcquireMultiple_whenArgumentNegative() {
        int negativePermits = -5;
        semaphore.init(0);
        try {
            semaphore.tryAcquire(negativePermits);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testTryAcquire_whenNotEnoughPermits() throws InterruptedException {
        int numberOfPermits = 10;
        semaphore.init(numberOfPermits);
        semaphore.acquire(10);
        boolean result = semaphore.tryAcquire(1);
        Assert.assertFalse(result);
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testInit_whenNotIntialized() {
        boolean result = semaphore.init(2);
        Assert.assertTrue(result);
        Assert.assertEquals(2, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testInit_whenAlreadyIntialized() {
        semaphore.init(2);
        boolean result = semaphore.init(4);
        Assert.assertFalse(result);
        Assert.assertEquals(2, semaphore.availablePermits());
    }

    private class AcquireThread extends Thread {
        ISemaphore semaphore;

        AcquireThread(ISemaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class BlockAcquireThread extends Thread {
        ISemaphore semaphore;

        CountDownLatch latch;

        BlockAcquireThread(ISemaphore semaphore, CountDownLatch latch) {
            this.semaphore = semaphore;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

