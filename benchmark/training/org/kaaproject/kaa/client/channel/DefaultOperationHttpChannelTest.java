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


import ChannelDirection.UP;
import ServerType.OPERATIONS;
import TransportProtocolIdConstants.HTTP_TRANSPORT_ID;
import TransportType.BOOTSTRAP;
import TransportType.EVENT;
import TransportType.LOGGING;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.client.AbstractKaaClient;
import org.kaaproject.kaa.client.channel.failover.FailoverManager;
import org.kaaproject.kaa.client.channel.impl.channels.DefaultOperationHttpChannel;
import org.kaaproject.kaa.client.persistence.KaaClientState;
import org.kaaproject.kaa.client.transport.AbstractHttpClient;
import org.kaaproject.kaa.common.TransportType;
import org.kaaproject.kaa.common.endpoint.security.KeyUtil;
import org.kaaproject.kaa.common.endpoint.security.MessageEncoderDecoder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultOperationHttpChannelTest {
    private static final Map<TransportType, ChannelDirection> SUPPORTED_TYPES = new HashMap<TransportType, ChannelDirection>();

    static {
        DefaultOperationHttpChannelTest.SUPPORTED_TYPES.put(EVENT, UP);
        DefaultOperationHttpChannelTest.SUPPORTED_TYPES.put(LOGGING, UP);
    }

    public ExecutorService fakeExecutor = new FakeExecutorService();

    @Test
    public void testChannelGetters() {
        AbstractKaaClient client = Mockito.mock(AbstractKaaClient.class);
        KaaClientState state = Mockito.mock(KaaClientState.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        KaaDataChannel channel = new DefaultOperationHttpChannel(client, state, failoverManager);
        Assert.assertEquals(DefaultOperationHttpChannelTest.SUPPORTED_TYPES, channel.getSupportedTransportTypes());
        Assert.assertEquals(HTTP_TRANSPORT_ID, channel.getTransportProtocolId());
        Assert.assertEquals("default_operations_http_channel", channel.getId());
    }

    @Test
    public void testChannelSync() throws Exception {
        KaaChannelManager manager = Mockito.mock(KaaChannelManager.class);
        AbstractHttpClient httpClient = Mockito.mock(AbstractHttpClient.class);
        FailoverManager failoverManager = Mockito.mock(FailoverManager.class);
        Mockito.when(httpClient.executeHttpRequest(Mockito.anyString(), Mockito.any(LinkedHashMap.class), Mockito.anyBoolean())).thenReturn(new byte[]{ 5, 5, 5 });
        MessageEncoderDecoder encDec = Mockito.mock(MessageEncoderDecoder.class);
        Mockito.when(httpClient.getEncoderDecoder()).thenReturn(encDec);
        AbstractKaaClient client = Mockito.mock(AbstractKaaClient.class);
        Mockito.when(client.createHttpClient(Mockito.anyString(), Mockito.any(PrivateKey.class), Mockito.any(PublicKey.class), Mockito.any(PublicKey.class))).thenReturn(httpClient);
        Mockito.when(client.getChannelManager()).thenReturn(manager);
        KaaClientState state = Mockito.mock(KaaClientState.class);
        KaaDataMultiplexer multiplexer = Mockito.mock(KaaDataMultiplexer.class);
        KaaDataDemultiplexer demultiplexer = Mockito.mock(KaaDataDemultiplexer.class);
        DefaultOperationHttpChannelTest.DefaultOperationHttpChannelFake channel = new DefaultOperationHttpChannelTest.DefaultOperationHttpChannelFake(client, state, failoverManager, 2);
        TransportConnectionInfo server = IpTransportInfoTest.createTestServerInfo(OPERATIONS, HTTP_TRANSPORT_ID, "localhost", 9889, KeyUtil.generateKeyPair().getPublic());
        channel.setServer(server);
        channel.sync(EVENT);
        channel.setDemultiplexer(demultiplexer);
        setDemultiplexer(null);
        channel.sync(EVENT);
        channel.setMultiplexer(multiplexer);
        setMultiplexer(null);
        channel.sync(BOOTSTRAP);
        channel.sync(EVENT);
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
        DefaultOperationHttpChannelTest.DefaultOperationHttpChannelFake channel = new DefaultOperationHttpChannelTest.DefaultOperationHttpChannelFake(client, state, failoverManager, 0);
        syncAll();
        channel.setDemultiplexer(demultiplexer);
        channel.setMultiplexer(multiplexer);
        shutdown();
        TransportConnectionInfo server = IpTransportInfoTest.createTestServerInfo(OPERATIONS, HTTP_TRANSPORT_ID, "localhost", 9889, KeyUtil.generateKeyPair().getPublic());
        channel.setServer(server);
        channel.sync(EVENT);
        syncAll();
        channel.verify();
    }

    class DefaultOperationHttpChannelFake extends DefaultOperationHttpChannel {
        private final int wantedNumberOfInvocations;

        public DefaultOperationHttpChannelFake(AbstractKaaClient client, KaaClientState state, FailoverManager failoverManager, int wantedNumberOfInvocations) {
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
            Mockito.verify(getDemultiplexer(), Mockito.times(wantedNumberOfInvocations)).processResponse(Mockito.any(byte[].class));
        }
    }
}

