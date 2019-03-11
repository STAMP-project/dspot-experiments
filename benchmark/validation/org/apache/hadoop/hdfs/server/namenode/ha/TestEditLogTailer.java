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


import DFSConfigKeys.DFS_HA_LOGROLL_PERIOD_KEY;
import DFSConfigKeys.DFS_HA_TAILEDITS_ALL_NAMESNODES_RETRY_KEY;
import DFSConfigKeys.DFS_HA_TAILEDITS_INPROGRESS_KEY;
import DFSConfigKeys.DFS_HA_TAILEDITS_PERIOD_KEY;
import DFSConfigKeys.DFS_HA_TAILEDITS_ROLLEDITS_TIMEOUT_KEY;
import EditLogTailer.DFS_HA_TAILEDITS_MAX_TXNS_PER_LOCK_KEY;
import FSEditLog.LOG;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.ha.ServiceFailedException;
import org.apache.hadoop.hdfs.HAUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.slf4j.event.Level;


@RunWith(Parameterized.class)
public class TestEditLogTailer {
    static {
        GenericTestUtils.setLogLevel(LOG, Level.DEBUG);
    }

    private static boolean useAsyncEditLog;

    public TestEditLogTailer(Boolean async) {
        TestEditLogTailer.useAsyncEditLog = async;
    }

    private static final String DIR_PREFIX = "/dir";

    private static final int DIRS_TO_MAKE = 20;

    static final long SLEEP_TIME = 1000;

    static final long NN_LAG_TIMEOUT = 10 * 1000;

    static {
        GenericTestUtils.setLogLevel(FSImage.LOG, Level.DEBUG);
        GenericTestUtils.setLogLevel(LOG, Level.DEBUG);
        GenericTestUtils.setLogLevel(EditLogTailer.LOG, Level.DEBUG);
    }

