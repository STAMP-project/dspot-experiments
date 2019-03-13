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


import ContainerProtos.ContainerCommandRequestProto.Builder;
import ContainerProtos.ContainerDataProto.State;
import ContainerProtos.CreateContainerRequestProto;
import ContainerProtos.Result.CONTAINER_MISSING;
import ContainerProtos.Type.CreateContainer;
import ReplicationFactor.ONE;
import ReplicationType.RATIS;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.client.ObjectStore;
import org.apache.hadoop.ozone.client.OzoneClient;
import org.apache.hadoop.ozone.client.io.KeyOutputStream;
import org.apache.hadoop.ozone.client.io.OzoneOutputStream;
import org.apache.hadoop.ozone.container.common.impl.HddsDispatcher;
import org.apache.hadoop.ozone.container.ozoneimpl.OzoneContainer;
import org.apache.hadoop.ozone.om.helpers.OmKeyArgs;
import org.apache.hadoop.ozone.om.helpers.OmKeyLocationInfo;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the containerStateMachine failure handling.
 */
public class TestContainerStateMachineFailures {
    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    private static OzoneClient client;

    private static ObjectStore objectStore;

    private static String volumeName;

    private static String bucketName;

    private static String path;

    private static int chunkSize;

    @Test
    public void testContainerStateMachineFailures() throws Exception {
        OzoneOutputStream key = TestContainerStateMachineFailures.objectStore.getVolume(TestContainerStateMachineFailures.volumeName).getBucket(TestContainerStateMachineFailures.bucketName).createKey("ratis", 1024, RATIS, ONE, new HashMap());
        // First write and flush creates a container in the datanode
        key.write("ratis".getBytes());
        key.flush();
        key.write("ratis".getBytes());
        // get the name of a valid container
        OmKeyArgs keyArgs = new OmKeyArgs.Builder().setVolumeName(TestContainerStateMachineFailures.volumeName).setBucketName(TestContainerStateMachineFailures.bucketName).setType(HddsProtos.ReplicationType.RATIS).setFactor(HddsProtos.ReplicationFactor.ONE).setKeyName("ratis").build();
        KeyOutputStream groupOutputStream = ((KeyOutputStream) (key.getOutputStream()));
        List<OmKeyLocationInfo> locationInfoList = groupOutputStream.getLocationInfoList();
        Assert.assertEquals(1, locationInfoList.size());
        OmKeyLocationInfo omKeyLocationInfo = locationInfoList.get(0);
        // delete the container dir
        FileUtil.fullyDelete(new File(TestContainerStateMachineFailures.cluster.getHddsDatanodes().get(0).getDatanodeStateMachine().getContainer().getContainerSet().getContainer(omKeyLocationInfo.getContainerID()).getContainerData().getContainerPath()));
        key.close();
        long containerID = omKeyLocationInfo.getContainerID();
        // Make sure the container is marked unhealthy
        Assert.assertTrue(((TestContainerStateMachineFailures.cluster.getHddsDatanodes().get(0).getDatanodeStateMachine().getContainer().getContainerSet().getContainer(containerID).getContainerState()) == (State.UNHEALTHY)));
        OzoneContainer ozoneContainer = TestContainerStateMachineFailures.cluster.getHddsDatanodes().get(0).getDatanodeStateMachine().getContainer();
        // make sure the missing containerSet is empty
        HddsDispatcher dispatcher = ((HddsDispatcher) (ozoneContainer.getDispatcher()));
        Assert.assertTrue(dispatcher.getMissingContainerSet().isEmpty());
        // restart the hdds datanode and see if the container is listed in the
        // in the missing container set and not in the regular set
        TestContainerStateMachineFailures.cluster.restartHddsDatanode(0, true);
        ozoneContainer = TestContainerStateMachineFailures.cluster.getHddsDatanodes().get(0).getDatanodeStateMachine().getContainer();
        dispatcher = ((HddsDispatcher) (ozoneContainer.getDispatcher()));
        Assert.assertNull(ozoneContainer.getContainerSet().getContainer(containerID));
        Assert.assertTrue(dispatcher.getMissingContainerSet().contains(containerID));
        ContainerProtos.ContainerCommandRequestProto.Builder request = ContainerProtos.ContainerCommandRequestProto.newBuilder();
        request.setCmdType(CreateContainer);
        request.setContainerID(containerID);
        request.setCreateContainer(CreateContainerRequestProto.getDefaultInstance());
        request.setTraceID(UUID.randomUUID().toString());
        request.setDatanodeUuid(TestContainerStateMachineFailures.cluster.getHddsDatanodes().get(0).getDatanodeDetails().getUuidString());
        Assert.assertEquals(CONTAINER_MISSING, dispatcher.dispatch(request.build(), null).getResult());
    }
}

