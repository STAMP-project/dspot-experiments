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


import DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY;
import DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY;
import DFSConfigKeys.DFS_DATANODE_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_DATANODE_IPC_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_BACKUP_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.test.PathUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test checks correctness of port usage by hdfs components:
 * NameNode, DataNode, SecondaryNamenode and BackupNode.
 *
 * The correct behavior is:<br>
 * - when a specific port is provided the server must either start on that port
 * or fail by throwing {@link java.net.BindException}.<br>
 * - if the port = 0 (ephemeral) then the server should choose
 * a free port and start on it.
 */
public class TestHDFSServerPorts {
    public static final Logger LOG = LoggerFactory.getLogger(TestHDFSServerPorts.class);

    // reset default 0.0.0.0 addresses in order to avoid IPv6 problem
    static final String THIS_HOST = (TestHDFSServerPorts.getFullHostName()) + ":0";

    private static final File TEST_DATA_DIR = PathUtils.getTestDir(TestHDFSServerPorts.class);

    static {
        DefaultMetricsSystem.setMiniClusterMode(true);
    }

    Configuration config;

    File hdfsDir;

    @Test(timeout = 300000)
    public void testNameNodePorts() throws Exception {
        runTestNameNodePorts(false);
        runTestNameNodePorts(true);
    }

    /**
     * Verify datanode port usage.
     */
    @Test(timeout = 300000)
    public void testDataNodePorts() throws Exception {
        NameNode nn = null;
        try {
            nn = startNameNode();
            // start data-node on the same port as name-node
            Configuration conf2 = new HdfsConfiguration(config);
            conf2.set(DFS_DATANODE_DATA_DIR_KEY, new File(hdfsDir, "data").getPath());
            conf2.set(DFS_DATANODE_ADDRESS_KEY, FileSystem.getDefaultUri(config).getAuthority());
            conf2.set(DFS_DATANODE_HTTP_ADDRESS_KEY, TestHDFSServerPorts.THIS_HOST);
            boolean started = canStartDataNode(conf2);
            Assert.assertFalse(started);// should fail

            // bind http server to the same port as name-node
            conf2.set(DFS_DATANODE_ADDRESS_KEY, TestHDFSServerPorts.THIS_HOST);
            conf2.set(DFS_DATANODE_HTTP_ADDRESS_KEY, config.get(DFS_NAMENODE_HTTP_ADDRESS_KEY));
            started = canStartDataNode(conf2);
            Assert.assertFalse(started);// should fail

            // both ports are different from the name-node ones
            conf2.set(DFS_DATANODE_ADDRESS_KEY, TestHDFSServerPorts.THIS_HOST);
            conf2.set(DFS_DATANODE_HTTP_ADDRESS_KEY, TestHDFSServerPorts.THIS_HOST);
            conf2.set(DFS_DATANODE_IPC_ADDRESS_KEY, TestHDFSServerPorts.THIS_HOST);
            started = canStartDataNode(conf2);
            Assert.assertTrue(started);// should start now

        } finally {
            stopNameNode(nn);
        }
    }

    /**
     * Verify secondary namenode port usage.
     */
    @Test(timeout = 300000)
    public void testSecondaryNodePorts() throws Exception {
        NameNode nn = null;
        try {
            nn = startNameNode();
            // bind http server to the same port as name-node
            Configuration conf2 = new HdfsConfiguration(config);
            conf2.set(DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, config.get(DFS_NAMENODE_HTTP_ADDRESS_KEY));
            TestHDFSServerPorts.LOG.info(("= Starting 1 on: " + (conf2.get(DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY))));
            boolean started = canStartSecondaryNode(conf2);
            Assert.assertFalse(started);// should fail

            // bind http server to a different port
            conf2.set(DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, TestHDFSServerPorts.THIS_HOST);
            TestHDFSServerPorts.LOG.info(("= Starting 2 on: " + (conf2.get(DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY))));
            started = canStartSecondaryNode(conf2);
            Assert.assertTrue(started);// should start now

        } finally {
            stopNameNode(nn);
        }
    }

    /**
     * Verify BackupNode port usage.
     */
    @Test(timeout = 300000)
    public void testBackupNodePorts() throws Exception {
        NameNode nn = null;
        try {
            nn = startNameNode();
            Configuration backup_config = new HdfsConfiguration(config);
            backup_config.set(DFS_NAMENODE_BACKUP_ADDRESS_KEY, TestHDFSServerPorts.THIS_HOST);
            // bind http server to the same port as name-node
            backup_config.set(DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY, backup_config.get(DFS_NAMENODE_HTTP_ADDRESS_KEY));
            TestHDFSServerPorts.LOG.info(("= Starting 1 on: " + (backup_config.get(DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY))));
            Assert.assertFalse("Backup started on same port as Namenode", canStartBackupNode(backup_config));// should fail

            // reset namenode backup address because Windows does not release
            // port used previously properly.
            backup_config.set(DFS_NAMENODE_BACKUP_ADDRESS_KEY, TestHDFSServerPorts.THIS_HOST);
            // bind http server to a different port
            backup_config.set(DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY, TestHDFSServerPorts.THIS_HOST);
            TestHDFSServerPorts.LOG.info(("= Starting 2 on: " + (backup_config.get(DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY))));
            boolean started = canStartBackupNode(backup_config);
            Assert.assertTrue("Backup Namenode should've started", started);// should start now

        } finally {
            stopNameNode(nn);
        }
    }
}

