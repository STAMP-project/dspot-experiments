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


import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.node.NodeManager;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.ozone.HddsDatanodeService;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests DeleteContainerCommand Handler.
 */
public class TestDeleteContainerHandler {
    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    private static ObjectStore objectStore;

    private static String volumeName = UUID.randomUUID().toString();

    private static String bucketName = UUID.randomUUID().toString();

    @Test(timeout = 60000)
    public void testDeleteContainerRequestHandlerOnClosedContainer() throws Exception {
        // the easiest way to create an open container is creating a key
        String keyName = UUID.randomUUID().toString();
        // create key
        createKey(keyName);
        // get containerID of the key
        ContainerID containerId = getContainerID(keyName);
        ContainerInfo container = TestDeleteContainerHandler.cluster.getStorageContainerManager().getContainerManager().getContainer(containerId);
        Pipeline pipeline = TestDeleteContainerHandler.cluster.getStorageContainerManager().getPipelineManager().getPipeline(container.getPipelineID());
        // We need to close the container because delete container only happens
        // on closed containers with force flag set to false.
        HddsDatanodeService hddsDatanodeService = TestDeleteContainerHandler.cluster.getHddsDatanodes().get(0);
        Assert.assertFalse(isContainerClosed(hddsDatanodeService, containerId.getId()));
        DatanodeDetails datanodeDetails = hddsDatanodeService.getDatanodeDetails();
        NodeManager nodeManager = TestDeleteContainerHandler.cluster.getStorageContainerManager().getScmNodeManager();
        // send the order to close the container
        nodeManager.addDatanodeCommand(datanodeDetails.getUuid(), new org.apache.hadoop.ozone.protocol.commands.CloseContainerCommand(containerId.getId(), pipeline.getId()));
        GenericTestUtils.waitFor(() -> isContainerClosed(hddsDatanodeService, containerId.getId()), 500, (5 * 1000));
        // double check if it's really closed (waitFor also throws an exception)
        Assert.assertTrue(isContainerClosed(hddsDatanodeService, containerId.getId()));
        // Check container exists before sending delete container command
        Assert.assertFalse(isContainerDeleted(hddsDatanodeService, containerId.getId()));
        // send delete container to the datanode
        nodeManager.addDatanodeCommand(datanodeDetails.getUuid(), new org.apache.hadoop.ozone.protocol.commands.DeleteContainerCommand(containerId.getId(), false));
        GenericTestUtils.waitFor(() -> isContainerDeleted(hddsDatanodeService, containerId.getId()), 500, (5 * 1000));
        Assert.assertTrue(isContainerDeleted(hddsDatanodeService, containerId.getId()));
    }

    @Test
    public void testDeleteContainerRequestHandlerOnOpenContainer() throws Exception {
        // the easiest way to create an open container is creating a key
        String keyName = UUID.randomUUID().toString();
        // create key
        createKey(keyName);
        // get containerID of the key
        ContainerID containerId = getContainerID(keyName);
        HddsDatanodeService hddsDatanodeService = TestDeleteContainerHandler.cluster.getHddsDatanodes().get(0);
        DatanodeDetails datanodeDetails = hddsDatanodeService.getDatanodeDetails();
        NodeManager nodeManager = TestDeleteContainerHandler.cluster.getStorageContainerManager().getScmNodeManager();
        // Send delete container command with force flag set to false.
        nodeManager.addDatanodeCommand(datanodeDetails.getUuid(), new org.apache.hadoop.ozone.protocol.commands.DeleteContainerCommand(containerId.getId(), false));
        // Here it should not delete it, and the container should exist in the
        // containerset
        int count = 1;
        // Checking for 5 seconds, whether it is containerSet, as after command
        // is issued, giving some time for it to process.
        while (!(isContainerDeleted(hddsDatanodeService, containerId.getId()))) {
            Thread.sleep(1000);
            count++;
            if (count == 5) {
                break;
            }
        } 
        Assert.assertFalse(isContainerDeleted(hddsDatanodeService, containerId.getId()));
        // Now delete container with force flag set to true. now it should delete
        // container
        nodeManager.addDatanodeCommand(datanodeDetails.getUuid(), new org.apache.hadoop.ozone.protocol.commands.DeleteContainerCommand(containerId.getId(), true));
        GenericTestUtils.waitFor(() -> isContainerDeleted(hddsDatanodeService, containerId.getId()), 500, (5 * 1000));
        Assert.assertTrue(isContainerDeleted(hddsDatanodeService, containerId.getId()));
    }
}

