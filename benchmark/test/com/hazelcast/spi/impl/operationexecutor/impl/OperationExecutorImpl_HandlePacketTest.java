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
package com.hazelcast.spi.impl.operationexecutor.impl;


import Packet.Type.OPERATION;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationexecutor.OperationRunner;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests {@link OperationExecutorImpl#handle(Packet)}.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class OperationExecutorImpl_HandlePacketTest extends OperationExecutorImpl_AbstractTest {
    @Test(expected = NullPointerException.class)
    public void test_whenNullPacket() {
        initExecutor();
        executor.accept(null);
    }

    @Test
    public void test_whenResponsePacket() {
        initExecutor();
        final NormalResponse normalResponse = new NormalResponse(null, 1, 0, false);
        final Packet packet = new Packet(serializationService.toBytes(normalResponse), 0).setPacketType(OPERATION).raiseFlags(Packet.FLAG_OP_RESPONSE);
        executor.accept(packet);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                OperationExecutorImpl_AbstractTest.DummyResponsePacketConsumer responsePacketConsumer = ((OperationExecutorImpl_AbstractTest.DummyResponsePacketConsumer) (OperationExecutorImpl_HandlePacketTest.this.responsePacketConsumer));
                responsePacketConsumer.packets.contains(packet);
                responsePacketConsumer.responses.contains(normalResponse);
            }
        });
    }

    @Test
    public void test_whenPartitionSpecificOperationPacket() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyOperation operation = new OperationExecutorImpl_AbstractTest.DummyOperation(0);
        final Packet packet = new Packet(serializationService.toBytes(operation), operation.getPartitionId()).setPacketType(OPERATION);
        executor.accept(packet);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                OperationRunner[] partitionHandlers = executor.getPartitionOperationRunners();
                OperationExecutorImpl_AbstractTest.DummyOperationRunner handler = ((OperationExecutorImpl_AbstractTest.DummyOperationRunner) (partitionHandlers[getPartitionId()]));
                HazelcastTestSupport.assertContains(handler.packets, packet);
            }
        });
    }

    @Test
    public void test_whenGenericOperationPacket() {
        initExecutor();
        final OperationExecutorImpl_AbstractTest.DummyOperation operation = new OperationExecutorImpl_AbstractTest.DummyOperation(Operation.GENERIC_PARTITION_ID);
        final Packet packet = new Packet(serializationService.toBytes(operation), operation.getPartitionId()).setPacketType(OPERATION);
        executor.accept(packet);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                OperationRunner[] genericHandlers = executor.getGenericOperationRunners();
                boolean found = false;
                for (OperationRunner h : genericHandlers) {
                    OperationExecutorImpl_AbstractTest.DummyOperationRunner dummyOperationHandler = ((OperationExecutorImpl_AbstractTest.DummyOperationRunner) (h));
                    if (dummyOperationHandler.packets.contains(packet)) {
                        found = true;
                        break;
                    }
                }
                Assert.assertTrue("Packet is not found on any of the generic handlers", found);
            }
        });
    }
}

