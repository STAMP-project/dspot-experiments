/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.namenode.ha;


import DFSConfigKeys.DFS_HA_TAILEDITS_INPROGRESS_KEY;
import DFSConfigKeys.DFS_HA_TAILEDITS_PERIOD_KEY;
import DFSConfigKeys.DFS_JOURNALNODE_EDIT_CACHE_SIZE_KEY;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.HAUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.hdfs.qjournal.server.JournalTestUtil;
import org.apache.hadoop.hdfs.server.namenode.NNStorage;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test cases for in progress tailing edit logs by
 * the standby node.
 */
public class TestStandbyInProgressTail {
    private static final Logger LOG = LoggerFactory.getLogger(TestStandbyInProgressTail.class);

    private Configuration conf;

    private MiniQJMHACluster qjmhaCluster;

    private MiniDFSCluster cluster;

    private NameNode nn0;

    private NameNode nn1;

    @Test
    public void testDefault() throws Exception {
        if ((qjmhaCluster) != null) {
            qjmhaCluster.shutdown();
        }
        conf = new Configuration();
        // Set period of tail edits to a large value (20 mins) for test purposes
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, (20 * 60));
        conf.setBoolean(DFS_HA_TAILEDITS_INPROGRESS_KEY, false);
        HAUtil.setAllowStandbyReads(conf, true);
        qjmhaCluster = new MiniQJMHACluster.Builder(conf).build();
        cluster = qjmhaCluster.getDfsCluster();
        try {
            // During HA startup, both nodes should be in
            // standby and we shouldn't have any edits files
            // in any edits directory!
            List<URI> allDirs = Lists.newArrayList();
            allDirs.addAll(cluster.getNameDirs(0));
            allDirs.addAll(cluster.getNameDirs(1));
            TestStandbyInProgressTail.assertNoEditFiles(allDirs);
            // Set the first NN to active, make sure it creates edits
            // in its own dirs and the shared dir. The standby
            // should still have no edits!
            cluster.transitionToActive(0);
            TestStandbyInProgressTail.assertEditFiles(cluster.getNameDirs(0), NNStorage.getInProgressEditsFileName(1));
            TestStandbyInProgressTail.assertNoEditFiles(cluster.getNameDirs(1));
            cluster.getNameNode(0).getRpcServer().mkdirs("/test", FsPermission.createImmutable(((short) (493))), true);
            cluster.getNameNode(1).getNamesystem().getEditLogTailer().doTailEdits();
            // StandbyNameNode should not finish tailing in-progress logs
            Assert.assertNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test", true, false, false));
            // Restarting the standby should not finalize any edits files
            // in the shared directory when it starts up!
            cluster.restartNameNode(1);
            TestStandbyInProgressTail.assertEditFiles(cluster.getNameDirs(0), NNStorage.getInProgressEditsFileName(1));
            TestStandbyInProgressTail.assertNoEditFiles(cluster.getNameDirs(1));
            // Additionally it should not have applied any in-progress logs
            // at start-up -- otherwise, it would have read half-way into
            // the current log segment, and on the next roll, it would have to
            // either replay starting in the middle of the segment (not allowed)
            // or double-replay the edits (incorrect).
            Assert.assertNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test", true, false, false));
            cluster.getNameNode(0).getRpcServer().mkdirs("/test2", FsPermission.createImmutable(((short) (493))), true);
            // If we restart NN0, it'll come back as standby, and we can
            // transition NN1 to active and make sure it reads edits correctly.
            cluster.restartNameNode(0);
            cluster.transitionToActive(1);
            // NN1 should have both the edits that came before its restart,
            // and the edits that came after its restart.
            Assert.assertNotNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test", true, false, false));
            Assert.assertNotNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test2", true, false, false));
        } finally {
            if ((qjmhaCluster) != null) {
                qjmhaCluster.shutdown();
            }
        }
    }

    @Test
    public void testSetup() throws Exception {
        // During HA startup, both nodes should be in
        // standby and we shouldn't have any edits files
        // in any edits directory!
        List<URI> allDirs = Lists.newArrayList();
        allDirs.addAll(cluster.getNameDirs(0));
        allDirs.addAll(cluster.getNameDirs(1));
        TestStandbyInProgressTail.assertNoEditFiles(allDirs);
        // Set the first NN to active, make sure it creates edits
        // in its own dirs and the shared dir. The standby
        // should still have no edits!
        cluster.transitionToActive(0);
        TestStandbyInProgressTail.assertEditFiles(cluster.getNameDirs(0), NNStorage.getInProgressEditsFileName(1));
        TestStandbyInProgressTail.assertNoEditFiles(cluster.getNameDirs(1));
        cluster.getNameNode(0).getRpcServer().mkdirs("/test", FsPermission.createImmutable(((short) (493))), true);
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test");
        // Restarting the standby should not finalize any edits files
        // in the shared directory when it starts up!
        cluster.restartNameNode(1);
        TestStandbyInProgressTail.assertEditFiles(cluster.getNameDirs(0), NNStorage.getInProgressEditsFileName(1));
        TestStandbyInProgressTail.assertNoEditFiles(cluster.getNameDirs(1));
        // Because we're using in-progress tailer, this should not be null
        Assert.assertNotNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test", true, false, false));
        cluster.getNameNode(0).getRpcServer().mkdirs("/test2", FsPermission.createImmutable(((short) (493))), true);
        // If we restart NN0, it'll come back as standby, and we can
        // transition NN1 to active and make sure it reads edits correctly.
        cluster.restartNameNode(0);
        cluster.transitionToActive(1);
        // NN1 should have both the edits that came before its restart,
        // and the edits that came after its restart.
        Assert.assertNotNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test", true, false, false));
        Assert.assertNotNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test2", true, false, false));
    }

    @Test
    public void testHalfStartInProgressTail() throws Exception {
        // Set the first NN to active, make sure it creates edits
        // in its own dirs and the shared dir. The standby
        // should still have no edits!
        cluster.transitionToActive(0);
        TestStandbyInProgressTail.assertEditFiles(cluster.getNameDirs(0), NNStorage.getInProgressEditsFileName(1));
        TestStandbyInProgressTail.assertNoEditFiles(cluster.getNameDirs(1));
        cluster.getNameNode(0).getRpcServer().mkdirs("/test", FsPermission.createImmutable(((short) (493))), true);
        // StandbyNameNode should tail the in-progress edit
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test");
        // Create a new edit and finalized it
        cluster.getNameNode(0).getRpcServer().mkdirs("/test2", FsPermission.createImmutable(((short) (493))), true);
        nn0.getRpcServer().rollEditLog();
        // StandbyNameNode shouldn't tail the edit since we do not call the method
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test2");
        // Create a new in-progress edit and let SBNN do the tail
        cluster.getNameNode(0).getRpcServer().mkdirs("/test3", FsPermission.createImmutable(((short) (493))), true);
        // StandbyNameNode should tail the finalized edit and the new in-progress
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test", "/test2", "/test3");
    }

    @Test
    public void testInitStartInProgressTail() throws Exception {
        // Set the first NN to active, make sure it creates edits
        // in its own dirs and the shared dir. The standby
        // should still have no edits!
        cluster.transitionToActive(0);
        TestStandbyInProgressTail.assertEditFiles(cluster.getNameDirs(0), NNStorage.getInProgressEditsFileName(1));
        TestStandbyInProgressTail.assertNoEditFiles(cluster.getNameDirs(1));
        cluster.getNameNode(0).getRpcServer().mkdirs("/test", FsPermission.createImmutable(((short) (493))), true);
        cluster.getNameNode(0).getRpcServer().mkdirs("/test2", FsPermission.createImmutable(((short) (493))), true);
        nn0.getRpcServer().rollEditLog();
        cluster.getNameNode(0).getRpcServer().mkdirs("/test3", FsPermission.createImmutable(((short) (493))), true);
        Assert.assertNull(NameNodeAdapter.getFileInfo(nn1, "/test", true, false, false));
        Assert.assertNull(NameNodeAdapter.getFileInfo(nn1, "/test2", true, false, false));
        Assert.assertNull(NameNodeAdapter.getFileInfo(nn1, "/test3", true, false, false));
        // StandbyNameNode should tail the finalized edit and the new in-progress
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test", "/test2", "/test3");
    }

    @Test
    public void testNewStartInProgressTail() throws Exception {
        cluster.transitionToActive(0);
        TestStandbyInProgressTail.assertEditFiles(cluster.getNameDirs(0), NNStorage.getInProgressEditsFileName(1));
        TestStandbyInProgressTail.assertNoEditFiles(cluster.getNameDirs(1));
        cluster.getNameNode(0).getRpcServer().mkdirs("/test", FsPermission.createImmutable(((short) (493))), true);
        cluster.getNameNode(0).getRpcServer().mkdirs("/test2", FsPermission.createImmutable(((short) (493))), true);
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test", "/test2");
        nn0.getRpcServer().rollEditLog();
        cluster.getNameNode(0).getRpcServer().mkdirs("/test3", FsPermission.createImmutable(((short) (493))), true);
        // StandbyNameNode should tail the finalized edit and the new in-progress
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test", "/test2", "/test3");
    }

    @Test
    public void testNonUniformConfig() throws Exception {
        // Test case where some NNs (in this case the active NN) in the cluster
        // do not have in-progress tailing enabled.
        Configuration newConf = cluster.getNameNode(0).getConf();
        newConf.setBoolean(DFS_HA_TAILEDITS_INPROGRESS_KEY, false);
        cluster.restartNameNode(0);
        cluster.transitionToActive(0);
        cluster.getNameNode(0).getRpcServer().mkdirs("/test", FsPermission.createImmutable(((short) (493))), true);
        cluster.getNameNode(0).getRpcServer().rollEdits();
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test");
    }

    @Test
    public void testEditsServedViaCache() throws Exception {
        cluster.transitionToActive(0);
        cluster.waitActive(0);
        TestStandbyInProgressTail.mkdirs(nn0, "/test", "/test2");
        nn0.getRpcServer().rollEditLog();
        for (int idx = 0; idx < (qjmhaCluster.getJournalCluster().getNumNodes()); idx++) {
            File[] startingEditFile = qjmhaCluster.getJournalCluster().getCurrentDir(idx, DFSUtil.getNamenodeNameServiceId(conf)).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches("edits_0+1-[0-9]+");
                }
            });
            Assert.assertNotNull(startingEditFile);
            Assert.assertEquals(1, startingEditFile.length);
            // Delete this edit file to ensure that edits can't be served via the
            // streaming mechanism - RPC/cache-based only
            startingEditFile[0].delete();
        }
        // Ensure edits were not tailed before the edit files were deleted;
        // quick spot check of a single dir
        Assert.assertNull(NameNodeAdapter.getFileInfo(nn1, "/tmp0", false, false, false));
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test", "/test2");
    }

    @Test
    public void testCorruptJournalCache() throws Exception {
        cluster.transitionToActive(0);
        cluster.waitActive(0);
        // Shut down one JN so there is only a quorum remaining to make it easier
        // to manage the remaining two
        qjmhaCluster.getJournalCluster().getJournalNode(0).stopAndJoin(0);
        TestStandbyInProgressTail.mkdirs(nn0, "/test", "/test2");
        JournalTestUtil.corruptJournaledEditsCache(1, qjmhaCluster.getJournalCluster().getJournalNode(1).getJournal(DFSUtil.getNamenodeNameServiceId(conf)));
        nn0.getRpcServer().rollEditLog();
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test", "/test2");
        TestStandbyInProgressTail.mkdirs(nn0, "/test3", "/test4");
        JournalTestUtil.corruptJournaledEditsCache(3, qjmhaCluster.getJournalCluster().getJournalNode(2).getJournal(DFSUtil.getNamenodeNameServiceId(conf)));
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test3", "/test4");
    }

    @Test
    public void testTailWithoutCache() throws Exception {
        qjmhaCluster.shutdown();
        // Effectively disable the cache by setting its size too small to be used
        conf.setInt(DFS_JOURNALNODE_EDIT_CACHE_SIZE_KEY, 1);
        qjmhaCluster = new MiniQJMHACluster.Builder(conf).build();
        cluster = qjmhaCluster.getDfsCluster();
        cluster.transitionToActive(0);
        cluster.waitActive(0);
        nn0 = cluster.getNameNode(0);
        nn1 = cluster.getNameNode(1);
        TestStandbyInProgressTail.mkdirs(nn0, "/test", "/test2");
        nn0.getRpcServer().rollEditLog();
        TestStandbyInProgressTail.mkdirs(nn0, "/test3", "/test4");
        // Skip the last directory; the JournalNodes' idea of the committed
        // txn ID may not have been updated to include it yet
        TestStandbyInProgressTail.waitForFileInfo(nn1, "/test", "/test2", "/test3");
    }
}

