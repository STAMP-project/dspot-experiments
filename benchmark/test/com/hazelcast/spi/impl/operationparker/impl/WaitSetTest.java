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
package com.hazelcast.spi.impl.operationparker.impl;


import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Notifier;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WaitSetTest {
    private ILogger logger = Logger.getLogger(WaitSetTest.class);

    private ConcurrentMap<WaitNotifyKey, WaitSet> waitSetMap = new ConcurrentHashMap<WaitNotifyKey, WaitSet>();

    private Queue<WaitSetEntry> delayQueue = new LinkedBlockingQueue<WaitSetEntry>();

    private NodeEngine nodeEngine;

    private OperationService operationService;

    @Test
    public void park_whenNoTimeoutSet() {
        WaitSet waitSet = newWaitSet();
        WaitSetTest.BlockedOperation op = new WaitSetTest.BlockedOperation();
        waitSet.park(op);
        Assert.assertEquals(1, waitSet.size());
        Assert.assertTrue(delayQueue.isEmpty());
    }

    @Test
    public void park_whenTimeoutSet() {
        WaitSet waitSet = newWaitSet();
        WaitSetTest.BlockedOperation op = new WaitSetTest.BlockedOperation();
        setWaitTimeout(100);
        waitSet.park(op);
        Assert.assertEquals(1, waitSet.size());
        WaitSetEntry entry = waitSet.find(op);
        Assert.assertTrue(entry.isValid());
        Assert.assertFalse(entry.isExpired());
        Assert.assertFalse(entry.isCancelled());
        Assert.assertEquals(1, delayQueue.size());
        Assert.assertSame(entry, delayQueue.poll());
    }

    @Test
    public void unpark_whenNoTimeoutSet() {
        WaitSet waitSet = newWaitSet();
        WaitSetTest.BlockedOperation op = new WaitSetTest.BlockedOperation();
        waitSet.park(op);
        Assert.assertEquals(1, waitSet.size());
        WaitSetEntry entry = waitSet.find(op);
        Assert.assertTrue(entry.isValid());
        Assert.assertFalse(entry.isExpired());
        Assert.assertFalse(entry.isCancelled());
    }

    @Test
    public void unpark_whenSuccess() {
        WaitSet waitSet = newWaitSet();
        WaitSetTest.BlockedOperation blockedOp = newBlockingOperationWithServiceNameAndObjectId("service1", "1");
        waitSet.park(blockedOp);
        WaitSetTest.NotifyingOperation notifyOp = newNotifyingOperationWithServiceNameAndObjectId("service1", "1");
        waitSet.unpark(notifyOp, notifyOp.getNotifiedKey());
        Mockito.verify(operationService).run(blockedOp);
        // the parked operation should be removed from the waitset
        Assert.assertEquals(0, waitSet.size());
        // since it is the last parked op, the waitset should be removed from the waitSetMap
        Assert.assertEquals(0, waitSetMap.size());
    }

    @Test
    public void totalValidWaitingOperations() {
        WaitSet waitSet = newWaitSet();
        waitSet.park(new WaitSetTest.BlockedOperation());
        waitSet.park(new WaitSetTest.BlockedOperation());
        waitSet.park(new WaitSetTest.BlockedOperation());
        Assert.assertEquals(3, waitSet.totalValidWaitingOperationCount());
    }

    @Test
    public void iterator() {
        WaitSet waitSet = newWaitSet();
        WaitSetTest.BlockedOperation op1 = new WaitSetTest.BlockedOperation();
        waitSet.park(op1);
        WaitSetTest.BlockedOperation op2 = new WaitSetTest.BlockedOperation();
        waitSet.park(op2);
        WaitSetTest.BlockedOperation op3 = new WaitSetTest.BlockedOperation();
        waitSet.park(op3);
        Iterator<WaitSetEntry> it = waitSet.iterator();
        Assert.assertEquals(op1, it.next().op);
        Assert.assertEquals(op2, it.next().op);
        Assert.assertEquals(op3, it.next().op);
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void invalidateAll() {
        WaitSet waitSet = newWaitSet();
        WaitSetTest.BlockedOperation op1 = WaitSetTest.newBlockingOperationWithCallerUuid("foo");
        waitSet.park(op1);
        WaitSetTest.BlockedOperation op2 = WaitSetTest.newBlockingOperationWithCallerUuid("bar");
        waitSet.park(op2);
        WaitSetTest.BlockedOperation op3 = WaitSetTest.newBlockingOperationWithCallerUuid("foo");
        waitSet.park(op3);
        waitSet.invalidateAll("foo");
        WaitSetTest.assertValid(waitSet, op1, false);
        WaitSetTest.assertValid(waitSet, op2, true);
        WaitSetTest.assertValid(waitSet, op3, false);
    }

    @Test
    public void cancelAll() {
        WaitSet waitSet = newWaitSet();
        WaitSetTest.BlockedOperation op1 = newBlockingOperationWithServiceNameAndObjectId("service1", "1");
        waitSet.park(op1);
        WaitSetTest.BlockedOperation op2 = newBlockingOperationWithServiceNameAndObjectId("service1", "2");
        waitSet.park(op2);
        WaitSetTest.BlockedOperation op3 = newBlockingOperationWithServiceNameAndObjectId("service2", "1");
        waitSet.park(op3);
        Exception cause = new Exception();
        waitSet.cancelAll("foo", "1", cause);
        WaitSetTest.assertCancelled(waitSet, op1, cause);
        WaitSetTest.assertCancelled(waitSet, op2, null);
        WaitSetTest.assertCancelled(waitSet, op3, null);
    }

    private static class BlockedOperation extends Operation implements BlockingOperation {
        private String objectId;

        private boolean hasRun;

        @Override
        public WaitNotifyKey getWaitKey() {
            return new WaitSetTest.WaitNotifyKeyImpl(getServiceName(), objectId);
        }

        @Override
        public boolean shouldWait() {
            return false;
        }

        @Override
        public void onWaitExpire() {
        }

        @Override
        public void run() throws Exception {
            hasRun = true;
        }
    }

    private static class NotifyingOperation extends Operation implements Notifier {
        private String objectId;

        @Override
        public boolean shouldNotify() {
            return true;
        }

        @Override
        public WaitNotifyKey getNotifiedKey() {
            return new WaitSetTest.WaitNotifyKeyImpl(getServiceName(), objectId);
        }

        @Override
        public void run() throws Exception {
        }
    }

    private static class WaitNotifyKeyImpl implements WaitNotifyKey {
        private final String serviceName;

        private final String objectName;

        public WaitNotifyKeyImpl(String serviceName, String objectName) {
            this.serviceName = serviceName;
            this.objectName = objectName;
        }

        @Override
        public String getServiceName() {
            return serviceName;
        }

        @Override
        public String getObjectName() {
            return objectName;
        }
    }
}

