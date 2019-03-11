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
package org.apache.hadoop.hdfs.qjournal.server;


import HttpConfig.Policy.HTTPS_ONLY;
import java.io.IOException;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.qjournal.MiniJournalCluster;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test checks that the JournalNode respects the following keys.
 *
 *  - DFS_JOURNALNODE_RPC_BIND_HOST_KEY
 *  - DFS_JOURNALNODE_HTTP_BIND_HOST_KEY
 *  - DFS_JOURNALNODE_HTTPS_BIND_HOST_KEY
 */
public class TestJournalNodeRespectsBindHostKeys {
    public static final Logger LOG = LoggerFactory.getLogger(TestJournalNodeRespectsBindHostKeys.class);

    private static final String WILDCARD_ADDRESS = "0.0.0.0";

    private static final String LOCALHOST_SERVER_ADDRESS = "127.0.0.1:0";

    private static final int NUM_JN = 1;

    private HdfsConfiguration conf;

    private MiniJournalCluster jCluster;

    private JournalNode jn;

    @Test(timeout = 300000)
    public void testRpcBindHostKey() throws IOException {
        TestJournalNodeRespectsBindHostKeys.LOG.info(("Testing without " + (DFSConfigKeys.DFS_JOURNALNODE_RPC_BIND_HOST_KEY)));
        // NN should not bind the wildcard address by default.
        jCluster = new MiniJournalCluster.Builder(conf).format(true).numJournalNodes(TestJournalNodeRespectsBindHostKeys.NUM_JN).build();
        jn = jCluster.getJournalNode(0);
        String address = TestJournalNodeRespectsBindHostKeys.getRpcServerAddress(jn);
        Assert.assertThat("Bind address not expected to be wildcard by default.", address, IsNot.not(("/" + (TestJournalNodeRespectsBindHostKeys.WILDCARD_ADDRESS))));
        TestJournalNodeRespectsBindHostKeys.LOG.info(("Testing with " + (DFSConfigKeys.DFS_JOURNALNODE_RPC_BIND_HOST_KEY)));
        // Tell NN to bind the wildcard address.
        conf.set(DFSConfigKeys.DFS_JOURNALNODE_RPC_BIND_HOST_KEY, TestJournalNodeRespectsBindHostKeys.WILDCARD_ADDRESS);
        // Verify that NN binds wildcard address now.
        jCluster = new MiniJournalCluster.Builder(conf).format(true).numJournalNodes(TestJournalNodeRespectsBindHostKeys.NUM_JN).build();
        jn = jCluster.getJournalNode(0);
        address = TestJournalNodeRespectsBindHostKeys.getRpcServerAddress(jn);
        Assert.assertThat((("Bind address " + address) + " is not wildcard."), address, Is.is(("/" + (TestJournalNodeRespectsBindHostKeys.WILDCARD_ADDRESS))));
    }

    @Test(timeout = 300000)
    public void testHttpBindHostKey() throws IOException {
        TestJournalNodeRespectsBindHostKeys.LOG.info(("Testing without " + (DFSConfigKeys.DFS_JOURNALNODE_HTTP_BIND_HOST_KEY)));
        // NN should not bind the wildcard address by default.
        conf.set(DFSConfigKeys.DFS_JOURNALNODE_HTTP_ADDRESS_KEY, TestJournalNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
        jCluster = new MiniJournalCluster.Builder(conf).format(true).numJournalNodes(TestJournalNodeRespectsBindHostKeys.NUM_JN).build();
        jn = jCluster.getJournalNode(0);
        String address = jn.getHttpAddress().toString();
        Assert.assertFalse("HTTP Bind address not expected to be wildcard by default.", address.startsWith(TestJournalNodeRespectsBindHostKeys.WILDCARD_ADDRESS));
        TestJournalNodeRespectsBindHostKeys.LOG.info(("Testing with " + (DFSConfigKeys.DFS_JOURNALNODE_HTTP_BIND_HOST_KEY)));
        // Tell NN to bind the wildcard address.
        conf.set(DFSConfigKeys.DFS_JOURNALNODE_HTTP_BIND_HOST_KEY, TestJournalNodeRespectsBindHostKeys.WILDCARD_ADDRESS);
        // Verify that NN binds wildcard address now.
        conf.set(DFSConfigKeys.DFS_JOURNALNODE_HTTP_ADDRESS_KEY, TestJournalNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
        jCluster = new MiniJournalCluster.Builder(conf).format(true).numJournalNodes(TestJournalNodeRespectsBindHostKeys.NUM_JN).build();
        jn = jCluster.getJournalNode(0);
        address = jn.getHttpAddress().toString();
        Assert.assertTrue((("HTTP Bind address " + address) + " is not wildcard."), address.startsWith(TestJournalNodeRespectsBindHostKeys.WILDCARD_ADDRESS));
    }

    private static final String BASEDIR = ((System.getProperty("test.build.dir", "target/test-dir")) + "/") + (TestJournalNodeRespectsBindHostKeys.class.getSimpleName());

    /**
     * HTTPS test is different since we need to setup SSL configuration.
     * NN also binds the wildcard address for HTTPS port by default so we must
     * pick a different host/port combination.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testHttpsBindHostKey() throws Exception {
        TestJournalNodeRespectsBindHostKeys.LOG.info(("Testing behavior without " + (DFSConfigKeys.DFS_JOURNALNODE_HTTPS_BIND_HOST_KEY)));
        TestJournalNodeRespectsBindHostKeys.setupSsl();
        conf.set(DFSConfigKeys.DFS_HTTP_POLICY_KEY, HTTPS_ONLY.name());
        // NN should not bind the wildcard address by default.
        conf.set(DFSConfigKeys.DFS_JOURNALNODE_HTTPS_ADDRESS_KEY, TestJournalNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
        jCluster = new MiniJournalCluster.Builder(conf).format(true).numJournalNodes(TestJournalNodeRespectsBindHostKeys.NUM_JN).build();
        jn = jCluster.getJournalNode(0);
        String address = jn.getHttpsAddress().toString();
        Assert.assertFalse("HTTP Bind address not expected to be wildcard by default.", address.startsWith(TestJournalNodeRespectsBindHostKeys.WILDCARD_ADDRESS));
        TestJournalNodeRespectsBindHostKeys.LOG.info(("Testing behavior with " + (DFSConfigKeys.DFS_JOURNALNODE_HTTPS_BIND_HOST_KEY)));
        // Tell NN to bind the wildcard address.
        conf.set(DFSConfigKeys.DFS_JOURNALNODE_HTTPS_BIND_HOST_KEY, TestJournalNodeRespectsBindHostKeys.WILDCARD_ADDRESS);
        // Verify that NN binds wildcard address now.
        conf.set(DFSConfigKeys.DFS_JOURNALNODE_HTTPS_ADDRESS_KEY, TestJournalNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
        jCluster = new MiniJournalCluster.Builder(conf).format(true).numJournalNodes(TestJournalNodeRespectsBindHostKeys.NUM_JN).build();
        jn = jCluster.getJournalNode(0);
        address = jn.getHttpsAddress().toString();
        Assert.assertTrue((("HTTP Bind address " + address) + " is not wildcard."), address.startsWith(TestJournalNodeRespectsBindHostKeys.WILDCARD_ADDRESS));
    }
}

