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


import ContainerProtos.Result.NO_SUCH_BLOCK;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.hdds.client.BlockID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.datanode.proto.ContainerProtos;
import org.apache.hadoop.hdds.scm.container.common.helpers.StorageContainerException;
import org.apache.hadoop.ozone.container.common.helpers.BlockData;
import org.apache.hadoop.ozone.container.common.helpers.ChunkInfo;
import org.apache.hadoop.ozone.container.common.volume.RoundRobinVolumeChoosingPolicy;
import org.apache.hadoop.ozone.container.common.volume.VolumeSet;
import org.apache.hadoop.ozone.container.keyvalue.impl.BlockManagerImpl;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * This class is used to test key related operations on the container.
 */
public class TestBlockManagerImpl {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private OzoneConfiguration config;

    private String scmId = UUID.randomUUID().toString();

    private VolumeSet volumeSet;

    private RoundRobinVolumeChoosingPolicy volumeChoosingPolicy;

    private KeyValueContainerData keyValueContainerData;

    private KeyValueContainer keyValueContainer;

    private BlockData blockData;

    private BlockManagerImpl blockManager;

    private BlockID blockID;

    @Test
    public void testPutAndGetBlock() throws Exception {
        Assert.assertEquals(0, keyValueContainer.getContainerData().getKeyCount());
        // Put Block
        blockManager.putBlock(keyValueContainer, blockData);
        Assert.assertEquals(1, keyValueContainer.getContainerData().getKeyCount());
        // Get Block
        BlockData fromGetBlockData = blockManager.getBlock(keyValueContainer, blockData.getBlockID());
        Assert.assertEquals(blockData.getContainerID(), fromGetBlockData.getContainerID());
        Assert.assertEquals(blockData.getLocalID(), fromGetBlockData.getLocalID());
        Assert.assertEquals(blockData.getChunks().size(), fromGetBlockData.getChunks().size());
        Assert.assertEquals(blockData.getMetadata().size(), fromGetBlockData.getMetadata().size());
    }

    @Test
    public void testDeleteBlock() throws Exception {
        Assert.assertEquals(0, keyValueContainer.getContainerData().getKeyCount());
        // Put Block
        blockManager.putBlock(keyValueContainer, blockData);
        Assert.assertEquals(1, keyValueContainer.getContainerData().getKeyCount());
        // Delete Block
        blockManager.deleteBlock(keyValueContainer, blockID);
        Assert.assertEquals(0, keyValueContainer.getContainerData().getKeyCount());
        try {
            blockManager.getBlock(keyValueContainer, blockID);
            Assert.fail("testDeleteBlock");
        } catch (StorageContainerException ex) {
            GenericTestUtils.assertExceptionContains("Unable to find the block", ex);
        }
    }

    @Test
    public void testListBlock() throws Exception {
        blockManager.putBlock(keyValueContainer, blockData);
        List<BlockData> listBlockData = blockManager.listBlock(keyValueContainer, 1, 10);
        Assert.assertNotNull(listBlockData);
        Assert.assertTrue(((listBlockData.size()) == 1));
        for (long i = 2; i <= 10; i++) {
            blockID = new BlockID(1L, i);
            blockData = new BlockData(blockID);
            blockData.addMetadata("VOLUME", "ozone");
            blockData.addMetadata("OWNER", "hdfs");
            List<ContainerProtos.ChunkInfo> chunkList = new ArrayList<>();
            ChunkInfo info = new ChunkInfo(String.format("%d.data.%d", blockID.getLocalID(), 0), 0, 1024);
            chunkList.add(info.getProtoBufMessage());
            blockData.setChunks(chunkList);
            blockManager.putBlock(keyValueContainer, blockData);
        }
        listBlockData = blockManager.listBlock(keyValueContainer, 1, 10);
        Assert.assertNotNull(listBlockData);
        Assert.assertTrue(((listBlockData.size()) == 10));
    }

    @Test
    public void testGetNoSuchBlock() throws Exception {
        Assert.assertEquals(0, keyValueContainer.getContainerData().getKeyCount());
        // Put Block
        blockManager.putBlock(keyValueContainer, blockData);
        Assert.assertEquals(1, keyValueContainer.getContainerData().getKeyCount());
        // Delete Block
        blockManager.deleteBlock(keyValueContainer, blockID);
        Assert.assertEquals(0, keyValueContainer.getContainerData().getKeyCount());
        try {
            // Since the block has been deleted, we should not be able to find it
            blockManager.getBlock(keyValueContainer, blockID);
            Assert.fail("testGetNoSuchBlock failed");
        } catch (StorageContainerException ex) {
            GenericTestUtils.assertExceptionContains("Unable to find the block", ex);
            Assert.assertEquals(NO_SUCH_BLOCK, ex.getResult());
        }
    }
}

