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
package org.apache.hadoop.ozone.container.common.statemachine.commandhandler;


import ReplicationFactor.ONE;
import ReplicationFactor.THREE;
import ReplicationType.RATIS;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.scm.pipeline.PipelineID;
import org.apache.hadoop.ozone.HddsDatanodeService;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyLocationInfo;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test container closing.
 */
public class TestCloseContainerByPipeline {
    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    private static OzoneClient client;

    private static ObjectStore objectStore;

    @Test
    public void testIfCloseContainerCommandHandlerIsInvoked() throws Exception {
        OzoneOutputStream key = TestCloseContainerByPipeline.objectStore.getVolume("test").getBucket("test").createKey("standalone", 1024, RATIS, ONE, new HashMap());
        key.write("standalone".getBytes());
        key.close();
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName("test").setBucketName("test").setType(HddsProtos.ReplicationType.RATIS).setFactor(HddsProtos.ReplicationFactor.ONE).setDataSize(1024).setKeyName("standalone").build();
        OmKeyLocationInfo omKeyLocationInfo = TestCloseContainerByPipeline.cluster.getOzoneManager().lookupKey(keyArgs).getKeyLocationVersions().get(0).getBlocksLatestVersionOnly().get(0);
        long containerID = omKeyLocationInfo.getContainerID();
        ContainerInfo container = TestCloseContainerByPipeline.cluster.getStorageContainerManager().getContainerManager().getContainer(ContainerID.valueof(containerID));
        Pipeline pipeline = TestCloseContainerByPipeline.cluster.getStorageContainerManager().getPipelineManager().getPipeline(container.getPipelineID());
        List<DatanodeDetails> datanodes = pipeline.getNodes();
        Assert.assertEquals(datanodes.size(), 1);
        DatanodeDetails datanodeDetails = datanodes.get(0);
        HddsDatanodeService datanodeService = null;
        Assert.assertFalse(isContainerClosed(TestCloseContainerByPipeline.cluster, containerID, datanodeDetails));
        for (HddsDatanodeService datanodeServiceItr : TestCloseContainerByPipeline.cluster.getHddsDatanodes()) {
            if (datanodeDetails.equals(datanodeServiceItr.getDatanodeDetails())) {
                datanodeService = datanodeServiceItr;
                break;
            }
        }
        CommandHandler closeContainerHandler = datanodeService.getDatanodeStateMachine().getCommandDispatcher().getCloseContainerHandler();
        int lastInvocationCount = closeContainerHandler.getInvocationCount();
        // send the order to close the container
        TestCloseContainerByPipeline.cluster.getStorageContainerManager().getScmNodeManager().addDatanodeCommand(datanodeDetails.getUuid(), new org.apache.hadoop.ozone.protocol.commands.CloseContainerCommand(containerID, pipeline.getId()));
        GenericTestUtils.waitFor(() -> isContainerClosed(TestCloseContainerByPipeline.cluster, containerID, datanodeDetails), 500, (5 * 1000));
        // Make sure the closeContainerCommandHandler is Invoked
        Assert.assertTrue(((closeContainerHandler.getInvocationCount()) > lastInvocationCount));
    }

