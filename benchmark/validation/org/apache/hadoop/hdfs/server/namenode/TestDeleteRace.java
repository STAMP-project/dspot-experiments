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


import DFSConfigKeys.DFS_BLOCK_REPLICATOR_CLASSNAME_KEY;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_PERMISSIONS_ENABLED_KEY;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.AddBlockFlag;
import org.apache.hadoop.hdfs.AppendTestUtil;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.BlockStoragePolicy;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicy;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockPlacementPolicyDefault;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeStorageInfo;
import org.apache.hadoop.hdfs.server.namenode.LeaseManager.Lease;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.net.Node;
import org.apache.hadoop.test.Whitebox;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test race between delete and other operations.  For now only addBlock()
 * is tested since all others are acquiring FSNamesystem lock for the
 * whole duration.
 */
public class TestDeleteRace {
    private static final int BLOCK_SIZE = 4096;

    private static final Logger LOG = LoggerFactory.getLogger(TestDeleteRace.class);

    private static final Configuration conf = new HdfsConfiguration();

    private MiniDFSCluster cluster;

    @Rule
    public Timeout timeout = new Timeout((60000 * 3));

    @Test
    public void testDeleteAddBlockRace() throws Exception {
        testDeleteAddBlockRace(false);
    }

    @Test
    public void testDeleteAddBlockRaceWithSnapshot() throws Exception {
        testDeleteAddBlockRace(true);
    }

    private static class SlowBlockPlacementPolicy extends BlockPlacementPolicyDefault {
        @Override
        public DatanodeStorageInfo[] chooseTarget(String srcPath, int numOfReplicas, Node writer, List<DatanodeStorageInfo> chosenNodes, boolean returnChosenNodes, Set<Node> excludedNodes, long blocksize, final BlockStoragePolicy storagePolicy, EnumSet<AddBlockFlag> flags) {
            DatanodeStorageInfo[] results = super.chooseTarget(srcPath, numOfReplicas, writer, chosenNodes, returnChosenNodes, excludedNodes, blocksize, storagePolicy, flags);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            return results;
        }
    }

    private class DeleteThread extends Thread {
        private FileSystem fs;

        private Path path;