    @Test
    public void testTailer() throws IOException, InterruptedException, ServiceFailedException {
        Configuration conf = TestEditLogTailer.getConf();
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 0);
        conf.setInt(DFS_HA_TAILEDITS_ALL_NAMESNODES_RETRY_KEY, 100);
        conf.setLong(DFS_HA_TAILEDITS_MAX_TXNS_PER_LOCK_KEY, 3);
        HAUtil.setAllowStandbyReads(conf, true);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
        cluster.waitActive();
        cluster.transitionToActive(0);
        NameNode nn1 = cluster.getNameNode(0);
        NameNode nn2 = cluster.getNameNode(1);
        try {
            for (int i = 0; i < ((TestEditLogTailer.DIRS_TO_MAKE) / 2); i++) {
                NameNodeAdapter.mkdirs(nn1, TestEditLogTailer.getDirPath(i), new org.apache.hadoop.fs.permission.PermissionStatus("test", "test", new FsPermission(((short) (493)))), true);
            }
            HATestUtil.waitForStandbyToCatchUp(nn1, nn2);
            Assert.assertEquals("Inconsistent number of applied txns on Standby", nn1.getNamesystem().getEditLog().getLastWrittenTxId(), ((nn2.getNamesystem().getFSImage().getLastAppliedTxId()) + 1));
            for (int i = 0; i < ((TestEditLogTailer.DIRS_TO_MAKE) / 2); i++) {
                Assert.assertTrue(NameNodeAdapter.getFileInfo(nn2, TestEditLogTailer.getDirPath(i), false, false, false).isDirectory());
            }
            for (int i = (TestEditLogTailer.DIRS_TO_MAKE) / 2; i < (TestEditLogTailer.DIRS_TO_MAKE); i++) {
                NameNodeAdapter.mkdirs(nn1, TestEditLogTailer.getDirPath(i), new org.apache.hadoop.fs.permission.PermissionStatus("test", "test", new FsPermission(((short) (493)))), true);
            }
            HATestUtil.waitForStandbyToCatchUp(nn1, nn2);
            Assert.assertEquals("Inconsistent number of applied txns on Standby", nn1.getNamesystem().getEditLog().getLastWrittenTxId(), ((nn2.getNamesystem().getFSImage().getLastAppliedTxId()) + 1));
            for (int i = (TestEditLogTailer.DIRS_TO_MAKE) / 2; i < (TestEditLogTailer.DIRS_TO_MAKE); i++) {
                Assert.assertTrue(NameNodeAdapter.getFileInfo(nn2, TestEditLogTailer.getDirPath(i), false, false, false).isDirectory());
            }
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testNN0TriggersLogRolls() throws Exception {
        TestEditLogTailer.testStandbyTriggersLogRolls(0);
    }

    @Test
    public void testNN1TriggersLogRolls() throws Exception {
        TestEditLogTailer.testStandbyTriggersLogRolls(1);
    }

    @Test
    public void testNN2TriggersLogRolls() throws Exception {
        TestEditLogTailer.testStandbyTriggersLogRolls(2);
    }

    /* 1. when all NN become standby nn, standby NN execute to roll log,
    it will be failed.
    2. when one NN become active, standby NN roll log success.
     */
    @Test
    public void testTriggersLogRollsForAllStandbyNN() throws Exception {
        Configuration conf = TestEditLogTailer.getConf();
        // Roll every 1s
        conf.setInt(DFS_HA_LOGROLL_PERIOD_KEY, 1);
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
        conf.setInt(DFS_HA_TAILEDITS_ALL_NAMESNODES_RETRY_KEY, 100);
        MiniDFSCluster cluster = null;
        try {
            cluster = TestEditLogTailer.createMiniDFSCluster(conf, 3);
            cluster.transitionToStandby(0);
            cluster.transitionToStandby(1);
            cluster.transitionToStandby(2);
            try {
                TestEditLogTailer.waitForLogRollInSharedDir(cluster, 3);
                Assert.fail(("After all NN become Standby state, Standby NN should roll log, " + "but it will be failed"));
            } catch (TimeoutException ignore) {
            }
            cluster.transitionToActive(0);
            TestEditLogTailer.waitForLogRollInSharedDir(cluster, 3);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 20000)
    public void testRollEditTimeoutForActiveNN() throws IOException {
        Configuration conf = TestEditLogTailer.getConf();
        conf.setInt(DFS_HA_TAILEDITS_ROLLEDITS_TIMEOUT_KEY, 5);// 5s

        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
        conf.setInt(DFS_HA_TAILEDITS_ALL_NAMESNODES_RETRY_KEY, 100);
        HAUtil.setAllowStandbyReads(conf, true);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(0).build();
        cluster.waitActive();
        cluster.transitionToActive(0);
        try {
            EditLogTailer tailer = Mockito.spy(cluster.getNamesystem(1).getEditLogTailer());
            AtomicInteger flag = new AtomicInteger(0);
            // Return a slow roll edit process.
            Mockito.when(tailer.getNameNodeProxy()).thenReturn(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    Thread.sleep(30000);// sleep for 30 seconds.

                    Assert.assertTrue(Thread.currentThread().isInterrupted());
                    flag.addAndGet(1);
                    return null;
                }
            });
            tailer.triggerActiveLogRoll();
            Assert.assertEquals(0, flag.get());
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testRollEditLogIOExceptionForRemoteNN() throws IOException {
        Configuration conf = TestEditLogTailer.getConf();
        // Roll every 1s
        conf.setInt(DFS_HA_LOGROLL_PERIOD_KEY, 1);
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
        MiniDFSCluster cluster = null;
        try {
            cluster = TestEditLogTailer.createMiniDFSCluster(conf, 3);
            cluster.transitionToActive(0);
            EditLogTailer tailer = Mockito.spy(cluster.getNamesystem(1).getEditLogTailer());
            final AtomicInteger invokedTimes = new AtomicInteger(0);
            // It should go on to next name node when IOException happens.
            Mockito.when(tailer.getNameNodeProxy()).thenReturn(tailer.new MultipleNameNodeProxy<Void>() {
                @Override
                protected Void doWork() throws IOException {
                    invokedTimes.getAndIncrement();
                    throw new IOException("It is an IO Exception.");
                }
            });
            tailer.triggerActiveLogRoll();
            // MultipleNameNodeProxy uses Round-robin to look for active NN
            // to do RollEditLog. If doWork() fails, then IOException throws,
            // it continues to try next NN. triggerActiveLogRoll finishes
            // either due to success, or using up retries.
            // In this test case, there are 2 remote name nodes, default retry is 3.
            // For test purpose, doWork() always returns IOException,
            // so the total invoked times will be default retry 3 * remote NNs 2 = 6
            Assert.assertEquals(6, invokedTimes.get());
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testStandbyTriggersLogRollsWhenTailInProgressEdits() throws Exception {
        // Time in seconds to wait for standby to catch up to edits from active
        final int standbyCatchupWaitTime = 2;
        // Time in seconds to wait before checking if edit logs are rolled while
        // expecting no edit log roll
        final int noLogRollWaitTime = 2;
        // Time in seconds to wait before checking if edit logs are rolled while
        // expecting edit log roll
        final int logRollWaitTime = 3;
        Configuration conf = TestEditLogTailer.getConf();
        conf.setInt(DFS_HA_LOGROLL_PERIOD_KEY, ((standbyCatchupWaitTime + noLogRollWaitTime) + 1));
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
        conf.setBoolean(DFS_HA_TAILEDITS_INPROGRESS_KEY, true);
        MiniDFSCluster cluster = TestEditLogTailer.createMiniDFSCluster(conf, 2);
        if (cluster == null) {
            Assert.fail("failed to start mini cluster.");
        }
        try {
            int activeIndex = (new Random().nextBoolean()) ? 1 : 0;
            int standbyIndex = (activeIndex == 0) ? 1 : 0;
            cluster.transitionToActive(activeIndex);
            NameNode active = cluster.getNameNode(activeIndex);
            NameNode standby = cluster.getNameNode(standbyIndex);
            long origTxId = active.getNamesystem().getFSImage().getEditLog().getCurSegmentTxId();
            for (int i = 0; i < ((TestEditLogTailer.DIRS_TO_MAKE) / 2); i++) {
                NameNodeAdapter.mkdirs(active, TestEditLogTailer.getDirPath(i), new org.apache.hadoop.fs.permission.PermissionStatus("test", "test", new FsPermission(((short) (493)))), true);
            }
            long activeTxId = active.getNamesystem().getFSImage().getEditLog().getLastWrittenTxId();
            TestEditLogTailer.waitForStandbyToCatchUpWithInProgressEdits(standby, activeTxId, standbyCatchupWaitTime);
            for (int i = (TestEditLogTailer.DIRS_TO_MAKE) / 2; i < (TestEditLogTailer.DIRS_TO_MAKE); i++) {
                NameNodeAdapter.mkdirs(active, TestEditLogTailer.getDirPath(i), new org.apache.hadoop.fs.permission.PermissionStatus("test", "test", new FsPermission(((short) (493)))), true);
            }
            boolean exceptionThrown = false;
            try {
                TestEditLogTailer.checkForLogRoll(active, origTxId, noLogRollWaitTime);
            } catch (TimeoutException e) {
                exceptionThrown = true;
            }
            Assert.assertTrue(exceptionThrown);
            TestEditLogTailer.checkForLogRoll(active, origTxId, logRollWaitTime);
        } finally {
            cluster.shutdown();
        }
    }
}

