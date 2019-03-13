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
package com.hazelcast.cp.internal.datastructures.semaphore;


import RaftSemaphoreService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftSessionlessSemaphoreBasicTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    protected ISemaphore semaphore;

    protected String objectName = "semaphore";

    protected String proxyName = (objectName) + "@group1";

    protected HazelcastInstance semaphoreInstance;

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProxyOnMetadataCPGroup() {
        semaphoreInstance.getCPSubsystem().getSemaphore(((objectName) + "@metadata"));
    }

    @Test
    public void testInit() {
        Assert.assertTrue(semaphore.init(7));
        Assert.assertEquals(7, semaphore.availablePermits());
    }

    @Test
    public void testInitFails_whenAlreadyInitialized() {
        Assert.assertTrue(semaphore.init(7));
        Assert.assertFalse(semaphore.init(5));
        Assert.assertEquals(7, semaphore.availablePermits());
    }

    @Test
    public void testAcquire() throws InterruptedException {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i++) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            semaphore.acquire();
        }
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test
    public void testAcquire_whenNoPermits() {
        semaphore.init(0);
        final Future future = HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(future.isDone());
                Assert.assertEquals(0, semaphore.availablePermits());
            }
        }, 5);
    }

    @Test
    public void testAcquire_whenNoPermits_andSemaphoreDestroyed() throws Exception {
        semaphore.init(0);
        Future future = HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.destroy();
        try {
            future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRelease() {
        int numberOfPermits = 20;
        for (int i = 0; i < numberOfPermits; i++) {
            Assert.assertEquals(i, semaphore.availablePermits());
            semaphore.release();
        }
        Assert.assertEquals(semaphore.availablePermits(), numberOfPermits);
    }

    @Test
    public void testAllowNegativePermits() {
        Assert.assertTrue(semaphore.init(10));
        semaphore.reducePermits(15);
        Assert.assertEquals((-5), semaphore.availablePermits());
        semaphore.release(10);
        Assert.assertEquals(5, semaphore.availablePermits());
    }

    @Test
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

    @Test
    public void testIncreasePermits() {
        Assert.assertTrue(semaphore.init(10));
        Assert.assertEquals(10, semaphore.availablePermits());
        semaphore.increasePermits(100);
        Assert.assertEquals(110, semaphore.availablePermits());
    }

    @Test
    public void testRelease_whenArgumentNegative() {
        try {
            semaphore.release((-5));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test
    public void testRelease_whenBlockedAcquireThread() {
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
            public void run() {
                Assert.assertEquals(0, semaphore.availablePermits());
            }
        });
    }

    @Test
    public void testMultipleAcquire() throws InterruptedException {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i += 5) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            semaphore.acquire(5);
        }
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test
    public void testMultipleAcquire_whenNegative() throws InterruptedException {
        int numberOfPermits = 10;
        semaphore.init(numberOfPermits);
        try {
            semaphore.acquire((-5));
            Assert.fail();
        } catch (IllegalArgumentException ignored) {
        }
        Assert.assertEquals(10, semaphore.availablePermits());
    }

    @Test
    public void testMultipleAcquire_whenNotEnoughPermits() {
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
            public void run() {
                Assert.assertTrue(thread.isAlive());
                Assert.assertEquals(5, semaphore.availablePermits());
            }
        }, 5);
    }

    @Test
    public void testMultipleRelease() {
        int numberOfPermits = 20;
        for (int i = 0; i < numberOfPermits; i += 5) {
            Assert.assertEquals(i, semaphore.availablePermits());
            semaphore.release(5);
        }
        Assert.assertEquals(semaphore.availablePermits(), numberOfPermits);
    }

    @Test
    public void testMultipleRelease_whenNegative() {
        semaphore.init(0);
        try {
            semaphore.release((-5));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test
    public void testMultipleRelease_whenBlockedAcquireThreads() throws Exception {
        int permits = 10;
        semaphore.init(permits);
        semaphore.acquire(permits);
        Future future = HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        semaphore.release();
        future.get();
    }

    @Test
    public void testDrain() throws InterruptedException {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        semaphore.acquire(5);
        int drainedPermits = semaphore.drainPermits();
        Assert.assertEquals(drainedPermits, (numberOfPermits - 5));
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test
    public void testDrain_whenNoPermits() {
        semaphore.init(0);
        Assert.assertEquals(0, semaphore.drainPermits());
    }

    @Test
    public void testReduce() {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i += 5) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            semaphore.reducePermits(5);
        }
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test
    public void testReduce_whenArgumentNegative() {
        try {
            semaphore.reducePermits((-5));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test
    public void testIncrease_whenArgumentNegative() {
        try {
            semaphore.increasePermits((-5));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test
    public void testTryAcquire() {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i++) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            Assert.assertTrue(semaphore.tryAcquire());
        }
        Assert.assertFalse(semaphore.tryAcquire());
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test
    public void testTryAcquireMultiple() {
        int numberOfPermits = 20;
        Assert.assertTrue(semaphore.init(numberOfPermits));
        for (int i = 0; i < numberOfPermits; i += 5) {
            Assert.assertEquals((numberOfPermits - i), semaphore.availablePermits());
            Assert.assertTrue(semaphore.tryAcquire(5));
        }
        Assert.assertEquals(semaphore.availablePermits(), 0);
    }

    @Test
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

    @Test
    public void testTryAcquire_whenNotEnoughPermits() throws InterruptedException {
        int numberOfPermits = 10;
        semaphore.init(numberOfPermits);
        semaphore.acquire(10);
        boolean result = semaphore.tryAcquire(1);
        Assert.assertFalse(result);
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test
    public void testInit_whenNotInitialized() {
        boolean result = semaphore.init(2);
        Assert.assertTrue(result);
        Assert.assertEquals(2, semaphore.availablePermits());
    }

    @Test
    public void testInit_whenAlreadyInitialized() {
        semaphore.init(2);
        boolean result = semaphore.init(4);
        Assert.assertFalse(result);
        Assert.assertEquals(2, semaphore.availablePermits());
    }

    @Test
    public void testIncreasePermits_notifiesPendingAcquires() {
        semaphore.init(1);
        final CountDownLatch latch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.tryAcquire(2, 10, TimeUnit.MINUTES);
                    latch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                CPGroupId groupId = getGroupId(semaphore);
                HazelcastInstance leader = getLeaderInstance(instances, groupId);
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
            }
        });
        semaphore.increasePermits(1);
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test(expected = DistributedObjectDestroyedException.class)
    public void test_destroy() {
        semaphore.destroy();
        semaphore.init(1);
    }
}

