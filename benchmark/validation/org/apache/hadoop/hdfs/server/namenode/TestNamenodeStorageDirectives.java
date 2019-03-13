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
package org.apache.hadoop.hdfs.server.namenode;


import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.ReconfigurationException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.DatanodeStorageInfo;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.RoundRobinVolumeChoosingPolicy;
import org.apache.hadoop.net.Node;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test to ensure that the StorageType and StorageID sent from Namenode
 * to DFSClient are respected.
 */
public class TestNamenodeStorageDirectives {
    public static final Logger LOG = LoggerFactory.getLogger(TestNamenodeStorageDirectives.class);

    private static final int BLOCK_SIZE = 512;

    private MiniDFSCluster cluster;

    /**
     * Verify that writing to SSD and DISK will write to the correct Storage
     * Types.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 60000)
    public void testTargetStorageTypes() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        // DISK and not anything else.
        testStorageTypes(new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK } }, "ONE_SSD", new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.RAM_DISK, StorageType.ARCHIVE });
        // only on SSD.
        testStorageTypes(new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK } }, "ALL_SSD", new StorageType[]{ StorageType.SSD }, new StorageType[]{ StorageType.RAM_DISK, StorageType.DISK, StorageType.ARCHIVE });
        // only on SSD.
        testStorageTypes(new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK, StorageType.DISK } }, "ALL_SSD", new StorageType[]{ StorageType.SSD }, new StorageType[]{ StorageType.RAM_DISK, StorageType.DISK, StorageType.ARCHIVE });
        // DISK and not anything else.
        testStorageTypes(new StorageType[][]{ new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK } }, "HOT", new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD, StorageType.ARCHIVE });
        testStorageTypes(new StorageType[][]{ new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE } }, "WARM", new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD });
        testStorageTypes(new StorageType[][]{ new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE } }, "COLD", new StorageType[]{ StorageType.ARCHIVE }, new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD, StorageType.DISK });
        // We wait for Lasy Persist to write to disk.
        testStorageTypes(new StorageType[][]{ new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK } }, "LAZY_PERSIST", new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.RAM_DISK, StorageType.SSD, StorageType.ARCHIVE });
    }

    /**
     * A VolumeChoosingPolicy test stub used to verify that the storageId passed
     * in is indeed in the list of volumes.
     *
     * @param <V>
     * 		
     */
    private static class TestVolumeChoosingPolicy<V extends FsVolumeSpi> extends RoundRobinVolumeChoosingPolicy<V> {
        static String expectedStorageId;

        @Override
        public V chooseVolume(List<V> volumes, long replicaSize, String storageId) throws IOException {
            Assert.assertEquals(TestNamenodeStorageDirectives.TestVolumeChoosingPolicy.expectedStorageId, storageId);
            return super.chooseVolume(volumes, replicaSize, storageId);
        }
    }

    private static class TestBlockPlacementPolicy extends BlockPlacementPolicyDefault {
        static DatanodeStorageInfo[] dnStorageInfosToReturn;

        @Override
        public DatanodeStorageInfo[] chooseTarget(String srcPath, int numOfReplicas, Node writer, List<DatanodeStorageInfo> chosenNodes, boolean returnChosenNodes, Set<Node> excludedNodes, long blocksize, final BlockStoragePolicy storagePolicy, EnumSet<AddBlockFlag> flags) {
            return TestNamenodeStorageDirectives.TestBlockPlacementPolicy.dnStorageInfosToReturn;
        }
    }

    @Test(timeout = 60000)
    public void testStorageIDBlockPlacementSpecific() throws IOException, InterruptedException, TimeoutException, ReconfigurationException {
        final StorageType[][] storageTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK } };
        final int numDataNodes = storageTypes.length;
        final int storagePerDataNode = storageTypes[0].length;
        startDFSCluster(1, numDataNodes, storagePerDataNode, storageTypes, TestNamenodeStorageDirectives.TestVolumeChoosingPolicy.class, TestNamenodeStorageDirectives.TestBlockPlacementPolicy.class);
        Path testFile = new Path("/test");
        final short replFactor = 1;
        final int numBlocks = 10;
        DatanodeStorageInfo dnInfoToUse = getDatanodeStorageInfo(0);
        TestNamenodeStorageDirectives.TestBlockPlacementPolicy.dnStorageInfosToReturn = new DatanodeStorageInfo[]{ dnInfoToUse };
        TestNamenodeStorageDirectives.TestVolumeChoosingPolicy.expectedStorageId = dnInfoToUse.getStorageID();
        // file creation invokes both BlockPlacementPolicy and VolumeChoosingPolicy,
        // and will test that the storage ids match
        createFile(testFile, numBlocks, replFactor);
    }
}

