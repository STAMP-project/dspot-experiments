/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.om;


import LifeCycle.State.RUNNING;
import OMConfigKeys.OZONE_OM_NODES_KEY;
import OMConfigKeys.OZONE_OM_NODE_ID_KEY;
import OMConfigKeys.OZONE_OM_PORT_DEFAULT;
import OMConfigKeys.OZONE_OM_RATIS_PORT_KEY;
import OMConfigKeys.OZONE_OM_SERVICE_IDS_KEY;
import java.net.InetSocketAddress;
import java.util.Collection;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.OmUtils;
import org.apache.hadoop.ozone.OzoneIllegalArgumentException;
import org.apache.hadoop.ozone.om.ratis.OzoneManagerRatisServer;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.ratis.protocol.RaftPeer;
import org.junit.Assert;
import org.junit.Test;

import static OMConfigKeys.OZONE_OM_ADDRESS_KEY;
import static OMConfigKeys.OZONE_OM_RATIS_PORT_DEFAULT;


/**
 * Tests OM related configurations.
 */
public class TestOzoneManagerConfiguration {
    private OzoneConfiguration conf;

    private MiniOzoneCluster cluster;

    private String omId;

    private String clusterId;

    private String scmId;

    private OzoneManager om;

    private OzoneManagerRatisServer omRatisServer;

    private static final long LEADER_ELECTION_TIMEOUT = 500L;

    /**
     * Test that if no OM address is specified, then the OM rpc server
     * is started on localhost.
     */
    @Test
    public void testNoConfiguredOMAddress() throws Exception {
        startCluster();
        om = cluster.getOzoneManager();
        Assert.assertTrue(NetUtils.isLocalAddress(om.getOmRpcServerAddr().getAddress()));
    }

    /**
     * Test that if only the hostname is specified for om address, then the
     * default port is used.
     */
    @Test
    public void testDefaultPortIfNotSpecified() throws Exception {
        String omNode1Id = "omNode1";
        String omNode2Id = "omNode2";
        String omNodesKeyValue = (omNode1Id + ",") + omNode2Id;
        conf.set(OZONE_OM_NODES_KEY, omNodesKeyValue);
        String omNode1RpcAddrKey = getOMAddrKeyWithSuffix(null, omNode1Id);
        String omNode2RpcAddrKey = getOMAddrKeyWithSuffix(null, omNode2Id);
        conf.set(omNode1RpcAddrKey, "0.0.0.0");
        conf.set(omNode2RpcAddrKey, "122.0.0.122");
        // Set omNode1 as the current node. omNode1 address does not have a port
        // number specified. So the default port should be taken.
        conf.set(OZONE_OM_NODE_ID_KEY, omNode1Id);
        startCluster();
        om = cluster.getOzoneManager();
        Assert.assertEquals("0.0.0.0", om.getOmRpcServerAddr().getHostName());
        Assert.assertEquals(OZONE_OM_PORT_DEFAULT, om.getOmRpcServerAddr().getPort());
        // Verify that the 2nd OMs address stored in the current OM also has the
        // default port as the port is not specified
        InetSocketAddress omNode2Addr = om.getPeerNodes().get(0).getRpcAddress();
        Assert.assertEquals("122.0.0.122", omNode2Addr.getHostString());
        Assert.assertEquals(OZONE_OM_PORT_DEFAULT, omNode2Addr.getPort());
    }

