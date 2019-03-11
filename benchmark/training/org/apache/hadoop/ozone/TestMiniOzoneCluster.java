/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone;


import EndpointStateMachine.EndPointStates.GETVERSION;
import EndpointStateMachine.EndPointStates.HEARTBEAT;
import HddsConfigKeys.OZONE_METADATA_DIRS;
import HddsProtos.ReplicationFactor.ONE;
import HddsProtos.ReplicationType.STAND_ALONE;
import OzoneConfigKeys.DFS_CONTAINER_IPC_PORT;
import OzoneConfigKeys.DFS_CONTAINER_IPC_PORT_DEFAULT;
import OzoneConfigKeys.DFS_CONTAINER_IPC_RANDOM_PORT;
import OzoneConfigKeys.DFS_CONTAINER_RATIS_IPC_RANDOM_PORT;
import Pipeline.PipelineState.OPEN;
import Port.Name.STANDALONE;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.XceiverClientGrpc;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.scm.pipeline.PipelineID;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.ozone.container.common.SCMTestUtils;
import org.apache.hadoop.ozone.container.common.helpers.ContainerUtils;
import org.apache.hadoop.ozone.container.common.statemachine.DatanodeStateMachine;
import org.apache.hadoop.ozone.container.common.statemachine.EndpointStateMachine;
import org.apache.hadoop.ozone.container.ozoneimpl.TestOzoneContainer;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.test.TestGenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for mini ozone cluster.
 */
public class TestMiniOzoneCluster {
    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    private static final File TEST_ROOT = TestGenericTestUtils.getTestDir();

    private static final File WRITE_TMP = new File(TestMiniOzoneCluster.TEST_ROOT, "write");

    private static final File READ_TMP = new File(TestMiniOzoneCluster.TEST_ROOT, "read");

    @Test(timeout = 30000)
    public void testStartMultipleDatanodes() throws Exception {
        final int numberOfNodes = 3;
        TestMiniOzoneCluster.cluster = MiniOzoneCluster.newBuilder(TestMiniOzoneCluster.conf).setNumDatanodes(numberOfNodes).build();
        TestMiniOzoneCluster.cluster.waitForClusterToBeReady();
        List<HddsDatanodeService> datanodes = TestMiniOzoneCluster.cluster.getHddsDatanodes();
        Assert.assertEquals(numberOfNodes, datanodes.size());
        for (HddsDatanodeService dn : datanodes) {
            // Create a single member pipe line
            List<DatanodeDetails> dns = new ArrayList<>();
            dns.add(dn.getDatanodeDetails());
            Pipeline pipeline = Pipeline.newBuilder().setState(OPEN).setId(PipelineID.randomId()).setType(STAND_ALONE).setFactor(ONE).setNodes(dns).build();
            // Verify client is able to connect to the container
            try (XceiverClientGrpc client = new XceiverClientGrpc(pipeline, TestMiniOzoneCluster.conf)) {
                client.connect();
                Assert.assertTrue(client.isConnected(pipeline.getFirstNode()));
            }
        }
    }

