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


import java.io.File;
import java.util.NoSuchElementException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ozone.container.common.helpers.BlockData;
import org.apache.hadoop.ozone.container.common.volume.VolumeSet;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.utils.MetadataKeyFilters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This class is used to test KeyValue container block iterator.
 */
@RunWith(Parameterized.class)
public class TestKeyValueBlockIterator {
    private KeyValueContainer container;

    private KeyValueContainerData containerData;

    private VolumeSet volumeSet;

    private Configuration conf;

    private File testRoot;

    private final String storeImpl;

    public TestKeyValueBlockIterator(String metadataImpl) {
        this.storeImpl = metadataImpl;
    }

    @Test
    public void testKeyValueBlockIteratorWithMixedBlocks() throws Exception {
        long containerID = 100L;
        int deletedBlocks = 5;
        int normalBlocks = 5;
        createContainerWithBlocks(containerID, normalBlocks, deletedBlocks);
        String containerPath = new File(containerData.getMetadataPath()).getParent();
        KeyValueBlockIterator keyValueBlockIterator = new KeyValueBlockIterator(containerID, new File(containerPath));
        int counter = 0;
        while (keyValueBlockIterator.hasNext()) {
            BlockData blockData = keyValueBlockIterator.nextBlock();
            Assert.assertEquals(blockData.getLocalID(), (counter++));
        } 
        Assert.assertFalse(keyValueBlockIterator.hasNext());
        keyValueBlockIterator.seekToFirst();
        counter = 0;
        while (keyValueBlockIterator.hasNext()) {
            BlockData blockData = keyValueBlockIterator.nextBlock();
            Assert.assertEquals(blockData.getLocalID(), (counter++));
        } 
        Assert.assertFalse(keyValueBlockIterator.hasNext());
        try {
            keyValueBlockIterator.nextBlock();
        } catch (NoSuchElementException ex) {
            GenericTestUtils.assertExceptionContains((("Block Iterator reached end " + "for ContainerID ") + containerID), ex);
        }
    }

    @Test
    public void testKeyValueBlockIteratorWithNextBlock() throws Exception {
        long containerID = 101L;
        createContainerWithBlocks(containerID, 2, 0);
        String containerPath = new File(containerData.getMetadataPath()).getParent();
        KeyValueBlockIterator keyValueBlockIterator = new KeyValueBlockIterator(containerID, new File(containerPath));
        long blockID = 0L;
        Assert.assertEquals((blockID++), keyValueBlockIterator.nextBlock().getLocalID());
        Assert.assertEquals(blockID, keyValueBlockIterator.nextBlock().getLocalID());
        try {
            keyValueBlockIterator.nextBlock();
        } catch (NoSuchElementException ex) {
            GenericTestUtils.assertExceptionContains((("Block Iterator reached end " + "for ContainerID ") + containerID), ex);
        }
    }

    @Test
    public void testKeyValueBlockIteratorWithHasNext() throws Exception {
        long containerID = 102L;
        createContainerWithBlocks(containerID, 2, 0);
        String containerPath = new File(containerData.getMetadataPath()).getParent();
        KeyValueBlockIterator keyValueBlockIterator = new KeyValueBlockIterator(containerID, new File(containerPath));
        long blockID = 0L;
        // Even calling multiple times hasNext() should not move entry forward.
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertEquals((blockID++), keyValueBlockIterator.nextBlock().getLocalID());
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertEquals(blockID, keyValueBlockIterator.nextBlock().getLocalID());
        keyValueBlockIterator.seekToLast();
        Assert.assertTrue(keyValueBlockIterator.hasNext());
        Assert.assertEquals(blockID, keyValueBlockIterator.nextBlock().getLocalID());
        keyValueBlockIterator.seekToFirst();
        blockID = 0L;
        Assert.assertEquals((blockID++), keyValueBlockIterator.nextBlock().getLocalID());
        Assert.assertEquals(blockID, keyValueBlockIterator.nextBlock().getLocalID());
        try {
            keyValueBlockIterator.nextBlock();
        } catch (NoSuchElementException ex) {
            GenericTestUtils.assertExceptionContains((("Block Iterator reached end " + "for ContainerID ") + containerID), ex);
        }
    }

    @Test
    public void testKeyValueBlockIteratorWithFilter() throws Exception {
        long containerId = 103L;
        int deletedBlocks = 5;
        int normalBlocks = 5;
        createContainerWithBlocks(containerId, normalBlocks, deletedBlocks);
        String containerPath = new File(containerData.getMetadataPath()).getParent();
        KeyValueBlockIterator keyValueBlockIterator = new KeyValueBlockIterator(containerId, new File(containerPath), MetadataKeyFilters.getDeletingKeyFilter());
        int counter = 5;
        while (keyValueBlockIterator.hasNext()) {
            BlockData blockData = keyValueBlockIterator.nextBlock();
            Assert.assertEquals(blockData.getLocalID(), (counter++));
        } 
    }

    @Test
    public void testKeyValueBlockIteratorWithOnlyDeletedBlocks() throws Exception {
        long containerId = 104L;
        createContainerWithBlocks(containerId, 0, 5);
        String containerPath = new File(containerData.getMetadataPath()).getParent();
        KeyValueBlockIterator keyValueBlockIterator = new KeyValueBlockIterator(containerId, new File(containerPath));
        // As all blocks are deleted blocks, blocks does not match with normal key
        // filter.
        Assert.assertFalse(keyValueBlockIterator.hasNext());
    }
}

