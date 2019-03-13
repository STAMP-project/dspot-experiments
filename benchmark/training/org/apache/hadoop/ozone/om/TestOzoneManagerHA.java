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


import OMFailoverProxyProvider.OMProxyInfo;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdfs.LogVerificationAppender;
import org.apache.hadoop.ozone.MiniOzoneHAClusterImpl;
import org.apache.hadoop.ozone.OzoneConfigKeys;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneClientFactory;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.om.ha.OMFailoverProxyProvider;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;


/**
 * Test Ozone Manager operation in distributed handler scenario.
 */
public class TestOzoneManagerHA {
    private MiniOzoneHAClusterImpl cluster = null;

    private ObjectStore objectStore;

    private OzoneConfiguration conf;

    private String clusterId;

    private String scmId;

    private int numOfOMs = 3;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public Timeout timeout = new Timeout(300000);

    /**
     * Test a client request when all OM nodes are running. The request should
     * succeed.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAllOMNodesRunning() throws Exception {
        createVolumeTest(true);
    }

    /**
     * Test client request succeeds even if one OM is down.
     */
    @Test
    public void testOneOMNodeDown() throws Exception {
        cluster.stopOzoneManager(1);
        Thread.sleep(((MiniOzoneHAClusterImpl.NODE_FAILURE_TIMEOUT) * 2));
        createVolumeTest(true);
    }

    /**
     * Test client request fails when 2 OMs are down.
     */
    @Test
    public void testTwoOMNodesDown() throws Exception {
        cluster.stopOzoneManager(1);
        cluster.stopOzoneManager(2);
        Thread.sleep(((MiniOzoneHAClusterImpl.NODE_FAILURE_TIMEOUT) * 2));
        createVolumeTest(false);
    }

    /**
     * Test that OMFailoverProxyProvider creates an OM proxy for each OM in the
     * cluster.
     */
    @Test
    public void testOMProxyProviderInitialization() throws Exception {
        OzoneClient rpcClient = cluster.getRpcClient();
        OMFailoverProxyProvider omFailoverProxyProvider = rpcClient.getObjectStore().getClientProxy().getOMProxyProvider();
        List<OMFailoverProxyProvider.OMProxyInfo> omProxies = omFailoverProxyProvider.getOMProxies();
        Assert.assertEquals(numOfOMs, omProxies.size());
        for (int i = 0; i < (numOfOMs); i++) {
            InetSocketAddress omRpcServerAddr = cluster.getOzoneManager(i).getOmRpcServerAddr();
            boolean omClientProxyExists = false;
            for (OMFailoverProxyProvider.OMProxyInfo omProxyInfo : omProxies) {
                if (omProxyInfo.getAddress().equals(omRpcServerAddr)) {
                    omClientProxyExists = true;
                    break;
                }
            }
            Assert.assertTrue((("There is no OM Client Proxy corresponding to OM " + "node") + (cluster.getOzoneManager(i).getOMNodId())), omClientProxyExists);
        }
    }

    /**
     * Test OMFailoverProxyProvider failover on connection exception to OM client.
     */
    @Test
    public void testOMProxyProviderFailoverOnConnectionFailure() throws Exception {
        OMFailoverProxyProvider omFailoverProxyProvider = objectStore.getClientProxy().getOMProxyProvider();
        String firstProxyNodeId = omFailoverProxyProvider.getCurrentProxyOMNodeId();
        createVolumeTest(true);
        // On stopping the current OM Proxy, the next connection attempt should
        // failover to a another OM proxy.
        cluster.stopOzoneManager(firstProxyNodeId);
        Thread.sleep(((OzoneConfigKeys.OZONE_CLIENT_FAILOVER_SLEEP_BASE_MILLIS_DEFAULT) * 4));
        // Next request to the proxy provider should result in a failover
        createVolumeTest(true);
        Thread.sleep(OzoneConfigKeys.OZONE_CLIENT_FAILOVER_SLEEP_BASE_MILLIS_DEFAULT);
        // Get the new OM Proxy NodeId
        String newProxyNodeId = omFailoverProxyProvider.getCurrentProxyOMNodeId();
        // Verify that a failover occured. the new proxy nodeId should be
        // different from the old proxy nodeId.
        Assert.assertNotEquals("Failover did not occur as expected", firstProxyNodeId, newProxyNodeId);
    }

