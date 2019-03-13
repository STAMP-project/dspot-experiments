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
package org.kaaproject.kaa.client.bootstrap;


import FailoverStatus.NO_CONNECTIVITY;
import TransportProtocolIdConstants.HTTP_TRANSPORT_ID;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.channel.BootstrapTransport;
import org.kaaproject.kaa.client.channel.IpTransportInfoTest;
import org.kaaproject.kaa.client.channel.KaaDataChannel;
import org.kaaproject.kaa.client.channel.KaaDataDemultiplexer;
import org.kaaproject.kaa.client.channel.KaaDataMultiplexer;
import org.kaaproject.kaa.client.channel.KaaInternalChannelManager;
import org.kaaproject.kaa.client.channel.KaaInvalidChannelException;
import org.kaaproject.kaa.client.channel.TransportConnectionInfo;
import org.kaaproject.kaa.client.channel.connectivity.ConnectivityChecker;
import org.kaaproject.kaa.client.channel.failover.FailoverManager;
import org.kaaproject.kaa.client.channel.failover.FailoverStatus;
import org.kaaproject.kaa.client.channel.failover.strategies.DefaultFailoverStrategy;
import org.kaaproject.kaa.client.channel.failover.strategies.FailoverStrategy;
import org.kaaproject.kaa.client.context.ExecutorContext;
import org.kaaproject.kaa.client.transport.TransportException;
import org.kaaproject.kaa.common.TransportType;
import org.kaaproject.kaa.common.endpoint.gen.ProtocolMetaData;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultBootstrapManagerTest {
    @Test
    public void testReceiveOperationsServerList() throws TransportException {
        BootstrapTransport transport = Mockito.mock(BootstrapTransport.class);
        DefaultBootstrapManager manager = new DefaultBootstrapManager(transport, null, null);
        boolean exception = false;
        try {
            manager.receiveOperationsServerList();
            manager.useNextOperationsServer(HTTP_TRANSPORT_ID, NO_CONNECTIVITY);
        } catch (BootstrapRuntimeException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        manager.receiveOperationsServerList();
        Mockito.verify(transport, Mockito.times(2)).sync();
    }

    @Test
    public void testOperationsServerInfoRetrieving() throws NoSuchAlgorithmException, InvalidKeySpecException, TransportException {
        ExecutorContext executorContext = Mockito.mock(ExecutorContext.class);
        DefaultBootstrapManager manager = new DefaultBootstrapManager(null, executorContext, null);
        boolean exception = false;
        try {
            manager.useNextOperationsServer(HTTP_TRANSPORT_ID, NO_CONNECTIVITY);
        } catch (BootstrapRuntimeException e) {
            exception = true;
        }
        Assert.assertTrue(exception);
        BootstrapTransport transport = Mockito.mock(BootstrapTransport.class);
        // Generating pseudo bootstrap key
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.genKeyPair();
        List<ProtocolMetaData> list = new ArrayList<ProtocolMetaData>();
        ProtocolMetaData md = IpTransportInfoTest.buildMetaData(HTTP_TRANSPORT_ID, "localhost", 9889, keyPair.getPublic());
        list.add(md);
        DefaultBootstrapManagerTest.ChanelManagerMock channelManager = Mockito.spy(new DefaultBootstrapManagerTest.ChanelManagerMock());
        Mockito.when(executorContext.getScheduledExecutor()).thenReturn(Executors.newScheduledThreadPool(1));
        FailoverStrategy strategy = new DefaultFailoverStrategy(1, 1, 1, TimeUnit.MILLISECONDS);
        FailoverManager failoverManager = Mockito.spy(new org.kaaproject.kaa.client.channel.failover.DefaultFailoverManager(channelManager, executorContext, strategy, 1, TimeUnit.MILLISECONDS));
        manager.setChannelManager(channelManager);
        manager.setFailoverManager(failoverManager);
        manager.setTransport(transport);
        manager.onProtocolListUpdated(list);
        manager.useNextOperationsServer(HTTP_TRANSPORT_ID, NO_CONNECTIVITY);
        Assert.assertTrue(channelManager.isServerUpdated());
        Assert.assertEquals("http://localhost:9889", channelManager.getReceivedUrl());
        manager.useNextOperationsServerByAccessPointId("some.name".hashCode());
        Mockito.verify(channelManager, Mockito.times(1)).onTransportConnectionInfoUpdated(ArgumentMatchers.any(TransportConnectionInfo.class));
    }

    @Test
    public void testUseServerByDnsName() throws NoSuchAlgorithmException {
        DefaultBootstrapManager manager = new DefaultBootstrapManager(null, null, null);
        DefaultBootstrapManagerTest.ChanelManagerMock channelManager = Mockito.spy(new DefaultBootstrapManagerTest.ChanelManagerMock());
        manager.setChannelManager(channelManager);
        BootstrapTransport transport = Mockito.mock(BootstrapTransport.class);
        manager.setTransport(transport);
        // Generating pseudo operation key
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.genKeyPair();
        List<ProtocolMetaData> list = new ArrayList<ProtocolMetaData>();
        ProtocolMetaData md = IpTransportInfoTest.buildMetaData(HTTP_TRANSPORT_ID, "localhost", 9889, keyPair.getPublic());
        list.add(md);
        manager.onProtocolListUpdated(list);
        Assert.assertEquals("http://localhost:9889", channelManager.getReceivedUrl());
        manager.useNextOperationsServerByAccessPointId("localhost2:9889".hashCode());
        Mockito.verify(transport, Mockito.times(1)).sync();
        list = new ArrayList<ProtocolMetaData>();
        md = IpTransportInfoTest.buildMetaData(HTTP_TRANSPORT_ID, "localhost2", 9889, keyPair.getPublic());
        list.add(md);
        manager.onProtocolListUpdated(list);
        Assert.assertEquals("http://localhost2:9889", channelManager.getReceivedUrl());
        Assert.assertTrue(channelManager.isServerUpdated());
    }

    public class ChanelManagerMock implements KaaInternalChannelManager {
        private boolean serverUpdated = false;

        private String receivedUrl;

        public ChanelManagerMock() {
        }

        public String getReceivedUrl() {
            return receivedUrl;
        }

        public boolean isServerUpdated() {
            return serverUpdated;
        }

        @Override
        public void setConnectivityChecker(ConnectivityChecker checker) {
        }

        @Override
        public void addChannel(KaaDataChannel channel) {
        }

        @Override
        public void removeChannel(KaaDataChannel channel) {
        }

        @Override
        public List<KaaDataChannel> getChannels() {
            return null;
        }

        @Override
        public KaaDataChannel getChannel(String id) {
            return null;
        }

        @Override
        public void onServerFailed(TransportConnectionInfo server, FailoverStatus status) {
        }

        @Override
        public void setFailoverManager(FailoverManager failoverManager) {
        }

        @Override
        public void onTransportConnectionInfoUpdated(TransportConnectionInfo newServer) {
            receivedUrl = getUrl();
            serverUpdated = true;
        }

        @Override
        public void clearChannelList() {
        }

        @Override
        public void setChannel(TransportType transport, KaaDataChannel channel) throws KaaInvalidChannelException {
        }

        @Override
        public void removeChannel(String id) {
        }

        @Override
        public void shutdown() {
        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }

        @Override
        public void setOperationMultiplexer(KaaDataMultiplexer multiplexer) {
            // TODO Auto-generated method stub
        }

        @Override
        public void setOperationDemultiplexer(KaaDataDemultiplexer demultiplexer) {
            // TODO Auto-generated method stub
        }

        @Override
        public void setBootstrapMultiplexer(KaaDataMultiplexer multiplexer) {
            // TODO Auto-generated method stub
        }

        @Override
        public void setBootstrapDemultiplexer(KaaDataDemultiplexer demultiplexer) {
            // TODO Auto-generated method stub
        }

        @Override
        public void sync(TransportType type) {
            // TODO Auto-generated method stub
        }

        @Override
        public void syncAck(TransportType type) {
            // TODO Auto-generated method stub
        }

        @Override
        public void syncAll(TransportType type) {
            // TODO Auto-generated method stub
        }

        @Override
        public TransportConnectionInfo getActiveServer(TransportType logging) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}

