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
package com.hazelcast.cp.internal.datastructures.countdownlatch;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestThread;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftCountDownLatchBasicTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    private ICountDownLatch latch;

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProxyOnMetadataCPGroup() {
        instances[0].getCPSubsystem().getCountDownLatch("latch@metadata");
    }

    // ================= trySetCount =================================================
    @Test(expected = IllegalArgumentException.class)
    public void testTrySetCount_whenArgumentNegative() {
        latch.trySetCount((-20));
    }

    @Test
    public void testTrySetCount_whenCountIsZero() {
        Assert.assertTrue(latch.trySetCount(40));
        Assert.assertEquals(40, latch.getCount());
    }

    @Test
    public void testTrySetCount_whenCountIsNotZero() {
        latch.trySetCount(10);
        Assert.assertFalse(latch.trySetCount(20));
        Assert.assertFalse(latch.trySetCount(0));
        Assert.assertEquals(10, latch.getCount());
    }

    @Test
    public void testTrySetCount_whenPositive() {
        latch.trySetCount(10);
        Assert.assertFalse(latch.trySetCount(20));
        Assert.assertEquals(10, latch.getCount());
    }

    @Test
    public void testTrySetCount_whenAlreadySet() {
        latch.trySetCount(10);
        Assert.assertFalse(latch.trySetCount(20));
        Assert.assertFalse(latch.trySetCount(100));
        Assert.assertFalse(latch.trySetCount(0));
        Assert.assertEquals(10, latch.getCount());
    }

    // ================= countDown =================================================
    @Test
    public void testCountDown() {
        latch.trySetCount(20);
        for (int i = 19; i >= 0; i--) {
            latch.countDown();
            Assert.assertEquals(i, latch.getCount());
        }
        latch.countDown();
        Assert.assertEquals(0, latch.getCount());
    }

    // ================= getCount =================================================
    @Test
    public void testGetCount() {
        latch.trySetCount(20);
        Assert.assertEquals(20, latch.getCount());
    }

    // ================= destroy =================================================
    // ================= await =================================================
    @Test(expected = NullPointerException.class)
    public void testAwait_whenNullUnit() throws InterruptedException {
        latch.await(1, null);
    }

    @Test
    public void testAwait() {
        Assert.assertTrue(latch.trySetCount(1));
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        });
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void testAwait_withManyThreads() {
        final CountDownLatch completedLatch = new CountDownLatch(10);
        latch.trySetCount(1);
        for (int i = 0; i < 10; i++) {
            new TestThread() {
                public void doRun() throws Exception {
                    if (latch.await(1, TimeUnit.MINUTES)) {
                        completedLatch.countDown();
                    }
                }
            }.start();
        }
        latch.countDown();
        HazelcastTestSupport.assertOpenEventually(completedLatch);
    }

    @Test
    public void testAwait_whenTimeOut() throws InterruptedException {
        latch.trySetCount(1);
        long time = System.currentTimeMillis();
        Assert.assertFalse(latch.await(100, TimeUnit.MILLISECONDS));
        long elapsed = (System.currentTimeMillis()) - time;
        Assert.assertTrue((elapsed >= 100));
        Assert.assertEquals(1, latch.getCount());
    }

    @Test(expected = DistributedObjectDestroyedException.class)
    public void testCountDown_afterDestroy() {
        latch.destroy();
        latch.countDown();
    }
}

