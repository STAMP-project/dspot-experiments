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
package org.apache.nifi.remote.protocol.socket;


import ResponseCode.PROPERTIES_OK;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.nifi.remote.Peer;
import org.apache.nifi.remote.cluster.ClusterNodeInformation;
import org.apache.nifi.remote.cluster.NodeInformation;
import org.apache.nifi.remote.protocol.HandshakeProperties;
import org.apache.nifi.remote.protocol.Response;
import org.junit.Assert;
import org.junit.Test;


public class TestSocketFlowFileServerProtocol {
    @Test
    public void testSendPeerListStandalone() throws Exception {
        final SocketFlowFileServerProtocol protocol = getDefaultSocketFlowFileServerProtocol();
        final Optional<ClusterNodeInformation> clusterNodeInfo = Optional.empty();
        final String siteToSiteHostname = "node1.example.com";
        final Integer siteToSitePort = 8081;
        final Integer siteToSiteHttpPort = null;
        final int apiPort = 8080;
        final boolean isSiteToSiteSecure = true;
        final int numOfQueuedFlowFiles = 100;
        final NodeInformation self = new NodeInformation(siteToSiteHostname, siteToSitePort, siteToSiteHttpPort, apiPort, isSiteToSiteSecure, numOfQueuedFlowFiles);
        final HandshakeProperties handshakeProperties = new HandshakeProperties();
        handshakeProperties.setCommsIdentifier("communication-identifier");
        handshakeProperties.setTransitUriPrefix("uri-prefix");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final Peer peer = getDefaultPeer(handshakeProperties, outputStream);
        protocol.handshake(peer);
        protocol.sendPeerList(peer, clusterNodeInfo, self);
        try (final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
            final Response handshakeResponse = Response.read(dis);
            Assert.assertEquals(PROPERTIES_OK, handshakeResponse.getCode());
            final int numPeers = dis.readInt();
            Assert.assertEquals(1, numPeers);
            Assert.assertEquals(siteToSiteHostname, dis.readUTF());
            Assert.assertEquals(siteToSitePort.intValue(), dis.readInt());
            Assert.assertEquals(isSiteToSiteSecure, dis.readBoolean());
            Assert.assertEquals(numOfQueuedFlowFiles, dis.readInt());
        }
    }

    @Test
    public void testSendPeerListCluster() throws Exception {
        final SocketFlowFileServerProtocol protocol = getDefaultSocketFlowFileServerProtocol();
        final List<NodeInformation> nodeInfoList = new ArrayList<>();
        final ClusterNodeInformation clusterNodeInformation = new ClusterNodeInformation();
        clusterNodeInformation.setNodeInformation(nodeInfoList);
        final Optional<ClusterNodeInformation> clusterNodeInfo = Optional.of(clusterNodeInformation);
        for (int i = 0; i < 3; i++) {
            final String siteToSiteHostname = String.format("node%d.example.com", i);
            final Integer siteToSitePort = 8081;
            final Integer siteToSiteHttpPort = null;
            final int apiPort = 8080;
            final boolean isSiteToSiteSecure = true;
            final int numOfQueuedFlowFiles = 100 + i;
            final NodeInformation nodeInformation = new NodeInformation(siteToSiteHostname, siteToSitePort, siteToSiteHttpPort, apiPort, isSiteToSiteSecure, numOfQueuedFlowFiles);
            nodeInfoList.add(nodeInformation);
        }
        final NodeInformation self = nodeInfoList.get(0);
        final HandshakeProperties handshakeProperties = new HandshakeProperties();
        handshakeProperties.setCommsIdentifier("communication-identifier");
        handshakeProperties.setTransitUriPrefix("uri-prefix");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final Peer peer = getDefaultPeer(handshakeProperties, outputStream);
        protocol.handshake(peer);
        protocol.sendPeerList(peer, clusterNodeInfo, self);
        try (final DataInputStream dis = new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray()))) {
            final Response handshakeResponse = Response.read(dis);
            Assert.assertEquals(PROPERTIES_OK, handshakeResponse.getCode());
            final int numPeers = dis.readInt();
            Assert.assertEquals(nodeInfoList.size(), numPeers);
            for (int i = 0; i < (nodeInfoList.size()); i++) {
                final NodeInformation node = nodeInfoList.get(i);
                Assert.assertEquals(node.getSiteToSiteHostname(), dis.readUTF());
                Assert.assertEquals(node.getSiteToSitePort().intValue(), dis.readInt());
                Assert.assertEquals(node.isSiteToSiteSecure(), dis.readBoolean());
                Assert.assertEquals(node.getTotalFlowFiles(), dis.readInt());
            }
        }
    }
}

