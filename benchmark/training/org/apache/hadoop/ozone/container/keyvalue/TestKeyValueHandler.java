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
package org.apache.hadoop.ozone.container.keyvalue;


import ContainerProtos.CloseContainerRequestProto;
import ContainerProtos.ContainerCommandResponseProto;
import ContainerProtos.ContainerDataProto.State.INVALID;
import ContainerProtos.CreateContainerRequestProto;
import ContainerProtos.Result.INVALID_CONTAINER_STATE;
import ContainerProtos.Type.CloseContainer;
import ContainerProtos.Type.CreateContainer;
import ContainerProtos.Type.DeleteBlock;
import ContainerProtos.Type.DeleteChunk;
import ContainerProtos.Type.DeleteContainer;
import ContainerProtos.Type.GetBlock;
import ContainerProtos.Type.GetSmallFile;
import ContainerProtos.Type.ListBlock;
import ContainerProtos.Type.ListChunk;
import ContainerProtos.Type.ListContainer;
import ContainerProtos.Type.PutBlock;
import ContainerProtos.Type.PutSmallFile;
import ContainerProtos.Type.ReadChunk;
import ContainerProtos.Type.ReadContainer;
import ContainerProtos.Type.UpdateContainer;
import ContainerProtos.Type.WriteChunk;
import StorageUnit.GB;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos.ContainerCommandRequestProto;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.ozone.container.common.helpers.ContainerMetrics;
import org.apache.hadoop.ozone.container.common.impl.ContainerSet;
import org.apache.hadoop.ozone.container.common.impl.HddsDispatcher;
import org.apache.hadoop.ozone.container.common.statemachine.DatanodeStateMachine;
import org.apache.hadoop.ozone.container.common.statemachine.StateContext;
import org.apache.hadoop.ozone.container.common.transport.server.ratis.DispatcherContext;
import org.apache.hadoop.ozone.container.common.volume.VolumeSet;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link KeyValueHandler}.
 */
public class TestKeyValueHandler {
    @Rule
    public TestRule timeout = new Timeout(300000);

    private static HddsDispatcher dispatcher;

    private static KeyValueHandler handler;

    private static final String DATANODE_UUID = UUID.randomUUID().toString();

    private final String baseDir = MiniDFSCluster.getBaseDirectory();

    private final String volume = (baseDir) + "disk1";

    private static final long DUMMY_CONTAINER_ID = 9999;

