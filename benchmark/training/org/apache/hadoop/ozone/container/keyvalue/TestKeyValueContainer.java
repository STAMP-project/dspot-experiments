/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.ozone.container.keyvalue;


import ContainerProtos.ContainerDataProto.State.CLOSED;
import ContainerProtos.Result.CONTAINER_ALREADY_EXISTS;
import ContainerProtos.Result.DISK_OUT_OF_SPACE;
import ContainerProtos.Result.UNSUPPORTED_REQUEST;
import DiskChecker.DiskOutOfSpaceException;
import StorageUnit.GB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.container.common.helpers.StorageContainerException;
import org.apache.hadoop.ozone.container.common.helpers.BlockData;
import org.apache.hadoop.ozone.container.common.impl.ContainerDataYaml;
import org.apache.hadoop.ozone.container.common.volume.HddsVolume;
import org.apache.hadoop.ozone.container.common.volume.RoundRobinVolumeChoosingPolicy;
import org.apache.hadoop.ozone.container.common.volume.VolumeSet;
import org.apache.hadoop.ozone.container.keyvalue.helpers.BlockUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.utils.MetadataStore;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Class to test KeyValue Container operations.
 */
public class TestKeyValueContainer {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private OzoneConfiguration conf;

    private String scmId = UUID.randomUUID().toString();

    private VolumeSet volumeSet;

    private RoundRobinVolumeChoosingPolicy volumeChoosingPolicy;

    private KeyValueContainerData keyValueContainerData;

    private KeyValueContainer keyValueContainer;

    private UUID datanodeId;

    @Test
    public void testBlockIterator() throws Exception {
        keyValueContainerData = new KeyValueContainerData(100L, ((long) (GB.toBytes(1))), UUID.randomUUID().toString(), datanodeId.toString());
        keyValueContainer = new KeyValueContainer(keyValueContainerData, conf);
        keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
        KeyValueBlockIterator blockIterator = keyValueContainer.blockIterator();
        // As no blocks created, hasNext should return false.
        Assert.assertFalse(blockIterator.hasNext());
        int blockCount = 10;
        addBlocks(blockCount);
        blockIterator = keyValueContainer.blockIterator();
        assertTrue(blockIterator.hasNext());
        BlockData blockData;
        int blockCounter = 0;
        while (blockIterator.hasNext()) {
            blockData = blockIterator.nextBlock();
            Assert.assertEquals((blockCounter++), blockData.getBlockID().getLocalID());
        } 
        Assert.assertEquals(blockCount, blockCounter);
    }

    @SuppressWarnings("RedundantCast")
    @Test
    public void testCreateContainer() throws Exception {
        // Create Container.
        keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
        keyValueContainerData = keyValueContainer.getContainerData();
        String containerMetaDataPath = keyValueContainerData.getMetadataPath();
        String chunksPath = keyValueContainerData.getChunksPath();
        // Check whether containerMetaDataPath and chunksPath exists or not.
        assertTrue((containerMetaDataPath != null));
        assertTrue((chunksPath != null));
        // Check whether container file and container db file exists or not.
        assertTrue(keyValueContainer.getContainerFile().exists(), ".Container File does not exist");
        assertTrue(keyValueContainer.getContainerDBFile().exists(), ("Container " + "DB does not exist"));
    }

    @Test
    public void testContainerImportExport() throws Exception {
        long containerId = keyValueContainer.getContainerData().getContainerID();
        // Create Container.
        keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
        keyValueContainerData = keyValueContainer.getContainerData();
        keyValueContainerData.setState(CLOSED);
        int numberOfKeysToWrite = 12;
        // write one few keys to check the key count after import
        MetadataStore metadataStore = BlockUtils.getDB(keyValueContainerData, conf);
        for (int i = 0; i < numberOfKeysToWrite; i++) {
            metadataStore.put(("test" + i).getBytes(StandardCharsets.UTF_8), "test".getBytes(StandardCharsets.UTF_8));
        }
        metadataStore.close();
        Map<String, String> metadata = new HashMap<>();
        metadata.put("key1", "value1");
        keyValueContainer.update(metadata, true);
        // destination path
        File folderToExport = folder.newFile("exported.tar.gz");
        TarContainerPacker packer = new TarContainerPacker();
        // export the container
        try (FileOutputStream fos = new FileOutputStream(folderToExport)) {
            keyValueContainer.exportContainerData(fos, packer);
        }
        // delete the original one
        keyValueContainer.delete();
        // create a new one
        KeyValueContainerData containerData = new KeyValueContainerData(containerId, 1, keyValueContainerData.getMaxSize(), UUID.randomUUID().toString(), datanodeId.toString());
        KeyValueContainer container = new KeyValueContainer(containerData, conf);
        HddsVolume containerVolume = volumeChoosingPolicy.chooseVolume(volumeSet.getVolumesList(), 1);
        String hddsVolumeDir = containerVolume.getHddsRootDir().toString();
        container.populatePathFields(scmId, containerVolume, hddsVolumeDir);
        try (FileInputStream fis = new FileInputStream(folderToExport)) {
            container.importContainerData(fis, packer);
        }
        Assert.assertEquals("value1", containerData.getMetadata().get("key1"));
        Assert.assertEquals(keyValueContainerData.getContainerDBType(), containerData.getContainerDBType());
        Assert.assertEquals(keyValueContainerData.getState(), containerData.getState());
        Assert.assertEquals(numberOfKeysToWrite, containerData.getKeyCount());
        Assert.assertEquals(keyValueContainerData.getLayOutVersion(), containerData.getLayOutVersion());
        Assert.assertEquals(keyValueContainerData.getMaxSize(), containerData.getMaxSize());
        Assert.assertEquals(keyValueContainerData.getBytesUsed(), containerData.getBytesUsed());
        // Can't overwrite existing container
        try {
            try (FileInputStream fis = new FileInputStream(folderToExport)) {
                container.importContainerData(fis, packer);
            }
            Assert.fail("Container is imported twice. Previous files are overwritten");
        } catch (IOException ex) {
            // all good
        }
    }