    /**
     * Test OMFailoverProxyProvider failover when current OM proxy is not
     * the current OM Leader.
     */
    @Test
    public void testOMProxyProviderFailoverToCurrentLeader() throws Exception {
        OMFailoverProxyProvider omFailoverProxyProvider = objectStore.getClientProxy().getOMProxyProvider();
        // Run couple of createVolume tests to discover the current Leader OM
        createVolumeTest(true);
        createVolumeTest(true);
        // The OMFailoverProxyProvider will point to the current leader OM node.
        String leaderOMNodeId = omFailoverProxyProvider.getCurrentProxyOMNodeId();
        // Perform a manual failover of the proxy provider to move the
        // currentProxyIndex to a node other than the leader OM.
        omFailoverProxyProvider.performFailover(omFailoverProxyProvider.getProxy().proxy);
        String newProxyNodeId = omFailoverProxyProvider.getCurrentProxyOMNodeId();
        Assert.assertNotEquals(leaderOMNodeId, newProxyNodeId);
        // Once another request is sent to this new proxy node, the leader
        // information must be returned via the response and a failover must
        // happen to the leader proxy node.
        createVolumeTest(true);
        Thread.sleep(2000);
        String newLeaderOMNodeId = omFailoverProxyProvider.getCurrentProxyOMNodeId();
        // The old and new Leader OM NodeId must match since there was no new
        // election in the Ratis ring.
        Assert.assertEquals(leaderOMNodeId, newLeaderOMNodeId);
    }

    @Test
    public void testOMRetryProxy() throws Exception {
        // Stop all the OMs. After making 5 (set maxRetries value) attempts at
        // connection, the RpcClient should give up.
        for (int i = 0; i < (numOfOMs); i++) {
            cluster.stopOzoneManager(i);
        }
        final LogVerificationAppender appender = new LogVerificationAppender();
        final Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
        try {
            createVolumeTest(true);
            Assert.fail("TestOMRetryProxy should fail when there are no OMs running");
        } catch (ConnectException e) {
            // Each retry attempt tries upto 10 times to connect. So there should be
            // 3*10 "Retrying connect to server" messages
            Assert.assertEquals(30, appender.countLinesWithMessage("Retrying connect to server:"));
            Assert.assertEquals(1, appender.countLinesWithMessage(("Failed to connect to OM. Attempted " + "3 retries and 3 failovers")));
        }
    }

    @Test
    public void testReadRequest() throws Exception {
        String volumeName = "volume" + (RandomStringUtils.randomNumeric(5));
        objectStore.createVolume(volumeName);
        OMFailoverProxyProvider omFailoverProxyProvider = objectStore.getClientProxy().getOMProxyProvider();
        String currentLeaderNodeId = omFailoverProxyProvider.getCurrentProxyOMNodeId();
        // A read request from any proxy should failover to the current leader OM
        for (int i = 0; i < (numOfOMs); i++) {
            // Failover OMFailoverProxyProvider to OM at index i
            OzoneManager ozoneManager = cluster.getOzoneManager(i);
            String omHostName = ozoneManager.getOmRpcServerAddr().getHostName();
            int rpcPort = ozoneManager.getOmRpcServerAddr().getPort();
            // Get the ObjectStore and FailoverProxyProvider for OM at index i
            final ObjectStore store = OzoneClientFactory.getRpcClient(omHostName, rpcPort, conf).getObjectStore();
            final OMFailoverProxyProvider proxyProvider = store.getClientProxy().getOMProxyProvider();
            // Failover to the OM node that the objectStore points to
            omFailoverProxyProvider.performFailoverIfRequired(ozoneManager.getOMNodId());
            // A read request should result in the proxyProvider failing over to
            // leader node.
            OzoneVolume volume = store.getVolume(volumeName);
            Assert.assertEquals(volumeName, volume.getName());
            Assert.assertEquals(currentLeaderNodeId, proxyProvider.getCurrentProxyOMNodeId());
        }
    }
}

