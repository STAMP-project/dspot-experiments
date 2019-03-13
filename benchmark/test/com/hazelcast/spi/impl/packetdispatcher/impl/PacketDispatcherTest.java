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
package com.hazelcast.spi.impl.packetdispatcher.impl;


import Packet.Type.EVENT;
import Packet.Type.JET;
import Packet.Type.NULL;
import Packet.Type.OPERATION;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.impl.PacketDispatcher;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.function.Consumer;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class PacketDispatcherTest extends HazelcastTestSupport {
    private Consumer<Packet> operationExecutor;

    private Consumer<Packet> eventService;

    // private Consumer<Packet> connectionManager;
    private Consumer<Packet> responseHandler;

    private Consumer<Packet> invocationMonitor;

    private PacketDispatcher dispatcher;

    private Consumer<Packet> jetService;

    @Test
    public void whenOperationPacket() {
        Packet packet = new Packet().setPacketType(OPERATION);
        dispatcher.accept(packet);
        Mockito.verify(operationExecutor).accept(packet);
        Mockito.verifyZeroInteractions(responseHandler, eventService, invocationMonitor, jetService);
    }

    @Test
    public void whenUrgentOperationPacket() {
        Packet packet = new Packet().setPacketType(OPERATION).raiseFlags(Packet.FLAG_URGENT);
        dispatcher.accept(packet);
        Mockito.verify(operationExecutor).accept(packet);
        Mockito.verifyZeroInteractions(responseHandler, eventService, invocationMonitor, jetService);
    }

    @Test
    public void whenOperationResponsePacket() {
        Packet packet = new Packet().setPacketType(OPERATION).raiseFlags(Packet.FLAG_OP_RESPONSE);
        dispatcher.accept(packet);
        Mockito.verify(responseHandler).accept(packet);
        Mockito.verifyZeroInteractions(operationExecutor, eventService, invocationMonitor, jetService);
    }

    @Test
    public void whenUrgentOperationResponsePacket() {
        Packet packet = new Packet().setPacketType(OPERATION).raiseFlags(((Packet.FLAG_OP_RESPONSE) | (Packet.FLAG_URGENT)));
        dispatcher.accept(packet);
        Mockito.verify(responseHandler).accept(packet);
        Mockito.verifyZeroInteractions(operationExecutor, eventService, invocationMonitor, jetService);
    }

    @Test
    public void whenOperationControlPacket() {
        Packet packet = new Packet().setPacketType(OPERATION).raiseFlags(Packet.FLAG_OP_CONTROL);
        dispatcher.accept(packet);
        Mockito.verify(invocationMonitor).accept(packet);
        Mockito.verifyZeroInteractions(responseHandler, operationExecutor, eventService, jetService);
    }

    @Test
    public void whenEventPacket() {
        Packet packet = new Packet().setPacketType(EVENT);
        dispatcher.accept(packet);
        Mockito.verify(eventService).accept(packet);
        Mockito.verifyZeroInteractions(responseHandler, operationExecutor, invocationMonitor, jetService);
    }

    @Test
    public void whenJetPacket() {
        Packet packet = new Packet().setPacketType(JET);
        dispatcher.accept(packet);
        Mockito.verify(jetService).accept(packet);
        Mockito.verifyZeroInteractions(responseHandler, operationExecutor, eventService, invocationMonitor);
    }

    // unrecognized packets are logged. No handlers is contacted.
    @Test
    public void whenUnrecognizedPacket_thenSwallowed() {
        Packet packet = new Packet().setPacketType(NULL);
        dispatcher.accept(packet);
        Mockito.verifyZeroInteractions(responseHandler, operationExecutor, eventService, invocationMonitor, jetService);
    }

    // when one of the handlers throws an exception, the exception is logged but not rethrown
    @Test
    public void whenProblemHandlingPacket_thenSwallowed() {
        Packet packet = new Packet().setPacketType(OPERATION);
        Mockito.doThrow(new ExpectedRuntimeException()).when(operationExecutor).accept(packet);
        dispatcher.accept(packet);
    }
}

