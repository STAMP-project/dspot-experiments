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


import HttpConfig.Policy.HTTPS_ONLY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.ssl.KeyStoreTestUtil;
import org.apache.hadoop.test.GenericTestUtils;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test checks that the NameNode respects the following keys:
 *
 *  - DFS_NAMENODE_RPC_BIND_HOST_KEY
 *  - DFS_NAMENODE_SERVICE_RPC_BIND_HOST_KEY
 *  - DFS_NAMENODE_LIFELINE_RPC_BIND_HOST_KEY
 *  - DFS_NAMENODE_HTTP_BIND_HOST_KEY
 *  - DFS_NAMENODE_HTTPS_BIND_HOST_KEY
 */
public class TestNameNodeRespectsBindHostKeys {
    public static final Logger LOG = LoggerFactory.getLogger(TestNameNodeRespectsBindHostKeys.class);

    private static final String WILDCARD_ADDRESS = "0.0.0.0";

    private static final String LOCALHOST_SERVER_ADDRESS = "127.0.0.1:0";

    private static String keystoresDir;

    private static String sslConfDir;

    @Test(timeout = 300000)
    public void testRpcBindHostKey() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing without " + (DFS_NAMENODE_RPC_BIND_HOST_KEY)));
        // NN should not bind the wildcard address by default.
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = TestNameNodeRespectsBindHostKeys.getRpcServerAddress(cluster);
            Assert.assertThat("Bind address not expected to be wildcard by default.", address, IsNot.not(("/" + (TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS))));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
                cluster = null;
            }
        }
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing with " + (DFS_NAMENODE_RPC_BIND_HOST_KEY)));
        // Tell NN to bind the wildcard address.
        conf.set(DFS_NAMENODE_RPC_BIND_HOST_KEY, TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS);
        // Verify that NN binds wildcard address now.
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = TestNameNodeRespectsBindHostKeys.getRpcServerAddress(cluster);
            Assert.assertThat((("Bind address " + address) + " is not wildcard."), address, Is.is(("/" + (TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS))));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 300000)
    public void testServiceRpcBindHostKey() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing without " + (DFS_NAMENODE_SERVICE_RPC_BIND_HOST_KEY)));
        conf.set(DFS_NAMENODE_SERVICE_RPC_ADDRESS_KEY, TestNameNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
        // NN should not bind the wildcard address by default.
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = TestNameNodeRespectsBindHostKeys.getServiceRpcServerAddress(cluster);
            Assert.assertThat("Bind address not expected to be wildcard by default.", address, IsNot.not(("/" + (TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS))));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
                cluster = null;
            }
        }
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing with " + (DFS_NAMENODE_SERVICE_RPC_BIND_HOST_KEY)));
        // Tell NN to bind the wildcard address.
        conf.set(DFS_NAMENODE_SERVICE_RPC_BIND_HOST_KEY, TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS);
        // Verify that NN binds wildcard address now.
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = TestNameNodeRespectsBindHostKeys.getServiceRpcServerAddress(cluster);
            Assert.assertThat((("Bind address " + address) + " is not wildcard."), address, Is.is(("/" + (TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS))));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 300000)
    public void testLifelineRpcBindHostKey() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing without " + (DFS_NAMENODE_LIFELINE_RPC_BIND_HOST_KEY)));
        conf.set(DFS_NAMENODE_LIFELINE_RPC_ADDRESS_KEY, TestNameNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
        // NN should not bind the wildcard address by default.
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = TestNameNodeRespectsBindHostKeys.getLifelineRpcServerAddress(cluster);
            Assert.assertThat("Bind address not expected to be wildcard by default.", address, IsNot.not(("/" + (TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS))));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
                cluster = null;
            }
        }
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing with " + (DFS_NAMENODE_LIFELINE_RPC_BIND_HOST_KEY)));
        // Tell NN to bind the wildcard address.
        conf.set(DFS_NAMENODE_LIFELINE_RPC_BIND_HOST_KEY, TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS);
        // Verify that NN binds wildcard address now.
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = TestNameNodeRespectsBindHostKeys.getLifelineRpcServerAddress(cluster);
            Assert.assertThat((("Bind address " + address) + " is not wildcard."), address, Is.is(("/" + (TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS))));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 300000)
    public void testHttpBindHostKey() throws IOException {
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing without " + (DFS_NAMENODE_HTTP_BIND_HOST_KEY)));
        // NN should not bind the wildcard address by default.
        try {
            conf.set(DFS_NAMENODE_HTTP_ADDRESS_KEY, TestNameNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = cluster.getNameNode().getHttpAddress().toString();
            Assert.assertFalse("HTTP Bind address not expected to be wildcard by default.", address.startsWith(TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
                cluster = null;
            }
        }
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing with " + (DFS_NAMENODE_HTTP_BIND_HOST_KEY)));
        // Tell NN to bind the wildcard address.
        conf.set(DFS_NAMENODE_HTTP_BIND_HOST_KEY, TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS);
        // Verify that NN binds wildcard address now.
        try {
            conf.set(DFS_NAMENODE_HTTP_ADDRESS_KEY, TestNameNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = cluster.getNameNode().getHttpAddress().toString();
            Assert.assertTrue((("HTTP Bind address " + address) + " is not wildcard."), address.startsWith(TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    private static final String BASEDIR = GenericTestUtils.getTempPath(TestNameNodeRespectsBindHostKeys.class.getSimpleName());

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
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing behavior without " + (DFS_NAMENODE_HTTPS_BIND_HOST_KEY)));
        TestNameNodeRespectsBindHostKeys.setupSsl();
        conf.set(DFS_CLIENT_HTTPS_KEYSTORE_RESOURCE_KEY, KeyStoreTestUtil.getClientSSLConfigFileName());
        conf.set(DFS_SERVER_HTTPS_KEYSTORE_RESOURCE_KEY, KeyStoreTestUtil.getServerSSLConfigFileName());
        conf.set(DFS_HTTP_POLICY_KEY, HTTPS_ONLY.name());
        // NN should not bind the wildcard address by default.
        try {
            conf.set(DFS_NAMENODE_HTTPS_ADDRESS_KEY, TestNameNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = cluster.getNameNode().getHttpsAddress().toString();
            Assert.assertFalse("HTTP Bind address not expected to be wildcard by default.", address.startsWith(TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
                cluster = null;
            }
        }
        TestNameNodeRespectsBindHostKeys.LOG.info(("Testing behavior with " + (DFS_NAMENODE_HTTPS_BIND_HOST_KEY)));
        // Tell NN to bind the wildcard address.
        conf.set(DFS_NAMENODE_HTTPS_BIND_HOST_KEY, TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS);
        // Verify that NN binds wildcard address now.
        try {
            conf.set(DFS_NAMENODE_HTTPS_ADDRESS_KEY, TestNameNodeRespectsBindHostKeys.LOCALHOST_SERVER_ADDRESS);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            String address = cluster.getNameNode().getHttpsAddress().toString();
            Assert.assertTrue((("HTTP Bind address " + address) + " is not wildcard."), address.startsWith(TestNameNodeRespectsBindHostKeys.WILDCARD_ADDRESS));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
            if (((((TestNameNodeRespectsBindHostKeys.keystoresDir) != null) && (!(TestNameNodeRespectsBindHostKeys.keystoresDir.isEmpty()))) && ((TestNameNodeRespectsBindHostKeys.sslConfDir) != null)) && (!(TestNameNodeRespectsBindHostKeys.sslConfDir.isEmpty()))) {
                KeyStoreTestUtil.cleanupSSLConfig(TestNameNodeRespectsBindHostKeys.keystoresDir, TestNameNodeRespectsBindHostKeys.sslConfDir);
            }
        }
    }
}

