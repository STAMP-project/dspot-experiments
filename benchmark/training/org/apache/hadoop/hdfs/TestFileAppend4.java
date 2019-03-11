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


import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY;
import DataNode.LOG;
import GenericTestUtils.DelayAnswer;
import HdfsClientConfigKeys.DFS_CLIENT_SOCKET_TIMEOUT_KEY;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.namenode.FSDirectory;
import org.apache.hadoop.hdfs.server.namenode.INodeFile;
import org.apache.hadoop.hdfs.server.namenode.LeaseExpiredException;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/* File Append tests for HDFS-200 & HDFS-142, specifically focused on:
 using append()/sync() to recover block information
 */
public class TestFileAppend4 {
    static final Logger LOG = LoggerFactory.getLogger(TestFileAppend4.class);

    static final long BLOCK_SIZE = 1024;

    static final long BBW_SIZE = 500;// don't align on bytes/checksum


    static final Object[] NO_ARGS = new Object[]{  };

    Configuration conf;

    MiniDFSCluster cluster;

    Path file1;

    FSDataOutputStream stm;

    {
        DFSTestUtil.setNameNodeLogLevel(Level.ALL);
        GenericTestUtils.setLogLevel(DataNode.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(DFSClient.LOG, Level.ALL);
    }

    /**
     * Test case that stops a writer after finalizing a block but
     * before calling completeFile, and then tries to recover
     * the lease from another thread.
     */
    @Test(timeout = 60000)
    public void testRecoverFinalizedBlock() throws Throwable {
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(5).build();
        try {
            cluster.waitActive();
            NamenodeProtocols preSpyNN = cluster.getNameNodeRpc();
            NamenodeProtocols spyNN = Mockito.spy(preSpyNN);
            // Delay completeFile
            GenericTestUtils.DelayAnswer delayer = new GenericTestUtils.DelayAnswer(TestFileAppend4.LOG);
            Mockito.doAnswer(delayer).when(spyNN).complete(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyLong());
            DFSClient client = new DFSClient(null, spyNN, conf, null);
            file1 = new Path("/testRecoverFinalized");
            final OutputStream stm = client.create("/testRecoverFinalized", true);
            // write 1/2 block
            AppendTestUtil.write(stm, 0, 4096);
            final AtomicReference<Throwable> err = new AtomicReference<Throwable>();
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        stm.close();
                    } catch (Throwable t) {
                        err.set(t);
                    }
                }
            };
            t.start();
            TestFileAppend4.LOG.info("Waiting for close to get to latch...");
            delayer.waitForCall();
            // At this point, the block is finalized on the DNs, but the file
            // has not been completed in the NN.
            // Lose the leases
            TestFileAppend4.LOG.info("Killing lease checker");
            client.getLeaseRenewer().interruptAndJoin();
            FileSystem fs1 = cluster.getFileSystem();
            FileSystem fs2 = AppendTestUtil.createHdfsWithDifferentUsername(fs1.getConf());
            TestFileAppend4.LOG.info("Recovering file");
            recoverFile(fs2);
            TestFileAppend4.LOG.info("Telling close to proceed.");
            delayer.proceed();
            TestFileAppend4.LOG.info("Waiting for close to finish.");
            t.join();
            TestFileAppend4.LOG.info("Close finished.");
            // We expect that close will get a "File is not open" error.
            Throwable thrownByClose = err.get();
            Assert.assertNotNull(thrownByClose);
            Assert.assertTrue((thrownByClose instanceof LeaseExpiredException));
            GenericTestUtils.assertExceptionContains("File is not open for writing", thrownByClose);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test case that stops a writer after finalizing a block but
     * before calling completeFile, recovers a file from another writer,
     * starts writing from that writer, and then has the old lease holder
     * call completeFile
     */
    @Test(timeout = 60000)
    public void testCompleteOtherLeaseHoldersFile() throws Throwable {
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(5).build();
        try {
            cluster.waitActive();
            NamenodeProtocols preSpyNN = cluster.getNameNodeRpc();
            NamenodeProtocols spyNN = Mockito.spy(preSpyNN);
            // Delay completeFile
            GenericTestUtils.DelayAnswer delayer = new GenericTestUtils.DelayAnswer(TestFileAppend4.LOG);
            Mockito.doAnswer(delayer).when(spyNN).complete(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyLong());
            DFSClient client = new DFSClient(null, spyNN, conf, null);
            file1 = new Path("/testCompleteOtherLease");
            final OutputStream stm = client.create("/testCompleteOtherLease", true);
            // write 1/2 block
            AppendTestUtil.write(stm, 0, 4096);
            final AtomicReference<Throwable> err = new AtomicReference<Throwable>();
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        stm.close();
                    } catch (Throwable t) {
                        err.set(t);
                    }
                }
            };
            t.start();
            TestFileAppend4.LOG.info("Waiting for close to get to latch...");
            delayer.waitForCall();
            // At this point, the block is finalized on the DNs, but the file
            // has not been completed in the NN.
            // Lose the leases
            TestFileAppend4.LOG.info("Killing lease checker");
            client.getLeaseRenewer().interruptAndJoin();
            FileSystem fs1 = cluster.getFileSystem();
            FileSystem fs2 = AppendTestUtil.createHdfsWithDifferentUsername(fs1.getConf());
            TestFileAppend4.LOG.info("Recovering file");
            recoverFile(fs2);
            TestFileAppend4.LOG.info("Opening file for append from new fs");
            FSDataOutputStream appenderStream = fs2.append(file1);
            TestFileAppend4.LOG.info("Writing some data from new appender");
            AppendTestUtil.write(appenderStream, 0, 4096);
            TestFileAppend4.LOG.info("Telling old close to proceed.");
            delayer.proceed();
            TestFileAppend4.LOG.info("Waiting for close to finish.");
            t.join();
            TestFileAppend4.LOG.info("Close finished.");
            // We expect that close will get a "Lease mismatch"
            // error.
            Throwable thrownByClose = err.get();
            Assert.assertNotNull(thrownByClose);
            Assert.assertTrue((thrownByClose instanceof LeaseExpiredException));
            GenericTestUtils.assertExceptionContains("not the lease owner", thrownByClose);
            // The appender should be able to close properly
            appenderStream.close();
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test the updation of NeededReplications for the Appended Block
     */
    @Test(timeout = 60000)
    public void testUpdateNeededReplicationsForAppendedFile() throws Exception {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
        DistributedFileSystem fileSystem = null;
        try {
            // create a file.
            fileSystem = cluster.getFileSystem();
            Path f = new Path("/testAppend");
            FSDataOutputStream create = fileSystem.create(f, ((short) (2)));
            create.write("/testAppend".getBytes());
            create.close();
            // Append to the file.
            FSDataOutputStream append = fileSystem.append(f);
            append.write("/testAppend".getBytes());
            append.close();
            // Start a new datanode
            cluster.startDataNodes(conf, 1, true, null, null);
            // Check for replications
            DFSTestUtil.waitReplication(fileSystem, f, ((short) (2)));
        } finally {
            if (null != fileSystem) {
                fileSystem.close();
            }
            cluster.shutdown();
        }
    }

    /**
     * Test that an append with no locations fails with an exception
     * showing insufficient locations.
     */
    @Test(timeout = 60000)
    public void testAppendInsufficientLocations() throws Exception {
        Configuration conf = new Configuration();
        // lower heartbeat interval for fast recognition of DN
        conf.setInt(DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, 1000);
        conf.setInt(DFS_HEARTBEAT_INTERVAL_KEY, 1);
        conf.setInt(DFS_CLIENT_SOCKET_TIMEOUT_KEY, 3000);
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(4).build();
        DistributedFileSystem fileSystem = null;
        try {
            // create a file with replication 3
            fileSystem = cluster.getFileSystem();
            Path f = new Path("/testAppend");
            FSDataOutputStream create = fileSystem.create(f, ((short) (2)));
            create.write("/testAppend".getBytes());
            create.close();
            // Check for replications
            DFSTestUtil.waitReplication(fileSystem, f, ((short) (2)));
            // Shut down all DNs that have the last block location for the file
            LocatedBlocks lbs = fileSystem.dfs.getNamenode().getBlockLocations("/testAppend", 0, Long.MAX_VALUE);
            List<DataNode> dnsOfCluster = cluster.getDataNodes();
            DatanodeInfo[] dnsWithLocations = lbs.getLastLocatedBlock().getLocations();
            for (DataNode dn : dnsOfCluster) {
                for (DatanodeInfo loc : dnsWithLocations) {
                    if (dn.getDatanodeId().equals(loc)) {
                        dn.shutdown();
                        DFSTestUtil.waitForDatanodeDeath(dn);
                    }
                }
            }
            // Wait till 0 replication is recognized
            DFSTestUtil.waitReplication(fileSystem, f, ((short) (0)));
            // Append to the file, at this state there are 3 live DNs but none of them
            // have the block.
            try {
                fileSystem.append(f);
                Assert.fail("Append should fail because insufficient locations");
            } catch (IOException e) {
                TestFileAppend4.LOG.info("Expected exception: ", e);
            }
            FSDirectory dir = cluster.getNamesystem().getFSDirectory();
            final INodeFile inode = INodeFile.valueOf(dir.getINode("/testAppend"), "/testAppend");
            Assert.assertTrue("File should remain closed", (!(inode.isUnderConstruction())));
        } finally {
            if (null != fileSystem) {
                fileSystem.close();
            }
            cluster.shutdown();
        }
    }
}

