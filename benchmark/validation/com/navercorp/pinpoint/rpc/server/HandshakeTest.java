/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.rpc.server;


import HandshakePropertyType.START_TIMESTAMP;
import com.navercorp.pinpoint.rpc.PinpointSocket;
import com.navercorp.pinpoint.rpc.client.PinpointClientHandshaker;
import com.navercorp.pinpoint.rpc.util.PinpointRPCTestUtils;
import com.navercorp.pinpoint.test.client.TestPinpointClient;
import com.navercorp.pinpoint.test.server.TestPinpointServerAcceptor;
import com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory;
import java.util.Collections;
import java.util.Map;
import org.jboss.netty.util.Timer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory.HandshakeType.DUPLEX;


public class HandshakeTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TestServerMessageListenerFactory testServerMessageListenerFactory = new TestServerMessageListenerFactory(DUPLEX);

    private static Timer timer = null;

    // simple test
    @Test
    public void handshakeTest1() throws InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
        int bindPort = testPinpointServerAcceptor.bind();
        TestPinpointClient testPinpointClient1 = new TestPinpointClient(testServerMessageListenerFactory.create(), PinpointRPCTestUtils.getParams());
        TestPinpointClient testPinpointClient2 = new TestPinpointClient(PinpointRPCTestUtils.getParams());
        try {
            testPinpointClient1.connect(bindPort);
            testPinpointClient2.connect(bindPort);
            testPinpointServerAcceptor.assertAwaitClientConnected(2, 3000);
        } finally {
            testPinpointClient1.closeAll();
            testPinpointClient2.closeAll();
            testPinpointServerAcceptor.close();
        }
    }

    @Test
    public void handshakeTest2() throws InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
        int bindPort = testPinpointServerAcceptor.bind();
        Map<String, Object> params = PinpointRPCTestUtils.getParams();
        TestPinpointClient testPinpointClient = new TestPinpointClient(testServerMessageListenerFactory.create(), params);
        try {
            testPinpointClient.connect(bindPort);
            testPinpointServerAcceptor.assertAwaitClientConnected(1, 3000);
            PinpointSocket writableServer = getWritableServer("application", "agent", ((Long) (params.get(START_TIMESTAMP.getName()))), testPinpointServerAcceptor.getConnectedPinpointSocketList());
            Assert.assertNotNull(writableServer);
            writableServer = getWritableServer("application", "agent", (((Long) (params.get(START_TIMESTAMP.getName()))) + 1), testPinpointServerAcceptor.getConnectedPinpointSocketList());
            Assert.assertNull(writableServer);
        } finally {
            testPinpointClient.closeAll();
            testPinpointServerAcceptor.close();
        }
    }

    @Test
    public void testExecuteCompleteWithoutStart() {
        int retryInterval = 100;
        int maxHandshakeCount = 10;
        Map<String, Object> emptyMap = Collections.emptyMap();
        PinpointClientHandshaker handshaker = new PinpointClientHandshaker(emptyMap, HandshakeTest.timer, retryInterval, maxHandshakeCount);
        handshaker.handshakeComplete(null);
        Assert.assertEquals(null, handshaker.getHandshakeResult());
        Assert.assertTrue(handshaker.isFinished());
    }

    @Test
    public void testExecuteAbortWithoutStart() {
        int retryInterval = 100;
        int maxHandshakeCount = 10;
        Map<String, Object> emptyMap = Collections.emptyMap();
        PinpointClientHandshaker handshaker = new PinpointClientHandshaker(emptyMap, HandshakeTest.timer, retryInterval, maxHandshakeCount);
        handshaker.handshakeAbort();
        Assert.assertTrue(handshaker.isFinished());
    }
}