    @Test
    public void testCloseContainerViaStandAlone() throws IOException, InterruptedException, TimeoutException {
        OzoneOutputStream key = TestCloseContainerByPipeline.objectStore.getVolume("test").getBucket("test").createKey("standalone", 1024, RATIS, ONE, new HashMap());
        key.write("standalone".getBytes());
        key.close();
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName("test").setBucketName("test").setType(HddsProtos.ReplicationType.RATIS).setFactor(HddsProtos.ReplicationFactor.ONE).setDataSize(1024).setKeyName("standalone").build();
        OmKeyLocationInfo omKeyLocationInfo = TestCloseContainerByPipeline.cluster.getOzoneManager().lookupKey(keyArgs).getKeyLocationVersions().get(0).getBlocksLatestVersionOnly().get(0);
        long containerID = omKeyLocationInfo.getContainerID();
        ContainerInfo container = TestCloseContainerByPipeline.cluster.getStorageContainerManager().getContainerManager().getContainer(ContainerID.valueof(containerID));
        Pipeline pipeline = TestCloseContainerByPipeline.cluster.getStorageContainerManager().getPipelineManager().getPipeline(container.getPipelineID());
        List<DatanodeDetails> datanodes = pipeline.getNodes();
        Assert.assertEquals(datanodes.size(), 1);
        DatanodeDetails datanodeDetails = datanodes.get(0);
        Assert.assertFalse(isContainerClosed(TestCloseContainerByPipeline.cluster, containerID, datanodeDetails));
        // Send the order to close the container, give random pipeline id so that
        // the container will not be closed via RATIS
        TestCloseContainerByPipeline.cluster.getStorageContainerManager().getScmNodeManager().addDatanodeCommand(datanodeDetails.getUuid(), new org.apache.hadoop.ozone.protocol.commands.CloseContainerCommand(containerID, PipelineID.randomId()));
        // double check if it's really closed (waitFor also throws an exception)
        // TODO: change the below line after implementing QUASI_CLOSED to CLOSED
        // logic. The container will be QUASI closed as of now
        GenericTestUtils.waitFor(() -> isContainerQuasiClosed(TestCloseContainerByPipeline.cluster, containerID, datanodeDetails), 500, (5 * 1000));
        Assert.assertTrue(isContainerQuasiClosed(TestCloseContainerByPipeline.cluster, containerID, datanodeDetails));
    }

    @Test
    public void testCloseContainerViaRatis() throws IOException, InterruptedException, TimeoutException {
        OzoneOutputStream key = TestCloseContainerByPipeline.objectStore.getVolume("test").getBucket("test").createKey("ratis", 1024, RATIS, THREE, new HashMap());
        key.write("ratis".getBytes());
        key.close();
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName("test").setBucketName("test").setType(HddsProtos.ReplicationType.RATIS).setFactor(HddsProtos.ReplicationFactor.THREE).setDataSize(1024).setKeyName("ratis").build();
        OmKeyLocationInfo omKeyLocationInfo = TestCloseContainerByPipeline.cluster.getOzoneManager().lookupKey(keyArgs).getKeyLocationVersions().get(0).getBlocksLatestVersionOnly().get(0);
        long containerID = omKeyLocationInfo.getContainerID();
        ContainerInfo container = TestCloseContainerByPipeline.cluster.getStorageContainerManager().getContainerManager().getContainer(ContainerID.valueof(containerID));
        Pipeline pipeline = TestCloseContainerByPipeline.cluster.getStorageContainerManager().getPipelineManager().getPipeline(container.getPipelineID());
        List<DatanodeDetails> datanodes = pipeline.getNodes();
        Assert.assertEquals(3, datanodes.size());
        for (DatanodeDetails details : datanodes) {
            Assert.assertFalse(isContainerClosed(TestCloseContainerByPipeline.cluster, containerID, details));
            // send the order to close the container
            TestCloseContainerByPipeline.cluster.getStorageContainerManager().getScmNodeManager().addDatanodeCommand(details.getUuid(), new org.apache.hadoop.ozone.protocol.commands.CloseContainerCommand(containerID, pipeline.getId()));
        }
        // Make sure that it is CLOSED
        for (DatanodeDetails datanodeDetails : datanodes) {
            GenericTestUtils.waitFor(() -> isContainerClosed(TestCloseContainerByPipeline.cluster, containerID, datanodeDetails), 500, (15 * 1000));
            // double check if it's really closed (waitFor also throws an exception)
            Assert.assertTrue(isContainerClosed(TestCloseContainerByPipeline.cluster, containerID, datanodeDetails));
        }
    }
}

