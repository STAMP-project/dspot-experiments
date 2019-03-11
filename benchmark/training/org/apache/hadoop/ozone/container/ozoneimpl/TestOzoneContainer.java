/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.ozone.container.ozoneimpl;


import ContainerProtos.ContainerCommandRequestProto;
import ContainerProtos.ContainerCommandResponseProto;
import ContainerProtos.Result.CLOSED_CONTAINER_IO;
import ContainerProtos.Result.DELETE_ON_OPEN_CONTAINER;
import ContainerProtos.Result.SUCCESS;
import DatanodeDetails.Port.Name.STANDALONE;
import HddsConfigKeys.OZONE_METADATA_DIRS;
import OzoneConfigKeys.DFS_CONTAINER_IPC_PORT;
import OzoneConfigKeys.DFS_CONTAINER_IPC_RANDOM_PORT;
import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos;
import org.apache.hadoop.hdds.scm.ScmConfigKeys;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.XceiverClientGrpc;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.container.ContainerTestHelper;
import org.apache.hadoop.ozone.container.common.statemachine.DatanodeStateMachine;
import org.apache.hadoop.ozone.container.common.statemachine.StateContext;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.Timeout;
import org.mockito.Mockito;


/**
 * Tests ozone containers.
 */
public class TestOzoneContainer {
    /**
     * Set the timeout for every test.
     */
    @Rule
    public Timeout testTimeout = new Timeout(300000);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testCreateOzoneContainer() throws Exception {
        long containerID = ContainerTestHelper.getTestContainerID();
        OzoneConfiguration conf = TestOzoneContainer.newOzoneConfiguration();
        OzoneContainer container = null;
        MiniOzoneCluster cluster = null;
        try {
            cluster = MiniOzoneCluster.newBuilder(conf).build();
            cluster.waitForClusterToBeReady();
            // We don't start Ozone Container via data node, we will do it
            // independently in our test path.
            Pipeline pipeline = ContainerTestHelper.createSingleNodePipeline();
            conf.set(ScmConfigKeys.HDDS_DATANODE_DIR_KEY, tempFolder.getRoot().getPath());
            conf.setInt(DFS_CONTAINER_IPC_PORT, pipeline.getFirstNode().getPort(STANDALONE).getValue());
            conf.setBoolean(DFS_CONTAINER_IPC_RANDOM_PORT, false);
            DatanodeDetails datanodeDetails = TestUtils.randomDatanodeDetails();
            StateContext context = Mockito.mock(StateContext.class);
            DatanodeStateMachine dsm = Mockito.mock(DatanodeStateMachine.class);
            Mockito.when(dsm.getDatanodeDetails()).thenReturn(datanodeDetails);
            Mockito.when(context.getParent()).thenReturn(dsm);
            container = new OzoneContainer(datanodeDetails, conf, context);
            // Setting scmId, as we start manually ozone container.
            container.getDispatcher().setScmId(UUID.randomUUID().toString());
            container.start();
            XceiverClientGrpc client = new XceiverClientGrpc(pipeline, conf);
            client.connect();
            TestOzoneContainer.createContainerForTesting(client, containerID);
        } finally {
            if (container != null) {
                container.stop();
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testOzoneContainerViaDataNode() throws Exception {
        MiniOzoneCluster cluster = null;
        try {
            long containerID = ContainerTestHelper.getTestContainerID();
            OzoneConfiguration conf = TestOzoneContainer.newOzoneConfiguration();
            // Start ozone container Via Datanode create.
            Pipeline pipeline = ContainerTestHelper.createSingleNodePipeline();
            conf.setInt(DFS_CONTAINER_IPC_PORT, pipeline.getFirstNode().getPort(STANDALONE).getValue());
            cluster = MiniOzoneCluster.newBuilder(conf).setRandomContainerPort(false).build();
            cluster.waitForClusterToBeReady();
            // This client talks to ozone container via datanode.
            XceiverClientGrpc client = new XceiverClientGrpc(pipeline, conf);
            TestOzoneContainer.runTestOzoneContainerViaDataNode(containerID, client);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testBothGetandPutSmallFile() throws Exception {
        MiniOzoneCluster cluster = null;
        XceiverClientGrpc client = null;
        try {
            OzoneConfiguration conf = TestOzoneContainer.newOzoneConfiguration();
            conf.set(OZONE_METADATA_DIRS, tempFolder.getRoot().getPath());
            client = TestOzoneContainer.createClientForTesting(conf);
            cluster = MiniOzoneCluster.newBuilder(conf).setRandomContainerPort(false).build();
            cluster.waitForClusterToBeReady();
            long containerID = ContainerTestHelper.getTestContainerID();
            TestOzoneContainer.runTestBothGetandPutSmallFile(containerID, client);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testCloseContainer() throws Exception {
        MiniOzoneCluster cluster = null;
        XceiverClientGrpc client = null;
        ContainerProtos.ContainerCommandResponseProto response;
        ContainerProtos.ContainerCommandRequestProto writeChunkRequest;
        ContainerProtos.ContainerCommandRequestProto putBlockRequest;
        ContainerProtos.ContainerCommandRequestProto request;
        try {
            OzoneConfiguration conf = TestOzoneContainer.newOzoneConfiguration();
            conf.set(OZONE_METADATA_DIRS, tempFolder.getRoot().getPath());
            client = TestOzoneContainer.createClientForTesting(conf);
            cluster = MiniOzoneCluster.newBuilder(conf).setRandomContainerPort(false).build();
            cluster.waitForClusterToBeReady();
            client.connect();
            long containerID = ContainerTestHelper.getTestContainerID();
            TestOzoneContainer.createContainerForTesting(client, containerID);
            writeChunkRequest = TestOzoneContainer.writeChunkForContainer(client, containerID, 1024);
            putBlockRequest = ContainerTestHelper.getPutBlockRequest(client.getPipeline(), writeChunkRequest.getWriteChunk());
            // Put block before closing.
            response = client.sendCommand(putBlockRequest);
            Assert.assertNotNull(response);
            Assert.assertEquals(SUCCESS, response.getResult());
            Assert.assertTrue(putBlockRequest.getTraceID().equals(response.getTraceID()));
            // Close the contianer.
            request = ContainerTestHelper.getCloseContainer(client.getPipeline(), containerID);
            response = client.sendCommand(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(SUCCESS, response.getResult());
            Assert.assertTrue(request.getTraceID().equals(response.getTraceID()));
            // Assert that none of the write  operations are working after close.
            // Write chunks should fail now.
            response = client.sendCommand(writeChunkRequest);
            Assert.assertNotNull(response);
            Assert.assertEquals(CLOSED_CONTAINER_IO, response.getResult());
            Assert.assertTrue(writeChunkRequest.getTraceID().equals(response.getTraceID()));
            // Read chunk must work on a closed container.
            request = ContainerTestHelper.getReadChunkRequest(client.getPipeline(), writeChunkRequest.getWriteChunk());
            response = client.sendCommand(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(SUCCESS, response.getResult());
            Assert.assertTrue(request.getTraceID().equals(response.getTraceID()));
            // Put block will fail on a closed container.
            response = client.sendCommand(putBlockRequest);
            Assert.assertNotNull(response);
            Assert.assertEquals(CLOSED_CONTAINER_IO, response.getResult());
            Assert.assertTrue(putBlockRequest.getTraceID().equals(response.getTraceID()));
            // Get block must work on the closed container.
            request = ContainerTestHelper.getBlockRequest(client.getPipeline(), putBlockRequest.getPutBlock());
            response = client.sendCommand(request);
            int chunksCount = putBlockRequest.getPutBlock().getBlockData().getChunksCount();
            ContainerTestHelper.verifyGetBlock(request, response, chunksCount);
            // Delete block must fail on a closed container.
            request = ContainerTestHelper.getDeleteBlockRequest(client.getPipeline(), putBlockRequest.getPutBlock());
            response = client.sendCommand(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(CLOSED_CONTAINER_IO, response.getResult());
            Assert.assertTrue(request.getTraceID().equals(response.getTraceID()));
        } finally {
            if (client != null) {
                client.close();
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testDeleteContainer() throws Exception {
        MiniOzoneCluster cluster = null;
        XceiverClientGrpc client = null;
        ContainerProtos.ContainerCommandResponseProto response;
        ContainerProtos.ContainerCommandRequestProto request;
        ContainerProtos.ContainerCommandRequestProto writeChunkRequest;
        ContainerProtos.ContainerCommandRequestProto putBlockRequest;
        try {
            OzoneConfiguration conf = TestOzoneContainer.newOzoneConfiguration();
            conf.set(OZONE_METADATA_DIRS, tempFolder.getRoot().getPath());
            client = TestOzoneContainer.createClientForTesting(conf);
            cluster = MiniOzoneCluster.newBuilder(conf).setRandomContainerPort(false).build();
            cluster.waitForClusterToBeReady();
            client.connect();
            long containerID = ContainerTestHelper.getTestContainerID();
            TestOzoneContainer.createContainerForTesting(client, containerID);
            writeChunkRequest = TestOzoneContainer.writeChunkForContainer(client, containerID, 1024);
            putBlockRequest = ContainerTestHelper.getPutBlockRequest(client.getPipeline(), writeChunkRequest.getWriteChunk());
            // Put key before deleting.
            response = client.sendCommand(putBlockRequest);
            Assert.assertNotNull(response);
            Assert.assertEquals(SUCCESS, response.getResult());
            Assert.assertTrue(putBlockRequest.getTraceID().equals(response.getTraceID()));
            // Container cannot be deleted because force flag is set to false and
            // the container is still open
            request = ContainerTestHelper.getDeleteContainer(client.getPipeline(), containerID, false);
            response = client.sendCommand(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(DELETE_ON_OPEN_CONTAINER, response.getResult());
            Assert.assertTrue(request.getTraceID().equals(response.getTraceID()));
            // Container can be deleted, by setting force flag, even with out closing
            request = ContainerTestHelper.getDeleteContainer(client.getPipeline(), containerID, true);
            response = client.sendCommand(request);
            Assert.assertNotNull(response);
            Assert.assertEquals(SUCCESS, response.getResult());
            Assert.assertTrue(request.getTraceID().equals(response.getTraceID()));
        } finally {
            if (client != null) {
                client.close();
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testXcieverClientAsync() throws Exception {
        MiniOzoneCluster cluster = null;
        XceiverClientGrpc client = null;
        try {
            OzoneConfiguration conf = TestOzoneContainer.newOzoneConfiguration();
            conf.set(OZONE_METADATA_DIRS, tempFolder.getRoot().getPath());
            client = TestOzoneContainer.createClientForTesting(conf);
            cluster = MiniOzoneCluster.newBuilder(conf).setRandomContainerPort(false).build();
            cluster.waitForClusterToBeReady();
            long containerID = ContainerTestHelper.getTestContainerID();
            TestOzoneContainer.runAsyncTests(containerID, client);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

