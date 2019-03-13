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
package com.navercorp.pinpoint.rpc.client;


import SocketStateCode.CLOSED_BY_CLIENT;
import SocketStateCode.CLOSED_BY_SERVER;
import SocketStateCode.CONNECT_FAILED;
import SocketStateCode.RUN_DUPLEX;
import SocketStateCode.UNEXPECTED_CLOSE_BY_CLIENT;
import SocketStateCode.UNEXPECTED_CLOSE_BY_SERVER;
import com.navercorp.pinpoint.rpc.PinpointSocket;
import com.navercorp.pinpoint.rpc.util.PinpointRPCTestUtils;
import com.navercorp.pinpoint.test.server.TestPinpointServerAcceptor;
import com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory;
import com.navercorp.pinpoint.test.utils.TestAwaitUtils;
import org.junit.Test;
import org.springframework.util.SocketUtils;

import static com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory.HandshakeType.DUPLEX;


/**
 *
 *
 * @author Taejin Koo
 */
public class PinpointClientStateTest {
    private final TestServerMessageListenerFactory testServerMessageListenerFactory = new TestServerMessageListenerFactory(DUPLEX);

    private final TestAwaitUtils awaitUtils = new TestAwaitUtils(100, 2000);

    @Test
    public void connectFailedStateTest() throws InterruptedException {
        PinpointClientFactory clientFactory = null;
        DefaultPinpointClientHandler handler = null;
        try {
            int availableTcpPort = SocketUtils.findAvailableTcpPort(47000);
            clientFactory = PinpointRPCTestUtils.createClientFactory(PinpointRPCTestUtils.getParams(), testServerMessageListenerFactory.create());
            handler = connect(clientFactory, availableTcpPort);
            assertHandlerState(CONNECT_FAILED, handler);
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientFactory);
        }
    }

    @Test
    public void closeStateTest() throws InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
        int bindPort = testPinpointServerAcceptor.bind();
        PinpointClientFactory clientSocketFactory = null;
        DefaultPinpointClientHandler handler = null;
        try {
            clientSocketFactory = PinpointRPCTestUtils.createClientFactory(PinpointRPCTestUtils.getParams(), testServerMessageListenerFactory.create());
            handler = connect(clientSocketFactory, bindPort);
            assertHandlerState(RUN_DUPLEX, handler);
            handler.close();
            assertHandlerState(CLOSED_BY_CLIENT, handler);
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientSocketFactory);
            testPinpointServerAcceptor.close();
        }
    }

    @Test
    public void closeByPeerStateTest() throws InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
        int bindPort = testPinpointServerAcceptor.bind();
        PinpointClientFactory clientFactory = null;
        DefaultPinpointClientHandler handler = null;
        try {
            clientFactory = PinpointRPCTestUtils.createClientFactory(PinpointRPCTestUtils.getParams(), testServerMessageListenerFactory.create());
            handler = connect(clientFactory, bindPort);
            assertHandlerState(RUN_DUPLEX, handler);
            testPinpointServerAcceptor.close();
            assertHandlerState(CLOSED_BY_SERVER, handler);
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientFactory);
            testPinpointServerAcceptor.close();
        }
    }

    @Test
    public void unexpectedCloseStateTest() throws InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
        int bindPort = testPinpointServerAcceptor.bind();
        PinpointClientFactory clientFactory = null;
        DefaultPinpointClientHandler handler = null;
        try {
            clientFactory = PinpointRPCTestUtils.createClientFactory(PinpointRPCTestUtils.getParams(), testServerMessageListenerFactory.create());
            handler = connect(clientFactory, bindPort);
            assertHandlerState(RUN_DUPLEX, handler);
            clientFactory.release();
            assertHandlerState(UNEXPECTED_CLOSE_BY_CLIENT, handler);
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientFactory);
            testPinpointServerAcceptor.close();
        }
    }

    @Test
    public void unexpectedCloseByPeerStateTest() throws InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
        int bindPort = testPinpointServerAcceptor.bind();
        PinpointClientFactory clientFactory = null;
        DefaultPinpointClientHandler handler = null;
        try {
            clientFactory = PinpointRPCTestUtils.createClientFactory(PinpointRPCTestUtils.getParams(), testServerMessageListenerFactory.create());
            handler = connect(clientFactory, bindPort);
            assertHandlerState(RUN_DUPLEX, handler);
            PinpointSocket pinpointServer = testPinpointServerAcceptor.getConnectedPinpointSocketList().get(0);
            stop(true);
            assertHandlerState(UNEXPECTED_CLOSE_BY_SERVER, handler);
        } finally {
            closeHandler(handler);
            closeSocketFactory(clientFactory);
            testPinpointServerAcceptor.close();
        }
    }
}

