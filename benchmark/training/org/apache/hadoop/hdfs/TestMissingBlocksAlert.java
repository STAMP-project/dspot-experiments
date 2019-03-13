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
package org.apache.hadoop.hdfs;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY;
import HdfsClientConfigKeys.Retry.WINDOW_BASE_KEY;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ChecksumException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The test makes sure that NameNode detects presense blocks that do not have
 * any valid replicas. In addition, it verifies that HDFS front page displays
 * a warning in such a case.
 */
public class TestMissingBlocksAlert {
    private static final Logger LOG = LoggerFactory.getLogger(TestMissingBlocksAlert.class);

    @Test
    public void testMissingBlocksAlert() throws IOException, InterruptedException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, MalformedObjectNameException, ReflectionException {
        MiniDFSCluster cluster = null;
        try {
            Configuration conf = new HdfsConfiguration();
            // minimize test delay
            conf.setInt(DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY, 0);
            conf.setInt(WINDOW_BASE_KEY, 10);
            int fileLen = 10 * 1024;
            conf.setInt(DFS_BLOCK_SIZE_KEY, (fileLen / 2));
            // start a cluster with single datanode
            cluster = new MiniDFSCluster.Builder(conf).build();
            cluster.waitActive();
            final BlockManager bm = cluster.getNamesystem().getBlockManager();
            DistributedFileSystem dfs = cluster.getFileSystem();
            // create a normal file
            DFSTestUtil.createFile(dfs, new Path("/testMissingBlocksAlert/file1"), fileLen, ((short) (3)), 0);
            Path corruptFile = new Path("/testMissingBlocks/corruptFile");
            DFSTestUtil.createFile(dfs, corruptFile, fileLen, ((short) (3)), 0);
            // Corrupt the block
            ExtendedBlock block = DFSTestUtil.getFirstBlock(dfs, corruptFile);
            cluster.corruptReplica(0, block);
            // read the file so that the corrupt block is reported to NN
            FSDataInputStream in = dfs.open(corruptFile);
            try {
                in.readFully(new byte[fileLen]);
            } catch (ChecksumException ignored) {
                // checksum error is expected.
            }
            in.close();
            TestMissingBlocksAlert.LOG.info("Waiting for missing blocks count to increase...");
            while ((dfs.getMissingBlocksCount()) <= 0) {
                Thread.sleep(100);
            } 
            Assert.assertTrue(((dfs.getMissingBlocksCount()) == 1));
            Assert.assertEquals(4, dfs.getLowRedundancyBlocksCount());
            Assert.assertEquals(3, bm.getUnderReplicatedNotMissingBlocks());
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mxbeanName = new ObjectName("Hadoop:service=NameNode,name=NameNodeInfo");
            Assert.assertEquals(1, ((long) ((Long) (mbs.getAttribute(mxbeanName, "NumberOfMissingBlocks")))));
            // now do the reverse : remove the file expect the number of missing
            // blocks to go to zero
            dfs.delete(corruptFile, true);
            TestMissingBlocksAlert.LOG.info("Waiting for missing blocks count to be zero...");
            while ((dfs.getMissingBlocksCount()) > 0) {
                Thread.sleep(100);
            } 
            Assert.assertEquals(2, dfs.getLowRedundancyBlocksCount());
            Assert.assertEquals(2, bm.getUnderReplicatedNotMissingBlocks());
            Assert.assertEquals(0, ((long) ((Long) (mbs.getAttribute(mxbeanName, "NumberOfMissingBlocks")))));
            Path replOneFile = new Path("/testMissingBlocks/replOneFile");
            DFSTestUtil.createFile(dfs, replOneFile, fileLen, ((short) (1)), 0);
            ExtendedBlock replOneBlock = DFSTestUtil.getFirstBlock(dfs, replOneFile);
            cluster.corruptReplica(0, replOneBlock);
            // read the file so that the corrupt block is reported to NN
            in = dfs.open(replOneFile);
            try {
                in.readFully(new byte[fileLen]);
            } catch (ChecksumException ignored) {
                // checksum error is expected.
            }
            in.close();
            Assert.assertEquals(1, dfs.getMissingReplOneBlocksCount());
            Assert.assertEquals(1, ((long) ((Long) (mbs.getAttribute(mxbeanName, "NumberOfMissingBlocksWithReplicationFactorOne")))));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

