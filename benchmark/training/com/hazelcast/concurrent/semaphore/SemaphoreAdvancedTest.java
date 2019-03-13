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
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class SemaphoreAdvancedTest extends HazelcastTestSupport {
    @Test(expected = IllegalStateException.class, timeout = 30000)
    public void testAcquire_whenInstanceShutdown() throws InterruptedException {
        HazelcastInstance hz = createHazelcastInstance();
        final ISemaphore semaphore = hz.getSemaphore(HazelcastTestSupport.randomString());
        hz.shutdown();
        semaphore.acquire();
    }

    @Test(timeout = 300000)
    public void testSemaphoreWithFailures() throws InterruptedException {
        final String semaphoreName = HazelcastTestSupport.randomString();
        final int k = 4;
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory((k + 1));
        final HazelcastInstance[] instances = factory.newInstances();
        final ISemaphore semaphore = instances[k].getSemaphore(semaphoreName);
        int initialPermits = 20;
        semaphore.init(initialPermits);
        for (int i = 0; i < k; i++) {
            int rand = ((int) ((Math.random()) * 5)) + 1;
            semaphore.acquire(rand);
            initialPermits -= rand;
            Assert.assertEquals(initialPermits, semaphore.availablePermits());
            semaphore.release(rand);
            initialPermits += rand;
            Assert.assertEquals(initialPermits, semaphore.availablePermits());
            instances[i].shutdown();
            semaphore.acquire(rand);
            initialPermits -= rand;
            Assert.assertEquals(initialPermits, semaphore.availablePermits());
            semaphore.release(rand);
            initialPermits += rand;
            Assert.assertEquals(initialPermits, semaphore.availablePermits());
        }
    }

    @Test(timeout = 300000)
    public void testMutex() throws InterruptedException {
        final String semaphoreName = HazelcastTestSupport.randomString();
        final int threadCount = 2;
        final HazelcastInstance[] instances = createHazelcastInstanceFactory(threadCount).newInstances();
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final int loopCount = 1000;
        class Counter {
            int count = 0;

            void inc() {
                (count)++;
            }

            int get() {
                return count;
            }
        }
        final Counter counter = new Counter();
        Assert.assertTrue(instances[0].getSemaphore(semaphoreName).init(1));
        for (int i = 0; i < threadCount; i++) {
            final ISemaphore semaphore = instances[i].getSemaphore(semaphoreName);
            new Thread() {
                public void run() {
                    for (int j = 0; j < loopCount; j++) {
                        try {
                            semaphore.acquire();
                            HazelcastTestSupport.sleepMillis(((int) ((Math.random()) * 3)));
                            counter.inc();
                        } catch (InterruptedException e) {
                            return;
                        } finally {
                            semaphore.release();
                        }
                    }
                    latch.countDown();
                }
            }.start();
        }
        HazelcastTestSupport.assertOpenEventually(latch);
        Assert.assertEquals((loopCount * threadCount), counter.get());
    }
}

