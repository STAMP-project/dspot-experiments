/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.wan;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class GatewaySenderEventRemoteDispatcherJUnitTest {
    @Test
    public void getConnectionShouldShutdownTheAckThreadReaderWhenEventProcessorIsShutDown() {
        AbstractGatewaySender sender = Mockito.mock(AbstractGatewaySender.class);
        AbstractGatewaySenderEventProcessor eventProcessor = Mockito.mock(AbstractGatewaySenderEventProcessor.class);
        GatewaySenderEventRemoteDispatcher dispatcher = new GatewaySenderEventRemoteDispatcher(eventProcessor, null);
        GatewaySenderEventRemoteDispatcher.AckReaderThread ackReaderThread = dispatcher.new AckReaderThread(sender, "AckReaderThread");
        dispatcher.setAckReaderThread(ackReaderThread);
        Assert.assertFalse(ackReaderThread.isShutdown());
        Mockito.when(eventProcessor.isStopped()).thenReturn(true);
        Assert.assertNull(dispatcher.getConnection(false));
        Assert.assertTrue(ackReaderThread.isShutdown());
    }

    @Test
    public void shuttingDownAckThreadReaderConnectionShouldshutdownTheAckThreadReader() {
        AbstractGatewaySender sender = Mockito.mock(AbstractGatewaySender.class);
        AbstractGatewaySenderEventProcessor eventProcessor = Mockito.mock(AbstractGatewaySenderEventProcessor.class);
        GatewaySenderEventRemoteDispatcher dispatcher = new GatewaySenderEventRemoteDispatcher(eventProcessor, null);
        GatewaySenderEventRemoteDispatcher.AckReaderThread ackReaderThread = dispatcher.new AckReaderThread(sender, "AckReaderThread");
        dispatcher.setAckReaderThread(ackReaderThread);
        dispatcher.shutDownAckReaderConnection();
        Assert.assertTrue(ackReaderThread.isShutdown());
    }
}

