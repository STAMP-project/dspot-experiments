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
package com.navercorp.pinpoint.rpc.stream;


import com.navercorp.pinpoint.rpc.PinpointSocket;
import com.navercorp.pinpoint.rpc.PinpointSocketException;
import com.navercorp.pinpoint.rpc.RecordedStreamChannelMessageListener;
import com.navercorp.pinpoint.rpc.client.SimpleMessageListener;
import com.navercorp.pinpoint.rpc.packet.stream.StreamClosePacket;
import com.navercorp.pinpoint.rpc.packet.stream.StreamCode;
import com.navercorp.pinpoint.rpc.packet.stream.StreamCreateFailPacket;
import com.navercorp.pinpoint.rpc.packet.stream.StreamCreatePacket;
import com.navercorp.pinpoint.rpc.server.PinpointServer;
import com.navercorp.pinpoint.test.client.TestPinpointClient;
import com.navercorp.pinpoint.test.server.TestPinpointServerAcceptor;
import com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory;
import com.navercorp.pinpoint.test.utils.TestAwaitTaskUtils;
import com.navercorp.pinpoint.test.utils.TestAwaitUtils;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Assert;
import org.junit.Test;

import static com.navercorp.pinpoint.test.server.TestServerMessageListenerFactory.HandshakeType.DUPLEX;


public class StreamChannelManagerTest {
    private final TestAwaitUtils awaitUtils = new TestAwaitUtils(10, 1000);

    private final TestServerMessageListenerFactory testServerMessageListenerFactory = new TestServerMessageListenerFactory(DUPLEX);

