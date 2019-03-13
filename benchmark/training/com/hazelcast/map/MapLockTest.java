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
package com.hazelcast.map;


import LockService.SERVICE_NAME;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.concurrent.lock.LockStoreContainer;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapLockTest extends HazelcastTestSupport {
    @Test
    public void testIsLocked_afterDestroy() {
        final IMap<String, String> map = getMap();
        final String key = HazelcastTestSupport.randomString();
        map.lock(key);
        map.destroy();
        Assert.assertFalse(map.isLocked(key));
    }

    @Test
    public void testIsLocked_afterDestroy_whenMapContainsKey() {
        final IMap<String, String> map = getMap();
        final String key = HazelcastTestSupport.randomString();
        map.put(key, "value");
        map.lock(key);
        map.destroy();
        Assert.assertFalse(map.isLocked(key));
    }

    @Test
    public void testBackupDies() throws TransactionException {
        Config config = getConfig();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = factory.newHazelcastInstance(config);
        final HazelcastInstance h2 = factory.newHazelcastInstance(config);
        final IMap<Integer, Integer> map1 = h1.getMap("testBackupDies");
        final int size = 50;
        final CountDownLatch latch = new CountDownLatch(1);
        Runnable runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < size; i++) {
                    map1.lock(i);
                    HazelcastTestSupport.sleepMillis(100);
                }
                for (int i = 0; i < size; i++) {
                    Assert.assertTrue(map1.isLocked(i));
                }
                for (int i = 0; i < size; i++) {
                    map1.unlock(i);
                }
                for (int i = 0; i < size; i++) {
                    Assert.assertFalse(map1.isLocked(i));
                }
                latch.countDown();
            }
        };
        new Thread(runnable).start();
        try {
            HazelcastTestSupport.sleepSeconds(1);
            h2.shutdown();
            Assert.assertTrue(latch.await(30, TimeUnit.SECONDS));
            for (int i = 0; i < size; i++) {
                Assert.assertFalse(map1.isLocked(i));
            }
        } catch (InterruptedException ignored) {
        }
    }

    @Test
    public void testLockEviction() throws Exception {
        final String name = HazelcastTestSupport.randomString();
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        final Config config = getConfig();
        final HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        final HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(instance2, instance1);
        final IMap<Integer, Integer> map = instance1.getMap(name);
        map.put(1, 1);
        map.lock(1, 1, TimeUnit.SECONDS);
        Assert.assertTrue(map.isLocked(1));
        final CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                map.lock(1);
                latch.countDown();
            }
        });
        t.start();
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLockTTL_whenZeroTimeout() throws Exception {
        final IMap<String, String> mm = getMap();
        final String key = "Key";
        mm.lock(key, 0, TimeUnit.SECONDS);
    }

    @Test
    public void testLockEviction2() throws Exception {
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        final Config config = getConfig();
        final HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        final HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(instance2, instance1);
        final String name = HazelcastTestSupport.randomString();
        final IMap<Integer, Integer> map = instance1.getMap(name);
        Random rand = new Random();
        for (int i = 0; i < 5; i++) {
            map.lock(i, ((rand.nextInt(5)) + 1), TimeUnit.SECONDS);
        }
        final CountDownLatch latch = new CountDownLatch(5);
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 5; i++) {
                    map.lock(i);
                    latch.countDown();
                }
            }
        });
        t.start();
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testLockMigration() throws Exception {
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        final Config config = getConfig();
        final HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        final String name = HazelcastTestSupport.randomString();
        final IMap<Object, Object> map = instance1.getMap(name);
        for (int i = 0; i < 1000; i++) {
            map.lock(i);
        }
        final HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        final HazelcastInstance instance3 = nodeFactory.newHazelcastInstance(config);
        Thread.sleep(3000);
        final CountDownLatch latch = new CountDownLatch(1000);
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    if (map.isLocked(i)) {
                        latch.countDown();
                    }
                }
            }
        });
        t.start();
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Category(NightlyTest.class)
    @Test
    public void testLockEvictionWithMigration() throws Exception {
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        final Config config = getConfig();
        final HazelcastInstance instance1 = nodeFactory.newHazelcastInstance(config);
        final String name = HazelcastTestSupport.randomString();
        final IMap<Integer, Object> map = instance1.getMap(name);
        for (int i = 0; i < 1000; i++) {
            map.lock(i, 20, TimeUnit.SECONDS);
        }
        final HazelcastInstance instance2 = nodeFactory.newHazelcastInstance(config);
        final HazelcastInstance instance3 = nodeFactory.newHazelcastInstance(config);
        for (int i = 0; i < 1000; i++) {
            Assert.assertTrue(map.isLocked(i));
        }
        final CountDownLatch latch = new CountDownLatch(1000);
        Thread t = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    map.lock(i);
                    latch.countDown();
                }
            }
        });
        t.start();
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testLockOwnership() throws Exception {
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        final Config config = getConfig();
        String name = HazelcastTestSupport.randomString();
        final HazelcastInstance node1 = nodeFactory.newHazelcastInstance(config);
        final HazelcastInstance node2 = nodeFactory.newHazelcastInstance(config);
        final IMap<Integer, Object> map1 = node1.getMap(name);
        final IMap<Integer, Object> map2 = node2.getMap(name);
        map1.lock(1);
        map2.unlock(1);
    }

    @Test
    public void testAbsentKeyIsLocked() throws Exception {
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        Config config = getConfig();
        final String name = HazelcastTestSupport.randomString();
        final String key = "KEY";
        final String val = "VAL_2";
        final HazelcastInstance node1 = nodeFactory.newHazelcastInstance(config);
        final HazelcastInstance node2 = nodeFactory.newHazelcastInstance(config);
        final IMap<String, String> map1 = node1.getMap(name);
        final IMap<String, String> map2 = node2.getMap(name);
        map1.lock(key);
        boolean putResult = map2.tryPut(key, val, 2, TimeUnit.SECONDS);
        Assert.assertFalse("the result of try put should be false as the absent key is locked", putResult);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                Assert.assertEquals("the key should be absent ", null, map1.get(key));
                Assert.assertEquals("the key should be absent ", null, map2.get(key));
            }
        });
    }

    @Test
    public void testLockTTLKey() {
        final IMap<String, String> map = getMap();
        final String key = "key";
        final String val = "val";
        final int TTL_SEC = 1;
        map.put(key, val, TTL_SEC, TimeUnit.SECONDS);
        map.lock(key);
        HazelcastTestSupport.sleepSeconds((TTL_SEC * 2));
        Assert.assertEquals("TTL of KEY has expired, KEY is locked, we expect VAL", val, map.get(key));
        map.unlock(key);
        Assert.assertEquals("TTL of KEY has expired, KEY is unlocked, we expect null", null, map.get(key));
    }

    @Test
    public void testClear_withLockedKey() {
        final IMap<String, String> map = getMap();
        final String KEY = "key";
        final String VAL = "val";
        map.put(KEY, VAL);
        map.lock(KEY);
        map.clear();
        Assert.assertEquals("a locked key should not be removed by map clear", false, map.isEmpty());
        Assert.assertEquals("a key present in a map, should be locked after map clear", true, map.isLocked(KEY));
    }

    /**
     * Do not use ungraceful node.getLifecycleService().terminate(), because that leads
     * backup inconsistencies between nodes and eventually this test will fail.
     * Instead use graceful node.getLifecycleService().shutdown().
     */
    @Test
    public void testClear_withLockedKey_whenNodeShutdown() {
        Config config = getConfig();
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        final HazelcastInstance node1 = nodeFactory.newHazelcastInstance(config);
        final HazelcastInstance node2 = nodeFactory.newHazelcastInstance(config);
        final String mapName = HazelcastTestSupport.randomString();
        final IMap<Object, Object> map = node2.getMap(mapName);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i);
        }
        String key = HazelcastTestSupport.generateKeyOwnedBy(node2);
        map.put(key, "value");
        map.lock(key);
        final CountDownLatch cleared = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.clear();
                cleared.countDown();
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(cleared);
        node1.getLifecycleService().shutdown();
        Assert.assertTrue("a key present in a map, should be locked after map clear", map.isLocked(key));
        Assert.assertEquals("unlocked keys not removed", 1, map.size());
    }

    @Test
    public void testTryLockLeaseTime_whenLockFree() throws InterruptedException {
        IMap<String, String> map = getMap();
        String key = HazelcastTestSupport.randomString();
        boolean isLocked = map.tryLock(key, 1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(isLocked);
    }

    @Test
    public void testTryLockLeaseTime_whenLockAcquiredByOther() throws InterruptedException {
        final IMap<String, String> map = getMap();
        final String key = HazelcastTestSupport.randomString();
        Thread thread = new Thread() {
            public void run() {
                map.lock(key);
            }
        };
        thread.start();
        thread.join();
        boolean isLocked = map.tryLock(key, 1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        Assert.assertFalse(isLocked);
    }

    @Test
    public void testTryLockLeaseTime_lockIsReleasedEventually() throws InterruptedException {
        final IMap<String, String> map = getMap();
        final String key = HazelcastTestSupport.randomString();
        map.tryLock(key, 1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(map.isLocked(key));
            }
        }, 30);
    }

    /**
     * See issue #4888
     */
    @Test
    public void lockStoreShouldBeRemoved_whenMapIsDestroyed() {
        HazelcastInstance instance = createHazelcastInstance(getConfig());
        IMap<Integer, Integer> map = instance.getMap(HazelcastTestSupport.randomName());
        for (int i = 0; i < 1000; i++) {
            map.lock(i);
        }
        map.destroy();
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(instance);
        LockServiceImpl lockService = nodeEngine.getService(SERVICE_NAME);
        int partitionCount = nodeEngine.getPartitionService().getPartitionCount();
        for (int i = 0; i < partitionCount; i++) {
            LockStoreContainer lockContainer = lockService.getLockContainer(i);
            Assert.assertEquals("LockStores should be empty", 0, lockContainer.getLockStores().size());
        }
    }
}

