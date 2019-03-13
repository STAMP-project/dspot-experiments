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


import CreateMode.PERSISTENT;
import HConstants.DEFAULT_ZOOKEEPER_ZNODE_PARENT;
import HConstants.ZOOKEEPER_ZNODE_PARENT;
import Ids.OPEN_ACL_UNSAFE;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseZKTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ZKTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ZKTests.class, MediumTests.class })
public class TestZKNodeTracker {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestZKNodeTracker.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestZKNodeTracker.class);

    private static final HBaseZKTestingUtility TEST_UTIL = new HBaseZKTestingUtility();

    /**
     * Test that we can interrupt a node that is blocked on a wait.
     */
    @Test
    public void testInterruptible() throws IOException, InterruptedException {
        Abortable abortable = new TestZKNodeTracker.StubAbortable();
        ZKWatcher zk = new ZKWatcher(getConfiguration(), "testInterruptible", abortable);
        final TestZKNodeTracker.TestTracker tracker = new TestZKNodeTracker.TestTracker(zk, "/xyz", abortable);
        start();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    blockUntilAvailable();
                } catch (InterruptedException e) {
                    throw new RuntimeException("Interrupted", e);
                }
            }
        };
        t.start();
        while (!(t.isAlive())) {
            Threads.sleep(1);
        } 
        stop();
        t.join();
        // If it wasn't interruptible, we'd never get to here.
    }

    @Test
    public void testNodeTracker() throws Exception {
        Abortable abortable = new TestZKNodeTracker.StubAbortable();
        ZKWatcher zk = new ZKWatcher(getConfiguration(), "testNodeTracker", abortable);
        ZKUtil.createAndFailSilent(zk, zk.getZNodePaths().baseZNode);
        final String node = ZNodePaths.joinZNode(zk.getZNodePaths().baseZNode, Long.toString(ThreadLocalRandom.current().nextLong()));
        final byte[] dataOne = Bytes.toBytes("dataOne");
        final byte[] dataTwo = Bytes.toBytes("dataTwo");
        // Start a ZKNT with no node currently available
        TestZKNodeTracker.TestTracker localTracker = new TestZKNodeTracker.TestTracker(zk, node, abortable);
        start();
        zk.registerListener(localTracker);
        // Make sure we don't have a node
        Assert.assertNull(getData(false));
        // Spin up a thread with another ZKNT and have it block
        TestZKNodeTracker.WaitToGetDataThread thread = new TestZKNodeTracker.WaitToGetDataThread(zk, node);
        thread.start();
        // Verify the thread doesn't have a node
        Assert.assertFalse(thread.hasData);
        // Now, start a new ZKNT with the node already available
        TestZKNodeTracker.TestTracker secondTracker = new TestZKNodeTracker.TestTracker(zk, node, null);
        start();
        zk.registerListener(secondTracker);
        // Put up an additional zk listener so we know when zk event is done
        TestZKNodeTracker.TestingZKListener zkListener = new TestZKNodeTracker.TestingZKListener(zk, node);
        zk.registerListener(zkListener);
        Assert.assertEquals(0, zkListener.createdLock.availablePermits());
        // Create a completely separate zk connection for test triggers and avoid
        // any weird watcher interactions from the test
        final ZooKeeper zkconn = ZooKeeperHelper.getConnectedZooKeeper(ZKConfig.getZKQuorumServersString(getConfiguration()), 60000);
        // Add the node with data one
        zkconn.create(node, dataOne, OPEN_ACL_UNSAFE, PERSISTENT);
        // Wait for the zk event to be processed
        zkListener.waitForCreation();
        thread.join();
        // Both trackers should have the node available with data one
        Assert.assertNotNull(getData(false));
        Assert.assertNotNull(blockUntilAvailable());
        Assert.assertTrue(Bytes.equals(getData(false), dataOne));
        Assert.assertTrue(thread.hasData);
        Assert.assertTrue(Bytes.equals(getData(false), dataOne));
        TestZKNodeTracker.LOG.info("Successfully got data one");
        // Make sure it's available and with the expected data
        Assert.assertNotNull(getData(false));
        Assert.assertNotNull(blockUntilAvailable());
        Assert.assertTrue(Bytes.equals(getData(false), dataOne));
        TestZKNodeTracker.LOG.info("Successfully got data one with the second tracker");
        // Drop the node
        zkconn.delete(node, (-1));
        zkListener.waitForDeletion();
        // Create a new thread but with the existing thread's tracker to wait
        TestZKNodeTracker.TestTracker threadTracker = thread.tracker;
        thread = new TestZKNodeTracker.WaitToGetDataThread(zk, node, threadTracker);
        thread.start();
        // Verify other guys don't have data
        Assert.assertFalse(thread.hasData);
        Assert.assertNull(getData(false));
        Assert.assertNull(getData(false));
        TestZKNodeTracker.LOG.info("Successfully made unavailable");
        // Create with second data
        zkconn.create(node, dataTwo, OPEN_ACL_UNSAFE, PERSISTENT);
        // Wait for the zk event to be processed
        zkListener.waitForCreation();
        thread.join();
        // All trackers should have the node available with data two
        Assert.assertNotNull(getData(false));
        Assert.assertNotNull(blockUntilAvailable());
        Assert.assertTrue(Bytes.equals(getData(false), dataTwo));
        Assert.assertNotNull(getData(false));
        Assert.assertNotNull(blockUntilAvailable());
        Assert.assertTrue(Bytes.equals(getData(false), dataTwo));
        Assert.assertTrue(thread.hasData);
        Assert.assertTrue(Bytes.equals(getData(false), dataTwo));
        TestZKNodeTracker.LOG.info("Successfully got data two on all trackers and threads");
        // Change the data back to data one
        zkconn.setData(node, dataOne, (-1));
        // Wait for zk event to be processed
        zkListener.waitForDataChange();
        // All trackers should have the node available with data one
        Assert.assertNotNull(getData(false));
        Assert.assertNotNull(blockUntilAvailable());
        Assert.assertTrue(Bytes.equals(getData(false), dataOne));
        Assert.assertNotNull(getData(false));
        Assert.assertNotNull(blockUntilAvailable());
        Assert.assertTrue(Bytes.equals(getData(false), dataOne));
        Assert.assertTrue(thread.hasData);
        Assert.assertTrue(Bytes.equals(getData(false), dataOne));
        TestZKNodeTracker.LOG.info("Successfully got data one following a data change on all trackers and threads");
    }

    public static class WaitToGetDataThread extends Thread {
        TestZKNodeTracker.TestTracker tracker;

        boolean hasData;

        public WaitToGetDataThread(ZKWatcher zk, String node) {
            tracker = new TestZKNodeTracker.TestTracker(zk, node, null);
            start();
            zk.registerListener(tracker);
            hasData = false;
        }

        public WaitToGetDataThread(ZKWatcher zk, String node, TestZKNodeTracker.TestTracker tracker) {
            this.tracker = tracker;
            hasData = false;
        }

        @Override
        public void run() {
            TestZKNodeTracker.LOG.info("Waiting for data to be available in WaitToGetDataThread");
            try {
                blockUntilAvailable();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            TestZKNodeTracker.LOG.info("Data now available in tracker from WaitToGetDataThread");
            hasData = true;
        }
    }

    public static class TestTracker extends ZKNodeTracker {
        public TestTracker(ZKWatcher watcher, String node, Abortable abortable) {
            super(watcher, node, abortable);
        }
    }

    public static class TestingZKListener extends ZKListener {
        private static final Logger LOG = LoggerFactory.getLogger(TestZKNodeTracker.TestingZKListener.class);

        private Semaphore deletedLock;

        private Semaphore createdLock;

        private Semaphore changedLock;

        private String node;

        public TestingZKListener(ZKWatcher watcher, String node) {
            super(watcher);
            deletedLock = new Semaphore(0);
            createdLock = new Semaphore(0);
            changedLock = new Semaphore(0);
            this.node = node;
        }

        @Override
        public void nodeDeleted(String path) {
            if (path.equals(node)) {
                TestZKNodeTracker.TestingZKListener.LOG.debug((("nodeDeleted(" + path) + ")"));
                deletedLock.release();
            }
        }

        @Override
        public void nodeCreated(String path) {
            if (path.equals(node)) {
                TestZKNodeTracker.TestingZKListener.LOG.debug((("nodeCreated(" + path) + ")"));
                createdLock.release();
            }
        }

        @Override
        public void nodeDataChanged(String path) {
            if (path.equals(node)) {
                TestZKNodeTracker.TestingZKListener.LOG.debug((("nodeDataChanged(" + path) + ")"));
                changedLock.release();
            }
        }

        public void waitForDeletion() throws InterruptedException {
            deletedLock.acquire();
        }

        public void waitForCreation() throws InterruptedException {
            createdLock.acquire();
        }

        public void waitForDataChange() throws InterruptedException {
            changedLock.acquire();
        }
    }

    public static class StubAbortable implements Abortable {
        @Override
        public void abort(final String msg, final Throwable t) {
        }

        @Override
        public boolean isAborted() {
            return false;
        }
    }

    @Test
    public void testCleanZNode() throws Exception {
        ZKWatcher zkw = new ZKWatcher(getConfiguration(), "testNodeTracker", new TestZKNodeTracker.StubAbortable());
        final ServerName sn = ServerName.valueOf("127.0.0.1:52", 45L);
        ZKUtil.createAndFailSilent(zkw, getConfiguration().get(ZOOKEEPER_ZNODE_PARENT, DEFAULT_ZOOKEEPER_ZNODE_PARENT));
        final String nodeName = zkw.getZNodePaths().masterAddressZNode;
        // Check that we manage the case when there is no data
        ZKUtil.createAndFailSilent(zkw, nodeName);
        MasterAddressTracker.deleteIfEquals(zkw, sn.toString());
        Assert.assertNotNull(ZKUtil.getData(zkw, nodeName));
        // Check that we don't delete if we're not supposed to
        ZKUtil.setData(zkw, nodeName, MasterAddressTracker.toByteArray(sn, 0));
        MasterAddressTracker.deleteIfEquals(zkw, ServerName.valueOf("127.0.0.2:52", 45L).toString());
        Assert.assertNotNull(ZKUtil.getData(zkw, nodeName));
        // Check that we delete when we're supposed to
        ZKUtil.setData(zkw, nodeName, MasterAddressTracker.toByteArray(sn, 0));
        MasterAddressTracker.deleteIfEquals(zkw, sn.toString());
        Assert.assertNull(ZKUtil.getData(zkw, nodeName));
        // Check that we support the case when the znode does not exist
        MasterAddressTracker.deleteIfEquals(zkw, sn.toString());// must not throw an exception

    }
}

