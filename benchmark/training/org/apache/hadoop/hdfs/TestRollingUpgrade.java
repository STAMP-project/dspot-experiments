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


import DFSConfigKeys.DFS_HA_TAILEDITS_PERIOD_KEY;
import DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_CHECK_PERIOD_KEY;
import DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_TXNS_KEY;
import DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY;
import RollingUpgradeAction.FINALIZE;
import RollingUpgradeAction.PREPARE;
import RollingUpgradeAction.QUERY;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import StartupOption.REGULAR;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.RollingUpgradeInfo;
import org.apache.hadoop.hdfs.qjournal.MiniJournalCluster;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.namenode.CheckpointFaultInjector;
import org.apache.hadoop.hdfs.server.namenode.FSImage;
import org.apache.hadoop.hdfs.server.namenode.SecondaryNameNode;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests rolling upgrade.
 */
public class TestRollingUpgrade {
    private static final Logger LOG = LoggerFactory.getLogger(TestRollingUpgrade.class);

    /**
     * Test DFSAdmin Upgrade Command.
     */
    @Test
    public void testDFSAdminRollingUpgradeCommands() throws Exception {
        // start a cluster
        final Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            final Path foo = new Path("/foo");
            final Path bar = new Path("/bar");
            final Path baz = new Path("/baz");
            {
                final DistributedFileSystem dfs = cluster.getFileSystem();
                final DFSAdmin dfsadmin = new DFSAdmin(conf);
                dfs.mkdirs(foo);
                // illegal argument "abc" to rollingUpgrade option
                TestRollingUpgrade.runCmd(dfsadmin, false, "-rollingUpgrade", "abc");
                TestRollingUpgrade.checkMxBeanIsNull();
                // query rolling upgrade
                TestRollingUpgrade.runCmd(dfsadmin, true, "-rollingUpgrade");
                // start rolling upgrade
                dfs.setSafeMode(SAFEMODE_ENTER);
                TestRollingUpgrade.runCmd(dfsadmin, true, "-rollingUpgrade", "prepare");
                dfs.setSafeMode(SAFEMODE_LEAVE);
                // query rolling upgrade
                TestRollingUpgrade.runCmd(dfsadmin, true, "-rollingUpgrade", "query");
                TestRollingUpgrade.checkMxBean();
                dfs.mkdirs(bar);
                // finalize rolling upgrade
                TestRollingUpgrade.runCmd(dfsadmin, true, "-rollingUpgrade", "finalize");
                // RollingUpgradeInfo should be null after finalization, both via
                // Java API and in JMX
                Assert.assertNull(dfs.rollingUpgrade(QUERY));
                TestRollingUpgrade.checkMxBeanIsNull();
                dfs.mkdirs(baz);
                TestRollingUpgrade.runCmd(dfsadmin, true, "-rollingUpgrade");
                // All directories created before upgrade, when upgrade in progress and
                // after upgrade finalize exists
                Assert.assertTrue(dfs.exists(foo));
                Assert.assertTrue(dfs.exists(bar));
                Assert.assertTrue(dfs.exists(baz));
                dfs.setSafeMode(SAFEMODE_ENTER);
                dfs.saveNamespace();
                dfs.setSafeMode(SAFEMODE_LEAVE);
            }
            // Ensure directories exist after restart
            cluster.restartNameNode();
            {
                final DistributedFileSystem dfs = cluster.getFileSystem();
                Assert.assertTrue(dfs.exists(foo));
                Assert.assertTrue(dfs.exists(bar));
                Assert.assertTrue(dfs.exists(baz));
            }
        } finally {
            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test(timeout = 30000)
    public void testRollingUpgradeWithQJM() throws Exception {
        String nnDirPrefix = (MiniDFSCluster.getBaseDirectory()) + "/nn/";
        final File nn1Dir = new File((nnDirPrefix + "image1"));
        final File nn2Dir = new File((nnDirPrefix + "image2"));
        TestRollingUpgrade.LOG.info(("nn1Dir=" + nn1Dir));
        TestRollingUpgrade.LOG.info(("nn2Dir=" + nn2Dir));
        final Configuration conf = new HdfsConfiguration();
        final MiniJournalCluster mjc = new MiniJournalCluster.Builder(conf).build();
        mjc.waitActive();
        TestRollingUpgrade.setConf(conf, nn1Dir, mjc);
        {
            // Start the cluster once to generate the dfs dirs
            final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).manageNameDfsDirs(false).checkExitOnShutdown(false).build();
            // Shutdown the cluster before making a copy of the namenode dir to release
            // all file locks, otherwise, the copy will fail on some platforms.
            cluster.shutdown();
        }
        MiniDFSCluster cluster2 = null;
        try {
            // Start a second NN pointed to the same quorum.
            // We need to copy the image dir from the first NN -- or else
            // the new NN will just be rejected because of Namespace mismatch.
            FileUtil.fullyDelete(nn2Dir);
            FileUtil.copy(nn1Dir, FileSystem.getLocal(conf).getRaw(), new Path(nn2Dir.getAbsolutePath()), false, conf);
            // Start the cluster again
            final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).manageNameDfsDirs(false).checkExitOnShutdown(false).build();
            final Path foo = new Path("/foo");
            final Path bar = new Path("/bar");
            final Path baz = new Path("/baz");
            final RollingUpgradeInfo info1;
            {
                final DistributedFileSystem dfs = cluster.getFileSystem();
                dfs.mkdirs(foo);
                // start rolling upgrade
                dfs.setSafeMode(SAFEMODE_ENTER);
                info1 = dfs.rollingUpgrade(PREPARE);
                dfs.setSafeMode(SAFEMODE_LEAVE);
                TestRollingUpgrade.LOG.info(("START\n" + info1));
                // query rolling upgrade
                Assert.assertEquals(info1, dfs.rollingUpgrade(QUERY));
                dfs.mkdirs(bar);
                cluster.shutdown();
            }
            // cluster2 takes over QJM
            final Configuration conf2 = TestRollingUpgrade.setConf(new Configuration(), nn2Dir, mjc);
            cluster2 = new MiniDFSCluster.Builder(conf2).numDataNodes(0).format(false).manageNameDfsDirs(false).build();
            final DistributedFileSystem dfs2 = cluster2.getFileSystem();
            // Check that cluster2 sees the edits made on cluster1
            Assert.assertTrue(dfs2.exists(foo));
            Assert.assertTrue(dfs2.exists(bar));
            Assert.assertFalse(dfs2.exists(baz));
            // query rolling upgrade in cluster2
            Assert.assertEquals(info1, dfs2.rollingUpgrade(QUERY));
            dfs2.mkdirs(baz);
            TestRollingUpgrade.LOG.info("RESTART cluster 2");
            cluster2.restartNameNode();
            Assert.assertEquals(info1, dfs2.rollingUpgrade(QUERY));
            Assert.assertTrue(dfs2.exists(foo));
            Assert.assertTrue(dfs2.exists(bar));
            Assert.assertTrue(dfs2.exists(baz));
            // restart cluster with -upgrade should fail.
            try {
                cluster2.restartNameNode("-upgrade");
            } catch (IOException e) {
                TestRollingUpgrade.LOG.info("The exception is expected.", e);
            }
            TestRollingUpgrade.LOG.info("RESTART cluster 2 again");
            cluster2.restartNameNode();
            Assert.assertEquals(info1, dfs2.rollingUpgrade(QUERY));
            Assert.assertTrue(dfs2.exists(foo));
            Assert.assertTrue(dfs2.exists(bar));
            Assert.assertTrue(dfs2.exists(baz));
            // finalize rolling upgrade
            final RollingUpgradeInfo finalize = dfs2.rollingUpgrade(FINALIZE);
            Assert.assertTrue(finalize.isFinalized());
            TestRollingUpgrade.LOG.info("RESTART cluster 2 with regular startup option");
            cluster2.getNameNodeInfos()[0].setStartOpt(REGULAR);
            cluster2.restartNameNode();
            Assert.assertTrue(dfs2.exists(foo));
            Assert.assertTrue(dfs2.exists(bar));
            Assert.assertTrue(dfs2.exists(baz));
        } finally {
            if (cluster2 != null)
                cluster2.shutdown();

        }
    }

    @Test
    public void testRollback() throws Exception {
        // start a cluster
        final Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            final Path foo = new Path("/foo");
            final Path bar = new Path("/bar");
            cluster.getFileSystem().mkdirs(foo);
            final Path file = new Path(foo, "file");
            final byte[] data = new byte[1024];
            ThreadLocalRandom.current().nextBytes(data);
            final FSDataOutputStream out = cluster.getFileSystem().create(file);
            out.write(data, 0, data.length);
            out.close();
            TestRollingUpgrade.checkMxBeanIsNull();
            TestRollingUpgrade.startRollingUpgrade(foo, bar, file, data, cluster);
            TestRollingUpgrade.checkMxBean();
            cluster.getFileSystem().rollEdits();
            cluster.getFileSystem().rollEdits();
            TestRollingUpgrade.rollbackRollingUpgrade(foo, bar, file, data, cluster);
            TestRollingUpgrade.checkMxBeanIsNull();
            TestRollingUpgrade.startRollingUpgrade(foo, bar, file, data, cluster);
            cluster.getFileSystem().rollEdits();
            cluster.getFileSystem().rollEdits();
            TestRollingUpgrade.rollbackRollingUpgrade(foo, bar, file, data, cluster);
            TestRollingUpgrade.startRollingUpgrade(foo, bar, file, data, cluster);
            cluster.restartNameNode();
            TestRollingUpgrade.rollbackRollingUpgrade(foo, bar, file, data, cluster);
            TestRollingUpgrade.startRollingUpgrade(foo, bar, file, data, cluster);
            cluster.restartNameNode();
            TestRollingUpgrade.rollbackRollingUpgrade(foo, bar, file, data, cluster);
            TestRollingUpgrade.startRollingUpgrade(foo, bar, file, data, cluster);
            TestRollingUpgrade.rollbackRollingUpgrade(foo, bar, file, data, cluster);
            TestRollingUpgrade.startRollingUpgrade(foo, bar, file, data, cluster);
            TestRollingUpgrade.rollbackRollingUpgrade(foo, bar, file, data, cluster);
        } finally {
            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test
    public void testDFSAdminDatanodeUpgradeControlCommands() throws Exception {
        // start a cluster
        final Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            final DFSAdmin dfsadmin = new DFSAdmin(conf);
            DataNode dn = cluster.getDataNodes().get(0);
            // check the datanode
            final String dnAddr = dn.getDatanodeId().getIpcAddr(false);
            final String[] args1 = new String[]{ "-getDatanodeInfo", dnAddr };
            TestRollingUpgrade.runCmd(dfsadmin, true, args1);
            // issue shutdown to the datanode.
            final String[] args2 = new String[]{ "-shutdownDatanode", dnAddr, "upgrade" };
            TestRollingUpgrade.runCmd(dfsadmin, true, args2);
            // the datanode should be down.
            GenericTestUtils.waitForThreadTermination("Async datanode shutdown thread", 100, 10000);
            Assert.assertFalse("DataNode should exit", dn.isDatanodeUp());
            // ping should fail.
            Assert.assertEquals((-1), dfsadmin.run(args1));
        } finally {
            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test(timeout = 300000)
    public void testFinalize() throws Exception {
        testFinalize(2);
    }

    @Test(timeout = 300000)
    public void testFinalizeWithMultipleNN() throws Exception {
        testFinalize(3);
    }

    @Test(timeout = 300000)
    public void testQuery() throws Exception {
        testQuery(2);
    }

    @Test(timeout = 300000)
    public void testQueryWithMultipleNN() throws Exception {
        testQuery(3);
    }

    @Test(timeout = 300000)
    public void testQueryAfterRestart() throws IOException, InterruptedException {
        final Configuration conf = new Configuration();
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            cluster.waitActive();
            DistributedFileSystem dfs = cluster.getFileSystem();
            dfs.setSafeMode(SAFEMODE_ENTER);
            // start rolling upgrade
            dfs.rollingUpgrade(PREPARE);
            TestRollingUpgrade.queryForPreparation(dfs);
            dfs.setSafeMode(SAFEMODE_ENTER);
            dfs.saveNamespace();
            dfs.setSafeMode(SAFEMODE_LEAVE);
            cluster.restartNameNodes();
            dfs.rollingUpgrade(QUERY);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 300000)
    public void testCheckpoint() throws IOException, InterruptedException {
        testCheckpoint(2);
    }

    @Test(timeout = 300000)
    public void testCheckpointWithMultipleNN() throws IOException, InterruptedException {
        testCheckpoint(3);
    }

    @Test(timeout = 60000)
    public void testRollBackImage() throws Exception {
        final Configuration conf = new Configuration();
        conf.setInt(DFS_NAMENODE_CHECKPOINT_TXNS_KEY, 10);
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
        conf.setInt(DFS_NAMENODE_CHECKPOINT_CHECK_PERIOD_KEY, 2);
        MiniQJMHACluster cluster = null;
        CheckpointFaultInjector old = CheckpointFaultInjector.getInstance();
        try {
            cluster = new MiniQJMHACluster.Builder(conf).setNumNameNodes(2).build();
            MiniDFSCluster dfsCluster = cluster.getDfsCluster();
            dfsCluster.waitActive();
            dfsCluster.transitionToActive(0);
            DistributedFileSystem dfs = dfsCluster.getFileSystem(0);
            for (int i = 0; i <= 10; i++) {
                Path foo = new Path(("/foo" + i));
                dfs.mkdirs(foo);
            }
            cluster.getDfsCluster().getNameNodeRpc(0).rollEdits();
            CountDownLatch ruEdit = new CountDownLatch(1);
            CheckpointFaultInjector.set(new CheckpointFaultInjector() {
                @Override
                public void duringUploadInProgess() throws IOException, InterruptedException {
                    if ((ruEdit.getCount()) == 1) {
                        ruEdit.countDown();
                        Thread.sleep(180000);
                    }
                }
            });
            ruEdit.await();
            RollingUpgradeInfo info = dfs.rollingUpgrade(PREPARE);
            Assert.assertTrue(info.isStarted());
            FSImage fsimage = dfsCluster.getNamesystem(0).getFSImage();
            TestRollingUpgrade.queryForPreparation(dfs);
            // The NN should have a copy of the fsimage in case of rollbacks.
            Assert.assertTrue(fsimage.hasRollbackFSImage());
        } finally {
            CheckpointFaultInjector.set(old);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * In non-HA setup, after rolling upgrade prepare, the Secondary NN should
     * still be able to do checkpoint
     */
    @Test
    public void testCheckpointWithSNN() throws Exception {
        MiniDFSCluster cluster = null;
        DistributedFileSystem dfs = null;
        SecondaryNameNode snn = null;
        try {
            Configuration conf = new HdfsConfiguration();
            cluster = new MiniDFSCluster.Builder(conf).build();
            cluster.waitActive();
            conf.set(DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, "0.0.0.0:0");
            snn = new SecondaryNameNode(conf);
            dfs = cluster.getFileSystem();
            dfs.mkdirs(new Path("/test/foo"));
            snn.doCheckpoint();
            // start rolling upgrade
            dfs.setSafeMode(SAFEMODE_ENTER);
            dfs.rollingUpgrade(PREPARE);
            dfs.setSafeMode(SAFEMODE_LEAVE);
            dfs.mkdirs(new Path("/test/bar"));
            // do checkpoint in SNN again
            snn.doCheckpoint();
        } finally {
            IOUtils.cleanup(null, dfs);
            if (snn != null) {
                snn.shutdown();
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

