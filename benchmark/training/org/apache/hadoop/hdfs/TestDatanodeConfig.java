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


import DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY;
import DFSConfigKeys.DFS_DATANODE_MAX_LOCKED_MEMORY_DEFAULT;
import DFSConfigKeys.DFS_DATANODE_MAX_LOCKED_MEMORY_KEY;
import NativeIO.POSIX;
import StartupOption.REGULAR;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.common.Util;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.io.nativeio.NativeIO;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Tests if a data-node can startup depending on configuration parameters.
 */
public class TestDatanodeConfig {
    private static final File BASE_DIR = new File(MiniDFSCluster.getBaseDirectory());

    private static MiniDFSCluster cluster;

    /**
     * Test that a data-node does not start if configuration specifies
     * incorrect URI scheme in data directory.
     * Test that a data-node starts if data directory is specified as
     * URI = "file:///path" or as a non URI path.
     */
    @Test
    public void testDataDirectories() throws IOException {
        File dataDir = new File(TestDatanodeConfig.BASE_DIR, "data").getCanonicalFile();
        Configuration conf = TestDatanodeConfig.cluster.getConfiguration(0);
        // 1. Test unsupported ecPolicy. Only "file:" is supported.
        String dnDir = TestDatanodeConfig.makeURI("shv", null, Util.fileAsURI(dataDir).getPath());
        conf.set(DFS_DATANODE_DATA_DIR_KEY, dnDir);
        DataNode dn = null;
        try {
            dn = DataNode.createDataNode(new String[]{  }, conf);
            Assert.fail();
        } catch (Exception e) {
            // expecting exception here
        } finally {
            if (dn != null) {
                dn.shutdown();
            }
        }
        Assert.assertNull("Data-node startup should have failed.", dn);
        // 2. Test "file:" ecPolicy and no ecPolicy (path-only). Both should work.
        String dnDir1 = (Util.fileAsURI(dataDir).toString()) + "1";
        String dnDir2 = TestDatanodeConfig.makeURI("file", "localhost", ((Util.fileAsURI(dataDir).getPath()) + "2"));
        String dnDir3 = (dataDir.getAbsolutePath()) + "3";
        conf.set(DFS_DATANODE_DATA_DIR_KEY, ((((dnDir1 + ",") + dnDir2) + ",") + dnDir3));
        try {
            TestDatanodeConfig.cluster.startDataNodes(conf, 1, false, REGULAR, null);
            Assert.assertTrue("Data-node should startup.", TestDatanodeConfig.cluster.isDataNodeUp());
        } finally {
            if ((TestDatanodeConfig.cluster) != null) {
                TestDatanodeConfig.cluster.shutdownDataNodes();
            }
        }
    }

    @Test(timeout = 60000)
    public void testMemlockLimit() throws Exception {
        Assume.assumeTrue(NativeIO.isAvailable());
        final long memlockLimit = POSIX.getCacheManipulator().getMemlockLimit();
        // Can't increase the memlock limit past the maximum.
        Assume.assumeTrue((memlockLimit != (Long.MAX_VALUE)));
        File dataDir = new File(TestDatanodeConfig.BASE_DIR, "data").getCanonicalFile();
        Configuration conf = TestDatanodeConfig.cluster.getConfiguration(0);
        conf.set(DFS_DATANODE_DATA_DIR_KEY, TestDatanodeConfig.makeURI("file", null, Util.fileAsURI(dataDir).getPath()));
        long prevLimit = conf.getLong(DFS_DATANODE_MAX_LOCKED_MEMORY_KEY, DFS_DATANODE_MAX_LOCKED_MEMORY_DEFAULT);
        DataNode dn = null;
        try {
            // Try starting the DN with limit configured to the ulimit
            conf.setLong(DFS_DATANODE_MAX_LOCKED_MEMORY_KEY, memlockLimit);
            dn = DataNode.createDataNode(new String[]{  }, conf);
            dn.shutdown();
            dn = null;
            // Try starting the DN with a limit > ulimit
            conf.setLong(DFS_DATANODE_MAX_LOCKED_MEMORY_KEY, (memlockLimit + 1));
            try {
                dn = DataNode.createDataNode(new String[]{  }, conf);
            } catch (RuntimeException e) {
                GenericTestUtils.assertExceptionContains("more than the datanode's available RLIMIT_MEMLOCK", e);
            }
        } finally {
            if (dn != null) {
                dn.shutdown();
            }
            conf.setLong(DFS_DATANODE_MAX_LOCKED_MEMORY_KEY, prevLimit);
        }
    }
}

