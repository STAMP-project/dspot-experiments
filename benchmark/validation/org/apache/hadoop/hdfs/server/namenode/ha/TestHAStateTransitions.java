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


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_HA_TAILEDITS_PERIOD_KEY;
import DFSConfigKeys.DFS_NAMENODE_DELEGATION_KEY_UPDATE_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY;
import DFSConfigKeys.DFS_NAMENODE_PATH_BASED_CACHE_REFRESH_INTERVAL_MS;
import DFSConfigKeys.DFS_NAMENODE_SAFEMODE_EXTENSION_KEY;
import EditLogTailer.LOG;
import com.google.common.util.concurrent.Uninterruptibles;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.ha.HAServiceProtocol.RequestSource;
import org.apache.hadoop.ha.HAServiceProtocol.StateChangeRequestInfo;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MultithreadedTestUtil.RepeatingTestThread;
import org.apache.hadoop.test.MultithreadedTestUtil.TestContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * Tests state transition from active->standby, and manual failover
 * and failback between two namenodes.
 */
public class TestHAStateTransitions {
    protected static final Logger LOG = LoggerFactory.getLogger(TestStandbyIsHot.class);

    private static final Path TEST_DIR = new Path("/test");

    private static final Path TEST_FILE_PATH = new Path(TestHAStateTransitions.TEST_DIR, "foo");

    private static final String TEST_FILE_STR = TestHAStateTransitions.TEST_FILE_PATH.toUri().getPath();

    private static final String TEST_FILE_DATA = "Hello state transitioning world";

    private static final StateChangeRequestInfo REQ_INFO = new StateChangeRequestInfo(RequestSource.REQUEST_BY_USER_FORCED);

    static {
        GenericTestUtils.setLogLevel(EditLogTailer.LOG, Level.TRACE);
    }

