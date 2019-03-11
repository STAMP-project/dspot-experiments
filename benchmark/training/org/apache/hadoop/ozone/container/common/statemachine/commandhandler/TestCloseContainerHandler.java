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
package org.apache.hadoop.ozone.container.common.statemachine.commandhandler;


import ReplicationFactor.ONE;
import ReplicationType.STAND_ALONE;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.ScmConfigKeys;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneClientFactory;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.client.rest.OzoneException;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyLocationInfo;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to behaviour of the datanode when recieve close container command.
 */
public class TestCloseContainerHandler {
    @Test
    public void test() throws IOException, InterruptedException, TimeoutException, OzoneException {
        // setup a cluster (1G free space is enough for a unit test)
        OzoneConfiguration conf = new OzoneConfiguration();
        conf.set(ScmConfigKeys.OZONE_SCM_CONTAINER_SIZE, "1GB");
        MiniOzoneCluster cluster = MiniOzoneCluster.newBuilder(conf).setNumDatanodes(1).build();
        cluster.waitForClusterToBeReady();
        // the easiest way to create an open container is creating a key
        OzoneClient client = OzoneClientFactory.getClient(conf);
        ObjectStore objectStore = client.getObjectStore();
        objectStore.createVolume("test");
        objectStore.getVolume("test").createBucket("test");
        OzoneOutputStream key = objectStore.getVolume("test").getBucket("test").createKey("test", 1024, STAND_ALONE, ONE, new HashMap());
        key.write("test".getBytes());
        key.close();
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName("test").setBucketName("test").setType(HddsProtos.ReplicationType.STAND_ALONE).setFactor(HddsProtos.ReplicationFactor.ONE).setDataSize(1024).setKeyName("test").build();
        OmKeyLocationInfo omKeyLocationInfo = cluster.getOzoneManager().lookupKey(keyArgs).getKeyLocationVersions().get(0).getBlocksLatestVersionOnly().get(0);
        ContainerID containerId = ContainerID.valueof(omKeyLocationInfo.getContainerID());
        ContainerInfo container = cluster.getStorageContainerManager().getContainerManager().getContainer(containerId);
        Pipeline pipeline = cluster.getStorageContainerManager().getPipelineManager().getPipeline(container.getPipelineID());
        Assert.assertFalse(isContainerClosed(cluster, containerId.getId()));
        DatanodeDetails datanodeDetails = cluster.getHddsDatanodes().get(0).getDatanodeDetails();
        // send the order to close the container
        cluster.getStorageContainerManager().getScmNodeManager().addDatanodeCommand(datanodeDetails.getUuid(), new org.apache.hadoop.ozone.protocol.commands.CloseContainerCommand(containerId.getId(), pipeline.getId()));
        GenericTestUtils.waitFor(() -> isContainerClosed(cluster, containerId.getId()), 500, (5 * 1000));
        // double check if it's really closed (waitFor also throws an exception)
        Assert.assertTrue(isContainerClosed(cluster, containerId.getId()));
    }
}

