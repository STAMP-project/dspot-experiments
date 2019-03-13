/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.client.channel;


import ServerType.OPERATIONS;
import SyncResponseResultType.SUCCESS;
import TransportProtocolIdConstants.TCP_TRANSPORT_ID;
import TransportType.EVENT;
import TransportType.PROFILE;
import TransportType.USER;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.channel.connectivity.ConnectivityChecker;
import org.kaaproject.kaa.client.channel.failover.FailoverManager;
import org.kaaproject.kaa.client.channel.impl.channels.DefaultOperationTcpChannel;
import org.kaaproject.kaa.client.persistence.KaaClientState;
import org.kaaproject.kaa.common.TransportType;
import org.kaaproject.kaa.common.avro.AvroByteArrayConverter;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.PingResponse;
import org.kaaproject.kaa.common.endpoint.gen.SyncRequest;
import org.kaaproject.kaa.common.endpoint.gen.SyncResponse;
import org.kaaproject.kaa.common.endpoint.security.KeyUtil;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultOperationTcpChannelTest {
    private final KeyPair clientKeys;

    public DefaultOperationTcpChannelTest() throws Exception {
        clientKeys = KeyUtil.generateKeyPair();
    }

    @Test
    public void testDefaultOperationTcpChannel() {
        KaaClientState clientState = Mockito.mock(KaaClientState.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        KaaDataChannel tcpChannel = new DefaultOperationTcpChannel(clientState, failoverManager, null);
        Assert.assertNotNull("New channel's id is null", tcpChannel.getId());
        Assert.assertNotNull("New channel does not support any of transport types", tcpChannel.getSupportedTransportTypes());
        Assert.assertNotEquals(0, tcpChannel.getSupportedTransportTypes().size());
    }

    @Test
    public void testSync() throws Exception {
        KaaClientState clientState = Mockito.mock(KaaClientState.class);
        Mockito.when(clientState.getPrivateKey()).thenReturn(clientKeys.getPrivate());
        Mockito.when(clientState.getPublicKey()).thenReturn(clientKeys.getPublic());
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        DefaultOperationTcpChannelTest.TestOperationTcpChannel tcpChannel = new DefaultOperationTcpChannelTest.TestOperationTcpChannel(clientState, failoverManager);
        AvroByteArrayConverter<SyncResponse> responseCreator = new AvroByteArrayConverter<SyncResponse>(SyncResponse.class);
        AvroByteArrayConverter<SyncRequest> requestCreator = new AvroByteArrayConverter<SyncRequest>(SyncRequest.class);
        KaaDataMultiplexer multiplexer = Mockito.mock(KaaDataMultiplexer.class);
        Mockito.when(multiplexer.compileRequest(Mockito.anyMapOf(TransportType.class, ChannelDirection.class))).thenReturn(requestCreator.toByteArray(new SyncRequest()));
        KaaDataDemultiplexer demultiplexer = Mockito.mock(KaaDataDemultiplexer.class);
        tcpChannel.setMultiplexer(multiplexer);
        tcpChannel.setDemultiplexer(demultiplexer);
        tcpChannel.sync(USER);
        // will cause call to KaaDataMultiplexer.compileRequest(...) after "CONNECT" messsage
        tcpChannel.sync(PROFILE);
        TransportConnectionInfo server = IpTransportInfoTest.createTestServerInfo(OPERATIONS, TCP_TRANSPORT_ID, "localhost", 9009, KeyUtil.generateKeyPair().getPublic());
        tcpChannel.setServer(server);// causes call to KaaDataMultiplexer.compileRequest(...) for "CONNECT" messsage

        byte[] rawConnack = new byte[]{ 32, 2, 0, 1 };
        tcpChannel.os.write(rawConnack);
        SyncResponse response = new SyncResponse();
        response.setStatus(SUCCESS);
        tcpChannel.os.write(getFrame().array());
        Thread.sleep(1000);// sleep a bit to let the message to be received

        tcpChannel.sync(USER);// causes call to KaaDataMultiplexer.compileRequest(...) for "KAA_SYNC" messsage

        Mockito.verify(multiplexer, Mockito.times(2)).compileRequest(Mockito.anyMapOf(TransportType.class, ChannelDirection.class));
        tcpChannel.sync(EVENT);
        Mockito.verify(multiplexer, Mockito.times(3)).compileRequest(Mockito.anyMapOf(TransportType.class, ChannelDirection.class));
        Mockito.verify(tcpChannel.socketMock, Mockito.times(3)).getOutputStream();
        Mockito.reset(multiplexer);
        tcpChannel.os.write(new PingResponse().getFrame().array());
        syncAll();
        Mockito.verify(multiplexer, Mockito.times(1)).compileRequest(getSupportedTransportTypes());
        tcpChannel.os.write(getFrame().array());
        syncAll();
        Mockito.verify(multiplexer, Mockito.times(1)).compileRequest(getSupportedTransportTypes());
        tcpChannel.shutdown();
    }

    @Test
    public void testConnectivity() throws NoSuchAlgorithmException {
        KaaClientState clientState = Mockito.mock(KaaClientState.class);
        Mockito.when(clientState.getPrivateKey()).thenReturn(clientKeys.getPrivate());
        Mockito.when(clientState.getPublicKey()).thenReturn(clientKeys.getPublic());
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        DefaultOperationTcpChannel channel = new DefaultOperationTcpChannel(clientState, failoverManager, null);
        TransportConnectionInfo server = IpTransportInfoTest.createTestServerInfo(OPERATIONS, TCP_TRANSPORT_ID, "www.test.fake", 999, KeyUtil.generateKeyPair().getPublic());
        ConnectivityChecker checker = Mockito.mock(ConnectivityChecker.class);
        Mockito.when(checker.checkConnectivity()).thenReturn(false);
        channel.setConnectivityChecker(checker);
    }

    class TestOperationTcpChannel extends DefaultOperationTcpChannel {
        public Socket socketMock;

        public OutputStream os;

        public InputStream is;

        public TestOperationTcpChannel(KaaClientState state, FailoverManager failoverManager) throws IOException {
            super(state, failoverManager, null);
            PipedInputStream in = new PipedInputStream(4096);
            PipedOutputStream out = new PipedOutputStream(in);
            os = out;
            is = in;
            socketMock = Mockito.mock(Socket.class);
            Mockito.when(socketMock.getOutputStream()).thenReturn(os);
            Mockito.when(socketMock.getInputStream()).thenReturn(is);
        }

        @Override
        protected Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return socketMock;
        }

        @Override
        public void shutdown() {
            super.shutdown();
        }
    }
}

