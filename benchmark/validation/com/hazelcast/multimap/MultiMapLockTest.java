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
package com.hazelcast.multimap;


import LockService.SERVICE_NAME;
import MultiMapConfig.ValueCollectionType.LIST;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.concurrent.lock.LockStoreContainer;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MultiMapLockTest extends HazelcastTestSupport {
    @Test(expected = NullPointerException.class)
    public void testLock_whenNullKey() {
        MultiMap<String, Integer> multiMap = getMultiMapForLock();
        multiMap.lock(null);
    }

    @Test(expected = NullPointerException.class)
    public void testUnlock_whenNullKey() {
        MultiMap<String, Integer> multiMap = getMultiMapForLock();
        multiMap.unlock(null);
    }

    @Test(timeout = 60000)
    public void testTryLockLeaseTime_whenLockFree() throws InterruptedException {
        MultiMap<String, Integer> multiMap = getMultiMapForLock();
        String key = HazelcastTestSupport.randomString();
        boolean isLocked = multiMap.tryLock(key, 1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(isLocked);
    }

    @Test(timeout = 60000)
    public void testTryLockLeaseTime_whenLockAcquiredByOther() throws InterruptedException {
        final MultiMap<String, Integer> multiMap = getMultiMapForLock();
        final String key = HazelcastTestSupport.randomString();
        Thread thread = new Thread() {
            public void run() {
                multiMap.lock(key);
            }
        };
        thread.start();
        thread.join();
        boolean isLocked = multiMap.tryLock(key, 1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        Assert.assertFalse(isLocked);
    }

    @Test
    public void testTryLockLeaseTime_lockIsReleasedEventually() throws InterruptedException {
        final MultiMap<String, Integer> multiMap = getMultiMapForLock();
        final String key = HazelcastTestSupport.randomString();
        multiMap.tryLock(key, 1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(multiMap.isLocked(key));
            }
        }, 30);
    }

    @Test
    public void testLock() throws Exception {
        final String name = "defMM";
        Config config = new Config();
        config.getMultiMapConfig(name).setValueCollectionType(LIST);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        final HazelcastInstance[] instances = factory.newInstances(config);
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        new Thread() {
            public void run() {
                instances[0].getMultiMap(name).lock("alo");
                latch.countDown();
                try {
                    latch2.await(10, TimeUnit.SECONDS);
                    instances[0].getMultiMap(name).unlock("alo");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
        Assert.assertFalse(instances[0].getMultiMap(name).tryLock("alo"));
        latch2.countDown();
        Assert.assertTrue(instances[0].getMultiMap(name).tryLock("alo", 20, TimeUnit.SECONDS));
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                instances[0].shutdown();
            }
        }.start();
        Assert.assertTrue(instances[1].getMultiMap(name).tryLock("alo", 20, TimeUnit.SECONDS));
    }

    /**
     * See issue #4888
     */
    @Test
    public void lockStoreShouldBeRemoved_whenMultimapIsDestroyed() {
        HazelcastInstance hz = createHazelcastInstance();
        MultiMap<String, Integer> multiMap = hz.getMultiMap(HazelcastTestSupport.randomName());
        for (int i = 0; i < 1000; i++) {
            multiMap.lock(("" + i));
        }
        multiMap.destroy();
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(hz);
        LockServiceImpl lockService = nodeEngine.getService(SERVICE_NAME);
        int partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        for (int i = 0; i < partitionCount; i++) {
            LockStoreContainer lockContainer = lockService.getLockContainer(i);
            Collection<LockStoreImpl> lockStores = lockContainer.getLockStores();
            Assert.assertEquals(("LockStores should be empty: " + lockStores), 0, lockStores.size());
        }
    }
}