    /**
     * Test a single node OM service (default setting for MiniOzoneCluster).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSingleNodeOMservice() throws Exception {
        // Default settings of MiniOzoneCluster start a sinle node OM service.
        startCluster();
        om = cluster.getOzoneManager();
        omRatisServer = om.getOmRatisServer();
        Assert.assertEquals(RUNNING, om.getOmRatisServerState());
        // OM's Ratis server should have only 1 peer (itself) in its RaftGroup
        Collection<RaftPeer> peers = omRatisServer.getRaftGroup().getPeers();
        Assert.assertEquals(1, peers.size());
        // The RaftPeer id should match the configured omId
        RaftPeer raftPeer = peers.toArray(new RaftPeer[1])[0];
        Assert.assertEquals(omId, raftPeer.getId().toString());
    }

    /**
     * Test configurating an OM service with three OM nodes.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testThreeNodeOMservice() throws Exception {
        // Set the configuration for 3 node OM service. Set one node's rpc
        // address to localhost. OM will parse all configurations and find the
        // nodeId representing the localhost
        final String omServiceId = "om-service-test1";
        final String omNode1Id = "omNode1";
        final String omNode2Id = "omNode2";
        final String omNode3Id = "omNode3";
        String omNodesKeyValue = (((omNode1Id + ",") + omNode2Id) + ",") + omNode3Id;
        String omNodesKey = OmUtils.addKeySuffixes(OZONE_OM_NODES_KEY, omServiceId);
        String omNode1RpcAddrKey = getOMAddrKeyWithSuffix(omServiceId, omNode1Id);
        String omNode2RpcAddrKey = getOMAddrKeyWithSuffix(omServiceId, omNode2Id);
        String omNode3RpcAddrKey = getOMAddrKeyWithSuffix(omServiceId, omNode3Id);
        String omNode3RatisPortKey = OmUtils.addKeySuffixes(OZONE_OM_RATIS_PORT_KEY, omServiceId, omNode3Id);
        conf.set(OZONE_OM_SERVICE_IDS_KEY, omServiceId);
        conf.set(omNodesKey, omNodesKeyValue);
        // Set node2 to localhost and the other two nodes to dummy addresses
        conf.set(omNode1RpcAddrKey, "123.0.0.123:9862");
        conf.set(omNode2RpcAddrKey, "0.0.0.0:9862");
        conf.set(omNode3RpcAddrKey, "124.0.0.124:9862");
        conf.setInt(omNode3RatisPortKey, 9898);
        startCluster();
        om = cluster.getOzoneManager();
        omRatisServer = om.getOmRatisServer();
        Assert.assertEquals(RUNNING, om.getOmRatisServerState());
        // OM's Ratis server should have 3 peers in its RaftGroup
        Collection<RaftPeer> peers = omRatisServer.getRaftGroup().getPeers();
        Assert.assertEquals(3, peers.size());
        // Ratis server RaftPeerId should match with omNode2 ID as node2 is the
        // localhost
        Assert.assertEquals(omNode2Id, omRatisServer.getRaftPeerId().toString());
        // Verify peer details
        for (RaftPeer peer : peers) {
            String expectedPeerAddress = null;
            switch (peer.getId().toString()) {
                case omNode1Id :
                    // Ratis port is not set for node1. So it should take the default port
                    expectedPeerAddress = "123.0.0.123:" + (OZONE_OM_RATIS_PORT_DEFAULT);
                    break;
                case omNode2Id :
                    expectedPeerAddress = "0.0.0.0:" + (OZONE_OM_RATIS_PORT_DEFAULT);
                    break;
                case omNode3Id :
                    // Ratis port is not set for node3. So it should take the default port
                    expectedPeerAddress = "124.0.0.124:9898";
                    break;
                default :
                    Assert.fail("Unrecognized RaftPeerId");
            }
            Assert.assertEquals(expectedPeerAddress, peer.getAddress());
        }
    }

    /**
     * Test a wrong configuration for OM HA. A configuration with none of the
     * OM addresses matching the local address should throw an error.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWrongConfiguration() throws Exception {
        String omServiceId = "om-service-test1";
        String omNode1Id = "omNode1";
        String omNode2Id = "omNode2";
        String omNode3Id = "omNode3";
        String omNodesKeyValue = (((omNode1Id + ",") + omNode2Id) + ",") + omNode3Id;
        String omNodesKey = OmUtils.addKeySuffixes(OZONE_OM_NODES_KEY, omServiceId);
        String omNode1RpcAddrKey = getOMAddrKeyWithSuffix(omServiceId, omNode1Id);
        String omNode2RpcAddrKey = getOMAddrKeyWithSuffix(omServiceId, omNode2Id);
        String omNode3RpcAddrKey = getOMAddrKeyWithSuffix(omServiceId, omNode3Id);
        conf.set(OZONE_OM_SERVICE_IDS_KEY, omServiceId);
        conf.set(omNodesKey, omNodesKeyValue);
        // Set node2 to localhost and the other two nodes to dummy addresses
        conf.set(omNode1RpcAddrKey, "123.0.0.123:9862");
        conf.set(omNode2RpcAddrKey, "125.0.0.2:9862");
        conf.set(omNode3RpcAddrKey, "124.0.0.124:9862");
        try {
            startCluster();
            Assert.fail("Wrong Configuration. OM initialization should have failed.");
        } catch (OzoneIllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains(((("Configuration has no " + (OZONE_OM_ADDRESS_KEY)) + " address that matches local ") + "node's address."), e);
        }
    }

    /**
     * Test multiple OM service configuration.
     */
    @Test
    public void testMultipleOMServiceIds() throws Exception {
        // Set up OZONE_OM_SERVICES_KEY with 2 service Ids.
        String om1ServiceId = "om-service-test1";
        String om2ServiceId = "om-service-test2";
        String omServices = (om1ServiceId + ",") + om2ServiceId;
        conf.set(OZONE_OM_SERVICE_IDS_KEY, omServices);
        String omNode1Id = "omNode1";
        String omNode2Id = "omNode2";
        String omNode3Id = "omNode3";
        String omNodesKeyValue = (((omNode1Id + ",") + omNode2Id) + ",") + omNode3Id;
        // Set the node Ids for the 2 services. The nodeIds need to be
        // distinch within one service. The ids can overlap between
        // different services.
        String om1NodesKey = OmUtils.addKeySuffixes(OZONE_OM_NODES_KEY, om1ServiceId);
        String om2NodesKey = OmUtils.addKeySuffixes(OZONE_OM_NODES_KEY, om2ServiceId);
        conf.set(om1NodesKey, omNodesKeyValue);
        conf.set(om2NodesKey, omNodesKeyValue);
        // Set the RPC addresses for all 6 OMs (3 for each service). Only one
        // node out of these must have the localhost address.
        conf.set(getOMAddrKeyWithSuffix(om1ServiceId, omNode1Id), "122.0.0.123:9862");
        conf.set(getOMAddrKeyWithSuffix(om1ServiceId, omNode2Id), "123.0.0.124:9862");
        conf.set(getOMAddrKeyWithSuffix(om1ServiceId, omNode3Id), "124.0.0.125:9862");
        conf.set(getOMAddrKeyWithSuffix(om2ServiceId, omNode1Id), "125.0.0.126:9862");
        conf.set(getOMAddrKeyWithSuffix(om2ServiceId, omNode2Id), "0.0.0.0:9862");
        conf.set(getOMAddrKeyWithSuffix(om2ServiceId, omNode3Id), "126.0.0.127:9862");
        startCluster();
        om = cluster.getOzoneManager();
        omRatisServer = om.getOmRatisServer();
        Assert.assertEquals(RUNNING, om.getOmRatisServerState());
        // OM's Ratis server should have 3 peers in its RaftGroup
        Collection<RaftPeer> peers = omRatisServer.getRaftGroup().getPeers();
        Assert.assertEquals(3, peers.size());
        // Verify that the serviceId and nodeId match the node with the localhost
        // address - om-service-test2 and omNode2
        Assert.assertEquals(om2ServiceId, om.getOMServiceId());
        Assert.assertEquals(omNode2Id, omRatisServer.getRaftPeerId().toString());
    }
}

