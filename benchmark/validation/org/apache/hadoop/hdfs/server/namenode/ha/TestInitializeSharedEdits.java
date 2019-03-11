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


import DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX;
import DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY;
import DFSConfigKeys.DFS_NAMESERVICES;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestInitializeSharedEdits {
    private static final Logger LOG = LoggerFactory.getLogger(TestInitializeSharedEdits.class);

    private static final Path TEST_PATH = new Path("/test");

    private Configuration conf;

    private MiniDFSCluster cluster;

    @Test
    public void testInitializeSharedEdits() throws Exception {
        assertCannotStartNameNodes();
        // Initialize the shared edits dir.
        Assert.assertFalse(NameNode.initializeSharedEdits(cluster.getConfiguration(0)));
        assertCanStartHaNameNodes("1");
        // Now that we've done a metadata operation, make sure that deleting and
        // re-initializing the shared edits dir will let the standby still start.
        shutdownClusterAndRemoveSharedEditsDir();
        assertCannotStartNameNodes();
        // Re-initialize the shared edits dir.
        Assert.assertFalse(NameNode.initializeSharedEdits(cluster.getConfiguration(0)));
        // Should *still* be able to start both NNs
        assertCanStartHaNameNodes("2");
    }

    @Test
    public void testFailWhenNoSharedEditsSpecified() throws Exception {
        Configuration confNoShared = new Configuration(conf);
        confNoShared.unset(DFS_NAMENODE_SHARED_EDITS_DIR_KEY);
        Assert.assertFalse(NameNode.initializeSharedEdits(confNoShared, true));
    }

    @Test
    public void testDontOverWriteExistingDir() throws IOException {
        Assert.assertFalse(NameNode.initializeSharedEdits(conf, false));
        Assert.assertTrue(NameNode.initializeSharedEdits(conf, false));
    }

    @Test
    public void testInitializeSharedEditsConfiguresGenericConfKeys() throws IOException {
        Configuration conf = new Configuration();
        conf.set(DFS_NAMESERVICES, "ns1");
        conf.set(DFSUtil.addKeySuffixes(DFS_HA_NAMENODES_KEY_PREFIX, "ns1"), "nn1,nn2");
        conf.set(DFSUtil.addKeySuffixes(DFS_NAMENODE_RPC_ADDRESS_KEY, "ns1", "nn1"), "localhost:1234");
        Assert.assertNull(conf.get(DFS_NAMENODE_RPC_ADDRESS_KEY));
        NameNode.initializeSharedEdits(conf);
        Assert.assertNotNull(conf.get(DFS_NAMENODE_RPC_ADDRESS_KEY));
    }
}

