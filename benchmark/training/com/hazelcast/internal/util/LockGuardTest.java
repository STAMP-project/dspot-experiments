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
package com.hazelcast.internal.util;


import com.hazelcast.nio.Address;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static LockGuard.NOT_LOCKED;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LockGuardTest {
    @Test
    public void testNotLocked() {
        LockGuard stateLock = NOT_LOCKED;
        Assert.assertNull(stateLock.getLockOwner());
        Assert.assertEquals(0L, stateLock.getLockExpiryTime());
    }

    @Test(expected = NullPointerException.class)
    public void testAllowsLock_nullTransactionId() throws Exception {
        LockGuard stateLock = NOT_LOCKED;
        stateLock.allowsLock(null);
    }

    @Test(expected = NullPointerException.class)
    public void testAllowsUnlock_nullTransactionId() throws Exception {
        LockGuard stateLock = NOT_LOCKED;
        stateLock.allowsUnlock(null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_nullEndpoint() throws Exception {
        new LockGuard(null, "txn", 1000);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor_nullTransactionId() throws Exception {
        Address endpoint = newAddress();
        new LockGuard(endpoint, null, 1000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nonPositiveLeaseTime() throws Exception {
        Address endpoint = newAddress();
        new LockGuard(endpoint, "txn", (-1000));
    }

    @Test
    public void testAllowsLock_success() throws Exception {
        LockGuard stateLock = NOT_LOCKED;
        Assert.assertTrue(stateLock.allowsLock("txn"));
    }

    @Test
    public void testAllowsLock_fail() throws Exception {
        Address endpoint = newAddress();
        LockGuard stateLock = new LockGuard(endpoint, "txn", 1000);
        Assert.assertFalse(stateLock.allowsLock("another-txn"));
    }

    @Test
    public void testIsLocked() throws Exception {
        LockGuard stateLock = NOT_LOCKED;
        Assert.assertFalse(stateLock.isLocked());
        Address endpoint = newAddress();
        stateLock = new LockGuard(endpoint, "txn", 1000);
        Assert.assertTrue(stateLock.isLocked());
    }

    @Test
    public void testIsLeaseExpired() throws Exception {
        LockGuard stateLock = NOT_LOCKED;
        Assert.assertFalse(stateLock.isLeaseExpired());
        Address endpoint = newAddress();
        stateLock = new LockGuard(endpoint, "txn", TimeUnit.HOURS.toMillis(1));
        Assert.assertFalse(stateLock.isLeaseExpired());
        stateLock = new LockGuard(endpoint, "txn", 1);
        final LockGuard finalStateLock = stateLock;
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(finalStateLock.isLeaseExpired());
            }
        });
    }
}

