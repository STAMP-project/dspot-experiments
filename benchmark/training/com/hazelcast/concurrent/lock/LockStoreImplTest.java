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
package com.hazelcast.concurrent.lock;


import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.DistributedObjectNamespace;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LockStoreImplTest extends HazelcastTestSupport {
    private static final ObjectNamespace OBJECT_NAME_SPACE = new DistributedObjectNamespace("service", "object");

    private static final int BACKUP_COUNT = 0;

    private static final int ASYNC_BACKUP_COUNT = 0;

    private LockService mockLockServiceImpl;

    private LockStoreImpl lockStore;

    private Data key = new HeapData();

    private String callerId = "called";

    private long threadId = 1;

    private long referenceId = 1;

    private long leaseTime = Long.MAX_VALUE;

    @Test
    public void testLock_whenUnlocked_thenReturnTrue() {
        boolean isLocked = lockAndIncreaseReferenceId();
        Assert.assertTrue(isLocked);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLock_whenMaximumLeaseTimeExceeded_thenThrowException() {
        Mockito.when(mockLockServiceImpl.getMaxLeaseTimeInMillis()).thenReturn(1L);
        lockAndIncreaseReferenceId();
    }

    @Test
    public void testLock_whenLockedBySameThread_thenReturnTrue() {
        lockAndIncreaseReferenceId();
        boolean isLocked = lockAndIncreaseReferenceId();
        Assert.assertTrue(isLocked);
    }

    @Test
    public void testLock_whenLockedByDifferentThread_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        (threadId)++;
        boolean isLocked = lockAndIncreaseReferenceId();
        Assert.assertFalse(isLocked);
    }

    @Test
    public void testGetRemainingLeaseTime_whenLockDoesNotExist_thenReturnNegativeOne() {
        long remainingLeaseTime = lockStore.getRemainingLeaseTime(key);
        Assert.assertEquals((-1), remainingLeaseTime);
    }

    @Test
    public void testGetRemainingLeaseTime_whenUnlocked_thenReturnNegativeOne() {
        lockAndIncreaseReferenceId();
        unlockAndIncreaseReferenceId();
        long remainingLeaseTime = lockStore.getRemainingLeaseTime(key);
        Assert.assertEquals((-1), remainingLeaseTime);
    }

    @Test
    public void testGetRemainingLeaseTime_whenLocked_thenReturnLeaseTime() {
        leaseTime = (Long.MAX_VALUE) / 2;
        lockAndIncreaseReferenceId();
        long remainingLeaseTime = lockStore.getRemainingLeaseTime(key);
        Assert.assertThat(remainingLeaseTime, Matchers.lessThanOrEqualTo(leaseTime));
        Assert.assertThat(remainingLeaseTime, Matchers.greaterThan(0L));
    }

    @Test
    public void testGetVersion_whenLockDoesNotExist_thenReturnNegativeOne() {
        int version = lockStore.getVersion(key);
        Assert.assertEquals((-1), version);
    }

    @Test
    public void testGetVersion_whenUnlocked_thenReturnNegativeOne() {
        lockAndIncreaseReferenceId();
        unlockAndIncreaseReferenceId();
        int version = lockStore.getVersion(key);
        Assert.assertEquals((-1), version);
    }

    @Test
    public void testGetVersion_whenLockedOnce_thenReturnPositiveOne() {
        lockAndIncreaseReferenceId();
        int version = lockStore.getVersion(key);
        Assert.assertEquals(1, version);
    }

    @Test
    public void testGetVersion_whenLockedTwice_thenReturnPositiveTwo() {
        lockAndIncreaseReferenceId();
        lockAndIncreaseReferenceId();
        int version = lockStore.getVersion(key);
        Assert.assertEquals(2, version);
    }

    @Test
    public void testIsLockedBy_whenLockDoesNotExist_thenReturnFalse() {
        boolean lockedBy = lockStore.isLockedBy(key, callerId, threadId);
        Assert.assertFalse(lockedBy);
    }

    @Test
    public void testIsLockedBy_whenLockedBySameCallerAndSameThread_thenReturnTrue() {
        lockAndIncreaseReferenceId();
        boolean lockedBy = lockStore.isLockedBy(key, callerId, threadId);
        Assert.assertTrue(lockedBy);
    }

    @Test
    public void testIsLockedBy_whenLockedBySameCallerAndDifferentThread_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        long differentThreadId = (threadId) + 1;
        boolean lockedBy = lockStore.isLockedBy(key, callerId, differentThreadId);
        Assert.assertFalse(lockedBy);
    }

    @Test
    public void testIsLockedBy_whenLockedByDifferentCallerAndSameThread_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        String differentCaller = (callerId) + "different";
        boolean lockedBy = lockStore.isLockedBy(key, differentCaller, threadId);
        Assert.assertFalse(lockedBy);
    }

    @Test
    public void testIsLockedBy_whenLockedByDifferentCallerAndDifferentThread_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        long differentThreadId = (threadId) + 1;
        String differentCaller = (callerId) + "different";
        boolean lockedBy = lockStore.isLockedBy(key, differentCaller, differentThreadId);
        Assert.assertFalse(lockedBy);
    }

    @Test
    public void testIsLocked_whenLockDoesNotExist_thenReturnFalse() {
        boolean locked = lockStore.isLocked(key);
        Assert.assertFalse(locked);
    }

    @Test
    public void testIsLocked_whenLockedAndUnlocked_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        unlockAndIncreaseReferenceId();
        boolean locked = lockStore.isLocked(key);
        Assert.assertFalse(locked);
    }

    @Test
    public void testIsLocked_whenLocked_thenReturnTrue() {
        lockAndIncreaseReferenceId();
        boolean locked = lockStore.isLocked(key);
        Assert.assertTrue(locked);
    }

    @Test
    public void testCanAcquireLock_whenLockDoesNotExist_thenReturnTrue() {
        boolean canAcquire = lockStore.canAcquireLock(key, callerId, threadId);
        Assert.assertTrue(canAcquire);
    }

    @Test
    public void testCanAcquireLock_whenLockedBySameThreadAndSameCaller_thenReturnTrue() {
        lockAndIncreaseReferenceId();
        boolean canAcquire = lockStore.canAcquireLock(key, callerId, threadId);
        Assert.assertTrue(canAcquire);
    }

    @Test
    public void testCanAquireLock_whenLockedBySameThreadAndDifferentCaller_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        String differentCaller = (callerId) + "different";
        boolean canAcquire = lockStore.canAcquireLock(key, differentCaller, threadId);
        Assert.assertFalse(canAcquire);
    }

    @Test
    public void testCanAcquireLock_whenLockedByDifferentThreadAndSameCaller_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        long differentThreadId = (threadId) + 1;
        boolean canAcquire = lockStore.canAcquireLock(key, callerId, differentThreadId);
        Assert.assertFalse(canAcquire);
    }

    @Test
    public void testCanAcquireLock_whenLockedByDifferentThreadAndDifferentCaller_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        long differentThreadId = (threadId) + 1;
        String differentCaller = (callerId) + "different";
        boolean canAcquire = lockStore.canAcquireLock(key, differentCaller, differentThreadId);
        Assert.assertFalse(canAcquire);
    }

    @Test
    public void testCanAcquireLock_whenLockedAndUnlocked_thenReturnTrue() {
        lockAndIncreaseReferenceId();
        unlockAndIncreaseReferenceId();
        long differentThreadId = (threadId) + 1;
        String differentCaller = (callerId) + "different";
        boolean canAcquire = lockStore.canAcquireLock(key, differentCaller, differentThreadId);
        Assert.assertTrue(canAcquire);
    }

    @Test
    public void testForceUnlock_whenLockDoesNotExists_thenReturnFalse() {
        boolean unlocked = lockStore.forceUnlock(key);
        Assert.assertFalse(unlocked);
    }

    @Test
    public void testForceUnlock_whenLocked_thenReturnTrue() {
        lockAndIncreaseReferenceId();
        boolean unlocked = lockStore.forceUnlock(key);
        Assert.assertTrue(unlocked);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetLocks_returnUnmodifiableCollection() {
        Collection<LockResource> locks = lockStore.getLocks();
        locks.clear();
    }

    @Test
    public void testGetLocks_whenNoLockExist_thenReturnEmptyCollection() {
        Collection<LockResource> locks = lockStore.getLocks();
        Assert.assertThat(locks, Matchers.is(Matchers.empty()));
    }

    @Test
    public void testGetLocks_whenLocked_thenReturnCollectionWithSingleItem() {
        lockAndIncreaseReferenceId();
        Collection<LockResource> locks = lockStore.getLocks();
        Assert.assertThat(locks, Matchers.hasSize(1));
    }

    @Test
    public void testGetLockedEntryCount() {
        lock();
        Assert.assertEquals(1, lockStore.getLockedEntryCount());
    }

    @Test
    public void testGetLockCount_whenLockDoesNotExist_thenReturnZero() {
        int lockCount = lockStore.getLockCount(key);
        Assert.assertThat(lockCount, Matchers.is(0));
    }

    @Test
    public void testGetLockCount_whenLockedOnce_thenReturnOne() {
        lockAndIncreaseReferenceId();
        int lockCount = lockStore.getLockCount(key);
        Assert.assertThat(lockCount, Matchers.is(1));
    }

    @Test
    public void testGetLockCount_whenLockedTwice_thenReturnTwo() {
        lockAndIncreaseReferenceId();
        lockAndIncreaseReferenceId();
        int lockCount = lockStore.getLockCount(key);
        Assert.assertThat(lockCount, Matchers.is(2));
    }

    @Test
    public void testGetLockCount_whenLockedTwiceWithTheSameReferenceId_thenReturnOne() {
        lock();
        lockAndIncreaseReferenceId();
        int lockCount = lockStore.getLockCount(key);
        Assert.assertThat(lockCount, Matchers.is(1));
    }

    @Test
    public void testUnlock_whenLockDoesNotExist_thenReturnFalse() {
        boolean unlocked = unlockAndIncreaseReferenceId();
        Assert.assertFalse(unlocked);
    }

    @Test
    public void testUnlock_whenLockedBySameCallerAndSameThreadId_thenReturnTrue() {
        lockAndIncreaseReferenceId();
        boolean unlocked = unlockAndIncreaseReferenceId();
        Assert.assertTrue(unlocked);
    }

    @Test
    public void testUnlock_whenLockedByDifferentCallerAndSameThreadId_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        callerId += "different";
        boolean unlocked = unlockAndIncreaseReferenceId();
        Assert.assertFalse(unlocked);
    }

    @Test
    public void testIsLocked_whenTxnLockedAndUnlockedWithSameReferenceId_thenReturnFalse() {
        // see https://github.com/hazelcast/hazelcast/issues/5923 for details
        txnLock();
        unlock();
        boolean locked = lockStore.isLocked(key);
        Assert.assertFalse(locked);
    }

    @Test
    public void testUnlock_whenLockedBySameCallerAndDifferentThreadId_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        (threadId)++;
        boolean unlocked = unlockAndIncreaseReferenceId();
        Assert.assertFalse(unlocked);
    }

    @Test
    public void testUnlock_whenLockedByDifferentCallerAndDifferentThreadId_thenReturnFalse() {
        lockAndIncreaseReferenceId();
        (threadId)++;
        callerId += "different";
        boolean unlocked = unlockAndIncreaseReferenceId();
        Assert.assertFalse(unlocked);
    }

    @Test
    public void testUnlock_whenLockedTwiceWithSameReferenceIdAndUnlockedOnce_thenReturnTrue() {
        lock();
        lockAndIncreaseReferenceId();
        boolean unlocked = unlockAndIncreaseReferenceId();
        Assert.assertTrue(unlocked);
    }

    @Test
    public void testTxnLock_whenLockDoesNotExist_thenResultTrue() {
        boolean locked = txnLockAndIncreaseReferenceId();
        Assert.assertTrue(locked);
    }

    @Test
    public void testTxnLock_whenLockedByDifferentCallerAndSameThreadId_thenReturnFalse() {
        txnLockAndIncreaseReferenceId();
        callerId += "different";
        boolean locked = txnLockAndIncreaseReferenceId();
        Assert.assertFalse(locked);
    }

    @Test
    public void testTxnLock_whenLockedBySameCallerAndDifferentThreadId_thenReturnFalse() {
        txnLockAndIncreaseReferenceId();
        (threadId)++;
        boolean locked = txnLockAndIncreaseReferenceId();
        Assert.assertFalse(locked);
    }

    @Test
    public void testIsBlockReads_whenLockDoesNotExist_thenReturnFalse() {
        boolean locked = lockStore.shouldBlockReads(key);
        Assert.assertFalse(locked);
    }

    @Test
    public void testIsIsBlockReads_whenNonTxnLocked_thenReturnFalse() {
        lock();
        boolean locked = lockStore.shouldBlockReads(key);
        Assert.assertFalse(locked);
    }

    @Test
    public void testIsBlockReads_whenTxnLocked_thenReturnTrue() {
        txnLock();
        boolean locked = lockStore.shouldBlockReads(key);
        Assert.assertTrue(locked);
    }

    @Test
    public void testIsBlockReads_whenTxnLockedAndUnlocked_thenReturnFalse() {
        txnLockAndIncreaseReferenceId();
        unlock();
        boolean locked = lockStore.shouldBlockReads(key);
        Assert.assertFalse(locked);
    }

    @Test
    public void testIsBlockReads_whenTxnLockedAndAttemptedToLockFromAnotherThread_thenReturnTrue() {
        txnLockAndIncreaseReferenceId();
        (threadId)++;
        lockAndIncreaseReferenceId();
        boolean locked = lockStore.shouldBlockReads(key);
        Assert.assertTrue(locked);
    }
}

