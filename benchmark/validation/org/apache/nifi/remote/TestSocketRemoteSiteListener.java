/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.remote;


import RequestType.REQUEST_PEER_LIST;
import java.lang.reflect.Method;
import java.util.Optional;
import org.apache.nifi.remote.cluster.NodeInformation;
import org.apache.nifi.remote.protocol.RequestType;
import org.apache.nifi.remote.protocol.ServerProtocol;
import org.apache.nifi.util.NiFiProperties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestSocketRemoteSiteListener {
    @Test
    public void testRequestPeerList() throws Exception {
        Method method = SocketRemoteSiteListener.class.getDeclaredMethod("handleRequest", ServerProtocol.class, Peer.class, RequestType.class);
        method.setAccessible(true);
        final NiFiProperties nifiProperties = Mockito.spy(NiFiProperties.class);
        final int apiPort = 8080;
        final int remoteSocketPort = 8081;
        final String remoteInputHost = "node1.example.com";
        Mockito.when(nifiProperties.getPort()).thenReturn(apiPort);
        Mockito.when(nifiProperties.getRemoteInputHost()).thenReturn(remoteInputHost);
        Mockito.when(nifiProperties.getRemoteInputPort()).thenReturn(remoteSocketPort);
        Mockito.when(nifiProperties.getRemoteInputHttpPort()).thenReturn(null);// Even if HTTP transport is disabled, RAW should work.

        Mockito.when(nifiProperties.isSiteToSiteHttpEnabled()).thenReturn(false);
        Mockito.when(nifiProperties.isSiteToSiteSecure()).thenReturn(false);
        final SocketRemoteSiteListener listener = new SocketRemoteSiteListener(remoteSocketPort, null, nifiProperties);
        final ServerProtocol serverProtocol = Mockito.mock(ServerProtocol.class);
        Mockito.doAnswer(( invocation) -> {
            final NodeInformation self = getArgumentAt(2, NodeInformation.class);
            // Listener should inform about itself properly:
            Assert.assertEquals(remoteInputHost, self.getSiteToSiteHostname());
            Assert.assertEquals(remoteSocketPort, self.getSiteToSitePort().intValue());
            Assert.assertNull(self.getSiteToSiteHttpApiPort());
            Assert.assertEquals(apiPort, self.getAPIPort());
            return null;
        }).when(serverProtocol).sendPeerList(ArgumentMatchers.any(Peer.class), ArgumentMatchers.any(Optional.class), ArgumentMatchers.any(NodeInformation.class));
        final Peer peer = null;
        method.invoke(listener, serverProtocol, peer, REQUEST_PEER_LIST);
    }
}

