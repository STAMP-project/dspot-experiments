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


import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import StartupOption.REGULAR;
import StartupOption.UPGRADE;
import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.hdfs.server.common.Storage;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for upgrading with HA enabled.
 */
public class TestDFSUpgradeWithHA {
    private static final Logger LOG = LoggerFactory.getLogger(TestDFSUpgradeWithHA.class);

    private Configuration conf;

    /**
     * Ensure that an admin cannot finalize an HA upgrade without at least one NN
     * being active.
     */
    @Test
    public void testCannotFinalizeIfNoActive() throws IOException, URISyntaxException {
        MiniDFSCluster cluster = null;
        FileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
            File sharedDir = new File(cluster.getSharedEditsDir(0, 1));
            // No upgrade is in progress at the moment.
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, false);
            // Transition NN0 to active and do some FS ops.
            cluster.transitionToActive(0);
            fs = HATestUtil.configureFailoverFs(cluster, conf);
            Assert.assertTrue(fs.mkdirs(new Path("/foo1")));
            // Do the upgrade. Shut down NN1 and then restart NN0 with the upgrade
            // flag.
            cluster.shutdownNameNode(1);
            cluster.getNameNodeInfos()[0].setStartOpt(UPGRADE);
            cluster.restartNameNode(0, false);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, false);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, true);
            // NN0 should come up in the active state when given the -upgrade option,
            // so no need to transition it to active.
            Assert.assertTrue(fs.mkdirs(new Path("/foo2")));
            // Restart NN0 without the -upgrade flag, to make sure that works.
            cluster.getNameNodeInfos()[0].setStartOpt(REGULAR);
            cluster.restartNameNode(0, false);
            // Make sure we can still do FS ops after upgrading.
            cluster.transitionToActive(0);
            Assert.assertTrue(fs.mkdirs(new Path("/foo3")));
            // Now bootstrap the standby with the upgraded info.
            int rc = BootstrapStandby.run(new String[]{ "-force" }, cluster.getConfiguration(1));
            Assert.assertEquals(0, rc);
            // Now restart NN1 and make sure that we can do ops against that as well.
            cluster.restartNameNode(1);
            cluster.transitionToStandby(0);
            cluster.transitionToActive(1);
            Assert.assertTrue(fs.mkdirs(new Path("/foo4")));
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            // Now there's no active NN.
            cluster.transitionToStandby(1);
            try {
                runFinalizeCommand(cluster);
                Assert.fail("Should not have been able to finalize upgrade with no NN active");
            } catch (IOException ioe) {
                GenericTestUtils.assertExceptionContains("Cannot finalize with no NameNode active", ioe);
            }
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Make sure that an HA NN with NFS-based HA can successfully start and
     * upgrade.
     */
    @Test
    public void testNfsUpgrade() throws IOException, URISyntaxException {
        MiniDFSCluster cluster = null;
        FileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
            File sharedDir = new File(cluster.getSharedEditsDir(0, 1));
            // No upgrade is in progress at the moment.
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, false);
            // Transition NN0 to active and do some FS ops.
            cluster.transitionToActive(0);
            fs = HATestUtil.configureFailoverFs(cluster, conf);
            Assert.assertTrue(fs.mkdirs(new Path("/foo1")));
            // Do the upgrade. Shut down NN1 and then restart NN0 with the upgrade
            // flag.
            cluster.shutdownNameNode(1);
            cluster.getNameNodeInfos()[0].setStartOpt(UPGRADE);
            cluster.restartNameNode(0, false);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, false);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, true);
            // NN0 should come up in the active state when given the -upgrade option,
            // so no need to transition it to active.
            Assert.assertTrue(fs.mkdirs(new Path("/foo2")));
            // Restart NN0 without the -upgrade flag, to make sure that works.
            cluster.getNameNodeInfos()[0].setStartOpt(REGULAR);
            cluster.restartNameNode(0, false);
            // Make sure we can still do FS ops after upgrading.
            cluster.transitionToActive(0);
            Assert.assertTrue(fs.mkdirs(new Path("/foo3")));
            // Now bootstrap the standby with the upgraded info.
            int rc = BootstrapStandby.run(new String[]{ "-force" }, cluster.getConfiguration(1));
            Assert.assertEquals(0, rc);
            // Now restart NN1 and make sure that we can do ops against that as well.
            cluster.restartNameNode(1);
            cluster.transitionToStandby(0);
            cluster.transitionToActive(1);
            Assert.assertTrue(fs.mkdirs(new Path("/foo4")));
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Make sure that an HA NN can successfully upgrade when configured using
     * JournalNodes.
     */
    @Test
    public void testUpgradeWithJournalNodes() throws IOException, URISyntaxException {
        MiniQJMHACluster qjCluster = null;
        FileSystem fs = null;
        try {
            MiniQJMHACluster.Builder builder = new MiniQJMHACluster.Builder(conf);
            builder.getDfsBuilder().numDataNodes(0);
            qjCluster = builder.build();
            MiniDFSCluster cluster = qjCluster.getDfsCluster();
            // No upgrade is in progress at the moment.
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, false);
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            // Transition NN0 to active and do some FS ops.
            cluster.transitionToActive(0);
            fs = HATestUtil.configureFailoverFs(cluster, conf);
            Assert.assertTrue(fs.mkdirs(new Path("/foo1")));
            // get the value of the committedTxnId in journal nodes
            final long cidBeforeUpgrade = getCommittedTxnIdValue(qjCluster);
            // Do the upgrade. Shut down NN1 and then restart NN0 with the upgrade
            // flag.
            cluster.shutdownNameNode(1);
            cluster.getNameNodeInfos()[0].setStartOpt(UPGRADE);
            cluster.restartNameNode(0, false);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, false);
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, true);
            Assert.assertTrue((cidBeforeUpgrade <= (getCommittedTxnIdValue(qjCluster))));
            // NN0 should come up in the active state when given the -upgrade option,
            // so no need to transition it to active.
            Assert.assertTrue(fs.mkdirs(new Path("/foo2")));
            // Restart NN0 without the -upgrade flag, to make sure that works.
            cluster.getNameNodeInfos()[0].setStartOpt(REGULAR);
            cluster.restartNameNode(0, false);
            // Make sure we can still do FS ops after upgrading.
            cluster.transitionToActive(0);
            Assert.assertTrue(fs.mkdirs(new Path("/foo3")));
            Assert.assertTrue(((getCommittedTxnIdValue(qjCluster)) > cidBeforeUpgrade));
            // Now bootstrap the standby with the upgraded info.
            int rc = BootstrapStandby.run(new String[]{ "-force" }, cluster.getConfiguration(1));
            Assert.assertEquals(0, rc);
            // Now restart NN1 and make sure that we can do ops against that as well.
            cluster.restartNameNode(1);
            cluster.transitionToStandby(0);
            cluster.transitionToActive(1);
            Assert.assertTrue(fs.mkdirs(new Path("/foo4")));
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (qjCluster != null) {
                qjCluster.shutdown();
            }
        }
    }

    @Test
    public void testFinalizeWithJournalNodes() throws IOException, URISyntaxException {
        MiniQJMHACluster qjCluster = null;
        FileSystem fs = null;
        try {
            MiniQJMHACluster.Builder builder = new MiniQJMHACluster.Builder(conf);
            builder.getDfsBuilder().numDataNodes(0);
            qjCluster = builder.build();
            MiniDFSCluster cluster = qjCluster.getDfsCluster();
            // No upgrade is in progress at the moment.
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, false);
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            // Transition NN0 to active and do some FS ops.
            cluster.transitionToActive(0);
            fs = HATestUtil.configureFailoverFs(cluster, conf);
            Assert.assertTrue(fs.mkdirs(new Path("/foo1")));
            final long cidBeforeUpgrade = getCommittedTxnIdValue(qjCluster);
            // Do the upgrade. Shut down NN1 and then restart NN0 with the upgrade
            // flag.
            cluster.shutdownNameNode(1);
            cluster.getNameNodeInfos()[0].setStartOpt(UPGRADE);
            cluster.restartNameNode(0, false);
            Assert.assertTrue((cidBeforeUpgrade <= (getCommittedTxnIdValue(qjCluster))));
            Assert.assertTrue(fs.mkdirs(new Path("/foo2")));
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, false);
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, true);
            // Now bootstrap the standby with the upgraded info.
            int rc = BootstrapStandby.run(new String[]{ "-force" }, cluster.getConfiguration(1));
            Assert.assertEquals(0, rc);
            cluster.restartNameNode(1);
            final long cidDuringUpgrade = getCommittedTxnIdValue(qjCluster);
            Assert.assertTrue((cidDuringUpgrade > cidBeforeUpgrade));
            runFinalizeCommand(cluster);
            Assert.assertEquals(cidDuringUpgrade, getCommittedTxnIdValue(qjCluster));
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (qjCluster != null) {
                qjCluster.shutdown();
            }
        }
    }

    /**
     * Make sure that even if the NN which initiated the upgrade is in the standby
     * state that we're allowed to finalize.
     */
    @Test
    public void testFinalizeFromSecondNameNodeWithJournalNodes() throws IOException, URISyntaxException {
        MiniQJMHACluster qjCluster = null;
        FileSystem fs = null;
        try {
            MiniQJMHACluster.Builder builder = new MiniQJMHACluster.Builder(conf);
            builder.getDfsBuilder().numDataNodes(0);
            qjCluster = builder.build();
            MiniDFSCluster cluster = qjCluster.getDfsCluster();
            // No upgrade is in progress at the moment.
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, false);
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            // Transition NN0 to active and do some FS ops.
            cluster.transitionToActive(0);
            fs = HATestUtil.configureFailoverFs(cluster, conf);
            Assert.assertTrue(fs.mkdirs(new Path("/foo1")));
            // Do the upgrade. Shut down NN1 and then restart NN0 with the upgrade
            // flag.
            cluster.shutdownNameNode(1);
            cluster.getNameNodeInfos()[0].setStartOpt(UPGRADE);
            cluster.restartNameNode(0, false);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, false);
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, true);
            // Now bootstrap the standby with the upgraded info.
            int rc = BootstrapStandby.run(new String[]{ "-force" }, cluster.getConfiguration(1));
            Assert.assertEquals(0, rc);
            cluster.restartNameNode(1);
            // Make the second NN (not the one that initiated the upgrade) active when
            // the finalize command is run.
            cluster.transitionToStandby(0);
            cluster.transitionToActive(1);
            runFinalizeCommand(cluster);
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (qjCluster != null) {
                qjCluster.shutdown();
            }
        }
    }

    /**
     * Make sure that an HA NN will start if a previous upgrade was in progress.
     */
    @Test
    public void testStartingWithUpgradeInProgressSucceeds() throws Exception {
        MiniDFSCluster cluster = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
            // Simulate an upgrade having started.
            for (int i = 0; i < 2; i++) {
                for (URI uri : cluster.getNameDirs(i)) {
                    File prevTmp = new File(new File(uri), Storage.STORAGE_TMP_PREVIOUS);
                    TestDFSUpgradeWithHA.LOG.info(("creating previous tmp dir: " + prevTmp));
                    Assert.assertTrue(prevTmp.mkdirs());
                }
            }
            cluster.restartNameNodes();
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Test rollback with NFS shared dir.
     */
    @Test
    public void testRollbackWithNfs() throws Exception {
        MiniDFSCluster cluster = null;
        FileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
            File sharedDir = new File(cluster.getSharedEditsDir(0, 1));
            // No upgrade is in progress at the moment.
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, false);
            // Transition NN0 to active and do some FS ops.
            cluster.transitionToActive(0);
            fs = HATestUtil.configureFailoverFs(cluster, conf);
            Assert.assertTrue(fs.mkdirs(new Path("/foo1")));
            // Do the upgrade. Shut down NN1 and then restart NN0 with the upgrade
            // flag.
            cluster.shutdownNameNode(1);
            cluster.getNameNodeInfos()[0].setStartOpt(UPGRADE);
            cluster.restartNameNode(0, false);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, false);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, true);
            // NN0 should come up in the active state when given the -upgrade option,
            // so no need to transition it to active.
            Assert.assertTrue(fs.mkdirs(new Path("/foo2")));
            // Now bootstrap the standby with the upgraded info.
            int rc = BootstrapStandby.run(new String[]{ "-force" }, cluster.getConfiguration(1));
            Assert.assertEquals(0, rc);
            cluster.restartNameNode(1);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, true);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, true);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            // Now shut down the cluster and do the rollback.
            Collection<URI> nn1NameDirs = cluster.getNameDirs(0);
            cluster.shutdown();
            conf.setStrings(DFS_NAMENODE_NAME_DIR_KEY, Joiner.on(",").join(nn1NameDirs));
            NameNode.doRollback(conf, false);
            // The rollback operation should have rolled back the first NN's local
            // dirs, and the shared dir, but not the other NN's dirs. Those have to be
            // done by bootstrapping the standby.
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, false);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, false);
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testRollbackWithJournalNodes() throws IOException, URISyntaxException {
        MiniQJMHACluster qjCluster = null;
        FileSystem fs = null;
        try {
            MiniQJMHACluster.Builder builder = new MiniQJMHACluster.Builder(conf);
            builder.getDfsBuilder().numDataNodes(0);
            qjCluster = builder.build();
            MiniDFSCluster cluster = qjCluster.getDfsCluster();
            // No upgrade is in progress at the moment.
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, false);
            // Transition NN0 to active and do some FS ops.
            cluster.transitionToActive(0);
            fs = HATestUtil.configureFailoverFs(cluster, conf);
            Assert.assertTrue(fs.mkdirs(new Path("/foo1")));
            final long cidBeforeUpgrade = getCommittedTxnIdValue(qjCluster);
            // Do the upgrade. Shut down NN1 and then restart NN0 with the upgrade
            // flag.
            cluster.shutdownNameNode(1);
            cluster.getNameNodeInfos()[0].setStartOpt(UPGRADE);
            cluster.restartNameNode(0, false);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, false);
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, true);
            // NN0 should come up in the active state when given the -upgrade option,
            // so no need to transition it to active.
            Assert.assertTrue(fs.mkdirs(new Path("/foo2")));
            final long cidDuringUpgrade = getCommittedTxnIdValue(qjCluster);
            Assert.assertTrue((cidDuringUpgrade > cidBeforeUpgrade));
            // Now bootstrap the standby with the upgraded info.
            int rc = BootstrapStandby.run(new String[]{ "-force" }, cluster.getConfiguration(1));
            Assert.assertEquals(0, rc);
            cluster.restartNameNode(1);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, true);
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, true);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            // Shut down the NNs, but deliberately leave the JNs up and running.
            Collection<URI> nn1NameDirs = cluster.getNameDirs(0);
            cluster.shutdown();
            conf.setStrings(DFS_NAMENODE_NAME_DIR_KEY, Joiner.on(",").join(nn1NameDirs));
            NameNode.doRollback(conf, false);
            final long cidAfterRollback = getCommittedTxnIdValue(qjCluster);
            Assert.assertTrue((cidBeforeUpgrade < cidAfterRollback));
            // make sure the committedTxnId has been reset correctly after rollback
            Assert.assertTrue((cidDuringUpgrade > cidAfterRollback));
            // The rollback operation should have rolled back the first NN's local
            // dirs, and the shared dir, but not the other NN's dirs. Those have to be
            // done by bootstrapping the standby.
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, false);
            TestDFSUpgradeWithHA.checkJnPreviousDirExistence(qjCluster, false);
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (qjCluster != null) {
                qjCluster.shutdown();
            }
        }
    }

    /**
     * Make sure that starting a second NN with the -upgrade flag fails if the
     * other NN has already done that.
     */
    @Test
    public void testCannotUpgradeSecondNameNode() throws IOException, URISyntaxException {
        MiniDFSCluster cluster = null;
        FileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
            File sharedDir = new File(cluster.getSharedEditsDir(0, 1));
            // No upgrade is in progress at the moment.
            TestDFSUpgradeWithHA.checkClusterPreviousDirExistence(cluster, false);
            TestDFSUpgradeWithHA.assertCTimesEqual(cluster);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, false);
            // Transition NN0 to active and do some FS ops.
            cluster.transitionToActive(0);
            fs = HATestUtil.configureFailoverFs(cluster, conf);
            Assert.assertTrue(fs.mkdirs(new Path("/foo1")));
            // Do the upgrade. Shut down NN1 and then restart NN0 with the upgrade
            // flag.
            cluster.shutdownNameNode(1);
            cluster.getNameNodeInfos()[0].setStartOpt(UPGRADE);
            cluster.restartNameNode(0, false);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 0, true);
            TestDFSUpgradeWithHA.checkNnPreviousDirExistence(cluster, 1, false);
            TestDFSUpgradeWithHA.checkPreviousDirExistence(sharedDir, true);
            // NN0 should come up in the active state when given the -upgrade option,
            // so no need to transition it to active.
            Assert.assertTrue(fs.mkdirs(new Path("/foo2")));
            // Restart NN0 without the -upgrade flag, to make sure that works.
            cluster.getNameNodeInfos()[0].setStartOpt(REGULAR);
            cluster.restartNameNode(0, false);
            // Make sure we can still do FS ops after upgrading.
            cluster.transitionToActive(0);
            Assert.assertTrue(fs.mkdirs(new Path("/foo3")));
            // Make sure that starting the second NN with the -upgrade flag fails.
            cluster.getNameNodeInfos()[1].setStartOpt(UPGRADE);
            try {
                cluster.restartNameNode(1, false);
                Assert.fail("Should not have been able to start second NN with -upgrade");
            } catch (IOException ioe) {
                GenericTestUtils.assertExceptionContains("It looks like the shared log is already being upgraded", ioe);
            }
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

