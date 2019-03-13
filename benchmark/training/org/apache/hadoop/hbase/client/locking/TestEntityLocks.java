/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.client.locking;


import LockHeartbeatResponse.LockStatus.LOCKED;
import LockHeartbeatResponse.LockStatus.UNLOCKED;
import LockService.BlockingInterface;
import LockType.EXCLUSIVE;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseIOException;
import org.apache.hadoop.hbase.shaded.protobuf.generated.LockServiceProtos.LockHeartbeatRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.LockServiceProtos.LockHeartbeatResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.LockServiceProtos.LockRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.LockServiceProtos.LockResponse;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ClientTests.class, SmallTests.class })
public class TestEntityLocks {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestEntityLocks.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestEntityLocks.class);

    private final Configuration conf = HBaseConfiguration.create();

    private final BlockingInterface master = Mockito.mock(BlockingInterface.class);

    private LockServiceClient admin;

    private ArgumentCaptor<LockRequest> lockReqArgCaptor;

    private ArgumentCaptor<LockHeartbeatRequest> lockHeartbeatReqArgCaptor;

    private static final LockHeartbeatResponse UNLOCKED_RESPONSE = LockHeartbeatResponse.newBuilder().setLockStatus(UNLOCKED).build();

    // timeout such that worker thread waits for 500ms for each heartbeat.
    private static final LockHeartbeatResponse LOCKED_RESPONSE = LockHeartbeatResponse.newBuilder().setLockStatus(LOCKED).setTimeoutMs(10000).build();

    private long procId;

    /**
     * Test basic lock function - requestLock, await, unlock.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEntityLock() throws Exception {
        final long procId = 100;
        final long workerSleepTime = 200;// in ms

        EntityLock lock = admin.namespaceLock("namespace", "description", null);
        lock.setTestingSleepTime(workerSleepTime);
        Mockito.when(master.requestLock(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(LockResponse.newBuilder().setProcId(procId).build());
        Mockito.when(master.lockHeartbeat(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(TestEntityLocks.UNLOCKED_RESPONSE, TestEntityLocks.UNLOCKED_RESPONSE, TestEntityLocks.UNLOCKED_RESPONSE, TestEntityLocks.LOCKED_RESPONSE);
        lock.requestLock();
        // we return unlock response 3 times, so actual wait time should be around 2 * workerSleepTime
        lock.await((4 * workerSleepTime), TimeUnit.MILLISECONDS);
        Assert.assertTrue(lock.isLocked());
        lock.unlock();
        Assert.assertTrue((!(lock.getWorker().isAlive())));
        Assert.assertFalse(lock.isLocked());
        // check LockRequest in requestLock()
        Mockito.verify(master, Mockito.times(1)).requestLock(ArgumentMatchers.any(), lockReqArgCaptor.capture());
        LockRequest request = lockReqArgCaptor.getValue();
        Assert.assertEquals("namespace", request.getNamespace());
        Assert.assertEquals("description", request.getDescription());
        Assert.assertEquals(EXCLUSIVE, request.getLockType());
        Assert.assertEquals(0, request.getRegionInfoCount());
        // check LockHeartbeatRequest in lockHeartbeat()
        Mockito.verify(master, Mockito.atLeastOnce()).lockHeartbeat(ArgumentMatchers.any(), lockHeartbeatReqArgCaptor.capture());
        for (LockHeartbeatRequest req : lockHeartbeatReqArgCaptor.getAllValues()) {
            Assert.assertEquals(procId, req.getProcId());
        }
    }

    /**
     * Test that abort is called when lock times out.
     */
    @Test
    public void testEntityLockTimeout() throws Exception {
        final long workerSleepTime = 200;// in ms

        Abortable abortable = Mockito.mock(Abortable.class);
        EntityLock lock = admin.namespaceLock("namespace", "description", abortable);
        lock.setTestingSleepTime(workerSleepTime);
        Mockito.when(master.requestLock(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(LockResponse.newBuilder().setProcId(procId).build());
        // Acquires the lock, but then it times out (since we don't call unlock() on it).
        Mockito.when(master.lockHeartbeat(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(TestEntityLocks.LOCKED_RESPONSE, TestEntityLocks.UNLOCKED_RESPONSE);
        lock.requestLock();
        lock.await();
        Assert.assertTrue(lock.isLocked());
        // Should get unlocked in next heartbeat i.e. after workerSleepTime. Wait 2x time.
        Assert.assertTrue(waitLockTimeOut(lock, (2 * workerSleepTime)));
        Assert.assertFalse(lock.getWorker().isAlive());
        Mockito.verify(abortable, Mockito.times(1)).abort(ArgumentMatchers.any(), ArgumentMatchers.eq(null));
    }

    /**
     * Test that abort is called when lockHeartbeat fails with IOException.
     */
    @Test
    public void testHeartbeatException() throws Exception {
        final long workerSleepTime = 100;// in ms

        Abortable abortable = Mockito.mock(Abortable.class);
        EntityLock lock = admin.namespaceLock("namespace", "description", abortable);
        lock.setTestingSleepTime(workerSleepTime);
        Mockito.when(master.requestLock(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(LockResponse.newBuilder().setProcId(procId).build());
        Mockito.when(master.lockHeartbeat(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(TestEntityLocks.LOCKED_RESPONSE).thenThrow(new ServiceException("Failed heartbeat!"));
        lock.requestLock();
        lock.await();
        Assert.assertTrue(waitLockTimeOut(lock, (100 * workerSleepTime)));
        while (lock.getWorker().isAlive()) {
            TimeUnit.MILLISECONDS.sleep(100);
        } 
        Mockito.verify(abortable, Mockito.times(1)).abort(ArgumentMatchers.any(), ArgumentMatchers.isA(HBaseIOException.class));
        Assert.assertFalse(lock.getWorker().isAlive());
    }
}