    /**
     * Test that Handler handles different command types correctly.
     */
    @Test
    public void testHandlerCommandHandling() throws Exception {
        // Test Create Container Request handling
        ContainerCommandRequestProto createContainerRequest = ContainerProtos.ContainerCommandRequestProto.newBuilder().setCmdType(CreateContainer).setContainerID(TestKeyValueHandler.DUMMY_CONTAINER_ID).setDatanodeUuid(TestKeyValueHandler.DATANODE_UUID).setCreateContainer(CreateContainerRequestProto.getDefaultInstance()).build();
        DispatcherContext context = new DispatcherContext.Builder().build();
        TestKeyValueHandler.dispatcher.dispatch(createContainerRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleCreateContainer(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any());
        // Test Read Container Request handling
        ContainerCommandRequestProto readContainerRequest = getDummyCommandRequestProto(ReadContainer);
        TestKeyValueHandler.dispatcher.dispatch(readContainerRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleReadContainer(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any());
        // Test Update Container Request handling
        ContainerCommandRequestProto updateContainerRequest = getDummyCommandRequestProto(UpdateContainer);
        TestKeyValueHandler.dispatcher.dispatch(updateContainerRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleUpdateContainer(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any());
        // Test Delete Container Request handling
        ContainerCommandRequestProto deleteContainerRequest = getDummyCommandRequestProto(DeleteContainer);
        TestKeyValueHandler.dispatcher.dispatch(deleteContainerRequest, null);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleDeleteContainer(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any());
        // Test List Container Request handling
        ContainerCommandRequestProto listContainerRequest = getDummyCommandRequestProto(ListContainer);
        TestKeyValueHandler.dispatcher.dispatch(listContainerRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleUnsupportedOp(ArgumentMatchers.any(ContainerCommandRequestProto.class));
        // Test Close Container Request handling
        ContainerCommandRequestProto closeContainerRequest = getDummyCommandRequestProto(CloseContainer);
        TestKeyValueHandler.dispatcher.dispatch(closeContainerRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleCloseContainer(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any());
        // Test Put Block Request handling
        ContainerCommandRequestProto putBlockRequest = getDummyCommandRequestProto(PutBlock);
        TestKeyValueHandler.dispatcher.dispatch(putBlockRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handlePutBlock(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Test Get Block Request handling
        ContainerCommandRequestProto getBlockRequest = getDummyCommandRequestProto(GetBlock);
        TestKeyValueHandler.dispatcher.dispatch(getBlockRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleGetBlock(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any());
        // Test Delete Block Request handling
        ContainerCommandRequestProto deleteBlockRequest = getDummyCommandRequestProto(DeleteBlock);
        TestKeyValueHandler.dispatcher.dispatch(deleteBlockRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleDeleteBlock(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any());
        // Test List Block Request handling
        ContainerCommandRequestProto listBlockRequest = getDummyCommandRequestProto(ListBlock);
        TestKeyValueHandler.dispatcher.dispatch(listBlockRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(2)).handleUnsupportedOp(ArgumentMatchers.any(ContainerCommandRequestProto.class));
        // Test Read Chunk Request handling
        ContainerCommandRequestProto readChunkRequest = getDummyCommandRequestProto(ReadChunk);
        TestKeyValueHandler.dispatcher.dispatch(readChunkRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleReadChunk(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Test Delete Chunk Request handling
        ContainerCommandRequestProto deleteChunkRequest = getDummyCommandRequestProto(DeleteChunk);
        TestKeyValueHandler.dispatcher.dispatch(deleteChunkRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleDeleteChunk(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any());
        // Test Write Chunk Request handling
        ContainerCommandRequestProto writeChunkRequest = getDummyCommandRequestProto(WriteChunk);
        TestKeyValueHandler.dispatcher.dispatch(writeChunkRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleWriteChunk(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Test List Chunk Request handling
        ContainerCommandRequestProto listChunkRequest = getDummyCommandRequestProto(ListChunk);
        TestKeyValueHandler.dispatcher.dispatch(listChunkRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(3)).handleUnsupportedOp(ArgumentMatchers.any(ContainerCommandRequestProto.class));
        // Test Put Small File Request handling
        ContainerCommandRequestProto putSmallFileRequest = getDummyCommandRequestProto(PutSmallFile);
        TestKeyValueHandler.dispatcher.dispatch(putSmallFileRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handlePutSmallFile(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Test Get Small File Request handling
        ContainerCommandRequestProto getSmallFileRequest = getDummyCommandRequestProto(GetSmallFile);
        TestKeyValueHandler.dispatcher.dispatch(getSmallFileRequest, context);
        Mockito.verify(TestKeyValueHandler.handler, Mockito.times(1)).handleGetSmallFile(ArgumentMatchers.any(ContainerCommandRequestProto.class), ArgumentMatchers.any());
    }

    @Test
    public void testVolumeSetInKeyValueHandler() throws Exception {
        File path = GenericTestUtils.getRandomizedTestDir();
        try {
            Configuration conf = new OzoneConfiguration();
            conf.set(HDDS_DATANODE_DIR_KEY, path.getAbsolutePath());
            ContainerSet cset = new ContainerSet();
            int[] interval = new int[1];
            interval[0] = 2;
            ContainerMetrics metrics = new ContainerMetrics(interval);
            VolumeSet volumeSet = new VolumeSet(UUID.randomUUID().toString(), conf);
            DatanodeDetails datanodeDetails = Mockito.mock(DatanodeDetails.class);
            DatanodeStateMachine stateMachine = Mockito.mock(DatanodeStateMachine.class);
            StateContext context = Mockito.mock(StateContext.class);
            Mockito.when(stateMachine.getDatanodeDetails()).thenReturn(datanodeDetails);
            Mockito.when(context.getParent()).thenReturn(stateMachine);
            KeyValueHandler keyValueHandler = new KeyValueHandler(conf, context, cset, volumeSet, metrics);
            Assert.assertEquals(("org.apache.hadoop.ozone.container.common" + ".volume.RoundRobinVolumeChoosingPolicy"), keyValueHandler.getVolumeChoosingPolicyForTesting().getClass().getName());
            // Set a class which is not of sub class of VolumeChoosingPolicy
            conf.set(HDDS_DATANODE_VOLUME_CHOOSING_POLICY, "org.apache.hadoop.ozone.container.common.impl.HddsDispatcher");
            try {
                new KeyValueHandler(conf, context, cset, volumeSet, metrics);
            } catch (RuntimeException ex) {
                GenericTestUtils.assertExceptionContains(("class org.apache.hadoop" + (".ozone.container.common.impl.HddsDispatcher not org.apache" + ".hadoop.ozone.container.common.interfaces.VolumeChoosingPolicy")), ex);
            }
        } finally {
            FileUtil.fullyDelete(path);
        }
    }

    @Test
    public void testCloseInvalidContainer() throws IOException {
        long containerID = 1234L;
        Configuration conf = new Configuration();
        KeyValueContainerData kvData = new KeyValueContainerData(containerID, ((long) (GB.toBytes(1))), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        KeyValueContainer container = new KeyValueContainer(kvData, conf);
        kvData.setState(INVALID);
        // Create Close container request
        ContainerCommandRequestProto closeContainerRequest = ContainerProtos.ContainerCommandRequestProto.newBuilder().setCmdType(CloseContainer).setContainerID(TestKeyValueHandler.DUMMY_CONTAINER_ID).setDatanodeUuid(TestKeyValueHandler.DATANODE_UUID).setCloseContainer(CloseContainerRequestProto.getDefaultInstance()).build();
        TestKeyValueHandler.dispatcher.dispatch(closeContainerRequest, null);
        Mockito.when(TestKeyValueHandler.handler.handleCloseContainer(ArgumentMatchers.any(), ArgumentMatchers.any())).thenCallRealMethod();
        Mockito.doCallRealMethod().when(TestKeyValueHandler.handler).closeContainer(ArgumentMatchers.any());
        // Closing invalid container should return error response.
        ContainerProtos.ContainerCommandResponseProto response = TestKeyValueHandler.handler.handleCloseContainer(closeContainerRequest, container);
        Assert.assertTrue("Close container should return Invalid container error", response.getResult().equals(INVALID_CONTAINER_STATE));
    }
}

