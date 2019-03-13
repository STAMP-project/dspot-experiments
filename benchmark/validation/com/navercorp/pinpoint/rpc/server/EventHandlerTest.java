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


import SocketStateCode.RUN_DUPLEX;
import SocketStateCode.RUN_WITHOUT_HANDSHAKE;
import com.navercorp.pinpoint.rpc.common.SocketStateCode;
import com.navercorp.pinpoint.rpc.server.handler.ServerStateChangeEventHandler;
import com.navercorp.pinpoint.rpc.util.PinpointRPCTestUtils;
import com.navercorp.pinpoint.test.client.TestRawSocket;
import com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory;
import org.junit.Assert;
import org.junit.Test;

import static com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory.HandshakeType.DUPLEX;


/**
 *
 *
 * @author koo.taejin
 */
public class EventHandlerTest {
    private static int bindPort;

    private final TestServerMessageListenerFactory testServerMessageListenerFactory = new TestServerMessageListenerFactory(DUPLEX);

    // Test for being possible to send messages in case of failure of registering packet ( return code : 2, lack of parameter)
    @Test
    public void registerAgentSuccessTest() throws Exception {
        EventHandlerTest.EventHandler eventHandler = new EventHandlerTest.EventHandler();
        PinpointServerAcceptor serverAcceptor = new PinpointServerAcceptor();
        serverAcceptor.addStateChangeEventHandler(eventHandler);
        serverAcceptor.setMessageListenerFactory(testServerMessageListenerFactory);
        serverAcceptor.bind("127.0.0.1", EventHandlerTest.bindPort);
        TestRawSocket testRawSocket = new TestRawSocket();
        try {
            testRawSocket.connect(EventHandlerTest.bindPort);
            sendAndReceiveSimplePacket(testRawSocket);
            Assert.assertEquals(eventHandler.getCode(), RUN_WITHOUT_HANDSHAKE);
            int code = sendAndReceiveRegisterPacket(testRawSocket, PinpointRPCTestUtils.getParams());
            Assert.assertEquals(eventHandler.getCode(), RUN_DUPLEX);
            sendAndReceiveSimplePacket(testRawSocket);
        } finally {
            testRawSocket.close();
            PinpointRPCTestUtils.close(serverAcceptor);
        }
    }

    @Test
    public void registerAgentFailTest() throws Exception {
        EventHandlerTest.ThrowExceptionEventHandler eventHandler = new EventHandlerTest.ThrowExceptionEventHandler();
        PinpointServerAcceptor serverAcceptor = new PinpointServerAcceptor();
        serverAcceptor.addStateChangeEventHandler(eventHandler);
        serverAcceptor.setMessageListenerFactory(testServerMessageListenerFactory);
        serverAcceptor.bind("127.0.0.1", EventHandlerTest.bindPort);
        TestRawSocket testRawSocket = new TestRawSocket();
        try {
            testRawSocket.connect(EventHandlerTest.bindPort);
            sendAndReceiveSimplePacket(testRawSocket);
            Assert.assertTrue(((eventHandler.getErrorCount()) > 0));
        } finally {
            testRawSocket.close();
            PinpointRPCTestUtils.close(serverAcceptor);
        }
    }

    class EventHandler implements ServerStateChangeEventHandler {
        private SocketStateCode code;

        @Override
        public void eventPerformed(PinpointServer pinpointServer, SocketStateCode stateCode) {
            this.code = stateCode;
        }

        @Override
        public void exceptionCaught(PinpointServer pinpointServer, SocketStateCode stateCode, Throwable e) {
        }

        public SocketStateCode getCode() {
            return code;
        }
    }

    class ThrowExceptionEventHandler implements ServerStateChangeEventHandler {
        private int errorCount = 0;

        @Override
        public void eventPerformed(PinpointServer pinpointServer, SocketStateCode stateCode) throws Exception {
            throw new Exception("always error.");
        }

        @Override
        public void exceptionCaught(PinpointServer pinpointServer, SocketStateCode stateCode, Throwable e) {
            (errorCount)++;
        }

        public int getErrorCount() {
            return errorCount;
        }
    }
}

