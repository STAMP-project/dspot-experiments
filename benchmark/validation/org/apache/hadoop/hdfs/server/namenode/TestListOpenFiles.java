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


import DFSConfigKeys.DFS_NAMENODE_LIST_OPENFILES_NUM_RESPONSES;
import OpenFilesIterator.FILTER_PATH_DEFAULT;
import OpenFilesType.ALL_OPEN_FILES;
import OpenFilesType.BLOCKING_DECOMMISSION;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.fs.BatchedRemoteIterator.BatchedEntries;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HAUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.OpenFileEntry;
import org.apache.hadoop.hdfs.server.namenode.ha.HATestUtil;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify open files listing.
 */
public class TestListOpenFiles {
    private static final int NUM_DATA_NODES = 3;

    private static final int BATCH_SIZE = 5;

    private static MiniDFSCluster cluster = null;

    private static DistributedFileSystem fs = null;

    private static NamenodeProtocols nnRpc = null;

    private static final Logger LOG = LoggerFactory.getLogger(TestListOpenFiles.class);

    @Test(timeout = 120000L)
    public void testListOpenFilesViaNameNodeRPC() throws Exception {
        HashMap<Path, FSDataOutputStream> openFiles = new HashMap<>();
        createFiles(TestListOpenFiles.fs, "closed", 10);
        verifyOpenFiles(openFiles);
        BatchedEntries<OpenFileEntry> openFileEntryBatchedEntries = TestListOpenFiles.nnRpc.listOpenFiles(0, EnumSet.of(ALL_OPEN_FILES), FILTER_PATH_DEFAULT);
        Assert.assertTrue("Open files list should be empty!", ((openFileEntryBatchedEntries.size()) == 0));
        BatchedEntries<OpenFileEntry> openFilesBlockingDecomEntries = TestListOpenFiles.nnRpc.listOpenFiles(0, EnumSet.of(BLOCKING_DECOMMISSION), FILTER_PATH_DEFAULT);
        Assert.assertTrue("Open files list blocking decommission should be empty!", ((openFilesBlockingDecomEntries.size()) == 0));
        openFiles.putAll(DFSTestUtil.createOpenFiles(TestListOpenFiles.fs, "open-1", 1));
        verifyOpenFiles(openFiles);
        openFiles.putAll(DFSTestUtil.createOpenFiles(TestListOpenFiles.fs, "open-2", (((TestListOpenFiles.BATCH_SIZE) * 2) + ((TestListOpenFiles.BATCH_SIZE) / 2))));
        verifyOpenFiles(openFiles);
        DFSTestUtil.closeOpenFiles(openFiles, ((openFiles.size()) / 2));
        verifyOpenFiles(openFiles);
        openFiles.putAll(DFSTestUtil.createOpenFiles(TestListOpenFiles.fs, "open-3", ((TestListOpenFiles.BATCH_SIZE) * 5)));
        verifyOpenFiles(openFiles);
        while ((openFiles.size()) > 0) {
            DFSTestUtil.closeOpenFiles(openFiles, 1);
            verifyOpenFiles(openFiles);
        } 
    }

    /**
     * Verify dfsadmin -listOpenFiles command in HA mode.
     */
    @Test(timeout = 120000)
    public void testListOpenFilesInHA() throws Exception {
        TestListOpenFiles.fs.close();
        TestListOpenFiles.cluster.shutdown();
        HdfsConfiguration haConf = new HdfsConfiguration();
        haConf.setLong(DFS_NAMENODE_LIST_OPENFILES_NUM_RESPONSES, TestListOpenFiles.BATCH_SIZE);
        MiniDFSCluster haCluster = new MiniDFSCluster.Builder(haConf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
        try {
            HATestUtil.setFailoverConfigurations(haCluster, haConf);
            FileSystem fileSystem = HATestUtil.configureFailoverFs(haCluster, haConf);
            List<ClientProtocol> namenodes = HAUtil.getProxiesForAllNameNodesInNameservice(haConf, HATestUtil.getLogicalHostname(haCluster));
            haCluster.transitionToActive(0);
            Assert.assertTrue(HAUtil.isAtLeastOneActive(namenodes));
            final byte[] data = new byte[1024];
            ThreadLocalRandom.current().nextBytes(data);
            DFSTestUtil.createOpenFiles(fileSystem, "ha-open-file", (((TestListOpenFiles.BATCH_SIZE) * 4) + ((TestListOpenFiles.BATCH_SIZE) / 2)));
            final DFSAdmin dfsAdmin = new DFSAdmin(haConf);
            final AtomicBoolean failoverCompleted = new AtomicBoolean(false);
            final AtomicBoolean listOpenFilesError = new AtomicBoolean(false);
            final int listingIntervalMsec = 250;
            Thread clientThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!(failoverCompleted.get())) {
                        try {
                            Assert.assertEquals(0, ToolRunner.run(dfsAdmin, new String[]{ "-listOpenFiles" }));
                            Assert.assertEquals(0, ToolRunner.run(dfsAdmin, new String[]{ "-listOpenFiles", "-blockingDecommission" }));
                            // Sleep for some time to avoid
                            // flooding logs with listing.
                            Thread.sleep(listingIntervalMsec);
                        } catch (Exception e) {
                            listOpenFilesError.set(true);
                            TestListOpenFiles.LOG.info("Error listing open files: ", e);
                            break;
                        }
                    } 
                }
            });
            clientThread.start();
            // Let client list open files for few
            // times before the NN failover.
            Thread.sleep((listingIntervalMsec * 2));
            TestListOpenFiles.LOG.info("Shutting down Active NN0!");
            haCluster.shutdownNameNode(0);
            TestListOpenFiles.LOG.info("Transitioning NN1 to Active!");
            haCluster.transitionToActive(1);
            failoverCompleted.set(true);
            Assert.assertEquals(0, ToolRunner.run(dfsAdmin, new String[]{ "-listOpenFiles" }));
            Assert.assertEquals(0, ToolRunner.run(dfsAdmin, new String[]{ "-listOpenFiles", "-blockingDecommission" }));
            Assert.assertFalse("Client Error!", listOpenFilesError.get());
            clientThread.join();
        } finally {
            if (haCluster != null) {
                haCluster.shutdown();
            }
        }
    }
}

