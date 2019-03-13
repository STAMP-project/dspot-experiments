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
package org.apache.hadoop.hbase.master;


import java.io.IOException;
import java.util.concurrent.Semaphore;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.ChoreService;
import org.apache.hadoop.hbase.CoordinatedStateManager;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.Server;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.ClusterConnection;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.monitoring.MonitoredTask;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.zookeeper.ClusterStatusTracker;
import org.apache.hadoop.hbase.zookeeper.ZKListener;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the {@link ActiveMasterManager}.
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestActiveMasterManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestActiveMasterManager.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestActiveMasterManager.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Test
    public void testRestartMaster() throws IOException, KeeperException {
        ZKWatcher zk = new ZKWatcher(TestActiveMasterManager.TEST_UTIL.getConfiguration(), "testActiveMasterManagerFromZK", null, true);
        try {
            ZKUtil.deleteNode(zk, zk.getZNodePaths().masterAddressZNode);
            ZKUtil.deleteNode(zk, zk.getZNodePaths().clusterStateZNode);
        } catch (KeeperException nne) {
        }
        // Create the master node with a dummy address
        ServerName master = ServerName.valueOf("localhost", 1, System.currentTimeMillis());
        // Should not have a master yet
        TestActiveMasterManager.DummyMaster dummyMaster = new TestActiveMasterManager.DummyMaster(zk, master);
        ClusterStatusTracker clusterStatusTracker = dummyMaster.getClusterStatusTracker();
        ActiveMasterManager activeMasterManager = dummyMaster.getActiveMasterManager();
        Assert.assertFalse(activeMasterManager.clusterHasActiveMaster.get());
        // First test becoming the active master uninterrupted
        MonitoredTask status = Mockito.mock(MonitoredTask.class);
        clusterStatusTracker.setClusterUp();
        activeMasterManager.blockUntilBecomingActiveMaster(100, status);
        Assert.assertTrue(activeMasterManager.clusterHasActiveMaster.get());
        assertMaster(zk, master);
        // Now pretend master restart
        TestActiveMasterManager.DummyMaster secondDummyMaster = new TestActiveMasterManager.DummyMaster(zk, master);
        ActiveMasterManager secondActiveMasterManager = secondDummyMaster.getActiveMasterManager();
        Assert.assertFalse(secondActiveMasterManager.clusterHasActiveMaster.get());
        activeMasterManager.blockUntilBecomingActiveMaster(100, status);
        Assert.assertTrue(activeMasterManager.clusterHasActiveMaster.get());
        assertMaster(zk, master);
    }

    /**
     * Unit tests that uses ZooKeeper but does not use the master-side methods
     * but rather acts directly on ZK.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testActiveMasterManagerFromZK() throws Exception {
        ZKWatcher zk = new ZKWatcher(TestActiveMasterManager.TEST_UTIL.getConfiguration(), "testActiveMasterManagerFromZK", null, true);
        try {
            ZKUtil.deleteNode(zk, zk.getZNodePaths().masterAddressZNode);
            ZKUtil.deleteNode(zk, zk.getZNodePaths().clusterStateZNode);
        } catch (KeeperException nne) {
        }
        // Create the master node with a dummy address
        ServerName firstMasterAddress = ServerName.valueOf("localhost", 1, System.currentTimeMillis());
        ServerName secondMasterAddress = ServerName.valueOf("localhost", 2, System.currentTimeMillis());
        // Should not have a master yet
        TestActiveMasterManager.DummyMaster ms1 = new TestActiveMasterManager.DummyMaster(zk, firstMasterAddress);
        ActiveMasterManager activeMasterManager = ms1.getActiveMasterManager();
        Assert.assertFalse(activeMasterManager.clusterHasActiveMaster.get());
        // First test becoming the active master uninterrupted
        ClusterStatusTracker clusterStatusTracker = ms1.getClusterStatusTracker();
        clusterStatusTracker.setClusterUp();
        activeMasterManager.blockUntilBecomingActiveMaster(100, Mockito.mock(MonitoredTask.class));
        Assert.assertTrue(activeMasterManager.clusterHasActiveMaster.get());
        assertMaster(zk, firstMasterAddress);
        // New manager will now try to become the active master in another thread
        TestActiveMasterManager.WaitToBeMasterThread t = new TestActiveMasterManager.WaitToBeMasterThread(zk, secondMasterAddress);
        t.start();
        // Wait for this guy to figure out there is another active master
        // Wait for 1 second at most
        int sleeps = 0;
        while ((!(t.manager.clusterHasActiveMaster.get())) && (sleeps < 100)) {
            Thread.sleep(10);
            sleeps++;
        } 
        // Both should see that there is an active master
        Assert.assertTrue(activeMasterManager.clusterHasActiveMaster.get());
        Assert.assertTrue(t.manager.clusterHasActiveMaster.get());
        // But secondary one should not be the active master
        Assert.assertFalse(t.isActiveMaster);
        // Close the first server and delete it's master node
        ms1.stop("stopping first server");
        // Use a listener to capture when the node is actually deleted
        TestActiveMasterManager.NodeDeletionListener listener = new TestActiveMasterManager.NodeDeletionListener(zk, zk.getZNodePaths().masterAddressZNode);
        zk.registerListener(listener);
        TestActiveMasterManager.LOG.info("Deleting master node");
        ZKUtil.deleteNode(zk, zk.getZNodePaths().masterAddressZNode);
        // Wait for the node to be deleted
        TestActiveMasterManager.LOG.info("Waiting for active master manager to be notified");
        listener.waitForDeletion();
        TestActiveMasterManager.LOG.info("Master node deleted");
        // Now we expect the secondary manager to have and be the active master
        // Wait for 1 second at most
        sleeps = 0;
        while ((!(t.isActiveMaster)) && (sleeps < 100)) {
            Thread.sleep(10);
            sleeps++;
        } 
        TestActiveMasterManager.LOG.debug((("Slept " + sleeps) + " times"));
        Assert.assertTrue(t.manager.clusterHasActiveMaster.get());
        Assert.assertTrue(t.isActiveMaster);
        TestActiveMasterManager.LOG.info("Deleting master node");
        ZKUtil.deleteNode(zk, zk.getZNodePaths().masterAddressZNode);
    }

    public static class WaitToBeMasterThread extends Thread {
        ActiveMasterManager manager;

        TestActiveMasterManager.DummyMaster dummyMaster;

        boolean isActiveMaster;

        public WaitToBeMasterThread(ZKWatcher zk, ServerName address) {
            this.dummyMaster = new TestActiveMasterManager.DummyMaster(zk, address);
            this.manager = this.dummyMaster.getActiveMasterManager();
            isActiveMaster = false;
        }

        @Override
        public void run() {
            manager.blockUntilBecomingActiveMaster(100, Mockito.mock(MonitoredTask.class));
            TestActiveMasterManager.LOG.info("Second master has become the active master!");
            isActiveMaster = true;
        }
    }

    public static class NodeDeletionListener extends ZKListener {
        private static final Logger LOG = LoggerFactory.getLogger(TestActiveMasterManager.NodeDeletionListener.class);

        private Semaphore lock;

        private String node;

        public NodeDeletionListener(ZKWatcher watcher, String node) {
            super(watcher);
            lock = new Semaphore(0);
            this.node = node;
        }

        @Override
        public void nodeDeleted(String path) {
            if (path.equals(node)) {
                TestActiveMasterManager.NodeDeletionListener.LOG.debug((("nodeDeleted(" + path) + ")"));
                lock.release();
            }
        }

        public void waitForDeletion() throws InterruptedException {
            lock.acquire();
        }
    }

    /**
     * Dummy Master Implementation.
     */
    public static class DummyMaster implements Server {
        private volatile boolean stopped;

        private ClusterStatusTracker clusterStatusTracker;

        private ActiveMasterManager activeMasterManager;

        public DummyMaster(ZKWatcher zk, ServerName master) {
            this.clusterStatusTracker = new ClusterStatusTracker(zk, this);
            clusterStatusTracker.start();
            this.activeMasterManager = new ActiveMasterManager(zk, master, this);
            zk.registerListener(activeMasterManager);
        }

        @Override
        public void abort(final String msg, final Throwable t) {
        }

        @Override
        public boolean isAborted() {
            return false;
        }

        @Override
        public Configuration getConfiguration() {
            return null;
        }

        @Override
        public ZKWatcher getZooKeeper() {
            return null;
        }

        @Override
        public CoordinatedStateManager getCoordinatedStateManager() {
            return null;
        }

        @Override
        public ServerName getServerName() {
            return null;
        }

        @Override
        public boolean isStopped() {
            return this.stopped;
        }

        @Override
        public void stop(String why) {
            this.stopped = true;
        }

        @Override
        public ClusterConnection getConnection() {
            return null;
        }

        public ClusterStatusTracker getClusterStatusTracker() {
            return clusterStatusTracker;
        }

        public ActiveMasterManager getActiveMasterManager() {
            return activeMasterManager;
        }

        @Override
        public ChoreService getChoreService() {
            return null;
        }

        @Override
        public ClusterConnection getClusterConnection() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public FileSystem getFileSystem() {
            return null;
        }

        @Override
        public boolean isStopping() {
            return false;
        }

        @Override
        public Connection createConnection(Configuration conf) throws IOException {
            return null;
        }
    }
}

