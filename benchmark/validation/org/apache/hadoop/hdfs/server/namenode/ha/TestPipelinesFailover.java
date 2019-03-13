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
import DFSConfigKeys.DFS_PERMISSIONS_ENABLED_KEY;
import HdfsClientConfigKeys.Failover.SLEEPTIME_MAX_KEY;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.AppendTestUtil;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeDescriptor;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.InternalDataNodeTestUtils;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.retry.RetryInvocationHandler;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.DelayAnswer;
import org.apache.hadoop.test.MultithreadedTestUtil.RepeatingTestThread;
import org.apache.hadoop.test.MultithreadedTestUtil.TestContext;
import org.apache.hadoop.util.Shell.ShellCommandExecutor;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static org.apache.log4j.Level.ALL;


/**
 * Test cases regarding pipeline recovery during NN failover.
 */
public class TestPipelinesFailover {
    static {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(RetryInvocationHandler.class), Level.DEBUG);
        DFSTestUtil.setNameNodeLogLevel(ALL);
    }

    protected static final Logger LOG = LoggerFactory.getLogger(TestPipelinesFailover.class);

    private static final Path TEST_PATH = new Path("/test-file");

    private static final int BLOCK_SIZE = 4096;

    private static final int BLOCK_AND_A_HALF = ((TestPipelinesFailover.BLOCK_SIZE) * 3) / 2;

    private static final int STRESS_NUM_THREADS = 25;

    private static final int STRESS_RUNTIME = 40000;

    private static final int NN_COUNT = 3;

    private static final long FAILOVER_SEED = System.currentTimeMillis();

    private static final Random failoverRandom = new Random(TestPipelinesFailover.FAILOVER_SEED);

    static {
        // log the failover seed so we can reproduce the test exactly
        TestPipelinesFailover.LOG.info((("Using random seed: " + (TestPipelinesFailover.FAILOVER_SEED)) + " for selecting active target NN during failover"));
    }

    enum TestScenario {

        GRACEFUL_FAILOVER() {
            @Override
            void run(MiniDFSCluster cluster, int previousActive, int activeIndex) throws IOException {
                cluster.transitionToStandby(previousActive);
                cluster.transitionToActive(activeIndex);
            }
        },
        ORIGINAL_ACTIVE_CRASHED() {
            @Override
            void run(MiniDFSCluster cluster, int previousActive, int activeIndex) throws IOException {
                cluster.restartNameNode(previousActive);
                cluster.transitionToActive(activeIndex);
            }
        };
        abstract void run(MiniDFSCluster cluster, int previousActive, int activeIndex) throws IOException;
    }

    enum MethodToTestIdempotence {

        ALLOCATE_BLOCK,
        COMPLETE_FILE;}

    /**
     * Tests continuing a write pipeline over a failover.
     */
    @Test(timeout = 30000)
    public void testWriteOverGracefulFailover() throws Exception {
        doWriteOverFailoverTest(TestPipelinesFailover.TestScenario.GRACEFUL_FAILOVER, TestPipelinesFailover.MethodToTestIdempotence.ALLOCATE_BLOCK);
    }

    @Test(timeout = 30000)
    public void testAllocateBlockAfterCrashFailover() throws Exception {
        doWriteOverFailoverTest(TestPipelinesFailover.TestScenario.ORIGINAL_ACTIVE_CRASHED, TestPipelinesFailover.MethodToTestIdempotence.ALLOCATE_BLOCK);
    }

    @Test(timeout = 30000)
    public void testCompleteFileAfterCrashFailover() throws Exception {
        doWriteOverFailoverTest(TestPipelinesFailover.TestScenario.ORIGINAL_ACTIVE_CRASHED, TestPipelinesFailover.MethodToTestIdempotence.COMPLETE_FILE);
    }

    /**
     * Tests continuing a write pipeline over a failover when a DN fails
     * after the failover - ensures that updating the pipeline succeeds
     * even when the pipeline was constructed on a different NN.
     */
    @Test(timeout = 30000)
    public void testWriteOverGracefulFailoverWithDnFail() throws Exception {
        doTestWriteOverFailoverWithDnFail(TestPipelinesFailover.TestScenario.GRACEFUL_FAILOVER);
    }

    @Test(timeout = 30000)
    public void testWriteOverCrashFailoverWithDnFail() throws Exception {
        doTestWriteOverFailoverWithDnFail(TestPipelinesFailover.TestScenario.ORIGINAL_ACTIVE_CRASHED);
    }

    /**
     * Tests lease recovery if a client crashes. This approximates the
     * use case of HBase WALs being recovered after a NN failover.
     */
    @Test(timeout = 30000)
    public void testLeaseRecoveryAfterFailover() throws Exception {
        final Configuration conf = new Configuration();
        // Disable permissions so that another user can recover the lease.
        conf.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, false);
        conf.setInt(DFS_BLOCK_SIZE_KEY, TestPipelinesFailover.BLOCK_SIZE);
        FSDataOutputStream stm = null;
        final MiniDFSCluster cluster = newMiniCluster(conf, 3);
        try {
            cluster.waitActive();
            cluster.transitionToActive(0);
            cluster.setBlockRecoveryTimeout(TimeUnit.SECONDS.toMillis(1));
            Thread.sleep(500);
            TestPipelinesFailover.LOG.info("Starting with NN 0 active");
            FileSystem fs = HATestUtil.configureFailoverFs(cluster, conf);
            stm = fs.create(TestPipelinesFailover.TEST_PATH);
            // write a block and a half
            AppendTestUtil.write(stm, 0, TestPipelinesFailover.BLOCK_AND_A_HALF);
            stm.hflush();
            TestPipelinesFailover.LOG.info("Failing over to NN 1");
            cluster.transitionToStandby(0);
            cluster.transitionToActive(1);
            Assert.assertTrue(fs.exists(TestPipelinesFailover.TEST_PATH));
            FileSystem fsOtherUser = createFsAsOtherUser(cluster, conf);
            TestPipelinesFailover.loopRecoverLease(fsOtherUser, TestPipelinesFailover.TEST_PATH);
            AppendTestUtil.check(fs, TestPipelinesFailover.TEST_PATH, TestPipelinesFailover.BLOCK_AND_A_HALF);
            // Fail back to ensure that the block locations weren't lost on the
            // original node.
            cluster.transitionToStandby(1);
            cluster.transitionToActive(0);
            AppendTestUtil.check(fs, TestPipelinesFailover.TEST_PATH, TestPipelinesFailover.BLOCK_AND_A_HALF);
        } finally {
            IOUtils.closeStream(stm);
            cluster.shutdown();
        }
    }

    /**
     * Test the scenario where the NN fails over after issuing a block
     * synchronization request, but before it is committed. The
     * DN running the recovery should then fail to commit the synchronization
     * and a later retry will succeed.
     */
    @Test(timeout = 30000)
    public void testFailoverRightBeforeCommitSynchronization() throws Exception {
        final Configuration conf = new Configuration();
        // Disable permissions so that another user can recover the lease.
        conf.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, false);
        conf.setInt(DFS_BLOCK_SIZE_KEY, TestPipelinesFailover.BLOCK_SIZE);
        FSDataOutputStream stm = null;
        final MiniDFSCluster cluster = newMiniCluster(conf, 3);
        try {
            cluster.waitActive();
            cluster.transitionToActive(0);
            Thread.sleep(500);
            TestPipelinesFailover.LOG.info("Starting with NN 0 active");
            FileSystem fs = HATestUtil.configureFailoverFs(cluster, conf);
            stm = fs.create(TestPipelinesFailover.TEST_PATH);
            // write a half block
            AppendTestUtil.write(stm, 0, ((TestPipelinesFailover.BLOCK_SIZE) / 2));
            stm.hflush();
            // Look into the block manager on the active node for the block
            // under construction.
            NameNode nn0 = cluster.getNameNode(0);
            ExtendedBlock blk = DFSTestUtil.getFirstBlock(fs, TestPipelinesFailover.TEST_PATH);
            DatanodeDescriptor expectedPrimary = DFSTestUtil.getExpectedPrimaryNode(nn0, blk);
            TestPipelinesFailover.LOG.info(("Expecting block recovery to be triggered on DN " + expectedPrimary));
            // Find the corresponding DN daemon, and spy on its connection to the
            // active.
            DataNode primaryDN = cluster.getDataNode(expectedPrimary.getIpcPort());
            DatanodeProtocolClientSideTranslatorPB nnSpy = InternalDataNodeTestUtils.spyOnBposToNN(primaryDN, nn0);
            // Delay the commitBlockSynchronization call
            DelayAnswer delayer = new DelayAnswer(TestPipelinesFailover.LOG);
            // new genstamp
            // new length
            // close file
            // delete block
            // new targets
            Mockito.doAnswer(delayer).when(nnSpy).commitBlockSynchronization(Mockito.eq(blk), Mockito.anyLong(), Mockito.anyLong(), Mockito.eq(true), Mockito.eq(false), Mockito.any(), Mockito.any());// new target storages

            DistributedFileSystem fsOtherUser = createFsAsOtherUser(cluster, conf);
            Assert.assertFalse(fsOtherUser.recoverLease(TestPipelinesFailover.TEST_PATH));
            TestPipelinesFailover.LOG.info("Waiting for commitBlockSynchronization call from primary");
            delayer.waitForCall();
            TestPipelinesFailover.LOG.info("Failing over to NN 1");
            cluster.transitionToStandby(0);
            cluster.transitionToActive(1);
            // Let the commitBlockSynchronization call go through, and check that
            // it failed with the correct exception.
            delayer.proceed();
            delayer.waitForResult();
            Throwable t = delayer.getThrown();
            if (t == null) {
                Assert.fail("commitBlockSynchronization call did not fail on standby");
            }
            GenericTestUtils.assertExceptionContains("Operation category WRITE is not supported", t);
            // Now, if we try again to recover the block, it should succeed on the new
            // active.
            TestPipelinesFailover.loopRecoverLease(fsOtherUser, TestPipelinesFailover.TEST_PATH);
            AppendTestUtil.check(fs, TestPipelinesFailover.TEST_PATH, ((TestPipelinesFailover.BLOCK_SIZE) / 2));
        } finally {
            IOUtils.closeStream(stm);
            cluster.shutdown();
        }
    }

    /**
     * Stress test for pipeline/lease recovery. Starts a number of
     * threads, each of which creates a file and has another client
     * break the lease. While these threads run, failover proceeds
     * back and forth between two namenodes.
     */
    @Test(timeout = (TestPipelinesFailover.STRESS_RUNTIME) * 3)
    public void testPipelineRecoveryStress() throws Exception {
        // The following section of code is to help debug HDFS-6694 about
        // this test that fails from time to time due to "too many open files".
        // 
        TestPipelinesFailover.LOG.info("HDFS-6694 Debug Data BEGIN");
        String[][] scmds = new String[][]{ new String[]{ "/bin/sh", "-c", "ulimit -a" }, new String[]{ "hostname" }, new String[]{ "ifconfig", "-a" } };
        for (String[] scmd : scmds) {
            String scmd_str = StringUtils.join(" ", scmd);
            try {
                ShellCommandExecutor sce = new ShellCommandExecutor(scmd);
                sce.execute();
                TestPipelinesFailover.LOG.info(((("'" + scmd_str) + "\' output:\n") + (sce.getOutput())));
            } catch (IOException e) {
                TestPipelinesFailover.LOG.warn((("Error when running '" + scmd_str) + "'"), e);
            }
        }
        TestPipelinesFailover.LOG.info("HDFS-6694 Debug Data END");
        HAStressTestHarness harness = new HAStressTestHarness();
        // Disable permissions so that another user can recover the lease.
        harness.conf.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, false);
        // This test triggers rapid NN failovers.  The client retry policy uses an
        // exponential backoff.  This can quickly lead to long sleep times and even
        // timeout the whole test.  Cap the sleep time at 1s to prevent this.
        harness.conf.setInt(SLEEPTIME_MAX_KEY, 1000);
        final MiniDFSCluster cluster = harness.startCluster();
        try {
            cluster.waitActive();
            cluster.transitionToActive(0);
            FileSystem fs = harness.getFailoverFs();
            DistributedFileSystem fsAsOtherUser = createFsAsOtherUser(cluster, harness.conf);
            TestContext testers = new TestContext();
            for (int i = 0; i < (TestPipelinesFailover.STRESS_NUM_THREADS); i++) {
                Path p = new Path(("/test-" + i));
                testers.addThread(new TestPipelinesFailover.PipelineTestThread(testers, fs, fsAsOtherUser, p));
            }
            // Start a separate thread which will make sure that replication
            // happens quickly by triggering deletion reports and replication
            // work calculation frequently.
            harness.addReplicationTriggerThread(500);
            harness.addFailoverThread(5000);
            harness.startThreads();
            testers.startThreads();
            testers.waitFor(TestPipelinesFailover.STRESS_RUNTIME);
            testers.stop();
            harness.stopThreads();
        } finally {
            System.err.println("===========================\n\n\n\n");
            harness.shutdown();
        }
    }

    /**
     * Test thread which creates a file, has another fake user recover
     * the lease on the file, and then ensures that the file's contents
     * are properly readable. If any of these steps fails, propagates
     * an exception back to the test context, causing the test case
     * to fail.
     */
    private static class PipelineTestThread extends RepeatingTestThread {
        private final FileSystem fs;

        private final FileSystem fsOtherUser;

        private final Path path;

        public PipelineTestThread(TestContext ctx, FileSystem fs, FileSystem fsOtherUser, Path p) {
            super(ctx);
            this.fs = fs;
            this.fsOtherUser = fsOtherUser;
            this.path = p;
        }

        @Override
        public void doAnAction() throws Exception {
            FSDataOutputStream stm = fs.create(path, true);
            try {
                AppendTestUtil.write(stm, 0, 100);
                stm.hflush();
                TestPipelinesFailover.loopRecoverLease(fsOtherUser, path);
                AppendTestUtil.check(fs, path, 100);
            } finally {
                try {
                    stm.close();
                } catch (IOException e) {
                    // should expect this since we lost the lease
                }
            }
        }

        @Override
        public String toString() {
            return "Pipeline test thread for " + (path);
        }
    }
}