        DeleteThread(FileSystem fs, Path path) {
            this.fs = fs;
            this.path = path;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                TestDeleteRace.LOG.info(("Deleting" + (path)));
                final FSDirectory fsdir = cluster.getNamesystem().dir;
                INode fileINode = fsdir.getINode4Write(path.toString());
                INodeMap inodeMap = ((INodeMap) (Whitebox.getInternalState(fsdir, "inodeMap")));
                fs.delete(path, false);
                // after deletion, add the inode back to the inodeMap
                inodeMap.put(fileINode);
                TestDeleteRace.LOG.info(("Deleted" + (path)));
            } catch (Exception e) {
                TestDeleteRace.LOG.info(e.toString());
            }
        }
    }

    private class RenameThread extends Thread {
        private FileSystem fs;

        private Path from;

        private Path to;

        RenameThread(FileSystem fs, Path from, Path to) {
            this.fs = fs;
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                TestDeleteRace.LOG.info(((("Renaming " + (from)) + " to ") + (to)));
                fs.rename(from, to);
                TestDeleteRace.LOG.info(((("Renamed " + (from)) + " to ") + (to)));
            } catch (Exception e) {
                TestDeleteRace.LOG.info(e.toString());
            }
        }
    }

    @Test
    public void testRenameRace() throws Exception {
        try {
            TestDeleteRace.conf.setClass(DFS_BLOCK_REPLICATOR_CLASSNAME_KEY, TestDeleteRace.SlowBlockPlacementPolicy.class, BlockPlacementPolicy.class);
            cluster = new MiniDFSCluster.Builder(TestDeleteRace.conf).build();
            FileSystem fs = cluster.getFileSystem();
            Path dirPath1 = new Path("/testRenameRace1");
            Path dirPath2 = new Path("/testRenameRace2");
            Path filePath = new Path("/testRenameRace1/file1");
            fs.mkdirs(dirPath1);
            FSDataOutputStream out = fs.create(filePath);
            Thread renameThread = new TestDeleteRace.RenameThread(fs, dirPath1, dirPath2);
            renameThread.start();
            // write data and close to make sure a block is allocated.
            out.write(new byte[32], 0, 32);
            out.close();
            // Restart name node so that it replays edit. If old path was
            // logged in edit, it will fail to come up.
            cluster.restartNameNode(0);
        } finally {
            if ((cluster) != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 600000)
    public void testDeleteAndCommitBlockSynchonizationRaceNoSnapshot() throws Exception {
        testDeleteAndCommitBlockSynchronizationRace(false);
    }

    @Test(timeout = 600000)
    public void testDeleteAndCommitBlockSynchronizationRaceHasSnapshot() throws Exception {
        testDeleteAndCommitBlockSynchronizationRace(true);
    }

    /**
     * Test the sequence of deleting a file that has snapshot,
     * and lease manager's hard limit recovery.
     */
    @Test
    public void testDeleteAndLeaseRecoveryHardLimitSnapshot() throws Exception {
        final Path rootPath = new Path("/");
        final Configuration config = new Configuration();
        // Disable permissions so that another user can recover the lease.
        config.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, false);
        config.setInt(DFS_BLOCK_SIZE_KEY, TestDeleteRace.BLOCK_SIZE);
        FSDataOutputStream stm = null;
        try {
            cluster = new MiniDFSCluster.Builder(config).numDataNodes(3).build();
            cluster.waitActive();
            final DistributedFileSystem fs = cluster.getFileSystem();
            final Path testPath = new Path("/testfile");
            stm = fs.create(testPath);
            TestDeleteRace.LOG.info(("test on " + testPath));
            // write a half block
            AppendTestUtil.write(stm, 0, ((TestDeleteRace.BLOCK_SIZE) / 2));
            stm.hflush();
            // create a snapshot, so delete does not release the file's inode.
            SnapshotTestHelper.createSnapshot(fs, rootPath, "snap");
            // delete the file without closing it.
            fs.delete(testPath, false);
            // write enough bytes to trigger an addBlock, which would fail in
            // the streamer.
            AppendTestUtil.write(stm, 0, TestDeleteRace.BLOCK_SIZE);
            // Mock a scenario that the lease reached hard limit.
            final LeaseManager lm = ((LeaseManager) (Whitebox.getInternalState(cluster.getNameNode().getNamesystem(), "leaseManager")));
            final TreeSet<Lease> leases = ((TreeSet<Lease>) (Whitebox.getInternalState(lm, "sortedLeases")));
            final TreeSet<Lease> spyLeases = new TreeSet(new Comparator<Lease>() {
                @Override
                public int compare(Lease o1, Lease o2) {
                    return Long.signum(((o1.getLastUpdate()) - (o2.getLastUpdate())));
                }
            });
            while (!(leases.isEmpty())) {
                final Lease lease = leases.first();
                final Lease spyLease = Mockito.spy(lease);
                Mockito.doReturn(true).when(spyLease).expiredHardLimit();
                spyLeases.add(spyLease);
                leases.remove(lease);
            } 
            Whitebox.setInternalState(lm, "sortedLeases", spyLeases);
            // wait for lease manager's background 'Monitor' class to check leases.
            Thread.sleep((2 * (TestDeleteRace.conf.getLong(DFSConfigKeys.DFS_NAMENODE_LEASE_RECHECK_INTERVAL_MS_KEY, DFSConfigKeys.DFS_NAMENODE_LEASE_RECHECK_INTERVAL_MS_DEFAULT))));
            TestDeleteRace.LOG.info("Now check we can restart");
            cluster.restartNameNodes();
            TestDeleteRace.LOG.info("Restart finished");
        } finally {
            if (stm != null) {
                IOUtils.closeStream(stm);
            }
            if ((cluster) != null) {
                cluster.shutdown();
            }
        }
    }
}

