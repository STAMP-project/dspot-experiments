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


import Packet.Type.OPERATION;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.operationservice.impl.InboundResponseHandlerSupplier.AsyncMultithreadedResponseHandler;
import com.hazelcast.spi.impl.operationservice.impl.InboundResponseHandlerSupplier.AsyncSingleThreadedResponseHandler;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InboundResponseHandlerSupplierTest extends HazelcastTestSupport {
    private InternalSerializationService serializationService;

    private InvocationRegistry invocationRegistry;

    private NodeEngine nodeEngine;

    private InboundResponseHandlerSupplier supplier;

    @Test
    public void get_whenZeroResponseThreads() {
        supplier = newSupplier(0);
        HazelcastTestSupport.assertInstanceOf(InboundResponseHandler.class, supplier.get());
    }

    @Test
    public void get_whenResponseThreads() {
        supplier = newSupplier(1);
        HazelcastTestSupport.assertInstanceOf(AsyncSingleThreadedResponseHandler.class, supplier.get());
    }

    @Test
    public void get_whenMultipleResponseThreads() {
        supplier = newSupplier(2);
        HazelcastTestSupport.assertInstanceOf(AsyncMultithreadedResponseHandler.class, supplier.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void get_whenNegativeResponseThreads() {
        newSupplier((-1));
    }

    @Test
    public void whenNoProblemPacket_andZeroResponseThreads() throws Exception {
        whenNoProblemPacket(0);
    }

    @Test
    public void whenNoProblemPacket_andOneResponseThreads() throws Exception {
        whenNoProblemPacket(1);
    }

    @Test
    public void whenNoProblemPacket_andMultipleResponseThreads() throws Exception {
        whenNoProblemPacket(2);
    }

    // test that is a bad response is send, the processing loop isn't broken
    // This test isn't terribly exciting since responses are constructed by
    // the system and unlikely to fail.
    @Test
    public void whenPacketThrowsException() {
        supplier = newSupplier(1);
        supplier.start();
        // create a registered invocation
        final Invocation invocation = newInvocation();
        invocationRegistry.register(invocation);
        final long callId = invocation.op.getCallId();
        // the response flag isn't set; so an exception is thrown.
        Packet badResponse = new Packet(serializationService.toBytes(new NormalResponse("bad", 1, 0, false))).setPacketType(OPERATION).setConn(Mockito.mock(Connection.class));
        Consumer<Packet> responseConsumer = supplier.get();
        responseConsumer.accept(badResponse);
        final Packet goodResponse = new Packet(serializationService.toBytes(new NormalResponse("foo", callId, 0, false))).setPacketType(OPERATION).raiseFlags(Packet.FLAG_OP_RESPONSE).setConn(Mockito.mock(Connection.class));
        responseConsumer.accept(goodResponse);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Invocation inv = invocationRegistry.get(callId);
                System.out.println(inv);
                Assert.assertNull(inv);
            }
        });
    }
}

