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
package org.kaaproject.kaa.client;


import FailoverDecision.FailoverAction;
import FailoverStatus.BOOTSTRAP_SERVERS_NA;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Test;
import org.kaaproject.kaa.client.bootstrap.DefaultBootstrapManager;
import org.kaaproject.kaa.client.channel.KaaInternalChannelManager;
import org.kaaproject.kaa.client.channel.ServerType;
import org.kaaproject.kaa.client.channel.TransportConnectionInfo;
import org.kaaproject.kaa.client.channel.failover.FailoverDecision;
import org.kaaproject.kaa.client.channel.failover.FailoverStatus;
import org.kaaproject.kaa.client.channel.failover.strategies.DefaultFailoverStrategy;
import org.kaaproject.kaa.client.channel.impl.ChannelRuntimeException;
import org.kaaproject.kaa.client.exceptions.KaaException;
import org.kaaproject.kaa.client.exceptions.KaaInvalidConfigurationException;
import org.kaaproject.kaa.client.exceptions.KaaRuntimeException;
import org.kaaproject.kaa.client.exceptions.KaaUnsupportedPlatformException;
import org.kaaproject.kaa.client.logging.AbstractLogCollector;
import org.kaaproject.kaa.client.persistence.KaaClientState;
import org.kaaproject.kaa.client.persistence.PersistentStorage;
import org.kaaproject.kaa.client.profile.ProfileRuntimeException;
import org.kaaproject.kaa.client.schema.SchemaRuntimeException;
import org.kaaproject.kaa.client.transport.TransportException;
import org.kaaproject.kaa.common.endpoint.gen.ProtocolMetaData;
import org.kaaproject.kaa.common.endpoint.gen.ProtocolVersionPair;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


public class KaaClientTest {
    private KaaClientPlatformContext platformContext;

    private KaaClientProperties clientProperties;

    private KaaClientStateListener stateListener;

    private PersistentStorage storage;

    private DefaultBootstrapManager bsManagerMock;

    private GenericKaaClient client;

    @Test(expected = KaaRuntimeException.class)
    public void initKaaClientDefaultKeyStrategy() {
        KaaClient client = Kaa.newClient(platformContext, stateListener, false);
    }

    @Test
    public void initKaaClientUserStrategy() {
        KaaClient client = Kaa.newClient(platformContext, stateListener, true);
    }

    @Test
    public void basicLifeCycleTest() throws Exception {
        client.start();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onStarted();
        Mockito.verify(bsManagerMock).receiveOperationsServerList();
        client.pause();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onPaused();
        client.resume();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onResume();
        client.stop();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onStopped();
    }

    @Test
    public void basicStartBSFailureTest() throws Exception {
        Mockito.doThrow(new TransportException("mock")).when(bsManagerMock).receiveOperationsServerList();
        client.start();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onStartFailure(Mockito.any(KaaException.class));
        Mockito.verify(bsManagerMock).receiveOperationsServerList();
        client.stop();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onStopped();
    }

    @Test
    public void failureOnStartTest() throws TransportException {
        Mockito.doThrow(new KaaRuntimeException(new Exception("cause"))).when(bsManagerMock).receiveOperationsServerList();
        client.start();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onStartFailure(Mockito.any(KaaException.class));
    }

    @Test
    public void failureOnStopTest() {
        client.start();
        AbstractLogCollector logCollector = Mockito.mock(AbstractLogCollector.class);
        Mockito.doThrow(new RuntimeException()).when(logCollector).stop();
        ReflectionTestUtils.setField(client, "logCollector", logCollector);
        client.stop();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onStopFailure(Mockito.any(KaaException.class));
    }

    @Test
    public void failureOnPauseTest() {
        client.start();
        KaaClientState clientState = Mockito.mock(KaaClientState.class);
        Mockito.doThrow(new RuntimeException()).when(clientState).persist();
        ReflectionTestUtils.setField(client, "kaaClientState", clientState);
        client.pause();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onPauseFailure(Mockito.any(KaaException.class));
    }

    @Test
    public void failureOnResumeTest() {
        client.start();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onStarted();
        client.pause();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onPaused();
        KaaInternalChannelManager channelManager = Mockito.mock(KaaInternalChannelManager.class);
        Mockito.doThrow(new RuntimeException()).when(channelManager).resume();
        ReflectionTestUtils.setField(client, "channelManager", channelManager);
        client.resume();
        Mockito.verify(stateListener, Mockito.timeout(1000)).onResumeFailure(Mockito.any(KaaException.class));
    }

    @Test
    public void exceptionCreationTest() {
        KaaUnsupportedPlatformException kaaUnsupportedPlatformException = new KaaUnsupportedPlatformException(new Exception());
        ProfileRuntimeException profileRuntimeException = new ProfileRuntimeException("");
        SchemaRuntimeException schemaRuntimeException1 = new SchemaRuntimeException();
        SchemaRuntimeException schemaRuntimeException2 = new SchemaRuntimeException("");
        KaaInvalidConfigurationException invalidConfigurationException = new KaaInvalidConfigurationException(new Exception());
        ChannelRuntimeException channelRuntimeException1 = new ChannelRuntimeException();
        ChannelRuntimeException channelRuntimeException2 = new ChannelRuntimeException(new Exception());
        ChannelRuntimeException channelRuntimeException3 = new ChannelRuntimeException("", new Exception());
        ChannelRuntimeException channelRuntimeException4 = new ChannelRuntimeException("", new Exception(), true, true);
    }

    @Test
    public void failureListenerTest() throws IOException, GeneralSecurityException {
        client.setFailoverStrategy(new DefaultFailoverStrategy() {
            @Override
            public FailoverDecision onFailover(FailoverStatus failoverStatus) {
                return new FailoverDecision(FailoverAction.FAILURE);
            }
        });
        client.start();
        ProtocolMetaData metaData = new ProtocolMetaData(1, new ProtocolVersionPair(1, 1), null);
        TransportConnectionInfo info = new org.kaaproject.kaa.client.channel.GenericTransportInfo(ServerType.BOOTSTRAP, metaData);
        client.getChannelManager().onServerFailed(info, BOOTSTRAP_SERVERS_NA);
        Mockito.verify(stateListener, Mockito.timeout(500)).onStopped();
    }
}

