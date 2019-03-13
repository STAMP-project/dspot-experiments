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
package org.apache.hadoop.hbase.zookeeper;


import HBaseMarkers.FATAL;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseZKTestingUtility;
import org.apache.hadoop.hbase.Stoppable;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ZKTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ZKTests.class, MediumTests.class })
public class TestZKLeaderManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKLeaderManager.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestZKLeaderManager.class);

    private static final String LEADER_ZNODE = "/test/" + (TestZKLeaderManager.class.getSimpleName());

    private static class MockAbortable implements Abortable {
        private boolean aborted;

        @Override
        public void abort(String why, Throwable e) {
            aborted = true;
            TestZKLeaderManager.LOG.error(FATAL, ("Aborting during test: " + why), e);
            Assert.fail(("Aborted during test: " + why));
        }

        @Override
        public boolean isAborted() {
            return aborted;
        }
    }

    private static class MockLeader extends Thread implements Stoppable {
        private volatile boolean stopped;

        private ZKWatcher watcher;

        private ZKLeaderManager zkLeader;

        private AtomicBoolean master = new AtomicBoolean(false);

        private int index;

        public MockLeader(ZKWatcher watcher, int index) {
            setDaemon(true);
            setName(("TestZKLeaderManager-leader-" + index));
            this.index = index;
            this.watcher = watcher;
            this.zkLeader = new ZKLeaderManager(watcher, TestZKLeaderManager.LEADER_ZNODE, Bytes.toBytes(index), this);
        }

        public boolean isMaster() {
            return master.get();
        }

        public int getIndex() {
            return index;
        }

        public ZKWatcher getWatcher() {
            return watcher;
        }

        @Override
        public void run() {
            while (!(stopped)) {
                zkLeader.start();
                zkLeader.waitToBecomeLeader();
                master.set(true);
                while ((master.get()) && (!(stopped))) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                } 
            } 
        }

        public void abdicate() {
            zkLeader.stepDownAsLeader();
            master.set(false);
        }

        @Override
        public void stop(String why) {
            stopped = true;
            abdicate();
            Threads.sleep(100);
            watcher.close();
        }

        @Override
        public boolean isStopped() {
            return stopped;
        }
    }

    private static HBaseZKTestingUtility TEST_UTIL;

    private static TestZKLeaderManager.MockLeader[] CANDIDATES;

    @Test
    public void testLeaderSelection() throws Exception {
        TestZKLeaderManager.MockLeader currentLeader = getCurrentLeader();
        // one leader should have been found
        Assert.assertNotNull("Leader should exist", currentLeader);
        TestZKLeaderManager.LOG.debug(("Current leader index is " + (currentLeader.getIndex())));
        byte[] znodeData = ZKUtil.getData(currentLeader.getWatcher(), TestZKLeaderManager.LEADER_ZNODE);
        Assert.assertNotNull("Leader znode should contain leader index", znodeData);
        Assert.assertTrue("Leader znode should not be empty", ((znodeData.length) > 0));
        int storedIndex = Bytes.toInt(znodeData);
        TestZKLeaderManager.LOG.debug(("Stored leader index in ZK is " + storedIndex));
        Assert.assertEquals("Leader znode should match leader index", currentLeader.getIndex(), storedIndex);
        // force a leader transition
        currentLeader.abdicate();
        // check for new leader
        currentLeader = getCurrentLeader();
        // one leader should have been found
        Assert.assertNotNull("New leader should exist after abdication", currentLeader);
        TestZKLeaderManager.LOG.debug(("New leader index is " + (currentLeader.getIndex())));
        znodeData = ZKUtil.getData(currentLeader.getWatcher(), TestZKLeaderManager.LEADER_ZNODE);
        Assert.assertNotNull("Leader znode should contain leader index", znodeData);
        Assert.assertTrue("Leader znode should not be empty", ((znodeData.length) > 0));
        storedIndex = Bytes.toInt(znodeData);
        TestZKLeaderManager.LOG.debug(("Stored leader index in ZK is " + storedIndex));
        Assert.assertEquals("Leader znode should match leader index", currentLeader.getIndex(), storedIndex);
        // force another transition by stopping the current
        currentLeader.stop("Stopping for test");
        // check for new leader
        currentLeader = getCurrentLeader();
        // one leader should have been found
        Assert.assertNotNull("New leader should exist after stop", currentLeader);
        TestZKLeaderManager.LOG.debug(("New leader index is " + (currentLeader.getIndex())));
        znodeData = ZKUtil.getData(currentLeader.getWatcher(), TestZKLeaderManager.LEADER_ZNODE);
        Assert.assertNotNull("Leader znode should contain leader index", znodeData);
        Assert.assertTrue("Leader znode should not be empty", ((znodeData.length) > 0));
        storedIndex = Bytes.toInt(znodeData);
        TestZKLeaderManager.LOG.debug(("Stored leader index in ZK is " + storedIndex));
        Assert.assertEquals("Leader znode should match leader index", currentLeader.getIndex(), storedIndex);
        // with a second stop we can guarantee that a previous leader has resumed leading
        currentLeader.stop("Stopping for test");
        // check for new
        currentLeader = getCurrentLeader();
        Assert.assertNotNull("New leader should exist", currentLeader);
    }
}

