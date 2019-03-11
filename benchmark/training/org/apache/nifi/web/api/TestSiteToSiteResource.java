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
package org.apache.nifi.web.api;


import HttpHeaders.PROTOCOL_VERSION;
import NiFiProperties.CLUSTER_IS_NODE;
import NiFiProperties.WEB_HTTP_PORT_FORWARDING;
import ResponseCode.ABORT;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.apache.nifi.cluster.coordination.ClusterCoordinator;
import org.apache.nifi.cluster.coordination.node.NodeWorkload;
import org.apache.nifi.cluster.protocol.NodeIdentifier;
import org.apache.nifi.web.NiFiServiceFacade;
import org.apache.nifi.web.api.dto.ControllerDTO;
import org.apache.nifi.web.api.dto.remote.PeerDTO;
import org.apache.nifi.web.api.entity.ControllerEntity;
import org.apache.nifi.web.api.entity.PeersEntity;
import org.apache.nifi.web.api.entity.TransactionResultEntity;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestSiteToSiteResource {
    @Test
    public void testGetControllerForOlderVersion() throws Exception {
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        final NiFiServiceFacade serviceFacade = Mockito.mock(NiFiServiceFacade.class);
        final ControllerEntity controllerEntity = new ControllerEntity();
        final ControllerDTO controller = new ControllerDTO();
        controllerEntity.setController(controller);
        controller.setRemoteSiteHttpListeningPort(8080);
        controller.setRemoteSiteListeningPort(9990);
        Mockito.doReturn(controller).when(serviceFacade).getSiteToSiteDetails();
        final SiteToSiteResource resource = getSiteToSiteResource(serviceFacade);
        final Response response = resource.getSiteToSiteDetails(req);
        ControllerEntity resultEntity = ((ControllerEntity) (response.getEntity()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertNull(("remoteSiteHttpListeningPort should be null since older version doesn't recognize this field" + " and throws JSON mapping exception."), resultEntity.getController().getRemoteSiteHttpListeningPort());
        Assert.assertEquals("Other fields should be retained.", new Integer(9990), controllerEntity.getController().getRemoteSiteListeningPort());
    }

    @Test
    public void testGetController() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final NiFiServiceFacade serviceFacade = Mockito.mock(NiFiServiceFacade.class);
        final ControllerEntity controllerEntity = new ControllerEntity();
        final ControllerDTO controller = new ControllerDTO();
        controllerEntity.setController(controller);
        controller.setRemoteSiteHttpListeningPort(8080);
        controller.setRemoteSiteListeningPort(9990);
        Mockito.doReturn(controller).when(serviceFacade).getSiteToSiteDetails();
        final SiteToSiteResource resource = getSiteToSiteResource(serviceFacade);
        final Response response = resource.getSiteToSiteDetails(req);
        ControllerEntity resultEntity = ((ControllerEntity) (response.getEntity()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("remoteSiteHttpListeningPort should be retained", new Integer(8080), resultEntity.getController().getRemoteSiteHttpListeningPort());
        Assert.assertEquals("Other fields should be retained.", new Integer(9990), controllerEntity.getController().getRemoteSiteListeningPort());
    }

    @Test
    public void testPeers() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final NiFiServiceFacade serviceFacade = Mockito.mock(NiFiServiceFacade.class);
        final SiteToSiteResource resource = getSiteToSiteResource(serviceFacade);
        final Response response = resource.getPeers(req);
        PeersEntity resultEntity = ((PeersEntity) (response.getEntity()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(1, resultEntity.getPeers().size());
        final PeerDTO peer = resultEntity.getPeers().iterator().next();
        Assert.assertEquals(8080, peer.getPort());
    }

    @Test
    public void testPeersPortForwarding() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final NiFiServiceFacade serviceFacade = Mockito.mock(NiFiServiceFacade.class);
        final Map<String, String> additionalProperties = new HashMap<>();
        additionalProperties.put(WEB_HTTP_PORT_FORWARDING, "80");
        final SiteToSiteResource resource = getSiteToSiteResource(serviceFacade, additionalProperties);
        final Response response = resource.getPeers(req);
        PeersEntity resultEntity = ((PeersEntity) (response.getEntity()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(1, resultEntity.getPeers().size());
        final PeerDTO peer = resultEntity.getPeers().iterator().next();
        Assert.assertEquals(80, peer.getPort());
    }

    @Test
    public void testPeersClustered() throws Exception {
        final HttpServletRequest req = createCommonHttpServletRequest();
        final NiFiServiceFacade serviceFacade = Mockito.mock(NiFiServiceFacade.class);
        final Map<String, String> clusterSettings = new HashMap<>();
        clusterSettings.put(CLUSTER_IS_NODE, "true");
        final SiteToSiteResource resource = getSiteToSiteResource(serviceFacade, clusterSettings);
        final ClusterCoordinator clusterCoordinator = Mockito.mock(ClusterCoordinator.class);
        final Map<String, NodeWorkload> hostportWorkloads = new HashMap<>();
        final Map<NodeIdentifier, NodeWorkload> workloads = new HashMap<>();
        IntStream.range(1, 4).forEach(( i) -> {
            final String hostname = "node" + i;
            final int siteToSiteHttpApiPort = 8110 + i;
            final NodeIdentifier nodeId = new NodeIdentifier(hostname, hostname, (8080 + i), hostname, (8090 + i), hostname, (8100 + i), siteToSiteHttpApiPort, false);
            final NodeWorkload workload = new NodeWorkload();
            workload.setReportedTimestamp(((System.currentTimeMillis()) - i));
            workload.setFlowFileBytes((1024 * i));
            workload.setFlowFileCount((10 * i));
            workload.setActiveThreadCount(i);
            workload.setSystemStartTime(((System.currentTimeMillis()) - (1000 * i)));
            workloads.put(nodeId, workload);
            hostportWorkloads.put(((hostname + ":") + siteToSiteHttpApiPort), workload);
        });
        Mockito.when(clusterCoordinator.getClusterWorkload()).thenReturn(workloads);
        resource.setClusterCoordinator(clusterCoordinator);
        final Response response = resource.getPeers(req);
        PeersEntity resultEntity = ((PeersEntity) (response.getEntity()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(3, resultEntity.getPeers().size());
        resultEntity.getPeers().stream().forEach(( peerDTO) -> {
            final NodeWorkload workload = hostportWorkloads.get((((peerDTO.getHostname()) + ":") + (peerDTO.getPort())));
            assertNotNull(workload);
            assertEquals(workload.getFlowFileCount(), peerDTO.getFlowFileCount());
        });
    }

    @Test
    public void testPeersVersionWasNotSpecified() throws Exception {
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        final NiFiServiceFacade serviceFacade = Mockito.mock(NiFiServiceFacade.class);
        final SiteToSiteResource resource = getSiteToSiteResource(serviceFacade);
        final Response response = resource.getPeers(req);
        TransactionResultEntity resultEntity = ((TransactionResultEntity) (response.getEntity()));
        Assert.assertEquals(400, response.getStatus());
        Assert.assertEquals(ABORT.getCode(), resultEntity.getResponseCode());
    }

    @Test
    public void testPeersVersionNegotiationDowngrade() throws Exception {
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.doReturn("999").when(req).getHeader(ArgumentMatchers.eq(PROTOCOL_VERSION));
        final NiFiServiceFacade serviceFacade = Mockito.mock(NiFiServiceFacade.class);
        final SiteToSiteResource resource = getSiteToSiteResource(serviceFacade);
        final Response response = resource.getPeers(req);
        PeersEntity resultEntity = ((PeersEntity) (response.getEntity()));
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(1, resultEntity.getPeers().size());
        Assert.assertEquals(new Integer(1), response.getMetadata().getFirst(PROTOCOL_VERSION));
    }
}