    @Test
    public void testDatanodeIDPersistent() throws Exception {
        // Generate IDs for testing
        DatanodeDetails id1 = TestUtils.randomDatanodeDetails();
        DatanodeDetails id2 = TestUtils.randomDatanodeDetails();
        DatanodeDetails id3 = TestUtils.randomDatanodeDetails();
        id1.setPort(DatanodeDetails.newPort(STANDALONE, 1));
        id2.setPort(DatanodeDetails.newPort(STANDALONE, 2));
        id3.setPort(DatanodeDetails.newPort(STANDALONE, 3));
        // Write a single ID to the file and read it out
        File validIdsFile = new File(TestMiniOzoneCluster.WRITE_TMP, "valid-values.id");
        validIdsFile.delete();
        ContainerUtils.writeDatanodeDetailsTo(id1, validIdsFile);
        DatanodeDetails validId = ContainerUtils.readDatanodeDetailsFrom(validIdsFile);
        Assert.assertEquals(id1, validId);
        Assert.assertEquals(id1.getProtoBufMessage(), validId.getProtoBufMessage());
        // Read should return an empty value if file doesn't exist
        File nonExistFile = new File(TestMiniOzoneCluster.READ_TMP, "non_exist.id");
        nonExistFile.delete();
        try {
            ContainerUtils.readDatanodeDetailsFrom(nonExistFile);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IOException));
        }
        // Read should fail if the file is malformed
        File malformedFile = new File(TestMiniOzoneCluster.READ_TMP, "malformed.id");
        createMalformedIDFile(malformedFile);
        try {
            ContainerUtils.readDatanodeDetailsFrom(malformedFile);
            Assert.fail("Read a malformed ID file should fail");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IOException));
        }
    }

    @Test
    public void testContainerRandomPort() throws IOException {
        Configuration ozoneConf = SCMTestUtils.getConf();
        File testDir = PathUtils.getTestDir(TestOzoneContainer.class);
        ozoneConf.set(DFS_DATANODE_DATA_DIR_KEY, testDir.getAbsolutePath());
        ozoneConf.set(OZONE_METADATA_DIRS, TestMiniOzoneCluster.TEST_ROOT.toString());
        // Each instance of SM will create an ozone container
        // that bounds to a random port.
        ozoneConf.setBoolean(DFS_CONTAINER_IPC_RANDOM_PORT, true);
        ozoneConf.setBoolean(DFS_CONTAINER_RATIS_IPC_RANDOM_PORT, true);
        try (DatanodeStateMachine sm1 = new DatanodeStateMachine(TestUtils.randomDatanodeDetails(), ozoneConf);DatanodeStateMachine sm2 = new DatanodeStateMachine(TestUtils.randomDatanodeDetails(), ozoneConf);DatanodeStateMachine sm3 = new DatanodeStateMachine(TestUtils.randomDatanodeDetails(), ozoneConf)) {
            HashSet<Integer> ports = new HashSet<Integer>();
            Assert.assertTrue(ports.add(sm1.getContainer().getReadChannel().getIPCPort()));
            Assert.assertTrue(ports.add(sm2.getContainer().getReadChannel().getIPCPort()));
            Assert.assertTrue(ports.add(sm3.getContainer().getReadChannel().getIPCPort()));
            // Assert that ratis is also on a different port.
            Assert.assertTrue(ports.add(sm1.getContainer().getWriteChannel().getIPCPort()));
            Assert.assertTrue(ports.add(sm2.getContainer().getWriteChannel().getIPCPort()));
            Assert.assertTrue(ports.add(sm3.getContainer().getWriteChannel().getIPCPort()));
        }
        // Turn off the random port flag and test again
        ozoneConf.setBoolean(DFS_CONTAINER_IPC_RANDOM_PORT, false);
        try (DatanodeStateMachine sm1 = new DatanodeStateMachine(TestUtils.randomDatanodeDetails(), ozoneConf);DatanodeStateMachine sm2 = new DatanodeStateMachine(TestUtils.randomDatanodeDetails(), ozoneConf);DatanodeStateMachine sm3 = new DatanodeStateMachine(TestUtils.randomDatanodeDetails(), ozoneConf)) {
            HashSet<Integer> ports = new HashSet<Integer>();
            Assert.assertTrue(ports.add(sm1.getContainer().getReadChannel().getIPCPort()));
            Assert.assertFalse(ports.add(sm2.getContainer().getReadChannel().getIPCPort()));
            Assert.assertFalse(ports.add(sm3.getContainer().getReadChannel().getIPCPort()));
            Assert.assertEquals(ports.iterator().next().intValue(), TestMiniOzoneCluster.conf.getInt(DFS_CONTAINER_IPC_PORT, DFS_CONTAINER_IPC_PORT_DEFAULT));
        }
    }

    /**
     * Test that a DN can register with SCM even if it was started before the SCM.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testDNstartAfterSCM() throws Exception {
        // Start a cluster with 1 DN
        TestMiniOzoneCluster.cluster = MiniOzoneCluster.newBuilder(TestMiniOzoneCluster.conf).setNumDatanodes(1).build();
        TestMiniOzoneCluster.cluster.waitForClusterToBeReady();
        // Stop the SCM
        StorageContainerManager scm = TestMiniOzoneCluster.cluster.getStorageContainerManager();
        scm.stop();
        // Restart DN
        TestMiniOzoneCluster.cluster.restartHddsDatanode(0, false);
        // DN should be in GETVERSION state till the SCM is restarted.
        // Check DN endpoint state for 20 seconds
        DatanodeStateMachine dnStateMachine = TestMiniOzoneCluster.cluster.getHddsDatanodes().get(0).getDatanodeStateMachine();
        for (int i = 0; i < 20; i++) {
            for (EndpointStateMachine endpoint : dnStateMachine.getConnectionManager().getValues()) {
                Assert.assertEquals(GETVERSION, endpoint.getState());
            }
            Thread.sleep(1000);
        }
        // DN should successfully register with the SCM after SCM is restarted.
        // Restart the SCM
        TestMiniOzoneCluster.cluster.restartStorageContainerManager();
        // Wait for DN to register
        TestMiniOzoneCluster.cluster.waitForClusterToBeReady();
        // DN should be in HEARTBEAT state after registering with the SCM
        for (EndpointStateMachine endpoint : dnStateMachine.getConnectionManager().getValues()) {
            Assert.assertEquals(HEARTBEAT, endpoint.getState());
        }
    }
}

