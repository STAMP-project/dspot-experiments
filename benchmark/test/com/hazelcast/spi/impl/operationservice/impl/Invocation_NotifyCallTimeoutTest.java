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
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.instance.Node;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_NotifyCallTimeoutTest extends HazelcastTestSupport {
    private OperationServiceImpl operationService;

    private Node node;

    private Invocation_NotifyCallTimeoutTest.WaitNotifyKeyImpl waitNotifyKey = new Invocation_NotifyCallTimeoutTest.WaitNotifyKeyImpl();

    @Test
    public void testInfiniteWaitTimeout() {
        Invocation_NotifyCallTimeoutTest.DummyBlockingOperation op = new Invocation_NotifyCallTimeoutTest.DummyBlockingOperation(waitNotifyKey);
        setPartitionId(0).setWaitTimeout((-1));
        Invocation invocation = new PartitionInvocation(operationService.invocationContext, op, 10, TimeUnit.MINUTES.toSeconds(2), TimeUnit.MINUTES.toSeconds(2), false, false);
        OperationAccessor.setInvocationTime(op, node.getClusterService().getClusterClock().getClusterTime());
        invocation.notifyCallTimeout();
        // now we verify if the wait timeout is still inifinite
        Assert.assertEquals((-1), getWaitTimeout());
    }

    @Test
    public void testTimedWait_andNearingZero() {
        Invocation_NotifyCallTimeoutTest.DummyBlockingOperation op = new Invocation_NotifyCallTimeoutTest.DummyBlockingOperation(waitNotifyKey);
        setPartitionId(0).setWaitTimeout(TimeUnit.SECONDS.toMillis(2));
        Invocation invocation = new PartitionInvocation(operationService.invocationContext, op, 10, TimeUnit.MINUTES.toSeconds(2), TimeUnit.MINUTES.toSeconds(2), false, false);
        OperationAccessor.setInvocationTime(op, node.getClusterService().getClusterClock().getClusterTime());
        // we sleep longer than the wait timeout; so eventually the waitTimeout should become 0
        HazelcastTestSupport.sleepSeconds(5);
        invocation.notifyCallTimeout();
        Assert.assertEquals(0, getWaitTimeout());
    }

    @Test
    public void testTimedWait() {
        Invocation_NotifyCallTimeoutTest.DummyBlockingOperation op = new Invocation_NotifyCallTimeoutTest.DummyBlockingOperation(waitNotifyKey);
        setPartitionId(0).setWaitTimeout(TimeUnit.SECONDS.toMillis(60));
        Invocation invocation = new PartitionInvocation(operationService.invocationContext, op, 10, TimeUnit.MINUTES.toSeconds(2), TimeUnit.MINUTES.toSeconds(2), false, false);
        OperationAccessor.setInvocationTime(op, node.getClusterService().getClusterClock().getClusterTime());
        HazelcastTestSupport.sleepSeconds(5);
        invocation.notifyCallTimeout();
        // we have a 60 wait timeout, if we wait 5 seconds and account for gc's etc; we should keep more than 40 seconds.
        Assert.assertTrue((("op.waitTimeout " + (getWaitTimeout())) + " is too small"), ((getWaitTimeout()) >= (TimeUnit.SECONDS.toMillis(40))));
        // we also need to verify that the wait timeout has decreased.
        Assert.assertTrue((("op.waitTimeout " + (getWaitTimeout())) + " is too small"), ((getWaitTimeout()) <= (TimeUnit.SECONDS.toMillis(55))));
    }

    private static class WaitNotifyKeyImpl implements WaitNotifyKey {
        private final String objectName = UuidUtil.newUnsecureUuidString();

        @Override
        public String getServiceName() {
            return "dummy";
        }

        @Override
        public String getObjectName() {
            return objectName;
        }
    }

    static class DummyBlockingOperation extends Operation implements BlockingOperation {
        private final WaitNotifyKey waitNotifyKey;

        private DummyBlockingOperation(WaitNotifyKey waitNotifyKey) {
            this.waitNotifyKey = waitNotifyKey;
        }

        @Override
        public void run() throws Exception {
        }

        @Override
        public WaitNotifyKey getWaitKey() {
            return waitNotifyKey;
        }

        @Override
        public boolean shouldWait() {
            return true;
        }

        @Override
        public void onWaitExpire() {
        }
    }
}

