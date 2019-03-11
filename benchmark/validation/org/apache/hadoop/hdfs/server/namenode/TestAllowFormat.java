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


import DFSConfigKeys.DFS_HA_NAMENODE_ID_KEY;
import DFSConfigKeys.DFS_NAMENODE_EDITS_PLUGIN_PREFIX;
import DFSConfigKeys.DFS_NAMENODE_SHARED_EDITS_DIR_KEY;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.ha.HATestUtil;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Startup and format tests
 */
public class TestAllowFormat {
    public static final String NAME_NODE_HOST = "localhost:";

    public static final String NAME_NODE_HTTP_HOST = "0.0.0.0:";

    private static final Logger LOG = LoggerFactory.getLogger(TestAllowFormat.class.getName());

    private static final File DFS_BASE_DIR = new File(PathUtils.getTestDir(TestAllowFormat.class), "dfs");

    private static Configuration config;

    private static MiniDFSCluster cluster = null;

    /**
     * start MiniDFScluster, try formatting with different settings
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testAllowFormat() throws IOException {
        TestAllowFormat.LOG.info("--starting mini cluster");
        // manage dirs parameter set to false
        NameNode nn;
        // 1. Create a new cluster and format DFS
        TestAllowFormat.config.setBoolean(DFSConfigKeys.DFS_NAMENODE_SUPPORT_ALLOW_FORMAT_KEY, true);
        TestAllowFormat.cluster = new MiniDFSCluster.Builder(TestAllowFormat.config).manageDataDfsDirs(false).manageNameDfsDirs(false).build();
        TestAllowFormat.cluster.waitActive();
        Assert.assertNotNull(TestAllowFormat.cluster);
        nn = TestAllowFormat.cluster.getNameNode();
        Assert.assertNotNull(nn);
        TestAllowFormat.LOG.info("Mini cluster created OK");
        // 2. Try formatting DFS with allowformat false.
        // NOTE: the cluster must be shut down for format to work.
        TestAllowFormat.LOG.info("Verifying format will fail with allowformat false");
        TestAllowFormat.config.setBoolean(DFSConfigKeys.DFS_NAMENODE_SUPPORT_ALLOW_FORMAT_KEY, false);
        try {
            TestAllowFormat.cluster.shutdown();
            NameNode.format(TestAllowFormat.config);
            Assert.fail("Format succeeded, when it should have failed");
        } catch (IOException e) {
            // expected to fail
            // Verify we got message we expected
            Assert.assertTrue("Exception was not about formatting Namenode", e.getMessage().startsWith(("The option " + (DFSConfigKeys.DFS_NAMENODE_SUPPORT_ALLOW_FORMAT_KEY))));
            TestAllowFormat.LOG.info(("Expected failure: " + (StringUtils.stringifyException(e))));
            TestAllowFormat.LOG.info("Done verifying format will fail with allowformat false");
        }
        // 3. Try formatting DFS with allowformat true
        TestAllowFormat.LOG.info("Verifying format will succeed with allowformat true");
        TestAllowFormat.config.setBoolean(DFSConfigKeys.DFS_NAMENODE_SUPPORT_ALLOW_FORMAT_KEY, true);
        NameNode.format(TestAllowFormat.config);
        TestAllowFormat.LOG.info("Done verifying format will succeed with allowformat true");
    }

    /**
     * Test to skip format for non file scheme directory configured
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFormatShouldBeIgnoredForNonFileBasedDirs() throws Exception {
        Configuration conf = new HdfsConfiguration();
        String logicalName = "mycluster";
        // DFS_NAMENODE_RPC_ADDRESS_KEY are required to identify the NameNode
        // is configured in HA, then only DFS_NAMENODE_SHARED_EDITS_DIR_KEY
        // is considered.
        String localhost = "127.0.0.1";
        InetSocketAddress nnAddr1 = new InetSocketAddress(localhost, 8020);
        InetSocketAddress nnAddr2 = new InetSocketAddress(localhost, 8020);
        HATestUtil.setFailoverConfigurations(conf, logicalName, nnAddr1, nnAddr2);
        conf.set(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY, new File(TestAllowFormat.DFS_BASE_DIR, "name").getAbsolutePath());
        conf.setBoolean(DFSConfigKeys.DFS_NAMENODE_SUPPORT_ALLOW_FORMAT_KEY, true);
        conf.set(DFSUtil.addKeySuffixes(DFS_NAMENODE_EDITS_PLUGIN_PREFIX, "dummy"), TestGenericJournalConf.DummyJournalManager.class.getName());
        conf.set(DFS_NAMENODE_SHARED_EDITS_DIR_KEY, (("dummy://" + localhost) + ":2181/ledgers"));
        conf.set(DFS_HA_NAMENODE_ID_KEY, "nn1");
        // An internal assert is added to verify the working of test
        NameNode.format(conf);
    }
}

