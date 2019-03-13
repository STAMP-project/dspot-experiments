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
package org.apache.hadoop.hdfs.server.namenode.ha;


import com.google.common.collect.Lists;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.HAUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.server.namenode.EditLogFileOutputStream;
import org.apache.hadoop.hdfs.server.namenode.NNStorage;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test cases for the handling of edit logs during failover
 * and startup of the standby node.
 */
public class TestEditLogsDuringFailover {
    private static final Logger LOG = LoggerFactory.getLogger(TestEditLogsDuringFailover.class);

    private static final int NUM_DIRS_IN_LOG = 5;

    static {
        // No need to fsync for the purposes of tests. This makes
        // the tests run much faster.
        EditLogFileOutputStream.setShouldSkipFsyncForTesting(true);
    }

    @Test
    public void testStartup() throws Exception {
        Configuration conf = new Configuration();
        HAUtil.setAllowStandbyReads(conf, true);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
        try {
            // During HA startup, both nodes should be in
            // standby and we shouldn't have any edits files
            // in any edits directory!
            List<URI> allDirs = Lists.newArrayList();
            allDirs.addAll(cluster.getNameDirs(0));
            allDirs.addAll(cluster.getNameDirs(1));
            allDirs.add(cluster.getSharedEditsDir(0, 1));
            assertNoEditFiles(allDirs);
            // Set the first NN to active, make sure it creates edits
            // in its own dirs and the shared dir. The standby
            // should still have no edits!
            cluster.transitionToActive(0);
            assertEditFiles(cluster.getNameDirs(0), NNStorage.getInProgressEditsFileName(1));
            assertEditFiles(Collections.singletonList(cluster.getSharedEditsDir(0, 1)), NNStorage.getInProgressEditsFileName(1));
            assertNoEditFiles(cluster.getNameDirs(1));
            cluster.getNameNode(0).getRpcServer().mkdirs("/test", FsPermission.createImmutable(((short) (493))), true);
            // Restarting the standby should not finalize any edits files
            // in the shared directory when it starts up!
            cluster.restartNameNode(1);
            assertEditFiles(cluster.getNameDirs(0), NNStorage.getInProgressEditsFileName(1));
            assertEditFiles(Collections.singletonList(cluster.getSharedEditsDir(0, 1)), NNStorage.getInProgressEditsFileName(1));
            assertNoEditFiles(cluster.getNameDirs(1));
            // Additionally it should not have applied any in-progress logs
            // at start-up -- otherwise, it would have read half-way into
            // the current log segment, and on the next roll, it would have to
            // either replay starting in the middle of the segment (not allowed)
            // or double-replay the edits (incorrect).
            Assert.assertNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test", true, false, false));
            cluster.getNameNode(0).getRpcServer().mkdirs("/test2", FsPermission.createImmutable(((short) (493))), true);
            // If we restart NN0, it'll come back as standby, and we can
            // transition NN1 to active and make sure it reads edits correctly at this point.
            cluster.restartNameNode(0);
            cluster.transitionToActive(1);
            // NN1 should have both the edits that came before its restart, and the edits that
            // came after its restart.
            Assert.assertNotNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test", true, false, false));
            Assert.assertNotNull(NameNodeAdapter.getFileInfo(cluster.getNameNode(1), "/test2", true, false, false));
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testFailoverFinalizesAndReadsInProgressSimple() throws Exception {
        testFailoverFinalizesAndReadsInProgress(false);
    }

    @Test
    public void testFailoverFinalizesAndReadsInProgressWithPartialTxAtEnd() throws Exception {
        testFailoverFinalizesAndReadsInProgress(true);
    }
}