    // Client to Server Stream
    @Test
    public void streamSuccessTest1() throws IOException, InterruptedException {
        StreamChannelManagerTest.SimpleStreamBO bo = new StreamChannelManagerTest.SimpleStreamBO();
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory, new StreamChannelManagerTest.ServerListener(bo));
        int bindPort = testPinpointServerAcceptor.bind();
        TestPinpointClient testPinpointClient = new TestPinpointClient();
        try {
            testPinpointClient.connect(bindPort);
            RecordedStreamChannelMessageListener clientListener = new RecordedStreamChannelMessageListener(4);
            ClientStreamChannelContext clientContext = testPinpointClient.openStream(new byte[0], clientListener);
            int sendCount = 4;
            for (int i = 0; i < sendCount; i++) {
                sendRandomBytes(bo);
            }
            clientListener.getLatch().await();
            Assert.assertEquals(sendCount, clientListener.getReceivedMessage().size());
            clientContext.getStreamChannel().close();
        } finally {
            testPinpointClient.closeAll();
            testPinpointServerAcceptor.close();
        }
    }

    // Client to Server Stream
    @Test
    public void streamSuccessTest2() throws IOException, InterruptedException {
        StreamChannelManagerTest.SimpleStreamBO bo = new StreamChannelManagerTest.SimpleStreamBO();
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory, new StreamChannelManagerTest.ServerListener(bo));
        int bindPort = testPinpointServerAcceptor.bind();
        TestPinpointClient testPinpointClient = new TestPinpointClient();
        try {
            testPinpointClient.connect(bindPort);
            RecordedStreamChannelMessageListener clientListener = new RecordedStreamChannelMessageListener(4);
            ClientStreamChannelContext clientContext = testPinpointClient.openStream(new byte[0], clientListener);
            RecordedStreamChannelMessageListener clientListener2 = new RecordedStreamChannelMessageListener(8);
            ClientStreamChannelContext clientContext2 = testPinpointClient.openStream(new byte[0], clientListener2);
            int sendCount = 4;
            for (int i = 0; i < sendCount; i++) {
                sendRandomBytes(bo);
            }
            clientListener.getLatch().await();
            Assert.assertEquals(sendCount, clientListener.getReceivedMessage().size());
            clientContext.getStreamChannel().close();
            sendCount = 4;
            for (int i = 0; i < sendCount; i++) {
                sendRandomBytes(bo);
            }
            clientListener2.getLatch().await();
            Assert.assertEquals(sendCount, clientListener.getReceivedMessage().size());
            Assert.assertEquals(8, clientListener2.getReceivedMessage().size());
            clientContext2.getStreamChannel().close();
        } finally {
            testPinpointClient.closeAll();
            testPinpointServerAcceptor.close();
        }
    }

    @Test
    public void streamSuccessTest3() throws IOException, InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
        int bindPort = testPinpointServerAcceptor.bind();
        StreamChannelManagerTest.SimpleStreamBO bo = new StreamChannelManagerTest.SimpleStreamBO();
        TestPinpointClient testPinpointClient = new TestPinpointClient(SimpleMessageListener.INSTANCE, new StreamChannelManagerTest.ServerListener(bo));
        try {
            testPinpointClient.connect(bindPort);
            testPinpointServerAcceptor.assertAwaitClientConnected(1000);
            List<PinpointSocket> writableServerList = testPinpointServerAcceptor.getConnectedPinpointSocketList();
            Assert.assertEquals(1, writableServerList.size());
            PinpointSocket writableServer = writableServerList.get(0);
            RecordedStreamChannelMessageListener clientListener = new RecordedStreamChannelMessageListener(4);
            if (writableServer instanceof PinpointServer) {
                ClientStreamChannelContext clientContext = openStream(new byte[0], clientListener);
                int sendCount = 4;
                for (int i = 0; i < sendCount; i++) {
                    sendRandomBytes(bo);
                }
                clientListener.getLatch().await();
                Assert.assertEquals(sendCount, clientListener.getReceivedMessage().size());
                clientContext.getStreamChannel().close();
            } else {
                Assert.fail();
            }
        } finally {
            testPinpointClient.closeAll();
            testPinpointServerAcceptor.close();
        }
    }

    @Test
    public void streamClosedTest1() throws IOException, InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
        int bindPort = testPinpointServerAcceptor.bind();
        TestPinpointClient testPinpointClient = new TestPinpointClient();
        try {
            testPinpointClient.connect(bindPort);
            RecordedStreamChannelMessageListener clientListener = new RecordedStreamChannelMessageListener(4);
            ClientStreamChannelContext clientContext = testPinpointClient.openStream(new byte[0], clientListener);
            StreamCreateFailPacket createFailPacket = clientContext.getCreateFailPacket();
            if (createFailPacket == null) {
                Assert.fail();
            }
            clientContext.getStreamChannel().close();
        } finally {
            testPinpointClient.closeAll();
            testPinpointServerAcceptor.close();
        }
    }

    @Test
    public void streamClosedTest2() throws IOException, InterruptedException {
        final StreamChannelManagerTest.SimpleStreamBO bo = new StreamChannelManagerTest.SimpleStreamBO();
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory, new StreamChannelManagerTest.ServerListener(bo));
        int bindPort = testPinpointServerAcceptor.bind();
        TestPinpointClient testPinpointClient = new TestPinpointClient();
        try {
            testPinpointClient.connect(bindPort);
            RecordedStreamChannelMessageListener clientListener = new RecordedStreamChannelMessageListener(4);
            ClientStreamChannelContext clientContext = testPinpointClient.openStream(new byte[0], clientListener);
            Assert.assertEquals(1, bo.getStreamChannelContextSize());
            clientContext.getStreamChannel().close();
            awaitUtils.await(new TestAwaitTaskUtils() {
                @Override
                public boolean checkCompleted() {
                    return (bo.getStreamChannelContextSize()) == 0;
                }
            });
            Assert.assertEquals(0, bo.getStreamChannelContextSize());
        } finally {
            testPinpointClient.closeAll();
            testPinpointServerAcceptor.close();
        }
    }

    // ServerSocket to Client Stream
    // ServerStreamChannel first close.
    @Test(expected = PinpointSocketException.class)
    public void streamClosedTest3() throws IOException, InterruptedException {
        TestPinpointServerAcceptor testPinpointServerAcceptor = new TestPinpointServerAcceptor(testServerMessageListenerFactory);
        int bindPort = testPinpointServerAcceptor.bind();
        StreamChannelManagerTest.SimpleStreamBO bo = new StreamChannelManagerTest.SimpleStreamBO();
        TestPinpointClient testPinpointClient = new TestPinpointClient(SimpleMessageListener.INSTANCE, new StreamChannelManagerTest.ServerListener(bo));
        testPinpointClient.connect(bindPort);
        try {
            testPinpointServerAcceptor.assertAwaitClientConnected(1000);
            List<PinpointSocket> writableServerList = testPinpointServerAcceptor.getConnectedPinpointSocketList();
            Assert.assertEquals(1, writableServerList.size());
            PinpointSocket writableServer = writableServerList.get(0);
            if (writableServer instanceof PinpointServer) {
                RecordedStreamChannelMessageListener clientListener = new RecordedStreamChannelMessageListener(4);
                ClientStreamChannelContext clientContext = openStream(new byte[0], clientListener);
                StreamChannelContext streamChannelContext = testPinpointClient.findStreamChannel(2);
                streamChannelContext.getStreamChannel().close();
                sendRandomBytes(bo);
                Thread.sleep(100);
                clientContext.getStreamChannel().close();
            } else {
                Assert.fail();
            }
        } finally {
            testPinpointClient.closeAll();
            testPinpointServerAcceptor.close();
        }
    }

    class ServerListener implements ServerStreamChannelMessageListener {
        private final StreamChannelManagerTest.SimpleStreamBO bo;

        public ServerListener(StreamChannelManagerTest.SimpleStreamBO bo) {
            this.bo = bo;
        }

        @Override
        public StreamCode handleStreamCreate(ServerStreamChannelContext streamChannelContext, StreamCreatePacket packet) {
            bo.addServerStreamChannelContext(streamChannelContext);
            return StreamCode.OK;
        }

        @Override
        public void handleStreamClose(ServerStreamChannelContext streamChannelContext, StreamClosePacket packet) {
            bo.removeServerStreamChannelContext(streamChannelContext);
        }
    }

    class SimpleStreamBO {
        private final List<ServerStreamChannelContext> serverStreamChannelContextList;

        public SimpleStreamBO() {
            serverStreamChannelContextList = new CopyOnWriteArrayList<ServerStreamChannelContext>();
        }

        public void addServerStreamChannelContext(ServerStreamChannelContext context) {
            serverStreamChannelContextList.add(context);
        }

        public void removeServerStreamChannelContext(ServerStreamChannelContext context) {
            serverStreamChannelContextList.remove(context);
        }

        void sendResponse(byte[] data) {
            for (ServerStreamChannelContext context : serverStreamChannelContextList) {
                context.getStreamChannel().sendData(data);
            }
        }

        int getStreamChannelContextSize() {
            return serverStreamChannelContextList.size();
        }
    }
}

