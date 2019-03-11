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
package org.apache.hadoop.ozone.container.common.impl;


import ContainerProtos.ContainerDataProto.State.CLOSED;
import ContainerProtos.ContainerDataProto.State.OPEN;
import ContainerProtos.ContainerType.KeyValueContainer;
import StorageUnit.GB;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.ozone.container.common.helpers.ContainerUtils;
import org.apache.hadoop.ozone.container.keyvalue.KeyValueContainerData;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests create/read .container files.
 */
public class TestContainerDataYaml {
    private static long testContainerID = 1234;

    private static String testRoot = new FileSystemTestHelper().getTestRootDir();

    private static final long MAXSIZE = ((long) (GB.toBytes(5)));

    @Test
    public void testCreateContainerFile() throws IOException {
        long containerID = (TestContainerDataYaml.testContainerID)++;
        File containerFile = createContainerFile(containerID);
        // Read from .container file, and verify data.
        KeyValueContainerData kvData = ((KeyValueContainerData) (ContainerDataYaml.readContainerFile(containerFile)));
        Assert.assertEquals(containerID, kvData.getContainerID());
        Assert.assertEquals(KeyValueContainer, kvData.getContainerType());
        Assert.assertEquals("RocksDB", kvData.getContainerDBType());
        Assert.assertEquals(containerFile.getParent(), kvData.getMetadataPath());
        Assert.assertEquals(containerFile.getParent(), kvData.getChunksPath());
        Assert.assertEquals(OPEN, kvData.getState());
        Assert.assertEquals(1, kvData.getLayOutVersion());
        Assert.assertEquals(0, kvData.getMetadata().size());
        Assert.assertEquals(TestContainerDataYaml.MAXSIZE, kvData.getMaxSize());
        // Update ContainerData.
        kvData.addMetadata("VOLUME", "hdfs");
        kvData.addMetadata("OWNER", "ozone");
        kvData.setState(CLOSED);
        ContainerDataYaml.createContainerFile(KeyValueContainer, kvData, containerFile);
        // Reading newly updated data from .container file
        kvData = ((KeyValueContainerData) (ContainerDataYaml.readContainerFile(containerFile)));
        // verify data.
        Assert.assertEquals(containerID, kvData.getContainerID());
        Assert.assertEquals(KeyValueContainer, kvData.getContainerType());
        Assert.assertEquals("RocksDB", kvData.getContainerDBType());
        Assert.assertEquals(containerFile.getParent(), kvData.getMetadataPath());
        Assert.assertEquals(containerFile.getParent(), kvData.getChunksPath());
        Assert.assertEquals(CLOSED, kvData.getState());
        Assert.assertEquals(1, kvData.getLayOutVersion());
        Assert.assertEquals(2, kvData.getMetadata().size());
        Assert.assertEquals("hdfs", kvData.getMetadata().get("VOLUME"));
        Assert.assertEquals("ozone", kvData.getMetadata().get("OWNER"));
        Assert.assertEquals(TestContainerDataYaml.MAXSIZE, kvData.getMaxSize());
    }

    @Test
    public void testIncorrectContainerFile() throws IOException {
        try {
            String containerFile = "incorrect.container";
            // Get file from resources folder
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(containerFile).getFile());
            KeyValueContainerData kvData = ((KeyValueContainerData) (ContainerDataYaml.readContainerFile(file)));
            Assert.fail("testIncorrectContainerFile failed");
        } catch (IllegalArgumentException ex) {
            GenericTestUtils.assertExceptionContains("No enum constant", ex);
        }
    }

    @Test
    public void testCheckBackWardCompatibilityOfContainerFile() throws IOException {
        // This test is for if we upgrade, and then .container files added by new
        // server will have new fields added to .container file, after a while we
        // decided to rollback. Then older ozone can read .container files
        // created or not.
        try {
            String containerFile = "additionalfields.container";
            // Get file from resources folder
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(containerFile).getFile());
            KeyValueContainerData kvData = ((KeyValueContainerData) (ContainerDataYaml.readContainerFile(file)));
            ContainerUtils.verifyChecksum(kvData);
            // Checking the Container file data is consistent or not
            Assert.assertEquals(CLOSED, kvData.getState());
            Assert.assertEquals("RocksDB", kvData.getContainerDBType());
            Assert.assertEquals(KeyValueContainer, kvData.getContainerType());
            Assert.assertEquals(9223372036854775807L, kvData.getContainerID());
            Assert.assertEquals("/hdds/current/aed-fg4-hji-jkl/containerDir0/1", kvData.getChunksPath());
            Assert.assertEquals("/hdds/current/aed-fg4-hji-jkl/containerDir0/1", kvData.getMetadataPath());
            Assert.assertEquals(1, kvData.getLayOutVersion());
            Assert.assertEquals(2, kvData.getMetadata().size());
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("testCheckBackWardCompatibilityOfContainerFile failed");
        }
    }

    /**
     * Test to verify {@link ContainerUtils#verifyChecksum(ContainerData)}.
     */
    @Test
    public void testChecksumInContainerFile() throws IOException {
        long containerID = (TestContainerDataYaml.testContainerID)++;
        File containerFile = createContainerFile(containerID);
        // Read from .container file, and verify data.
        KeyValueContainerData kvData = ((KeyValueContainerData) (ContainerDataYaml.readContainerFile(containerFile)));
        ContainerUtils.verifyChecksum(kvData);
        cleanup();
    }

    /**
     * Test to verify incorrect checksum is detected.
     */
    @Test
    public void testIncorrectChecksum() {
        try {
            String containerFile = "incorrect.checksum.container";
            // Get file from resources folder
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(containerFile).getFile());
            KeyValueContainerData kvData = ((KeyValueContainerData) (ContainerDataYaml.readContainerFile(file)));
            ContainerUtils.verifyChecksum(kvData);
            Assert.fail("testIncorrectChecksum failed");
        } catch (Exception ex) {
            GenericTestUtils.assertExceptionContains(("Container checksum error for " + "ContainerID:"), ex);
        }
    }
}

