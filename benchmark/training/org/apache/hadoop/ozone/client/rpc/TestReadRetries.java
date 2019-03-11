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
 * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.ozone.client.rpc;


import HddsProtos.LifeCycleState;
import OmKeyArgs.Builder;
import ReplicationFactor.THREE;
import ReplicationType.RATIS;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.XceiverClientManager;
import org.apache.hadoop.hdds.scm.XceiverClientRatis;
import org.apache.hadoop.hdds.scm.XceiverClientSpi;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.scm.protocolPB.StorageContainerLocationProtocolClientSideTranslatorPB;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneBucket;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.OzoneKeyDetails;
import org.apache.hadoop.ozone.client.OzoneKeyLocation;
import org.apache.hadoop.ozone.client.OzoneVolume;
import org.apache.hadoop.ozone.client.io.KeyOutputStream;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.om.OzoneManager;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyLocationInfo;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test read retries from multiple nodes in the pipeline.
 */
public class TestReadRetries {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static MiniOzoneCluster cluster = null;

    private static OzoneClient ozClient = null;

    private static ObjectStore store = null;

    private static OzoneManager ozoneManager;

    private static StorageContainerLocationProtocolClientSideTranslatorPB storageContainerLocationClient;

    private static final String SCM_ID = UUID.randomUUID().toString();

    @Test
    public void testPutKeyAndGetKeyThreeNodes() throws Exception {
        String volumeName = UUID.randomUUID().toString();
        String bucketName = UUID.randomUUID().toString();
        String value = "sample value";
        TestReadRetries.store.createVolume(volumeName);
        OzoneVolume volume = TestReadRetries.store.getVolume(volumeName);
        volume.createBucket(bucketName);
        OzoneBucket bucket = volume.getBucket(bucketName);
        String keyName = UUID.randomUUID().toString();
        OzoneOutputStream out = bucket.createKey(keyName, value.getBytes().length, RATIS, THREE, new HashMap());
        KeyOutputStream groupOutputStream = ((KeyOutputStream) (out.getOutputStream()));
        XceiverClientManager manager = groupOutputStream.getXceiverClientManager();
        out.write(value.getBytes());
        out.close();
        // First, confirm the key info from the client matches the info in OM.
        OmKeyArgs.Builder builder = new OmKeyArgs.Builder();
        builder.setVolumeName(volumeName).setBucketName(bucketName).setKeyName(keyName);
        OmKeyLocationInfo keyInfo = TestReadRetries.ozoneManager.lookupKey(builder.build()).getKeyLocationVersions().get(0).getBlocksLatestVersionOnly().get(0);
        long containerID = keyInfo.getContainerID();
        long localID = keyInfo.getLocalID();
        OzoneKeyDetails keyDetails = bucket.getKey(keyName);
        Assert.assertEquals(keyName, keyDetails.getName());
        List<OzoneKeyLocation> keyLocations = keyDetails.getOzoneKeyLocations();
        Assert.assertEquals(1, keyLocations.size());
        Assert.assertEquals(containerID, keyLocations.get(0).getContainerID());
        Assert.assertEquals(localID, keyLocations.get(0).getLocalID());
        // Make sure that the data size matched.
        Assert.assertEquals(value.getBytes().length, keyLocations.get(0).getLength());
        ContainerInfo container = TestReadRetries.cluster.getStorageContainerManager().getContainerManager().getContainer(ContainerID.valueof(containerID));
        Pipeline pipeline = TestReadRetries.cluster.getStorageContainerManager().getPipelineManager().getPipeline(container.getPipelineID());
        List<DatanodeDetails> datanodes = pipeline.getNodes();
        DatanodeDetails datanodeDetails = datanodes.get(0);
        Assert.assertNotNull(datanodeDetails);
        XceiverClientSpi clientSpi = manager.acquireClient(pipeline);
        Assert.assertTrue((clientSpi instanceof XceiverClientRatis));
        XceiverClientRatis ratisClient = ((XceiverClientRatis) (clientSpi));
        ratisClient.watchForCommit(keyInfo.getBlockCommitSequenceId(), 5000);
        // shutdown the datanode
        TestReadRetries.cluster.shutdownHddsDatanode(datanodeDetails);
        Assert.assertTrue(((container.getState()) == (LifeCycleState.OPEN)));
        // try to read, this shouls be successful
        readKey(bucket, keyName, value);
        Assert.assertTrue(((container.getState()) == (LifeCycleState.OPEN)));
        // shutdown the second datanode
        datanodeDetails = datanodes.get(1);
        TestReadRetries.cluster.shutdownHddsDatanode(datanodeDetails);
        Assert.assertTrue(((container.getState()) == (LifeCycleState.OPEN)));
        // the container is open and with loss of 2 nodes we still should be able
        // to read via Standalone protocol
        // try to read
        readKey(bucket, keyName, value);
        // shutdown the 3rd datanode
        datanodeDetails = datanodes.get(2);
        TestReadRetries.cluster.shutdownHddsDatanode(datanodeDetails);
        try {
            // try to read
            readKey(bucket, keyName, value);
            Assert.fail("Expected exception not thrown");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("Failed to execute command"));
            Assert.assertTrue(e.getMessage().contains(("on the pipeline " + (pipeline.getId()))));
        }
        manager.releaseClient(clientSpi, false);
    }
}