    @Test
    public void testDuplicateContainer() throws Exception {
        try {
            // Create Container.
            keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
            keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
            Assert.fail("testDuplicateContainer failed");
        } catch (StorageContainerException ex) {
            GenericTestUtils.assertExceptionContains(("ContainerFile already " + "exists"), ex);
            Assert.assertEquals(CONTAINER_ALREADY_EXISTS, ex.getResult());
        }
    }

    @Test
    public void testDiskFullExceptionCreateContainer() throws Exception {
        Mockito.when(volumeChoosingPolicy.chooseVolume(ArgumentMatchers.anyList(), ArgumentMatchers.anyLong())).thenThrow(DiskOutOfSpaceException.class);
        try {
            keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
            Assert.fail("testDiskFullExceptionCreateContainer failed");
        } catch (StorageContainerException ex) {
            GenericTestUtils.assertExceptionContains("disk out of space", ex);
            Assert.assertEquals(DISK_OUT_OF_SPACE, ex.getResult());
        }
    }

    @Test
    public void testDeleteContainer() throws Exception {
        keyValueContainerData.setState(CLOSED);
        keyValueContainer = new KeyValueContainer(keyValueContainerData, conf);
        keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
        keyValueContainer.delete();
        String containerMetaDataPath = keyValueContainerData.getMetadataPath();
        File containerMetaDataLoc = new File(containerMetaDataPath);
        Assert.assertFalse("Container directory still exists", containerMetaDataLoc.getParentFile().exists());
        Assert.assertFalse("Container File still exists", keyValueContainer.getContainerFile().exists());
        Assert.assertFalse("Container DB file still exists", keyValueContainer.getContainerDBFile().exists());
    }

    @Test
    public void testCloseContainer() throws Exception {
        keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
        keyValueContainer.close();
        keyValueContainerData = keyValueContainer.getContainerData();
        Assert.assertEquals(CLOSED, keyValueContainerData.getState());
        // Check state in the .container file
        String containerMetaDataPath = keyValueContainerData.getMetadataPath();
        File containerFile = keyValueContainer.getContainerFile();
        keyValueContainerData = ((KeyValueContainerData) (ContainerDataYaml.readContainerFile(containerFile)));
        Assert.assertEquals(CLOSED, keyValueContainerData.getState());
    }

    @Test
    public void testUpdateContainer() throws IOException {
        keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("VOLUME", "ozone");
        metadata.put("OWNER", "hdfs");
        keyValueContainer.update(metadata, true);
        keyValueContainerData = keyValueContainer.getContainerData();
        Assert.assertEquals(2, keyValueContainerData.getMetadata().size());
        // Check metadata in the .container file
        File containerFile = keyValueContainer.getContainerFile();
        keyValueContainerData = ((KeyValueContainerData) (ContainerDataYaml.readContainerFile(containerFile)));
        Assert.assertEquals(2, keyValueContainerData.getMetadata().size());
    }

    @Test
    public void testUpdateContainerUnsupportedRequest() throws Exception {
        try {
            keyValueContainerData.setState(CLOSED);
            keyValueContainer = new KeyValueContainer(keyValueContainerData, conf);
            keyValueContainer.create(volumeSet, volumeChoosingPolicy, scmId);
            Map<String, String> metadata = new HashMap<>();
            metadata.put("VOLUME", "ozone");
            keyValueContainer.update(metadata, false);
            Assert.fail("testUpdateContainerUnsupportedRequest failed");
        } catch (StorageContainerException ex) {
            GenericTestUtils.assertExceptionContains(("Updating a closed container " + "without force option is not allowed"), ex);
            Assert.assertEquals(UNSUPPORTED_REQUEST, ex.getResult());
        }
    }
}

