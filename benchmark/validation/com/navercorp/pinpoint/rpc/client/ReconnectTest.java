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


import com.navercorp.pinpoint.rpc.Future;
import com.navercorp.pinpoint.rpc.PinpointSocketException;
import com.navercorp.pinpoint.rpc.ResponseMessage;
import com.navercorp.pinpoint.rpc.TestByteUtils;
import com.navercorp.pinpoint.rpc.util.PinpointRPCTestUtils;
import com.navercorp.pinpoint.test.server.TestPinpointServerAcceptor;
import com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory;
import com.navercorp.pinpoint.test.utils.TestAwaitUtils;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import static com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory.HandshakeType.DUPLEX;


/**
 *
 *
 * @author emeroad
 */
// @Ignore
public class ReconnectTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static PinpointClientFactory clientFactory;

    private final TestServerMessageListenerFactory testServerMessageListenerFactory = new TestServerMessageListenerFactory(DUPLEX);

    private final TestAwaitUtils awaitUtils = new TestAwaitUtils(100, 1000);

    @Test
    public void reconnect() throws IOException, InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor();
        int bindPort = testPinpointServerAcceptor.bind();
        final AtomicBoolean reconnectPerformed = new AtomicBoolean(false);
        TestPinpointServerAcceptor newTestPinpointServerAcceptor = null;
        try {
            PinpointClient client = ReconnectTest.clientFactory.connect("localhost", bindPort);
            client.addPinpointClientReconnectEventListener(new PinpointClientReconnectEventListener() {
                @Override
                public void reconnectPerformed(PinpointClient client) {
                    reconnectPerformed.set(true);
                }
            });
            testPinpointServerAcceptor.close();
            logger.debug("server.close");
            assertClientDisconnected(client);
            newTestPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
            newTestPinpointServerAcceptor.bind(bindPort);
            logger.debug("bind server");
            assertClientConnected(client);
            logger.debug("request server");
            byte[] randomByte = TestByteUtils.createRandomByte(10);
            byte[] response = PinpointRPCTestUtils.request(client, randomByte);
            Assert.assertArrayEquals(randomByte, response);
            PinpointRPCTestUtils.close(client);
        } finally {
            TestPinpointServerAcceptor.staticClose(newTestPinpointServerAcceptor);
        }
        Assert.assertTrue(reconnectPerformed.get());
    }

    @Test
    public void scheduledConnect() throws IOException, InterruptedException {
        final PinpointClientFactory clientFactory = new DefaultPinpointClientFactory();
        clientFactory.setReconnectDelay(200);
        PinpointClient client = null;
        TestPinpointServerAcceptor testPinpointServerAcceptor = null;
        try {
            int availableTcpPort = SocketUtils.findAvailableTcpPort(47000);
            client = clientFactory.scheduledConnect("localhost", availableTcpPort);
            testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
            testPinpointServerAcceptor.bind(availableTcpPort);
            assertClientConnected(client);
            logger.debug("request server");
            byte[] randomByte = TestByteUtils.createRandomByte(10);
            byte[] response = PinpointRPCTestUtils.request(client, randomByte);
            Assert.assertArrayEquals(randomByte, response);
        } finally {
            PinpointRPCTestUtils.close(client);
            clientFactory.release();
            TestPinpointServerAcceptor.staticClose(testPinpointServerAcceptor);
        }
    }

    @Test
    public void scheduledConnectAndClosed() throws IOException, InterruptedException {
        int availableTcpPort = SocketUtils.findAvailableTcpPort(47000);
        PinpointClient client = ReconnectTest.clientFactory.scheduledConnect("localhost", availableTcpPort);
        logger.debug("close");
        PinpointRPCTestUtils.close(client);
    }

    @Test
    public void scheduledConnectDelayAndClosed() throws IOException, InterruptedException {
        int availableTcpPort = SocketUtils.findAvailableTcpPort(47000);
        PinpointClient client = ReconnectTest.clientFactory.scheduledConnect("localhost", availableTcpPort);
        Thread.sleep(2000);
        logger.debug("close pinpoint client");
        PinpointRPCTestUtils.close(client);
    }

    @Test
    public void scheduledConnectStateTest() {
        int availableTcpPort = SocketUtils.findAvailableTcpPort(47000);
        PinpointClient client = ReconnectTest.clientFactory.scheduledConnect("localhost", availableTcpPort);
        client.send(new byte[10]);
        try {
            Future future = client.sendAsync(new byte[10]);
            future.await();
            future.getResult();
            Assert.fail();
        } catch (PinpointSocketException e) {
        }
        try {
            client.sendSync(new byte[10]);
            Assert.fail();
        } catch (PinpointSocketException e) {
        }
        try {
            PinpointRPCTestUtils.request(client, new byte[10]);
            Assert.fail();
        } catch (PinpointSocketException e) {
        }
        PinpointRPCTestUtils.close(client);
    }

    @Test
    public void serverFirstClose() throws IOException, InterruptedException {
        // when abnormal case in which server has been closed first, confirm that a socket should be closed properly.
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor();
        int bindPort = testPinpointServerAcceptor.bind();
        PinpointClient client = ReconnectTest.clientFactory.connect("127.0.0.1", bindPort);
        byte[] randomByte = TestByteUtils.createRandomByte(10);
        Future<ResponseMessage> response = client.request(randomByte);
        response.await();
        try {
            response.getResult();
        } catch (Exception e) {
            logger.debug("timeout.", e);
        }
        // close server by force
        testPinpointServerAcceptor.close();
        assertClientDisconnected(client);
        PinpointRPCTestUtils.close(client);
    }

    @Test
    public void serverCloseAndWrite() throws IOException, InterruptedException {
        // when abnormal case in which server has been closed first, confirm that a client socket should be closed properly.
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor();
        int bindPort = testPinpointServerAcceptor.bind();
        PinpointClient client = ReconnectTest.clientFactory.connect("127.0.0.1", bindPort);
        // just close server and request
        testPinpointServerAcceptor.close();
        byte[] randomByte = TestByteUtils.createRandomByte(10);
        Future<ResponseMessage> response = client.request(randomByte);
        response.await();
        try {
            response.getResult();
            Assert.fail("expected exception");
        } catch (Exception e) {
        }
        assertClientDisconnected(client);
        PinpointRPCTestUtils.close(client);
    }
}

