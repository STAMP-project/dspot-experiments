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


import DFSConfigKeys.DFS_DATANODE_DU_RESERVED_KEY;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeDescriptor;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeManager;
import org.apache.hadoop.hdfs.server.datanode.FsDatasetTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This tests InterDataNodeProtocol for block handling.
 */
public class TestNamenodeCapacityReport {
    private static final Logger LOG = LoggerFactory.getLogger(TestNamenodeCapacityReport.class);

    /**
     * The following test first creates a file.
     * It verifies the block information from a datanode.
     * Then, it updates the block with new information and verifies again.
     */
    @Test
    public void testVolumeSize() throws Exception {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        // Set aside fifth of the total capacity as reserved
        long reserved = 10000;
        conf.setLong(DFS_DATANODE_DU_RESERVED_KEY, reserved);
        try {
            cluster = new MiniDFSCluster.Builder(conf).build();
            cluster.waitActive();
            final FSNamesystem namesystem = cluster.getNamesystem();
            final DatanodeManager dm = cluster.getNamesystem().getBlockManager().getDatanodeManager();
            // Ensure the data reported for each data node is right
            final List<DatanodeDescriptor> live = new ArrayList<DatanodeDescriptor>();
            final List<DatanodeDescriptor> dead = new ArrayList<DatanodeDescriptor>();
            dm.fetchDatanodes(live, dead, false);
            Assert.assertTrue(((live.size()) == 1));
            long used;
            long remaining;
            long configCapacity;
            long nonDFSUsed;
            long bpUsed;
            float percentUsed;
            float percentRemaining;
            float percentBpUsed;
            for (final DatanodeDescriptor datanode : live) {
                used = datanode.getDfsUsed();
                remaining = datanode.getRemaining();
                nonDFSUsed = datanode.getNonDfsUsed();
                configCapacity = datanode.getCapacity();
                percentUsed = datanode.getDfsUsedPercent();
                percentRemaining = datanode.getRemainingPercent();
                bpUsed = datanode.getBlockPoolUsed();
                percentBpUsed = datanode.getBlockPoolUsedPercent();
                TestNamenodeCapacityReport.LOG.info(((((((((((("Datanode configCapacity " + configCapacity) + " used ") + used) + " non DFS used ") + nonDFSUsed) + " remaining ") + remaining) + " perentUsed ") + percentUsed) + " percentRemaining ") + percentRemaining));
                // There will be 5% space reserved in ext filesystem which is not
                // considered.
                Assert.assertTrue((configCapacity >= ((used + remaining) + nonDFSUsed)));
                Assert.assertTrue((percentUsed == (DFSUtilClient.getPercentUsed(used, configCapacity))));
                Assert.assertTrue((percentRemaining == (DFSUtilClient.getPercentRemaining(remaining, configCapacity))));
                Assert.assertTrue((percentBpUsed == (DFSUtilClient.getPercentUsed(bpUsed, configCapacity))));
            }
            // 
            // Currently two data directories are created by the data node
            // in the MiniDFSCluster. This results in each data directory having
            // capacity equals to the disk capacity of the data directory.
            // Hence the capacity reported by the data node is twice the disk space
            // the disk capacity
            // 
            // So multiply the disk capacity and reserved space by two
            // for accommodating it
            // 
            final FsDatasetTestUtils utils = cluster.getFsDatasetTestUtils(0);
            int numOfDataDirs = utils.getDefaultNumOfDataDirs();
            long diskCapacity = numOfDataDirs * (utils.getRawCapacity());
            reserved *= numOfDataDirs;
            configCapacity = namesystem.getCapacityTotal();
            used = namesystem.getCapacityUsed();
            nonDFSUsed = namesystem.getNonDfsUsedSpace();
            remaining = namesystem.getCapacityRemaining();
            percentUsed = namesystem.getPercentUsed();
            percentRemaining = namesystem.getPercentRemaining();
            bpUsed = namesystem.getBlockPoolUsedSpace();
            percentBpUsed = namesystem.getPercentBlockPoolUsed();
            TestNamenodeCapacityReport.LOG.info(("Data node directory " + (cluster.getDataDirectory())));
            TestNamenodeCapacityReport.LOG.info(((((((((((((((((((((("Name node diskCapacity " + diskCapacity) + " configCapacity ") + configCapacity) + " reserved ") + reserved) + " used ") + used) + " remaining ") + remaining) + " nonDFSUsed ") + nonDFSUsed) + " remaining ") + remaining) + " percentUsed ") + percentUsed) + " percentRemaining ") + percentRemaining) + " bpUsed ") + bpUsed) + " percentBpUsed ") + percentBpUsed));
            // Ensure new total capacity reported excludes the reserved space
            Assert.assertTrue((configCapacity == (diskCapacity - reserved)));
            // Ensure new total capacity reported excludes the reserved space
            // There will be 5% space reserved in ext filesystem which is not
            // considered.
            Assert.assertTrue((configCapacity >= ((used + remaining) + nonDFSUsed)));
            // Ensure percent used is calculated based on used and present capacity
            Assert.assertTrue((percentUsed == (DFSUtilClient.getPercentUsed(used, configCapacity))));
            // Ensure percent used is calculated based on used and present capacity
            Assert.assertTrue((percentBpUsed == (DFSUtilClient.getPercentUsed(bpUsed, configCapacity))));
            // Ensure percent used is calculated based on used and present capacity
            Assert.assertTrue((percentRemaining == ((((float) (remaining)) * 100.0F) / ((float) (configCapacity)))));
            // Adding testcase for non-dfs used where we need to consider
            // reserved replica also.
            final int fileCount = 5;
            final DistributedFileSystem fs = cluster.getFileSystem();
            // create streams and hsync to force datastreamers to start
            DFSOutputStream[] streams = new DFSOutputStream[fileCount];
            for (int i = 0; i < fileCount; i++) {
                streams[i] = ((DFSOutputStream) (fs.create(new Path(("/f" + i))).getWrappedStream()));
                streams[i].write("1".getBytes());
                streams[i].hsync();
            }
            triggerHeartbeats(cluster.getDataNodes());
            Assert.assertTrue((configCapacity > (((namesystem.getCapacityUsed()) + (namesystem.getCapacityRemaining())) + (namesystem.getNonDfsUsedSpace()))));
            // There is a chance that nonDFS usage might have slightly due to
            // testlogs, So assume 1MB other files used within this gap
            Assert.assertTrue(((((((namesystem.getCapacityUsed()) + (namesystem.getCapacityRemaining())) + (namesystem.getNonDfsUsedSpace())) + (fileCount * (fs.getDefaultBlockSize()))) - configCapacity) < (1 * 1024)));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    private static final float EPSILON = 1.0E-4F;

    @Test
    public void testXceiverCount() throws Exception {
        testXceiverCountInternal(0);
        testXceiverCountInternal(1);
    }
}

