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


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_DetectHeartbeatTimeoutTest extends HazelcastTestSupport {
    @Test
    public void whenCallTimeoutDisabled() {
        Config config = new Config();
        config.setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), "1000");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance();
        HazelcastInstance remote = factory.newHazelcastInstance();
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        Operation operation = new VoidOperation();
        InvocationFuture future = ((InvocationFuture) (opService.createInvocationBuilder(null, operation, HazelcastTestSupport.getPartitionId(remote)).setCallTimeout(Long.MAX_VALUE).invoke()));
        Invocation invocation = future.invocation;
        Assert.assertEquals(Long.MAX_VALUE, invocation.op.getCallTimeout());
        Assert.assertEquals(Long.MAX_VALUE, invocation.callTimeoutMillis);
        Assert.assertEquals(HeartbeatTimeout.NO_TIMEOUT__CALL_TIMEOUT_DISABLED, invocation.detectTimeout(TimeUnit.SECONDS.toMillis(1)));
        Assert.assertFalse(future.isDone());
    }

    @Test
    public void whenResponseAvailable() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance();
        HazelcastInstance remote = factory.newHazelcastInstance();
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        Operation operation = new SlowOperation(TimeUnit.SECONDS.toMillis(60));
        InvocationFuture future = ((InvocationFuture) (opService.invokeOnPartition(null, operation, HazelcastTestSupport.getPartitionId(remote))));
        Invocation invocation = future.invocation;
        invocation.pendingResponse = "foo";
        invocation.backupsAcksExpected = 1;
        Assert.assertEquals(HeartbeatTimeout.NO_TIMEOUT__RESPONSE_AVAILABLE, invocation.detectTimeout(TimeUnit.SECONDS.toMillis(1)));
        Assert.assertFalse(future.isDone());
    }

    @Test
    public void whenCallTimeoutNotExpired() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance();
        HazelcastInstance remote = factory.newHazelcastInstance();
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        Operation operation = new SlowOperation(TimeUnit.SECONDS.toMillis(60));
        InvocationFuture future = ((InvocationFuture) (opService.invokeOnPartition(null, operation, HazelcastTestSupport.getPartitionId(remote))));
        Invocation invocation = future.invocation;
        Assert.assertEquals(HeartbeatTimeout.NO_TIMEOUT__CALL_TIMEOUT_NOT_EXPIRED, invocation.detectTimeout(TimeUnit.SECONDS.toMillis(1)));
        Assert.assertFalse(future.isDone());
    }

    @Test
    public void whenCallTimeoutExpired_ButOperationHeartbeatHasNot() {
        Config config = new Config();
        config.setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), "5000");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        InvocationFuture future = ((InvocationFuture) (opService.invokeOnPartition(new SlowOperation(TimeUnit.SECONDS.toMillis(60)).setPartitionId(HazelcastTestSupport.getPartitionId(remote)))));
        assertDetectHeartbeatTimeoutEventually(future.invocation, HeartbeatTimeout.NO_TIMEOUT__HEARTBEAT_TIMEOUT_NOT_EXPIRED);
    }

    /**
     * This test checks if the invocation expires eventually after the operation did manage to execute and did manage to send
     * some heartbeats but for whatever reason the response was not received.
     * <p>
     * We do this by sending in a void operation that runs for an long period (so there are heartbeats) but on completion it
     * doesn't send a response.
     */
    @Test
    public void whenExpiresEventually() {
        Config config = new Config();
        config.setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), "5000");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance local = factory.newHazelcastInstance(config);
        HazelcastInstance remote = factory.newHazelcastInstance(config);
        OperationService opService = HazelcastTestSupport.getOperationService(local);
        Operation operation = new VoidOperation(TimeUnit.SECONDS.toMillis(20));
        InvocationFuture future = ((InvocationFuture) (opService.invokeOnPartition(null, operation, HazelcastTestSupport.getPartitionId(remote))));
        Invocation invocation = future.invocation;
        assertDetectHeartbeatTimeoutEventually(invocation, HeartbeatTimeout.NO_TIMEOUT__CALL_TIMEOUT_NOT_EXPIRED);
        assertDetectHeartbeatTimeoutEventually(invocation, HeartbeatTimeout.NO_TIMEOUT__HEARTBEAT_TIMEOUT_NOT_EXPIRED);
        assertDetectHeartbeatTimeoutEventually(invocation, HeartbeatTimeout.TIMEOUT);
    }
}