    /**
     * Test which takes a single node and flip flops between
     * active and standby mode, making sure it doesn't
     * double-play any edits.
     */
    @Test(timeout = 300000)
    public void testTransitionActiveToStandby() throws Exception {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(1).build();
        try {
            cluster.waitActive();
            cluster.transitionToActive(0);
            FileSystem fs = cluster.getFileSystem(0);
            fs.mkdirs(TestHAStateTransitions.TEST_DIR);
            cluster.transitionToStandby(0);
            try {
                fs.mkdirs(new Path("/x"));
                Assert.fail("Didn't throw trying to mutate FS in standby state");
            } catch (Throwable t) {
                GenericTestUtils.assertExceptionContains("Operation category WRITE is not supported", t);
            }
            cluster.transitionToActive(0);
            // Create a file, then delete the whole directory recursively.
            DFSTestUtil.createFile(fs, new Path(TestHAStateTransitions.TEST_DIR, "foo"), 10, ((short) (1)), 1L);
            fs.delete(TestHAStateTransitions.TEST_DIR, true);
            // Now if the standby tries to replay the last segment that it just
            // wrote as active, it would fail since it's trying to create a file
            // in a non-existent directory.
            cluster.transitionToStandby(0);
            cluster.transitionToActive(0);
            Assert.assertFalse(fs.exists(TestHAStateTransitions.TEST_DIR));
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test that transitioning a service to the state that it is already
     * in is a nop, specifically, an exception is not thrown.
     */
    @Test(timeout = 300000)
    public void testTransitionToCurrentStateIsANop() throws Exception {
        Configuration conf = new Configuration();
        conf.setLong(DFS_NAMENODE_PATH_BASED_CACHE_REFRESH_INTERVAL_MS, 1L);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(1).build();
        LinkedList<Thread> crmThreads = new LinkedList<Thread>();
        try {
            cluster.waitActive();
            addCrmThreads(cluster, crmThreads);
            cluster.transitionToActive(0);
            addCrmThreads(cluster, crmThreads);
            cluster.transitionToActive(0);
            addCrmThreads(cluster, crmThreads);
            cluster.transitionToStandby(0);
            addCrmThreads(cluster, crmThreads);
            cluster.transitionToStandby(0);
            addCrmThreads(cluster, crmThreads);
        } finally {
            cluster.shutdown();
        }
        // Verify that all cacheReplicationMonitor threads shut down
        for (Thread thread : crmThreads) {
            Uninterruptibles.joinUninterruptibly(thread);
        }
    }

    /**
     * Tests manual failover back and forth between two NameNodes.
     */
    @Test(timeout = 300000)
    public void testManualFailoverAndFailback() throws Exception {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(1).build();
        try {
            cluster.waitActive();
            // test the only namespace
            testManualFailoverFailback(cluster, conf, 0);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Regression test for HDFS-2693: when doing state transitions, we need to
     * lock the FSNamesystem so that we don't end up doing any writes while it's
     * "in between" states.
     * This test case starts up several client threads which do mutation operations
     * while flipping a NN back and forth from active to standby.
     */
    @Test(timeout = 120000)
    public void testTransitionSynchronization() throws Exception {
        Configuration conf = new Configuration();
        final MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
        try {
            cluster.waitActive();
            ReentrantReadWriteLock spyLock = NameNodeAdapter.spyOnFsLock(cluster.getNameNode(0).getNamesystem());
            Mockito.doAnswer(new GenericTestUtils.SleepAnswer(50)).when(spyLock).writeLock();
            final FileSystem fs = HATestUtil.configureFailoverFs(cluster, conf);
            TestContext ctx = new TestContext();
            for (int i = 0; i < 50; i++) {
                final int finalI = i;
                ctx.addThread(new RepeatingTestThread(ctx) {
                    @Override
                    public void doAnAction() throws Exception {
                        Path p = new Path(("/test-" + finalI));
                        fs.mkdirs(p);
                        fs.delete(p, true);
                    }
                });
            }
            ctx.addThread(new RepeatingTestThread(ctx) {
                @Override
                public void doAnAction() throws Exception {
                    cluster.transitionToStandby(0);
                    Thread.sleep(50);
                    cluster.transitionToActive(0);
                }
            });
            ctx.startThreads();
            ctx.waitFor(20000);
            ctx.stop();
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test for HDFS-2812. Since lease renewals go from the client
     * only to the active NN, the SBN will have out-of-date lease
     * info when it becomes active. We need to make sure we don't
     * accidentally mark the leases as expired when the failover
     * proceeds.
     */
    @Test(timeout = 120000)
    public void testLeasesRenewedOnTransition() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(1).build();
        FSDataOutputStream stm = null;
        FileSystem fs = HATestUtil.configureFailoverFs(cluster, conf);
        NameNode nn0 = cluster.getNameNode(0);
        NameNode nn1 = cluster.getNameNode(1);
        try {
            cluster.waitActive();
            cluster.transitionToActive(0);
            TestHAStateTransitions.LOG.info("Starting with NN 0 active");
            stm = fs.create(TestHAStateTransitions.TEST_FILE_PATH);
            long nn0t0 = NameNodeAdapter.getLeaseRenewalTime(nn0, TestHAStateTransitions.TEST_FILE_STR);
            Assert.assertTrue((nn0t0 > 0));
            long nn1t0 = NameNodeAdapter.getLeaseRenewalTime(nn1, TestHAStateTransitions.TEST_FILE_STR);
            Assert.assertEquals("Lease should not yet exist on nn1", (-1), nn1t0);
            Thread.sleep(5);// make sure time advances!

            HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
            long nn1t1 = NameNodeAdapter.getLeaseRenewalTime(nn1, TestHAStateTransitions.TEST_FILE_STR);
            Assert.assertTrue(("Lease should have been created on standby. Time was: " + nn1t1), (nn1t1 > nn0t0));
            Thread.sleep(5);// make sure time advances!

            TestHAStateTransitions.LOG.info("Failing over to NN 1");
            cluster.transitionToStandby(0);
            cluster.transitionToActive(1);
            long nn1t2 = NameNodeAdapter.getLeaseRenewalTime(nn1, TestHAStateTransitions.TEST_FILE_STR);
            Assert.assertTrue("Lease should have been renewed by failover process", (nn1t2 > nn1t1));
        } finally {
            IOUtils.closeStream(stm);
            cluster.shutdown();
        }
    }

    /**
     * Test that delegation tokens continue to work after the failover.
     */
    @Test(timeout = 300000)
    public void testDelegationTokensAfterFailover() throws IOException {
        Configuration conf = new Configuration();
        conf.setBoolean(DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY, true);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
        try {
            cluster.waitActive();
            cluster.transitionToActive(0);
            NameNode nn1 = cluster.getNameNode(0);
            NameNode nn2 = cluster.getNameNode(1);
            String renewer = UserGroupInformation.getLoginUser().getUserName();
            Token<DelegationTokenIdentifier> token = nn1.getRpcServer().getDelegationToken(new Text(renewer));
            TestHAStateTransitions.LOG.info("Failing over to NN 1");
            cluster.transitionToStandby(0);
            cluster.transitionToActive(1);
            nn2.getRpcServer().renewDelegationToken(token);
            nn2.getRpcServer().cancelDelegationToken(token);
            token = nn2.getRpcServer().getDelegationToken(new Text(renewer));
            Assert.assertTrue((token != null));
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Tests manual failover back and forth between two NameNodes
     * for federation cluster with two namespaces.
     */
    @Test(timeout = 300000)
    public void testManualFailoverFailbackFederationHA() throws Exception {
        Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHAFederatedTopology(2)).numDataNodes(1).build();
        try {
            cluster.waitActive();
            // test for namespace 0
            testManualFailoverFailback(cluster, conf, 0);
            // test for namespace 1
            testManualFailoverFailback(cluster, conf, 1);
        } finally {
            cluster.shutdown();
        }
    }

    @Test(timeout = 300000)
    public void testFailoverWithEmptyInProgressEditLog() throws Exception {
        TestHAStateTransitions.testFailoverAfterCrashDuringLogRoll(false);
    }

    @Test(timeout = 300000)
    public void testFailoverWithEmptyInProgressEditLogWithHeader() throws Exception {
        TestHAStateTransitions.testFailoverAfterCrashDuringLogRoll(true);
    }

    /**
     * The secret manager needs to start/stop - the invariant should be that
     * the secret manager runs if and only if the NN is active and not in
     * safe mode. As a state diagram, we need to test all of the following
     * transitions to make sure the secret manager is started when we transition
     * into state 4, but none of the others.
     * <pre>
     *         SafeMode     Not SafeMode
     * Standby   1 <------> 2
     *           ^          ^
     *           |          |
     *           v          v
     * Active    3 <------> 4
     * </pre>
     */
    @Test(timeout = 60000)
    public void testSecretManagerState() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY, true);
        conf.setInt(DFS_NAMENODE_DELEGATION_KEY_UPDATE_INTERVAL_KEY, 50);
        conf.setInt(DFS_BLOCK_SIZE_KEY, 1024);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(1).waitSafeMode(false).build();
        try {
            cluster.transitionToActive(0);
            DFSTestUtil.createFile(cluster.getFileSystem(0), TestHAStateTransitions.TEST_FILE_PATH, 6000, ((short) (1)), 1L);
            cluster.getConfiguration(0).setInt(DFS_NAMENODE_SAFEMODE_EXTENSION_KEY, 60000);
            cluster.restartNameNode(0);
            NameNode nn = cluster.getNameNode(0);
            TestHAStateTransitions.banner("Started in state 1.");
            Assert.assertTrue(nn.isStandbyState());
            Assert.assertTrue(nn.isInSafeMode());
            Assert.assertFalse(isDTRunning(nn));
            TestHAStateTransitions.banner("Transition 1->2. Should not start secret manager");
            NameNodeAdapter.leaveSafeMode(nn);
            Assert.assertTrue(nn.isStandbyState());
            Assert.assertFalse(nn.isInSafeMode());
            Assert.assertFalse(isDTRunning(nn));
            TestHAStateTransitions.banner("Transition 2->1. Should not start secret manager.");
            NameNodeAdapter.enterSafeMode(nn, false);
            Assert.assertTrue(nn.isStandbyState());
            Assert.assertTrue(nn.isInSafeMode());
            Assert.assertFalse(isDTRunning(nn));
            TestHAStateTransitions.banner("Transition 1->3. Should not start secret manager.");
            nn.getRpcServer().transitionToActive(TestHAStateTransitions.REQ_INFO);
            Assert.assertFalse(nn.isStandbyState());
            Assert.assertTrue(nn.isInSafeMode());
            Assert.assertFalse(isDTRunning(nn));
            TestHAStateTransitions.banner("Transition 3->1. Should not start secret manager.");
            nn.getRpcServer().transitionToStandby(TestHAStateTransitions.REQ_INFO);
            Assert.assertTrue(nn.isStandbyState());
            Assert.assertTrue(nn.isInSafeMode());
            Assert.assertFalse(isDTRunning(nn));
            TestHAStateTransitions.banner("Transition 1->3->4. Should start secret manager.");
            nn.getRpcServer().transitionToActive(TestHAStateTransitions.REQ_INFO);
            NameNodeAdapter.leaveSafeMode(nn);
            Assert.assertFalse(nn.isStandbyState());
            Assert.assertFalse(nn.isInSafeMode());
            Assert.assertTrue(isDTRunning(nn));
            TestHAStateTransitions.banner("Transition 4->3. Should stop secret manager");
            NameNodeAdapter.enterSafeMode(nn, false);
            Assert.assertFalse(nn.isStandbyState());
            Assert.assertTrue(nn.isInSafeMode());
            Assert.assertFalse(isDTRunning(nn));
            TestHAStateTransitions.banner("Transition 3->4. Should start secret manager");
            NameNodeAdapter.leaveSafeMode(nn);
            Assert.assertFalse(nn.isStandbyState());
            Assert.assertFalse(nn.isInSafeMode());
            Assert.assertTrue(isDTRunning(nn));
            for (int i = 0; i < 20; i++) {
                // Loop the last check to suss out races.
                TestHAStateTransitions.banner("Transition 4->2. Should stop secret manager.");
                nn.getRpcServer().transitionToStandby(TestHAStateTransitions.REQ_INFO);
                Assert.assertTrue(nn.isStandbyState());
                Assert.assertFalse(nn.isInSafeMode());
                Assert.assertFalse(isDTRunning(nn));
                TestHAStateTransitions.banner("Transition 2->4. Should start secret manager");
                nn.getRpcServer().transitionToActive(TestHAStateTransitions.REQ_INFO);
                Assert.assertFalse(nn.isStandbyState());
                Assert.assertFalse(nn.isInSafeMode());
                Assert.assertTrue(isDTRunning(nn));
            }
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * This test also serves to test
     * {@link HAUtil#getProxiesForAllNameNodesInNameservice(Configuration, String)} and
     * {@link DFSUtil#getRpcAddressesForNameserviceId(Configuration, String, String)}
     * by virtue of the fact that it wouldn't work properly if the proxies
     * returned were not for the correct NNs.
     */
    @Test(timeout = 300000)
    public void testIsAtLeastOneActive() throws Exception {
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(new HdfsConfiguration()).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
        try {
            Configuration conf = new HdfsConfiguration();
            HATestUtil.setFailoverConfigurations(cluster, conf);
            List<ClientProtocol> namenodes = HAUtil.getProxiesForAllNameNodesInNameservice(conf, HATestUtil.getLogicalHostname(cluster));
            Assert.assertEquals(2, namenodes.size());
            Assert.assertFalse(HAUtil.isAtLeastOneActive(namenodes));
            cluster.transitionToActive(0);
            Assert.assertTrue(HAUtil.isAtLeastOneActive(namenodes));
            cluster.transitionToStandby(0);
            Assert.assertFalse(HAUtil.isAtLeastOneActive(namenodes));
            cluster.transitionToActive(1);
            Assert.assertTrue(HAUtil.isAtLeastOneActive(namenodes));
            cluster.transitionToStandby(1);
            Assert.assertFalse(HAUtil.isAtLeastOneActive(namenodes));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

