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
package com.hazelcast.client.map;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMapLockTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    @Test(expected = NullPointerException.class)
    public void testisLocked_whenKeyNull_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        map.isLocked(null);
    }

    @Test
    public void testisLocked_whenKeyAbsent_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        boolean isLocked = map.isLocked("NOT_THERE");
        Assert.assertFalse(isLocked);
    }

    @Test
    public void testisLocked_whenKeyPresent_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final Object key = "key";
        map.put(key, "value");
        final boolean isLocked = map.isLocked(key);
        Assert.assertFalse(isLocked);
    }

    @Test(expected = NullPointerException.class)
    public void testLock_whenKeyNull_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        map.lock(null);
    }

    @Test
    public void testLock_whenKeyAbsent_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final Object key = "key";
        map.lock(key);
        Assert.assertTrue(map.isLocked(key));
    }

    @Test
    public void testLock_whenKeyPresent_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final Object key = "key";
        map.put(key, "value");
        map.lock(key);
        Assert.assertTrue(map.isLocked(key));
    }

    @Test
    public void testLock_whenLockedRepeatedly_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final Object key = "key";
        map.lock(key);
        map.lock(key);
        Assert.assertTrue(map.isLocked(key));
    }

    @Test(expected = NullPointerException.class)
    public void testUnLock_whenKeyNull_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        map.unlock(null);
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testUnLock_whenKeyNotPresentAndNotLocked_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        map.unlock("NOT_THERE_OR_LOCKED");
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testUnLock_whenKeyPresentAndNotLocked_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        map.put(key, "value");
        map.unlock(key);
    }

    @Test
    public void testUnLock_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        map.lock(key);
        map.unlock(key);
        Assert.assertFalse(map.isLocked(key));
    }

    @Test
    public void testUnLock_whenKeyLockedRepeatedly_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        map.lock(key);
        map.lock(key);
        map.unlock(key);
        Assert.assertTrue(map.isLocked(key));
    }

    @Test(expected = NullPointerException.class)
    public void testForceUnlock_whenKeyNull_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        map.forceUnlock(null);
    }

    @Test
    public void testForceUnlock_whenKeyNotPresentAndNotLocked_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        map.forceUnlock("NOT_THERE_OR_LOCKED");
    }

    @Test
    public void testForceUnlock_whenKeyPresentAndNotLocked_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        map.put(key, "value");
        map.forceUnlock(key);
    }

    @Test
    public void testForceUnlock_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        map.lock(key);
        map.forceUnlock(key);
        Assert.assertFalse(map.isLocked(key));
    }

    @Test
    public void testForceUnLock_whenKeyLockedRepeatedly_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        map.lock(key);
        map.lock(key);
        map.unlock(key);
        Assert.assertTrue(map.isLocked(key));
    }

    @Test
    public void testLockAbsentKey_thenPutKey_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String value = "value";
        map.lock(key);
        map.put(key, value);
        map.unlock(key);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testLockAbsentKey_thenPutKeyIfAbsent_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String value = "value";
        map.lock(key);
        map.putIfAbsent(key, value);
        map.unlock(key);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testLockPresentKey_thenPutKey_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String oldValue = "oldValue";
        final String newValue = "newValue";
        map.put(key, oldValue);
        map.lock(key);
        map.put(key, newValue);
        map.unlock(key);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testLockPresentKey_thenSetKey_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String oldValue = "oldValue";
        final String newValue = "newValue";
        map.put(key, oldValue);
        map.lock(key);
        map.set(key, newValue);
        map.unlock(key);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testLockPresentKey_thenReplace_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String oldValue = "oldValue";
        final String newValue = "newValue";
        map.put(key, oldValue);
        map.lock(key);
        map.replace(key, newValue);
        map.unlock(key);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testLockPresentKey_thenRemoveKey_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String oldValue = "oldValue";
        map.put(key, oldValue);
        map.lock(key);
        map.remove(key);
        map.unlock(key);
        Assert.assertFalse(map.isLocked(key));
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testLockPresentKey_thenDeleteKey_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String oldValue = "oldValue";
        map.put(key, oldValue);
        map.lock(key);
        map.delete(key);
        map.unlock(key);
        Assert.assertFalse(map.isLocked(key));
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testLockPresentKey_thenEvictKey_fromSameThread() {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String oldValue = "oldValue";
        map.put(key, oldValue);
        map.lock(key);
        map.evict(key);
        map.unlock(key);
        Assert.assertFalse(map.isLocked(key));
        Assert.assertNull(map.get(key));
    }

    @Test
    public void testLockKey_thenPutAndCheckKeySet_fromOtherThread() throws InterruptedException {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String value = "oldValue";
        final CountDownLatch putWhileLocked = new CountDownLatch(1);
        final CountDownLatch checkingKeySet = new CountDownLatch(1);
        new Thread() {
            public void run() {
                try {
                    map.lock(key);
                    map.put(key, value);
                    putWhileLocked.countDown();
                    checkingKeySet.await();
                    map.unlock(key);
                } catch (Exception e) {
                }
            }
        }.start();
        putWhileLocked.await();
        Set keySet = map.keySet();
        Assert.assertFalse(keySet.isEmpty());
        checkingKeySet.countDown();
    }

    @Test
    public void testLockKey_thenPutAndGet_fromOtherThread() throws InterruptedException {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String value = "oldValue";
        final CountDownLatch putWhileLocked = new CountDownLatch(1);
        final CountDownLatch checkingKeySet = new CountDownLatch(1);
        new Thread() {
            public void run() {
                try {
                    map.lock(key);
                    map.put(key, value);
                    putWhileLocked.countDown();
                    checkingKeySet.await();
                    map.unlock(key);
                } catch (Exception e) {
                }
            }
        }.start();
        putWhileLocked.await();
        Assert.assertEquals(value, map.get(key));
        checkingKeySet.countDown();
    }

    @Test
    public void testLockKey_thenRemoveAndGet_fromOtherThread() throws InterruptedException {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String value = "oldValue";
        final CountDownLatch removeWhileLocked = new CountDownLatch(1);
        final CountDownLatch checkingKey = new CountDownLatch(1);
        map.put(key, value);
        new Thread() {
            public void run() {
                try {
                    map.lock(key);
                    map.remove(key);
                    removeWhileLocked.countDown();
                    checkingKey.await();
                    map.unlock(key);
                } catch (Exception e) {
                }
            }
        }.start();
        removeWhileLocked.await();
        Assert.assertNull(map.get(key));
        checkingKey.countDown();
    }

    @Test
    public void testLockKey_thenTryPutOnKey() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String value = "value";
        map.put(key, value);
        map.lock(key);
        final CountDownLatch tryPutReturned = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.tryPut(key, "NEW_VALUE", 1, TimeUnit.SECONDS);
                tryPutReturned.countDown();
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(tryPutReturned);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testLockTTLExpires_usingIsLocked() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        map.lock(key, 2, TimeUnit.SECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(map.isLocked(key));
            }
        }, 10);
    }

    @Test
    public void testLockTTLExpires() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String oldValue = "value";
        final String newValue = "NEW_VALUE";
        map.put(key, oldValue);
        map.lock(key, 1, TimeUnit.SECONDS);
        final CountDownLatch tryPutReturned = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.tryPut(key, newValue, 60, TimeUnit.SECONDS);
                tryPutReturned.countDown();
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(tryPutReturned);
        Assert.assertEquals(newValue, map.get(key));
    }

    @Test
    public void testLockTTLExpires_onAbsentKey() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final String value = "value";
        map.lock(key, 1, TimeUnit.SECONDS);
        final CountDownLatch tryPutReturned = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.tryPut(key, value, 60, TimeUnit.SECONDS);
                tryPutReturned.countDown();
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(tryPutReturned);
        Assert.assertEquals(value, map.get(key));
    }

    @Test
    public void testisLocked_whenLockedFromOtherThread() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final CountDownLatch lockedLatch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.lock(key);
                lockedLatch.countDown();
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(lockedLatch);
        Assert.assertTrue(map.isLocked(key));
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testUnLocked_whenLockedFromOtherThread() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final CountDownLatch lockedLatch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.lock(key);
                lockedLatch.countDown();
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(lockedLatch);
        map.unlock(key);
    }

    @Test
    public void testForceUnLocked_whenLockedFromOtherThread() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final CountDownLatch lockedLatch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.lock(key);
                map.lock(key);
                lockedLatch.countDown();
            }
        }.start();
        lockedLatch.await(10, TimeUnit.SECONDS);
        map.forceUnlock(key);
        Assert.assertFalse(map.isLocked(key));
    }

    @Test
    public void testTryPut_whenLockedFromOtherThread() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final CountDownLatch lockedLatch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.lock(key);
                lockedLatch.countDown();
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(lockedLatch);
        Assert.assertFalse(map.tryPut(key, "value", 1, TimeUnit.SECONDS));
    }

    @Test
    public void testTryRemove_whenLockedFromOtherThread() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final CountDownLatch lockedLatch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.lock(key);
                lockedLatch.countDown();
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(lockedLatch);
        Assert.assertFalse(map.tryRemove(key, 1, TimeUnit.SECONDS));
    }

    @Test
    public void testTryLock_whenLockedFromOtherThread() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        final CountDownLatch lockedLatch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                map.lock(key);
                lockedLatch.countDown();
            }
        }.start();
        HazelcastTestSupport.assertOpenEventually(lockedLatch);
        Assert.assertFalse(map.tryLock(key));
    }

    @Test
    public void testLock_whenUnLockedFromOtherThread() throws Exception {
        final IMap map = client.getMap(HazelcastTestSupport.randomString());
        final String key = "key";
        map.lock(key);
        final CountDownLatch beforeLock = new CountDownLatch(1);
        final CountDownLatch afterLock = new CountDownLatch(1);
        new Thread() {
            public void run() {
                beforeLock.countDown();
                map.lock(key);
                afterLock.countDown();
            }
        }.start();
        beforeLock.await();
        map.unlock(key);
        HazelcastTestSupport.assertOpenEventually(afterLock);
    }

    @Test(timeout = 60000)
    public void testTryLockLeaseTime_whenLockFree() throws InterruptedException {
        IMap map = getMapForLock();
        String key = HazelcastTestSupport.randomString();
        boolean isLocked = map.tryLock(key, 1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(isLocked);
    }

    @Test(timeout = 60000)
    public void testTryLockLeaseTime_whenLockAcquiredByOther() throws InterruptedException {
        final IMap map = getMapForLock();
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
        final IMap map = getMapForLock();
        final String key = HazelcastTestSupport.randomString();
        map.tryLock(key, 1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(map.isLocked(key));
            }
        }, 30);
    }

    @Test
    public void testExecuteOnKeyWhenLock() throws InterruptedException {
        final IMap map = getMapForLock();
        final String key = HazelcastTestSupport.randomString();
        map.lock(key);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                String payload = HazelcastTestSupport.randomString();
                Object ret = map.executeOnKey(key, new ClientMapLockTest.LockEntryProcessor(payload));
                Assert.assertEquals(payload, ret);
            }
        }, 30);
        map.unlock(key);
    }

    private static class LockEntryProcessor implements EntryProcessor<Object, Object> , Serializable {
        public final String payload;

        public LockEntryProcessor(String payload) {
            this.payload = payload;
        }

        @Override
        public Object process(Map.Entry entry) {
            return payload;
        }

        @Override
        public EntryBackupProcessor getBackupProcessor() {
            return null;
        }
    }
}

