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


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests LockStoreProxy when the internal LockStoreImpl has been cleared.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LockStoreProxyTest extends HazelcastTestSupport {
    private static final int PARTITION_ID = 1;

    private static final ObjectNamespace NAMESPACE = new com.hazelcast.spi.DistributedObjectNamespace(MapService.SERVICE_NAME, "test");

    private Data key = new HeapData();

    private String callerId = "called";

    private long threadId = 1;

    private long otherThreadId = 2;

    private long referenceId = 1;

    private long leaseTimeInfinite = Long.MAX_VALUE;

    private long leaseTimeShort = 60000L;

    private HazelcastInstance instance;

    private LockStoreProxy lockStoreProxy;

    @Test
    public void lock() {
        Assert.assertTrue(lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeInfinite));
        Assert.assertFalse(lockStoreProxy.canAcquireLock(key, callerId, otherThreadId));
    }

    @Test
    public void localLock() {
        Assert.assertTrue(lockStoreProxy.localLock(key, callerId, threadId, referenceId, leaseTimeInfinite));
        Assert.assertFalse(lockStoreProxy.canAcquireLock(key, callerId, otherThreadId));
    }

    @Test
    public void txnLock() {
        Assert.assertTrue(lockStoreProxy.txnLock(key, callerId, threadId, referenceId, leaseTimeInfinite, true));
        Assert.assertFalse(lockStoreProxy.canAcquireLock(key, callerId, otherThreadId));
    }

    @Test
    public void extendLeaseTime_whenLockStoreImplIsNull() {
        Assert.assertFalse(lockStoreProxy.extendLeaseTime(key, callerId, threadId, leaseTimeInfinite));
    }

    @Test
    public void extendLeaseTime_whenLockWasLocked() {
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeShort);
        Assert.assertTrue(lockStoreProxy.extendLeaseTime(key, callerId, threadId, leaseTimeInfinite));
    }

    @Test
    public void unlock_whenLockStoreImplIsNull() {
        Assert.assertFalse(lockStoreProxy.unlock(key, callerId, threadId, referenceId));
    }

    @Test
    public void unlock_whenLockWasLocked() {
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeShort);
        Assert.assertTrue(lockStoreProxy.unlock(key, callerId, threadId, referenceId));
    }

    @Test
    public void isLocked_whenLockStoreImplIsNull() {
        Assert.assertFalse(lockStoreProxy.isLocked(key));
    }

    @Test
    public void isLocked_whenLockWasLocked() {
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeShort);
        Assert.assertTrue(lockStoreProxy.isLocked(key));
    }

    @Test
    public void isLockedBy_whenLockStoreImplIsNull() {
        Assert.assertFalse(lockStoreProxy.isLockedBy(key, callerId, threadId));
    }

    @Test
    public void isLockedBy_whenLockWasLocked() {
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeShort);
        Assert.assertTrue(lockStoreProxy.isLockedBy(key, callerId, threadId));
    }

    @Test
    public void getLockCount() {
        Assert.assertEquals(0, lockStoreProxy.getLockCount(key));
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeShort);
        Assert.assertEquals(1, lockStoreProxy.getLockCount(key));
    }

    @Test
    public void getLockedEntryCount() {
        Assert.assertEquals(0, lockStoreProxy.getLockedEntryCount());
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeShort);
        Assert.assertEquals(1, lockStoreProxy.getLockedEntryCount());
    }

    @Test
    public void getRemainingLeaseTime_whenLockStoreImplIsNull() {
        Assert.assertEquals(0, lockStoreProxy.getRemainingLeaseTime(key));
    }

    @Test
    public void getRemainingLeaseTime_whenLockWasLocked() {
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeShort);
        Assert.assertTrue(((lockStoreProxy.getRemainingLeaseTime(key)) > 0));
    }

    @Test
    public void canAcquireLock_whenLockWasLocked() {
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeInfinite);
        Assert.assertFalse(lockStoreProxy.canAcquireLock(key, callerId, otherThreadId));
    }

    @Test
    public void canAcquireLock_whenLockStoreImplIsNull() {
        Assert.assertTrue(lockStoreProxy.canAcquireLock(key, callerId, threadId));
    }

    @Test
    public void shouldBlockReads_whenLockWasLocked() {
        lockStoreProxy.txnLock(key, callerId, threadId, referenceId, leaseTimeInfinite, true);
        Assert.assertTrue(lockStoreProxy.shouldBlockReads(key));
    }

    @Test
    public void shouldBlockReads_whenLockStoreImplIsNull() {
        Assert.assertFalse(lockStoreProxy.shouldBlockReads(key));
    }

    @Test
    public void getLockedKeys_whenLockWasLocked() {
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeInfinite);
        Assert.assertEquals(Collections.singleton(key), lockStoreProxy.getLockedKeys());
    }

    @Test
    public void getLockedKeys_whenLockStoreImplIsNull() {
        Assert.assertEquals(Collections.emptySet(), lockStoreProxy.getLockedKeys());
    }

    @Test
    public void forceUnlock_whenLockWasLocked() {
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeInfinite);
        Assert.assertTrue(lockStoreProxy.forceUnlock(key));
    }

    @Test
    public void forceUnlock_whenLockStoreImplIsNull() {
        Assert.assertFalse(lockStoreProxy.forceUnlock(key));
    }

    @Test
    public void getOwnerInfo_whenLockStoreImplIsNull() {
        Assert.assertEquals(LockStoreProxy.NOT_LOCKED, lockStoreProxy.getOwnerInfo(key));
    }

    @Test
    public void getOwnerInfo_whenLockWasLocked() {
        lockStoreProxy.lock(key, callerId, threadId, referenceId, leaseTimeInfinite);
        HazelcastTestSupport.assertContains(lockStoreProxy.getOwnerInfo(key), callerId);
    }
}

