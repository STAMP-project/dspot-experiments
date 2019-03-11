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


import DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_TXNS_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * A JUnit test for checking if restarting DFS preserves integrity.
 * Specifically with FSImage being written in parallel
 */
public class TestParallelImageWrite {
    private static final int NUM_DATANODES = 4;

    /**
     * check if DFS remains in proper condition after a restart
     */
    @Test
    public void testRestartDFS() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        FSNamesystem fsn = null;
        int numNamenodeDirs;
        DFSTestUtil files = new DFSTestUtil.Builder().setName("TestRestartDFS").setNumFiles(200).build();
        final String dir = "/srcdat";
        final Path rootpath = new Path("/");
        final Path dirpath = new Path(dir);
        long rootmtime;
        FileStatus rootstatus;
        FileStatus dirstatus;
        try {
            cluster = new MiniDFSCluster.Builder(conf).format(true).numDataNodes(TestParallelImageWrite.NUM_DATANODES).build();
            String[] nameNodeDirs = conf.getStrings(DFS_NAMENODE_NAME_DIR_KEY, new String[]{  });
            numNamenodeDirs = nameNodeDirs.length;
            Assert.assertTrue("failed to get number of Namenode StorageDirs", (numNamenodeDirs != 0));
            FileSystem fs = cluster.getFileSystem();
            files.createFiles(fs, dir);
            rootmtime = fs.getFileStatus(rootpath).getModificationTime();
            rootstatus = fs.getFileStatus(dirpath);
            dirstatus = fs.getFileStatus(dirpath);
            fs.setOwner(rootpath, ((rootstatus.getOwner()) + "_XXX"), null);
            fs.setOwner(dirpath, null, ((dirstatus.getGroup()) + "_XXX"));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
        try {
            // Force the NN to save its images on startup so long as
            // there are any uncheckpointed txns
            conf.setInt(DFS_NAMENODE_CHECKPOINT_TXNS_KEY, 1);
            // Here we restart the MiniDFScluster without formatting namenode
            cluster = new MiniDFSCluster.Builder(conf).format(false).numDataNodes(TestParallelImageWrite.NUM_DATANODES).build();
            fsn = cluster.getNamesystem();
            FileSystem fs = cluster.getFileSystem();
            Assert.assertTrue("Filesystem corrupted after restart.", files.checkFiles(fs, dir));
            final FileStatus newrootstatus = fs.getFileStatus(rootpath);
            Assert.assertEquals(rootmtime, newrootstatus.getModificationTime());
            Assert.assertEquals(((rootstatus.getOwner()) + "_XXX"), newrootstatus.getOwner());
            Assert.assertEquals(rootstatus.getGroup(), newrootstatus.getGroup());
            final FileStatus newdirstatus = fs.getFileStatus(dirpath);
            Assert.assertEquals(dirstatus.getOwner(), newdirstatus.getOwner());
            Assert.assertEquals(((dirstatus.getGroup()) + "_XXX"), newdirstatus.getGroup());
            rootmtime = fs.getFileStatus(rootpath).getModificationTime();
            final String checkAfterRestart = TestParallelImageWrite.checkImages(fsn, numNamenodeDirs);
            // Modify the system and then perform saveNamespace
            files.cleanup(fs, dir);
            files.createFiles(fs, dir);
            fsn.setSafeMode(SAFEMODE_ENTER);
            cluster.getNameNodeRpc().saveNamespace(0, 0);
            final String checkAfterModify = TestParallelImageWrite.checkImages(fsn, numNamenodeDirs);
            Assert.assertFalse((((("Modified namespace should change fsimage contents. " + "was: ") + checkAfterRestart) + " now: ") + checkAfterModify), checkAfterRestart.equals(checkAfterModify));
            fsn.setSafeMode(SAFEMODE_LEAVE);
            files.cleanup(fs, dir);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

