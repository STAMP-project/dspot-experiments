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
package org.apache.nifi.remote.client;


import TransferDirection.RECEIVE;
import TransferDirection.SEND;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.net.ssl.SSLContext;
import org.apache.nifi.remote.protocol.http.HttpProxy;
import org.apache.nifi.remote.util.SiteToSiteRestApiClient;
import org.apache.nifi.web.api.dto.ControllerDTO;
import org.apache.nifi.web.api.dto.PortDTO;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestSiteInfoProvider {
    @Test
    public void testSecure() throws Exception {
        final Set<String> expectedClusterUrl = new LinkedHashSet<>(Arrays.asList(new String[]{ "https://node1:8443", "https://node2:8443" }));
        final String expectedActiveClusterUrl = "https://node2:8443/nifi-api";
        final SSLContext expectedSslConText = Mockito.mock(SSLContext.class);
        final HttpProxy expectedHttpProxy = Mockito.mock(HttpProxy.class);
        final SiteInfoProvider siteInfoProvider = Mockito.spy(new SiteInfoProvider());
        siteInfoProvider.setClusterUrls(expectedClusterUrl);
        siteInfoProvider.setSslContext(expectedSslConText);
        siteInfoProvider.setProxy(expectedHttpProxy);
        final ControllerDTO controllerDTO = new ControllerDTO();
        final PortDTO inputPort1 = new PortDTO();
        inputPort1.setName("input-one");
        inputPort1.setId("input-0001");
        final PortDTO inputPort2 = new PortDTO();
        inputPort2.setName("input-two");
        inputPort2.setId("input-0002");
        final PortDTO outputPort1 = new PortDTO();
        outputPort1.setName("output-one");
        outputPort1.setId("output-0001");
        final PortDTO outputPort2 = new PortDTO();
        outputPort2.setName("output-two");
        outputPort2.setId("output-0002");
        final Set<PortDTO> inputPorts = new HashSet<>();
        inputPorts.add(inputPort1);
        inputPorts.add(inputPort2);
        final Set<PortDTO> outputPorts = new HashSet<>();
        outputPorts.add(outputPort1);
        outputPorts.add(outputPort2);
        controllerDTO.setInputPorts(inputPorts);
        controllerDTO.setOutputPorts(outputPorts);
        controllerDTO.setRemoteSiteListeningPort(8081);
        controllerDTO.setRemoteSiteHttpListeningPort(8443);
        controllerDTO.setSiteToSiteSecure(true);
        // SiteInfoProvider uses SiteToSIteRestApiClient to get ControllerDTO.
        Mockito.doAnswer(( invocation) -> {
            final SSLContext sslContext = getArgumentAt(0, SSLContext.class);
            final HttpProxy httpProxy = invocation.getArgumentAt(1, HttpProxy.class);
            Assert.assertEquals(expectedSslConText, sslContext);
            Assert.assertEquals(expectedHttpProxy, httpProxy);
            final SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
            Mockito.when(apiClient.getController(ArgumentMatchers.eq(expectedClusterUrl))).thenReturn(controllerDTO);
            Mockito.when(apiClient.getBaseUrl()).thenReturn(expectedActiveClusterUrl);
            return apiClient;
        }).when(siteInfoProvider).createSiteToSiteRestApiClient(ArgumentMatchers.any(), ArgumentMatchers.any());
        // siteInfoProvider should expose correct information of the remote NiFi cluster.
        Assert.assertEquals(controllerDTO.getRemoteSiteListeningPort(), siteInfoProvider.getSiteToSitePort());
        Assert.assertEquals(controllerDTO.getRemoteSiteHttpListeningPort(), siteInfoProvider.getSiteToSiteHttpPort());
        Assert.assertEquals(controllerDTO.isSiteToSiteSecure(), siteInfoProvider.isSecure());
        Assert.assertTrue(siteInfoProvider.isWebInterfaceSecure());
        Assert.assertEquals(inputPort1.getId(), siteInfoProvider.getInputPortIdentifier(inputPort1.getName()));
        Assert.assertEquals(inputPort2.getId(), siteInfoProvider.getInputPortIdentifier(inputPort2.getName()));
        Assert.assertEquals(outputPort1.getId(), siteInfoProvider.getOutputPortIdentifier(outputPort1.getName()));
        Assert.assertEquals(outputPort2.getId(), siteInfoProvider.getOutputPortIdentifier(outputPort2.getName()));
        Assert.assertNull(siteInfoProvider.getInputPortIdentifier("not-exist"));
        Assert.assertNull(siteInfoProvider.getOutputPortIdentifier("not-exist"));
        Assert.assertEquals(inputPort1.getId(), siteInfoProvider.getPortIdentifier(inputPort1.getName(), SEND));
        Assert.assertEquals(outputPort1.getId(), siteInfoProvider.getPortIdentifier(outputPort1.getName(), RECEIVE));
        Assert.assertEquals(expectedActiveClusterUrl, siteInfoProvider.getActiveClusterUrl().toString());
    }

    @Test
    public void testPlain() throws Exception {
        final Set<String> expectedClusterUrl = new LinkedHashSet<>(Arrays.asList(new String[]{ "http://node1:8443, http://node2:8443" }));
        final String expectedActiveClusterUrl = "http://node2:8443/nifi-api";
        final SiteInfoProvider siteInfoProvider = Mockito.spy(new SiteInfoProvider());
        siteInfoProvider.setClusterUrls(expectedClusterUrl);
        final ControllerDTO controllerDTO = new ControllerDTO();
        controllerDTO.setInputPorts(Collections.emptySet());
        controllerDTO.setOutputPorts(Collections.emptySet());
        controllerDTO.setRemoteSiteListeningPort(8081);
        controllerDTO.setRemoteSiteHttpListeningPort(8080);
        controllerDTO.setSiteToSiteSecure(false);
        // SiteInfoProvider uses SiteToSIteRestApiClient to get ControllerDTO.
        Mockito.doAnswer(( invocation) -> {
            final SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
            Mockito.when(apiClient.getController(ArgumentMatchers.eq(expectedClusterUrl))).thenReturn(controllerDTO);
            Mockito.when(apiClient.getBaseUrl()).thenReturn(expectedActiveClusterUrl);
            return apiClient;
        }).when(siteInfoProvider).createSiteToSiteRestApiClient(ArgumentMatchers.any(), ArgumentMatchers.any());
        // siteInfoProvider should expose correct information of the remote NiFi cluster.
        Assert.assertEquals(controllerDTO.getRemoteSiteListeningPort(), siteInfoProvider.getSiteToSitePort());
        Assert.assertEquals(controllerDTO.getRemoteSiteHttpListeningPort(), siteInfoProvider.getSiteToSiteHttpPort());
        Assert.assertEquals(controllerDTO.isSiteToSiteSecure(), siteInfoProvider.isSecure());
        Assert.assertFalse(siteInfoProvider.isWebInterfaceSecure());
        Assert.assertEquals(expectedActiveClusterUrl, siteInfoProvider.getActiveClusterUrl().toString());
    }

    @Test
    public void testConnectException() throws Exception {
        final Set<String> expectedClusterUrl = new LinkedHashSet<>(Arrays.asList(new String[]{ "http://node1:8443, http://node2:8443" }));
        final SiteInfoProvider siteInfoProvider = Mockito.spy(new SiteInfoProvider());
        siteInfoProvider.setClusterUrls(expectedClusterUrl);
        final ControllerDTO controllerDTO = new ControllerDTO();
        controllerDTO.setInputPorts(Collections.emptySet());
        controllerDTO.setOutputPorts(Collections.emptySet());
        controllerDTO.setRemoteSiteListeningPort(8081);
        controllerDTO.setRemoteSiteHttpListeningPort(8080);
        controllerDTO.setSiteToSiteSecure(false);
        // SiteInfoProvider uses SiteToSIteRestApiClient to get ControllerDTO.
        Mockito.doAnswer(( invocation) -> {
            final SiteToSiteRestApiClient apiClient = Mockito.mock(SiteToSiteRestApiClient.class);
            Mockito.when(apiClient.getController(ArgumentMatchers.eq(expectedClusterUrl))).thenThrow(new IOException("Connection refused."));
            return apiClient;
        }).when(siteInfoProvider).createSiteToSiteRestApiClient(ArgumentMatchers.any(), ArgumentMatchers.any());
        try {
            siteInfoProvider.getSiteToSitePort();
            Assert.fail();
        } catch (IOException e) {
        }
        try {
            siteInfoProvider.getActiveClusterUrl();
            Assert.fail();
        } catch (IOException e) {
        }
    }
}

