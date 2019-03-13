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


import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.util.HostsFileWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DFS_HOSTS and DFS_HOSTS_EXCLUDE tests
 */
@RunWith(Parameterized.class)
public class TestHostsFiles {
    private static final Logger LOG = LoggerFactory.getLogger(TestHostsFiles.class.getName());

    private Class hostFileMgrClass;

    public TestHostsFiles(Class hostFileMgrClass) {
        this.hostFileMgrClass = hostFileMgrClass;
    }

    @Test
    public void testHostsExcludeInUI() throws Exception {
        Configuration conf = getConf();
        short REPLICATION_FACTOR = 2;
        final Path filePath = new Path("/testFile");
        HostsFileWriter hostsFileWriter = new HostsFileWriter();
        hostsFileWriter.initialize(conf, "temp/decommission");
        // Two blocks and four racks
        String[] racks = new String[]{ "/rack1", "/rack1", "/rack2", "/rack2" };
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(racks.length).racks(racks).build();
        final FSNamesystem ns = cluster.getNameNode().getNamesystem();
        try {
            // Create a file with one block
            final FileSystem fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, filePath, 1L, REPLICATION_FACTOR, 1L);
            ExtendedBlock b = DFSTestUtil.getFirstBlock(fs, filePath);
            DFSTestUtil.waitForReplication(cluster, b, 2, REPLICATION_FACTOR, 0);
            // Decommission one of the hosts with the block, this should cause
            // the block to get replicated to another host on the same rack,
            // otherwise the rack policy is violated.
            BlockLocation[] locs = fs.getFileBlockLocations(fs.getFileStatus(filePath), 0, Long.MAX_VALUE);
            String name = locs[0].getNames()[0];
            TestHostsFiles.LOG.info((("adding '" + name) + "' to decommission"));
            hostsFileWriter.initExcludeHost(name);
            ns.getBlockManager().getDatanodeManager().refreshNodes(conf);
            DFSTestUtil.waitForDecommission(fs, name);
            // Check the block still has sufficient # replicas across racks
            DFSTestUtil.waitForReplication(cluster, b, 2, REPLICATION_FACTOR, 0);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mxbeanName = new ObjectName("Hadoop:service=NameNode,name=NameNodeInfo");
            String nodes = ((String) (mbs.getAttribute(mxbeanName, "LiveNodes")));
            Assert.assertTrue("Live nodes should contain the decommissioned node", nodes.contains("Decommissioned"));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
            hostsFileWriter.cleanup();
        }
    }

    @Test
    public void testHostsIncludeForDeadCount() throws Exception {
        Configuration conf = getConf();
        HostsFileWriter hostsFileWriter = new HostsFileWriter();
        hostsFileWriter.initialize(conf, "temp/decommission");
        hostsFileWriter.initIncludeHosts(new String[]{ "localhost:52", "127.0.0.1:7777" });
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            final FSNamesystem ns = cluster.getNameNode().getNamesystem();
            Assert.assertTrue(((ns.getNumDeadDataNodes()) == 2));
            Assert.assertTrue(((ns.getNumLiveDataNodes()) == 0));
            // Testing using MBeans
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName mxbeanName = new ObjectName("Hadoop:service=NameNode,name=FSNamesystemState");
            String nodes = (mbs.getAttribute(mxbeanName, "NumDeadDataNodes")) + "";
            Assert.assertTrue((((Integer) (mbs.getAttribute(mxbeanName, "NumDeadDataNodes"))) == 2));
            Assert.assertTrue((((Integer) (mbs.getAttribute(mxbeanName, "NumLiveDataNodes"))) == 0));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
            hostsFileWriter.cleanup();
        }
    }
}

