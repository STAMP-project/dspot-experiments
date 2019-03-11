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


import ChannelDirection.BIDIRECTIONAL;
import TransportProtocolIdConstants.HTTP_TRANSPORT_ID;
import TransportType.BOOTSTRAP;
import TransportType.CONFIGURATION;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.AbstractKaaClient;
import org.kaaproject.kaa.client.channel.failover.FailoverManager;
import org.kaaproject.kaa.client.channel.impl.channels.DefaultBootstrapChannel;
import org.kaaproject.kaa.client.persistence.KaaClientState;
import org.kaaproject.kaa.client.transport.AbstractHttpClient;
import org.kaaproject.kaa.common.endpoint.security.KeyUtil;
import org.kaaproject.kaa.common.endpoint.security.MessageEncoderDecoder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultBootstrapChannelTest {
    public ExecutorService fakeExecutor = new FakeExecutorService();

    @Test
    public void testChannelGetters() {
        AbstractKaaClient client = Mockito.mock(AbstractKaaClient.class);
        KaaClientState state = Mockito.mock(KaaClientState.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        KaaDataChannel channel = new DefaultBootstrapChannel(client, state, failoverManager);
        Assert.assertEquals(BIDIRECTIONAL, channel.getSupportedTransportTypes().get(BOOTSTRAP));
        Assert.assertEquals(HTTP_TRANSPORT_ID, channel.getTransportProtocolId());
        Assert.assertEquals("default_bootstrap_channel", channel.getId());
    }

    @Test
    public void testChannelSync() throws Exception {
        KaaChannelManager manager = Mockito.mock(KaaChannelManager.class);
        AbstractHttpClient httpClient = Mockito.mock(AbstractHttpClient.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        Mockito.when(httpClient.executeHttpRequest(Mockito.anyString(), Mockito.any(LinkedHashMap.class), Mockito.anyBoolean())).thenReturn(new byte[]{ 5, 5, 5 });
        AbstractKaaClient client = Mockito.mock(AbstractKaaClient.class);
        Mockito.when(client.createHttpClient(Mockito.anyString(), Mockito.any(PrivateKey.class), Mockito.any(PublicKey.class), Mockito.any(PublicKey.class))).thenReturn(httpClient);
        Mockito.when(client.getChannelManager()).thenReturn(manager);
        KaaClientState state = Mockito.mock(KaaClientState.class);
        KaaDataMultiplexer multiplexer = Mockito.mock(KaaDataMultiplexer.class);
        KaaDataDemultiplexer demultiplexer = Mockito.mock(KaaDataDemultiplexer.class);
        DefaultBootstrapChannelTest.DefaultBootstrapChannelMock channel = new DefaultBootstrapChannelTest.DefaultBootstrapChannelMock(client, state, failoverManager, 2);
        TransportConnectionInfo server = IpTransportInfoTest.createTestServerInfo(ServerType.BOOTSTRAP, HTTP_TRANSPORT_ID, "localhost", 9889, KeyUtil.generateKeyPair().getPublic());
        channel.setServer(server);
        channel.sync(BOOTSTRAP);
        channel.setDemultiplexer(demultiplexer);
        setDemultiplexer(null);
        channel.sync(BOOTSTRAP);
        channel.setMultiplexer(multiplexer);
        setMultiplexer(null);
        channel.sync(CONFIGURATION);
        channel.sync(BOOTSTRAP);
        channel.verify();
    }

    @Test
    public void testShutdown() throws Exception {
        KaaChannelManager manager = Mockito.mock(KaaChannelManager.class);
        AbstractHttpClient httpClient = Mockito.mock(AbstractHttpClient.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        Mockito.when(httpClient.executeHttpRequest(Mockito.anyString(), Mockito.any(LinkedHashMap.class), Mockito.anyBoolean())).thenThrow(new Exception());
        AbstractKaaClient client = Mockito.mock(AbstractKaaClient.class);
        Mockito.when(client.createHttpClient(Mockito.anyString(), Mockito.any(PrivateKey.class), Mockito.any(PublicKey.class), Mockito.any(PublicKey.class))).thenReturn(httpClient);
        Mockito.when(client.getChannelManager()).thenReturn(manager);
        KaaClientState state = Mockito.mock(KaaClientState.class);
        KaaDataMultiplexer multiplexer = Mockito.mock(KaaDataMultiplexer.class);
        KaaDataDemultiplexer demultiplexer = Mockito.mock(KaaDataDemultiplexer.class);
        DefaultBootstrapChannelTest.DefaultBootstrapChannelFake channel = new DefaultBootstrapChannelTest.DefaultBootstrapChannelFake(client, state, failoverManager, 0);
        channel.setDemultiplexer(demultiplexer);
        channel.setMultiplexer(multiplexer);
        shutdown();
        TransportConnectionInfo server = IpTransportInfoTest.createTestServerInfo(ServerType.BOOTSTRAP, HTTP_TRANSPORT_ID, "localhost", 9889, KeyUtil.generateKeyPair().getPublic());
        channel.setServer(server);
        channel.sync(BOOTSTRAP);
        syncAll();
        channel.verify();
    }

    class DefaultBootstrapChannelFake extends DefaultBootstrapChannel {
        private final int wantedNumberOfInvocations;

        public DefaultBootstrapChannelFake(AbstractKaaClient client, KaaClientState state, FailoverManager failoverManager, int wantedNumberOfInvocations) {
            super(client, state, failoverManager);
            this.wantedNumberOfInvocations = wantedNumberOfInvocations;
        }

        @Override
        protected ExecutorService createExecutor() {
            super.createExecutor();
            return fakeExecutor;
        }

        public void verify() throws Exception {
            Mockito.verify(getMultiplexer(), Mockito.times(wantedNumberOfInvocations)).compileRequest(Mockito.anyMap());
            Mockito.verify(getDemultiplexer(), Mockito.times(wantedNumberOfInvocations)).processResponse(Mockito.eq(new byte[]{ 5, 5, 5 }));
        }
    }

    class DefaultBootstrapChannelMock extends DefaultBootstrapChannelTest.DefaultBootstrapChannelFake {
        public DefaultBootstrapChannelMock(AbstractKaaClient client, KaaClientState state, FailoverManager failoverManager, int wantedNumberOfInvocations) {
            super(client, state, failoverManager, wantedNumberOfInvocations);
        }

        protected AbstractHttpClient getHttpClient() {
            AbstractHttpClient client = Mockito.mock(AbstractHttpClient.class);
            MessageEncoderDecoder crypt = Mockito.mock(MessageEncoderDecoder.class);
            try {
                Mockito.when(crypt.decodeData(Mockito.any(byte[].class))).thenReturn(new byte[]{ 5, 5, 5 });
            } catch (GeneralSecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Mockito.when(client.getEncoderDecoder()).thenReturn(crypt);
            return client;
        }
    }
}

