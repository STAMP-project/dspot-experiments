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
package org.apache.hadoop.ozone.client.rpc;


import ReplicationFactor.ONE;
import ReplicationType.RATIS;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyInfo;
import org.apache.hadoop.ozone.om.helpers.OmKeyLocationInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the validity BCSID of a container.
 */
public class TestBCSID {
    private static OzoneConfiguration conf = new OzoneConfiguration();

    private static MiniOzoneCluster cluster;

    private static OzoneClient client;

    private static ObjectStore objectStore;

    private static String volumeName;

    private static String bucketName;

    @Test
    public void testBCSID() throws Exception {
        OzoneOutputStream key = TestBCSID.objectStore.getVolume(TestBCSID.volumeName).getBucket(TestBCSID.bucketName).createKey("ratis", 1024, RATIS, ONE, new HashMap());
        key.write("ratis".getBytes());
        key.close();
        // get the name of a valid container.
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(TestBCSID.volumeName).setBucketName(TestBCSID.bucketName).setType(HddsProtos.ReplicationType.RATIS).setFactor(HddsProtos.ReplicationFactor.ONE).setKeyName("ratis").build();
        OmKeyInfo keyInfo = TestBCSID.cluster.getOzoneManager().lookupKey(keyArgs);
        List<OmKeyLocationInfo> keyLocationInfos = keyInfo.getKeyLocationVersions().get(0).getBlocksLatestVersionOnly();
        Assert.assertEquals(1, keyLocationInfos.size());
        OmKeyLocationInfo omKeyLocationInfo = keyLocationInfos.get(0);
        long blockCommitSequenceId = TestBCSID.cluster.getHddsDatanodes().get(0).getDatanodeStateMachine().getContainer().getContainerSet().getContainer(omKeyLocationInfo.getContainerID()).getContainerReport().getBlockCommitSequenceId();
        Assert.assertTrue((blockCommitSequenceId > 0));
        // make sure the persisted block Id in OM is same as that seen in the
        // container report to be reported to SCM.
        Assert.assertEquals(blockCommitSequenceId, omKeyLocationInfo.getBlockCommitSequenceId());
        // verify that on restarting the datanode, it reloads the BCSID correctly.
        TestBCSID.cluster.restartHddsDatanode(0, true);
        Assert.assertEquals(blockCommitSequenceId, TestBCSID.cluster.getHddsDatanodes().get(0).getDatanodeStateMachine().getContainer().getContainerSet().getContainer(omKeyLocationInfo.getContainerID()).getContainerReport().getBlockCommitSequenceId());
    }
}

